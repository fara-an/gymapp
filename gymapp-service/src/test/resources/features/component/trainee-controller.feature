@TraineeController
Feature: Trainee Controller API

  Scenario: Register new trainee
    When I register a trainee with firstName "Santa", lastName "Clause", dateOfBirth "1998-04-20T00:00:00" and address "North Pole"
    Then Trainee Controller: the response status should be 200
    And Trainee Controller: the response should contain username and password

  Scenario: Get trainee Emily.Brown with her trainers
    When I get trainee by username "Emily.Brown"
    Then I should receive trainee details with name "Emily"
    And the response should include trainer "John.Doe"

  Scenario: Get trainee Michael.White with his trainers
    When I get trainee by username "Michael.White"
    Then I should receive trainee details with name "Michael"
    And the response should include trainers "Jane.Smith" and "John.Doe"

  Scenario: Get trainee that does not exist
    When I get trainee by username "Non.Existent"
    Then Trainee Controller: the response status should be 404
    And the error message should contain "Error occurred during database interaction"


  Scenario: Get trainings for Emily.Brown
    When I get trainings for trainee "Emily.Brown"
    Then the response should include training "Leg Day"

  Scenario: Get trainings for Michael.White
    When I get trainings for trainee "Michael.White"
    Then the response should include training "Morning Cardio"
    And the response should include training "Yoga Basics"

  Scenario: Delete trainee Michael.White
    When I delete trainee with username "Michael.White"
    Then the trainee should be deleted successfully

  Scenario: Delete trainee that does not exist
    When I delete trainee with nonexistent username "Ghost.User"
    Then Trainee Controller: the response status should be 404
    And the error message should contain "Error occurred during database interaction"


  Scenario: Assign new trainer to Emily.Brown
    When I assign trainer "Clementine.Krujencki" to trainee "Emily.Brown"
    Then the response should include trainer "Clementine"

  Scenario: Assign trainer to a trainee that does not exist
    When I assign trainer "John.Doe" to trainee "Ghost.User"
    Then Trainee Controller: the response status should be 404
    And the error message should contain "Error occurred during database interaction"
