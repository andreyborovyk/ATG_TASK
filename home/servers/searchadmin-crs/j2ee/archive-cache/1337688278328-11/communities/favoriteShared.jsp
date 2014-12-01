<%-- 
Page:           favoriteShared.jsp
Gear:           Favorite Communities
gearmode:       content
displayMode:    shared
Author:         Aimee Reveno
Description:    This is the shared view of the content mode for Favorite Communities.
                Use of this page by the user allows them to view communities they
                (the user) has designated as favorites for viewing here.
--%>
<%@ page import="java.io.*,java.util.*,atg.repository.RepositoryItem" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/fcg-taglib" prefix="fcg" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<core:demarcateTransaction id="favoriteCommunitiesPageXA">

<paf:InitializeGearEnvironment id="pafEnv">
<i18n:bundle baseName="atg.gears.communities.communities" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:getvalueof id="profile" bean="Profile">

<core:IfNotNull value='<%= request.getParameter("action") %>'>
  <core:If value='<%= request.getParameter("action").equals("add_favorite") %>'>
    <fcg:addFavorite id="addResult"
			user="<%= (atg.repository.RepositoryItem) profile %>"
			addCommId='<%= request.getParameter("paf_community_id") %>'>
	   <core:IfNot value="<%= addResult.getSuccess() %>">
	   <center><font color=red size="-2"><b><%= addResult.getErrorMessage() %></b></font></center>
        </core:IfNot>
    </fcg:addFavorite>
  </core:If>
</core:IfNotNull>

<fcg:UserCommunities id="faves" user="<%= (atg.repository.RepositoryItem) profile %>">

<core:exclusiveIf>
  <core:if value="<%= pafEnv.isRegisteredUser() %>">

    <%--  IF NOT IN FAVORITES  --%>

    <core:IfNot value="<%= faves.contains( pafEnv.getCommunity().getId() ) %>">
      <table width="99%" align=center border="0">
        <tr>
          <td align="left" valign="top"><font class="small"><%= pafEnv.getCommunity().getName(response.getLocale()) %></font></td>
          <td align="right" valign="top"><font class="small_bold">
            <core:CreateUrl id="addCommUrl" url="<%= pafEnv.getOriginalRequestURI()%>">
              <core:UrlParam param="action" value="add_favorite"/>
              <core:UrlParam param="paf_community_id" value="<%= pafEnv.getCommunity().getId() %>"/>
                <a href="<%=addCommUrl.getNewUrl()%>" class="gear_nav"><i18n:message key="add-favorite-link"/></a>
            </core:CreateUrl>
          </font></td>
        </tr>
      </table>
    </core:IfNot>
  </core:if>

  <%--  IF TRANSIENT  --%>

  <core:DefaultCase>
    <table width="99%" align=center border="0">
      <tr>
        <td align="left" colspan=2><font class="small"><%= pafEnv.getCommunity().getName(response.getLocale()) %>
		</font></td>
      </tr>
	</table>
  </core:DefaultCase>
</core:exclusiveIf>

<%--  END IF NOT IN FAVORITES OR TRANSIENT  --%>

<%--   THE LIST  --%>

<core:exclusiveIf>
  <core:if value="<%= pafEnv.isRegisteredUser() %>">
    <core:If value="<%= faves.getFavorites().size() > 0  %>">
      <table width="99%" align=center border="0">

    <core:ForEach id="communityLinks"
              values="<%= faves.getFavorites() %>"
              castClass="atg.portal.framework.Community"
              elementId="community">

      <tr>
        <td align=left valign=top><font class="small">
          <core:ExclusiveIf>
            <core:IfEqual object1="<%= community.getId() %>"
                    object2="<%= pafEnv.getCommunity().getId() %>">
              <%= community.getName(response.getLocale()) %>
            </core:IfEqual>
            <core:IfNot value="<%= community.isEnabled()%>">
            <i18n:message id="wrapperA" key="community_is_wrapper_a"/>
            <i18n:message id="wrapperB" key="community_is_wrapper_b"/>
             <%= community.getName(response.getLocale()) %>&nbsp;
             <i18n:message key="community_is_inactive">
              <i18n:messageArg value="<%=wrapperA%>"/>
              <i18n:messageArg value="<%=wrapperB%>"/>
             </i18n:message>
            </core:IfNot>
            <core:DefaultCase>
              <core:CreateUrl id="commUrl" url="<%= pafEnv.getCommunityURI( community.getId() ) %>">
                <a href="<%= commUrl.getNewUrl() %>" class="gear_content"><%= community.getName(response.getLocale()) %></a>
              </core:CreateUrl>
            </core:DefaultCase>
          </core:ExclusiveIf>
        </font></td>
        <core:If value="<%= community.isLeader(pafEnv) %>">
          <td align=right valign=top><font class="small_bold">
            <core:CreateUrl id="leaderUrl" url="/portal/settings/community_settings.jsp">
              <core:UrlParam param="paf_community_id" value="<%= community.getId() %>"/>
              <paf:encodeUrlParam param="paf_page_url" value="<%= pafEnv.getCommunityURI( community.getId() ) %>"/>
                <a href="<%= leaderUrl.getNewUrl() %>" class="gear_nav"><i18n:message key="leader-link"/></a>
            </core:CreateUrl>
          </font></td>
        </core:If>
        <core:IfNot value="<%= community.isLeader(pafEnv) %>">
          <td>&nbsp;</td>
        </core:IfNot>
    </core:ForEach>

      </tr>
    </table>

    </core:If>
  </core:if>

</core:exclusiveIf>


<%-- END THE LIST --%>

<%--  ALL COMMUNITIES   and   EDIT FAVORITES  --%>

<table border="0" width="99%" align=center>
  <tr><td NOWRAP align=left><font class="small_bold">
    <core:CreateUrl id="fullview" url="<%= pafEnv.getOriginalRequestURI() %>">
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
        <a href="<%= fullview.getNewUrl() %>" class="gear_nav"><i18n:message key="all-communities-link"/></a>
    </core:CreateUrl>

    <core:exclusiveIf>
      <core:if value="<%= pafEnv.isRegisteredUser() %>">
        <core:If value="<%= faves.getFavorites().size() > 0  %>">
          <core:CreateUrl id="fullview" url="<%= pafEnv.getOriginalRequestURI() %>">
            <core:UrlParam param="paf_dm" value="full"/>
            <core:UrlParam param="paf_gm" value="userConfig"/>
            <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/> | <a href="<%= fullview.getNewUrl() %>" class="gear_nav"><i18n:message key="edit-favorites-link"/></a>
          </core:CreateUrl>
        </core:If>
        <core:IfNot value="<%= faves.getFavorites().size() > 0  %>">&nbsp;
        </core:IfNot>
      </core:if>
    </core:exclusiveIf>
    </font></td>
  </tr>
</table>

<%--  END  ALL COMMUNITIES   and   EDIT FAVORITES --%>

</fcg:UserCommunities>
</dsp:getvalueof>


</paf:InitializeGearEnvironment>

</core:demarcateTransaction>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/communities/communities.war/favoriteShared.jsp#2 $$Change: 651448 $--%>
