DROP TABLE IF EXISTS `facebook_profiles`;
CREATE TABLE `facebook_profiles` (
  `id` varchar(32) NOT NULL,
  `name` varchar(128) NOT NULL,
  `last_completed_task_date` bigint(13) NOT NULL DEFAULT '-1',
  `positive_points_like` tinyint(4) NOT NULL,
  `positive_points_comment` tinyint(4) NOT NULL,
  `positive_points_post` tinyint(4) NOT NULL,
  `positive_points_share` int(4) NOT NULL,
  `negative_points_like` int(6) NOT NULL,
  `negative_points_comment` int(6) NOT NULL,
  `negative_points_post` int(6) NOT NULL,
  `negative_points_share` int(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;