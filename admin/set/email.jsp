<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><%@ page import="mmb.framework.*"%><%@ page import="mmb.util.*"%><%
String path = request.getContextPath(); 
CustomAction action = new CustomAction(request);

	String subject = request.getParameter("subject");
	String content = request.getParameter("content");
	String from = request.getParameter("from");
	String to = request.getParameter("to");
	String tip = null;
if(!action.isMethodGet()){
	boolean res = EmailUtil.send(subject, content, from, to, true);
	if(res)
		tip="发送成功";
	else
		tip="发送失败";
}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link rel="stylesheet" type="text/css" href="<%=path%>/css/table.css">
<script type="text/javascript" src="<%=path %>/js/jquery.js"></script>
<script type="text/javascript" src="<%=path %>/js/checkForm.js"></script>
<title>测试邮件</title>
<style>
	.input{
		 display: block; 
		 width: 70%;
	}
</style>
<script type="text/javascript">
	function checkForm(){
		return true;
	}
</script>
</head>
<body>
<%if(tip!=null){%><%=tip%><br/><%}%>
	<form action="email.jsp" method="post" onsubmit="return checkForm();">
		<table align="center" class="gridtable">
		<tr>
			<th colspan="8">测试邮件</th>
		</tr>
		<tr>
			<td>邮件主题:</td>
			<td colspan="7"><input type="text" name="subject" class="input" value="<%if(subject!=null){%><%=subject%><%}else{%>测试<%}%>"/></td>
		</tr>
		<tr>
			<td>发件人:</td>
			<td colspan="7"><input type="text" name="from" class="input" value="<%if(from!=null){%><%=from%><%}else{%>noreply@ebinf.com<%}%>"/></td>
		</tr>
		<tr>
			<td>收件人:</td>
			<td colspan="7"><input type="text" name="to" class="input" value="<%if(to!=null){%><%=to%><%}%>"/></td>
		</tr>
		<tr>
			<td>邮件内容:</td>
			<td colspan="7"><textarea name="content" id="content" cols="60" rows="10" /><%if(content!=null){%><%=content%><%}%></textarea></td>
		</tr>
		<tr>
			<td colspan="8" align="center"><input type="submit" value="发送邮件" /></td>
		</tr>
		</table>
	</form>
</body>
</html>
