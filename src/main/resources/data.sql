-- Insert Training Types
INSERT INTO training_type (id, training_type_name) VALUES (1, 'Strength Training');
INSERT INTO training_type (id, training_type_name) VALUES (2, 'Cardio');
INSERT INTO training_type (id, training_type_name) VALUES (3, 'Yoga');

INSERT INTO userprofile (user_id, first_name, last_name, user_name, password, is_active) VALUES (1, 'John', 'Doe', 'John.Doe', 'pass123', true);
INSERT INTO userprofile (user_id, first_name, last_name, user_name, password, is_active) VALUES (2, 'Jane', 'Smith', 'Jane.Smith', '$2a$12$6gje1w1tueyUwcsOcCW5H.NPrAG6E9VM12KppiCTvRecvavckBiva', true);
INSERT INTO userprofile (user_id, first_name, last_name, user_name, password, is_active) VALUES (3, 'Emily', 'Brown', 'Emily.Brown', 'pass789', true);
INSERT INTO userprofile (user_id, first_name, last_name, user_name, password, is_active) VALUES (4, 'Michael', 'White', 'Michael.White', 'pass999', true);
INSERT INTO userprofile (user_id, first_name, last_name, user_name, password, is_active) VALUES (5, 'Clementine', 'Krujencki', 'Clementine.Krujencki', 'pass000',true);
-- Insert Trainers
INSERT INTO trainer (user_id, specialization) VALUES (1, 1);
INSERT INTO trainer (user_id, specialization) VALUES (2, 2);
INSERT INTO trainer (user_id, specialization) VALUES (5, 3);

-- Insert Trainees
INSERT INTO trainee (user_id, date_of_birth, address) VALUES (3, '1998-04-20T00:00:00', '123 Main St');
INSERT INTO trainee (user_id, date_of_birth, address) VALUES (4, '2000-10-10T00:00:00', '456 Side Ave');

-- Insert Trainer-Trainee Relationships
INSERT INTO trainer_trainee (trainer_id, trainee_id) VALUES (1, 3);
INSERT INTO trainer_trainee (trainer_id, trainee_id) VALUES (2, 4);
INSERT INTO trainer_trainee (trainer_id, trainee_id) VALUES (1, 4);

-- Insert Trainings
INSERT INTO training (id, trainer_id, trainee_id, training_name, training_type_id, training_date_start, training_duration, training_date_end) VALUES (1, 1, 3, 'Leg Day', 1, '2025-06-14T08:00:00', 60,'2025-06-14T09:00:00' );
INSERT INTO training (id, trainer_id, trainee_id, training_name, training_type_id, training_date_start, training_duration,training_date_end) VALUES ( 2, 2, 4, 'Morning Cardio', 2, '2025-06-14T10:00:00', 90,'2025-06-14T11:30:00');
INSERT INTO training (id, trainer_id, trainee_id, training_name, training_type_id, training_date_start, training_duration,training_date_end) VALUES ( 3, 1, 4, 'Yoga Basics', 3, '2025-06-14T12:00:00', 90,'2025-06-14T13:30:00');

ALTER TABLE training ALTER COLUMN id RESTART WITH 4;