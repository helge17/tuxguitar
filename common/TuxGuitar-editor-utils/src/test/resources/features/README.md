# Feature files (BDD with Cucumber)

Behaviour of the editor written as Gherkin scenarios, run by Cucumber 7 on JUnit 5 as part of `mvn test`.

| What | Where |
|---|---|
| Feature files | `src/test/resources/features/<area>/<name>.feature` (this folder) |
| Step definitions | `src/test/java/app/tuxguitar/editor/features/*Steps.java` |
| Scenario state | `SongWorld`: one per scenario, injected into every step class (picocontainer) |
| Runner | `TestFeatures` (JUnit Platform suite: every feature here, glue in that package) |
| Report | `target/cucumber-report.html` after a run |

The steps drive the real editor actions headless, through `SongActionFixture`: an action is mapped in the action
manager and executed on an action context, as the app does when a menu item or key runs it. The GUI (SWT/JFX
windows, dialogs, painting) is not reached; a feature describes what an action does to the song.

## Running

```
mvn -f common/TuxGuitar-editor-utils/pom.xml test                       # everything, features included
mvn -f common/TuxGuitar-editor-utils/pom.xml test -Dtest=TestFeatures   # only the features
mvn -f common/TuxGuitar-editor-utils/pom.xml test -Dtest=TestFeatures -Dcucumber.filter.tags=@wip
```

Tag the scenario you are working on `@wip` to run it alone; remove the tag when it passes.

## Adding a feature

1. Write the `.feature` file first, in the words of someone using the editor. Reuse the steps below where they fit.
2. Run the features. A new step fails as undefined, and the output prints a Java snippet for it.
3. Put the snippet in the `*Steps` class of its area (make a new one per area, taking `SongWorld` in its
   constructor). A step only sets up the action context and executes the action, or reads the song back; the
   behaviour stays in the action.
4. Run again: the scenario fails because the behaviour is missing, then implement it until it passes.

Keep the state of a scenario in `SongWorld`, never in a static field: scenarios must not see each other's songs.

## Glossary

- **Song**: a new song has one guitar track in standard tuning and one 4/4 measure, filled with four quarter beats.
  A measure that is not full switches the editor to free edition mode, so the default song keeps it full.
- **Caret**: where typing goes: a string of a beat, in voice 0. It starts on the first beat.
- **String**: numbered from 1, the highest string (high E in standard tuning).
- **Fret**: 0 is the open string; the track's max fret (29 by default) is the last one accepted.
- **Beat reading**: the notes of a beat's voice 0, lowest string number first, as `s<string>f<fret>` separated by
  spaces: `"s4f2 s5f3"`. An empty beat reads `""`.
- **Succeeds / is refused**: whether the action reported success on its action context.

## Steps

| Step | Class |
|---|---|
| `Given a new song with one guitar track` | `SongSteps` |
| `Given the track's max fret is {int}` | `SongSteps` |
| `Then the caret's beat reads {string}` | `SongSteps` |
| `Then the action succeeds` / `Then the action is refused` | `SongSteps` |
| `When I enter fret {int} on string {int}` / `Given I have entered fret {int} on string {int}` | `NoteSteps` |
