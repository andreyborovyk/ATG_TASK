<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:hasCommunityRole roles="leader,gear-manager">
<dsp:page>

<i18n:bundle baseName="atg.gear.poll.PollResources" localeAttribute="userLocale" changeResponseLocale="false" />
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
   String configTarget = "setCurrentPoll";  // DEFAULT SETTING HERE
   if (  request.getParameter("config_page") != null ) {
      configTarget =  request.getParameter("config_page");
   }
 %>

<core:demarcateTransaction id="pollInstanceConfigXA">
<%-- Display config options --%>
<table bgcolor="#cccccc" width="98%" border="0" cellspacing="0" cellpadding="4">
    <tr>
      <td><font class="smaller">

<%-- redundent messaging
<font class="smaller_bold">
<i18n:message key="instanceConfigTitle"/>:&nbsp;<%=gearName%></font><br><img src='<%= response.encodeURL("images/clear.gif")%>' height="3" width="3"><br>
--%>  
<%-- return link --%>
<%-- not needed 
 <core:CreateUrl id="selectURL" url="<%=successURL%>">
  <a href="<%= selectURL.getNewUrl() %>" ><font class="smaller"><i18n:message key="config-basics-link" /></font></a>
  </core:CreateUrl>
  <font class="small">&nbsp;|&nbsp;</font>
--%>

    <%-- Set Current Poll --%>

	<core:CreateUrl id="addURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="setCurrentPoll"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
	<core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
        <% style = ("setCurrentPoll".equals(configTarget)) ? styleOn : styleOff ; %>
	<nobr><a href="<%= addURL.getNewUrl() %>"  <%=style %> ><i18n:message key="set-current-poll-link"/></a></nobr>
	</core:CreateUrl>
<font class="small">&nbsp;|&nbsp;</font>
    <%-- Create Poll --%>
	<core:CreateUrl id="addURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="addPoll"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
	<core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
        <% style = ("addPoll".equals(configTarget)) ? styleOn : styleOff ; %>
	<nobr><a href="<%= addURL.getNewUrl() %>"  <%=style %> ><i18n:message key="create-poll-link"/></a></nobr>
	</core:CreateUrl>
<font class="small">&nbsp;|&nbsp;</font>
    <%-- Available Polls --%>
	<core:CreateUrl id="adminURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="pollAdmin"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
	<core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
        <% style = ("pollAdmin".equals(configTarget)) ? styleOn : styleOff ; %>
        <nobr><a href="<%= adminURL.getNewUrl() %>" <%=style %> ><i18n:message key="poll-admin-link"/></a></nobr>
         </core:CreateUrl>
<font class="small">&nbsp;|&nbsp;</font>
      <core:CreateUrl id="editGearUrl" url="<%= origURI %>">
        <core:UrlParam param="paf_dm" value="full"/>
        <core:UrlParam param="paf_gm" value="instanceConfig"/>
        <core:UrlParam param="config_page" value="resourceBundle"/>
        <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
        <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
        <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
       <% style = ("resourceBundle".equals(configTarget)) ? styleOn : styleOff ; %>
       <nobr><a href="<%= editGearUrl.getNewUrl() %>" <%= style%>  ><i18n:message key="resourcesLabel"/></a></nobr>
      </core:CreateUrl>
<font class="small">&nbsp;|&nbsp;</font>
      <core:CreateUrl id="editGearUrl" url="<%= origURI %>">
        <core:UrlParam param="paf_dm" value="full"/>
        <core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="alerts"/>
        <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
        <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
        <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
        <% style = ("alerts".equals(configTarget)) ? styleOn : styleOff ; %>
        <nobr><a href="<%= editGearUrl.getNewUrl() %>"   <%=style %> ><i18n:message key="alertConfigLabel"/></a></nobr>
      </core:CreateUrl>
     </font></td>
    </tr>
   </table>
</core:demarcateTransaction>


   <core:ExclusiveIf>
     <core:If value='<%="resourceBundle".equals(configTarget)%>'>
        <jsp:include page="/admin/configResourceBundle.jsp" flush="true" />
     </core:If>

      <core:If value='<%="alerts".equals(configTarget)%>'>
        <jsp:include page="/admin/configAlerts.jsp" flush="true" />
      </core:If>

      <core:If value='<%="pollAdmin".equals(configTarget)%>'>
        <jsp:include page="/admin/mainPollAdmin.jsp" flush="true" />
      </core:If>

      <core:If value='<%="editPoll".equals(configTarget)%>'>
	<jsp:include page="/admin/pollEditForm.jsp" flush="true" />
      </core:If>

      <core:If value='<%="pollResults".equals(configTarget)%>'>
        <jsp:include page="/admin/pollResults.jsp" flush="true" />
      </core:If>

      <core:If value='<%="addPoll".equals(configTarget)%>'>
        <jsp:include page="/admin/pollCreateForm.jsp" flush="true" />
      </core:If>

      <core:If value='<%="editResponse".equals(configTarget)%>'>
        <jsp:include page="/admin/responseEditForm.jsp" flush="true" />
      </core:If>

      <core:If value='<%="setCurrentPoll".equals(configTarget)%>'>
        <jsp:include page="/admin/setCurrentPoll.jsp" flush="true" />
      </core:If>


   </core:ExclusiveIf>


</paf:InitializeGearEnvironment>
</dsp:page>
</paf:hasCommunityRole>
<%-- @version $Id: //app/portal/version/10.0.3/poll/poll.war/admin/instanceConfig.jsp#2 $$Change: 651448 $--%>
