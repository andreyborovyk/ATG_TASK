<%-- 
Page:   	discussionShared.jsp
Gear:  	 	Discussion Gear
gearmode: 	content (the default mode)
displayMode: 	shared

Author:      	Jeff Banister
Description: 	This is the shared view of the discussion gear.  It will display 
		either a list of discussion boards, or if there is only one board, 
		will show the topic list for that board.
--%>

<!-- Begin discussion Gear display -->
<%@ taglib uri="/discussion-taglib" prefix="discuss" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<core:demarcateTransaction id="discussionSharedPageXA">

<dsp:page>

<paf:InitializeGearEnvironment id="pafEnv">

<paf:setFrameworkLocale/>

<%
   String origURI=pafEnv.getOriginalRequestURI();
   String gearID=pafEnv.getGear().getId();
   String userID=null;
   atg.portal.framework.ColorPalette cp = pafEnv.getPage().getColorPalette();
   String titleBGColor = cp.getGearTitleBackgroundColor();
   String titleTextColor = cp.getGearTitleTextColor();
   String gearBGColor = cp.getGearBackgroundColor();
   String gearTextColor = cp.getGearTextColor();
   String highlightTextColor = cp.getHighlightTextColor();
   String highlightBGColor = cp.getHighlightBackgroundColor();
   if (cp.getHighlightBackgroundColor() == null || cp.getHighlightBackgroundColor() == "" ) {
    highlightTextColor = "000000";
    highlightBGColor   = "cccccc";
   }
   String textLength=pafEnv.getGearInstanceParameter("displayTextLengthShared");

   String bundleName=pafEnv.getGearInstanceParameter("resourceBundle");
   // default to old (pre-5.6) name, in case new file not installed
   if (bundleName==null || bundleName.equals("")) {
     bundleName="atg.gears.discussion.discussion";
   } 
%>

<i18n:bundle baseName="<%= bundleName %>" changeResponseLocale="false" />
<i18n:message id="allForumsLink" key="all-forums-link"/>
<i18n:message id="allTopicsLink" key="all-topics-link"/>
<i18n:message id="forumHeader" key="forumHeader"/>
<i18n:message id="mostRecentHeader" key="mostRecentHeader"/>
<i18n:message id="numPostsHeader" key="numPostsHeader"/>
<i18n:message id="newPostLinkText" key="newPostLinkText"/>
<i18n:message id="topicHeader" key="topicHeader"/>
<i18n:message id="postedByHeader" key="postedByHeader"/>
<i18n:message id="numRepliesHeader" key="numRepliesHeader"/>
<i18n:message id="postDateHeader" key="postDateHeader"/>
<i18n:message id="noForumsMessage" key="noForumsMessage"/>

<%-- get user ID if logged in --%>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:droplet name="Switch">
  <dsp:param bean="Profile.transient" name="value"/>
  <dsp:oparam name="false">
     <dsp:getvalueof id="profile" bean="Profile">
          <% userID= ((atg.repository.RepositoryItem) profile).getRepositoryId(); %>
     </dsp:getvalueof>
  </dsp:oparam>
</dsp:droplet>


<discuss:getGearForums id="forums"
 				gear="<%= gearID %>"
 				user="<%= userID %>"
                                rangeSize='<%=pafEnv.getGearInstanceParameter("forumDisplayCountShared") %>'>


<core:ExclusiveIf>

