@planned
Feature: Hosting VST3 plugins
  Each track sounds through its sound chain: a source followed by VST3 effects, in order. The source is a General MIDI
  program played by the TuxGuitar Synthesizer, a VST3 instrument, or an audio track's takes. A disabled VST3 effect is
  passed by. The sound chain belongs to the track's channel, and VST3 plugins run in a process apart from TuxGuitar.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And the MIDI Port is "TuxGuitar Synthesizer"
    And the audio device is "Test Interface"
    And the VST3 folders hold the test plugins
    And I have set track 1's General MIDI program to "Distortion Electric Guitar"

  # Finding plugins

  Scenario: scanning the VST3 folders lists VST3 effects and VST3 instruments apart
    When I scan the VST3 folders
    Then the VST3 effects read "Test Clip 0.5, Test Echo, Test Gain, Test Latency, Test Tempo"
    And the VST3 instruments read "Test Click, Test Sine"

  Scenario: only VST3 plugins are listed
    Given the VST3 folders also hold the VST 2 plugin "Old Amp"
    When I scan the VST3 folders
    Then the VST3 effects read "Test Clip 0.5, Test Echo, Test Gain, Test Latency, Test Tempo"

  Scenario: VST3 plugins run in a process apart from TuxGuitar, which listens on no network port
    Given I have added the VST3 effect "Test Gain" to track 1
    When I play the song from measure 1 beat 1
    Then "Test Gain" runs in a process other than TuxGuitar's
    And TuxGuitar listens on no network port

  # VST3 effects

  Scenario: a VST3 effect is added at the end of a track's sound chain
    When I add the VST3 effect "Test Gain" to track 1
    Then the action succeeds
    And track 1's sound chain reads "Distortion Electric Guitar > Test Gain"

  Scenario: a VST3 effect processes a General MIDI program played by the TuxGuitar Synthesizer
    Given I have entered fret 5 on string 1
    And I have added the VST3 effect "Test Gain" to track 1
    When I play the song from measure 1 beat 1
    Then track 1 peaks twice as high as without the VST3 effect, within 1%

  Scenario: a VST3 effect processes an audio track's takes
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have added the VST3 effect "Test Gain" to track 2
    When I play the song from measure 1 beat 1
    Then track 2 sounds a 440 Hz tone peaking at 0.8 within 0.01 in measure 1

  Scenario Outline: VST3 effects process the signal in the order of the sound chain
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have added the VST3 effect "<first>" to track 2
    And I have added the VST3 effect "<second>" to track 2
    When I play the song from measure 1 beat 1
    Then track 2 sounds a 440 Hz tone peaking at <peak> within 0.01 in measure 1

    Examples:
      | first         | second        | peak |
      | Test Gain     | Test Clip 0.5 | 0.5  |
      | Test Clip 0.5 | Test Gain     | 0.8  |

  Scenario: the sound chain has room above full scale between VST3 effects
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.8 from measure 1 beat 1 to measure 2 beat 1
    And I have added the VST3 effect "Test Gain" to track 2
    And I have added the VST3 effect "Test Clip 0.5" to track 2
    When I play the song from measure 1 beat 1
    Then track 2 sounds a 440 Hz tone clipped at 0.5 within 0.01 in measure 1

  Scenario: Volume and Balance apply after the sound chain
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have added the VST3 effect "Test Gain" to track 2
    And I have added the VST3 effect "Test Clip 0.5" to track 2
    And I have set the Volume of track 2's channel to 32
    When I play the song from measure 1 beat 1
    Then track 2 sounds a 440 Hz tone peaking at 0.5 within 0.01 in measure 1
    And the output peaks lower in measure 1 than it does at Volume 127

  Scenario: moving a VST3 effect up changes the order it processes in
    Given I have added the VST3 effect "Test Gain" to track 1
    And I have added the VST3 effect "Test Clip 0.5" to track 1
    When I move VST3 effect 2 of track 1 up
    Then track 1's sound chain reads "Distortion Electric Guitar > Test Clip 0.5 > Test Gain"

  Scenario: a disabled VST3 effect is passed by
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have added the VST3 effect "Test Gain" to track 2
    And I have disabled VST3 effect 1 of track 2
    When I play the song from measure 1 beat 1
    Then track 2's sound chain reads "takes > [Test Gain]"
    And track 2 sounds a 440 Hz tone peaking at 0.4 within 0.01 in measure 1

  Scenario: enabling a disabled VST3 effect brings it back
    Given I have added the VST3 effect "Test Gain" to track 1
    And I have disabled VST3 effect 1 of track 1
    When I enable VST3 effect 1 of track 1
    Then track 1's sound chain reads "Distortion Electric Guitar > Test Gain"

  Scenario: removing a VST3 effect takes it out of the sound chain
    Given I have added the VST3 effect "Test Gain" to track 1
    And I have added the VST3 effect "Test Clip 0.5" to track 1
    When I remove VST3 effect 1 of track 1
    Then track 1's sound chain reads "Distortion Electric Guitar > Test Clip 0.5"

  Scenario: the same VST3 effect can be added twice
    Given I have added the VST3 effect "Test Gain" to track 1
    When I add the VST3 effect "Test Gain" to track 1
    Then track 1's sound chain reads "Distortion Electric Guitar > Test Gain > Test Gain"

  Scenario: tracks that share a channel share its sound chain
    Given I have added a guitar track on the channel of track 1
    When I add the VST3 effect "Test Gain" to track 1
    Then track 2's sound chain reads "Distortion Electric Guitar > Test Gain"

  # Undo and redo of sound chain changes

  Scenario Outline: changes to the sound chain can be undone
    Given I have added the VST3 effect "Test Gain" to track 1
    And I have added the VST3 effect "Test Clip 0.5" to track 1
    And I have <changed>
    When I undo
    Then track 1's sound chain reads "Distortion Electric Guitar > Test Gain > Test Clip 0.5"

    Examples:
      | changed                                           |
      | added the VST3 effect "Test Echo" to track 1      |
      | removed VST3 effect 1 of track 1                  |
      | moved VST3 effect 2 of track 1 up                 |
      | disabled VST3 effect 1 of track 1                 |
      | chosen the VST3 instrument "Test Sine" for track 1 |

  Scenario Outline: undone changes to the sound chain can be redone
    Given I have added the VST3 effect "Test Gain" to track 1
    And I have added the VST3 effect "Test Clip 0.5" to track 1
    And I have <changed>
    And I have undone
    When I redo
    Then track 1's sound chain reads "<after>"

    Examples:
      | changed                                            | after                                                              |
      | added the VST3 effect "Test Echo" to track 1       | Distortion Electric Guitar > Test Gain > Test Clip 0.5 > Test Echo |
      | removed VST3 effect 1 of track 1                   | Distortion Electric Guitar > Test Clip 0.5                         |
      | moved VST3 effect 2 of track 1 up                  | Distortion Electric Guitar > Test Clip 0.5 > Test Gain             |
      | disabled VST3 effect 1 of track 1                  | Distortion Electric Guitar > [Test Gain] > Test Clip 0.5           |
      | chosen the VST3 instrument "Test Sine" for track 1 | Test Sine > Test Gain > Test Clip 0.5                              |

  # VST3 instruments

  Scenario: a VST3 instrument replaces the General MIDI program as the source
    Given I have entered fret 5 on string 1
    And I have chosen the VST3 instrument "Test Sine" for track 1
    When I play the song from measure 1 beat 1
    Then track 1's sound chain reads "Test Sine"
    And track 1 sounds a 440 Hz sine within 1 Hz in measure 1

  Scenario: the VST3 effects stay when the source changes
    Given I have added the VST3 effect "Test Gain" to track 1
    When I choose the VST3 instrument "Test Sine" for track 1
    Then track 1's sound chain reads "Test Sine > Test Gain"

  Scenario: going back to General MIDI restores the program the track had
    Given I have chosen the VST3 instrument "Test Sine" for track 1
    When I go back to the General MIDI program for track 1
    Then track 1's sound chain reads "Distortion Electric Guitar"

  Scenario: an audio track cannot take a VST3 instrument
    Given I have added an audio track recording input channel 1
    When I choose the VST3 instrument "Test Sine" for track 2
    Then the action is refused
    And track 2's sound chain reads "takes"

  Scenario Outline: a plugin is used only as what it is
    When I <use>
    Then the action is refused
    And track 1's sound chain reads "Distortion Electric Guitar"

    Examples:
      | use                                              |
      | choose the VST3 instrument "Test Gain" for track 1 |
      | add the VST3 effect "Test Sine" to track 1         |

  Scenario: entering a note sounds it through the VST3 instrument
    Given I have chosen the VST3 instrument "Test Sine" for track 1
    When I enter fret 5 on string 1
    Then the output sounds a 440 Hz sine within 1 Hz

  Scenario Outline: dynamics reach a VST3 instrument as velocity
    Given I have entered fret 5 on string 1 at dynamic <dynamic>
    And I have chosen the VST3 instrument "Test Sine" for track 1
    When I play the song from measure 1 beat 1
    Then track 1 sounds a 440 Hz sine peaking at <peak> within 0.01 in measure 1

    Examples:
      | dynamic | peak  |
      | fff     | 0.5   |
      | mf      | 0.311 |
      | ppp     | 0.059 |

  Scenario: a bend reaches a VST3 instrument as a rise in pitch
    Given I have entered fret 5 on string 1 with a full bend
    And I have chosen the VST3 instrument "Test Sine" for track 1
    When I play the song from measure 1 beat 1
    Then track 1 sounds a 440 Hz sine rising to 493.9 Hz within 1 Hz in measure 1

  Scenario Outline: VST3 plugins are told the tempo, time signature and play state that sound
    Given I have set the tempo of measure 3 to 60 bpm
    And I have set the time signature of measure 3 to 3/4
    And I have set the Percentage of Tempo to <percent>
    And I have added the VST3 effect "Test Tempo" to track 1
    When I play the song from measure 1 beat 1
    Then "Test Tempo" was told <first> bpm in 4/4 in measure 1 and <third> bpm in 3/4 in measure 3
    And "Test Tempo" was told the song was playing in every measure

    Examples:
      | percent | first | third |
      | 100     | 120   | 60    |
      | 80      | 96    | 48    |

  # Plugin editors and parameters

  Scenario: opening a VST3 plugin's editor shows the plugin's own editor
    Given I have added the VST3 effect "Test Gain" to track 1
    When I open the editor of VST3 effect 1 of track 1
    Then the plugin's own editor of VST3 effect 1 of track 1 is open

  Scenario: a VST3 plugin without an editor of its own gets TuxGuitar's editor of its parameters
    Given I have added the VST3 effect "Test Clip 0.5" to track 1
    When I open the editor of VST3 effect 1 of track 1
    Then TuxGuitar's editor lists the parameters "Ceiling" of VST3 effect 1 of track 1

  Scenario: a change made in a plugin's editor changes the sound and modifies the song
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.1 from measure 1 beat 1 to measure 2 beat 1
    And I have added the VST3 effect "Test Gain" to track 2
    When the plugin sets "Gain" of VST3 effect 1 of track 2 to 3.0
    Then the song is modified
    And when the song plays, track 2 sounds a 440 Hz tone peaking at 0.3 within 0.01 in measure 1

  Scenario: one gesture in a plugin's editor is one undo step
    Given I have added the VST3 effect "Test Gain" to track 1
    And the plugin has set "Gain" of VST3 effect 1 of track 1 through 2.5 to 3.0 in one gesture
    When I undo
    Then "Gain" of VST3 effect 1 of track 1 is 2.0
