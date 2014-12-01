<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<i18n:bundle baseName="atg.gear.user_registration.user_registration" localeAttribute="userLocale" changeResponseLocale="false" />

<paf:InitializeGearEnvironment id="pafEnv">

<% 
   atg.portal.framework.ColorPalette cp = pafEnv.getPage().getColorPalette();
   String titleBGColor = cp.getGearTitleBackgroundColor();
   String titleTextColor = cp.getGearTitleTextColor();
   String gearBGColor = cp.getGearBackgroundColor();
   String gearTextColor = cp.getGearTextColor();
   String pageBGColor=cp.getPageBackgroundColor();

   String preGreeting = pafEnv.getGearInstanceParameter("preGreetingMessage");
   if (preGreeting == null) {
	 preGreeting = "";
   } 
   String postGreeting = pafEnv.getGearInstanceParameter("postGreetingMessage");
   if (postGreeting == null) {
	 postGreeting = "";
   } 
   String preRegister = pafEnv.getGearInstanceParameter("preRegisterMessage");
   if (preRegister == null) {
	 preRegister = "";
   } 
   String postRegister = pafEnv.getGearInstanceParameter("postRegisterMessage");
   if (postRegister == null) {
	 postRegister = "";
   } 
   String registerLink = pafEnv.getGearInstanceParameter("registerLinkText");
   if (registerLink == null) {
	 registerLink = "register";
    } 
   String successURL = pafEnv.getGearInstanceParameter("successURL");
   if (successURL == null) {
	 successURL = pafEnv.getOriginalRequestURI();
   } 

   String greetingName="";

   String logoutURL="/portal/authentication/logout_wml.jsp";


%>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<!-- This form should not show what the current profile attributes are so we will
     disable the ability to extract default values from the profile. -->
<dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>

<dsp:getvalueof id="profile" idtype="atg.userprofiling.Profile" bean="Profile">
  <core:exclusiveIf>
  <core:if value="<%= !profile.isTransient() %>">

      <%-- Determine whether to show name, and what name to show (firstname if there, login name if not) --%>
       <core:IfNotNull value='<%=pafEnv.getGearInstanceParameter("showNameLogin")%>'>
       <core:If value='<%=pafEnv.getGearInstanceParameter("showNameLogin") %>'>
	  <dsp:getvalueof id="firstName" idtype="java.lang.String" bean="Profile.firstName">
	    <core:ExclusiveIf>
               <core:IfNotNull value="<%=firstName%>">
	          <% greetingName= (String) firstName; %>
	       </core:IfNotNull>
               <core:DefaultCase>
	          <dsp:getvalueof id="login" idtype="java.lang.String" bean="Profile.login">
	            <% greetingName= (String) login; %>
	          </dsp:getvalueof>
               </core:DefaultCase>
	    </core:ExclusiveIf>
	  </dsp:getvalueof>
       </core:If>
       </core:IfNotNull>

<%--  commenting this out to try putting actual logout form here
      <p><%= preGreeting %> <%= greetingName %></p>

     	<core:CreateUrl id="logoutUrl" url="<%= logoutURL %>">
   		<core:UrlParam param="successURL" value="<%= pafEnv.getOriginalRequestURI() %>"/>
                <p><a href="<%= atg.servlet.ServletUtil.escapeHtmlString(logoutUrl.getNewUrl()) %>"><i18n:message key="logout-link"/></a></p>
     	</core:CreateUrl>
--%>

  <p><i18n:message key="pre-logout-msg"/>&nbsp;<%= greetingName %></p>
  <p><i18n:message key="post-logout-msg"/></p>


        <do type="accept" label="Logout">
           <dsp:go href="<%= request.getRequestURI() %>" method="post">
                <dsp:postfield bean="ProfileFormHandler.logoutSuccessURL" value="<%= atg.servlet.ServletUtil.escapeHtmlString(successURL)%>" />
                <dsp:postfield bean="ProfileFormHandler.logout" value=" " />
           </dsp:go>
        </do>


  </core:if>   
  <core:defaultCase>

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


    <do type="accept" label="Login">
	<dsp:go href="<%=pafEnv.getOriginalRequestURI()%>" method="post">
	<dsp:postfield bean="ProfileFormHandler.loginSuccessURL" value="<%=atg.servlet.ServletUtil.escapeHtmlString(successURL)%>" />
	<dsp:postfield bean="ProfileFormHandler.value.login" value="$login" />
	<dsp:postfield bean="ProfileFormHandler.value.password" value="$password" />
	<dsp:postfield bean="ProfileFormHandler.login" value=" " />
	</dsp:go>
    </do>
  
  </core:defaultCase>
  </core:exclusiveIf>
</dsp:getvalueof>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/user_registration/user_registration.war/wml/login_wml.jsp#2 $$Change: 651448 $--%>
