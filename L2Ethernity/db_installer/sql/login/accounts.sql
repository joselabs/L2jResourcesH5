CREATE TABLE IF NOT EXISTS `accounts` (
  `login` VARCHAR(45) NOT NULL default '',
  `password` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `lastAccess` int(11) NOT NULL DEFAULT '0',
  `accessLevel` TINYINT NOT NULL DEFAULT 0,
  `lastIP` CHAR(15) NULL DEFAULT NULL,
  `lastServer` TINYINT DEFAULT 1,
  `banExpire` int(11) NOT NULL DEFAULT '0',
  `allowIp` varchar(255) NOT NULL DEFAULT '',
  `allowHwid` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;