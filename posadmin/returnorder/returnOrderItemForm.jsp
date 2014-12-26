<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>returnOrderItem form</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />
<link rel="STYLESHEET" type="text/css" href="<%=request.getContextPath()%>/js/jquery/msgbox/jquery.msgbox.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.validate.js"></script>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery/msgbox/jquery.dragndrop.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery/msgbox/jquery.msgbox.js"></script>
<script type="text/javascript">
var returnOrderItem={};

//表单验证
returnOrderItem.validateForm = function(){
	if($('#returnOrderItemForm').valid()){
		if($('input[name="returnOrderItem.count"]').val() == 0){
			alert('商品数量必须大于0！');
			return false;
		}
		return true;
	}else{
		return false;
	}
}

//返回列表
returnOrderItem.backList = function(){
	window.location.href = $('#initPath').val()+'/pos/returnOrderItem!returnOrderItemList.do?returnOrderId='+$('#returnOrderId').val();
}

$(document).ready(function(){
	//验证表单
	$("#returnOrderItemForm").validate();

	//选择商品
	$('#productName').click(function(){
		new $.msgbox({
			title: '选择商品',
			width: 700,
			height: 480,
			type: 'ajax',
			content: $('#initPath').val()+'/pos/Product!toSelectProductListView.do',
			onAjaxed: function(){}
		}).show();
	});
});
</script>
</head>
<body style="min-height:500px;">
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 退货单管理 - <c:if test="${returnOrderItem.id != 0}">修改退货单条目</c:if><c:if test="${returnOrderItem.id == 0}">新增退货单条目</c:if></div>
	<div class="ropt"></div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
	<form id="returnOrderItemForm" action="<%=request.getContextPath()%>/pos/returnOrderItem!saveReturnOrderItem.do" method="post" onsubmit="return returnOrderItem.validateForm();">
	<input type="hidden" name="returnOrderItem.id" value="${returnOrderItem.id }" />
	<input type="hidden" name="returnOrderItem.returnOrderId" value="${returnOrderId}" />
	<input type="hidden" id="returnOrderId" name="returnOrderId" value="${returnOrderId}" />
	<table cellspacing="1" cellpadding="2" border="0" width="100%" class="pn-ftable" style="font-size:13px;">
		<tr>
			<td width="12%" class="pn-flabel pn-flabel-h">退货单号:</td>
			<td width="88%" class="pn-fcontent">${returnOrder.orderNumber }</td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h">负责人:</td>
			<td class="pn-fcontent">${returnOrder.charger }</td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>商品:</td>
			<td class="pn-fcontent">
				<input type="hidden" id="productId" name="returnOrderItem.productId" value="${returnOrderItem.productId }" />
				<input type="text" id="productName" class="required" value="${returnOrderItem.product.name }" readonly="readonly" />
			</td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>商品数量:</td>
			<td class="pn-fcontent">
				<input type="text" name="returnOrderItem.count" class="required digits" value="<c:if test="${returnOrderItem.id != 0}">${returnOrderItem.count }</c:if>" />
			</td>
		</tr>
		<tr>
			<td class="pn-fcontent" align="center" colspan="2">
				<input type="submit" class="submit" value="提交" />
				<input type="button" class="return-button" value="返回" onclick="returnOrderItem.backList();" />
			</td>
		</tr>
	</table>
	</form>
</div>
</body>
</html>