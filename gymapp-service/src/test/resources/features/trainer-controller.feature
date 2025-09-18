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
