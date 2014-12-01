<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<% System.out.println("reloadParentFrame.jsp invoked!"); %>
<html>
<head></head>
<body onLoad="window.parent.location.reload();"></body>
</html>

</dsp:page>

<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/reloadParentFrame.jsp#2 $$Change: 651448 $--%>
