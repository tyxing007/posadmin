<%@ page contentType="text/html;charset=utf-8" %><%@ page import="mmboa.util.StringUtil,mmboa.base.voUser,mmboa.base.*" %><%@ page import="mmb.framework.*" %><%
	CustomAction action = new CustomAction(request);
	voUser user = (voUser)session.getAttribute("userView");
	if(!action.isMethodGet()) {
		String old = request.getParameter("old");
		String pwd = request.getParameter("pwd");
		String pwd2 = request.getParameter("pwd2");
		if(old==null||!old.equals(user.getPassword()) && !mmboa.util.Secure.encryptPwd(old).equals(user.getPassword())){
			action.tip("tip","老密码不正确!");
		} else if(pwd == null || pwd.length()<6) {
			action.tip("tip","新密码至少6位!");
		} else if(!pwd.equals(pwd2)) {
			action.tip("tip","两次输入的新密码不匹配!");
		} else {
			UserService service = new UserService();
            service.getDbOp().init();
            service.updateUser(" password='"+mmboa.util.Secure.encryptPwd(pwd)+"'", "id="+user.getId());	// 加密后的字符串一定不包含单引号等异常字符，所以不需要tosql
			service.releaseAll();
			user.setPassword(pwd);
			action.tip("tip","密码修改成功");
		}
	}
%>
<html>
<title>好东东后台</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body style="margin-left:12px;">
<%if(action.isResult("tip")){%><font color=red><%=action.getTip()%></font><br/><%}%>
修改密码<br/>
<form method=post action="set.jsp">
原密码:<input type=password name=old><br/>
新密码:<input type=password name=pwd><br/>
新密码:<input type=password name=pwd2><br/>
<input type=submit value="确认修改"><br/>
</form>
</body>
</html>