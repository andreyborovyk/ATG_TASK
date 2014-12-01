<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*" errorPage="/error.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<%
 //Obtain request/response
 GearServletResponse gearServletResponse = 
     (GearServletResponse)request.getAttribute(Attribute.GEARSERVLETRESPONSE);
 GearServletRequest gearServletRequest = 
     (GearServletRequest)request.getAttribute(Attribute.GEARSERVLETREQUEST);
%>
<fmt:bundle basename="atg.portlet.workflow.Resources">
<p><center><fmt:message key="about.name"/></center></p>
<p><center><fmt:message key="about.copyright"/></center></p>
</fmt:bundle>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/ppa/proposal/about/html/about.jsp#2 $$Change: 651448 $--%>
