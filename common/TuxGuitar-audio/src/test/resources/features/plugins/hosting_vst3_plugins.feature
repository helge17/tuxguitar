Feature: Hosting VST3 plugins
  Each track sounds through its sound chain: a source followed by effects, in order. The source is a General MIDI
  program played by the TuxGuitar Synthesizer, a VST3 instrument, or an audio track's takes. A disabled effect is
  passed by. The sound chain belongs to the track's channel, and plugins run in a process apart from TuxGuitar.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And I have set the MIDI Port to "TuxGuitar Synthesizer"
    And I have set the audio device to "Test Interface"
    And the VST3 folders hold the test plugins
    And I have set the General MIDI program of track 1's channel to "Distortion Electric Guitar"

  # Finding and running plugins

  Scenario: scanning the VST3 folders lists VST3 effects and VST3 instruments apart
    When I scan the VST3 folders
    Then the VST3 effects read "Test Clip 0.5, Test Drone, Test Echo, Test Gain, Test Latency, Test Stereo, Test Tempo"
    And the VST3 instruments read "Test Click, Test Sine"

  Scenario: a VST 2 plugin in the VST3 folders is not listed
    Given the VST3 folders also hold the VST 2 plugin "Old Amp"
    When I scan the VST3 folders
    Then neither the VST3 effects nor the VST3 instruments name "Old Amp"
    And the VST3 effects read "Test Clip 0.5, Test Drone, Test Echo, Test Gain, Test Latency, Test Stereo, Test Tempo"

  Scenario: VST3 plugins run in a process apart from TuxGuitar, and nothing listens on a network port
    Given I have added the VST3 effect "Test Gain" to track 1
    When I play the song from measure 1 beat 1
    Then "Test Gain" runs in a process other than TuxGuitar's
    And neither TuxGuitar nor the plugin process listens on a network port

  # VST3 effects

  Scenario: a VST3 effect is added at the end of a track's sound chain
    When I add the VST3 effect "Test Gain" to track 1
    Then the action succeeds
    And track 1's sound chain reads "Distortion Electric Guitar > Test Gain"

  Scenario: a VST3 effect processes a General MIDI program played by the TuxGuitar Synthesizer
    Given I have entered fret 5 on string 1
    And I have added the VST3 effect "Test Clip 0.5" to track 1
    And I have set "Ceiling" of VST3 effect 1 of track 1 to 0.01
    When I play the song from measure 1 beat 1
    Then track 1 peaks at 0.01 within 1% in measure 1

  Scenario: changing the General MIDI program keeps the VST3 effects
    Given I have added the VST3 effect "Test Gain" to track 1
    When I set the General MIDI program of track 1's channel to "Overdrive Electric Guitar"
    Then track 1's sound chain reads "Overdrive Electric Guitar > Test Gain"

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

  Scenario: the sound chain has room above full scale between effects
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.8 from measure 1 beat 1 to measure 2 beat 1
    And I have added the VST3 effect "Test Gain" to track 2
    And I have added the VST3 effect "Test Gain" to track 2
    And I have set "Gain" of VST3 effect 2 of track 2 to 0.25
    When I play the song from measure 1 beat 1
    Then track 2 sounds a 440 Hz tone peaking at 0.4 within 0.01 in measure 1

  Scenario: a VST3 instrument's sound chain has room above full scale between effects
    Given I have entered fret 5 on string 1 at dynamic fff
    And I have chosen the VST3 instrument "Test Sine" for track 1
    And I have added the VST3 effect "Test Gain" to track 1
    And I have set "Gain" of VST3 effect 1 of track 1 to 4.0
    And I have added the VST3 effect "Test Gain" to track 1
    And I have set "Gain" of VST3 effect 2 of track 1 to 0.25
    When I play the song from measure 1 beat 1
    Then track 1 sounds a 440 Hz tone peaking at 0.5 within 0.01 in measure 1

  Scenario: Volume applies after the sound chain
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have added the VST3 effect "Test Gain" to track 2
    And I have added the VST3 effect "Test Clip 0.5" to track 2
    And I have set the Volume of track 2's channel to 32
    When I play the song from measure 1 beat 1
    Then track 2 sounds a 440 Hz tone peaking at 0.5 within 0.01 in measure 1
    And the output peaks below 0.3 in measure 1

  Scenario Outline: Balance applies after the sound chain
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have muted track 1
    And I have set the Balance of track 2's channel to <balance>
    When I play the song from measure 1 beat 1
    Then the output's left side <left> 440 Hz tone above -60 dBFS
    And the output's right side <right> 440 Hz tone above -60 dBFS

    Examples:
      | balance | left     | right    |
      | 0       | holds a  | holds no |
      | 127     | holds no | holds a  |

  Scenario: a mono take at centre Balance sounds equally left and right
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have muted track 1
    And I have set the Balance of track 2's channel to 64
    When I play the song from measure 1 beat 1
    Then the output's left and right sides peak within 2% of each other in measure 1

  Scenario: a mono take through a VST3 effect with a stereo output sounds as the effect makes it
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have added the VST3 effect "Test Stereo" to track 2
    When I play the song from measure 1 beat 1
    Then track 2's left side sounds a 440 Hz tone peaking at 0.4 within 0.01 in measure 1
    And track 2's right side sounds a 440 Hz tone peaking at 0.2 within 0.01 in measure 1

  Scenario: Balance scales each side of a stereo signal by today's law
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    And I have added the VST3 effect "Test Stereo" to track 2
    And I have muted track 1
    And I have set the Balance of track 2's channel to 64
    When I play the song from measure 1 beat 1
    Then the output's left side peaks at 0.281 within 0.005 in measure 1
    And the output's right side peaks at 0.142 within 0.005 in measure 1

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
      | changed                                            |
      | added the VST3 effect "Test Echo" to track 1       |
      | removed VST3 effect 1 of track 1                   |
      | moved VST3 effect 2 of track 1 up                  |
      | disabled VST3 effect 1 of track 1                  |
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
    And track 1 sounds a 440 Hz tone within 1 Hz in measure 1

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
      | use                                                |
      | choose the VST3 instrument "Test Gain" for track 1 |
      | add the VST3 effect "Test Sine" to track 1         |

  @planned
  Scenario: entering a note sounds it through the VST3 instrument
    Given I have chosen the VST3 instrument "Test Sine" for track 1
    When I enter fret 5 on string 1
    Then the output sounds a 440 Hz tone within 1 Hz within 0.5 seconds of entering it

  Scenario Outline: dynamics reach a VST3 instrument as velocity
    Given I have entered fret 5 on string 1 at dynamic <dynamic>
    And I have chosen the VST3 instrument "Test Sine" for track 1
    When I play the song from measure 1 beat 1
    Then track 1 sounds a 440 Hz tone peaking at <peak> within 0.01 in measure 1

    Examples:
      | dynamic | peak  |
      | fff     | 0.5   |
      | mf      | 0.311 |
      | ppp     | 0.059 |

  @planned
  Scenario: a bend on one note of a chord reaches the VST3 instrument for that note only, in exact semitones
    Given I have entered fret 5 on string 1 with a full bend
    And I have entered fret 5 on string 2
    And I have chosen the VST3 instrument "Test Sine" for track 1
    When I play the song from measure 1 beat 1
    Then track 1 sounds a 440 Hz tone rising to 493.9 Hz within 1 Hz in measure 1
    And track 1 sounds a 329.6 Hz tone that stays within 1 Hz in measure 1

  @planned
  Scenario: the Metronome does not go through a percussion track's VST3 instrument
    Given I have added a percussion track
    And I have chosen the VST3 instrument "Test Sine" for track 2
    And I have turned the Metronome on
    When I play the song from measure 1 beat 1
    Then the output holds 16 Metronome clicks
    And track 2 sounds no tone above -60 dBFS

  @planned
  Scenario Outline: VST3 plugins are told the tempo, time signature and play state that sound
    Given I have set the tempo of measure 3 only to 60 bpm
    And I have set the time signature of measure 3 only to 3/4
    And I have set the Percentage of Tempo to <percent>
    And I have added the VST3 effect "Test Tempo" to track 1
    When I play the song from measure 1 beat 1
    Then "Test Tempo" was told <first> bpm in 4/4 in measure 1 and <third> bpm in 3/4 in measure 3
    And "Test Tempo" was told the song was playing in every measure

    Examples:
      | percent | first | third |
      | 100     | 120   | 60    |
      | 80      | 96    | 48    |

  @planned
  Scenario: VST3 plugins are told the musical position, which jumps back with a loop
    Given I have turned Play looped on over measures 2 to 3
    And I have added the VST3 effect "Test Tempo" to track 1
    When I play the song from measure 2 beat 1 for 2 passes
    Then "Test Tempo" was told musical position 4 at the start of each pass, within 1/22050 of a quarter note

  # Plugin editors, parameters and presets

  @planned
  Scenario: opening a VST3 plugin's editor shows the plugin's own editor without modifying the song
    Given I have added the VST3 effect "Test Gain" to track 1
    And I have saved the song as a TuxGuitar file
    When I open the editor of VST3 effect 1 of track 1
    Then the plugin's own editor of VST3 effect 1 of track 1 is open
    And the song is not modified

  @planned
  Scenario: a VST3 plugin without an editor of its own is edited through its parameters
    Given I have added the VST3 effect "Test Clip 0.5" to track 1
    When I open the editor of VST3 effect 1 of track 1
    Then TuxGuitar's parameter editor of VST3 effect 1 of track 1 is open
    And the parameters of VST3 effect 1 of track 1 read "Ceiling"

  @planned
  Scenario: a change made in a plugin's editor changes the sound
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.1 from measure 1 beat 1 to measure 2 beat 1
    And I have added the VST3 effect "Test Gain" to track 2
    And the plugin has set "Gain" of VST3 effect 1 of track 2 to 3.0
    When I play the song from measure 1 beat 1
    Then track 2 sounds a 440 Hz tone peaking at 0.3 within 0.01 in measure 1

  @planned
  Scenario: a change made in a plugin's editor modifies the song
    Given I have added the VST3 effect "Test Gain" to track 1
    And I have saved the song as a TuxGuitar file
    When the plugin sets "Gain" of VST3 effect 1 of track 1 to 3.0
    Then the song is modified

  @planned
  Scenario: one gesture in a plugin's editor is one undo step
    Given I have added the VST3 effect "Test Gain" to track 1
    And the plugin has set "Gain" of VST3 effect 1 of track 1 through 2.5 to 3.0
    When I undo
    Then "Gain" of VST3 effect 1 of track 1 is 2.0

  @planned
  Scenario: a preset chosen in a plugin's own editor modifies the song
    Given I have added the VST3 effect "Test Gain" to track 1
    And I have saved the song as a TuxGuitar file
    When the plugin loads its preset "Loud" for VST3 effect 1 of track 1
    Then the song is modified
    And the state of VST3 effect 1 of track 1 is the preset "Loud"

  @planned
  Scenario: a preset chosen in a plugin's own editor is one undo step
    Given I have added the VST3 effect "Test Gain" to track 1
    And the plugin has loaded its preset "Loud" for VST3 effect 1 of track 1
    When I undo
    Then the state of VST3 effect 1 of track 1 is byte for byte what it was before the preset
