<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:hasCommunityRole roles="leader,gear-manager">
<dsp:page>

<i18n:bundle baseName="atg.portal.gear.calendar.CalendarResources" localeAttribute="userLocale" changeResponseLocale="false" />

<paf:InitializeGearEnvironment id="pafEnv">

<% String origURI= pafEnv.getOriginalRequestURI();
   String gearID = pafEnv.getGear().getId();
   String gearName = pafEnv.getGear().getName(response.getLocale());
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String successURL = request.getParameter("paf_success_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String communityID = request.getParameter("paf_community_id");
 
   
   String style = "";
   String styleOn  = " class='smaller_bold' style='text-decoration:none;color:#000000;' ";
   String styleOff = " class='smaller' ";
   String configTarget = "calendar";  // DEFAULT SETTING HERE
   if (  request.getParameter("config_page") != null ) {
      configTarget =  request.getParameter("config_page");
   }

 %>


<core:demarcateTransaction id="calendarInstanceConfigXA">
<%-- Display config options --%>
  <table bgcolor="#cccccc" width="98%" border="0" cellspacing="0" cellpadding="4">

    <tr>
      <td><font class="smaller">&nbsp;&nbsp;

      <core:CreateUrl id="editGearUrl" url="<%= origURI %>">
        <core:UrlParam param="paf_dm" value="full"/>
        <core:UrlParam param="paf_gm" value="instanceConfig"/>
        <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
        <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
        <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>

<%-- gear parameter config --%>
<% style = ("calendar".equals(configTarget)) ? styleOn : styleOff ; %>
<a href='<%= editGearUrl.getNewUrl()+"&config_page=calendar" %>' <%=style %>> <i18n:message key="mainConfigLabel"/></a>

<font class="small">&nbsp;|&nbsp;</font>

<%-- permissions --%>
<% style = ("permissions".equals(configTarget)) ? styleOn : styleOff ; %>
<a href='<%= editGearUrl.getNewUrl()+"&config_page=permissions" %>' <%=style %>> <i18n:message key="permissionsConfigLabel"/></a>

<font class="small">&nbsp;|&nbsp;</font>

<%-- alerts config --%>
<% style = ("alerts".equals(configTarget)) ? styleOn : styleOff ; %>
<a href='<%= editGearUrl.getNewUrl()+"&config_page=alerts" %>' href="<%= editGearUrl.getNewUrl() %>" <%=style %>><i18n:message key="alertConfigLabel"/></a>

</font></td>

</core:CreateUrl>

</tr>
</table>
</core:demarcateTransaction>

<core:ExclusiveIf>

      <core:If value='<%="alerts".equals(configTarget)%>'>
        <jsp:include page="/html/admin/configAlerts.jsp" flush="true" />
      </core:If>

      <core:If value='<%="permissions".equals(configTarget)%>'>
        <jsp:include page="/html/admin/configPermissions.jsp" flush="true" />
      </core:If>

      <core:If value='<%="calendar".equals(configTarget)%>'>
        <jsp:include page="/html/admin/configCalendar.jsp" flush="true" />
      </core:If>
 
</core:ExclusiveIf>


</paf:InitializeGearEnvironment>
</dsp:page>
</paf:hasCommunityRole>
<%-- @version $Id: //app/portal/version/10.0.3/calendar/calendar.war/html/admin/instanceConfig.jsp#2 $$Change: 651448 $--%>
