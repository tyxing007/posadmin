<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="/struts-tags"%> 

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>商品列表</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript">
var product={};

//删除商品
product.deleteProduct = function(productId){
	if(confirm('确定删除该商品吗？')){
		window.location.href = '<%=request.getContextPath()%>/pos/Product!deleteProduct.do?product.id='+productId;
	}
}

//点击同步商品
product.clickSyncProduct = function(obj){
	window.location.href = '<%=request.getContextPath()%>/pos/Product!syncProductDataFromPoscenter.do';
	$(obj).text('商品同步中...').removeAttr('href');
}
</script>
</head>
<body>
	<table width="800" cellpadding="3" cellspacing="1" align="center">
	</table>
	 <table width="800" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align="center">
      <tr bgcolor="#4688D6">
      <!-- <td width="100" align="center"><font color="#FFFFFF">id</font></td> -->
      <td width="100" align="center"><font color="#FFFFFF">商品编码</font></td>
      <td align="center" width="200"><font color="#FFFFFF">商品名称</font></td>
      <td align="center" width="100"><font color="#FFFFFF">销售数量</font></td>
      <td align="center" width="100"><font color="#FFFFFF">销售总额</font></td>
      <td align="center" width="100"><font color="#FFFFFF">销售时间</font></td>
      <td align="center" width="100"><font color="#FFFFFF">剩余库存</font></td>
    </tr>
    <c:iterator value="sclist" var="li">
         <tr bgcolor='#F8F8F8' >
       <!--  <td><c:property value="#li.id"/></td> --> 
          <td  align="center"><c:property value="#li.barCode"/></td>
          <td><c:property value="#li.productName"/></td>
          <td><c:property value="#li.count"/></td>
          <td><c:property value="#li.totalSales"/></td>
          <td><c:property value=""/></td>
          <td><c:property value="#li.stock"/></td>
       </tr>
   </c:iterator>
	</table>

	<table width="800" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align="center">
	<tr>
		<td><a href="orderCount!getOrderCountList.do?page.pageNum=1&page.pageCount=${page.pageCount}" >首页</a></td>
		<td><a href="orderCount!getOrderCountList.do?page.pageNum=${page.pageNum-1}&page.pageCount=${page.pageCount}">上一页</a></td>
		<td><a href="orderCount!getOrderCountList.do?page.pageNum=${page.pageNum+1}&page.pageCount=${page.pageCount}">下一页</a></td>
		<td><a href="orderCount!getOrderCountList.do?page.pageNum=${page.totalPages}&page.pageCount=${page.pageCount}">尾页</a></td>
		<td width="200">第${page.pageNum}/${page.totalPages}页,共${page.totalRecords}条记录</td>
	</tr>
	</table>

</body>
</html>
