CREATE DATABASE  IF NOT EXISTS `db_gestione_di_immagini_JS` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `db_gestione_di_immagini_JS`;
-- MySQL dump 10.13  Distrib 8.4.1, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: db_gestione_di_immagini
-- ------------------------------------------------------
-- Server version	8.4.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Album`
--

DROP TABLE IF EXISTS `Album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Album` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Title` varchar(45) NOT NULL,
  `Creator` varchar(45) NOT NULL,
  `CreationDate` date NOT NULL DEFAULT (curdate()),
  PRIMARY KEY (`ID`),
  KEY `creatore_idx` (`Creator`),
  CONSTRAINT `creator` FOREIGN KEY (`Creator`) REFERENCES `User` (`Email`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Album`
--

LOCK TABLES `Album` WRITE;
/*!40000 ALTER TABLE `Album` DISABLE KEYS */;
/*!40000 ALTER TABLE `Album` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Comment`
--

DROP TABLE IF EXISTS `Comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Comment` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Text` varchar(255) NOT NULL,
  `User` varchar(45) NOT NULL,
  `Image` int NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `utente_idx` (`User`),
  KEY `image_idx` (`Image`),
  CONSTRAINT `image` FOREIGN KEY (`Image`) REFERENCES `Image` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `user` FOREIGN KEY (`User`) REFERENCES `User` (`Email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Comment`
--

LOCK TABLES `Comment` WRITE;
/*!40000 ALTER TABLE `Comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `Comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Image`
--

DROP TABLE IF EXISTS `Image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Image` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Title` varchar(45) NOT NULL,
  `CreationDate` date NOT NULL DEFAULT (curdate()),
  `Description` varchar(255) DEFAULT NULL,
  `Path` varchar(255) NOT NULL,
  `User` varchar(45) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `utente_idx` (`User`),
  CONSTRAINT `utente` FOREIGN KEY (`User`) REFERENCES `User` (`Email`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Image`
--

LOCK TABLES `Image` WRITE;
/*!40000 ALTER TABLE `Image` DISABLE KEYS */;
/*!40000 ALTER TABLE `Image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ImageOfAlbum`
--

DROP TABLE IF EXISTS `ImageOfAlbum`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ImageOfAlbum` (
  `Album` int NOT NULL,
  `Image` int NOT NULL,
  `Position` int NOT NULL,
  `User` varchar(45) NOT NULL,
  PRIMARY KEY (`Album`,`Image`,`User`),
  KEY `fk_ImageOfAlbum_1_idx` (`Image`),
  KEY `fk_ImageOfAlbum_2_idx` (`Album`),
  KEY `fk_ImageOfAlbum_3_idx` (`User`),
  CONSTRAINT `fk_ImageOfAlbum_1` FOREIGN KEY (`Image`) REFERENCES `Image` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ImageOfAlbum_2` FOREIGN KEY (`Album`) REFERENCES `Album` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_ImageOfAlbum_3` FOREIGN KEY (`User`) REFERENCES `User` (`Email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ImageOfAlbum`
--

LOCK TABLES `ImageOfAlbum` WRITE;
/*!40000 ALTER TABLE `ImageOfAlbum` DISABLE KEYS */;
/*!40000 ALTER TABLE `ImageOfAlbum` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `User` (
  `Email` varchar(45) NOT NULL,
  `Password` varchar(45) NOT NULL,
  `Name` varchar(45) NOT NULL,
  `Surname` varchar(45) NOT NULL,
  PRIMARY KEY (`Email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `User`
--

LOCK TABLES `User` WRITE;
/*!40000 ALTER TABLE `User` DISABLE KEYS */;
INSERT INTO `User` VALUES ('aldo.pinelli@gmail.com','HabibiHabebti','Aldo','Pinelli'),('christian.prendin@gmail.com','piana','Christian','Prendin'),('default','0','0','0'),('mirko.pika@gmail.com','abruzzo','Mirko','Pika'),('riccardo.piana.8@gmail.com','prend','Riccardo','Pianalto');
/*!40000 ALTER TABLE `User` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'db_gestione_di_immagini'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-07-29 16:31:02
