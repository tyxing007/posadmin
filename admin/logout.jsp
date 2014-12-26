<%@ include file="taglibs.jsp"%><%@ page import="mmboa.base.voUser" %><%@ page contentType="text/html;charset=utf-8" %><%@ page import="mmboa.base.voUser" %><%
voUser user = (voUser)session.getAttribute("userView");
if(user!=null) {
	mmboa.util.CookieUtil ck = new mmboa.util.CookieUtil(request,response);
	ck.removeCookie("opau");
	ck.removeCookie("opap");
	session.invalidate();
	//Client uc = new Client();
	//String $ucsynlogout = uc.uc_user_synlogout(); 
	//out.println("退出成功"+$ucsynlogout);
}
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<script>
parent.location="../login.do";
</script>
<html xmlns="http://www.w3.org/1999/xhtml">
fasdfasfad
</html>