package mmb.posadmin.domain;

import java.util.List;

/**
 * 同步红蓝卡信息用到的实体
 *    [仅同步过程使用]
 * @author qiuranke
 *
 */
public class SyncPriceCardInfo {
	
	public List<PriceCard> priceCardList ;
	
	public List<PriceCardCharge> priceCardChargeList;

	public List<PriceCard> getPriceCardList() {
		return priceCardList;
	}

	public void setPriceCardList(List<PriceCard> priceCardList) {
		this.priceCardList = priceCardList;
	}

	public List<PriceCardCharge> getPriceCardChargeList() {
		return priceCardChargeList;
	}

	public void setPriceCardChargeList(List<PriceCardCharge> priceCardChargeList) {
		this.priceCardChargeList = priceCardChargeList;
	}
	
    
}
