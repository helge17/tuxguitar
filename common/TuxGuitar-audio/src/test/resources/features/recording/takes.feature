Feature: Takes
  A track plays, at each point, the newest take that covers it, unless I chose a take for those measures. A recording
  makes one take per stretch of song time without a jump, and is one undo step. Takes stay on the song's measures and
  beats as I edit the song, the way notes do. Takes are numbered in recording order and never renumbered.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And I have set the MIDI Port to "TuxGuitar Synthesizer"
    And I have set the audio device to "Test Interface"
    And the VST3 folders hold the test plugins
    And I have added an audio track recording input channel 1
    And I have armed track 2

  # Which take plays

  Scenario: a second take plays over the first where they overlap, and keeps it
    Given I have recorded from measure 1 beat 1 to measure 3 beat 1
    When I record from measure 2 beat 1 to measure 4 beat 1
    Then track 2's takes read "take 2: m2b1 to m4b1, take 1: m1b1 to m3b1"
    And track 2's playback reads "m1: take 1, m2: take 2, m3: take 2, m4: none"

  Scenario: choosing a take for some measures plays it there
    Given I have recorded from measure 1 beat 1 to the end of measure 4
    And I have recorded from measure 1 beat 1 to the end of measure 4
    And I have recorded from measure 1 beat 1 to the end of measure 4
    When I choose take 1 of track 2 for measures 2 to 2
    Then track 2's takes read "take 3: m1b1 to m5b1, take 2: m1b1 to m5b1, take 1: m1b1 to m5b1"
    And track 2's playback reads "m1: take 3, m2: take 1, m3: take 3, m4: take 3"

  Scenario: a new recording plays where it recorded, over a take I had chosen there
    Given I have recorded from measure 1 beat 1 to the end of measure 4
    And I have recorded from measure 1 beat 1 to the end of measure 4
    And I have chosen take 1 of track 2 for measures 2 to 3
    When I record from measure 3 beat 1 to measure 4 beat 1
    Then track 2's playback reads "m1: take 2, m2: take 1, m3: take 3, m4: take 2"

  Scenario: choosing a take for measures it only partly covers applies where it covers them
    Given I have recorded from measure 1 beat 1 to measure 3 beat 1
    And I have recorded from measure 2 beat 1 to the end of measure 4
    When I choose take 1 of track 2 for measures 2 to 4
    Then track 2's playback reads "m1: take 1, m2: take 1, m3: take 2, m4: take 2"

  Scenario: choosing a take for measures it does not cover is refused
    Given I have recorded from measure 1 beat 1 to measure 2 beat 1
    And I have recorded from measure 2 beat 1 to measure 3 beat 1
    When I choose take 2 of track 2 for measures 1 to 1
    Then the action is refused
    And track 2's playback reads "m1: take 1, m2: take 2, m3: none, m4: none"

  Scenario: deleting a take uncovers what is below it, and the others keep their numbers
    Given I have recorded from measure 1 beat 1 to measure 3 beat 1
    And I have recorded from measure 2 beat 1 to measure 4 beat 1
    And I have recorded from measure 3 beat 1 to measure 4 beat 1
    When I delete take 2 of track 2
    Then track 2's takes read "take 3: m3b1 to m4b1, take 1: m1b1 to m3b1"
    And track 2's playback reads "m1: take 1, m2: take 1, m3: take 3, m4: none"

  Scenario: a deleted take's number is not given again
    Given I have recorded from measure 1 beat 1 to measure 2 beat 1
    And I have recorded from measure 2 beat 1 to measure 3 beat 1
    And I have deleted take 2 of track 2
    When I record from measure 3 beat 1 to measure 4 beat 1
    Then track 2's takes read "take 3: m3b1 to m4b1, take 1: m1b1 to m2b1"

  Scenario: an undone recording's number is given again
    Given I have recorded from measure 1 beat 1 to measure 2 beat 1
    And I have recorded from measure 2 beat 1 to measure 3 beat 1
    And I have undone
    When I record from measure 3 beat 1 to measure 4 beat 1
    Then track 2's takes read "take 2: m3b1 to m4b1, take 1: m1b1 to m2b1"

  Scenario: going up to a newer take crossfades inside the newer take, after the boundary
    Given track 2 has a take of a constant level of 0.4 from measure 1 beat 1 to measure 3 beat 1
    And track 2 has a take of a constant level of -0.4 from measure 2 beat 1 to measure 3 beat 1
    When I play the song from measure 1 beat 1
    Then track 2 goes from 0.4 to -0.4 over the 256 samples from measure 2 beat 1, within 1 sample
    And no two consecutive samples of track 2 differ by more than 0.01 from measure 1 beat 1 to measure 3 beat 1

  Scenario: going back down to an older take crossfades inside the newer take, before the boundary
    Given track 2 has a take of a constant level of 0.4 from measure 1 beat 1 to measure 4 beat 1
    And track 2 has a take of a constant level of -0.4 from measure 2 beat 1 to measure 3 beat 1
    When I play the song from measure 1 beat 1
    Then track 2 goes from -0.4 to 0.4 over the 256 samples before measure 3 beat 1, within 1 sample
    And no two consecutive samples of track 2 differ by more than 0.01 from measure 2 beat 1 to measure 4 beat 1

  Scenario: a take chosen for a measure counts as the newer one at its boundaries
    Given track 2 has a take of a constant level of 0.4 from measure 1 beat 1 to measure 4 beat 1
    And track 2 has a take of a constant level of -0.4 from measure 1 beat 1 to measure 4 beat 1
    And I have chosen take 1 of track 2 for measures 2 to 2
    When I play the song from measure 1 beat 1
    Then track 2 goes from -0.4 to 0.4 over the 256 samples from measure 2 beat 1, within 1 sample
    And track 2 goes from 0.4 to -0.4 over the 256 samples before measure 3 beat 1, within 1 sample

  Scenario: between two takes chosen for neighbouring measures, the newer one's side crossfades
    Given track 2 has a take of a constant level of 0.4 from measure 1 beat 1 to measure 4 beat 1
    And track 2 has a take of a constant level of -0.4 from measure 1 beat 1 to measure 4 beat 1
    And I have chosen take 2 of track 2 for measures 2 to 2
    And I have chosen take 1 of track 2 for measures 3 to 3
    When I play the song from measure 1 beat 1
    Then track 2 goes from -0.4 to 0.4 over the 256 samples before measure 3 beat 1, within 1 sample
    And no two consecutive samples of track 2 differ by more than 0.01 from measure 2 beat 1 to measure 4 beat 1

  Scenario: at the jump back of a loop, the audio fades out before the jump and starts at full level after it
    Given track 2 has a take of a constant level of 0.4 from measure 1 beat 1 to the end of measure 4
    And I have turned Play looped on over measures 2 to 3
    When I play the song from measure 2 beat 1 for 2 passes
    Then track 2 fades from 0.4 to 0 over the last 256 samples of the first pass, within 1 sample
    And track 2's first sample of the second pass is 0.4

  Scenario: a take that starts against silence starts at full level
    Given track 2 has a take of a constant level of 0.4 from measure 2 beat 1 to measure 3 beat 1
    When I play the song from measure 1 beat 1
    Then track 2's first sample above 0 is 0.4, at measure 2 beat 1 within 1 sample

  # Undo

  Scenario Outline: choosing and deleting takes can be undone
    Given I have recorded from measure 1 beat 1 to measure 3 beat 1
    And I have recorded from measure 2 beat 1 to measure 4 beat 1
    And I have <changed>
    When I undo
    Then track 2's takes read "take 2: m2b1 to m4b1, take 1: m1b1 to m3b1"
    And track 2's playback reads "m1: take 1, m2: take 2, m3: take 2, m4: none"

    Examples:
      | changed                                      |
      | chosen take 1 of track 2 for measures 2 to 2 |
      | deleted take 2 of track 2                    |
      | deleted take 1 of track 2                    |

  Scenario: redoing a recording brings back the same take, sample for sample
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have recorded from measure 1 beat 1 to measure 2 beat 1
    And I have undone
    When I redo
    Then track 2's takes read "take 1: m1b1 to m2b1"
    And take 1 of track 2 is sample for sample what it was

  Scenario: undo steps back through recordings and sound chain changes in the order they were made
    Given I have recorded from measure 1 beat 1 to measure 2 beat 1
    And I have added the VST3 effect "Test Gain" to track 2
    And I have recorded from measure 2 beat 1 to measure 3 beat 1
    When I undo 2 times
    Then track 2's takes read "take 1: m1b1 to m2b1"
    And track 2's sound chain reads "takes"

  # Punch-in

  Scenario Outline: a recording keeps only what falls inside the punch range
    Given I have set the punch range to measures 2 to 3
    When I record from <from> to <to>
    Then track 2's takes read "<takes>"

    Examples:
      | from             | to                   | takes                |
      | measure 1 beat 1 | the end of measure 4 | take 1: m2b1 to m4b1 |
      | measure 2 beat 3 | the end of measure 4 | take 1: m2b3 to m4b1 |
      | measure 1 beat 1 | measure 2 beat 3     | take 1: m2b1 to m2b3 |
      | measure 2 beat 1 | measure 4 beat 1     | take 1: m2b1 to m4b1 |

  Scenario: a recording that never reaches the punch range keeps no take and adds no undo step
    Given I have set the punch range to measures 3 to 4
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then track 2's takes read ""
    And the undo history is as it was before the recording

  Scenario: a take that starts inside a measure splits the playback reading by beat
    Given I have recorded from measure 1 beat 1 to the end of measure 4
    And I have set the punch range to measures 2 to 3
    When I record from measure 2 beat 3 to the end of measure 4
    Then track 2's playback reads "m1: take 1, m2b1: take 1, m2b3: take 2, m3: take 2, m4: take 1"

  Scenario: a punch range past the end of the song ends with the song
    When I set the punch range to measures 3 to 6
    Then the punch range is measures 3 to 4

  Scenario: a punch range that starts past the end of the song is refused
    When I set the punch range to measures 5 to 6
    Then the action is refused
    And there is no punch range

  Scenario: a punch range stays on its measure numbers when measures are inserted
    Given I have set the punch range to measures 2 to 3
    When I insert a measure before measure 1
    Then the punch range is measures 2 to 3
    And the song has 5 measures

  Scenario: removing all the measures of a punch range removes it
    Given I have set the punch range to measures 3 to 4
    When I remove measures 3 to 4
    Then there is no punch range
    And the song has 2 measures

  Scenario: outside the punch range I hear the take below, inside it the input
    Given track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to the end of measure 4
    And I have set track 2's monitoring to Auto
    And the player plays a 880 Hz tone peaking at 0.4 on input channel 1
    And I have set the punch range to measures 2 to 3
    When I record from measure 1 beat 1 to the end of measure 4
    Then track 2 sounds a 440 Hz tone and no 880 Hz tone above -60 dBFS in measures 1 and 4
    And track 2 sounds a 880 Hz tone and no 440 Hz tone above -60 dBFS in measures 2 to 3, but for 256 samples at each end

  # Loop recording

  Scenario: with Play looped on, each pass over the Loop Range records a take
    Given I have turned Play looped on over measures 2 to 3
    When I record from measure 2 beat 1 for 3 passes
    Then track 2's takes read "take 3: m2b1 to m4b1, take 2: m2b1 to m4b1, take 1: m2b1 to m4b1"
    And track 2's playback reads "m1: none, m2: take 3, m3: take 3, m4: none"

  Scenario: each loop pass's take holds what was played on that pass
    Given I have turned Play looped on over measures 2 to 3
    And the player plays a 440 Hz tone on input channel 1 on the first pass and a 880 Hz tone on the second
    When I record from measure 2 beat 1 for 2 passes
    Then take 1 of track 2 holds a 440 Hz tone within 1% and no 880 Hz tone above -60 dBFS
    And take 2 of track 2 holds a 880 Hz tone within 1% and no 440 Hz tone above -60 dBFS

  Scenario: what I play late on a pass, by the input's latency, stays in that pass's take
    Given I have set the audio device to "Test Interface" with 300 samples of input latency and 1024 of output latency
    And I have turned Play looped on over measures 2 to 3
    And the player plays clicks of -0.25 on every beat on input channel 1
    When I record from measure 2 beat 1 for 2 passes
    Then take 1 of track 2 holds 8 clicks, each within 1 sample of a beat
    And take 2 of track 2 holds 8 clicks, each within 1 sample of a beat

  Scenario: loop recording is one undo step
    Given I have turned Play looped on over measures 2 to 3
    And I have recorded from measure 2 beat 1 for 3 passes
    When I undo
    Then track 2's takes read ""

  Scenario: stopping in the middle of a pass keeps that pass's take up to where I stopped
    Given I have turned Play looped on over measures 2 to 3
    When I record from measure 2 beat 1 and stop at measure 3 beat 2 of the second pass
    Then track 2's takes read "take 2: m2b1 to m3b2, take 1: m2b1 to m4b1"

  Scenario: Count In sounds only before the first pass
    Given I have turned Play looped on over measures 2 to 3
    And I have turned Count In on
    When I record from measure 2 beat 1 for 3 passes
    Then the output holds 4 Count In clicks, all before measure 2 beat 1 of the first pass

  Scenario: with a punch range, each loop pass keeps only what falls inside it
    Given I have turned Play looped on over measures 2 to 3
    And I have set the punch range to measures 3 to 4
    When I record from measure 2 beat 1 for 2 passes
    Then track 2's takes read "take 2: m3b1 to m4b1, take 1: m3b1 to m4b1"

  Scenario: in Training Mode each pass is recorded at its own Percentage of Tempo
    Given I have turned Training Mode on from 80% to 100% by 10% over measures 2 to 3
    And the player plays clicks of -0.25 on every beat on input channel 1
    When I record from measure 2 beat 1 for 3 passes
    Then takes 1, 2 and 3 of track 2 are 220500, 196000 and 176400 samples long
    And every take of track 2 holds 8 clicks, each within 1 sample of a beat at the Percentage it was recorded at

  # Repeats and alternate endings

  Scenario: a take on repeated measures plays on every pass
    Given I have repeated measures 2 to 3 once
    And track 2 has a take of clicks of -0.25 on every beat from measure 1 beat 1 to the end of measure 4
    When I play the song from measure 1 beat 1
    Then the output holds 24 clicks, each within 1 sample of a beat in song time

  Scenario: recording through a repeat makes a take per pass, and the second pass plays on both
    Given I have repeated measures 2 to 3 once
    When I record from measure 1 beat 1 until the song ends
    Then track 2's takes read "take 2: m2b1 to m5b1, take 1: m1b1 to m4b1"
    And track 2's playback reads "m1: take 1, m2: take 2, m3: take 2, m4: take 2"

  Scenario: recording through alternate endings makes a take at every jump
    Given I have repeated measure 2 once, with measure 3 as the first ending and measure 4 as the second
    When I record from measure 1 beat 1 until the song ends
    Then track 2's takes read "take 3: m4b1 to m5b1, take 2: m2b1 to m3b1, take 1: m1b1 to m4b1"

  # Editing the song under takes

  Scenario: a take moves with its measures when a measure is inserted before them
    Given I have recorded from measure 2 beat 1 to measure 3 beat 1
    When I insert a measure before measure 1
    Then track 2's takes read "take 1: m3b1 to m4b1"

  Scenario: a measure inserted inside takes is silent for all of them, and the takes span over it
    Given track 2 has a take of a tone per measure of 220, 330, 440 and 550 Hz from measure 1 beat 1 to the end of measure 4
    And track 2 has a take of a tone per measure of 660 and 770 Hz from measure 2 beat 1 to the end of measure 3
    And I have inserted a measure before measure 3
    When I play the song from measure 1 beat 1
    Then track 2's takes read "take 2: m2b1 to m5b1, take 1: m1b1 to m6b1"
    And track 2 sounds 220, 660, nothing, 770 and 550 Hz in measures 1 to 5

  Scenario: removing a measure inside a take removes its audio
    Given track 2 has a take of a tone per measure of 220, 330, 440 and 550 Hz from measure 1 beat 1 to the end of measure 4
    And I have removed measure 2
    When I play the song from measure 1 beat 1
    Then track 2 sounds 220, 440 and 550 Hz in measures 1 to 3

  Scenario: where a measure's audio is cut out of a take, the audio before the cut fades out
    Given track 2 has a take of a constant level of 0.4 from measure 1 beat 1 to the end of measure 4
    And I have removed measure 2
    When I play the song from measure 1 beat 1
    Then track 2 fades from 0.4 to 0 over the 256 samples before measure 2 beat 1, within 1 sample
    And track 2's sample at measure 2 beat 1 is 0.4

  Scenario: removing a measure under a take and undoing brings its audio back sample for sample
    Given track 2 has a take of a tone per measure of 220, 330, 440 and 550 Hz from measure 1 beat 1 to the end of measure 4
    And I have removed measure 2
    When I undo
    Then take 1 of track 2 is sample for sample what it was
    And track 2's takes read "take 1: m1b1 to m5b1"

  Scenario: removing every measure a take covers removes the take
    Given I have recorded from measure 2 beat 1 to measure 3 beat 1
    When I remove measure 2
    Then track 2's takes read ""

  Scenario: pasting a measure pastes the audio that plays there, as a new take
    Given track 2 has a take of a tone per measure of 220, 330, 440 and 550 Hz from measure 1 beat 1 to the end of measure 4
    And I have copied measure 1 and pasted it over measure 3
    When I play the song from measure 1 beat 1
    Then track 2's takes read "take 2: m3b1 to m4b1, take 1: m1b1 to m5b1"
    And track 2 sounds 220, 330, 220 and 550 Hz in measures 1 to 4

  Scenario: pasting a measure where two takes play pastes both as one new take
    Given track 2 has a take of a tone per measure of 220, 330, 440 and 550 Hz from measure 1 beat 1 to the end of measure 4
    And track 2 has a take of a 660 Hz tone peaking at 0.4 from measure 1 beat 3 to measure 2 beat 1
    And I have copied measure 1 and pasted it over measure 3
    When I play the song from measure 1 beat 1
    Then track 2's takes read "take 3: m3b1 to m4b1, take 2: m1b3 to m2b1, take 1: m1b1 to m5b1"
    And track 2 sounds 220 Hz then 660 Hz from beat 3 in measure 3

  Scenario: pasting a measure that plays nothing leaves the target's audio as it was
    Given track 2 has a take of a tone per measure of 220, 330 and 440 Hz from measure 2 beat 1 to the end of measure 4
    And I have copied measure 1 and pasted it over measure 3
    When I play the song from measure 1 beat 1
    Then track 2's takes read "take 1: m2b1 to m5b1"
    And track 2 sounds nothing, 220, 330 and 440 Hz in measures 1 to 4

  Scenario Outline: audio and notes are pasted only onto their own kind of track
    Given track 1 has a quarter note on every beat
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have copied measure 1 of track <from>
    When I paste it over measure 3 of track <to>
    Then the action is refused
    And <unchanged>

    Examples:
      | from | to | unchanged                              |
      | 2    | 1  | track 1 still has 4 notes in measure 3 |
      | 1    | 2  | track 2 holds no notes                 |

  Scenario Outline: changing a measure's time signature keeps the audio on its beats
    Given track 2 has a take of a tone per measure of 220, 330, 440 and 550 Hz from measure 1 beat 1 to the end of measure 4
    And I have set the time signature of measure 2 only to <signature>
    When I play the song from measure 1 beat 1
    Then track 2 sounds 330 Hz for the first <sounding> of measure 2 and nothing for the rest of it
    And track 2 sounds 440 and 550 Hz in measures 3 to 4

    Examples:
      | signature | sounding |
      | 3/4       | 3 beats  |
      | 5/4       | 4 beats  |
      | 8/8       | 8 beats  |

  Scenario: editing the tempo under a take stretches it so its beats stay on the song's beats
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have recorded from measure 1 beat 1 to measure 3 beat 1
    And I have set the tempo of measure 2 only to 60 bpm
    When I play the song from measure 1 beat 1
    Then track 2 plays for 6 seconds within 5 ms
    And track 2 sounds 8 clicks, each within 5 ms of a beat

  Scenario: a take that starts inside a measure stays on its beat when the tempo changes
    Given the player plays one click of -0.25 at measure 1 beat 3 on input channel 1
    And I have recorded from measure 1 beat 3 to measure 2 beat 1
    And I have set the tempo of measure 1 only to 60 bpm
    When I play the song from measure 1 beat 1
    Then track 2's takes read "take 1: m1b3 to m2b1"
    And track 2 sounds its click at measure 1 beat 3 within 5 ms

  Scenario: editing the tempo under a take keeps its pitch
    Given the player plays a 440 Hz tone peaking at 0.4 on input channel 1
    And I have recorded from measure 1 beat 1 to measure 2 beat 1
    And I have set the tempo of measure 1 only to 60 bpm
    When I play the song from measure 1 beat 1
    Then track 2 sounds a 440 Hz tone within 1% in measure 1

  Scenario: setting the tempo back to what a take was recorded at plays it sample for sample
    Given the player plays a 440 Hz tone peaking at 0.4 on input channel 1
    And I have recorded from measure 1 beat 1 to measure 2 beat 1
    And I have set the tempo of measure 1 only to 60 bpm
    And I have set the tempo of measure 1 only to 120 bpm
    When I play the song from measure 1 beat 1
    Then track 2 sounds take 1 sample for sample in measure 1
