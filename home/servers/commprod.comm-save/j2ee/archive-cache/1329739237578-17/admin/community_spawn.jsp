<%@ page import="java.io.*,java.util.*,atg.repository.RepositoryItem"%>

<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>

<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/portal/admin/CommunityTemplateFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String" param="paf_page_url">
<dsp:getvalueof id="dsp_community_template_id" idtype="java.lang.String" 
                param="paf_community_template_id">

<core:demarcateTransaction id="demarcateXA">

<dsp:form action="community.jsp" method="POST" name="spawncommunity">

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="CommunityTemplateFormHandler.formError"/>
  <dsp:oparam name="false">
     <%--     reset the session scoped form handler --%>
     <dsp:setvalue bean="CommunityTemplateFormHandler.reset"/>
  </dsp:oparam>        
</dsp:droplet>

<%-- Reset form exception for the session scoped CommunityFormHandler --%>
<dsp:setvalue bean="CommunityTemplateFormHandler.resetFormExceptions"/>


<%-- hidden form fields --%>
<core:createUrl id="formFailureURL" url="<%= dsp_page_url %>">
  <core:urlParam param="mode" value="23"/>
  <core:urlParam param="paf_community_template_id" 
                 value="<%= dsp_community_template_id %>"/>
  <core:urlParam param="paf_page_url" value="<%= dsp_page_url %>"/>
  <dsp:input type="hidden" bean="CommunityTemplateFormHandler.failureURL" 
             value="<%= formFailureURL.getNewUrl() %>"/>
</core:createUrl>
<core:createUrl id="formSuccessURL" url="<%= dsp_page_url %>">
  <core:urlParam param="mode" value="1"/>
  <core:urlParam param="paf_page_url" value="<%= dsp_page_url %>"/>
  <dsp:input type="hidden" bean="CommunityTemplateFormHandler.successURL" 
             value="<%= formSuccessURL.getNewUrl() %>"/>
</core:createUrl>
<dsp:input type="hidden" bean="CommunityTemplateFormHandler.communityTemplateId"
           value="<%= dsp_community_template_id %>"/>

<%-- visible form fields --%>


<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="pageheader_edit">
<img src='<%= response.encodeURL("images/write.gif")%>' height="15" width="28" alt="" border="0"><i18n:message key="admin-community-spawn-header">
<i18n:messageArg value="<%= adminEnv.getCommunityTemplate().getName() %>"/>
</i18n:message>
</td></tr></table>
</td></tr></table>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
<font class="smaller"><i18n:message key="admin-community-spawn-helpertext"/>
</td></tr></table>
<img src='<%= response.encodeURL("images/clear.gif")%>' height="1" width="1" border="0"><br>
<table bgcolor="#BAD8EC" width="100%" cellpadding=2 cellspacing=0 border=0><tr><td>
<table cellpadding="2" cellspacing="0" border="0">
<tr>
  <td>
    <font class="smaller"><i18n:message key="admin-community-spawn-select-folder"/>
  </td>
  <td>
    &nbsp;
  </td>
</tr>
<tr>
  <td>

<dsp:setvalue bean="CommunityTemplateFormHandler.communityFolderId"
              value="<%= adminEnv.getCommunityTemplate().getParentFolder().getId() %>"/>

<%
   String folderName = "";
   String folderId = "";
   int    level, pos=0, i=0;
   String spacers = "";
   String clearGif = response.encodeURL("images/clear.gif");
%>

    <admin:GetCommunityLevels id="items">
      <font class="smaller">
      <dsp:select bean="CommunityTemplateFormHandler.communityFolderId">

        <core:ForEach id="allCommFolders"
                      values="<%= items.getItemAndLevel() %>"
		      castClass="atg.portal.admin.ItemAndLevel"
		      elementId="CommFolderItem">
<%
 folderName   = (String) ((RepositoryItem) CommFolderItem.getItem()).getPropertyValue("name");
 folderId     = (String) ((RepositoryItem) CommFolderItem.getItem()).getRepositoryId();
 level        = CommFolderItem.getLevel();
 spacers      = "";

 for(i=1; i<level;i++) {
  spacers += "&nbsp;&nbsp;";
 }
