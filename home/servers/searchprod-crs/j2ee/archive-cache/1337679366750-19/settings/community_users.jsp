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

<paf:hasCommunityRole roles="leader,user-manager">
<% hasLocalAccess = true; %>

<core:demarcateTransaction id="demarcateXA">
  <% try { %>

<admin:InitializeAdminEnvironment id="adminEnv">


<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/portal/admin/CommunityFormHandler"/>
<dsp:importbean bean="/atg/portal/admin/CommunityPrincipalFormHandler"/>
<i18n:message key="imageroot" id="i18n_imageroot"/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>

<title><i18n:message key="title-community_users"/></title>

<%
    String clearGif =  response.encodeURL("images/clear.gif");
    String mode          = "1";          // used for sub page with area
                                         // this copies an index array to a rendering array 
                                         // that provides the side links
    if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    } else {
       mode = "1"; // default to community pages
    }

%>

<dsp:include page="nav_header_main.jsp" flush="false">
  <dsp:param  name="mode" value="<%=mode%>" /> 
  <dsp:param  name="thisNavHighlight" value="users" /> 
</dsp:include>


<br /><center>
<table width="98%" border="0" cellpadding="0" cellspacing="0">
<tr>

<td width="10"><img src='<%=clearGif %>' height="1" width="10"  alt="" /></td>
<td width="150" valign="top">

 <dsp:include page="nav_sidebar.jsp" flush="false">
  <dsp:param  name="mode" value="<%=mode%>" /> 
  <dsp:param  name="sideBarIndex" value="1" /> 
 </dsp:include>

</td>
<td width="10"><img src='<%=clearGif %>' height="1" width="10"  alt="" /></td>

<td valign="top" width="80%" align="left">

<%@ include file="fragments/form_messages.jspf"%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="CommunityPrincipalFormHandler.formError"/>
  <dsp:oparam name="true">
    <dsp:setvalue bean="CommunityPrincipalFormHandler.resetFormExceptions"/>
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:setvalue bean="CommunityPrincipalFormHandler.reset"/>
  </dsp:oparam>
</dsp:droplet>

