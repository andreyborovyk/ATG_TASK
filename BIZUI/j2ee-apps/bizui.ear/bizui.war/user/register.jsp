<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*"%>
<%
  String uri = "html/register.jsp";
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
  if(portalServletRequest != null) {
    Device device = (Device)portalServletRequest.getAttribute(Attribute.DEVICE);
    if((device != null) && ("text/vnd.wap.wml".equals(device.getMimeType()))) {
      uri = "wml/register.jsp";
    }
  }
%>
<jsp:include page="<%= uri %>" flush="true"/>

</dsp:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/user/register.jsp#2 $$Change: 651448 $--%>
