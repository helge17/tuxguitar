@planned
Feature: Audio tracks
  An audio track sounds its takes instead of notes. It has no strings, holds no notes, and has a channel of its own.
  Audio tracks need the TuxGuitar Synthesizer as the MIDI Port.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And the MIDI Port is "TuxGuitar Synthesizer"
    And the audio device is "Test Interface"

  Scenario: a new audio track has no strings and no takes, and sounds its takes
    When I add an audio track recording input channel 1
    Then the action succeeds
    And the song has 2 tracks
    And track 2 is an audio track with 0 strings
    And track 2's takes read ""
    And track 2's sound chain reads "takes"

  Scenario: an audio track gets a channel of its own
    When I add an audio track recording input channel 1
    Then track 2's channel is not track 1's channel

  Scenario: an audio track holds no notes
    Given I have added an audio track recording input channel 1
    And the caret is on track 2
    When I enter fret 5 on string 1
    Then the action is refused

  Scenario: no other track can use an audio track's channel
    Given I have added an audio track recording input channel 1
    When I set track 1's channel to the channel of track 2
    Then the action is refused
    And track 2's sound chain reads "takes"

  Scenario: an audio track cannot use another track's channel
    Given I have added an audio track recording input channel 1
    When I set track 2's channel to the channel of track 1
    Then the action is refused

  Scenario: cloning an audio track clones its takes and sound chain onto a new channel
    Given the VST3 folders hold the test plugins
    And I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have added the VST3 effect "Test Gain" to track 2
    When I clone track 2
    Then track 3 is an audio track
    And track 3's takes read "take 1: m1b1 to m2b1"
    And track 3's sound chain reads "takes > Test Gain"
    And track 3's channel is not track 2's channel

  Scenario: removing an audio track and undoing brings its takes back sample for sample
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have removed track 2
    When I undo
    Then track 2 is an audio track
    And take 1 of track 2 is sample for sample what it was before

  Scenario: moving an armed audio track keeps it armed and keeps its takes
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have armed track 2
    When I move track 2 up
    Then track 1 is an audio track
    And track 1 is armed
    And track 1's takes read "take 1: m1b1 to m2b1"

  Scenario: a guitar track cannot become an audio track, nor an audio track a guitar track
    Given I have added an audio track recording input channel 1
    When I give track 1 the source "takes"
    Then the action is refused
    And track 1's sound chain reads "Steel String Acoustic Guitar"

  Scenario Outline: muting and soloing work on audio tracks as on other tracks
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have <done>
    When I play the song from measure 1 beat 1
    Then the output <result> a 440 Hz tone above -60 dBFS

    Examples:
      | done              | result        |
      | muted track 2     | does not hold |
      | soloed track 1    | does not hold |
      | soloed track 2    | holds         |
      | left both unmuted | holds         |

  Scenario Outline: audio tracks are silent and cannot record unless the MIDI Port is the TuxGuitar Synthesizer
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And the MIDI Port is "<port>"
    When I play the song from measure 1 beat 1
    Then the output <result> a 440 Hz tone above -60 dBFS

    Examples:
      | port                  | result        |
      | TuxGuitar Synthesizer | holds         |
      | Test MIDI Out         | does not hold |

  Scenario: recording is refused unless the MIDI Port is the TuxGuitar Synthesizer
    Given I have added an audio track recording input channel 1
    And I have armed track 2
    And the MIDI Port is "Test MIDI Out"
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then the action is refused
    And track 2's takes read ""
