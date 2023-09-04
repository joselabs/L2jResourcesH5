ALTER TABLE `characters` DROP COLUMN `hitman_target`;
ALTER TABLE `characters` ADD COLUMN `hitman_target` TEXT DEFAULT NULL AFTER `language`;