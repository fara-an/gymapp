
Feature:Assign  Trainer To Trainee

Scenario: Assign new trainer to Emily.Brown
When I assign trainer "Clementine.Krujencki" to trainee "Emily.Brown"
Then the response should include trainer "Clementine"
