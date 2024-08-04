INSERT INTO roles VALUES (1, 'USER'), (2, 'ADMIN');

INSERT INTO `users` (`id`, `username`, `password`)
VALUES
    (1,'admin', '$2a$10$x3duRzDNgNr/Gck2fpNS2OFxrWNIeWJhWafQlBacBkww3pT9H4GqG');

INSERT INTO `users_roles` (`user_id`, `role_id`)
VALUES
    (1, 1),
    (1, 2);