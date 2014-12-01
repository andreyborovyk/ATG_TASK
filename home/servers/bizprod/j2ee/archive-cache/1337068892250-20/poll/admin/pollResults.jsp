<%@ taglib uri="/poll-taglib" prefix="poll" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

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
   String thisConfigPage="pollResults";

%>

<poll:getPoll id="newPoll" pollId='<%=request.getParameter("pollId")%>'>
<% String pollTitle = (String)newPoll.getPoll().getPropertyValue("title"); %>
<%
    String bgColor="ffffff";
    String textColor="000000";
    String titleBGColor="CCCCCC";
    String titleTextColor="000000";
    String textParam=request.getParameter("responseText");
    if (textParam==null) {
      textParam="";
    }
    String shortNameParam=request.getParameter("shortName");
    if (shortNameParam==null) {
      shortNameParam="";
    }
    String barColor=null;
    String colorParam=request.getParameter("barColor");
    if (colorParam==null) {
      colorParam="";
    }

   // stuff for alternating background color in results
   int bgSwitch=0;
   String defaultBGColor = "ffffff";
   String resultsBGColor = defaultBGColor;
   String resultsAltColor = "dddddd";
%>

<i18n:message id="confirmClearMsg" key="confirm-clear-msg"/>

<script>
<!---
function confirmClear(f) {
  return confirm("<%=confirmClearMsg%>")
  }
-->
</script>

<core:IfNotNull value='<%= request.getParameter("action")%>'>
   <core:If value='<%= request.getParameter("action").equals("clear_votes")%>'>
     <poll:clearPollResults id="clearPoll"
                          pollId='<%= request.getParameter("pollId") %>'>
     </poll:clearPollResults>
   </core:If>
</core:IfNotNull>


<%-- removed back link
<font size="-2" face="verdana,arial,geneva,helvetica">
      <core:CreateUrl id="adminURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="pollAdmin"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
        <a href="<%= adminURL.getNewUrl() %>">&laquo; <i18n:message key="backtomain-link"/></font></a>&nbsp;
     </core:CreateUrl>
 <br>&nbsp;<br>
 --%>
 
  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit">
<i18n:message key="poll-results-title">
   <i18n:messageArg value="<%= pollTitle %>"/>
</i18n:message>
</td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
        <font class="smaller">This page displays poll results.
        </td></tr></table>
  <img src="/gear/poll/images/clear.gif" height=1 width=1 border=0><br>
  
  
  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>

<table border="0" cellpadding="1" cellspacing="0"  bgcolor="#333333"><tr><td>
<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="#ffffff"><tr><td valign="top">

<table CELLSPACING="0" width="100%" cellpadding="0" border="0">

  <tr>
     <td width="30%"><img src="/gear/poll/images/clear.gif" height="1" width="1" border="0"></td>
     <td width="70%"><img src="/gear/poll/images/clear.gif" height="1" width="1" border="0"></td>
  </tr>

  <tr>
    <td colspan="2"><img src="/gear/poll/images/clear.gif" height="5" width="1" border="0"></td>
  </tr>
</table>

      <%-- ********************************************************************** --%>
      <%-- Poll Results Display  --%>
      <%-- ********************************************************************** --%>
      <table width="100%" cellspacing="0" cellpadding="0" border="0">

      <tr bgcolor="#<%= bgColor %>">
        <td><img src="/gear/poll/images/clear.gif" height="3" width="1" border="0"></td>
      </tr>


      <tr bgcolor="#<%= bgColor %>">
        <td><img src="/gear/poll/images/clear.gif" height="3" width="1" border="0"></td>
      </tr>

      <tr>
        <td bgcolor="#ffffff" ><img src="/gear/poll/images/clear.gif" height="1" width="1" border="0
