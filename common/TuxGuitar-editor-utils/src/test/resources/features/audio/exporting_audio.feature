@planned
Feature: Exporting audio
  Exporting the song as audio renders every audible track through its sound chain into one file: the synthesizer's
  tracks, VST3 instruments, and the takes of audio tracks with their effects. The file starts at the start of the
  song, plays at 100% of the song's tempo and is aligned beat for beat, whatever the plugins delay or the Play Mode.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And the VST3 folders hold these plugins:
      | name       | kind       | does                                                      |
      | Test Click | instrument | plays a one-sample click at the start of each note         |
      | Test Gain  | effect     | multiplies the signal by its Gain parameter, 2 by default |
      | Test Delay | effect     | delays the signal by 256 samples and reports it            |
    And track 1 has a quarter note on every beat
    And I have chosen the VST3 instrument "Test Click" for track 1
    And I have added an audio track
    And track 2 has a take of a click on every beat over measures 1 to 4

  Scenario: a take is in the exported file on its beats
    When I export the song as a WAV file at 44100 Hz
    Then the file's click of measure 2 beat 1 is at sample 88200 within 1 sample
    And every click of track 2 in the file lies within 1 sample of its beat

  Scenario: plugin latency does not shift the exported file
    Given I have added the VST3 effect "Test Delay" to track 2
    When I export the song as a WAV file at 44100 Hz
    Then on every beat the clicks of track 1 and track 2 in the file are within 1 sample of each other
    And the file's click of measure 1 beat 1 is at sample 0 within 1 sample

  Scenario: effects are in the exported file
    Given I have removed the notes of track 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 over measure 1
    And I have added the VST3 effect "Test Gain" to track 2
    When I export the song as a WAV file at 44100 Hz
    Then the file sounds a 440 Hz tone peaking at 0.8 within 0.01 in measure 1

  Scenario: the Play Mode does not change the exported file
    Given the Percentage of Tempo is 80
    And Play looped is on over the Loop Range of measures 2 to 3
    When I export the song as a WAV file at 44100 Hz
    Then the file lasts 8 seconds within 1 block of 512 samples
    And the file's click of measure 2 beat 1 is at sample 88200 within 1 sample

  Scenario Outline: muted tracks are left out, as on playback
    Given track <muted> is muted
    When I export the song as a WAV file at 44100 Hz
    Then the file holds the clicks of track <heard> only

    Examples:
      | muted | heard |
      | 1     | 2     |
      | 2     | 1     |

  Scenario: a soloed audio track is the only one exported
    Given track 2 is soloed
    When I export the song as a WAV file at 44100 Hz
    Then the file holds the clicks of track 2 only
