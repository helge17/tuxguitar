# Audio features (BDD with Cucumber): audio tracks, line-in recording, VST3 hosting

Specification of audio tracks, line-in recording, VST3 effects and instruments, keeping audio in sync, saving and
exporting it. It is written ahead of the implementation: every feature here is tagged `@planned`, its steps are not
defined yet, and the normal run leaves it out (`src/test/resources/junit-platform.properties`). Remove the tag from a
scenario when you start implementing it.

The conventions are those of `common/TuxGuitar-editor-utils/src/test/resources/features/README.md`: first person, in
the words of someone using TuxGuitar; behaviour, never the GUI; the action under test is the one `When`; earlier actions
are past-tense `Given`s; boundaries in Scenario Outlines; every `Then` fails if the feature does nothing.

| What | Where |
|---|---|
| Feature files | `src/test/resources/features/<area>/<name>.feature` (this folder) |
| Step definitions | `src/test/java/app/tuxguitar/audio/features/*Steps.java` (none yet) |
| Runner | `TestFeatures` |

```
mvn -f common/TuxGuitar-audio/pom.xml test                                                   # implemented scenarios
mvn -f common/TuxGuitar-audio/pom.xml test -Dtest=TestFeatures -Dcucumber.filter.tags=@planned  # the whole spec
```

The second line fails on undefined steps until they exist; it proves every file parses and prints step snippets.

Before the file format changes, capture the golden file `saving/no-audio.content.xml` from today's writer (the
`content.xml` of the Background song saved as a TuxGuitar file), so "saves exactly as before" has something to compare.

## How the scenarios run

