
-- ----------------------------
-- Table structure for `adjust_price_bill`
-- ----------------------------
DROP TABLE IF EXISTS `adjust_price_bill`;
CREATE TABLE `adjust_price_bill` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) DEFAULT NULL COMMENT '商品id',
  `bill_number` varchar(20) DEFAULT NULL COMMENT '调价单号',
  `target_price` double DEFAULT NULL COMMENT '目标价',
  `audit_status` int(1) DEFAULT NULL COMMENT '审核状态[1:待审核；2:审核通过；3:审核未通过]',
  `use_status` int(1) DEFAULT NULL COMMENT '使用状态[1:未提交；2:已提交]',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='调价单';


-- ----------------------------
-- Table structure for `balance`
-- ----------------------------
DROP TABLE IF EXISTS `balance`;
CREATE TABLE `balance` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pos_code` varchar(255) DEFAULT NULL COMMENT 'pos机编码',
  `cashier_id` int(11) DEFAULT NULL COMMENT '收银员id',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '上班时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '交接时间',
  `theory_cash` double DEFAULT NULL COMMENT '理论金额',
  `pos_cash` double DEFAULT NULL COMMENT 'pos机金额',
  `handover_id` int(11) DEFAULT NULL COMMENT '接收人id',
  `init_poscash` double DEFAULT NULL COMMENT '初始pos机金额[银头]',
  `add_poscash` double NOT NULL DEFAULT '0' COMMENT '追加金额',
  `back_text` varchar(300) DEFAULT NULL COMMENT '备注',
  `is_finish` int(1) DEFAULT NULL COMMENT '是否日结  0 --交接   1---日结',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='收银员交接记录';

-- ----------------------------
-- Table structure for `buy_gift_event`
-- ----------------------------
DROP TABLE IF EXISTS `buy_gift_event`;
CREATE TABLE `buy_gift_event` (
  `id` int(11) NOT NULL,
  `event_id` int(11) DEFAULT NULL COMMENT '活动id',
  `product_id` int(11) DEFAULT NULL COMMENT '商品id',
  `product_count` int(11) DEFAULT NULL COMMENT '商品数量',
  `user_limit` int(11) DEFAULT NULL COMMENT '人数限制',
  `remain_count` int(11) DEFAULT NULL COMMENT '已用数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='买赠活动';

-- ----------------------------
-- Table structure for `buy_gift_product`
-- ----------------------------
DROP TABLE IF EXISTS `buy_gift_product`;
CREATE TABLE `buy_gift_product` (
  `id` int(11) NOT NULL,
  `buy_gift_event_id` int(11) DEFAULT NULL COMMENT '买赠活动id',
  `gift_product_id` int(11) DEFAULT NULL COMMENT '赠送商品id',
  `max_gift_count` int(11) DEFAULT NULL COMMENT '最大赠送数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='买赠活动赠品';


-- ----------------------------
-- Table structure for `cash_stream`
-- ----------------------------
DROP TABLE IF EXISTS `cash_stream`;
CREATE TABLE `cash_stream` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `serial_number` varchar(20) DEFAULT NULL COMMENT '对应订单的流水号',
  `pos_code` varchar(255) DEFAULT NULL COMMENT 'pos机编码',
  `cashier_id` int(11) DEFAULT NULL COMMENT '收银员id',
  `cash` double DEFAULT NULL COMMENT '实收现金',
  `get_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '收银时间',
  PRIMARY KEY (`id`),
  KEY `idx_serial_number` (`serial_number`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='现金流';

-- ----------------------------
-- Table structure for `cashier`
-- ----------------------------
DROP TABLE IF EXISTS `cashier`;
CREATE TABLE `cashier` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `name` varchar(20) DEFAULT NULL COMMENT '姓名',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `username` varchar(10) DEFAULT NULL COMMENT '用户名',
  `password` varchar(50) DEFAULT NULL COMMENT '密码',
  `sale_type` int(1) DEFAULT '1' COMMENT '销售价类型[1:仅标牌价；2:突破标牌价；3:突破限价；4:突破锁价]',
  `is_delete` int(1) DEFAULT NULL COMMENT '是否删除 0-未删  1-已删除',
  `forbidden` int(1) unsigned zerofill NOT NULL COMMENT '0-代表未禁用  1--已禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='收银员';

-- ----------------------------
-- Table structure for `check_stock`
-- ----------------------------
DROP TABLE IF EXISTS `check_stock`;
CREATE TABLE `check_stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `charger` varchar(50) DEFAULT NULL COMMENT '盘点负责人',
  `check_time` datetime DEFAULT NULL COMMENT '盘点时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `use_status` int(11) DEFAULT NULL COMMENT '使用状态[0:未提交；1:已提交]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='盘点表';

-- ----------------------------
-- Table structure for `check_stock_item`
-- ----------------------------
DROP TABLE IF EXISTS `check_stock_item`;
CREATE TABLE `check_stock_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `check_stock_id` int(11) DEFAULT NULL COMMENT '所属盘点id',
  `product_id` int(11) DEFAULT NULL COMMENT '商品id',
  `charger` varchar(50) DEFAULT NULL COMMENT '盘点负责人',
  `purchase_price` double DEFAULT NULL COMMENT '进价',
  `sale_price` double DEFAULT NULL COMMENT '售价',
  `count` int(11) DEFAULT NULL COMMENT '盘点数量',
  `stock` int(11) DEFAULT NULL COMMENT '电脑库存',
  `check_time` datetime DEFAULT NULL COMMENT '盘点时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_check_stock_id` (`check_stock_id`) USING BTREE,
  KEY `idx_product_id` (`product_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='盘点条目表';

-- ----------------------------
-- Table structure for `combo_event`
-- ----------------------------
DROP TABLE IF EXISTS `combo_event`;
CREATE TABLE `combo_event` (
  `id` int(11) NOT NULL,
  `event_id` int(11) DEFAULT NULL COMMENT '活动id',
  `combo_price` double DEFAULT NULL COMMENT '套餐价',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='套餐活动';

-- ----------------------------
-- Table structure for `combo_product`
-- ----------------------------
DROP TABLE IF EXISTS `combo_product`;
CREATE TABLE `combo_product` (
  `id` int(11) NOT NULL,
  `combo_event_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `product_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='套餐商品';


-- ----------------------------
-- Table structure for `count_discount_event`
-- ----------------------------
DROP TABLE IF EXISTS `count_discount_event`;
CREATE TABLE `count_discount_event` (
  `id` int(11) NOT NULL,
  `event_id` int(11) DEFAULT NULL COMMENT '活动id',
  `product_id` int(11) DEFAULT NULL COMMENT '商品id',
  `product_count` int(11) DEFAULT NULL COMMENT '数量',
  `type` int(11) DEFAULT NULL COMMENT '折扣类型[1:阶梯折扣;2:固定折扣]',
  `discount` double DEFAULT NULL COMMENT '数值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数量折扣活动';

-- ----------------------------
-- Table structure for `event`
-- ----------------------------
DROP TABLE IF EXISTS `event`;
CREATE TABLE `event` (
  `id` int(11) NOT NULL,
  `type` int(1) DEFAULT NULL COMMENT '活动类型 ：1-买赠 2-换购 3-金额折扣 4-数量折扣 5-套餐',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `rule_desc` varchar(500) DEFAULT NULL COMMENT '规则说明',
  `use_status` int(1) DEFAULT NULL COMMENT '使用状态[1:未生效；2:已生效；3:已过期]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='活动';

-- ----------------------------
-- Table structure for `goods_class`
-- ----------------------------
DROP TABLE IF EXISTS `goods_class`;
CREATE TABLE `goods_class` (
  `id` varchar(20) NOT NULL DEFAULT '' COMMENT '记录号',
  `parent_id` varchar(20) DEFAULT NULL COMMENT '父分类Id',
  `name` varchar(50) DEFAULT NULL COMMENT '名称',
  `desc` varchar(200) DEFAULT NULL COMMENT '描述',
  `tree_level` int(11) DEFAULT NULL COMMENT '级别',
  `is_leaf` int(11) DEFAULT NULL COMMENT '是否叶子节点[0:不是；1:是]',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品分类';

-- ---------------------------
-- Table structure for `invoice`
-- ----------------------------
DROP TABLE IF EXISTS `invoice`;
CREATE TABLE `invoice` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `product_id` int(10) DEFAULT NULL COMMENT '商品id',
  `serial_number` varchar(20) DEFAULT NULL COMMENT '流水单号[采购单号、发货单号]',
  `oper_type` varchar(1) DEFAULT NULL COMMENT '操作类型[0:收货；1:销售；2:退货；3:租赁；4:还租；5:盘点；6:退货到中心库]',
  `before_count` int(10) DEFAULT NULL COMMENT '操作前商品库存数量',
  `count` int(10) DEFAULT NULL COMMENT '商品变动数量',
  `after_count` int(10) DEFAULT NULL COMMENT '操作后商品库存数量',
  `oper_user` varchar(255) DEFAULT NULL COMMENT '操作人',
  `create_time` datetime DEFAULT NULL COMMENT '操作时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`) USING BTREE,
  KEY `idx_serial_number` (`serial_number`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='进销存记录';

-- ----------------------------
-- Table structure for `lease_order`
-- ----------------------------
DROP TABLE IF EXISTS `lease_order`;
CREATE TABLE `lease_order` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `serial_number` char(20) DEFAULT NULL COMMENT '订单流水号',
  `pos_code` varchar(20) DEFAULT NULL COMMENT 'pos机编号',
  `cashier_id` int(10) DEFAULT NULL COMMENT '收银员id',
  `member_id` varchar(30) DEFAULT NULL COMMENT '会员id',
  `price` double DEFAULT NULL COMMENT '订单总金额',
  `deposit` double DEFAULT NULL COMMENT '订单总押金/应归还的总押金',
  `order_type` int(11) DEFAULT NULL COMMENT '订单类型[0:租赁订单；1:还租订单]',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_number_type` (`serial_number`,`order_type`) USING BTREE,
  KEY `idx_member_id` (`member_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='租赁订单';


-- ----------------------------
-- Table structure for `lease_order_product`
-- ----------------------------
DROP TABLE IF EXISTS `lease_order_product`;
CREATE TABLE `lease_order_product` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `lease_order_id` int(11) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `count` int(11) DEFAULT NULL COMMENT '数量',
  `pre_price` double DEFAULT NULL COMMENT '单价',
  `per_deposit` double DEFAULT NULL COMMENT '单押金',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '赁租开始时间',
  `end_time` timestamp NULL DEFAULT NULL COMMENT '租赁结束时间',
  `time_length` double DEFAULT NULL COMMENT '租赁时长',
  `lease_style` int(1) DEFAULT NULL COMMENT '租赁方式[0:按日租赁；1:包月租赁]',
  PRIMARY KEY (`id`),
  KEY `idx_order_style` (`lease_order_id`,`lease_style`) USING BTREE,
  KEY `idx_product_id` (`product_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='租赁订单商品';


-- ----------------------------
-- Table structure for `member`
-- ----------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member` (
  `id` varchar(30) NOT NULL COMMENT '会员id',
  `name` varchar(10) DEFAULT NULL COMMENT '会员姓名',
  `id_card` varchar(18) DEFAULT NULL COMMENT '会员身份证号',
  `mobile` varchar(20) DEFAULT NULL COMMENT '手机号',
  `register_time` datetime DEFAULT NULL COMMENT '会员注册时间',
  `use_state` int(1) DEFAULT '0' COMMENT '使用状态[0:使用中; 1:已注销;2:新卡;3:挂失]',
  `change_time` datetime DEFAULT NULL COMMENT '修改时间 记录本地的修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员';

-- ----------------------------
-- Table structure for `member_account`
-- ----------------------------
DROP TABLE IF EXISTS `member_account`;
CREATE TABLE `member_account` (
  `id` varchar(30) NOT NULL COMMENT '会员id',
  `available_balance` double DEFAULT NULL COMMENT '可用余额',
  `freeze_balance` double DEFAULT NULL COMMENT '结冻金额',
  `consumption` double DEFAULT NULL COMMENT '消费总金额',
  `score` int(11) DEFAULT NULL COMMENT '会员积分',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='会员账户信息';

-- ----------------------------
-- Table structure for `member_account_detail`
-- ----------------------------
DROP TABLE IF EXISTS `member_account_detail`;
CREATE TABLE `member_account_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `member_id` varchar(30) DEFAULT NULL COMMENT '会员id',
  `type` int(11) DEFAULT NULL COMMENT '类型[1:充值；2:提现；3:购买；4:租赁；5:退货]',
  `order_id` int(11) DEFAULT NULL COMMENT '订单id',
  `income` double DEFAULT '0' COMMENT '收入',
  `pay` double DEFAULT '0' COMMENT '支出',
  `balance` double DEFAULT '0' COMMENT '账户余额',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='会员账户收支明细';

-- ----------------------------
-- Table structure for `member_lease`
-- ----------------------------
DROP TABLE IF EXISTS `member_lease`;
CREATE TABLE `member_lease` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `serial_number` varchar(255) DEFAULT NULL COMMENT '流水号',
  `member_id` varchar(30) DEFAULT NULL COMMENT '会员id',
  `product_id` int(11) DEFAULT NULL COMMENT '商品id',
  `start_time` datetime DEFAULT NULL COMMENT '租赁开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '租赁结束时间',
  `type` int(11) DEFAULT NULL COMMENT '租赁类型[0-日租，1-月租]',
  PRIMARY KEY (`id`),
  KEY `idx_serial_number` (`serial_number`) USING BTREE,
  KEY `idx_member_id` (`member_id`) USING BTREE,
  KEY `idx_product_id` (`product_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='会员租赁信息';


-- ----------------------------
-- Table structure for `member_score`
-- ----------------------------
DROP TABLE IF EXISTS `member_score`;
CREATE TABLE `member_score` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `member_id` varchar(30) DEFAULT NULL COMMENT '会员id',
  `order_id` int(11) DEFAULT NULL COMMENT '订单id',
  `current_score` int(11) DEFAULT NULL COMMENT '当前积分',
  `add_score` int(11) DEFAULT NULL COMMENT '此单增加积分',
  `minus_score` int(11) DEFAULT NULL COMMENT '此单减少积分',
  `create_time` datetime DEFAULT NULL COMMENT '积分时间',
  `type` int(11) DEFAULT NULL COMMENT '积分类型[1-购物，2-退货]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `money_destination`
-- ----------------------------
DROP TABLE IF EXISTS `money_destination`;
CREATE TABLE `money_destination` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '金钱去向id',
  `order_id` int(11) DEFAULT NULL COMMENT '退货订单id',
  `type` int(11) DEFAULT NULL COMMENT '去向类型[1:现金；2:会员卡；3:银行卡]',
  `money` double DEFAULT NULL COMMENT '金额',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='订单的金钱去向';

-- ----------------------------
-- Table structure for `money_discount_event`
-- ----------------------------
DROP TABLE IF EXISTS `money_discount_event`;
CREATE TABLE `money_discount_event` (
  `id` int(11) NOT NULL,
  `event_id` int(11) DEFAULT NULL COMMENT '活动id',
  `money` double DEFAULT NULL COMMENT '金额',
  `type` varchar(255) DEFAULT NULL COMMENT '类型[1:减钱 2：折扣]',
  `number_value` double DEFAULT NULL COMMENT '数值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='金额折扣活动';

-- ----------------------------
-- Table structure for `money_source`
-- ----------------------------
DROP TABLE IF EXISTS `money_source`;
CREATE TABLE `money_source` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '金钱来源id',
  `order_id` int(11) DEFAULT NULL COMMENT '销售订单id',
  `type` int(11) DEFAULT NULL COMMENT '来源类型[1:现金；2:会员卡；3:银行卡；4:购物券]',
  `money` double DEFAULT NULL COMMENT '金额',
  `withdraw_money` double unsigned DEFAULT NULL COMMENT '可提取money',
  `swip_card_number` varchar(255) DEFAULT NULL COMMENT '刷卡单号',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='订单的金钱来源';

-- ----------------------------
-- Table structure for `permission`
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `seq` int(10) unsigned NOT NULL DEFAULT '0',
  `parent` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL DEFAULT '',
  `bak` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Index_2` (`parent`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES ('1', '0', '0', '超级管理员', '超级管理员');

-- ----------------------------
-- Table structure for `pos_manager`
-- ----------------------------
DROP TABLE IF EXISTS `pos_manager`;
CREATE TABLE `pos_manager` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `pos_code` varchar(255) DEFAULT NULL COMMENT 'pos机编码',
  `pos_ip` varchar(255) DEFAULT NULL COMMENT 'pos机ip',
  `create_time` datetime DEFAULT NULL COMMENT 'pos机增加日期',
  `oper_user` varchar(255) DEFAULT NULL COMMENT '操作人',
  `is_delete` int(1) unsigned zerofill DEFAULT '0' COMMENT '是否删除[0--未删除,1-已删除]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `price_card`
-- ----------------------------
DROP TABLE IF EXISTS `price_card`;
CREATE TABLE `price_card` (
  `id` varchar(30) NOT NULL DEFAULT '' COMMENT '调价卡卡号',
  `clerk_name` varchar(20) DEFAULT NULL COMMENT '店员姓名',
  `open_time` datetime DEFAULT NULL COMMENT '开卡时间',
  `type` int(2) DEFAULT NULL COMMENT '类型[1:红卡，2:蓝卡]',
  `state` int(2) DEFAULT NULL COMMENT '使用状态[1:白卡；2:可使用；3:停用]',
  `supplier_id` int(10) DEFAULT NULL COMMENT '供应商id',
  `password` varchar(100) DEFAULT NULL COMMENT '卡密码',
  `point` double DEFAULT '0' COMMENT '卡内点数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='调价卡';


-- ----------------------------
-- Table structure for `price_card_charge`
-- ----------------------------
DROP TABLE IF EXISTS `price_card_charge`;
CREATE TABLE `price_card_charge` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `price_card_id` varchar(50) DEFAULT NULL COMMENT '卡号',
  `order_id` int(11) DEFAULT NULL COMMENT '销售订单id',
  `card_type` int(2) DEFAULT NULL COMMENT '卡类型【1-红卡  2-蓝卡】',
  `charge_time` datetime DEFAULT NULL COMMENT '充值时间',
  `consume_type` int(2) DEFAULT NULL COMMENT '消费类型【1-充值  2-消费】',
  `consume_cash` double DEFAULT NULL COMMENT '消费金额',
  `total_cash` double DEFAULT NULL COMMENT '账户余额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='调价卡收支明细';

-- ----------------------------
-- Table structure for `product`
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `goods_class_id` varchar(20) DEFAULT NULL COMMENT '商品分类id',
  `bar_code` char(255) DEFAULT NULL COMMENT '条形码',
  `name` char(255) DEFAULT NULL COMMENT '商品名称',
  `lease_price` double DEFAULT NULL COMMENT '商品租赁价格',
  `month_lease_price` double DEFAULT NULL COMMENT '包月租赁价格',
  `sale_price` double DEFAULT NULL COMMENT '商品销售价',
  `deposit` double DEFAULT NULL COMMENT '押金',
  `stock` int(11) DEFAULT NULL COMMENT '库存量',
  `is_delete` int(1) DEFAULT NULL COMMENT '是否删除',
  `supplier_id` int(11) DEFAULT NULL COMMENT '供应商id',
  `limit_price` double DEFAULT NULL COMMENT '限价',
  `lock_price` double DEFAULT NULL COMMENT '锁价',
  `red_lines` double DEFAULT NULL COMMENT '红卡额度',
  `blue_lines` double DEFAULT NULL COMMENT '蓝卡额度',
  PRIMARY KEY (`id`),
  KEY `idx_bar_code` (`bar_code`,`is_delete`) USING BTREE,
  KEY `idx_goods_class_id` (`goods_class_id`) USING BTREE,
  KEY `idx_supplier_id` (`supplier_id`) USING BTREE,
  KEY `idx_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='商品';

-- ----------------------------
-- Table structure for `receive_order`
-- ----------------------------
DROP TABLE IF EXISTS `receive_order`;
CREATE TABLE `receive_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `send_order_id` int(11) DEFAULT NULL COMMENT '对应发货单id',
  `order_number` varchar(10) DEFAULT NULL COMMENT '收货单号',
  `charger` varchar(255) DEFAULT NULL COMMENT '收货负责人',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '收货时间',
  `use_status` int(11) DEFAULT NULL COMMENT '使用状态[0:未提交；1:已提交；2:已归档]',
  PRIMARY KEY (`id`),
  KEY `idx_send_order_id` (`send_order_id`) USING BTREE,
  KEY `idx_order_number` (`order_number`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='收货单';

-- ----------------------------
-- Table structure for `receive_order_item`
-- ----------------------------
DROP TABLE IF EXISTS `receive_order_item`;
CREATE TABLE `receive_order_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(10) DEFAULT NULL COMMENT '收货单号',
  `product_id` int(11) DEFAULT NULL COMMENT '商品id',
  `send_count` int(11) DEFAULT NULL COMMENT '发货数量',
  `receive_count` int(11) DEFAULT NULL COMMENT '实收数量',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`) USING BTREE,
  KEY `idx_product_id` (`product_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='收货单条目';

-- ----------------------------
-- Table structure for `return_order`
-- ----------------------------
DROP TABLE IF EXISTS `return_order`;
CREATE TABLE `return_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '退货单id',
  `order_number` varchar(20) DEFAULT NULL COMMENT '退货单号',
  `charger` varchar(50) DEFAULT NULL COMMENT '负责人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `use_status` int(11) DEFAULT NULL COMMENT '使用状态[0:未提交；1:已提交]',
  PRIMARY KEY (`id`),
  KEY `idx_order_number` (`order_number`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='退货单';


-- ----------------------------
-- Table structure for `return_order_item`
-- ----------------------------
DROP TABLE IF EXISTS `return_order_item`;
CREATE TABLE `return_order_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '退货单条目id',
  `return_order_id` int(11) DEFAULT NULL COMMENT '所属退货单id',
  `product_id` int(11) DEFAULT NULL COMMENT '商品id',
  `count` int(11) DEFAULT NULL COMMENT '商品数量',
  PRIMARY KEY (`id`),
  KEY `idx_return_order_id` (`return_order_id`) USING BTREE,
  KEY `idx_product_id` (`product_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='退货单条目';

-- ----------------------------
-- Table structure for `saled_order`
-- ----------------------------
DROP TABLE IF EXISTS `saled_order`;
CREATE TABLE `saled_order` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `serial_number` char(20) DEFAULT NULL COMMENT '订单流水号',
  `pos_code` varchar(20) DEFAULT NULL COMMENT 'pos机编号',
  `cashier_id` int(10) DEFAULT NULL COMMENT '收银员id',
  `member_id` varchar(30) DEFAULT NULL COMMENT '会员id',
  `price` double DEFAULT NULL COMMENT '单订总金额',
  `saled_time` timestamp NULL DEFAULT NULL COMMENT '销售时间',
  `order_type` int(1) DEFAULT NULL COMMENT '订单类型[0-销售订单,1-退货订单]',
  `score` int(11) DEFAULT NULL COMMENT '订单积分',
  PRIMARY KEY (`id`),
  KEY `idx_number_type` (`serial_number`,`order_type`) USING BTREE,
  KEY `idx_member_id` (`member_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='销售订单';

-- ----------------------------
-- Table structure for `saled_order_event`
-- ----------------------------
DROP TABLE IF EXISTS `saled_order_event`;
CREATE TABLE `saled_order_event` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `saled_order_id` int(11) DEFAULT NULL COMMENT '订单id',
  `event_id` int(11) DEFAULT NULL COMMENT '活动id',
  `detail_event_id` int(11) DEFAULT NULL COMMENT '具体的活动id',
  `ext_id` int(11) DEFAULT NULL COMMENT '额外的比如赠品等记录的id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='订单活动中间表';

-- ----------------------------
-- Table structure for `saled_order_product`
-- ----------------------------
DROP TABLE IF EXISTS `saled_order_product`;
CREATE TABLE `saled_order_product` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) DEFAULT NULL,
  `saled_order_id` int(11) DEFAULT NULL,
  `count` int(11) DEFAULT NULL COMMENT '购买数量',
  `pre_price` double DEFAULT NULL COMMENT '价单',
  `event_remark` varchar(50) DEFAULT NULL COMMENT '活动备注',
  `order_type` int(1) DEFAULT NULL COMMENT '活动类型',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`) USING BTREE,
  KEY `idx_saled_order_id` (`saled_order_id`,`order_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='销售订单商品';

-- ----------------------------
-- Table structure for `supplier`
-- ----------------------------
DROP TABLE IF EXISTS `supplier`;
CREATE TABLE `supplier` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '供应商名字',
  `phone` varchar(255) DEFAULT NULL COMMENT '供应商电话',
  `is_delete` int(11) unsigned zerofill DEFAULT '00000000000' COMMENT '是否删除 0-未删除  1--已删除',
  `use_status` int(11) DEFAULT NULL COMMENT '使用状态[1:未发布；2:已发布]',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `swap_buy_event`
-- ----------------------------
DROP TABLE IF EXISTS `swap_buy_event`;
CREATE TABLE `swap_buy_event` (
  `id` int(11) NOT NULL,
  `event_id` int(11) DEFAULT NULL COMMENT '活动id',
  `money` double DEFAULT NULL COMMENT '金额',
  `append_money` double DEFAULT NULL COMMENT '添加金额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='换购活动';


-- ----------------------------
-- Table structure for `swap_buy_product`
-- ----------------------------
DROP TABLE IF EXISTS `swap_buy_product`;
CREATE TABLE `swap_buy_product` (
  `id` int(11) NOT NULL,
  `swap_buy_event_id` int(11) DEFAULT NULL COMMENT '换购活动id',
  `gift_product_id` int(11) DEFAULT NULL COMMENT '赠送商品id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='换购活动商品';

-- ----------------------------
-- Table structure for `system_config`
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `id` int(11) NOT NULL COMMENT 'id',
  `shop_code` varchar(50) DEFAULT NULL COMMENT '店面编号',
  `saled_order_last_submit_time` datetime DEFAULT NULL COMMENT '销售订单上次提交时间',
  `lease_order_last_submit_time` datetime DEFAULT NULL COMMENT '租赁订单上次提交时间',
  `invoice_last_submit_time` datetime DEFAULT NULL COMMENT '进销存上次提交时间',
  `member_last_submit_time` datetime DEFAULT NULL COMMENT '会员信息上次提交时间',
  `member_last_sync_time` datetime DEFAULT NULL COMMENT '会员信息上次同步时间',
  `version_number` varchar(50) DEFAULT NULL COMMENT '店面系统的版本号',
  `version_name` varchar(255) DEFAULT NULL COMMENT '店面系统的版本名称',
  `version_desc` varchar(255) DEFAULT NULL COMMENT '店面系统的版本描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统配置信息';

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config` VALUES ('1', 'S01', '2013-01-20 14:40:40', '2013-01-20 14:29:42', '2013-01-02 13:40:46', '2013-01-20 09:50:38', '2013-01-02 09:15:44', 'V2013070201', '测试版1', 'aaaaa');

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL DEFAULT '',
  `password` varchar(45) NOT NULL DEFAULT '',
  `create_datetime` datetime NOT NULL,
  `flag` varchar(45) NOT NULL DEFAULT '0',
  `name` varchar(45) NOT NULL DEFAULT '',
  `phone` varchar(45) NOT NULL DEFAULT '',
  `address` varchar(255) NOT NULL DEFAULT '',
  `postcode` varchar(45) NOT NULL DEFAULT '',
  `nick` varchar(45) DEFAULT NULL,
  `email` varchar(45) NOT NULL,
  `code` varchar(45) NOT NULL DEFAULT '',
  `gender` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `departments` varchar(45) NOT NULL DEFAULT '',
  `staff_id` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `usernameidx` (`username`) USING BTREE,
  KEY `emailidx` (`email`) USING BTREE,
  KEY `codeidx` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'admin', '8wAW9RYebN3WPpalOe__Yg))', '2012-11-27 09:33:49', '0', 'admin', '', '', '', null, 'zhangkai@ebinf.com', '', '1', '', '362');

-- ----------------------------
-- Table structure for `user_group`
-- ----------------------------
DROP TABLE IF EXISTS `user_group`;
CREATE TABLE `user_group` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(45) NOT NULL DEFAULT '',
  `bak` text NOT NULL,
  `flags` varbinary(1000) NOT NULL,
  `flag` bigint(20) unsigned NOT NULL DEFAULT '0',
  `flag2` bigint(20) unsigned NOT NULL DEFAULT '0',
  `flag3` bigint(20) unsigned NOT NULL DEFAULT '0',
  `flag4` bigint(20) unsigned NOT NULL DEFAULT '0',
  `flag5` bigint(20) unsigned NOT NULL DEFAULT '0',
  `flag6` bigint(20) unsigned NOT NULL DEFAULT '0',
  `flag7` bigint(20) unsigned NOT NULL DEFAULT '0',
  `flag8` bigint(20) unsigned NOT NULL DEFAULT '0',
  `flag9` bigint(20) unsigned NOT NULL DEFAULT '0',
  `flag10` bigint(20) unsigned NOT NULL DEFAULT '0',
  `dept` int(10) unsigned NOT NULL DEFAULT '0',
  `catalog` int(10) unsigned NOT NULL DEFAULT '0',
  `seq` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_group
-- ----------------------------
INSERT INTO `user_group` VALUES ('1', '超级管理员', '可以进行权限修改', 0x01, '1152921503533105151', '1152921504606846975', '1152921504606846975', '1152921504606846975', '1152921504606846975', '1152921504606322687', '1152921504606846975', '1152921504606846975', '1152921504606846975', '0', '0', '0', '99');

-- ----------------------------
-- Table structure for `user_permission`
-- ----------------------------
DROP TABLE IF EXISTS `user_permission`;
CREATE TABLE `user_permission` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL DEFAULT '0',
  `security_level` int(10) unsigned NOT NULL DEFAULT '0',
  `permission` int(10) unsigned NOT NULL DEFAULT '0',
  `groups` varchar(1000) NOT NULL DEFAULT '',
  `flags` varbinary(1000) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Index_2` (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_permission
-- ----------------------------
INSERT INTO `user_permission` VALUES ('1', '1', '10', '0', ',1,', 0x0000000000000000);

-- ----------------------------
-- Table structure for `view_tree`
-- ----------------------------
DROP TABLE IF EXISTS `view_tree`;
CREATE TABLE `view_tree` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `parent_id` int(10) unsigned NOT NULL DEFAULT '0',
  `seq` int(10) unsigned NOT NULL DEFAULT '0',
  `name` varchar(45) NOT NULL DEFAULT '',
  `url` varchar(225) NOT NULL,
  `node_url` varchar(45) NOT NULL DEFAULT '',
  `limits` varchar(45) NOT NULL DEFAULT '',
  `target` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of view_tree
-- ----------------------------
INSERT INTO `view_tree` VALUES ('79', '0', '100', '系统管理', 'perm/index.jsp', '', '0', '');
INSERT INTO `view_tree` VALUES ('80', '0', '401', '商品管理', '../pos/Product!productList.do', '', '0', '');
INSERT INTO `view_tree` VALUES ('81', '0', '402', '会员管理', '../pos/member!memberList.do', '', '0', '');
INSERT INTO `view_tree` VALUES ('82', '0', '403', '统计', '../pos/orderCount!getCount.do', '', '0', '');
INSERT INTO `view_tree` VALUES ('83', '0', '404', '租赁订单', '../pos/order!leaseOrderList.do', '', '0', '');
INSERT INTO `view_tree` VALUES ('86', '0', '400', '收银员管理', '../pos/cashier!cashierList.do', '', '', '');
INSERT INTO `view_tree` VALUES ('85', '0', '405', '销售订单', '../pos/order!saledOrderList.do', '', '0', '');
INSERT INTO `view_tree` VALUES ('87', '0', '406', '交接记录', '../pos/balance!balanceList.do', '', '0', '');
INSERT INTO `view_tree` VALUES ('88', '0', '407', '盘点列表', '../pos/checkStock!checkStockList.do', '', '0', '');
INSERT INTO `view_tree` VALUES ('91', '0', '410', '进销存', '../pos/invoice!productList.do', '', '0', '');
INSERT INTO `view_tree` VALUES ('90', '0', '408', '收货单管理', '../pos/receiveOrder!receiveOrderList.do', '', '0', '');
INSERT INTO `view_tree` VALUES ('92', '0', '399', 'POS机管理', '../pos/posManager!posManagerList.do', '', '', '');
INSERT INTO `view_tree` VALUES ('93', '0', '412', '系统更新', '../pos/systemUpdate!toSystemUpdateView.do', '', '0', '');
INSERT INTO `view_tree` VALUES ('94', '0', '409', '退货单管理', '../pos/returnOrder!returnOrderList.do', '', '0', '');
INSERT INTO `view_tree` VALUES ('95', '0', '414', '店面IP更新', '../enter/poscenter!shopIpUpdateForm.do', '', '', '');
INSERT INTO `view_tree` VALUES ('96', '0', '416', '系统配置', '../pos/systemConfig!toSystemConfigView.do', '', '0', '');
INSERT INTO `view_tree` VALUES ('97', '0', '418', '促销活动', '../pos/event!eventList.do', '', '0', '');
INSERT INTO `view_tree` VALUES ('99', '0', '422', '调价单管理', '../pos/adjustPriceBill!adjustPriceBillList.do', '', '0', '');
INSERT INTO `view_tree` VALUES ('100', '0', '420', '红蓝卡管理', '../pos/priceCard!priceCardList.do', '', '', '');
