Feature: Getting and handling of content from directories list

  Scenario: Loading content from a directory list
    Given a resource exists specifying a directory list of foo notes
    And a content handler is initialised with a resourceIO pointing to that list
    When the content handler is called to get all content
    Then all the content in those directories is loaded

  Scenario: getting tags with a search query
    Given a resource exists specifying a directory list of notes1
    And a content handler is initialised with a resourceIO pointing to that list
    When we search for a tag matching the search string "git config link"
    Then a single search result is returned
    And the 1st search result is the git config link tag



