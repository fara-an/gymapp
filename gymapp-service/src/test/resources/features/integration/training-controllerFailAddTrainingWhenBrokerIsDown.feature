@TrainingController
Feature: Training management with JMS workload integration

  Background:
    Given the system has a trainer "John.Doe" with specialization "Strength Training"
    And the system has a trainee "Emily.Brown"


  Scenario: Fallback triggered when broker is stopped on add
    Given the message broker is stopped
    When I add a training with name "Evening Yoga" type "Strength Training" duration 45
    Then the response status should be 503
    And no message should be delivered to the queue



