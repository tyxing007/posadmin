<%@page import="mmboa.base.UserCompetenceAction"%>
<%@page import="mmboa.base.voUser"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%
	UserCompetenceAction uca = new UserCompetenceAction(request);
	if (uca.isMethodGet()) {
		String del = uca.getParameterString("del");
		String add = uca.getParameterString("add");
		int id = uca.getParameterInt("id");
		if (del != null && !"".equals(del)) {
			if (id != 0) {
				if (uca.delCompetence(id)) {
					out.print("<font color='red'>删除权限成功</font>");

				} else {
					out.print("<font color='red'>删除权限失败</font>");

				}

			}
		}
		if (add != null && !"".equals(add)) {
			if (id != 0) {
				if (uca.addCompetence(id)) {
					out.print("<font color='red'>添加权限成功</font>");

				} else {

					out.print("<font color='red'>添加权限失败</font>");
				}

			}
		}
	}
	ArrayList<voUser> userlist = uca.getAllUser();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户权限处理列表</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
</head>
<body>
	 <table width="50%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align="center">
      <tr bgcolor="#4688D6">
      <td width="100" align="center"><font color="#FFFFFF">id</font></td>
      <td width="100" align="center"><font color="#FFFFFF">username</font></td>
      <td align="center"><font color="#FFFFFF">说明</font></td>
      <td align="center" width="50"><font color="#FFFFFF">操作</font></td>
    </tr>

		<%
			if (userlist.size() > 0) {
				for (int i = 0; i < userlist.size(); i++) {
					int userid = userlist.get(i).getId();
					%>
					<tr bgcolor='#F8F8F8'>
					<%
					if (uca.checkUserById(userid)) {
		%>
			<td><%=userlist.get(i).getId()%></td>
			<td><%=userlist.get(i).getUsername()%></td>
			<td>有权限，是否删除</td>
			<td><a
				href="usercompetencelist.jsp?del=yes&id=<%=userlist.get(i).getId()%>"
				onclick="javascript:if   (confirm( '是否删除权限? '))   {   return   true;}else   return   false; ">删</a>
			</td>
			<%
				} else {
			%>
			<td><%=userlist.get(i).getId()%></td>
			<td><%=userlist.get(i).getUsername()%></td>
			<td>无权限，是否增加权限</td>
			<td><a
				href="usercompetencelist.jsp?add=yes&id=<%=userlist.get(i).getId()%>"
				onclick="javascript:if   (confirm( '是否增加权限? '))   {   return   true;}else   return   false; ">增</a>
			</td>
			<%
				}
			%>
		</tr>
		<%
			}

			}
		%>
	</table>
</body>
</html>