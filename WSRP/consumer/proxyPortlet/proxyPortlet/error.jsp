<%@ page isErrorPage="true" %>

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
       java.io.PrintWriter writer = response.getWriter();
       writer.println("<br>");
       StackTraceElement[] stackTrace = exception.getStackTrace();
       for (int i = 0; i < stackTrace.length; i++) {
           if (i == 10) {
               writer.println("<br><br> ...stack trace cropped");
               break;
           }
           writer.print("<br>");
           writer.println(stackTrace[i]);
       }
       //exception.printStackTrace(new java.io.PrintWriter(response.getOutputStream()));
   }
   catch(Exception e) {
   }
 }
%>

    </pre>
<%-- @version $Id: //product/WSRP/version/10.0.3/consumer/proxyPortlet/proxyPortlet/error.jsp#2 $$Change: 651448 $--%>
