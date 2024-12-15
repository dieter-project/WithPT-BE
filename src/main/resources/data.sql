-- member 가입
INSERT INTO users
    (id, created_date, last_modified_date, auth_provider, birth, email, image_url, join_date, name, password, role, sex, user_type)
VALUES (1, NOW(), NOW(), 'EMAIL', '1994-07-19', 'member@test.com', 'https://withpt-s3.s3.ap-northeast-2.amazonaws.com/PROFILE/default_profile/MEMBER_MAN.png', NOW(), '최강록', 'member1234', 'MEMBER', 'MAN', 'MEMBER');

INSERT INTO member
    (diet_type, exercise_frequency, height, nickname, target_weight, weight, id)
VALUES ('Carb_Protein_Fat', 'EVERYDAY', 173.0, '최강록', 70.3, 75.3, 1);


-- 트레이너 가입
INSERT INTO users
    (id, created_date, last_modified_date, auth_provider, birth, email, image_url, join_date, name, password, role, sex, user_type)
VALUES (2, NOW(), NOW(), 'EMAIL', '1994-07-19', 'trainer@test.com', 'https://withpt-s3.s3.ap-northeast-2.amazonaws.com/PROFILE/default_profile/TRAINER_MAN.png', NOW(), '원세영', 'trainer1234', 'TRAINER', 'MAN', 'TRAINER');

INSERT INTO trainer
    (id)
VALUES (2); -- users 테이블의 마지막 삽입된 id 값 사용

INSERT INTO gym
    (id, created_date, last_modified_date, address, latitude, longitude, name)
VALUES (1, NOW(), NOW(), '경기도 김포시 사우동 231-413', 3.143151, 4.151661, '아자아자 피트니스 센터'),
       (2, NOW(), NOW(), '경기도 김포시 감정동 231-413', 3.143151, 4.151661, '으라차차 피트니스 센터');

INSERT INTO gym_trainer
    (id, created_date, last_modified_date, gym_id, hire_date, retirement_date, trainer_id)
VALUES (1, NOW(), NOW(), 1, '2022-01-01', NULL, 2),
       (2, NOW(), NOW(), 2, '2022-01-01', NULL, 2);

-- 아자아자 피트니스 센터 스케줄
INSERT INTO work_schedule
    (id, created_date, last_modified_date, gym_trainer_id, in_time, out_time, day)
VALUES (1, NOW(), NOW(), 1, '10:00', '18:00', 'MON'),
       (2, NOW(), NOW(), 1, '12:00', '22:00', 'TUE'),
       (3, NOW(), NOW(), 1, '10:00', '18:00', 'WED'),
       (4, NOW(), NOW(), 1, '10:00', '18:00', 'THU'),
       (5, NOW(), NOW(), 1, '12:00', '22:00', 'FRI'),
       (6, NOW(), NOW(), 1, '12:00', '17:00', 'SAT'),
       (7, NOW(), NOW(), 1, '12:00', '14:00', 'SUN');


-- 으라차차 피트니스 센터 스케줄
INSERT INTO work_schedule
    (id, created_date, last_modified_date, gym_trainer_id, in_time, out_time, day)
VALUES (8, NOW(), NOW(), 2, '19:00', '23:00', 'MON'),
       (9, NOW(), NOW(), 2, '19:00', '23:00', 'WED'),
       (10, NOW(), NOW(), 2, '19:00', '23:00', 'THU');

INSERT INTO career
    (id, created_date, last_modified_date, center_name, end_of_work_year_month, job_position, start_of_work_year_month, status, trainer_id)
VALUES (1, NOW(), NOW(), '아자아자 피트니스 센터', '2023-12-01', '센터장', '2022-05-01', 'EMPLOYED', 2),
       (2, NOW(), NOW(), '아자아자 피트니스 센터', '2022-05-01', '팀장', '2020-01-01', 'EMPLOYED', 2),
       (3, NOW(), NOW(), '으라차차 피트니스 센터', '2023-12-01', '사원', '2018-10-01', 'EMPLOYED', 2);

