<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.core.exception.StackTraceUtils,atg.servlet.ServletUtil" isErrorPage="true" %>
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

%>
<html>
  <head>
    <title>Error</title>

     <style type="text/css" media="all">
     <!--
       @import url("<c:url context='/atg' value='/templates/style/css/style.jsp'/>");
     -->
     </style>
  </head>
  <body >
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

<%-- @version $Id: //product/PubPortlet/version/10.0.3/AssetBrowse.war/error.jsp#2 $$Change: 651448 $--%>
