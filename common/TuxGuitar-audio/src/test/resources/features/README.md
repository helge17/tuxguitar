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

Before the file format changes, capture two golden files from today's writer: `saving/no-audio.tg` (the Background
song saved as a TuxGuitar file) and its `content.xml` as `saving/no-audio.content.xml`. Compare with the `TGVersion`
node masked, since that node holds the TuxGuitar release. A "reader of file format 2.0" is today's reader, kept as a
test fixture; give it the format code "tg", or it reports "Unsupported file format" instead of "please upgrade".

## Areas

| Area | Feature |
|---|---|
| `tracks/audio_tracks` | audio tracks: adding, channels, cloning, removing, which MIDI Port they need |
| `recording/recording_line_in` | arming, input channels and level, Count In, monitoring, recording at another Percentage of Tempo |
| `recording/takes` | takes, comping, punch-in, loop recording, repeats, undo, editing the song under takes |
| `plugins/hosting_vst3_plugins` | VST3 effects and instruments in a track's sound chain |
| `sync/keeping_audio_in_sync` | plugin delay compensation, latency, loops, Percentage of Tempo, tails |
| `saving/saving_audio_and_plugins` | saving and reopening; file format versions; other file formats |
| `export/exporting_audio` | exporting audio |

## How the scenarios run

The module is a headless audio engine: sound chains, takes, and a render clock. Scenarios drive it offline, on the
sample clock of a fake audio device (`Test Interface`), in blocks of 512 samples, faster than real time. The device's
clock runs while TuxGuitar is stopped too. "Recording from A to B" or "playing until B" stops on the exact sample of B.

The test plugins are real VST3 bundles built for the tests, so scanning, loading in the helper process and the
plugin's own editor are exercised for real; their audio behaviour is fixed below, so every number can be checked
exactly. One integration test outside the features loads the VST3 SDK's `again` example.

The module needs, besides `tuxguitar-lib`: `tuxguitar-editor-utils` (editing actions and undo), the synthesizer host
core and Gervill moved out of `desktop/TuxGuitar-synth` so they run headless, and at test scope `tuxguitar-midi` and
`tuxguitar-gtp` for the other-format scenarios. Once implemented, the module goes into the build-scripts POMs.

A past-tense `Given` checks that its action happened: "I have recorded from A to B" fails if the recording made no
take, so a scenario that then undoes it cannot pass when nothing was ever recorded.

## Changes to existing behaviour and code

These features change how TuxGuitar works today. They are requirements, not side effects:

- **A native audio backend** replaces Java Sound for the TuxGuitar Synthesizer. It opens the audio device chosen in
  TuxGuitar's settings at the device's sample rate, opens its input channels one by one, and reports the device's input
  and output latency. The audio between processors is 32-bit float, so a sound chain has room above full scale.
- **The sound chain is today's synthesizer program** (`TGProgram`: a MIDI receiver followed by audio processors,
  each enabled or disabled, moved up or down). VST3 effects and instruments are new processor types next to LV2's;
  LV2 entries appear in the readings and the numbering like VST3 ones.
