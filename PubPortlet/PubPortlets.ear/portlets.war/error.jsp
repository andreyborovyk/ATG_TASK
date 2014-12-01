<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*,atg.core.exception.StackTraceUtils,atg.servlet.ServletUtil" isErrorPage="true" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<dsp:page>


<%--
    Generic Error Page.
--%>

<%
  if(exception == null)
    exception = (Exception)request.getAttribute("javax.servlet.error.exception");
  Integer statusCode  = (Integer)request.getAttribute("javax.servlet.error.status_code");
  Class exceptionType = (Class)request.getAttribute("javax.servlet.error.exception_type");
  String message      = (String)request.getAttribute("javax.servlet.error.message");
  String requestURI   = (String)request.getAttribute("javax.servlet.error.request_uri");
  String servletName  = (String)request.getAttribute("javax.servlet.error.servlet_name");

  Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
  Style style = null;
  String cssURL = "";
  if(cm != null) {
   style = cm.getStyle();
   if(style != null)
     cssURL = style.getCSSURL();
  }
  
  Page pg = (Page)request.getAttribute(Attribute.PAGE);
  ColorPalette colorPalette = null;
  String bodyTagData = "";
  if(pg != null) {
    colorPalette = pg.getColorPalette();
    if(colorPalette != null)
      bodyTagData = colorPalette.getBodyTagData();
  }

  String contentType = "text/html";
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
  if(portalServletRequest != null) {
    Device device = (Device)portalServletRequest.getAttribute(Attribute.DEVICE);
    if (device != null) {
      if("text/vnd.wap.wml".equals(device.getMimeType())) {
        contentType = "text/vnd.wap.wml";
      }
    }
  }
%>
<html>
  <head>
    <title>Error</title>
    <link rel="stylesheet" type="text/css" href="<%= cssURL %>" src="<%= cssURL %>"/>
    <style type="text/css" media="all">
    BODY { 
	  background-image: none;	
      font: 75% Tahoma, Geneva, "Bitstream Vera Sans", Verdana, Arial, Helvetica, sans-serif;
      margin: 3px;
      padding: 3px;
      line-height:  13px;
      text-align: none;
      background-color: #F0F0F0;
    }
    </style>
  </head>
  <body <%= bodyTagData %> >
    <h1>Error</h1>
    <pre>
      statusCode: <%= statusCode %>
      message: <%= message %>
      requestURI: <%= requestURI %>
      servletName: <%= servletName %>

      exceptionType: <%= exceptionType %>
      exception: <%= exception %>

      stackTrace:
<%
 if(exception != null) {
   out.print(StackTraceUtils.getStackTrace(exception, -1, -1));
   ServletUtil.getDynamoRequest(request).logError(exception);
 }
%>

    </pre>
  </body>
</html>

</dsp:page>

<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/error.jsp#2 $$Change: 651448 $--%>
