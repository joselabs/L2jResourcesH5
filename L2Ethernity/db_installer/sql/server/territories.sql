CREATE TABLE IF NOT EXISTS `territories` (
  `territoryId` INT NOT NULL default 0,
  `castleId` INT NOT NULL default 0,
  `lordObjectId` int(11) NOT NULL default 0,

  `fortId` INT NOT NULL default 0,
  `ownedWardIds` varchar(30) NOT NULL DEFAULT '',
  PRIMARY KEY (`territoryId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT IGNORE INTO `territories` VALUES
(81,1,0,101,'81;'),
(82,2,0,103,'82;'),
(83,3,0,104,'83;'),
(84,4,0,105,'84;'),
(85,5,0,106,'85;'),
(86,6,0,108,'86;'),
(87,7,0,109,'87;'),
(88,8,0,110,'88;'),
(89,9,0,111,'89;');