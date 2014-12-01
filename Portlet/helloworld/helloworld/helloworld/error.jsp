<%@ page isErrorPage="true" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<%
  String uri ="html/error.jsp";
%>
<jsp:include page="<%= uri %>"/>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/portlet/helloworld/helloworld/error.jsp#2 $$Change: 651448 $--%>
