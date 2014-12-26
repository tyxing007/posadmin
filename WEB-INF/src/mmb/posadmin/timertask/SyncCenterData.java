package mmb.posadmin.timertask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;

import mmboa.util.Constants;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class SyncCenterData {

	private static Logger log = Logger.getLogger(SyncCenterData.class);
	
	/**
	 * 启动同步定时任务
	 */
	public void startSyncTask() {
		//同步中心库数据的时间
		String syncCenterDataTime = Constants.config.getProperty("syncCenterDataTime");
		if(StringUtils.isNotBlank(syncCenterDataTime)) {
			log.info("启动同步中心库数据的定时任务...");
			int syncHour = Integer.parseInt(syncCenterDataTime.split(":")[0]); //时
			int syncMinute = Integer.parseInt(syncCenterDataTime.split(":")[1]); //分
			
			//计算首次执行任务的时间
			Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, syncHour);
			c.set(Calendar.MINUTE, syncMinute);
			c.set(Calendar.SECOND, 0);
			c.add(Calendar.DAY_OF_MONTH, 1);
			log.info("首次执行同步任务的时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getTime()));
			
			//定时周期
			long period = 24 * 3600 * 1000;
			
			//创建定时器
			Timer _timer = new Timer();
			
			//同步商品分类的定时任务
			_timer.scheduleAtFixedRate(new SyncGoodsClassTask(), c.getTime(), period);
			
			//同步商品分类的定时任务
			c.add(Calendar.MINUTE, 1);
			_timer.scheduleAtFixedRate(new SyncProductTask(), c.getTime(), period);
			
			//提交店面库存的定时任务
			c.add(Calendar.MINUTE, 1);
			_timer.scheduleAtFixedRate(new SubmitShopStockTask(), c.getTime(), period);
			
			//提交店面进销存的定时任务
			c.add(Calendar.MINUTE, 1);
			_timer.scheduleAtFixedRate(new SubmitInvoiceTask(), c.getTime(), period);
			
			//同步会员信息的定时任务
			c.add(Calendar.MINUTE, 1);
			_timer.scheduleAtFixedRate(new SyncMemberTask(), c.getTime(), period);
			
			//提交订单信息的定时任务
			c.add(Calendar.MINUTE, 1);
			_timer.scheduleAtFixedRate(new SubmitOrderTask(), c.getTime(), period);
			
			log.info("同步中心库数据的定时任务启动成功！");
		}
    }
}
