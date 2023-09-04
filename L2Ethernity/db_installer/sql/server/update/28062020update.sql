ALTER TABLE `characters` MODIFY COLUMN `game_points` decimal(20,0) NOT NULL DEFAULT '0';
ALTER TABLE `character_variables` MODIFY COLUMN `expire_time` decimal(20,0) NOT NULL DEFAULT '0';
ALTER TABLE `characters` DROP COLUMN `title_color`;