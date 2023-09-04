DROP TABLE IF EXISTS `character_scheme_contents`;
CREATE TABLE `character_scheme_contents` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `schemeId` int(11) DEFAULT NULL,
  `skill_id` int(8) DEFAULT NULL,
  `skill_level` int(4) DEFAULT NULL,
  `premium_lvl` int(4) DEFAULT NULL,
  `buff_class` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;