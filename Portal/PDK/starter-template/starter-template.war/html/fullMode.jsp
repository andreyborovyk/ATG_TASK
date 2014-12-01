<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<dsp:page>
<i18n:bundle baseName="atg.gears.helloworld.helloworld" localeAttribute="userLocale" changeResponseLocale="false" />

<h3><i18n:message key="helloworld-full"/></h3>
 
<p>

<i18n:message key="helloworld-description"/>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/PDK/starter-template/starter-template.war/html/fullMode.jsp#2 $$Change: 651448 $--%>
