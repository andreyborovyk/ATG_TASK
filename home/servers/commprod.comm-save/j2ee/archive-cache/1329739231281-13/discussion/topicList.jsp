
<%-- 
Page:   	topicList.jsp
Gear:  	 	Discussion Gear
gearmode: 	content (the default mode)
displayMode: 	full

Author:      	Jeff Banister
Description: 	This page is included by discussionFull.jsp, and displays a list of topics for a 
     		single discussion forum. The column headers allow the list to be sorted by 
		each column.
     		A range parameter is used to limit the number of topics shown on a single 
		page. If there are more topics than the range size, a "next" and/or "previous" 
		link will be shown.
--%>

<%@ taglib uri="/discussion-taglib" prefix="discuss" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<core:demarcateTransaction id="topicListXA">
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
   String textLength=pafEnv.getGearInstanceParameter("displayTextLengthFull");
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

<i18n:bundle baseName="<%= bundleName %>" changeResponseLocale="false" />
<i18n:message id="prevLink" key="prev-link"/>
<i18n:message id="nextLink" key="next-link"/>
<i18n:message id="newPostLinkText" key="newPostLinkText"/>
<i18n:message id="topicHeader" key="topicHeader"/>
<i18n:message id="postedByHeader" key="postedByHeader"/>
<i18n:message id="numRepliesHeader" key="numRepliesHeader"/>
<i18n:message id="postDateHeader" key="postDateHeader"/>
<i18n:message id="noPostsMessage" key="noPostsMessage"/>

   <%-- put this here to work around bug where first URL doesn't get a ? before first param --%>
   <core:CreateUrl id="testURL" url="<%= origURI %>">
   </core:CreateUrl>

<discuss:getForumTopics id="forumTopics" 
   		forumID="<%= forumID %>"
	        sortOrder="<%= sortOrder %>"
		startRange="<%=startRange%>"
		rangeSize="<%=rangeSize%>">
		


<table width="98%" align="center" cellspacing="0" cellpadding="0" border="0">

   <tr bgcolor="#<%= gearBGColor%>">
      <td colspan="3" ><img src="/gear/discussion/images/clear.gif" height="10" width="1" border="0"></td>
   </tr>

   <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3" align="center"><font class="large_bold" color="#<%= gearTextColor %>">&nbsp;<%= forumTopics.getForum().getPropertyValue("name")%></font>
     </td>
   </tr>

   <tr bgcolor="#<%= gearBGColor%>">
      <td colspan="3" ><img src="/gear/discussion/images/clear.gif" height="5" width="1" border="0"></td>
   </tr>

  <tr bgcolor="#<%= gearBGColor %>">
   <core:CreateUrl id="postURL" url="<%= origURI %>">
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
      <core:UrlParam param="forumID" value="<%=forumID%>"/>
      <core:UrlParam param="action" value="post_message"/>
      <core:UrlParam param="trFlag" value="T"/>
      <td colspan="1" align="left">
      <a href="<%= postURL.getNewUrl() %>"><font class="small_bold"><%=newPostLinkText%></font></a>
      </td>
   </core:CreateUrl>
    
<%-- taking full forum list link out 
  <core:CreateUrl id="forumsURL" url="<%= origURI %>">
    <core:UrlParam param="paf_dm" value="full"/>
    <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
    <core:UrlParam param="action" value="forum_list"/>
    <core:UrlParam param="startRange" value="0"/>
    <core:UrlParam param="rangeSize" value='<%= pafEnv.getGearInstanceParameter("forumDisplayCountFull") %>'/>
    <td colspan=2  align="right">
     &nbsp;&nbsp;<a href="<%= forumsURL.getNewUrl() %>"><font class="small"><%=forumListLinkText%></font></a>&nbsp;&nbsp;
    </td>
  </core:CreateUrl>
    
--%>
  <td colspan="2">&nbsp;</td>
  </tr>   
  
   <tr bgcolor="#<%= gearBGColor%>">
      <td colspan="3" ><img src="/gear/discussion/images/clear.gif" height="5" width="1" border="0"></td>
      </tr>
</table>

<table width="98%" align="center" border="0" cellpadding="2" cellspacing="0">

