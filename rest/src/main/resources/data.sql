-- Remove all data
truncate table user_details_to_role restart identity cascade;
truncate table roles restart identity cascade;
truncate table users_details restart identity cascade;
truncate table user_to_liked_review restart identity cascade;
truncate table reviews restart identity cascade;
truncate table critics restart identity cascade;
truncate table users restart identity cascade;

-- Fill roles
INSERT INTO public.roles (id, name)
VALUES (1, 'ROLE_USER')
ON CONFLICT DO NOTHING;
INSERT INTO public.roles (id, name)
VALUES (2, 'ROLE_CRITIC')
ON CONFLICT DO NOTHING;
INSERT INTO public.roles (id, name)
VALUES (3, 'ROLE_ADMIN')
ON CONFLICT DO NOTHING;

-- Fill users
INSERT INTO public.users (id, email, first_name, registration_date, second_name)
VALUES (1, 'user@m.ru', 'Andrey', '2020-05-18 12:36:13.730549', 'Polska')
ON CONFLICT DO NOTHING;
INSERT INTO public.users (id, email, first_name, registration_date, second_name)
VALUES (2, 'critic@m.ru', 'Petr', '2020-05-18 12:36:48.459495', 'Polska')
ON CONFLICT DO NOTHING;
INSERT INTO public.users (id, email, first_name, registration_date, second_name)
VALUES (3, 'admin@m.ru', 'Petr', '2020-05-18 12:36:54.850775', 'Polska')
ON CONFLICT DO NOTHING;

-- Fill user_details
INSERT INTO public.users_details (id, enabled, locked, password, user_id)
VALUES (1, true, false, '$2a$10$hpL8ZJ4drt6Igd7T1mTe1usTPin0VXjt4D7.9CmpcEKoelDBmCAkK', 1)
ON CONFLICT DO NOTHING;
INSERT INTO public.users_details (id, enabled, locked, password, user_id)
VALUES (2, true, false, '$2a$10$I86gxnn2aBJjMPalfaVI7.enj2Aq8B4Uv.yt/.H4Lyl4MLXrVfINi', 2)
ON CONFLICT DO NOTHING;
INSERT INTO public.users_details (id, enabled, locked, password, user_id)
VALUES (3, true, false, '$2a$10$GgbXiN3XPkQHgg1tReIn7e2Y.dJ7YrR7MjN/FGTqmDK./VtbEDOe.', 3)
ON CONFLICT DO NOTHING;

-- Fill user_details_to_role
INSERT INTO public.user_details_to_role (user_details_list_id, roles_id)
VALUES (1, 1)
ON CONFLICT DO NOTHING;
INSERT INTO public.user_details_to_role (user_details_list_id, roles_id)
VALUES (2, 1)
ON CONFLICT DO NOTHING;
INSERT INTO public.user_details_to_role (user_details_list_id, roles_id)
VALUES (3, 1)
ON CONFLICT DO NOTHING;
INSERT INTO public.user_details_to_role (user_details_list_id, roles_id)
VALUES (3, 3)
ON CONFLICT DO NOTHING;
INSERT INTO public.user_details_to_role (user_details_list_id, roles_id)
VALUES (2, 2)
ON CONFLICT DO NOTHING;

-- Fill critics
INSERT INTO public.critics (id, photo_url, user_id)
VALUES (1, 'https://i.pinimg.com/originals/7b/aa/25/7baa252dbdfeed669c152bedd2fa5feb.jpg', 2)
ON CONFLICT DO NOTHING;

-- Fill reviews
INSERT INTO public.reviews (id, date, rating, subject, text, author_id)
VALUES (1, '2020-05-18', 8.5, 'Jojo Rabbit', 'German charm.', 1)
ON CONFLICT DO NOTHING;
INSERT INTO public.reviews (id, date, rating, subject, text, author_id)
VALUES (2, '2020-05-12', 9.5, 'The Lighthouse', 'Masterpiece.', 1)
ON CONFLICT DO NOTHING;

-- Fill user_to_liked_review
INSERT INTO public.user_to_liked_review (user_id, review_id)
VALUES (1, 1)
ON CONFLICT DO NOTHING;
INSERT INTO public.user_to_liked_review (user_id, review_id)
VALUES (1, 2)
ON CONFLICT DO NOTHING;

-- Restart sequences
ALTER SEQUENCE critics_id_seq RESTART with 2;
ALTER SEQUENCE reviews_id_seq RESTART with 3;
ALTER SEQUENCE roles_id_seq RESTART with 4;
ALTER SEQUENCE users_details_id_seq RESTART with 4;
ALTER SEQUENCE users_id_seq RESTART with 4;