<core:Switch value="<%= mode%>">
 
  <core:Case value="1"><%-- members individuals --%>
    <dsp:include page="community_users_ind.jsp" flush="false">
     <dsp:param name="roleName" value="member"/>
     <dsp:param name="mode" value="<%=mode%>"/> 
   </dsp:include>
  </core:Case>

  <core:Case value="1a"><%--add members individuals --%>
    <dsp:include page="community_users_add_ind.jsp" flush="false">
     <dsp:param name="roleName" value="member"/>
    </dsp:include>
  </core:Case>

  <core:Case value="1b"><%-- membership requests --%>
    <dsp:include page="community_users_membership.jsp" flush="true">
    </dsp:include>
  </core:Case>

  <core:Case value="1d"><%-- members role editor --%>
    <dsp:include page="community_users_role_editor.jsp" flush="false">
     <dsp:param name="roleName" value="member"/>
 
    </dsp:include>
  </core:Case>

  <core:Case value="2"><%-- members organizations --%>
    <dsp:include page="community_users_org.jsp" flush="false">
     <dsp:param name="roleName" value="member"/>
    </dsp:include>
  </core:Case>

  <core:Case value="2a"><%-- add member organizations --%>
    <dsp:include page="community_users_add_org.jsp" flush="false">
     <dsp:param name="roleName" value="member"/>
    </dsp:include>
  </core:Case>

  <core:Case value="2b"><%-- list organizations' members --%>
    <dsp:include page="community_users_org_members.jsp" flush="false">
     <dsp:param name="roleName" value="member"/>
    </dsp:include>
  </core:Case>

  <core:Case value="2ab"><%-- list organizations' members --%>
    <dsp:include page="community_users_org_members.jsp" flush="false">
     <dsp:param name="roleName" value="member"/>
    </dsp:include>
  </core:Case>

  <core:Case value="3"><%-- guest individuals --%>
    <dsp:include page="community_users_ind.jsp" flush="false">
     <dsp:param name="roleName" value="guest"/>
    </dsp:include>
  </core:Case>

  <core:Case value="3a"><%--add guest individuals --%>
    <dsp:include page="community_users_add_ind.jsp" flush="false">
     <dsp:param name="roleName" value="guest"/>
    </dsp:include>
  </core:Case>

  <core:Case value="4"><%-- guest organizations --%>
    <dsp:include page="community_users_org.jsp" flush="false">
     <dsp:param name="roleName" value="guest"/>
    </dsp:include>
  </core:Case>

  <core:Case value="4a"><%-- add guest organizations --%>
    <dsp:include page="community_users_add_org.jsp" flush="false">
     <dsp:param name="roleName" value="guest"/>
    </dsp:include>
  </core:Case>

  <core:Case value="4b"><%-- list organizations' guest --%>
    <dsp:include page="community_users_org_members.jsp" flush="false">
     <dsp:param name="roleName" value="guest"/>
    </dsp:include>
  </core:Case>

  <core:Case value="4ab"><%-- list organizations' guest  --%>
    <dsp:include page="community_users_org_members.jsp" flush="false">
     <dsp:param name="roleName" value="guest"/>
    </dsp:include>
  </core:Case>

  <core:Case value="5"><%-- leader individuals --%>
    <dsp:include page="community_users_ind.jsp" flush="false">
     <dsp:param name="roleName" value="leader"/>
    </dsp:include>
  </core:Case>

  <core:Case value="5a"><%--add leader individuals --%>
    <dsp:include page="community_users_add_ind.jsp" flush="false">
     <dsp:param name="roleName" value="leader"/>
    </dsp:include>
  </core:Case>

  <core:Case value="6"><%-- leader organizations --%>
    <dsp:include page="community_users_org.jsp" flush="false">
     <dsp:param name="roleName" value="leader" />
    </dsp:include>
  </core:Case>

  <core:Case value="6a"><%-- add leader organizations --%>
    <dsp:include page="community_users_add_org.jsp" flush="false">
     <dsp:param name="roleName" value="leader" />
    </dsp:include>
  </core:Case>

  <core:Case value="6b"><%-- list organizations' leader --%>
    <dsp:include page="community_users_org_members.jsp" flush="false">
     <dsp:param name="roleName" value="leader" />
    </dsp:include>
  </core:Case>

  <core:Case value="6ab"><%-- list organizations' leader --%>
    <dsp:include page="community_users_org_members.jsp" flush="false">
     <dsp:param name="roleName" value="leader"/>
    </dsp:include>
  </core:Case>

  <core:Case value="7"><%-- create user 
    <dsp:include page="community_users_create.jsp" flush="false">
    </dsp:include>--%>
  </core:Case>

  <core:Case value="8"><%-- about users --%>
  
  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
   <table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
    <font class="pageheader">
     <i18n:message key="community_users_about_header"/>
    </font>
   </td></tr></table>
  </td></tr></table>

  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
   <font class="small"><br /><blockquote>
    <i18n:message key="helpertext-about-usersA"/><br /><br />
    <i18n:message key="helpertext-about-usersB"/><br /><br />
    <i18n:message key="helpertext-about-usersC"/>
    </font></blockquote>
   </td></tr></table>

  </core:Case>

</core:Switch>

</td>
</tr>
</table>

</center>
</body>
</html>

</admin:InitializeAdminEnvironment>

  <% } catch (Exception e) { e.printStackTrace();%>
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
<paf:hasCommunityRole roles="settings-manager">
 <% if ( !hasLocalAccess ) {
   hasLocalAccess = true; 
 %>
  <dsp:include page="community_settings.jsp" flush="false"/>
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
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_users.jsp#2 $$Change: 651448 $--%>
