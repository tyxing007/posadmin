<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ include file="/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>系统配置信息</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript">
var systemConfig={};
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 系统配置信息</div>
	<div class="ropt"></div>
	<div class="clear"></div>
</div>

<table cellspacing="1" cellpadding="2" border="0" width="600" class="pn-ftable" style="font-size:13px; margin-left: 5px;">
<thead class="pn-lthead">
	<tr>
		<th colspan="2">系统配置信息</th>
	</tr>
</thead>
<tbody>
	<tr>
		<td width="30%" class="pn-flabel pn-flabel-h">店面编号：</td>
		<td width="70%" class="pn-fcontent">${systemConfig.shopCode}</td>
	</tr>
	<tr>
		<td width="30%" class="pn-flabel pn-flabel-h">销售订单上次提交时间：</td>
		<td width="70%" class="pn-fcontent"><span><fmt:formatDate value="${systemConfig.saledOrderLastSubmitTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span></td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h">租赁订单上次提交时间：</td>
		<td class="pn-fcontent"><span><fmt:formatDate value="${systemConfig.leaseOrderLastSubmitTime}" pattern="yyyy-MM-dd HH:mm:ss"/></span></td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h">进销存上次提交时间：</td>
		<td class="pn-fcontent"><fmt:formatDate value="${systemConfig.invoiceLastSubmitTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h">会员信息上次提交时间：</td>
		<td class="pn-fcontent"><fmt:formatDate value="${systemConfig.memberLastSubmitTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h">会员信息上次同步时间：</td>
		<td class="pn-fcontent"><fmt:formatDate value="${systemConfig.memberLastSyncTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	</tr>
</tbody>
</table>
</body>
</html>