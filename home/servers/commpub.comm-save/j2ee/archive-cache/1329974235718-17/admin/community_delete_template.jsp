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

<core:demarcateTransaction id="demarcateXA">

<%
  String dsp_page_url = null;
  String dsp_community_template_id = null;
  String deleteTemplateURL = null;
%>

<dsp:getvalueof id="dsp_page_url_gv" idtype="java.lang.String" param="paf_page_url">
  <% dsp_page_url = dsp_page_url_gv; %>
</dsp:getvalueof>
<dsp:getvalueof id="dsp_community_template_id_gv" idtype="java.lang.String" 
                param="paf_community_template_id">
  <% dsp_community_template_id = dsp_community_template_id_gv; %>
</dsp:getvalueof>


<core:createUrl id="deleteTemplateFormURL" url="community.jsp">
  <core:urlParam param="mode" value="24"/>
  <% deleteTemplateURL = deleteTemplateFormURL.getNewUrl(); %>
</core:createUrl>

<dsp:form action="<%= deleteTemplateURL %>" method="POST" name="deletetemplate">

<%-- hidden fields --%>
<core:createUrl id="formFailureURL" url="<%= dsp_page_url %>">
  <core:urlParam param="mode" value="24"/>
  <core:urlParam param="paf_community_template_id" value="<%= dsp_community_template_id %>"/>
  <core:urlParam param="paf_page_url" value="<%= dsp_page_url %>"/>
  <dsp:input type="hidden" bean="CommunityTemplateFormHandler.failureURL"
             value="<%= formFailureURL.getNewUrl() %>"/>
</core:createUrl>
<core:createUrl id="formSuccessURL" url="<%= dsp_page_url %>">
  <core:urlParam param="mode" value="20"/>
  <core:urlParam param="paf_page_url" value="<%= dsp_page_url %>"/>
  <dsp:input type="hidden" bean="CommunityTemplateFormHandler.successURL"
             value="<%= formSuccessURL.getNewUrl() %>"/>
</core:createUrl>
<core:createUrl id="formCancelURL" url="<%= dsp_page_url %>">
  <core:urlParam param="mode" value="20"/>
  <core:urlParam param="paf_page_url" value="<%= dsp_page_url %>"/>
  <dsp:input type="hidden" bean="CommunityTemplateFormHandler.cancelURL"
             value="<%= formCancelURL.getNewUrl() %>"/>
</core:createUrl>
<dsp:input type="hidden" bean="CommunityTemplateFormHandler.communityTemplateId"
           value="<%= dsp_community_template_id %>"/>
		   
		   
<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="pageheader_edit">
 <i18n:message key="admin-community-template-delete-header"/>
</td></tr></table>
</td></tr></table>


<img src='<%= response.encodeURL("images/clear.gif")%>' height="1" width="1" border="0"><br>

<table bgcolor="#BAD8EC" width="100%" cellpadding=4 cellspacing=0 border=0>
<tr>
  <td><font class="smaller">
    <i18n:message key="admin-community-template-delete-subheader">
      <i18n:messageArg value="<%= adminEnv.getCommunityTemplate().getName() %>"/>
    </i18n:message>
  </td>
</tr>
<tr>
  <td><br>
    <i18n:message id="deleteYes" key="yes"/>
    <dsp:input type="submit" bean="CommunityTemplateFormHandler.deleteCommunityTemplate"
               value="<%= deleteYes %>" name="YesTemplateDelete"/>&nbsp;&nbsp;&nbsp;

    <i18n:message id="deleteNo" key="no"/>
    <dsp:input type="submit" bean="CommunityTemplateFormHandler.cancel" 
               value="<%= deleteNo %>" name="NoTemplateDelete"/><br><br>
  </td>
</tr>
</table>

</dsp:form>

</core:demarcateTransaction>

</admin:InitializeAdminEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/community_delete_template.jsp#2 $$Change: 651448 $--%>
