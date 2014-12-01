<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%
  PortalServletResponse portalServletResponse = 
     (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
  PortalContextImpl portalContext = new PortalContextImpl(portalServletRequest);
%>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" changeResponseLocale="false" />

<core:demarcateTransaction id="demarcateXA">

<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/portal/admin/CommunityFormHandler"/>
<dsp:importbean bean="/atg/portal/admin/CommunityTemplateFormHandler"/>
<dsp:importbean bean="/atg/portal/admin/SearchCommunities"/>
<dsp:importbean bean="/atg/portal/admin/CommunityFolder"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<i18n:message id="i18n_imageroot" key="imageroot"/>
<html>
<head>
<title><i18n:message key="title-admin-communities"/></title>
<link rel="STYLESHEET" type="text/css" href='<%= response.encodeURL("css/default.css") %>'>
</head>

<%-- body tag is declared in header_main.jspf and is shared by all admin pages --%>

<% 
  String thisNavHighlight = "";
  String thisModeStr = null;
%>

<dsp:param name="thisNavHighlight" value="community"/>

<%
    String clearGif =  response.encodeURL("images/clear.gif");
    thisNavHighlight     = "community";  // used for header_main
    String currPid       = "0";          // used for sidebar sub selection
    String mode          = "1";          // used for sub page with area
                                         // this copies an index array to a rendering array 
                                         // that provides the side links

    if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    } else {
       mode = "1"; // default to available communities
    }
    String greetingName = "";
%>

<%@ include file="nav_header_main.jspf"%>

<br>
<table width="98%" border="0" cellpadding="0" cellspacing="0">
<tr>
<td valign="top" width="20"><img src='<%=clearGif %>' height="1" width="20" alt=""></td>

<td width="150" valign="top"><font class="smaller">
 <%@ include file="nav_sidebar.jspf"%><br>
</font></td>

<td width="20"><img src='<%=clearGif %>' height="1" width="20" alt=""></td>
<td valign="top" width="90%" align="left">

<%@ include file="form_messages.jspf"%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="CommunityFormHandler.formError"/>
  <dsp:oparam name="false">
     <%--     reset the session scoped form handler --%>
     <dsp:setvalue bean="CommunityFormHandler.reset"/>
  </dsp:oparam>        
</dsp:droplet>

<%-- Reset form exception for the session scoped CommunityFormHandler --%>
<dsp:setvalue bean="CommunityFormHandler.resetFormExceptions"/>

<%-- Display any errors processing form --%>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="CommunityTemplateFormHandler.formError"/>
  <dsp:oparam name="true">
    <dsp:setvalue bean="CommunityTemplateFormHandler.reset"/>
  </dsp:oparam>
</dsp:droplet>

