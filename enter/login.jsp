<%@ page contentType="text/html;charset=utf-8" %><%@ page import="mmboa.util.*"%><%
CookieUtil ck = new CookieUtil(request,response);
if(request.getParameter("dc")!=null) {		// 删除cookie的操作
	ck.removeCookie("u");
	ck.removeCookie("p");
	ck.removeCookie("ru");
	ck.removeCookie("rp");
	response.sendRedirect("login.do");
	return;
}
String username = ck.getCookieValue("u");
String password = ck.getCookieValue("p");
boolean ru=ck.getCookieValue("ru")==null||ck.getCookieValue("ru").equals("1");
boolean rp=ck.getCookieValue("rp")==null||ck.getCookieValue("rp").equals("1");
String focus = "username";
boolean simple = false;	// 简化版本
if(username==null||!ru)
	username="";
else		// 已经保存了密码则焦点放到密码框
	focus="password";
if(password==null||!rp)
	password="";
else
	focus = "username";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>好东东POS后台登陆</title>
<link rel="shortcut icon" href="/favicon.ico"> 
</head>
<style>
body {
	font-family: "宋体";
	font-size: 12px;
}
.input1 {
	margin: 0px;
	padding: 1px;
	height: 15px;
	width: 100px;
}
<%if(!simple){%>

#dv {
	margin-top:15%;
	margin-left:auto;
	margin-right:auto;
	float:center;
	width:500px
}

<%}%>

</style>
<body onload="document.f1.<%=focus%>.focus();document.f1.<%=focus%>.select();" bgcolor="#ffffff">
<form  method="post" action="login.do" name="f1">

<%if(simple){%>

	<table border="1" cellpadding="3" cellspacing="3" style="margin:auto;text-align:center;">
  <tr>
    <td>用户名：</td>
    <td><input name="username" type="text" class="input1" value="<%=username%>"></td>
  </tr>
  <tr>
    <td>密 &nbsp;码：</td>
    <td><input name="password" type="password" class="input1" value="<%=password%>"></td>
  </tr>
  <tr>
    <td colspan=2><input name="ru" type="checkbox" class="input2" value="1" <%if(ru){%>checked<%}%> >记住用户名 
    <input name="rp" type="checkbox" class="input2" value="1" <%if(rp){%>checked<%}%> >简化版本</td>
  </tr>
  <tr>
    <td><input type="submit" value="登 陆" border="0" /></td>
    <td style="text-decoration:underline"><a href="login.do?dc=1"><font color="#000000">删除Cookie</font></a></td>
  </tr>
</table>

<%}else{%>

<table border="0" cellpadding="0" cellspacing="0" id="dv">
  <tr>
    <td style="background:white url(images/blue_03.gif) no-repeat top left;vertical-align:top;">
		<table border="0" cellpadding="0" cellspacing="0" width="220px" style="margin-left:12px;margin-top:0px;vertical-align:top;">
		<tr height="40px"><td align="left" style="margin-top:10px;padding-left:40px;;color:white;font-weight:bold;font-size:16px;letter-spacing:2px;" >
			好东东POS后台
		</td></tr>
		<tr><td>
			<table border="0" cellpadding="0" cellspacing="0" style="margin:10px 20px 5px 24px">
			  <tr>
			    <td width="55px">用户名：</td>
			    <td><input name="username" type="text" class="input1" value="<%=username%>"></td>
			  </tr>
			</table>
		</td></tr>
		<tr><td>
			<table border="0" cellpadding="0" cellspacing="0" style="margin:5px 20px 5px 24px">
			  <tr>
			    <td width="55">密 &nbsp;码：</td>
			    <td><input name="password" type="password" class="input1" value="<%=password%>"></td>
			  </tr>
			</table>
		</td></tr>
		<tr><td><div style="margin:5px 5px 5px 15px"><img src="images/line.gif" /></div></td></tr>
		<tr><td>
			<table border="0" cellpadding="0" cellspacing="0" style="margin:5px 20px 5px 18px">
			  <tr>
			    <td width="90"><input name="ru" type="checkbox" class="input2" value="1" <%if(ru){%>checked<%}%> >记住用户名</td>
			    <td><input name="rp" type="checkbox" class="input2" value="1" <%if(rp){%>checked<%}%> >自动登录</td>
			  </tr>
			</table>
		</td></tr>
		<tr><td>
			<table border="0" cellpadding="0" cellspacing="0" style="margin:5px 20px 5px 24px">
			  <tr>
			    <td width="90"><input type="image" src="images/denglu_22.gif" border="0" /></td>
			    <td style="text-decoration:underline"><a href="login.do?dc=1"><font color="#000000">删除Cookie</font></a></td>
			  </tr>
			</table>
		</td></tr>
		</table>
	</td>
    <td width="273" height="213" style="background-image:url(images/blue_05.gif)"></td>
  </tr>
</table>
<%}%>

</form>
</body>
</html>
