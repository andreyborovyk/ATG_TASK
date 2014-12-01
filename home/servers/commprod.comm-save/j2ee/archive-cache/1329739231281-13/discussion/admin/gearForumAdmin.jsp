<%-- 
Page:   	gearForumAdmin.jsp
Gear:  	 	Discussion Gear
gearmode: 	instanceConfig
displayMode: 	full

Author:      	Jeff Banister
Description: 	This page is included by instanceConfig.jsp, and serves as the interface
	   	to add or remove forums from a gear instance, and to administer forums.
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
   String gearName=pafEnv.getGear().getName(response.getLocale());
   String configPage="ForumAdmin";
   String topicPage="TopicList";
   String gearID=pafEnv.getGear().getId();
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String communityID = request.getParameter("paf_community_id");

   String gearNameEncoded="";
   if (gearName!=null) {  gearNameEncoded=atg.servlet.ServletUtil.escapeURLString((String) gearName); }

%>
<%-- Handle "action" if one is requested, then display page --%>
<core:IfNotNull value='<%= request.getParameter("action")%>'>
   <core:If value='<%= request.getParameter("action").equals("remove_forum")%>'>
     <discuss:removeGearForum id="deleteForum"
                          forumId='<%= request.getParameter("forum_id") %>'
                          gearId="<%= gearID%>">
     </discuss:removeGearForum>
   </core:If>
</core:IfNotNull>

<core:IfNotNull value='<%= request.getParameter("action")%>'> 
  <core:If value='<%= request.getParameter("action").equals("add_forum")%>'> 
    <discuss:addGearForum id="addResult" 
		    forumId='<%= request.getParameter("forum_id") %>'
		    gearId="<%= gearID %>"
		    env="<%= pafEnv %>">
      <core:IfNot value="<%= addResult.getSuccess() %>" >
           <br><br><font class="error"><b><i18n:message key="<%=addResult.getErrorMessage()%>" /></b></font><br><br>
      </core:IfNot>
    </discuss:addGearForum>
  </core:If>
</core:IfNotNull>

<core:IfNotNull value='<%= request.getParameter("action")%>'>
   <core:If value='<%= request.getParameter("action").equals("readd_forum")%>'>
     <discuss:reAddForum id="deleteForum"
                          forumId='<%= request.getParameter("forum_id") %>'>
     </discuss:reAddForum>
   </core:If>
</core:IfNotNull>

<i18n:message id="i18n_quote" key="html_quote"/>
  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit">
<i18n:message key="admin-forums-title">
 <i18n:messageArg value='<%= pafEnv.getGear().getName(response.getLocale()) %>'/>
  <i18n:messageArg value="<%=i18n_quote%>"/>
</i18n:message>&nbsp;
</td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
        <font class="smaller">You can view forums currently assigned to this discussion gear, as well as add or create forums, on these pages.
        </td></tr></table>
  
<img src='/gear/discussion/images/clear.gif' height="1" width="1" border="0"><br>

<core:demarcateTransaction id="gearForumAdminXA">

<%-- *********  Currently Selected Forums ********** --%>

<discuss:getGearForums id="forums" gear="<%= gearID%>" >



<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">

<tr>
    <td align="left">
    <font class="smaller" class="smaller"><i18n:message key="currently-selected"/></font>


<table cellpadding="2" cellspacing="0" border="0" width="100%">

<%-- Only show this list if there are forums selected --%>
<core:ExclusiveIf>

<core:If value="<%= forums.getForumCount() > 0 %>">

<%-- Column headers --%>
<tr bgcolor="#cccccc">
<td valign="top" align="left" width="40%"> <font class="smaller"><b>Forum Name</b></font></td>
<td valign="top" align="left" width="15%"> <font class="smaller"><b><i18n:message key="date-created-header"/></b></td>
<td valign="top" align="left" width="15%"> <font class="smaller"><b><i18n:message key="lastpostdate-header"/></b></td>
<td nowrap valign="top" align="left" width="10%"> <font class="smaller"><b><i18n:message key="numposts-header"/></b></td>
<td valign="top" align="left" width="20%"> <font class="smaller">&nbsp;</td>
</tr>