<%-- Reset form exception for the session scoped CommunityFormHandler --%>
<dsp:setvalue bean="CommunityTemplateFormHandler.resetFormExceptions"/>

 <core:Switch value="<%= mode%>">
 
  <core:case value="2">
    <dsp:droplet name="/atg/dynamo/droplet/Switch">
    <dsp:param name="value" bean="CommunityFormHandler.formError"/>
      <dsp:oparam name="true">
           <dsp:setvalue bean="CommunityFormHandler.resetFormExceptions"/>
      </dsp:oparam>
      <dsp:oparam name="false">
      <%--Don't reset the form if there is form exception --%>
      <dsp:setvalue bean="CommunityFormHandler.reset"/>
    </dsp:oparam>
    </dsp:droplet>
   <dsp:include page="community_new.jsp" flush="false"/>
  </core:case>

  <core:case value="3">
   <dsp:include page="community_newfolder.jsp" flush="false">
     <dsp:param name="mode" value="<%= mode %>"/>
   </dsp:include>
  </core:case>

  <core:case value="4">
   <dsp:include page="community_search.jsp" flush="false"/>
  </core:case>

  <core:case value="5"><%-- about communities --%>
   <font class="small" >
    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
	<font class="pageheader"> <i18n:message key="admin-about-communities-header"/>
	</font></td></tr></table>
	</td></tr></table>
	
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
	<font class="smaller">
	
    <i18n:message key="admin-helpertext-communitiesA"/><br><br>
    <i18n:message key="admin-helpertext-communitiesB"/><br><br>
    <i18n:message key="admin-helpertext-communitiesC"/>
   </font>
   </td></tr></table><br>
  </core:case>


  <core:case value="6">
    <dsp:include page="community_gears.jsp" flush="false"/>
  </core:case>
  <core:case value="8">
    <dsp:include page="community_gears.jsp" flush="false"/>
  </core:case>
  <core:case value="7">
    <dsp:include page="community_delete.jsp" flush="false"/>
  </core:case>
  <core:case value="9">
    <dsp:include page="community_delete.jsp" flush="false"/>
  </core:case>
  <core:case value="20">
    <dsp:include page="community_templates.jsp" flush="false"/>
  </core:case>

  <%-- create a community template --%>
  <core:case value="21">
    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%">
      <tr>
        <td>
	  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%">
	    <tr>
	      <td>
	        <font class="pageheader"><i18n:message key="admin-community-template-new-header"/>
              </td>
	    </tr>
	  </table>
	</td>
      </tr>
    </table>
    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%">
      <tr>
        <td>
	  <font class="smaller">
	    <i18n:message key="admin-community-template-new-helpertext"/>
	  </font>
	</td>
      </tr>
    </table>

    <img src="<%=clearGif%>" height="1" width="7" border="0" alt=""><br> 
	
    <font class="smaller">

    <%-- call to getAllItems to get root community folder --%>
    <admin:GetAllItems id="items">
    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#c9d1d7" width="100%">
      <tr>
        <td>
	  <% request.setAttribute("folder_id", items.getRootCommunityFolder().getId()); %>
	  <table cellpadding="1" cellspacing="0" border="0" >
	    <tr>
	      <td>
	        <img src="<%=clearGif%>" height="3" width="7" border="0" alt=""><img src='<%=response.encodeURL("images/folder_open.gif")%>' height="13" width="16" border="0" alt="">
	      </td>
	      <td nowrap colspan="2">
	        <font class="small">&nbsp;<b><%=items.getRootCommunityFolder().getName(response.getLocale())%></b>
	      </td>
	    </tr>

	    <core:ForEach id="communities"
	                  values="<%=adminEnv.getCommunityFolder( items.getRootCommunityFolder().getId()).getCommunities() %>"
			  castClass="atg.portal.framework.Community"
			  elementId="community">

	    <%  portalContext.setCommunity(community); %>
	    <tr>
	      <td><img src="<%=clearGif%>" height="2" width="2" border="0" alt=""></td>
	      <td  valign="top">
	        <font class="small">&nbsp;&nbsp;<%=community.getName(response.getLocale())%></font>
	      </td>
	      <%-- create url for generating template --%>
	        <core:CreateUrl id="templateUrl" url="/portal/admin/community.jsp">
	          <core:UrlParam param="mode" value="22"/>
		  <core:UrlParam param="paf_page_url" value="/portal/admin/community.jsp"/>
		  <td nowrap align="right" valign="top">
		    <font class="smaller">&nbsp;&nbsp;<dsp:a href="<%=portalServletResponse.encodePortalURL(templateUrl.getNewUrl(),portalContext)%>"><i18n:message key="admin-create-community-template"/></dsp:a></font>
		  </td>
	        </core:CreateUrl>

		<% String communityID = community.getId();%>
	    </tr>
	    </core:ForEach><%--  community --%>

	    <tr>
	      <td colspan="3"><img src="<%=clearGif%>" height="5" width="2" border="0" alt=""></td>
	    </tr>
	    <tr>
	      <td></td>
	      <td colspan="2">
	        <dsp:include page="community_newtemplate.jsp" flush="false"/>
	      </td>
	    </tr>
	  </table>
	</td>
      </tr>
    </table>
    </admin:GetAllItems>


   </font>
   <br><br><br>
   </font>
  </core:case>

  <core:case value="22">
    <dsp:include page="community_newtemplate_form.jsp" flush="false"/>
  </core:case>

  <core:case value="23">
    <dsp:include page="community_spawn.jsp" flush="false"/>
  </core:case>

  <core:case value="24">
    <dsp:include page="community_delete_template.jsp" flush="false"/>
  </core:case>

  <core:defaultCase>
   <font class="smaller">
        <%-- heade title --%>
        <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
	<font class="pageheader"><i18n:message key="admin-communities-header-communities"/>
	</td></tr></table>
	</td></tr></table>
	<%-- helper text --%>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
	<font class="smaller"><i18n:message key="admin-communities-infotext-communities"/><br>
	</td></tr></table><img src="<%=clearGif%>" height="1" width="7" border="0" alt=""><table cellpadding="4" cellspacing="0" border="0" width="100%" bgcolor="#c9d1d7">
	
        <admin:GetAllItems id="items">
        <tr><td>
	
	<%-- this table generates the list                                    --%>

 <table cellpadding="0" cellspacing="1" border="0" width="60%">
  <% request.setAttribute("folder_id", items.getRootCommunityFolder().getId()); %>
  <tr>
   <td><img src="<%=clearGif%>" height="3" width="7" border="0" alt=""><img src='<%=response.encodeURL("images/folder_open.gif")%>' height="13" width="16" border="0" alt=""></td>
   <td nowrap colspan="5"><font class="small">&nbsp;<b><%=items.getRootCommunityFolder().getName(response.getLocale())%></b></td>
  </tr>

  <core:ForEach id="communities"
    values="<%=adminEnv.getCommunityFolder( items.getRootCommunityFolder().getId()).getCommunitySet(atg.portal.framework.Comparators.getCommunityComparator()) %>"
    castClass="atg.portal.framework.Community"
    elementId="community">
   <%   portalContext.setCommunity(community); %>
  <tr>
   <td>&nbsp;&nbsp;</td>
   <td nowrap width="210"><font class="small">&nbsp;&nbsp;<%=community.getName(response.getLocale())%></font></td>

    <%-- build URL to edit community with link --%>
   <core:CreateUrl id="editUrl" url="/portal/settings/community_settings.jsp">
    <core:UrlParam param="paf_page_url" value="/portal/admin/community.jsp"/>
    <td nowrap><font class="smaller">&nbsp;&nbsp;<dsp:a href="<%= portalServletResponse.encodePortalURL(editUrl.getNewUrl(),portalContext)%>"><i18n:message key="admin-community-edit"/></dsp:a></font></td>
    </core:CreateUrl>

    <%-- build URL to add gears to community with link --%>
    <core:CreateUrl id="gearsUrl" url="/portal/admin/community.jsp">
      <core:UrlParam param="mode" value="6"/>
      <core:UrlParam param="paf_page_url" value="/portal/admin/community.jsp"/>
      <td nowrap><font class="smaller">&nbsp;&nbsp;<dsp:a href="<%= portalServletResponse.encodePortalURL(gearsUrl.getNewUrl(),portalContext)%>"><i18n:message key="admin-community-gears"/></dsp:a></font></td>
      </core:CreateUrl>

    <%-- build URL to delete community with link --%>
    <core:CreateUrl id="deleteUrl" url="/portal/admin/community.jsp">
      <core:UrlParam param="mode" value="7"/>
      <core:UrlParam param="returnMode" value="1"/>
      <core:UrlParam param="paf_page_url" value="/portal/admin/community.jsp"/>
      <td nowrap><font class="smaller">&nbsp;&nbsp;<dsp:a href="<%= portalServletResponse.encodePortalURL(deleteUrl.getNewUrl(),portalContext)%>"><i18n:message key="admin-community-delete"/></dsp:a></font></td>
    </core:CreateUrl>

    <%
       String communityID = community.getId(); 
       portalContext.setPage(portalContext.getCommunity().getDefaultPage()); 
    %>
    <%-- using paf environment get URL to community with link --%>
    <td nowrap><font class="smaller">&nbsp;
    <dsp:droplet name="Switch">
      <dsp:param name="value" value="<%= community.isEnabledString() %>"/>
      <dsp:oparam name="false">
        <i18n:message key="admin-community-inactive"/>
      </dsp:oparam>
      <dsp:oparam name="true">
        <core:CreateUrl id="viewUrl" url="<%= adminEnv.getCommunityURI(communityID) %>">
          <core:UrlParam param="paf_default_view" value="true"/>
          <dsp:a href="<%= portalServletResponse.encodePortalURL(viewUrl.getNewUrl(),portalContext)%>"><i18n:message key="admin-community-visit"/></dsp:a>
        </core:CreateUrl>
      </dsp:oparam>
    </dsp:droplet>
    </font></td>
   </tr>
   </core:ForEach>
   <tr>
    <td colspan="6"><img src="<%=clearGif%>" height="2" width="200" border="0" alt=""></td>
   </tr>
   <tr>
   <td>&nbsp;&nbsp;</td>
   <td colspan="5">
     <dsp:include page="community_folder.jsp" flush="false"/> 
   </td>
   </tr>
  </table>

  </admin:GetAllItems>

  <br><br><br>

  
 </td>
 </tr>
 </table>
 <%--end table generating list--%>

 </td></tr></table>
 <%--end table bounding list--%>


  </core:defaultCase>
 </core:Switch>


 </td>
 </tr>
 </table>
 <%--end table generating list--%>

 </td></tr></table>
 <%--end table bounding list--%>


</body>
</html>
</admin:InitializeAdminEnvironment>

</core:demarcateTransaction>

</dsp:page>


<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/community.jsp#2 $$Change: 651448 $--%>
