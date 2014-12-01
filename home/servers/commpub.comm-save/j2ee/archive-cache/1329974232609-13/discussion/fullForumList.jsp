
<%-- 
Page:   	fullForumList.jsp
Gear:  	 	Discussion Gear
gearmode: 	content (the default mode)
displayMode: 	full

Author:      	Jeff Banister
Description: 	This page is included by discussionFull.jsp, and displays a list of all
     		forums associated with the current gear instance.
     		The column headers allow sorting by forum name, lastPostDate, or number of posts.
     		A range parameter is used to limit the number of forums shown on a single page. If 
     		there are more forums than the range size, a "next" and/or "previous" link will be
     		shown.
--%>

<%@ taglib uri="/discussion-taglib" prefix="discuss" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<core:demarcateTransaction id="fullForumListXA">
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

<i18n:bundle baseName="<%= bundleName %>" changeResponseLocale="false" />
<i18n:message id="prevLink" key="prev-link"/>
<i18n:message id="nextLink" key="next-link"/>
<i18n:message id="forumHeader" key="forumHeader"/>
<i18n:message id="mostRecentHeader" key="mostRecentHeader"/>
<i18n:message id="numPostsHeader" key="numPostsHeader"/>
<i18n:message id="newPostLinkText" key="newPostLinkText"/>
<i18n:message id="postedByHeader" key="postedByHeader"/>
<i18n:message id="postDateHeader" key="postDateHeader"/>
<i18n:message id="noForumsMessage" key="noForumsMessage"/>
<i18n:message id="sharedViewLabel" key="sharedViewLabel"/>

<discuss:getGearForums id="forums" 
				gear="<%= gearID %>"
				startRange="<%=startRange%>"
	        		sortOrder="<%= sortOrder %>"
				rangeSize="<%=rangeSize%>">

<table width="95%" align="center" border="0">

<tr bgcolor="#<%= gearBGColor %>">
<td colspan="3" ><img src="/gear/discussion/images/clear.gif" height="3" width="684" border="0"></td>
</tr>

<tr>
  <td colspan="3" align="center"><font class="large_bold" color="#<%=gearTextColor%>"><%= sharedViewLabel %></font>
  </td>
</tr>

<tr bgcolor="#<%= gearBGColor %>">
<td colspan="3" ><img src="/gear/discussion/images/clear.gif" height="3" width="1" border="0"></td>
</tr>

<%-- Column headers --%>
<tr bgcolor="#<%= titleBGColor %>">
  <core:CreateUrl id="forumsURL" url="<%= origURI %>">
    <core:UrlParam param="paf_dm" value="full"/>
    <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
    <core:UrlParam param="action" value="forum_list"/>
    <core:UrlParam param="startRange" value="0"/>
    <core:UrlParam param="rangeSize" value="<%= rangeSize %>"/>
    <core:UrlParam param="sortOrder" value="name"/>
    <td valign="top" align="left" width="40%">
     &nbsp;&nbsp;<a href="<%= forumsURL.getNewUrl() %>"><font class="small" color="#<%= titleTextColor %>"><%=forumHeader%></font></a>&nbsp;&nbsp;
    </td>
  </core:CreateUrl>

  <core:CreateUrl id="forumsURL" url="<%= origURI %>">
    <core:UrlParam param="paf_dm" value="full"/>
    <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
    <core:UrlParam param="action" value="forum_list"/>
    <core:UrlParam param="startRange" value="0"/>
    <core:UrlParam param="rangeSize" value="<%= rangeSize %>"/>
    <core:UrlParam param="sortOrder" value="date"/>
    <td nowrap valign="top" align="left" width="40%">
     &nbsp;&nbsp;<a href="<%= forumsURL.getNewUrl() %>"><font class="small" color="#<%= titleTextColor %>"><%=mostRecentHeader%></font></a>&nbsp;&nbsp;
    </td>
  </core:CreateUrl>

  <core:CreateUrl id="forumsURL" url="<%= origURI %>">
    <core:UrlParam param="paf_dm" value="full"/>
    <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
    <core:UrlParam param="action" value="forum_list"/>
    <core:UrlParam param="startRange" value="0"/>
    <core:UrlParam param="rangeSize" value="<%= rangeSize %>"/>
    <core:UrlParam param="sortOrder" value="numPosts"/>
    <td nowrap valign="top" align="center" width="20%">
     &nbsp;&nbsp;<a href="<%= forumsURL.getNewUrl() %>"><font class="small" color="#<%= titleTextColor %>"><%=numPostsHeader%></font></a>&nbsp;&nbsp;
    </td>
  </core:CreateUrl>
