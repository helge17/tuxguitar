# Audio features (BDD with Cucumber): audio tracks, line-in recording, VST3 hosting

Specification of audio tracks, line-in recording, VST3 effects and instruments, keeping audio in sync, saving and
exporting it. It is written ahead of the implementation: a scenario not implemented yet is tagged `@planned` (on the
scenario, on an `Examples` block, or on the whole feature), and the normal run leaves it out
(`src/test/resources/junit-platform.properties`). Remove the tag from a scenario when you start implementing it.
Implemented so far, rendered offline on the Test Interface: `tracks/audio_tracks`, `recording/takes`,
`recording/recording_line_in`, `plugins/hosting_vst3_plugins` and `sync/keeping_audio_in_sync`, but for the scenarios
still tagged `@planned` in them: takes, General MIDI programs, the mix, time-stretching, input channels and levels, the
round trip, refusals during a recording, Count In, monitoring, sound chains of VST3 effects and instruments in their
helper processes, and plugin delay compensation. Export is implemented headless (TGAudioExporter in this module; the
desktop export dialog does not call it yet). Saving is implemented in lib's TuxGuitar file format (format 3.0: a
`TGAudio` node in content.xml, each take a 32-bit float wave entry `takes/<id>.wav` written by this module's
`TGTakeWave`) and `TGAudioSaving.install`, which gives the take codec to the reader and writer and stores each VST3
plugin's state before the write action; the desktop does not call it yet. The player's position
is the one the output sounds: live, lib's sequencer on the synthesizer's clock reports the tick the output sounds
(chains' latency plus output latency behind what it schedules); offline, `TGReportedPositions` reports it block by
block the same way. The desktop does not feed the audio device's output latency in yet
(`TGSynthModel.setOutputLatency`).

The conventions are those of `common/TuxGuitar-editor-utils/src/test/resources/features/README.md`: first person, in
the words of someone using TuxGuitar; behaviour, never the GUI; the action under test is the one `When`; earlier actions
are past-tense `Given`s; boundaries in Scenario Outlines; every `Then` fails if the feature does nothing.

| What | Where |
|---|---|
| Feature files | `src/test/resources/features/<area>/<name>.feature` (this folder) |
| Step definitions | `src/test/java/app/tuxguitar/audio/features/*Steps.java` |
| Scenario state | `AudioWorld`: the song of a document (for undo), the Test Interface, remembered takes |
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
clock runs while TuxGuitar is stopped too.

Where playing and recording stop: "play the song from A" plays until the song ends; "for N passes" stops at the end of
pass N; "to B" or "until B" stops at B. All of them run on until the most delayed track has sounded everything before
the stop point, and a recording keeps taking input for one round trip after it, so what was played before the stop
point is in the take. That run-on is TuxGuitar's, not the step's: the step captures the output until TuxGuitar reports
it has stopped. "Stop at X" stops at once; "stop after X" stops at the first block after X sounds, but "stop after the
second Count In click" stops half a beat after that click's note reaches the drums, clear of its attack and of the third
click. "And do not stop it"
waits up to 10 seconds past the end of the song for TuxGuitar to stop on its own, and fails if it does not. A range
"from A to B" includes A and ends just before B. "I am recording from A" and "I am playing the song from A" mean that
recording or playing from A is under way when the `When` happens.

The test plugins are real VST3 bundles built for the tests, so scanning, loading in the helper process and the
plugin's own editor are exercised for real; their audio behaviour is fixed below, so every number can be checked
exactly. One integration test outside the features loads the VST3 SDK's `again` example. Their C++ source, and the
helper process's, is in `common/TuxGuitar-vst3/native`: before the tests of this module (and of `TuxGuitar-vst3`) Maven
runs its build (CMake, against the VST3 SDK pinned there) into `common/TuxGuitar-vst3/native-build`, once, and stops
saying what to install when no C++ toolchain is found. No binary is committed.

The module needs, besides `tuxguitar-lib`: `tuxguitar-editor-utils` (editing actions and undo), the synthesizer host
core and Gervill moved out of `desktop/TuxGuitar-synth` so they run headless, and at test scope `tuxguitar-midi` and
`tuxguitar-gtp` for the other-format scenarios. Sounding a note as it is entered is desktop code today
(`TGUpdateModifiedNoteController` calls `MidiPlayer.playBeat`); the module rebuilds that listener. Once implemented, the
module goes into the build-scripts POMs.

A past-tense `Given` checks that its action happened: "I have recorded from A to B" fails if the recording made no
take, so a scenario that then undoes it cannot pass when nothing was ever recorded.

## Changes to existing behaviour and code

These features change how TuxGuitar works today. They are requirements, not side effects:

- **A native audio backend** replaces Java Sound for the TuxGuitar Synthesizer. It opens the audio device chosen in
  TuxGuitar's settings at the device's sample rate, opens its input channels one by one, and reports the device's input
  and output latency. The audio between processors is 32-bit float, so a sound chain has room above full scale.
- **The sound chain is today's synthesizer program** (`TGProgram`: a MIDI receiver followed by audio processors,
  each enabled or disabled, moved up or down). VST3 effects and instruments are new processor types next to LV2's;
  LV2 entries appear in the readings and the numbering like VST3 ones. When the source is a General MIDI program it
  follows the channel's Bank and Program, and the sound chain reading names it from them. Today a custom program
  freezes Gervill's own `bank:program` and ignores Program changes; that stops, and a format 2.0 file whose program
  holds a different `bank:program` plays the channel's Program (the program's soundbank and channel mode are kept). The
  per-instrument presets in the synthesizer settings (`synth.program.<bank>.<program>`) are not changed by this
  feature: they apply where they apply today.