%>

          <core:exclusiveIf>
	    <core:ifEqual object1="<%= folderId %>" 
	                  object2="<%= adminEnv.getCommunityTemplate().getParentFolder().getId() %>">
	      <dsp:option value="<%= folderId %>" selected="<%=true%>"/><%=spacers%><%= folderName %>
	    </core:ifEqual>
	    <core:defaultCase>
	      <dsp:option value="<%= folderId %>" selected="<%=false%>"/><%=spacers%><%= folderName %>
	    </core:defaultCase>
	  </core:exclusiveIf>
        </core:ForEach>
      </dsp:select>
    </admin:GetCommunityLevels>
  </td>
  <td>&nbsp;</td>
</tr>


 <%-- removing create folder link
<tr>
  <td>
    link to create new community folder 
    <core:createUrl id="createFolderUrl" url="/portal/admin/community.jsp">
      <core:urlParam param="mode" value="3"/>
      <core:urlParam param="paf_page_url" value="<%= dsp_page_url %>"/>
      <dsp:a href="<%= createFolderUrl.getNewUrl() %>"><i18n:message key="admin-community-spawn-create-folder"/></dsp:a>
    </core:createUrl>
  </td>
</tr>--%>


<tr>
  <td><font class="smaller">
    <i18n:message key="admin-community-spawn-name"/>
  </td>
</tr>
<tr>
  <td>
    <dsp:input bean="CommunityTemplateFormHandler.communityName"/>
  </td>
</tr>
<tr>
  <td>
    <font class="smaller"><i18n:message key="admin-community-spawn-url"/>
  </td>
</tr>
<tr>
  <td>
    <dsp:input bean="CommunityTemplateFormHandler.URLName"/>&nbsp;&nbsp;<font class="smaller"><i18n:message key="admin-community-spawn-url-example"/>
  </td>
</tr>

<%-- status --%>
<tr>
  <td><font class="smaller">
    <i18n:message key="community_settings_status_title"/><br>
    <dsp:setvalue bean="CommunityTemplateFormHandler.active"
                  value="<%= new Boolean(adminEnv.getCommunityTemplate().isEnabled()) %>"/>
    <dsp:select bean="CommunityTemplateFormHandler.active">
	  <dsp:option value="true" /><i18n:message key="community_settings_status_online"/>
          <dsp:option value="false" /><i18n:message key="community_settings_status_offline"/>
    
      <%--
      <core:exclusiveIf>
        <core:If value="<%= adminEnv.getCommunityTemplate().isEnabled() %>">
	  <dsp:option value="true" selected="true" /><i18n:message key="community_settings_status_online"/>
          <dsp:option value="false" /><i18n:message key="community_settings_status_offline"/>
        </core:If>
	<core:defaultCase>
	  <dsp:option value="true" /><i18n:message key="community_settings_status_online"/>
          <dsp:option value="false" selected="true"/><i18n:message key="community_settings_status_offline"/>
	</core:defaultCase>
      </core:exclusiveIf>
      --%>
    </dsp:select>
  </td>
</tr>

<%-- description --%>
<tr>
  <td><font class="smaller">
    <i18n:message key="community_settings_description"/><br>
    <dsp:textarea bean="CommunityTemplateFormHandler.communityDescription" cols="38" rows="3" />
  </td>
</tr>

