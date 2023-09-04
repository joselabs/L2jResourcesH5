CREATE TABLE IF NOT EXISTS `grandboss_data` (
  `boss_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `loc_x` mediumint(6) NOT NULL DEFAULT '0',
  `loc_y` mediumint(6) NOT NULL DEFAULT '0',
  `loc_z` mediumint(6) NOT NULL DEFAULT '0',
  `heading` mediumint(6) NOT NULL DEFAULT '0',
  `respawn_time` bigint(13) unsigned NOT NULL DEFAULT '0',
  `currentHP` decimal(30,15) NOT NULL,
  `currentMP` decimal(30,15) NOT NULL,
  `status` tinyint(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`boss_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT IGNORE INTO `grandboss_data` VALUES 
(29001, -21610, 181594, -5734, 0, 0, 229898.48, 667.776, 0), 				-- Queen Ant (40)
(29006, 17726, 108915, -6480, 0, 0, 622493.58388, 3793.536, 0), 			-- Core (50)
(29014, 55024, 17368, -5412, 10126, 0, 622493.58388, 3793.536, 0), 			-- Orfen (50)
(29020, 116033, 17447, 10107, -25348, 0, 4068372, 39960, 0), 				-- Baium (75)
(29028, -105200, -253104, -15264, 0, 0, 62041918, 2248572, 0), 				-- Valakas (85)
(29068, 185708, 114298, -8221, 32768, 0, 62802301, 1998000, 0), 			-- Antharas Strong (85)
(29118, 0, 0, 0, 0, 0, 4109288, 1220547, 0); 						-- Beleth (83)