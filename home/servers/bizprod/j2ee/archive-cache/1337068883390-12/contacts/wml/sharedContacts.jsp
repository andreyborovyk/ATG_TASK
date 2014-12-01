<!-- Begin contacts Gear display -->
<%@ taglib uri="/contacts-taglib" prefix="contacts" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<i18n:bundle baseName="atg.gears.contacts.contacts" changeResponseLocale="false" />

<paf:InitializeGearEnvironment id="gearEnv">

<contacts:getUserList id="members" 
                      community="<%= gearEnv.getCommunity() %>"
                      count='<%= gearEnv.getGearInstanceParameter("sharedDisplayCount") %>' 
                      sortOrder="acending"
                      sortField="lastName">
<p>
  <table columns="2">
  <core:ForEach id="people" 
                values="<%= members.getMembers() %>"
                castClass="atg.repository.RepositoryItem"
                elementId="member">

    <core:CreateUrl id="fullGearUrl" url="<%= gearEnv.getOriginalRequestURI() %>" xml="true">
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
      <core:UrlParam param="login" value='<%= member.getPropertyValue("login") %>'/>
      <core:UrlParam param="action" value="profile" />
      <tr>
        <td><a href="<%= fullGearUrl.getNewUrl() %>"><%= atg.servlet.ServletUtil.escapeHtmlString((String)member.getPropertyValue("firstName")) %>&nbsp;<%= atg.servlet.ServletUtil.escapeHtmlString((String)member.getPropertyValue("lastName")) %></a></td>
        <% atg.repository.RepositoryItem homeAddress =  (atg.repository.RepositoryItem) member.getPropertyValue("homeAddress"); %>
        <td><a href="wtai://wp/mc;<%= homeAddress.getPropertyValue("phoneNumber") %>"><i18n:message key="dial"/></a></td>
      </tr>
    </core:CreateUrl>
  </core:ForEach>
  </table>
</p>
  <core:CreateUrl id="fullGearUrl" url="<%= gearEnv.getOriginalRequestURI() %>" xml="true">
    <core:UrlParam param="paf_dm" value="full"/>
    <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
    <core:UrlParam param="action" value="search" />
    <core:UrlParam param="index" value="0" />
      <p align="center"><small><a href="<%= fullGearUrl.getNewUrl() %>"><i18n:message key="morecontacts"/></a></small></p>
  </core:CreateUrl>


</contacts:getUserList>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/contacts/contacts.war/wml/sharedContacts.jsp#2 $$Change: 651448 $--%>
