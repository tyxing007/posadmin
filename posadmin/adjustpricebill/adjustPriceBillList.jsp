<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false"%>
<%@ include file="/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>调价单列表</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript">
var adjustPriceBill={};

//新增
adjustPriceBill.addAdjustPriceBill = function(){
	window.location.href = $('#initPath').val()+'/pos/adjustPriceBill!toFormView.do';
}

//删除
adjustPriceBill.deleteAdjustPriceBill = function(adjustPriceBillId){
	if(confirm('确定删除该调价单吗？')){
		window.location.href = $('#initPath').val()+'/pos/adjustPriceBill!deleteAdjustPriceBill.do?adjustPriceBill.id='+adjustPriceBillId;
	}
}

//同步活动
adjustPriceBill.syncBill = function(obj){
	$(obj).val('同步中...').attr('disabled', true);
	$.post($('#initPath').val()+'/pos/adjustPriceBill!syncAdjustPriceBill.do', function(result){
		if(result == 'true'){
			alert('同步成功！');
			window.location.href = $('#initPath').val()+'/pos/adjustPriceBill!adjustPriceBillList.do';
		}else{
			alert(result);
		}
	});
}

//跳转到指定页面
adjustPriceBill.goPage = function(pageNum,pageCount){
	var pageNum = '<input type="hidden" name="page.pageNum" value="'+pageNum+'" />';
	var pageCount = '<input type="hidden" name="page.pageCount" value="'+pageCount+'" />';
	$('#adjustPriceBillSearchForm').append(pageNum);
	$('#adjustPriceBillSearchForm').append(pageCount);
	$('#adjustPriceBillSearchForm').submit();
}

$(document).ready(function(){
});
</script>
</head>
<body style="min-height: 500px;">
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 调价单管理 - 列表</div>
	<div class="ropt">
		<input type="button" onclick="adjustPriceBill.addAdjustPriceBill();" class="submit" value="新增" /> &nbsp;
		<input type="button" onclick="adjustPriceBill.syncBill(this);" value="同步调价单" />&nbsp;&nbsp;&nbsp;
	</div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
<form id="adjustPriceBillSearchForm" action="<%=request.getContextPath()%>/pos/adjustPriceBill!adjustPriceBillList.do" style="padding-top:5px;" method="post">
<div>
	调价单号:<input type="text" name="billNumber" value="${billNumber}"/>&nbsp;&nbsp;
	商品名称:<input type="text" name="productName" value="${productName}"/>&nbsp;&nbsp;
	使用状态:
	<select name="useStatus">
		<option value="0">全部</option>
		<option value="1" <c:if test="${useStatus==1}">selected="selected"</c:if>>未提交</option>
		<option value="2" <c:if test="${useStatus==2}">selected="selected"</c:if>>已提交</option>
	</select>&nbsp;&nbsp;
	审核状态:
	<select name="auditStatus">
		<option value="0">全部</option>
		<option value="1" <c:if test="${auditStatus==1}">selected="selected"</c:if>>待审核</option>
		<option value="2" <c:if test="${auditStatus==2}">selected="selected"</c:if>>审核通过</option>
		<option value="3" <c:if test="${auditStatus==3}">selected="selected"</c:if>>审核未通过</option>
	</select>&nbsp;&nbsp;
	<input type="submit" value="查询" class="query" />
</div>
</form>

<table width="100%" cellspacing="1" cellpadding="0" border="0" class="pn-ltable">
<thead class="pn-lthead">
<tr>
	<th>调价单号</th>
	<th>商品条形码</th>
	<th>商品名称</th>
	<th>商品标价</th>
	<th>商品限价</th>
	<th>商品锁价</th>
	<th>目标价</th>
	<th>创建时间</th>
	<th>使用状态</th>
	<th>审核状态</th>
	<th>操作</th>
</tr>
</thead>
<tbody class="pn-ltbody">
<c:forEach var="bill" items="${page.list}">
<tr>
	<td align="center">${bill.billNumber}</td>
	<td align="center">${bill.product.barCode}</td>
	<td>${bill.product.name}</td>
	<td align="right"><fmt:formatNumber value="${bill.product.salePrice}" pattern="#,##0.00" /></td>
	<td align="right"><fmt:formatNumber value="${bill.product.limitPrice}" pattern="#,##0.00" /></td>
	<td align="right"><fmt:formatNumber value="${bill.product.lockPrice}" pattern="#,##0.00" /></td>
	<td align="right"><fmt:formatNumber value="${bill.targetPrice}" pattern="#,##0.00" /></td>
	<td align="center"><fmt:formatDate value="${bill.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	<td align="center">
		<c:if test="${bill.useStatus==1}">未提交</c:if>
		<c:if test="${bill.useStatus==2}">已提交</c:if>
	</td>
	<td align="center">
		<c:if test="${bill.auditStatus==1}">待审核</c:if>
		<c:if test="${bill.auditStatus==2}">审核通过</c:if>
		<c:if test="${bill.auditStatus==3}">审核未通过</c:if>
	</td>
	<td align="center">
		<c:if test="${bill.useStatus==1}">
		<a href="<%=request.getContextPath()%>/pos/adjustPriceBill!toFormView.do?adjustPriceBill.id=${bill.id}">修改</a>
		<a href="javascript:void(0);" onclick="adjustPriceBill.deleteAdjustPriceBill(${bill.id});">删除</a>
		</c:if>
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
		<a href="javascript:;" onclick="adjustPriceBill.goPage(1,${page.pageCount});">首页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="adjustPriceBill.goPage(${page.pageNum-1},${page.pageCount});">上一页</a>
		</c:if>
		&nbsp;&nbsp;&nbsp;
		<c:if var="isLastPage" test="${page.pageNum>=page.totalPages}">
		下一页&nbsp;&nbsp;&nbsp;尾页&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${!isLastPage}">
		<a href="javascript:;" onclick="adjustPriceBill.goPage(${page.pageNum+1},${page.pageCount});">下一页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="adjustPriceBill.goPage(${page.totalPages},${page.pageCount});">尾页</a>
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