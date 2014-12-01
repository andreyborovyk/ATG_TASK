<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*,java.util.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>

<dsp:page>
<%
  PortalServletResponse portalServletResponse = 
     (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
%>
<c:set var="PORTALSERVLETREQUEST"><%= Attribute.PORTALSERVLETREQUEST %></c:set>
<c:set var="PORTALSERVLETRESPONSE"><%= Attribute.PORTALSERVLETRESPONSE %></c:set>
<c:set var="portalServletRequest"     value="${requestScope[PORTALSERVLETREQUEST]}"/>
<c:set var="portalServletResponse"    value="${requestScope[PORTALSERVLETRESPONSE]}"/>
<c:set var="page"                     value="${portalServletRequest.page}"/>
<c:set var="regionMap"                value="${page.regions}"/>

<div id="mainContent">

  <c:set var="gears" value="${regionMap['100_Middle'].gears}"/>
  <%@ include file="BizUIRegion.jspf" %>

</div>

</dsp:page>
<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/templates/layout/html/100.jsp#2 $$Change: 651448 $--%>
