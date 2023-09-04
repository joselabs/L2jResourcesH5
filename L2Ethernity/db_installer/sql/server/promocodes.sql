DROP TABLE IF EXISTS `promocodes`;
CREATE TABLE `promocodes` (
  `name` varchar(255) NOT NULL,
  `value` int(11) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;