<%@ taglib uri="/dspTaglib" prefix="dsp" %>

<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Cache"/>

<head><title>Dynamo Cache Droplet Test JSP Page</title></head>
<body>
<h1>Dynamo Cache Droplet Test JSP Page</h1>


<h2>Here is a cached piece of content</h2>

<dsp:droplet name="Cache">
  <dsp:param name="key" value="cachevalue1"/>
  <dsp:oparam name="output">

    <% System.out.println ("---in cachevalue1 output"); %>
    This content was generated at <%=System.currentTimeMillis ()%>.

  </dsp:oparam>
</dsp:droplet>

<h2>Here is a cached piece of content that expires every 5 seconds</h2>

<dsp:droplet name="Cache">
  <dsp:param name="key" value="cachevalue2"/>
  <dsp:param name="cacheCheckSeconds" value="5"/>
  <dsp:oparam name="output">

    <% System.out.println ("---in cachevalue2 output"); %>
    This content was generated at <%=System.currentTimeMillis ()%>.

  </dsp:oparam>
</dsp:droplet>

<h2>Here is a piece of content that says it has no URL's</h2>

<dsp:droplet name="Cache">
  <dsp:param name="key" value="cachevalue3"/>
  <dsp:param name="hasNoURLs" value="true"/>
  <dsp:oparam name="output">

    <% System.out.println ("---in cachevalue3 output"); %>
    This content was generated at <%=System.currentTimeMillis ()%>.

  </dsp:oparam>
</dsp:droplet>

<p><a href="cacheTest2.jsp">Go to cacheTest2.jsp</a>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/DAS/version/10.0.3/templates/DAF/j2ee-apps/dyn/cacheTest.jsp#2 $$Change: 651448 $--%>
