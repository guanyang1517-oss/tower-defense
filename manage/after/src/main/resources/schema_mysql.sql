CREATE DATABASE IF NOT EXISTS `td_manage` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `td_manage`;

CREATE TABLE IF NOT EXISTS `game_config` (
  `id` BIGINT NOT NULL,
  `initial_money` INT NOT NULL,
  `initial_health` INT NOT NULL,
  `kill_reward` INT NOT NULL,
  `tower_cost` INT NOT NULL,
  `tower_range` FLOAT NOT NULL,
  `blocker_cost` INT NOT NULL,
  `blocker_max_health` FLOAT NOT NULL,
  `blocker_return_damage` FLOAT NOT NULL,
  `enemy_base_speed` FLOAT NOT NULL,
  `enemy_base_health` INT NOT NULL,
  `enemy_attack_damage` FLOAT NOT NULL,
  `update_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `enemy_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `enemy_code` VARCHAR(64) NOT NULL,
  `enemy_name` VARCHAR(64) NOT NULL,
  `texture_path` VARCHAR(255) NOT NULL,
  `attack_texture_path` VARCHAR(255) DEFAULT NULL,
  `health` INT NOT NULL,
  `speed` FLOAT NOT NULL,
  `attack_damage` FLOAT NOT NULL,
  `reward` INT NOT NULL,
  `enabled` BIT NOT NULL,
  `create_time` DATETIME NOT NULL,
  `update_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_enemy_code` (`enemy_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `level_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `level_code` VARCHAR(64) NOT NULL,
  `level_name` VARCHAR(64) NOT NULL,
  `map_path` VARCHAR(255) NOT NULL,
  `max_wave` INT NOT NULL,
  `enabled` BIT NOT NULL,
  `remark` VARCHAR(255) DEFAULT NULL,
  `create_time` DATETIME NOT NULL,
  `update_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_level_code` (`level_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `wave_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `level_id` BIGINT NOT NULL,
  `wave_number` INT NOT NULL,
  `break_time` FLOAT NOT NULL,
  `create_time` DATETIME NOT NULL,
  `update_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_level_wave` (`level_id`, `wave_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `wave_spawn_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `wave_id` BIGINT NOT NULL,
  `enemy_type_id` BIGINT NOT NULL,
  `spawn_count` INT NOT NULL,
  `spawn_interval` FLOAT NOT NULL,
  `path_index` INT NOT NULL,
  `spawn_order` INT NOT NULL,
  `create_time` DATETIME NOT NULL,
  `update_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `account` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL,
  `password` VARCHAR(64) NOT NULL,
  `role` VARCHAR(16) NOT NULL,
  `enabled` BIT NOT NULL,
  `page_permissions` VARCHAR(255) DEFAULT NULL,
  `create_time` DATETIME NOT NULL,
  `update_time` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `game_config`
(`id`, `initial_money`, `initial_health`, `kill_reward`, `tower_cost`, `tower_range`, `blocker_cost`, `blocker_max_health`, `blocker_return_damage`, `enemy_base_speed`, `enemy_base_health`, `enemy_attack_damage`, `update_time`)
VALUES
(1, 600, 10, 50, 100, 250, 150, 1500, 1, 150, 10, 50, NOW())
ON DUPLICATE KEY UPDATE `update_time` = NOW();





INSERT INTO `account`
(`username`, `password`, `role`, `enabled`, `create_time`, `update_time`)
SELECT 'root', '4813494d137e1631bba301d5acab6e7bb7aa74ce1185d456565ef51d737677b2', 'ADMIN', 1, NOW(), NOW()
WHERE NOT EXISTS (
  SELECT 1 FROM `account` WHERE `username` = 'root'
);
