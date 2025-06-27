CREATE TABLE IF NOT EXISTS `orion_bank_account`
(
    `accountId` INTEGER PRIMARY KEY AUTOINCREMENT,
    `maxAmount` LONG    NOT NULL DEFAULT 25000,
    `amount`    LONG    NOT NULL DEFAULT 0,
    `owner`     INTEGER NOT NULL,
    FOREIGN KEY (`owner`) REFERENCES `orion_player` (`id`)
);

CREATE TABLE IF NOT EXISTS `orion_bank_transactions`
(
    `txId`        INTEGER PRIMARY KEY AUTOINCREMENT,
    `accountId`   INTEGER      NOT NULL,
    `from`        INTEGER,
    `amount`      INTEGER      NOT NULL,
    `diagnostics` VARCHAR(255) NOT NULL,
    `timestamp`   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`accountId`) REFERENCES `orion_bank_account` (`accountId`),
    FOREIGN KEY (`from`) REFERENCES `orion_player` (`id`)
);

CREATE TABLE IF NOT EXISTS `orion_bank_cards`
(
    `cardId`        INTEGER PRIMARY KEY AUTOINCREMENT,
    `accountId`     INTEGER      NOT NULL,
    `pinHash`       VARCHAR(255) NOT NULL,
    `spendingLimit` INTEGER      NOT NULL,
    `description`   TEXT         NOT NULL DEFAULT '',
    FOREIGN KEY (`accountId`) REFERENCES `orion_bank_account` (`accountId`)
);