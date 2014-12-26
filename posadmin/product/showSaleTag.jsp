<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<script type="text/javascript">
var saleTag={};

//导出商品销售标签图片
saleTag.exportSaleTagImg = function(saleTegImgPath){
	window.location.href = $('#initPath').val()+'/pos/Product!exportSaleTagImg.do?saleTegImgPath='+saleTegImgPath;
}
</script>

<table cellspacing="1" cellpadding="2" border="0" width="100%" class="pn-ftable" style="font-size:13px;">
<thead class="pn-lthead">
	<tr>
		<th>销售标签图片</th>
	</tr>
</thead>
<tbody>
	<tr>
		<td align="center" class="pn-fcontent" style="padding:5px;">
			<img alt="销售标签" src="<%=request.getContextPath()%>/${saleTegImgPath}" />
		</td>
	</tr>
</tbody>
</table>
<div style="text-align:center; margin-top:20px;">
	<input type="button" value="下载" onclick="saleTag.exportSaleTagImg('${saleTegImgPath}');" class="sendbox" />&nbsp;&nbsp;
	<input type="button" class="return-button" value="关闭" onclick="$('a.jMsgbox-closeWrap').click();" />
</div>