set foreign_key_checks=0;

ALTER TABLE `users`
ADD COLUMN `email` VARCHAR(255) NOT NULL AFTER `username`,
ADD UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE;

ALTER TABLE `users`
CHANGE COLUMN `email` `email` VARCHAR(255) NOT NULL AFTER `id`,
CHANGE COLUMN `username` `username` VARCHAR(255) NOT NULL AFTER `email`,
CHANGE COLUMN `password` `password` VARCHAR(255) NOT NULL AFTER `username`,
CHANGE COLUMN `schema_name` `schema_name` VARCHAR(255) NOT NULL AFTER `password`;


set foreign_key_checks=1;