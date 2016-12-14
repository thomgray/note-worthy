@Markdown
Feature: Parsing markdown

  Scenario: Parsing a md document with a single string
    Given a md document exists with a single string
    When the document is parsed
    Then the result contains 1 paragraph

  Scenario: Parsing a md document with a single string with a plain link
    Given a md document exists with a single string containing a plain link
    When the document is parsed
    Then the result contains 1 paragraph
    And the 1st paragraph contains 1 link

  Scenario: Parsing a md document with a single string with a plain link
    Given a md document exists with a single string containing a plain link
    When the document is parsed
    Then the result contains 1 paragraph
    And the 1st paragraph contains 1 link

  Scenario: plain links and referenced links
    Given a md document exists with a "google" ref link and a "github" link
    When the document is parsed
    And we take the links in the 1st paragraph
    Then there are 2 links
    And the 1st link is "google"
    And the 2nd link is "github"