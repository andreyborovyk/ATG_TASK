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

<core:demarcateTransaction id="demarcateXA">

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="CommunityTemplateFormHandler.formError"/>
  <dsp:oparam name="false">
    <%--     reset the session scoped form handler --%>
    <dsp:setvalue bean="CommunityTemplateFormHandler.reset"/>
  </dsp:oparam>        
</dsp:droplet>

<%-- Reset form exceptions for the session scoped CommunityFormHandler --%>
<dsp:setvalue bean="CommunityTemplateFormHandler.resetFormExceptions"/>

<%
    String clearGif =  response.encodeURL("images/clear.gif");
%>


    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
	<font class="pageheader"><i18n:message key="admin-community-template-list-header"/>
	</td></tr></table>
	</td></tr></table>


<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
<font class="smaller">
<core:exclusiveIf>
<core:ifNull value="<%= adminEnv.getCommunityTemplates() %>">
	<i18n:message key="admin-community-template-no-templates"/>
</core:ifNull>
<core:defaultCase>
	<i18n:message key="admin-community-template-list-subheader"/>
</core:defaultCase>
</core:exclusiveIf>
</td></tr></table>

<img src="<%=clearGif%>" height="1" width="7" border="0"><br> 
 




<core:exclusiveIf>
<core:ifNull value="<%= adminEnv.getCommunityTemplates() %>">
<!--moved message up above-->
</core:ifNull>

<core:defaultCase>
<table cellpadding="2" cellspacing="0" border="0" bgcolor="#c9d1d7" width="100%"><tr><td>
<table cellpadding="2" cellspacing="0" border="0">
<core:forEach id="templatesForEach"
              values="<%= adminEnv.getCommunityTemplates() %>"
	      castClass="atg.portal.framework.CommunityTemplate"
	      elementId="communityTemplate">

  <tr>
    <td nowrap width="200"><font class="small"><%= communityTemplate.getName() %></font></td>
    <td width="90%" nowrap>
      <font class="smaller">
      <core:createUrl id="spawnCommunityUrl" url="/portal/admin/community.jsp">
        <core:urlParam param="mode" value="23"/>
	<core:urlParam param="paf_community_template_id" value="<%= communityTemplate.getId() %>"/>
	<core:urlParam param="paf_page_url" value="/portal/admin/community.jsp"/>
	
	<dsp:a href="<%= spawnCommunityUrl.getNewUrl() %>"><i18n:message key="admin-community-template-spawn"/></dsp:a>
      </core:createUrl>
      </font>
    &nbsp;&nbsp;&nbsp;
      <core:createUrl id="deleteCommunityTemplateUrl" url="/portal/admin/community.jsp">
        <core:urlParam param="mode" value="24"/>
	<core:urlParam param="paf_community_template_id" value="<%= communityTemplate.getId() %>"/>
	<core:urlParam param="paf_page_url" value="/portal/admin/community.jsp"/>
	
	<font class="smaller"><dsp:a href="<%= deleteCommunityTemplateUrl.getNewUrl() %>"><i18n:message key="admin-community-template-delete"/></dsp:a></font>
      </core:createUrl>
    &nbsp;&nbsp;&nbsp;
      <core:createUrl id="exportCommunityTemplateUrl" 
                      url="/portal/admin/download">
        <core:urlParam param="mode" value="24"/>
	<core:urlParam param="paf_community_template_id" value="<%= communityTemplate.getId() %>"/>
	<core:urlParam param="paf_page_url" value="/portal/admin/community.jsp"/>
	
	<font class="smaller"><dsp:a href="<%= exportCommunityTemplateUrl.getNewUrl() %>"><i18n:message key="admin-community-template-export"/></dsp:a></font>
      </core:createUrl>
    </td>
  </tr>  

</core:forEach>
</table>
</td></tr></table>
</core:defaultCase>
</core:exclusiveIf>




</core:demarcateTransaction>

</admin:InitializeAdminEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/community_templates.jsp#2 $$Change: 651448 $--%>
