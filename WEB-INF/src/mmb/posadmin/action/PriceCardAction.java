package mmb.posadmin.action;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.posadmin.domain.PriceCard;

import mmb.posadmin.service.PriceCardService;
import mmb.posadmin.util.ResponseUtils;


import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class PriceCardAction extends ActionSupport {

	private static final long serialVersionUID = -1886742717826609109L;
    @SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(PriceCardAction.class);
    private HttpServletRequest request = ServletActionContext.getRequest();
	
	private PriceCardService pcs = new PriceCardService();
	private PriceCard priceCard = new PriceCard();
	private Page<PriceCard> page = new Page<PriceCard>();
	private List<PriceCard> list = new ArrayList<PriceCard>();
	private String clerkName;
	private String priceCardId;
	private int type;
	private int state;
	//消费类型
	private int consumeType;
	
	public PriceCard getPriceCard() {
		return priceCard;
	}
	public void setPriceCard(PriceCard priceCard) {
		this.priceCard = priceCard;
	}
	public Page<PriceCard> getPage() {
		return page;
	}
	public void setPage(Page<PriceCard> page) {
		this.page = page;
	}
	public List<PriceCard> getList() {
		return list;
	}
	public void setList(List<PriceCard> list) {
		this.list = list;
	}
	public String getClerkName() {
		return clerkName;
	}
	public void setClerkName(String clerkName) {
		this.clerkName = clerkName;
	}
	public String getPriceCardId() {
		return priceCardId;
	}
	public void setPriceCardId(String priceCardId) {
		this.priceCardId = priceCardId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getConsumeType() {
		return consumeType;
	}
	public void setConsumeType(int consumeType) {
		this.consumeType = consumeType;
	}
	
	public String priceCardList(){
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("state", this.state);
		param.put("clerkname", this.clerkName);
		param.put("pricecardid", this.priceCardId);
		param.put("type", this.type);
		this.setPage(pcs.getPriceCardList(this.page, param));
		this.setList(this.page.getList());
		return SUCCESS;
	}
	
	public String priceCardChargeList(){
		
		return "chargeList";
	}

	//跳转到红蓝卡详细信息界面
	public String toPriceCardDtail(){
		this.setPriceCard(pcs.getPriceCardById(this.priceCard.getId()));
		return "detail";
	}
	
	/**
	 * 从中心库同步红蓝卡信息
	 */
	public void syncPriceCardInfo() {
		String result = "true";
		try {
			//加一个application范围的锁
			synchronized (request.getServletContext()) {
				pcs.syncPriceCardInfoFromCenter();
			}
		} catch (Exception e) {
			result = "从中心库同步红蓝卡信息时出现异常："+e;
		}
		ResponseUtils.renderText(ServletActionContext.getResponse(), result);
	}

}