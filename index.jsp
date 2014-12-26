<%@ page contentType="text/html;charset=utf-8"%>
<%
	String path = request.getContextPath();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>好东东POS后台系统</title>
</head>
<frameset rows="86,*" cols="1000*" frameborder="NO" border="0" framespacing="0">
	<frame src="admin/top.jsp" name="topFrame" scrolling="NO" noresize>
	<frameset id="contentFrame" name="contentFrame" rows="*" cols="220,*"
		framespacing="0" frameborder="NO" border="0">
		<frame src="admin/viewTree2.jsp" id="leftFrame" name="leftFrame" noresize>
		<%
			boolean res = request.getSession().getAttribute("simplifyWeb") == null;
		%>
		<frame src="" id="mainFrame" name="mainFrame">
	</frameset>
</frameset>
<noframes>
<body>
</body>
</noframes>
</html>
