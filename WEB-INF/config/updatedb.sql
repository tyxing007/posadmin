



--V2013070201
--价格体系完成后的版本
ALTER TABLE `product` ADD COLUMN `limit_price`  double NULL COMMENT '限价';
ALTER TABLE `product` ADD COLUMN `lock_price`  double NULL COMMENT '锁价';
ALTER TABLE `product` ADD COLUMN `red_lines`  double NULL COMMENT '红卡额度';
ALTER TABLE `product` ADD COLUMN `blue_lines`  double NULL COMMENT '蓝卡额度';
ALTER TABLE `cashier` ADD COLUMN `sale_type`  int(1) NULL COMMENT '销售价类型[1:仅标牌价；2:突破标牌价；3:突破限价；4:突破锁价]' ;
CREATE TABLE `adjust_price_bill` (`id` int(11) NOT NULL AUTO_INCREMENT,  `product_id` int(11) DEFAULT NULL COMMENT '商品id',  `bill_number` varchar(20) DEFAULT NULL COMMENT '调价单号',  `target_price` double DEFAULT NULL COMMENT '目标价',  `audit_status` int(1) DEFAULT NULL COMMENT '审核状态[1:待审核；2:审核通过；3:审核未通过]',  `use_status` int(1) DEFAULT NULL COMMENT '使用状态[1:未提交；2:已提交]',  `create_time` datetime DEFAULT NULL COMMENT '创建时间',  PRIMARY KEY (`id`),  KEY `idx_product_id` (`product_id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='调价单';
CREATE TABLE `price_card` (`id` varchar(30) NOT NULL DEFAULT '' COMMENT '调价卡卡号',  `clerk_name` varchar(20) DEFAULT NULL COMMENT '店员姓名',  `open_time` datetime DEFAULT NULL COMMENT '开卡时间',  `type` int(2) DEFAULT NULL COMMENT '类型[1:红卡，2:蓝卡]',  `state` int(2) DEFAULT NULL COMMENT '使用状态[1:白卡；2:可使用；3:停用]',  `supplier_id` int(10) DEFAULT NULL COMMENT '供应商id',  `password` varchar(100) DEFAULT NULL COMMENT '卡密码',  `point` double unsigned DEFAULT '0' COMMENT '卡内点数',  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='调价卡';
CREATE TABLE `price_card_charge` (`id` int(11) NOT NULL AUTO_INCREMENT,  `price_card_id` varchar(50) DEFAULT NULL COMMENT '卡号',  `order_id` int(11) DEFAULT NULL COMMENT '销售订单id',  `card_type` int(2) DEFAULT NULL COMMENT '卡类型【1-红卡  2-蓝卡】',  `charge_time` datetime DEFAULT NULL COMMENT '充值时间',  `consume_type` int(2) DEFAULT NULL COMMENT '消费类型【1-充值  2-消费】',  `consume_cash` double DEFAULT NULL COMMENT '消费金额',  `total_cash` double DEFAULT NULL COMMENT '账户余额',  PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8 COMMENT='调价卡收支明细';
INSERT INTO `view_tree` (`parent_id`, `seq`, `name`, `url`, `node_url`, `limits`, `target`) VALUES (0, 420, '调价单管理', '../pos/adjustPriceBill!adjustPriceBillList.do', '', '0', '');
INSERT INTO `view_tree` (`parent_id`, `seq`, `name`, `url`, `node_url`, `limits`, `target`) VALUES (0, 422, '红蓝卡管理', '../pos/priceCard!priceCardList.do', '', '0', '');


--V2013062501
--会员积分完成后的版本
ALTER TABLE `member_account` ADD COLUMN `score`  int NULL COMMENT '会员积分' AFTER `consumption`;
ALTER TABLE `saled_order` ADD COLUMN `score`  int NULL COMMENT '订单积分' AFTER `order_type`;
DROP TABLE IF EXISTS `member_score`;
CREATE TABLE `member_score` ( `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id', `member_id` varchar(30) DEFAULT NULL COMMENT '会员id', `order_id` int(11) DEFAULT NULL COMMENT '订单id', `current_score` int(11) DEFAULT NULL COMMENT '当前积分', `add_score` int(11) DEFAULT NULL COMMENT '此单增加积分', `minus_score` int(11) DEFAULT NULL COMMENT '此单减少积分', `create_time` datetime DEFAULT NULL COMMENT '积分时间', `type` int(11) DEFAULT NULL COMMENT '积分类型[1-购物，2-退货]', PRIMARY KEY (`id`) ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

--V2013061301
--商品促销活动完成后的版本

DROP TABLE IF EXISTS `event`;
CREATE TABLE `event` (  `id` int(11) NOT NULL,  `type` int(1) DEFAULT NULL COMMENT '活动类型 ：1-买赠 2-换购 3-金额折扣 4-数量折扣 5-套餐',  `start_time` datetime DEFAULT NULL COMMENT '开始时间',  `end_time` datetime DEFAULT NULL COMMENT '结束时间',  `rule_desc` varchar(500) DEFAULT NULL COMMENT '规则说明',  `use_status` int(1) DEFAULT NULL COMMENT '使用状态[1:未生效；2:已生效；3:已过期]',  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='活动';

DROP TABLE IF EXISTS `buy_gift_event`;
CREATE TABLE `buy_gift_event` (  `id` int(11) NOT NULL,  `event_id` int(11) DEFAULT NULL COMMENT '活动id',  `product_id` int(11) DEFAULT NULL COMMENT '商品id',  `product_count` int(11) DEFAULT NULL COMMENT '商品数量',  `user_limit` int(11) DEFAULT NULL COMMENT '人数限制',  `remain_count` int(11) DEFAULT NULL COMMENT '已用数量',  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='买赠活动';

DROP TABLE IF EXISTS `buy_gift_product`;
CREATE TABLE `buy_gift_product` (  `id` int(11) NOT NULL,  `buy_gift_event_id` int(11) DEFAULT NULL COMMENT '买赠活动id',  `gift_product_id` int(11) DEFAULT NULL COMMENT '赠送商品id',  `max_gift_count` int(11) DEFAULT NULL COMMENT '最大赠送数量',  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='买赠活动赠品';

DROP TABLE IF EXISTS `swap_buy_event`;
CREATE TABLE `swap_buy_event` (  `id` int(11) NOT NULL,  `event_id` int(11) DEFAULT NULL COMMENT '活动id',  `money` double DEFAULT NULL COMMENT '金额',  `append_money` double DEFAULT NULL COMMENT '添加金额',  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='换购活动';

DROP TABLE IF EXISTS `swap_buy_product`;
CREATE TABLE `swap_buy_product` (  `id` int(11) NOT NULL,  `swap_buy_event_id` int(11) DEFAULT NULL COMMENT '换购活动id',  `gift_product_id` int(11) DEFAULT NULL COMMENT '赠送商品id',  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='换购活动商品';

DROP TABLE IF EXISTS `combo_event`;
CREATE TABLE `combo_event` (  `id` int(11) NOT NULL,  `event_id` int(11) DEFAULT NULL COMMENT '活动id',  `combo_price` double DEFAULT NULL COMMENT '套餐价',  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='套餐活动';

DROP TABLE IF EXISTS `combo_product`;
CREATE TABLE `combo_product` (  `id` int(11) NOT NULL,  `combo_event_id` int(11) DEFAULT NULL,  `product_id` int(11) DEFAULT NULL,  `product_count` int(11) DEFAULT NULL,  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='套餐商品';

DROP TABLE IF EXISTS `count_discount_event`;
CREATE TABLE `count_discount_event` (  `id` int(11) NOT NULL,  `event_id` int(11) DEFAULT NULL COMMENT '活动id',  `product_id` int(11) DEFAULT NULL COMMENT '商品id',  `product_count` int(11) DEFAULT NULL COMMENT '数量',  `type` int(11) DEFAULT NULL COMMENT '折扣类型[1:阶梯折扣;2:固定折扣]',  `discount` double DEFAULT NULL COMMENT '数值',  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数量折扣活动';

DROP TABLE IF EXISTS `money_discount_event`;
CREATE TABLE `money_discount_event` (  `id` int(11) NOT NULL,  `event_id` int(11) DEFAULT NULL COMMENT '活动id',  `money` double DEFAULT NULL COMMENT '金额',  `type` varchar(255) DEFAULT NULL COMMENT '类型[1:减钱 2：折扣]',  `number_value` double DEFAULT NULL COMMENT '数值',  PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='金额折扣活动';

DROP TABLE IF EXISTS `saled_order_event`;
CREATE TABLE `saled_order_event` (  `id` int(11) NOT NULL AUTO_INCREMENT,  `saled_order_id` int(11) DEFAULT NULL COMMENT '订单id',  `event_id` int(11) DEFAULT NULL COMMENT '活动id',  `detail_event_id` int(11) DEFAULT NULL COMMENT '具体的活动id',  `ext_id` int(11) DEFAULT NULL COMMENT '额外的比如赠品等记录的id',  PRIMARY KEY (`id`)) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='订单活动中间表';

--订单商品表添加event_remark字段
ALTER TABLE `saled_order_product` ADD COLUMN `event_remark`  varchar(50) NULL COMMENT '活动备注' AFTER `pre_price`;

INSERT INTO `view_tree` (`parent_id`, `seq`, `name`, `url`, `node_url`, `limits`, `target`) VALUES (0, 418, '促销活动', '../pos/event!eventList.do', '', '0', '');


--V2013010101
--初始版本


----------------------------------说明----------------------------------------
--1.版本号命名规范:以大写字母V开头；2-9位为年月日；最后两位为当天发布的第几个版本
--eg.V2013010101:表示2013年1月1日的第一个版本
--2.版本号前面加上‘--’注释，需要的更新的sql语句放到版本号的下面
--3.每个sql语句占用一行，不能跨行
------------------------------------------------------------------------------