<%-- 
Page:           allCommunitiesFull.jsp
Gear:           Favorite Communities
gearmode:       content
displayMode:    full
Author:         Aimee Reveno
Description:    This is the full view of the content mode for Favorite Communities.
                Use of this page by the user allows them to view two lists of
                available communities to them.  The first is of all communities to
                which they are a member, the second is all communities to which
                they may have access or view.
--%>
<%@ page import="java.io.*,java.util.*,atg.repository.*" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/fcg-taglib" prefix="fcg" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<core:demarcateTransaction id="favoriteCommunitiesPageXF">

<paf:InitializeGearEnvironment id="pafEnv">
<i18n:bundle baseName="atg.gears.communities.communities" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:getvalueof id="profile" bean="Profile">

<%
    String clearGIF = pafEnv.getGear().getServletContext() + "/images/clear.gif";
%>

<blockquote>
<br><img src="<%= clearGIF %>" width="370" height="1" border="0"><br>

<table cellpadding=0 cellspacing=0 border=0 width="70%">

<fcg:UserCommunities id="faves" user="<%= (atg.repository.RepositoryItem) profile %>">

<!-- START MEMBER COMMUNITIES -->
<core:If value="<%= pafEnv.isRegisteredUser() %>">
  <core:IfNot value="<%=  faves.getMemberCommunities().size() < 1   %>">

    <tr>
      <td colspan="2" align=left>
        <b><font class="medium_bold"><i18n:message key="my-communities-title"/></font></b>
      </td>
    </tr>
    <core:ForEach id="membForEach" values="<%= faves.getMemberCommunities() %>"
                  castClass="atg.portal.framework.Community" elementId="membComm">

      <core:If value="<%=membComm.isEnabled()%>">
      <tr>
        <td align=left><font class="small">
          <core:CreateUrl id="mcUrl" 
                 url="<%= pafEnv.getCommunityURI( membComm.getId() ) %>">
            <a href="<%= mcUrl.getNewUrl() %>" class="gear_content"><%= membComm.getName(response.getLocale()) %></a>
          </core:CreateUrl></font>
        </td>
        <core:If value="<%= membComm.isLeader(pafEnv) %>">
          <td align=right><font class="smaller">
            <core:CreateUrl id="leaderUrl"
                  url="/portal/settings/community_settings.jsp" >
              <core:UrlParam param="paf_community_id" value="<%= membComm.getId() %>"/>
              <paf:encodeUrlParam param="paf_page_url" value="<%=pafEnv.getOriginalRequestURI()%>"/>
               <a href="<%= leaderUrl.getNewUrl() %>"><i18n:message key="leader-link"/></a>&nbsp;
            </core:CreateUrl></font>
          </td>
        </core:If>
        <core:IfNot value="<%= membComm.isLeader(pafEnv) %>">
          <td>&nbsp;</td>
        </core:IfNot>
      </tr>
      </core:If>
    </core:ForEach>

    <tr><td colspan="2">
        <img src="<%= clearGIF %>" width="1" height="15" border="0">
      </td></tr>

    <tr>
      <td colspan="2" bgcolor='<%= "#" + pafEnv.getPage().getColorPalette().getGearTitleBackgroundColor()%>'><img src="<%= clearGIF %>" width="1" height="1" border="0"></td>
    </tr>

  </core:IfNot>
</core:If>

<!-- END MEMBER COMMUNITIES -->

<!-- START ALL COMMUNITIES -->

<tr>
  <td colspan="2" align=left><b>
    <font class="medium_bold"><i18n:message key="all-communities-title"/></font></b>
  </td>
</tr>

<core:IfNot value="<%=  faves.getAllCommunities().size() < 1   %>">

  <core:ForEach id="commsForEach" values="<%=faves.getAllCommunities() %>"
           castClass="atg.portal.framework.Community" elementId="comm">
    <tr>
      <td align=left><font class="small">
          <core:IfNot value="<%= comm.isEnabled()%>">
            <i18n:message id="wrapperA" key="community_is_wrapper_a"/>
            <i18n:message id="wrapperB" key="community_is_wrapper_b"/>
             <%= comm.getName(response.getLocale()) %>&nbsp;
             <i18n:message key="community_is_inactive">
              <i18n:messageArg value="<%=wrapperA%>"/>
              <i18n:messageArg value="<%=wrapperB%>"/>
             </i18n:message>
            </core:IfNot>
            <core:If value="<%= comm.isEnabled()%>">
             <core:CreateUrl id="allcommUrl" url="<%= pafEnv.getCommunityURI( comm.getId() ) %>">
              <a href="<%= allcommUrl.getNewUrl() %>" class="gear_content"><%= comm.getName(response.getLocale()) %></a>
             </core:CreateUrl>
          </core:If>
      </font></td>
      <core:If value="<%= comm.isLeader(pafEnv) %>">
        <td align=right><font class="smaller">
          <core:CreateUrl id="leaderUrl"
                url="/portal/settings/community_settings.jsp" >
            <core:UrlParam param="paf_community_id" value="<%= comm.getId() %>"/>
            <paf:encodeUrlParam param="paf_page_url" value="<%=pafEnv.getOriginalRequestURI()%>"/>
              <a href="<%= leaderUrl.getNewUrl() %>"><i18n:message key="leader-link"/></a>&nbsp;
          </core:CreateUrl></font>
        </td>
      </core:If>
      <core:IfNot value="<%= comm.isLeader(pafEnv) %>">
        <td>&nbsp;</td>
      </core:IfNot>
    </tr>
  </core:ForEach>

</core:IfNot>
<core:If value="<%= faves.getAllCommunities().size() < 1 %>">

  <tr>
    <td colspan=2><font class="small">
      <i18n:message key="no-communities-msg"/></font>
    </td>
  </tr>

</core:If>

<!-- END ALL COMMUNITIES -->

</fcg:UserCommunities>

</table>
</blockquote>

<br><br>

</dsp:getvalueof>
</paf:InitializeGearEnvironment>

</core:demarcateTransaction>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/communities/communities.war/allCommunitiesFull.jsp#2 $$Change: 651448 $--%>
