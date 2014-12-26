<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>维护收银员</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/checkForm.js"></script>
<script type="text/javascript">
var cashier={};

//表单验证
cashier.validateForm = function(){
	//姓名
	if(isNull('name')){
		alert('姓名不能为空！');
		return false;
	}
	//用户名
	if(isNull('username')){
		alert('用户名不能为空！');
		return false;
	}

	if($('#cashierId').val()=='0' || $('#updatePasswordBut').attr('update')=='1'){
		//密码
		if(isNull('password1')){
			alert('密码不能为空！');
			return false;
		}
		//确认密码
		if(isNull('password2')){
			alert('确认密码不能为空！');
			return false;
		}
		//两次输入密码不相同
		if($('#password1').val() != $('#password2').val()) {
			alert('两次输入密码不相同！');
			return false;
		}
	}
	return true;
}

//返回列表
cashier.backList = function(){
	window.location.href = $('#initPath').val()+'/pos/cashier!cashierList.do';
}

$(function(){
	//修改密码按钮
	$("#updatePasswordBut").toggle(
		function (){
			$(this).text('取消修改').attr('update', '1');
			$('#password1').parent().parent().show();
			$('#password2').parent().parent().show();
		},
		function (){
			$(this).text('修改密码').attr('update', '0');
			$('#password1').val('').parent().parent().hide();
			$('#password2').val('').parent().parent().hide();
		}
	);
    
	if($('#cashierId').val()=='0'){
		$('#username').val('');
		$('#password1').val('');
	}
});
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 收银员管理 - <c:if test="${cashier.id != 0}">修改收银员信息</c:if><c:if test="${cashier.id == 0}">新增收银员</c:if></div>
	<div class="ropt"></div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
	<form action="<%=request.getContextPath()%>/pos/cashier!saveCashier.do" method="post" onsubmit="return cashier.validateForm();">
	<input type="hidden" id="cashierId" name="cashier.id" value="${cashier.id}" />
	<table cellspacing="1" cellpadding="2" border="0" width="100%" class="pn-ftable" style="font-size:13px;">
		<tr>
			<td width="12%" class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>姓名：</td>
			<td width="88%" class="pn-fcontent"><input type="text" id="name" name="cashier.name" value="${cashier.name }" /></td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h">身份证号：</td>
			<td class="pn-fcontent"><input type="text" id="idCard" name="cashier.idCard" value="${cashier.idCard }" /></td>
		</tr>
		<tr>
			<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>用户名：</td>
			<td class="pn-fcontent"><input type="text" id="username" name="cashier.username" value="<c:if test="${cashier.id != 0}">${cashier.username}</c:if>" /></td>
		</tr>
		<c:if test="${cashier.id == 0}">
			<tr>
				<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>密码：</td>
				<td class="pn-fcontent"><input type="password" id="password1" name="cashier.password" value="" /></td>
			</tr>
			<tr>
				<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>确认密码：</td>
				<td class="pn-fcontent"><input type="password" id="password2" value="" /></td>
			</tr>
		</c:if>
		<c:if test="${cashier.id != 0}">
			<tr>
				<td class="pn-flabel pn-flabel-h">密码：</td>
				<td class="pn-fcontent"><a href="javascript:;" id="updatePasswordBut" update="0">修改密码</a></td>
			</tr>
			<tr style="display: none;">
				<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>新密码：</td>
				<td class="pn-fcontent"><input type="password" id="password1" name="cashier.password" value="" /></td>
			</tr>
			<tr style="display: none;">
				<td class="pn-flabel pn-flabel-h"><span class="pn-frequired">*</span>确认密码：</td>
				<td class="pn-fcontent"><input type="password" id="password2" value="" /></td>
			</tr>
		</c:if>
		<c:if test="${cashier.id != 0}">
		<tr>
			<td class="pn-flabel pn-flabel-h">使用状态：</td>
			<td class="pn-fcontent">
				<input type="radio" name="cashier.forbidden" value="0" <c:if test="${cashier.forbidden==0 || cashier.id==0}">checked="checked"</c:if> />有效
				<input type="radio" name="cashier.forbidden" value="1" <c:if test="${cashier.forbidden==1}">checked="checked"</c:if> />禁用
			</td>
		</tr>
		</c:if>
		<tr>
			<td class="pn-flabel pn-flabel-h">销售价类型：</td>
			<td class="pn-fcontent">
				<input type="radio" name="cashier.saleType" value="1" <c:if test="${cashier.saleType==1 || cashier.id==0}">checked="checked"</c:if> />仅标牌价
				<input type="radio" name="cashier.saleType" value="2" <c:if test="${cashier.saleType==2}">checked="checked"</c:if> />突破标牌价
				<input type="radio" name="cashier.saleType" value="3" <c:if test="${cashier.saleType==3}">checked="checked"</c:if> />突破限价
				<input type="radio" name="cashier.saleType" value="4" <c:if test="${cashier.saleType==4}">checked="checked"</c:if> />突破锁价
			</td>
		</tr>
		<tr>
			<td align="center" colspan="2" class="pn-fcontent">
				<input type="submit" value="提交" class="submit" />
				<input type="button" value="返回" onclick="cashier.backList();" class="return-button" />
			</td>
		</tr>
	</table>
	</form>
</div>
</body>
</html>