<!-- Begin contacts Gear display -->
<%@ taglib uri="/contacts-taglib" prefix="contacts" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<dsp:page>
<i18n:bundle baseName="atg.gears.contacts.contacts" changeResponseLocale="false" />


<paf:InitializeGearEnvironment id="gearEnv">

<p align="center"><b><i18n:message key="unsupported"/></b></p>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/contacts/contacts.war/wml/contactsSearch.jsp#2 $$Change: 651448 $--%>
