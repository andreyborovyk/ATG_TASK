<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*,atg.portal.framework.impl.repository.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%
  Portal portal = (Portal)request.getAttribute(Attribute.PORTAL);
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
<dsp:importbean bean="/atg/portal/admin/SearchCommunities"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

<%
  String clearGif =  response.encodeURL("images/clear.gif");
  boolean flushSearchStr = true;
  if  ( request.getParameter("searchExecuted") != null ) {
   flushSearchStr = false;
  }
 if ( request.getParameter("searchStr") != null ) {
  flushSearchStr = false;
%>
  <dsp:setvalue bean="SearchCommunities.textInput" value='<%= request.getParameter("searchStr") %>'/>
  <dsp:setvalue      bean="SearchCommunities.search" value="foo"/>
<%
 }
%>
<dsp:getvalueof  bean="SearchCommunities.textInput" id="currentSearchStr">
<%
 if (flushSearchStr == true) {
  currentSearchStr = "";
 }
%>

<core:CreateUrl id="formTargetURL" url="community.jsp">
<core:UrlParam param="mode" value='<%=request.getParameter("mode")%>'/>
<core:UrlParam param="searchExecuted" value="true"/>
<dsp:form action="<%=formTargetURL.getNewUrl()%>" method="POST">
<input type="hidden" name="mode" value='<%=request.getParameter("mode")%>' >

<p align="left">
    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
	<font class="pageheader"><i18n:message key="admin-communities-header-search-communities"/>
	</td></tr></table>
	</td></tr></table>
	
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
	<font class="smaller"><i18n:message key="admin-communities-infotext-search-communities"/>
	</td></tr></table>
	<img src="<%=clearGif%>" height="1" width="1" border="0"><br>
	
	<table cellpadding="6" cellspacing="0" border="0" bgcolor="#c9d1d7" width="100%">
 
 <input type="hidden" name="searchExecuted" value="true" >
 <tr>
 	<td width="250">
 		<font class="smaller">
  		<i18n:message key="admin-community-search-string"/>
  	</td>
	<td width="80%">
		<dsp:input type="text" bean="SearchCommunities.textInput" size="20" value="<%=currentSearchStr%>"/>
	</td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td>
 
 <!-- use this hidden form tag to make sure the search handler is invoked if
       someone does not hit the submit button -->
  <dsp:input type="hidden" bean="SearchCommunities.search" value="Search"/>
  <i18n:message id="search02" key="search" /><dsp:input type="submit" bean="SearchCommunities.search" value="<%= search02 %>"/>

</dsp:form>
</core:CreateUrl><%-- formTargetURL --%>
</td></tr></table>

<img src="<%=clearGif%>" height="1" width="1" border="0"><br>
<core:If value='<%=!(currentSearchStr.equals(""))%>'>
<table cellpadding="6" cellspacing="0" border="0" bgcolor="#c9d1d7" width="100%"><tr><td>
<dsp:droplet name="IsEmpty">
  <dsp:param name="value" bean="SearchCommunities.searchResults"/>
  <dsp:oparam name="false">
 <b><font class="smaller">
 <i18n:message key="admin-community-search-results"/>
</font><br>
</b>

<img src="<%=clearGif%>" height="5" width="1" border="0"><br>
<table cellpadding="0" cellspacing="1" border="0" background="images/clear.gif">
    <dsp:getvalueof id="results" bean="SearchCommunities.searchResults">
    <core:ForEach id="searchResults"
     values="<%= results %>"
     castClass="atg.repository.RepositoryItem"
     elementId="community">

<%
  portalContext.setCommunity(((PortalImpl)portal).getCommunityForItem(community));
%>

       <tr><td nowrap><font class="small">
       <%= community.getPropertyValue("name") %>

       <!-- build URL to edit community with link -->

       <core:CreateUrl id="editCommLeaderUrl" url="/portal/settings/community_settings.jsp">
         <core:UrlParam param="paf_page_url" value="/portal/admin/community.jsp"/>
         </font></td><td nowrap><font class="smaller">&nbsp;&nbsp;
         <dsp:a href="<%= portalServletResponse.encodePortalURL(editCommLeaderUrl.getNewUrl(),portalContext)%>"><i18n:message key="admin-community-edit"/></dsp:a>
       </core:CreateUrl>
       </td>

       <dsp:getvalueof id="searchStr" bean="SearchCommunities.textInput" >

        <core:CreateUrl id="return_page_url" url="/portal/admin/community.jsp">
         <core:UrlParam param="mode" value="4"/>
         <paf:encodeUrlParam param="searchStr" value="<%=searchStr%>"/>

    <!-- build URL to add gears to community with link -->
    <core:CreateUrl id="gearsUrl" url="/portal/admin/community.jsp">
      <core:UrlParam param="mode" value="8"/>
         <paf:encodeUrlParam param="paf_page_url" value='<%=return_page_url.getNewUrl()%>'/>
      <core:UrlParam param="paf_page_url" value="/portal/admin/community.jsp"/>
      <td nowrap><font class="smaller">&nbsp;&nbsp;<dsp:a href="<%=portalServletResponse.encodePortalURL(gearsUrl.getNewUrl(),portalContext)%>"><i18n:message key="admin-community-gears"/></dsp:a></font></td>
      </core:CreateUrl>



        <td nowrap>
        <!-- build URL to delete community with link -->
        <core:CreateUrl id="deleteCommLeaderUrl" url="/portal/admin/community.jsp">
         <core:UrlParam param="mode" value="9"/>
         <paf:encodeUrlParam param="paf_page_url" value='<%=return_page_url.getNewUrl()%>'/>

</font></td><td nowrap><font class="smaller">&nbsp;&nbsp;
        <dsp:a href="<%= portalServletResponse.encodePortalURL(deleteCommLeaderUrl.getNewUrl(),portalContext)%>"><i18n:message key="admin-community-delete"/></dsp:a>
        </core:CreateUrl>


        </core:CreateUrl>
       </dsp:getvalueof>

       <% String communityID = (String) community.getRepositoryId();%>
       <!-- using paf environment get URL to community with link -->

       </font></td><td nowrap><font class="smaller">&nbsp;
        <%  String isActive = community.getPropertyValue("enabled").toString() ;    %>
     <core:Switch value="<%=isActive%>">
       <core:Case value="true">
         <core:CreateUrl id="viewCommLeaderUrl" url="<%= adminEnv.getCommunityURI(communityID) %>">
         <dsp:a href="<%=viewCommLeaderUrl.getNewUrl()%>"><i18n:message key="admin-community-visit"/></dsp:a>
       </core:CreateUrl>
     </core:Case>
     <core:Case value="false">
       <i18n:message key="admin-community-inactive"/>
     </core:Case>
    </core:Switch>
    </td></tr>
  </core:ForEach>
</table>

</dsp:getvalueof>
</dsp:oparam>
<dsp:oparam name="true">
<font class="smaller"><b>
<i18n:message key="admin-community-search-results"/></b>
<br><br>
<i18n:message key="admin-community-search-results-none"/>
</font>
</dsp:oparam>
</dsp:droplet>

</td></tr></table>
</core:If>

</dsp:getvalueof>

</admin:InitializeAdminEnvironment>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/community_search.jsp#2 $$Change: 651448 $--%>
