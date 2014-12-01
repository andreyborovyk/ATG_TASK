<%-- 
Page:   	discussionEdit.jsp
Gear:  	 	Discussion Gear
gearmode: 	userConfig
displayMode: 	full

Author:      	Jeff Banister
Description: 	This is the edit mode (userConfig) page for the discussion gear, where the 
		user may select specific discussion boards to display in their gear.  This 
		page handles both "add" and "remove" actions, if requested, before retrieving 
		and displaying the lists of forums.
--%>

<%@ taglib uri="/discussion-taglib" prefix="discuss" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">


  <%

     String origURI=pafEnv.getOriginalRequestURI();
     String gearID=pafEnv.getGear().getId();
     String userID=null;
  
     String gearName=pafEnv.getGear().getName(response.getLocale());
     String gearNameEncoded="Discussion+Board";
     if (gearName!=null) {  gearNameEncoded=atg.servlet.ServletUtil.escapeURLString((String) gearName); }

     String forumID=request.getParameter("forumID"); 
  
     atg.portal.framework.ColorPalette cp = pafEnv.getPage().getColorPalette();
     String titleBGColor = cp.getGearTitleBackgroundColor();
     String titleTextColor = cp.getGearTitleTextColor();
     String gearBGColor = cp.getGearBackgroundColor();
     String gearTextColor = cp.getGearTextColor();
     String pageBGColor=cp.getPageBackgroundColor();

     String bundleName=pafEnv.getGearInstanceParameter("resourceBundle");
     // default to old (pre-5.6) name, in case new file not installed
     if (bundleName==null || bundleName.equals("")) {
       bundleName="atg.gears.discussion.discussion";
     } 
  %>

<i18n:bundle baseName="<%= bundleName %>" changeResponseLocale="false" />

<i18n:message id="userConfigTitleLabel" key="userConfigTitleLabel"/>
<i18n:message id="userConfigBanner" key="userConfigBanner"/>
<i18n:message id="removeMineHeader" key="remove-mine-header"/>
<i18n:message id="removeLink" key="remove-link"/>
<i18n:message id="noPrefMessage" key="no-pref-message"/>
<i18n:message id="addMineHeader" key="add-mine-header"/>
<i18n:message id="addLink" key="add-link"/>
<i18n:message id="doneButtonLabel" key="done-button-label"/>
<i18n:message id="yourBoardsHeader" key="yourBoardsHeader"/>
<i18n:message id="forumHeader" key="forumHeader"/>
<i18n:message id="mostRecentHeader" key="mostRecentHeader"/>
<i18n:message id="numPostsHeader" key="numPostsHeader"/>
<i18n:message id="allBoardsHeader" key="allBoardsHeader"/>

<i18n:message key="login-message" id="loginMsg"/>

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
  <dsp:oparam name="true">
     <core:CreateUrl id="loginURL" url="<%= pafEnv.getLoginURI(false) %>">
       <paf:encodeUrlParam param="successURL" value='<%= pafEnv.getOriginalRequestURI()  + "?paf_dm=full&paf_gm=userConfig&paf_gear_id=" + gearID%>'/>
       <core:UrlParam param="col_pb" value="<%= pageBGColor %>"/>
       <core:UrlParam param="col_gtt" value="<%= titleTextColor %>"/>
       <core:UrlParam param="col_gtb" value="<%= titleBGColor %>"/>
       <core:UrlParam param="col_gt" value="<%= gearTextColor %>"/>
       <core:UrlParam param="col_gb" value="<%= gearBGColor %>"/>
       <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>

       <core:UrlParam param="userMessage" value="<%=loginMsg%>"/>

       <core:Redirect url="<%= loginURL.getNewUrl() %>"/>
     </core:CreateUrl>
  </dsp:oparam>
</dsp:droplet>


<core:IfNotNull value='<%= request.getParameter("action")%>'>
   <core:If value='<%= request.getParameter("action").equals("remove_forum")%>'>
     <discuss:removeUserForum id="deleteForum"
                          forumId='<%= request.getParameter("forumID") %>'
                          gearId="<%= gearID %>"
                          userId="<%=userID %>">
     </discuss:removeUserForum>
   </core:If>
</core:IfNotNull>


