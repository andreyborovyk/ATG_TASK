<!-- Begin contacts Gear display -->
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/alert-taglib" prefix="alert" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<dsp:page>

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
%>

<center>
<table width="99%" border="0">

  <!-- Group Alerts -->


<!-- Community Alerts -->
<core:if value='<%= gearEnv.getGearInstanceParameter("communityEnabled") %>'>
  <tr bgcolor="<%= highlightBGColor %>">
    <td valign="bottom" align="left" width="10%"><font size="1" color="<%=highlightTextColor %>" class="small">&nbsp;<i18n:message key="date-header"/>&nbsp;</font></td>
    <td valign="bottom" align="left" width="50%"><font size="1" color="<%=highlightTextColor %>" class="small">&nbsp;<i18n:message key="community-message-header"/></font></td>
  </tr>

  <alert:getAlerts id="alerts"
                   gearEnvironment="<%= gearEnv %>"
                   index="0"
                   count="-1"  
                   alertType="alert_group">
    <core:ForEach id="alertsForEach"
                  values="<%= alerts.getAlerts() %>"
                  castClass="atg.portal.alert.Alert"
                  elementId="alert">
      <tr bgcolor="<%= gearBGColor %>" >
        <core:ifNull value="<%= alert.getURL() %>">
          <td nowrap><font size="1" color="<%=gearTextColor %>"><i18n:formatDateTime value="<%= alert.getCreationDate() %>" dateStyle="short" timeStyle="short" /></font></td>
          <td><font size="1" color="<%=gearTextColor %>"><%= alert.getTitle() %></font></td>
        </core:ifNull>
        <core:ifNotNull value="<%= alert.getURL() %>">
          <td valign="top" align="left" nowrap><font size="1" color="<%=gearTextColor %>"><i18n:formatDateTime value="<%= alert.getCreationDate() %>" dateStyle="short" timeStyle="short" /></font></td>
          <td><font size="1" color="<%=gearTextColor %>"><a href="<%= alert.getURL() %>"><%= alert.getTitle() %></a></font></td>
        </core:ifNotNull>
      </tr>
    </core:ForEach>
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

    <td valign="bottom" align="left" width="10%"><font size="1" color="<%=highlightTextColor %>" class="small">&nbsp;<i18n:message key="date-header"/>&nbsp;</font></td>

    <td valign="bottom" align="left" width="50%"><font size="1" color="<%=highlightTextColor %>" class="small">&nbsp;<i18n:message key="user-message-header"/></font></td>

  </tr>

  <alert:getAlerts id="alerts"
                   gearEnvironment="<%= gearEnv %>"
                   index="0"
                   count="-1" 
                   alertType="alert_user">
    <core:ForEach id="alertsForEach"
                  values="<%= alerts.getAlerts() %>"
                  castClass="atg.portal.alert.Alert"
                  elementId="alert">
      <tr bgcolor="<%= gearBGColor %>" >
        <core:ifNull value="<%= alert.getURL() %>">

          <td valign="top" align="left" nowrap><font size="1" color="<%=gearTextColor %>"><i18n:formatDateTime value="<%= alert.getCreationDate() %>" dateStyle="short" timeStyle="short" /></font></td>
          <td><font size="1" color="<%=gearTextColor %>"><%= alert.getTitle() %></font>
        </core:ifNull>
        <core:ifNotNull value="<%= alert.getURL() %>">
          <td valign="top" align="left" nowrap><font size="1" color="<%=gearTextColor %>"><i18n:formatDateTime value="<%= alert.getCreationDate() %>" dateStyle="short" timeStyle="short" /></font></td>
          <td><font size="1" color="<%=gearTextColor %>"><a href="<%= alert.getURL() %>"><%= alert.getTitle() %></a></font></td>
        </core:ifNotNull>
      </tr>
    </core:ForEach>

<core:If value="<%= alerts.getAlerts().size() < 1  %>">
<tr><td colspan="2" nowrap>
<font color="<%=highlightTextColor %>" class="small">&nbsp;<i18n:message key="user-message-none"/></font>
</td></tr>
</core:If>

  </alert:getAlerts>
</core:if>

</table>
<br>
</center>

</paf:InitializeGearEnvironment>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/alert/alert.war/html/alertSearch.jsp#1 $$Change: 651360 $--%>
