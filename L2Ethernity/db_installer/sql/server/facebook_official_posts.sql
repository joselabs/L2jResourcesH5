DROP TABLE IF EXISTS `facebook_official_posts`;
CREATE TABLE `facebook_official_posts` (
  `post_id` varchar(36) NOT NULL,
  `rewards_like` tinyint(1) NOT NULL,
  `rewards_comment` tinyint(1) NOT NULL,
  `rewards_post` tinyint(1) NOT NULL,
  `rewards_share` tinyint(1) NOT NULL,
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;