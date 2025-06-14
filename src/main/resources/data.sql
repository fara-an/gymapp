-- Insert Training Types
INSERT INTO training_type (id, training_type_name) VALUES (1, 'Strength Training');
INSERT INTO training_type (id, training_type_name) VALUES (2, 'Cardio');
INSERT INTO training_type (id, training_type_name) VALUES (3, 'Yoga');

-- Insert User Profiles
INSERT INTO user_profile (id, first_name, last_name, user_name, password, is_active)
VALUES (1, 'John', 'Doe', 'jdoe', 'pass123', true),
       (2, 'Jane', 'Smith', 'jsmith', 'pass456', true),
       (3, 'Emily', 'Brown', 'ebrown', 'pass789', true),
       (4, 'Michael', 'White', 'mwhite', 'pass999', true);

-- Insert Trainers
INSERT INTO trainer (id, user_id, specialization)
VALUES (1, 1, 1),
       (2, 2, 2);

-- Insert Trainees
INSERT INTO trainee (id, user_id, date_of_birth, address)
VALUES (1, 3, '1998-04-20T00:00:00', '123 Main St'),
       (2, 4, '2000-10-10T00:00:00', '456 Side Ave');

-- Insert Trainer-Trainee Relationship (Many-to-Many)
INSERT INTO trainer_trainee (trainer_id, trainee_id)
VALUES (1, 1),
       (2, 1),
       (1, 2);

-- Insert Trainings
INSERT INTO training (id, trainer_id, trainee_id, training_name, training_type_id, training_date, training_duration)
VALUES (1, 1, 1, 'Leg Day', 1, '2025-06-14T08:00:00', 1.5),
       (2, 2, 1, 'Morning Cardio', 2, '2025-06-14T10:00:00', 1.0),
       (3, 1, 2, 'Yoga Basics', 3, '2025-06-14T12:00:00', 1.25);
