-- ----------------------------
-- Table structure for character_premium
-- ----------------------------
CREATE TABLE IF NOT EXISTS `character_premium` (
  `account` varchar(45) NOT NULL DEFAULT '',
  `id` int(5) NOT NULL DEFAULT '0',
  `status` int(1) NOT NULL DEFAULT '0',
  `expireTime` decimal(20,0) NOT NULL DEFAULT '0',
  PRIMARY KEY (`account`)
);