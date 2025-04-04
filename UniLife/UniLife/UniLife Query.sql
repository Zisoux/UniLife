show databases;
create database unilife;
use unilife;
show tables;
SHOW CREATE DATABASE unilife;
SET NAMES 'utf8mb4';
SHOW VARIABLES LIKE 'character_set%';
ALTER DATABASE `unilife` CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
SET character_set_client = 'utf8mb4';

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE timetables (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    semester VARCHAR(20) NOT NULL,  -- 예: '2025년 1학기'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,  -- 강의명
    professor VARCHAR(100),  -- 교수명
    credits INT NOT NULL,  -- 학점
    is_major BOOLEAN NOT NULL DEFAULT FALSE,  -- 전공 여부
    location VARCHAR(100),  -- 강의실
    day ENUM('월', '화', '수', '목', '금', '토', '일') NOT NULL,  -- 강의 요일
    start_time TIME NOT NULL,  -- 강의 시작 시간
    end_time TIME NOT NULL  -- 강의 종료 시간
);

CREATE TABLE timetable_courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    timetable_id INT NOT NULL,
    course_id INT NOT NULL,
    day_of_week ENUM('월', '화', '수', '목', '금', '토', '일') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (timetable_id) REFERENCES timetables(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

CREATE TABLE friends (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    friend_id INT NOT NULL,
    status ENUM('pending', 'accepted', 'rejected') DEFAULT 'pending',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE semesters (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    year INT NOT NULL,  -- 예: 2025
    term ENUM('1학기', '2학기') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE enrolled_courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    semester_id INT NOT NULL,
    course_id INT NOT NULL,
    grade DECIMAL(3,2) CHECK (grade BETWEEN 0 AND 4.5),  -- 예: 3.5, 4.0
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

CREATE TABLE schedule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    enrolled_course_id INT NOT NULL,  -- 수강한 강의 ID (연동됨)
    day ENUM('월', '화', '수', '목', '금', '토', '일') NOT NULL,  -- 요일
    start_time TIME NOT NULL,  -- 강의 시작 시간
    end_time TIME NOT NULL,  -- 강의 종료 시간
    location VARCHAR(100),  -- 강의실 정보
    FOREIGN KEY (enrolled_course_id) REFERENCES enrolled_courses(id) ON DELETE CASCADE
);

CREATE TABLE gpa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    semester_id INT NOT NULL,
    total_gpa DECIMAL(3,2),  -- 전체 평점
    major_gpa DECIMAL(3,2),  -- 전공 평점
    elective_gpa DECIMAL(3,2),  -- 교양 평점
    total_credits INT,  -- 전체 취득 학점
    major_credits INT,  -- 전공 취득 학점
    elective_credits INT,  -- 교양 취득 학점
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (semester_id) REFERENCES semesters(id) ON DELETE CASCADE
);

CREATE TABLE portfolios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,  -- 사용자 ID
    title VARCHAR(100) NOT NULL,  -- 포트폴리오 제목
    description TEXT,  -- 설명
    file_path VARCHAR(255),  -- 업로드된 파일 경로 (첨부파일 저장)
    start_date DATE NOT NULL,  -- 시작 날짜
    end_date DATE NOT NULL,  -- 종료 날짜
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE events (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,  -- 사용자 ID
    title VARCHAR(100) NOT NULL,  -- 일정 제목 (강의명, 포트폴리오명 등)
    start DATETIME NOT NULL,  -- 시작 시간
    end DATETIME NOT NULL,  -- 종료 시간
    type ENUM('class', 'portfolio', 'schedule') NOT NULL,  -- 일정 유형
    reference_id INT,  -- 참조 테이블의 ID (강의 ID 또는 포트폴리오 ID)
    location VARCHAR(100),  -- 장소 (강의실, 기타 일정 장소)
    color VARCHAR(20),  -- FullCalendar에서 표시할 색상
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
