DROP TABLE IF EXISTS `weekly_trader_entries`;
CREATE TABLE `weekly_trader_entries` (
  `products` longtext NOT NULL,
  `ingredients` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
