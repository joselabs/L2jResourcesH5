DROP TABLE IF EXISTS `character_achievements`;
CREATE TABLE `character_achievements` (
  `charId` INT NOT NULL default 0,
  `id` INT NOT NULL default 0,
  `points` BIGINT UNSIGNED NOT NULL default 0,
  PRIMARY KEY (`charId`, `id`, `points`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;