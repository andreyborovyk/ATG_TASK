<%--
    This is the full view of the Poll gear. Essentially this is a slightly
    modified version of the shared view.  This page is the target for the
    poll form submission, and will either display the poll results (if the 
    user voted or selected "view results") or re-display the poll form if there
    was an error, like voting without making a selection.
--%>

<!-- ******** Begin Poll Gear display ******** -->
<%@ taglib uri="/poll-taglib" prefix="poll" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<paf:InitializeGearEnvironment id="pafEnv">


<%
   String origURI=pafEnv.getOriginalRequestURI();
   String gearID=pafEnv.getGear().getId();
   atg.portal.framework.ColorPalette cp = pafEnv.getPage().getColorPalette();
   String titleBGColor = cp.getGearTitleBackgroundColor();
   String titleTextColor = cp.getGearTitleTextColor();
   String gearBGColor = cp.getGearBackgroundColor();
   String gearTextColor = cp.getGearTextColor();
   String barColor=null;
   String highlightTextColor = cp.getHighlightTextColor();
   String highlightBGColor = cp.getHighlightBackgroundColor();
   if (cp.getHighlightBackgroundColor() == null || cp.getHighlightBackgroundColor() == "" ) {
    highlightTextColor = "000000";
    highlightBGColor   = "cccccc";
   }
   String pollIdParam="pollId"+gearID;  // URL param containing poll Id for this gear
   String sessionKey="poll."+gearID;
   boolean displayResults=false;
   boolean alreadyVoted=false;

   String bundleName=pafEnv.getGearInstanceParameter("resourceBundle");
   // default to old (pre-5.6) name, in case new file not installed
   if (bundleName==null || bundleName.equals("")) {
         bundleName="atg.gear.poll.PollResources";
   }
   
   // stuff for alternating background color in results
   int bgSwitch=0;
   String resultsBGColor = gearBGColor;
   String resultsAltColor = "EEEEEE";
%>

<i18n:bundle baseName="<%= bundleName %>" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="noChoiceMessage" key="no-choice-message"/>
<i18n:message id="responseHeader" key="response-header"/>
<i18n:message id="countHeader" key="count-header"/>
<i18n:message id="percentageHeader" key="percentage-header"/>
<i18n:message id="percentageSymbol" key="percentage-symbol"/>
<i18n:message id="voteLink" key="vote-link"/>
<i18n:message id="submitVoteButtonLabel" key="submit-vote-button-label"/>
<i18n:message id="resultsLink" key="results-link"/>
<i18n:message id="pollLabel" key="poll-label"/>
<i18n:message id="noPollMessage" key="no-poll-message"/>
<i18n:message id="backLink" key="back-link"/>

<dsp:importbean bean="/atg/portal/gear/poll/PollVoteFormHandler"/>
<dsp:importbean bean="/atg/portal/gear/poll/PollTrackerBean"/>


 <%-- Determine whether to show poll or results --%>
<dsp:getvalueof id="pollTracker" bean="PollTrackerBean">
 <core:ExclusiveIf>
   <core:If value="<%= ((atg.portal.gear.poll.PollTrackerBean) pollTracker).hasPoll(request.getParameter(pollIdParam)) %>">
     <% displayResults=true;
        alreadyVoted=true;
     %>
   </core:If>
   <core:If value='<%=gearID.equals(request.getParameter("pollGear")) && request.getParameter("viewPollResults")!=null%>'>
     <% displayResults=true; %>
   </core:If>
 </core:ExclusiveIf>
</dsp:getvalueof>

<%-- if a form was submitted and had errors, display message and redisplay form --%>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="PollVoteFormHandler.formError"/>
  <dsp:oparam name="true">
    <% displayResults=false; %>
    <font class="error"><UL>
      <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
        <dsp:param name="exceptions" bean="PollVoteFormHandler.formExceptions"/>
        <dsp:oparam name="output">
        <% displayResults=false; %>
          <LI> <dsp:valueof param="message"></dsp:valueof>
        </dsp:oparam>
      </dsp:droplet>
    </UL></font>
  </dsp:oparam>
</dsp:droplet>

<%-- show either results or poll form, --%>
<core:ExclusiveIf>

 <core:If value='<%= displayResults %>' >

