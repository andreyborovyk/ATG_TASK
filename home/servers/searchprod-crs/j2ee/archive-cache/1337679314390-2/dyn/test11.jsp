<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/Configuration"/>

<head><title>Dynamo Test JSP Page</title></head>
<body>
<h1>Dynamo Test JSP Page</h1>
Welcome to the Dynamo!  
<p>
If you are seeing this page,
it means you have correctly installed DAF.  Here is a link back to 
<dsp:a href="test11.jsp">this page</dsp:a>.
<p>
<%
  DynamoHttpServletRequest drequest = ServletUtil.getDynamoRequest(request);

  Object o = drequest.resolveName("/atg/userprofiling/Profile");
  if (o == null) out.print("DPS is not installed!");
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
<dsp:setvalue param="testParam" value='<%= "3" %>'/>
<li>
You have visited this page: <%= count.intValue() %> times in this session.
<li>
Your session id is: <%= session.getId() %>
</ul>
<p>
<dsp:getvalueof id="myvar" param="testParam">
<% out.print("Here's the value of the testParam parameter: " + myvar); %>
</dsp:getvalueof>
<p>
This response is being served with:
<ul>
<li>
Encoding: <%= response.getCharacterEncoding() %>
<li>
Locale: <%= response.getLocale() %>
<li>
Set response buffer size: 4096
<% 
	response.setBufferSize(4096); 
%>
<li>
Get response buffer size: <%= response.getBufferSize() %>
</ul>
<p>
Here is the complete state for this request:
<pre>
<%
  out.print(ServletUtil.escapeHtmlString(ServletUtil.getDynamoRequest(request).toString()));
%>
</pre>

<h2>Include Test A - No Oparam</h2>
<br>Should see this first
<dsp:include page="inc1.jsp"/>
<br>Should see this fifth
<br>End Include Test A

<h2>Include Test B - With Oparam</h2>
<br>See this zeroth
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" value='<%= "true" %>'/>
  <dsp:oparam name="true">
<br>Should see this first
     <dsp:include page="inc1d11.jsp"/>
<br>Should see this ninth
  </dsp:oparam>
</dsp:droplet>
<br>End Include Test B (tenth)

<h2>Include Test C - included file in childfolder that have referense with a relative path</h2>
<br>first see content of childdir/inc11.jsp
<dsp:include page="childdir/inc11.jsp" flush="true"></dsp:include>
<br>End Include Test C

</pre>
</body>
</dsp:page>
<%-- @version $Id: //product/DAS/version/10.0.3/templates/DAF/j2ee-apps/dyn/test11.jsp#2 $$Change: 651448 $--%>
