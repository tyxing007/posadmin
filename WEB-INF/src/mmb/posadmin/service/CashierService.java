package mmb.posadmin.service;

import java.io.StringReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mmb.posadmin.action.Page;
import mmb.posadmin.domain.Cashier;
import mmb.system.admin.AdminService;
import mmboa.base.voUser;
import mmboa.util.LogUtil;
import mmboa.util.Secure;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbOperation;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.stream.JsonReader;

public class CashierService extends BaseService {
	
	private static Logger log = Logger.getLogger(CashierService.class);
	
	/**
	 * 分页获取收银员列表信息
	 * @param page
	 * @return
	 */
	public Page<Cashier> getCashierList(Page<Cashier> page) {
		page = getPageFullValues(page);
		List<Cashier> tmp = new ArrayList<Cashier>();
		DbOperation db = new DbOperation();
		try {
			StringBuilder sql = new StringBuilder(50);
			sql.append("select id,name,id_card,username,password,forbidden,sale_type from cashier where is_delete=0 order by id desc limit ");
			sql.append(page.getFirstResult()).append(",").append(page.getPageCount());
			ResultSet rs = db.executeQuery(sql.toString());
			Cashier c;
			while (rs.next()) {
				c = new Cashier();
				c.setId(rs.getInt("id"));
				c.setName(rs.getString("name"));
				c.setIdCard(rs.getString("id_card"));
				c.setUsername(rs.getString("username"));
				c.setPassword(rs.getString("password"));
				c.setForbidden(rs.getInt("forbidden"));
				c.setSaleType(rs.getInt("sale_type"));
				tmp.add(c);
			}
			page.setList(tmp);
		} catch (Exception e) {
			log.error("分页获取收银员列表信息时出现异常：", e);
		} finally {
			this.release(db);
		}
		return page;
	}

	/**
	 *   获取当前分页的信息
	 * @param page
	 * @return
	 */
	public Page<Cashier> getPageFullValues(Page<Cashier> page){
		DbOperation db = new DbOperation("oa");
		try{
			StringBuilder sb = new StringBuilder(50);
			sb.append("select count(1) from cashier where is_delete = 0");
			ResultSet rs = db.executeQuery(sb.toString());
		    if(rs.next()){
		    	page.setTotalRecords(rs.getInt(1));
		    }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.release(db);
		}
		return page;
	}
	
	/**
	 * 根据收银员id获取收银员对象信息
	 * @param id 收银员id
	 * @return
	 */
	public Cashier getCashierById(int id) {
		return (Cashier) this.getXXX("`id`="+id, "cashier", Cashier.class.getName());
	}
	
	/**
	 * 根据用户名获取收银员对象信息
	 * @param username 用户名
	 * @return
	 */
	public Cashier getCashierByUsername(String username) {
		return (Cashier) this.getXXX("`username`='"+username+"'", "cashier", Cashier.class.getName());
	}
	
	/**
	 * 更新收银员信息
	 * @param product 收银员信息
	 * @return
	 */
	public boolean updateCashier(Cashier cashier) {
		//更新语句
		StringBuilder set = new StringBuilder();
		set.append("`name`='").append(cashier.getName()).append("' ");
		set.append(",`id_card`='").append(cashier.getIdCard()).append("' ");
		set.append(",`username`='").append(cashier.getUsername()).append("' ");
		set.append(",`forbidden`='").append(cashier.getForbidden()).append("' ");
		set.append(",sale_type=").append(cashier.getSaleType());
		if(StringUtils.isNotBlank(cashier.getPassword())) {
			set.append(",`password`='").append(cashier.getPassword()).append("'");
		}
		return this.updateXXX(set.toString(), "`id`="+cashier.getId(), "cashier");
	}
	
	/**
	 * 删除收银员信息（假删）
	 * @param id 收银员id
	 * @return
	 */
	public boolean deleteCashierById(int id) {
		return this.updateXXX("`is_delete`=1", "`id`="+id, "cashier");
	}
	
