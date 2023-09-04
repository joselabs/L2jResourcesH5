DROP TABLE IF EXISTS `events_custom_data`;
CREATE TABLE IF NOT EXISTS `events_custom_data` (
  `event_name` VARCHAR(35) NOT NULL,
  `expire_time` decimal(20,0) NOT NULL DEFAULT '0',
  PRIMARY KEY (`event_name`)
) DEFAULT CHARSET=utf8;