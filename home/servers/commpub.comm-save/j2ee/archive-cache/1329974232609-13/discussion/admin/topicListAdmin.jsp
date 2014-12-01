<%-- 
Page:   	topicListAdmin.jsp
Gear:  	 	Discussion Gear
gearmode: 	instanceConfig
displayMode: 	full

Author:      	Jeff Banister
Description: 	This page is included by instanceConfig.jsp, and will display a list of topics
		for a particular discussion forum.  This is similar to the content (default) mode
		page topicList.jsp, with the exception that this page allows for a topic to be
		deleted from a forum.  This page will handle the delete action if requested
		before rendering the list.
--%>

<%@ taglib uri="/discussion-taglib" prefix="discuss" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<i18n:bundle baseName="atg.gears.discussion.discussionAdmin" localeAttribute="userLocale" changeResponseLocale="false" />

<paf:InitializeGearEnvironment id="pafEnv">

<%
   String origURI=pafEnv.getOriginalRequestURI();
   String gearID=pafEnv.getGear().getId();
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String communityID = request.getParameter("paf_community_id");

   String adminPage="ForumAdmin";
   String topicPage="TopicList";
   String threadPage="ThreadList";

   String startRange = request.getParameter("startRange");
   String rangeSize = request.getParameter("rangeSize");
   String topicDisplayCount="10";
   String threadCount="10";
   String sortOrder=request.getParameter("sortOrder");

   String forumID=request.getParameter("fid");
   String topicID=request.getParameter("tid");
   String titleBGColor = "336699";
   String titleTextColor = "ffffff";
   String gearBGColor = "ffffff";
   String gearTextColor = "000000";
   

%>

<core:IfNotNull value='<%= request.getParameter("action")%>'>
   <core:If value='<%= request.getParameter("action").equals("delete")%>'>
     <discuss:deletePost id="deletePost"
                          messageId='<%=request.getParameter("tid")%>'>
     </discuss:deletePost>
   </core:If>
</core:IfNotNull>


    

<core:demarcateTransaction id="topicListAdminXA">

<discuss:getForumTopics id="forumTopics" 
   		forumID="<%= forumID %>"
	        sortOrder="<%= sortOrder %>"
		startRange="<%=startRange%>"
		rangeSize="<%=rangeSize%>">
		
<i18n:message id="i18n_quote" key="html_quote"/>
  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit">
Administer "<%= forumTopics.getForum().getPropertyValue("name")%>"
</td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
        <font class="smaller"><i18n:message key="thread-list-description">
	   <i18n:messageArg value='<%= forumTopics.getForum().getPropertyValue("name")%>'/>
	   <i18n:messageArg value="<%=i18n_quote%>"/>
	</i18n:message>
        </td></tr></table>
  
<img src='/gear/discussion/images/clear.gif' height="1" width="1" border="0"><br>




<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>

<table border="0" cellpadding="2" cellspacing="0">

<tr>
<td colspan="5" ><img src="/gear/discussion/images/clear.gif" height="6" width="1" border="0"></td>
</tr>

<%-- Create listing only if there are postings on this board --%>
<core:ExclusiveIf>

  <core:If value="<%= forumTopics.getTopicCount() <= 0 %>"> 
    <tr  bgcolor="#CCCCCC">
      <td colspan="5"><font class="smaller"><i18n:message key="noposts-message"/></font></td>
    </tr>
  </core:If>

<core:DefaultCase>



