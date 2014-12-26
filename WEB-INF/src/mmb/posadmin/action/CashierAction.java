package mmb.posadmin.action;

import java.util.List;

import mmb.posadmin.domain.Cashier;
import mmb.posadmin.service.CashierService;
import mmboa.util.Secure;

import org.apache.commons.lang3.StringUtils;

import com.opensymphony.xwork2.ActionSupport;

public class CashierAction extends ActionSupport {

	
	private static final long serialVersionUID = -7963363088246879135L;
	
	private List<Cashier> list ;
	
	private Cashier cashier  = new Cashier();
	
	private Page<Cashier> page;
	
	private CashierService  cashierService = new CashierService();
	
	public List<Cashier> getList() {
		return list;
	}

	public void setList(List<Cashier> list) {
		this.list = list;
	}

	public Cashier getCashier() {
		return cashier;
	}

	public void setCashier(Cashier cashier) {
		this.cashier = cashier;
	}

	public Page<Cashier> getPage() {
		return page;
	}

	public void setPage(Page<Cashier> page) {
		this.page = page;
	}
	
	
	/**
	 * 跳转至收银员列表界面
	 * @return
	 */
	public String cashierList(){
		if(this.page == null){
			this.page = new Page<Cashier>();
		}
		setPage(cashierService.getCashierList(page));
		setList(this.page.list);
		return SUCCESS;
	}

	/**
	 * 跳转到商品表单页面
	 * @return
	 */
	public String toCashierFormView() {
		//修改
		if(cashier.getId() != 0) {
			this.cashier = cashierService.getCashierById(cashier.getId());
		}

		return INPUT;
	}
	
	/**
	 * 跳转到商品详情页面
	 * @return
	 */
	public String toCashierDetailView() {
		//获取商品信息
		this.cashier = cashierService.getCashierById(cashier.getId());
		
		return "detail";
	}
	
	/**
	 * 保存商品信息
	 * @return
	 */
	public String saveCashier() {
		//新建
		if(cashier.getId() == 0) {
			cashier.setPassword(Secure.encryptPwd(cashier.getPassword())); //对密码进行加密
			cashierService.addXXX(cashier, "cashier");
		}
		//修改
		else {
			if(StringUtils.isNotBlank(cashier.getPassword())) {
				cashier.setPassword(Secure.encryptPwd(cashier.getPassword())); //对密码进行加密
			}
			cashierService.updateCashier(cashier);
		}
		
		return this.cashierList();
	}
	
	/**
	 * 删除商品信息
	 * @return
	 */
	public String deleteCashier() {
		//删除
		cashierService.deleteCashierById(this.cashier.getId());
		
		return this.cashierList();
	}
	
	

}