The module is a headless audio engine: sound chains, takes, and a render clock. Scenarios drive it offline, on the
sample clock of a fake audio device (`Test Interface`), in blocks of 512 samples, faster than real time. "Recording from
A to B" or "playing until B" stops on the exact sample of B. Plugins are fake VST3 plugins whose behaviour is fixed
below, so every number in a scenario can be checked exactly. One integration test outside the features loads a real
VST3 plugin (the SDK's `again` example).

## Areas

| Area | Feature |
|---|---|
| `tracks/audio_tracks` | audio tracks: adding, channels, cloning, removing, which MIDI Port they need |
| `recording/recording_line_in` | arming, input channels and level, Count In, monitoring, recording at another Percentage of Tempo |
| `recording/takes` | stacking, comping, punch-in, loop recording, repeats, undo, editing the song under takes |
| `plugins/` | hosting VST3 effects and instruments in a track's sound chain |
| `sync/` | keeping audio in sync: plugin delay compensation, latency, loops, Percentage of Tempo, tempo edits |
| `saving/` | saving and reopening songs with takes and plugins; other file formats |
| `export/` | exporting audio |

## Rules every feature relies on

- **The TuxGuitar Synthesizer**: sound chains, audio tracks and VST3 plugins work only when the MIDI Port is
  "TuxGuitar Synthesizer". Any other MIDI Port (an external synth, FluidSynth, JACK, a MIDI device) bypasses them.
- **Sample-accurate playback**: while the TuxGuitar Synthesizer plays, the audio device's sample clock drives the
  player, and every note reaches its instrument on its exact sample inside a block. The player reports a beat when the
  output sounds it, so the played beat is no longer highlighted early by the output buffer.
- **Plugins run apart from TuxGuitar**: VST3 plugins run in a helper process separate from TuxGuitar. It talks to
  TuxGuitar over pipes or shared memory and never listens on a network port. It loads plugins only from the VST3
  folders. (VST2 hosting was removed in 2024 because its bridge listened on a network port and was flagged as a
  Trojan, issue #312. The VST3 SDK is MIT-licensed since 3.8, October 2025, which fits TuxGuitar's LGPL-2.1.)
- **Takes are shared, never copied**: a take's samples never change once recorded. Cloning a song or a track and
  undo snapshots share them, so undo does not multiply memory.
- **The song and the session**: arming, monitoring, input level, the audio device and the recording offset are not
  part of the song. They are not saved, not undoable, and do not mark the song modified. Each audio track's input
  channel(s) are part of the song.

## Glossary

### Time and positions

- **Sample**: one frame of audio at the audio device's sample rate. At 44100 Hz and 120 bpm a quarter note is 22050
  samples and a 4/4 measure 88200; at 48000 Hz they are 24000 and 96000.
- **Position**: `measure <m> beat <b>`, both from 1. A beat is the unit of the measure's time signature (an eighth in
  6/8, so 6/8 has 6 beats); `beat 2.5` is half way through beat 2. "The end of measure 4" is where measure 5 would start.
- **Song time**: positions as the song plays them, following repeats and alternate endings. "Pass" means one time
  through a repeated or looped range.
- **Percentage of Tempo, Play looped, Loop Range, Training Mode**: the Play Mode settings that exist today. Training
  Mode raises the percentage after each pass of the Loop Range, from its start percentage to its end, by its increment.
- **Count In**: clicks before playback or recording starts; one per beat of the starting measure, or the number set as
  "Count In ticks".

### Tracks and sound

- **Audio track**: a track that sounds its takes instead of notes. It has no strings, holds no notes, has a channel of
  its own that no other track shares, and records from the input channel(s) set for it.
- **Sound chain**: what a track sounds through: a source followed by VST3 effects, in processing order. It belongs to
  the track's channel. **Sound chain reading**: the source and the VST3 effects joined by ` > `, a disabled effect in
  brackets: `"Distortion Electric Guitar > Test Gain > [Test Clip 0.5]"`.
- **Source**: a General MIDI program played by the TuxGuitar Synthesizer (read by its name, e.g.
  `Distortion Electric Guitar`), a VST3 instrument (read by its plugin name), or, on an audio track, its takes (read
  `takes`).
- **VST3 effect**: a VST3 plugin that processes audio (an amp modeler, a pedal). Always "VST3 effect", never a bare
  "effect": TuxGuitar's "Effects" are note effects (bends, slides, ...). "VST3 effect 2 of track 1" is the second
  VST3 effect in track 1's sound chain.
- **VST3 instrument**: a VST3 plugin that turns notes into sound; it replaces the General MIDI program as the source.
- **Volume and Balance**: the channel's, applied after the sound chain. Reverb, Chorus, Tremolo and Phaser of a channel
  reach a VST3 instrument as their MIDI controllers, and do nothing on an audio track.

### Recording

- **Audio device**: the audio interface TuxGuitar plays and records through, chosen in TuxGuitar's settings.
- **Input channel**: one input of the audio device. A take recorded from one input channel is mono; from a pair, stereo.
- **Arm**: mark an audio track to record from its input channel(s). Only audio tracks can be armed.
- **Take**: one recording on an audio track: the dry input (VST3 effects are never recorded into it), stored as 32-bit
  float at the audio device's sample rate, anchored to the song's measures and beats. When the song's tempo differs from
  the tempo it was recorded at, it is time-stretched so its beats stay on the song's beats. Takes are numbered in
  recording order per track, and never renumbered.
- **Take stack**: a track's takes, top first. At each point the track plays the topmost take that covers it. Choosing a
  take for a range of measures puts that range of it on top (comping). Switching from one take to another crossfades
  over 220 samples (5 ms at 44100 Hz).
- **Takes reading**: the take stack, top first, as `take <n>: <from> to <to>` joined by `, `, positions short:
  `"take 2: m2b1 to m4b1, take 1: m1b1 to m3b1"`.
- **Playback reading**: which take a track plays in each measure: `"m1: take 1, m2: take 2, m3: take 2, m4: none"`.
- **Punch range**: measures outside which a recording keeps nothing.
- **Loop recording**: recording with Play looped on: each pass over the Loop Range records a take, the last on top.
- **Monitoring**: hearing an armed track's input through its sound chain, while recording or stopped.
- **Input level**: the peak of what an input channel delivered in the last second, in dBFS. At -0.1 dBFS or above the
  input is **clipping**.
- **Latency**: how many samples late something delivers its sound: a VST3 plugin, the audio input or the audio output.
- **Recording offset**: a setting, in samples, added to the input latency the audio device reports, because drivers
  often report it wrong.

### Sync

- **Plugin delay compensation**: delaying each track so that all tracks reach the output as late as the slowest one.
  A monitored input is not delayed by it.
- **Time-stretch**: playing a take faster or slower without changing its pitch.
- **Tail**: what a VST3 effect still sounds after its input has stopped (an echo, a reverb). Tails carry across the
  jump back of a loop, are cleared when playback stops or jumps, and an exported file runs on until every tail has
  fallen below -90 dBFS.

### Measuring

- **A track's tap**: the track's audio where it joins the output mix: after its sound chain and after plugin delay
  compensation, before Volume and Balance. "Track 2 sounds ..." is measured at track 2's tap.
- **The output**: what the audio device plays: the mix of every track, the Count In and the metronome.
- **Click**: a one-sample impulse. A click's position is its first sample above half its peak (a time-stretched click is
  no longer one sample).
