<%@ page isErrorPage="true" %>

<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

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
  </head>
  <body>
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
     exception.printStackTrace(new java.io.PrintWriter(response.getOutputStream()));
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

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/portlet/slot/slot/html/error.jsp#2 $$Change: 651448 $--%>
