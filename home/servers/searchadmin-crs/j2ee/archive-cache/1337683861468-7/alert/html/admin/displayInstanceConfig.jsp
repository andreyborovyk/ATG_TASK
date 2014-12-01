<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<paf:InitializeGearEnvironment id="gearEnv">
<paf:hasCommunityRole roles="leader,gear-manager">

<i18n:bundle baseName="atg.portal.gear.alert.alert" localeAttribute="userLocale" changeResponseLocale="false" />


<% String origURI= gearEnv.getOriginalRequestURI();
   String gearId = gearEnv.getGear().getId();
   String pageId = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String communityId = request.getParameter("paf_community_id");

   String communityAdminURI="/portal/settings/community_gears.jsp";
   String clearGif = gearEnv.getGear().getServletContext() + "/html/images/clear.gif";

%>
<%--
  <core:CreateUrl id="selectURL" url="<%=communityAdminURI%>">
         <core:UrlParam param="paf_page_id" value="<%= pageId %>"/>
         <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
         <core:UrlParam param="paf_community_id" value="<%= communityId %>"/>
    <a href="<%= selectURL.getNewUrl() %>"><font class="smaller"><i18n:message key="back-to-content-link"/>&nbsp;</font></a>
  </core:CreateUrl>
  <br><br>
 --%>
  
  
<i18n:message id="i18n_quote" key="html_quote"/>
<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit">
<i18n:message key="instance-config-title">
 <i18n:messageArg value='<%= gearEnv.getGear().getName(response.getLocale()) %>'/>
  <i18n:messageArg value="<%=i18n_quote%>"/>
</i18n:message>  </font>
</td></tr></table>
</td></tr></table>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
<font class="smaller"><i18n:message key="admin_instance_display_helper"/></font>
</td></tr></table>

<img src="<%=clearGif%>" height="1" width="1" border="0"><br>

	
  
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>

<%-- ****** Display Parameters ****** --%>
<font class="smaller_bold"><i18n:message key="display-parameters"/></font>


 <table border="0" cellpadding="0" cellspacing="0" width="100%"> 
	 <tr><td bgcolor="#666666" colspan="2"><img src='<%=clearGif%>' height="1" width="1" border="0"></td></tr>
	      <tr bgcolor="#dddddd">
      <td width="35%"><font class="smaller"><i18n:message key="shared-display-count-title"/></td>
      <td><font class="smaller"><%= gearEnv.getGearInstanceParameter("sharedDisplayCount") %></font>&nbsp;</td>
    </tr>
    </tr>
	<tr><td bgcolor="#666666" colspan="2"><img src='<%=clearGif%>' height="1" width="1" border="0"></td></tr>
	</table>

        <core:CreateUrl id="editDisplayUrl" url="<%= origURI %>">
          <core:UrlParam param="paf_dm" value="full"/>
          <core:UrlParam param="paf_gm" value="instanceConfig"/>
          <core:UrlParam param="config_page" value="displayParams"/>
          <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
          <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
          <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
          <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
          <a href="<%= editDisplayUrl.getNewUrl() %>"><font class="smaller"><i18n:message key="config-shared-count-param-link"/></font></a>
        </core:CreateUrl>


 <br><br>
    <%-- ****** Access Parameters ****** --%>


        <font class="smaller_bold"><i18n:message key="access-config"/></font>

 <table border="0" cellpadding="0" cellspacing="0" width="100%">
 	 <tr><td bgcolor="#666666" colspan="2"><img src='<%=clearGif%>' height="1" width="1" border="0"></td></tr> 
    <tr bgcolor="#dddddd">
      <td width="35%"><font class="smaller"><i18n:message key="aggregated-community-alerts-title"/></font></td>
      <td>
        <core:If value='<%= ((String)gearEnv.getGearInstanceParameter("aggregatedCommunityEnabled"))  %>'>
          <font class="smaller"><i18n:message key="constant-true"/></font>
        </core:If>
        <core:IfNot value='<%= ((String)gearEnv.getGearInstanceParameter("aggregatedCommunityEnabled")) %>'>
          <font class="smaller"><i18n:message key="constant-false"/></font>
        </core:IfNot>
      </td>
    </tr>

    <tr bgcolor="#ffffff">
      <td><font class="smaller"><i18n:message key="community-enabled-title" /></font></td>
      <td>
        <core:If value='<%= ((String)gearEnv.getGearInstanceParameter("communityEnabled")) %>'>
          <font class="smaller"><i18n:message key="constant-true"/></font>
        </core:If>
        <core:IfNot value='<%= ((String)gearEnv.getGearInstanceParameter("communityEnabled")) %>'>
          <font class="smaller"><i18n:message key="constant-false"/></font>
        </core:IfNot>
      </td>
    </tr>

 
    <tr bgcolor="#dddddd">
      <td><font class="smaller"><i18n:message key="role-enabled-title" /></font></td>
      <td>
        <core:If value='<%= ((String)gearEnv.getGearInstanceParameter("roleEnabled")) %>'>
          <font class="smaller"><i18n:message key="constant-true"/></font>
        </core:If>
        <core:IfNot value='<%= ((String)gearEnv.getGearInstanceParameter("roleEnabled")) %>'>
          <font class="smaller"><i18n:message key="constant-false"/></font>
        </core:IfNot>
      </td>
    </tr>

 
    <tr bgcolor="#ffffff">
      <td><font class="smaller"><i18n:message key="organization-enabled-title" /></font></td>
      <td>
        <core:If value='<%= ((String)gearEnv.getGearInstanceParameter("organizationEnabled")) %>'>
          <font class="smaller"><i18n:message key="constant-true"/></font>
        </core:If>
        <core:IfNot value='<%= ((String)gearEnv.getGearInstanceParameter("organizationEnabled")) %>'>
          <font class="smaller"><i18n:message key="constant-false"/></font>
        </core:IfNot>
      </td>
    </tr>

 
    <tr bgcolor="#dddddd">
      <td><font class="smaller"><i18n:message key="user-enabled-title" /></font></td>
      <td>
        <core:If value='<%= ((String)gearEnv.getGearInstanceParameter("userEnabled")) %>'>
          <font class="smaller"><i18n:message key="constant-true"/></font>
        </core:If>
        <core:IfNot value='<%= ((String)gearEnv.getGearInstanceParameter("userEnabled")) %>'>
          <font class="smaller"><i18n:message key="constant-false"/></font>
        </core:IfNot>
      </td>
    </tr>
 	 <tr><td bgcolor="#666666" colspan="2"><img src='<%=clearGif%>' height="1" width="1" border="0"></td></tr> 
  </table>
 

        <core:CreateUrl id="editAccessUrl" url="<%= origURI %>">
          <core:UrlParam param="paf_dm" value="full"/>
          <core:UrlParam param="paf_gm" value="<%=  gearEnv.getGearMode() %>"/>
          <core:UrlParam param="config_page" value="accessParams"/>
          <core:UrlParam param="paf_gear_id" value="<%= gearId %>"/>
          <core:UrlParam param="paf_page_id" value="<%= pageId %>"/>
          <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
          <core:UrlParam param="paf_community_id" value="<%= communityId %>"/>
          <a href="<%= editAccessUrl.getNewUrl() %>"><font class="smaller"><i18n:message key="config-access-param-link" /></font></a>
        </core:CreateUrl>
  

 
 </td></tr></table>
<br><br>
</paf:hasCommunityRole>
</paf:InitializeGearEnvironment>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/alert/alert.war/html/admin/displayInstanceConfig.jsp#1 $$Change: 651360 $--%>
