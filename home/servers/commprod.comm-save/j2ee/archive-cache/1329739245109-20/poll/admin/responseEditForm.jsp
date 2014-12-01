<%@ taglib uri="/poll-taglib" prefix="poll" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<i18n:bundle baseName="atg.gear.poll.PollResources" localeAttribute="userLocale" changeResponseLocale="false" />
<paf:InitializeGearEnvironment id="pafEnv">

<dsp:importbean bean="/atg/portal/gear/poll/ResponseFormHandler"/>
<%
   String origURI=pafEnv.getOriginalRequestURI();
   String gearID=pafEnv.getGear().getId();
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String successURL = request.getParameter("paf_success_url");
   String communityID = request.getParameter("paf_community_id");
   String gearName=pafEnv.getGear().getName(response.getLocale());
   String thisConfigPage="editResponse";
   String pollId = request.getParameter("pollId");
   String responseId = request.getParameter("response_id");

%>

<dsp:setvalue bean="ResponseFormHandler.repositoryId" value="<%=responseId%>" />

<i18n:message id="submitButtonLabel" key="submit-button-label"/>
<i18n:message id="addResponsesButtonLabel" key="add-responses-button-label"/>
<i18n:message id="responseListHeader" key="response-list-header"/>
<i18n:message id="addResponseHeader" key="addresponse-header"/>





<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="ResponseFormHandler.formError"/>
  <dsp:oparam name="true">
    <font class="error"><UL>
      <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
        <dsp:param name="exceptions" bean="ResponseFormHandler.formExceptions"/>
        <dsp:oparam name="output">

          <LI> <dsp:valueof param="message"></dsp:valueof>
	
        </dsp:oparam>
      </dsp:droplet>
    </UL></font>
  </dsp:oparam>
  <dsp:oparam name="false">
    <core:ifNotNull value='<%=request.getParameter("formSubmit")%>'>

      &nbsp;<img src="/gear/poll/images/info.gif">&nbsp;&nbsp;<font class="smaller"><i18n:message key="edit-response-feedback-msg"/></font>
    </core:ifNotNull>
  </dsp:oparam>
</dsp:droplet>


    <core:CreateUrl id="editURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="editPoll"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
        <paf:encodeUrlParam param="paf_success_url" value="<%= successURL%>"/>
	<core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
         <core:UrlParam param="pollId" value="<%= pollId%>"/>
         <a href="<%= editURL.getNewUrl() %>"><font class="smaller">&laquo;&nbsp;<i18n:message key="backtopoll-link"/></font></a>
        </core:CreateUrl>
<dsp:form action="<%= origURI %>" method="POST">

<input type="hidden" name="paf_gear_id" value="<%= gearID %>">
<input type="hidden" name="paf_dm" value="full">
<input type="hidden" name="paf_gm" value="instanceConfig">
<input type="hidden" name="config_page" value="<%= thisConfigPage%>">
<input type="hidden" name="paf_page_id" value="<%= pageID %>"/>
<input type="hidden" name="paf_page_url" value="<%= pageURLEncoded %>"/>
<input type="hidden" name="paf_community_id" value="<%= communityID %>"/>
<input type="hidden" name="pollId" value="<%= pollId %>"/>
<input type="hidden" name="response_id" value="<%= responseId %>"/>
<input type="hidden" name="paf_success_url" value="<%= successURL %>"/>
<input type="hidden" name="formSubmit" value="true"/>

<dsp:input type="hidden" bean="ResponseFormHandler.repositoryId"  value="<%=responseId%>"/>
<core:CreateUrl id="createURL" url="<%= origURI %>">
   <core:UrlParam param="paf_dm" value="full"/>
   <core:UrlParam param="paf_gm" value="instanceConfig"/>
   <core:UrlParam param="config_page" value="<%=thisConfigPage%>"/>
   <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
   <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
   <core:UrlParam param="pollId" value="<%= pollId %>"/>
   <core:UrlParam param="response_id" value="<%= responseId %>"/>
   <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
   <paf:encodeUrlParam param="paf_success_url" value="<%= successURL%>"/>
   <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
   <core:UrlParam param="formSubmit" value="true"/>
      <dsp:input type="hidden" bean="ResponseFormHandler.successUrl"  value="<%=createURL.getNewUrl()%>"/>
</core:CreateUrl>

 
  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit">
<i18n:message key="editresponse-title"/>
</td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
        <font class="smaller">Use the form below to edit this poll response.
        </td></tr></table>
  <img src="/gear/poll/images/clear.gif" height=1 width=1 border=0><br>

     <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>
<table cellpadding="1" cellspacing="0" border="0">

<tr>
   <td nowrap align="left"><font class="smaller"><i18n:message key="response-header"/>:</b></font></td>
   <td>
  <dsp:input bean="ResponseFormHandler.value.ResponseText" size="50"  maxlength="200" type="text"/><br>
   </td>
</tr>


  <tr>
    <td>&nbsp;</td>
	<td><br><dsp:input type="submit" value="<%=submitButtonLabel%>" bean="ResponseFormHandler.update"/>
   </td>
  </tr>
</table>

</td></tr></table><br><br>

</dsp:form>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/poll/poll.war/admin/responseEditForm.jsp#2 $$Change: 651448 $--%>
