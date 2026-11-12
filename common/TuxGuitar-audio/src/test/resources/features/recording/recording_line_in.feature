@planned
Feature: Recording line-in audio
  An armed audio track records what its input channel delivers while the song plays, as a take that starts where
  recording started and sits on the song's beats. A take keeps the dry input: VST3 effects shape what I hear, never
  what is recorded.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And the MIDI Port is "TuxGuitar Synthesizer"
    And the audio device is "Test Interface"
    And the VST3 folders hold the test plugins
    And I have added an audio track recording input channel 1

  Scenario: an armed audio track records a take from where recording started
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have armed track 2
    When I record from measure 2 beat 1 to measure 4 beat 1
    Then track 2's takes read "take 1: m2b1 to m4b1"
    And take 1 of track 2 holds 8 clicks, each within 1 sample of a beat

  Scenario Outline: a take sits on the beats at the audio device's sample rate
    Given the audio device is "Test Interface" at <rate> Hz
    And the player plays clicks of -0.25 on every beat on input channel 1
    And I have armed track 2
    When I record from measure 1 beat 1 to measure 3 beat 1
    Then take 1 of track 2 is <length> samples long
    And take 1 of track 2 holds a click at sample <second measure> within 1 sample

    Examples:
      | rate  | length | second measure |
      | 44100 | 176400 | 88200          |
      | 48000 | 192000 | 96000          |

  Scenario: a take ends where I stopped, not on a measure line
    Given I have armed track 2
    When I record from measure 1 beat 1 to measure 2 beat 2.5
    Then track 2's takes read "take 1: m1b1 to m2b2.5"
    And take 1 of track 2 is 121275 samples long

  Scenario: recording stops at the end of the song
    Given I have armed track 2
    When I record from measure 3 beat 1 until the song ends
    Then track 2's takes read "take 1: m3b1 to m5b1"

  Scenario: a take follows the tempo changes of the song as it is recorded
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have set the tempo of measure 3 to 60 bpm
    And I have armed track 2
    When I record from measure 1 beat 1 until the song ends
    Then take 1 of track 2 is 12 seconds long within 1 sample
    And take 1 of track 2 holds 16 clicks, each within 1 sample of a beat

  Scenario: a take follows a time signature change of the song
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have set the time signature of measure 2 to 3/4
    And I have armed track 2
    When I record from measure 1 beat 1 to measure 3 beat 1
    Then take 1 of track 2 is 3.5 seconds long within 1 sample
    And take 1 of track 2 holds 7 clicks, each within 1 sample of a beat

  Scenario: recording with no armed track is refused
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then the action is refused
    And track 2's takes read ""

  Scenario: a guitar track cannot be armed
    When I arm track 1
    Then the action is refused
    And track 1 is not armed

  Scenario: a recording stopped where it started keeps no take and leaves nothing to undo
    Given I have armed track 2
    When I record from measure 2 beat 1 to measure 2 beat 1
    Then the action succeeds
    And track 2's takes read ""
    And there is nothing to undo

  Scenario: two armed tracks each hold what their own input channel delivered
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And the player plays a 440 Hz tone peaking at 0.4 on input channel 2
    And I have added an audio track recording input channel 2
    And I have armed track 2
    And I have armed track 3
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then take 1 of track 2 holds 4 clicks, each within 1 sample of a beat
    And take 1 of track 2 holds no 440 Hz tone above -60 dBFS
    And take 1 of track 3 holds a 440 Hz tone peaking at 0.4 within 0.01
    And take 1 of track 3 holds no clicks

  Scenario: a recording on two armed tracks is one undo step
    Given I have added an audio track recording input channel 2
    And I have armed track 2
    And I have armed track 3
    And I have recorded from measure 1 beat 1 to measure 2 beat 1
    When I undo
    Then track 2's takes read ""
    And track 3's takes read ""

  Scenario Outline: a take records the input channels its track is set to
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And the player plays a 440 Hz tone peaking at 0.4 on input channel 2
    And I have set track 2 to record input channels <channels>
    And I have armed track 2
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then take 1 of track 2 is <shape>
    And take 1 of track 2 holds <holds>

    Examples:
      | channels | shape  | holds                                            |
      | 1        | mono   | the clicks only                                  |
      | 2        | mono   | the tone only                                    |
      | 1 and 2  | stereo | the clicks on the left and the tone on the right |

  # Count In

  Scenario: Count In clicks one beat apart before recording starts, and is not recorded
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have turned Count In on
    And I have armed track 2
    When I record from measure 2 beat 1 to measure 3 beat 1
    Then the output sounds 4 Count In clicks, 22050 samples apart within 1 sample
    And the last Count In click sounds 22050 samples before measure 2 beat 1, within 1 sample
    And track 2's takes read "take 1: m2b1 to m3b1"
    And take 1 of track 2 holds 4 clicks, each within 1 sample of a beat

  Scenario: Count In sounds in a song without a percussion track
    Given the song has no percussion track
    And I have turned Count In on
    And I have armed track 2
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then the output sounds 4 Count In clicks, 22050 samples apart within 1 sample

  Scenario Outline: Count In follows the time signature and tempo of the measure recording starts in
    Given I have set the time signature of measure 2 to <signature>
    And I have set the tempo of measure 2 to <bpm> bpm
    And I have turned Count In on
    And I have armed track 2
    When I record from measure 2 beat 1 to measure 3 beat 1
    Then the output sounds <clicks> Count In clicks, <apart> samples apart within 1 sample

    Examples:
      | signature | bpm | clicks | apart |
      | 4/4       | 120 | 4      | 22050 |
      | 3/4       | 120 | 3      | 22050 |
      | 6/8       | 120 | 6      | 11025 |
      | 4/4       | 60  | 4      | 44100 |

  Scenario: Count In ticks set the number of clicks
    Given I have turned Count In on with 2 Count In ticks
    And I have armed track 2
    When I record from measure 2 beat 1 to measure 3 beat 1
    Then the output sounds 2 Count In clicks, 22050 samples apart within 1 sample

  Scenario: Count In before a start in the middle of a measure keeps to the beat
    Given I have turned Count In on
    And I have armed track 2
    When I record from measure 2 beat 3 to measure 3 beat 1
    Then the output sounds 4 Count In clicks, 22050 samples apart within 1 sample
    And the last Count In click sounds 22050 samples before measure 2 beat 3, within 1 sample

  Scenario: Count In counts at the Percentage of Tempo I record at
    Given I have set the Percentage of Tempo to 80
    And I have turned Count In on
    And I have armed track 2
    When I record from measure 2 beat 1 to measure 3 beat 1
    Then the output sounds 4 Count In clicks, 27562.5 samples apart within 1 sample

  Scenario: stopping during Count In keeps no take
    Given I have turned Count In on
    And I have armed track 2
    When I record from measure 2 beat 1 and stop after the second Count In click
    Then track 2's takes read ""
    And there is nothing to undo

  # Input level and monitoring

  Scenario Outline: the input level shows the peak an input channel delivered, and whether it is clipping
    Given I have armed track 2
    And the player plays a 440 Hz tone peaking at <peak> dBFS on input channel 1
    When the input has delivered 1 second
    Then track 2's input level reads <peak> dBFS within 0.05 dB
    And track 2's input <clipping>

    Examples:
      | peak | clipping        |
      | -6   | is not clipping |
      | -0.2 | is not clipping |
      | -0.1 | is clipping     |
      | 0    | is clipping     |

  Scenario: with monitoring on I hear the input through the armed track's sound chain while recording
    Given I have added the VST3 effect "Test Gain" to track 2
    And I have armed track 2
    And I have turned monitoring on for track 2
    And the player plays a 440 Hz tone peaking at 0.4 on input channel 1
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then track 2 sounds a 440 Hz tone peaking at 0.8 within 0.01 in measure 1
    And take 1 of track 2 holds a 440 Hz tone peaking at 0.4 within 0.01

  Scenario: with monitoring off the input is recorded but not heard
    Given I have armed track 2
    And I have turned monitoring off for track 2
    And the player plays a 440 Hz tone peaking at 0.4 on input channel 1
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then the output holds no 440 Hz tone above -60 dBFS
    And take 1 of track 2 holds a 440 Hz tone peaking at 0.4 within 0.01

  Scenario: with monitoring on I hear the input through the sound chain while stopped
    Given I have added the VST3 effect "Test Gain" to track 2
    And I have armed track 2
    And I have turned monitoring on for track 2
    And the player plays a 440 Hz tone peaking at 0.4 on input channel 1
    When the input has delivered 1 second
    Then track 2 sounds a 440 Hz tone peaking at 0.8 within 0.01
    And track 2's takes read ""

  Scenario: while recording, the armed track's earlier takes are silent where it records
    Given track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to the end of measure 4
    And I have armed track 2
    And I have turned monitoring off for track 2
    When I record from measure 1 beat 3 to measure 3 beat 1
    Then the output holds a 440 Hz tone above -60 dBFS from measure 1 beat 1 to measure 1 beat 3
    And the output holds no 440 Hz tone above -60 dBFS from measure 1 beat 3 to measure 3 beat 1

  Scenario Outline: the monitored input is late by its own path only, not by other tracks' latency
    Given the audio device is "Test Interface" with <input> samples of input latency and <output> of output latency
    And I have added the VST3 effect "Test Latency" set to 2048 samples to track 1
    And I have added the VST3 effect "Test Latency" set to <own> samples to track 2
    And I have armed track 2
    And I have turned monitoring on for track 2
    And the player plays one click of -0.25 at measure 1 beat 2 on input channel 1
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then the click reaches the output <late> samples after the player played it, within 1 sample

    Examples:
      | input | output | own | late |
      | 0     | 0      | 0   | 0    |
      | 300   | 1024   | 0   | 1324 |
      | 300   | 1024   | 128 | 1452 |

  # Recording at another Percentage of Tempo

  Scenario: recording at a lower Percentage of Tempo stores the take at the song's tempo
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have set the Percentage of Tempo to 80
    And I have armed track 2
    When I record from measure 1 beat 1 to measure 3 beat 1
    Then take 1 of track 2 is 4 seconds long at 100% within 5 ms
    And take 1 of track 2 holds 8 clicks, each within 5 ms of a beat at 100%

  Scenario: recording at a lower Percentage of Tempo keeps the pitch
    Given the player plays a 440 Hz tone peaking at 0.4 on input channel 1
    And I have set the Percentage of Tempo to 80
    And I have armed track 2
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then take 1 of track 2 holds a 440 Hz tone within 1%
