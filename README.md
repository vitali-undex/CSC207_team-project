# Quercus Course Tracker

*Working name — the team can rename it.* CSC207 Fall 2026 team project, Group 19 (TUT0301).

**Team:** Vitalii, Lucas, Eric, Brian, Henry

## Project domain

**Productivity / education.** A Java Swing desktop app that connects to a student's Quercus account and
brings deadlines, grades and announcements from all of their courses into one place, next to their own tasks.

### Specification

A student connects the app to their Quercus account with a personal access token. The app reads the student's
courses, the assignments in each course with their due dates and scores, and each course's announcements. It
shows every deadline on a calendar, lists announcements from all courses together, and works out each course's
current weighted grade from the grading scheme the student copies in from the syllabus. Students can also keep
their own tasks, such as "study for the midterm", next to the Quercus deadlines. The app saves what it loads, so
it still opens with the last known data when there is no internet connection.

## User stories

One story per team member, each led by one person (see [MVP](#mvp)).

| # | User story | Lead |
| --- | --- | --- |
| 1 | As a student, I want to connect the app to my Quercus account so that my courses load without me typing them in. | Eric |
| 2 | As a student, I want to see all my upcoming deadlines on a calendar so that I can plan my week. | Lucas |
| 3 | As a student, I want to enter each course's grading scheme and see my current weighted grade so that I know where I stand. | Vitalii |
| 4 | As a student, I want to read new announcements from all my courses in one place so that I don't miss changes. | Brian |
| 5 | As a student, I want to add my own tasks and tick them off so that I can track work Quercus doesn't list. | Henry |

<details>
<summary>What changed from the first draft of the stories, and why</summary>

- **Login became "Connect my Quercus account" (story 1).** The provided login starter code already does
  sign-up and log-in, so a login story risks being graded as minimal. Connecting with a Quercus token is
  distinct, and it is where the app's API work starts. Each student's data is still saved under their account.
- **"Read all course information from Quercus and fill out the app" became "My own tasks" (story 5).** Reading
  from Quercus is *how* stories 1–4 work, not a separate goal for the student. As its own story it overlaps with
  all the others (the rubric's scope row) and describes implementation (the specification row). The Quercus API
  code is shared data-access code that every lead adds the calls they need to.
- Stories now say who wants it, what they want, and **why** ("so that ..."), and none of them mention how the
  program does it.

</details>

## Use cases

Each use case follows the lecture format. The lead takes the lead on its interactor and interfaces, its
controller and presenter, and its part of the UI.

### Use case 1: Connect Quercus account — lead: Eric

- **Primary actor:** Student
- **Preconditions:** The student has a Quercus personal access token (Quercus → Account → Settings → New access token).
- **Trigger:** The student opens the app for the first time, or chooses "Connect Quercus".
- **Success outcome:** The account is saved and the student's current courses are shown.
- **Main success scenario:**
  1. The student enters their Quercus address and access token.
  2. The system checks the token with Quercus.
  3. The system loads the student's active courses.
  4. The system saves the account and the course list.
  5. The system shows the student's name and their courses.
- **Extensions:**
  - **2a.** The token is rejected: the system says the token is invalid or expired, and nothing is saved.
  - **2b.** Quercus can't be reached: the system says so and offers to try again.
  - **3a.** The student has no active courses: the system shows an empty course list with a note.

### Use case 2: View deadlines calendar — lead: Lucas

- **Primary actor:** Student
- **Preconditions:** A Quercus account is connected.
- **Trigger:** The student opens the Calendar view, or moves to another month.
- **Success outcome:** The month shows every deadline from the student's courses.
- **Main success scenario:**
  1. The student opens the calendar.
  2. The system loads the assignments with due dates for every connected course.
  3. The system shows each deadline on its day, labelled with its course and title.
  4. The student selects a day.
  5. The system lists that day's deadlines with course, title, due time and points.
- **Extensions:**
  - **2a.** Quercus can't be reached: the system shows the deadlines saved from the last successful load and
    marks them as possibly out of date.
  - **3a.** There are no deadlines in the month: the system shows an empty month.

### Use case 3: Calculate weighted grade — lead: Vitalii

- **Primary actor:** Student
- **Preconditions:** A Quercus account is connected, and the student has the course syllabus.
- **Trigger:** The student opens a course's Grades view.
- **Success outcome:** The course's current weighted grade is shown with each component's average and weight,
  and the grading scheme is saved.
- **Main success scenario:**
  1. The student picks a course.
  2. The system loads the course's assignment groups and the student's scores.
  3. The student enters each group's weight from the syllabus, and how many lowest scores it drops.
  4. The system calculates each group's average and the weighted grade over the groups graded so far.
  5. The system saves the grading scheme and shows the grade with its breakdown.
- **Extensions:**
  - **3a.** The weights don't add up to 100%: the system shows the difference and asks for a fix.
  - **4a.** Nothing is graded yet: the system shows the scheme with "no grade yet".
  - **2a.** Quercus can't be reached: the system uses the saved scores and says when they were last updated.

### Use case 4: Read course announcements — lead: Brian

- **Primary actor:** Student
- **Preconditions:** A Quercus account is connected.
- **Trigger:** The student opens the Announcements tab.
- **Success outcome:** Announcements from all courses are listed newest first, with new ones marked.
- **Main success scenario:**
  1. The student opens the Announcements tab.
  2. The system loads recent announcements for every connected course.
  3. The system lists them newest first with course, title and date, marking the ones posted since the
     student's last visit.
  4. The student selects an announcement.
  5. The system shows its full text and records it as read.
- **Extensions:**
  - **2a.** Quercus can't be reached: the system shows the saved announcements with a note.
  - **3a.** The student filters by course: the list shows only that course's announcements.

### Use case 5: Track my own tasks — lead: Henry

- **Primary actor:** Student
- **Preconditions:** None. Tasks work without a Quercus connection, and can be linked to a course when one exists.
- **Trigger:** The student chooses "Add task", or ticks a task.
- **Success outcome:** The task list is saved and shown, sorted by due date.
- **Main success scenario:**
  1. The student enters a title, an optional course and an optional due date.
  2. The system checks that the title isn't empty.
  3. The system adds the task and saves the list.
  4. The system shows the updated list, sorted by due date.
  5. Later, the student ticks a task as done, and the system saves it and shows it as done.
- **Extensions:**
  - **2a.** The title is empty: the system asks for one, and nothing is saved.
  - **1a.** The due date is in the past: the system asks the student to confirm.

## MVP

All five use cases are in the MVP, since every member must take the lead on at least one.

| Use case | Lead | Branch |
| --- | --- | --- |
| 1. Connect Quercus account | Eric | `eric/connect-quercus` |
| 2. View deadlines calendar | Lucas | `lucas/deadline-calendar` |
| 3. Calculate weighted grade | Vitalii | `vitalii/grade-calculator` |
| 4. Read course announcements | Brian | `brian/announcements` |
| 5. Track my own tasks | Henry | `henry/personal-tasks` |

**After the MVP, if there's time:** a what-if planner ("what do I need on the final?"), and the external tool
adapter described under [Future extensions](#future-extensions).

## Entities

A first design — each lead refines the entities their use case needs.

- **Course:** `id`, `code`, `name`, `term`
- **Assignment:** `id`, `courseId`, `name`, `dueAt`, `pointsPossible`, `groupId`, `score` (empty until graded);
  `isGraded()`, `isDueOn(date)`
- **AssignmentGroup:** `id`, `courseId`, `name`, `weight`, `dropLowest`, `assignments`; `average()`
- **GradingScheme:** `courseId`, `groups`; `totalWeight()`, `weightedGrade()`, `isComplete()`
- **Announcement:** `id`, `courseId`, `title`, `message`, `postedAt`, `read`; `markRead()`
- **Task:** `id`, `title`, `courseId` (optional), `dueDate` (optional), `done`; `complete()`
- **StudentAccount:** `name`, `quercusUrl`, `courses`

## API usage

**[Canvas LMS REST API](https://canvas.instructure.com/doc/api/)** — Quercus is U of T's Canvas, so every call goes
to `https://q.utoronto.ca/api/v1/` with the header `Authorization: Bearer <token>`. List endpoints return pages of
results: request `per_page=100` and follow the `Link: rel="next"` header. Each student uses their own token, which
is never committed (see [Running the app](#running-the-app)).

| Use case | Endpoint | Called successfully? |
| --- | --- | --- |
| 1 | `GET /users/self`, `GET /courses?enrollment_state=active` | Yes, from the command line (Nov 16). Not yet from Java. |
| 2 | `GET /courses/:id/assignments` (uses `due_at`) | Yes, from the command line (Nov 16). Not yet from Java. |
| 3 | `GET /courses/:id/assignment_groups?include[]=assignments&include[]=submission` | Not yet |
| 4 | `GET /announcements?context_codes[]=course_:id` | Yes, from the command line (Nov 16). Not yet from Java. |
| 5 | None — tasks are stored locally | — |

## Data persistence

The app saves its data as JSON files in a folder in the user's home directory (`~/.quercus-tracker/`), never in
the repo:

- the connected account and course list (use case 1)
- the last loaded deadlines, scores and announcements, so the app works offline (use cases 2–4)
- grading schemes and read markers (use cases 3–4)
- tasks (use case 5)

The final demo should show both saving and loading: close the app, reopen it offline, and the data is still there.

## Architecture

The code follows Clean Architecture, as in the `ca-login` lab code:

- Packages: `entity`, `use_case.<use case>`, `interface_adapter.<use case>`, `view`, `data_access`, `app`.
- Each use case defines its own small data access interface. One `CanvasApiDataAccessObject` implements the
  interfaces that need Quercus, and one JSON file DAO implements the ones that need local storage. That keeps
  each interactor depending only on the methods it calls (ISP), and on interfaces rather than on Quercus or files
  (DIP).
- `app.AppBuilder` creates every object and passes the dependencies in through constructors.

## Team workflow

**Branches**

- `main` is protected. Changes arrive only by pull request, approved by **all four** other teammates. Nobody can
  push to it directly, force-push or delete it.
- `dev` is the integration branch, with no rules.
- Each lead works on their own branch from the [MVP table](#mvp), branched from `dev`.

**How a change gets to `main`**

1. Work on your branch and commit often.
2. Open a pull request from your branch into `dev`, and ask at least one teammate to review it.
3. When `dev` is stable, someone opens a pull request from `dev` into `main`, and all four other members approve it.

> **Fork check:** this repo is a fork, so GitHub suggests the *original* repository as the base when you open a
> pull request. Always check that the base repository is **vitali-undex/CSC207_team-project**.

**Every pull request**

- Says which use case it is for, and updates this README if behaviour changed.
- Includes or updates unit tests for any interactor it touches.
- Has been checked with Checkstyle (the rubric requires a tool like it for the top two code-quality levels).
- Contains only your own use case's code. Graders check the git log for each person's contribution.

## Milestones

From the Quercus project timeline:

- [x] **Milestone 1** (Nov 13): README with stories, use cases and leads; core entities.
- [ ] **Milestone 2** (Nov 20): everyone has merged a substantial PR, reviewed a teammate's PR, and has runnable code
      for their use case; the API calls work from Java; one use case works end to end.
- [ ] **Milestone 3** (Nov 27): at least one unit test per interactor; all use cases integrated.
- [ ] **Milestone 4** (Dec 4): 100% line coverage for the entity and use case layers; bugs fixed; presentation rehearsed.
- [ ] **Presentations:** Dec 7/8 in lab. Every member presents; the demo shows each member's story working.

## Future extensions

- **External tool adapter.** Let tools such as Claude Code add tasks or record scores through a small
  command-line (or MCP) adapter. In Clean Architecture this is just another controller calling the same input
  boundaries as the Swing views, so no use case changes (a good Open/Closed example for the presentation). The
  adapter must never expose the student's Quercus token.
- A what-if grade planner, and reminders before deadlines.

## Running the app

1. Open the project in IntelliJ as a Maven project, using Java 11 or newer.
2. Create a Quercus access token: Quercus → Account → Settings → **New access token**.
3. Run `app.Main`, choose **Connect Quercus**, and paste the token.

For command-line experiments with the API, copy `.env.example` to `.env` and fill it in. `.env` is ignored by git
— never commit a token.