- **Plugins run apart from TuxGuitar, with no network port.** VST3 and LV2 plugins run in a helper process that talks to
  TuxGuitar over pipes or shared memory and never listens on a network port. LV2 moves off its TCP socket
  (`TGRemoteHost`). Plugins load only from the VST3 (or LV2) folders. VST2 hosting was removed in 2024 because its
  bridge listened on a network port and was flagged as a Trojan (issue #312); the VST3 SDK is MIT-licensed since 3.8
  (October 2025), which fits TuxGuitar's LGPL-2.1.
- **Sample-accurate playback.** While the TuxGuitar Synthesizer plays, the audio device's sample clock drives the
  player, and every note reaches its instrument on its exact sample inside a block. The player reports a beat when the
  output sounds it, so the played beat is no longer highlighted early by the output buffer.
- **Seamless loops.** With Play looped on, the jump back to the start of the Loop Range is seamless: no restart, no gap,
  tails carry across. Count In sounds once, before the first pass, at the first pass's Percentage of Tempo.
- **Count In and Metronome** sound through the TuxGuitar Synthesizer's General MIDI drums, outside every sound chain,
  whether or not the song has a percussion channel, on the sample clock, and delayed like the tracks by plugin delay
  compensation.
- **File format 3.0** for a song with an audio track. A reader of format 2.0 refuses it with its "please upgrade"
  message. This version still opens format 2.0 files, and saves a song without audio tracks as format 2.0. A VST3 sound
  chain is saved under channel parameter keys older versions do not read, so they play the General MIDI program.
- **Audio export** gets sample rate and bit depth options next to its File Type and File Encoding.

## Rules every feature relies on

- **The TuxGuitar Synthesizer**: sound chains, audio tracks and VST3 plugins work only when the MIDI Port is
  "TuxGuitar Synthesizer". Any other MIDI Port bypasses them.
- **Takes are shared, never copied**: a take's samples never change once recorded. Cloning, pasting and undo snapshots
  share them, so undo does not multiply memory.
- **The song and the session**: arming, monitoring, input level, the punch range, the audio device and the recording
  offset are not part of the song. They are not saved, not undoable, and do not mark the song modified. Each audio
  track's input channel(s) and each track's next take number are part of the song.
- **During a recording** the song cannot be edited and Play Mode, arming and input channels cannot be changed; such
  actions are refused. Mute and solo still work: they change what is heard, never what is recorded.

## Glossary

### Time and positions

- **Sample**: one frame of audio at the audio device's sample rate. At 44100 Hz and 120 bpm a quarter note is 22050
  samples and a 4/4 measure 88200; at 48000 Hz they are 24000 and 96000.
- **Position**: `measure <m> beat <b>`, both from 1. A beat is the unit of the measure's time signature (an eighth in
  6/8, so 6/8 has 6 beats); `beat 2.5` is half way through beat 2. "The end of measure 4" is where measure 5 would
  start. Short form in readings: `m2b1`, `m2b2.5`.
- **Editing tempo and time signature**: "the tempo from measure 3 to the end" changes measures 3 to the last; "the time
  signature of measure 2 only" changes measure 2 alone.
- **Song time**: positions as the song plays them, following repeats and alternate endings. A **pass** is one time
  through a repeated or looped range. "Until the song ends" follows song time; "to the end of measure 4" is a position.
- **Percentage of Tempo, Play looped, Loop Range, Training Mode, Count In, Count In ticks, Metronome**: the existing
  Play Mode and transport settings. Training Mode raises the percentage after each pass of the Loop Range, from its
  start percentage to its end, by its increment.

### Tracks and sound

- **Audio track**: a track that sounds its takes instead of notes. It has no strings, holds no notes, and has a channel
  of its own that no other track shares and that does not use one of the 16 MIDI channels. It records from the input
  channel(s) set for it.
- **Sound chain**: what a track sounds through: a source followed by audio processors (VST3 or LV2 effects), in
  processing order. It belongs to the track's channel; tracks sharing a channel are summed into one chain.
  **Sound chain reading**: the source and the effects joined by ` > `, a disabled effect in brackets:
  `"Distortion Electric Guitar > Test Gain > [Test Clip 0.5]"`.
- **Source**: a General MIDI program played by the TuxGuitar Synthesizer (read by its name, e.g.
  `Distortion Electric Guitar`), a VST3 instrument (read by its plugin name), or, on an audio track, its takes (read
  `takes`).
- **VST3 effect**: a VST3 plugin that processes audio (an amp modeler, a pedal). Always "VST3 effect", never a bare
  "effect": TuxGuitar's "Effects" are note effects. "VST3 effect 2 of track 1" is the second effect in track 1's sound
  chain. A mono signal into an effect with a stereo input feeds both its inputs.
- **VST3 instrument**: a VST3 plugin that turns notes into sound; it replaces the General MIDI program as the source.
  Notes reach it with their velocity, and bends reach it per note, in exact semitones, so other notes ringing on the
  track are not bent.
