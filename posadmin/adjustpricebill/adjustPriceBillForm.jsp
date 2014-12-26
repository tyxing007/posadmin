<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>维护调价单信息</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/jquery/msgbox/jquery.msgbox.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.validate.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.form.js"></script>

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery/msgbox/jquery.dragndrop.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery/msgbox/jquery.msgbox.js"></script>

<script type="text/javascript">
var adjustPriceBill={};

//返回列表
adjustPriceBill.backList = function(){
	window.location.href = $('#initPath').val()+'/pos/adjustPriceBill!adjustPriceBillList.do';
}

//提交
adjustPriceBill.saveOrSubmit = function(useStatus){
	//表单验证
	if(!$('#adjustPriceBillForm').valid()){
		return ;
	}

	if(useStatus==2 && !confirm('提交后将不能修改调价单信息，确定提交吗？')) {
		return ;
	}

	var data = {};
	data['adjustPriceBill.useStatus'] = useStatus;
	if(useStatus == 2) {
		data['adjustPriceBill.auditStatus'] = 1;
	}
	
	$('#adjustPriceBillForm').ajaxSubmit({
		dataType: 'text',
		data: data,
		success: function(json){
			if(json == 'success'){
				adjustPriceBill.backList();
			}else{
				alert(json);
			}
		},
		error: function(msg){
			alert(msg);
		}
	});
}

$(document).ready(function(){
	//验证表单
	$("#adjustPriceBillForm").validate();

	//选择商品
	$('#productName').click(function(){
		new $.msgbox({
			title: '选择商品',
			width: 700,
			height: 480,
			type: 'ajax',
			content: $('#initPath').val()+'/pos/Product!toSelectProductListView.do'
		}).show();
	});
});
</script>
</head>
<body style="min-height:500px;">
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 调价单管理 - <c:if test="${adjustPriceBill.id != 0}">修改调价单信息</c:if><c:if test="${adjustPriceBill.id == 0}">新增调价单</c:if></div>
	<div class="ropt"></div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
<form id="adjustPriceBillForm" action="<%=request.getContextPath()%>/pos/adjustPriceBill!saveAdjustPriceBill.do" method="post">
<input type="hidden" name="adjustPriceBill.id" value="${adjustPriceBill.id }" />
<table cellspacing="1" cellpadding="2" border="0" width="100%" class="pn-ftable" style="font-size:13px;">
	<tr>
		<td width="12%" class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>调价商品:</td>
		<td width="88%" class="pn-fcontent">
			<input type="hidden" id="productId" name="adjustPriceBill.productId" value="${adjustPriceBill.productId }" />
			<input type="text" id="productName" name="productName" class="required" value="${adjustPriceBill.product.name }" readonly="readonly" />
		</td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>目标价:</td>
		<td class="pn-fcontent"><input type="text" class="required money" name="adjustPriceBill.targetPrice" value="<c:if test="${adjustPriceBill.id != 0}">${adjustPriceBill.targetPrice }</c:if>" /></td>
	</tr>
	<tr>
		<td class="pn-fcontent" align="center" colspan="2">
			<input type="button" class="submit" value="保存" onclick="adjustPriceBill.saveOrSubmit(1);" />&nbsp;&nbsp;
			<input type="button" class="submit" value="提交" onclick="adjustPriceBill.saveOrSubmit(2);" />&nbsp;&nbsp;
			<input type="button" class="return-button" value="返回" onclick="adjustPriceBill.backList();" />
		</td>
	</tr>
</table>
</form>
</div>
</body>
</html>