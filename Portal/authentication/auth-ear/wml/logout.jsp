<%@ page contentType="text/vnd.wap.wml" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<i18n:bundle baseName="atg.portal.authentication.AuthenticationResources" localeAttribute="userLocale" changeResponseLocale="false" />

<?xml version="1.0"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM/DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml">

<%-- Check if the logout has been submitted --%>
<core:IfNotNull value='<%=request.getParameter("regmode")%>'>
  <core:If value='<%= request.getParameter("regmode").equals("logout") %>'>
    <core:Redirect url='<%= request.getParameter("successURL") %>'/>
  </core:If>
</core:IfNotNull>


<dsp:page>
  <%
     String successURL=request.getParameter("successURL");
     if (successURL==null) {
        successURL = request.getContextPath() + "/wml/login.jsp";
     }
     String greetingName="";
  %>
  <dsp:importbean bean="/atg/userprofiling/Profile"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
  <dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
  <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

  <wml>

    <card id="logout" title="<i18n:message key="logout-title"/>">
      <dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>

      <dsp:droplet name="Switch">
        <dsp:param bean="Profile.transient" name="value"/>
        <dsp:oparam name="false">
          
          <dsp:getvalueof id="firstName" idtype="java.lang.String" bean="Profile.firstName">
            <core:ExclusiveIf>
              <core:IfNotNull value="<%=firstName%>">
                <% greetingName= atg.servlet.ServletUtil.escapeHtmlString((String) firstName); %>
              </core:IfNotNull>
              <core:DefaultCase>
                <dsp:getvalueof id="login" idtype="java.lang.String" bean="Profile.login">
                  <% greetingName= atg.servlet.ServletUtil.escapeHtmlString((String) login); %>
                </dsp:getvalueof>
              </core:DefaultCase>
            </core:ExclusiveIf>
          </dsp:getvalueof>
          
          <p>
            <i18n:message key="pre-logout-msg"/>&nbsp;<%= greetingName %>&nbsp;<br/><br/>
            <i18n:message key="post-logout-msg"/>
          </p>
          
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

          <do type="prev" label="Prev">
            <prev/>
          </do>

        </dsp:oparam>
      </dsp:droplet>

    </card>

  </wml>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/wml/logout.jsp#2 $$Change: 651448 $--%>