<tr>
<td colspan="4" ><img src="/gear/discussion/images/clear.gif" height="3" width="1" border="0"></td>
</tr>

<%-- Create listing only if there are postings on this board --%>
<core:ExclusiveIf>

  <core:If value="<%= forumTopics.getTopicCount() <= 0 %>"> 
    <tr  bgcolor="#<%=gearBGColor %>">
      <td colspan="4"><font class="small" color="#<%= gearTextColor %>">&nbsp;<%=noPostsMessage%></font></td>
    </tr>
  </core:If>

<core:DefaultCase>

<%-- Column headers allow changing sort order --%>
<tr bgcolor="#<%= titleBGColor %>">
   <core:CreateUrl id="subjectURL" url="<%=origURI %>">
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
      <core:UrlParam param="forumID" value="<%=forumID %>"/>
      <core:UrlParam param="action" value="topic_list"/>
      <core:UrlParam param="sortOrder" value="subject"/>
      <core:UrlParam param="startRange" value="0"/>
      <core:UrlParam param="rangeSize" value="<%= rangeSize%>"/>
      <td valign="top" align="left"  width="40%"> 
      <a href="<%= subjectURL.getNewUrl() %>"><font class="small" color="#<%= titleTextColor %>"><%=topicHeader%></a>
      </td>
   </core:CreateUrl>
<%-- disabling the "sort by user" column header, since sorting on a 
     subproperty (forum.user.login) doesn't work

   <core:CreateUrl id="userURL" url="<%= origURI %>">
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
      <core:UrlParam param="forumID" value="<%= forumID %>"/>
      <core:UrlParam param="action" value="topic_list"/>
      <core:UrlParam param="sortOrder" value="user"/>
      <core:UrlParam param="startRange" value="0"/>
      <core:UrlParam param="rangeSize" value="<%= rangeSize%>"/>
      <td nowrap valign="top" align="left" width="30%">
      <a href="<%= userURL.getNewUrl() %>"><font class="small" color="#<%= titleTextColor %>"><%=postedByHeader%></a>
      </td>
   </core:CreateUrl>
--%>
      <td nowrap valign="top" align="left" width="30%">
      <font class="small" color="#<%= titleTextColor %>"><%=postedByHeader%>
      </td>

  <core:CreateUrl id="dateURL" url="<%= origURI %>">
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
      <core:UrlParam param="forumID" value="<%=forumID%>"/>
      <core:UrlParam param="action" value="topic_list"/>
      <core:UrlParam param="sortOrder" value="children"/>
      <core:UrlParam param="startRange" value="0"/>
      <core:UrlParam param="rangeSize" value="<%= rangeSize%>"/>
      <td valign="top" align="center" width="5">
      <a href="<%= dateURL.getNewUrl() %>"><font size="2" color="#<%= titleTextColor %>"><%=numRepliesHeader%></a>&nbsp;</td>
  </core:CreateUrl>

   <core:CreateUrl id="dateURL" url="<%= origURI %>">
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
      <core:UrlParam param="forumID" value="<%=forumID%>"/>
      <core:UrlParam param="action" value="topic_list"/>
      <core:UrlParam param="sortOrder" value="date"/>
      <core:UrlParam param="startRange" value="0"/>
      <core:UrlParam param="rangeSize" value="<%= rangeSize%>"/>
      <td nowrap valign="top" align="left" width="15%">
      <a href="<%= dateURL.getNewUrl() %>"><font class="small" color="#<%= titleTextColor %>"><%=postDateHeader%></a>
      </td>
   </core:CreateUrl>
</tr>

