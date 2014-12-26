<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>收货单条目列表</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.validate.js"></script>
<script type="text/javascript">
var receiveOrderItem={};

//表单验证
receiveOrderItem.validateForm = function(){
	if($('#receiveOrderForm').valid()){
		return true;
	}else{
		return false;
	}
}

//返回列表
receiveOrderItem.backList = function(){
	window.location.href = $('#initPath').val()+'/pos/receiveOrder!receiveOrderList.do';
}

//确认收货
receiveOrderItem.confirmReceive = function(){
	if(!$('#receiveOrderForm').valid()){
		return false;
	}
	
	if(confirm('确认后将不能修改收货单信息，确定提交该收货单吗？')){
		$("#receiveOrderForm").submit();
	}
}

//导出Excel
receiveOrderItem.exportExcel = function(receiveOrderId){
	window.location.href = $('#initPath').val()+'/pos/receiveOrder!exportExcel.do?receiveOrder.id='+receiveOrderId;
}

$(document).ready(function(){
	//验证表单
	$("#receiveOrderForm").validate();

});
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 收货单管理 - 收货单条目列表</div>
	<div class="ropt">
		<c:if test="${receiveOrder.useStatus == 0}">
			<input type="button" onclick="receiveOrderItem.confirmReceive();" class="" value="确认收货" /> &nbsp; 
		</c:if>
		<c:if test="${receiveOrder.useStatus == 1}">
			<input type="button" onclick="receiveOrderItem.exportExcel(${receiveOrder.id});" class="sendbox" value="导出Excel" /> &nbsp; 
		</c:if>
		<input type="button" class="return-button" value="返回" onclick="receiveOrderItem.backList();" />
	</div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">

<form id="receiveOrderForm" action="<%=request.getContextPath()%>/pos/receiveOrder!confirmReceive.do" method="post">
<input type="hidden" id="receiveOrderId" name="receiveOrder.id" value="${receiveOrder.id }" />
<c:if var="noConfirmReceive" test="${receiveOrder.useStatus == 0}">
<table cellspacing="1" cellpadding="2" border="0" width="100%" class="pn-ftable" style="font-size:13px;">
	<tr>
		<td width="12%" class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>收货单号:</td>
		<td width="88%" class="pn-fcontent">
			${receiveOrder.orderNumber }
			<input type="hidden" name="receiveOrder.orderNumber" value="${receiveOrder.orderNumber }" />
		</td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>负责人:</td>
		<td class="pn-fcontent">
			<input type="text" name="receiveOrder.charger" class="required" value="${receiveOrder.charger }" />
		</td>
	</tr>
</table>
</c:if>
<c:if test="${!noConfirmReceive}">
<table cellspacing="1" cellpadding="2" border="0" width="100%" class="pn-ftable" style="font-size:13px;">
	<tr>
		<td width="12%" class="pn-flabel pn-flabel-h">收货单号:</td>
		<td width="88%" class="pn-fcontent">${receiveOrder.orderNumber }</td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h">负责人:</td>
		<td class="pn-fcontent">${receiveOrder.charger }</td>
	</tr>
</table>
</c:if>

<table id="goodsList" width="100%" cellspacing="1" cellpadding="0" border="0" style="" class="pn-ltable">
<thead class="pn-lthead">
<tr>
	<th>序号</th>
	<th>商品名称</th>
	<th>发货数量</th>
	<th>实收数量</th>
</tr>
</thead>
<tbody class="pn-ltbody">
<c:forEach var="receiveOrderItem" items="${allItemList}" varStatus="status">
<tr>
	<td align="center">${status.index+1}</td>
	<td>${receiveOrderItem.product.name }</td>
	<td align="center">${receiveOrderItem.sendCount}</td>
	<td align="center">
		<c:if test="${noConfirmReceive}">
			<input type="hidden" name="itemList[${status.index}].id" value="${receiveOrderItem.id}" />
			<input type="text" size="5" class="required" name="itemList[${status.index}].receiveCount" value="${receiveOrderItem.sendCount}" />
			<input type="hidden" name="itemList[${status.index}].productId" value="${receiveOrderItem.productId}" />
		</c:if>
		<c:if test="${!noConfirmReceive}">
			${receiveOrderItem.receiveCount}
		</c:if>
	</td>
</tr>
</c:forEach>
</tbody>
</table>
</form>
<br/>
</div>
</body>
</html>