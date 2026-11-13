-- V4__baseline_catalog.sql: Ensure catalog has sample books
INSERT INTO book (title, genre, year, archived, created_at, version)
SELECT 'Spring in Action 6th Edition', 'TECHNICAL', 2022, false, CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM book WHERE title = 'Spring in Action 6th Edition');

INSERT INTO book (title, genre, year, archived, created_at, version)
SELECT 'Designing Data-Intensive Applications', 'SYSTEM_DESIGN', 2017, false, CURRENT_TIMESTAMP, 0
WHERE NOT EXISTS (SELECT 1 FROM book WHERE title = 'Designing Data-Intensive Applications');
