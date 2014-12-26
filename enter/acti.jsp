<%@ page contentType="text/html;charset=utf-8" %><%@ page import="mmboa.base.voUser"%><%@ page import="mmboa.util.*"%><%@ page import="mmboa.base.*"%><%
	UserAction action = new UserAction(request);
	UserService service = UserAction.service;
	int id = action.getParameterInt("id");
	voUser user2;
	String code = action.getParameterString("code");
	if(action.isMethodGet()){
		UserActivate ua = service.getUserActivate("id="+id+" and used=0");
		if(ua!=null && ua.getCode().equals(code)){
			user2=service.getUser("id="+ua.getUserId());
			session.setAttribute("activateUser", user2);
		}else{
			response.sendRedirect("login.jsp");
			return;
		}
	} else {
		user2 = (voUser)session.getAttribute("activateUser");
		if(user2!=null){
			String password = action.getParameterString("password");
			String password2 = action.getParameterString("password2");
			if(password.equals(password2) && password.length()>=6){
				user2.setPassword(Secure.encryptPwd(password));
				service.executeUpdate("update user set password='"+user2.getPassword()+"' where id=" + user2.getId());
				service.executeUpdate("update user_activate set used=1 where user_id=" + user2.getId());
				session.removeAttribute("activateUser");
				response.sendRedirect("login.jsp");
				return;
			}
		}else{
			response.sendRedirect("login.jsp");
			return;
		}
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>好东东后台登陆</title>
</head>
<body>
<form  method="post" action="acti.jsp" name="f1">
【<%=user2.getName()%>】<br/>
用户名：<%=user2.getUsername()%><br/>
设置我的密码：<input type="password" name="password"/><br/>
再次输入密码：<input type="password" name="password2"/><br/>
<input type="submit" value="确定"/>
</form>
</body>
</html>
