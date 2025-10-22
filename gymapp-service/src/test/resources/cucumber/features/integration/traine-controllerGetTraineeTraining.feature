
Feature:Get Trainee trainings API


Scenario: Get trainings for Emily.Brown
When I get trainings for trainee "Emily.Brown"
Then the response should include training "Leg Day"