<%-- membership --%>
<tr>
  <dsp:setvalue bean="CommunityTemplateFormHandler.membershipRequest" 
                value="<%= adminEnv.getCommunityTemplate().getMembershipRequest() %>"/>

  <td>
    <font class="smaller"><i18n:message key="community_settings_requestmembership_title"/></font><br>
      <table cellpadding="0" cellspacing="0" border="0">
        <tr>
          <td><img src="<%=clearGif%>" width="20" height="1"></td>
          <td><img src="<%=clearGif%>" width="20" height="1"></td>
        </tr>
        <tr>
          <td><dsp:input type="radio" bean="CommunityTemplateFormHandler.membershipRequest" value="automatic"/></td>
          <td nowrap><font class="adminbody"><i18n:message key="community_settings_requestmembership_autoexcept"/></font></td>
        </tr>
        <tr>
          <td><dsp:input type="radio" bean="CommunityTemplateFormHandler.membershipRequest" value="allow"/></td>
          <td nowrap><font class="adminbody"><i18n:message key="community_settings_requestmembership_notify"/></font></td>
        </tr>
        <tr>
          <td><dsp:input type="radio" bean="CommunityTemplateFormHandler.membershipRequest" value="disallow" /></td>
          <td nowrap><font class="adminbody"><i18n:message key="community_settings_requestmembership_noexcept"/></font></td>
        </tr>
      </table>
    </font>
  </td>
</tr>

<admin:GetAllItems id="items">

<tr>
  <td>

<%-- page template --%>

    <font class="smaller"><i18n:message key="community_settings_pagetemplate_title"/></font><br>
    <font class="small">
    <dsp:setvalue bean="CommunityTemplateFormHandler.pageTemplateId"
                  value="<%= adminEnv.getCommunityTemplate().getPageTemplate().getId() %>"/>
    <dsp:select bean="CommunityTemplateFormHandler.pageTemplateId">
      <core:ForEach id="allTemplates"
              values="<%= items.getPageTemplates(atg.portal.framework.Comparators.getPageTemplateComparator()) %>"
              castClass="atg.portal.framework.PageTemplate"
              elementId="templateItem">
        <dsp:option value="<%=  templateItem.getId() %>"/>
	<%=  templateItem.getName() %>
      </core:ForEach>
    </dsp:select>
    </font><br>
    <img src="<%=clearGif%>" border="0" height="6" width="1"><br>


<%-- style --%>

    <font class="smaller"><i18n:message key="community_settings_style_title"/></font><br>
    <font class="small">
    <dsp:setvalue bean="CommunityTemplateFormHandler.styleId"
                  value="<%= adminEnv.getCommunityTemplate().getStyle().getId() %>"/>
    <dsp:select bean="CommunityTemplateFormHandler.styleId">
      <% boolean noStyle = false; %>

      <dsp:getvalueof id="crntStyle" bean="CommunityTemplateFormHandler.styleId">
      <core:ifNull value="<%= crntStyle %>">
        <% noStyle = true; %>  
      </core:ifNull>

      <core:ForEach id="allStyles"
            values="<%= items.getStyles(atg.portal.framework.Comparators.getStyleComparator()) %>"
            castClass="atg.portal.framework.Style"
            elementId="styleItem">
        <dsp:option value="<%= styleItem.getId() %>"/><%= styleItem.getName() %>
      </core:ForEach>
      </dsp:getvalueof>

    </dsp:select>
    </font>
    <br>


<%-- gear template --%>

    <img src="<%=clearGif%>" border="0" height="6" width="1"><br>
    <font class="smaller"><i18n:message key="community_settings_geartemplate_title"/></font><br>
    <font class="small">
    <dsp:setvalue bean="CommunityTemplateFormHandler.gearTemplateId"
                  value="<%= adminEnv.getCommunityTemplate().getGearTitleTemplate().getId() %>"/>
    <dsp:select bean="CommunityTemplateFormHandler.gearTemplateId">
      <core:ForEach id="allTemplates"
            values="<%= items.getGearTitleTemplates(atg.portal.framework.Comparators.getGearTitleTemplateComparator()) %>"
            castClass="atg.portal.framework.GearTitleTemplate"
            elementId="templateItem">
        <dsp:option value="<%=  templateItem.getId() %>"/>
	<%=  templateItem.getName() %>
      </core:ForEach>
    </dsp:select>
    </font>
  </td>
</tr>

</admin:GetAllItems>

