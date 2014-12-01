<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">
<i18n:bundle baseName="atg.gears.helloworld.helloworld" localeAttribute="userLocale" changeResponseLocale="false" />

<h3><i18n:message key="helloworld-shared"/></h3>

<p>

<core:CreateUrl id="fullGearUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
  <core:UrlParam param="paf_dm" value="full"/>
  <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
  <p>
  <i18n:message key="clickhere">
    <% String newFullGearUrlString = "<a href=\"" + fullGearUrl.getNewUrl() + "\">"; %>
    <i18n:messageArg value="<%= newFullGearUrlString %>"/>
    <i18n:messageArg value="</a>"/>
  </i18n:message>
</core:CreateUrl>

<p>

<h3><i18n:message key="gear-environment"/></h3><br>
<ul>
<li><i18n:message key="orig-request-uri-label"/><%= pafEnv.getOriginalRequestURI() %></li>
<li><i18n:message key="community-label"/><%= pafEnv.getCommunity() %></li>
<li><i18n:message key="page-label"/><%= pafEnv.getPage() %></li>
<li><i18n:message key="displaymode-label"/><%= pafEnv.getDisplayMode() %></li>
<li><i18n:message key="gear-label"/><%= pafEnv.getGear() %></li>
<li><i18n:message key="region-label"/><%= pafEnv.getRegion() %></li>
</ul>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/PDK/starter-template/starter-template.war/html/sharedMode.jsp#2 $$Change: 651448 $--%>
