CREATE TABLE IF NOT EXISTS `destruction_bosses` (
  `bossId` smallint(5) unsigned NOT NULL,
  `altar_name` varchar(25) NOT NULL,
  `status` int(1) NOT NULL DEFAULT 0,
  `currentHp` decimal(8,0) DEFAULT NULL,
  `currentMp` decimal(8,0) DEFAULT NULL,
  PRIMARY KEY (`bossId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT IGNORE INTO `destruction_bosses` (`bossId`,`altar_name`,`status`,`currentHp`,`currentMp`) VALUES
(25750,'Darkelf',0,0,0), -- Zombie Lord Ferkel (20)
(25782,'Dwarf',0,0,0), -- Nellis Vengeful Spirit (39)
(25800,'Dwarf',0,0,0), -- Mammon Collector Talos (25)
(25779,'Orc',0,0,0), -- Betrayer of Urutu Freki (25)
(25735,'Gludin',0,0,0), -- Greyclaw Kutus (23)
(25738,'Gludin',0,0,0), -- Lead Tracker Sharuk (23)
(25741,'Gludin',0,0,0), -- Sukar Wererat Chief (21)
(25744,'Gludio',0,0,0), -- Ikuntai (25)
(25747,'Gludio',0,0,0), -- Zombie Lord Crowl (25)
(25753,'Dion',0,0,0), -- Guillotine Warden (35)
(25754,'Dion',0,0,0), -- Fire Lord Shadar (35)
(25757,'Dion',0,0,0), -- Soul Collector Acheron (35)
(25767,'Oren',0,0,0), -- Carnage Lord Gato (50)
(25770,'Oren',0,0,0), -- Ketra Commander Atis (49)
(25760,'Giran',0,0,0), -- Lord Ishka (60)
(25763,'Giran',0,0,0), -- Demon Kuri (59)
(25766,'Giran',0,0,0), -- Ancient Drake (60)
(25773,'Heine',0,0,0), -- Beacon of Blue Sky (45)
(25776,'Heine',0,0,0), -- Earth Protector Panathen (43)
(25787,'Goddard',0,0,0), -- Ketra Hero Hekaton (80)
(25790,'Goddard',0,0,0), -- Varka Hero Shadith (80)
(25784,'Schutgart',0,0,0), -- Rayito the Looter (37)
(25793,'Aden',0,1825269,3130), -- Doom Blade Tanatos (72)
(25794,'Aden',0,945900,3347), -- Kernon (75)
(25797,'Aden',0,888658,2987); -- Meanas Anor (70)