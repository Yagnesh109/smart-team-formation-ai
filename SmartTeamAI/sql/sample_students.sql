USE smart_team_ai;

-- Insert sample students (BCrypt password: password123 -> $2a$10$eQQmgQqEgPhfeRKyirJGte3IgANLokS6OpO8FxnJAqBy5oQBdkS0m)
INSERT INTO students (name, email, password_hash, department, study_year, experience_level, availability, interests, bio, active) VALUES
('Aarav Sharma', 'aarav.sharma@smartteam.edu', '$2a$10$eQQmgQqEgPhfeRKyirJGte3IgANLokS6OpO8FxnJAqBy5oQBdkS0m', 'CSE', 3, 'Advanced', 'Weekends, Monday', 'AI/ML, Python, PyTorch, Deep Learning', 'Machine learning specialist experienced in PyTorch, ScikitLearn, computer vision, and neural network optimization.', TRUE),
('Priya Patel', 'priya.patel@smartteam.edu', '$2a$10$eQQmgQqEgPhfeRKyirJGte3IgANLokS6OpO8FxnJAqBy5oQBdkS0m', 'CSE', 4, 'Advanced', 'Monday, Evenings', 'AI/ML, Data Science, Python, NLP', 'Hands-on experience in NLP, Python, PyTorch, ScikitLearn, transformers, and data processing pipelines.', TRUE),
('Rohan Gupta', 'rohan.gupta@smartteam.edu', '$2a$10$eQQmgQqEgPhfeRKyirJGte3IgANLokS6OpO8FxnJAqBy5oQBdkS0m', 'IT', 3, 'Intermediate', 'Weekends', 'Full Stack, Java, Spring Boot, MySQL', 'Backend engineer proficient in Java, Spring Boot, Hibernate, REST APIs, and database modeling.', TRUE),
('Ananya Desai', 'ananya.desai@smartteam.edu', '$2a$10$eQQmgQqEgPhfeRKyirJGte3IgANLokS6OpO8FxnJAqBy5oQBdkS0m', 'CSE', 2, 'Intermediate', 'Monday, Weekends', 'UI/UX, Frontend, React, Figma', 'Passionate about frontend UI engineering, React.js, Tailwind CSS, Figma prototyping, and design systems.', TRUE),
('Vikram Singh', 'vikram.singh@smartteam.edu', '$2a$10$eQQmgQqEgPhfeRKyirJGte3IgANLokS6OpO8FxnJAqBy5oQBdkS0m', 'AI & DS', 3, 'Intermediate', 'Monday, Wednesday', 'AI/ML, Python, Machine Learning', 'Data science student skilled in ScikitLearn, Pandas, NumPy, statistical modeling, and data visualization.', TRUE),
('Sneha Reddy', 'sneha.reddy@smartteam.edu', '$2a$10$eQQmgQqEgPhfeRKyirJGte3IgANLokS6OpO8FxnJAqBy5oQBdkS0m', 'CSE', 4, 'Advanced', 'Weekends', 'Cloud, DevOps, Docker, Python', 'Cloud practitioner with experience in AWS, Docker, Kubernetes, CI/CD pipelines, and Python microservices.', TRUE),
('Kabir Mehta', 'kabir.mehta@smartteam.edu', '$2a$10$eQQmgQqEgPhfeRKyirJGte3IgANLokS6OpO8FxnJAqBy5oQBdkS0m', 'IT', 2, 'Beginner', 'Monday', 'Java, Python, Web Development', 'Enthusiastic second-year student exploring Python scripting, web frameworks, and collaborative software projects.', TRUE),
('Divya Nair', 'divya.nair@smartteam.edu', '$2a$10$eQQmgQqEgPhfeRKyirJGte3IgANLokS6OpO8FxnJAqBy5oQBdkS0m', 'CSE', 3, 'Intermediate', 'Weekends, Evenings', 'AI/ML, PyTorch, Computer Vision', 'Focused on computer vision research, PyTorch convolutional models, OpenCV, and multimodal learning.', TRUE),
('Aditya Verma', 'aditya.verma@smartteam.edu', '$2a$10$eQQmgQqEgPhfeRKyirJGte3IgANLokS6OpO8FxnJAqBy5oQBdkS0m', 'ECE', 3, 'Intermediate', 'Monday', 'Embedded Systems, IoT, Python', 'Hardware-software co-design enthusiast with Raspberry Pi, ESP32, and Python telemetry experience.', TRUE),
('Meera Joshi', 'meera.joshi@smartteam.edu', '$2a$10$eQQmgQqEgPhfeRKyirJGte3IgANLokS6OpO8FxnJAqBy5oQBdkS0m', 'CSE', 4, 'Advanced', 'Full Time, Monday', 'AI/ML, PyTorch, ScikitLearn, Data Science', 'Senior researcher working on PyTorch neural architectures, ScikitLearn baseline benchmarking, and data science.', TRUE)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Insert sample peer ratings to demonstrate collaboration & reliability metrics
INSERT INTO ratings (project_id, from_student, to_student, technical_score, teamwork_score, reliability_score, review)
SELECT 1, 1, s.id, 5, 5, 5, 'Outstanding technical knowledge and great team player.'
FROM students s WHERE s.email = 'aarav.sharma@smartteam.edu'
ON DUPLICATE KEY UPDATE technical_score=5;

INSERT INTO ratings (project_id, from_student, to_student, technical_score, teamwork_score, reliability_score, review)
SELECT 1, 1, s.id, 5, 4, 5, 'Very reliable contributor with deep ML expertise.'
FROM students s WHERE s.email = 'priya.patel@smartteam.edu'
ON DUPLICATE KEY UPDATE technical_score=5;

INSERT INTO ratings (project_id, from_student, to_student, technical_score, teamwork_score, reliability_score, review)
SELECT 1, 1, s.id, 4, 5, 4, 'Great collaboration and communicative.'
FROM students s WHERE s.email = 'vikram.singh@smartteam.edu'
ON DUPLICATE KEY UPDATE technical_score=4;
