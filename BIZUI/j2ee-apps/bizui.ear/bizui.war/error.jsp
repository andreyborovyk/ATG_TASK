<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*,atg.core.util.StringUtils,atg.core.exception.StackTraceUtils,atg.servlet.ServletUtil" isErrorPage="true" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<dsp:page>


<%--
    Generic Error Page.
--%>

<fmt:bundle basename="atg.bizui.error">

<%
  if(exception == null)
    exception = (Exception)request.getAttribute("javax.servlet.error.exception");
  Integer statusCode  = (Integer)request.getAttribute("javax.servlet.error.status_code");
  Class exceptionType = (Class)request.getAttribute("javax.servlet.error.exception_type");
  String message      = (String)request.getAttribute("javax.servlet.error.message");
  String requestURI   = (String)request.getAttribute("javax.servlet.error.request_uri");
  String servletName  = (String)request.getAttribute("javax.servlet.error.servlet_name");

  String stack = StackTraceUtils.getStackTrace(exception, 50, 10);
  stack = StringUtils.replace(stack, '\n', "<br/>");
  stack = StringUtils.replace(stack, '\t', "&nbsp;&nbsp;&nbsp;");
                  
  ServletUtil.getDynamoRequest(request).logError("BIZUI page error: statusCode: " +
     statusCode + " message: " + message + " requestURI: " + requestURI + " servletName: " + 
     servletName + " exceptionType: " + exceptionType);
  if(exception != null) {
    //out.print(StackTraceUtils.getStackTrace(exception, -1, -1));
    ServletUtil.getDynamoRequest(request).logError(exception);
  }

  /*Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
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
  }*/

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

<c:set var="PORTALSERVLETREQUEST"><%= Attribute.PORTALSERVLETREQUEST %></c:set>
<c:set var="portalServletRequest"     value="${requestScope[PORTALSERVLETREQUEST]}"/>
<c:set var="uri" value="${portalServletRequest.portalContextPath}/bcc"/>
<c:set var="actionURL" value="${uri}"/>

<fmt:message var="bccbutton" scope="request" key="error-button"/>
<% String bccbutton = (String) request.getAttribute("bccbutton"); %>
<fmt:message var="showbutton" scope="request" key="error-stack-show-button" />
<% String showbutton = (String) request.getAttribute("showbutton"); %>
<fmt:message var="hidebutton" scope="request" key="error-stack-hide-button" />
<% String hidebutton = (String) request.getAttribute("hidebutton"); %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>

    <title><fmt:message key="error-title"/></title>
    <link rel="icon" href="<c:url value='/favicon.ico'/>" type="image/x-icon" />
    <link rel="SHORTCUT ICON" href="<c:url value='/favicon.ico'/>" type="image/x-icon" />
    <link rel="stylesheet" href="<c:url context='/atg' value='/templates/style/css/error.css'/>" type="text/css" />

  </head>
  
<script type="text/javascript">
  function flipStackTrace() {
    var stackTraceObject = document.getElementById("stacktrace");
    var stackButtonObject = document.getElementById("stackbutton");

    if (stackTraceObject != null){
      if (stackTraceObject.style.display == "block"){
        stackButtonObject.value = "<%= showbutton %>";
        stackTraceObject.style.display = "none";
      }
      else {
        stackButtonObject.value = "<%= hidebutton %>";
        stackTraceObject.style.display = "block";
      }
    }
    return false;
  }
</script>


  <body id="errorBody">
    <div id="centered-error">
       <div id="centered-error2">
          
          <dsp:form action="${actionURL}" method="post">
          
          <div id="errorMiddle"  style="line-height: 20px !important;">
            <span style="color: #EB910F !important"><fmt:message key="error-heading"/></span><p/>
            <span style="color: #EB910F !important"><fmt:message key="error-message"/></span>
          </div>

          <div id="errorBottom">
            <input iclass="buttonError" type="submit" value="<%= bccbutton %>" style="font-size: 15px !important;"/>&nbsp;
            <input id="stackbutton" iclass="buttonError" type="button" value="<%= showbutton %>" onClick="flipStackTrace()" style="font-size: 15px !important;"/>
          </div>

          </dsp:form><p/>
          <div id="stacktrace" align="left" style="display: none;">
            <fmt:message key="error-request-URI-label"/>: <%= requestURI %><br/>
            <fmt:message key="error-message-label"/>: <%= message %><p/>
            <%= stack %>
          </div>
          
       </div>
    </div>
  </body>
</html>
</fmt:bundle>

</dsp:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/error.jsp#2 $$Change: 651448 $--%>
