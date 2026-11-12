@planned
Feature: Exporting audio
  Exporting the song as audio renders every audible track through its sound chain into one file: General MIDI
  programs, VST3 instruments, and audio tracks' takes, with their VST3 effects. The file follows the song as written,
  repeats included, at 100% of its tempo, beat for beat from its first sample, whatever the plugins delay. It runs on
  until the VST3 effects' tails have died away.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And the MIDI Port is "TuxGuitar Synthesizer"
    And the audio device is "Test Interface"
    And the VST3 folders hold the test plugins
    And track 1 has a quarter note on every beat
    And I have chosen the VST3 instrument "Test Click" for track 1
    And I have added an audio track recording input channel 1
    And track 2 has a take of clicks of -0.25 on every beat from measure 1 beat 1 to the end of measure 4

  Scenario Outline: the exported file holds every track on its beats, in the format chosen
    When I export the song as a <type> file at <rate> Hz in <bits>
    Then the file holds 16 positive clicks and 16 negative clicks
    And the file's clicks of measure 2 beat 1 are at sample <second measure> within 1 sample
    And on every beat the click of track 1 and the click of track 2 in the file are within 1 sample of each other

    Examples:
      | type | rate  | bits          | second measure |
      | WAV  | 44100 | 16 bits       | 88200          |
      | WAV  | 48000 | 24 bits       | 96000          |
      | WAV  | 44100 | 32-bit float  | 88200          |
      | AIFF | 44100 | 16 bits       | 88200          |

  Scenario: plugin latency does not shift the exported file
    Given I have added the VST3 effect "Test Latency" set to 2048 samples to track 2
    And I have set "Latency" of the VST3 instrument of track 1 to 512
    When I export the song as a WAV file at 44100 Hz in 16 bits
    Then the file's clicks of measure 1 beat 1 are at sample 0 within 1 sample
    And on every beat the click of track 1 and the click of track 2 in the file are within 1 sample of each other

  Scenario: VST3 effects are in the exported file
    Given I have added the VST3 effect "Test Gain" to track 2
    When I export the song as a WAV file at 44100 Hz in 32-bit float
    Then the file's negative clicks are twice as high as without the VST3 effect, within 1%

  Scenario: the file runs on until the VST3 effects' tails have died away
    Given I have added the VST3 effect "Test Echo" to track 2
    When I export the song as a WAV file at 44100 Hz in 16 bits
    Then the file lasts 8.5 seconds within 512 samples
    And the file holds the echo of the click of measure 4 beat 4, 1 second after it, within 1 sample

  Scenario: without tails the file ends with the last measure
    When I export the song as a WAV file at 44100 Hz in 16 bits
    Then the file lasts 8 seconds within 512 samples

  Scenario: the exported file follows the repeats of the song
    Given measures 2 to 3 are repeated once
    When I export the song as a WAV file at 44100 Hz in 16 bits
    Then the file lasts 12 seconds within 512 samples
    And the file holds 24 negative clicks, each within 1 sample of a beat in song time

  Scenario: what only plays live is not in the exported file
    Given I have set the Percentage of Tempo to 80
    And I have turned Play looped on over measures 2 to 3
    And I have turned Count In on
    And I have turned the Metronome on
    And I have armed track 2
    And I have turned monitoring on for track 2
    And the player plays a 440 Hz tone peaking at 0.4 on input channel 1
    When I export the song as a WAV file at 44100 Hz in 16 bits
    Then the file lasts 8 seconds within 512 samples
    And the file holds 16 positive clicks and 16 negative clicks, and nothing else above -60 dBFS
    And the file's clicks of measure 2 beat 1 are at sample 88200 within 1 sample

  Scenario Outline: muted and soloed tracks are left out, as when the song plays
    Given I have <done>
    When I export the song as a WAV file at 44100 Hz in 16 bits
    Then the file holds <positive> positive clicks and <negative> negative clicks

    Examples:
      | done           | positive | negative |
      | muted track 1  | 0        | 16       |
      | muted track 2  | 16       | 0        |
      | soloed track 2 | 0        | 16       |
      | soloed track 1 | 16       | 0        |

  Scenario: exporting renders through the TuxGuitar Synthesizer whatever the MIDI Port
    Given the MIDI Port is "Test MIDI Out"
    When I export the song as a WAV file at 44100 Hz in 16 bits
    Then the file holds 16 positive clicks and 16 negative clicks
