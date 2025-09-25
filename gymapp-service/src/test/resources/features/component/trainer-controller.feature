@TrainerController
Feature: Trainer Controller API

  Scenario: Register a new trainer
    When I register a trainer with firstName "Alex", lastName "Johnson" and specialization "Strength Training"
    Then Trainer Controller: the response status should be 200
    And Trainer Controller: the response should contain username and password

  Scenario: Get trainer by username
    When I get trainer "John.Doe"
    Then Trainer Controller: the response status should be 200
    And Trainer Controller: the response should contain trainer firstName "John"

  Scenario: Update a trainer
    When I update trainer with id 1 to have firstName "Johnny", lastName "DoeUpdated", username "John.Doe", active "true"
    Then Trainer Controller: the response status should be 200
    And Trainer Controller: the response should contain trainer firstName "Johnny"

  Scenario: Get unassigned trainers for a trainee
    When I get unassigned trainers for trainee "Emily.Brown"
    Then Trainer Controller: the response status should be 200
    And Trainer Controller: the response should contain trainer "Clementine"

  Scenario: Get trainer trainings
    When I get trainings for trainer "John.Doe"
    Then Trainer Controller: the response status should be 200
    And Trainer Controller: the response should contain training "Leg Day"
    And Trainer Controller: the response should contain training "Yoga Basics"

  # Negative Test Scenarios
  Scenario: Register trainer with missing required fields
    When I register a trainer with firstName "", lastName "" and specialization ""
    Then Trainer Controller: the response status should be 400

  Scenario: Register trainer with invalid specialization
    When I register a trainer with firstName "Test", lastName "Trainer" and specialization "Invalid Specialization"
    Then Trainer Controller: the response status should be 404
    And Trainer Controller: the response should contain error message "Error occurred during database interaction"

  Scenario: Get non-existent trainer
    When I get trainer "NonExistent.Trainer"
    Then Trainer Controller: the response status should be 404
    And Trainer Controller: the response should contain error message "Error occurred during database interaction"

  Scenario: Update non-existent trainer
    When I update trainer with id 999 to have firstName "Nonexistent", lastName "Trainer", username "nonexistent.trainer", active "true"
    Then Trainer Controller: the response status should be 404
    And Trainer Controller: the response should contain error message "Error occurred during database interaction"

  Scenario: Get unassigned trainers for non-existent trainee
    When I get unassigned trainers for trainee "NonExistent.Trainee"
    Then Trainer Controller: the response status should be 200
    And Trainer Controller: the response should be an empty list


  Scenario: Update trainer with invalid data
    When I update trainer with id 1 to have firstName "", lastName "", username "", active "true"
    Then Trainer Controller: the response status should be 400
