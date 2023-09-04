DROP TABLE IF EXISTS `character_promocodes`;
CREATE TABLE `character_promocodes` (
  `charId` INT NOT NULL DEFAULT 0,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;