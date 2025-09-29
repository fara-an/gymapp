@UserController
Feature: UserController API component tests

  Scenario: Successful login with valid credentials
    Given a user with username "Jane.Smith" and password "pass456"
    When I send a POST request to "/users/login"
    Then the response status should be 200
    And the response should contain "Login successful"
    And the response should contain a JWT token

  Scenario: Failed login with invalid credentials
    Given a user with username "john" and password "wrongPass"
    When I send a POST request to "/users/login"
    Then the response status should be 400


  Scenario: Toggle active status of an existing user
    When I send a PATCH request to "/users/Jane.Smith"
    Then the response status should be 200

  Scenario: Toggle active status of a non-existing user
    When I send a PATCH request to "/users/ghost"
    Then the response status should be 404


  @changePass
  Scenario: Change password successfully
    When I send a PUT request to "/users/password" with old password "pass456" and new password "pass000" of username "Jane.Smith"
    Then the response status should be 200
    And the response should contain "Changed password successfully"

  Scenario: Change password fails due to wrong old password
    When I send a PUT request to "/users/password" with old password "badOld" and new password "newSecret" of username "Jane.Smith"
    Then the response status should be 401
    And the response should contain "Invalid credentials provided"

  Scenario: Successful logout with valid token
    When I send a GET request to "/users/logout" with the token
    Then the response status should be 200
    And the response should contain "Logged out successfully"

  Scenario: Logout fails with invalid token
    Given an invalid JWT token "invalidToken"
    When I send a GET request to "/users/logout" with the token
    Then the response status should be 403
