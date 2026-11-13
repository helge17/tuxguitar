Feature: Audio tracks
  An audio track sounds its takes instead of notes. It has no strings, holds no notes, and has a channel of its own,
  outside the 16 MIDI channels. Audio tracks need the TuxGuitar Synthesizer as the MIDI Port.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And I have set the MIDI Port to "TuxGuitar Synthesizer"
    And I have set the audio device to "Test Interface"
    And the VST3 folders hold the test plugins

  Scenario: a new audio track has no strings, no takes and a channel of its own
    When I add an audio track recording input channel 2
    Then the action succeeds
    And track 2 is an audio track with 0 strings
    And track 2 records input channel 2
    And track 2's takes read ""
    And track 2's sound chain reads "takes"
    And track 2's channel is not track 1's channel

  Scenario: an audio track can be added when no MIDI channel is free, and takes none
    Given I have added guitar tracks, each on a channel of its own, until no MIDI channel is free
    When I add an audio track recording input channel 1
    Then the action succeeds
    And the last track is an audio track
    And the last track's channel is given no MIDI channel

  Scenario: an audio track holds no notes
    Given I have added an audio track recording input channel 1
    And I have moved the caret to track 2
    When I enter fret 5 on string 1
    Then the action is refused
    And track 2 holds no notes

  Scenario Outline: an audio track and another track cannot share a channel
    Given I have added an audio track recording input channel 1
    When I set track <from>'s channel to the channel of track <to>
    Then the action is refused
    And track 1's sound chain reads "Steel String Acoustic Guitar"
    And track 2's sound chain reads "takes"

    Examples:
      | from | to |
      | 1    | 2  |
      | 2    | 1  |

  Scenario Outline: an audio track's channel takes no General MIDI program, and a guitar track cannot become an audio track
    Given I have added an audio track recording input channel 1
    When I <change>
    Then the action is refused
    And track 1's sound chain reads "Steel String Acoustic Guitar"
    And track 2's sound chain reads "takes"

    Examples:
      | change                                                                            |
      | set the General MIDI program of track 2's channel to "Distortion Electric Guitar" |
      | make track 2's channel a percussion channel                                       |

    # choosing a sound chain's source comes with VST3 instruments
    @planned
    Examples:
      | change                                    |
      | give track 1's channel the source "takes" |

  Scenario: cloning an audio track clones its takes and sound chain onto a new channel
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have added the VST3 effect "Test Gain" to track 2
    And I have set "Gain" of VST3 effect 1 of track 2 to 3.0
    And I have armed track 2
    When I clone track 2
    Then track 3 is an audio track
    And track 3's takes read "take 1: m1b1 to m2b1"
    And track 3's sound chain reads "takes > Test Gain"
    And "Gain" of VST3 effect 1 of track 3 is 3.0
    And track 3's channel is not track 2's channel
    And track 3 is not armed

  Scenario: a cloned audio track keeps the takes chosen per measure and the next take number
    Given I have added an audio track recording input channel 1
    And I have armed track 2
    And I have recorded from measure 1 beat 1 to measure 3 beat 1
    And I have recorded from measure 1 beat 1 to measure 3 beat 1
    And I have chosen take 1 of track 2 for measures 2 to 2
    And I have cloned track 2
    And I have armed track 3
    When I record from measure 3 beat 1 to measure 4 beat 1
    Then track 3's takes read "take 3: m3b1 to m4b1, take 2: m1b1 to m3b1, take 1: m1b1 to m3b1"
    And track 3's playback reads "m1: take 2, m2: take 1, m3: take 3, m4: none"

  Scenario: removing an audio track removes its channel
    Given I have added an audio track recording input channel 1
    When I remove track 2
    Then the song has 1 channel

  Scenario: removing an armed audio track and undoing brings it back with its takes, not armed
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have armed track 2
    And I have removed track 2
    When I undo
    Then track 2 is an audio track
    And take 1 of track 2 is sample for sample what it was
    And track 2 is not armed

  Scenario: moving an armed audio track keeps it armed and keeps its takes
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have armed track 2
    When I move track 2 up
    Then track 1 is an audio track
    And track 1 is armed
    And track 1's takes read "take 1: m1b1 to m2b1"

  Scenario Outline: audio tracks are muted and soloed as other tracks are
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have <done>
    When I play the song from measure 1 beat 1
    Then the output <result> 440 Hz tone above -60 dBFS

    Examples:
      | done           | result        |
      | muted track 2  | holds no      |
      | soloed track 1 | holds no      |
      | soloed track 2 | holds a       |
      | muted track 1  | holds a       |

  Scenario Outline: audio tracks sound only when the MIDI Port is the TuxGuitar Synthesizer
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have set the MIDI Port to "<port>"
    When I play the song from measure 1 beat 1
    Then the output <result> 440 Hz tone above -60 dBFS

    Examples:
      | port                  | result   |
      | TuxGuitar Synthesizer | holds a  |
      | Test MIDI Out         | holds no |

  Scenario Outline: recording needs the TuxGuitar Synthesizer as the MIDI Port
    Given I have added an audio track recording input channel 1
    And I have armed track 2
    And I have set the MIDI Port to "<port>"
    When I record from measure 1 beat 1 to measure 2 beat 1
    Then the action <outcome>
    And track 2's takes read "<takes>"

    Examples:
      | port                  | outcome    | takes                |
      | TuxGuitar Synthesizer | succeeds   | take 1: m1b1 to m2b1 |
      | Test MIDI Out         | is refused |                      |
