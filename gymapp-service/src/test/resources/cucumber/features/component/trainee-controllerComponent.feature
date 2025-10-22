@TraineeController
Feature: Trainee Controller API

  Scenario: Register new trainee (positive)
    When I register a trainee with firstName "Santa", lastName "Clause", dateOfBirth "1998-04-20T00:00:00" and address "North Pole"
    Then the response status should be 200
    And the response should contain username and password

  Scenario: Register trainee with invalid body (validation error)
    When I register an invalid trainee body:
      """
      {
        "firstName": "",
        "lastName": ""
      }
      """
    Then the response status should be 400

  Scenario: Get trainee by username (positive)
    Given trainee "john.doe" exists
    When I request trainee by username "john.doe"
    Then the response status should be 200

  Scenario: Get trainee by username - not found (negative)
    Given trainee "ghost" does not exist
    When I request trainee by username "ghost"
    Then the response status should be 404

  Scenario: Delete trainee (positive)
    Given trainee "john.doe" exists for delete
    When I delete trainee by username "john.doe"
    Then the response status should be 200
    And the response should contain message "Deleted trainee with username  john.doe"

  Scenario: Delete trainee - not found (negative)
    Given trainee "ghost" does not exist for delete
    When I delete trainee by username "ghost"
    Then the response status should be 404

  Scenario: Update trainee (positive)
    Given trainee id 1 exists
    When I update trainee id 1 with payload:
      """
      {
        "userName":"john.doe",
        "firstName":"John",
        "lastName":"Doe",
        "birthday":"1998-04-20T00:00:00",
        "address":"Earth",
        "isActive": true
      }
      """
    Then the response status should be 200

  Scenario: Update trainee - id not found (negative)
    When I update trainee id 999 with payload:
      """
      {
        "userName":"ghost",
        "firstName":"Ghost",
        "lastName":"User",
        "birthday":"1998-04-20T00:00:00",
        "address":"Nowhere",
        "isActive": true
      }
      """
    Then the response status should be 404
    And the response should contain message "Error occurred"

  Scenario: Get trainings for trainee with filters (positive)
    Given trainee "john.doe" exists with trainings
    When I request trainings for trainee "john.doe"
    Then the response status should be 200
    And the response should contain trainings list

  Scenario: Assign trainers (positive)
    Given trainee "john.doe" exists with trainings and training id 100
    When I assign trainers to trainee "john.doe" with payload:
      """
      [{"trainerUsername":"t1","trainingId":100}]
      """
    Then the response status should be 200
    And the response should contain trainer "T"

  Scenario: Assign trainers - training not enrolled (negative)
    Given trainee "john.doe" exists but not enrolled in training 999
    When I assign trainers to trainee "john.doe" with payload:
      """
      [{"trainerUsername":"t1","trainingId":999}]
      """
    Then the response status should be 400
    And the response should contain message "User provided illegal argument"

