package mmb.posadmin.timertask;

import java.util.TimerTask;

import mmb.posadmin.service.MemberService;

import org.apache.log4j.Logger;

/**
 * 同步会员信息的定时任务
 */
public class SyncMemberTask extends TimerTask {
	
	private static Logger log = Logger.getLogger(SyncMemberTask.class);

	/**
	 * 定时任务运行体
	 */
	public void run() {
		MemberService ms = new MemberService();
		try {
			//同步本地会员更改信息去中心库
			log.info("同步本地会员更改信息去中心库开始");
			ms.syncMemberInfoToPoscenter();
			log.info("同步本地会员更改信息去中心库结束");
		} catch (Exception e) {
			log.error("定时任务同步本地会员更改信息去中心库时出现异常：", e);
			return ;
		}
		
		try {
			//从中心库同步会员基本信息
			log.info("从中心库同步会员基本信息开始");
			ms.syncMemberInfoFromPoscenter();
			log.info("从中心库同步会员基本信息结束");
		} catch (Exception e) {
			log.error("定时任务从中心库同步会员基本信息时出现异常：", e);
		}
	}

}
