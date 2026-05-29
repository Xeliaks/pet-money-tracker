Feature: Transaction Management
  As a user of the expense tracker
  I want to manage my financial transactions
  So that I can keep an accurate record of my personal expenses

  Background:
    Given the tracker is empty

  Scenario: Add a single expense transaction
    When I add a "Coffee" expense of 4.50 in "FOOD" on "2024-01-15"
    Then the tracker contains 1 transaction

  Scenario: List all transactions
    Given I have added a "Coffee" expense of 4.50 in "FOOD" on "2024-01-15"
    And I have added a "Bus ticket" expense of 2.00 in "TRANSPORT" on "2024-01-16"
    When I list all transactions
    Then the result contains 2 transactions

  Scenario: Search transactions by keyword
    Given I have added a "Coffee shop" expense of 4.50 in "FOOD" on "2024-01-15"
    And I have added a "Bus ticket" expense of 2.00 in "TRANSPORT" on "2024-01-16"
    When I search for "coffee"
    Then the result contains 1 transaction

  Scenario: Filter transactions by category
    Given I have added a "Coffee" expense of 4.50 in "FOOD" on "2024-01-15"
    And I have added a "Bus ticket" expense of 2.00 in "TRANSPORT" on "2024-01-16"
    And I have added a "Lunch" expense of 12.00 in "FOOD" on "2024-01-17"
    When I filter by category "FOOD"
    Then the result contains 2 transactions

  Scenario: Delete a transaction
    Given I have added a "Coffee" expense of 4.50 in "FOOD" on "2024-01-15"
    When I delete the transaction with title "Coffee"
    Then the tracker contains 0 transactions