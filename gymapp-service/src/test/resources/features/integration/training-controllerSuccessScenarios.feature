@TrainingController
Feature: Training management with JMS workload integration

  Background:
    Given the system has a trainer "John.Doe" with specialization "Strength Training"
    And the system has a trainee "Emily.Brown"

  Scenario: Successfully add a training when broker is running
    When I add a training with name "Morning Yoga" type "Strength Training" duration 60
    Then the response status should be 200
    And a message should be sent to the queue

  Scenario: Successfully delete a training when broker is running
    Given I have an existing training with name "DeleteMe Yoga" type "Strength Training" duration 30
    When I delete this training
    Then the response status should be 200
    And a message should be sent to the queue


