-- Datenbank erstellen
CREATE DATABASE IF NOT EXISTS `immatrikulation` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- DB benutzen
USE `immatrikulation`;

-- Tabelle studiengang
DROP TABLE IF EXISTS `studiengang`;

CREATE TABLE `studiengang` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Beispielstudiengänge einfügen
INSERT INTO `studiengang` (`name`) VALUES
('Informatik (B.Sc.)'),
('Wirtschaftsinformatik (B.Sc.)'),
('Betriebswirtschaftslehre (B.Sc.)'),
('Maschinenbau (B.Sc.)'),
('Elektrotechnik (B.Sc.)'),
('Medieninformatik (B.Sc.)'),
('Rechtswissenschaft (Staatsexamen)');

-- Tabelle immatrikulationsantrag
DROP TABLE IF EXISTS `immatrikulationsantrag`;

CREATE TABLE `immatrikulationsantrag` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nachname` VARCHAR(255),
  `vorname` VARCHAR(255),
  `geburtsdatum` VARCHAR(10),
  `geburtsort` VARCHAR(255),
  `staatsangehoerigkeit` VARCHAR(255),
  `adresse` TEXT,
  `email` VARCHAR(255),
  `telefonnummer` VARCHAR(50),
  `studiengang_id` INT,
  `abschluss` VARCHAR(50),
  `hochschulsemester` INT,
  `hzb_zeugnis_name` VARCHAR(255),
  `hzb_zeugnis_content` LONGBLOB,
  `krankenversicherung_name` VARCHAR(255),
  `krankenversicherung_content` LONGBLOB,
  `eingereicht_am` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`studiengang_id`) REFERENCES `studiengang`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Fertig!
