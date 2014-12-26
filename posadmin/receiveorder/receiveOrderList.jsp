<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>收货单列表</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
var receiveOrder={};

//跳转到指定页面
receiveOrder.goPage = function(pageNum,pageCount){
	var pageNum = '<input type="hidden" name="page.pageNum" value="'+pageNum+'" />';
	var pageCount = '<input type="hidden" name="page.pageCount" value="'+pageCount+'" />';
	$('#receiveOrderSearchForm').append(pageNum);
	$('#receiveOrderSearchForm').append(pageCount);
	$('#receiveOrderSearchForm').submit();
}

//同步发货单
receiveOrder.syncSendOrder = function(obj){
	window.location.href = $('#initPath').val()+'/pos/receiveOrder!syncSendOrderFromPoscenter.do';
	$(obj).val('同步中...').attr('disabled', true);
}
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 收货单管理 - 收货单列表</div>
	<div class="ropt">
		<input type="button" onclick="receiveOrder.syncSendOrder(this);" class="" value="同步发货单" /> &nbsp; 
	</div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
<form id="receiveOrderSearchForm" style="padding-top:5px;" action="<%=request.getContextPath()%>/pos/receiveOrder!receiveOrderList.do" method="post">
<div>
	收货单号:<input type="text" size="12" name="receiveOrder.orderNumber" value="${receiveOrder.orderNumber }" />&nbsp;&nbsp;
	负责人:<input type="text" size="12" name="receiveOrder.charger" value="${receiveOrder.charger }" />&nbsp;&nbsp;
	创建时间:<input type="text" size="15" name="startTime" class="Wdate" onclick="WdatePicker()" value="${startTime }" readonly="readonly" />
	- <input type="text" size="15" name="endTime" class="Wdate" onclick="WdatePicker()" value="${endTime }" readonly="readonly"/>&nbsp;&nbsp;
	订单状态:
	<select name="receiveOrder.useStatus">
		<option value="-1">-全部-</option>
		<option value="0" <c:if test="${receiveOrder.useStatus == 0}">selected="selected"</c:if>>未确认</option>
		<option value="1" <c:if test="${receiveOrder.useStatus == 1}">selected="selected"</c:if>>已确认</option>
	</select>&nbsp;&nbsp;
	<input type="submit" value="查询" class="query" />
</div>
</form>

<table width="100%" cellspacing="1" cellpadding="0" border="0" class="pn-ltable">
<thead class="pn-lthead">
<tr>
	<th>收货单号</th>
	<th>负责人</th>
	<th>创建时间</th>
	<th>状态</th>
	<th>操作</th>
</tr>
</thead>
<tbody class="pn-ltbody">
<c:forEach var="receiveOrder" items="${page.list}">
<tr>
	<td align="center">${receiveOrder.orderNumber }</td>
	<td align="center">${receiveOrder.charger }</td>
	<td align="center"><fmt:formatDate value="${receiveOrder.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	<td align="center">
		<c:choose>
			<c:when test="${receiveOrder.useStatus == 0}">未确认</c:when>
			<c:when test="${receiveOrder.useStatus == 1}">已确认</c:when>
		</c:choose>
	</td>
	<td align="center">
		<c:if test="${receiveOrder.useStatus == 0}">
			<a href="<%=request.getContextPath()%>/pos/receiveOrderItem!receiveOrderItemList.do?receiveOrderId=${receiveOrder.id }">确认收货</a>
		</c:if>
		<c:if test="${receiveOrder.useStatus == 1}">
			<a href="<%=request.getContextPath()%>/pos/receiveOrderItem!receiveOrderItemList.do?receiveOrderId=${receiveOrder.id }">收货单条目</a>
		</c:if>
	</td>
</tr>
</c:forEach>
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
		<a href="javascript:;" onclick="receiveOrder.goPage(1,${page.pageCount});">首页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="receiveOrder.goPage(${page.pageNum-1},${page.pageCount});">上一页</a>
		</c:if>
		&nbsp;&nbsp;&nbsp;
		<c:if var="isLastPage" test="${page.pageNum>=page.totalPages}">
		下一页&nbsp;&nbsp;&nbsp;尾页&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${!isLastPage}">
		<a href="javascript:;" onclick="receiveOrder.goPage(${page.pageNum+1},${page.pageCount});">下一页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="receiveOrder.goPage(${page.totalPages},${page.pageCount});">尾页</a>
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