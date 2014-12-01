<!-- Begin contacts Gear display -->
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/alert-taglib" prefix="alert" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<paf:includeOnly/>
<dsp:page>

<core:demarcateTransaction id="alertSharedPageXA">

<paf:InitializeGearEnvironment id="gearEnv">

<i18n:bundle baseName="atg.portal.gear.alert.alert" localeAttribute="userLocale" changeResponseLocale="false" />

<%
  atg.portal.framework.ColorPalette cp = gearEnv.getPage().getColorPalette();
  String gearBGColor = cp.getGearBackgroundColor();

  String highlightTextColor = cp.getHighlightTextColor();
  String highlightBGColor = cp.getHighlightBackgroundColor();

  if (cp.getHighlightBackgroundColor() == null || cp.getHighlightBackgroundColor() == "" ) 
  {
    highlightTextColor = "#000000";
    highlightBGColor   = "#cccccc";
  }
  String gearTextColor = "#" + cp.getGearTextColor();
  boolean showAllLink  = false;
%>

<center>

<!-- Group Alerts - targetted for community, role, org -->
<table width="99%" border="0">


<!-- Community Alerts -->
<core:if value='<%= gearEnv.getGearInstanceParameter("communityEnabled") %>'>

  <tr bgcolor="<%= highlightBGColor %>">
    <td valign="bottom" align="left" width="10%"><font color="<%=highlightTextColor %>" class="small">&nbsp;<i18n:message key="date-header"/></font></td>
    <td valign="bottom" align="left" width="50%"><font color="<%=highlightTextColor %>" class="small">&nbsp;<i18n:message key="community-message-header"/></font></td>
  </tr>

  <alert:getAlerts id="alerts"
                   gearEnvironment="<%= gearEnv %>"
                   index="0"
                   alertType="alert_group"
                   count='<%= gearEnv.getGearInstanceParameter("sharedDisplayCount") %>'>
    <core:forEach id="alertsForEach"
                  values="<%= alerts.getAlerts() %>"
                  castClass="atg.portal.alert.Alert"
                  elementId="alert">
    <% showAllLink = true; %>  
    <tr bgcolor="<%= gearBGColor %>" >
        <core:ifNull value="<%= alert.getURL() %>">
          <td valign="top" align="left" nowrap><font class="smaller" color="<%=gearTextColor %>"><i18n:formatDateTime value="<%= alert.getCreationDate() %>" dateStyle="short" timeStyle="short" />&nbsp;</font></td>
          <td><font size="1" color="<%=gearTextColor %>"><%= alert.getTitle() %></font></td>
        </core:ifNull>
        <core:ifNotNull value="<%= alert.getURL() %>">
          <td valign"top" align="left"><font class="smaller" color="<%=gearTextColor %>"><i18n:formatDateTime value="<%= alert.getCreationDate() %>" dateStyle="short" timeStyle="short" />&nbsp;</font></td>
          <td valign="top" align="left"><font class="smaller" color="<%=gearTextColor %>"><a href="<%= alert.getURL() %>"><%= alert.getTitle() %></a></font></td>
        </core:ifNotNull>
      </tr>
    </core:forEach>
<core:If value="<%= alerts.getAlerts().size() < 1  %>">
<tr><td colspan="2" nowrap><font color="<%=highlightTextColor %>" class="small">&nbsp;<i18n:message key="community-message-none"/></font>
</td></tr>
</core:If>
  </alert:getAlerts>


<tr><td><font size="-2">&nbsp;</font></td></tr>
</core:if>

<!-- User Alerts -->
<core:if value='<%= gearEnv.getGearInstanceParameter("userEnabled") %>'>
  <tr bgcolor="<%= highlightBGColor %>">
    <td valign="bottom" align="left" width="10%">
      <font class="small" color="<%=highlightTextColor %>" class="small">&nbsp;<i18n:message key="date-header"/></font>
    </td>
    <td valign="bottom" align="left" width="50%"><font class="small" color="<%=highlightTextColor %>" class="small">&nbsp;<i18n:message key="user-message-header"/></font>
    </td>
  </tr>

  <alert:getAlerts id="alerts"
                   gearEnvironment="<%= gearEnv %>"
                   index="0"
                   alertType="alert_user"
                   count='<%= gearEnv.getGearInstanceParameter("sharedDisplayCount") %>' >
    <core:forEach id="alertsForEach"
                  values="<%= alerts.getAlerts() %>"
                  castClass="atg.portal.alert.Alert"
                  elementId="alert">
    <% showAllLink = true; %>  
      <tr bgcolor="<%= gearBGColor %>" >
        <core:ifNull value="<%= alert.getURL() %>">
          <td nowrap><font class="smaller" color="<%=gearTextColor %>"><i18n:formatDateTime value="<%= alert.getCreationDate() %>" dateStyle="short" timeStyle="short" />&nbsp;</font></td>
          <td><font class="smaller" color="<%=gearTextColor %>"><%= alert.getTitle() %></font></td>
        </core:ifNull>
        <core:ifNotNull value="<%= alert.getURL() %>">
          <td valign"top" align="left"><font class="smaller" color="<%=gearTextColor %>"><i18n:formatDateTime value="<%= alert.getCreationDate() %>" dateStyle="short" timeStyle="short" />&nbsp;</font></td>
          <td valign="top" align="left"><font class="smaller" color="<%=gearTextColor %>"><a href="<%= alert.getURL() %>"><%= alert.getTitle() %></a></font></td>
        </core:ifNotNull>
      </tr> 
    </core:forEach>


<core:If value="<%= alerts.getAlerts().size() < 1  %>">
<tr><td colspan="2" nowrap>
<font color="<%=highlightTextColor %>" class="small">&nbsp;<i18n:message key="user-message-none"/></font>
</td></tr>
</core:If>
  </alert:getAlerts>


<tr><td><font size="-2">&nbsp;</font></td></tr>
</core:if>


<core:If value="<%= showAllLink %>">
<tr><td colspan="2"><font  class="small_bold">
      <core:CreateUrl id="moreUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
        <core:UrlParam param="paf_dm" value="full" />
        <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>" />
        <core:UrlParam param="action" value="search" />
        <core:UrlParam param="index" value="0"/>
        <a  class="gear_nav" href="<%= moreUrl.getNewUrl() %>"><i18n:message key="more-link"/></a>
      </core:CreateUrl>
   </font></td></tr>
</core:If>

</table>
</center>

</paf:InitializeGearEnvironment>

</core:demarcateTransaction>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/alert/alert.war/html/alertShared.jsp#1 $$Change: 651360 $--%>
