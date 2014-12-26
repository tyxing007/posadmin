<%@ page contentType="text/html;charset=utf-8" %><%@ page import="mmboa.base.voUser" %><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><%
voUser user = (voUser)session.getAttribute("userView");
String path=request.getContextPath();
%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<script type="text/javascript" src="<%=path %>/js/jquery.js"></script>
	<script type="text/javascript">
	$(function(){
		//loginBbs();
			$("#tu_1").click(function(){
				//top.frames['mainFrame'].location.href="<%=path %>/hr/publication!main.do";
			});
	});
	/**
	function loginBbs(){
		$.ajax({
			url:"../enter/discuz!JumpDiscuz.do",
			type:"post",
			data:"",
			dataType:"html",
			success:function(data){
				var result=data.split(",");
				if(result[0]=="success"){
				$("#divBbsLogin").html(result[1]);
				}
			}
		});
	}*/
	
	</script>
<title>无标题文档</title>
</head>
<script> 
function toggletree() {
	var f=parent.document.getElementById('contentFrame');
	if(f.cols!='0,*')
		f.cols='0,*';
	else
		f.cols='220,*';
	return false;
}
</script>
<style>
#ind_1 {
	background-color: #5CC2D0;
	margin: 0px;
	padding: 0px;
	width: 100%;
	height: 85px;
	border-bottom-width: 1px;
	border-bottom-style: solid;
	border-bottom-color: #666666;
	box-shadow:5px 5px 0 rgba(0,0,0,.5);
	position: relative;
}
body {
	margin: 0px;
	padding: 0px;
}
#tu_1 {
	float: left;
	left: 8px;
	position: absolute;
}
#tu_2 {
	float: right;
}

#tu_3{
	float:center;
	font-size: 28px;
	padding-top:25px;
	text-align:center;
}

#tet {
	font-size: 12px;
	top: 60px;
	right: 20px;
	float:right;
	position: absolute;
}
a:link {
	color: #ffffff;
	text-decoration: none;
}

a:visited {
   color: #ffffff;
	text-decoration: none;
}

a:hover {
	text-decoration: underline;
	color: #ffffff;
}

</style>
<body>
<div id="divBbsLogin"></div>
<div id="ind_1">
<div id="tu_1"><img src="images/logo.jpg" /></div>
<div id="tu_3"><img src="images/title_1.png" /></div>
<div id="tet">
<table border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td><font color="#ffffff"><%=user.getDisplayName()%></font></td>
    <td style="padding:0px 0px 0px 7px;"><a href="logout.jsp">[注销]</a>
 |  <a href="#" onclick="top.mainFrame.location.reload();return false;">刷新页面</a> |  <%if(true){%><a href="#" onclick="return toggletree()">隐藏功能树</a><%}%></td>
  </tr>
</table>
</div>
</div>
</body>
</html>
