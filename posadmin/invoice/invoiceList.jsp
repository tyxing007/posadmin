<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>进销存列表</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript">
var invoice={};

//跳转到指定页面
invoice.goPage = function(pageNum,pageCount){
	var url = $('#initPath').val()+'/pos/invoice!invoiceList.do?productId='+$('#productId').val();
	if(pageNum != null && pageCount != null){
		url += '&page.pageNum='+pageNum;
	}
	if(pageCount != null){
		url += '&page.pageCount='+pageCount;
	}
	window.location.href = url;
}

//返回列表
invoice.backList = function(){
	window.location.href = $('#initPath').val()+'/pos/invoice!productList.do';
}
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<input type="hidden" id="productId" value="${productId }" />
<div class="box-positon">
	<div class="rpos">当前位置: 进销存管理 - 操作列表</div>
	<div class="ropt">
		<input type="button" class="return-button" value="返回" onclick="invoice.backList();" />&nbsp;&nbsp;
	</div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">

<table id="goodsList" width="100%" cellspacing="1" cellpadding="0" border="0" style="" class="pn-ltable">
<thead class="pn-lthead">
<tr>
	<th>商品名称</th>
	<th>操作类型</th>
	<th>操作前商品数量</th>
	<th>商品变动数量</th>
	<th>操作后商品数量</th>
	<th>操作人</th>
	<th>操作时间</th>
	<th>流水单号</th>
	<th>备注</th>
</tr>
</thead>
<tbody class="pn-ltbody">
<c:forEach var="invoice" items="${page.list}">
<tr>
	<td>${invoice.productName }</td>
	<td align="center">
		<c:choose>
			<c:when test="${invoice.operType == 0}">收货</c:when>
			<c:when test="${invoice.operType == 1}">销售</c:when>
			<c:when test="${invoice.operType == 2}">退货</c:when>
			<c:when test="${invoice.operType == 3}">租赁</c:when>
			<c:when test="${invoice.operType == 4}">还租</c:when>
			<c:when test="${invoice.operType == 5}">盘点</c:when>
			<c:when test="${invoice.operType == 6}">退货到中心库</c:when>
		</c:choose>
	</td>
	<td>${invoice.beforeCount }</td>
	<td>${invoice.count }</td>
	<td>${invoice.afterCount }</td>
	<td>${invoice.operUser }</td>
	<td align="center"><fmt:formatDate value="${invoice.createTime }" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	<td align="center">${invoice.serialNumber}</td>
	<td>${invoice.remark }</td>
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
		<a href="javascript:;" onclick="invoice.goPage(1,${page.pageCount});">首页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="invoice.goPage(${page.pageNum-1},${page.pageCount});">上一页</a>
		</c:if>
		&nbsp;&nbsp;&nbsp;
		<c:if var="isLastPage" test="${page.pageNum>=page.totalPages}">
		下一页&nbsp;&nbsp;&nbsp;尾页&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${!isLastPage}">
		<a href="javascript:;" onclick="invoice.goPage(${page.pageNum+1},${page.pageCount});">下一页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="invoice.goPage(${page.totalPages},${page.pageCount});">尾页</a>
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