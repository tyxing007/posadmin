package mmb.posadmin.timertask;

import java.util.TimerTask;

import mmb.posadmin.service.ProductService;

import org.apache.log4j.Logger;

/**
 * 提交店面库存的定时任务
 */
public class SubmitShopStockTask extends TimerTask {
	
	private static Logger log = Logger.getLogger(SubmitShopStockTask.class);

	/**
	 * 定时任务运行体
	 */
	public void run() {
		try {
			//同步商品信息
			log.info("提交店面库存信息开始");
			ProductService ps = new ProductService();
			ps.submitShopStockToCenter();
			log.info("提交店面库存信息结束");
		} catch (Exception e) {
			log.error("定时任务提交店面库存信息时出现异常：", e);
		}
	}

}
