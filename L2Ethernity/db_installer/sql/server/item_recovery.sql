DROP TABLE IF EXISTS `item_recovery`;
CREATE TABLE `item_recovery`  (
  `charId` int(10) NOT NULL,
  `item_id` int(11) NOT NULL,
  `object_id` int(11) NOT NULL,
  `count` int(20) NULL DEFAULT 0,
  `enchant_level` int(11) NULL DEFAULT 0,
  `augmentation` int(11) NOT NULL,
  `elementals` TEXT NULL DEFAULT NULL,
  `time` bigint(13) NULL DEFAULT NULL,
  PRIMARY KEY (`object_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;