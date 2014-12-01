<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:hasCommunityRole roles="leader,gear-manager">
<dsp:page>

<i18n:bundle baseName="atg.gear.slotgear.SlotgearResources" localeAttribute="userLocale" changeResponseLocale="false" />
<paf:InitializeGearEnvironment id="pafEnv">

<% String origURI= pafEnv.getOriginalRequestURI();
   String gearID = pafEnv.getGear().getId();
   String gearName = pafEnv.getGear().getName(response.getLocale());
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String communityID = request.getParameter("paf_community_id");
   String successURL = request.getParameter("paf_success_url");

   String contextPath = request.getContextPath() ;
   String clearGIF = contextPath+"/images/clear.gif";
   String infoGIF = contextPath+"/images/info.gif";

    String style = "";
    String styleOn  = " class='smaller_bold' style='text-decoration:none;color:#000000;' ";
    String styleOff = " class='smaller' ";
    String configTarget = "slot";  // DEFAULT SETTING HERE
    if (  request.getParameter("config_page") != null ) {
       configTarget =  request.getParameter("config_page");
    }
 %>

<core:demarcateTransaction id="instanceConfigXA">
<%-- Display config options --%>

  <table bgcolor="#cccccc" width="100%" border="0" cellspacing="0" cellpadding="4">

    <tr>
      <td><font class="smaller">
    <%-- Slot --%>
	<core:CreateUrl id="slotURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="slot"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
	<core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
	<% style = ("slot".equals(configTarget)) ? styleOn : styleOff ; %>
	<a href="<%= slotURL.getNewUrl() %>" <%=style %>> <i18n:message key="configure-slot-link"/></a>
	</core:CreateUrl>
<font class="small">&nbsp;|&nbsp;</font>

    <%-- Targeting Params --%>
	<core:CreateUrl id="targetURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="targeting"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
	<core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
	<% style = ("targeting".equals(configTarget)) ? styleOn : styleOff ; %>
	<a href="<%= targetURL.getNewUrl() %>" <%=style %>> <i18n:message key="targeting-link"/></a>
	</core:CreateUrl>
<font class="small">&nbsp;|&nbsp;</font>
    <%-- Display Params --%>
	<core:CreateUrl id="displayURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="config_page" value="displayParams"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
	<core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
	<% style = ("displayParams".equals(configTarget)) ? styleOn : styleOff ; %>
	<a href="<%= displayURL.getNewUrl() %>" <%=style %>> <i18n:message key="display-params-link"/></a>
	</core:CreateUrl>
     </font></td>
    </tr>
   </table>
</core:demarcateTransaction>
<core:IfNotNull value='<%= request.getParameter("config_page")%>'>

   <core:ExclusiveIf>

      <core:If value='<%="targeting".equals(request.getParameter("config_page"))%>'>
        <jsp:include page="/admin/configTargetingParams.jsp" flush="true" />
      </core:If>

      <core:If value='<%="displayParams".equals(request.getParameter("config_page"))%>'>
        <jsp:include page="/admin/configDisplayParams.jsp" flush="true" />
      </core:If>

    <core:DefaultCase>
        <jsp:include page="/admin/configSlot.jsp" flush="true" />
    </core:DefaultCase>
 
  </core:ExclusiveIf>

</core:IfNotNull>

<core:IfNull value='<%= request.getParameter("config_page")%>'>
      <jsp:include page="/admin/configSlot.jsp" flush="true" />
</core:IfNull>

</paf:InitializeGearEnvironment>
</dsp:page>
</paf:hasCommunityRole>
<%-- @version $Id: //app/portal/version/10.0.3/slotgear/slotgear.war/admin/instanceConfig.jsp#2 $$Change: 651448 $--%>
