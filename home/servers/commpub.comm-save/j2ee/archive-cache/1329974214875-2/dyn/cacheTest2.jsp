<%@ taglib uri="/dspTaglib" prefix="dsp" %>

<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Cache"/>

<head><title>Dynamo Cache Droplet Test JSP Page2</title></head>
<body>
<h1>Dynamo Cache Droplet Test JSP Page2</h1>

<h2>Same test as <a href="cacheTest.jsp">cacheTest.jsp</a>, using the
same cache keys but on a different page.</h2>

<h2>Here is a cached piece of content</h2>

<dsp:droplet name="Cache">
  <dsp:param name="key" value="cachevalue1"/>
  <dsp:oparam name="output">

    <% System.out.println ("---in test2 cachevalue1 output"); %>
    Test 2 - This content was generated at <%=System.currentTimeMillis ()%>.

  </dsp:oparam>
</dsp:droplet>

<h2>Here is a cached piece of content that expires every 5 seconds</h2>

<dsp:droplet name="Cache">
  <dsp:param name="key" value="cachevalue2"/>
  <dsp:param name="cacheCheckSeconds" value="5"/>
  <dsp:oparam name="output">

    <% System.out.println ("---in test2 cachevalue2 output"); %>
    Test 2 - This content was generated at <%=System.currentTimeMillis ()%>.

  </dsp:oparam>
</dsp:droplet>

<h2>Here is a piece of content that says it has no URL's</h2>

<dsp:droplet name="Cache">
  <dsp:param name="key" value="cachevalue3"/>
  <dsp:param name="hasNoURLs" value="true"/>
  <dsp:oparam name="output">

    <% System.out.println ("---in test2 cachevalue3 output"); %>
    Test 2 - This content was generated at <%=System.currentTimeMillis ()%>.

  </dsp:oparam>
</dsp:droplet>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/DAS/version/10.0.3/templates/DAF/j2ee-apps/dyn/cacheTest2.jsp#2 $$Change: 651448 $--%>
