<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="mmboa.base.voUser" %>
<%
	voUser user = (voUser)session.getAttribute("userView");
	mmb.system.admin.UserGroupBean group = user.getGroup();
	if(!group.isFlag(0)) {
		response.sendRedirect("error.jsp");
		return;
	}
	mmb.framework.PermissionFrk.clearGroup();
	mmb.framework.PermissionFrk.clearPermMap();
	session.setAttribute("promptMsg", "缓存已成功清除");
	response.sendRedirect("index.jsp");
	return;
%>