CREATE TABLE `orion_coin_transaction`
(
    `id`                  INTEGER PRIMARY KEY AUTOINCREMENT,
    `player`              INTEGER      NOT NULL REFERENCES `orion_player` (`id`),
    `transaction_partner` INTEGER REFERENCES `orion_player` (`id`),
    `amount`              LONG         NOT NULL,
    `reason`              VARCHAR(255) NOT NULL
);