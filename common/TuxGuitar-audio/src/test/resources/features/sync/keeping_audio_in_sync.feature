Feature: Keeping audio in sync
  Takes, the notes of other tracks, the Count In, the Metronome and the player's position stay together, whatever
  plugins and the audio device delay. Every track is delayed to the latest one (plugin delay compensation). A take is
  placed by the full round trip, so what I played in time with what I heard lands on the beat. At another Percentage of
  Tempo, takes are time-stretched and keep their pitch.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And I have set the MIDI Port to "TuxGuitar Synthesizer"
    And I have set the audio device to "Test Interface"
    And the VST3 folders hold the test plugins
    And track 1 has a quarter note on every beat
    And I have chosen the VST3 instrument "Test Click" for track 1
    And I have set the Balance of track 1's channel to 0
    And I have added an audio track recording input channel 1
    And I have set the Balance of track 2's channel to 127
    And track 2 has a take of clicks of -0.25 on every beat from measure 1 beat 1 to the end of measure 4

  # Plugin delay compensation

  Scenario Outline: tracks with different latencies reach the output together
    Given I have added the VST3 effect "Test Latency" to track 1
    And I have set "Latency" of VST3 effect 1 of track 1 to <effect 1>
    And I have set "Latency" of the VST3 instrument of track 1 to <instrument 1>
    And I have added the VST3 effect "Test Latency" to track 2
    And I have set "Latency" of VST3 effect 1 of track 2 to <effect 2>
    When I play the song from measure 1 beat 1
    Then the output's left side holds 16 clicks and its right side 16 clicks
    And on every beat the left and right clicks reach the output within 1 sample of each other

    Examples:
      | effect 1 | instrument 1 | effect 2 |
      | 0        | 0            | 0        |
      | 0        | 0            | 256      |
      | 256      | 0            | 0        |
      | 0        | 512          | 0        |
      | 512      | 0            | 2048     |
      | 0        | 0            | 30000    |

  Scenario: disabling a delaying VST3 effect keeps the tracks together
    Given I have added the VST3 effect "Test Latency" to track 2
    And I have set "Latency" of VST3 effect 1 of track 2 to 2048
    And I have disabled VST3 effect 1 of track 2
    When I play the song from measure 1 beat 1
    Then on every beat the left and right clicks reach the output within 1 sample of each other

  Scenario: a muted track's latency still counts, so muting it moves nothing
    Given I have added the VST3 effect "Test Latency" to track 2
    And I have set "Latency" of VST3 effect 1 of track 2 to 2048
    And I have muted track 2
    When I play the song from measure 1 beat 1
    Then the output's left side holds 16 clicks and its right side none
    And every left click reaches the output 2048 samples after its beat, within 1 sample

  Scenario: when a plugin's latency changes while playing, the tracks are together again from the next beat
    Given I have added the VST3 effect "Test Latency" to track 2
    And I am playing the song from measure 1 beat 1
    When the plugin sets "Latency" of VST3 effect 1 of track 2 to 2048 at measure 2 beat 1
    Then from measure 2 beat 2 on, the left and right clicks of every beat reach the output within 1 sample of each other

  Scenario: playing to the end of the song runs on until the most delayed track has sounded its last beat
    Given I have added the VST3 effect "Test Latency" to track 2
    And I have set "Latency" of VST3 effect 1 of track 2 to 30000
    When I play the song from measure 1 beat 1 until the song ends
    Then the output's left side holds 16 clicks and its right side 16 clicks

  @planned
  Scenario: the Metronome sounds with the tracks whatever the plugins delay
    Given I have added the VST3 effect "Test Latency" to track 1
    And I have set "Latency" of VST3 effect 1 of track 1 to 2048
    And I have turned the Metronome on
    When I play the song from measure 1 beat 1
    Then the output holds 16 Metronome clicks, each within 1 sample of the left click on its beat

  Scenario: Count In sounds one beat before the first beat, whatever the plugins delay
    Given I have added the VST3 effect "Test Latency" to track 1
    And I have set "Latency" of VST3 effect 1 of track 1 to 2048
    And I have turned Count In on
    When I play the song from measure 1 beat 1
    Then the last Count In click reaches the output 22050 samples before the left click of measure 1 beat 1, within 1 sample

  Scenario Outline: TuxGuitar reports a beat as played when the output sounds it
    Given I have set the audio device to "Test Interface" with 0 samples of input latency and <output> of output latency
    And I have added the VST3 effect "Test Latency" to track 2
    And I have set "Latency" of VST3 effect 1 of track 2 to <plugin>
    When I play the song from measure 1 beat 1
    Then TuxGuitar reports each of the 16 beats within 512 samples of the output sounding it

    Examples:
      | output | plugin |
      | 0      | 0      |
      | 1024   | 0      |
      | 1024   | 2048   |

  # Recording against what I hear

  Scenario Outline: a take recorded in time with what I hear lands on the beat
    Given I have set the audio device to "Test Interface" with <input> samples of input latency and <output> of output latency
    And I have added the VST3 effect "Test Latency" to track 1
    And I have set "Latency" of VST3 effect 1 of track 1 to <backing>
    And I have added an audio track recording input channel 1
    And the player plays clicks of -0.25 on every beat on input channel 1
    And I have armed track 3
    When I record from measure 1 beat 1 to measure 3 beat 1
    Then take 1 of track 3 holds 8 clicks, each within 1 sample of a beat

    Examples:
      | input | output | backing |
      | 0     | 0      | 0       |
      | 300   | 0      | 0       |
      | 4410  | 0      | 0       |
      | 300   | 1024   | 0       |
      | 300   | 1024   | 2048    |

  Scenario: the recording offset corrects an input latency the audio device reports wrong
    Given I have set the audio device to "Test Interface" with 364 samples of input latency, reporting 300
    And I have set the recording offset to 64 samples
    And I have added an audio track recording input channel 1
    And the player plays clicks of -0.25 on every beat on input channel 1
    And I have armed track 3
    When I record from measure 1 beat 1 to measure 3 beat 1
    Then take 1 of track 3 holds 8 clicks, each within 1 sample of a beat

  Scenario: the effects of an armed track do not shift what it records
    Given I have added an audio track recording input channel 1
    And I have added the VST3 effect "Test Latency" to track 3
    And I have set "Latency" of VST3 effect 1 of track 3 to 2048
    And the player plays clicks of -0.25 on every beat on input channel 1
    And I have armed track 3
    When I record from measure 1 beat 1 to measure 3 beat 1
    Then take 1 of track 3 holds 8 clicks, each within 1 sample of a beat

  # Starting, stopping, looping, repeats

  Scenario Outline: playing from the middle of a take starts at the matching point of the take
    Given track 2 has a take of a tone per measure of 220, 330, 440 and 550 Hz from measure 1 beat 1 to the end of measure 4
    And I have added the VST3 effect "Test Latency" to track 1
    And I have set "Latency" of VST3 effect 1 of track 1 to 2048
    And I have set the Percentage of Tempo to <percent>
    When I play the song from measure 3 beat 2
    Then track 2 sounds 440 Hz, then 550 Hz from the left click of measure 4 beat 1 on, within <tolerance>

    Examples:
      | percent | tolerance |
      | 100     | 1 sample  |
      | 80      | 5 ms      |

  Scenario: stopping and playing again starts the take back at the matching point
    Given track 2 has a take of a tone per measure of 220, 330, 440 and 550 Hz from measure 1 beat 1 to the end of measure 4
    And I have played the song from measure 1 beat 1 and stopped at measure 2 beat 3
    When I play the song from measure 2 beat 3
    Then track 2 sounds 330 Hz, then 440 Hz from the left click of measure 3 beat 1 on, within 1 sample

  Scenario: stopping clears the effects' tails
    Given I have added the VST3 effect "Test Echo" to track 2
    And I have played the song from measure 1 beat 1 and stopped at measure 1 beat 4.5
    When I play the song from measure 4 beat 1
    Then the output's right side holds 8 clicks: the 4 of measure 4 and their echoes

  Scenario: looping over the Loop Range keeps the take on the beats on every pass
    Given I have turned Play looped on over measures 2 to 3
    And I have added the VST3 effect "Test Latency" to track 2
    And I have set "Latency" of VST3 effect 1 of track 2 to 256
    When I play the song from measure 2 beat 1 for 3 passes
    Then the output's left side holds 24 clicks and its right side 24 clicks
    And on every beat the left and right clicks reach the output within 1 sample of each other

  Scenario: a tail carries across the jump back of a loop
    Given I have turned Play looped on over measures 2 to 3
    And track 2 has a take of one click of -0.25 at measure 3 beat 4.5 from measure 3 beat 4 to measure 4 beat 1
    And I have added the VST3 effect "Test Echo" to track 2
    When I play the song from measure 2 beat 1 for 2 passes
    Then the echo of the click of measure 3 beat 4.5 reaches the output 2205 samples into the second pass, within 1 sample

  Scenario: a tail carries across a repeat
    Given track 2 has a take of one click of -0.25 at measure 3 beat 4.5 from measure 3 beat 4 to measure 4 beat 1
    And I have repeated measures 2 to 3 once
    And I have added the VST3 effect "Test Echo" to track 2
    When I play the song from measure 1 beat 1
    Then the echo of the first pass's click of measure 3 beat 4.5 reaches the output 2205 samples into the second pass, within 1 sample

  Scenario: a Loop Range shorter than the plugin latency stays in sync
    Given I have set the Percentage of Tempo to 400
    And I have turned Play looped on over measures 2 to 2
    And I have added the VST3 effect "Test Latency" to track 2
    And I have set "Latency" of VST3 effect 1 of track 2 to 30000
    When I play the song from measure 2 beat 1 for 4 passes
    Then the output's left side holds 16 clicks and its right side 16 clicks
    And on every beat the left and right clicks reach the output within 5 ms of each other

  # Percentage of Tempo

  Scenario Outline: at another Percentage of Tempo takes are stretched to the beats and keep their pitch
    Given track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have set the Percentage of Tempo to <percent>
    When I play the song from measure 1 beat 1
    Then track 2 plays measure 1 for <seconds> seconds within 5 ms
    And track 2 sounds a 440 Hz tone within 1% in measure 1
    And on every beat of measures 2 to 4 the left and right clicks reach the output within 5 ms of each other

    Examples:
      | percent | seconds |
      | 25      | 8       |
      | 50      | 4       |
      | 80      | 2.5     |
      | 100     | 2       |
      | 200     | 1       |
      | 400     | 0.5     |

  Scenario Outline: at the ends of the Percentage of Tempo takes still last as long as their measures and keep their pitch
    Given track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have set the Percentage of Tempo to <percent>
    When I play the song from measure 1 beat 1 to measure 2 beat 1
    Then track 2 plays measure 1 for <seconds> seconds within 5 ms
    And track 2 sounds a 440 Hz tone within 1% in measure 1

    Examples:
      | percent | seconds |
      | 1       | 200     |
      | 500     | 0.4     |

  Scenario: in Training Mode each pass stretches the take to that pass's Percentage of Tempo
    Given I have turned Training Mode on from 80% to 100% by 10% over measures 2 to 3
    When I play the song from measure 2 beat 1 for 3 passes
    Then the passes last 5, 4.444 and 4 seconds within 5 ms
    And on every beat the left and right clicks reach the output within 5 ms of each other

  Scenario: in Training Mode a take keeps its pitch on every pass
    Given track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 2 beat 1 to measure 4 beat 1
    And I have turned Training Mode on from 80% to 100% by 10% over measures 2 to 3
    When I play the song from measure 2 beat 1 for 3 passes
    Then track 2 sounds a 440 Hz tone within 1% on every pass
