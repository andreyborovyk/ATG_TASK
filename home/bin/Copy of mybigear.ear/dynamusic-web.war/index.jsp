<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/Configuration"/>

<head><title>dynamusic-web JSP Index Page</title></head>
<body>
<h1>dynamusic-web Test JSP Page</h1>
Welcome to the dynamusic-web!  
<p>
If you are seeing this page,
it means you have correctly created a new ATG module.  Here is a link back to 
<dsp:a href="index.jsp">this page</dsp:a>.
<p>
<%
  DynamoHttpServletRequest drequest = ServletUtil.getDynamoRequest(request);

  Object o = drequest.resolveName("/atg/userprofiling/Profile");
  if (o == null) out.print("DPS is not available!");
  else out.println("Your profile is: " + o);
%>

<ul>
<li>Dynamo's document root is:
<dsp:valueof bean="Configuration.documentRoot">not set</dsp:valueof>
<li>
Your request's context path is:
<dsp:valueof bean="/OriginatingRequest.contextPath">null</dsp:valueof>
<li>
The full request URI of this page:
<dsp:valueof bean="/OriginatingRequest.requestURI">null</dsp:valueof>
<% 
Integer count;
synchronized (session) {
  count = (Integer) session.getAttribute ("visitCounter");
  if (count == null) count = new Integer (0);
  else count = new Integer(count.intValue() + 1);
  session.setAttribute ("visitCounter", count);
}
%>
<!-- Test setting an object parameter -->
<dsp:setvalue param="testParam" value="3"/>
<li>
You have visited this page: <%= count.intValue() %> times in this session.
<li>
Your session id is: <%= session.getId() %>
</ul>
<p>
This response is being served with:
<ul>
<li>
Encoding: <%= response.getCharacterEncoding() %>
<li>
Locale: <%= response.getLocale() %>
<li>
Buffer size: <%= response.getBufferSize() %>
</ul>

</body>
</dsp:page>
<%-- @version $Id: //product/Eclipse/main/plugins/atg.project/templates/index.jsp#1 $$Change: 425088 $--%>
