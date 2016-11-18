@AttributedString
Feature: Attributed Strings

  Scenario: Concatenating attributed string
    Given an attributed string exists with a single, homogenous attribute
    When the string is concatenated with another homogenous attributed string
    Then the length of the result string is equal to the sum of the lenghts of the other strings
    And the string has two attributes spanning the length of the string

  Scenario: Getting a substring within a range
    Given a long attributed string exists with several attributes
    When a substring is extracted from the string
    Then an attributed string is returned
    And the result string matches the substring of the original
    And the attributes match the attributes of the original

  Scenario: Getting a substring with a regex
    Given a long attributed string exists with several attributes
    And a regex is specified that mas a match within the string
    When the string is regexed for a match
    Then an attributed string is returned
    And the result matches the regex
    And The attributes in the result string are preserved from the original

  Scenario: Adding an attribute that overlaps a similar attribute
    Given a RED attributed string exists of length 12
    When an attribute RED is added to the string in range 3 - 6
    Then the result string has 1 attributes
    And the 1st attribute of the result is in range 0 - 12

  Scenario: Adding an attribute that overlaps a preexisting attribute
    Given a RED attributed string exists of length 12
    When an attribute BLUE is added to the string in range 3 - 6
    Then the result string has 3 attributes
    And the 1st attribute of the result is in range 0 - 3
    And the 1st attribute of the result is "RED"
    And the 2nd attribute of the result is in range 3 - 6
    And the 2nd attribute of the result is "BLUE"
    And the 3rd attribute of the result is in range 6 - 12
    And the 3rd attribute of the result is "RED"

  Scenario: Adding an attribute that overlaps several attributes
    Given a RED attributed string exists with text "this is an attributed string with several attributes"
    And the string has a BLUE_B attribute in range 5 - 21
    When an attribute YELLOW is added to the string in range 11 - 30
    Then the 1st attribute of the result is in range 0 - 5
    And the 1st attribute of the result is "RED"
    And the 2nd attribute of the result is in range 5 - 11
    And the 2nd attribute of the result is "RED BLUE_B"
    And the 3rd attribute of the result is in range 11 - 21
    And the 3rd attribute of the result is "YELLOW BLUE_B"
    And the 4th attribute of the result is in range 21 - 30
    And the 4th attribute of the result is "YELLOW"
    And the 5th attribute of the result is in range 30 - 52
    And the 5th attribute of the result is "RED"
    And the result string has 5 attributes

  Scenario: adding attributes with a regex
    Given a CYAN attributed string exists with text "this is a regular bit of text"
    When RED attributes are added to the string with a regex "regular"
    Then the result string has 3 attributes
    And the result string attributes do not overlap
    And the 1st attribute of the result is in range 0 - 10
    And the 2nd attribute of the result is in range 10 - 17
    And the 3rd attribute of the result is in range 17 - 29

  Scenario: adding attributed to regex groups
    Given a CYAN attributed string exists with text "this is a regular bit of text"
    And a regex is specified matching 2 groups in the string
    When different attributes are added to the regex groups
    Then the result string has 4 attributes
    And the result string attributes do not overlap
    And the 1st attribute of the result is in range 0 - 2
    And the 2nd attribute of the result is in range 2 - 4
    And the 3nd attribute of the result is in range 4 - 25
    And the 4th attribute of the result is in range 25 - 29
