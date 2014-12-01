<%@ taglib uri="/dsp-taglib" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<paf:hasCommunityRole roles="leader,gear-manager">
<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">
<%@ include file="../includeBundle.jspf" %>
<%
   String origURI= pafEnv.getOriginalRequestURI(); 
   String gearID=pafEnv.getGear().getId();
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String communityID = request.getParameter("paf_community_id");
   String pafSuccessURL = request.getParameter("paf_success_url");
   String communityAdminURI="/portal/settings/community_gears.jsp";
   String contextPath = pafEnv.getGearDefinition().getServletContext();
   String gearName = pafEnv.getGear().getName(response.getLocale());
   String style = "";
   String styleOn  = " class='smaller_bold' style='text-decoration:none;color:#000000;' ";
   String styleOff = " class='smaller' ";
   String configTarget = "";  // DEFAULT SETTING HERE
   if (  request.getParameter("config_page") != null ) {
      configTarget =  request.getParameter("config_page");
   }
%>
<%----%>
<%----%>
<%----%>
  <table bgcolor="#cccccc" width="98%" border="0" cellspacing="0" cellpadding="4">

    <tr>
      <td><font class="smaller">
      &nbsp;&nbsp;&nbsp;&nbsp;
            <core:CreateUrl id="editGearUrl" url="<%= origURI %>">
              <core:UrlParam param="paf_dm" value="full"/>
              <core:UrlParam param="paf_gm" value="instanceConfig"/>
              <core:UrlParam param="config_page" value="Update"/>
              <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
              <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
              <paf:encodeUrlParam param="paf_success_url" value="<%= pafSuccessURL %>"/>              
              <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
              <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
              
        <% style = ("Update".equals(configTarget)) ? styleOn : styleOff ; %>
              <a href="<%= editGearUrl.getNewUrl() %>" <%=style%>><i18n:message key="updateConfigurationLink"/></a>
            </core:CreateUrl>
<font class="small">&nbsp;|&nbsp;</font>
 
      <core:CreateUrl id="editGearUrl" url="<%= origURI %>">
        <core:UrlParam param="paf_dm" value="full"/>
        <core:UrlParam param="paf_gm" value="instanceConfig"/>
        <core:UrlParam param="config_page" value="AlertConfig"/>
        <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
        <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
        <paf:encodeUrlParam param="paf_success_url" value="<%= pafSuccessURL %>"/>              
        <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
        <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
        <% style = ("AlertConfig".equals(configTarget)) ? styleOn : styleOff ; %>
        <a href="<%= editGearUrl.getNewUrl() %>" <%=style%> ><i18n:message key="displayConfigAlertsUpdateLinkText"/></a>
      </core:CreateUrl>
    </td></tr>
   </table>

   <core:ExclusiveIf>
      <core:If value='<%="Update".equals(request.getParameter("config_page"))%>'>
	        <jsp:include page="updateInstanceConfig.jsp" flush="true" />
      </core:If>

     <core:If value='<%="AlertConfig".equals(request.getParameter("config_page"))%>'>
	      <%-- need to use @include directive since this form will issue a redirect --%>
	      <%@ include file="updateAlertConfig.jspf" %>
      </core:If>

    <core:DefaultCase>
      <jsp:include page="displayInstanceConfig.jsp" flush="true" />
    </core:DefaultCase>
 
  </core:ExclusiveIf>
</paf:InitializeGearEnvironment>
</dsp:page>
</paf:hasCommunityRole>
<%-- @version $Id: //app/portal/version/10.0.3/screenscraper/screenscraper.war/html/admin/instanceConfig.jsp#2 $$Change: 651448 $--%>
