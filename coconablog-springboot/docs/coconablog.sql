-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- 主机： mysql:3306
-- 生成日期： 2026-04-15 04:38:47
-- 服务器版本： 8.0.32
-- PHP 版本： 8.2.27

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 数据库： `coconablog`
--

-- --------------------------------------------------------

--
-- 表的结构 `article`
--

CREATE TABLE `article` (
  `id` bigint NOT NULL COMMENT '文章ID，主键',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章标题',
  `slug` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'URL友好标识，唯一',
  `summary` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文章摘要',
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章内容（Markdown或HTML）',
  `cover_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图URL',
  `user_id` bigint NOT NULL COMMENT '作者ID，关联user(id)',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID，关联category(id)',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-草稿，1-已发布，2-回收站',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览次数',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数（冗余）',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '评论数（冗余）',
  `is_top` tinyint(1) NOT NULL DEFAULT '0' COMMENT '置顶：0-否，1-是',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间，NULL表示未发布',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- --------------------------------------------------------

--
-- 表的结构 `article_tag`
--

CREATE TABLE `article_tag` (
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `tag_id` bigint NOT NULL COMMENT '标签ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章-标签关联表';

-- --------------------------------------------------------

--
-- 表的结构 `category`
--

CREATE TABLE `category` (
  `id` bigint NOT NULL COMMENT '分类ID，主键',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称，唯一',
  `slug` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'URL友好标识，唯一',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类描述',
  `parent_id` bigint DEFAULT NULL COMMENT '父分类ID，NULL表示顶级分类',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序值，越小越靠前',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- --------------------------------------------------------

--
-- 表的结构 `comment`
--

CREATE TABLE `comment` (
  `id` bigint NOT NULL COMMENT '评论ID，主键',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评论内容',
  `user_id` bigint NOT NULL COMMENT '评论人ID，关联user(id)',
  `article_id` bigint NOT NULL COMMENT '所属文章ID，关联article(id)',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID，NULL表示顶级评论',
  `reply_to_id` bigint DEFAULT NULL COMMENT '回复目标用户ID（冗余，便于通知）',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数（冗余）',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-正常，1-待审核，2-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- --------------------------------------------------------

--
-- 表的结构 `config`
--

CREATE TABLE `config` (
  `id` int NOT NULL COMMENT '配置ID，主键',
  `key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键，唯一',
  `value` text COLLATE utf8mb4_unicode_ci COMMENT '配置值',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '配置描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- --------------------------------------------------------

--
-- 表的结构 `like`
--

CREATE TABLE `like` (
  `id` bigint NOT NULL COMMENT '点赞记录ID，主键',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID，关联user(id)',
  `target_id` bigint NOT NULL COMMENT '目标ID（文章ID或评论ID）',
  `target_type` tinyint(1) NOT NULL COMMENT '目标类型：0-文章，1-评论',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';

-- --------------------------------------------------------

--
-- 表的结构 `tag`
--

CREATE TABLE `tag` (
  `id` bigint NOT NULL COMMENT '标签ID，主键',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称，唯一',
  `slug` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'URL友好标识，唯一',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- --------------------------------------------------------

--
-- 表的结构 `user`
--

CREATE TABLE `user` (
  `id` bigint NOT NULL COMMENT '用户ID，主键',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名，唯一',
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密后的密码',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱，唯一',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `role` tinyint(1) NOT NULL DEFAULT '0' COMMENT '角色：0-普通用户，1-管理员',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0-正常，1-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login` datetime DEFAULT NULL COMMENT '最后登录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

--
-- 转储表的索引
--

--
-- 表的索引 `article`
--
ALTER TABLE `article`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_slug` (`slug`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_category_id` (`category_id`),
  ADD KEY `idx_status_publish` (`status`,`publish_time`),
  ADD KEY `idx_top_publish` (`is_top`,`publish_time`);

--
-- 表的索引 `article_tag`
--
ALTER TABLE `article_tag`
  ADD PRIMARY KEY (`article_id`,`tag_id`),
  ADD KEY `idx_tag_id` (`tag_id`);

--
-- 表的索引 `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_name` (`name`),
  ADD UNIQUE KEY `uk_slug` (`slug`),
  ADD KEY `idx_parent_id` (`parent_id`);

--
-- 表的索引 `comment`
--
ALTER TABLE `comment`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_article_time` (`article_id`,`create_time`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_parent_id` (`parent_id`);

--
-- 表的索引 `config`
--
ALTER TABLE `config`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_key` (`key`);

--
-- 表的索引 `like`
--
ALTER TABLE `like`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_user_target` (`user_id`,`target_id`,`target_type`),
  ADD KEY `idx_target` (`target_id`,`target_type`);

--
-- 表的索引 `tag`
--
ALTER TABLE `tag`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_name` (`name`),
  ADD UNIQUE KEY `uk_slug` (`slug`);

--
-- 表的索引 `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_username` (`username`),
  ADD UNIQUE KEY `uk_email` (`email`);

--
-- 在导出的表使用AUTO_INCREMENT
--

--
-- 使用表AUTO_INCREMENT `article`
--
ALTER TABLE `article`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章ID，主键';

--
-- 使用表AUTO_INCREMENT `category`
--
ALTER TABLE `category`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID，主键';

--
-- 使用表AUTO_INCREMENT `comment`
--
ALTER TABLE `comment`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID，主键';

--
-- 使用表AUTO_INCREMENT `config`
--
ALTER TABLE `config`
  MODIFY `id` int NOT NULL AUTO_INCREMENT COMMENT '配置ID，主键';

--
-- 使用表AUTO_INCREMENT `like`
--
ALTER TABLE `like`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞记录ID，主键';

--
-- 使用表AUTO_INCREMENT `tag`
--
ALTER TABLE `tag`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID，主键';

--
-- 使用表AUTO_INCREMENT `user`
--
ALTER TABLE `user`
  MODIFY `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键';

--
-- 限制导出的表
--

--
-- 限制表 `article`
--
ALTER TABLE `article`
  ADD CONSTRAINT `fk_article_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_article_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- 限制表 `article_tag`
--
ALTER TABLE `article_tag`
  ADD CONSTRAINT `fk_article_tag_article` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_article_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- 限制表 `comment`
--
ALTER TABLE `comment`
  ADD CONSTRAINT `fk_comment_article` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

--
-- 限制表 `like`
--
ALTER TABLE `like`
  ADD CONSTRAINT `fk_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
