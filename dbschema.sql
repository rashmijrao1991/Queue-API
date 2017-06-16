CREATE DATABASE IF NOT EXISTS `tasks`;
use`tasks`;
CREATE TABLE IF NOT EXISTS `user_details` (
  `id` BIGINT(20) NOT NULL,
  `timestamp` BIGINT(15) NOT NULL,
  `priority` int(10) NOT NULL,
  `rank` double(20,5) ,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
