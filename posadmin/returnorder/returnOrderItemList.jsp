<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>退货单条目列表</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript">
var returnOrderItem={};

//新增退货单
returnOrderItem.addReturnOrderItem = function(){
	window.location.href = $('#initPath').val()+'/pos/returnOrderItem!toReturnOrderItemFormView.do?returnOrderId='+$('#returnOrderId').val();
}

//删除退货单
returnOrderItem.deleteReturnOrderItem = function(returnOrderItemId){
	if(confirm('确定删除该退货单条目吗？')){
		window.location.href = $('#initPath').val()+'/pos/returnOrderItem!deleteReturnOrderItem.do?returnOrderItem.id='+returnOrderItemId+'&returnOrderId='+$('#returnOrderId').val();
	}
}

//跳转到指定页面
returnOrderItem.goPage = function(pageNum,pageCount){
	var url = $('#initPath').val()+'/pos/returnOrderItem!returnOrderItemList.do?returnOrderId='+$('#returnOrderId').val();
	if(pageNum != null && pageCount != null){
		url += '&page.pageNum='+pageNum;
	}
	if(pageCount != null){
		url += '&page.pageCount='+pageCount;
	}
	window.location.href = url;
}

//返回列表
returnOrderItem.backList = function(){
	window.location.href = $('#initPath').val()+'/pos/returnOrder!returnOrderList.do';
}

//提交退货单
returnOrderItem.submitReturnOrder = function(returnOrderId){
	if(confirm('提交后将不能修改退货单信息，确定提交该退货单吗？')){
		window.location.href = $('#initPath').val()+'/pos/returnOrder!submitReturnOrder.do?returnOrder.id='+returnOrderId;
	}
}
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 退货单管理 - 退货单条目列表</div>
	<div class="ropt">
		<c:if test="${returnOrder.useStatus == 0 && currentUser.securityLevel >= 9}">
			<c:if test="${page.totalRecords > 0}">
			<input type="button" onclick="returnOrderItem.submitReturnOrder(${returnOrder.id});" class="" value="提交退货单" /> &nbsp; 
			</c:if>
			<input type="button" onclick="returnOrderItem.addReturnOrderItem();" class="submit" value="新增" /> &nbsp; 
		</c:if>
		<input type="button" class="return-button" value="返回" onclick="returnOrderItem.backList();" />&nbsp;&nbsp;&nbsp;
	</div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
<form style="padding-top:5px;" action="<%=request.getContextPath()%>/pos/returnOrderItem!returnOrderItemList.do" method="post">
<input type="hidden" id="returnOrderId" name="returnOrderId" value="${returnOrderId }" />
<div>
	商品名称:<input type="text" name="productName" value="${productName}" />&nbsp;&nbsp;
	<input type="submit" value="查询" class="query" />
</div>
</form>

<table id="goodsList" width="100%" cellspacing="1" cellpadding="0" border="0" style="" class="pn-ltable">
<thead class="pn-lthead">
<tr>
	<th>序号</th>
	<th>退货单号</th>
	<th>商品名称</th>
	<th>商品数量</th>
	<th>操作</th>
</tr>
</thead>
<tbody class="pn-ltbody">
<c:forEach var="returnOrderItem" items="${page.list}" varStatus="status">
<tr>
	<td align="center">${status.index+1}</td>
	<td align="center">${returnOrder.orderNumber }</td>
	<td>${returnOrderItem.product.name }</td>
	<td align="center">${returnOrderItem.count}</td>
	<td align="center">
		<c:if test="${returnOrder.useStatus == 0}">
			<a href="<%=request.getContextPath()%>/pos/returnOrderItem!toReturnOrderItemFormView.do?returnOrderId=${returnOrderId}&returnOrderItem.id=${returnOrderItem.id }">修改</a>
			<a href="javascript:void(0);" onclick="returnOrderItem.deleteReturnOrderItem(${returnOrderItem.id });">删除</a>
		</c:if>
	</td>
</tr>
</c:forEach>
</tbody>
</table>

<c:if test="${page.totalPages > 1}">
<table width="100%" style="font-size:13px;">
	<tbody>
	<tr>
		<td align="center" class="pn-sp">
		<c:if var="isFirstPage" test="${page.pageNum<=1}">
		首页 &nbsp;&nbsp;&nbsp;上一页&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${!isFirstPage}">
		<a href="javascript:;" onclick="returnOrderItem.goPage(1,${page.pageCount});">首页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="returnOrderItem.goPage(${page.pageNum-1},${page.pageCount});">上一页</a>
		</c:if>
		&nbsp;&nbsp;&nbsp;
		<c:if var="isLastPage" test="${page.pageNum>=page.totalPages}">
		下一页&nbsp;&nbsp;&nbsp;尾页&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${!isLastPage}">
		<a href="javascript:;" onclick="returnOrderItem.goPage(${page.pageNum+1},${page.pageCount});">下一页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="returnOrderItem.goPage(${page.totalPages},${page.pageCount});">尾页</a>
		</c:if>
		&nbsp;&nbsp;&nbsp;&nbsp;
		第${page.pageNum}/${page.totalPages}页,共${page.totalRecords}条记录
	</td>
	</tr>
	</tbody>
</table>
</c:if>
</div>
</body>
</html>