<core:IfNotNull value='<%= request.getParameter("action")%>'> 
  <core:If value='<%= request.getParameter("action").equals("add_forum")%>'> 
    <discuss:addUserForum id="addResult"
                    forumId="<%= forumID %>"
                    gearId="<%= gearID%>"
                    userId="<%=userID %>">
      <core:IfNot value="<%= addResult.getSuccess() %>" >
           <font color=cc0000 class="small"><b><i18n:message key="<%=addResult.getErrorMessage()%>" /></b></font>
      </core:IfNot>
    </discuss:addUserForum>
  </core:If>
</core:IfNotNull>

<core:demarcateTransaction id="discussionEditPageXA">

<center>
<br>
<font class="large_bold"><%=userConfigTitleLabel %>&nbsp;<%=gearName%></font>
<br>
<br>
<font class="small">&nbsp;<%=userConfigBanner %>&nbsp;</font>
<br>
</center>

<table border=0 cellPadding=0 cellSpacing=0 width="100%" align="center">
<tr>
<td><img src="/gear/discussion/images/clear.gif" height="1" width="684" border="0"></td>
</tr>
</table>

<%-- Show list of user's forums --%>


<discuss:getUserForums id="userForums"
			   gear="<%=gearID%>" 
			   user="<%=userID%>">

<table border=0 cellPadding=0 cellSpacing=0 width="90%" align="center">

<tr>
<td colspan="4"><img src="/gear/discussion/images/clear.gif" height="10" width="1" border="0"></td>
</tr>

<tr>
<td colspan="4" ><img src="/gear/discussion/images/clear.gif" height="10" width="1" border="0"></td>
</tr>

<tr>
  <td colSpan="4"><font class="small"><b><%=yourBoardsHeader%></b></font></td>
</tr>

<tr>
<td colspan="4" bgcolor="#ffffff" ><img src="/gear/discussion/images/clear.gif" height="1" width="1" border="0"></td>
</tr>

<%-- Column headers --%>
<tr bgcolor="#<%=titleBGColor%>">
<td valign="bottom" align="left" width="40%"> <font class="small" color="#<%=titleTextColor%>">&nbsp;&nbsp;<%=forumHeader%></font></td>
<td valign="bottom" align="left" width="15%"> <font class="small" color="#<%=titleTextColor%>"><%=mostRecentHeader%></font></td>
<td valign="bottom" align="left" width="10%"> <font class="small" color="#<%=titleTextColor%>"><%=numPostsHeader%></font></td>
<td valign="bottom" align="left" width="20%"> <font class="small" color="#<%=titleTextColor%>"><%= removeMineHeader %><br> <%= gearName%></font></td>
</tr>

<tr bgcolor="#<%=titleBGColor%>">
<td colspan="4"><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
</tr>

<tr>
<td colspan="4" bgcolor="#666666" ><img src="/gear/discussion/images/clear.gif" height="1" width="1" border="0"></td>
</tr>

<core:ExclusiveIf>
<core:If value="<%= userForums.getForumCount() > 0 %>">
  <core:ForEach id="userForumsForEach"
	      values="<%= userForums.getForumList() %>"
              castClass="atg.repository.RepositoryItem"
              elementId="forum">

   <tr bgcolor="#<%=gearBGColor%>">
      <td valign="top" align="left" > <font class="small" color="#<%=gearTextColor%>">
         &nbsp;&nbsp;<%= forum.getPropertyValue("name") %>
      </font></td>
      <td valign="top" align="left" > <font class="small" color="#<%=gearTextColor%>">
          &nbsp;<i18n:formatDateTime value='<%= (java.util.Date) forum.getPropertyValue("lastPostTime") %>' dateStyle="short" timeStyle="short" />&nbsp;
      </font></td>
      <td valign="top" align="center" > <font class="small" color="#<%=gearTextColor%>">
         <%= forum.getPropertyValue("numPosts") %>
      </font></td>
      <core:CreateUrl id="removeURL" url="<%= origURI %>">
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gm" value="userConfig"/>
         <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="forumID" value="<%= forum.getRepositoryId()%>"/>
         <core:UrlParam param="action" value="remove_forum"/>
         <td valign="top" align="left" > 
         <a href="<%= removeURL.getNewUrl() %>"><font class="small"><%=removeLink %></font></a>
         </td>
      </core:CreateUrl>
   </tr>

   <tr bgcolor="#<%= gearBGColor %>">
      <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
   </tr>

   <tr>
      <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="1" width="1" border="0"></td>
   </tr>

  </core:ForEach>
</core:If>

