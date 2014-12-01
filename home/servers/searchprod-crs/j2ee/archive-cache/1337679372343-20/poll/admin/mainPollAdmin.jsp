<%--
  This is the main admin page for polls, which will list all polls, and allow the admin
  to create, delete, or modify polls.  

--%>
<%@ taglib uri="/poll-taglib" prefix="poll" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<dsp:page>
<i18n:bundle baseName="atg.gear.poll.PollResources" localeAttribute="userLocale" changeResponseLocale="false" />
<paf:InitializeGearEnvironment id="pafEnv">

<%

   String origURI=pafEnv.getOriginalRequestURI();
   String gearID=pafEnv.getGear().getId();
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String successURL = request.getParameter("paf_success_url");
   String communityID = request.getParameter("paf_community_id");
   String gearName=pafEnv.getGear().getName(response.getLocale());
   String thisConfigPage="pollAdmin";

   // stuff for alternating background color for polls/results
   int bgSwitch=0;
   String defaultBGColor = "ffffff";
   String resultsBGColor = defaultBGColor;
   String resultsAltColor = "dddddd";

%>

<i18n:message id="confirmClearMsg" key="confirm-clear-msg"/>
<i18n:message id="confirmDeleteMsg" key="confirm-delete-msg"/>

<script>
<!---
function confirmClear(f) {
  return confirm("<%=confirmClearMsg%>")
  }
function confirmDelete(f) {
  return confirm("<%=confirmDeleteMsg%>")
  }
-->
</script>

<core:IfNotNull value='<%= request.getParameter("action")%>'>

   <core:If value='<%= request.getParameter("action").equals("delete_poll")%>'>
     <poll:deletePoll id="deletePoll"
                          pollId='<%= request.getParameter("pollId") %>'>
     </poll:deletePoll>
   </core:If>

   <core:If value='<%= request.getParameter("action").equals("clear_votes")%>'>
     <poll:clearPollResults id="clearPoll"
                          pollId='<%= request.getParameter("pollId") %>'>
     </poll:clearPollResults>
   </core:If>

</core:IfNotNull>

<%-- removed back link
<core:CreateUrl id="adminURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="default"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
<font size="-2" face="verdana,arial,geneva,helvetica"><a href='<%=adminURL.getNewUrl()%>'>&laquo; <i18n:message key="backtogear-link"/></a></font>
 </core:CreateUrl>
--%>


<poll:getAllPolls id="polls">

<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="pageheader_edit"><i18n:message key="admin_main_poll_title"/></font>
</td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
<font class="smaller"><i18n:message key="admin_main_poll_admin_intro"/>
</td></tr></table>

<img src="/gear/poll/images/clear.gif" height=1 width=1 border=0><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>
<table width="100%" cellspacing="0" cellpadding="1" border="0">

<%-- Column headers --%>

<tr>
<td colspan="6" ><img src="/gear/poll/images/clear.gif" height="10" width="1" border="0"></td>
</tr>

<tr bgcolor="#CCCCCC">
<td valign="top" align="left" width="20%"> <font class="smaller" ><b>&nbsp;&nbsp;<i18n:message key="allpolls-header"/></b></font></td>
<td valign="top" align="left" width="40%"> <font class="smaller" ><b><i18n:message key="question-header"/></b></font></td>
<td valign="top" align="center" width="15%"> <font class="smaller"><b><i18n:message key="results-header"/></b></font></td>
<td valign="top" align="center" width="15%"> <font class="smaller"><b><i18n:message key="edit-header"/></b></font></td>
<td valign="top" align="center" width="15%"> <font class="smaller"><b><i18n:message key="clear-votes-header"/></b></font></td>
<td valign="top" align="center" width="10%"> <font class="smaller"><b><i18n:message key="delete-label"/>&nbsp;&nbsp;</b></font></td>
</tr>

<tr bgcolor="#CCCCCC">
<td colspan="6"><img src="/gear/poll/images/clear.gif" height="2" width="1" border="0"></td>
</tr>

<core:ForEach id="pollForEach"
            	values="<%= polls.getPollList() %>"
		castClass="atg.repository.RepositoryItem"
		elementId="poll">

   <tr bgcolor="#<%= resultsBGColor %>">
      <td valign="top" align="left" > <font class="smaller">
         <%= poll.getPropertyValue("title") %>
      </td>
      <td valign="top" align="left" > <font  class="smaller">
         <%= poll.getPropertyValue("questionText") %>
      </td>
      <core:CreateUrl id="resultsURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="pollResults"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
        <paf:encodeUrlParam param="paf_success_url" value="<%= successURL%>"/>
	<core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
         <core:UrlParam param="pollId" value="<%= poll.getRepositoryId()%>"/>
         <td valign="top" align="center" >
         <a href="<%= resultsURL.getNewUrl() %>"><i18n:message key="admin-results-link"/></a>
         </td>
        </core:CreateUrl>
      <core:CreateUrl id="editURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="editPoll"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
        <paf:encodeUrlParam param="paf_success_url" value="<%= successURL%>"/>
	<core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
         <core:UrlParam param="pollId" value="<%= poll.getRepositoryId()%>"/>
         <td valign="top" align="center" >
         <a href="<%= editURL.getNewUrl() %>"><i18n:message key="edit-link"/></a>
         </td>
        </core:CreateUrl>
      <core:CreateUrl id="clearURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="<%=thisConfigPage%>"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
        <paf:encodeUrlParam param="paf_success_url" value="<%= successURL%>"/>
	<core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
         <core:UrlParam param="pollId" value="<%= poll.getRepositoryId()%>"/>
         <core:UrlParam param="action" value="clear_votes"/>
         <td valign="top" align="center">
         <a href="<%= clearURL.getNewUrl() %>"  onclick="return confirmClear()"><i18n:message key="clear-votes-link"/></a>
         </td>
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
         <core:UrlParam param="pollId" value="<%= poll.getRepositoryId()%>"/>
         <core:UrlParam param="action" value="delete_poll"/>
         <td valign="top" align="center">
         <a href="<%= deleteURL.getNewUrl() %>" onclick="return confirmDelete()"><img src="/gear/poll/images/icon_delete.gif" border=0></a>
        </core:CreateUrl>
      </td>
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

</table>
</poll:getAllPolls>

<table border="0" cellpadding="0" cellspacing="0" width="30%">

<tr>
  <td><img src="/gear/poll/images/clear.gif" height="20" width="1" border="0"></td>
</tr>

</table>
</td></tr></table><br><br>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/poll/poll.war/admin/mainPollAdmin.jsp#2 $$Change: 651448 $--%>
