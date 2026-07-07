-- MySQL dump 10.13  Distrib 8.0.42, for Linux (x86_64)
--
-- Host: localhost    Database: sportsphere_db
-- ------------------------------------------------------
-- Server version	8.0.42-0ubuntu0.20.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `coaches`
--

DROP TABLE IF EXISTS `coaches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coaches` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `facility_id` bigint NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `specialization` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sport` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `price_per_hour` decimal(10,2) NOT NULL,
  `average_rating` decimal(3,2) DEFAULT '0.00',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `idx_coaches_facility_sport` (`facility_id`,`sport`),
  CONSTRAINT `coaches_ibfk_1` FOREIGN KEY (`facility_id`) REFERENCES `facilities` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coaches`
--

LOCK TABLES `coaches` WRITE;
/*!40000 ALTER TABLE `coaches` DISABLE KEYS */;
INSERT INTO `coaches` VALUES (1,1,'Milan Gajić','Kondiciona priprema i napredni rad na tehnici','Tenis',2500.00,4.85,1),(2,1,'Ana Ivanović','Škola tenisa za decu i odrasle amatere','Tenis',3500.00,4.95,1),(3,2,'Nikola Jokić','Individualni rad na postu, pregledu igre i šutu','Košarka',4000.00,5.00,1),(4,1,'Petar Kostić','Kondicioni trening i rad na tehnici','Košarka',2800.00,4.70,1),(5,1,'Milica Jović','Odbojkaška škola za mlade','Odbojka',2600.00,4.80,1),(6,2,'Darko Stanković','Teniski trener za turnirske igrače','Tenis',3200.00,4.60,1),(7,2,'Jelena Marković','Fudbalska kondicija i tehnika','Fudbal',2200.00,4.40,1),(8,6,'Aleksandar Popović','Individualni rad na šutu i prodoru','Košarka',3500.00,4.90,1),(9,6,'Tamara Nikolić','Teniska akademija za decu','Tenis',3000.00,4.75,1),(10,6,'Nemanja Vidić','Fudbalska taktika i priprema','Fudbal',2700.00,4.50,1),(11,9,'Igor Filipović','Košarkaška škola','Košarka',2500.00,4.65,1),(12,9,'Sandra Petrović','Odbojka za rekreativce','Odbojka',2300.00,4.55,1),(13,10,'Dejan Savić','Kondicioni trening i fitness','Fudbal',2000.00,4.40,1),(14,10,'Ana Todorović','Tenis za početnike','Tenis',2400.00,4.70,1),(15,3,'Goran Stanković','Teniska škola za sve uzraste','Tenis',2300.00,4.50,1),(16,3,'Marina Jović','Košarkaški treninzi za mlade','Košarka',2600.00,4.60,1),(17,4,'Dejan Petrović','Tenis za početnike i napredne','Tenis',1800.00,4.40,1),(18,4,'Ana Nikolić','Odbojka za rekreativce','Odbojka',1500.00,4.30,1),(19,5,'Milan Todorović','Fudbalska kondicija','Fudbal',1700.00,4.35,1),(20,5,'Sandra Milovanović','Košarkaška tehnika','Košarka',2000.00,4.55,1),(21,7,'Milan Radovanović','Teniska škola za početnike i napredne','Tenis',2500.00,4.60,1),(22,7,'Jelena Petrović','Košarkaški treninzi i taktika','Košarka',3000.00,4.75,1),(23,7,'Nikola Simić','Fudbalska kondicija i tehnika','Fudbal',2200.00,4.40,1),(24,7,'Tamara Ilić','Odbojka za decu i odrasle','Odbojka',2400.00,4.65,1),(25,13,'Zoran Vidić','Tenis za sve uzraste','Tenis',1600.00,4.30,1),(26,13,'Biljana Marković','Odbojkaški treninzi','Odbojka',1400.00,4.25,1),(27,13,'Aleksandar Lukić','Fudbalska priprema','Fudbal',1500.00,4.20,1),(28,14,'Ivana Stanković','Teniska akademija','Tenis',1900.00,4.50,1),(29,14,'Petar Jovanović','Košarka za talente','Košarka',2100.00,4.55,1),(30,17,'Nemanja Petrović','Tenis i kondicija','Tenis',2400.00,4.50,1),(31,17,'Milica Antić','Košarka i rad na tehnici','Košarka',2700.00,4.60,1),(32,17,'Stefan Đorđević','Fudbalska taktika','Fudbal',2000.00,4.35,1),(33,17,'Jelena Zorić','Odbojka za sve nivoe','Odbojka',1800.00,4.40,1),(34,1,'Milan Ragbić','Ragbi za početnike i napredne','Ragbi',2800.00,4.60,1),(43,7,'Test Trener','TestSport specijalista','TestSport',1500.00,4.00,1);
/*!40000 ALTER TABLE `coaches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `courts`
--

DROP TABLE IF EXISTS `courts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `facility_id` bigint NOT NULL,
  `court_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sport_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `capacity` int NOT NULL DEFAULT '1',
  `equipment_description` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CLOSED',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_court_per_facility` (`facility_id`,`court_name`),
  CONSTRAINT `courts_ibfk_1` FOREIGN KEY (`facility_id`) REFERENCES `facilities` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courts`
--

LOCK TABLES `courts` WRITE;
/*!40000 ALTER TABLE `courts` DISABLE KEYS */;
INSERT INTO `courts` VALUES (1,1,'Glavna Hala Parket','Košarka',1,NULL,'CLOSED'),(2,1,'Mala Sala','Odbojka',1,NULL,'CLOSED'),(3,2,'Betonski Teren za Basket','Košarka',1,NULL,'CLOSED'),(4,2,'Teniski Šljakasti Teren','Tenis',1,NULL,'CLOSED'),(5,5,'Otvoreni teren 1','Košarka',4,'Profesionalni kosevi, gumirana podloga.','OPENED'),(6,5,'Zatvorena Hala B','Fudbal',12,'Vrhunska vestacka trava sa grejanjem i osvetljenjem.','CLOSED'),(7,6,'Glavni košarkaški teren','Košarka',12,'Profesionalni koševi, semafor, svlačionice','CLOSED'),(8,6,'Pomoćni teren za odbojku','Odbojka',12,'Mreža za odbojku, profesionalna podloga','OPENED'),(9,6,'Teren za Mali Fudbal','Fudbal',10,'Vrhunska veštačka trava, mantinele i profesionalni golovi.','CLOSED'),(10,7,'OPEN Court 1','TestSport',20,'Goals, nets','OPENED'),(11,7,'Indoor Court A','TestSport',10,'Balls','CLOSED'),(12,7,'Indoor Court B','TestSport',12,'Nets','CLOSED'),(14,9,'Terena Glavna','Fudbal',12,'Golovi, mreže, reflektori','OPENED'),(15,9,'Dvorana A','Košarka',100,'Koševi, tribine, parket','CLOSED'),(16,10,'Sala Kondicije','Fudbal',20,'Sprave za teretanu, podloga za trening','CLOSED'),(17,10,'Teren Tenis','Tenis',8,'Mreže, linije, osvetljenje','OPENED'),(20,13,'Zemun Open Pitch','Fudbal',14,'Golovi, mreže, klupa za zamenu','OPENED'),(21,13,'Zemun Indoor Hall','Košarka',120,'Koševi, parket, elektronski semafor','CLOSED'),(22,14,'SportPlus Hall 1','Odbojka',80,'Mreže, tribine, podloga','CLOSED'),(23,14,'SportPlus Court A','Tenis',6,'Mreže, linije, osvetljenje','OPENED'),(25,14,'Court 59','Odbojka',15,'Najbolja muzika uz dnevni trening','CLOSED'),(26,4,'Sala 2','Odbojka',12,'Lopte,mreza, personalna oprema za igrace','CLOSED'),(27,1,'Ragbi teren','Ragbi',15,'...','CLOSED'),(28,16,'Glavni teren','Odbojka',4,'Vrhunski odbojkaski teren, full opremljen za profesionalne timove','OPENED'),(29,16,'Nova Sala','Ragbi',11,'Ragbi sala za rekreaciju ili profesionalne treninge','OPENED'),(30,16,'Mala sala','Fudbal',20,'Fudbalska sala za studentske treninge, opremljen sa loptama','CLOSED'),(31,17,'Nova sala','Košarka',13,'Kosevi,lopte..','OPENED'),(32,17,'Veliki teren','Fudbal',15,'Full opremljen teren za fudbal','CLOSED');
/*!40000 ALTER TABLE `courts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `equipment`
--

DROP TABLE IF EXISTS `equipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sport_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `stock_quantity` int NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `sport_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_equipment_sport` (`sport_type`),
  KEY `fk_sport` (`sport_id`),
  CONSTRAINT `fk_sport` FOREIGN KEY (`sport_id`) REFERENCES `sports` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `equipment`
--

LOCK TABLES `equipment` WRITE;
/*!40000 ALTER TABLE `equipment` DISABLE KEYS */;
INSERT INTO `equipment` VALUES (7,'Performance flasica','/uploads/equipment/eq_87e34b8e.jpg','Tenis',1500.00,5,'Voda napaja vaše mišiće i hladi vas dok trenirate. Uz ovu Performance bocu u ruci, lako je da ostanete hidrirani.',NULL),(8,'Loptica','/uploads/equipment/eq_a86ea8cd.jpg','Tenis',550.00,4,'Life begins with a serve.',NULL),(9,'Tennis Racket','/uploads/equipment/eq_7758aeb7.jpg','Tenis',20000.00,2,'Wilson Federer 21\" Green Junior Tennis Racket',NULL),(10,'Lopta','/uploads/equipment/eq_c1c0eb25.jpg','Fudbal',4450.00,5,'...',NULL),(11,'Patike','/uploads/equipment/eq_53d794fc.jpg','Fudbal',16500.00,1,'...',NULL),(12,'Knee pads','/uploads/equipment/eq_5e9501c4.jpg','Odbojka',2000.00,3,'nikee',NULL),(13,'Lopta','/uploads/equipment/eq_5975d58a.jpg','Odbojka',5500.00,1,'...',NULL),(14,'Lopta 2','/uploads/equipment/eq_5b028282.jpg','Fudbal',4300.00,0,'...',NULL),(15,'Gilbert ball','/uploads/equipment/eq_b7fe143f.jpg','Ragbi',5500.00,2,'...',NULL),(16,'Ball','/uploads/equipment/eq_f3f66e57.jpg','Košarka',6900.00,4,'...',NULL),(17,'Knee pads','/uploads/equipment/eq_2ec3b128.jpg','Košarka',3400.00,3,'...',NULL),(18,'Basketball rim','/uploads/equipment/eq_5baebe21.jpg','Košarka',24000.00,3,'...',NULL);
/*!40000 ALTER TABLE `equipment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facilities`
--

DROP TABLE IF EXISTS `facilities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facilities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `city` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `price_per_hour` decimal(10,2) NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_by` bigint DEFAULT NULL,
  `company_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `company_address` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `company_id_number` varchar(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  `pib` varchar(9) COLLATE utf8mb4_unicode_ci NOT NULL,
  `work_start_time` varchar(5) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '08:00',
  `work_end_time` varchar(5) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '22:00',
  `max_no_shows` int NOT NULL DEFAULT '3',
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  CONSTRAINT `facilities_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facilities`
--

LOCK TABLES `facilities` WRITE;
/*!40000 ALTER TABLE `facilities` DISABLE KEYS */;
INSERT INTO `facilities` VALUES (1,'Sport Eko Centar','Beograd','Braće Jerković 119a','Vrhunska hala za dvoranske sportove sa sertifikovanim parketom.',3500.00,'CLOSED','APPROVED',NULL,'Sport Eko Centar DOO','Vojvode Šupljikca 12, Beograd','17234567','101234567','08:00','22:00',3),(2,'Olimp Otvoreni Tereni','Beograd','Vjekoslava Kovača 11','Otvoreni tereni za košarku i tenis sa veštačkom travom.',2000.00,'OPEN','APPROVED',NULL,'Olimp Sport DOO','Bulevar Oslobođenja 45, Beograd','20876543','108765432','08:00','22:00',3),(3,'Novi Nepotvrđeni Objekat','Niš','Bulevar Nemanjića bb','Objekat koji čeka odobrenje administratora.',2500.00,'CLOSED','APPROVED',NULL,'Nis Sport Centar DOO','Obrenovićeva 5, Niš','21112233','109988776','08:00','22:00',3),(4,'Mali Tas','Beograd','Beogradska 4','Sjajni tereni za tenis',1500.00,'OPENED','APPROVED',NULL,'Tas d.o.o.','Kralja Milana 10','12345678','102345678','08:00','22:00',3),(5,'Mali Tas','Beograd','Beogradska 12','Sportski kompleks u srcu grada.',1500.00,'SPORTS_CENTER','APPROVED',12,'Sport d.o.o.','Ruzveltova 5','12345678','102345678','08:00','22:00',3),(6,'Dynamic Sport Center','Beograd','Dunavska 62','Moderan sportski kompleks sa multifunkcionalnim terenima.',4000.00,'CLOSED','APPROVED',NULL,'Dynamic Sport DOO','Dunavska 62, Beograd','20123456','105678912','07:00','23:00',3),(7,'MultiCourt Facility','Niš','Petra Petrovica 12','Facility with multiple courts for testing.',2000.00,'CLOSED','APPROVED',NULL,'MultiSport DOO','Petra Petrovica 12, Niš','33112255','223344556','07:00','23:00',3),(9,'Sportski Centar Meridian','Beograd','Bulevar Zorana Đinđića 15','Kompletan centar sa otvorenim terenima i dvoranama za timske sportove.',1500.00,'SPORT_CENTER','APPROVED',12,'Meridian DOO','Bulevar Poslovni 1','87654321','123456780','07:00','23:00',3),(10,'Trening Akademija Nova','Novi Sad','Trening 3','Akademija za vrhunske treninge i kondiciju.',900.00,'TRAINING_CENTER','APPROVED',12,'Akademija Nova DOO','Sportska 7','23456789','987654321','06:00','22:00',3),(13,'Centar Rekreativa Zemun','Beograd','Zemun Plaza 2','Rekreativni centar sa otvorenim i zatvorenim terenima za lokalne klubove.',1100.00,'SPORT_CENTER','APPROVED',12,'Rekreativa Zemun DOO','Trg Sporta 4','34567890','112233445','08:00','22:00',3),(14,'Akademija SportPlus','Novi Sad','SportPlus 10','Akademija sa fokusom na razvoj talenata i individualne treninge.',1000.00,'TRAINING_CENTER','APPROVED',12,'SportPlus DOO','Trg Treninga 1','45678901','554433221','06:00','21:00',3),(16,'Sport EKO 59','Beograd','Bulevar Milutina Milankovica 5','Novi eko centar 59, G deo',1100.00,'CLOSED','PENDING',30,'Sport Eko Centar DOO','Vojvode Šupljikca 12, Beograd','17234567','101234567','08:00','22:00',3),(17,'MultiCentar_Sport','Beograd','Batutova 2','Savrseno mesto za fizicku aktivnost',2500.00,'CLOSED','APPROVED',25,'MultiSport DOO','Petra Petrovica 12, Niš','33112255','223344556','08:00','20:00',3);
/*!40000 ALTER TABLE `facilities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facility_employees`
--

DROP TABLE IF EXISTS `facility_employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facility_employees` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `facility_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_facility_user` (`facility_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `facility_employees_ibfk_1` FOREIGN KEY (`facility_id`) REFERENCES `facilities` (`id`) ON DELETE CASCADE,
  CONSTRAINT `facility_employees_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facility_employees`
--

LOCK TABLES `facility_employees` WRITE;
/*!40000 ALTER TABLE `facility_employees` DISABLE KEYS */;
INSERT INTO `facility_employees` VALUES (16,1,30),(19,1,34),(4,4,12),(5,4,13),(6,5,12),(15,7,25),(11,9,12),(12,10,12),(13,13,12),(14,14,12),(17,16,30),(18,17,25);
/*!40000 ALTER TABLE `facility_employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facility_images`
--

DROP TABLE IF EXISTS `facility_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facility_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `facility_id` bigint NOT NULL,
  `image_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_facility_images_facility` (`facility_id`),
  CONSTRAINT `facility_images_ibfk_1` FOREIGN KEY (`facility_id`) REFERENCES `facilities` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facility_images`
--

LOCK TABLES `facility_images` WRITE;
/*!40000 ALTER TABLE `facility_images` DISABLE KEYS */;
INSERT INTO `facility_images` VALUES (1,1,'https://picsum.photos/seed/sporteko1/600/400'),(2,1,'https://picsum.photos/seed/sporteko2/600/400'),(3,1,'https://picsum.photos/seed/sporteko3/600/400'),(4,2,'https://picsum.photos/seed/olimp1/600/400'),(5,2,'https://picsum.photos/seed/olimp2/600/400'),(6,2,'https://picsum.photos/seed/olimp3/600/400'),(7,5,'https://picsum.photos/seed/malitas1/600/400'),(8,5,'https://picsum.photos/seed/malitas2/600/400'),(9,5,'https://picsum.photos/seed/malitas3/600/400'),(10,6,'https://picsum.photos/seed/dynamic1/600/400'),(11,6,'https://picsum.photos/seed/dynamic2/600/400'),(12,6,'https://picsum.photos/seed/dynamic3/600/400'),(13,9,'https://picsum.photos/seed/meridian1/600/400'),(14,9,'https://picsum.photos/seed/meridian2/600/400'),(15,9,'https://picsum.photos/seed/meridian3/600/400'),(16,10,'https://picsum.photos/seed/akademija1/600/400'),(17,10,'https://picsum.photos/seed/akademija2/600/400'),(18,10,'https://picsum.photos/seed/akademija3/600/400'),(19,13,'https://picsum.photos/seed/zemun1/600/400'),(20,13,'https://picsum.photos/seed/zemun2/600/400'),(21,13,'https://picsum.photos/seed/zemun3/600/400'),(22,14,'https://picsum.photos/seed/sportplus1/600/400'),(23,14,'https://picsum.photos/seed/sportplus2/600/400'),(24,14,'https://picsum.photos/seed/sportplus3/600/400'),(25,1,'https://picsum.photos/seed/sporteko1/600/400'),(26,1,'https://picsum.photos/seed/sporteko2/600/400'),(27,1,'https://picsum.photos/seed/sporteko3/600/400'),(28,2,'https://picsum.photos/seed/olimp1/600/400'),(29,2,'https://picsum.photos/seed/olimp2/600/400'),(30,2,'https://picsum.photos/seed/olimp3/600/400'),(31,5,'https://picsum.photos/seed/malitas1/600/400'),(32,5,'https://picsum.photos/seed/malitas2/600/400'),(33,5,'https://picsum.photos/seed/malitas3/600/400'),(34,6,'https://picsum.photos/seed/dynamic1/600/400'),(35,6,'https://picsum.photos/seed/dynamic2/600/400'),(36,6,'https://picsum.photos/seed/dynamic3/600/400'),(37,9,'https://picsum.photos/seed/meridian1/600/400'),(38,9,'https://picsum.photos/seed/meridian2/600/400'),(39,9,'https://picsum.photos/seed/meridian3/600/400'),(40,10,'https://picsum.photos/seed/akademija1/600/400'),(41,10,'https://picsum.photos/seed/akademija2/600/400'),(42,10,'https://picsum.photos/seed/akademija3/600/400'),(43,13,'https://picsum.photos/seed/zemun1/600/400'),(44,13,'https://picsum.photos/seed/zemun2/600/400'),(45,13,'https://picsum.photos/seed/zemun3/600/400'),(46,14,'https://picsum.photos/seed/sportplus1/600/400'),(47,14,'https://picsum.photos/seed/sportplus2/600/400'),(48,14,'https://picsum.photos/seed/sportplus3/600/400');
/*!40000 ALTER TABLE `facility_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facility_ratings`
--

DROP TABLE IF EXISTS `facility_ratings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facility_ratings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `facility_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `is_like` tinyint(1) NOT NULL,
  `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_facility_ratings_facility` (`facility_id`),
  KEY `idx_facility_ratings_user` (`user_id`),
  CONSTRAINT `facility_ratings_ibfk_1` FOREIGN KEY (`facility_id`) REFERENCES `facilities` (`id`) ON DELETE CASCADE,
  CONSTRAINT `facility_ratings_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facility_ratings`
--

LOCK TABLES `facility_ratings` WRITE;
/*!40000 ALTER TABLE `facility_ratings` DISABLE KEYS */;
INSERT INTO `facility_ratings` VALUES (1,1,2,1,'Odlična hala, sve preporuke!','2026-07-02 23:55:41'),(2,5,24,1,'dobro mesto brt','2026-07-03 14:48:31'),(3,10,20,1,'very good','2026-07-03 18:27:36'),(4,14,21,0,'meh','2026-07-03 18:31:08'),(5,1,29,1,'bas odlican prostor','2026-07-04 03:58:25'),(6,1,29,1,'sve najbolje','2026-07-04 03:58:33'),(7,1,23,1,'Ragbiiiiii','2026-07-07 07:44:35'),(8,1,28,0,'ac ne radi','2026-07-07 07:47:17');
/*!40000 ALTER TABLE `facility_ratings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `no_shows`
--

DROP TABLE IF EXISTS `no_shows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `no_shows` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `facility_id` bigint NOT NULL,
  `count` int NOT NULL DEFAULT '0',
  `is_blocked` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_user_facility` (`user_id`,`facility_id`),
  KEY `no_shows_ibfk_2` (`facility_id`),
  CONSTRAINT `no_shows_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `no_shows_ibfk_2` FOREIGN KEY (`facility_id`) REFERENCES `facilities` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `no_shows`
--

LOCK TABLES `no_shows` WRITE;
/*!40000 ALTER TABLE `no_shows` DISABLE KEYS */;
/*!40000 ALTER TABLE `no_shows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `equipment_id` bigint NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `discount_percent` int DEFAULT '0',
  `final_price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `equipment_id` (`equipment_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`equipment_id`) REFERENCES `equipment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (9,7,7,1,1500.00,15,1275.00),(10,8,9,1,20000.00,15,17000.00),(11,8,7,1,1500.00,15,1275.00),(12,9,8,4,550.00,15,467.50),(14,11,9,1,20000.00,50,10000.00),(15,12,7,1,1500.00,50,750.00),(16,13,11,1,16500.00,30,11550.00),(17,14,14,1,4300.00,30,3010.00),(18,15,13,1,5500.00,0,5500.00),(19,16,11,1,16500.00,30,11550.00),(20,17,12,1,2000.00,0,2000.00),(21,17,13,1,5500.00,0,5500.00),(22,18,12,3,2000.00,0,2000.00),(23,19,11,1,16500.00,30,11550.00),(24,20,15,1,5500.00,0,5500.00),(25,21,17,2,3400.00,0,3400.00);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `order_date` datetime NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (4,23,'2026-07-03 02:18:30',7500.00,'DELIVERED'),(5,20,'2026-07-03 02:36:03',7000.00,'DELIVERED'),(6,21,'2026-07-03 03:01:57',16650.00,'ORDERED'),(7,24,'2026-07-03 04:17:04',1275.00,'DELIVERED'),(8,21,'2026-07-03 04:25:03',18275.00,'DELIVERED'),(9,24,'2026-07-03 04:44:06',1870.00,'CANCELLED'),(10,21,'2026-07-03 14:39:54',6200.00,'DELIVERED'),(11,24,'2026-07-03 17:55:13',10000.00,'DELIVERED'),(12,29,'2026-07-04 03:31:25',750.00,'DELIVERED'),(13,24,'2026-07-04 03:51:29',11550.00,'DELIVERED'),(14,29,'2026-07-04 03:55:18',3010.00,'DELIVERED'),(15,29,'2026-07-04 03:55:27',5500.00,'DELIVERED'),(16,33,'2026-07-07 07:57:06',11550.00,'CANCELLED'),(17,33,'2026-07-07 07:57:15',7500.00,'ORDERED'),(18,26,'2026-07-07 07:58:30',6000.00,'DELIVERED'),(19,28,'2026-07-07 08:51:02',11550.00,'ORDERED'),(20,27,'2026-07-07 09:48:17',5500.00,'ORDERED'),(21,27,'2026-07-07 09:48:30',6800.00,'ORDERED');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_tokens`
--

DROP TABLE IF EXISTS `password_reset_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_at` timestamp NOT NULL,
  `used` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_token` (`token`),
  KEY `pwreset_ibfk_1` (`user_id`),
  CONSTRAINT `pwreset_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_tokens`
--

LOCK TABLES `password_reset_tokens` WRITE;
/*!40000 ALTER TABLE `password_reset_tokens` DISABLE KEYS */;
INSERT INTO `password_reset_tokens` VALUES (1,29,'6fd1ba22906049acb9f32de9f68e61ae','2026-07-04 01:20:08','2026-07-04 01:50:08',1);
/*!40000 ALTER TABLE `password_reset_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promotions`
--

DROP TABLE IF EXISTS `promotions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promotions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `discount_percent` int NOT NULL,
  `sport_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promotions`
--

LOCK TABLES `promotions` WRITE;
/*!40000 ALTER TABLE `promotions` DISABLE KEYS */;
INSERT INTO `promotions` VALUES (2,'Vikend ludilo',30,'Fudbal','2026-01-01','2026-12-31',1),(3,'Teniski početak',10,'Tenis','2026-01-01','2026-12-31',1),(4,'Festival sezona',15,'Tenis','2026-07-03','2026-08-03',1),(6,'LETO',50,'Tenis','2026-05-06','2026-07-08',1),(7,'Void',60,'Odbojka','2026-08-08','2026-09-08',1);
/*!40000 ALTER TABLE `promotions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservations`
--

DROP TABLE IF EXISTS `reservations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `court_id` bigint NOT NULL,
  `reservation_date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `court_id` (`court_id`),
  CONSTRAINT `reservations_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `reservations_ibfk_2` FOREIGN KEY (`court_id`) REFERENCES `courts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservations`
--

LOCK TABLES `reservations` WRITE;
/*!40000 ALTER TABLE `reservations` DISABLE KEYS */;
INSERT INTO `reservations` VALUES (1,2,1,'2026-06-28','15:00:00','16:00:00','CONFIRMED'),(2,2,3,'2026-08-10','19:00:00','20:00:00','CONFIRMED'),(3,4,1,'2026-06-23','13:00:00','14:00:00','CONFIRMED'),(4,2,4,'2026-06-26','10:00:00','11:00:00','PENDING'),(5,4,4,'2026-06-26','12:00:00','13:00:00','COMPLETED'),(6,2,5,'2026-06-27','17:00:00','18:00:00','CONFIRMED'),(8,4,5,'2026-06-25','17:40:00','18:40:00','CONFIRMED'),(12,23,2,'2026-07-03','09:00:00','10:00:00','CONFIRMED'),(13,23,6,'2026-07-04','11:00:00','12:00:00','CONFIRMED'),(14,20,17,'2026-07-04','09:00:00','10:00:00','CONFIRMED'),(15,20,17,'2026-07-11','08:00:00','09:00:00','CONFIRMED'),(16,20,3,'2026-07-05','11:00:00','12:00:00','PENDING'),(17,20,3,'2026-07-03','12:00:00','13:00:00','PENDING'),(18,24,6,'2026-07-04','14:00:00','15:00:00','CONFIRMED'),(19,24,20,'2026-07-05','18:00:00','19:00:00','CONFIRMED'),(20,21,10,'2026-07-04','12:00:00','13:00:00','CONFIRMED'),(21,21,12,'2026-07-04','08:00:00','09:00:00','CONFIRMED'),(22,21,23,'2026-07-10','10:00:00','11:00:00','CONFIRMED'),(23,21,26,'2026-07-03','12:00:00','13:00:00','CONFIRMED'),(24,20,16,'2026-07-04','13:00:00','14:00:00','CONFIRMED'),(25,20,16,'2026-07-02','18:00:00','19:00:00','CONFIRMED'),(26,20,16,'2026-07-02','19:00:00','20:00:00','CONFIRMED'),(27,21,25,'2026-07-03','19:00:00','20:00:00','CONFIRMED'),(28,21,1,'2026-07-05','13:00:00','14:00:00','CONFIRMED'),(29,21,12,'2026-07-04','07:00:00','08:00:00','CONFIRMED'),(30,21,12,'2026-07-03','20:00:00','21:00:00','CONFIRMED'),(31,21,12,'2026-07-04','19:00:00','20:00:00','CONFIRMED'),(32,20,21,'2026-07-05','19:00:00','20:00:00','CONFIRMED'),(33,20,21,'2026-07-04','21:00:00','22:00:00','CONFIRMED'),(34,20,20,'2026-07-04','15:00:00','16:00:00','CONFIRMED'),(35,20,10,'2026-07-04','20:00:00','21:00:00','CONFIRMED'),(36,29,1,'2026-07-01','15:00:00','16:00:00','CONFIRMED'),(37,29,1,'2026-07-05','18:00:00','19:00:00','CONFIRMED'),(38,24,5,'2026-07-04','17:00:00','18:00:00','CONFIRMED'),(39,29,1,'2026-07-02','18:00:00','19:00:00','CONFIRMED'),(40,29,1,'2026-07-05','19:00:00','20:00:00','CONFIRMED'),(41,24,1,'2026-07-05','21:00:00','22:00:00','CONFIRMED'),(42,24,1,'2026-06-30','10:00:00','11:00:00','CONFIRMED'),(43,28,27,'2026-07-05','19:00:00','20:00:00','CONFIRMED'),(44,28,27,'2026-07-04','19:00:00','20:00:00','CONFIRMED'),(45,23,1,'2026-07-08','20:00:00','21:00:00','CONFIRMED'),(46,23,1,'2026-07-10','11:00:00','12:00:00','CONFIRMED'),(47,23,1,'2026-07-06','14:00:00','15:00:00','CONFIRMED'),(48,23,27,'2026-07-10','18:00:00','19:00:00','CONFIRMED'),(49,23,27,'2026-07-08','14:00:00','15:00:00','CONFIRMED'),(50,23,27,'2026-07-06','19:00:00','20:00:00','CONFIRMED'),(51,28,27,'2026-07-09','15:00:00','16:00:00','CONFIRMED'),(52,28,27,'2026-07-11','15:00:00','16:00:00','CONFIRMED'),(53,28,2,'2026-07-09','11:00:00','12:00:00','CONFIRMED'),(54,33,12,'2026-07-07','10:00:00','11:00:00','CONFIRMED'),(55,33,12,'2026-07-08','19:00:00','20:00:00','CONFIRMED'),(56,33,12,'2026-07-10','18:00:00','19:00:00','CONFIRMED'),(57,33,12,'2026-07-12','13:00:00','14:00:00','CONFIRMED'),(58,26,11,'2026-07-10','13:00:00','14:00:00','CONFIRMED'),(59,26,11,'2026-07-11','19:00:00','20:00:00','CONFIRMED'),(60,26,11,'2026-07-12','22:00:00','23:00:00','CONFIRMED'),(61,26,12,'2026-07-09','12:00:00','13:00:00','CONFIRMED'),(62,26,12,'2026-07-11','18:00:00','19:00:00','CONFIRMED'),(63,31,3,'2026-07-08','13:00:00','14:00:00','PENDING'),(64,31,3,'2026-07-09','19:00:00','20:00:00','PENDING'),(65,31,4,'2026-07-09','13:00:00','14:00:00','PENDING'),(66,31,4,'2026-07-07','18:00:00','19:00:00','PENDING'),(67,31,4,'2026-07-07','11:00:00','12:00:00','PENDING'),(68,27,1,'2026-07-07','17:00:00','18:00:00','CONFIRMED'),(69,27,32,'2026-07-09','11:00:00','12:00:00','CONFIRMED'),(70,27,32,'2026-07-08','18:00:00','19:00:00','CONFIRMED'),(71,27,32,'2026-07-12','19:00:00','20:00:00','CONFIRMED');
/*!40000 ALTER TABLE `reservations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sports`
--

DROP TABLE IF EXISTS `sports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sports` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sports`
--

LOCK TABLES `sports` WRITE;
/*!40000 ALTER TABLE `sports` DISABLE KEYS */;
INSERT INTO `sports` VALUES (1,'Fudbal','Timski sport sa loptom'),(2,'Košarka','Sport sa košem i loptom'),(3,'Tenis','Sport sa reketom i loptom'),(4,'Odbojka','Timski sport'),(5,'TestSport','Test sport entry'),(6,'Ragbi','Ragbi je uzbudljiv i dinamičan ekipni sport koji se igra sa ovalnom loptom . Dve najpopularnije verzije su ragbi 15 (unija) i ragbi 13 (liga), a igra se i brža verzija sa sedam igrača (ragbi 7) . U Srbiji, takmičenja organizuju Ragbi Savez Srbije i Ragbi 13 federacije , a ljubitelji mogu pratiti domaća prvenstva i nastupe nacionalnih timova .');
/*!40000 ALTER TABLE `sports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teammate_ad_responses`
--

DROP TABLE IF EXISTS `teammate_ad_responses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teammate_ad_responses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ad_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ad_id` (`ad_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `teammate_ad_responses_ibfk_1` FOREIGN KEY (`ad_id`) REFERENCES `teammate_ads` (`id`) ON DELETE CASCADE,
  CONSTRAINT `teammate_ad_responses_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teammate_ad_responses`
--

LOCK TABLES `teammate_ad_responses` WRITE;
/*!40000 ALTER TABLE `teammate_ad_responses` DISABLE KEYS */;
INSERT INTO `teammate_ad_responses` VALUES (1,1,2,'PENDING'),(3,1,23,'PENDING'),(7,5,24,'APPROVED'),(8,5,21,'APPROVED'),(9,7,23,'APPROVED'),(10,5,23,'PENDING'),(11,6,23,'APPROVED'),(12,1,21,'PENDING'),(13,6,21,'APPROVED'),(14,8,20,'APPROVED'),(15,6,20,'PENDING'),(16,1,24,'PENDING');
/*!40000 ALTER TABLE `teammate_ad_responses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teammate_ads`
--

DROP TABLE IF EXISTS `teammate_ads`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teammate_ads` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `creator_id` bigint NOT NULL,
  `sport` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `city` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `event_date` date NOT NULL,
  `event_time` time NOT NULL,
  `players_needed` int NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `creator_id` (`creator_id`),
  CONSTRAINT `teammate_ads_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teammate_ads`
--

LOCK TABLES `teammate_ads` WRITE;
/*!40000 ALTER TABLE `teammate_ads` DISABLE KEYS */;
INSERT INTO `teammate_ads` VALUES (1,4,'Kosarka','Beograd','2026-06-25','20:00:00',3,'OPEN'),(2,2,'Tenis','Beograd','2026-06-20','17:00:00',1,'CLOSED'),(5,20,'Košarka','Niš','2026-07-04','10:00:00',5,'OPEN'),(6,24,'Odbojka','Beograd','2026-07-10','11:00:00',1,'CLOSED'),(7,21,'Tenis','Beograd','2026-07-09','07:00:00',0,'CLOSED'),(8,21,'Fudbal','Novi Sad','2026-07-07','15:00:00',5,'CLOSED'),(9,33,'Ragbi','Niš','2026-08-08','14:00:00',8,'OPEN');
/*!40000 ALTER TABLE `teammate_ads` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trainings`
--

DROP TABLE IF EXISTS `trainings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trainings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `coach_id` bigint NOT NULL,
  `court_id` bigint NOT NULL,
  `training_date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `idx_trainings_coach_date` (`coach_id`,`training_date`),
  CONSTRAINT `trainings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `trainings_ibfk_2` FOREIGN KEY (`coach_id`) REFERENCES `coaches` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trainings`
--

LOCK TABLES `trainings` WRITE;
/*!40000 ALTER TABLE `trainings` DISABLE KEYS */;
INSERT INTO `trainings` VALUES (1,2,1,0,'2026-06-27','10:00:00','11:00:00','SCHEDULED'),(2,2,1,0,'2026-06-20','16:00:00','17:00:00','COMPLETED'),(6,23,1,1,'2026-07-04','18:00:00','19:00:00','SCHEDULED'),(7,23,11,15,'2026-07-17','15:00:00','16:00:00','SCHEDULED'),(8,20,14,17,'2026-07-31','14:00:00','15:00:00','SCHEDULED'),(9,20,3,3,'2026-08-03','22:00:00','23:00:00','SCHEDULED'),(10,24,10,8,'2026-07-27','09:00:00','10:00:00','SCHEDULED'),(11,21,4,1,'2026-07-24','16:00:00','17:00:00','SCHEDULED'),(12,21,10,9,'2026-08-04','08:00:00','09:00:00','SCHEDULED'),(13,21,4,2,'2026-07-17','11:00:00','12:00:00','SCHEDULED'),(14,20,14,17,'2026-07-05','10:00:00','11:00:00','SCHEDULED'),(15,29,4,2,'2026-08-08','17:00:00','18:00:00','SCHEDULED'),(16,29,1,1,'2026-08-08','08:00:00','09:00:00','SCHEDULED'),(17,21,5,2,'2026-09-08','13:00:00','14:00:00','SCHEDULED'),(18,28,5,2,'2026-10-08','09:00:00','10:00:00','SCHEDULED'),(19,33,10,9,'2026-07-08','15:00:00','16:00:00','SCHEDULED'),(20,27,7,3,'2026-07-08','10:00:00','11:00:00','SCHEDULED'),(21,28,20,5,'2026-08-08','09:00:00','10:00:00','SCHEDULED'),(22,28,21,11,'2026-07-08','15:00:00','16:00:00','SCHEDULED'),(23,28,34,27,'2026-07-08','19:00:00','20:00:00','SCHEDULED'),(24,28,32,32,'2026-10-08','14:00:00','15:00:00','SCHEDULED'),(25,28,13,16,'2026-12-08','14:00:00','15:00:00','SCHEDULED'),(26,24,32,32,'2026-05-08','09:00:00','10:00:00','SCHEDULED'),(27,24,33,31,'2026-12-08','15:00:00','17:00:00','SCHEDULED'),(28,21,23,11,'2026-07-08','10:00:00','11:00:00','SCHEDULED'),(29,21,21,12,'2026-09-07','12:00:00','13:00:00','SCHEDULED'),(30,21,23,11,'2026-08-07','10:00:00','11:00:00','SCHEDULED'),(31,20,32,32,'2026-07-07','15:00:00','16:00:00','SCHEDULED'),(32,28,31,31,'2026-09-07','20:00:00','21:00:00','SCHEDULED');
/*!40000 ALTER TABLE `trainings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `first_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `profile_picture_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `favorite_sports` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (2,'jelena','jelena123','jelena@gmail.com','Jelena','Simic','0641234567','ATHLETE','APPROVED','/uploads/profiles/jelena.jpg','Kosarka,Tenis'),(4,'marko','marko123','marko@gmail.com','Marko','Igrac','063111222','ATHLETE','APPROVED',NULL,'Fudbal'),(12,'zaposleni1','$2a$10$TnoTiUsShAlj1pnLj/VG2ekILwESrZJ3ODb28oHmH9RhZNvOB0XnC','zaposleni1@gmail.com','Marko','Markovic','0641234567','EMPLOYEE','APPROVED','/uploads/profiles/profile_zaposleni1_984fc815.jpeg',NULL),(13,'zaposleni2','$2a$10$wA5UjmJpvxhyIsycJ/tiU.dS9PSE32kx/LjVrmXmYbacGeq6958N6','zaposleni2@gmail.com','Nikolina','Nikolic','0659876543','EMPLOYEE','APPROVED','/uploads/profiles/profile_zaposleni2_a4249793.jpeg',NULL),(20,'lele','$2a$10$chX2dxhlhy6FEudOeLAeo.ohRaX1WMtK19JpZ/Eub2E3IS2N8MaXy','lola_lele2@gmail.com','LOLA','Pantic','061225697','ATHLETE','APPROVED','/uploads/profiles/profile_lele_dde050ce.jpeg','Odbojka,Tenis'),(21,'maja','$2a$10$uRnFdfRdGM7b/vqzgeznw.d3VhdKJPFO3GSnAi8sWorH/fZ3dmg4C','maja.summer@gamil.com','Marija','Lazarevic','062335862','ATHLETE','APPROVED','/uploads/profiles/profile_maja_3f3bf4d8.jpeg','Odbojka,Tenis'),(22,'admin2','$2b$12$CLjDsF4Wgh1yrCNkk/l2dORoXFwPLUOsPaNTIudpYEGEYEl041/o.','admin2@example.com','Admin','Test','+38160000000','ADMIN','APPROVED',NULL,NULL),(23,'monster','$2a$10$PpN7qh0Op2cehjQmvbGfkOce3fb/weRoTfIsqcaVnoMPjSn0FHZtS','energy@solit.com','Lando','Noriss','0652284848','ATHLETE','APPROVED','/uploads/profiles/profile_monster_cb99b310.jpeg','Tenis'),(24,'cuna','$2a$10$m49JFznp4AuWLfCW/t0gauSJSnDXBIKNwAaoor0slfrpgJDVAHiX.','cuna@mi.com','Kosta','Peric','0658822612','ATHLETE','APPROVED','/uploads/profiles/profile_cuna_0030b5f8.jpeg','Košarka,Odbojka'),(25,'worker1','$2a$10$iBQnIVw/gV0reomRlTZEsOTxDAUElg5dSmj.x606B6D15vStHRa5K','saul@goodman.com','Jimmy','Vexler','055682579','EMPLOYEE','APPROVED','/uploads/profiles/profile_worker1_cbac1ce8.jpeg',NULL),(26,'saint','$2a$10$od/yGbj50A4ZuOrOTlxns.lZ1bkrv4PwhlU9iVsOCckXyMrAfOFYa','saintBig555@gmail.com','Sasa','Nestorovic','056685625','ATHLETE','APPROVED',NULL,'Košarka'),(27,'gregor','$2a$10$MrBs0hQUhyBVfmd4qq4NEOBtmkaTf.WeDcl.9ucsSRg8wSZvACnIW','gregiGregor@gamil.com','Gregor','Jovic','0652234786','ATHLETE','APPROVED','/uploads/profiles/profile_gregor_95ea9834.jpeg','Fudbal,Odbojka'),(28,'joca','$2a$10$Z6MxV6pf/BNdAdqPkDJMLeAfjfKZD.jkwqvRJJ3pOV3uC0bWS3NE6','JovanRistic25@gmail.com','Jovan','Ristic','0145258823','ATHLETE','APPROVED',NULL,'Odbojka'),(29,'levi','$2a$10$1xNukObg1SFUwvZlNqkVYu9ztjokkUaqnI8KBkRn8Gb981wUWstWO','LeviGOD@gmail.com','Levii','Ac','065339875','ATHLETE','APPROVED','/uploads/profiles/profile_levi_e55fe63a.png','Fudbal,Košarka,Ragbi'),(30,'worker3','$2a$10$uV9wUoXCN33XKR1YoixYeeRD.AtNZZvfhyzQVb2c8Pg/vZXGms.Pm','anastasija777@gmail.com','Anastasija','Pantic','056752862','EMPLOYEE','APPROVED','/uploads/profiles/profile_worker3_88156aa7.jpeg',NULL),(31,'john','$2a$10$ITngZLJWyB..DSyVVRr7Yu273AAF0J70/cYYyZvBgeTXLWkmkAQXW','JohnSnow@gmail.com','Jovan','Beli','056582622','ATHLETE','APPROVED','/uploads/profiles/profile_john_22b0c524.png','Ragbi,Tenis'),(32,'zika','$2a$10$CGklHW4sRxN6pyj6EZ.CCe4YeZJltQ3J9ccgYfg9DfZ3gPmkkql8O','ZzzNmg@gmail.com','Zika','Zikic','890522685','ATHLETE','PENDING','/uploads/profiles/profile_zika_571eb001.jpeg','Fudbal,Odbojka,Tenis,Ragbi'),(33,'kosta','$2a$10$LFaGEmTOneTRskb1zFT58ejUIjObkEaXrQVzHq6WPu1DxWBRW9.v6','kolehouse@gmail.com','Kosta','Stefanovic','556328202','ATHLETE','APPROVED','/uploads/profiles/profile_kosta_3f3799e5.jpeg','Odbojka,Tenis,Ragbi'),(34,'branka','$2a$10$qILmZXpQbW2vaktbApfD0eiz3FBJSl/vSwEO87HrNgRapcu.eUOg.','bbbb@gmail.com','Branka','Lazic','069745584','EMPLOYEE','APPROVED','/uploads/profiles/profile_branka_1f329d85.jpeg',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-07 15:11:40
