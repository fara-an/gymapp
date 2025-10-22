Feature: Training management in GymApp

  Scenario: Add training successfully
    Given ActiveMQ service is running
    And GymApp service is up
    When I add a new training with trainer "Jane.Smith" and trainee "Emily.Brown"
    Then the training should be persisted in GymApp
    And the trainer workload should be updated in TrainerWorkloadService

  Scenario: Add training fails when ActiveMQ is down
    Given ActiveMQ service is stopped
    And GymApp service is down
    When I add a new training with trainer "Jane.Smith" and trainee "Emily.Brown"
    Then GymApp should return a service unavailable error
    And the training should not be persisted in GymApp
    And the trainer workload should not be updated in TrainerWorkloadService
