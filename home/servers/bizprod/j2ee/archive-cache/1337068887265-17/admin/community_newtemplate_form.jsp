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

<dsp:importbean bean="/atg/portal/admin/CommunityTemplateFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

<core:demarcateTransaction id="demarcateXA">
<%
    String clearGif =  response.encodeURL("images/clear.gif");
%>
<dsp:form action="/portal/admin/community.jsp" method="POST" name="newtemplate">


<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="CommunityTemplateFormHandler.formError"/>
  <dsp:oparam name="true">
    <%--     reset the session scoped form handler --%>
    <dsp:setvalue bean="CommunityTemplateFormHandler.reset"/>
  </dsp:oparam>        
</dsp:droplet>

<%-- Reset form exception for the session scoped CommunityFormHandler --%>
<dsp:setvalue bean="CommunityTemplateFormHandler.resetFormExceptions"/>


<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="pageheader_edit">
<img src='<%= response.encodeURL("images/write.gif")%>' height="15" width="28" alt="" border="0">
	<i18n:message key="admin-community-template-create-header">
	<i18n:messageArg value="<%= adminEnv.getCommunity().getName(response.getLocale()) %>"/>
	</i18n:message>
</td></tr></table>
</td></tr></table>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
<font class="smaller"><i18n:message key="admin-community-template-create-helpertext"/>
</td></tr></table>

<img src="<%=clearGif%>" height="1" width="7" border="0"><br> 


<%
  Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
  String dsp_community_id = "";
  if(cm != null) {
    dsp_community_id = cm.getId();
  }
%> 
<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String" param="paf_page_url">

<%-- hidden form fields --%>
<core:createUrl id="formFailureURL" url="<%= dsp_page_url %>">
  <core:urlParam param="mode" value="22"/>
  <core:urlParam param="paf_page_url" value="<%= dsp_page_url %>"/>
  <dsp:input type="hidden" bean="CommunityTemplateFormHandler.failureURL" 
             value="<%= portalServletResponse.encodePortalURL(formFailureURL.getNewUrl(),portalContext) %>"/>
</core:createUrl>
<core:createUrl id="formSuccessURL" url="<%= dsp_page_url %>">
  <core:urlParam param="mode" value="20"/>
  <core:urlParam param="paf_page_url" value="<%= dsp_page_url %>"/>
  <dsp:input type="hidden" bean="CommunityTemplateFormHandler.successURL" 
             value="<%= formSuccessURL.getNewUrl() %>"/>
</core:createUrl>
<dsp:input type="hidden" bean="CommunityTemplateFormHandler.communityId"
           value="<%= dsp_community_id %>"/>

<%-- visible form fields --%>
 <table cellpadding="2" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<table cellSpacing=0 cellPadding=2 border=0>
  <tr>
    <td align=left valign=top width="150">
      <font class="smaller"><i18n:message key="admin-community-template-create-name-prompt"/><br>
    </td>
    <td>
      <dsp:input type="text" bean="CommunityTemplateFormHandler.templateName" 
                 required="<%= true %>" value=""/>
    </td>
  </tr>
  <tr>
    <td></td>
    <td>
      <i18n:message id="done1" key="save" />
      <dsp:input type="submit" bean="CommunityTemplateFormHandler.createCommunityTemplate" 
                 value="<%= done1 %>" submitvalue="success" name="create-community"/>
    </td>
  </tr>
</table>
</td></tr></table>

</dsp:getvalueof> <%-- page url --%>

</dsp:form>

</core:demarcateTransaction>

</admin:InitializeAdminEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/community_newtemplate_form.jsp#2 $$Change: 651448 $--%>
