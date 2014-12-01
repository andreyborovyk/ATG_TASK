<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*"%>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<%
  String uri = "html/content.jsp";
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
  if(portalServletRequest != null) {
    Device device = (Device)portalServletRequest.getAttribute(Attribute.DEVICE);
    if((device != null) && ("text/vnd.wap.wml".equals(device.getMimeType()))) {
      uri = "wml/content.jsp";
    }
  }
%>
<jsp:include page="<%= uri %>" flush="true"/>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/ppa/proposal/content/content.jsp#2 $$Change: 651448 $--%>