- **Plugins run apart from TuxGuitar, with no network port.** VST3 and LV2 plugins run in a helper process that talks to
  TuxGuitar over pipes or shared memory and never listens on a network port. For VST3 the helper's commands and replies
  go over the helper's own standard input and output (anonymous pipes: no name, so no other process can connect to
  them), and the audio stays in shared memory (a file both map); there is no named pipe, Unix socket or network port.
  Each VST3 plugin runs in a helper process of its own, so a plugin that crashes or hangs silences only itself; it is
  opened again, with its last state, the next time its sound chain opens.
  LV2 moves off its TCP socket
  (`TGRemoteHost`). Plugins load only from the VST3 (or LV2) folders. VST2 hosting was removed in 2024 because its
  bridge listened on a network port and was flagged as a Trojan (issue #312); the VST3 SDK is MIT-licensed since 3.8
  (October 2025), which fits TuxGuitar's LGPL-2.1.
- **Sample-accurate playback.** While the TuxGuitar Synthesizer plays, the audio device's sample clock drives the
  player, and every note reaches its instrument on its exact sample inside a block. The player reports a beat when the
  output sounds it (the plugin delay compensation and the output latency after the beat's events), so the played beat
  is no longer highlighted early by the output buffer. Live and offline the same
  synthesizer code times the events: before each block the sequencer sends the block's events, each on its frame (a
  note starts on that sample; Gervill starts its other events with the control period that holds it). Gervill's
  stream sounds what it renders two control periods later (600 samples at 44100 Hz): that is the latency of a sound
  chain with a General MIDI source, counted like a plugin's by plugin delay compensation and so by the round trip;
  offline each track's chain latency is taken out, so a note is heard from its sample. A MIDI Port other than the
  TuxGuitar Synthesizer keeps today's timing.
- **Seamless loops.** With Play looped on, the jump back to the start of the Loop Range is seamless: no restart, no gap,
  tails carry across. Count In sounds once, before the first pass, at the first pass's Percentage of Tempo.
- **Count In and Metronome** sound through the TuxGuitar Synthesizer's General MIDI drums, outside every sound chain,
  whether or not the song has a percussion channel, on the sample clock, at the exact Percentage of Tempo (today's
  Count In rounds the tempo down to a whole bpm), and delayed like the tracks by plugin delay compensation. A Count In
  or Metronome click's position is the sample on which its note reaches the drums. The drums join the output at the
  Volume and Balance of the song's percussion channel, the channel they click on today, with the tracks' law; a song
  without one (today it has no Count In and no Metronome) clicks as a new channel sounds: Volume 127, Balance 64.
- **File format 3.0** for a song with an audio track. A reader of format 2.0 opening it as a `.tg` file refuses it with
  its "please upgrade" message (the step compares the message of the innermost cause). This version still opens format 2.0 files, and saves
  a song without audio tracks as format 2.0. A VST3 sound chain is saved under channel parameter keys outside the
  `synth.program` prefix, which older versions keep but do not use, so they play the General MIDI program. Which
  message an older version gives depends on the release: the released 2.1.x readers check the zip's entries before the
  version and refuse a 3.0 file with "Unsupported file format"; 2.0.x, and versions from this one on (whose format
  detector takes every major version from 2 up, and whose reader checks the version first), say "please upgrade".
- **Audio export** gets sample rate and bit depth options (16 bits, 24 bits, 32-bit float) next to its File Type
  (as today: WAVE, AIFF, AU) and File Encoding; 32-bit float is the PCM_FLOAT encoding it offers today. Takes recorded at
  another sample rate are resampled. Exporting always renders through the TuxGuitar Synthesizer, whatever the MIDI
  Port. The file runs on for the longest tail of an effect or a VST3 instrument that is heard; tails of muted or
  left-out tracks and of disabled effects do not count. Along one sound chain the tails add up (an echo of an echo rings
  for both); an endless tail counts as 10 seconds.

## Rules every feature relies on

- **The TuxGuitar Synthesizer**: while playing and recording, sound chains, audio tracks and VST3 plugins work only
  when the MIDI Port is "TuxGuitar Synthesizer"; any other MIDI Port bypasses them. Exporting audio always renders
  through the TuxGuitar Synthesizer.
- **Takes are shared, never copied**: a take's samples never change once recorded. Cloning, pasting and undo snapshots
  share them, so undo does not multiply memory.
- **The song and the session**: arming, monitoring, input level, the punch range, the audio device and the recording
  offset are not part of the song. A track's monitoring is `Off` until I set it, and is kept while the track is
  disarmed and armed again. They are not saved, not undoable, and do not mark the song modified. Each audio
  track's input channel(s) and each track's next take number are part of the song.
- **During a recording** the song cannot be edited (moving a track and copying a song in included) and Play Mode, the
  punch range, arming and input channels cannot be changed; such actions are refused. Mute, solo, a channel's Volume and
  Balance, monitoring and gestures in a plugin's editor still work; they change what is heard, never what is recorded. An undo step made during a recording comes before the recording's.
- **Recording stops at the end of the song**: to keep the ring of a last chord, add an empty measure.

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
  start percentage to its end, by its increment. Count In starts playing: its first click is where the output starts.
- **Click sound**: the drum note and velocity Count In and the Metronome play, a TuxGuitar setting (not saved in the
  song, not undoable): Side Stick (note 37) at velocity 95 until I change it.

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
  chain. A mono signal into an effect with a stereo input feeds both its inputs; a stereo signal into an effect with a
  mono input is fed as the mean of its two sides.
- **VST3 instrument**: a VST3 plugin that turns notes into sound; it replaces the General MIDI program as the source.
  Notes reach it with their velocity, and bends reach it per note, in exact semitones, so other notes ringing on the
  track are not bent.
- **Volume and Balance**: the channel's, applied after the sound chain, with today's laws. Balance 0 is full left, 127
  full right, 64 the centre (today's constant-power law leaves the two sides up to 1.3% apart there). Volume 127 with
  Balance 0 or 127 passes the signal unchanged to its side. On a stereo signal the same law applies to each side: the
  left side is scaled as a mono signal's left, the right as its right.

### Recording

- **Audio device**: the audio interface TuxGuitar plays and records through, chosen in TuxGuitar's settings.
- **Input channel**: one input of the audio device. A take recorded from one input channel is mono; from a pair,
  stereo. Two audio tracks may record the same input channel.
- **Arm**: mark an audio track to record from its input channel(s). Only audio tracks can be armed.
- **Take**: one recording on an audio track over a stretch of song time with no jump in it: the dry input (effects are
  never recorded into it), stored as 32-bit float at the audio device's sample rate, anchored measure by measure: each
  measure's audio sits at its offset from the measure's start in quarter-note time (a measure changed from 4/4 to 8/8
  keeps its audio where it was; audio past the end of a shortened measure is not heard, and comes back only by undo).
  When the tempo the song sounds at (its tempo × Percentage of Tempo) differs from the one the take was recorded at, the
  take is time-stretched so its beats stay on the song's beats; at the same tempo it plays its samples unchanged. A recording that crosses a repeat, an
  alternate ending or the jump back of a loop makes one take per stretch. Takes get numbers in recording order from the
  track's next take number, which undo restores and deleting a take never lowers.
- **Takes reading**: a track's takes, newest first, as `take <n>: <from> to <to>` joined by `, `:
  `"take 2: m2b1 to m4b1, take 1: m1b1 to m3b1"`.
- **What a track plays**: at each point, the newest take covering it, unless I have chosen a take for those measures
  (comping): the take I chose last for a measure plays there. Choosing a take for measures applies to the measures it
  covers, and is refused if it covers none of them. A new recording plays where it recorded: it clears the choices
  there. A cloned track keeps its takes, its choices and its next take number.
- **Editing measures under takes**: takes follow their measures like notes. A measure inserted inside takes is silent
  for all of them, and a take spans over it. Removing a measure removes its audio. Pasting a measure pastes the audio
  that plays there as one new take sharing the samples (even when two takes play in it); pasting a measure that plays
  nothing leaves the target's audio as it was. Audio cannot be pasted onto a guitar track, nor notes onto an audio
  track.
- **Playback reading**: which take a track plays, measure by measure: `"m1: take 1, m2: take 2, m3: take 2, m4: none"`.
  A measure where the take changes inside it is split by beat: `"m2b1: take 1, m2b3: take 2"`.
- **Crossfade**: where a track goes from one take to another, the fade lies on the newer take's side of the boundary
  (a take chosen for a measure counts as the newer there; between takes chosen for neighbouring measures, the newer is
  the one recorded later): going to a newer take, it fades in over its first 256 samples while the older fades out;
  going back to an older take, the newer fades out over its last 256 samples before the boundary while the older fades
  in. Where a take starts or ends against silence there is no fade, except between two chosen takes: there the newer
  one fades on its side even where the older has no audio under the fade (it just fades in, or out). At a jump in song
  time (a loop's jump back, a repeat, an alternate ending), and where a take's audio is cut inside it (a removed
  measure, an inserted one, a shortened measure), the audio before fades out over its last 256 samples and the audio
  after starts at full level. At a punch range boundary, the monitored input counts as the newer take.
- **Punch range**: measures outside which a recording keeps nothing. It stays on measure numbers and ends with the
  song; one that starts past the end of the song is refused, and removing all its measures removes it.
- **Loop recording**: recording with Play looped on: each pass over the Loop Range records a take. One recording is
  one undo step, however many takes it makes.
- **Monitoring**: hearing an armed track's input through its sound chain. Each audio track's monitoring is `Off`,
  `Auto` or `Always`; it only matters while the track is armed. `Auto`: the input is heard while stopped, and while
  recording inside the punch range (or everywhere, without one); while just playing, the track plays its takes.
  `Always`: the input is heard whenever the track is armed, and the track's takes are not, so I can practise along with
  the song without recording. `Off`: the input is never heard. Whatever the monitoring, while recording the armed
  track's takes are silent where it records (inside the punch range, or everywhere without one).
- **Input level**: the peak of what a track's input channel(s) delivered in the last second, in dBFS (the higher of
  the two for a pair). At -0.1 dBFS or above the input is **clipping**; it shows **has clipped** until I reset it.
- **Round trip**: output latency + the plugin delay compensation of what I hear + input latency + recording offset.
  A take is placed earlier by its round trip, so one recorded against General MIDI tracks or the Count In lands on the
  beat although Gervill sounds them 600 samples late (at 44100 Hz).
- **Latency**: how many samples late something delivers its sound: a plugin, the audio input or the audio output.
- **Recording offset**: a setting, in samples, added to the input latency the audio device reports, because drivers
  sometimes report it wrong.

### Sync

- **Plugin delay compensation**: delaying each track, the Count In and the Metronome so that all reach the output as
  late as the slowest track: the slowest sound chain's latency, Gervill's included, a muted track's too, and the
  General MIDI drums' (Count In and Metronome) whether or not they click. A monitored input is not delayed by it. When a plugin's latency changes while playing, the
  tracks are together again from the next beat (TuxGuitar realigns them at the next block it renders). Playing to the end of the song runs on until the most delayed track has
  sounded its last beat.
- **Time-stretch**: playing a take faster or slower without changing its pitch.
- **Tail**: what an effect still sounds after its input stops, as long as it reports (an echo, a reverb). Tails carry
  across the jump back of a loop and across repeats, and are cleared when playback stops or I start it elsewhere. An
  exported file runs on for the longest tail, at most 10 seconds.

### Measuring

- **A track's tap**: the audio of the track's channel where it joins the output mix: after its sound chain and plugin
  delay compensation, before Volume and Balance. "Track 2 sounds ..." is measured at track 2's tap.
- **The output**: what the audio device plays, left and right: every channel after Volume and Balance, the Count In and
  the Metronome.
- **The drum tap**: the General MIDI drums Count In and the Metronome play on, where they join the mix. Count In and
  Metronome clicks are counted there, in two parts: a click's position is the sample on which TuxGuitar sends its note to
  the drums (a drum's attack is no click, so the position is not measured from the audio), and each click must sound on
  the drum tap, and in the output, above -60 dBFS within its beat. **Every Count In click sounds drum note N at velocity
  V**: the drum tap is, sample for sample, what the General MIDI drums sound playing that note at that velocity on the
  clicks' samples.
- **A track plays for N seconds**: how long its audio lasts as the song plays; **a take is N samples long**: how long it
  is stored.
- **Click** (in a take or a signal): a one-sample impulse. Its position is its first sample above half its peak; next to a tone, a click is a
  jump of more than 0.1 between two samples. A time-stretched click is no longer one sample.
- **Peak**: the largest absolute sample value, 1.0 being full scale (0 dBFS).
- **A tone within 1% (or 1 Hz)**: by its fundamental frequency. **No ... above -60 dBFS**: by the level at that
  frequency. **Nothing**, in a list of tones per measure: no tone above -60 dBFS there.
- **Nothing else above -60 dBFS**: no sample other than the named signals above 0.001.
- **Musical position**: quarter notes from the start of the song, in song order as written (measure 2 beat 1 of a
  4/4 song is quarter-note position 4).
- **TuxGuitar reports a beat**: the beat the player shows as played (the played-beat highlight).
- **A beat at P%**: where the beat falls when the song plays at P% Percentage of Tempo.

## Test plugins and devices

"The VST3 folders hold the test plugins" installs all of these:

| Plugin | Kind | Does | Latency | Tail |
|---|---|---|---|---|
| `Test Gain` | VST3 effect | multiplies the signal by its parameter `Gain` (2.0 by default) | 0 | 0 |
| `Test Clip 0.5` | VST3 effect | clips the signal to plus or minus its parameter `Ceiling` (0.5 by default); no editor of its own | 0 | 0 |
| `Test Latency` | VST3 effect | delays the signal by its parameter `Latency` in samples (0 by default) | `Latency` | 0 |
| `Test Echo` | VST3 effect | adds one copy of the signal at half its level, 0.3 s later (13230 samples at 44100 Hz) | 0 | 0.3 s |
| `Test Drone` | VST3 effect | passes the signal and adds a 100 Hz tone at 0.1 forever | 0 | infinite |
| `Test Hang` | VST3 effect | passes the signal, and hangs once: the block holding the sample its parameter `Hang at` counts to (samples processed since it started; never by default) comes back `Hang for` milliseconds later (never by default) | 0 | 0 |
| `Test Stereo` | VST3 effect | mono in, stereo out: the signal on the left, half of it on the right | 0 | 0 |
| `Test Tempo` | VST3 effect | passes the signal and records the tempo, time signature, play state and musical position it is given | 0 | 0 |
| `Test Sine` | VST3 instrument | a sine at each note's pitch, peaking at 0.5 × velocity / 127, following per-note tuning | 0 | 0 |
| `Test Click` | VST3 instrument | a click of +0.5 at the start of each note, delayed by its parameter `Latency` (0 by default) | `Latency` | 0 |

Each test plugin keeps a state of 1 KB that changes with its parameters and its preset, so saving can be checked byte
for byte, and has its own editor (except `Test Clip 0.5`) with presets; `Test Gain`'s preset `Loud` sets `Gain` to 4.0. "The plugin sets X to v" is the plugin doing
it from its own editor, as one gesture (begin edit, changes, end edit). The helper process reports its process id and
open sockets, so "runs apart" and "listens on no network port" can be checked.

`Old Amp` is a VST 2 plugin placed in the VST3 folders by a scenario. `Test MIDI Out` is a MIDI Port other than the
TuxGuitar Synthesizer.

`Test Interface` (and `Other Interface`) are audio devices with the sample rate and input channels a scenario gives
them (44100 Hz and 2 by default). Each delivers the input late by its input latency and sounds the output late by its
output latency (0 by default), and reports those latencies unless a scenario says it reports something else.

Signals: "the player plays ..." is what reaches the input: the scenario's musician (not TuxGuitar's player), playing
in time with what they hear at the output. "Track N has a take of ..." puts a take on the track directly, as if
recorded at the song's tempo. A signal is `clicks of -0.25 on every beat`, `one click of -0.25 at <position>`,
`a <f> Hz tone peaking at <p>` (a sine; `<p>` may be in dBFS; 0.4 when not given), `a tone per measure of <f1>, <f2>,
... Hz` (a different tone in each measure from the take's first, one per measure it covers, so a shift by whole beats
shows), or `a constant level of <v>`. `<signal> for <duration>, then <signal>` plays one after the other;
`<signal> on the first pass and <signal> on the second` changes with the pass. "Track N sounds 220, 330 and 440 Hz in
measures 1 to 3" means each measure sounds its tone, within 1%, and no other above -60 dBFS.

## Out of scope

- Failure handling: a plugin that crashes or hangs, a plugin missing on another machine, an audio device unplugged
  while recording, a song opened on a device whose sample rate differs from its takes'. One narrow exception: a VST3
  plugin that hangs goes silent for each block it does not answer in time while the other tracks keep playing on their
  beats, and is restarted (on its own, with its last state) after a longer wait. An offline render (an export) waits for each block
  instead, and ends only a plugin that has not answered for 2 seconds.
- Importing audio or backing tracks from Guitar Pro 7 files (they are dropped on import today).
- Android (it shares the file reader, so it refuses a song with audio tracks as older desktop versions do).
- Recording MIDI from a MIDI input.
- Input gain inside TuxGuitar: the input level is set on the audio device.
- Editing takes beyond what the features say (trimming, fades, gain, moving a take by hand).
- What the channel's Reverb, Chorus, Tremolo and Phaser do on audio tracks and with VST3 instruments.
- Which native audio library the backend uses.
