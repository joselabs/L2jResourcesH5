DROP TABLE IF EXISTS `facebook_completed_tasks`;
CREATE TABLE `facebook_completed_tasks` (
  `player_id` int(11) NOT NULL,
  `taken_date` bigint(13) NOT NULL,
  `comment_approved` tinyint(1) NOT NULL,
  `rewarded` tinyint(1) NOT NULL,
  `action_id` varchar(36) NOT NULL,
  `action_type_name` varchar(16) NOT NULL,
  `executor_id` varchar(32) NOT NULL,
  `created_date` bigint(13) NOT NULL,
  `extraction_date` bigint(13) NOT NULL,
  `message` text NOT NULL,
  `father_id` varchar(36) NOT NULL,
  PRIMARY KEY (`player_id`,`action_id`,`action_type_name`,`father_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;