<core:DefaultCase>
   <tr bgcolor="#<%= gearBGColor %>">
      <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="5" width="1" border="0"></td>
   </tr>

   <tr bgcolor="#<%= gearBGColor %>">
      <td colspan="4"><font class="small" color="#<%=gearTextColor%>"><%=noPrefMessage%></font></td>
   </tr>

   <tr bgcolor="#<%= gearBGColor %>">
      <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="5" width="1" border="0"></td>
   </tr>

</core:DefaultCase>

</core:ExclusiveIf>
</table>
</discuss:getUserForums>

<%-- Show list of all forums assigned to this gear --%>

<discuss:getGearForums id="forums" gear="<%=gearID%>" >

<table width="90%"  cellpadding="0" cellspacing="0" border="0" align="center">

<tr>
<td colspan="4"><img src="/gear/discussion/images/clear.gif" height="10" width="1" border="0"></td>
</tr>

<tr>
<td colspan="4" ><img src="/gear/discussion/images/clear.gif" height="10" width="1" border="0"></td>
</tr>

<tr>
  <td colSpan="4"><font class="small"><b><%=allBoardsHeader%></b></font></td>
</tr>

<tr>
<td colspan="4" bgcolor="#ffffff" ><img src="/gear/discussion/images/clear.gif" height="1" width="1" border="0"></td>
</tr>

<%-- Column headers --%>
<tr bgcolor="#<%=titleBGColor%>">
<td valign="bottom" align="left" width="40%"> <font class="small" color="#<%=titleTextColor%>">&nbsp;&nbsp;<%=forumHeader%></font></td>
<td valign="bottom" align="left" width="15%"> <font class="small" color="#<%=titleTextColor%>"><%=mostRecentHeader%></font></td>
<td valign="bottom" align="left" width="10%"> <font class="small" color="#<%=titleTextColor%>"><%=numPostsHeader%></font></td>
<td valign="bottom" align="left" width="20%"> <font class="small" color="#<%=titleTextColor%>"><%=addMineHeader%><br> <%= gearName%></font></td>
</tr>

<tr bgcolor="#<%=titleBGColor%>">
<td colspan="4"><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
</tr>

<tr>
<td colspan="4" bgcolor="#666666" ><img src="/gear/discussion/images/clear.gif" height="1" width="1" border="0"></td>
</tr>

<core:ForEach id="forumsForEach"
	      values="<%= forums.getForumList() %>"
              castClass="atg.repository.RepositoryItem"
              elementId="forum">

   <tr bgcolor="#<%=gearBGColor%>">
      <td valign="top" align="left" > <font class="small" color="#<%=gearTextColor%>">
         &nbsp;&nbsp;<%= forum.getPropertyValue("name") %>
      </font></td>
      <td valign="top" align="left" > <font class="small" color="#<%=gearTextColor%>">
          &nbsp;<i18n:formatDateTime value='<%= (java.util.Date) forum.getPropertyValue("lastPostTime") %>' dateStyle="short" timeStyle="short" />&nbsp;
      </font></td>
      <td valign="top" align="center" > <font class="small" color="#<%=gearTextColor%>">
         <%= forum.getPropertyValue("numPosts") %>
      </font></td>
      <core:CreateUrl id="addURL" url="<%= origURI %>">
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gm" value="userConfig"/>
         <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="forumID" value="<%= forum.getRepositoryId()%>"/>
         <core:UrlParam param="action" value="add_forum"/>
         <td valign="top" align="left" > 
         <a href="<%= addURL.getNewUrl() %>"><font class="small"><%=addLink%></font></a>
         </td>
      </core:CreateUrl>
   </tr>

   <tr bgcolor="#<%= gearBGColor %>">
      <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="2" width="1" border="0"></td>
   </tr>

   <tr>
      <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="1" width="1" border="0"></td>
   </tr>

</core:ForEach>

   <tr bgcolor="#<%= gearBGColor %>">
      <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="3" width="1" border="0"></td>
   </tr>


<core:CreateUrl id="doneURL" url="<%= origURI %>">
<form action="<%=doneURL.getNewUrl()%>">
   <tr>
     <td colspan="4" align="center" > 
     <input type="submit" value="<%=doneButtonLabel%>">
     </td>
   </tr>
</form>
</core:CreateUrl>

   <tr bgcolor="#<%= gearBGColor %>">
      <td colspan="4"><img src="/gear/discussion/images/clear.gif" height="5" width="1" border="0"></td>
   </tr>


</table>
</discuss:getGearForums>

</core:demarcateTransaction>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/discussion/discussion.war/discussionEdit.jsp#2 $$Change: 651448 $--%>
