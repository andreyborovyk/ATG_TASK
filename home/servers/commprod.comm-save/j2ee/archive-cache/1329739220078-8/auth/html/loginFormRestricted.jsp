<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--
     This page is a login form, which uses the ProfileFormHandler to authenticate a userid
     and password. This page is intended to serve as the default sample login page, and can be
     updated or replace for a given portal implementation.

     This version of the form is intended to be used for a "members only" restricted community, 
     where you most likely would not want a link to register a new user on this page, since after
     registering, the user still would not have access to the community by default. 
     
     If there is an authentication error, a message will be displayed, and the page re-rendered.
     If authentication is successful, the ProfileFormHandler will redirect to the URL specified
     in the loginSuccessURL property. 

     Note:  The loginSuccessURL property is set via a request parameter "successURL".  If this
     	    parameter is not set, the login form itself is set as the successURL.  This is not
	    necessarily a desirable behavior, but in this sample page, we cannot make any assumptions
	    about what be be a suitable default page, since this page is not part of any specific
	    community.  For a specific portal implementation, there may be a reasonable default (like
	    the portal/site home page, or a "default" community), in which case this page should be
	    updated (or replaced) to reflect this default.
     
--%>
<paf:setFrameworkLocale />

<dsp:page>

<i18n:bundle baseName="atg.portal.authentication.AuthenticationResources" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="loginbutton" key="login-title"/>

<html>
<head>
<title><i18n:message key="title-authen-login"/></title>

<link rel="STYLESHEET" type="text/css" href="css/default.css" src="css/default.css">
</head>

<%

   String userMessage = request.getParameter("userMessage");
   if ( userMessage == null) {
        userMessage   = "";
   }
   String greetingName="";

   String pageBGColor=request.getParameter("col_pb");
   if (pageBGColor==null) {
      pageBGColor="999999";
   }
   String titleBGColor=request.getParameter("col_gtb");
   if (titleBGColor==null) {
      titleBGColor="336699";
   }
   String titleTextColor=request.getParameter("col_gtt");
   if (titleTextColor==null) {
      titleTextColor="CCCCCC";
   }
   String gearBGColor=request.getParameter("col_gb");
   if (gearBGColor==null) {
      gearBGColor="CCCCCC";
   }
   String gearTextColor=request.getParameter("col_gt");
   if (gearTextColor==null) {
      gearTextColor="000000";
   }
   String successURL=request.getParameter("successURL");
   if (successURL==null) {
      successURL = request.getContextPath() + "/html/loginForm.jsp";
   }
   String registerURL = request.getContextPath() + "/html/registrationForm.jsp";
   String registerSuccessURL = request.getContextPath() + "/html/registrationSuccessPage.jsp";
   
%>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

    <!-- This form should not show what the current profile attributes are so we will
    	 disable the ability to extract default values from the profile. -->
  <dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>

<body   bgcolor='#ffffff' text='#000000' link='#6666ff' vlink='#336699' alink='#996633' marginheight='0' marginwidth='0' topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0'>
 <dsp:droplet name="Switch">
  <dsp:param bean="ProfileFormHandler.profile.transient" name="value"/>

  <dsp:oparam name="false">
   
     <dsp:getvalueof id="login" idtype="java.lang.String" bean="Profile.login">
