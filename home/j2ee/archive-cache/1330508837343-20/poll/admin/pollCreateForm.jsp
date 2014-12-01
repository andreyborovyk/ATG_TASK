<%@ taglib uri="/poll-taglib" prefix="poll" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<i18n:bundle baseName="atg.gear.poll.PollResources" localeAttribute="userLocale" changeResponseLocale="false" />
<paf:InitializeGearEnvironment id="pafEnv">

<dsp:importbean bean="/atg/portal/gear/poll/PollFormHandler"/>


<%
   String origURI=pafEnv.getOriginalRequestURI();
   String gearID=pafEnv.getGear().getId();
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String successURL = request.getParameter("paf_success_url");
   String communityID = request.getParameter("paf_community_id");
   String gearName=pafEnv.getGear().getName(response.getLocale());
   String thisConfigPage="addPoll";

   int numResponses=5;
   int moreResponses;
   if (request.getParameter("numResp")!=null) {
      try {
         numResponses=Integer.parseInt(request.getParameter("numResp"));
	 
      } catch (Exception e) {
        numResponses=5;
      }
   }
   moreResponses=numResponses+5;
   String moreResponseParam=Integer.toString(moreResponses);
%>
<i18n:message id="submitButtonLabel" key="submit-button-label"/>
<i18n:message id="addResponsesButtonLabel" key="add-responses-button-label"/>
<i18n:message id="responseListHeader" key="response-list-header"/>
<i18n:message id="addResponseHeader" key="addresponse-header"/>
<i18n:message id="newPollFeedbackMsg" key="new-poll-feedback-msg"/>
<i18n:message id="newpollTitle" key="newpoll-title"/>
<i18n:message id="pollTitleLabel" key="poll-title-label"/>
<i18n:message id="pollQuestionLabel" key="poll-question-label"/>


<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="PollFormHandler.formError"/>
  <dsp:oparam name="true">
    <font class="error"><UL>
      <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
        <dsp:param name="exceptions" bean="PollFormHandler.formExceptions"/>
        <dsp:oparam name="output">

          <LI> <dsp:valueof param="message"></dsp:valueof>

        </dsp:oparam>
      </dsp:droplet>
    </UL></font>
  </dsp:oparam>
  <dsp:oparam name="false">
    <core:ifNotNull value='<%=request.getParameter("formSubmit")%>'>

      &nbsp;<img src="/gear/poll/images/info.gif">&nbsp;&nbsp;<font class="smaller"><%=newPollFeedbackMsg%></font>
 
    </core:ifNotNull>
  </dsp:oparam>
</dsp:droplet>


<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit">
<i18n:message key="admin_poll_create_title"/>
</td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
        <font class="smaller"><i18n:message key="admin_poll_create_intro"/>
        </td></tr></table>
  <img src="/gear/poll/images/clear.gif" height=1 width=1 border=0><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>
<table cellpadding="1" cellspacing="0" border="0">

   <tr >
      <td colspan="2"><img src="/gear/poll/images/clear.gif" height="1" width="1" border="0"></td>
    </tr>


<dsp:form action="<%= origURI %>" method="POST">

<input type="hidden" name="paf_gear_id" value="<%= gearID %>">
<input type="hidden" name="paf_dm" value="full">
<input type="hidden" name="paf_gm" value="instanceConfig">
<input type="hidden" name="config_page" value="<%= thisConfigPage%>">
<input type="hidden" name="paf_page_id" value="<%= pageID %>"/>
<input type="hidden" name="paf_page_url" value="<%= pageURLEncoded %>"/>
<input type="hidden" name="paf_community_id" value="<%= communityID %>"/>
<input type="hidden" name="paf_success_url" value="<%= successURL %>"/>
<input type="hidden" name="numResp" value="<%=numResponses%>"/>

<dsp:input type="hidden" bean="PollFormHandler.gearId"  value="<%=gearID%>"/>

