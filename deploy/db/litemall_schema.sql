drop database if exists packageapp;
drop user if exists 'packageapp'@'%';
-- 支持emoji：需要mysql数据库参数： character_set_server=utf8mb4
create database packageapp default character set utf8mb4 collate utf8mb4_unicode_ci;
use packageapp;
create user 'packageapp'@'%' identified by 'packageapp123456';
grant all privileges on packageapp.* to 'packageapp'@'%';
flush privileges;