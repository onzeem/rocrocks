-- data.sql — full seed data for local development.
--
-- Runs once, automatically, the first time Spring Boot starts against a
-- database file that doesn't exist yet (H2 file mode: jdbc:h2:file:./data/budgetdb).
-- If you need to re-seed after changing this file, delete data/budgetdb.mv.db
-- (and its .trace.db sibling, if present) first, then restart — Spring Boot
-- won't re-run this against a database file that already exists.
--
-- One demo user (id 33), covering September 2026 — matching what the
-- frontend currently requests (DEMO_USER_ID in home.html / budget.html /
-- transactions.html, and the hardcoded YEAR/MONTH built from real device
-- time). If your machine's real clock is in a different month by the time
-- you're testing this, either update the budgets row's month/year below,
-- or temporarily hardcode YEAR/MONTH in the frontend to 2026/9 to match.

-- Users -------------------------------------------------------------------

INSERT INTO users (id, name, email) VALUES (33, 'Test User', 'test@example.com');

-- Categories ----------------------------------------------------------------
-- Names/order match categories-data.js on the frontend (icons/colors live
-- there, not in this table — the frontend looks up icon/color by name).

INSERT INTO categories (id, name, description, icon, color) VALUES
  (1, 'Housing', 'Rent, utilities', '🏠', 'grape'),
  (2, 'Groceries', 'Food at home', '🥑', 'mint'),
  (3, 'Transport', 'Fuel, transit', '🚌', 'yellow'),
  (4, 'Dining out', 'Restaurants, delivery', '🍜', 'coral'),
  (5, 'Subscriptions', 'Recurring services', '📺', 'pink'),
  (6, 'Entertainment', 'Movies, streaming', '🎬', 'sky'),
  (7, 'Utilities', 'Water, power, internet', '💧', 'teal');

-- Budget for September 2026 --------------------------------------------------

INSERT INTO budgets (id, user_id, budget_month, budget_year, total_income)
VALUES (1, 33, 9, 2026, 5900.00);

INSERT INTO budget_categories (id, budget_id, category_id, allocated_amount) VALUES
  (1, 1, 1, 1500.00),  -- Housing
  (2, 1, 2, 500.00),   -- Groceries
  (3, 1, 3, 300.00),   -- Transport
  (4, 1, 4, 200.00),   -- Dining out
  (5, 1, 5, 80.00),    -- Subscriptions
  (6, 1, 6, 90.00),    -- Entertainment
  (7, 1, 7, 150.00);   -- Utilities

-- Transactions for September 2026 -------------------------------------------
-- No "income" rows here — the backend tracks income as a single fixed
-- amount on the budget itself (total_income above), not via transactions.
-- home.html always maps every returned transaction as an expense ("out").

INSERT INTO transactions (id, user_id, category_id, amount, date, description, type) VALUES
  (1,  33, 2, 54.20,   '2026-09-19', 'Green Leaf Market', 'EXPENSE'),
  (2,  33, 1, 1365.00, '2026-09-17', 'Riverside Apartments', 'EXPENSE'),
  (3,  33, 4, 28.75,   '2026-09-16', 'Tanto Ramen', 'EXPENSE'),
  (4,  33, 3, 81.00,   '2026-09-15', 'Metro Transit Pass', 'EXPENSE'),
  (5,  33, 5, 9.99,    '2026-09-14', 'Cloudline Storage', 'EXPENSE'),
  (6,  33, 4, 41.10,   '2026-09-12', 'Basil & Vine', 'EXPENSE'),
  (7,  33, 2, 67.85,   '2026-09-10', 'Green Leaf Market', 'EXPENSE'),
  (8,  33, 7, 96.40,   '2026-09-08', 'City Water & Power', 'EXPENSE'),
  (9,  33, 5, 11.99,   '2026-09-06', 'SoundWave+', 'EXPENSE'),
  (10, 33, 3, 81.00,   '2026-09-05', 'Metro Transit Pass', 'EXPENSE'),
  (11, 33, 2, 48.60,   '2026-09-03', 'Green Leaf Market', 'EXPENSE'),
  (12, 33, 1, 1365.00, '2026-09-01', 'Riverside Apartments', 'EXPENSE'),
  (13, 33, null, 2950.00, '2026-09-18', 'Payroll deposit', 'INCOME'),
  (14, 33, null, 2950.00, '2026-09-04', 'Payroll deposit', 'INCOME'),
  (15, 33, null, 3000.00, '2026-09-10', 'Freelance payment', 'INCOME'),
  (16, 33, null, 3100.00, '2026-09-15', 'Year-end bonus', 'INCOME');

-- Savings goals -------------------------------------------------------------

INSERT INTO savings_goals (id, user_id, name, icon, color, target_amount, saved_amount, due_date, note) VALUES
  (1, 33, 'Emergency fund', '🛟', 'grape', 6000.00, 4200.00, null, null),
  (2, 33, 'Iceland trip',   '✈️', 'coral', 2500.00, 980.00,  '2027-06-01', 'Add $130/mo to make the June date'),
  (3, 33, 'New laptop',     '💻', 'mint',  1100.00, 1100.00, null, null);

-- IMPORTANT: the tables above use auto-generated IDs (GenerationType.IDENTITY).
-- Manually inserting explicit ID values, like above, doesn't tell H2's
-- internal identity counter to skip past them — without the RESTART WITH
-- statements below, the very next row the app tries to insert (e.g. through
-- add-transaction.html) collides head-on with one of these seeded IDs.
ALTER TABLE users ALTER COLUMN id RESTART WITH 34;
ALTER TABLE budgets ALTER COLUMN id RESTART WITH 2;
ALTER TABLE budget_categories ALTER COLUMN id RESTART WITH 8;
ALTER TABLE transactions ALTER COLUMN id RESTART WITH 17;
ALTER TABLE savings_goals ALTER COLUMN id RESTART WITH 4;