<%--
  This tag retrieves page parameter value and converts it to unicode charset.
  --%>
<%@ tag language="java" body-content="empty" %>
<%@ include file="/templates/top.tagf" %>
<%@ attribute name="paramName" required="true" rtexprvalue="false" %>

<% String paramName = (String) getJspContext().getAttribute("paramName"); 
   String param = request.getParameter(paramName); 
   String unicodeParam = new String(param.getBytes( "ISO-8859-1" ), "utf-8"); %>
   
   <%=unicodeParam%>