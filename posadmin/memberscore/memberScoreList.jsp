<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>会员积分明细</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript">
var memberScore={};

//跳转到指定页面
memberScore.goPage = function(pageNum,pageCount){
	var pageNum = '<input type="hidden" name="page.pageNum" value="'+pageNum+'" />';
	var pageCount = '<input type="hidden" name="page.pageCount" value="'+pageCount+'" />';
	$('#memberScoreForm').append(pageNum);
	$('#memberScoreForm').append(pageCount);
	$('#memberScoreForm').submit();
}

//返回列表
memberScore.backList = function(){
	window.location.href = $('#initPath').val()+'/pos/member!memberList.do';
}
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: 会员积分明细 - <span style="font-weight:bold;">${member.name}</span></div>
	<div class="ropt">
		<input type="button" value="返回" onclick="memberScore.backList();" class="return-button" />&nbsp;&nbsp;
	</div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
<form id="memberScoreForm" action="<%=request.getContextPath()%>/pos/memberScore!memberScoreList.do" method="post" style="padding-top:5px;">
<input type="hidden" name="memberId" value="${memberId }" />
<div style="vertical-align: middle;">
	积分流向:<input type="checkbox" name="scoreFlow" value="add" <c:if test="${scoreFlow=='add'}">checked="checked"</c:if> />新增&nbsp;
	<input type="checkbox" name="scoreFlow" value="minus" <c:if test="${scoreFlow=='minus'}">checked="checked"</c:if> />兑换&nbsp;&nbsp;&nbsp;
	类型:
	<select name="type">
		<option value="0">全部</option>
		<option value="1" <c:if test="${type==1}">selected="selected"</c:if>>购物</option>
		<option value="2" <c:if test="${type==2}">selected="selected"</c:if>>退货</option>
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
		<th>新增</th>
		<th>兑换</th>
		<th>当前积分</th>
		<th>详情</th>
	</tr>
</thead>
<tbody class="pn-ltbody">
	<c:forEach var="score" items="${page.list}">
		<tr>
			<td align="center"><fmt:formatDate value="${score.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			<td align="center">
				<c:choose>
					<c:when test="${score.type == 1}">购物</c:when>
					<c:when test="${score.type == 2}">退货</c:when>
				</c:choose>
			</td>
			<td align="right">
				<c:if test="${score.addScore != 0}">
				<span style="color:#2ABE7D; font-weight:bold;"><fmt:formatNumber value="${score.addScore}" pattern="#,###" /></span>
				</c:if>
			</td>
			<td align="right">
				<c:if test="${score.minusScore != 0}">
					<span style="color:#FF7200; font-weight:bold;"><fmt:formatNumber value="${score.minusScore}" pattern="#,###" /></span>
				</c:if>
			</td>
			<td align="right"><fmt:formatNumber value="${score.currentScore}" pattern="#,###" /></td>
			<td align="center">
				<a href="<%=request.getContextPath()%>/pos/order!saledOrderList.do?orderId=${score.orderId}">查看</a>
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
		<a href="javascript:;" onclick="memberScore.goPage(1,${page.pageCount});">首页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="memberScore.goPage(${page.pageNum-1},${page.pageCount});">上一页</a>
		</c:if>
		&nbsp;&nbsp;&nbsp;
		<c:if var="isLastPage" test="${page.pageNum>=page.totalPages}">
		下一页&nbsp;&nbsp;&nbsp;尾页&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${!isLastPage}">
		<a href="javascript:;" onclick="memberScore.goPage(${page.pageNum+1},${page.pageCount});">下一页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="memberScore.goPage(${page.totalPages},${page.pageCount});">尾页</a>
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