<%-- Column headers allow changing sort order --%>
<tr bgcolor="#CCCCCC">
   <core:CreateUrl id="subjectURL" url="<%=origURI%>">
      <core:UrlParam param="config_page" value="<%=topicPage%>"/>
      <core:UrlParam param="fid" value="<%=forumID %>"/>
      <core:UrlParam param="action" value="topic_list"/>
      <core:UrlParam param="sortOrder" value="subject"/>
      <core:UrlParam param="startRange" value="0"/>
      <core:UrlParam param="rangeSize" value="<%= rangeSize%>"/>
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gm" value="instanceConfig"/>
      <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
      <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
      <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
      <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
      <td valign="top" align="left"  width="40%"> 
      &nbsp;<a href="<%= subjectURL.getNewUrl() %>"><font class="smaller" color="#000000"><b><i18n:message key="topic-header"/></b></font></a>
      </td>
   </core:CreateUrl>

   <core:CreateUrl id="userURL" url="<%=origURI%>">
      <core:UrlParam param="config_page" value="<%=topicPage%>"/>
      <core:UrlParam param="fid" value="<%= forumID %>"/>
      <core:UrlParam param="action" value="topic_list"/>
      <core:UrlParam param="sortOrder" value="user"/>
      <core:UrlParam param="startRange" value="0"/>
      <core:UrlParam param="rangeSize" value="<%= rangeSize%>"/>
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gm" value="instanceConfig"/>
      <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
      <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
      <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
      <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
      <td valign="top" align="left" width="20%">
      <a href="<%= userURL.getNewUrl() %>"><font class="smaller" color="#000000"><b><i18n:message key="postedby-label"/></b></font></a>
      </td>
   </core:CreateUrl>


  <core:CreateUrl id="dateURL" url="<%=origURI%>">
      <core:UrlParam param="config_page" value="<%=topicPage%>"/>
      <core:UrlParam param="fid" value="<%=forumID%>"/>
      <core:UrlParam param="action" value="topic_list"/>
      <core:UrlParam param="sortOrder" value="children"/>
      <core:UrlParam param="startRange" value="0"/>
      <core:UrlParam param="rangeSize" value="<%= rangeSize%>"/>
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gm" value="instanceConfig"/>
      <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
      <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
      <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
      <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
      <td valign="top" align="center" width="5">
      <a href="<%= dateURL.getNewUrl() %>"><font class="smaller" color="#000000"><b><i18n:message key="numresponses-header"/></b></font></a>&nbsp;</td>
  </core:CreateUrl>

   <core:CreateUrl id="dateURL" url="<%=origURI%>">
      <core:UrlParam param="config_page" value="<%=topicPage%>"/>
      <core:UrlParam param="fid" value="<%=forumID%>"/>
      <core:UrlParam param="action" value="topic_list"/>
      <core:UrlParam param="sortOrder" value="date"/>
      <core:UrlParam param="startRange" value="0"/>
      <core:UrlParam param="rangeSize" value="<%= rangeSize%>"/>
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gm" value="instanceConfig"/>
      <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
      <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
      <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
      <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
      <td nowrap valign="top" align="right" width="15%">
      <a href="<%= dateURL.getNewUrl() %>"><font class="smaller" color="#000000"><b><i18n:message key="postdate-header"/></b></font></a>
      </td>
   </core:CreateUrl>

	<td width="10%"><font class="smaller"><b>&nbsp;&nbsp;<i18n:message key="delete-label"/><b/></font></td>
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
          <core:CreateUrl id="topicURL" url="<%=origURI%>">
             <core:UrlParam param="config_page" value="<%=threadPage%>"/>
             <core:UrlParam param="fid" value="<%=forumID%>"/>
             <core:UrlParam param="tid" value="<%= topic.getRepositoryId()%>"/>
             <core:UrlParam param="startRange" value="0"/>
             <core:UrlParam param="rangeSize" value="<%= threadCount%>"/>
             <core:UrlParam param="paf_dm" value="full"/>
             <core:UrlParam param="paf_gm" value="instanceConfig"/>
             <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
             <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
             <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
             <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
  	     <td valign="top" align="left" width="50%">
             <font class="small" color="#<%= gearTextColor %>">
             &nbsp;<a href="<%= topicURL.getNewUrl() %>"><font class="small"><%= topic.getPropertyValue("subject") %></a>
             </font></td>
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
          &nbsp;<i18n:formatDateTime value='<%= topic.getPropertyValue("creationDate") %>' dateStyle="short" timeStyle="short" />&nbsp;
	</font></td>
  	 <td rowspan="2" valign="middle" align="center" width="15%"><font class="small">
		      <core:CreateUrl id="adminURL" url="<%=origURI%>">
                        <core:UrlParam param="config_page" value="<%=topicPage%>"/>
         		<core:UrlParam param="fid" value="<%=forumID%>"/>
         		<core:UrlParam param="tid" value="<%= topic.getRepositoryId()%>"/>
         		<core:UrlParam param="startRange" value="0"/>
         		<core:UrlParam param="rangeSize" value="<%= threadCount%>"/>
         		<core:UrlParam param="action" value="delete"/>
                        <core:UrlParam param="paf_dm" value="full"/>
                        <core:UrlParam param="paf_gm" value="instanceConfig"/>
                        <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
                        <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
                        <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
                        <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
         		<a href="<%= adminURL.getNewUrl() %>"><img src="/gear/discussion/images/icon_delete.gif" border=0 alt="<i18n:message key="delete-alttext"/>"></a>
      		</core:CreateUrl>
	      </font></td>
      </tr>

      <tr bgcolor="#<%= gearBGColor %>">
  	<td valign="top" align="left" colspan="3">
	<font class="smaller" color="#<%= gearTextColor %>">
	  &nbsp;
          <core:If value='<%= topic.getPropertyValue("content") !=null %>'>
	     <%= topic.getPropertyValue("content") %>
	  </core:If>
	</font></td>
      </tr>

      <tr>
        <td colspan="5"><img src="/gear/discussion/images/clear.gif" height="1" width="1" border="0"></td>
      </tr>
   </core:ForEach>

