<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*" errorPage="/error.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib uri="dsp" prefix="dsp" %>

<%
 //Obtain request/response
 GearServletResponse gearServletResponse = 
     (GearServletResponse)request.getAttribute(Attribute.GEARSERVLETRESPONSE);
 GearServletRequest gearServletRequest = 
     (GearServletRequest)request.getAttribute(Attribute.GEARSERVLETREQUEST);
%>
<fmt:bundle basename="atg.portlet.workflow.Resources">
<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<div class="gearBody">

<dsp:droplet name="Switch">
  <dsp:param name="value" param="pwf_viewName"/>
  <dsp:oparam name="subject">
    <dsp:include src="subject.jsp"/>
  </dsp:oparam>
  <dsp:oparam name="task">
    <dsp:include src="task.jsp"/>
  </dsp:oparam>
  <dsp:oparam name="default">
    <dsp:include src="default.jsp"/>
  </dsp:oparam>
</dsp:droplet>

</div>

</dsp:page>
</fmt:bundle>
<%-- @version $Id: //app/portal/version/10.0.3/ppa/proposal/content/html/content.jsp#2 $$Change: 651448 $--%>