INSERT INTO academic
    (id, created_date, last_modified_date, country, degree, emrollment_year_month, graduation_year_month, institution, major, name, trainer_id)
VALUES (1, NOW(), NOW(), 'America', 'MASTER', '2015-02-01', '2021-03-01', 'OVERSEAS_UNIVERSITY', 'Sport Management', 'Boston University', 2),
       (2, NOW(), NOW(), 'Korea', 'BACHELOR', '2015-02-01', '2021-03-01', 'FOUR_YEAR_UNIVERSITY', '소프트웨어', '광운대', 2),
       (3, NOW(), NOW(), 'Korea', 'HIGH_SCHOOL_DIPLOMA', '2015-02-01', '2021-03-01', 'HIGH_SCHOOL', '전공 없음', '풍무고등학교', 2);

INSERT INTO certificate
    (id, created_date, last_modified_date, acquisition_year_month, institution, name, trainer_id)
VALUES (1, NOW(), NOW(), '2023-03-01', '문화체육관광부부', '스포츠지도자 2급 보디빌딩', 2);

INSERT INTO award
    (id, created_date, last_modified_date, acquisition_year_month, institution, name, trainer_id)
VALUES (1, NOW(), NOW(), '2023-03-01', '기관', '수상', 2);

INSERT INTO education
    (id, created_date, last_modified_date, acquisition_year_month, institution, name, trainer_id)
VALUES (1, NOW(), NOW(), '2023-10-01', '박시현 아카데미', '체형분석과 운동처방 PART 1, 2', 2);

SELECT SLEEP(2); -- 2초 지연

-- 체육관에 회원 추가
INSERT INTO personal_training
(id,
 created_date,
 last_modified_date,
 center_first_registration_month,
 center_last_re_registration_month,
 info_input_status,
 note,
 registration_allowed_date,
 registration_allowed_status,
 registration_request_date,
 registration_status,
 remaining_pt_count,
 total_pt_count,
 gym_trainer_id,
 member_id)
VALUES (1, NOW(), NOW(), NULL, NULL, 'INFO_EMPTY', NULL, NULL, 'WAITING', now(), 'ALLOWED_BEFORE', 0, 0, 1, 1);

INSERT INTO notification
    (id, notification_type, created_date, last_modified_date, created_at, is_read, text, type, receiver_id, sender_id, personal_training_id)
VALUES (1, 'PERSONAL_TRAINING', NOW(), NOW(), NOW(), false, '아자아자 피트니스 센터 / 원세영 트레이너 PT 등록 요청이 도착했습니다.', 'PT_REGISTRATION_REQUEST', 1, 2, 1);

SELECT SLEEP(2); -- 2초 지연

-- 회원 -  PT 등록 허용하기
UPDATE
    personal_training
SET last_modified_date=now(),
    registration_allowed_date=now(),
    registration_allowed_status='ALLOWED',
    registration_status='ALLOWED'
WHERE id = 1;

INSERT INTO notification
    (id, created_date, last_modified_date, created_at, is_read, receiver_id, sender_id, text, type, personal_training_id, notification_type)
VALUES
    (2, now(), now(), now(), false, 2, 1, '최강록 회원님이 PT 등록을 수락하였습니다.', 'PT_REGISTRATION_REQUEST', 1, 'PERSONAL_TRAINING');

SELECT SLEEP(2); -- 2초 지연
-- 초기 PT 세부 정보 입력
insert into personal_training_info
    (id, created_date, last_modified_date, personal_training_id, pt_count, registration_date, registration_status)
values
    (1, now(), now(), 1, 30, now(), 'NEW_REGISTRATION');

insert into ptcount_log
    (id, created_date, last_modified_date, personal_training_id, registration_date, registration_status, remaining_pt_count, total_pt_count)
values
    (1, now(), now(), 1, now(), 'NEW_REGISTRATION', 30, 30);

update
    personal_training
set
    last_modified_date=now(),
    center_first_registration_month=now(),
    info_input_status='INFO_REGISTERED',
    note='허리 디스크',
    registration_status='NEW_REGISTRATION',
    remaining_pt_count=30,
    total_pt_count=30
where id=1;