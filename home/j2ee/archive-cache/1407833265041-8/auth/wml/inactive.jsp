<%@ page contentType="text/vnd.wap.wml" %>
<?xml version="1.0"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM/DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<i18n:bundle baseName="atg.portal.authentication.AuthenticationResources" localeAttribute="userLocale" changeResponseLocale="false" />
<wml>
    <card id="accessDenied" title='<i18n:message key="title-inactive-community"/>'>
      <p align="center" mode="nowrap">
        
          <b><i18n:message key="wml-inactive"/></b>
        
      </p>
      <p mode="wrap"><i18n:message key="inactive_sorry_message"/></p>
    </card>
</wml>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/wml/inactive.jsp#2 $$Change: 651448 $--%>
