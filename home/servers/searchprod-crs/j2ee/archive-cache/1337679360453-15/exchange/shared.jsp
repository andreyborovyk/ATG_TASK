<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">

<i18n:bundle baseName="atg.portal.gear.exchange.Exchange" changeResponseLocale="false" />

<core:ExclusiveIf>
  <core:If value='<%= (gearEnv.getGearUserParameter("userID") == null) || ("".equals(gearEnv.getGearUserParameter("userID"))) %>'>
  <br />
   <font class="small">
   <blockquote>
    <i18n:message key="WARN_CONFIG"/>
   </blockquote>
   </font>	
  <br />
  </core:If>
  <core:DefaultCase>
    <ul>
      <core:If value='<%= "true".equals(gearEnv.getGearUserParameter("showInbox")) %>'>
        <li>
          <core:CreateUrl id="inboxUrl" url='<%= gearEnv.getGearInstanceParameter("proxyURL") %>'>
            <core:UrlParam param="paf_dm" value="full" />
            <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>" />
            <core:UrlParam param="content" value="Inbox" />
            <a href="<%= inboxUrl.getNewUrl() %>"><i18n:message key="INBOX"/></a>
          </core:CreateUrl>
        </li>
      </core:If>
      <core:If value='<%= "true".equals(gearEnv.getGearUserParameter("showCalendar")) %>'>
        <li>
          <core:CreateUrl id="calendarUrl" url='<%= gearEnv.getGearInstanceParameter("proxyURL") %>'>
            <core:UrlParam param="paf_dm" value="full" />
            <core:UrlParam param="paf_gear_id" value='<%= gearEnv.getGear().getId() %>' />
            <core:UrlParam param="content" value="Calendar" />
            <a href="<%= calendarUrl.getNewUrl() %>"><i18n:message key="CALENDAR"/></a>
          </core:CreateUrl>
        </li>
      </core:If>
      <core:If value='<%= "true".equals(gearEnv.getGearUserParameter("showContacts")) %>'>
        <li>
          <core:CreateUrl id="contactsUrl" url='<%= gearEnv.getGearInstanceParameter("proxyURL") %>'>
            <core:UrlParam param="paf_dm" value="full" />
            <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>" />
            <core:UrlParam param="content" value="Contacts" />
            <a href="<%= contactsUrl.getNewUrl() %>"><i18n:message key="CONTACTS"/></a>
          </core:CreateUrl>
        </li>
      </core:If>
      <core:If value='<%= "true".equals(gearEnv.getGearUserParameter("showTasks")) %>'>
        <li>
          <core:CreateUrl id="tasksUrl" url='<%= gearEnv.getGearInstanceParameter("proxyURL") %>'>
            <core:UrlParam param="paf_dm" value="full" />
            <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>" />
            <core:UrlParam param="content" value="Tasks" />
            <a href="<%= tasksUrl.getNewUrl() %>"><i18n:message key="TASKS"/></a>
          </core:CreateUrl>
        </li>
      </core:If>
    </ul>
  </core:DefaultCase>
</core:ExclusiveIf>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/exchange-passthrough/exchange.war/shared.jsp#1 $$Change: 651360 $--%>
