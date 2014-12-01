<!-- Begin contacts Gear display -->
<%@ taglib uri="/contacts-taglib" prefix="contacts" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<i18n:bundle baseName="atg.gears.contacts.contacts" localeAttribute="userLocale" changeResponseLocale="false" />

<%
 
  atg.portal.framework.ColorPalette cp = gearEnv.getPage().getColorPalette();
  String cellColor = cp.getGearBackgroundColor();
  String highlightTextColor = cp.getHighlightTextColor();
  String highlightBGColor = cp.getHighlightBackgroundColor();

  if (cp.getHighlightBackgroundColor() == null || cp.getHighlightBackgroundColor() == "" ) 
  {
    highlightTextColor = "#000000";
    highlightBGColor   = "#cccccc";
  }

  boolean cellColorSwitch = false;

%>

<table width="100%" cellspacing="2" cellpadding="2" border="0">

  <tr>
    <td colspan="3" bgcolor="<%= cellColor %>" align=center><font class="large_bold"><i18n:message key="search-title"/></font></td>
  </tr>

  <contacts:getUserList id="members" 
                        community="<%= gearEnv.getCommunity() %>"
                        index='<%= request.getParameter("index") %>'
                        count='<%= gearEnv.getGearInstanceParameter("fullDisplayCount") %>'
                        sortOrder='<%= gearEnv.getGearInstanceParameter("defaultSortOrder") %>'
                        sortField='<%= gearEnv.getGearInstanceParameter("defaultSortField") %>'>

    <core:ifNotNull value="<%= members.getMembers() %>">

      <tr>
        <td colspan="3">
          <core:IfGreaterThan int1="<%= members.getIndexAsInt() %>"
                            int2="<%= 0 %>"><font class="small">
            <core:CreateUrl id="fullGearUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
              <core:UrlParam param="paf_dm" value="full"/>
              <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
              <core:UrlParam param="action" value="search" />
              <core:UrlParam param="index" value="<%= Integer.toString(members.getIndexAsInt() - members.getCountAsInt() - 1)%>" />	
            &nbsp;<a href="<%= fullGearUrl.getNewUrl() %>" class="gear_nav"><i18n:message key="search-back-link"/></a>
            </core:CreateUrl></font>

          </core:IfGreaterThan>

          <core:IfEqual int1="<%= members.getMembers().length %>"
                        int2='<%= Integer.parseInt(gearEnv.getGearInstanceParameter("fullDisplayCount")) %>'><font class="small">
            <core:CreateUrl id="fullGearUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
              <core:UrlParam param="paf_dm" value="full"/>
              <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
              <core:UrlParam param="action" value="search" />
              <core:UrlParam param="index" value="<%= Integer.toString(members.getIndexAsInt() + members.getCountAsInt())%>" />	
          &nbsp;<a href="<%= fullGearUrl.getNewUrl() %>" class="gear_nav"><i18n:message key="search-forward-link"/></a>
            </core:CreateUrl></font>

          </core:IfEqual>
      </td>
    </tr>

    <tr bgcolor="#<%= highlightBGColor %>">
      <td><font color="#<%= highlightTextColor %>" class="small"><i18n:message key="col-header-name"/></font></td>
      <td><font color="#<%= highlightTextColor %>" class="small"><i18n:message key="col-header-email"/></font></td>
      <td><font color="#<%= highlightTextColor %>" class="small"><i18n:message key="col-header-last"/></font></td>
    </tr>

    <core:ForEach id="people" 
                  values="<%= members.getMembers() %>"
                  castClass="atg.repository.RepositoryItem"
                  elementId="member">

      <tr bgcolor="#<%= cellColor%>">

        <td><font class="small">
          <core:CreateUrl id="fullGearUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
            <core:UrlParam param="paf_dm" value="full"/>
            <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
            <core:UrlParam param="login" value='<%= member.getPropertyValue("login") %>'/>
            <core:UrlParam param="action" value="profile" />
              <a href="<%= fullGearUrl.getNewUrl() %>" class="gear_content"><%= member.getPropertyValue("lastName") %>,&nbsp;<%= member.getPropertyValue("firstName") %></a><br>
          </core:CreateUrl>
        </font></td>

        <td><font class="small">
          <a href="<%= "mailto:" + member.getPropertyValue("email") %>" class="gear_content"><%= member.getPropertyValue("email") %></a>
       </font></td>

        <td><font class="small">
          <i18n:formatDateTime value='<%= member.getPropertyValue("lastActivity") %>' dateStyle="short" timeStyle="short" />
        </font></td>

      </tr> 
    </core:ForEach>
  </core:ifNotNull>

  <core:ifNull value="<%=members.getMembers()%>">
    <tr>
      <td align=left colspan="3">
        <font size="-1" class="small"><i18n:message key="no-users-message"/></font>
      </td>
    </tr>
  </core:ifNull>

</contacts:getUserList>

</table>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/contacts/contacts.war/html/contactsSearch.jsp#2 $$Change: 651448 $--%>
