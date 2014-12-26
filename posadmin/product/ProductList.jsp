<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="s" uri="/struts-tags"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商品列表</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />
<link rel="STYLESHEET" type="text/css" href="<%=request.getContextPath()%>/js/jquery/msgbox/jquery.msgbox.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery/msgbox/jquery.dragndrop.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery/msgbox/jquery.msgbox.js"></script>
<script type="text/javascript">
var product={};

//导出Excel
product.exportExcel = function(){
	window.location.href = $('#initPath').val()+'/pos/Product!exportExcel.do';
}

//点击同步商品
product.clickSyncProduct = function(obj){
	window.location.href = $('#initPath').val()+'/pos/Product!syncProductDataFromPoscenter.do';
	$(obj).val('同步中...').attr('disabled', true);
}

//提交库存信息
product.submitShopStockToCenter = function(obj){
	$.post($('#initPath').val()+'//pos/Product!submitShopStockToCenter.do', function(result){
		if(result == 'true'){
			alert('提交成功！');
		}else{
			alert(result);
		}
	});
}

//查看
product.showProduct = function(id){
	new $.msgbox({
		title: '查看商品详情',
		width: 700,
		height: 480,
		type: 'ajax',
		content: $('#initPath').val()+'/pos/Product!toProductDetailView.do?product.id='+id
	}).show();
}

//查看销售标签
product.showSaleTagView = function(productId){
    new $.msgbox({
		title: '销售标签',
		width: 700,
		height: 480,
		anim: 1,
		type: 'ajax',
		content: $('#initPath').val()+'/pos/Product!toShowSaleTagView.do?product.id='+productId
	}).show();
}

//跳转到指定页面
product.goPage = function(pageNum,pageCount){
	var pageNum = '<input type="hidden" name="page.pageNum" value="'+pageNum+'" />';
	var pageCount = '<input type="hidden" name="page.pageCount" value="'+pageCount+'" />';
	$('#productSearchForm').append(pageNum);
	$('#productSearchForm').append(pageCount);
	$('#productSearchForm').submit();
}
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 商品管理 - 列表</div>
	<div class="ropt">
		<input type="button" onclick="product.clickSyncProduct(this);" class="return-button" value="同步商品" /> &nbsp; 
		<input type="button" onclick="product.exportExcel();" class="sendbox" value="导出Excel" /> &nbsp; 
		<input title="把库存信息提交到中心库" type="button" onclick="product.submitShopStockToCenter();" class="upload-file" value="提交库存" /> &nbsp; 
	</div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
<form id="productSearchForm" style="padding-top:5px;" action="<%=request.getContextPath()%>/pos/Product!productList.do" method="post">
<div>
	条形码:<input type="text" name="barCode" value="${barCode}" />&nbsp;&nbsp;
	商品名称:<input type="text" name="productName" value="${productName}" />&nbsp;&nbsp;
	<input type="submit" value="查询" class="query" />
</div>
</form>

<table id="goodsList" width="100%" cellspacing="1" cellpadding="0" border="0" class="pn-ltable">
<thead class="pn-lthead">
	<tr>
		<th>条形码</th>
		<th>商品名称</th>
		<th>标牌价</th>
		<th>限价</th>
		<th>锁价</th>
		<th>红卡额度</th>
		<th>蓝卡额度</th>
		<th>日租赁价格</th>
		<th>包月租赁价格</th>
		<th>押金</th>
		<th>库存量</th>
		<th>操作</th>
	</tr>
</thead>
<tbody class="pn-ltbody">
    <s:iterator value="page.list" var="li">
	<tr>
		<td align="center"><s:property value="#li.barCode"/></td>
		<td><s:property value="#li.name"/></td>
		<td align="right"><s:property value="#li.salePrice"/></td>
		<td align="right"><s:property value="#li.limitPrice"/></td>
		<td align="right"><s:property value="#li.lockPrice"/></td>
		<td align="right"><s:property value="#li.redLines"/></td>
		<td align="right"><s:property value="#li.blueLines"/></td>
		<td align="right"><s:property value="#li.leasePrice"/></td>
		<td align="right"><s:property value="#li.monthLeasePrice"/></td>
		<td align="right"><s:property value="#li.deposit"/></td>
		<td align="right"><s:property value="#li.stock"/></td>
		<td align="center">
			<a href="javascript:;" onclick="product.showProduct(<s:property value="#li.id"/>);">查看</a>
          	<a href="<%=request.getContextPath()%>/pos/Product!toProductFormView.do?product.id=<s:property value="#li.id"/>">修改</a>
          	<a href="javascript:;" onclick="product.showSaleTagView(<s:property value="#li.id"/>);">销售标签</a>
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
		<a href="javascript:;" onclick="product.goPage(1,${page.pageCount});">首页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="product.goPage(${page.pageNum-1},${page.pageCount});">上一页</a>
		</c:if>
		&nbsp;&nbsp;&nbsp;
		<c:if var="isLastPage" test="${page.pageNum>=page.totalPages}">
		下一页&nbsp;&nbsp;&nbsp;尾页&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${!isLastPage}">
		<a href="javascript:;" onclick="product.goPage(${page.pageNum+1},${page.pageCount});">下一页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="product.goPage(${page.totalPages},${page.pageCount});">尾页</a>
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
