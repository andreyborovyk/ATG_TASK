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
<paf:setFrameworkLocale/>

<dsp:page>

<paf:hasCommunityRole roles="leader,gear-manager">
<% hasLocalAccess = true; %>

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<core:demarcateTransaction id="demarcateXA">
  <% try { %>

<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/portal/admin/CommunityFormHandler"/>

<dsp:importbean bean="/atg/portal/admin/GearFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:getvalueof id="dsp_gear_id" idtype="java.lang.String"      param="paf_gear_id">
<dsp:getvalueof id="dsp_page_id" idtype="java.lang.String"      param="paf_page_id">
<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String"     param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">

<i18n:message key="imageroot" id="i18n_imageroot"/>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>

<title><i18n:message key="title-community_gears"/></title>

<script language="Javascript"  type="text/javascript">

if (parent.frames.length > 0 ) {
  parent.document.location.href = document.location.href;
}

</script>


<%
 
    atg.servlet.DynamoHttpServletRequest dynamoRequest=atg.servlet.ServletUtil.getDynamoRequest(request);
    String dsp_paf_community_id = dynamoRequest.getParameter("paf_community_id"); 

    String clearGif =  response.encodeURL("images/clear.gif");
    String mode          = "1";
    String outputStr     = "";

    if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    } else {
       mode = "1"; // default to gears display page
    }

%>



<dsp:include page="nav_header_main.jsp" flush="false">
  <dsp:param  name="mode"             value="<%=mode%>" /> 
  <dsp:param  name="thisNavHighlight" value="gears" /> 
</dsp:include>



<br /><center>
<table width="98%" border="0" cellpadding="0" cellspacing="0">
<tr>

<td width="10"><img src='<%=clearGif %>' height="1" width="10"  alt="" /></td>
<td width="150" valign="top">

 <dsp:include page="nav_sidebar.jsp" flush="false">
  <dsp:param  name="mode" value="<%=mode%>" /> 
  <dsp:param  name="sideBarIndex" value="3" /> 
 </dsp:include>

</td>
<td width="10"><img src='<%=clearGif %>' height="1" width="10"  alt="" /></td>

<td valign="top" width="80%" align="left">

<%@ include file="fragments/form_messages.jspf"%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="GearFormHandler.formError"/>
  <dsp:oparam name="true">
    <dsp:setvalue bean="GearFormHandler.resetFormExceptions"/>
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:setvalue bean="GearFormHandler.reset"/>
  </dsp:oparam>
</dsp:droplet>

 <core:Switch value="<%= mode%>">

  <core:Case value="1"><%-- default mode --%>
<% 
  String CpagesURLStr = null;
%>
<core:CreateUrl id="CpagesURL"      url="/portal/settings/community_pages.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id"  value='<%= communityId %>'/>
    <% CpagesURLStr = CpagesURL.getNewUrl();%>
</core:CreateUrl>
 <i18n:message id="link_text" key="community_gears_intro_page_intro_edit_link"/>
 <%
     String editUrlStr = link_text; 
 %>
<paf:hasCommunityRole roles="page-manager">
 <%
      editUrlStr = "<a  target='_top' href='"+CpagesURLStr+"'>"+link_text+"</a>"; 
 %>
</paf:hasCommunityRole>

   <%
       String rowHighlight = "eeeeee";
       String deleteMode = "5";
       String returnMode = "1";
       String colspan = "4";
       String titleSize = "small";
       boolean showConfigureLink = true;
    %>
	
<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
<font class="pageheader">
    <i18n:message key="community_gears_intro_page_header_param">
     <i18n:messageArg value="<%=adminEnv.getCommunity().getName()%>"/>
    </i18n:message>
</font></td></tr></table>
</td></tr></table>
	
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
<font class="smaller">
    <i18n:message key="community_gears_intro_page_intro" />
    <i18n:message key="community_gears_intro_page_intro_edit_params">
     <i18n:messageArg value="<%= editUrlStr %>"/> 
    </i18n:message>
</font></td></tr></table>
<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0" alt="" /><br />
<table cellpadding="6" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%"><tr><td>
    <%@include file="fragments/community_gears_listing.jspf"%>
