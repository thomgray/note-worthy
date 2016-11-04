Feature: Loading content from strings

  Scenario: Loading a content tag from an mdl string
    Given an mdl string exists with one base tag and nested tags to be loaded
    When the string is loaded with an mdl content loader
    Then the content loader result contains 1 value
    And the 1st content loader result is a content tag
    When we take the contents of the content tag
    Then the content loader result contains 3 values
    And the 1st content loader result is a content tag
    And the 2nd content loader result is a content tag
    And the 3rd content loader result is a content tag

  Scenario: Loading content from a file path
    Given a mdl file exists with 3 content tags
    When the file is loaded with an mdl loader
    Then the content loader loads a result
    And the content loader result contains 3 values
    And the 1st content loader result is a content tag
    And the 2nd content loader result is a content tag
    And the 3rd content loader result is a content tag

  Scenario: Loading content from a directory
    Given a directory exists containing mdl notes
    When content is loaded from the directory with an mdl loader
    Then the content loader loads a result
    And the content loader result contains 3 values
    And the 1st content loader result is a content tag
    And the 2nd content loader result is a content tag
    And the 3rd content loader result is a content tag
