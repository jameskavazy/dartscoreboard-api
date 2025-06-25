---- AUTOMATED TEST DATA -----
--
--Fresh start
DELETE FROM visits;
DELETE FROM legs;
DELETE FROM sets;
DELETE FROM matches_users;
DELETE FROM matches;
DELETE FROM users;


INSERT INTO users (user_id, username, screen_name) VALUES
  ('user-1', 'user1@example.com', 'user1'),
  ('user-2', 'user2@example.com', 'user2'),
  ('user-3', 'user3@example.com', 'user3');


INSERT INTO matches (match_id, created_at, match_type, race_to_leg, race_to_set, winner_id, match_status)
VALUES ('match-1', '2025-06-15T20:38:21.414670Z', 'FiveO', 1, 1, NULL, 'ONGOING');


INSERT INTO matches_users (match_id, user_id, position)
VALUES ('match-1', 'user-1', 0), ('match-1', 'user-2', 1), ('match-1', 'user-3', 2);


INSERT INTO sets (set_id, match_id, set_winner_id, created_at)
VALUES ('set-1', 'match-1', NULL, CURRENT_TIMESTAMP);


INSERT INTO legs (leg_id, set_id, match_id, turn_index, winner_id, created_at)
VALUES ('leg-1', 'set-1', 'match-1', 0, NULL, CURRENT_TIMESTAMP);


INSERT INTO visits (visit_id, leg_id, user_id, score, checkout, created_at)
VALUES ('visit-1', 'leg-1', 'user-1', 100, false, '2025-06-15 21:25:11.857'),
       ('visit-2', 'leg-1', 'user-2', 140, false, '2025-06-15 21:26:11.857'),
       ('visit-3', 'leg-1', 'user-3', 180, false, '2025-06-15 21:27:11.857'),
       ('visit-4', 'leg-1', 'user-1', 180, false, '2025-06-15 21:28:11.857'),
       ('visit-5', 'leg-1', 'user-2', 60, false, '2025-06-15 21:29:11.857'),
       ('visit-6', 'leg-1', 'user-3', 180, false, '2025-06-15 21:30:11.857'),
       ('visit-7', 'leg-1', 'user-1', 100, false, '2025-06-15 21:30:11.857'),
       ('visit-8', 'leg-1', 'user-2', 90, false, '2025-06-15 21:30:11.857');
