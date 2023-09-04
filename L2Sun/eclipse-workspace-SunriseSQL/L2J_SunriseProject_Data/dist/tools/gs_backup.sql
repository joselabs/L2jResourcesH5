-- MySQL dump 10.13  Distrib 5.6.45, for Win64 (x86_64)
--
-- Host: localhost    Database: sunriseSql
-- ------------------------------------------------------
-- Server version	5.6.45-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account_data`
--

DROP TABLE IF EXISTS `account_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_data` (
  `account_name` varchar(45) NOT NULL DEFAULT '',
  `var` varchar(20) NOT NULL DEFAULT '',
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`account_name`,`var`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_data`
--

LOCK TABLES `account_data` WRITE;
/*!40000 ALTER TABLE `account_data` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `accounts`
--

DROP TABLE IF EXISTS `accounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accounts` (
  `login` varchar(45) NOT NULL DEFAULT '',
  `password` varchar(45) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `lastactive` bigint(13) unsigned NOT NULL DEFAULT '0',
  `accessLevel` tinyint(4) NOT NULL DEFAULT '0',
  `lastIP` char(15) DEFAULT NULL,
  `lastServer` tinyint(4) DEFAULT '1',
  `pcIp` char(15) DEFAULT NULL,
  `hop1` char(15) DEFAULT NULL,
  `hop2` char(15) DEFAULT NULL,
  `hop3` char(15) DEFAULT NULL,
  `hop4` char(15) DEFAULT NULL,
  PRIMARY KEY (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts`
--

LOCK TABLES `accounts` WRITE;
/*!40000 ALTER TABLE `accounts` DISABLE KEYS */;
/*!40000 ALTER TABLE `accounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `accounts_ipauth`
--

DROP TABLE IF EXISTS `accounts_ipauth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `accounts_ipauth` (
  `login` varchar(45) NOT NULL,
  `ip` char(15) NOT NULL,
  `type` enum('deny','allow') DEFAULT 'allow'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `accounts_ipauth`
--

LOCK TABLES `accounts_ipauth` WRITE;
/*!40000 ALTER TABLE `accounts_ipauth` DISABLE KEYS */;
/*!40000 ALTER TABLE `accounts_ipauth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gameservers`
--

DROP TABLE IF EXISTS `gameservers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gameservers` (
  `server_id` int(11) NOT NULL DEFAULT '0',
  `hexid` varchar(50) NOT NULL DEFAULT '',
  `host` varchar(50) NOT NULL DEFAULT '',
  PRIMARY KEY (`server_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gameservers`
--

LOCK TABLES `gameservers` WRITE;
/*!40000 ALTER TABLE `gameservers` DISABLE KEYS */;
/*!40000 ALTER TABLE `gameservers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-01-31 11:45:51
