
<%-- 
Page:   	threadListAdmin.jsp
Gear:  	 	Discussion Gear
gearmode: 	instanceConfig
displayMode: 	full

Author:      	Jeff Banister
Description: 	This page is included by instanceConfig.jsp, and will display a message thread 
		(topic and list of resonses).  This is similar (although the display has changed
		somewhat) to the threadList.jsp page for the content (default) mode of the gear,
		with the exception that this page provides the ability to delete individual responses.
		This page will handle the delete action if requested before rendering the list.
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
                          messageId='<%=request.getParameter("threadID")%>'>
     </discuss:deletePost>
   </core:If>
</core:IfNotNull>

<core:demarcateTransaction id="threadListAdminXA">

<discuss:getTopicThreads id="topicThreads" 
   		forumID="<%= forumID %>"
   		topicID="<%= topicID %>"
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


<i18n:message id="i18n_quote" key="html_quote"/>
  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit">
<i18n:message key="thread-admin-title">
 <i18n:messageArg value='<%= topicThreads.getTopic().getPropertyValue("subject")%>' />
 <i18n:messageArg value="<%=i18n_quote%>" />
</i18n:message>
</td></tr></table>
</td></tr></table>


  
<img src='/gear/discussion/images/clear.gif' height="1" width="1" border="0"><br>


<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>


<table width="90%" cellspacing="0" cellpadding="0" border="0">

<tr bgcolor="#<%= gearBGColor %>">
   <td nowrap colspan="3" align="left"><font class="smaller">
      <i18n:message key="postedby-label"/>: <%= parentUserName %>
   </font></td>
</tr>

<tr">
<td colspan="3"><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
</tr>


<tr bgcolor="#<%= gearBGColor %>">
   <td colspan="3"><font class="smaller" color="#<%= gearTextColor %>">
    
    <core:If value='<%= topicThreads.getTopic().getPropertyValue("content") !=null %>'>
        <%= topicThreads.getTopic().getPropertyValue("content") %>
    </core:If>
    
   </font></td>
</tr>


      <tr bgcolor="#<%=gearBGColor %>">
      <td colspan="3"><img src="/gear/discussion/images/clear.gif" height="3" width="1" border="0"></td>
      </tr>

      <tr>
      <td colspan="3" bgcolor="#666666" ><img src="/gear/discussion/images/clear.gif" height="1" width="1" border="0"></td>
      </tr>

      <tr>
      <td colspan="3"><img src="/gear/discussion/images/clear.gif" height="5" width="1" border="0"></td>
      </tr>

</table>

<table width="90%" cellspacing="0" cellpadding="0" border="0">


