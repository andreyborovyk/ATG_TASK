<%@ page contentType="text/vnd.wap.wml" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<i18n:bundle baseName="atg.portal.authentication.AuthenticationResources" localeAttribute="userLocale" changeResponseLocale="false" />

<?xml version="1.0"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM/DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">

<dsp:page>
  <%
     String successURL=request.getParameter("successURL");
     if (successURL==null) {
        successURL="";
     }
   %>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

  <wml>

    <card id="login" title="<i18n:message key="login-title"/>">

      <!-- This form should not show what the 
           current profile attributes are so 
           we will disable the ability to extract
           default values from the profile. -->
      <dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>

      <dsp:droplet name="Switch">
        <dsp:param bean="Profile.transient" name="value"/>
        <dsp:oparam name="false">

          <!-- A profile has already 
               been logged in -->
          <p><i18n:message key="login_card_already-logged_in"/></p>

          <do type="prev" label="Prev">
            <prev/>
          </do>

        </dsp:oparam>
        <dsp:oparam name="default">

          <dsp:droplet name="Switch">
	    <dsp:param bean="ProfileFormHandler.formError" name="value"/>
	    <dsp:oparam name="true">
 
  	      <dsp:droplet name="ProfileErrorMessageForEach">
                <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
                <dsp:oparam name="output">

                  <p><dsp:valueof param="message"/></p>

      	        </dsp:oparam>
  	      </dsp:droplet>

            </dsp:oparam>
          </dsp:droplet>  



          <p><i18n:message key="login-label"/>:<input type="text" name="login" title="<i18n:message key="login-label"/>" size="20" maxlength="20" value=""/></p>

          <p><i18n:message key="password-label"/>:<input type="password" name="password" title="<i18n:message key="password-label"/>" size="10" maxlength="35" value=""/></p>

          
          <p><anchor title="<i18n:message key="login-button-label"/>"><i18n:message key="login-button-label"/>
	    <dsp:go href='<%= request.getContextPath() +"/wml/login.jsp"%>' method="post">
	      <dsp:postfield bean="ProfileFormHandler.loginSuccessURL" value="<%= atg.servlet.ServletUtil.escapeHtmlString(successURL) %>" />
	      <dsp:postfield bean="ProfileFormHandler.value.login" value="$login" />
	      <dsp:postfield bean="ProfileFormHandler.value.password" value="$password" />
	      <dsp:postfield bean="ProfileFormHandler.login" value=" " />
	    </dsp:go>
          </anchor></p>

          <do type="prev" label="Prev">
            <prev/>
          </do>  

        </dsp:oparam>

      </dsp:droplet>

    </card>  

  </wml>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/wml/login.jsp#2 $$Change: 651448 $--%>
