<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<i18n:bundle baseName="atg.gear.user_registration.user_registration" localeAttribute="userLocale" changeResponseLocale="false" />


<!-- Begin login shell -->

<paf:InitializeGearEnvironment id="pafEnv">

<%

   String origURI=pafEnv.getOriginalRequestURI();
   String gearID=pafEnv.getGear().getId();
   atg.portal.framework.ColorPalette cp = pafEnv.getPage().getColorPalette();
   String titleBGColor = cp.getGearTitleBackgroundColor();
   String titleTextColor = cp.getGearTitleTextColor();
   String gearBGColor = cp.getGearBackgroundColor();
   String gearTextColor = cp.getGearTextColor();

%>

<%
   String preLogoutMsg = pafEnv.getGearInstanceParameter("preLogoutMessage");
   if (preLogoutMsg == null) {
	 preLogoutMsg = "";
    }
   String postLogoutMsg = pafEnv.getGearInstanceParameter("postLogoutMessage");
   if (postLogoutMsg == null) {
	 postLogoutMsg = "";
    }
%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
<dsp:param name="value" param="regmode" />

   <dsp:oparam name="chgpass">
	<%@ include file="changePasswordForm.jspf" %>
   </dsp:oparam>

   <dsp:oparam name="default">
   <i18n:message key="no-registration"/>
   </dsp:oparam>

</dsp:droplet>
</paf:InitializeGearEnvironment>


</dsp:page>
<!-- End login shell -->
<%-- @version $Id: //app/portal/version/10.0.3/user_registration/user_registration.war/loginShell.jsp#2 $$Change: 651448 $--%>
