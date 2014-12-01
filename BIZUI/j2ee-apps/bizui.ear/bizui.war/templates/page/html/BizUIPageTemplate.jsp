<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*,java.util.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="pafrt"   uri="http://www.atg.com/taglibs/portal/paf-rt1_3" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>
<%@ taglib prefix="web-ui"  uri="http://www.atg.com/taglibs/web-ui" %>
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
<c:set var="community"                value="${portalServletRequest.community}"/>
<c:set var="page"                     value="${portalServletRequest.page}"/>
<c:set var="communityName"            value="${community.name}"/>
<c:set var="pageName"                 value="${page.name}"/>

<c:set var="contextPath"><%= request.getContextPath() %></c:set>
<paf:encodeURL var="adminURL"     url="/portal/settings/community_settings.jsp"/>
<paf:encodeURL var="customizeURL" url="/portal/settings/user.jsp"/>
<paf:encodeURL var="loginURL"     url="${contextPath}/user/login.jsp"/>
<paf:encodeURL var="logoutURL"    url="${contextPath}/user/logout.jsp"/>

<dsp:importbean var="profile" bean="/atg/userprofiling/Profile"/>
<fmt:setBundle var="bundle"        basename="atg.bizui.bizui" />
<fmt:setBundle var="viewMapBundle" basename="atg.web.viewmapping.ViewMappingResources"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
  <%@ include file="includes/head.jspf" %>
  <script type="text/javascript">
    dojo.addOnLoad(fireOnLoad);
  </script>
</head>

<body class="atg">

  <script language="JavaScript" type="text/javascript">
    registerOnResize( function() { resizeAssetBrowser(); } );
  </script>

  <!-- branding banner -->
  <%@ include file="includes/branding_banner.jspf" %>
  <!-- end branding banner -->

  <!-- contentHolder -->
  <div id="newContentHolder">
    <paf:layout />
  
    <!-- footer -->
    <%@ include file="includes/footer.jspf" %>
    <!-- end of footer -->

  </div>
  <!-- end of contentHolder -->
  
  <!-- portlet iframe definitions -->
  <%@ include file="includes/iframe_defs.jspf" %>
  <!-- end of iframes defs -->

<p></p>
<p></p>
</body>
</html>

</dsp:page>
<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/templates/page/html/BizUIPageTemplate.jsp#2 $$Change: 651448 $--%>