<%-- Create listing only if there are postings on this board --%>
<core:ExclusiveIf>

  <core:If value="<%= topicThreads.getThreadCount() <= 0 %>">
    <tr  bgcolor="#cccccc">
      <td colspan="4"><font class="smaller" color="#<%= gearTextColor %>">&nbsp;<i18n:message key="noresponse-message"/></font></td>
    </tr>
  </core:If>

  <core:DefaultCase>

      <%-- Column headers allow changing sort order, except response text column --%>
      <tr bgcolor="#cccccc">
            <td valign="top" align="left" width="40%"> 
            <font class="smaller" color="#000000"><b><i18n:message key="reply-header"/></b></font>
            </td>

         <core:CreateUrl id="userURL" url="<%=origURI%>">
            <core:UrlParam param="config_page" value="<%=threadPage%>"/>
            <core:UrlParam param="fid" value="<%=forumID%>"/>
            <core:UrlParam param="tid" value="<%=topicID%>"/>
            <core:UrlParam param="sortOrder" value="user"/>
            <core:UrlParam param="startRange" value="0"/>
            <core:UrlParam param="rangeSize" value="<%= rangeSize%>"/>
            <core:UrlParam param="paf_dm" value="full"/>
            <core:UrlParam param="paf_gm" value="instanceConfig"/>
            <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
            <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
            <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
            <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
            <td valign="top" align="left" width="30%">
            <a href="<%= userURL.getNewUrl() %>"><font class="smaller" color="#000000"><b><i18n:message key="postedby-label"/></b></font></a>
            </td>
         </core:CreateUrl>
         <core:CreateUrl id="dateURL" url="<%=origURI%>">
            <core:UrlParam param="config_page" value="<%=threadPage%>"/>
            <core:UrlParam param="fid" value="<%= forumID %>"/>
            <core:UrlParam param="tid" value="<%=topicID%>"/>
            <core:UrlParam param="sortOrder" value="creationDate%20DESC"/>
            <core:UrlParam param="startRange" value="0"/>
            <core:UrlParam param="rangeSize" value="<%= rangeSize%>"/>
            <core:UrlParam param="paf_dm" value="full"/>
            <core:UrlParam param="paf_gm" value="instanceConfig"/>
            <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
            <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
            <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
            <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
            <td valign="top" align="center" width="15%">
            <a href="<%= dateURL.getNewUrl() %>"><font class="smaller" color="#000000"><b><i18n:message key="postdate-header"/></b></font></a>
            </td>
         </core:CreateUrl>
	<td width="10%" align="center"><font class="smaller" color="#000000"><b>&nbsp;&nbsp;<i18n:message key="delete-label"/><b/></font></td>
      </tr>

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
        	<td valign="top" align="left" width="40%"><font class="smaller" color="#<%= gearTextColor %>">
                   &nbsp;<%= topic.getPropertyValue("content") %></font>
            	</font></td>
        	<td valign="top" align="left" width="30%"><font class="smaller" color="#<%= gearTextColor %>">
		<%= userName %>
            	</font></td>
        	<td nowrap valign="top" align="left" width="15%"><font class="smaller" color="#<%= gearTextColor %>">
                  &nbsp;<i18n:formatDateTime value='<%= topic.getPropertyValue("creationDate") %>' dateStyle="short" timeStyle="short" />&nbsp;
      	        </font></td>
  	          <td valign="middle" align="center" width="15%"><font class="smaller">
		      <core:CreateUrl id="adminURL" url="<%=origURI%>">
                        <core:UrlParam param="config_page" value="<%=threadPage%>"/>
         		<core:UrlParam param="fid" value="<%=forumID%>"/>
         		<core:UrlParam param="threadID" value="<%= topic.getRepositoryId()%>"/>
         		<core:UrlParam param="tid" value="<%= topicID%>"/>
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
              <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="3" width="1" border="0"></td>
            </tr>

            <tr>
              <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
            </tr>
         </core:ForEach>

      <%-- Next and previous links --%>
      <tr>
      <td align="left"><font face="verdana,arial" class="smaller">
   <core:ExclusiveIf>
      <core:If value="<%=  topicThreads.getStartRangeInt() > 0 %>">
          <core:CreateUrl id="topicURL" url="<%=origURI%>">
             <core:UrlParam param="config_page" value="<%=threadPage%>"/>
             <core:UrlParam param="fid" value="<%=forumID%>"/>
             <core:UrlParam param="tid" value="<%=topicID%>"/>
             <core:UrlParam param="startRange" value="<%= Integer.toString(topicThreads.getPrevStartRangeInt()) %>"/>
             <core:UrlParam param="rangeSize" value="<%= rangeSize%>"/>
             <core:UrlParam param="paf_dm" value="full"/>
             <core:UrlParam param="paf_gm" value="instanceConfig"/>
             <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
             <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
             <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
             <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
             <paf:encodeUrlParam param="sortOrder" value="<%= sortOrder %>"/>
             <font class="smaller" color="#<%= gearTextColor %>">
               <a href="<%= topicURL.getNewUrl() %>"><i18n:message key="prev-link"/>&nbsp;<%= rangeSize%></a>&nbsp;&nbsp;&nbsp;&nbsp;
          </core:CreateUrl>
      </core:If>

     <core:DefaultCase>
      &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
     </core:DefaultCase>
   </core:ExclusiveIf>

      <core:If value="<%=  ( topicThreads.getStartRangeInt()+topicThreads.getRangeSize() > 0) && (topicThreads.getThreadCount() > topicThreads.getStartRangeInt()+topicThreads.getRangeSize()) %>">
          <core:CreateUrl id="topicURL" url="<%=origURI%>">
             <core:UrlParam param="config_page" value="<%=threadPage%>"/>
             <core:UrlParam param="fid" value="<%=forumID%>"/>
             <core:UrlParam param="tid" value="<%=topicID%>"/>
             <core:UrlParam param="startRange" value="<%= Integer.toString(topicThreads.getStartRangeInt()+topicThreads.getRangeSize()) %>"/>
             <core:UrlParam param="rangeSize" value="<%= rangeSize%>"/>
             <core:UrlParam param="paf_dm" value="full"/>
             <core:UrlParam param="paf_gm" value="instanceConfig"/>
             <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
             <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
             <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
             <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
             <paf:encodeUrlParam param="sortOrder" value="<%= sortOrder %>"/>
             <font class="smaller" color="#<%= gearTextColor %>">
               <a href="<%= topicURL.getNewUrl() %>"><i18n:message key="next-link"/>&nbsp;<%= rangeSize%></a>
             </font></td>
          </core:CreateUrl>
      </core:If>
      </font></td>
      </tr>

   </core:DefaultCase>
</core:ExclusiveIf>


   </table>

   
   </tr></tr></table>
</discuss:getTopicThreads>

</core:demarcateTransaction>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/discussion/discussion.war/admin/threadListAdmin.jsp#2 $$Change: 651448 $--%>