<%-- ********************************************************************** --%>
<%-- Forum List Display  --%>
<%-- ********************************************************************** --%>

  <core:If value="<%= forums.getForumCount() > 1 %>">

   <table width="99%" align="center" border="0" cellpadding="0">

   <%-- Column headers --%>
   <tr bgcolor="#<%= highlightBGColor %>">
   <td valign="bottom" align="left" width="50%"><font  color="#<%=highlightTextColor %>" class="small">&nbsp;<%=forumHeader%></font></td>
   <td valign="bottom" align="left" width="30%"><font  color="#<%=highlightTextColor %>" class="small"><%=mostRecentHeader%></font></td>
   <td valign="bottom" align="left" width="20%"><font  color="#<%=highlightTextColor %>" class="small"><%=numPostsHeader%></font></td>
   </tr>

   <tr bgcolor="#<%= gearBGColor %>">
   <td colspan="3"><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
   </tr>

   <core:ForEach id="forumsForEach"
              values="<%= forums.getForumList() %>"
                castClass="atg.repository.RepositoryItem"
              elementId="forum">

   <tr bgcolor="#<%= gearBGColor %>">
      <core:CreateUrl id="fullGearUrl" url="<%= origURI%>">
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="forumID" value="<%= forum.getRepositoryId()%>"/>
         <core:UrlParam param="action" value="topic_list"/>
         <core:UrlParam param="startRange" value="0"/>
         <core:UrlParam param="rangeSize" value='<%= pafEnv.getGearInstanceParameter("topicDisplayCountFull") %>'/>
         <td valign="top" align="left" width="50%"><font  class="small">
         <a href="<%= fullGearUrl.getNewUrl() %>" class="gear_content"><%= forum.getPropertyValue("name") %></a></font>
         </td>
      </core:CreateUrl>
      <td valign="top" align="left" width="30%"><font  color="#<%=gearTextColor %>" class="small">
      <i18n:formatDateTime value='<%= (java.util.Date) forum.getPropertyValue("lastPostTime") %>' dateStyle="short" timeStyle="short" />
      </font></td>
      <td valign="top" align="center" width="20%"><font  color="#<%=gearTextColor %>" class="small">
         <%=forum.getPropertyValue("numPosts") %>
      </font></td>
   </tr>

   </core:ForEach>

   <!--removed old horizontal line style
   <tr bgcolor="#<%= gearBGColor %>">
   <td colspan="3"><img src="/gear/discussion/images/clear.gif" height="3" width="1" border="0"></td>
   </tr>
   -->

    <tr>
	         <td colspan="3"><hr size="1" noshade></td>
      </tr>

   <tr bgcolor="#<%= gearBGColor %>">
      <core:CreateUrl id="fullGearUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
         <core:UrlParam param="action" value="forum_list"/>
         <core:UrlParam param="startRange" value="0"/>
         <core:UrlParam param="rangeSize" value='<%= pafEnv.getGearInstanceParameter("forumDisplayCountFull") %>'/>
         <td colspan="3"><font size="2" class="small">
         <a href="<%= fullGearUrl.getNewUrl() %>" class="gear_nav"><%= allForumsLink %></a></font>
         </td>
      </core:CreateUrl>
    </tr>
  </table>
</core:If>

<%-- ********************************************************************** --%>
<%-- Single Thread Display  --%>
<%-- ********************************************************************** --%>

   <core:If value="<%= forums.getForumCount() == 1 %>">



   <% String rangeSize=pafEnv.getGearInstanceParameter("topicDisplayCountShared");
      String fullRangeSize=pafEnv.getGearInstanceParameter("topicDisplayCountFull");
      String sortOrder=request.getParameter("sortOrder");
      // horribly expensive - replacing this with new tag feature to return a single forum ID
      //String forumIDold= ((atg.repository.RepositoryItem) forums.getForumList()[0]).getRepositoryId();
      String forumID=forums.getForumId();
      String threadCount=pafEnv.getGearInstanceParameter("threadDisplayCount");
   %>

   <discuss:getForumTopics id="forumTopics"
   		forumID="<%= forumID %>"
	        sortOrder="<%= sortOrder %>"
		startRange="0"
		rangeSize="<%= rangeSize %>">



   <table width="99%" align="center" border="0" cellpadding="0">

   <tr>
      <td colspan="2" align="left"><font size="2" class="small_bold"><%= forumTopics.getForum().getPropertyValue("name")%></font>
      </td>

   <core:CreateUrl id="postURL" url="<%= origURI %>">
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
      <core:UrlParam param="forumID" value="<%=forumID%>"/>
      <core:UrlParam param="action" value="post_message"/>
      <core:UrlParam param="trFlag" value="T"/>
      <td colspan="2" nowrap align="right"><font class="small">
      <a href="<%= postURL.getNewUrl() %>" class="gear_nav"><%=newPostLinkText%></a>&nbsp;
      </font></td>
   </core:CreateUrl>

   </tr>
<%--
   <tr>
   <td colspan="4" ><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
   </tr>
