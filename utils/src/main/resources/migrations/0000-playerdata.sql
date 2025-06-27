CREATE TABLE `orion_player`
(
    `id`          INTEGER PRIMARY KEY AUTOINCREMENT,
    `uuid`        VARCHAR(36) NOT NULL,
    `name`        VARCHAR(32) NOT NULL,
    `first_login` TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `last_login`  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `debug`       BOOLEAN     NOT NULL DEFAULT FALSE,
    `team_login`  BOOLEAN     NOT NULL DEFAULT FALSE,
    `social_spy`  BOOLEAN     NOT NULL DEFAULT FALSE,
    `command_spy` BOOLEAN     NOT NULL DEFAULT FALSE,
    `coins`       LONG        NOT NULL DEFAULT 1000,
    `locale`      VARCHAR(5)  NOT NULL DEFAULT 'en'
)