<tr bgcolor="#<%= gearBGColor %>">
<td colspan="4"><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
</tr>

   <% String userName=null; %>
   <core:ForEach id="topicsForEach"
              values="<%= forumTopics.getTopicList() %>"
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
          <core:CreateUrl id="topicURL" url="<%= origURI %>">
             <core:UrlParam param="paf_dm" value="full"/>
             <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
             <core:UrlParam param="forumID" value="<%=forumID%>"/>
             <core:UrlParam param="topicID" value="<%= topic.getRepositoryId()%>"/>
             <core:UrlParam param="action" value="thread_list"/>
             <core:UrlParam param="startRange" value="0"/>
             <core:UrlParam param="rangeSize" value="<%= threadCount%>"/>
  	     <td valign="top" align="left" width="50%">
             <a href="<%= topicURL.getNewUrl() %>"><font size="2"><%= topic.getPropertyValue("subject") %></a>
             </td>
          </core:CreateUrl>
  	<td valign="top" align="left" width="30%">
 	<font class="small" color="#<%= gearTextColor %>">
	<%=userName%>
	</font></td>
  	<td valign="top" align="center" width="10%">
	<font class="small" color="#<%= gearTextColor %>">
	  <%= topic.getPropertyValue("childrenQty") %>
	</font></td>
  	<td nowrap rowspan="2" valign="top" align="left" width="10%">
	<font class="small" color="#<%= gearTextColor %>">
          <i18n:formatDateTime value='<%= (java.util.Date) topic.getPropertyValue("creationDate") %>' dateStyle="short" timeStyle="short" />&nbsp;
	</td>
      </tr>

      <tr bgcolor="#<%= gearBGColor %>">
  	<td valign="top" align="left">
	<font class="smaller" color="#<%= gearTextColor %>">
          <core:If value='<%= topic.getPropertyValue("content") !=null %>'>
	     <%=  forumTopics.trimText(topic.getPropertyValue("content").toString(), textLength) %>
	  </core:If>
	</font></td>
      </tr>
      <tr bgcolor="#<%= gearBGColor %>">
        <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="3" width="1" border="0"></td>
      </tr>

      <tr>
        <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
      </tr>
   </core:ForEach>

<%-- Next and previous links --%>
<tr>
<td align="left"><font class="small">
   <core:ExclusiveIf>
     <core:If value="<%=  forumTopics.getStartRangeInt() > 0 %>">
      <core:CreateUrl id="fullGearUrl" url="<%= origURI %>">
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="action" value="topic_list"/>
         <paf:encodeUrlParam param="sortOrder" value="<%= sortOrder %>"/>
         <core:UrlParam param="forumID" value="<%=forumID %>"/>
         <core:UrlParam param="startRange" value="<%= Integer.toString(forumTopics.getPrevStartRangeInt()) %>"/>
         <core:UrlParam param="rangeSize" value="<%= rangeSize %>"/>
         <a href="<%= fullGearUrl.getNewUrl() %>"><font class="small"><%=prevLink%>&nbsp;<%= rangeSize%></a>&nbsp;&nbsp;&nbsp;&nbsp;
      </core:CreateUrl>
     </core:If>

     <core:DefaultCase>
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
     </core:DefaultCase>
   </core:ExclusiveIf>

   <core:If value="<%=  ( forumTopics.getStartRangeInt()+forumTopics.getRangeSize() > 0) && (forumTopics.getTopicCount() > forumTopics.getStartRangeInt()+forumTopics.getRangeSize()) %>">
      <core:CreateUrl id="fullGearUrl" url="<%= origURI %>">
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="action" value="topic_list"/>
         <paf:encodeUrlParam param="sortOrder" value="<%= sortOrder %>"/>
         <core:UrlParam param="forumID" value="<%=forumID %>"/>
         <core:UrlParam param="startRange" value="<%= Integer.toString(forumTopics.getStartRangeInt()+forumTopics.getRangeSize()) %>"/>
         <core:UrlParam param="rangeSize" value="<%= rangeSize %>"/>
         <a href="<%= fullGearUrl.getNewUrl() %>"><font class="small"><%=nextLink%>&nbsp;<%= rangeSize%></a>
      </core:CreateUrl>
   </core:If>
</font></td>
</tr>

      <tr>
        <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="5" width="1" border="0"></td>
      </tr>

</core:DefaultCase>
</core:ExclusiveIf>

</table>


</discuss:getForumTopics>
</paf:InitializeGearEnvironment>
</dsp:page>
</core:demarcateTransaction>
<%-- @version $Id: //app/portal/version/10.0.3/discussion/discussion.war/topicList.jsp#2 $$Change: 651448 $--%>
