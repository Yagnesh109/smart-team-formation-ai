CREATE DATABASE IF NOT EXISTS smart_team_ai CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_team_ai;

CREATE TABLE IF NOT EXISTS students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    department VARCHAR(100) NOT NULL,
    study_year INT NOT NULL,
    experience_level VARCHAR(30) NOT NULL,
    availability VARCHAR(120) NOT NULL,
    interests VARCHAR(500),
    bio VARCHAR(1000),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS skills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(80) NOT NULL UNIQUE,
    category VARCHAR(80)
);

CREATE TABLE IF NOT EXISTS student_skills (
    student_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    skill_level INT NOT NULL DEFAULT 3,
    PRIMARY KEY(student_id, skill_id),
    CONSTRAINT fk_ss_student FOREIGN KEY(student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_ss_skill FOREIGN KEY(skill_id) REFERENCES skills(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    owner_id BIGINT NOT NULL,
    team_size INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    required_skills VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_project_owner FOREIGN KEY(owner_id) REFERENCES students(id)
);

CREATE TABLE IF NOT EXISTS team_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    role_name VARCHAR(80) NOT NULL,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(project_id, student_id),
    CONSTRAINT fk_tm_project FOREIGN KEY(project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_tm_student FOREIGN KEY(student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS project_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    requested_by BIGINT NOT NULL,
    request_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    message VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pr_project FOREIGN KEY(project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_pr_student FOREIGN KEY(student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_pr_sender FOREIGN KEY(requested_by) REFERENCES students(id)
);

CREATE TABLE IF NOT EXISTS ratings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    from_student BIGINT NOT NULL,
    to_student BIGINT NOT NULL,
    technical_score INT NOT NULL,
    teamwork_score INT NOT NULL,
    reliability_score INT NOT NULL,
    review VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rating_project FOREIGN KEY(project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_rating_from FOREIGN KEY(from_student) REFERENCES students(id),
    CONSTRAINT fk_rating_to FOREIGN KEY(to_student) REFERENCES students(id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    title VARCHAR(160) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_student FOREIGN KEY(student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reporter_id BIGINT NOT NULL,
    target_type VARCHAR(30) NOT NULL,
    target_id BIGINT NOT NULL,
    reason VARCHAR(120) NOT NULL,
    details VARCHAR(1000),
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reporter FOREIGN KEY(reporter_id) REFERENCES students(id)
);

INSERT IGNORE INTO skills(name, category) VALUES
('Java','Programming'),('Python','Programming'),('C++','Programming'),('JavaScript','Programming'),
('React','Frontend'),('HTML/CSS','Frontend'),('UI/UX','Design'),('MySQL','Database'),
('Spring Boot','Backend'),('Hibernate','Backend'),('Machine Learning','AI/ML'),('Data Science','AI/ML'),
('Android','Mobile'),('Testing','Quality'),('Git','Tools'),('Communication','Soft Skill');
