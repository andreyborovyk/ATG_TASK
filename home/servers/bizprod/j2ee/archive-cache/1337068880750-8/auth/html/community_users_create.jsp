<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ page import="java.io.*,java.util.*" %>

<paf:setFrameworkLocale />

<dsp:page>

<i18n:bundle baseName="atg.portal.authentication.MembershipCaptureResources" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:bundle baseName="atg.userprofiling.UserProfileTemplateResources" id="userBundle" changeResponseLocale="false" />


<core:demarcateTransaction id="demarcateXA">
<% try { %>

<html>
<head>
<title></title>

<link rel="STYLESHEET" type="text/css" href="/portal/settings/css/default.css" src="/portal/settings/css/default.css">
</head>
<body bgcolor="#ffffff" topmargin="0" marginheight="0" leftmargin="0" marginwidth="0">



<i18n:message key="imageroot" id="i18n_imageroot"/>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileAdminFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<%-- FORM  ELEMENTS HERE  --%>

<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">
<dsp:getvalueof id="dsp_page_url"     idtype="java.lang.String" param="paf_page_url">
<dsp:getvalueof id="mode"             idtype="java.lang.String" param="mode">

<% 
 String errorGif = "<img src='"+response.encodeURL("../images/error.gif")+"' border='0'>"; 
 String clearGif = "<img src='"+response.encodeURL("../images/clear.gif")+"' border='0'>"; 

 String targetStyleTargets[] = {"firstName","lastName","login","email","password","confirmpassword"};
 Map targetStyleClass = new HashMap();
 for ( int i = 0 ; i < targetStyleTargets.length ; i++ ) {
   targetStyleClass.put(targetStyleTargets[i],"adminbody");
 }
%>
<dsp:droplet name="Switch">
<dsp:param bean="ProfileAdminFormHandler.formError" name="value"/>
<dsp:oparam name="true">
  <font class="error">
    <dsp:droplet name="ProfileErrorMessageForEach">
      <dsp:param bean="ProfileAdminFormHandler.formExceptions" name="exceptions"/>
      <dsp:oparam name="output">
	<img src='<%=response.encodeURL("/portal/settings/images/error.gif")%>'>&nbsp;<dsp:valueof param="message"/><br>
      </dsp:oparam>
    </dsp:droplet>
    <br />
</dsp:oparam>
</dsp:droplet>
  	<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
<font class="pageheader"> 
<i18n:message key="form_title"/></font>
</td></tr></table>
</td></tr></table>


<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
<font class="smaller"><i18n:message key="create_user_helper_text"/>
<br><superscript>*</superscript><i18n:message key="community_members_create_required"/>
</td></tr></table>


<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="98%"><tr><td>
<font class="smaller">
<img src="<%= response.encodeURL("images/clear.gif")%>" height="5" width="1" border="0"><br>
<table border=0 cellpadding=0 cellspacing=0>
<dsp:form action="community_users_create.jsp" method="POST">

<core:CreateUrl  id="successURL" url="cuCreateSuccess.jsp">
  <core:UrlParam param="mode" value="<%=mode%>" />
  <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
  <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
  <dsp:input bean="ProfileAdminFormHandler.createSuccessURL" type="HIDDEN" value="<%=successURL.getNewUrl()%>"/>
</core:CreateUrl> 

<core:CreateUrl  id="errorURL" url="community_users_create.jsp">
  <core:UrlParam param="mode" value="<%=mode%>" />
  <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
  <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
  <dsp:input bean="ProfileAdminFormHandler.createErrorURL" type="HIDDEN" value="<%=errorURL.getNewUrl()%>"/>
</core:CreateUrl> 

<core:CreateUrl  id="cancelURL" url="cuCreateCanceled.jsp">
  <core:UrlParam param="mode" value="<%=mode%>" />
  <core:UrlParam param="paf_community_id" value="<%=dsp_community_id%>"/>
  <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
  <dsp:input bean="ProfileAdminFormHandler.cancelURL" type="HIDDEN" value="<%=cancelURL.getNewUrl()%>"/>
</core:CreateUrl> 

<dsp:input bean="ProfileAdminFormHandler.communityId" type="HIDDEN" value="<%=dsp_community_id%>"/>
<dsp:input bean="ProfileAdminFormHandler.confirmPassword"  type="hidden" value="true" />

<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_firstname"/>*</font></td>
<td><dsp:input bean="ProfileAdminFormHandler.value.firstName" name="testie" size="24" type="TEXT"  required="<%=true%>" />&nbsp;&nbsp;&nbsp;</td>
</tr>
<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_lastname"/>*</font></td>
<td><dsp:input bean="ProfileAdminFormHandler.value.lastName"  size="24" type="TEXT"  required="<%=true%>" /></td>
</tr>
<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_email"/>*</font></td>
<td><dsp:input bean="ProfileAdminFormHandler.value.email"  size="24" type="TEXT"  required="<%=true%>" /></td>
</tr>
<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_username"/>*</font></td>
<td><dsp:input bean="ProfileAdminFormHandler.value.login"  size="20" type="TEXT"  required="<%=true%>" /></td>
</tr>
<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_password"/>*</font></td>
<td><dsp:input bean="ProfileAdminFormHandler.value.password"  size="20" type="PASSWORD"  required="<%=true%>" /></td>
</tr>
<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_password_confirm"/>*</font></td>
<td><dsp:input bean="ProfileAdminFormHandler.value.confirmpassword"  size="20" type="PASSWORD" required="<%=true%>" /></td>
</tr>
<tr>

<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_role"/></font></td>
<td>
<dsp:select bean="ProfileAdminFormHandler.userType">
  <dsp:option value="guest"/><i18n:message key="field_access_guest"/>
  <dsp:option value="member"/><i18n:message key="field_access_member"/>
  <dsp:option value="leader"/><i18n:message key="field_access_leader"/>
</dsp:select>

</td>
</tr>


<dsp:droplet name="IsNull">
  <dsp:param bean="ProfileAdminFormHandler.organizations" name="value"/>
  <dsp:oparam name="false">
    <tr>
    <td NOWRAP><font class="adminbody"><i18n:message key="field_label_organization"/></font></td>
    <td>
    <dsp:select bean="ProfileAdminFormHandler.organization">
      <dsp:option value=""/><i18n:message key="field_organization_defaultnone"/>

 <dsp:droplet name="ForEach">
        <dsp:param bean="ProfileAdminFormHandler.organizations" name="array"/>
         <dsp:oparam name="output">
           <dsp:getvalueof id="elementRepositoryId" idtype="java.lang.String" param="element.repositoryId">
             <dsp:option value="<%= elementRepositoryId %>"/>
           </dsp:getvalueof>
           <dsp:valueof param="element.name"/>
         </dsp:oparam>
      </dsp:droplet>
    </dsp:select>
  </dsp:oparam>
 </td>
 </tr>
</dsp:droplet>

<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_gender"/></font></td>
<td>
<i18n:message key="genderMale" id="maleOptionValue" bundle="<%= userBundle%>"/>
<i18n:message key="genderFemale" id="femaleOptionValue" bundle="<%= userBundle%>"/>

<dsp:select bean="ProfileAdminFormHandler.value.gender">
   <dsp:option value="<%= maleOptionValue%>"/><i18n:message key="field_label_male"/>
   <dsp:option value="<%= femaleOptionValue%>"/><i18n:message key="field_label_female"/>
</dsp:select>
</td>
</tr>

<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_add1"/></font></td>
<td>
<dsp:input bean="ProfileAdminFormHandler.value.homeAddress.address1"  size="20" type="ADDR1" />
</td>
</tr>

<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_add2"/></font></td>
<td>
<dsp:input bean="ProfileAdminFormHandler.value.homeAddress.address2"  size="20" type="ADDR2"/>
</td>
</tr>

<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_city"/></font></td>
<td>
<dsp:input bean="ProfileAdminFormHandler.value.homeAddress.city"  size="20" type="CITY" />
</td>
</tr>

<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_state"/></font></td>
<td>
<dsp:input bean="ProfileAdminFormHandler.value.homeAddress.state"  size="20" type="STATE"/>
</td>
</tr>

<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_zip"/></font></td>
<td>
<dsp:input bean="ProfileAdminFormHandler.value.homeAddress.postalCode"  size="20" type="POSTALCODE"/>
</td>
</tr>
<tr>

<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_country"/></font></td>
<td>
<dsp:input bean="ProfileAdminFormHandler.value.homeAddress.country"  size="20" type="COUNTRY" />
</td>
</tr>

<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_phone"/></font></td>
<td>
<dsp:input bean="ProfileAdminFormHandler.value.homeAddress.phoneNumber"  size="20" type="PHONE" />
</td>
</tr>


<tr>
<td NOWRAP><font class="adminbody"><i18n:message key="field_label_fax"/></font></td>
<td>
<dsp:input bean="ProfileAdminFormHandler.value.homeAddress.faxNumber"  size="20" type="FAX"/>
</td>
</tr>


<tr>
<td colspan="2">

<img src="images/line_dashed_hori_180_3.gif" width="180" height="3" border="0">
<br> 
<%-- SUBMIT BUTTONS HERE --%>
<font class="small">
<i18n:message id="done01" key="done"/>
<i18n:message id="reset01" key="reset"/>
<i18n:message id="cancel01" key="cancel"/>
<dsp:input type="SUBMIT" bean="ProfileAdminFormHandler.create" value="<%=done01%>" />&nbsp;&nbsp;
<input type="RESET"  value="<%=reset01%>" />&nbsp;&nbsp;
<%--<dsp:input type="SUBMIT" bean="ProfileAdminFormHandler.cancel" value="<%=cancel01%>" />--%>
</font>
<br><br>

</td>
</tr>
</dsp:form>
</table>

</td></tr></table>
<br>

</dsp:getvalueof><%-- mode             --%>
</dsp:getvalueof><%-- paf_page_url     --%>
</dsp:getvalueof><%-- paf_community_id --%>

</body>
</html>

  <% } catch (Exception e) { %>
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

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/html/community_users_create.jsp#2 $$Change: 651448 $--%>