- **Volume and Balance**: the channel's, applied after the sound chain. Balance 0 is full left, 127 full right.

### Recording

- **Audio device**: the audio interface TuxGuitar plays and records through, chosen in TuxGuitar's settings.
- **Input channel**: one input of the audio device. A take recorded from one input channel is mono; from a pair,
  stereo. Two audio tracks may record the same input channel.
- **Arm**: mark an audio track to record from its input channel(s). Only audio tracks can be armed.
- **Take**: one recording on an audio track over a stretch of song time with no jump in it: the dry input (effects are
  never recorded into it), stored as 32-bit float at the audio device's sample rate, anchored to the song's measures and
  beats. When the song's tempo differs from the tempo it was recorded at, it is time-stretched so its beats stay on the
  song's beats; at the tempo it was recorded at, it plays its samples unchanged. A recording that crosses a repeat, an
  alternate ending or the jump back of a loop makes one take per stretch. Takes get numbers in recording order from the
  track's next take number, which undo restores and deleting a take never lowers.
- **Takes reading**: a track's takes, newest first, as `take <n>: <from> to <to>` joined by `, `:
  `"take 2: m2b1 to m4b1, take 1: m1b1 to m3b1"`.
- **What a track plays**: at each point, the newest take covering it, unless I have chosen a take for those measures
  (comping): the take I chose last for a measure plays there. Choosing a take for measures applies to the measures it
  covers, and is refused if it covers none of them.
- **Playback reading**: which take a track plays, measure by measure: `"m1: take 1, m2: take 2, m3: take 2, m4: none"`.
  A measure where the take changes inside it is split by beat: `"m2b1: take 1, m2b3: take 2"`.
- **Crossfade**: where a track goes from one take to another, the take it goes to fades in over its first 256 samples
  while the other fades out. Where a take starts or ends against silence there is no fade.
- **Punch range**: measures outside which a recording keeps nothing. It stays on measure numbers and ends with the song.
- **Loop recording**: recording with Play looped on: each pass over the Loop Range records a take. One recording is
  one undo step, however many takes it makes.
- **Monitoring**: hearing an armed track's input through its sound chain. With monitoring on, the input is heard while
  stopped, and while recording inside the punch range (or everywhere, without one); while just playing, the track plays
  its takes. Monitoring is off when a track is armed.
- **Input level**: the peak of what an input channel delivered in the last second, in dBFS. At -0.1 dBFS or above the
  input is **clipping**; it shows **has clipped** until I reset it.
- **Latency**: how many samples late something delivers its sound: a plugin, the audio input or the audio output.
- **Recording offset**: a setting, in samples, added to the input latency the audio device reports, because drivers
  sometimes report it wrong.

### Sync

- **Plugin delay compensation**: delaying each track, the Count In and the Metronome so that all reach the output as
  late as the slowest track. A monitored input is not delayed by it. When a plugin's latency changes while playing, the
  tracks are together again from the next beat. Playing to the end of the song runs on until the most delayed track has
  sounded its last beat.
- **Time-stretch**: playing a take faster or slower without changing its pitch.
- **Tail**: what an effect still sounds after its input stops, as long as it reports (an echo, a reverb). Tails carry
  across the jump back of a loop and across repeats, and are cleared when playback stops or I start it elsewhere. An
  exported file runs on for the longest tail, at most 10 seconds.

### Measuring

- **A track's tap**: the audio of the track's channel where it joins the output mix: after its sound chain and plugin
  delay compensation, before Volume and Balance. "Track 2 sounds ..." is measured at track 2's tap.
- **The output**: what the audio device plays, left and right: every channel after Volume and Balance, the Count In and
  the Metronome. Count In and Metronome clicks are counted where they join the mix.
- **A track plays for N seconds**: how long its audio lasts as the song plays; **a take is N samples long**: how long it
  is stored.
