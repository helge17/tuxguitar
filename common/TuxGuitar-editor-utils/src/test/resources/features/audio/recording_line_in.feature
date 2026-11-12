@planned
Feature: Recording line-in audio
  An armed audio track records what its audio input delivers while the song plays, as a take.
  A take starts where recording started and sits on the song's measures, so it plays back with the score.
  A take keeps the dry input: the track's effects shape what I hear, never what is recorded.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And the audio input "Test Interface" with 2 input channels
    And I have chosen input channel 1 of "Test Interface" as the audio input
    And the input plays a click on every beat of the song

  Scenario: a new audio track has no instrument, no strings and no takes
    When I add an audio track
    Then the song has 2 tracks
    And track 2 is an audio track with 0 takes
    And track 2's sound chain reads "takes"
    And the action succeeds

  Scenario: an audio track holds no notes
    Given I have added an audio track
    And the caret is on track 2
    When I enter fret 5 on string 1
    Then the action is refused

  Scenario: an armed audio track records a take from where recording started
    Given I have added an audio track
    And I have armed track 2
    When I record from measure 2 beat 1 to measure 4 beat 1
    Then track 2 has 1 take
    And take 1 of track 2 runs from measure 2 beat 1 to measure 4 beat 1
    And every click in take 1 of track 2 lies within 1 sample of its beat

  Scenario: a take ends where I stopped, not on a measure line
    Given I have added an audio track
    And I have armed track 2
    When I record from measure 1 beat 1 to measure 2 beat 3
    Then take 1 of track 2 runs from measure 1 beat 1 to measure 2 beat 3

  Scenario: recording stops at the end of the song
    Given I have added an audio track
    And I have armed track 2
    When I record from measure 3 beat 1 without stopping
    Then take 1 of track 2 runs from measure 3 beat 1 to the end of measure 4

  Scenario: recording with no armed track is refused
    Given I have added an audio track
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then the action is refused
    And track 2 has 0 takes

  Scenario: a guitar track cannot be armed
    When I arm track 1
    Then the action is refused

  Scenario: Count In plays before recording and is not recorded
    Given Count In is on
    And I have added an audio track
    And I have armed track 2
    When I record from measure 2 beat 1 to measure 3 beat 1
    Then 4 Count In clicks sound before measure 2 beat 1
    And take 1 of track 2 runs from measure 2 beat 1 to measure 3 beat 1
    And every click in take 1 of track 2 lies within 1 sample of its beat

  Scenario: Count In follows the time signature of the measure recording starts in
    Given measure 2 is in 3/4
    And Count In is on
    And I have added an audio track
    And I have armed track 2
    When I record from measure 2 beat 1 to measure 3 beat 1
    Then 3 Count In clicks sound before measure 2 beat 1

  Scenario: a take follows the song's tempo changes
    Given measure 3 is at 60 bpm
    And I have added an audio track
    And I have armed track 2
    When I record from measure 1 beat 1 to the end of measure 4
    Then every click in take 1 of track 2 lies within 1 sample of its beat

  Scenario: recording ignores the Play Mode
    Given the Percentage of Tempo is 80
    And Play looped is on over the Loop Range of measures 2 to 3
    And I have added an audio track
    And I have armed track 2
    When I record from measure 1 beat 1 to measure 4 beat 1
    Then take 1 of track 2 runs from measure 1 beat 1 to measure 4 beat 1
    And every click in take 1 of track 2 lies within 1 sample of its beat

  Scenario Outline: a take records the input channels I chose
    Given I have chosen input channels <channels> of "Test Interface" as the audio input
    And I have added an audio track
    And I have armed track 2
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then take 1 of track 2 is <shape>

    Examples:
      | channels | shape  |
      | 1        | mono   |
      | 2        | mono   |
      | 1 and 2  | stereo |

  Scenario: two armed tracks record their own input channels at once
    Given the input plays a click on every beat on channel 1 and a 440 Hz tone on channel 2
    And I have added an audio track recording input channel 1 of "Test Interface"
    And I have added an audio track recording input channel 2 of "Test Interface"
    And I have armed tracks 2 and 3
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then take 1 of track 2 holds the clicks
    And take 1 of track 3 holds the 440 Hz tone

  Scenario: the input level shows the peak of what the input delivers
    Given I have added an audio track
    And I have armed track 2
    And the input plays a 440 Hz tone peaking at -6 dBFS
    When I listen to the input
    Then track 2's input level reads -6 dBFS within 0.1 dB
    And track 2's input is not clipping

  Scenario: an input that reaches full scale is shown as clipping
    Given I have added an audio track
    And I have armed track 2
    And the input plays a 440 Hz tone peaking at 0 dBFS
    When I listen to the input
    Then track 2's input is clipping

  Scenario: with monitoring on I hear the input through the track's effects
    Given I have added an audio track
    And I have added the VST3 effect "Test Gain" to track 2
    And I have armed track 2
    And monitoring is on for track 2
    And the input plays a 440 Hz tone peaking at 0.4
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then the output sounds a 440 Hz tone peaking at 0.8 within 0.01
    And take 1 of track 2 holds a 440 Hz tone peaking at 0.4 within 0.01

  Scenario: with monitoring off the input is recorded but not heard
    Given I have added an audio track
    And I have armed track 2
    And monitoring is off for track 2
    And the input plays a 440 Hz tone peaking at 0.4
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then the output sounds no 440 Hz tone
    And take 1 of track 2 holds a 440 Hz tone peaking at 0.4 within 0.01

  Scenario: a second take is kept on top of the first
    Given I have added an audio track
    And I have armed track 2
    And I have recorded from measure 1 beat 1 to measure 3 beat 1
    When I record from measure 2 beat 1 to measure 4 beat 1
    Then track 2 has 2 takes
    And track 2's takes read "take 2: measure 2 beat 1 to measure 4 beat 1, take 1: measure 1 beat 1 to measure 3 beat 1"
    And track 2 plays take 1 in measure 1 and take 2 in measures 2 to 3

  Scenario: choosing a take puts it on top
    Given I have added an audio track
    And I have armed track 2
    And I have recorded from measure 1 beat 1 to measure 3 beat 1
    And I have recorded from measure 2 beat 1 to measure 4 beat 1
    When I choose take 1 of track 2
    Then track 2 plays take 1 in measures 1 to 2 and take 2 in measure 3

  Scenario: deleting a take uncovers the take below it
    Given I have added an audio track
    And I have armed track 2
    And I have recorded from measure 1 beat 1 to measure 3 beat 1
    And I have recorded from measure 2 beat 1 to measure 4 beat 1
    When I delete take 2 of track 2
    Then track 2 has 1 take
    And track 2 plays take 1 in measures 1 to 2

  Scenario: punch-in records only inside the punch range
    Given I have added an audio track
    And I have armed track 2
    And I have recorded from measure 1 beat 1 to the end of measure 4
    And the punch range is measures 2 to 3
    When I record from measure 1 beat 1 to the end of measure 4
    Then track 2 has 2 takes
    And take 2 of track 2 runs from measure 2 beat 1 to the end of measure 3
    And track 2 plays take 1 in measures 1 and 4 and take 2 in measures 2 to 3

  Scenario: a punch range the recording never reaches records nothing
    Given I have added an audio track
    And I have armed track 2
    And the punch range is measures 3 to 4
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then track 2 has 0 takes

  Scenario: undo removes the take a recording made and redo brings it back
    Given I have added an audio track
    And I have armed track 2
    And I have recorded from measure 1 beat 1 to measure 2 beat 1
    When I undo
    Then track 2 has 0 takes
    When I redo
    Then track 2 has 1 take
    And take 1 of track 2 is sample for sample what was recorded

  Scenario: a take moves with its measure when a measure is inserted before it
    Given I have added an audio track
    And I have armed track 2
    And I have recorded from measure 2 beat 1 to measure 3 beat 1
    When I insert a measure before measure 1
    Then take 1 of track 2 runs from measure 3 beat 1 to measure 4 beat 1

  Scenario: editing the tempo after recording does not stretch a take
    Given I have added an audio track
    And I have armed track 2
    And I have recorded from measure 1 beat 1 to measure 2 beat 1
    When I set the tempo of measure 1 to 60 bpm
    Then take 1 of track 2 still starts at measure 1 beat 1
    And take 1 of track 2 still lasts 2 seconds
