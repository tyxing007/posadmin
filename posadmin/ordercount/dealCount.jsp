<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>交易统计</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 统计</div>
	<div class="ropt"></div>
	<div class="clear"></div>
</div>

<form action="<%=request.getContextPath()%>/pos/orderCount!getCount.do" method="post" style="padding-top:5px; padding-left:10px;">
<table width="600" border="0" cellpadding="1" cellspacing="0">
	<tr>
		<td align="right">交易日期：</td>
		<td>
			<input type="text" name="date" class="Wdate" onclick="WdatePicker()" onfocus="WdatePicker({maxDate:'%y-%M-%d'})" readonly="readonly" value="${date}">
			<input type="submit" value="查询" class="query" />
		</td>
	</tr>
</table>
</form>
<br/>

<div class="body-box">
<table width="600" border="1" cellpadding="0" cellspacing="0">
	<tr align="center">
		<th rowspan="2" width="100" style="font-size: 20px;">销售额</th>
		<th width="250">销售金额</th>
		<th width="250">退货金额</th>
	</tr>
	<tr align="center">
		<td>${counter.totalSales}</td>
		<td>${counter.totalBackSales}</td>
	</tr>
</table>
<br/>

<table width="600" border="1" cellpadding="1" cellspacing="0">
	<tr align="center">
		<th rowspan="2" width="100" style="font-size: 20px;">订单数量</th>
		<th>销售订单</th>
		<th>退货订单</th>
		<th>租赁订单</th>
		<th>还租订单</th>
	</tr>
	<tr align="center">
		<td>${counter.totalSaleOrders}</td>
		<td>${counter.totalBackSaleOrders}</td>
		<td>${counter.totalLeaseOrders}</td>
		<td>${counter.totalBackLeaseOrders}</td>
	</tr>
</table>
<br/>

<table width="600" border="1" cellpadding="1" cellspacing="0">
	<tr align="center">
		<th rowspan="2" width="100" style="font-size: 20px;">押金</th>
		<th width="250">收入</th>
		<th width="250">退还</th>
	</tr>
	<tr align="center">
		<td>${counter.totalDeposit}</td>
		<td>${counter.totalBackDeposit}</td>
	</tr>
</table>
<br/>

<table width="600" border="1" cellpadding="1" cellspacing="0">
	<tr align="center">
		<th rowspan="2" width="100" style="font-size: 20px;">租金</th>
		<th>收入</th>
	</tr>
	<tr align="center">
		<td>${counter.totalLeaseCash}</td>
	</tr>
</table>
</div>
</body>
</html>