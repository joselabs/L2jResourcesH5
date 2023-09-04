CREATE TABLE `streams` (
  `channel_name` varchar(255) NOT NULL,
  `connected_player_id` int(12) DEFAULT NULL,
  `connected_player_server` varchar(255) DEFAULT NULL,
  `ids_awaiting_approval` varchar(255) DEFAULT NULL,
  `not_rewarded_seconds` int(12) DEFAULT NULL,
  `total_rewarded_seconds_today` int(12) DEFAULT NULL,
  `punished_until_date` int(12) DEFAULT NULL,
  PRIMARY KEY (`channel_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
