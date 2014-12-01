<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--
     This page is a login form, which uses the ProfileFormHandler to authenticate a userid
     and password. This page is intended to serve as the default sample login page, and can be
     updated or replace for a given portal implementation.
     
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

<paf:InitializeEnvironment id="pafEnv">
<i18n:bundle baseName="atg.portal.authentication.AuthenticationResources" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="loginbutton" key="login-title"/>



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
   String successURL = request.getParameter("successURL");
   if (successURL==null) {
       successURL = pafEnv.getLoginURI();
   }
   String registerURL = pafEnv.getRegistrationURI();
   String registerSuccessURL = pafEnv.getRegistrationSuccessURI();
   

   String clearGif = response.encodeURL( request.getContextPath() +"/images/clear.gif" );
   String cssSrc   = response.encodeURL( request.getContextPath() +"/html/css/default.css");

%>



<html>
<head>
<title><i18n:message key="title-authen-login"/></title>
<link rel="STYLESHEET" type="text/css" href="<%=cssSrc%>" src="<%=cssSrc%>">

<script language="Javascript">
<!--

function loginFormInit(){
 if(document.loginForm != null){
  document.loginForm.login.focus();
 }
}

//-->
</script>
</head>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

    <!-- This form should not show what the current profile attributes are so we will
    	 disable the ability to extract default values from the profile. -->
  <dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>



<body   bgcolor='#ffffff' text='#000000' link='#6666ff' vlink='#336699' alink='#996633' marginheight='0' marginwidth='0' topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0' onload="loginFormInit()">

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

   <dsp:form action='<%= request.getContextPath() +"/html/loginForm.jsp"%>' method="POST" name="loginForm">

    <table border="0" cellpadding="0" cellspacing="0" width="385">
      <tr>
        <td width="350"><img src="<%= response.encodeURL(  request.getContextPath() +"/images/login/left_filler_arrows.gif")%>" width="350" height="505" border="0"></td>
        <td valign="top">
          <table border="0" cellpadding="0" cellspacing="0" width="64">
            <tr>
              <td><img src="<%=  response.encodeURL( request.getContextPath() +"/images/login/en_upper_area.gif")%>" width="298" height="281" border="0"></td>
            </tr>

     <dsp:input bean="ProfileFormHandler.loginSuccessURL" type="HIDDEN" value="<%= successURL %>" />
     <input type="hidden" name="successURL" value="<%=successURL%>" >
     <input type="hidden" name="col_pb" value="<%=pageBGColor%>" >
     <input type="hidden" name="col_gtt" value="<%=titleTextColor%>" >
     <input type="hidden" name="col_gtb" value="<%=titleBGColor%>" >
     <input type="hidden" name="col_gb" value="<%=gearBGColor%>" >
     <input type="hidden" name="col_gt" value="<%=gearTextColor%>" >


<tr>

<td bgcolor="#d9d9d9"><font color="#333333" class="small"

    <core:IfNotNull value="<%= userMessage%>">
     <blockquote><b>

      <%= userMessage %>

     </b></blockquote>
     </core:IfNotNull>


</font>



<table bgcolor="#d9d9d9" width="190" cellpadding=0 cellspacing=0 border=0 >
 <tr>
     <td bgcolor="#FFFFFF"  rowspan=9 width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
     <td bgcolor="#666666" width="190"  colspan="1"  ><img src="<%= clearGif %>" width="296"   height="1" border="0"></td>

     <td bgcolor="#FFFFFF"  rowspan=9 width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
  
                  </tr>
                  <tr>
                    <td bgcolor="white"  colspan=2><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
                  </tr>

                  <dsp:droplet name="Switch">
                  <dsp:param bean="ProfileFormHandler.formError" name="value"/>
                  <dsp:oparam name="true">
                  <tr><td colspan="2"> 
                  <center><br>
                  <font class="small" color=cc0000><b>
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
                    <td colspan=2><table cellpadding=0 cellspacing=2 border=0 width="205">
    <tr><td></td>
     <td align="right"><img src="<%=clearGif%>" width="1" height="5" border="0"></td>
     </tr>
     <tr>
     <td><font class="smaller" color="#666666">&nbsp;&nbsp;&nbsp;<i18n:message key="login-label"/>&nbsp;&nbsp;&nbsp;</font></td>
     <td align="right"><font class="small"><dsp:input type="TEXT" size="12" maxlength="20" bean="ProfileFormHandler.value.login" name="login"/></font></td>
    </tr>
   <tr>
   <td></td>
   <td align="right"><img src="<%= clearGif%>" width="1" height="3" border="0"></td>
  </tr>
  <tr>
   <td><font class="smaller" color="#666666">&nbsp;&nbsp;&nbsp;<i18n:message key="password-label"/>&nbsp;&nbsp;&nbsp;</font></td>
  <td align="right"><font class="small"><dsp:input type="PASSWORD" size="12" maxlength="35" bean="ProfileFormHandler.value.password"/></font></td>
  </tr>
  <tr>
  <td></td>
  <td align="right"><img src="<%= clearGif%>" width="1" height="1" border="0"></td>
  </tr>
  <tr>
  <td>&nbsp;</td>
  <td align="right"><font class="smaller">&nbsp;
  <dsp:input type="SUBMIT" value="<%= loginbutton %>" bean="ProfileFormHandler.login"/>
 </font></td>
  </tr>
  <tr>
  <td></td>
 <td align="right"><img src="<%=clearGif%>" width="1" height="5" border="0"></td>
 </tr>
 </table></td>
                  </tr>
                  <tr>
                    <td bgcolor="#666666" ><img src="<%= clearGif%>" width="1" height="1" border="0"></td>
                  </tr>
                  <tr>
                    <td bgcolor="#ffffff" ><img src="<%= clearGif%>" width="1" height="1" border="0"></td>
                  </tr>
               
   <core:CreateUrl id="registerUrl" url="<%= registerURL %>">
     <paf:encodeUrlParam param="communityURL" value='<%= successURL %>'/>
      <core:UrlParam param="successURL" value="<%= registerSuccessURL %>"/>
      <core:UrlParam param="col_pb" value="<%= pageBGColor %>"/>
      <core:UrlParam param="col_gtt" value="<%= titleTextColor %>"/>
      <core:UrlParam param="col_gtb" value="<%= titleBGColor %>"/>
      <core:UrlParam param="col_gt" value="<%= gearTextColor %>"/>
      <core:UrlParam param="col_gb" value="<%= gearBGColor %>"/>
<tr>
<td bgcolor="#d9d9d9" NOWRAP><font class="smaller_bold" size="-1" color="#3d3d3d">&nbsp;&nbsp;&nbsp;&nbsp;<b><i18n:message key="login-pre-reg-question"/> <i18n:message key="login-register-link-arg1"/> <a href="<%=registerUrl.getNewUrl() %>"><i18n:message key="login-register-link-text"/></a> <i18n:message key="login-register-link-arg2"/></b>

</font></td></tr>

    </core:CreateUrl>





                  </table></td>
                </tr>

<tr><td><img src="<%=  response.encodeURL( request.getContextPath() +"/images/login/en_lower_area_logout.gif")%>" width="298" height="73" border="0" usemap="#en_lower_area14c205e"></td>
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

</paf:InitializeEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/html/loginForm.jsp#2 $$Change: 651448 $--%>
