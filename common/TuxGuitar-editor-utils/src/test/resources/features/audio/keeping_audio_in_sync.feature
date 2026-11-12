@planned
Feature: Keeping audio in sync
  Recorded takes, the synthesizer's tracks and the player's position stay together, whatever the plugins delay.
  Each track is delayed so that all tracks reach the output as late as the slowest one (plugin delay compensation),
  and the player reports a beat when the output sounds it. A take is placed earlier by the input's latency, so what
  I played on a beat lands on that beat. Below or above 100% Percentage of Tempo, takes are time-stretched to the
  new tempo and keep their pitch.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And the VST3 folders hold these plugins:
      | name        | kind       | does                                              |
      | Test Click  | instrument | plays a one-sample click at the start of each note |
      | Test Gain   | effect     | multiplies the signal by its Gain parameter, 2 by default |
    And track 1 has a quarter note on every beat
    And I have chosen the VST3 instrument "Test Click" for track 1
    And I have added an audio track
    And track 2 has a take of a click on every beat over measures 1 to 4

  Scenario Outline: tracks with different plugin latencies reach the output together
    Given track 1's effects report <latency 1> samples of latency
    And track 2's effects report <latency 2> samples of latency
    When I play the song from measure 1 beat 1
    Then on every beat the clicks of track 1 and track 2 reach the output within 1 sample of each other

    Examples:
      | latency 1 | latency 2 |
      | 0         | 0         |
      | 0         | 256       |
      | 256       | 0         |
      | 512       | 2048      |
      | 0         | 44100     |

  Scenario: disabling a delaying effect keeps the tracks together
    Given track 2's effects report 2048 samples of latency
    And I have disabled the effects of track 2
    When I play the song from measure 1 beat 1
    Then on every beat the clicks of track 1 and track 2 reach the output within 1 sample of each other

  Scenario: muting a delaying track does not move the other tracks
    Given track 2's effects report 2048 samples of latency
    And track 2 is muted
    When I play the song from measure 1 beat 1
    Then on every beat the clicks of track 1 reach the output within 1 sample of where they reach it unmuted

  Scenario Outline: the player reports a beat when the output sounds it
    Given the audio output has <output latency> samples of latency
    And track 2's effects report <plugin latency> samples of latency
    When I play the song from measure 1 beat 1
    Then the player reports each beat within 512 samples of the output sounding it

    Examples:
      | output latency | plugin latency |
      | 0              | 0              |
      | 1024           | 0              |
      | 1024           | 2048           |

  Scenario Outline: a take is placed earlier by the input's latency
    Given the audio input has <input latency> samples of latency
    And the input plays a click on every beat of the song
    And I have added an audio track
    And I have armed track 3
    When I record from measure 1 beat 1 to measure 3 beat 1
    Then every click in take 1 of track 3 lies within 1 sample of its beat

    Examples:
      | input latency |
      | 0             |
      | 300           |
      | 4410          |

  Scenario: the effects on an armed track do not shift what it records
    Given the input plays a click on every beat of the song
    And I have added an audio track
    And track 3's effects report 2048 samples of latency
    And I have armed track 3
    When I record from measure 1 beat 1 to measure 3 beat 1
    Then every click in take 1 of track 3 lies within 1 sample of its beat

  Scenario: playing from the middle of a take starts at the matching point of the take
    When I play the song from measure 3 beat 2
    Then the first click of track 2 reaches the output within 1 sample of the first click of track 1
    And no click of track 2 from before measure 3 beat 2 is heard

  Scenario: looping over a Loop Range keeps the take on the beats on every pass
    Given Play looped is on over the Loop Range of measures 2 to 3
    And track 2's effects report 256 samples of latency
    When I play the song from measure 2 beat 1 for 3 passes
    Then on every pass and every beat the clicks of track 1 and track 2 reach the output within 1 sample of each other
    And no click of track 2 from measure 4 is heard

  Scenario Outline: a slower or faster tempo stretches takes and keeps their pitch
    Given the Percentage of Tempo is <percent>
    And track 2 has a take of a 440 Hz tone over measure 1 and a click on every beat of measures 2 to 4
    When I play the song from measure 1 beat 1
    Then on every beat from measure 2 the clicks of track 1 and track 2 reach the output within 5 ms of each other
    And track 2 sounds 440 Hz within 1% in measure 1
    And measure 1 of track 2 lasts <seconds> seconds within 5 ms

    Examples:
      | percent | seconds |
      | 1       | 200     |
      | 50      | 4       |
      | 80      | 2.5     |
      | 100     | 2       |
      | 200     | 1       |
      | 500     | 0.4     |

  Scenario: in Training Mode each pass stretches the take to that pass's tempo
    Given Training Mode is on from 80% to 100% by 10% over the Loop Range of measures 2 to 3
    When I play the song from measure 2 beat 1 for 3 passes
    Then the passes play at 80%, 90% and 100%
    And on every pass and every beat the clicks of track 1 and track 2 reach the output within 5 ms of each other
    And track 2 keeps its pitch on every pass

  Scenario: stopping and playing again starts the take back on the beat
    Given I have played the song from measure 1 beat 1 and stopped at measure 2 beat 3
    When I play the song from measure 2 beat 3
    Then the first click of track 2 reaches the output within 1 sample of the first click of track 1