<core:ForEach id="forumsForEach"
	      values="<%= forums.getForumList() %>"
              castClass="atg.repository.RepositoryItem"
              elementId="forum">

   <tr bgcolor="#ffffff">
      <td valign="top" align="left" >
        <core:CreateUrl id="topicURL" url="<%=origURI%>">
         <core:UrlParam param="config_page" value="<%=topicPage%>"/>
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gm" value="instanceConfig"/>
	 <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="fid" value="<%= forum.getRepositoryId()%>"/>
         <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
         <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
         <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
         <core:UrlParam param="rangeSize" value="10"/>
           <a href="<%= topicURL.getNewUrl() %>"><font class="smaller"><%= forum.getPropertyValue("name") %></a>
        </core:CreateUrl>
      </td>
      <td nowrap valign="top" align="left" > <font class="smaller">
         <i18n:formatDateTime value='<%= forum.getPropertyValue("creationDate") %>' dateStyle="short" timeStyle="short" />&nbsp;
      </font></td>
      <td nowrap valign="top" align="left" > <font class="smaller">
        <i18n:formatDateTime value='<%= forum.getPropertyValue("lastPostTime") %>' dateStyle="short" timeStyle="short" />&nbsp;
      </font></td>
      <td valign="top" align="center" > <font class="smaller">
         <%= forum.getPropertyValue("numPosts") %>
      </font></td>
      <td valign="top" align="left" > <font class="smaller">
      <core:CreateUrl id="adminURL" url="<%= origURI %>">
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gm" value="instanceConfig"/>
	 <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="action" value="remove_forum"/>
         <core:UrlParam param="forum_id" value="<%= forum.getRepositoryId()%>"/>
         <core:UrlParam param="config_page" value="<%= configPage%>"/>
         <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
         <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
         <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
         <a href="<%= adminURL.getNewUrl() %>"><font class="smaller"><i18n:message key="remove-link"/></a>
      </core:CreateUrl>
      </font></td>
   </tr>
</core:ForEach>

</core:If>

<core:defaultCase>
<tr><td colspan="5" bgcolor="#CCCCCC" align="left"><font class="smaller"><i18n:message key="no-forums-selected-msg"/></font></td></tr>
</core:defaultCase>

</table>
</core:ExclusiveIf>

</discuss:getGearForums>

<core:CreateUrl id="addURL" url="<%=origURI%>">
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gm" value="instanceConfig"/>
	 <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="gear_id" value="<%= gearID%>"/>
         <core:UrlParam param="gear_name" value="<%= gearNameEncoded %>"/>
         <core:UrlParam param="config_page" value="AddGearForum"/>
         <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
         <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
         <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
         <a href="<%= addURL.getNewUrl() %>"><font class="smaller"><i18n:message key="create-board-link"/></font></a>
  </core:CreateUrl>


<%-- *********  List of forums removed from this gear ********** --%>

<discuss:getGearForums id="forums" gear="<%= gearID%>" removed="<%=true%>">

<%-- Only show this list if there are forums selected --%>
<table width="100%"  cellspacing="0" cellpadding="0" border="0">

<tr>
  <td colspan="5"><img src="/gear/discussion/images/clear.gif" height="10" width="1" border="0"></td>
</tr>

<tr>
  <td colspan="4" valign="top" align="left"> <font class="smaller"><br><i18n:message key="removedforums-header"/></font></td>
  
</tr>

<tr>
  <td colspan="5"><img src="/gear/discussion/images/clear.gif" height="5" width="1" border="0"></td>
</tr>
<tr><td colspan="5">

<core:ExclusiveIf>
<table cellpadding="2" cellspacing="0" border="0" width="100%">
<core:If value="<%= forums.getForumCount() > 0 %>">

<%-- Column headers --%>
<tr bgcolor="#cccccc">
<td valign="top" align="left" width="40%"> <font class="smaller"><b><i18n:message key="forum-header"/></b></font></td>
<td valign="top" align="left" width="15%"> <font class="smaller"><b><i18n:message key="date-created-header"/></b></font></td>
<td valign="top" align="left" width="15%"> <font class="smaller"><b><i18n:message key="lastpostdate-header"/></b></font></td>
<td nowrap valign="top" align="left" width="10%"> <font class="smaller"><b><i18n:message key="numposts-header"/></b></td>
<td valign="top" align="left" width="20%"> <font class="smaller"><b><i18n:message key="add-header"/></b></font></td>
</tr>



