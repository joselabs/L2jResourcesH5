DROP TABLE IF EXISTS `character_promocodes_account`;
CREATE TABLE `character_promocodes_account` (
  `account` VARCHAR(45) DEFAULT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;