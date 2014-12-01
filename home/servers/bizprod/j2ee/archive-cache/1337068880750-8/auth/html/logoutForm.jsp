
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.authentication.AuthenticationResources" localeAttribute="userLocale" changeResponseLocale="false" />

<%-- Check if the logout has been submitted --%>
<core:IfNotNull value='<%=request.getParameter("regmode")%>'>
  <core:If value='<%= request.getParameter("regmode").equals("logout") %>'>
    <core:Redirect url='<%= request.getParameter("successURL") %>'/>
  </core:If>
</core:IfNotNull>

<%
   String clearGif = response.encodeURL( request.getContextPath() +"/images/clear.gif" );
   String cssSrc   = response.encodeURL( request.getContextPath() +"/html/css/default.css");
%>
<dsp:page>
<paf:InitializeEnvironment id="pafEnv">
<html>
<head>
<title>
<i18n:message key="logout-title"/>
</title>

<link rel="STYLESHEET" type="text/css" href="<%=cssSrc%>" src="<%=cssSrc%>">
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

   String successURL = request.getParameter("successURL");   
%>

<body   bgcolor='#ffffff' text='#000000' link='#6666ff' vlink='#336699' alink='#996633' marginheight='0' marginwidth='0' topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0'>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>


<!-- This form should not show what the current profile attributes are so we will
     disable the ability to extract default values from the profile. -->
<dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>

<dsp:droplet name="Switch">
  <dsp:param bean="ProfileFormHandler.profile.transient" name="value"/>

      <%-- ************ LOGOUT FORM ************ --%>

      <dsp:oparam name="false">

    <table border="0" cellpadding="0" cellspacing="0" width="385">
      <tr>
        <td width="350"><img src='<%= response.encodeURL( request.getContextPath() +"/images/login/left_filler_arrows.gif")%>' width="350" height="505" border="0"></td>
        <td valign="top">
          <table border="0" cellpadding="0" cellspacing="0" width="64">
            <tr>
              <td><img src='<%= response.encodeURL( request.getContextPath() +"/images/login/en_upper_area_logout.gif")%>' width="298" height="281" border="0"></td>
            </tr>
            <tr>
            <td bgcolor="#d9d9d9"><table bgcolor="#d9d9d9" width="297" cellpadding=0 cellspacing=0 border=0  >
                  <tr>
                    <td bgcolor="#FFFFFF"  rowspan=8 width="1"><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
                    <td bgcolor="#333333" width="90"   ><img src="<%=clearGif%>" width="90"   height="1" border="0"></td>
                    <td bgcolor="#333333" width="207" ><img src="<%=clearGif%>" width="206" height="1" border="0"></td>
                  </tr>
                  <tr>
                    <td bgcolor="white"  colspan=2><img src="<%= clearGif%>" width="1" height="1" border="0"></td>
                  </tr>
                  <tr>
                    <td colspan=2>

  <%-- Determine whether to show name, and what name to show (firstname if there, login name if not) --%>
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


<core:CreateUrl id="formURL" url='<%= pafEnv.getLogoutURI()%>' >
<paf:encodeUrlParam param="successURL" value='<%=request.getParameter("successURL")%>'/>

 <dsp:form action='<%= formURL.getNewUrl()%>' method="POST">

      <input type="hidden" name="successURL" value='<%=request.getParameter("successURL")%>'>
      <dsp:input bean="ProfileFormHandler.logoutSuccessURL" type="HIDDEN" value="<%= successURL %>" />
<%--      <dsp:input type="hidden" bean="ProfileFormHandler.expireSessionOnLogout" value="false"/>--%>

<center><br>
<font class="smaller" color="#<%=gearTextColor%>"><i18n:message key="pre-logout-msg"/>&nbsp;<%= greetingName %>.&nbsp;<br><br><i18n:message key="post-logout-msg"/>
<br><br>
<i18n:message id="logout01" key="logout-button"/>
	      <dsp:input type="SUBMIT" value="<%= logout01 %>" bean="ProfileFormHandler.logout"/>
		  
</font>
</center>

	  
</dsp:form>
</core:CreateUrl>
                  </td>
                  </tr>
                  <tr>
                    <td bgcolor="#333333" colspan=2><img src="<%=clearGif%>" width="1" height="1" border="0"></td>
                  </tr>
                  </table>
                 </td>
                </tr>

                <tr>
                  <td><img src='<%= response.encodeURL( request.getContextPath() +"/images/login/en_lower_area_logout.gif")%>' width="298" height="73" border="0"></td>
                </tr>

              </table>
            </td>
          </tr>
        </table>

    
      </dsp:oparam>

      <dsp:oparam name="default">
         <core:CreateUrl id="logoutSuccessURL" url='<%= pafEnv.getLoginURI() %>'>
             <core:UrlParam param="regmode" value="logout"/>
             <paf:encodeUrlParam param="successURL" value='<%=request.getParameter("successURL")%>'/>
             <core:Redirect url="<%= logoutSuccessURL.getNewUrl() %>"/>
         </core:CreateUrl>
      </dsp:oparam>

</dsp:droplet>

</body>
</paf:InitializeEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/html/logoutForm.jsp#2 $$Change: 651448 $--%>
