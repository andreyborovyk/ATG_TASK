<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<%-- Check if the logout has been submitted --%>
<core:IfNotNull value='<%=request.getParameter("regmode")%>'>
  <core:If value='<%= request.getParameter("regmode").equals("logout") %>'>
    <core:Redirect url='<%= request.getParameter("successURL") %>'/>
  </core:If>
</core:IfNotNull>

<dsp:page>

<i18n:bundle baseName="atg.portal.authentication.AuthenticationResources" localeAttribute="userLocale" changeResponseLocale="false" />

<i18n:message key="imageroot" id="i18n_imageroot"/>
<%

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
 
   String userMessage = request.getParameter("userMessage");
   if ( userMessage == null) {
        userMessage   = "";
   }
   String greetingName="";
   String paramSuccess =  request.getParameter("successURL");
   String successURL = "";
   // always redirect to the restricted login page (no registration link), pass successURL onto login page.
  if (paramSuccess == null ) { 
    String loginSuccessURL=request.getParameter("successURL");
     successURL = request.getContextPath() + "/html/loginFormRestricted.jsp";
     if (loginSuccessURL!=null) {
      successURL+="?successURL="+loginSuccessURL;
     }
   } else {
     successURL = paramSuccess;
   }
%>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>


<html>
<head>
<title><i18n:message key="title-authen-access-denied"/></title>

<style media="screen" type="text/css">
<!--
 font {font-family:verdana,arial,helvetica }
 body {font-family:verdana,arial,helvetica }
  .small   {font-size:10px; font-weight : }
  .medium  {font-size:12px; }
-->
</style>
</head>


<body   bgcolor='#ffffff' text='#000000' link='#6666ff' vlink='#336699' alink='#996633' marginheight='0' marginwidth='0' topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0'>











    <table border="0" cellpadding="0" cellspacing="0" width="385">
      <tr>
        <td width="350"><img src="<%= request.getContextPath() +"/images/login/left_filler_arrows.gif"%>" width="350" height="505" border="0"></td>
        <td valign="top">
          <table border="0" cellpadding="0" cellspacing="0" width="64">
            <tr>
              <td><img src="<%= request.getContextPath() +"/images/login/en_upper_area_logout.gif"%>" width="298" height="281" border="0"></td>
            </tr>
            <tr>
            <td bgcolor="#d9d9d9"><table bgcolor="#d9d9d9" width="297" cellpadding=0 cellspacing=0 border=0  >
                  <tr>
                    <td bgcolor="#FFFFFF"  rowspan=8 width="1"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="1" height="1" border="0"></td>
                    <td bgcolor="#333333" width="90"   ><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="90"   height="1" border="0"></td>
                    <td bgcolor="#333333" width="207" ><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="206" height="1" border="0"></td>
                  </tr>
                  <tr>
                    <td bgcolor="white"  colspan=2><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="1" height="1" border="0"></td>
                  </tr>
                  <tr>
                    <td colspan=2>

<center><br>
<font class="small" color="#<%=gearTextColor%>"><i18n:message key="access_sorry_message"/>
<br><br>

<dsp:form action='<%= request.getContextPath() +"/html/accessDenied.jsp"%>' method="POST">
<core:CreateUrl id="logoutSuccessURL" url='<%= request.getContextPath() +"/html/logoutForm.jsp"%>'>
 <core:UrlParam param="regmode" value="logout"/>
 <paf:encodeUrlParam param="successURL" value="<%=successURL%>"/>
 <dsp:input bean="ProfileFormHandler.logoutSuccessURL" type="HIDDEN" value="<%= logoutSuccessURL.getNewUrl() %>" />
</core:CreateUrl>
<i18n:message id="logout01" key="access-denied-login-button-label"/>
  <dsp:input type="submit" 

       bean="ProfileFormHandler.logout"  
       border="0"   
       name="Logout"
       align="middle"
	   value="<%= logout01 %>"
      /></font>
	  <font class="small">&nbsp;

	  
		  </dsp:form>
</font>
</center>

	  
                  </td>
                  </tr>
                  <tr>
                    <td bgcolor="#333333" colspan=2><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="1" height="1" border="0"></td>
                  </tr>
                  </table>
                 </td>
                </tr>

                <tr>
                  <td><img src="<%= request.getContextPath() +"/images/login/en_lower_area_logout.gif"%>" width="298" height="73" border="0"></td>
                </tr>

              </table>
            </td>
          </tr>
        </table>
</body>
</html>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/html/accessDenied.jsp#2 $$Change: 651448 $--%>
