<!-- Begin contacts Gear display -->
<%@ taglib uri="/contacts-taglib" prefix="contacts" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<dsp:page>
<core:demarcateTransaction id="contactsSharedPageXA">

<paf:InitializeGearEnvironment id="gearEnv">
<i18n:bundle baseName="atg.gears.contacts.contacts" localeAttribute="userLocale" changeResponseLocale="false" />

<contacts:getUserList id="members" 
                      community="<%= gearEnv.getCommunity() %>"
                      count='<%= gearEnv.getGearInstanceParameter("sharedDisplayCount") %>' 
                      sortOrder='<%= gearEnv.getGearInstanceParameter("defaultSortOrder") %>'
                      sortField='<%= gearEnv.getGearInstanceParameter("defaultSortField") %>'>
  <table border="0" width="99%" align=center>



<core:ifNotNull value="<%= members.getMembers() %>">
  <core:ForEach id="people" 
                values="<%= members.getMembers() %>"
                castClass="atg.repository.RepositoryItem"
                elementId="member">
  <tr>
    <td align=left><a href="<%= "mailto:" + member.getPropertyValue("email") %>"><img src="<%= gearEnv.getGear().getServletContext() + "/html/images/envelope.gif" %>" border="0"></a><font size="-1" class="small">
      <core:CreateUrl id="fullGearUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
        <core:UrlParam param="paf_dm" value="full"/>
        <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
        <core:UrlParam param="login" value='<%= member.getPropertyValue("login") %>'/>
        <core:UrlParam param="action" value="profile" />
        &nbsp;<a href="<%= fullGearUrl.getNewUrl() %>" class="gear_content"><%= member.getPropertyValue("firstName") %>&nbsp;<%= member.getPropertyValue("lastName") %></a>
      </core:CreateUrl></font>
    </td>
  </tr>
  </core:ForEach>

  <tr>
    <td align=left><font size="-1" class="small">
      <core:CreateUrl id="fullGearUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
        <core:UrlParam param="paf_dm" value="full"/>
        <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
        <core:UrlParam param="action" value="search" />
        <core:UrlParam param="index" value="0" />
      <a href="<%= fullGearUrl.getNewUrl() %>" class="gear_nav"><i18n:message key="all-contacts-link"/></a>
      </core:CreateUrl></font>
    </td>
  </tr>
</core:ifNotNull>

<core:ifNull value="<%=members.getMembers()%>">
  <tr>
    <td align=left>
      <font size="-1" class="small"><i18n:message key="no-users-message"/></font>
    </td>
  </tr>
</core:ifNull>




</table>
</contacts:getUserList>
</paf:InitializeGearEnvironment>

</core:demarcateTransaction>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/contacts/contacts.war/html/sharedContacts.jsp#2 $$Change: 651448 $--%>
