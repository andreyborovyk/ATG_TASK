
<%-- 
Page:   	threadList.jsp
Gear:  	 	Discussion Gear
gearmode: 	content (the default mode)
displayMode: 	full

Author:      	Jeff Banister
Description: 	This page is included by discussionFull.jsp, and displays a discussion 
		topic, along with a list of all responses (i.e. a thread).
     		A range parameter is used to limit the number of responses shown on a 
		single page. If there are more responses than the range size, a "next" 
		and/or "previous" link will be shown.
--%>

<%@ taglib uri="/discussion-taglib" prefix="discuss" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<core:demarcateTransaction id="threadListXA">
<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">

<paf:setFrameworkLocale/>

<%
   String origURI=pafEnv.getOriginalRequestURI();
   String gearID=pafEnv.getGear().getId();

   String startRange = request.getParameter("startRange");
   String rangeSize = request.getParameter("rangeSize");
   String topicDisplayCount=pafEnv.getGearInstanceParameter("topicDisplayCountFull");
   String forumID=request.getParameter("forumID"); 
   String topicID=request.getParameter("topicID"); 
   String threadCount=pafEnv.getGearInstanceParameter("threadDisplayCount");
   String sortOrder=request.getParameter("sortOrder");

   atg.portal.framework.ColorPalette cp = pafEnv.getPage().getColorPalette();
   // String titleBGColor = cp.getGearTitleBackgroundColor();
   // String titleTextColor = cp.getGearTitleTextColor();
   String titleBGColor = cp.getHighlightBackgroundColor();
   String titleTextColor = cp.getHighlightTextColor();
   String gearBGColor = cp.getGearBackgroundColor();
   String gearTextColor = cp.getGearTextColor();

   String bundleName=pafEnv.getGearInstanceParameter("resourceBundle");
   // default to old (pre-5.6) name, in case new file not installed
   if (bundleName==null || bundleName.equals("")) {
     bundleName="atg.gears.discussion.discussion";
   } 

%>

<i18n:bundle baseName="<%= bundleName %>"  changeResponseLocale="false" />
<i18n:message id="subjectLabel" key="subject-label"/>
<i18n:message id="postedbyLabel" key="postedby-label"/>
<i18n:message id="prevLink" key="prev-link"/>
<i18n:message id="nextLink" key="next-link"/>
<i18n:message id="messageLabel" key="message-label"/>
<i18n:message id="numPostsHeader" key="numPostsHeader"/>
<i18n:message id="topicHeader" key="topicHeader"/>
<i18n:message id="postedByHeader" key="postedByHeader"/>
<i18n:message id="numRepliesHeader" key="numRepliesHeader"/>
<i18n:message id="postDateHeader" key="postDateHeader"/>
<i18n:message id="noForumsMessage" key="noForumsMessage"/>
<i18n:message id="topicListLinkText" key="topicListLinkText"/>
<i18n:message id="noResponsesMessage" key="noResponsesMessage"/>
<i18n:message id="replyToLinkText" key="replyToLinkText"/>

   <%-- put this here to work around bug where first URL doesn't get a ? before first param --%>
   <core:CreateUrl id="testURL" url="<%= origURI %>">
   </core:CreateUrl>

<discuss:getTopicThreads id="topicThreads" 
   		forumID='<%= request.getParameter("forumID") %>'
   		topicID='<%= request.getParameter("topicID") %>'
   		sortOrder="<%= sortOrder %>"
		startRange="<%=startRange%>"
		rangeSize="<%=rangeSize%>">

    <%-- Had to do this stuff to avoid null pointer exception on login name. For some reason,
         using core:ExclusiveIf to filter both null user and null login didn't work. --%>
    <% String parentUserName="anonymous";
       if (topicThreads.getTopic().getPropertyValue("user")!=null) {
         if ( ((atg.repository.RepositoryItem) topicThreads.getTopic().getPropertyValue("user")).getPropertyValue("login")!=null) {
	   parentUserName= (String) ((atg.repository.RepositoryItem) topicThreads.getTopic().getPropertyValue("user")).getPropertyValue("login");
	 }
       }
    
    %>

<table width="100%" cellspacing="0" cellpadding="0" border="0">

<tr bgcolor="#<%= gearBGColor %>">
<td colspan="4"><img src="/gear/discussion/images/clear.gif" height="5" width="1" border="0"></td>
</tr>