<poll:getPoll id="poll" pollId='<%=request.getParameter(pollIdParam)%>'>
			    

      <%-- ********************************************************************** --%>
      <%-- Poll Results Display  --%>
      <%-- ********************************************************************** --%>
      <table width="100%" border="0">

      <tr bgcolor="#<%= gearBGColor %>">
        <td>
        <font size="1" class="large">&nbsp;<%=pollLabel%></font>
        </td>
      </tr>

      <tr bgcolor="#<%= gearBGColor %>">
        <td><img src="/gear/poll/images/clear.gif" height="2" width="1" border="0"></td>
      </tr>
      <%-- Poll Question --%>
      <tr bgcolor="#<%= gearBGColor %>">
        <td valign="top" align="left" ><font size="1" color="#<%=gearTextColor %>" class="small">&nbsp;<%=poll.getQuestionText()%>&nbsp;</font></td>
      </tr>
     </table>

   <table width="100%" border="0">
   <%-- Poll Results --%>

   
 <tr bgcolor="#<%= highlightBGColor %>">
  <td colspan="4">
   <table border=0 width="100%" cellpadding="2" cellspacing="0">
    <tr bgcolor="#<%= highlightBGColor %>">
      <td><font size="1" color="#<%=highlightTextColor %>" class="large">&nbsp;<%=responseHeader%></font></td>
      <td><font size="1" color="#<%=highlightTextColor %>" class="large"><%=countHeader%>&nbsp;</font></td>
      <td colspan="2" align="left"><font size="1" color="#<%=highlightTextColor %>" class="large"><%=percentageHeader%>&nbsp;</font></td>
    </tr>

   <core:ForEach id="pollResults"
              values="<%= poll.getPollResultList() %>"
                castClass="atg.portal.gear.poll.PollResult"
              elementId="pollResult">

      <tr bgcolor="#<%= resultsBGColor %>">
         <td nowrap valign="middle" align="left" ><font size="1" color="#<%=gearTextColor %>" class="small">
          &nbsp;<%= pollResult.getResponseText() %> 
         </font></td>
         <td valign="middle" align="left" > <font size="1" color="#<%=gearTextColor %>" class="small">
          &nbsp;&nbsp;&nbsp;<%= pollResult.getCount() %>
         </font></td>
	 <% barColor=pollResult.getBarColor();
	    if (barColor==null) {
	      barColor="000000";
	    }
	  %>
         <td><table width="1" border="0"><tr><td bgcolor="#<%= barColor%>"><img src="/gear/poll/images/clear.gif" height="5" width="<%= pollResult.getPercentage()*2 %>" border="0"></td></tr></table>
 	 </td>
         <td valign="middle" align="center" ><font size="1" color="#<%=gearTextColor %>" class="small">
          &nbsp;<%= pollResult.getPercentage() %><%=percentageSymbol%>
	 </td>
      </tr>

      <% // Alternate results background color
         if (bgSwitch==0) {
            resultsBGColor=resultsAltColor;
	    bgSwitch=1;
	 } else {
            resultsBGColor=gearBGColor;
	    bgSwitch=0;
	 }
      %>
   </core:ForEach>
   </table>
  </td>
 </tr>
      <tr bgcolor="#<%= gearBGColor %>">
        <td colspan="4"><img src="/gear/poll/images/clear.gif" height="2" width="1" border="0"></td>
      </tr>

      <%-- Show link to vote if this is view-only mode for this gearID --%>
      <tr bgcolor="#<%= gearBGColor %>">
        <core:CreateUrl id="voteUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
          <td colspan="4" align="left" nowrap>
         <font size="2"><a href="<%= voteUrl.getNewUrl() %>" class="gear_nav">&nbsp;&lt;&nbsp;<%=backLink%></a></font>
          </td>
       </core:CreateUrl>
       </tr>
      <tr bgcolor="#<%= gearBGColor %>">
        <td colspan="4"><img src="/gear/poll/images/clear.gif" height="2" width="1" border="0"></td>
      </tr>
     </table>

    </poll:getPoll>
 </core:If>

 <core:DefaultCase>

<%-- ********************************************************************** --%>
<%-- Poll Form Display  --%>
<%-- ********************************************************************** --%>

