<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>shopIpManager form</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.validate.js"></script>
<script type="text/javascript">
var shopIpManager={};

//表单验证
shopIpManager.validateForm = function(){
	if(!$('#shopIpManagerForm').valid()){return;}
	return true;
}

$(document).ready(function(){
	//验证表单
	$("#shopIpManagerForm").validate();
});
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 店面IP管理 - 更新IP地址信息</div>
	<div class="ropt"></div>
	<div class="clear"></div>
</div>


<div id="bodyBox" class="body-box">
	<form id="shopIpManagerForm" action="<%=request.getContextPath()%>/enter/poscenter!shopIpUpdateForm.do" method="post" onsubmit="return shopIpManager.validateForm();">
	<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
	<table cellspacing="1" cellpadding="2" border="0" width="100%" class="pn-ftable" style="font-size:13px;">
		<tr>
			<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>店面IP地址：</td>
			<td class="pn-fcontent"><input type="text" id="shopIpAddress" class="required" name="shopIpAddress" value="${shopIpAddress}" >&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" class="submit" value="更新" /></td>
		</tr>
	</table>
	</form>
</div>
</body>
</html>