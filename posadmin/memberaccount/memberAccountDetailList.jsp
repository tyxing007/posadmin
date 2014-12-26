<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>会员收支明细</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
var memberAccountDetail={};

//跳转到指定页面
memberAccountDetail.goPage = function(pageNum,pageCount){
	var pageNum = '<input type="hidden" name="page.pageNum" value="'+pageNum+'" />';
	var pageCount = '<input type="hidden" name="page.pageCount" value="'+pageCount+'" />';
	$('#memberAccountDetailForm').append(pageNum);
	$('#memberAccountDetailForm').append(pageCount);
	$('#memberAccountDetailForm').submit();
}

//返回列表
memberAccountDetail.backList = function(){
	window.location.href = $('#initPath').val()+'/pos/member!memberList.do';
}
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 会员收支明细 - <span style="font-weight:bold;">${member.name}</span></div>
	<div class="ropt">
		<input type="button" value="返回" onclick="memberAccountDetail.backList();" class="return-button" />&nbsp;&nbsp;
	</div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
<form id="memberAccountDetailForm" action="<%=request.getContextPath()%>/pos/memberAccountDetail!memberAccountDetailList.do" method="post" style="padding-top:5px;">
<input type="hidden" name="memberId" value="${memberId }" />
<div style="vertical-align: middle;">
	资金流向:<input type="checkbox" name="moneyFlow" value="income" <c:if test="${moneyFlow=='income'}">checked="checked"</c:if> />收入&nbsp;
	<input type="checkbox" name="moneyFlow" value="pay" <c:if test="${moneyFlow=='pay'}">checked="checked"</c:if> />支出&nbsp;&nbsp;&nbsp;
	类型:
	<select name="type">
		<option value="0">全部</option>
		<option value="1" <c:if test="${type==1}">selected="selected"</c:if>>充值</option>
		<option value="2" <c:if test="${type==2}">selected="selected"</c:if>>提现</option>
		<option value="3" <c:if test="${type==3}">selected="selected"</c:if>>购买</option>
		<option value="4" <c:if test="${type==4}">selected="selected"</c:if>>租赁</option>
		<option value="5" <c:if test="${type==5}">selected="selected"</c:if>>退货</option>
	</select>&nbsp;&nbsp;&nbsp;
	创建时间:<input type="text" size="15" name="startTime" class="Wdate" onclick="WdatePicker()" value="${startTime }" readonly="readonly" />
	- <input type="text" size="15" name="endTime" class="Wdate" onclick="WdatePicker()" value="${endTime }" readonly="readonly"/>&nbsp;&nbsp;
	<input id="queryBut" type="submit" class="query" value="查询" />
</div>
</form>
<table width="100%" cellspacing="1" cellpadding="0" border="0" class="pn-ltable">
<thead class="pn-lthead">
      <tr>
		<th>日期</th>
		<th>类型</th>
		<th>收入(元)</th>
		<th>支出(元)</th>
		<th>账户余额(元)</th>
		<th>详情</th>
	</tr>
</thead>
<tbody class="pn-ltbody">
	<c:forEach var="detail" items="${page.list}">
		<tr>
			<td align="center"><fmt:formatDate value="${detail.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			<td align="center">
				<c:choose>
					<c:when test="${detail.type == 1}">充值</c:when>
					<c:when test="${detail.type == 2}">提现</c:when>
					<c:when test="${detail.type == 3}">购买</c:when>
					<c:when test="${detail.type == 4}">租赁</c:when>
					<c:when test="${detail.type == 5}">退货</c:when>
				</c:choose>
			</td>
			<td align="right">
				<c:if test="${detail.income != 0}">
				<span style="color:#2ABE7D; font-weight:bold;"><fmt:formatNumber value="${detail.income}" pattern="#,##0.00" /></span>
				</c:if>
			</td>
			<td align="right">
				<c:if test="${detail.pay != 0}">
					<span style="color:#FF7200; font-weight:bold;"><fmt:formatNumber value="${detail.pay}" pattern="#,##0.00" /></span>
				</c:if>
			</td>
			<td align="right"><fmt:formatNumber value="${detail.balance}" pattern="#,##0.00" /></td>
			<td align="center">
				<c:if test="${detail.type == 3}">
					<a href="<%=request.getContextPath()%>/pos/order!saledOrderList.do?orderId=${detail.orderId}">查看</a>
				</c:if>
				<c:if test="${detail.type == 4}">
					<a href="<%=request.getContextPath()%>/pos/order!leaseOrderList.do?orderId=${detail.orderId}&orderType=1">查看</a>
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
		<a href="javascript:;" onclick="memberAccountDetail.goPage(1,${page.pageCount});">首页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="memberAccountDetail.goPage(${page.pageNum-1},${page.pageCount});">上一页</a>
		</c:if>
		&nbsp;&nbsp;&nbsp;
		<c:if var="isLastPage" test="${page.pageNum>=page.totalPages}">
		下一页&nbsp;&nbsp;&nbsp;尾页&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${!isLastPage}">
		<a href="javascript:;" onclick="memberAccountDetail.goPage(${page.pageNum+1},${page.pageCount});">下一页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="memberAccountDetail.goPage(${page.totalPages},${page.pageCount});">尾页</a>
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