	/**
	 * POS机根据用户名和密码获取收银员信息
	 * @param json POS机传递的JSON数据
	 * @return
	 */
	public String posGetCashier(String json) {
		StringBuffer jsonResult = new StringBuffer(); //返回结果
		try {
			String username = null; //用户名
			String password = null; //密码
			String posCode = null;//POS机编码
			JsonReader jr = new JsonReader(new StringReader(json));
			jr.beginObject();
			String attrName = null;
			while (jr.hasNext()) {
				attrName = jr.nextName();
				if ("username".equals(attrName)) {
					username = jr.nextString();
				} else if ("password".equals(attrName)) {
					password = jr.nextString();
				}else if("posCode".equals(attrName)){
					posCode = jr.nextString();
				}
			}
			jr.endObject();
			
			//根据用户名获取收银员信息
			Cashier cashier = this.getCashierByUsername(username);
			
			//获取是否录过银头的状态信息
			BalanceService bs = new BalanceService();
			String posState = bs.getPosState(posCode);
			
			//返回结果(JSON格式)
			jsonResult.append("{");
			if(cashier == null) {
				jsonResult.append("\"message\":\"用户名不存在\"");
			} else if(!cashier.getPassword().equals(Secure.encryptPwd(password))) {
				jsonResult.append("\"message\":\"密码不正确\"");
			} else {
				jsonResult.append("\"id\":\"").append(cashier.getId()).append("\"");
				jsonResult.append(",\"name\":\"").append(cashier.getName().replaceAll("\"", "\\\\\"")).append("\"");
				jsonResult.append(",\"idCard\":\"").append(cashier.getIdCard()).append("\"");
				jsonResult.append(",\"username\":\"").append(cashier.getUsername()).append("\"");
				jsonResult.append(",\"posState\":\"").append(posState).append("\"");
				jsonResult.append(",\"forbidden\":\"").append(cashier.getForbidden()).append("\"");
				jsonResult.append(",\"isDelete\":").append(cashier.getIsDelete());
			}
			jsonResult.append("}");
		} catch (Exception e) {
			jsonResult = new StringBuffer("{\"message\":\"系统出现异常"+e+"\"}");
			log.error("POS机根据用户名和密码获取收银员信息时出现异常：", e);
		}
		return jsonResult.toString();
	}

	/**
	 * pos机根据用户名和密码获取店长信息
	 * @param json pos机传递的json数据
	 * @return
	 */
	public String posGetShopManager(String json) {
		StringBuffer jsonResult = new StringBuffer(); //返回结果
		try {
			String username = null; //用户名
			String password = null; //密码
			JsonReader jr = new JsonReader(new StringReader(json));
			jr.beginObject();
			String attrName = null;
			while (jr.hasNext()) {
				attrName = jr.nextName();
				if ("username".equals(attrName)) {
					username = jr.nextString();
				} else if ("password".equals(attrName)) {
					password = jr.nextString();
				}
			}
			jr.endObject();
			
			//根据用户名获取店长信息
			voUser user = new AdminService().getAdmin(username);
			
			//返回结果(json格式)
			jsonResult.append("{");
			if(user == null) {
				jsonResult.append("\"message\":\"用户名不存在\"");
			} else if(!user.getPassword().equals(Secure.encryptPwd(password))) {
				jsonResult.append("\"message\":\"密码不正确\"");
			} else {
				jsonResult.append("\"id\":\"").append(user.getId()).append("\"");
				jsonResult.append(",\"name\":\"").append(user.getName().replaceAll("\"", "\\\\\"")).append("\"");
				jsonResult.append(",\"username\":\"").append(user.getUsername()).append("\"");
			}
			jsonResult.append("}");
		} catch (Exception e) {
			jsonResult = new StringBuffer("{\"message\":\"系统出现异常\"}");
			e.printStackTrace();
			LogUtil.logAccess("pos机根据用户名和密码获取店长信息时出现异常："+e.getMessage());
		}
		
		return jsonResult.toString();
	}

}
