CREATE TABLE IF NOT EXISTS `siegable_clanhall` (
  `id` int(10) NOT NULL DEFAULT '0',
  `ownerId` int(10) DEFAULT NULL,
  `Grade` decimal(1,0) NOT NULL default '0',
  `nextSiege` bigint(20) DEFAULT NULL,
  `siegeLenght` int(10) DEFAULT NULL,
  `schedule_config` varchar(20) DEFAULT NULL, 
  PRIMARY KEY (`id`),
  KEY `ownerId` (`ownerId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT IGNORE INTO `siegable_clanhall` (`id`, `ownerId`, `Grade`, `nextSiege`, `siegeLenght`, `schedule_config`) VALUES
(21, 0, 3, 0, 3600000, '14;0;0;12;00'),
(34, 0, 3, 0, 3600000, '14;0;0;12;00'),
(35, 0, 3, 0, 3600000, '14;0;0;12;00'),
(62, 0, 3, 0, 3600000, '14;0;0;12;00'),
(63, 0, 3, 0, 3600000, '14;0;0;12;00'),
(64, 0, 3, 0, 3600000, '14;0;0;12;00');