Feature: Trainer Controller API

  Scenario: Successfully register a trainer
    When I register a trainer with firstName "John", lastName "Doe", trainingType "Cardio"
    Then Trainer Controller: the response status should be 200
    And Trainer Controller: the response should contain username "john_doe" and password "encodedPass"

  Scenario: Fail to register trainer with missing firstName
    When I register a trainer with firstName "", lastName "Doe", trainingType "Cardio"
    Then Trainer Controller: the response status should be 400
    And Trainer Controller: the response error should contain "firstName"

  Scenario: Fail to register trainer when trainingType not found
    When I register a trainer with firstName "John", lastName "Doe", non-existent trainingType "UnknownType"
    Then Trainer Controller: the response status should be 404
    And Trainer Controller: the response error should contain "Error occurred during database interaction"

  Scenario: Get trainer by username
    When I request trainer with username "john_doe"
    Then Trainer Controller: the response status should be 200
    And Trainer Controller: the response should contain trainer firstName "John" and lastName "Doe"

  Scenario: Fail to get trainer with non-existing username
    When I request non-existent trainer with username "not_found"
    Then Trainer Controller: the response status should be 404
    And Trainer Controller: the response error should contain "Error occurred during database interaction"

  Scenario: Update trainer profile
    When I update trainer with id 1 with firstName "Jane", lastName "Doe", username "jane_doe"
    Then Trainer Controller: the response status should be 200
    And Trainer Controller: the response should contain trainer firstName "Jane" and lastName "Doe"

  Scenario: Fail to update trainer with invalid username
    When I update trainer with id 1 with firstName "Jane", lastName "Doe", username ""
    Then Trainer Controller: the response status should be 400
    And Trainer Controller: the response error should contain "userName"
