<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>posManager form</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.validate.js"></script>
<script type="text/javascript">
var posManager={};

//表单验证
posManager.validateForm = function(){
	if(!$('#posManagerForm').valid()){return;}
	return true;
}

//返回列表
posManager.backList = function(){
	window.location.href = $('#initPath').val()+'/pos/posManager!posManagerList.do';
}

$(document).ready(function(){
	//验证表单
	$("#posManagerForm").validate();
});
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: POS机管理 - <c:if test="${posManager.id != 0}">修改POS机信息</c:if><c:if test="${posManager.id == 0}">新增pos机</c:if></div>
	<div class="ropt"></div>
	<div class="clear"></div>
</div>


<div id="bodyBox" class="body-box">
	<form id="posManagerForm" action="<%=request.getContextPath()%>/pos/posManager!savePos.do" method="post" onsubmit="return posManager.validateForm();">
	<input type="hidden" name="posManager.id" value="${posManager.id }" />
	<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
	<table cellspacing="1" cellpadding="2" border="0" width="100%" class="pn-ftable" style="font-size:13px;">
		<tr>
			<td width="12%" class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>POS机编码：</td>
			<td width="88%" class="pn-fcontent"><input type="text" id="posCode" class="required" name="posManager.posCode" value="${posManager.posCode }" /></td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>POS机IP地址：</td>
			<td class="pn-fcontent"><input type="text" id="posIp" class="required" name="posManager.posIp" value="<c:if test="${posManager.id != 0}">${posManager.posIp }</c:if>" /></td>
		</tr>
		<tr>
			<td align="center" colspan="2" class="pn-fcontent">
				<input type="submit" class="submit" value="提交" />
				<input type="button" class="return-button" value="返回" onclick="posManager.backList();" />
			</td>
		</tr>
	</table>
	</form>
</div>
</body>
</html>