@planned
Feature: Recording line-in audio
  An armed audio track records what its input channels deliver while the song plays, as a take that starts where
  recording started and sits on the song's beats. A take keeps the dry input: VST3 effects shape what I hear, never
  what is recorded.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And I have set the MIDI Port to "TuxGuitar Synthesizer"
    And I have set the audio device to "Test Interface"
    And the VST3 folders hold the test plugins
    And I have added an audio track recording input channel 1

  Scenario: an armed audio track records a take from where recording started
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have armed track 2
    When I record from measure 2 beat 1 to measure 4 beat 1
    Then track 2's takes read "take 1: m2b1 to m4b1"
    And take 1 of track 2 holds 8 clicks, each within 1 sample of a beat

  Scenario Outline: a take sits on the beats at the audio device's sample rate
    Given I have set the audio device to "Test Interface" at <rate> Hz
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
    And I have set the tempo from measure 3 to the end to 60 bpm
    And I have armed track 2
    When I record from measure 1 beat 1 until the song ends
    Then take 1 of track 2 is 529200 samples long
    And take 1 of track 2 holds 16 clicks, each within 1 sample of a beat

  Scenario: a take follows a time signature change of the song
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have set the time signature of measure 2 only to 3/4
    And I have armed track 2
    When I record from measure 1 beat 1 to measure 3 beat 1
    Then take 1 of track 2 is 154350 samples long
    And take 1 of track 2 holds 7 clicks, each within 1 sample of a beat

  Scenario: recording with no armed track is refused
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then the action is refused
    And track 2's takes read ""

  Scenario: a guitar track cannot be armed
    When I arm track 1
    Then the action is refused
    And track 1 is not armed

  Scenario: a recording stopped where it started keeps no take and adds no undo step
    Given I have armed track 2
    When I record from measure 2 beat 1 to measure 2 beat 1
    Then the action succeeds
    And track 2's takes read ""
    And the undo history is as it was before the recording

  # Input channels

  Scenario Outline: a take records the input channels its track is set to
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And the player plays a 440 Hz tone peaking at 0.4 on input channel 2
    And I have set track 2 to record input channels <channels>
    And I have armed track 2
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then take 1 of track 2 has <count> channels
    And channel 1 of take 1 of track 2 holds <first>
    And channel 2 of take 1 of track 2 holds <second>

    Examples:
      | channels | count | first                           | second                          |
      | 1        | 1     | 4 clicks and no 440 Hz tone     | nothing                         |
      | 2        | 1     | a 440 Hz tone and no clicks     | nothing                         |
      | 1 and 2  | 2     | 4 clicks and no 440 Hz tone     | a 440 Hz tone and no clicks     |

  Scenario: two armed tracks each record their own input channel at once
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And the player plays a 440 Hz tone peaking at 0.4 on input channel 2
    And I have added an audio track recording input channel 2
    And I have armed track 2
    And I have armed track 3
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then channel 1 of take 1 of track 2 holds 4 clicks and no 440 Hz tone
    And channel 1 of take 1 of track 3 holds a 440 Hz tone and no clicks

  Scenario: a recording on two armed tracks is one undo step
    Given I have added an audio track recording input channel 2
    And I have armed track 2
    And I have armed track 3
    And I have recorded from measure 1 beat 1 to measure 2 beat 1
    When I undo
    Then track 2's takes read ""
    And track 3's takes read ""

  Scenario: two armed tracks on the same input channel record the same take
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have added an audio track recording input channel 1
    And I have armed track 2
    And I have armed track 3
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then take 1 of track 3 is sample for sample take 1 of track 2
    And take 1 of track 2 holds 4 clicks, each within 1 sample of a beat

  Scenario: a muted armed track still records
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have armed track 2
    And I have muted track 2
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then take 1 of track 2 holds 4 clicks, each within 1 sample of a beat

  Scenario Outline: the song, the Play Mode, arming and input channels cannot change during a recording
    Given I have armed track 2
    And I am recording from measure 1 beat 1
    When I <change>
    Then the action is refused
    And <unchanged>

    Examples:
      | change                                          | unchanged                               |
      | set the Percentage of Tempo to 80               | the Percentage of Tempo is 100          |
      | insert a measure before measure 3               | the song has 4 measures                 |
      | disarm track 2                                  | track 2 is armed                        |
      | set track 2 to record input channels 2          | track 2 records input channel 1         |

  # Count In

  Scenario Outline: Count In clicks one beat apart before recording starts, and is not recorded
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have <percussion>
    And I have turned Count In on
    And I have armed track 2
    When I record from measure 2 beat 1 to measure 3 beat 1
    Then the output holds 4 Count In clicks, 22050 samples apart within 1 sample
    And the last Count In click sounds 22050 samples before measure 2 beat 1, within 1 sample
    And track 2's takes read "take 1: m2b1 to m3b1"
    And take 1 of track 2 holds 4 clicks, each within 1 sample of a beat

    Examples:
      | percussion                             |
      | left the song without a percussion channel |
      | added a percussion track                |

  Scenario Outline: Count In follows the time signature, tempo and Percentage of Tempo of the measure recording starts in
    Given I have set the time signature of measure 2 only to <signature>
    And I have set the tempo from measure 2 to the end to <bpm> bpm
    And I have set the Percentage of Tempo to <percent>
    And I have turned Count In on
    And I have armed track 2
    When I record from measure 2 beat 1 to measure 3 beat 1
    Then the output holds <clicks> Count In clicks, <apart> samples apart within 1 sample

    Examples:
      | signature | bpm | percent | clicks | apart   |
      | 4/4       | 120 | 100     | 4      | 22050   |
      | 3/4       | 120 | 100     | 3      | 22050   |
      | 6/8       | 120 | 100     | 6      | 11025   |
      | 4/4       | 60  | 100     | 4      | 44100   |
      | 4/4       | 120 | 80      | 4      | 27562.5 |
      | 4/4       | 117 | 80      | 4      | 28269.2 |

  Scenario: Count In ticks set the number of clicks
    Given I have turned Count In on with 2 Count In ticks
    And I have armed track 2
    When I record from measure 2 beat 1 to measure 3 beat 1
    Then the output holds 2 Count In clicks, 22050 samples apart within 1 sample

  Scenario: Count In before a start in the middle of a measure keeps to the beat
    Given I have turned Count In on
    And I have armed track 2
    When I record from measure 2 beat 3 to measure 3 beat 1
    Then the output holds 4 Count In clicks, 22050 samples apart within 1 sample
    And the last Count In click sounds 22050 samples before measure 2 beat 3, within 1 sample

  Scenario: stopping during Count In keeps no take and adds no undo step
    Given I have turned Count In on
    And I have armed track 2
    When I record from measure 2 beat 1 and stop after the second Count In click
    Then track 2's takes read ""
    And the undo history is as it was before the recording

  # Input level

  Scenario Outline: the input level shows the peak an input channel delivered, and whether it is clipping
    Given I have armed track 2
    And the player plays <signal> on input channel 1
    When the input has delivered 1 second
    Then track 2's input level reads <level> dBFS within 0.01 dB
    And track 2's input <clipping>

    Examples:
      | signal                            | level | clipping        |
      | a 440 Hz tone peaking at -6 dBFS  | -6    | is not clipping |
      | a constant level of 0.97724       | -0.2  | is not clipping |
      | a constant level of 0.98855       | -0.1  | is clipping     |
      | a constant level of 1.0           | 0     | is clipping     |

  Scenario: an input that clipped shows it has clipped until I reset it
    Given I have armed track 2
    And the player has played a constant level of 1.0 for 1 sample on input channel 1, then 10 seconds at -20 dBFS
    When I look at track 2's input
    Then track 2's input is not clipping
    And track 2's input has clipped

  Scenario: resetting the clip indicator clears it
    Given I have armed track 2
    And the player has played a constant level of 1.0 for 1 sample on input channel 1, then 10 seconds at -20 dBFS
    When I reset track 2's clip indicator
    Then track 2's input has not clipped

  # Monitoring

  Scenario: arming a track leaves monitoring off
    When I arm track 2
    Then monitoring is off for track 2

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

  Scenario: with monitoring on, just playing plays the track's takes, not the input
    Given track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to the end of measure 4
    And I have armed track 2
    And I have turned monitoring on for track 2
    And the player plays a 880 Hz tone peaking at 0.4 on input channel 1
    When I play the song from measure 1 beat 1
    Then track 2 sounds a 440 Hz tone and no 880 Hz tone above -60 dBFS in measures 1 to 4

  Scenario: while recording, the armed track's earlier takes are silent where it records
    Given track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to the end of measure 4
    And I have armed track 2
    And I have set the punch range to measures 2 to 2
    When I record from measure 1 beat 1 to the end of measure 4
    Then the output holds a 440 Hz tone above -60 dBFS in measures 1, 3 and 4
    And the output holds no 440 Hz tone above -60 dBFS in measure 2

  Scenario Outline: the monitored input is late by its own path only, not by other tracks' latency
    Given I have set the audio device to "Test Interface" with <input> samples of input latency and <output> of output latency
    And I have added the VST3 effect "Test Latency" to track 1
    And I have set "Latency" of VST3 effect 1 of track 1 to 2048
    And I have added the VST3 effect "Test Latency" to track 2
    And I have set "Latency" of VST3 effect 1 of track 2 to <own>
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

  Scenario: a take recorded at a lower Percentage of Tempo is stored as played
    Given the player plays clicks of -0.25 on every beat on input channel 1
    And I have set the Percentage of Tempo to 80
    And I have armed track 2
    When I record from measure 1 beat 1 to measure 3 beat 1
    Then take 1 of track 2 is 220500 samples long
    And take 1 of track 2 holds 8 clicks, each within 1 sample of a beat at 80%

  Scenario: a take recorded at a lower Percentage of Tempo plays on the beats at 100%, at its pitch
    Given the player plays a 440 Hz tone peaking at 0.4 on input channel 1
    And I have set the Percentage of Tempo to 80
    And I have armed track 2
    And I have recorded from measure 1 beat 1 to measure 3 beat 1
    And I have set the Percentage of Tempo to 100
    When I play the song from measure 1 beat 1
    Then track 2 plays for 4 seconds within 5 ms
    And track 2 sounds a 440 Hz tone within 1% in measures 1 to 2
