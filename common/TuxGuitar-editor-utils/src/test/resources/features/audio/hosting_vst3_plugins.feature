@planned
Feature: Hosting VST3 plugins
  Each track sounds through a sound chain: its instrument followed by its effects, in order.
  The instrument is a General MIDI program played by TuxGuitar's synthesizer, a VST3 instrument, or, on an audio
  track, its takes. Effects are VST3 effects such as amp modelers and pedals; disabled effects are passed by.
  The sound chain belongs to the track's channel, so tracks that share a channel share it.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And track 1's instrument is "Distortion Electric Guitar"
    And the VST3 folders hold these plugins:
      | name          | kind       | does                                   |
      | Test Gain     | effect     | multiplies the signal by its Gain parameter, 2 by default |
      | Test Clip 0.5 | effect     | clips the signal at 0.5                |
      | Test Delay    | effect     | delays the signal by 256 samples       |
      | Test Sine     | instrument | plays a sine at the pitch of each note |

  Scenario: the installed VST3 plugins are listed by kind
    When I look at the available VST3 plugins
    Then the VST3 effects read "Test Clip 0.5, Test Delay, Test Gain"
    And the VST3 instruments read "Test Sine"

  Scenario: a VST3 effect is added to the end of a track's sound chain
    When I add the VST3 effect "Test Gain" to track 1
    Then track 1's sound chain reads "Distortion Electric Guitar > Test Gain"
    And the action succeeds

  Scenario: a VST3 effect processes a track played by the synthesizer
    Given I have entered fret 5 on string 1
    When I add the VST3 effect "Test Gain" to track 1
    Then track 1 sounds twice as loud as without the effect, within 1%

  Scenario: a VST3 effect processes an audio track's takes
    Given I have added an audio track
    And track 2 has a take of a 440 Hz tone peaking at 0.4 over measure 1
    When I add the VST3 effect "Test Gain" to track 2
    Then track 2 sounds a 440 Hz tone peaking at 0.8 within 0.01 in measure 1

  Scenario Outline: effects process the signal in the order of the sound chain
    Given I have added an audio track
    And track 2 has a take of a 440 Hz tone peaking at 0.4 over measure 1
    And I have added the VST3 effect "<first>" to track 2
    And I have added the VST3 effect "<second>" to track 2
    Then track 2 sounds a 440 Hz tone peaking at <peak> within 0.01 in measure 1

    Examples:
      | first         | second        | peak |
      | Test Gain  | Test Clip 0.5 | 0.5  |
      | Test Clip 0.5 | Test Gain  | 0.8  |

  Scenario: moving an effect up changes the order it processes in
    Given I have added the VST3 effect "Test Gain" to track 1
    And I have added the VST3 effect "Test Clip 0.5" to track 1
    When I move the effect "Test Clip 0.5" of track 1 up
    Then track 1's sound chain reads "Distortion Electric Guitar > Test Clip 0.5 > Test Gain"

  Scenario: a disabled effect is passed by
    Given I have added an audio track
    And track 2 has a take of a 440 Hz tone peaking at 0.4 over measure 1
    And I have added the VST3 effect "Test Gain" to track 2
    When I disable the effect "Test Gain" of track 2
    Then track 2's sound chain reads "takes > [Test Gain]"
    And track 2 sounds a 440 Hz tone peaking at 0.4 within 0.01 in measure 1

  Scenario: enabling a disabled effect brings it back
    Given I have added the VST3 effect "Test Gain" to track 1
    And I have disabled the effect "Test Gain" of track 1
    When I enable the effect "Test Gain" of track 1
    Then track 1's sound chain reads "Distortion Electric Guitar > Test Gain"

  Scenario: removing an effect takes it out of the sound chain
    Given I have added the VST3 effect "Test Gain" to track 1
    And I have added the VST3 effect "Test Clip 0.5" to track 1
    When I remove the effect "Test Gain" from track 1
    Then track 1's sound chain reads "Distortion Electric Guitar > Test Clip 0.5"

  Scenario: the same effect can be added twice
    Given I have added the VST3 effect "Test Gain" to track 1
    When I add the VST3 effect "Test Gain" to track 1
    Then track 1's sound chain reads "Distortion Electric Guitar > Test Gain > Test Gain"

  Scenario Outline: changes to the sound chain can be undone and redone
    Given I have added the VST3 effect "Test Gain" to track 1
    And I have added the VST3 effect "Test Clip 0.5" to track 1
    When I <change>
    And I undo
    Then track 1's sound chain reads "Distortion Electric Guitar > Test Gain > Test Clip 0.5"
    When I redo
    Then track 1's sound chain reads "<after>"

    Examples:
      | change                                          | after                                                    |
      | add the VST3 effect "Test Delay" to track 1     | Distortion Electric Guitar > Test Gain > Test Clip 0.5 > Test Delay |
      | remove the effect "Test Gain" from track 1   | Distortion Electric Guitar > Test Clip 0.5                 |
      | move the effect "Test Clip 0.5" of track 1 up   | Distortion Electric Guitar > Test Clip 0.5 > Test Gain  |
      | disable the effect "Test Gain" of track 1    | Distortion Electric Guitar > [Test Gain] > Test Clip 0.5 |

  Scenario: tracks that share a channel share its sound chain
    Given I have added a guitar track on the channel of track 1
    When I add the VST3 effect "Test Gain" to track 1
    Then track 2's sound chain reads "Distortion Electric Guitar > Test Gain"

  Scenario: a VST3 instrument replaces the General MIDI program
    Given I have entered fret 5 on string 1
    When I choose the VST3 instrument "Test Sine" for track 1
    Then track 1's sound chain reads "Test Sine"
    And track 1 sounds a 440 Hz sine within 1 Hz in measure 1

  Scenario: the effects stay when the instrument changes
    Given I have added the VST3 effect "Test Gain" to track 1
    When I choose the VST3 instrument "Test Sine" for track 1
    Then track 1's sound chain reads "Test Sine > Test Gain"

  Scenario: going back to General MIDI restores the program the track had
    Given I have chosen the VST3 instrument "Test Sine" for track 1
    When I choose General MIDI as the instrument of track 1
    Then track 1's sound chain reads "Distortion Electric Guitar"

  Scenario: an audio track cannot take a VST3 instrument
    Given I have added an audio track
    When I choose the VST3 instrument "Test Sine" for track 2
    Then the action is refused
    And track 2's sound chain reads "takes"

  Scenario: a VST3 effect cannot be chosen as an instrument, nor an instrument added as an effect
    When I choose the VST3 instrument "Test Gain" for track 1
    Then the action is refused
    When I add the VST3 effect "Test Sine" to track 1
    Then the action is refused
    And track 1's sound chain reads "Distortion Electric Guitar"

  Scenario: opening a plugin's editor asks the plugin to show it
    Given I have added the VST3 effect "Test Gain" to track 1
    When I open the editor of the effect "Test Gain" of track 1
    Then the plugin "Test Gain" of track 1 is showing its editor

  Scenario: a change made in a plugin's editor changes the sound and modifies the song
    Given I have added an audio track
    And track 2 has a take of a 440 Hz tone peaking at 0.1 over measure 1
    And I have added the VST3 effect "Test Gain" to track 2
    When the plugin "Test Gain" of track 2 sets its parameter "Gain" to 3.0
    Then track 2 sounds a 440 Hz tone peaking at 0.3 within 0.01 in measure 1
    And the song is modified
