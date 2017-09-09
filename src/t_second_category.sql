/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50719
 Source Host           : localhost:3306
 Source Schema         : bookstore

 Target Server Type    : MySQL
 Target Server Version : 50719
 File Encoding         : 65001

 Date: 09/09/2017 12:09:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_second_category
-- ----------------------------
DROP TABLE IF EXISTS `t_second_category`;
CREATE TABLE `t_second_category` (
  `category_id` int(11) DEFAULT NULL,
  `second_category_id` int(11) NOT NULL,
  `second_category_name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`second_category_id`),
  KEY `t_second_category_ibfk_1` (`category_id`),
  CONSTRAINT `t_second_category_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `t_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
