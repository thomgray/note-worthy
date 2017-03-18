Feature: Parsing with MDL Parser

  Scenario: Getting the content of a regular MDL file
    Given an mdl string exists with one base tag and nested tags
    When the file is parsed with an mdl parser
    Then a result is returned
    And the result contains 1 value
    And the 1st result is a content tag

  Scenario: Getting the content of a string with no tag markers
    Given an mdl string exists without tag markings
    When the file is parsed with an mdl parser
    Then a result is returned
    And the result contains 1 value
    And the 1st result is a content string
    And the result string is equal to the source string

  Scenario: Initialising tags with the parent visible flag
    Given an mdl string exists defining a parent visible tag
    When the file is parsed with an mdl parser
    And we take the 1st item of the result
    Then the result is a content tag
    And the result is parent visible

  Scenario: Initialising tags with the universal reference flag
    Given an mdl string exists defining a universal reference tag
    When the file is parsed with an mdl parser
    And we take the 1st item of the result
    Then the result is a content tag
    And the result is universally referenced

  Scenario: Initialising tags with the content invisible flag
    Given an mdl string exists defining a content invisible tag
    When the file is parsed with an mdl parser
    And we take the 1st item of the result
    Then the result is a content tag
    And the result is content invisible
    #and ...
    When a tag is initialised with that result
    Then the tag is content invisible




