CREATE TABLE IF NOT EXISTS `territories` (
  `territoryId` INT NOT NULL DEFAULT 0,
  `castleId` INT NOT NULL DEFAULT 0,
  `fortId` INT NOT NULL DEFAULT 0,
  `lordId` INT NOT NULL DEFAULT 0,
  `ownedWardIds` varchar(30) NOT NULL DEFAULT '',
  PRIMARY KEY (`territoryId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT IGNORE INTO `territories` VALUES
(81,1,101,0,'81;'),
(82,2,103,0,'82;'),
(83,3,104,0,'83;'),
(84,4,105,0,'84;'),
(85,5,106,0,'85;'),
(86,6,108,0,'86;'),
(87,7,109,0,'87;'),
(88,8,110,0,'88;'),
(89,9,111,0,'89;');