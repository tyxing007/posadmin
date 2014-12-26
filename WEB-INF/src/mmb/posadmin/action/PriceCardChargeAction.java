package mmb.posadmin.action;

import java.util.HashMap;
import java.util.Map;

import mmb.posadmin.domain.PriceCardCharge;
import mmb.posadmin.service.PriceCardChargeService;

import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionSupport;

public class PriceCardChargeAction extends ActionSupport{

	private static final long serialVersionUID = 5633416310268147362L;
    @SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(PriceCardChargeAction.class);
	
	private PriceCardChargeService pccs = new PriceCardChargeService();
	private PriceCardCharge priceCardCharge = new PriceCardCharge();
	private Page<PriceCardCharge> page = new Page<PriceCardCharge>();
	
	private String priceCardId;
	//消费类型
	private int consumeType;
	//充值时间
	private String chargeTime;
 
	public int getConsumeType() {
		return consumeType;
	}
	public void setConsumeType(int consumeType) {
		this.consumeType = consumeType;
	}
	public String getChargeTime() {
		return chargeTime;
	}
	public void setChargeTime(String chargeTime) {
		this.chargeTime = chargeTime;
	}
	
	public PriceCardCharge getPriceCardCharge() {
		return priceCardCharge;
	}
	public void setPriceCardCharge(PriceCardCharge priceCardCharge) {
		this.priceCardCharge = priceCardCharge;
	}
	public Page<PriceCardCharge> getPage() {
		return page;
	}
	public void setPage(Page<PriceCardCharge> page) {
		this.page = page;
	}
	
	public String getPriceCardId() {
		return priceCardId;
	}
	public void setPriceCardId(String priceCardId) {
		this.priceCardId = priceCardId;
	}
	
	/**
	 * 跳转到红蓝卡收支明细页面
	 * @return
	 */
	public String priceCardChargeList(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("pricecardid", this.priceCardId);
		param.put("consumetype", this.consumeType);
		param.put("chargetime", this.chargeTime);
		this.setPage(pccs.getPriceCardChargeList(this.page, param));
		return SUCCESS;
	}
	
}
