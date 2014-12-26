<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查看换购活动</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript">
var swapBuyEvent={};

//返回列表
swapBuyEvent.backList = function(){
	window.location.href = $('#initPath').val()+'/pos/event!eventList.do';
}
</script>
</head>
<body style="min-height:500px;">
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 促销活动管理 - 查看换购活动</div>
	<div class="ropt"></div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
<table cellspacing="1" cellpadding="2" border="0" width="100%" class="pn-ftable" style="font-size:13px;">
	<tr>
		<td width="12%" class="pn-flabel pn-flabel-h">活动类型：</td>
		<td width="88%" class="pn-fcontent">换购</td>
	</tr>
	<tr>
		<td width="12%" class="pn-flabel pn-flabel-h">开始时间:</td>
		<td width="88%" class="pn-fcontent"><fmt:formatDate value="${event.startTime}" pattern="yyyy-MM-dd HH:mm" /></td>
	</tr>
	<tr>
		<td width="12%" class="pn-flabel pn-flabel-h">结束时间:</td>
		<td width="88%" class="pn-fcontent"><fmt:formatDate value="${event.endTime}" pattern="yyyy-MM-dd HH:mm" /></td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h">规则描述:</td>
		<td class="pn-fcontent">${event.ruleDesc }</td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h">具体规则:</td>
		<td class="pn-fcontent">
			<table id="productListTable" width="100%" class="pn-ftable" cellspacing="1">
				<tr align="center" style="">
					<td class="pn-fcontent" width="200">需消费金额</td>
					<td class="pn-fcontent">添加金额</td>
					<td class="pn-fcontent">换购商品</td>
				</tr>
				<c:forEach var="swapBuyEvent" items="${event.swapBuyEventList}">
				<tr align="center">
					<td class="pn-fcontent">${swapBuyEvent.money}</td>
					<td class="pn-fcontent">${swapBuyEvent.appendMoney}</td>
					<td class="pn-fcontent">
						<c:forEach var="swapBuyProduct" items="${swapBuyEvent.swapBuyProductList}" >
							${swapBuyProduct.giftProductName}<br/>
						</c:forEach>
		            </td>
				</tr>
			   </c:forEach>
			</table>
		</td>
	</tr>
	<tr>
		<td class="pn-fcontent" align="center" colspan="2">
			<input type="button" class="return-button" value="返回" onclick="swapBuyEvent.backList();" />
		</td>
	</tr>
</table>
</div>
</body>
</html>