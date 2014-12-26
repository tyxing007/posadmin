package mmb.posadmin.timertask;

import java.util.TimerTask;

import mmb.posadmin.service.OrderService;

import org.apache.log4j.Logger;

/**
 * 提交订单信息的定时任务
 */
public class SubmitOrderTask extends TimerTask {
	
	private static Logger log = Logger.getLogger(SubmitOrderTask.class);

	/**
	 * 定时任务运行体
	 */
	public void run() {
		OrderService os = new OrderService();
		try {
			//向中心库同步租赁订单信息
			log.info("向中心库同步租赁订单信息开始");
			os.syncLeaseOrderInfoToCenter();
			log.info("向中心库同步租赁订单信息结束");
		} catch (Exception e) {
			log.error("定时任务向中心库同步租赁订单信息时出现异常：", e);
		}
		
		try {
			//向中心库同步销售订单信息
			log.info("向中心库同步销售订单信息开始");
			os.syncSaledOrderInfoToCenter();
			log.info("向中心库同步销售订单信息结束");
		} catch (Exception e) {
			log.error("定时任务向中心库同步销售订单信息时出现异常：", e);
		}
	}

}
