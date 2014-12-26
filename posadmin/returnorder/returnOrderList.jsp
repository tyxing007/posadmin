<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>退货单列表</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
var returnOrder={};

//新增退货单
returnOrder.addReturnOrder = function(){
	window.location.href = $('#initPath').val()+'/pos/returnOrder!toReturnOrderFormView.do';
}

//删除退货单
returnOrder.deleteReturnOrder = function(returnOrderId){
	if(confirm('确定删除该退货单及其所有退货单条目吗？')){
		window.location.href = $('#initPath').val()+'/pos/returnOrder!deleteReturnOrder.do?returnOrder.id='+returnOrderId;
	}
}

//跳转到指定页面
returnOrder.goPage = function(pageNum,pageCount){
	var pageNumInput = '<input type="hidden" name="page.pageNum" value="'+pageNum+'" />';
	var pageCountInput = '<input type="hidden" name="page.pageCount" value="'+pageCount+'" />';
	$('#returnOrderSearchForm').append(pageNumInput);
	$('#returnOrderSearchForm').append(pageCountInput);
	$('#returnOrderSearchForm').submit();
}

//跳转到导入退货单页面
returnOrder.toImportReturnOrderView = function(purchaseOrderId){
	window.location.href = $('#initPath').val()+'/posadmin/returnorder/importReturnOrder.jsp';
}
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 退货单管理 - 退货单列表</div>
	<div class="ropt">
		<c:if test="${currentUser.securityLevel >= 9}">
		<input type="button" onclick="returnOrder.addReturnOrder();" class="submit" value="新增" /> &nbsp; 
		<input type="button" onclick="returnOrder.toImportReturnOrderView();" class="generate-index-page" value="导入退货单" /> &nbsp;
		</c:if>
	</div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
<form id="returnOrderSearchForm" style="padding-top:5px;" action="<%=request.getContextPath()%>/pos/returnOrder!returnOrderList.do" method="post">
<div>
	退货单号:<input type="text" size="12" name="returnOrder.orderNumber" value="${returnOrder.orderNumber }" />&nbsp;&nbsp;
	负责人:<input type="text" size="12" name="returnOrder.charger" value="${returnOrder.charger }" />&nbsp;&nbsp;
	创建时间:<input type="text" size="15" name="startTime" class="Wdate" onclick="WdatePicker()" value="${startTime }" readonly="readonly" />
	- <input type="text" size="15" name="endTime" class="Wdate" onclick="WdatePicker()" value="${endTime }" readonly="readonly"/>&nbsp;&nbsp;
	订单状态:
	<select name="returnOrder.useStatus">
		<option value="-1">-全部-</option>
		<option value="0" <c:if test="${returnOrder.useStatus == 0}">selected="selected"</c:if>>未提交</option>
		<option value="1" <c:if test="${returnOrder.useStatus == 1}">selected="selected"</c:if>>已提交</option>
	</select>&nbsp;&nbsp;
	<input type="submit" value="查询" class="query" />
</div>
</form>

<table width="100%" cellspacing="1" cellpadding="0" border="0" class="pn-ltable">
<thead class="pn-lthead">
<tr>
	<th>退货单号</th>
	<th>负责人</th>
	<th>创建时间</th>
	<th>状态</th>
	<th>操作</th>
</tr>
</thead>
<tbody class="pn-ltbody">
<c:forEach var="returnOrder" items="${page.list}">
<tr>
	<td align="center">${returnOrder.orderNumber }</td>
	<td align="center">${returnOrder.charger }</td>
	<td align="center"><fmt:formatDate value="${returnOrder.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	<td align="center">
		<c:choose>
			<c:when test="${returnOrder.useStatus == 0}">未提交</c:when>
			<c:when test="${returnOrder.useStatus == 1}">已提交</c:when>
		</c:choose>
	</td>
	<td align="center">
		<c:if test="${returnOrder.useStatus == 0 && currentUser.securityLevel >= 9}">
			<a href="<%=request.getContextPath()%>/pos/returnOrder!toReturnOrderFormView.do?returnOrder.id=${returnOrder.id }">修改</a>
			<a href="javascript:void(0);" onclick="returnOrder.deleteReturnOrder(${returnOrder.id });">删除</a>
		</c:if>
		<a href="<%=request.getContextPath()%>/pos/returnOrderItem!returnOrderItemList.do?returnOrderId=${returnOrder.id }">退货单条目</a>
	</td>
</tr>
</c:forEach>
</tbody>
</table>

<table width="100%" style="font-size:13px;">
	<tbody>
	<tr>
		<td align="center" class="pn-sp">
		<c:if var="isFirstPage" test="${page.pageNum<=1}">
		首页 &nbsp;&nbsp;&nbsp;上一页&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${!isFirstPage}">
		<a href="javascript:;" onclick="returnOrder.goPage(1,${page.pageCount});">首页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="returnOrder.goPage(${page.pageNum-1},${page.pageCount});">上一页</a>
		</c:if>
		&nbsp;&nbsp;&nbsp;
		<c:if var="isLastPage" test="${page.pageNum>=page.totalPages}">
		下一页&nbsp;&nbsp;&nbsp;尾页&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${!isLastPage}">
		<a href="javascript:;" onclick="returnOrder.goPage(${page.pageNum+1},${page.pageCount});">下一页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="returnOrder.goPage(${page.totalPages},${page.pageCount});">尾页</a>
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