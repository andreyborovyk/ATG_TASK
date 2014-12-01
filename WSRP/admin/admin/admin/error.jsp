<%@ page import="java.io.*,java.util.*" isErrorPage="true" %>

<%
  if(exception == null)
    exception = (Exception)request.getAttribute("javax.servlet.error.exception");
  Integer statusCode  = (Integer)request.getAttribute("javax.servlet.error.status_code");
  Class exceptionType = (Class)request.getAttribute("javax.servlet.error.exception_type");
  String message      = (String)request.getAttribute("javax.servlet.error.message");
  String requestURI   = (String)request.getAttribute("javax.servlet.error.request_uri");
  String servletName  = (String)request.getAttribute("javax.servlet.error.servlet_name");

  String contentType = "text/html";

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
<%-- @version $Id: //product/WSRP/version/10.0.3/admin/admin/admin/error.jsp#2 $$Change: 651448 $--%>