<core:demarcateTransaction id="pollSharedPageXA">

   <poll:getPoll id="poll" 
 		gearId="<%= gearID %>">
				

   <core:ExclusiveIf>

     <core:IfNotNull value="<%= poll.getQuestionText()%>">

      <table width="100%" border="0">
   
      <tr bgcolor="#<%= highlightBGColor %>">
        <td colspan="4">
         <table border=0 width="100%" cellpadding="2" cellspacing="0">
          <tr bgcolor="#<%= highlightBGColor %>">
          <td nowrap align="left"><font size="1" class="large">&nbsp;<%=pollLabel%></font></td>
      <core:CreateUrl id="resultsUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
         <core:UrlParam param="<%=pollIdParam%>" value="<%=poll.getPollId()%>"/>
         <core:UrlParam param="viewPollResults" value="true"/>
         <core:UrlParam param="pollGear" value="<%=gearID%>"/>
	 <core:UrlParam param="paf_dm" value="full"/>
	 <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
           <td align="right" nowrap><font size="2"><a href="<%= resultsUrl.getNewUrl() %>" class="gear_nav"><%=resultsLink%></a></font>&nbsp;</td>
      </core:CreateUrl>
          </tr>
	 </table>
	</td>
      </tr>

      <tr bgcolor="#<%= gearBGColor %>">
       <td width="10%" height="1"><img src="/gear/poll/images/clear.gif" height="1" width="1" border="0"></td>
       <td width="40%" height="1"><img src="/gear/poll/images/clear.gif" height="1" width="1" border="0"></td>
       <td width="40%" height="1"><img src="/gear/poll/images/clear.gif" height="1" width="1" border="0"></td>
       <td width="10%" height="1"><img src="/gear/poll/images/clear.gif" height="1" width="1" border="0"></td>
      </tr>

      <%-- Poll Question --%>
      <tr bgcolor="#<%= gearBGColor %>">
        <td align="left" >&nbsp;</td>
        <td colspan="2" valign="top" align="left" ><font size="1" color="#<%=gearTextColor %>" class="small"><%=poll.getQuestionText()%></font></td>
        <td valign="top" align="left" >&nbsp;</td>
      </tr>

      <tr bgcolor="#<%= gearBGColor %>">
        <td colspan="4"><img src="/gear/poll/images/clear.gif" height="2" width="1" border="0"></td>
      </tr>

   <%--  ****** Poll Response form ****** --%>


<%--  ****** Poll Response form ****** --%>
<dsp:form  method="post" action="<%= pafEnv.getOriginalRequestURI() %>">


<!-- need hidden vars for full gear mode -->
<input type="hidden" name="paf_dm" value="full">
<input type="hidden" name="paf_gear_id" value="<%=gearID%>">

<dsp:input type="hidden" bean="PollVoteFormHandler.pollId" value="<%=poll.getPollId()%>" />
<dsp:input type="hidden" bean="PollVoteFormHandler.gearId" value="<%=gearID%>" />

 <core:CreateUrl id="resultsUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="<%=pollIdParam%>" value="<%=poll.getPollId()%>"/>
    <core:UrlParam param="viewPollResults" value="true"/>
    <core:UrlParam param="pollGear" value="<%=gearID%>"/>
    <core:UrlParam param="paf_dm" value="full"/>
    <core:UrlParam param="paf_gear_id" value="<%=gearID%>"/>

    <dsp:input type="hidden" bean="PollVoteFormHandler.successUrl" value="<%= resultsUrl.getNewUrl() %>"/>
    <dsp:input type="hidden" bean="PollVoteFormHandler.failureUrl" value="<%= resultsUrl.getNewUrl() %>"/>
 </core:CreateUrl>

   <core:ForEach id="pollResponses"
              values="<%= poll.getResponseList() %>"
                castClass="atg.repository.RepositoryItem"
              elementId="pollResponse">

      <tr bgcolor="#<%= gearBGColor %>">
         <td valign="middle" align="left" ><font size="1" color="#<%=gearTextColor %>" class="small">
          <dsp:input type="radio" bean="PollVoteFormHandler.pollSelection" value="<%= pollResponse.getRepositoryId() %>"/>
         </font></td>
         <td colspan="2" valign="middle" align="left" ><font size="1" color="#<%=gearTextColor %>" class="small">
          <%= pollResponses.getCount() %>.&nbsp;<%= pollResponse.getPropertyValue("responseText") %> 
         </font></td>
         <td valign="middle" align="center" >&nbsp;</td>
      </tr>

   </core:ForEach>

   <tr bgcolor="#<%= gearBGColor %>">
      <td colspan="4"><img src="/gear/poll/images/clear.gif" height="2" width="1" border="0"></td>
   </tr>

   <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="2" align="left"><font size="2" class="small">&nbsp;<dsp:input type="submit" value="<%= submitVoteButtonLabel %>" bean="PollVoteFormHandler.vote"/>&nbsp;&nbsp;&nbsp;&nbsp;</td></font>
     </td>

         <td colspan="2" align="left" nowrap>&nbsp;</td>
    </tr>

    <tr bgcolor="#<%= gearBGColor %>">
      <td colspan="4"><img src="/gear/poll/images/clear.gif" height="2" width="1" border="0"></td>
    </tr>

   </table>
   </dsp:form>
   </core:IfNotNull>

   <%-- ********************************************************************** --%>
   <%-- No Poll Display  --%>
   <%-- ********************************************************************** --%>

   <core:DefaultCase>
      <font size="2" class="small"><%=noPollMessage%></font>
   </core:DefaultCase>

   </core:ExclusiveIf>
  </poll:getPoll>
</core:demarcateTransaction>


 </core:DefaultCase>
</core:ExclusiveIf>


</paf:InitializeGearEnvironment>
</dsp:page>
<!-- ******** End of Poll Gear display ******** -->
<%-- @version $Id: //app/portal/version/10.0.3/poll/poll.war/pollFull.jsp#2 $$Change: 651448 $--%>
