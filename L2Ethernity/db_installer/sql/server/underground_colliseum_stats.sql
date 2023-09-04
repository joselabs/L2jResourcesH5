CREATE TABLE IF NOT EXISTS `underground_colliseum_stats` (
  `arenaId` int(1) unsigned NOT NULL,
  `leader` varchar(16) NOT NULL,
  `wins` int(5) NOT NULL DEFAULT 0,
  PRIMARY KEY (`arenaId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;