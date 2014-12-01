<%@ page import=",atg.portal.servlet.*" %>
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

   String successURL = null;
   try {
      successURL = pafEnv.getGearInstanceParameter("successURL");
      if (successURL == null) {
   	 successURL = pafEnv.getOriginalRequestURI();
      } else if (successURL.trim().length()<=0) {
   	 successURL = pafEnv.getOriginalRequestURI();
      }
   } catch(IllegalArgumentException e) {
   	 successURL = pafEnv.getOriginalRequestURI();
   }
   
   String greetingName="";

   String registerURL=pafEnv.getRegistrationURI();
   String registerSuccessURL=pafEnv.getRegistrationSuccessURI();
   String logoutURL=pafEnv.getLogoutURI();
   String updateURL=pafEnv.getUpdateProfileURI();

 //Obtain request/response
 GearServletResponse gearServletResponse = 
     (GearServletResponse)request.getAttribute(Attribute.GEARSERVLETRESPONSE);
 GearServletRequest gearServletRequest = 
     (GearServletRequest)request.getAttribute(Attribute.GEARSERVLETREQUEST);
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

    <div style="margin:5px"> 
      <table width="99%" border="0" align=center>
      <tr bgColor="#<%=gearBGColor%>">
        <td colspan="2" align=left width="100%"><font color="#<%=gearTextColor%>" class="small"><i18n:message key="greeting-message"><i18n:messageArg value="<%=greetingName%>"/></i18n:message></font><br>
	
        </td>
      </tr>
      <tr bgColor="#<%=gearBGColor%>">
        <td colspan="2"><img src="<%= pafEnv.getGear().getServletContext() + "/images/clear.gif"%>" height="5" width="1" border="0"></td>
      </tr>
      <tr bgColor="#<%=gearBGColor%>">
        <td colspan="2" align=left><font class="small_bold">

	    <core:CreateUrl id="profileUrl" url="<%= updateURL %>">
   		<paf:encodeUrlParam param="successURL" value="<%= pafEnv.getOriginalRequestURI() %>"/>
   		<core:UrlParam param="col_pb" value="<%= pageBGColor %>"/>
   		<core:UrlParam param="col_gtt" value="<%= titleTextColor %>"/>
   		<core:UrlParam param="col_gtb" value="<%= titleBGColor %>"/>
   		<core:UrlParam param="col_gt" value="<%= gearTextColor %>"/>
   		<core:UrlParam param="col_gb" value="<%= gearBGColor %>"/>
        	  <a href="<%=profileUrl.getNewUrl() %>"><i18n:message key="profile-update-link"/></a> | 
	    </core:CreateUrl>

     	    
              <a href="<%= gearServletResponse.encodeGearURL(logoutURL) %>" class="gear_nav"><i18n:message key="logout-link"/></a>
         
        </font></td>
      </tr>

      <tr bgColor="#<%=gearBGColor%>">
        <td colspan="2"><img src="<%= pafEnv.getGear().getServletContext()+ "/images/clear.gif"%>" height="2" width="1" border="0"></td>
      </tr>

      <tr>
        <td colspan="2" align="left"><font color="#<%=gearTextColor%>" class="small">
          <core:CreateUrl id="chgpassUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
            <core:UrlParam param="paf_dm" value="full"/>
            <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
            <paf:encodeUrlParam param="dest_page" value="<%= pafEnv.getOriginalRequestURI() %>"/>
            <core:UrlParam param="regmode" value="chgpass"/>
              <a href="<%= chgpassUrl.getNewUrl() %>" class="gear_nav"><i18n:message key="change-password-label"/></a>
          </core:CreateUrl>
        </font></td>
      </tr>

      </table>
     </div>

  </core:if>   
  <core:defaultCase>
  
  <table width="99%" align=center border="0">
    <dsp:form name="portalLogin" action="<%= pafEnv.getOriginalRequestURI() %>" method="POST">

    <dsp:input bean="ProfileFormHandler.loginSuccessURL" type="HIDDEN" value="<%= successURL %>" />

    <dsp:droplet name="Switch">
	<dsp:param bean="ProfileFormHandler.formError" name="value"/>
	<dsp:oparam name="true"> 
        <tr bgColor="#<%=gearBGColor%>">
           <td colspan="2" align=center>
   	   <font color="#cc0000" class="small_bold">
  	   <dsp:droplet name="ProfileErrorMessageForEach">
      		<dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
      		<dsp:oparam name="output">
                  <i18n:message key="login_failure_error_msg"/><br>
      		</dsp:oparam>
  	   </dsp:droplet>
    	   </font>
           </td>
         </tr>
      </dsp:oparam>
   </dsp:droplet>  

  <tr bgColor="#<%=gearBGColor%>">
    <td valign=middle align=right><font color="#<%=gearTextColor%>" class="small"><i18n:message key="login-label"/>:&nbsp;</font></td>
    <td>&nbsp;<dsp:input type="TEXT" size="10" maxlength="20" bean="ProfileFormHandler.value.login"/></td>
  </tr>

  <tr bgColor="#<%=gearBGColor%>">
    <td valign=middle align=right><font color="#<%=gearTextColor%>" class="small"><i18n:message key="password-label"/>:&nbsp;</font></td>
    <td>&nbsp;<dsp:input type="PASSWORD" size="10" maxlength="35" bean="ProfileFormHandler.value.password"/></td>
  </tr>

  <tr bgColor="#<%=gearBGColor%>">
    <td colspan="2" align="center"><dsp:input type="SUBMIT" value="Login" bean="ProfileFormHandler.login"/></td>
  </tr>

  <tr bgColor="#<%=gearBGColor%>">
    <td colspan="2"><img src="<%= pafEnv.getGear().getServletContext() + "/images/clear.gif"%>" height="2" width="1" border="0"></td>
  </tr>

<core:If value='<%=pafEnv.getGearInstanceParameter("showRegistration") %>'>
  <tr bgColor="#<%=gearBGColor%>">
   
      <td colspan="2" align="center"><font color="#<%=gearTextColor%>" class="small">
        <i18n:message key="register-message"/>&nbsp;<a href="<%= gearServletResponse.encodeGearURL(registerURL) %>" class="gear_content"><i18n:message key="register-link"/></a>
      </font></td>
  </tr>
</core:If>

</table>

</dsp:form>

  </core:defaultCase>
  </core:exclusiveIf>
</dsp:getvalueof>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/user_registration/user_registration.war/loginShared.jsp#2 $$Change: 651448 $--%>
