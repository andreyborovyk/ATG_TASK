<!-- Begin contacts Gear display -->
<%@ taglib uri="/contacts-taglib" prefix="contacts" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<i18n:bundle baseName="atg.gears.contacts.contacts" changeResponseLocale="false" />

<paf:InitializeGearEnvironment id="gearEnv">
<p>
<core:IfNotNull value='<%= request.getParameter("login") %>' >
  <contacts:getUserProfile id="profile"
                           login='<%= request.getParameter("login") %>' >
    <b><%= atg.servlet.ServletUtil.escapeHtmlString((String)profile.getUser().getPropertyValue("firstName")) %>&nbsp;<%= atg.servlet.ServletUtil.escapeHtmlString((String)profile.getUser().getPropertyValue("lastName")) %></b><br/>
    <table columns="2">
    <tr>
     <td><i18n:message key="gender-label"/></td><td><%= profile.getUser().getPropertyValue("gender") %></td>
    </tr>
    <core:IfNotNull value='<%= profile.getUser().getPropertyValue("lastActivity") %>'>
      <tr>
       <td><i18n:message key="last-act-label"/></td><td><%= profile.getUser().getPropertyValue("lastActivity") %></td>
      </tr>  
    </core:IfNotNull>
    <% 
      atg.repository.RepositoryItem homeAddress =  (atg.repository.RepositoryItem) profile.getUser().getPropertyValue("homeAddress"); 
    %>
    <core:IfNotNull value='<%= homeAddress.getPropertyValue("phoneNumber") %>'>
      <tr>
       <td><i18n:message key="phone-label"/></td><td><%= homeAddress.getPropertyValue("phoneNumber") %></td>
      </tr>  
    </core:IfNotNull>
    </table>

  </contacts:getUserProfile>
</core:IfNotNull>
<core:IfNull value='<%= request.getParameter("login") %>' >
  <b><i18n:message key="error-message"/></b>
</core:IfNull>
</p>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/contacts/contacts.war/wml/contactsProfile.jsp#2 $$Change: 651448 $--%>
