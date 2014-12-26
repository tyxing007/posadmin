<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>交接列表</title>
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
	<div class="rpos">当前位置: 交接记录</div>
	<div class="ropt"></div>
	<div class="clear"></div>
</div>

<form action="<%=request.getContextPath()%>/pos/balance!balanceList.do" method="post" style="padding-top:5px; padding-left:10px;">
<div>
	日期:<input type="text" name="date" class="Wdate" onclick="WdatePicker()" onfocus="WdatePicker({maxDate:'%y-%M-%d'})" readonly="readonly" value="${date}">&nbsp;&nbsp;
	POS机编号:<input type="text" name="posCode" value="${posCode}" />&nbsp;&nbsp;
	<input type="submit" value="查询" class="query" />
</div>
</form>
<br/>

<div class="body-box">
<table width="100%" cellspacing="1" cellpadding="0" border="0" class="pn-ltable">
<thead class="pn-lthead">
	<tr>
		<th>POS机编号</th>
		<th>收银员</th>
		<th>接收人</th>
		<th>交接时间</th>
		<th>理论金额</th>
		<th>POS金额</th>
		<th>银头</th>
		<th>追加金额</th>
		<th>备注</th>
	</tr>
</thead>
<tbody class="pn-ltbody">
	<c:forEach var="balance" items="${balanceList}">
	<tr>
		<td align="center">${balance.posCode }</td>
		<td>${balance.cashier.name }</td>
		<td>${balance.handover.name }</td>
		<td align="center"><fmt:formatDate value="${balance.startTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
		<td align="right"><fmt:formatNumber value="${balance.theoryCash}" pattern="#,##0.00" /></td>
		<td align="right"><fmt:formatNumber value="${balance.posCash}" pattern="#,##0.00" /></td>
		<td align="right"><fmt:formatNumber value="${balance.initPoscash}" pattern="#,##0.00" /></td>
		<td align="right"><fmt:formatNumber value="${balance.addPoscash}" pattern="#,##0.00" /></td>
		<td>${balance.backText }</td>
	</tr>
	</c:forEach>
</tbody>
</table>
</div>
</body>
</html>