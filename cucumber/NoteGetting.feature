Feature: Retrieving notes

  Scenario: I should be able to ask for the index for a given label
    When I type a label followed by -i
    Then I see the index for the given label
