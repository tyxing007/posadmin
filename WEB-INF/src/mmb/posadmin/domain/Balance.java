package mmb.posadmin.domain;

import java.sql.Timestamp;

/**
 * 收银员交接记录
 */
public class Balance {

	public int id;

	/**
	 * pos机编码
	 */
	public String posCode;

	/**
	 * 收银员id
	 */
	public int cashierId;

	/**
	 * 收银员
	 */
	public Cashier cashier;

	/**
	 * 上班时间
	 */
	public Timestamp startTime;

	/**
	 * 交接时间
	 */
	public Timestamp endTime;

	/**
	 * 理论现金
	 */
	public double theoryCash;

	/**
	 * POS机金额
	 */
	public double posCash;

	/**
	 * 接收人id
	 */
	public int handoverId;

	/**
	 * 接收人
	 */
	public Cashier handover;

	/**
	 * 初始POS机金额[银头]
	 */
	public double initPoscash;

	/**
	 * 追加POS机金额
	 */
	public double addPoscash;

	/**
	 * 备注
	 */
	public String backText;

	/**
	 * 是否日结
	 */
	public int isFinish;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPosCode() {
		return posCode;
	}

	public void setPosCode(String posCode) {
		this.posCode = posCode;
	}

	public int getCashierId() {
		return cashierId;
	}

	public void setCashierId(int cashierId) {
		this.cashierId = cashierId;
	}

	public Cashier getCashier() {
		return cashier;
	}

	public void setCashier(Cashier cashier) {
		this.cashier = cashier;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public double getTheoryCash() {
		return theoryCash;
	}

	public void setTheoryCash(double theoryCash) {
		this.theoryCash = theoryCash;
	}

	public double getPosCash() {
		return posCash;
	}

	public void setPosCash(double posCash) {
		this.posCash = posCash;
	}

	public int getHandoverId() {
		return handoverId;
	}

	public void setHandoverId(int handoverId) {
		this.handoverId = handoverId;
	}

	public Cashier getHandover() {
		return handover;
	}

	public void setHandover(Cashier handover) {
		this.handover = handover;
	}

	public double getInitPoscash() {
		return initPoscash;
	}

	public void setInitPoscash(double initPoscash) {
		this.initPoscash = initPoscash;
	}

	public double getAddPoscash() {
		return addPoscash;
	}

	public void setAddPoscash(double addPoscash) {
		this.addPoscash = addPoscash;
	}

	public String getBackText() {
		return backText;
	}

	public void setBackText(String backText) {
		this.backText = backText;
	}

	public int getIsFinish() {
		return isFinish;
	}

	public void setIsFinish(int isFinish) {
		this.isFinish = isFinish;
	}

}
