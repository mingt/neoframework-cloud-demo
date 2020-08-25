-- ahming marks: 针对 schema.sql 自动运行，先改名

-- CREATE DATABASE `auth` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
-- USE auth;

-- DROP TABLE IF EXISTS `users`;
-- CREATE TABLE `users` (
--   `username` VARCHAR(50) NOT NULL,
--   `password` VARCHAR(50) NOT NULL,
--   `enabled` TINYINT(1) NOT NULL,
--   PRIMARY KEY (`username`)
-- ) ENGINE=INNODB DEFAULT CHARSET=utf8;

-- DROP TABLE IF EXISTS `authorities`;
-- CREATE TABLE `authorities` (
--   `username` VARCHAR(50) NOT NULL,
--   `authority` VARCHAR(50) NOT NULL,
--   UNIQUE KEY `ix_auth_username` (`username`,`authority`)
-- ) ENGINE=INNODB DEFAULT CHARSET=utf8;

-- ahming notes: If anyone is having issues with INNODB / Utf-8 trying to put an UNIQUE index on a VARCHAR(256) field,
-- switch it to VARCHAR(255). It seems 255 is the limitation

-- DROP TABLE IF EXISTS `oauth_client_details`;
-- CREATE TABLE `oauth_client_details` (
--   `client_id` VARCHAR(255) NOT NULL,
--   `resource_ids` VARCHAR(255) DEFAULT NULL,
--   `client_secret` VARCHAR(255) DEFAULT NULL,
--   `scope` VARCHAR(255) DEFAULT NULL,
--   `authorized_grant_types` VARCHAR(255) DEFAULT NULL,
--   `web_server_redirect_uri` VARCHAR(255) DEFAULT NULL,
--   `authorities` VARCHAR(255) DEFAULT NULL,
--   `access_token_validity` INT(11) DEFAULT NULL,
--   `refresh_token_validity` INT(11) DEFAULT NULL,
--   `additional_information` VARCHAR(255) DEFAULT NULL,
--   `autoapprove` VARCHAR(255) DEFAULT NULL,
--   PRIMARY KEY (`client_id`)
-- ) ENGINE=INNODB DEFAULT CHARSET=utf8;