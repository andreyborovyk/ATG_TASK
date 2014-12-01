<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>

<%
  PortalServletResponse portalServletResponse = 
     (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
  PortalContextImpl portalContext = new PortalContextImpl(portalServletRequest);
%>

<dsp:page>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />

<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<% String FolderID = (String) request.getAttribute("folder_id"); 
   String clearGif =  response.encodeURL("images/clear.gif");
   String dottedGif =  response.encodeURL("images/dotted.gif");
%>
<table  cellpadding="0" cellspacing="1" border="0">
<core:ForEach id="folders"
  values="<%= adminEnv.getCommunityFolder(FolderID).getChildFolderSet(atg.portal.framework.Comparators.getFolderComparator()) %>"
  castClass="atg.portal.framework.folder.CommunityFolder"
  elementId="folder">

  <tr>
  <td><img src="<%=clearGif%>" height="3" width="7" border="0"><img src='<%=response.encodeURL("images/folder_open.gif")%>' height="13" width="16" border="0"></td>
  <td nowrap colspan="5"><font class="small">&nbsp;<b><%=folder.getName(response.getLocale())%></b></td></tr>


  <core:ForEach id="communities"
    values="<%= folder.getCommunitySet(atg.portal.framework.Comparators.getCommunityComparator()) %>"
    castClass="atg.portal.framework.Community"
    elementId="community">
    <tr>
    <td><img src="<%=clearGif%>" height="2" width="2" border="0"></td>
    <td width="65%" valign="top" NOWRAP><font class="small">&nbsp;&nbsp;<%=community.getName(response.getLocale())%></font></td>

<%
  portalContext.setCommunity(community);
%>

    <!-- build URL to edit community with link -->
    <core:CreateUrl id="editUrl" url="/portal/settings/community_settings.jsp">
      <core:UrlParam param="paf_page_url" value="/portal/admin/community.jsp"/>
     <td  valign="top" NOWRAP><font class="smaller">&nbsp;&nbsp;<dsp:a href="<%= portalServletResponse.encodePortalURL(editUrl.getNewUrl(),portalContext)%>"><i18n:message key="admin-community-edit"/></dsp:a></font></td>
    </core:CreateUrl>

    <!-- build URL to add gears to community with link -->
    <core:CreateUrl id="gearsUrl" url="/portal/admin/community.jsp">
      <core:UrlParam param="mode" value="6"/>
      <core:UrlParam param="paf_page_url" value="/portal/admin/community.jsp"/>
      <td  valign="top" NOWRAP><font class="smaller">&nbsp;&nbsp;<dsp:a href="<%= portalServletResponse.encodePortalURL(gearsUrl.getNewUrl(),portalContext)%>"><i18n:message key="admin-community-gears"/></dsp:a></font></td>
      </core:CreateUrl>


    <!-- build URL to delete community with link -->
    <core:CreateUrl id="deleteUrl" url="/portal/admin/community.jsp">
      <core:UrlParam param="mode" value="7"/>
      <core:UrlParam param="paf_page_url" value="/portal/admin/community.jsp"/>
      <td  valign="top" NOWRAP><font class="smaller">&nbsp;&nbsp;<dsp:a href="<%=portalServletResponse.encodePortalURL(deleteUrl.getNewUrl(),portalContext)%>"><i18n:message key="admin-community-delete"/></dsp:a></font></td>
    </core:CreateUrl>

    <% String communityID = community.getId();%>

    <!-- using paf environment get URL to community with link -->
    <td  valign="top" NOWRAP><font class="smaller">&nbsp;
    <dsp:droplet name="Switch">
      <dsp:param name="value" value="<%= community.isEnabledString() %>"/>
      <dsp:oparam name="false">
        <i18n:message key="admin-community-inactive"/>
      </dsp:oparam>
      <dsp:oparam name="true">
        <core:CreateUrl id="viewUrl" url="<%= adminEnv.getCommunityURI(communityID) %>">
          <core:UrlParam param="paf_default_view" value="true"/>
          <dsp:a href="<%=viewUrl.getNewUrl()%>"><i18n:message key="admin-community-visit"/></dsp:a>
        </core:CreateUrl>
      </dsp:oparam>
    </dsp:droplet>
    </font></td>

     </tr>
    </core:ForEach>
    
  <core:IfNotNull value="<%=folder.getCommunityFolders()%>">
    <tr><td>&nbsp;&nbsp;</td>
    <% request.setAttribute("folder_id", folder.getId()); %>
    <td colspan="5">
     <dsp:include page="community_folder.jsp" flush="false"/>
    </td>
   </tr>
  </core:IfNotNull>
  <tr><td colspan="6"><img src="<%=clearGif%>" height="5" width="2" border="0"></td></tr>
 </core:ForEach>
</table>

</admin:InitializeAdminEnvironment>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/community_folder.jsp#2 $$Change: 651448 $--%>
