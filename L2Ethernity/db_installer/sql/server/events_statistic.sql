DROP TABLE IF EXISTS `events_statistic`;
CREATE TABLE IF NOT EXISTS `events_statistic` (
  `eventId` VARCHAR(35) NOT NULL,
  `char_name` VARCHAR(35) NOT NULL,
  `clan_name` varchar(45),
  `ally_name` varchar(45),
  `classId` TINYINT UNSIGNED DEFAULT NULL,
  `scores` SMALLINT UNSIGNED DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;