-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: healthy_data
-- ------------------------------------------------------
-- Server version	8.0.40

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
-- Table structure for table `blood_sugar`
--

DROP TABLE IF EXISTS `blood_sugar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blood_sugar` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `blood_sugar` double DEFAULT NULL,
  `measure_time` datetime(6) DEFAULT NULL,
  `warning_status` varchar(255) DEFAULT NULL,
  `meal_status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `blood_sugar_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `body_composition`
--

DROP TABLE IF EXISTS `body_composition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `body_composition` (
  `record_id` bigint NOT NULL AUTO_INCREMENT,
  `age` int NOT NULL,
  `bmi` double NOT NULL,
  `body_fat` double NOT NULL,
  `height` double NOT NULL,
  `muscle_mass` double DEFAULT NULL,
  `record_date` datetime(6) DEFAULT NULL,
  `weight` double NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`record_id`),
  KEY `FKhvwyoptcsaf4sq47khweil2rx` (`user_id`),
  CONSTRAINT `FKhvwyoptcsaf4sq47khweil2rx` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `calorie_tracker`
--

DROP TABLE IF EXISTS `calorie_tracker`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `calorie_tracker` (
  `tracker_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `calories_intake` int DEFAULT NULL,
  `calories_burned` int DEFAULT NULL,
  `remaining_calories` int DEFAULT NULL,
  `target_calories` int DEFAULT NULL,
  `protein` double NOT NULL,
  `carbohydrates` double NOT NULL,
  `fat` double NOT NULL,
  `water_intake` int DEFAULT NULL,
  `water_target` int DEFAULT NULL,
  `record_date` date DEFAULT NULL,
  PRIMARY KEY (`tracker_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `calorie_tracker_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exercise`
--

DROP TABLE IF EXISTS `exercise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercise` (
  `id` bigint NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `exercise_type` varchar(50) DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `intensity` varchar(20) DEFAULT NULL,
  `exercise_date` timestamp NULL DEFAULT NULL,
  `calories_burned` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exercises`
--

DROP TABLE IF EXISTS `exercises`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercises` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `exercise_type` varchar(255) DEFAULT NULL,
  `duration_minutes` int DEFAULT NULL,
  `heart_rate` int DEFAULT NULL,
  `intensity` varchar(255) DEFAULT NULL,
  `calories_burned` double DEFAULT NULL,
  `exercise_date` datetime(6) DEFAULT NULL,
  `duration` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `exercises_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `health_score`
--

DROP TABLE IF EXISTS `health_score`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `health_score` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `total_score` int DEFAULT NULL,
  `bmi_score` int DEFAULT NULL,
  `blood_sugar_score` int DEFAULT NULL,
  `exercise_score` int DEFAULT NULL,
  `sleep_score` int DEFAULT NULL,
  `diet_score` int DEFAULT NULL,
  `period_type` varchar(20) DEFAULT NULL,
  `record_date` date DEFAULT NULL,
  `calories_intake` int DEFAULT NULL,
  `calories_burned` int DEFAULT NULL,
  `water_intake` double DEFAULT NULL,
  `tdee` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `health_score_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `health_scores`
--

DROP TABLE IF EXISTS `health_scores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `health_scores` (
  `score_id` bigint NOT NULL AUTO_INCREMENT,
  `activity_level` varchar(255) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `blood_sugar` double DEFAULT NULL,
  `blood_sugar_score` int DEFAULT NULL,
  `bmi` double DEFAULT NULL,
  `bmi_score` int DEFAULT NULL,
  `calories_burned` int DEFAULT NULL,
  `calories_intake` int DEFAULT NULL,
  `deep_sleep_minutes` int DEFAULT NULL,
  `diet_score` int DEFAULT NULL,
  `exercise_minutes` int DEFAULT NULL,
  `exercise_score` int DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `heart_rate` int DEFAULT NULL,
  `height` double DEFAULT NULL,
  `period_type` varchar(255) DEFAULT NULL,
  `record_date` datetime(6) DEFAULT NULL,
  `sleep_hours` double DEFAULT NULL,
  `sleep_score` int DEFAULT NULL,
  `tdee` int DEFAULT NULL,
  `total_score` int DEFAULT NULL,
  `water_intake` int DEFAULT NULL,
  `water_score` int DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`score_id`),
  KEY `FKj42a3i4f5yqlp1mwfk5el8rqm` (`user_id`),
  CONSTRAINT `FKj42a3i4f5yqlp1mwfk5el8rqm` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `meal_record`
--

DROP TABLE IF EXISTS `meal_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `meal_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `food_name` varchar(100) DEFAULT NULL,
  `portion` double DEFAULT NULL,
  `calories` int DEFAULT NULL,
  `protein` double DEFAULT NULL,
  `carbs` double DEFAULT NULL,
  `fat` double DEFAULT NULL,
  `meal_type` varchar(20) DEFAULT NULL,
  `record_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `meal_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `meal_records`
--

DROP TABLE IF EXISTS `meal_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `meal_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `calories` double NOT NULL,
  `carbs` double NOT NULL,
  `fat` double NOT NULL,
  `food_description` varchar(255) DEFAULT NULL,
  `food_id` varchar(255) DEFAULT NULL,
  `meal_type` varchar(255) DEFAULT NULL,
  `portion` double NOT NULL,
  `protein` double NOT NULL,
  `record_time` datetime(6) DEFAULT NULL,
  `serving_size` double NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKshno6uutyyeb1auy7fs9xbjb4` (`user_id`),
  CONSTRAINT `FKshno6uutyyeb1auy7fs9xbjb4` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sleep_record`
--

DROP TABLE IF EXISTS `sleep_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sleep_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `sleep_start` datetime(6) DEFAULT NULL,
  `sleep_end` datetime(6) DEFAULT NULL,
  `sleep_quality` enum('FAIR','GOOD','POOR') DEFAULT NULL,
  `deep_sleep_minutes` int DEFAULT NULL,
  `light_sleep_minutes` int NOT NULL,
  `total_sleep_hours` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `sleep_record_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sleeprecord`
--

DROP TABLE IF EXISTS `sleeprecord`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sleeprecord` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `deep_sleep_minutes` int NOT NULL,
  `light_sleep_minutes` int NOT NULL,
  `sleep_end` datetime(6) DEFAULT NULL,
  `sleep_quality` enum('FAIR','GOOD','POOR') DEFAULT NULL,
  `sleep_start` datetime(6) DEFAULT NULL,
  `total_sleep_hours` double DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKh9jhemd0b4yk556bim7fww5qg` (`user_id`),
  CONSTRAINT `FKh9jhemd0b4yk556bim7fww5qg` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `userinfo`
--

DROP TABLE IF EXISTS `userinfo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `userinfo` (
  `user_id` bigint NOT NULL,
  `height` int DEFAULT NULL,
  `weight` int DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `age` int DEFAULT NULL,
  `height` double DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `activity_level` varchar(255) DEFAULT NULL,
  `tdee` int DEFAULT NULL,
  `water_target` int DEFAULT NULL,
  `bmi` double DEFAULT NULL,
  `create_date` datetime(6) DEFAULT NULL,
  `last_login_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'healthy_data'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-17  7:24:03
