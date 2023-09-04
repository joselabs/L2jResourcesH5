CREATE TABLE IF NOT EXISTS `blood_altars` (
   `altar_name` varchar(25) NOT NULL,
   `status` int(1) NOT NULL DEFAULT 0,
   `progress` int(1) NOT NULL DEFAULT 0,
   `changeTime` bigint(13) unsigned NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT IGNORE INTO `blood_altars` (`altar_name`,`status`,`progress`,`changeTime`) VALUES
('Aden',0,0,0),
('TalkingIsland',0,0,0),
('Elven',0,0,0),
('Darkelf',0,0,0),
('Dwarf',0,0,0),
('Orc',0,0,0),
('Kamael',0,0,0),
('Gludin',0,0,0),
('Gludio',0,0,0),
('Dion',0,0,0),
('Oren',0,0,0),
('Giran',0,0,0),
('Heine',0,0,0),
('Goddard',0,0,0),
('Rune',0,0,0),
('Schutgart',0,0,0),
('Primeval',0,0,0);