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
   String thisConfigPage="editPoll";
   String pollId = request.getParameter("pollId");

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

   // stuff for alternating background color for polls/results
   int bgSwitch=0;
   String defaultBGColor = "ffffff";
   String resultsBGColor = defaultBGColor;
   String resultsAltColor = "dddddd";

%>

<dsp:setvalue bean="PollFormHandler.pollId" value="<%=pollId%>" />

<i18n:message id="confirmDeleteMsg" key="confirm-delete-response-msg"/>

<script>
<!---
function confirmDelete(f) {
  return confirm("<%=confirmDeleteMsg%>")
  }
-->
</script>

<i18n:message id="submitButtonLabel" key="submit-button-label"/>
<i18n:message id="addResponsesButtonLabel" key="add-responses-button-label"/>
<i18n:message id="responseListHeader" key="response-list-header"/>
<i18n:message id="addResponseHeader" key="addresponse-header"/>



<%-- Delete response if action param is set --%>
<core:IfNotNull value='<%= request.getParameter("action")%>'>
   <core:If value='<%= request.getParameter("action").equals("delete_response")%>'>
     <poll:deleteResponse id="deleteResponse"
                          responseId='<%= request.getParameter("response_id") %>'>
     </poll:deleteResponse>
    
      &nbsp;<img src="/gear/poll/images/info.gif">&nbsp;&nbsp;<font class="smaller"><i18n:message key="delete-response-feedback-msg"/></font>

   </core:If>
</core:IfNotNull>


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
   
      &nbsp;<img src="/gear/poll/images/info.gif">&nbsp;&nbsp;<font class="smaller"><i18n:message key="edit-poll-feedback-msg"/></font>

    </core:ifNotNull>
  </dsp:oparam>
</dsp:droplet>


<dsp:form action="<%= origURI %>" method="POST">

<input type="hidden" name="paf_gear_id" value="<%= gearID %>">
<input type="hidden" name="paf_dm" value="full">
<input type="hidden" name="paf_gm" value="instanceConfig">
<input type="hidden" name="config_page" value="<%= thisConfigPage%>">
<input type="hidden" name="paf_page_id" value="<%= pageID %>"/>
<input type="hidden" name="paf_page_url" value="<%= pageURLEncoded %>"/>
<input type="hidden" name="paf_community_id" value="<%= communityID %>"/>
<input type="hidden" name="numResp" value="<%=numResponses%>"/>
<input type="hidden" name="paf_success_url" value="<%= successURL %>"/>

<dsp:input type="hidden" bean="PollFormHandler.gearId"  value="<%=gearID%>"/>
<dsp:input type="hidden" bean="PollFormHandler.pollId"  value="<%=pollId%>"/>

<core:CreateUrl id="createURL" url="<%= origURI %>">
   <core:UrlParam param="paf_dm" value="full"/>
   <core:UrlParam param="paf_gm" value="instanceConfig"/>
   <core:UrlParam param="config_page" value="<%=thisConfigPage%>"/>
   <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
   <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
   <core:UrlParam param="pollId" value="<%= pollId %>"/>
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
   <core:UrlParam param="config_page" value="<%=thisConfigPage%>"/>
   <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
   <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
   <core:UrlParam param="pollId" value="<%= pollId %>"/>
   <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
   <paf:encodeUrlParam param="paf_success_url" value="<%= successURL%>"/>
   <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
   <core:UrlParam param="numResp" value="<%=moreResponseParam%>"/>
      <dsp:input type="hidden" bean="PollFormHandler.moreUrl"  value="<%=moreURL.getNewUrl()%>"/>
</core:CreateUrl>



  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit">
<i18n:message key="editpoll-title">
   <i18n:messageArg value="<%= gearName %>"/>
</i18n:message>
</td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
        <font class="smaller">Use the form below to create a new poll question.
        </td></tr></table>
  <img src="/gear/poll/images/clear.gif" height=1 width=1 border=0><br>

  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>
<table cellpadding="1" cellspacing="0" border="0">



    <tr >
      <td colspan="2"><img src="/gear/poll/images/clear.gif" height="5" width="1" border="0"></td>
    </tr>

<tr>
   <td nowrap align="left"><font class="smaller">&nbsp;<i18n:message key="poll-title-label"/>:</b></font></td>
   <td>
  <dsp:input bean="PollFormHandler.pollTitle" size="50"  maxlength="100" type="text"/><br>
   </td>
</tr>


<tr>
   <td nowrap align="left" valign="top"><font class="smaller">&nbsp;<i18n:message key="poll-question-label"/>:</b></font></td>
   <td>
     <dsp:textarea bean="PollFormHandler.questionText" cols="40" rows="5" wrap="PHYSICAL" ></dsp:textarea>&nbsp;
   </td>
