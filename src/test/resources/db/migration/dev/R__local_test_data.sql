TRUNCATE TABLE
    auth.account,
    employee.department,
    employee.team,
    employee.position,
    employee.profile
    RESTART IDENTITY CASCADE;

INSERT INTO employee.department (department_code, department_name)
VALUES ('DPT100', '경영지원부'),
       ('DPT200', '인사부'),
       ('DPT300', '재무부'),
       ('DPT400', '마케팅부'),
       ('DPT500', '영업부'),
       ('DPT600', '운영부');

INSERT INTO employee.team (team_code, department_id, team_name)
VALUES ('TM100', 1, '경영팀'),
       ('TM200', 2, '인사팀'),
       ('TM201', 2, '보상팀'),
       ('TM300', 3, '재무팀'),
       ('TM301', 3, '회계팀'),
       ('TM400', 4, '브랜드팀'),
       ('TM401', 4, '시장조사팀'),
       ('TM500', 5, '고객관리팀'),
       ('TM501', 5, '국내영업팀'),
       ('TM502', 5, '해외영업팀'),
       ('TM600', 6, '품질관리팀'),
       ('TM601', 6, '물류팀');

INSERT INTO employee.position (position_code, position_name, basic_salary, incentive_salary)
VALUES ('PSN10', '대표', 100000000, 5000000),
       ('PSN20', '이사', 5000000, 250000),
       ('PSN30', '부장', 3500000, 175000),
       ('PSN40', '차장', 3000000, 150000),
       ('PSN50', '과장', 2500000, 125000),
       ('PSN60', '대리', 2000000, 100000),
       ('PSN70', '주임', 1500000, 75000),
       ('PSN80', '사원', 1250000, 50000);