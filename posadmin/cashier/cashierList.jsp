<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>收银员列表</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />
<link rel="STYLESHEET" type="text/css" href="<%=request.getContextPath()%>/js/jquery/msgbox/jquery.msgbox.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery/msgbox/jquery.dragndrop.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery/msgbox/jquery.msgbox.js"></script>
<script type="text/javascript">
var cashier={};

//新增
cashier.addCashier = function(cashierId){
	window.location.href = $('#initPath').val()+'/pos/cashier!toCashierFormView.do';
}

//删除
cashier.deleteCashier = function(cashierId){
	if(confirm('确定删除该收银员信息吗？')){
		window.location.href = '<%=request.getContextPath()%>/pos/cashier!deleteCashier.do?cashier.id='+cashierId;
	}
}

//查看
cashier.showCashier = function(id){
	new $.msgbox({
		title: '查看收银员详情',
		width: 700,
		height: 480,
		type: 'ajax',
		content: $('#initPath').val()+'/pos/cashier!toCashierDetailView.do?cashier.id='+id
	}).show();
}

//跳转到指定页面
cashier.goPage = function(pageNum,pageCount){
	var url = $('#initPath').val()+'/pos/cashier!cashierList.do?d=1';
	if(pageNum != null && pageCount != null){
		url += '&page.pageNum='+pageNum;
	}
	if(pageCount != null){
		url += '&page.pageCount='+pageCount;
	}
	window.location.href = url;
}
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 收银员管理 - 列表</div>
	<div class="ropt">
		<input type="button" onclick="cashier.addCashier();" class="submit" value="新增" /> &nbsp; 
	</div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
<table id="goodsList" width="100%" cellspacing="1" cellpadding="0" border="0" class="pn-ltable">
<thead class="pn-lthead">
	<tr>
      <th>用户名</th>
      <th>姓名</th>
      <th>身份证号</th>
      <th>销售价类型</th>
      <th>使用状态</th>
      <th>操作</th>
	</tr>
</thead>
<tbody class="pn-ltbody">
	<s:iterator value="list" var="li">
	<tr>
		<td><s:property value="#li.username"/></td>
		<td><s:property value="#li.name"/></td>
		<td align="center"><s:property value="#li.idCard"/></td>
		<td align="center">
			<c:if test="${li.saleType==1}">仅标牌价</c:if>
			<c:if test="${li.saleType==2}">突破标牌价</c:if>
			<c:if test="${li.saleType==3}">突破限价</c:if>
			<c:if test="${li.saleType==4}">突破锁价</c:if>
		</td>
		<s:if test="#li.forbidden==0">
		<td align="center">有效</td>
		</s:if>
		<s:if test="#li.forbidden==1">
		<td align="center">禁用</td>
		</s:if>
		<td align="center">
		<a href="javascript:;" onclick="cashier.showCashier(<s:property value="#li.id"/>);">查看</a>
		<a href="<%=request.getContextPath()%>/pos/cashier!toCashierFormView.do?Cashier.id=<s:property value="#li.id"/>">修改</a>
		</td>
	</tr>
	</s:iterator>
</tbody>
</table>

<table id="dtPageTable" width="100%" style="font-size:13px;">
	<tbody>
	<tr>
		<td align="center" class="pn-sp">
		<c:if var="isFirstPage" test="${page.pageNum<=1}">
		首页 &nbsp;&nbsp;&nbsp;上一页&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${!isFirstPage}">
		<a href="javascript:;" onclick="cashier.goPage(1,${page.pageCount});">首页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="cashier.goPage(${page.pageNum-1},${page.pageCount});">上一页</a>
		</c:if>
		&nbsp;&nbsp;&nbsp;
		<c:if var="isLastPage" test="${page.pageNum>=page.totalPages}">
		下一页&nbsp;&nbsp;&nbsp;尾页&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${!isLastPage}">
		<a href="javascript:;" onclick="cashier.goPage(${page.pageNum+1},${page.pageCount});">下一页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="cashier.goPage(${page.totalPages},${page.pageCount});">尾页</a>
		</c:if>
		&nbsp;&nbsp;&nbsp;&nbsp;
		第${page.pageNum}/${page.totalPages}页,共${page.totalRecords}条记录
	</td>
	</tr>
	</tbody>
</table>
</div>
</body>
</html>
