@Autocomplete

  Feature: Autocompletions

    Scenario: Autocomplete options for a single unfinished root tag
      Given a search engine exists that points to a resource directory
      And an autocompleter exists with that search engine

      When I autocomplete "top"
      Then I receive a list with 1 item
      Then I get receive a list containing "topic manager"

    Scenario: Autocomplete with a base tag
      Given a search engine exists that points to a resource directory
      And an autocompleter exists with that search engine

      When I autocomplete "topic manager archi"
      Then I receive a list with 1 item
      Then I get receive a list containing "architecture"