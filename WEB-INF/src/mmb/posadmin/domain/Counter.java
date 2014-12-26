package mmb.posadmin.domain;

public class Counter {
	 
	/**
	 * 商品销售总额统计
	 */
	 private double totalSales;
	 
	 /**
	  * 商品退货总额统计
	  */
	 private double totalBackSales;
	
	/**
	  * 商品销售订单总数
	  */
	 private int totalSaleOrders;
	 
	 /**
	  * 商品退货订单总数
	  */
	 private int totalBackSaleOrders;
	 
	 /**
	  * 商品租赁订单总数
	  */
	 private int totalLeaseOrders;
	 
	 /**
	  * 商品租赁归还订单总数
	  */
	 private int totalBackLeaseOrders;
	 
	 /**
	  * 押金总数
	  */
	 private double totalDeposit;
	 
	 /**
	  * 归还的押金总数
	  */
	 private double totalBackDeposit;

	/**
	  * 租金总数
	  */
	 private double totalLeaseCash;

	public double getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(double totalSales) {
		this.totalSales = totalSales;
	}

	public int getTotalSaleOrders() {
		return totalSaleOrders;
	}

	public void setTotalSaleOrders(int totalSaleOrders) {
		this.totalSaleOrders = totalSaleOrders;
	}

	public int getTotalBackSaleOrders() {
		return totalBackSaleOrders;
	}

	public void setTotalBackSaleOrders(int totalBackSaleOrders) {
		this.totalBackSaleOrders = totalBackSaleOrders;
	}

	public int getTotalLeaseOrders() {
		return totalLeaseOrders;
	}

	public void setTotalLeaseOrders(int totalLeaseOrders) {
		this.totalLeaseOrders = totalLeaseOrders;
	}

	public int getTotalBackLeaseOrders() {
		return totalBackLeaseOrders;
	}

	public void setTotalBackLeaseOrders(int totalBackLeaseOrders) {
		this.totalBackLeaseOrders = totalBackLeaseOrders;
	}

	public double getTotalDeposit() {
		return totalDeposit;
	}

	public void setTotalDeposit(double totalDeposit) {
		this.totalDeposit = totalDeposit;
	}

	public double getTotalLeaseCash() {
		return totalLeaseCash;
	}

	public void setTotalLeaseCash(double totalLeaseCash) {
		this.totalLeaseCash = totalLeaseCash;
	}

	public double getTotalBackSales() {
		return totalBackSales;
	}

	public void setTotalBackSales(double totalBackSales) {
		this.totalBackSales = totalBackSales;
	}

	public double getTotalBackDeposit() {
		return totalBackDeposit;
	}

	public void setTotalBackDeposit(double totalBackDeposit) {
		this.totalBackDeposit = totalBackDeposit;
	}
	 

}
