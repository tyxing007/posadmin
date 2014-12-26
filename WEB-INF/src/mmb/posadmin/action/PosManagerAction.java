package mmb.posadmin.action;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import mmb.framework.IConstants;
import mmb.posadmin.domain.PosManager;
import mmb.posadmin.service.PosManagerService;
import mmboa.base.voUser;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class PosManagerAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2086207515336487092L;

	private PosManager posManager = new PosManager();

	private PosManagerService pms = new PosManagerService();
    
	private List<PosManager> list;
	
	private Page<PosManager> page;

	public PosManager getPosManager() {
		return posManager;
	}

	public void setPosManager(PosManager posManager) {
		this.posManager = posManager;
	}

	public PosManagerService getPms() {
		return pms;
	}

	public void setPms(PosManagerService pms) {
		this.pms = pms;
	}

	public Page<PosManager> getPage() {
		return page;
	}

	public void setPage(Page<PosManager> page) {
		this.page = page;
	}
    
	public List<PosManager> getList() {
		return list;
	}

	public void setList(List<PosManager> list) {
		this.list = list;
	}

	/**
	 * 新增pos机界面
	 * @return
	 */
	public String addPos() {
		if(this.posManager.getId() != 0){
			this.posManager =pms.getPosManagerById(posManager.getId());
		}
		//获取当前用户信息
		voUser vo = (voUser) ServletActionContext.getRequest().getSession().getAttribute(IConstants.USER_VIEW_KEY);
		this.posManager.setOperUser(vo.getCode());
		return "add";
	}

	/**
	 * POS机列表信息页面
	 * @return
	 */
	public String posManagerList() {
		if(this.page == null){
			this.page = new Page<PosManager>();
		}
		setPage(pms.getPosManagerList(page));
		setList(this.page.list);
		return SUCCESS;
	}
	
	/**
	 * 删除POS机信息
	 * @return
	 */
	public String deletePosManager() {
		//删除
		pms.deletePosManagerById(this.posManager.getId());
		return this.posManagerList();
	}
    
	/**
	 * 跳转到POS机详细信息页面
	 * @return
	 */
	public String toPosManagerFormView() {
		if(posManager.getId() != 0) {
			this.posManager = pms.getPosManagerById(posManager.getId());
		}
		return "detail";
	}
	
	
	/**
	 * 保存POS机信息
	 * @return
	 */
	public String savePos() {
		// 新建
		if (posManager.getId() == 0) {
			posManager.setCreateTime(new Timestamp(new Date().getTime()));
			pms.addXXX(posManager, "pos_manager");
		}
		// 修改
		else {
			pms.updatePos(posManager);
		}

		return this.posManagerList();
	}

}
