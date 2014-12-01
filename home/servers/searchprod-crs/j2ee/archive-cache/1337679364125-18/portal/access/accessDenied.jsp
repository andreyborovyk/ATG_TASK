<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*"%>
<%
  String uri = "html/accessDenied.jsp";
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
  if(portalServletRequest != null) {
    Device device = (Device)portalServletRequest.getAttribute(Attribute.DEVICE);
    if((device != null) && ("text/vnd.wap.wml".equals(device.getMimeType()))) {
      uri = "wml/accessDenied.jsp";
    }
  }
%>
<jsp:include page="<%= uri %>"/>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/portal.war/access/accessDenied.jsp#2 $$Change: 651448 $--%>
