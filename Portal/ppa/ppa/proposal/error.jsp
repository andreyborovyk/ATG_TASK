<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" isErrorPage="true" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<%--
    Generic Error Page.
--%>
<paf:setFrameworkLocale />
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
    if("text/vnd.wap.wml".equals(device.getMimeType())) {
      contentType = "text/vnd.wap.wml";
    }
  }
  if("text/vnd.wap.wml".equals(contentType)) {
%>
<?xml version="1.0"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM/DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">
<wml>
  <card id="error" title="Error">
    <p>statusCode: <%= statusCode %><br/>
message: <%= message %><br/>
requestURI: <%= requestURI %><br/>
servletName: <%= servletName %><br/>
exceptionType: <%= exceptionType %><br/>
exception: <%= exception %><br/>
    </p>
  </card>
</wml>
<% } else { %>
<html>
  <head>
    <title>Error</title>
    <link rel="stylesheet" type="text/css" href="<%= cssURL %>" src="<%= cssURL %>"/>
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
   try {
     exception.printStackTrace(new PrintWriter(response.getOutputStream()));
   } catch(Exception e) {
   } finally {
     try {
       exception.printStackTrace(response.getWriter());
     } catch(Exception e) {
     } 
   }
 }
%>

    </pre>
  </body>
</html>


<% } %>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/ppa/proposal/error.jsp#2 $$Change: 651448 $--%>
