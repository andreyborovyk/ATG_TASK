<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />
<paf:setFrameworkLocale />
<dsp:page>
 <dsp:include page="./community.jsp" flush="false"/>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/index1.jsp#2 $$Change: 651448 $--%>
