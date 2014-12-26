<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>红蓝卡充值明细</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
var priceCardCharge={};

//跳转到指定页面
priceCardCharge.goPage = function(pageNum,pageCount){
	var pageNum = '<input type="hidden" name="page.pageNum" value="'+pageNum+'" />';
	var pageCount = '<input type="hidden" name="page.pageCount" value="'+pageCount+'" />';
	$('#priceCardChargeForm').append(pageNum);
	$('#priceCardChargeForm').append(pageCount);
	$('#priceCardChargeForm').submit();
}

//返回列表
priceCardCharge.backList = function(){
	window.location.href = $('#initPath').val()+'/pos/priceCard!priceCardList.do';
}
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 红蓝卡充值明细 - <span style="font-weight:bold;">${priceCardId}</span></div>
	<div class="ropt">
		<input type="button" value="返回" onclick="priceCardCharge.backList();" class="return-button" />&nbsp;&nbsp;
	</div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
<form id="priceCardChargeForm" action="<%=request.getContextPath()%>/pos/priceCardCharge!priceCardChargeList.do" method="post" style="padding-top:5px;">
<input type="hidden" name="priceCardId" value="${priceCardId }" />
<div style="vertical-align: middle;">
	类型:
	<select name="consumeType">
		<option value="0">全部</option>
		<option value="1" <c:if test="${consumeType==1}">selected="selected"</c:if>>充值</option>
		<option value="2" <c:if test="${consumeType==2}">selected="selected"</c:if>>消费</option>
	</select>&nbsp;&nbsp;&nbsp;
	<input id="queryBut" type="submit" class="query" value="查询" />
</div>
</form>
<table width="100%" cellspacing="1" cellpadding="0" border="0" class="pn-ltable">
<thead class="pn-lthead">
      <tr>
		<th>时间</th>
		<th>类型</th>
		<th>收入(元)</th>
		<th>支出(元)</th>
		<th>账户余额(元)</th>
	</tr>
</thead>
<tbody class="pn-ltbody">
	<c:forEach var="detail" items="${page.list}">
		<tr>
			<td align="center"><fmt:formatDate value="${detail.chargeTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			<td align="center">
				<c:choose>
					<c:when test="${detail.consumeType == 1}">充值</c:when>
					<c:when test="${detail.consumeType == 2}">消费</c:when>
				</c:choose>
			</td>
			<td align="right">
				<c:if test="${detail.consumeType == 1}">
				<span style="color:#2ABE7D; font-weight:bold;"><fmt:formatNumber value="${detail.consumeCash}" pattern="#,##0.00" /></span>
				</c:if>
			</td>
			<td align="right">
				<c:if test="${detail.consumeType == 2}">
					<span style="color:#FF7200; font-weight:bold;"><fmt:formatNumber value="${detail.consumeCash}" pattern="#,##0.00" /></span>
				</c:if>
			</td>
			<td align="right"><fmt:formatNumber value="${detail.totalCash}" pattern="#,##0.00" /></td>
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
		<a href="javascript:;" onclick="priceCardCharge.goPage(1,${page.pageCount});">首页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="priceCardCharge.goPage(${page.pageNum-1},${page.pageCount});">上一页</a>
		</c:if>
		&nbsp;&nbsp;&nbsp;
		<c:if var="isLastPage" test="${page.pageNum>=page.totalPages}">
		下一页&nbsp;&nbsp;&nbsp;尾页&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${!isLastPage}">
		<a href="javascript:;" onclick="priceCardCharge.goPage(${page.pageNum+1},${page.pageCount});">下一页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="priceCardCharge.goPage(${page.totalPages},${page.pageCount});">尾页</a>
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