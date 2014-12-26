package mmb.posadmin.timertask;

import java.util.TimerTask;

import mmb.posadmin.service.InvoiceService;

import org.apache.log4j.Logger;

/**
 * 提交店面进销存的定时任务
 */
public class SubmitInvoiceTask extends TimerTask {
	
	private static Logger log = Logger.getLogger(SubmitInvoiceTask.class);

	/**
	 * 定时任务运行体
	 */
	public void run() {
		try {
			//提交店面进销存信息
			log.info("提交店面进销存信息开始");
			InvoiceService is = new InvoiceService();
			is.syncInvoiceInfoToPoscenter();
			log.info("提交店面进销存信息结束");
		} catch (Exception e) {
			log.error("定时任务提交店面进销存信息时出现异常：", e);
		}
	}

}