<core:ForEach id="forumsForEach"
	      values="<%= forums.getForumList() %>"
              castClass="atg.repository.RepositoryItem"
              elementId="forum">

   <tr bgcolor="#ffffff">
      <td valign="top" align="left" >
        <core:CreateUrl id="topicURL" url="<%=origURI%>">
         <core:UrlParam param="config_page" value="<%=topicPage%>"/>
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gm" value="instanceConfig"/>
	 <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="fid" value="<%= forum.getRepositoryId()%>"/>
         <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
         <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
         <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
         <core:UrlParam param="rangeSize" value="10"/>
          <a href="<%= topicURL.getNewUrl() %>"><font class="smaller"><%= forum.getPropertyValue("name") %></a>
        </core:CreateUrl>
      </td>
      <td nowrap valign="top" align="left" > <font class="smaller">
          &nbsp;<i18n:formatDateTime value='<%= forum.getPropertyValue("creationDate") %>' dateStyle="short" timeStyle="short" />&nbsp;
      </td>
      <td nowrap valign="top" align="left" > <font class="smaller">
         &nbsp;<i18n:formatDateTime value='<%= forum.getPropertyValue("lastPostTime") %>' dateStyle="short" timeStyle="short" />&nbsp;
      </td>
      <td valign="top" align="center" > <font class="smaller">
         <%= forum.getPropertyValue("numPosts") %>
      </font></td>
      <td valign="top" align="left" > <font class="smaller">
      <core:CreateUrl id="adminURL" url="<%= origURI %>">
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gm" value="instanceConfig"/>
	 <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="action" value="readd_forum"/>
         <core:UrlParam param="forum_id" value="<%= forum.getRepositoryId()%>"/>
         <core:UrlParam param="config_page" value="<%= configPage%>"/>
         <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
         <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
         <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
         <a href="<%= adminURL.getNewUrl() %>"><i18n:message key="add-link"/></a>
      </core:CreateUrl>
      </font></td>
   </tr>


</core:ForEach>
</core:If>

<core:defaultCase>
<tr bgcolor="#cccccc"><td colspan="5" align="left"><font class="smaller"><i18n:message key="no-forums-removed-msg"/></font></td></tr>
</core:defaultCase>

</table>
</core:ExclusiveIf>
<br>
</discuss:getGearForums>
</tr></td></table>

<%-- *********  List of all forums ********** --%>

<discuss:getAllForums id="forums" >

<table width="50%"  cellspacing="0" cellpadding="0" border="0">

<tr>
  <td colspan="2"><img src="/gear/discussion/images/clear.gif" height="10" width="1" border="0"></td>
</tr>

<tr>
  <td valign="top" align="left"> <font class="smaller"><i18n:message key="public-forums-header"/></b></td>
         <td nowrap align="right" width="50%"> &nbsp;</td>
</tr>
<tr><td colspan="5">

<table cellpadding="2" cellspacing="0" border="0" width="100%">

<%-- Column headers --%>
<tr bgcolor="#CCCCCC">
<td valign="top" align="left" width="70%"> <font class="smaller"><b><i18n:message key="allboards-header"/></b></font></td>
<td valign="top" align="left" width="30%"> <font class="smaller"><b><i18n:message key="add-header"/></b></font></td>
</tr>



<core:ForEach id="forumsForEach"
	      values="<%= forums.getForumList() %>"
              castClass="atg.repository.RepositoryItem"
              elementId="forum">

   <tr bgcolor="#ffffff">
      <td valign="top" align="left" > <font class="smaller">
        <%= forum.getPropertyValue("name") %>
     </td>
      <core:CreateUrl id="adminURL" url="<%= origURI %>">
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gm" value="instanceConfig"/>
	 <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="forum_id" value="<%= forum.getRepositoryId()%>"/>
         <core:UrlParam param="config_page" value="<%= configPage%>"/>
         <core:UrlParam param="action" value="add_forum"/>
         <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
         <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
         <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
         <td valign="top" align="left" > 
         <a href="<%= adminURL.getNewUrl() %>"><font class="smaller"><i18n:message key="add-link"/></a>
         </td>
      </core:CreateUrl>
   </tr>

</core:ForEach>
</table>

</td></tr>
</table>


</td></tr></table>
<br><br>
</discuss:getAllForums>
</core:demarcateTransaction>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/discussion/discussion.war/admin/gearForumAdmin.jsp#2 $$Change: 651448 $--%>
