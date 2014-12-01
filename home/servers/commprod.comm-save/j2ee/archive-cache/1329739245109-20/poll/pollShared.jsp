<%--
    This is the shared view of the Poll gear. It will display a question, along with a set of resonses.
    The response is submitted to the full-page view of the gear, where the form handling is now
    done (this is a recent change to this page). A link to view results (without submitting) will
    also be provided.

--%>

<!-- ******** Begin Poll Gear display ******** -->
<%@ taglib uri="/poll-taglib" prefix="poll" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<paf:InitializeGearEnvironment id="pafEnv">

<dsp:importbean bean="/atg/portal/gear/poll/PollVoteFormHandler"/>
<dsp:importbean bean="/atg/portal/gear/poll/PollTrackerBean"/>

<%
   String clearGif = response.encodeURL("/gear/poll/images/clear.gif");
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

   boolean hasFullMode=pafEnv.getGear().hasMode("content", "full");

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



<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="PollVoteFormHandler.formError"/>
  <dsp:oparam name="true">
    <% displayResults=false; %>
    <font class="error"><UL>
      <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
        <dsp:param name="exceptions" bean="PollVoteFormHandler.formExceptions"/>
        <dsp:oparam name="output">
          <LI> <dsp:valueof param="message"></dsp:valueof>
        </dsp:oparam>
      </dsp:droplet>
    </UL></font>
  </dsp:oparam>
</dsp:droplet>


<core:demarcateTransaction id="pollSharedPageXA">

<poll:getPoll id="poll" gearId='<%=gearID%>'>

<dsp:getvalueof id="pollTracker" bean="PollTrackerBean">
 <%-- Determine whether to show poll or results, based on session attribute. --%>
 <core:ExclusiveIf>
   <core:If value="<%= ((atg.portal.gear.poll.PollTrackerBean) pollTracker).hasPoll(poll.getPollId()) %>">
     <% displayResults=true;
        alreadyVoted=true;
     %>
   </core:If>
   <core:If value='<%=gearID.equals(request.getParameter("pollGear")) && request.getParameter("viewPollResults")!=null%>'>
     <% displayResults=true; %>
   </core:If>
 </core:ExclusiveIf>
</dsp:getvalueof>

<core:ExclusiveIf>
 <core:If value='<%= displayResults %>' >


      <%-- ********************************************************************** --%>
      <%-- Poll Results Display  --%>
      <%-- ********************************************************************** --%>
      <table width="100%" border="0">

      <tr bgcolor="#<%= gearBGColor %>">
        <td>
        <font size="-1" class="small"><%=pollLabel%></font>
        </td>
      </tr>

      <tr bgcolor="#<%= gearBGColor %>">
        <td><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
      </tr>
      <%-- Poll Question --%>
      <tr bgcolor="#<%= gearBGColor %>">
        <td valign="top" align="left" ><font size="1" color="#<%=gearTextColor %>" class="small"><%=poll.getQuestionText()%></font></td>
      </tr>
     </table>

   <table width="100%" border="0">
   <%-- Poll Results --%>

   
  <tr bgcolor="#<%= highlightBGColor %>">
  <td colspan="4">
   <table border=0 width="100%" cellpadding="2" cellspacing="0">
    <tr bgcolor="#<%= highlightBGColor %>">
      <td><font size="1" color="#<%=highlightTextColor %>" class="small"><%=responseHeader%></font></td>
      <td><font size="1" color="#<%=highlightTextColor %>" class="small"><%=countHeader%>&nbsp;</font></td>
      <td colspan="2" align="center"><font size="1" color="#<%=highlightTextColor %>" class="small"><%=percentageHeader%></font></td>
   </tr>
   <core:ForEach id="pollResults"
              values="<%= poll.getPollResultList() %>"
                castClass="atg.portal.gear.poll.PollResult"
              elementId="pollResult">

      <tr bgcolor="#<%= resultsBGColor %>">
         <td  valign="middle" align="left" ><font size="1" color="#<%=gearTextColor %>" class="small">
          &nbsp;<%= pollResult.getResponseText() %> 
         </font></td>
         <td valign="middle" align="left" > <font size="1" color="#<%=gearTextColor %>" class="small">
          &nbsp;<%= pollResult.getCount() %>
         </font></td>
	 <% barColor=pollResult.getBarColor();
	    if (barColor==null) {
	      barColor="000000";
	    }
	  %>
         <td><table width="1" border="0"><tr><td bgcolor="#<%= barColor%>"><img src="<%=clearGif%>" height="3" width="<%= pollResult.getPercentage() %>" border="0"></td></tr></table>
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
        <td colspan="4"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
      </tr>

      <%-- Show link to vote if this is view-only mode for this gearID --%>
      <tr bgcolor="#<%= gearBGColor %>">
       <core:IfNot value='<%=alreadyVoted%>'>
        <core:CreateUrl id="voteUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
          <td colspan="4" align="left" >
         &nbsp;<font size="2"><a href="<%= voteUrl.getNewUrl() %>" class="gear_nav"><%=voteLink%></a></font>
          </td>
       </core:CreateUrl>
       </tr>
      <tr bgcolor="#<%= gearBGColor %>">
        <td colspan="4"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
      </tr>
       </core:IfNot>
     </table>

 </core:If>

 <core:DefaultCase>

