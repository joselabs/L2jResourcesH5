CREATE TABLE IF NOT EXISTS `character_minigame_score` (
	`charId` int(11) NOT NULL,
	`score` int(11) NOT NULL,
	PRIMARY KEY (`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;