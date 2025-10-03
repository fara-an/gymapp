Feature: Get Trainer Trainings

  Scenario: Get trainer trainings
When I get trainings for trainer "John.Doe"
Then Trainer Controller: the response status should be 200
And Trainer Controller: the response should contain training "Leg Day"
And Trainer Controller: the response should contain training "Yoga Basics"