"></td>
      </tr>

      <%-- Poll Question --%>
      <tr bgcolor="#<%= bgColor %>">
        <td valign="top" align="left" ><font size="2" color="#<%=textColor %>">&nbsp;<b><i18n:message key="poll-question-label"/>:</b>&nbsp;<%=newPoll.getQuestionText()%></font></td>
      </tr>

      <tr bgcolor="#<%= bgColor %>">
        <td><img src="/gear/poll/images/clear.gif" height="5" width="1" border="0"></td>
      </tr>
     </table>

   <table width="100%" cellspacing="0" cellpadding="0" border="0">
   <%-- Poll Results --%>


   <tr bgcolor="#<%= titleBGColor %>">
      <td><font size="2" color="#<%=titleTextColor %>">&nbsp;<b><i18n:message key="response-header"/></b>&nbsp;</font></td>
      <td><font size="2" color="#<%=titleTextColor %>">&nbsp;<b><i18n:message key="count-header"/>&nbsp;</b></font></td>
      <td colspan="2" align="left"><font size="2" color="#<%=titleTextColor %>"><b><i18n:message key="percentage-header"/></b></font></td>
   </tr>
   <tr bgcolor="#<%= titleBGColor %>">
     <td colspan="4"><img src="/gear/poll/images/clear.gif" height="2" width="1" border="0"></td>
   </tr>

   <tr bgcolor="#<%= bgColor %>">
      <td colspan="4"><img src="/gear/poll/images/clear.gif" height="5" width="1" border="0"></td>
   </tr>
   <core:ForEach id="pollResults"
              values="<%= newPoll.getPollResultList() %>"
                castClass="atg.portal.gear.poll.PollResult"
              elementId="pollResult">

      <tr bgcolor="#<%= resultsBGColor %>">
         <td nowrap valign="middle" align="left" ><font size="2" color="#<%=textColor %>">
          &nbsp;<%= pollResult.getResponseText() %>
         </font></td>
         <td valign="middle" align="left" > <font size="2" color="#<%=textColor %>">
          &nbsp;<%= pollResult.getCount() %>
         </font></td>
	 <% barColor=pollResult.getBarColor();
	    if (barColor==null) {
	      barColor="000000";
	    }
	  %>
         <td><table width="1" border="0"><tr><td bgcolor="#<%= barColor%>"><img src="/gear/poll/images/clear.gif" height="6" width="<%= pollResult.getPercentage()*2 %>" border="0"></td></tr></table>
 	 </td>
         <td valign="middle" align="center" ><font size="2" color="#<%=textColor %>">
          &nbsp;<%= pollResult.getPercentage() %><i18n:message key="percentage-symbol"/>
	 </td>
      </tr>
      <tr bgcolor="#<%= bgColor %>">
        <td colspan="4"><img src="/gear/poll/images/clear.gif" height="2" width="1" border="0"></td>
      </tr>

      <% // Alternate results background color
         if (bgSwitch==0) {
            resultsBGColor=resultsAltColor;
	    bgSwitch=1;
	 } else {
            resultsBGColor=defaultBGColor;
	    bgSwitch=0;
	 }
      %>
   </core:ForEach>
      <tr bgcolor="#<%= bgColor %>">
        <td colspan="4"><img src="/gear/poll/images/clear.gif" height="5" width="1" border="0"></td>
      </tr>

      <tr bgcolor="#<%= bgColor %>">
      <core:CreateUrl id="clearURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="<%=thisConfigPage%>"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
        <paf:encodeUrlParam param="paf_success_url" value="<%= successURL%>"/>
	<core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
         <core:UrlParam param="pollId" value='<%= request.getParameter("pollId")%>'/>
         <core:UrlParam param="action" value="clear_votes"/>
         <td colspan="4" valign="top" align="left" >
         &nbsp;<a href="<%= clearURL.getNewUrl() %>"  onclick="return confirmClear()"><font size="2"><i18n:message key="clear-votes-link"/></font></a>
         </td>
        </core:CreateUrl>
      </tr>

      <tr bgcolor="#<%= bgColor %>">
        <td colspan="4"><img src="/gear/poll/images/clear.gif" height="5" width="1" border="0"></td>
      </tr>

     </table>


</td></tr></table></td></tr></table>


</td></tr></table><br><br>

</poll:getPoll>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/poll/poll.war/admin/pollResults.jsp#2 $$Change: 651448 $--%>