<%-- ********************************************************************** --%>
<%-- Poll Form Display  --%>
<%-- ********************************************************************** --%>


   <core:ExclusiveIf>

     <core:IfNotNull value="<%= poll.getQuestionText()%>">


         <table  width="100%" cellpadding="0" border="0" cellspacing="0">
          <tr bgcolor="#<%= highlightBGColor %>">
          <td  align="left"><font size="-1" class="small"><%=pollLabel%>&nbsp;&nbsp;</font></td>
            <core:CreateUrl id="resultsUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
              <core:UrlParam param="<%=pollIdParam%>" value="<%=poll.getPollId()%>"/>
              <core:UrlParam param="viewPollResults" value="true"/>
              <core:UrlParam param="pollGear" value="<%=gearID%>"/>
              <core:if value="<%= hasFullMode %>">
	        <core:UrlParam param="paf_dm" value="full"/>
	        <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
              </core:if>
           <td align="right" nowrap><font size="2"><a href="<%= resultsUrl.getNewUrl() %>" class="gear_nav"><%=resultsLink%></a>&nbsp;</font></td>
      </core:CreateUrl>
</tr>
</table>

<font style="margin:4px"  size="1" color="#<%=gearTextColor %>" class="small"><%=poll.getQuestionText()%></font>

<table width="100%" border="0" cellpadding="1" cellspacing="0">

<%--  ****** Poll Response form ****** --%>
<dsp:form  method="post" action="<%= pafEnv.getOriginalRequestURI() %>">
<!-- need hidden vars for full gear mode -->
  <core:if value="<%= hasFullMode %>">
   <input type="hidden" name="paf_dm" value="full">
   <input type="hidden" name="paf_gear_id" value="<%=gearID%>">
  </core:if>

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

      <tr bgcolor="#<%= gearBGColor %>" width="10">
         <td align="center" valign="top" ><font size="1" color="#<%=gearTextColor %>" class="small">
          <dsp:input type="radio" bean="PollVoteFormHandler.pollSelection" value="<%= pollResponse.getRepositoryId() %>"/></font></td>
         <td width="80%" valign="top" align="left" ><font size="1" color="#<%=gearTextColor %>" class="small">
          <%= pollResponses.getCount() %>.&nbsp;<%= pollResponse.getPropertyValue("responseText") %> 
         </font></td>
      </tr>

   </core:ForEach>

   <tr bgcolor="#<%= gearBGColor %>">
      <td colspan="2"><img src="<%=clearGif%>" height="3" width="1" border="0"></td>
   </tr>

   <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="2" align="left"><font size="2" class="small">&nbsp;&nbsp;<dsp:input type="submit" value="<%= submitVoteButtonLabel %>" bean="PollVoteFormHandler.vote"/></td></font>
     </td>
    </tr>
   </table>
 <br>
</dsp:form>
   </core:IfNotNull>

   <%-- ********************************************************************** --%>
   <%-- No Poll Display  --%>
   <%-- ********************************************************************** --%>

   <core:DefaultCase>
      <font size="2" class="small"><%=noPollMessage%></font>
   </core:DefaultCase>

   </core:ExclusiveIf>  <%-- ifNotNull QuestionText --%>

   </core:DefaultCase>
   </core:ExclusiveIf>  <%-- poll tracker --%>


</poll:getPoll>
</core:demarcateTransaction>


</paf:InitializeGearEnvironment>
</dsp:page>
<!-- ******** End of Poll Gear display ******** -->
<%-- @version $Id: //app/portal/version/10.0.3/poll/poll.war/pollShared.jsp#2 $$Change: 651448 $--%>

