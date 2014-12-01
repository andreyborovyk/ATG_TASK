<%@ page isErrorPage="true" %>

<html>

<%@ taglib uri="/coreTaglib" prefix="core" %>
<jsp:useBean id="dmsContext" scope="request" class="atg.sqljmsadmin.taglib.DMSAdminContext"/>

<head>
<title>Error</title>
</head>

<body text=00214A bgcolor=ffffff link=E87F02 vlink=637DA6>

<core:RollbackTransaction id="rollbackXA">
</core:RollbackTransaction>

<a href="main.jsp">main</a>
<p>

error, rolled back transaction.

<p>

<core:ifNotNull value='<%= request.getAttribute("dms_exception") %>'>
  <jsp:setProperty name="dmsContext" property="exception"
       value='<%= request.getAttribute("dms_exception") %>'/>
</core:ifNotNull>

<code>
<pre>
<core:exclusiveIf>
  <core:ifNotNull value="<%= dmsContext.getException() %>">
<% dmsContext.getException().printStackTrace(new java.io.PrintWriter(out)); %>
  </core:ifNotNull>
  <core:defaultCase>
<% exception.printStackTrace(new java.io.PrintWriter(out)); %>
  </core:defaultCase>
</core:exclusiveIf>
</pre>
</code>

</body>
<%-- @version $Id: //product/DAS/version/10.0.3/release/SQLJMSAdmin/j2ee-apps/sqlJmsAdmin/webModule/error.jsp#2 $$Change: 651448 $--%>
