@planned
Feature: Saving audio tracks and plugins
  A song saved as a TuxGuitar file keeps its audio tracks, their takes sample for sample, and every track's sound
  chain with each plugin's settings. Which audio input I record from is a TuxGuitar setting, not part of the song.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And the VST3 folders hold these plugins:
      | name      | kind       | does                                                      |
      | Test Gain | effect     | multiplies the signal by its Gain parameter, 2 by default |
      | Test Sine | instrument | plays a sine at the pitch of each note                    |

  Scenario: takes come back sample for sample, where they were, in the same order
    Given I have added an audio track
    And track 2 has a take of a 440 Hz tone over measures 1 to 2
    And track 2 has a take of a click on every beat over measures 2 to 3
    When I save the song as a TuxGuitar file and open it again
    Then track 2 is an audio track with 2 takes
    And track 2's takes read "take 2: measure 2 beat 1 to measure 4 beat 1, take 1: measure 1 beat 1 to measure 3 beat 1"
    And every take of track 2 is sample for sample what was saved

  Scenario: the sound chain comes back with each plugin's settings
    Given I have added an audio track
    And I have chosen the VST3 instrument "Test Sine" for track 1
    And I have added the VST3 effect "Test Gain" to track 2
    And the plugin "Test Gain" of track 2 has set its parameter "Gain" to 3.0
    And I have added the VST3 effect "Test Gain" to track 2
    And I have disabled the second effect "Test Gain" of track 2
    When I save the song as a TuxGuitar file and open it again
    Then track 1's sound chain reads "Test Sine"
    And track 2's sound chain reads "takes > Test Gain > [Test Gain]"
    And the parameter "Gain" of the first effect "Test Gain" of track 2 is 3.0

  Scenario: a song without audio tracks or plugins saves as it did before
    When I save the song as a TuxGuitar file
    Then the file is what this version of TuxGuitar saved before audio tracks existed

  # OPEN DECISION: what an older TuxGuitar does with a file that holds takes. Today's reader refuses a .tg file
  # whose archive holds anything besides version.txt and content.xml.
  Scenario: an older TuxGuitar refuses a song with takes instead of losing them silently
    Given I have added an audio track
    And track 2 has a take of a 440 Hz tone over measures 1 to 2
    When I save the song as a TuxGuitar file
    Then TuxGuitar 2.0 refuses to open the file

  Scenario: the audio input is not saved with the song
    Given the audio input "Test Interface" with 2 input channels
    And I have chosen input channel 1 of "Test Interface" as the audio input
    When I save the song as a TuxGuitar file
    And I choose input channel 2 of "Test Interface" as the audio input
    And I open the file again
    Then the audio input is input channel 2 of "Test Interface"

  Scenario Outline: formats that cannot hold audio leave the audio tracks out
    Given I have added an audio track
    And track 2 has a take of a 440 Hz tone over measures 1 to 2
    When I export the song as a <format> file
    Then the exported song has 1 track

    Examples:
      | format   |
      | MIDI     |
      | .gp5     |
      | MusicXML |
