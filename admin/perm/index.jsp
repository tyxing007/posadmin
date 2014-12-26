<%@ page contentType="text/html;charset=utf-8" %><%@ page import="java.util.*"%>
<%@ page import="mmboa.base.voUser" %><%@ page import="mmb.framework.*" %><%@ page import="mmb.system.admin.*" %>
<%
	voUser user = (voUser)session.getAttribute("userView");
	mmb.system.admin.UserGroupBean group = user.getGroup();
	String path = request.getContextPath();
	if(!group.isFlag(0)) {
		response.sendRedirect("error.jsp");
		return;
	}
%>
<html>
<title>好东东POS后台系统</title>
<script>
</script>
<script type="text/javascript" src="../js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body style="margin-left:12px;line-height:150%;font-size:14px;">
<%@include file="../../header.jsp"%>
<br>
<a href="users.jsp">查看<font color=red>用户权限</font>设置</a><br/>
<a href="groups.jsp">查看<font color=red>权限组</font>设置</a><br/>
<a href="perms.jsp">查看<font color=red>权限</font>设置</a><br/>
<a href="usercompetencelist.jsp">增删<font color=red>用户权限</font>设置</a><br/>

<br/>
<br/>
<a href="clearCache.jsp" onclick="return confirm('此操作将清除所有权限缓存，确认？')">清除权限组缓存</a><br/>
<br/>
<br/>
<a href="../tree/viewTree3.jsp"><font color=green>左侧功能树</font>管理</a><br/>

<br/>
<form method=post action="index.jsp">
密码原文:<input type=text name=pwd><input type=submit value="加密">
</form>
<%
String pwd=request.getParameter("pwd");
if(pwd!=null){
%><input type=text size="30" value="<%=mmboa.util.Secure.encryptPwd(pwd)%>"/><%}%>
</body>
</html>