<!-- Begin contacts Gear display -->
<%@ taglib uri="/contacts-taglib" prefix="contacts" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@page import="java.io.*,java.util.*,atg.portal.framework.UserUtilities" %>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<i18n:bundle baseName="atg.gears.contacts.contacts" localeAttribute="userLocale" changeResponseLocale="false" />

<%
  String memberId = request.getParameter("login");
  String adminRoleName = gearEnv.getCommunity().LEADER_ROLE;
  String memberRoleName = gearEnv.getCommunity().MEMBER_ROLE;
  String guestRoleName = gearEnv.getCommunity().GUEST_ROLE;
  boolean viewingAllowed = false;

  if ( UserUtilities.userHasCommunityRole( memberId, gearEnv.getCommunity(), adminRoleName )
    || UserUtilities.userHasCommunityRole( memberId, gearEnv.getCommunity(), memberRoleName )
    || UserUtilities.userHasCommunityRole( memberId, gearEnv.getCommunity(), guestRoleName ) )
  { viewingAllowed = true; }

%>
<core:IfNotNull value="<%= memberId %>" >
  <core:If value="<%= viewingAllowed %>">

    <contacts:getUserProfile id="profile"
                             login='<%= request.getParameter("login") %>' >
      <table cellpadding="10" border="0" width="350">
        <tr>
          <td><font class="small">
            <i18n:message key="name-label"/>
            <%= profile.getUser().getPropertyValue("firstName") %>&nbsp;<%= profile.getUser().getPropertyValue("lastName") %><br>

            <i18n:message key="gender-label"/>
            <%= profile.getUser().getPropertyValue("gender") %><br>

            <core:IfNotNull value='<%= profile.getUser().getPropertyValue("lastActivity") %>'>
              <i18n:message key="last-act-label"/>
              <i18n:formatDateTime value='<%= profile.getUser().getPropertyValue("lastActivity") %>' dateStyle="short" timeStyle="short"/><br>
            </core:IfNotNull>
          </font></td>
        </tr>
      </table>
    </contacts:getUserProfile>
  </core:If>
  <core:IfNot value="<%= viewingAllowed %>">
    <table cellpadding="10" border="0" width="350">
      <tr>
        <td><font class="small">
          <i18n:message key="not-allowed-message"/>
        </font></td>
      </tr>
    </table>
  </core:IfNot>
</core:IfNotNull>
<core:IfNull value='<%= request.getParameter("login") %>' >
  <h3><i18n:message key="error-message"/></h3>
</core:IfNull>
</paf:InitializeGearEnvironment>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/contacts/contacts.war/html/contactsProfile.jsp#2 $$Change: 651448 $--%>
