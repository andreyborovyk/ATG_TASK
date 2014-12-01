<%@ taglib uri="/discussion-taglib" prefix="discuss" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/docexch-taglib" prefix="dex" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">
<dex:DocExchPage id="dexpage" gearEnv="<%= pafEnv %>">
<%
   String origURI = pafEnv.getOriginalRequestURI();
   String gearID = pafEnv.getGear().getId();

   String startRange = atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("startRange");
   String rangeSize = atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("rangeSize");
   String forumID=atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("forumID");
   String topicID=atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("topicID");
   String sortOrder=atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("sortOrder");
   if (sortOrder!=null)
       sortOrder=atg.servlet.ServletUtil.escapeURLString(sortOrder);

   atg.portal.framework.ColorPalette cp = pafEnv.getPage().getColorPalette();
   String titleBGColor = cp.getGearTitleBackgroundColor();
   String titleTextColor = cp.getGearTitleTextColor();
   String gearBGColor = cp.getGearBackgroundColor();
   String gearTextColor = cp.getGearTextColor();

   String bundleName = dexpage.getDiscResourceBundle();
%>
<!-- bundleName -->
<i18n:bundle baseName="<%= bundleName %>" changeResponseLocale="false" />
<i18n:message id="discTopicHeader" key="discTopicHeader"/>
<i18n:message id="discPostedByHeader" key="discPostedByHeader"/>
<i18n:message id="discPostDateHeader" key="discPostDateHeader"/>
<i18n:message id="discNoPostsMessage" key="discNoPostsMessage"/>

   <%-- put this here to work around bug where first URL doesnt get a ? before first param --%>
   <core:CreateUrl id="testURL" url="<%= origURI %>">
   </core:CreateUrl>

<discuss:getForumTopics id="forumTopics"
         forumID="<%= forumID %>"
         sortOrder='<%= atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("sortOrder") %>'
         startRange="<%= startRange %>"
         rangeSize="<%= rangeSize %>">


<table width="500" cellspacing="0" cellpadding="0" border="0">

<tr>
<td colspan="4" ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height="3" width="1" border="0"></td>
</tr>

<%-- Create listing only if there are postings on this board --%>
<core:ExclusiveIf>

  <core:If value="<%= forumTopics.getTopicCount() <= 0 %>">
    <tr  bgcolor="#<%=gearBGColor %>">
      <td colspan="4"><font class="small" color="#<%= gearTextColor %>">&nbsp;<%= discNoPostsMessage %></font></td>
    </tr>
  </core:If>

<core:DefaultCase>

<tr>
<td colspan="4" bgcolor="#ffffff" ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height="1" width="1" border="0"></td>
</tr>

<%-- Column headers allow changing sort order --%>
<tr bgcolor="#<%= titleBGColor %>">
      <td valign="top" align="left"  width="40%">
      <font class="small" color="#<%= titleTextColor %>">&nbsp;<%= discTopicHeader %></font>
      </td>

      <td nowrap valign="top" align="left" width="30%">
      <font class="small" color="#<%= titleTextColor %>"><%= discPostedByHeader %></font>
      </td>

      <td nowrap valign="top" align="left" width="15%">
      <font class="small" color="#<%= titleTextColor %>"><%= discPostDateHeader %></font>
      </td>
</tr>

<tr bgcolor="#<%= titleBGColor %>">
<td colspan="4"><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height="2" width="1" border="0"></td>
</tr>

<tr>
<td colspan="4" bgcolor="#666666" ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height="1" width="1" border="0"></td>
</tr>
<tr>
<td colspan="4" bgcolor="#ffffff" ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height="5" width="1" border="0"></td>
</tr>

   <% String userName=null; %>
   <core:ForEach id="topicsForEach"
              values="<%= forumTopics.getTopicList() %>"
              castClass="atg.repository.RepositoryItem"
              elementId="topic">

	    <%-- Had to do this stuff to avoid null pointer exception on login name. For some reason,
	         using core:ExclusiveIf to filter both null user and null login didnt work. --%>
	    <% userName="anonymous";
	       if (topic.getPropertyValue("user")!=null) {
	         if ( ((atg.repository.RepositoryItem) topic.getPropertyValue("user")).getPropertyValue("login")!=null) {
		   userName= (String) ((atg.repository.RepositoryItem) topic.getPropertyValue("user")).getPropertyValue("login");
		 }
	       }

	    %>

      <tr bgcolor="#<%= gearBGColor %>">
  	<td valign="top" align="left" width="50%">
          &nbsp;<b><font class="small"><%= topic.getPropertyValue("subject") %></font></b>
        </td>

  	<td valign="top" align="left" width="30%">
 	  <font class="smaller" color="#<%= gearTextColor %>">
	  <%=userName%>
	  </font></td>
  	<td rowspan="2" valign="top" align="left" width="10%">
	<font size="-1" color="#<%= gearTextColor %>">
	  <%= topic.getPropertyValue("creationDate") %>
	</font></td>
      </tr>

      <tr bgcolor="#<%= gearBGColor %>">
  	<td valign="top" align="left" colspan="3">

  	<%--  ** table to keep long posting indented  ** --%>
  		<table cellpadding="5" cellspacing="0" border="0"><tr><td>
				<font class="small" color="#<%= gearTextColor %>">
          <core:If value='<%= topic.getPropertyValue("content") !=null %>'>
	     			<%= topic.getPropertyValue("content") %>
	  			</core:If>
						</font>
			</td></tr></table>

	</td>
      </tr>
      <tr bgcolor="#<%= gearBGColor %>">
        <td colspan="4"><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height="3" width="1" border="0"></td>
      </tr>

      <tr>
        <td colspan="4"><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height="2" width="1" border="0"></td>
      </tr>
   </core:ForEach>

</core:DefaultCase>
</core:ExclusiveIf>

      <tr>
        <td colspan="3" bgcolor="#666666" ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height="1" width="1" border="0"></td>
      </tr>

</table>


</discuss:getForumTopics>
</dex:DocExchPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/discussionList.jsp#2 $$Change: 651448 $--%>
