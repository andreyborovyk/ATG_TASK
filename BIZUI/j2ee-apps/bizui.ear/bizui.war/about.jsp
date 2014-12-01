<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" prefix="dsp" %>
<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" prefix="dspel" %>




<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<dspel:page>

<dspel:importbean bean="/atg/dynamo/service/VersionService" var="version"/>

<html>
<head>
  <title>About ATG Business Control Center 10.0.3</title>
  <style type="text/css" media="all">
  @import url("<c:url value='/templates/style/css/style.jsp'/>");
  </style>
</head>
<%-- $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/about.jsp#3 $ --%>
<body class="popup">
  <div id="assetBrowserHeader"><h2>ATG Business Control Center 10.0.3</h2></div>

  <%@ include file="aboutcontents.jsp" %>

</body>
</html>
</dspel:page>
<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/about.jsp#3 $$Change: 651648 $--%>