<core:CreateUrl id="createURL" url="<%= origURI %>">
   <core:UrlParam param="paf_dm" value="full"/>
   <core:UrlParam param="paf_gm" value="instanceConfig"/>
   <core:UrlParam param="config_page" value="addPoll"/>
   <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
   <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
   <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
   <paf:encodeUrlParam param="paf_success_url" value="<%= successURL%>"/>
   <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
   <core:UrlParam param="formSubmit" value="true"/>
   <core:UrlParam param="numResp" value='<%=request.getParameter("numResp")%>'/>
      <dsp:input type="hidden" bean="PollFormHandler.successUrl"  value="<%=createURL.getNewUrl()%>"/>
      <dsp:input type="hidden" bean="PollFormHandler.failureUrl"  value="<%=createURL.getNewUrl()%>"/>      
</core:CreateUrl>

 
<core:CreateUrl id="moreURL" url="<%= origURI %>">
   <core:UrlParam param="paf_dm" value="full"/>
   <core:UrlParam param="paf_gm" value="instanceConfig"/>
   <core:UrlParam param="config_page" value="addPoll"/>
   <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
   <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
   <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
   <paf:encodeUrlParam param="paf_success_url" value="<%= successURL%>"/>
   <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
   <core:UrlParam param="numResp" value="<%=moreResponseParam%>"/>
      <dsp:input type="hidden" bean="PollFormHandler.moreUrl"  value="<%=moreURL.getNewUrl()%>"/>
</core:CreateUrl>

<!--
     <tr>
        <td  colspan="2" align="left"><font class="smaller_bold"><b>&nbsp;<%=newpollTitle%></font></td>
     </tr>
-->

<tr>
   <td nowrap align="left"><font class="smaller">&nbsp;<%=pollTitleLabel%>:</b></font></td>
   <td>
  <dsp:input bean="PollFormHandler.pollTitle" size="50"  maxlength="255" type="text"/><br>
   </td>
</tr>


<tr>
   <td nowrap align="left" valign="top"><font class="smaller">&nbsp;<%=pollQuestionLabel%>:</b></font></td>
   <td>
     <dsp:textarea bean="PollFormHandler.questionText" cols="40" rows="5" wrap="PHYSICAL" ></dsp:textarea>&nbsp;
   </td>
</tr>

      <tr >
        <td colspan="2"><img src="/gear/poll/images/clear.gif" height="5" width="1" border="0"></td>
      </tr>

<tr valign="top">
  <td align="left"><font  class="smaller"><%=responseListHeader%>:</td>
  <td align="left"><font class="smaller"><%=addResponseHeader%></td>
</tr>
<%--
<dsp:getvalueof id="responseList" bean="PollFormHandler.responses">

<core:forEach id="list" rangeCount="<%=numResponses%>"
              values="<%= responseList %>"
                castClass="String"
              elementId="pollResponse">

--%>

<% for (int idx=0;idx<numResponses;idx++) { %>

<tr>
  <td align="right"><font class="smaller"><%= idx+1 %></td>
  <td align="left"><dsp:input type="text" bean='<%= "PollFormHandler.responses[" + idx + "]" %>' size="50" maxlength="200"/></td>
</tr>

<% } %>

  <tr>
    <td align=right>&nbsp;</td>
    <td align=left><dsp:input type="submit" bean="PollFormHandler.addMore" value="<%=addResponsesButtonLabel%>"/></td>
  </tr>

<tr>
  <td>&nbsp;</td>
  <td align="left"><font  class="smaller">
  <dsp:input type="checkbox" bean="PollFormHandler.makeDefaultPoll"/>&nbsp;Make this current poll.<br>
</td>
</tr>
  <tr>
    <td colspan="2" align=left><br><br><dsp:input type="submit" value="<%=submitButtonLabel%>" bean="PollFormHandler.add"/>
   </td>
  </tr>
      <tr >
        <td colspan="2"><img src="/gear/poll/images/clear.gif" height="5" width="1" border="0"></td>
      </tr>
</table>

</td></tr></table><br><br>

</dsp:form>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/poll/poll.war/admin/pollCreateForm.jsp#2 $$Change: 651448 $--%>