</td></tr></table>
  </core:Case><%-- end default mode --%>
 
  <core:Case value="2"><%-- select non-shared gears --%>
    <dsp:include page="community_gears_add.jsp" flush="false"/> 
  </core:Case>

  <core:Case value="3"><%-- select shared gears --%>
    <dsp:include page="community_gears_add_shared.jsp" flush="false"/>
  </core:Case>

  <core:Case value="11"><%-- select shared gears confirm--%>
    <dsp:include page="community_gears_add_shared_confirm.jsp" flush="false"/>
  </core:Case>

  <core:Case value="6"><%-- select non-shared gears --%>
    <dsp:include page="community_gears_add_configure.jsp" flush="false"/> 
  </core:Case>
 
  <core:Case value="7"><%-- configure gear basics --%>
   <dsp:include page="community_gears_configure_basic.jsp" flush="false"/>
  </core:Case>
 
  <core:Case value="8"><%-- configure gear sharing--%>
   <dsp:include page="community_gears_configure_sharing.jsp" flush="false"/>
  </core:Case>
 
  <core:Case value="12"><%-- configure gear ACL --%>
   <dsp:include page="community_gears_access_editor.jsp" flush="false"/>
  </core:Case>

  <core:Case value="13"><%-- user selection for role assignment --%>
   <dsp:include page="community_gears_role_assigner.jsp" flush="false"/>
  </core:Case>
 
  <core:Case value="14"><%-- role assignment for a user --%>
   <dsp:include page="community_gears_role_editor.jsp" flush="false"/>
  </core:Case>
 
  <core:Case value="15"><%-- role assignment for a user --%>

   <dsp:include page="community_gears_configure_additional.jsp" flush="false">
    <dsp:param name="paf_gear_id" value="<%=dsp_gear_id%>"/>
    <dsp:param name="paf_community_id" value="<%=dsp_community_id%>"/>
    <dsp:param name="paf_page_url" value="<%=dsp_page_url%>"/>
    <dsp:param name="paf_gear_id" value="<%=dsp_gear_id%>"/>
    <dsp:param name="paf_dm"  value="full"/>
    <dsp:param name="paf_gm"  value="=instanceConfig"/>

   </dsp:include>

<%--
<dsp:importbean bean="/atg/portal/admin/GearFormHandler"/>
<dsp:getvalueof id="gearItem" idtype="atg.portal.framework.Gear" bean="GearFormHandler.gear">
<dsp:getvalueof id="gearName" bean="GearFormHandler.name">
  <% request.setAttribute("atg.paf.Gear", (String) request.getParameter("paf_gear_id") ); 
     request.setAttribute("atg.paf.Page", (String) request.getParameter("paf_page_id") ); 
     request.setAttribute("atg.paf.Community", (String) request.getParameter("paf_community_id") ); 
     String callingPage = "additional";
     String divider = "<font class='smaller'>&nbsp;&nbsp;|&nbsp;&nbsp;</font>";
  %>
  <%@include file="fragments/community_gears_configure_nav.jspf" %>
  <paf:InitializeGearEnvironment id="gearEnv">
    <paf:PrepareGearRenderers id="gearRenderers">
      <paf:PrepareGearRenderer gearRenderers="<%= gearRenderers.getGearRenderers() %>"
                               gear="<%= gearEnv.getGear() %>"
                               displayMode='full'
                               gearMode='instanceConfig' />
      <paf:RenderPreparedGear gear="<%= gearEnv.getGear() %>"
                              gearRenderers="<%= gearRenderers.getGearRenderers() %>" />
    </paf:PrepareGearRenderers>
  </paf:InitializeGearEnvironment>
</dsp:getvalueof>
</dsp:getvalueof>
--%>


  </core:Case>
 
  <core:Case value="4"><%-- about gears --%>


  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
   <table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
    <font class="pageheader">
     <i18n:message key="community_gears_about_page_header"/>
    </font>
   </td></tr></table>
  </td></tr></table>

  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%">
   <tr><td><font class="small"><br /><blockquote>
    <i18n:message key="helpertext-about-gearsA"/><br /><br />
    <i18n:message key="helpertext-about-gearsB"/><br /><br />
    <i18n:message key="helpertext-about-gearsC"/>
   </blockquote></font></td></tr>
  </table>
     
  </core:Case>
  
</core:Switch>

  <core:If value='<%= mode.equals("5") ||  mode.equals("9") ||  mode.equals("10")  %>'>
    <%-- confirm delete gears --%>
     <dsp:include page="community_gears_confirm_delete.jsp"  flush="false"/>
  </core:If>

</td>
</tr>
</table>

</center>
</body>
</html>

</dsp:getvalueof><%-- dsp_page_id      --%>
</dsp:getvalueof><%-- dsp_page_url     --%>
</dsp:getvalueof><%-- dsp_community_id --%>
</dsp:getvalueof><%-- dsp_gear_id --%>
                

</admin:InitializeAdminEnvironment>

  <% } catch (Exception e) {%>
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

<paf:hasCommunityRole roles="leader,settings-manager,page-manager,gear-manager,user-manager" barrier="true"/>


</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_gears.jsp#2 $$Change: 651448 $--%>
