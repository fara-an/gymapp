Feature: Training Controller API

  Scenario: Add new training successfully
    When I add a training with trainee "trainee1", trainer "trainer1", name "Morning Session", type "Yoga", start "2025-09-30T09:00:00" and duration 60
    Then the response should contain the training with name "Morning Session", trainer "trainer1", trainee "trainee1" and type "Yoga"

  Scenario: Fail to add training when trainer specialization mismatch
    When I add a training with trainee "trainee1", trainer "trainer1", name "Morning Session", type "Pilates", start "2025-08-30T09:00:00" and duration 60
    Then the response should fail with status 400 and message "TrainingServiceImpl: Trainer specialization 'Yoga' does not match required 'Pilates'"

  Scenario: Fail to add training when trainee has conflict
    When I add a training with trainee "trainee1", trainer "trainer1", name "Morning Session", type "Yoga", start "2025-09-30T09:00:00" and duration 60 that conflicts trainee
    Then  the response should fail with status 409 and message "Problem with user input"

  Scenario: Delete training successfully
    When I delete training with trainer "trainer1", trainee "trainee1", start "2025-09-30T09:00:00"
    Then the training should be deleted successfully

  Scenario: Fail to delete non-existent training
    When I delete non-existent training with trainer "trainer1", trainee "trainee1", start "2025-09-30T09:00:00"
    Then the response should fail with status 400 and message "Training not found"

  Scenario: Find existing training successfully
    When I request training with trainer "trainer1", trainee "trainee1", start "2025-09-30T09:00:00"
    Then the response should contain 200 status code

  Scenario: Fail to find training that does not exist
    When I request a training that does not exist with trainer "trainer1", trainee "trainee1", start "2025-09-30T09:00:00"
    Then the response should fail with status 404 and message "Error occurred during database interaction"
