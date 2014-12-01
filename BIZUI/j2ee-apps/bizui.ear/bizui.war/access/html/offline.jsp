<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>

<dsp:page>

<fmt:bundle basename="atg.portal.access">
<%
  Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
  Style style = null;
  String cssURL = "";
  if(cm != null) {
   style = cm.getStyle();
   if(style != null)
     cssURL = response.encodeURL(style.getCSSURL());
  }
  
  Page pg = (Page)request.getAttribute(Attribute.PAGE);
  ColorPalette colorPalette = null;
  String bodyTagData = "";
  if(pg != null) {
    colorPalette = pg.getColorPalette();
    if(colorPalette != null)
      bodyTagData = colorPalette.getBodyTagData();
  }
%>
<html>
  <head>
    <title><fmt:message key="offline-title"/></title>
    <link rel="stylesheet" type="text/css" href="<%= cssURL %>" src="<%= cssURL %>"/>
  </head>
  <body <%= bodyTagData %> >
    <h1><fmt:message key="offline-heading"/></h1>
    <p><fmt:message key="offline-message"/></p>
  </body>
</html>
</fmt:bundle>

</dsp:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/access/html/offline.jsp#2 $$Change: 651448 $--%>
