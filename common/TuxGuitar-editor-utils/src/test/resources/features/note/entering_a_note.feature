Feature: Entering a note
  A fret typed at the caret puts a note on the caret's string, in the caret's beat.
  The track's neck ends at its max fret; a fret past it is refused.

  Background:
    Given a new song with one guitar track

  Scenario: a fret on an empty beat becomes a note
    When I enter fret 5 on string 2
    Then the caret's beat reads "s2f5"
    And the action succeeds

  Scenario: a fret on another string of the same beat makes a chord
    Given I have entered fret 3 on string 5
    When I enter fret 2 on string 4
    Then the caret's beat reads "s4f2 s5f3"

  Scenario: a fret on the same string replaces the note there
    Given I have entered fret 3 on string 5
    When I enter fret 7 on string 5
    Then the caret's beat reads "s5f7"

  Scenario Outline: frets up to the max fret are accepted
    Given the track's max fret is <max fret>
    When I enter fret <fret> on string 1
    Then the caret's beat reads "s1f<fret>"
    And the action succeeds

    Examples:
      | max fret | fret |
      | 29       | 0    |
      | 29       | 29   |
      | 12       | 12   |

  Scenario Outline: a fret past the max fret is refused
    Given the track's max fret is <max fret>
    When I enter fret <fret> on string 1
    Then the caret's beat reads ""
    And the action is refused

    Examples:
      | max fret | fret |
      | 29       | 30   |
      | 12       | 13   |
