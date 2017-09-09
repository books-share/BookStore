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

 Date: 09/09/2017 11:04:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_book
-- ----------------------------
DROP TABLE IF EXISTS `t_book`;
CREATE TABLE `t_book` (
  `name` varchar(50) DEFAULT NULL,
  `author` varchar(50) DEFAULT NULL,
  `price` double DEFAULT NULL,
  `discount` double DEFAULT NULL,
  `publishing` varchar(50) DEFAULT NULL,
  `publish_time` varchar(50) DEFAULT NULL,
  `edition` int(11) DEFAULT NULL,
  `page_num` int(11) DEFAULT NULL,
  `isnb` varchar(50) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `second_category_id` int(11) DEFAULT NULL,
  `bid` int(11) NOT NULL AUTO_INCREMENT,
  `img_url_small` varchar(50) DEFAULT NULL,
  `img_url_big` varchar(50) DEFAULT NULL,
  `img_url_mid` varchar(50) DEFAULT NULL,
  `book_description` varchar(300) DEFAULT NULL,
  `pack` varchar(50) DEFAULT NULL,
  `book_format` varchar(50) DEFAULT NULL,
  `editor_ comment` varchar(300) DEFAULT NULL,
  `author_ description` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`bid`),
  KEY `category_id` (`category_id`),
  KEY `second_category_id` (`second_category_id`),
  CONSTRAINT `t_book_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `t_category` (`category_id`),
  CONSTRAINT `t_book_ibfk_2` FOREIGN KEY (`second_category_id`) REFERENCES `t_second_category` (`second_category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
