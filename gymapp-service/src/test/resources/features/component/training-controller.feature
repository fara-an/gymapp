@TrainingController
Feature: TrainingController API tests

  Scenario: Successfully add a training
    Given a trainer "John.Doe" exists with specialization "Strength Training"
    And a trainee "Emily.Brown" exists
    When I send a POST request to "/trainings" with trainingDto:
    Then TrainingController the response status should be 200
    And TrainingController the response should contain "Strength Training"

  Scenario: Fail to add training due to wrong specialization
    Given a trainer "John.Doe" exists but with different specialization other than "Pilates"
    And a trainee "Emily.Brown" exists
    When I send a POST request to "/trainings" with non-existent trainingDto:
    Then TrainingController the response status should be 409
    And TrainingController the response should contain "Problem with user input"

  Scenario: Find a training
    Given a training exists between trainer "John.Doe" and trainee "Emily.Brown" at "2025-06-14T08:00:00"
    When I send a GET request to "/trainings?trainerUsername=John.Doe&traineeUsername=Emily.Brown&startTime=2025-06-14T08:00:00"
    Then TrainingController the response status should be 200

  Scenario: Successfully delete a training
    Given a training exists between trainer "John.Doe" and trainee "Emily.Brown" at "2025-06-14T08:00:00"
    When I send a DELETE request to "/trainings?trainerUsername=John.Doe&traineeUsername=Emily.Brown&startTime=2025-06-14T08:00:00"
    Then TrainingController the response status should be 200


  Scenario: Fail to add training due to trainee schedule conflict
    And a trainer "John.Doe" exists with specialization "Strength Training"
    When I send a POST request to "/trainings" with trainingDto:
    Then TrainingController the response status should be 409
    And TrainingController the response should contain "Problem with user input"
