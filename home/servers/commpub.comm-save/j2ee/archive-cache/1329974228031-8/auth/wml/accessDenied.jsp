<%@ page contentType="text/vnd.wap.wml" %>
<?xml version="1.0"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM/DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<dsp:page>

<i18n:bundle baseName="atg.portal.authentication.AuthenticationResources" localeAttribute="userLocale" changeResponseLocale="false" />


  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
  <%
     String successURL=request.getParameter("successURL");
     if (successURL==null) {
        successURL = request.getContextPath() + "/wml/login.jsp";
     }
     String greetingName="";
  %>
<wml>
    <card id="accessDenied" title='<i18n:message key="title-authen-access-denied"/>'>
      <p align="center" mode="nowrap">
        <big>
          <b><i18n:message key="access_denied"/></b>
        </big>
      </p>
      <p mode="wrap"><i18n:message key="access_sorry_message"/></p>

      <dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>
      <dsp:droplet name="Switch">
        <dsp:param bean="Profile.transient" name="value"/>
        <dsp:oparam name="false">

 <do type="accept" label="<i18n:message key="logout-title"/>">
   <dsp:go href='<%= request.getContextPath()+"/wml/logout.jsp"%>' method="post">
  <dsp:postfield bean="ProfileFormHandler.logoutSuccessURL" value="<%= atg.servlet.ServletUtil.escapeHtmlString(successURL) %>" />
   <dsp:postfield bean="ProfileFormHandler.logout" value=" " />
  </dsp:go>
 </do>

 <do type="prev" label="Prev">
     <prev/>
 </do>


        </dsp:oparam>
	<dsp:oparam name="default">

 <do type="accept" label="<i18n:message key="login-title"/>">
   <dsp:go href='<%= request.getContextPath()+"/wml/login.jsp"%>' method="post">
  <dsp:postfield bean="ProfileFormHandler.loginSuccessURL" value="<%= atg.servlet.ServletUtil.escapeHtmlString(successURL) %>" />
   <dsp:postfield bean="ProfileFormHandler.logout" value=" " />
  </dsp:go>
 </do>


 <do type="prev" label="Prev">
   <prev/>
 </do>

        </dsp:oparam>
      </dsp:droplet>

    </card>
</wml>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/wml/accessDenied.jsp#2 $$Change: 651448 $--%>
