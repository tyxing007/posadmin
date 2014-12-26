<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<table cellspacing="1" cellpadding="2" border="0" width="100%" class="pn-ftable" style="font-size:13px;">
<tbody>
	<tr>
		<td width="20%" class="pn-flabel pn-flabel-h">姓名：</td>
		<td width="80%" class="pn-fcontent">${cashier.name }</td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h">身份证：</td>
		<td class="pn-fcontent">${cashier.idCard }</td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h">用户名：</td>
		<td class="pn-fcontent">${cashier.username }</td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h">使用状态：</td>
		<td class="pn-fcontent">
			<c:if test="${cashier.forbidden == 0 }">有效</c:if>
			<c:if test="${cashier.forbidden == 1 }">禁用</c:if>
		</td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h">销售价类型：</td>
		<td class="pn-fcontent">
			<c:if test="${cashier.saleType==1}">仅标牌价</c:if>
			<c:if test="${cashier.saleType==2}">突破标牌价</c:if>
			<c:if test="${cashier.saleType==3}">突破限价</c:if>
			<c:if test="${cashier.saleType==4}">突破锁价</c:if>
		</td>
	</tr>
	<tr>
		<td align="center" colspan="2" class="pn-fcontent">
			<input type="button" value="返回" onclick="$('a.jMsgbox-closeWrap').click();" class="return-button" />
		</td>
	</tr>
</tbody>
</table>
