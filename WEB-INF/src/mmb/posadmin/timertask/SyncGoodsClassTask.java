package mmb.posadmin.timertask;

import java.util.TimerTask;

import mmb.posadmin.service.GoodsClassService;

import org.apache.log4j.Logger;

/**
 * 同步商品分类的定时任务
 */
public class SyncGoodsClassTask extends TimerTask {
	
	private static Logger log = Logger.getLogger(SyncGoodsClassTask.class);

	/**
	 * 定时任务运行体
	 */
	public void run() {
		try {
			//同步商品分类
			log.info("同步商品分类开始");
			GoodsClassService gcs = new GoodsClassService();
			gcs.syncGoodsClassDataFromPoscenter();
			log.info("同步商品分类结束");
		} catch (Exception e) {
			log.error("定时任务同步商品分类信息时出现异常：", e);
		}
	}

}