<%-- customization --%>
<tr>
  <dsp:setvalue bean="CommunityTemplateFormHandler.personalization"
                value="<%= adminEnv.getCommunityTemplate().getPersonalization() %>"/>
  <dsp:getvalueof id="customizeLevel" bean="CommunityTemplateFormHandler.personalization">
    <core:IfNull value="<%=customizeLevel%>">
      <dsp:setvalue bean="CommunityTemplateFormHandler.personalization" value="none"/>
    </core:IfNull>
  </dsp:getvalueof>

  <td NOWRAP>
    <font class="adminbody"><i18n:message key="community_settings_personalization_title"/><br>
    &nbsp;<dsp:input type="radio" 
	             bean="CommunityTemplateFormHandler.personalization" value="community"/>
    <i18n:message key="community_settings_personalization_membersandcreate"/>
    <br>
    &nbsp;<dsp:input type="radio" 
                     bean="CommunityTemplateFormHandler.personalization" value="page"/>
    <i18n:message key="community_settings_personalization_membersnocreate"/>
    <br>
    &nbsp;<dsp:input type="radio" 
                     bean="CommunityTemplateFormHandler.personalization" value="none"/>
    <i18n:message key="community_settings_personalization_noallow"/><br>
    <br>
    </font>
  </td>
</tr>

<%-- access control --%>
<tr>
  <dsp:setvalue bean="CommunityTemplateFormHandler.accessLevel"
                value="<%= new Integer(adminEnv.getCommunityTemplate().getAccessLevel()) %>"/>
  <dsp:getvalueof id="accessLevel" bean="CommunityTemplateFormHandler.accessLevel">
    <core:IfNull value="<%=accessLevel%>">
      <dsp:setvalue bean="CommunityTemplateFormHandler.accessLevel" value="4"/>
    </core:IfNull>
  </dsp:getvalueof>
  <td>
    <font class="adminbody">
    <i18n:message key="community_settings_access_title"/><br>
    &nbsp;<dsp:input type="radio" bean="CommunityTemplateFormHandler.accessLevel" value="0" />
    <i18n:message key="community_settings_access_any"/>
    <br>
    &nbsp;<dsp:input type="radio" bean="CommunityTemplateFormHandler.accessLevel" value="1"/>
    <i18n:message key="community_settings_access_all"/>
    <br>
    &nbsp;<dsp:input type="radio" bean="CommunityTemplateFormHandler.accessLevel" value="2"/>
    <i18n:message key="community_settings_access_guestandmeberandleader"/>
    <br>
    &nbsp;<dsp:input type="radio" bean="CommunityTemplateFormHandler.accessLevel" value="3"/>
    <i18n:message key="community_settings_access_memberandleader"/>
    <br>
    &nbsp;<dsp:input type="radio" bean="CommunityTemplateFormHandler.accessLevel" value="4"/>
    <i18n:message key="community_settings_access_leader"/><br>
    <br>
    </font>
  </td>
</tr>

<tr>
  <dsp:setvalue bean="CommunityTemplateFormHandler.cloneSharedGears"
                value="<%= new Boolean(false) %>"/>
  <td>
    <font class="adminbody">
    <i18n:message key="community_template_cloneSharedGears_title"/><br>
    &nbsp;<dsp:input type="radio" bean="CommunityTemplateFormHandler.cloneSharedGears" value="<%= new Boolean(false) %>" />
    <i18n:message key="community_template_cloneSharedGears_false"/>
    <br>
    &nbsp;<dsp:input type="radio" bean="CommunityTemplateFormHandler.cloneSharedGears" value="<%= new Boolean(true) %>"/>
    <i18n:message key="community_template_cloneSharedGears_true"/>
    <br>
    </font>
  </td>
</tr>

<tr>
  <td>
    <i18n:message id="done2" key="save"/>
    <dsp:input type="submit" bean="CommunityTemplateFormHandler.spawnCommunity"
               value="<%= done2 %>" submitvalue="success" name="spawn-community"/>
  </td>
</tr>
</table>

</td></tr></table>
<br>

</dsp:form>

</core:demarcateTransaction>

</dsp:getvalueof> <%-- dsp_community_template_id --%>
</dsp:getvalueof> <%-- dsp_page_url --%>
</admin:InitializeAdminEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/community_spawn.jsp#2 $$Change: 651448 $--%>
