<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*" errorPage="/error.jsp" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<%
 //Obtain request/response
 GearServletResponse gearServletResponse = 
     (GearServletResponse)request.getAttribute(Attribute.GEARSERVLETRESPONSE);
 GearServletRequest gearServletRequest = 
     (GearServletRequest)request.getAttribute(Attribute.GEARSERVLETREQUEST);
%>
<p><center>Servlet Dump Portlet</center></p>
<p><center>Copyright (C) 1997-2010 Art Technology Group, Inc.</center></p>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/portlets/servlet-dump/about/html/about.jsp#2 $$Change: 651448 $--%>
