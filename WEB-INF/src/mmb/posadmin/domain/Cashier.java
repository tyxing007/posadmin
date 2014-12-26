package mmb.posadmin.domain;

import java.io.Serializable;

/**
 * 收银员
 */
public class Cashier implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 序号
	 */
	public int id ;
	
	/**
	 * 姓名
	 * 
	 */
	public String name;
	
	/**
	 * 身份证号
	 */
	public String idCard;
	
	/**
	 * 用户名
	 */
	public String username;
	
	/**
	 * 密码
	 */
	public String password;
	
	/**
	 * 销售价类型[1:仅标牌价；2:突破标牌价；3:突破限价；4:突破锁价]
	 */
	public int saleType;
	
	/**
	 * 是否删除[0:未删除；1:已删除]
	 */
	public int isDelete;
    
	/**
	 * 是否禁用[0:未禁用；1:已禁用]
	 */
	public int forbidden;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getSaleType() {
		return saleType;
	}

	public void setSaleType(int saleType) {
		this.saleType = saleType;
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	public int getForbidden() {
		return forbidden;
	}

	public void setForbidden(int forbidden) {
		this.forbidden = forbidden;
	}
	
}
