<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="mmboa.base.voUser" %>
<%
	voUser user = (voUser)session.getAttribute("userView");
%>
<html>
<title>好东东后台</title>
<script>
</script>
<script type="text/javascript" src="js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body style="margin-left:12px;">
<%@include file="../../header.jsp"%>
          <br>
          没有权限访问<br/>
</body>
</html>