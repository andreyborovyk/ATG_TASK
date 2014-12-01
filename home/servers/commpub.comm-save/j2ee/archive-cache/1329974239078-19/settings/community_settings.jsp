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

<%
  boolean hasLocalAccess = false;
%>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>

<paf:hasCommunityRole roles="leader,settings-manager">

<% hasLocalAccess = true; %>
<core:demarcateTransaction id="demarcateXA">
  <% try { %>

<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

<title><i18n:message key="title-community_settings"/></title>

<%
    String clearGif =  response.encodeURL("images/clear.gif");
    String mode     = "1";              // used for sub page with area
                                         // this copies an index array to a rendering array 
                                         // that provides the side link
    if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    } else {
       mode = "1"; // default to community settings
    }

%>

<dsp:include page="nav_header_main.jsp" flush="false">
  <dsp:param  name="mode" value="<%=mode%>" /> 
  <dsp:param  name="thisNavHighlight" value="settings" /> 
</dsp:include>


<br /><center>
<table width="98%" border="0" cellpadding="0" cellspacing="0">
<tr>

<td width="10"><img src='<%=clearGif %>' height="1" width="10"  alt="" /></td>
<td width="150" valign="top">

 <dsp:include page="nav_sidebar.jsp" flush="false">
  <dsp:param  name="mode" value="<%=mode%>" /> 
  <dsp:param  name="sideBarIndex" value="0" /> 
 </dsp:include>

</td>
<td width="10"><img src='<%=clearGif %>' height="1" width="10" alt="" /></td>

<td valign="top" width="80%" align="left">

<%@ include file="fragments/form_messages.jspf"%>
<!-- -->
<core:Switch value="<%= mode%>">
 
  <core:Case value="2"><%-- about settings --%>

   <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%">
     <tr><td>
       <table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%">
         <tr><td><font class="pageheader">
            <i18n:message key="community-settings-about-header"/>
         </font></td></tr>
       </table>
     </td></tr>
   </table>

   <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%">
      <tr><td><font class="small"><br /><blockquote>
        <i18n:message key="helpertext-about-settingsA"/><br /><br />
        <i18n:message key="helpertext-about-settingsB"/><br /><br />
        <i18n:message key="helpertext-about-settingsC"/> 
	</font></blockquote></td></tr>
    </table>
  </core:Case>

  <core:Case value="3"><%-- access  --%>
    <dsp:include page="community_settings_edit_access.jsp" flush="false">
     <dsp:param name="mode" value="<%=mode%>"/> 
    </dsp:include>
  </core:Case>

  <core:Case value="4"><%-- advanced access  --%>
    <dsp:include page="community_access_editor.jsp" flush="false">
     <dsp:param name="mode" value="<%=mode%>"/> 
    </dsp:include>
  </core:Case>

  <core:DefaultCase>
    <dsp:include page="community_settings_edit.jsp" flush="false">
     <dsp:param name="mode" value="<%=mode%>"/> 
    </dsp:include>
  </core:DefaultCase><%-- mode=1 --%>

</core:Switch>

</td>
</tr>
</table>
</center>
<BR /><BR />

</body>
</html>

</admin:InitializeAdminEnvironment>

  <% } catch (Exception e) { %>

  <core:setTransactionRollbackOnly id="rollbackOnlyXA">
    <core:ifNot value="<%= rollbackOnlyXA.isSuccess() %>">
      The following exception was thrown:
      <pre>
       <%= rollbackOnlyXA.getException() %>
      </pre>
    </core:ifNot>
  </core:setTransactionRollbackOnly>
  <% } %>

</core:demarcateTransaction>


</paf:hasCommunityRole>
<paf:hasCommunityRole roles="user-manager">
 <% if ( !hasLocalAccess ) {
   hasLocalAccess = true; 
 %>
  <dsp:include page="community_users.jsp" flush="false"/>
 <%
    }
 %>
</paf:hasCommunityRole>
<paf:hasCommunityRole roles="page-manager">
 <% if ( !hasLocalAccess ) {
   hasLocalAccess = true; 
 %>
  <dsp:include page="community_pages.jsp" flush="false"/>
 <%
    }
 %>
</paf:hasCommunityRole>
<paf:hasCommunityRole roles="gear-manager">
 <% if ( !hasLocalAccess ) {
   hasLocalAccess = true; 
 %>
  <dsp:include page="community_gears.jsp" flush="false"/>
 <%
    }
 %>
</paf:hasCommunityRole>

<paf:hasCommunityRole roles="leader,settings-manager,page-manager,gear-manager,user-manager" barrier="true"/>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_settings.jsp#2 $$Change: 651448 $--%>
