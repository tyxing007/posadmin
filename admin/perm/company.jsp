<%@ page contentType="text/html;charset=utf-8" %><%@ include file="../../taglibs.jsp"%><%@ page import="java.util.*,mmboa.base.voUser,mmb.framework.*,mmb.system.admin.*" %><%
String path = request.getContextPath();
voUser user = (voUser)session.getAttribute("userView");
mmb.system.admin.UserGroupBean group = user.getGroup();
if(!group.isFlag(0)){
	response.sendRedirect("error.jsp");
	return;
}
CustomAction action = new CustomAction(request);
int id = action.getParameterInt("id");
int groupid = action.getParameterInt("groupid");
UserCompanyPermission m=PermissionFrk.getUserCompanyPermission(groupid,id);
UserGroupBean u = PermissionFrk.getUserGroup(groupid);
if(u!=null&&m!=null) {
	if(!action.isMethodGet()) {
		if(!"null".equals(request.getParameter("flag")))
			m.setFlags(mmboa.util.BinaryFlag.fromStrings(action.getParameterString("flag")));
		PermissionFrk.updateUserCompanyPermission(m, false);
	}
}
List permList = PermissionFrk.getPermissionList(" 1 order by id");
%>
<html>
<head>
<title>好东东后台</title>
<script type="text/javascript" src="<%=path%>/js/jquery.js"></script> 
<script type="text/javascript" src="<%=path%>/js/jquery.ztree.core-3.1.min.js"></script> 
<script type="text/javascript" src="<%=path%>/js/jquery.ztree.excheck-3.1.min.js"></script> 
<link href="<%=path%>/css/global.css" rel="stylesheet" type="text/css">
<link href="<%=path%>/css/zTreeStyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
	<!--
		var zTreeObj;
		var setting = {
			check: {
				enable: true
			},
			view: {
				showIcon: false
			},
			data: {
				simpleData: {
					enable: true
				}
			}
		};
		var zNodes =[<%
		PermissionBean perm = (PermissionBean)permList.get(0);%>
		{ id:-1, pId:<%=perm.getParent()%>, name:"<%=perm.getName()%>(0)"<%if(m.isInFlag(0)){%>, checked:true<%}%>},<%
		for(int i = 1;i < permList.size();i++){
			perm = (PermissionBean)permList.get(i);%>
			{ id:<%=perm.getId()%>, pId:<%=perm.getParent()%>, name:"<%=perm.getName()%>(<%=perm.getId()%>)"<%if(m.isInFlag(perm.getId())){%>, checked:true<%}%>}<%=(i==permList.size()-1?"":",")%>
		<%}%>
		];
		$(document).ready(function(){
			zTreeObj=$.fn.zTree.init($("#treeDemo"), setting, zNodes);
		});
		function addvaluetohidden() {
			var nodes = zTreeObj.getCheckedNodes();
			var s = '';//选中节点ids
			//遍历选中的节点，为s赋值
			for(var i=0; i<nodes.length; i++){
				if (s != '')
					s += ',';
				if(nodes[i].id==-1){
					s +='0';
				}else{
					s += nodes[i].id;
				}
			}
			$("#flag").val(s);
		}
		//-->
</script>
</head>
<body style="margin-left:12px;">
<%@include file="../../header.jsp"%>
<a href="companys.jsp">返回</a><br/><%
if(m!=null){
	mmboa.hr.entity.WorkPlace com = mmboa.cache.WorkPlaceCache.getWorkPlace(m.getCompanyId());
	%><form action="company.jsp?id=<%=id%>&groupid=<%=groupid%>" method="post" onsubmit="addvaluetohidden()">
	<table align="left" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
	<tr><td height="30" align="center" bgcolor="#F8F8F8">权限组</td><td height="30" bgcolor="#F8F8F8"><%=u.getName()%>(<%=u.getId()%>)</td></tr>
	<tr><td height="30" align="center" bgcolor="#F8F8F8">公司名</td><td height="30" bgcolor="#F8F8F8"><%=com.getName()%>(<%=com.getId()%>)</td></tr>
	<tr><td height="30" align="center" bgcolor="#F8F8F8">权限</td><td><ul id="treeDemo" class="ztree"></ul><input id="flag" type="hidden" name="flag" value="null"/></td></tr>
	<tr><td height="30" align="center" bgcolor="#F8F8F8" colspan=2><input type="submit" value="确认修改"></td></tr>
	</table>
	</form>
<%}else{%>
用户不存在<br/>
<%}%>
</body>
</html>