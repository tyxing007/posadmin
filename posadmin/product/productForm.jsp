<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>product form</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.validate.js"></script>
<script type="text/javascript">
var product={};

//表单验证
product.validateForm = function(){
	if(!$('#productForm').valid()){return;}
	return true;
}

//返回列表
product.backList = function(){
	window.location.href = $('#initPath').val()+'/pos/Product!productList.do';
}

$(document).ready(function(){
	//验证表单
	$("#productForm").validate();
});
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 商品管理 - 修改商品信息</div>
	<div class="ropt"></div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
	<form id="productForm" action="<%=request.getContextPath()%>/pos/Product!saveProduct.do" method="post" onsubmit="return product.validateForm();">
	<input type="hidden" name="product.id" value="${product.id }" />
	<input type="hidden" name="product.stock" value="${product.stock }" />
	<table cellspacing="1" cellpadding="2" border="0" width="100%" class="pn-ftable" style="font-size:13px;">
		<tr>
			<td class="pn-flabel pn-flabel-h">条形码：</td>
			<td class="pn-fcontent">
				${product.barCode }
				<input type="hidden" id="barCode" name="product.barCode" value="${product.barCode }" />
			</td>
		</tr>
		<tr>
			<td width="12%" class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>商品名称：</td>
			<td width="88%" class="pn-fcontent"><input type="text" id="name" class="required" name="product.name" value="${product.name }" /></td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>日租赁价格(元)：</td>
			<td class="pn-fcontent"><input type="text" id="leasePrice" class="required money" name="product.leasePrice" value="<c:if test="${product.id != 0}">${product.leasePrice }</c:if>" /></td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>包月租赁价格(元)：</td>
			<td class="pn-fcontent"><input type="text" id="monthLeasePrice" class="required money" name="product.monthLeasePrice" value="<c:if test="${product.id != 0}">${product.monthLeasePrice }</c:if>" /></td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>标牌价(元)：</td>
			<td class="pn-fcontent"><input type="text" id="salePrice" class="required money" name="product.salePrice" value="<c:if test="${product.id != 0}">${product.salePrice }</c:if>" /></td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h">限价(元):</td>
			<td class="pn-fcontent"><input type="text" class="money" name="product.limitPrice" value="<c:if test="${product.id != 0}">${product.limitPrice }</c:if>" /></td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h">锁价(元):</td>
			<td class="pn-fcontent"><input type="text" class="money" name="product.lockPrice" value="<c:if test="${product.id != 0}">${product.lockPrice }</c:if>" /></td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h">红卡额度:</td>
			<td class="pn-fcontent"><input type="text" class="floats" name="product.redLines" value="<c:if test="${product.id != 0}">${product.redLines }</c:if>" /></td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h">蓝卡额度:</td>
			<td class="pn-fcontent"><input type="text" class="floats" name="product.blueLines" value="<c:if test="${product.id != 0}">${product.blueLines }</c:if>" /></td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>押金(元)：</td>
			<td class="pn-fcontent"><input type="text" id="deposit" class="required money" name="product.deposit" value="<c:if test="${product.id != 0}">${product.deposit }</c:if>" /></td>
		</tr>
		<tr>
			<td align="center" colspan="2" class="pn-fcontent">
				<input type="submit" class="submit" value="提交" />
				<input type="button" class="return-button" value="返回" onclick="product.backList();" />
			</td>
		</tr>
	</table>
	</form>
</div>
</body>
</html>