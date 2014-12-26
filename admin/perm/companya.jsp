<%@ page contentType="text/html;charset=utf-8" %><%@ include file="../../taglibs.jsp"%><%@ page import="java.util.*,mmboa.base.*,mmb.framework.*,mmb.system.admin.*,mmboa.cache.*" %><%
String path = request.getContextPath();
CustomAction action = new CustomAction(request);
voUser user = (voUser)session.getAttribute("userView");
mmb.system.admin.UserGroupBean group = user.getGroup();
if(!group.isFlag(0)){
	response.sendRedirect("error.jsp");
	return;
}
List list=null;
Map map=PermissionFrk.getComPermMap();
if(map!=null){
	list=new ArrayList(map.values());
}
if(!action.isMethodGet()) {
	UserCompanyPermission m=new UserCompanyPermission();
	m.setUserGroupId(action.getParameterInt("groupid"));
	m.setCompanyId(action.getParameterInt("companyid"));	
	if(!"null".equals(request.getParameter("flag"))){
		m.setFlags(mmboa.util.BinaryFlag.fromStrings(action.getParameterString("flag")));
	}	
	PermissionFrk.updateUserCompanyPermission(m, true);
	response.sendRedirect("companys.jsp");
	return;
}
List permList = PermissionFrk.getPermissionList(" 1 order by id");
List<UserGroupBean> mUserGroupBeanList = new ArrayList(PermissionFrk.getGroupMap().values());
List<mmboa.hr.entity.WorkPlace> companyList=new ArrayList(WorkPlaceCache.getWorkPlaces().values());
%><html>
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
		{ id:-1, pId:<%=perm.getParent()%>, name:"<%=perm.getName()%>(0)"},<%
		for(int i = 1;i < permList.size();i++){
			perm = (PermissionBean)permList.get(i);%>
			{ id:<%=perm.getId()%>, pId:<%=perm.getParent()%>, name:"<%=perm.getName()%>(<%=perm.getId()%>)"}<%=(i==permList.size()-1?"":",")%>
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
<form action="companya.jsp" method="post" onsubmit="addvaluetohidden()">
	<table align="left" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
	<tr><td height="30" align="center" bgcolor="#F8F8F8">权限组</td><td height="30" bgcolor="#F8F8F8"><select name="groupid"><%
	for(int i=0;i<mUserGroupBeanList.size();i++){
		UserGroupBean m=mUserGroupBeanList.get(i);		
		 %><option value="<%=m.getId()%>"><%=m.getName()+"("+m.getId()+")"%></option><%
	}
	%></select></td></tr>
	<tr><td height="30" align="center" bgcolor="#F8F8F8">公司</td><td height="30" bgcolor="#F8F8F8"><select name="companyid"><%
	for(int i=0;i<companyList.size();i++){
		mmboa.hr.entity.WorkPlace m=companyList.get(i);		
		 %><option value="<%=m.getId()%>"><%=m.getName()+"("+m.getId()+")"%></option><%
	}
	%></select></td></tr>
	<tr><td height="30" align="center" bgcolor="#F8F8F8">权限</td><td height="30" bgcolor="#F8F8F8"><ul id="treeDemo" class="ztree"></ul>
	<input id="flag" type="hidden" name="flag" value="null"/></td></tr>
	<tr><td height="30" align="center" bgcolor="#F8F8F8" colspan=2><input type="submit" value="确认添加"></td></tr>
	</table>
	</form>
</body>
</html>