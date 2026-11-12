@planned
Feature: Takes
  A track's takes are stacked, newest on top, and at each point the track plays the topmost take that covers it.
  Choosing a take for some measures puts those measures of it on top. Takes stay on the song's measures and beats as I
  edit the song, the way notes do. Takes are numbered in recording order and never renumbered.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And the MIDI Port is "TuxGuitar Synthesizer"
    And the audio device is "Test Interface"
    And the VST3 folders hold the test plugins
    And I have added an audio track recording input channel 1
    And I have armed track 2

  # Stacking and comping

  Scenario: a second take goes on top of the first and keeps it
    Given I have recorded from measure 1 beat 1 to measure 3 beat 1
    When I record from measure 2 beat 1 to measure 4 beat 1
    Then track 2's takes read "take 2: m2b1 to m4b1, take 1: m1b1 to m3b1"
    And track 2's playback reads "m1: take 1, m2: take 2, m3: take 2, m4: none"

  Scenario: choosing a take for some measures puts them on top
    Given I have recorded from measure 1 beat 1 to the end of measure 4
    And I have recorded from measure 1 beat 1 to the end of measure 4
    And I have recorded from measure 1 beat 1 to the end of measure 4
    When I choose take 1 of track 2 for measures 2 to 2
    Then track 2's playback reads "m1: take 3, m2: take 1, m3: take 3, m4: take 3"

  Scenario: choosing a take for all its measures moves it to the top, keeping its number
    Given I have recorded from measure 1 beat 1 to measure 3 beat 1
    And I have recorded from measure 2 beat 1 to measure 4 beat 1
    When I choose take 1 of track 2 for measures 1 to 2
    Then track 2's takes read "take 1: m1b1 to m3b1, take 2: m2b1 to m4b1"
    And track 2's playback reads "m1: take 1, m2: take 1, m3: take 2, m4: none"

  Scenario: deleting a take uncovers the take below it, and the others keep their numbers
    Given I have recorded from measure 1 beat 1 to measure 3 beat 1
    And I have recorded from measure 2 beat 1 to measure 4 beat 1
    And I have recorded from measure 3 beat 1 to measure 4 beat 1
    When I delete take 2 of track 2
    Then track 2's takes read "take 3: m3b1 to m4b1, take 1: m1b1 to m3b1"
    And track 2's playback reads "m1: take 1, m2: take 1, m3: take 3, m4: none"

  Scenario: switching from one take to another crossfades
    Given track 2 has a take of a constant level of 0.4 from measure 1 beat 1 to measure 3 beat 1
    And track 2 has a take of a constant level of -0.4 from measure 2 beat 1 to measure 3 beat 1
    When I play the song from measure 1 beat 1
    Then track 2 goes from 0.4 to -0.4 over 220 samples within 1 sample, centred on measure 2 beat 1
    And no two consecutive samples of track 2 differ by more than 0.01 within 1000 samples of measure 2 beat 1

  Scenario Outline: choosing and deleting takes can be undone
    Given I have recorded from measure 1 beat 1 to measure 3 beat 1
    And I have recorded from measure 2 beat 1 to measure 4 beat 1
    And I have <changed>
    When I undo
    Then track 2's takes read "take 2: m2b1 to m4b1, take 1: m1b1 to m3b1"
    And track 2's playback reads "m1: take 1, m2: take 2, m3: take 2, m4: none"

    Examples:
      | changed                                       |
      | chosen take 1 of track 2 for measures 2 to 2  |
      | deleted take 2 of track 2                     |
      | deleted take 1 of track 2                     |

  Scenario: redoing a recording brings back the same take, sample for sample
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have recorded from measure 1 beat 1 to measure 2 beat 1
    And I have undone
    When I redo
    Then track 2's takes read "take 1: m1b1 to m2b1"
    And take 1 of track 2 is sample for sample what was recorded

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
      | from             | to                    | takes                  |
      | measure 1 beat 1 | the end of measure 4  | take 1: m2b1 to m4b1   |
      | measure 2 beat 3 | the end of measure 4  | take 1: m2b3 to m4b1   |
      | measure 1 beat 1 | measure 2 beat 3      | take 1: m2b1 to m2b3   |
      | measure 2 beat 1 | measure 4 beat 1      | take 1: m2b1 to m4b1   |
      | measure 1 beat 1 | measure 2 beat 1      |                        |

  Scenario: a punch range past the end of the song ends with the song
    When I set the punch range to measures 3 to 6
    Then the punch range is measures 3 to 4

  Scenario: outside the punch range I hear the take below, inside it the input
    Given track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to the end of measure 4
    And I have turned monitoring on for track 2
    And the player plays a 880 Hz tone peaking at 0.4 on input channel 1
    And I have set the punch range to measures 2 to 3
    When I record from measure 1 beat 1 to the end of measure 4
    Then track 2 sounds a 440 Hz tone and no 880 Hz tone above -60 dBFS in measures 1 and 4
    And track 2 sounds a 880 Hz tone and no 440 Hz tone above -60 dBFS in measures 2 to 3

  # Loop recording

  Scenario: with Play looped on, each pass over the Loop Range records a take, the last on top
    Given I have turned Play looped on over measures 2 to 3
    When I record from measure 2 beat 1 for 3 passes
    Then track 2's takes read "take 3: m2b1 to m4b1, take 2: m2b1 to m4b1, take 1: m2b1 to m4b1"

  Scenario: each loop pass's take holds what was played on that pass
    Given I have turned Play looped on over measures 2 to 3
    And the player plays a 440 Hz tone on the first pass and a 880 Hz tone on the second
    When I record from measure 2 beat 1 for 2 passes
    Then take 1 of track 2 holds a 440 Hz tone within 1%
    And take 2 of track 2 holds a 880 Hz tone within 1%

  Scenario: in Training Mode each pass is recorded at its own Percentage of Tempo and stored at the song's tempo
    Given I have turned Training Mode on from 80% to 100% by 10% over measures 2 to 3
    And the player plays clicks of -0.25 on every beat on input channel 1
    When I record from measure 2 beat 1 for 3 passes
    Then track 2 has 3 takes, each 4 seconds long at 100% within 5 ms
    And every take of track 2 holds 8 clicks, each within 5 ms of a beat at 100%

  # Repeats

  Scenario: a take on repeated measures plays on every pass
    Given measures 2 to 3 are repeated once
    And track 2 has a take of clicks of -0.25 on every beat from measure 1 beat 1 to the end of measure 4
    When I play the song from measure 1 beat 1
    Then the output holds 24 negative clicks, each within 1 sample of a beat in song time

  Scenario: recording through a repeat records a take per pass
    Given measures 2 to 3 are repeated once
    When I record from measure 1 beat 1 until the song ends
    Then track 2's takes read "take 2: m2b1 to m5b1, take 1: m1b1 to m4b1"

  # Editing the song under takes

  Scenario: a take moves with its measures when a measure is inserted before them
    Given I have recorded from measure 2 beat 1 to measure 3 beat 1
    When I insert a measure before measure 1
    Then track 2's takes read "take 1: m3b1 to m4b1"

  Scenario: a measure inserted inside a take is silent, and the rest of the take moves on
    Given I have recorded from measure 1 beat 1 to the end of measure 4
    When I insert a measure before measure 3
    Then track 2's playback reads "m1: take 1, m2: take 1, m3: none, m4: take 1, m5: take 1"

  Scenario: removing a measure inside a take removes its audio
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have recorded from measure 1 beat 1 to the end of measure 4
    When I remove measure 2
    Then track 2's playback reads "m1: take 1, m2: take 1, m3: take 1"
    And track 2 plays in measures 2 to 3 what it played in measures 3 to 4

  Scenario: removing a measure under a take and undoing brings its audio back sample for sample
    Given I have recorded from measure 1 beat 1 to the end of measure 4
    And I have removed measure 2
    When I undo
    Then take 1 of track 2 is sample for sample what was recorded
    And track 2's playback reads "m1: take 1, m2: take 1, m3: take 1, m4: take 1"

  Scenario: removing every measure a take covers removes the take
    Given I have recorded from measure 2 beat 1 to measure 3 beat 1
    When I remove measure 2
    Then track 2's takes read ""

  Scenario: pasting measures copies their audio
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have recorded from measure 1 beat 1 to measure 2 beat 1
    When I copy measure 1 and paste it over measure 3
    Then track 2 plays in measure 3 what it plays in measure 1

  Scenario: shortening a measure under a take cuts the audio of the beats it loses
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have recorded from measure 1 beat 1 to the end of measure 4
    When I set the time signature of measure 2 to 3/4
    Then track 2 plays in measure 2 the first 3 beats of what it played there
    And track 2 plays in measures 3 to 4 what it played there

  Scenario: editing the tempo under a take stretches it so its beats stay on the song's beats
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have recorded from measure 1 beat 1 to measure 3 beat 1
    When I set the tempo of measure 2 to 60 bpm
    Then track 2's takes read "take 1: m1b1 to m3b1"
    And take 1 of track 2 plays for 6 seconds within 5 ms
    And when the song plays, take 1 of track 2 sounds 8 clicks, each within 5 ms of a beat

  Scenario: editing the tempo under a take keeps its pitch
    Given the player plays a 440 Hz tone peaking at 0.4 on input channel 1
    And I have recorded from measure 1 beat 1 to measure 2 beat 1
    When I set the tempo of measure 1 to 60 bpm
    Then when the song plays, track 2 sounds a 440 Hz tone within 1% in measure 1
