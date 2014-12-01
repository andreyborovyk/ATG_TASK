<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%
  PortalServletResponse portalServletResponse = 
     (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
  PortalContextImpl portalContext = new PortalContextImpl(portalServletRequest);
%>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>

<admin:InitializeAdminEnvironment id="adminEnv">
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<% String FolderID = (String) request.getAttribute("folder_id"); 
   String clearGif =  response.encodeURL("images/clear.gif");
%>

<core:demarcateTransaction id="demarcateXA">

<core:ForEach id="folders"
              values="<%= adminEnv.getCommunityFolder(FolderID).getChildFolderSet(atg.portal.framework.Comparators.getFolderComparator()) %>"
              castClass="atg.portal.framework.folder.CommunityFolder"
	      elementId="folder">

  <table width="100%" cellpadding="1" cellspacing="0" border="0">
    <tr>
      <td width="20">
        <img src='<%=response.encodeURL("images/folder_open.gif")%>' height="13" width="16" border="0">
      </td>
      <td nowrap colspan="2"><font class="small">&nbsp;<b><%=folder.getName(response.getLocale())%></b></td>
    </tr>

    <core:ForEach id="communities"
                  values="<%= folder.getCommunitySet(atg.portal.framework.Comparators.getCommunityComparator()) %>"
		  castClass="atg.portal.framework.Community"
		  elementId="community">
      <% portalContext.setCommunity(community); %>
      <tr>
        <td></td>
	<td valign="top"><font class="small">&nbsp;<%=community.getName(response.getLocale())%></font></td>

        <%-- build URL to go to new communit template form --%>
	<core:createUrl id="newTemplateUrl" url="/portal/admin/community.jsp">
	  <core:urlParam param="mode" value="22"/>
	  <core:urlParam param="paf_page_url" value="/portal/admin/community.jsp"/>
	  <td nowrap align="right" valign="top"><font class="smaller">&nbsp;&nbsp;<dsp:a href="<%=portalServletResponse.encodePortalURL(newTemplateUrl.getNewUrl(),portalContext)%>"><i18n:message key="admin-create-community-template"/></dsp:a></font></td>
	</core:createUrl>

        <% String communityID = community.getId();%>
      </tr>
    </core:ForEach>
    
    <core:IfNotNull value="<%=folder.getCommunityFolders()%>">
      <tr>
        <td></td>
	<td colspan="2">
	  <% request.setAttribute("folder_id", folder.getId()); %>
	  <dsp:include page="community_newtemplate.jsp" flush="false"/>
	</td>
      </tr>
    </core:IfNotNull>
 
    <tr><td colspan="3"><img src="<%=clearGif%>" height="5" width="2" border="0"></td></tr>
  </table>
</core:ForEach>


</core:demarcateTransaction>

</admin:InitializeAdminEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/community_newtemplate.jsp#2 $$Change: 651448 $--%>
