<%@ page contentType="text/xml;charset=utf-8" %><%@ page import="mmboa.base.*" %><%@ page import="org.jdom.*" %><%@ page import="org.jdom.output.*" %><%@ page import="java.util.*" %><%
	
	UserAction action = new UserAction(request);
	int id = action.getParameterInt("id");
	List<Department> list = UserAction.service.getDepartmentList("1",0,1000,"id");

	HashMap elementMap = new HashMap(list.size());	// 用id来快速找到element
// 创建xml文件的根目录tree
Element rootElement = new Element("tree");
// 创建树的属性
Document myDocument = new Document(rootElement);
{
	Element result = new Element("tree");
}
for (int i = 0; i < list.size(); i += 1) {
	Department vo = list.get(i);
	Element result = new Element("tree");
	// 如果不是 但是目录
	if (vo != null) {
		result.setAttribute("action", "../base/department.jsp?id=" + vo.getId());
		result.setAttribute("text", vo.getName());
		result.setAttribute("target", "mainFrame");
		if(vo.getParentId()==0) {
			rootElement.addContent(result);
			elementMap.put(new Integer(vo.getId()),result);
		} else {
			Element e = (Element)elementMap.get(new Integer(vo.getParentId()));
			if(e!=null) {
				e.addContent(result);
				elementMap.put(new Integer(vo.getId()),result);
			}
		}
	}
}

// Output the xml将此页面转化为xml
XMLOutputter outputter = new XMLOutputter();

org.jdom.output.Format format = org.jdom.output.Format.getCompactFormat();
outputter.setFormat(format);

outputter.output(myDocument, out);
%>
