CREATE TABLE IF NOT EXISTS `character_offline_buffs` (
  `charId` int(10) unsigned NOT NULL,
  `skillId` int(10) unsigned NOT NULL DEFAULT '0',
  `level` int(10) unsigned NOT NULL DEFAULT '0',
  `itemId` int(10) unsigned NOT NULL DEFAULT '0',
  `price` bigint(20) unsigned NOT NULL DEFAULT '0',
  KEY `charId` (`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;