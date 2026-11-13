Feature: Exporting audio
  Exporting the song as audio renders every audible track through its sound chain into one file: General MIDI
  programs, VST3 instruments, and audio tracks' takes, with their effects. The file follows the song as written,
  repeats included, at 100% of its tempo, beat for beat from its first sample, whatever the plugins delay. It runs on
  for the longest tail the effects report, at most 10 seconds.

  Background:
    Given a new song with one guitar track and 4 measures of 4/4 at 120 bpm
    And I have set the MIDI Port to "TuxGuitar Synthesizer"
    And I have set the audio device to "Test Interface"
    And the VST3 folders hold the test plugins
    And track 1 has a quarter note on every beat
    And I have chosen the VST3 instrument "Test Click" for track 1
    And I have set the Balance of track 1's channel to 0
    And I have added an audio track recording input channel 1
    And I have set the Balance of track 2's channel to 127
    And track 2 has a take of clicks of -0.25 on every beat from measure 1 beat 1 to the end of measure 4

  Scenario Outline: the exported file holds every track on its beats, in the format chosen
    When I export the song as a <type> file at <rate> Hz in <bits>
    Then the file's left side holds 16 clicks and its right side 16 clicks
    And the file's left and right clicks of measure 2 beat 1 are at sample <second measure> within 1 sample
    And on every beat the file's left and right clicks are within 1 sample of each other

    Examples:
      | type | rate  | bits         | second measure |
      | WAVE | 44100 | 16 bits      | 88200          |
      | WAVE | 44100 | 32-bit float | 88200          |
      | AIFF | 44100 | 16 bits      | 88200          |

    @planned
    Examples:
      | type | rate  | bits         | second measure |
      | WAVE | 48000 | 24 bits      | 96000          |

  Scenario: plugin latency does not shift the exported file
    Given I have added the VST3 effect "Test Latency" to track 2
    And I have set "Latency" of VST3 effect 1 of track 2 to 2048
    And I have set "Latency" of the VST3 instrument of track 1 to 512
    When I export the song as a WAVE file at 44100 Hz in 16 bits
    Then the file's left and right clicks of measure 1 beat 1 are at sample 0 within 1 sample
    And on every beat the file's left and right clicks are within 1 sample of each other
    And the file lasts 352800 samples, within 1 sample

  Scenario: VST3 effects are in the exported file
    Given I have added the VST3 effect "Test Gain" to track 2
    When I export the song as a WAVE file at 44100 Hz in 32-bit float
    Then the file's right clicks peak at 0.5 within 1%

  @planned
  Scenario: a take recorded at 44100 Hz keeps its pitch in a file exported at 48000 Hz
    Given track 2 has a take of a 440 Hz tone peaking at 0.4 from measure 1 beat 1 to measure 2 beat 1
    When I export the song as a WAVE file at 48000 Hz in 24 bits
    Then the file's right side sounds a 440 Hz tone within 1% in measure 1

  Scenario: without tails the file ends with the last measure
    When I export the song as a WAVE file at 44100 Hz in 16 bits
    Then the file lasts 352800 samples, within 1 sample

  Scenario: the file runs on for the longest tail the effects report
    Given I have added the VST3 effect "Test Echo" to track 2
    When I export the song as a WAVE file at 44100 Hz in 16 bits
    Then the file lasts 366030 samples, within 1 sample
    And the file holds the echo of the click of measure 4 beat 4, 13230 samples after it, within 1 sample

  Scenario Outline: an effect with an endless tail runs the file on for 10 seconds, unless it is not heard
    Given I have added the VST3 effect "Test Drone" to track 2
    And I have <done>
    When I export the song as a WAVE file at 44100 Hz in 16 bits
    Then the file lasts <length> samples, within 1 sample

    Examples:
      | done                                  | length |
      | kept VST3 effect 1 of track 2 enabled | 793800 |
      | disabled VST3 effect 1 of track 2     | 352800 |
      | muted track 2                         | 352800 |

  Scenario: the exported file follows the repeats of the song
    Given I have repeated measures 2 to 3 once
    When I export the song as a WAVE file at 44100 Hz in 16 bits
    Then the file lasts 529200 samples, within 1 sample
    And the file's right side holds 24 clicks, each within 1 sample of a beat in song time

  Scenario: what only plays live is not in the exported file
    Given I have set the Percentage of Tempo to 80
    And I have turned Play looped on over measures 2 to 3
    And I have turned Count In on
    And I have turned the Metronome on
    And I have armed track 2
    And I have set track 2's monitoring to Auto
    And the player plays a 440 Hz tone peaking at 0.4 on input channel 1
    When I export the song as a WAVE file at 44100 Hz in 16 bits
    Then the file lasts 352800 samples, within 1 sample
    And the file's left side holds 16 clicks and its right side 16 clicks, and nothing else above -60 dBFS
    And the file's left and right clicks of measure 2 beat 1 are at sample 88200 within 1 sample

  Scenario Outline: muted and soloed tracks are left out, as when the song plays
    Given I have <done>
    When I export the song as a WAVE file at 44100 Hz in 16 bits
    Then the file's left side holds <left> clicks and its right side <right> clicks

    Examples:
      | done           | left | right |
      | muted track 1  | 0    | 16    |
      | muted track 2  | 16   | 0     |
      | soloed track 2 | 0    | 16    |
      | soloed track 1 | 16   | 0     |

  Scenario: exporting renders through the TuxGuitar Synthesizer whatever the MIDI Port
    Given I have set the MIDI Port to "Test MIDI Out"
    When I export the song as a WAVE file at 44100 Hz in 16 bits
    Then the file's left side holds 16 clicks and its right side 16 clicks