--%>
   <%-- Column headers do not allow changing sort order in shared view --%>
   <tr bgcolor="#<%= highlightBGColor %>">
      <td nowrap valign="bottom" align="left"  width="40%"><font  color ="#<%= highlightTextColor %>" class="small"><%=topicHeader%></font></td>

      <td nowrap valign="bottom" align="left" width="30%"><font  color ="#<%=highlightTextColor %>" class="small"><%=postedByHeader%></font></td>

      <td valign="bottom" align="center" width="5"><font  color ="#<%= highlightTextColor %>" class="small"><%=numRepliesHeader%></font></td>

      <td nowrap valign="bottom" align="right" width="15%"><font  color ="#<%= highlightTextColor %>" class="small">&nbsp;<%=postDateHeader%>&nbsp;</font></td>
   </tr>

   <tr bgcolor="#<%=gearBGColor %>">
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
             <font color="#<%= gearTextColor %>" class="small">
             <a href="<%= topicURL.getNewUrl() %>" class="gear_content"><%= topic.getPropertyValue("subject") %></a>
             </font>
          </core:CreateUrl>
	</font></td>
  	<td valign="top" align="left" width="20%">
 	<font color="#<%= gearTextColor %>" class="small">
 	  <%= userName %>
	</font></td>
  	<td valign="top" align="center" width="10%">
	<font color="#<%= gearTextColor %>" class="small">
	  <%= topic.getPropertyValue("childrenQty") %>
	</font></td>
  	<td rowspan="2" valign="top" align="left" width="20%">
	<font color="#<%= gearTextColor %>" class="small">
          <i18n:formatDateTime value='<%= (java.util.Date) topic.getPropertyValue("creationDate") %>' dateStyle="short" timeStyle="short" />
	</font></td>
      </tr>

      <tr bgcolor="#<%= gearBGColor %>">
  	<td valign="top" align="left" colspan=3>
	<font color="#<%= gearTextColor %>" class="smaller">
          <core:If value='<%= topic.getPropertyValue("content") !=null %>'>
	    <%=  forumTopics.trimText(topic.getPropertyValue("content").toString(), textLength) %>
	  </core:If>
	</font></td>
      </tr>
<%--
      <tr bgcolor="#<%= gearBGColor %>">
        <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="3" width="1" border="0"></td>
      </tr>
--%>
      <tr>
        <td colspan="4"><hr size="1" noshade></td>
      </tr>
   </core:ForEach>
              <tr>
                <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
              </tr>
              <tr>
           <core:CreateUrl id="allTopicsUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
              <core:UrlParam param="paf_dm" value="full"/>
              <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
              <core:UrlParam param="action" value="topic_list"/>
              <core:UrlParam param="forumID" value="<%= forumID %>"/>
              <core:UrlParam param="startRange" value="0"/>
              <core:UrlParam param="rangeSize" value="<%= fullRangeSize %>"/>
                <td colspan="2"><font size="-1" class="small">
                <a href="<%= allTopicsUrl.getNewUrl() %>" class="gear_content"><%=allTopicsLink %>&nbsp;<%=forumTopics.getForum().getPropertyValue("name")%></a>
                </font></td>
           </core:CreateUrl>
           <core:CreateUrl id="fullGearUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
              <core:UrlParam param="paf_dm" value="full"/>
              <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
              <core:UrlParam param="forumID" value="<%= forumID %>"/>
              <core:UrlParam param="action" value="forum_list"/>
              <core:UrlParam param="startRange" value="0"/>
              <core:UrlParam param="rangeSize" value='<%= pafEnv.getGearInstanceParameter("forumDisplayCountFull") %>'/>
                <td colspan="2" align="right"><font size="-1" class="small">
                <a href="<%= fullGearUrl.getNewUrl() %>" class="gear_nav"><%= allForumsLink %></a>&nbsp;
                </font></td>
           </core:CreateUrl>
              </tr>
              <tr>
                <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
              </tr>
   </table>


   </discuss:getForumTopics>
   </core:If>

   <%-- ********************************************************************** --%>
   <%-- No Forum Display  --%>
   <%-- ********************************************************************** --%>

   <core:DefaultCase>
   <font size="2" class="small"><%=noForumsMessage%></font>
   </core:DefaultCase>

</core:ExclusiveIf>

</discuss:getGearForums>
</paf:InitializeGearEnvironment>
</dsp:page>
<!-- End of discussion Gear display -->

</core:demarcateTransaction>
<%-- @version $Id: //app/portal/version/10.0.3/discussion/discussion.war/discussionShared.jsp#2 $$Change: 651448 $--%>
