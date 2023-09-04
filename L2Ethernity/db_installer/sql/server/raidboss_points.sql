CREATE TABLE IF NOT EXISTS `raidboss_points` (
  `charId` INT NOT NULL,
  `boss_id` INT NOT NULL,
  `points` INT NOT NULL DEFAULT '0',
  KEY `charId` (`charId`),
  KEY `boss_id` (`boss_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;