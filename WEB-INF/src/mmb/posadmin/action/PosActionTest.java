package mmb.posadmin.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * pos机与posadmin接口 测试类
 * 
 */
public class PosActionTest {
	public static final String BASE_URL = "http://192.168.5.231:8080/admin";

	//pos机查询收银员信息
	public static Map<String, String> getCashier() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("path", "/enter/pos!getCashier.do");
		String username = "99";
		String password = "5";
		param.put("body", "{\"username\":\""+username+"\", \"password\":\""+password+"\"}");
		return param;
	}
	
	//pos端录入银头信息
	public static Map<String, String> initBalance() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("path", "/enter/pos!initBalance.do");
		param.put("body", "{\"posCode\":\"06\",\"initPoscash\":100.0,\"cashierId\":1}");
		return param;
	}
	
	//新增会员信息
	public static Map<String, String> addNewMember() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("path", "/enter/pos!addNewMember.do");
		param.put("body", "{\"id\":\";888000000=0000000002?\",\"name\":\"001\",\"idCard\":\"\",\"availableBalance\":100.0,\"cashierId\":13,\"posCode\":\"01\",\"mobile\":\"130\"}");
		return param;
	}
	
	//保存销售订单
	public static Map<String, String> syncSaleInfoToPos() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("path", "/enter/pos!syncSaleInfoToPos.do");
		String body = "{\"products\":[{\"id\":109000,\"count\":1,\"prePrice\":3000.0}],\"memberId\":\"\",\"serialId\":\"0100000007\",\"price\":3000.0,\"cash\":3000.0,\"cashierId\":13,\"posCode\":\"01\"}";
		body = "{\"products\":[{\"id\":14,\"count\":1,\"prePrice\":888.0}],\"memberId\":\";888000001=0000000002?\",\"serialId\":\"0100000024\",\"price\":88.0,\"cash\":929.0,\"cashierId\":13,\"posCode\":\"01\"}";
		body = "{\"productList\":[{\"productId\":109035,\"count\":1,\"prePrice\":9.0},{\"productId\":109034,\"count\":1,\"prePrice\":15.0}],\"moneySourceList\":[{\"swipCardNumber\":\"154817056144\",\"type\":3,\"money\":24.0}],\"memberId\":\"\",\"serialNumber\":\"0400000400\",\"price\":24.0,\"cashierId\":6,\"posCode\":\"02\"}";
		body = "{\"productList\":[{\"productId\":109050,\"count\":1,\"prePrice\":1000.0,\"eventRemark\":\"\",\"redId\":null,\"blueId\":null,\"redPoint\":0.0,\"bluePoint\":0.0},{\"productId\":109046,\"count\":1,\"prePrice\":1800.0,\"eventRemark\":\"\",\"redId\":\";888000001=0000000001?\",\"blueId\":\"\",\"redPoint\":799.0,\"bluePoint\":0.0}],\"moneySourceList\":[{\"type\":1,\"money\":2800.0}],\"saledOrderEventList\":[],\"memberId\":\"\",\"serialNumber\":\"0022222\",\"price\":2800.0,\"cashierId\":1,\"posCode\":\"01\",\"score\":0}";
		param.put("body", body);
		return param;
	}
	
	//会员充值
	public static Map<String, String> memberRecharge() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("path", "/enter/pos!memberRecharge.do");
		String body = "{\"memberId\":\";888000001=0000000002?\",\"money\":2000, \"posCode\":\"01\", \"cashierId\":1}";
		param.put("body", body);
		return param;
	}
	
	//会员提现
	public static Map<String, String> doWithdrawMemberCash() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("path", "/enter/pos!doWithdrawMemberCash.do");
		String body = "{\"memberId\":\";888000001=0000000002?\",\"withdrawCash\":50, \"posCode\":\"01\", \"cashierId\":1}";
		param.put("body", body);
		return param;
	}
	
	//保存租赁订单
	public static Map<String, String> saveLeaseOrder() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("path", "/enter/pos!saveLeaseOrder.do");
		String body = "{\"memberId\":\";888000001=0000000002?\",\"posCode\":\"01\",\"cashierId\":1,\"cash\":0,\"serialId\":\"zulin001\",\"price\":100,\"deposit\":200,\"products\":[{\"id\":108999,\"count\":1,\"prePrice\":100,\"perDeposit\":200,\"startTime\":1,\"endTime\":xxx,\"timeLength\":1,\"leaseStyle\":0}],”memlease”:[{“id”:xxx,“memberId”:xxx,“productId”:xxx,”startTime”:xxx,”endTime”:xxx,”serialNumber”:”xxx”,”type”:xxx}]}";
		param.put("body", body);
		return param;
	}
	
	//获取活动列表
	public static Map<String, String> getEventList() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("path", "/enter/pos!getEventList.do");
		String body = "";
		param.put("body", body);
		return param;
	}
	
	//查询销售商品信息接口
	public static Map<String, String> getSaledProduct() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("path", "/enter/pos!getSaledProduct.do");
		String body = "{\"serialId\":\"0100000085\",\"barCode\":\"1368091767448\"}";
		param.put("body", body);
		return param;
	}
	
	//pos机根据会员id查询会员信息
	public static Map<String, String> posGetMemberById() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("path", "/enter/pos!posGetMemberById.do");
		String body = "{\"id\":\";888000001=0000000002?\"}";
		param.put("body", body);
		return param;
	}
	
	//pos机根据会员id查询会员信息
	public static Map<String, String> backSaledOrder() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("path", "/enter/pos!backSaledOrder.do");
		String body = "{\"serialNumber\":\"0110000013\",\"posCode\":\"01\",\"cashierId\":1,\"memberId\":\";888000001=0000000002?\",\"price\":10,\"score\":10,\"productList\":[{\"productId\":109046,\"count\":1,\"prePrice\":10,\"eventRemark\":\"\"}],\"moneySourceList\":[{\"id\":203,\"orderId\":417,\"type\":2,\"money\":10,\"withdrawMoney\":0.0,\"swipCardNumber\":\"\"}],\"moneyDestinationList\":[{\"type\":2,\"money\":10}]}";
		param.put("body", body);
		return param;
	}
	
	//获取调价单
	public static Map<String, String> getAdjustPriceBill() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("path", "/enter/pos!getAdjustPriceBill.do");
		String body = "{\"billNumber\":\"000001\"}";
		param.put("body", body);
		return param;
	}
	
	//获取红蓝卡信息
	public static Map<String, String> getPriceCard() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("path", "/enter/pos!getPriceCard.do");
		String body = "{\"id\":\"001\",\"password\":\"123\"}";
		param.put("body", body);
		return param;
	}
	
	public static void main(String[] args) {
		try {
			//pos机查询收银员信息
			Map<String, String> param = null;
			//param = PosActionTest.getCashier();
			//param = PosActionTest.initBalance();
			//param = PosActionTest.addNewMember();
			param = PosActionTest.syncSaleInfoToPos();
			//param = PosActionTest.memberRecharge();
			//param = PosActionTest.doWithdrawMemberCash();
			//param = PosActionTest.getEventList();
			//param = PosActionTest.getSaledProduct();
			//param = PosActionTest.posGetMemberById();
			//param = PosActionTest.backSaledOrder();
			//param = PosActionTest.getAdjustPriceBill();
			//param = PosActionTest.getPriceCard();
			
			
			URL url = new URL(BASE_URL + param.get("path"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("content-type", "text/html");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(50000);
			conn.setReadTimeout(50000);
			conn.connect();
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
			out.write(param.get("body"));
			out.flush();

			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuffer sb = new StringBuffer();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();

			System.out.println("result===" + sb.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
