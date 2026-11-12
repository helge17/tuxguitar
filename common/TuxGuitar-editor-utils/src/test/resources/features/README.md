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

A feature tagged `@planned` is a specification written ahead of its implementation: its steps are not defined yet and
the normal run leaves it out. Remove the tag from a scenario once it is being implemented.

## Planned: audio tracks and VST3 plugins (`audio/`)

Recording line-in audio, hosting VST3 effects and instruments, keeping audio in sync, saving it and exporting it.
Out of scope for these features, on purpose:

- Failure handling: a plugin that crashes or hangs, a plugin missing on another machine, an audio device unplugged
  while recording, a device whose sample rate differs from the song's.
- Importing audio or backing tracks from Guitar Pro 7 files (they are dropped on import today).
- Android.
- Recording MIDI from a MIDI input.

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

### Audio (planned)

- **Sample**: one frame of audio. The song's audio runs at 44100 samples per second; at 120 bpm a quarter note is
  22050 samples and a 4/4 measure 88200.
- **Position**: `measure <m> beat <b>`, both numbered from 1. "The end of measure 4" is where measure 5 would start.
- **Audio input**: an input channel (or a pair, for stereo) of an audio device, chosen in TuxGuitar's settings. It is
  not part of the song.
- **Audio track**: a track that sounds its takes instead of notes. It has no strings and holds no notes.
- **Arm**: mark an audio track to record from its audio input. Only audio tracks can be armed.
- **Take**: one recording on an audio track: the dry input, from the position recording started to where it stopped.
  A track's takes are stacked, newest on top; at each point the track plays the topmost take that covers it.
- **Punch range**: measures outside which a recording keeps nothing.
- **Monitoring**: hearing the input through the armed track's sound chain while recording.
- **Input level**: the peak of what the input delivers, in dBFS; at 0 dBFS the input is clipping.
- **Sound chain**: a track's instrument followed by its effects, in processing order. It belongs to the track's
  channel. Read as the instrument and effects joined by ` > `, a disabled effect in brackets:
  `"Distortion Electric Guitar > Test Gain > [Test Clip 0.5]"`. An audio track's instrument reads `takes`.
- **Instrument**: what turns a track's notes into sound: a General MIDI program played by TuxGuitar's synthesizer
  (read by its name, e.g. `Distortion Electric Guitar`) or a VST3 instrument (read by its plugin name).
- **Effect**: a VST3 effect in a sound chain, such as an amp modeler or a pedal.
- **Latency**: how many samples late something delivers its sound: an effect, the audio input or the audio output.
- **Plugin delay compensation**: delaying every track to the latest one, so all tracks reach the output together.
- **Time-stretch**: playing a take faster or slower, to follow the Percentage of Tempo, without changing its pitch.
- **Test plugins and signals**: fake VST3 plugins (`Test Gain`, `Test Clip 0.5`, `Test Delay`, `Test Sine`,
  `Test Click`) and a fake audio device (`Test Interface`) whose behaviour each feature states, so a scenario's numbers
  can be checked exactly.

## Steps

| Step | Class |
|---|---|
| `Given a new song with one guitar track` | `SongSteps` |
| `Given the track's max fret is {int}` | `SongSteps` |
| `Then the caret's beat reads {string}` | `SongSteps` |
| `Then the action succeeds` / `Then the action is refused` | `SongSteps` |
| `When I enter fret {int} on string {int}` / `Given I have entered fret {int} on string {int}` | `NoteSteps` |
