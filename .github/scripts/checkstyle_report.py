"""Turn Checkstyle's XML result into GitHub annotations and a run summary.

Usage: python3 checkstyle_report.py target/checkstyle-result.xml

Prints one annotation per violation (GitHub shows them on the code in the pull request),
writes a table of every violation to the run's summary, and exits with 1 when there is at
least one violation, so the Checkstyle check shows as failed.
"""
import os
import sys
import xml.etree.ElementTree as ElementTree


def escape(text):
    """Escape a value for a GitHub workflow command."""
    return text.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")


def main(result_path):
    workspace = os.environ.get("GITHUB_WORKSPACE", os.getcwd())
    violations = []
    for file_element in ElementTree.parse(result_path).getroot().iter("file"):
        path = os.path.relpath(file_element.get("name"), workspace)
        for error in file_element.iter("error"):
            rule = error.get("source", "").rsplit(".", 1)[-1].removesuffix("Check")
            message = " ".join(error.get("message", "").split())
            violations.append((path, error.get("line", "1"), error.get("column", ""),
                               rule, message))

    for path, line, column, rule, message in violations:
        location = f"file={path},line={line}" + (f",col={column}" if column else "")
        print(f"::warning {location},title=Checkstyle {rule}::{escape(message)}")

    summary_path = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary_path:
        with open(summary_path, "a", encoding="utf-8") as summary:
            if violations:
                summary.write(f"## Checkstyle: {len(violations)} violation(s)\n\n")
                summary.write("Run `mvn checkstyle:check` locally to see the same list.\n\n")
                summary.write("| File | Line | Rule | Message |\n| --- | --- | --- | --- |\n")
                for path, line, _, rule, message in violations:
                    cell = message.replace("|", "\\|")
                    summary.write(f"| `{path}` | {line} | {rule} | {cell} |\n")
            else:
                summary.write("## Checkstyle: no violations\n")

    print(f"Checkstyle: {len(violations)} violation(s).")
    return 1 if violations else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1]))
