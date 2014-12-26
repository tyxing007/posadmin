package mmb.posadmin.timertask;

import java.util.TimerTask;

import mmb.posadmin.service.ProductService;

import org.apache.log4j.Logger;

/**
 * 同步商品信息的定时任务
 */
public class SyncProductTask extends TimerTask {
	
	private static Logger log = Logger.getLogger(SyncProductTask.class);

	/**
	 * 定时任务运行体
	 */
	public void run() {
		try {
			//同步商品信息
			log.info("同步商品信息开始");
			ProductService ps = new ProductService();
			ps.syncProductDataFromPoscenter();
			log.info("同步商品信息结束");
		} catch (Exception e) {
			log.error("定时任务同步商品信息时出现异常：", e);
		}
	}

}
