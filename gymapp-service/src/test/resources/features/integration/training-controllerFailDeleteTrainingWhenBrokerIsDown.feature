@TrainingController
Feature: Training management with JMS workload integration

  Background:
    Given the system has a trainer "John.Doe" with specialization "Strength Training"
    And the system has a trainee "Emily.Brown"


  Scenario: Fallback triggered when broker is stopped on delete
    Given I have an existing training with name "FailDelete Yoga" type "Strength Training" duration 30
    And the message broker is stopped
    When I delete this training
    Then the response status should be 503
    And no message should be delivered to the queue
