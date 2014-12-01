<%@ page language="java" %><?xml version="1.0" encoding="UTF-8"?>
<categories.response status="0">
<%String theCat = request.getParameter("CATEGORIES");
//Assumes we have 0-9 categories, otherwise the simple substring search will not work, i.e. '10' will
//be picked up with 'theCat.indexOf("1")'
if (theCat != null && theCat.indexOf("1")>=0){%>
	<category>
		<category.name>ROIMax News</category.name>
		<category.info id="1" bookmark="1"/>
	</category>
<%}
if (theCat != null && theCat.indexOf("2")>=0){%>
	<category>
		<category.name>Business News</category.name>
		<category.info id="2" bookmark="1"/>
	</category>
<%}
if (theCat != null && theCat.indexOf("3")>=0){%>
	<category>
		<category.name>Financial News</category.name>
		<category.info id="3" bookmark="1"/>
	</category>
<%}
if (theCat == null){%>
	<category>
		<category.name>ROIMax News</category.name>
		<category.info id="1" bookmark="1"/>
	</category>
	<category>
		<category.name>Business News</category.name>
		<category.info id="2" bookmark="1"/>
	</category>	<category>
		<category.name>Financial News</category.name>
		<category.info id="3" bookmark="1"/>
	</category>
<%}%>
</categories.response>
<%-- @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocol.war/data/generic.categories.jsp#2 $$Change: 651448 $--%>
