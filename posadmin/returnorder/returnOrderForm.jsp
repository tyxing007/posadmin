<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ReturnOrder form</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.validate.js"></script>
<script type="text/javascript">
var returnOrder={};

//表单验证
returnOrder.validateForm = function(){
	if($('#returnOrderForm').valid()){
		return true;
	}else{
		return false;
	}
}

//返回列表
returnOrder.backList = function(){
	window.location.href = $('#initPath').val()+'/pos/returnOrder!returnOrderList.do';
}

$(document).ready(function(){
	//验证表单
	$("#returnOrderForm").validate();
});
</script>
</head>
<body style="min-height:500px;">
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 退货单管理 - <c:if test="${returnOrder.id != 0}">修改退货单</c:if><c:if test="${returnOrder.id == 0}">新增退货单</c:if></div>
	<div class="ropt"></div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
	<form id="returnOrderForm" action="<%=request.getContextPath()%>/pos/returnOrder!saveReturnOrder.do" method="post" onsubmit="return returnOrder.validateForm();">
	<input type="hidden" id="returnOrderId" name="returnOrder.id" value="${returnOrder.id }" />
	<input type="hidden" name="returnOrder.useStatus" value="0" />
	<input type="hidden" name="returnOrder.orderNumber" value="${returnOrder.orderNumber }" />
	<table cellspacing="1" cellpadding="2" border="0" width="100%" class="pn-ftable" style="font-size:13px;">
		<tr>
			<td width="12%" class="pn-flabel pn-flabel-h">退货单号:</td>
			<td width="88%" class="pn-fcontent">${returnOrder.orderNumber }</td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>负责人:</td>
			<td class="pn-fcontent">
				<input type="text" name="returnOrder.charger" class="required" value="${returnOrder.charger }" />
			</td>
		</tr>
		<tr>
			<td class="pn-fcontent" align="center" colspan="2">
				<input type="submit" class="submit" value="提交" />
				<input type="button" class="return-button" value="返回" onclick="returnOrder.backList();" />
			</td>
		</tr>
	</table>
	</form>
</div>
</body>
</html>