Feature: Parsing with MDL Parser

  Scenario: Getting the content of a regular MDL file
    Given an mdl file exists with one base tag and nested tags
    When the file is parsed with an mdl parser
    Then A result is returned
    And the result contains 1 value
    And the 1st result is a content tag

  Scenario: Getting the nested content of a regular MDL file
    Given an mdl file exists with one base tag and nested tags
    When the file is parsed with an mdl parser
    And we parse the 1st item of the result
    And the file is parsed with an mdl parser
    Then the result contains 3 values
    And the 1st result is a content tag
    And the 2nd result is a content tag
    And the 3rd result is a content tag

  Scenario: Getting the content of a string with no tag markers
    Given a string exists without tag markings
    When the file is parsed with an mdl parser
    Then A result is returned
    And the result contains 1 value
    And the 1st result is a content string
    And the result string is equal to the source string

  Scenario: Initialising tags with the parent visible flag
    Given a string exists defining a parent visible tag
    When the file is parsed with an mdl parser
    And we take the 1st item of the result
    Then the result is a content tag
    And the result is parent visible