</tr>

      <tr >
        <td colspan="2"><img src="/gear/poll/images/clear.gif" height="5" width="1" border="0"></td>
      </tr>

</table>
<br>
<%-- ******  Current Response List ******  --%>

<poll:getPoll id="editPoll" pollId='<%=pollId%>'>

<table CELLSPACING="0" width="60%" cellpadding="2" border="0">

  <%-- ***** Column Headers ****** --%>
  <tr bgcolor="#CCCCCC">
    <td align="left"><font class="smaller_bold">
      &nbsp;<b><i18n:message key="response-text-label"/></b>&nbsp;
    </font></td>
    <td align="center"><font class="smaller_bold">
      &nbsp;<b><i18n:message key="count-header"/></b>&nbsp;
    </font></td>
    <td align="center"><font class="smaller_bold">
      &nbsp;<b><i18n:message key="edit-response-header"/></b>&nbsp;
    </font></td>
    <td align="center"><font class="smaller_bold">
      &nbsp;<b><i18n:message key="delete-response-header"/></b>&nbsp;
    </font></td>
  </tr>

   <core:ForEach id="pollResponses"
              values="<%= editPoll.getPollResultList() %>"
                castClass="atg.portal.gear.poll.PollResult"
              elementId="pollResult">

      <tr bgcolor="#<%= resultsBGColor %>">
         <td valign="middle" align="left" ><font class="smaller">
          &nbsp;<%= pollResult.getResponseText() %>
         </font></td>
         <td valign="middle" align="center" ><font class="smaller">
          &nbsp;<%= pollResult.getCount() %>
         </font></td>
         <core:CreateUrl id="editURL" url="<%= origURI %>">
	   <core:UrlParam param="paf_dm" value="full"/>
	   <core:UrlParam param="paf_gm" value="instanceConfig"/>
	   <core:UrlParam param="config_page" value="editResponse"/>
	   <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	   <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	   <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
           <paf:encodeUrlParam param="paf_success_url" value="<%= successURL%>"/>
	   <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
            <core:UrlParam param="pollId" value='<%=pollId%>'/>
            <core:UrlParam param="response_id" value="<%= pollResult.getResponseId()%>"/>
             <td align="center"><font class="smaller">
             <a href="<%= editURL.getNewUrl() %>" ><i18n:message key="edit-link"/></a>
             </font></td>
        </core:CreateUrl>
         <core:CreateUrl id="deleteURL" url="<%= origURI %>">
	   <core:UrlParam param="paf_dm" value="full"/>
	   <core:UrlParam param="paf_gm" value="instanceConfig"/>
	   <core:UrlParam param="config_page" value="<%=thisConfigPage%>"/>
	   <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	   <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	   <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
           <paf:encodeUrlParam param="paf_success_url" value="<%= successURL%>"/>
	   <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
            <core:UrlParam param="pollId" value='<%=pollId%>'/>
            <core:UrlParam param="response_id" value="<%= pollResult.getResponseId()%>"/>
            <core:UrlParam param="action" value="delete_response"/>
             <td align="center"><font class="smaller">
             <a href="<%= deleteURL.getNewUrl() %>" onclick="return confirmDelete()"><img src="/gear/poll/images/icon_delete.gif" border=0></a>
             </font></td>
        </core:CreateUrl>
      </tr>
      <% // Alternate poll/results background color
         if (bgSwitch==0) {
            resultsBGColor=resultsAltColor;
	    bgSwitch=1;
	 } else {
            resultsBGColor=defaultBGColor;
	    bgSwitch=0;
	 }
      %>
   </core:ForEach>
</poll:getPoll>
      <tr >
        <td colspan="2"><img src="/gear/poll/images/clear.gif" height="5" width="1" border="0"></td>
      </tr>
</table>
<br>

<%-- ******  New Responses Form ******  --%>
<table CELLSPACING="0" width="80%" cellpadding="2" border="0">
<tr valign="top">
  <td align="left"><font class="smaller"><%=responseListHeader%>:</td>
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
  <td align="left"><font class="smaller">
  <dsp:input type="checkbox" bean="PollFormHandler.makeDefaultPoll"/>&nbsp;Make this current poll.<br>
</td>
</tr>
  <tr>
    <td colspan="2" align=left><br><dsp:input type="submit" value="<%=submitButtonLabel%>" bean="PollFormHandler.update"/>
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
<%-- @version $Id: //app/portal/version/10.0.3/poll/poll.war/admin/pollEditForm.jsp#2 $$Change: 651448 $--%>
