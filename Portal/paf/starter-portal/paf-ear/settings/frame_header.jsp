<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%
  //Community
  Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
  String communityId = null;
  if(cm != null) {
    communityId = cm.getId();
  }

  //Page
  Page pg = (Page)request.getAttribute(Attribute.PAGE);
  String pageId = null;
  if(pg != null) {
    pageId = pg.getId();
  }

  //Request/Response
  PortalServletResponse portalServletResponse = 
     (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
  PortalContextImpl portalContext = new PortalContextImpl(portalServletRequest);
%>

<paf:setFrameworkLocale />

<dsp:page>
<admin:InitializeAdminEnvironment id="adminEnv">

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<paf:hasCommunityRole roles="leader,settings-manager,user-manager,page-manager,gear-manager" barrier="true"/>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<html>
<head>
<title></title>

<%  String thisNavHighlight = "";
    if ( request.getParameter("navHighLight") != null ) {
      thisNavHighlight = request.getParameter("navHighLight") ; 
    }
    String mode          = "1";       
    if ( request.getParameter("mode") != null ) {
      thisNavHighlight = request.getParameter("mode") ;
    }

%>

<dsp:include page="nav_header_main.jsp" flush="false">
  <dsp:param  name="mode" value="<%=mode%>" /> 
  <dsp:param  name="thisNavHighlight" value="<%=thisNavHighlight%>" /> 
</dsp:include>

</body>
</html>

</admin:InitializeAdminEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/frame_header.jsp#2 $$Change: 651448 $--%>
