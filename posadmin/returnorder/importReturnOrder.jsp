<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>导入退货单</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.validate.js"></script>

<script type="text/javascript">
var importReturnOrder={};

//表单验证
importReturnOrder.validateForm = function(){
	if($('#importReturnOrderForm').valid()){
		return true;
	}else{
		return false;
	}
}

//返回列表
importReturnOrder.backList = function(){
	window.location.href = $('#initPath').val()+'/pos/returnOrder!returnOrderList.do';
}

$(document).ready(function(){
	//验证表单
	$("#importReturnOrderForm").validate();
});
</script>
</head>
<body style="min-height:500px;">
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 退货单管理 - 导入退货单</div>
	<div class="ropt"></div>
	<div class="clear"></div>
</div>

<div class="body-box">
	<form id="importReturnOrderForm" action="<%=request.getContextPath()%>/pos/returnOrder!importReturnOrder.do" method="post" onsubmit="return importReturnOrder.validateForm();" enctype="multipart/form-data">
	<table cellspacing="1" cellpadding="2" border="0" width="100%" class="pn-ftable" style="font-size:13px;">
		<tr>
			<td width="20%" class="pn-flabel pn-flabel-h">退货单文件:</td>
			<td width="80%" class="pn-fcontent">
				<input type="file" name="excel" class="required" />
			</td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h">提示:</td>
			<td class="pn-fcontent">
				1、点击下载<a href="<%=request.getContextPath()%>/posadmin/returnorder/returnOrderTemplate.xls" style="color: blue;">退货单模板文件</a><br/>
				2、确保退货单文件与模板文件格式一致<br/>
				3、导入前确保系统中已经存在要导入的商品信息
			</td>
		</tr>
		<tr>
			<td class="pn-fcontent" align="center" colspan="2">
				<input type="submit" class="submit" value="导入" />
				<input type="button" class="return-button" value="返回" onclick="importReturnOrder.backList();" />
			</td>
		</tr>
	</table>
	</form>
</div>
</body>
</html>