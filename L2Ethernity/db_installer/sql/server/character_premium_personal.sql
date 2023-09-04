-- ----------------------------
-- Table structure for character_premium_personal
-- ----------------------------
CREATE TABLE IF NOT EXISTS `character_premium_personal` (
  `charId` INT UNSIGNED NOT NULL DEFAULT 0,
  `id` int(5) NOT NULL DEFAULT '0',
  `status` int(1) NOT NULL DEFAULT '0',
  `expireTime` decimal(20,0) NOT NULL DEFAULT '0',
  PRIMARY KEY (`charId`)
);