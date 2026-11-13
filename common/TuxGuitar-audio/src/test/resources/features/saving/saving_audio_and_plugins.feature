Feature: Saving audio tracks and plugins
  A song saved as a TuxGuitar file keeps its audio tracks with their takes sample for sample, and every sound chain
  with each plugin's state. A song with an audio track needs file format 3.0, which older TuxGuitar versions opening
  it as a .tg file refuse with their "please upgrade" message; any other song stays in format 2.0, which they still
  open.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And I have set the MIDI Port to "TuxGuitar Synthesizer"
    And I have set the audio device to "Test Interface"
    And the VST3 folders hold the test plugins

  Scenario: takes come back sample for sample, where they were, chosen as they were
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 3 beat 1
    And track 2 has a take of clicks of -0.25 on every beat from measure 2 beat 1 to measure 4 beat 1
    And I have chosen take 1 of track 2 for measures 2 to 2
    And I have saved the song as a TuxGuitar file
    When I open it again
    Then track 2 is an audio track
    And track 2's takes read "take 2: m2b1 to m4b1, take 1: m1b1 to m3b1"
    And track 2's playback reads "m1: take 1, m2: take 1, m3: take 2, m4: none"
    And every take of track 2 is sample for sample what it was

  Scenario: a deleted take is not saved, and its number is not given again after reopening
    Given I have added an audio track recording input channel 1
    And I have armed track 2
    And I have recorded from measure 1 beat 1 to measure 2 beat 1
    And I have recorded from measure 2 beat 1 to measure 3 beat 1
    And I have deleted take 2 of track 2
    And I have saved the song as a TuxGuitar file
    And I have opened it again
    And I have armed track 2
    When I record from measure 3 beat 1 to measure 4 beat 1
    Then track 2's takes read "take 3: m3b1 to m4b1, take 1: m1b1 to m2b1"

  Scenario: a take keeps the tempo it was recorded at across saving
    Given I have added an audio track recording input channel 1
    And I have armed track 2
    And I have set the Percentage of Tempo to 80
    And the player plays clicks of -0.25 on every beat on input channel 1
    And I have recorded from measure 1 beat 1 to measure 3 beat 1
    And I have saved the song as a TuxGuitar file
    And I have opened it again
    And I have set the Percentage of Tempo to 100
    When I play the song from measure 1 beat 1
    Then track 2 plays for 4 seconds within 5 ms
    And track 2 sounds 8 clicks, each within 5 ms of a beat

  Scenario: each audio track's input channels are saved with the song
    Given I have added an audio track recording input channel 2
    And I have added an audio track recording input channels 1 and 2
    And I have saved the song as a TuxGuitar file
    When I open it again
    Then track 2 records input channel 2
    And track 3 records input channels 1 and 2

  Scenario: sound chains come back with each plugin's state
    Given I have added an audio track recording input channel 1
    And I have chosen the VST3 instrument "Test Sine" for track 1
    And I have added the VST3 effect "Test Gain" to track 2
    And I have set "Gain" of VST3 effect 1 of track 2 to 3.0
    And I have added the VST3 effect "Test Gain" to track 2
    And I have disabled VST3 effect 2 of track 2
    And I have saved the song as a TuxGuitar file
    When I open it again
    Then track 1's sound chain reads "Test Sine"
    And track 2's sound chain reads "takes > Test Gain > [Test Gain]"
    And "Gain" of VST3 effect 1 of track 2 is 3.0
    And "Gain" of VST3 effect 2 of track 2 is 2.0
    And the state of every VST3 plugin is byte for byte what the plugin gave when the song was saved

  Scenario: the General MIDI program a VST3 instrument replaced comes back after reopening
    Given I have set the General MIDI program of track 1's channel to "Distortion Electric Guitar"
    And I have chosen the VST3 instrument "Test Sine" for track 1
    And I have saved the song as a TuxGuitar file
    And I have opened it again
    When I go back to the General MIDI program for track 1
    Then track 1's sound chain reads "Distortion Electric Guitar"

  Scenario Outline: what is not part of the song is not saved
    Given I have added an audio track recording input channel 1
    And I have <done>
    And I have saved the song as a TuxGuitar file
    When I open it again
    Then <after>

    Examples:
      | done                                         | after                       |
      | armed track 2                                | track 2 is not armed        |
      | armed track 2 and set its monitoring to Auto | track 2's monitoring is Off |
      | set the punch range to measures 2 to 3       | there is no punch range     |

  Scenario: the audio device is a TuxGuitar setting, not part of the song
    Given I have saved the song as a TuxGuitar file
    And I have set the audio device to "Other Interface"
    When I open it again
    Then the audio device is "Other Interface"

  # File format versions

  Scenario: a song with an audio track saves as file format 3.0, which format 2.0 readers refuse
    Given I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    When I save the song as a TuxGuitar file
    Then the file's format version is 3.0
    And a reader of file format 2.0 refuses it with "A new version of TuxGuitar is required to read this file, please upgrade"

  Scenario: an audio track without takes also needs file format 3.0
    Given I have added an audio track recording input channel 1
    When I save the song as a TuxGuitar file
    Then the file's format version is 3.0

  Scenario: a song without audio tracks or VST3 plugins saves exactly as before
    When I save the song as a TuxGuitar file
    Then the file's format version is 2.0
    And the file holds only "version.txt" and "content.xml"
    And its "content.xml" is the golden file "saving/no-audio.content.xml", the TuxGuitar version aside

  Scenario: this version still opens a format 2.0 file
    When I open the format 2.0 file "saving/no-audio.tg"
    Then the song has 1 track and 4 measures

  Scenario: a song with VST3 plugins and no audio track stays in format 2.0, and older readers see only its General MIDI program
    Given I have set the General MIDI program of track 1's channel to "Distortion Electric Guitar"
    And I have chosen the VST3 instrument "Test Sine" for track 1
    And I have added the VST3 effect "Test Gain" to track 1
    When I save the song as a TuxGuitar file
    Then the file's format version is 2.0
    And a reader of file format 2.0 finds track 1's channel on the General MIDI program "Distortion Electric Guitar" with no "synth.program" keys

  # Other file formats

  @planned
  Scenario Outline: file formats that cannot hold audio leave the audio tracks out
    Given I have entered fret 5 on string 1
    And I have added an audio track recording input channel 1
    And track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    When I export the song as a <format> file
    Then TuxGuitar reads the exported file back with 1 track

    Examples:
      | format |
      | MIDI   |
      | GP5    |

  @planned
  Scenario Outline: a track with a VST3 instrument exports with the General MIDI program it had
    Given I have entered fret 5 on string 1
    And I have set the General MIDI program of track 1's channel to "Distortion Electric Guitar"
    And I have chosen the VST3 instrument "Test Sine" for track 1
    When I export the song as a <format> file
    Then TuxGuitar reads the exported file back with track 1 on the General MIDI program "Distortion Electric Guitar"

    Examples:
      | format |
      | MIDI   |
      | GP5    |