<%-- Next and previous links --%>
<tr>
<td align="left"><font class="small">
   <core:ExclusiveIf>
     <core:If value="<%=  forumTopics.getStartRangeInt() > 0 %>">
        <core:CreateUrl id="topicURL" url="<%=origURI%>">
         <core:UrlParam param="config_page" value="<%=topicPage%>"/>
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gm" value="instanceConfig"/>
	 <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
         <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
         <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
         <core:UrlParam param="rangeSize" value="<%= rangeSize %>"/>
         <paf:encodeUrlParam param="sortOrder" value="<%= sortOrder %>"/>
         <core:UrlParam param="fid" value="<%=forumID %>"/>
         <core:UrlParam param="startRange" value="<%= Integer.toString(forumTopics.getPrevStartRangeInt()) %>"/>
         <a href="<%= topicURL.getNewUrl() %>"><i18n:message key="prev-link"/>&nbsp;<%= rangeSize%></a>&nbsp;&nbsp;&nbsp;&nbsp;
        </core:CreateUrl>
     </core:If>

     <core:DefaultCase>
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
     </core:DefaultCase>
   </core:ExclusiveIf>

   <core:If value="<%=  ( forumTopics.getStartRangeInt()+forumTopics.getRangeSize() > 0) && (forumTopics.getTopicCount() > forumTopics.getStartRangeInt()+forumTopics.getRangeSize()) %>">
        <core:CreateUrl id="topicURL" url="<%=origURI%>">
         <core:UrlParam param="config_page" value="<%=topicPage%>"/>
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gm" value="instanceConfig"/>
	 <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
         <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
         <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
         <core:UrlParam param="rangeSize" value="<%= rangeSize %>"/>
         <paf:encodeUrlParam param="sortOrder" value="<%= sortOrder %>"/>
         <core:UrlParam param="fid" value="<%=forumID %>"/>
         <core:UrlParam param="startRange" value="<%= Integer.toString(forumTopics.getStartRangeInt()+forumTopics.getRangeSize()) %>"/>
         <a href="<%= topicURL.getNewUrl() %>"><i18n:message key="next-link"/>&nbsp;<%= rangeSize%></a>
        </core:CreateUrl>
   </core:If>
</font></td>
</tr>

</core:DefaultCase>
</core:ExclusiveIf>

</table>

</td></tr></table>
</discuss:getForumTopics>
</core:demarcateTransaction>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/discussion/discussion.war/admin/topicListAdmin.jsp#2 $$Change: 651448 $--%>
