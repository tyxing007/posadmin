package mmb.posadmin.service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mmb.posadmin.action.Page;
import mmb.posadmin.domain.PosManager;
import mmboa.util.db.BaseService;
import mmboa.util.db.DbOperation;

public class PosManagerService extends BaseService{
	
	/**
	 * 更新Pos机的信息
	 * @param pm
	 * @return
	 */
	public boolean updatePos(PosManager pm){
		//更新语句
		StringBuilder set = new StringBuilder();
		set.append("`pos_code`='").append(pm.getPosCode()).append("', ");
		set.append("`pos_ip`='").append(pm.getPosIp()).append("'");
		return this.updateXXX(set.toString(), "`id`="+pm.getId(), "pos_manager");
	}
	
	/**
	 * 根据PosManger的id获取PosManager对象信息
	 * @param id 
	 * @return
	 */
	public PosManager getPosManagerById(int id) {
		return (PosManager) this.getXXX("`id`="+id, "pos_manager", PosManager.class.getName());
	}
	
	/**
	 * 删除POS机信息（假删）
	 * @param id 
	 * @return
	 */
	public boolean deletePosManagerById(int id) {
		return this.updateXXX("`is_delete`=1", "`id`="+id, "pos_manager");
	}
	
	
	/**
	 * POS机列表信息
	 * @param page
	 * @return
	 */
	public Page<PosManager> getPosManagerList(Page<PosManager> page){
		page = getPageFullValues(page);
		List<PosManager>  tmp = new ArrayList<PosManager>();
		DbOperation db = new DbOperation("oa");
		PosManager p ;
		try{
			StringBuilder sql = new StringBuilder(50);
			sql.append("select id,pos_code,pos_ip,create_time,oper_user from pos_manager where is_delete = 0 order by id desc limit ");
			sql.append(page.getFirstResult()).append(",").append(page.getPageCount());
			ResultSet rs = db.executeQuery(sql.toString());
			while(rs.next()){
			    p = new PosManager();
			    p.setId(rs.getInt("id"));
			    p.setPosCode(rs.getString("pos_code"));
			    p.setPosIp(rs.getString("pos_ip"));
			    p.setCreateTime(rs.getTimestamp("create_time"));
			    p.setOperUser(rs.getString("oper_user"));
			    tmp.add(p);
			}
			page.setList(tmp);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.release(db);
		}
		
		return page;
	}

	/**
	 *   获取当前分页的信息
	 * @param page
	 * @return
	 */
	public Page<PosManager> getPageFullValues(Page<PosManager> page){
		DbOperation db = new DbOperation("oa");
		try{
			StringBuilder sb = new StringBuilder(50);
			sb.append("select count(1) from pos_manager where is_delete = 0");
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
	 * 得到所有POS机的IP集合
	 * @return
	 */
	public Set<String> getPosIp(){
		Set<String> tmp = new HashSet<String>();
		DbOperation db = new DbOperation("oa");
		try{
			StringBuilder sb = new StringBuilder(50);
			sb.append("select pos_ip from pos_manager where is_delete = 0");
			ResultSet rs = db.executeQuery(sb.toString());
		    while(rs.next()){
		    	tmp.add(rs.getString("pos_ip"));
		    }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.release(db);
		}
		return tmp;
	}
	

}