- **Click**: a one-sample impulse. Its position is its first sample above half its peak; next to a tone, a click is a
  jump of more than 0.1 between two samples. A time-stretched click is no longer one sample.
- **Peak**: the largest absolute sample value, 1.0 being full scale (0 dBFS).
- **A tone within 1%**: by its fundamental frequency. **No ... above -60 dBFS**: by the level at that frequency.

## Test plugins and devices

"The VST3 folders hold the test plugins" installs all of these:

| Plugin | Kind | Does | Latency | Tail |
|---|---|---|---|---|
| `Test Gain` | VST3 effect | multiplies the signal by its parameter `Gain` (2.0 by default) | 0 | 0 |
| `Test Clip 0.5` | VST3 effect | clips the signal to plus or minus its parameter `Ceiling` (0.5 by default); no editor of its own | 0 | 0 |
| `Test Latency` | VST3 effect | delays the signal by its parameter `Latency` in samples (0 by default) | `Latency` | 0 |
| `Test Echo` | VST3 effect | adds one copy of the signal at half its level, 13230 samples (0.3 s) later | 0 | 13230 |
| `Test Drone` | VST3 effect | passes the signal and adds a 100 Hz tone at 0.1 forever | 0 | infinite |
| `Test Stereo` | VST3 effect | mono in, stereo out: the signal on the left, half of it on the right | 0 | 0 |
| `Test Tempo` | VST3 effect | passes the signal and records the tempo, time signature and play state it is given | 0 | 0 |
| `Test Sine` | VST3 instrument | a sine at each note's pitch, peaking at 0.5 × velocity / 127, following per-note tuning | 0 | 0 |
| `Test Click` | VST3 instrument | a click of +0.5 at the start of each note, delayed by its parameter `Latency` (0 by default) | `Latency` | 0 |

Each test plugin keeps a state of 1 KB that changes with its parameters and its preset, so saving can be checked byte
for byte, and has its own editor (except `Test Clip 0.5`) with presets. "The plugin sets X to v" is the plugin doing
it from its own editor, as one gesture (begin edit, changes, end edit). The helper process reports its process id and
open sockets, so "runs apart" and "listens on no network port" can be checked.

`Old Amp` is a VST 2 plugin placed in the VST3 folders by a scenario. `Test MIDI Out` is a MIDI Port other than the
TuxGuitar Synthesizer.

`Test Interface` (and `Other Interface`) are audio devices with the sample rate and input channels a scenario gives
them (44100 Hz and 2 by default). Each delivers the input late by its input latency and sounds the output late by its
output latency (0 by default), and reports those latencies unless a scenario says it reports something else.

Signals: "the player plays ..." is what reaches the input: the scenario's musician, playing in time with what they hear
at the output. "Track N has a take of ..." puts a take on the track directly, as if recorded at the song's tempo.
A signal is `clicks of -0.25 on every beat`, `one click of -0.25 at <position>`, `a <f> Hz tone peaking at <p>` (a
sine; `<p>` may be in dBFS; 0.4 when not given), `a tone per measure of <f1>, <f2>, ... Hz` (a different tone in each
measure, so a shift by whole beats shows), or `a constant level of <v>`. "Track N sounds 220, 330 and 440 Hz in
measures 1 to 3" means each measure sounds its tone, within 1%, and no other above -60 dBFS.

## Out of scope

- Failure handling: a plugin that crashes or hangs, a plugin missing on another machine, an audio device unplugged
  while recording, a song opened on a device whose sample rate differs from its takes'.
- Importing audio or backing tracks from Guitar Pro 7 files (they are dropped on import today).
- Android (it shares the file reader, so it refuses a song with audio tracks as older desktop versions do).
- Recording MIDI from a MIDI input.
- Input gain inside TuxGuitar: the input level is set on the audio device.
- Editing takes beyond what the features say (trimming, fades, gain, moving a take by hand).
- What the channel's Reverb, Chorus, Tremolo and Phaser do on audio tracks and with VST3 instruments.
- Which native audio library the backend uses.
