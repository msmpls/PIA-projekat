-- =====================================================
-- Migracija: Tabela za galeriju slika sportskih objekata
-- Pokrenite ovaj SQL u MySQL bazi sportsphere_db
-- =====================================================

CREATE TABLE IF NOT EXISTS `facility_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `facility_id` bigint NOT NULL,
  `image_path` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_facility_images_facility` (`facility_id`),
  CONSTRAINT `facility_images_ibfk_1` FOREIGN KEY (`facility_id`) REFERENCES `facilities` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Brisanje starih podataka (ako ponovo pokrećete migraciju)
DELETE FROM `facility_images`;

-- Uzorci slika za demo (koriste picsum.photos placeholder slike)
-- Slike se razlikuju po seed-u da bi svaka bila jedinstvena
INSERT INTO `facility_images` (`facility_id`, `image_path`) VALUES
(1, 'https://picsum.photos/seed/sporteko1/600/400'),
(1, 'https://picsum.photos/seed/sporteko2/600/400'),
(1, 'https://picsum.photos/seed/sporteko3/600/400'),
(2, 'https://picsum.photos/seed/olimp1/600/400'),
(2, 'https://picsum.photos/seed/olimp2/600/400'),
(2, 'https://picsum.photos/seed/olimp3/600/400'),
(5, 'https://picsum.photos/seed/malitas1/600/400'),
(5, 'https://picsum.photos/seed/malitas2/600/400'),
(5, 'https://picsum.photos/seed/malitas3/600/400'),
(6, 'https://picsum.photos/seed/dynamic1/600/400'),
(6, 'https://picsum.photos/seed/dynamic2/600/400'),
(6, 'https://picsum.photos/seed/dynamic3/600/400'),
(9, 'https://picsum.photos/seed/meridian1/600/400'),
(9, 'https://picsum.photos/seed/meridian2/600/400'),
(9, 'https://picsum.photos/seed/meridian3/600/400'),
(10, 'https://picsum.photos/seed/akademija1/600/400'),
(10, 'https://picsum.photos/seed/akademija2/600/400'),
(10, 'https://picsum.photos/seed/akademija3/600/400'),
(13, 'https://picsum.photos/seed/zemun1/600/400'),
(13, 'https://picsum.photos/seed/zemun2/600/400'),
(13, 'https://picsum.photos/seed/zemun3/600/400'),
(14, 'https://picsum.photos/seed/sportplus1/600/400'),
(14, 'https://picsum.photos/seed/sportplus2/600/400'),
(14, 'https://picsum.photos/seed/sportplus3/600/400');
