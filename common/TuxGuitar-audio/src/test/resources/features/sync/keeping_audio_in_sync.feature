@planned
Feature: Keeping audio in sync
  Takes, the notes of other tracks and the player's position stay together, whatever VST3 plugins and the audio device
  delay. Every track is delayed to the latest one (plugin delay compensation). A take is placed by the full round trip,
  so what I played in time with what I heard lands on the beat. At another Percentage of Tempo, takes are
  time-stretched and keep their pitch.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And the MIDI Port is "TuxGuitar Synthesizer"
    And the audio device is "Test Interface"
    And the VST3 folders hold the test plugins
    And track 1 has a quarter note on every beat
    And I have chosen the VST3 instrument "Test Click" for track 1
    And I have added an audio track recording input channel 1
    And track 2 has a take of clicks of -0.25 on every beat from measure 1 beat 1 to the end of measure 4

  # Plugin delay compensation

  Scenario Outline: tracks with different latencies reach the output together
    Given I have added the VST3 effect "Test Latency" set to <latency 1> samples to track 1
    And I have set "Latency" of the VST3 instrument of track 1 to <instrument latency>
    And I have added the VST3 effect "Test Latency" set to <latency 2> samples to track 2
    When I play the song from measure 1 beat 1
    Then the output holds 16 positive clicks and 16 negative clicks
    And on every beat the click of track 1 and the click of track 2 reach the output within 1 sample of each other

    Examples:
      | latency 1 | instrument latency | latency 2 |
      | 0         | 0                  | 0         |
      | 0         | 0                  | 256       |
      | 256       | 0                  | 0         |
      | 0         | 512                | 0         |
      | 512       | 0                  | 2048      |
      | 0         | 0                  | 44100     |

  Scenario: disabling a delaying VST3 effect keeps the tracks together
    Given I have added the VST3 effect "Test Latency" set to 2048 samples to track 2
    And I have disabled VST3 effect 1 of track 2
    When I play the song from measure 1 beat 1
    Then on every beat the click of track 1 and the click of track 2 reach the output within 1 sample of each other

  Scenario: muting a delaying track does not move the other tracks
    Given I have added the VST3 effect "Test Latency" set to 2048 samples to track 2
    And I have muted track 2
    When I play the song from measure 1 beat 1
    Then the output holds 16 positive clicks and no negative clicks
    And every click of track 1 reaches the output on the same sample as with track 2 unmuted

  Scenario Outline: the player reports a beat when the output sounds it
    Given the audio device is "Test Interface" with 0 samples of input latency and <output> of output latency
    And I have added the VST3 effect "Test Latency" set to <plugin> samples to track 2
    When I play the song from measure 1 beat 1
    Then the player reports each of the 16 beats within 512 samples of the output sounding it

    Examples:
      | output | plugin |
      | 0      | 0      |
      | 1024   | 0      |
      | 1024   | 2048   |

  # Recording against what I hear

  Scenario Outline: a take recorded in time with what I hear lands on the beat
    Given the audio device is "Test Interface" with <input> samples of input latency and <output> of output latency
    And I have added the VST3 effect "Test Latency" set to <backing> samples to track 1
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
    Given the audio device is "Test Interface" with 364 samples of input latency, reporting 300
    And the recording offset is 64 samples
    And I have added an audio track recording input channel 1
    And the player plays clicks of -0.25 on every beat on input channel 1
    And I have armed track 3
    When I record from measure 1 beat 1 to measure 3 beat 1
    Then take 1 of track 3 holds 8 clicks, each within 1 sample of a beat

  Scenario: the VST3 effects of an armed track do not shift what it records
    Given I have added an audio track recording input channel 1
    And I have added the VST3 effect "Test Latency" set to 2048 samples to track 3
    And the player plays clicks of -0.25 on every beat on input channel 1
    And I have armed track 3
    When I record from measure 1 beat 1 to measure 3 beat 1
    Then take 1 of track 3 holds 8 clicks, each within 1 sample of a beat

  # Starting, stopping, looping

  Scenario: playing from the middle of a take starts at the matching point of the take
    Given I have added the VST3 effect "Test Latency" set to 2048 samples to track 1
    When I play the song from measure 3 beat 2
    Then the first click of track 2 that reaches the output is the one of measure 3 beat 2
    And it reaches the output within 1 sample of the click of track 1 on that beat

  Scenario: stopping and playing again starts the take back on the beat
    Given I have played the song from measure 1 beat 1 and stopped at measure 2 beat 3
    When I play the song from measure 2 beat 3
    Then on every beat the click of track 1 and the click of track 2 reach the output within 1 sample of each other

  Scenario: stopping clears the VST3 effects' tails
    Given I have added the VST3 effect "Test Echo" to track 2
    And I have played the song from measure 1 beat 1 and stopped at measure 2 beat 1
    When I play the song from measure 4 beat 1
    Then no echo of a click from measures 1 to 2 reaches the output

  Scenario: looping over the Loop Range keeps the take on the beats on every pass
    Given I have turned Play looped on over measures 2 to 3
    And I have added the VST3 effect "Test Latency" set to 256 samples to track 2
    When I play the song from measure 2 beat 1 for 3 passes
    Then the output holds 24 positive clicks and 24 negative clicks
    And on every beat the click of track 1 and the click of track 2 reach the output within 1 sample of each other

  Scenario: a VST3 effect's tail carries across the jump back of a loop
    Given I have turned Play looped on over measures 2 to 3
    And I have added the VST3 effect "Test Echo" to track 2
    When I play the song from measure 2 beat 1 for 2 passes
    Then the echo of the click of measure 3 beat 4 reaches the output 1 second after it, within 1 sample

  Scenario: a Loop Range shorter than the plugin latency stays in sync
    Given I have set the Percentage of Tempo to 400
    And I have turned Play looped on over measures 2 to 2
    And I have added the VST3 effect "Test Latency" set to 44100 samples to track 2
    When I play the song from measure 2 beat 1 for 4 passes
    Then on every beat the click of track 1 and the click of track 2 reach the output within 5 ms of each other

  # Percentage of Tempo

  Scenario Outline: at another Percentage of Tempo takes are stretched to the beats and keep their pitch
    Given track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have set the Percentage of Tempo to <percent>
    When I play the song from measure 1 beat 1
    Then measure 1 lasts <seconds> seconds within 5 ms
    And track 2 sounds a 440 Hz tone within 1% in measure 1
    And on every beat of measures 2 to 4 the click of track 1 and the click of track 2 reach the output within 5 ms of each other

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
    Then track 2 sounds a 440 Hz tone within 1% for <seconds> seconds within 5 ms

    Examples:
      | percent | seconds |
      | 1       | 200     |
      | 500     | 0.4     |

  Scenario: playing from the middle of a take at 80% starts at the matching point of the take
    Given I have set the Percentage of Tempo to 80
    When I play the song from measure 3 beat 2
    Then the first click of track 2 that reaches the output is the one of measure 3 beat 2
    And it reaches the output within 5 ms of the click of track 1 on that beat

  Scenario: in Training Mode each pass stretches the take to that pass's Percentage of Tempo
    Given I have turned Training Mode on from 80% to 100% by 10% over measures 2 to 3
    When I play the song from measure 2 beat 1 for 3 passes
    Then the passes last 5, 4.444 and 4 seconds within 5 ms
    And on every beat the click of track 1 and the click of track 2 reach the output within 5 ms of each other

  Scenario: in Training Mode a take keeps its pitch on every pass
    Given track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 2 beat 1 to measure 4 beat 1
    And I have turned Training Mode on from 80% to 100% by 10% over measures 2 to 3
    When I play the song from measure 2 beat 1 for 3 passes
    Then track 2 sounds a 440 Hz tone within 1% on every pass
