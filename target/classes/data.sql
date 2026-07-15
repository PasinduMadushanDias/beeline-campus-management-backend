-- ==========================================
-- Beeline Campus SMS — Seed Data v4.0
-- Schema: User (auth) → Student/Staff (profile) → Branch
-- Attendance permission + ID-based tracking
-- Branch-scoped homework management
-- ==========================================

-- ── Branches (with embedded fee config) ──
INSERT INTO branches (id, name, duration, schedule, color, total_fee, installments_count, due_day_value)
VALUES
    (1, 'Galle', '5 Months', 'Mon, Wed, Fri', 'blue', 45000, 3, 'Every 5th of the month'),
    (2, 'Matara', '1 Year', 'Sunday Only', 'teal', 45000, 3, 'Every 5th of the month')
    ON CONFLICT (id) DO NOTHING;

SELECT setval('branches_id_seq', (SELECT COALESCE(MAX(id), 0) FROM branches));

-- ── Users (auth-only: no branch_id) ──
INSERT INTO users (id, full_name, username, password, email, role, status)
VALUES
    (1, 'Admin User', 'admin', '123', 'admin@beeline.lk', 'ADMIN', 'ACTIVE'),
    (2, 'Mr. Kamal Perera', 'teacher', '123', 'kamal@beeline.lk', 'TEACHER', 'ACTIVE'),
    (3, 'Ms. Nisha Fernando', 'nisha.fernando', '123', 'nisha@beeline.lk', 'TEACHER', 'ACTIVE'),
    (4, 'Mr. Ruwan Silva', 'staff', '123', 'ruwan@beeline.lk', 'STAFF', 'ACTIVE'),
    (5, 'Ms. Amaya Dias', 'amaya.dias', '123', 'amaya@beeline.lk', 'STAFF', 'ACTIVE'),
    (6, 'Mr. Dinesh Rajapaksha', 'dinesh.r', '123', 'dinesh@beeline.lk', 'STAFF', 'ON_LEAVE')
    ON CONFLICT (id) DO NOTHING;

SELECT setval('users_id_seq', (SELECT COALESCE(MAX(id), 0) FROM users));

-- ── Staff profiles (Teachers + Staff → linked to User, with attendance permission) ──
INSERT INTO staff (id, user_id, can_mark_attendance)
VALUES
    (1, 2, true),
    (2, 3, true),
    (3, 4, true),
    (4, 5, false),
    (5, 6, false)
    ON CONFLICT (id) DO NOTHING;

SELECT setval('staff_id_seq', (SELECT COALESCE(MAX(id), 0) FROM staff));

-- ── Staff ↔ Branch assignments (ManyToMany join table) ──
INSERT INTO staff_branches (staff_id, branch_id)
VALUES
    (1, 1),
    (2, 2),
    (3, 1),
    (4, 2),
    (5, 1)
    ON CONFLICT DO NOTHING;

-- ── Students ──
-- Intentionally left empty: students are created only through Admin registration
-- (StudentService.registerStudent), which also generates their auth User and QR ID.
-- setval's 2nd arg must be >= 1, so on an empty table we pass 1 with is_called=false
-- (next nextval() returns 1) instead of MAX(id)'s fallback of 0, which Postgres rejects.
SELECT setval('students_id_seq', COALESCE((SELECT MAX(id) FROM students), 1), (SELECT MAX(id) FROM students) IS NOT NULL);

-- ── Announcements ──
INSERT INTO announcements (id, title, content, posted_at, posted_by, target_branch_id)
VALUES
    (1, 'Welcome to Term 2!', 'Dear students, welcome back for Term 2. Please review the updated schedule.', '2025-06-01 09:00:00', 2, NULL),
    (2, 'Galle Branch — Extra Class', 'There will be an extra class this Saturday for Galle branch students.', '2025-06-03 14:30:00', 2, 1),
    (3, 'Matara Weekend Schedule', 'Sunday classes will start at 8:30 AM from next week onwards.', '2025-06-05 10:00:00', 3, 2),
    (4, 'Fee Payment Reminder', 'Please ensure your 2nd installment is paid by the 5th of this month.', '2025-06-07 08:00:00', 2, NULL)
    ON CONFLICT (id) DO NOTHING;

SELECT setval('announcements_id_seq', (SELECT COALESCE(MAX(id), 0) FROM announcements));

-- ── Homework Tasks (branch-scoped daily assignments by Admin) ──
INSERT INTO homework_tasks (id, branch_id, assigned_date, task_details)
VALUES
    (1, 1, '2025-06-02', 'Chatter box step 2, Vocabulary step 3'),
    (2, 2, '2025-06-02', 'Reading comprehension page 15-18, Grammar exercise 4'),
    (3, 1, '2025-06-04', 'Listening practice unit 5, Speaking drill pairs'),
    (4, 2, '2025-06-08', 'Essay: My Hometown (200 words), Vocabulary list week 6')
    ON CONFLICT (id) DO NOTHING;

SELECT setval('homework_tasks_id_seq', (SELECT COALESCE(MAX(id), 0) FROM homework_tasks));

-- ── Student Homework Submissions ──
-- Intentionally left empty: no seeded students to attach submissions to.
SELECT setval('student_homework_submissions_id_seq', COALESCE((SELECT MAX(id) FROM student_homework_submissions), 1), (SELECT MAX(id) FROM student_homework_submissions) IS NOT NULL);

-- ── Attendance Records ──
-- Intentionally left empty: no seeded students to attach attendance to.
SELECT setval('attendance_id_seq', COALESCE((SELECT MAX(id) FROM attendance), 1), (SELECT MAX(id) FROM attendance) IS NOT NULL);