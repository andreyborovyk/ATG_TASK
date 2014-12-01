<%@ taglib uri="/contacts-taglib" prefix="contacts" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<paf:hasCommunityRole roles="leader,gear-manager">

<paf:InitializeGearEnvironment id="pafEnv">

<i18n:bundle baseName="atg.gears.contacts.contacts" localeAttribute="userLocale" changeResponseLocale="false" />

<% String origURI= pafEnv.getOriginalRequestURI();
   String gearID = pafEnv.getGear().getId();
   String gearName = pafEnv.getGear().getName(response.getLocale());
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String successURL = request.getParameter("paf_success_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String communityID = request.getParameter("paf_community_id");
   String styleOn  = " class='smaller_bold' style='text-decoration:none;color:#000000;' ";
   String styleOn2  = " class='smaller_bold' style='color:#0000cc;' ";
   String styleOff = " class='smaller' ";
   String tempStyle = styleOff;
   String style = "";
   String configTarget = "displayParams";  // DEFAULT VIEW  HERE
   if (  request.getParameter("config_page") != null ) {
      configTarget =  request.getParameter("config_page");
   }
 %>

<table bgcolor="#cccccc" width="98%" border="0" cellspacing="0" cellpadding="4">
 <tr>
   <td><font class="smaller">&nbsp;&nbsp;
  <core:CreateUrl id="mainURL" url="<%= origURI %>">
   <core:UrlParam param="paf_dm" value="full"/>
   <core:UrlParam param="paf_gm" value="instanceConfig"/>
   <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
   <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
   <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
   <paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
   <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>

<% style = ("displayParams".equals(configTarget)) ? styleOn : styleOff ; %>
<nobr><a    href='<%= mainURL.getNewUrl() + "&config_page=displayParams" %>' <%=style %> ><i18n:message key="admin_instance_link_display_params"/></a></nobr>

<font class="small">&nbsp;|&nbsp;</font>
<% style = ("sortParams".equals(configTarget)) ? styleOn : styleOff ; %>
<nobr><a  href='<%= mainURL.getNewUrl() + "&config_page=sortParams" %>'  <%=style %> ><i18n:message key="admin_instance_link_sort_params"/></a></nobr>

   </core:CreateUrl>   
  </font></td>
 </tr>
</table>


   <core:ExclusiveIf>

      <core:If value='<%= "displayParams".equals(configTarget) %>'>
        <jsp:include page="/html/admin/configDisplayParams.jsp" flush="true" />
      </core:If>

      <core:If value='<%="sortParams".equals(configTarget)%>'>
        <jsp:include page="/html/admin/configSortParams.jsp" flush="true" />
      </core:If>
 
  </core:ExclusiveIf>

</paf:InitializeGearEnvironment>

</paf:hasCommunityRole>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/contacts/contacts.war/html/admin/instanceConfig.jsp#2 $$Change: 651448 $--%>