<br><blockquote><font class="smaller" color="#666666"><i18n:message key="already-logged-in-msg">
<i18n:messageArg value="<%=login%>"/></i18n:message></font></blockquote><br>
     </dsp:getvalueof>
  </dsp:oparam>

  <%-- ************ LOGIN FORM ************ --%>

  <dsp:oparam name="default">

   <dsp:form action='<%= request.getContextPath() +"/html/loginForm.jsp"%>' method="POST">

    <table border="0" cellpadding="0" cellspacing="0" width="385">
      <tr>
        <td width="350"><img src="<%= request.getContextPath() +"/images/login/left_filler_arrows.gif"%>" width="350" height="505" border="0"></td>
        <td valign="top">
          <table border="0" cellpadding="0" cellspacing="0" width="64">
            <tr>
              <td><img src="<%= request.getContextPath() +"/images/login/en_upper_area.gif"%>" width="298" height="281" border="0"></td>
            </tr>

     <dsp:input bean="ProfileFormHandler.loginSuccessURL" type="HIDDEN" value="<%= successURL %>" />
     <input type="hidden" name="successURL" value="<%=successURL%>" >
     <input type="hidden" name="col_pb" value="<%=pageBGColor%>" >
     <input type="hidden" name="col_gtt" value="<%=titleTextColor%>" >
     <input type="hidden" name="col_gtb" value="<%=titleBGColor%>" >
     <input type="hidden" name="col_gb" value="<%=gearBGColor%>" >
     <input type="hidden" name="col_gt" value="<%=gearTextColor%>" >

            <tr>
            <td bgcolor="#d9d9d9"><font color="#333333" class="small">

    <core:IfNotNull value="<%= userMessage%>">
     <blockquote><b>

      <%= userMessage %>

     </b></blockquote>
     </core:IfNotNull>


      </font>


                <table bgcolor="#d9d9d9" width="297" cellpadding=0 cellspacing=0 border=0  >
                  <tr>
                    <td bgcolor="#FFFFFF"  rowspan=8 width="1"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="1" height="1" border="0"></td>
                    <td bgcolor="#333333" width="90"   ><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="90"   height="1" border="0"></td>
                    <td bgcolor="#333333" width="207" ><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="206" height="1" border="0"></td>
                  </tr>
                  <tr>
                    <td bgcolor="white"  colspan=2><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="1" height="1" border="0"></td>
                  </tr>

                  <dsp:droplet name="Switch">
                  <dsp:param bean="ProfileFormHandler.formError" name="value"/>
                  <dsp:oparam name="true">
                  <tr><td colspan="2"> 
                  <center><br>
                  <font class="error"><b>
                  <dsp:droplet name="ProfileErrorMessageForEach">
                   <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
                    <dsp:oparam name="output">
                     <i18n:message key="login_failure_error_msg"/><br>
                    </dsp:oparam>
                   </dsp:droplet>
                  </b></font><br></center>
                  </td></tr>
                  </dsp:oparam>
                  </dsp:droplet>

                  <tr>
                    <td colspan=2>

  <table cellpadding=0 cellspacing=2 border=0>
    <tr><td></td>
                    <td align="right"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="1" height="5" border="0"></td>
                  </tr>
                  <tr>
                    <td><font class="smaller" color="#666666">&nbsp;&nbsp;&nbsp;<i18n:message key="login-label"/>&nbsp;&nbsp;&nbsp;</font></td>
                    <td align="right"><font class="small"><dsp:input type="TEXT" size="16" maxlength="20" bean="ProfileFormHandler.value.login"/></font></td>
                  </tr>
                  <tr>
                    <td></td>
                    <td align="right"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="1" height="3" border="0"></td>
                  </tr>
                  <tr>
                    <td><font class="smaller" color="#666666">&nbsp;&nbsp;&nbsp;<i18n:message key="password-label"/>&nbsp;&nbsp;&nbsp;</font></td>
                    <td align="right"><font class="small"><dsp:input type="PASSWORD" size="16" maxlength="35" bean="ProfileFormHandler.value.password"/></font></td>
                  </tr>
                  <tr>
                    <td></td>
                    <td align="right"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="1" height="1" border="0"></td>
                  </tr>
                  <tr>
                    <td>&nbsp;</td>
                    <td align="right"><font class="smaller">&nbsp;

                    <dsp:input type="SUBMIT" value="<%= loginbutton %>" bean="ProfileFormHandler.login"/>

                   </font></td>
                  </tr>
                  <tr>
                    <td></td>
                    <td align="right"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="1" height="5" border="0"></td>
                  </tr>
                  </table></td>
                  </tr>
                  <tr>
                    <td bgcolor="#333333" colspan=2><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="1" height="1" border="0"></td>
                  </tr>
                  </table>
                 </td>
                </tr>

                <tr>
                  <td><img src="<%= request.getContextPath() +"/images/login/lower_area_noregis.gif"%>" width="298" height="73" border="0" ></td>
                </tr>

              </table>
            </td>
          </tr>
        </table>

   </dsp:form>

  </dsp:oparam>

</dsp:droplet>

</body>

</html>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/html/loginFormRestricted.jsp#2 $$Change: 651448 $--%>