<tr bgcolor="#<%= gearBGColor %>">
   <core:CreateUrl id="topicsURL" url="<%=origURI %>">
       <core:UrlParam param="paf_dm" value="full"/>
       <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
       <core:UrlParam param="forumID" value="<%=forumID %>"/>
       <core:UrlParam param="action" value="topic_list"/>
       <core:UrlParam param="startRange" value="0"/>
       <core:UrlParam param="rangeSize" value="<%= topicDisplayCount%>"/>
       <td nowrap align="left">
       &nbsp;<a href="<%= topicsURL.getNewUrl() %>"><font class="small_bold"><%=topicListLinkText%></font></a>
       </td>
   </core:CreateUrl>
      <td>&nbsp;</td>
   <core:CreateUrl id="postURL" url="<%=origURI %>">
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
      <core:UrlParam param="forumID" value="<%=forumID %>"/>
      <core:UrlParam param="ultimateID" value="<%=topicID%>"/>
      <core:UrlParam param="parentID" value="<%=topicID%>"/>
      <core:UrlParam param="action" value="post_message"/>
      <core:UrlParam param="trFlag" value="R"/>
      <td nowrap colspan="2" align="right">
      <a href="<%= postURL.getNewUrl() %>"><font class="small_bold"><%=replyToLinkText%></font></a>&nbsp;
      </td>
   </core:CreateUrl>
</tr>

<tr bgcolor="#<%= gearBGColor %>">
<td colspan="4"><img src="/gear/discussion/images/clear.gif" height="10" width="1" border="0"></td>
</tr>

<tr bgcolor="#<%= gearBGColor %>">
  <td align="left" width="15%">
   <font class="small" color="#<%= gearTextColor %>"><b>&nbsp;<%=subjectLabel%>: </b>
  </td>
  <td colspan="2" align="left" width="85%"><font class="small" color="#<%= gearTextColor %>">
    <%= topicThreads.getTopic().getPropertyValue("subject")%>
  </font></td>
      <td>&nbsp;</td>
</tr>

<tr bgcolor="#<%= gearBGColor %>">
<td colspan="4"><img src="/gear/discussion/images/clear.gif" height="5" width="1" border="0"></td>
</tr>

<tr bgcolor="#<%= gearBGColor %>">
   <td align="left" valign="top"><font class="small" color="#<%= gearTextColor %>">
      <b>&nbsp;<%=messageLabel%>: </b>
   </font></td>
   <td colspan="3"><font class="small" color="#<%= gearTextColor %>">
    <core:IfNotNull value='<%= topicThreads.getTopic().getPropertyValue("content") %>'>
        <%= topicThreads.getTopic().getPropertyValue("content") %>
    </core:IfNotNull>
   </font></td>
</tr>

<tr bgcolor="#<%= gearBGColor %>">
<td colspan="4"><img src="/gear/discussion/images/clear.gif" height="5" width="1" border="0"></td>
</tr>

<tr bgcolor="#<%= gearBGColor %>">
   <td align="left"><font class="small" color="#<%= gearTextColor %>">
      <b>&nbsp;<%=postedbyLabel%>: </b>
   </font></td>
   <td colspan="2" align="left"><font class="small" color="#<%= gearTextColor %>">
    <%= parentUserName %>
   </font></td>
   <td>&nbsp;</td>
</tr>

<tr bgcolor="#<%= gearBGColor %>">
<td colspan="4"><img src="/gear/discussion/images/clear.gif" height="25" width="1" border="0"></td>
</tr>

</table>

<table width="100%" cellspacing="0" cellpadding="0" border="0">


<%-- Create listing only if there are postings on this board --%>
<core:ExclusiveIf>

  <core:If value="<%= topicThreads.getThreadCount() <= 0 %>">

    <tr bgcolor="#<%= gearBGColor %>">
        <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="3" width="1" border="0"></td>
    </tr>

    <tr  bgcolor="#<%=gearBGColor %>">
      <td><font class="small" color="#<%= gearTextColor %>">&nbsp;<%=noResponsesMessage%></font></td>
    </tr>
    <tr bgcolor="#<%= gearBGColor %>">
        <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="3" width="1" border="0"></td>
    </tr>

  </core:If>

  <core:DefaultCase>

