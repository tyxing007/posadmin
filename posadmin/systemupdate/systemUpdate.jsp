<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>系统更新</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/admin.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/skin/css/theme.css" />

<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript">
var systemUpdate={};

//检查更新
systemUpdate.checkUpdate = function(dom){
	$.get($('#initPath').val()+'/pos/systemUpdate!getLatestShopVersion.do', function(json){
		if(json == 'error'){
			alert('检查更新时出现异常！');
		}else if(json == ''){
			alert('无新版本！');
		}else{
			var shopVersion = eval('('+json+')');
			var oldVersionNumber = $('#oldVersionNumber').val();
			if(oldVersionNumber!='' && oldVersionNumber==shopVersion.versionNumber){
				alert('当前版本为最新版本！');
			}else{
				var suffix = shopVersion.filePath.substring(shopVersion.filePath.lastIndexOf('.'));
				$('#versionNumber').html(shopVersion.versionNumber);
				$('#versionName').html(shopVersion.versionName);
				$('#versionDesc').html(shopVersion.versionDesc);
				$('#suffix').val(suffix);
				$('#newVersionTable').show();
			}
		}
	});
}

//下载更新
systemUpdate.downloadUpdate = function(){
	var data = {'versionNumber':$('#versionNumber').text(),'versionName':$('#versionName').text(),'versionDesc':$('#versionDesc').text(),'suffix':$('#suffix').val()};
	$.post($('#initPath').val()+'/pos/systemUpdate!downloadShopVersion.do', data, function(json){
		var result = eval('('+json+')');
		if(result.success){
			alert('更新成功！更新包存放路径：店面服务器'+result.filePath);
		}else{
			alert('更新失败！');
		}
	});
}
</script>
</head>
<body>
<input type="hidden" id="initPath" value="<%=request.getContextPath()%>" />
<input type="hidden" id="oldVersionNumber" value="${versionNumber}" />
<div class="box-positon">
	<div class="rpos">当前位置: 系统更新</div>
	<div class="ropt"></div>
	<div class="clear"></div>
</div>

<c:if test="${versionNumber == null}">
<table cellspacing="1" cellpadding="2" border="0" width="500" class="pn-ftable" style="font-size:13px; margin-left: 5px;">
<thead class="pn-lthead">
	<tr>
		<th>本地无版本信息</th>
	</tr>
</thead>
</table>
</c:if>
<c:if test="${versionNumber != null}">
<table cellspacing="1" cellpadding="2" border="0" width="500" class="pn-ftable" style="font-size:13px; margin-left: 5px;">
<thead class="pn-lthead">
	<tr>
		<th colspan="2">当前版本</th>
	</tr>
</thead>
<tbody>
	<tr>
		<td width="30%" class="pn-flabel pn-flabel-h">版本号：</td>
		<td width="70%" class="pn-fcontent"><span>${versionNumber}</span></td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h">版本名称：</td>
		<td class="pn-fcontent"><span>${versionName}</span></td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h">版本描述：</td>
		<td class="pn-fcontent">${versionDesc }</td>
	</tr>
</tbody>
</table>
</c:if>

<div style="margin-left: 215px; margin-top: 10px; margin-bottom: 10px;">
<input type="button" value="检查更新" onclick="systemUpdate.checkUpdate(this);" class="return-button" />
</div>

<table id="newVersionTable" cellspacing="1" cellpadding="2" border="0" width="500" class="pn-ftable" style="font-size:13px; margin-left: 5px; display: none;">
<thead class="pn-lthead">
	<tr>
		<th colspan="2">最新版本</th>
	</tr>
</thead>
<tbody>
	<tr>
		<td width="30%" class="pn-flabel pn-flabel-h">版本号：</td>
		<td width="70%" class="pn-fcontent"><span id="versionNumber"></span></td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h">版本名称：</td>
		<td class="pn-fcontent"><span id="versionName"></span></td>
	</tr>
	<tr>
		<td class="pn-flabel pn-flabel-h">版本描述：</td>
		<td class="pn-fcontent"><span id="versionDesc"></span></td>
	</tr>
	<tr>
		<td align="center" colspan="2" class="pn-fcontent">
			<input type="hidden" id="suffix" />
			<input type="button" value="下载更新" onclick="systemUpdate.downloadUpdate();" class="sendbox" />
		</td>
	</tr>
</tbody>
</table>
</body>
</html>