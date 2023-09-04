DROP TABLE IF EXISTS `character_scheme_list`;
CREATE TABLE `character_scheme_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `charId` varchar(40) DEFAULT NULL,
  `scheme_name` varchar(36) DEFAULT NULL,
  `mod_accepted` tinyint(1) DEFAULT NULL,
  `icon` tinyint(2) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;