- **Peak**: the largest absolute sample value, on a scale where 1.0 is full scale (0 dBFS).

## Test plugins and device

The fakes the steps provide. "The VST3 folders hold the test plugins" installs all of them.

| Plugin | Kind | Does | Latency it reports |
|---|---|---|---|
| `Test Gain` | VST3 effect | multiplies the signal by its parameter `Gain` (2.0 by default) | 0 |
| `Test Clip 0.5` | VST3 effect | clips the signal to between minus and plus its parameter `Ceiling` (0.5 by default) | 0 |
| `Test Latency` | VST3 effect | delays the signal by its parameter `Latency` in samples (0 by default) | `Latency` |
| `Test Echo` | VST3 effect | adds one copy of the signal at half its level, 1 second later | 0 |
| `Test Tempo` | VST3 effect | passes the signal and records the tempo, time signature and play state TuxGuitar gives it | 0 |
| `Test Sine` | VST3 instrument | plays a sine at each note's pitch, peaking at 0.5 × velocity / 127, following pitch bend | 0 |
| `Test Click` | VST3 instrument | plays a click of +0.5 on the first sample of each note | its parameter `Latency`, 0 by default |

Each test plugin also keeps a state of 1 KB of bytes that changes with its parameters, so saving can be checked byte
for byte. Each one has an editor, except `Test Clip 0.5`.

`Test Interface` is an audio device with the sample rate and number of input channels a scenario gives it (44100 Hz and
2 by default). It delivers the input late by its input latency, and sounds the output late by its output latency (0 by
default each). It reports latencies as given, unless a scenario says it reports something else. What "the player
plays" reaches the input timed against what the player hears at the output, so latencies add up as in real life.

Signals a scenario names: `clicks of -0.25 on every beat` (take clicks are negative, `Test Click`'s positive, so
the two are told apart in a mix), `one click of -0.25 at <position>`, `a <f> Hz tone peaking at <p>` (a sine; its
peak may be given in dBFS), `a constant level of <v>`. "A track has a take of ..." puts a take on the track directly,
as if it had been recorded at the song's tempo, without going through the input.

"The player plays ..." is what reaches the input: the scenario's musician, playing in time with what they hear.
"A Hz tone within 1%" is measured by its fundamental frequency; "no ... above -60 dBFS" by the level of that frequency.

## Out of scope

- Failure handling: a plugin that crashes or hangs, a plugin missing on another machine, an audio device unplugged
  while recording, a song opened on a device whose sample rate differs from its takes'.
- Importing audio or backing tracks from Guitar Pro 7 files (they are dropped on import today).
- Android (it shares the file reader, so it refuses a song with audio tracks as older desktop versions do).
- Recording MIDI from a MIDI input.
- Input gain inside TuxGuitar: the input level is set on the audio device.
- Editing takes beyond what the features say (trimming, fades, gain, moving a take by hand).
