-- =====================================================
-- Migracija: No-show tracking + max_no_shows kolona
-- Pokrenite ovaj SQL u MySQL bazi sportsphere_db
-- =====================================================

-- 1. Dodaj kolonu max_no_shows u facilities (broj dozvoljenih nedolazaka pre blokade)
ALTER TABLE `facilities` ADD COLUMN `max_no_shows` INT NOT NULL DEFAULT 3;

-- Postavi razlicite vrednosti za postojece objekte
UPDATE `facilities` SET `max_no_shows` = 3 WHERE `max_no_shows` = 3;

-- 2. Kreiraj tabelu za pracenje nedolazaka korisnika po objektu
CREATE TABLE IF NOT EXISTS `no_shows` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `facility_id` bigint NOT NULL,
  `count` int NOT NULL DEFAULT 0,
  `is_blocked` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_user_facility` (`user_id`, `facility_id`),
  CONSTRAINT `no_shows_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `no_shows_ibfk_2` FOREIGN KEY (`facility_id`) REFERENCES `facilities` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Dodaj kolonu is_active u promotions (ako ne postoji) za lako upravljanje
ALTER TABLE `promotions` ADD COLUMN IF NOT EXISTS `is_active` tinyint(1) DEFAULT 1;

-- Provera
SELECT name, max_no_shows FROM facilities WHERE status = 'APPROVED';
