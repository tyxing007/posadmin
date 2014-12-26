package mmb.posadmin.domain;

import java.sql.Timestamp;
import java.util.List;

/**
 * 调价卡信息
 * @author qiuranke
 */
public class PriceCard {
	
	//调价卡卡号
	public String id;
	
	//店员姓名
	public String clerkName;
	
	//开卡时间
	public Timestamp openTime;
	
	//供应商id
	public int supplierId;
	
	/**
	 * 使用状态[1:白卡；2:使用中；3:停用]
	 */
	public int state;
	
	//会员卡密码
	public String password;
	
	//会员卡余额
	public double point;
	
	/**
	 * 类型[1:红卡，2:蓝卡]
	 */
	public int type;
	
	public List<PriceCardCharge> priceCardChargeList;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClerkName() {
		return clerkName;
	}

	public void setClerkName(String clerkName) {
		this.clerkName = clerkName;
	}

	public Timestamp getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Timestamp openTime) {
		this.openTime = openTime;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public double getPoint() {
		return point;
	}

	public void setPoint(double point) {
		this.point = point;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<PriceCardCharge> getPriceCardChargeList() {
		return priceCardChargeList;
	}

	public void setPriceCardChargeList(List<PriceCardCharge> priceCardChargeList) {
		this.priceCardChargeList = priceCardChargeList;
	}

}