<%--  REMOVE COLUMN HEADERS
      <tr bgcolor="#<%= titleBGColor %>">
         <core:CreateUrl id="subjectURL" url="<%=origURI %>">
            <core:UrlParam param="paf_dm" value="full"/>
            <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
            <core:UrlParam param="forumID" value="<%=forumID %>"/>
            <core:UrlParam param="topicID" value="<%=topicID%>"/>
            <core:UrlParam param="action" value="thread_list"/>
            <core:UrlParam param="sortOrder" value="subject"/>
            <core:UrlParam param="startRange" value="0"/>
            <core:UrlParam param="rangeSize" value="<%= rangeSize%>"/>
            <td valign="top" align="left" width="40%"> 
            &nbsp;<a href="<%= subjectURL.getNewUrl() %>"><font sclass="small" color="#<%= titleTextColor %>"><%=subjectLabel%></font></a>
            </td>
         </core:CreateUrl>

         <core:CreateUrl id="userURL" url="<%=origURI %>">
            <core:UrlParam param="paf_dm" value="full"/>
            <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
            <core:UrlParam param="forumID" value="<%=forumID%>"/>
            <core:UrlParam param="topicID" value="<%=topicID%>"/>
            <core:UrlParam param="action" value="thread_list"/>
            <core:UrlParam param="sortOrder" value="user"/>
            <core:UrlParam param="startRange" value="0"/>
            <core:UrlParam param="rangeSize" value="<%= rangeSize%>"/>
            <td valign="top" align="left" width="30%">
            <a href="<%= userURL.getNewUrl() %>"><font class="small" color="#<%= titleTextColor %>"><%=postedbyLabel%></font></a>
            </td>
         </core:CreateUrl>
         <core:CreateUrl id="dateURL" url="<%=origURI %>">
            <core:UrlParam param="paf_dm" value="full"/>
            <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
            <core:UrlParam param="forumID" value="<%= forumID %>"/>
            <core:UrlParam param="topicID" value="<%=topicID%>"/>
            <core:UrlParam param="action" value="thread_list"/>
            <core:UrlParam param="sortOrder" value="creationDate%20DESC"/>
            <core:UrlParam param="startRange" value="0"/>
            <core:UrlParam param="rangeSize" value="<%= rangeSize%>"/>
            <td valign="top" align="center" width="15%">
            <a href="<%= dateURL.getNewUrl() %>"><font class="small" color="#<%= titleTextColor %>"><%=pafEnv.postDateHeader%></font></a>
            </td>
         </core:CreateUrl>
         <td valign="top" align="right" width="15%"><font color="#<%=titleTextColor%>" class="small"></font>&nbsp;</td>
      </tr>

<tr bgcolor="#<%= titleBGColor %>">
<td colspan="4"><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
</tr>

<tr>
<td colspan="4" bgcolor="#666666" ><img src="/gear/discussion/images/clear.gif" height="1" width="1" border="0"></td>
</tr>

