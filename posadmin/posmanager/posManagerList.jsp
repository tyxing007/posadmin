<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="s" uri="/struts-tags"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>POS机信息列表</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript">
var posManager={};

//新增
posManager.addPos = function(cashierId){
	window.location.href = $('#initPath').val()+'/pos/posManager!addPos.do';
}

//删除
posManager.deletePosManager = function(id){
	if(confirm('确定删除该POS机吗？')){
		window.location.href = $('#initPath').val()+'/pos/posManager!deletePosManager.do?posManager.id='+id;
	}
}

//跳转到指定页面
posManager.goPage = function(pageNum,pageCount){
	var url = $('#initPath').val()+'/pos/posManager!posManagerList.do?d=1';
	if(pageNum != null && pageCount != null){
		url += '&page.pageNum='+pageNum;
	}
	if(pageCount != null){
		url += '&page.pageCount='+pageCount;
	}
	window.location.href = url;
}
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<div class="box-positon">
	<div class="rpos">当前位置: POS机信息管理 - 列表</div>
	<div class="ropt">
		<input type="button" onclick="posManager.addPos();" class="submit" value="新增" /> &nbsp; 
	</div>
	<div class="clear"></div>
</div>

<div id="bodyBox" class="body-box">
<table id="goodsList" width="100%" cellspacing="1" cellpadding="0" border="0" style="" class="pn-ltable">
<thead class="pn-lthead">
      <tr>
      <th align="center">POS机编码</th>
      <th align="center">POS机IP</th>
      <th align="center">创建时间</th>
      <th align="center">操作</th>
    </tr>
</thead>
<tbody class="pn-ltbody">
    <s:iterator value="list" var="li">
         <tr>
          <td align="center"><s:property value="#li.posCode"/></td>
          <td align="center"><s:property value="#li.posIp"/></td>
          <td align="center"><s:date name="#li.createTime" format="yyyy-MM-dd HH:mm:ss"/></td>
          <td align="center">
          	<a href="<%=request.getContextPath()%>/pos/posManager!toPosManagerFormView.do?posManager.id=<s:property value="#li.id"/>">查看</a>
          	<a href="<%=request.getContextPath()%>/pos/posManager!addPos.do?posManager.id=<s:property value="#li.id"/>">修改</a>
          	<a href="javascript:void(0);" onclick="posManager.deletePosManager(<s:property value="#li.id"/>);">删除</a>
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
		<a href="javascript:;" onclick="posManager.goPage(1,${page.pageCount});">首页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="posManager.goPage(${page.pageNum-1},${page.pageCount});">上一页</a>
		</c:if>
		&nbsp;&nbsp;&nbsp;
		<c:if var="isLastPage" test="${page.pageNum>=page.totalPages}">
		下一页&nbsp;&nbsp;&nbsp;尾页&nbsp;&nbsp;&nbsp;
		</c:if>
		<c:if test="${!isLastPage}">
		<a href="javascript:;" onclick="posManager.goPage(${page.pageNum+1},${page.pageCount});">下一页</a>&nbsp;&nbsp;&nbsp;
		<a href="javascript:;" onclick="posManager.goPage(${page.totalPages},${page.pageCount});">尾页</a>
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