</tr>

<tr bgcolor="#<%= gearBGColor %>">
<td colspan="3"><img src="/gear/discussion/images/clear.gif" height="5" width="1" border="0"></td>
</tr>

<core:ForEach id="forumsForEach"
	      values="<%= forums.getForumList() %>"
              castClass="atg.repository.RepositoryItem"
              elementId="forum">

   <tr bgcolor="#<%= gearBGColor %>">
      <td valign="top" align="left" width="50%"> <font class="small">
      <core:CreateUrl id="fullGearUrl" url="<%= origURI %>">
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="forumID" value="<%= forum.getRepositoryId()%>"/>
         <core:UrlParam param="action" value="topic_list"/>
         <core:UrlParam param="startRange" value="0"/>
         <core:UrlParam param="rangeSize" value="<%= topicDisplayCount%>"/>
         &nbsp;<a href="<%= fullGearUrl.getNewUrl() %>"><%= forum.getPropertyValue("name") %></a>&nbsp;
      </core:CreateUrl>
      </font></td>
      <td valign="top" align="left" width="30%"> <font class="small">
      &nbsp;<i18n:formatDateTime value='<%= (java.util.Date)forum.getPropertyValue("lastPostTime") %>' dateStyle="short" timeStyle="short" />
      </font></td>
      <td valign="top" align="center" width="20%"> <font class="small">
         <%=forum.getPropertyValue("numPosts")%>
      </font></td>
   </tr>

   <tr bgcolor="#<%= gearBGColor %>">
      <td colspan="3" ><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
   </tr>

   <tr>
       <td colspan="3"><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
   </tr>
</core:ForEach>

<%-- Next and previous links --%>
<tr>
<td colspan="3" ><img src="/gear/discussion/images/clear.gif" height="10" width="1" border="0"></td>
</tr>

<tr>
<td align="left"><font class="small">
  <core:ExclusiveIf>
   <core:If value="<%=  forums.getStartRangeInt() > 0 %>">
      <core:CreateUrl id="fullGearUrl" url="<%= origURI %>">
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="action" value="forum_list"/>
         <core:UrlParam param="startRange" value="<%= Integer.toString(forums.getPrevStartRangeInt()) %>"/>
         <core:UrlParam param="rangeSize" value="<%= rangeSize %>"/>
         <paf:encodeUrlParam param="sortOrder" value="<%= sortOrder %>"/>
         &nbsp;<a href="<%= fullGearUrl.getNewUrl() %>"><%=prevLink%>&nbsp;<%= rangeSize%></a>&nbsp;&nbsp;&nbsp;&nbsp;
      </core:CreateUrl>
   </core:If>

     <core:DefaultCase>
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
     </core:DefaultCase>
   </core:ExclusiveIf>

   <core:If value="<%=  ( forums.getStartRangeInt()+forums.getRangeSize() > 0) && (forums.getForumCount() > forums.getStartRangeInt()+forums.getRangeSize()) %>">
      <core:CreateUrl id="fullGearUrl" url="<%= origURI %>">
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="action" value="forum_list"/>
         <core:UrlParam param="startRange" value="<%= Integer.toString(forums.getStartRangeInt()+forums.getRangeSize()) %>"/>
         <core:UrlParam param="rangeSize" value="<%= rangeSize %>"/>
         <paf:encodeUrlParam param="sortOrder" value="<%= sortOrder %>"/>
         <a href="<%= fullGearUrl.getNewUrl() %>"><%=nextLink%>&nbsp;<%= rangeSize%></a>
      </core:CreateUrl>
   </core:If>
</font></td>
</tr>

</table>
</discuss:getGearForums>
</paf:InitializeGearEnvironment>
</dsp:page>
</core:demarcateTransaction>
<%-- @version $Id: //app/portal/version/10.0.3/discussion/discussion.war/fullForumList.jsp#2 $$Change: 651448 $--%>