--%>

         <% String userName=null; %>
         <core:ForEach id="topicsForEach"
              values="<%= topicThreads.getThreadList() %>"
              castClass="atg.repository.RepositoryItem"
              elementId="topic">

	    <%-- Had to do this stuff to avoid null pointer exception on login name. For some reason,
	         using core:ExclusiveIf to filter both null user and null login didn't work. --%>
	    <% userName="anonymous";
	       if (topic.getPropertyValue("user")!=null) {
	         if ( ((atg.repository.RepositoryItem) topic.getPropertyValue("user")).getPropertyValue("login")!=null) {
		   userName= (String) ((atg.repository.RepositoryItem) topic.getPropertyValue("user")).getPropertyValue("login");
		 }
	       }
	    
	    %>
	    <tr bgcolor="#<%= gearBGColor %>">
        	<td valign="top" align="left" width="10%"><font class="small" color="#<%= gearTextColor %>"><b>
                   &nbsp;<%= userName %>
                   </b></font>
            	</font></td>
        	<td  nowrap valign="top" align="left" width="95%"><font class="smaller" color="#<%= gearTextColor %>">
                  <%= postDateHeader%>&nbsp;<i18n:formatDateTime value='<%= (java.util.Date) topic.getPropertyValue("creationDate") %>' dateStyle="short" timeStyle="short" />&nbsp;
      	        </font></td>
                <td>&nbsp;</td>
            </tr>
            <tr bgcolor="#<%= gearBGColor %>">
              <td colspan="3"><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
            </tr>

            <tr bgcolor="#<%= gearBGColor %>">
              <td>&nbsp;</td>
  	      <td valign="top" align="left" colspan="4"><font class="small" color="#<%= gearTextColor %>">
    		<core:If value='<%= topic.getPropertyValue("content") !=null %>'>
        		<%= topic.getPropertyValue("content") %>
    		</core:If>
	      </font></td>
              <td>&nbsp;</td>
            </tr>
            <tr bgcolor="#<%= gearBGColor %>">
              <td colspan="3"><img src="/gear/discussion/images/clear.gif" height="10" width="1" border="0"></td>
            </tr>

            <tr>
              <td colspan="3"><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
            </tr>
         </core:ForEach>

      <%-- Next and previous links --%>
      <tr>
      <td colspan="2" align="left">
   <core:ExclusiveIf>
      <core:If value="<%=  topicThreads.getStartRangeInt() > 0 %>">
            <core:CreateUrl id="fullGearUrl" url="<%= origURI %>">
               <core:UrlParam param="paf_dm" value="full"/>
               <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
               <core:UrlParam param="action" value="thread_list"/>
               <paf:encodeUrlParam param="sortOrder" value="<%= sortOrder %>"/>
               <core:UrlParam param="forumID" value="<%=forumID %>"/>
               <core:UrlParam param="topicID" value="<%=topicID%>"/>
               <core:UrlParam param="startRange" value="<%= Integer.toString(topicThreads.getPrevStartRangeInt()) %>"/>
               <core:UrlParam param="rangeSize" value="<%= rangeSize %>"/>
               &nbsp;<a href="<%= fullGearUrl.getNewUrl() %>"><font class="small"><%=prevLink%>&nbsp;<%= rangeSize%></font></a>&nbsp;&nbsp;&nbsp;&nbsp;
            </core:CreateUrl>
      </core:If>

     <core:DefaultCase>
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
     </core:DefaultCase>
   </core:ExclusiveIf>

      <core:If value="<%=  ( topicThreads.getStartRangeInt()+topicThreads.getRangeSize() > 0) && (topicThreads.getThreadCount() > topicThreads.getStartRangeInt()+topicThreads.getRangeSize()) %>">
            <core:CreateUrl id="fullGearUrl" url="<%= origURI %>">
               <core:UrlParam param="paf_dm" value="full"/>
               <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
               <core:UrlParam param="action" value="thread_list"/>
               <paf:encodeUrlParam param="sortOrder" value="<%= sortOrder %>"/>
               <core:UrlParam param="forumID" value="<%=forumID %>"/>
               <core:UrlParam param="topicID" value="<%=topicID%>"/>
               <core:UrlParam param="startRange" value="<%= Integer.toString(topicThreads.getStartRangeInt()+topicThreads.getRangeSize()) %>"/>
               <core:UrlParam param="rangeSize" value="<%= rangeSize %>"/>
               <a href="<%= fullGearUrl.getNewUrl() %>"><font class="small"><%=nextLink%>&nbsp;<%= rangeSize%></font></a>
            </core:CreateUrl>
      </core:If>
      </td>
      </tr>

      <tr bgcolor="#<%= gearBGColor %>">
          <td colspan="3"><img src="/gear/discussion/images/clear.gif" height="3" width="1" border="0"></td>
      </tr>

<tr bgcolor="#<%= gearBGColor %>">
   <core:CreateUrl id="topicsURL" url="<%=origURI %>">
       <core:UrlParam param="paf_dm" value="full"/>
       <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
       <core:UrlParam param="forumID" value="<%=forumID %>"/>
       <core:UrlParam param="action" value="topic_list"/>
       <core:UrlParam param="startRange" value="0"/>
       <core:UrlParam param="rangeSize" value="<%= topicDisplayCount%>"/>
       <td nowrap align="left">
       &nbsp;<a href="<%= topicsURL.getNewUrl() %>"><font class="small"><%=topicListLinkText%></font></a>
       </td>
   </core:CreateUrl>
   <core:CreateUrl id="postURL" url="<%=origURI %>">
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
      <core:UrlParam param="forumID" value="<%=forumID %>"/>
      <core:UrlParam param="ultimateID" value="<%=topicID%>"/>
      <core:UrlParam param="parentID" value="<%=topicID%>"/>
      <core:UrlParam param="action" value="post_message"/>
      <core:UrlParam param="trFlag" value="R"/>
      <td nowrap colspan="2" align="right">
      <a href="<%= postURL.getNewUrl() %>"><font class="small"><%=replyToLinkText%></font></a>&nbsp;
      </td>
   </core:CreateUrl>
</tr>
      <tr bgcolor="#<%= gearBGColor %>">
          <td colspan="3"><img src="/gear/discussion/images/clear.gif" height="5" width="1" border="0"></td>
      </tr>

   </core:DefaultCase>
</core:ExclusiveIf>


   </table>

</discuss:getTopicThreads>
</paf:InitializeGearEnvironment>
</dsp:page>
</core:demarcateTransaction>
<%-- @version $Id: //app/portal/version/10.0.3/discussion/discussion.war/threadList.jsp#2 $$Change: 651448 $--%>
