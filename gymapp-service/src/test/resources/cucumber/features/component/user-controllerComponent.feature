Feature: User Controller API

  Scenario: Successful login
    When I log in with username "john" and password "password123"
    Then UserController response status should be 200
    And UserController response should contain a JWT token

  Scenario: Login with invalid credentials
    When I log in with username "john" and password "wrongpass"
    Then UserController response status should be 400
    And UserController response should contain message "Bad credentials"

  Scenario: Toggle active status
    When I toggle active status for user "john"
    Then UserController response status should be 200

  Scenario: Toggle active status for non-existent user
    When I toggle active status for user "ghost"
    Then UserController response status should be 404
    And UserController response should contain message "Error occurred during database interaction"

  Scenario: Change password
    When I change the password for user "john" from "oldpass" to "newpass"
    Then UserController response status should be 200
    And UserController response should contain message "Changed password successfully"

  Scenario: Change password with wrong old password
    When I change the password for user "john" from "wrongOld" to "newpass"
    Then UserController response status should be 400
    And UserController response should contain message "User provided illegal argument"

  Scenario: Logout
    When I log out with token "mocked-jwt-token"
    Then UserController response status should be 200
    And UserController response should contain message "Logged out successfully"

  Scenario: Logout with invalid token
    When I log out with token "invalid-jwt-token"
    Then UserController response status should be 400
    And UserController response should contain message "User provided illegal argument"
