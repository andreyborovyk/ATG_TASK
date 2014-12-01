<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.authentication.AuthenticationResources" localeAttribute="userLocale" changeResponseLocale="false" />

<html>
<head>
<title>
  <i18n:message key="registration-title"/>
</title>
<link rel="STYLESHEET" type="text/css" href="css/default.css" src="css/default.css">
</head>



<dsp:page>

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
   
   String cancelURL=request.getParameter("communityURL");
   if (cancelURL==null) {
      cancelURL = successURL;
   }
   
   String clearGif =  request.getContextPath() + response.encodeURL("/images/clear.gif");
%>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<body   bgcolor='#<%=pageBGColor%>' text='#000000' link='#6666ff' vlink='#336699' alink='#996633' marginheight='0' marginwidth='0' topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0'>
<br><br>
<blockquote>
<table border="0" cellpadding="1" cellspacing="0"  bgcolor="#333333"><tr><td>
<table border="0" cellpadding="0" cellspacing="0" width="100%" bgcolor="#<%=gearBGColor%>"><tr><td valign="top">

<table width=100% border="0" cellspacing="0" cellpadding="0">

  <tr bgcolor="#<%= titleBGColor %>">
     <td colspan="2" align="center"><font class="large_bold" color="#<%=titleTextColor%>"><i18n:message key="registration-title"/></font></td>
  </tr>

  <tr bgcolor="#33333">
     <td colspan="2"><img src="<%= clearGif%>" height="1" width="1" border="0"></td>
  </tr>
</table>
<!-- This form should not show what the current profile attributes are so we will
     disable the ability to extract default values from the profile. -->
<dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>

<dsp:form action="<%= request.getRequestURI() %>" method="POST">
<core:urlParamValue id="communityURL" param="communityURL">
  <core:CreateUrl id="fullURL" url='<%= successURL%>'>
     <paf:encodeUrlParam param="communityURL" value='<%=communityURL%>'/>
     <dsp:input bean="ProfileFormHandler.createSuccessURL" type="HIDDEN" value="<%=fullURL.getNewUrl()%>" />
  </core:CreateUrl>
  <dsp:input bean="ProfileFormHandler.cancelURL" type="HIDDEN" value="<%=communityURL%>" />
<%
 String registrationURL = communityURL.toString();
 int indx = communityURL.toString().length();
 indx = communityURL.toString().indexOf(";");
 if(indx > 0)
  registrationURL = communityURL.toString().substring(0,indx);
%>
  <dsp:input bean="ProfileFormHandler.value.registrationURL" type="HIDDEN" value="<%=registrationURL%>" />
</core:urlParamValue>
<dsp:input bean="ProfileFormHandler.confirmPassword"  type="hidden" value="true" />

  <input type="hidden" name="successURL" value="<%=successURL%>" >
  <input type="hidden" name="communityURL" value="<%=cancelURL%>" >
	<input type="hidden" name="col_pb" value="<%=pageBGColor%>" >
	<input type="hidden" name="col_gtt" value="<%=titleTextColor%>" >
	<input type="hidden" name="col_gtb" value="<%=titleBGColor%>" >
	<input type="hidden" name="col_gb" value="<%=gearBGColor%>" >
	<input type="hidden" name="col_gt" value="<%=gearTextColor%>" >

<center>
<table width=100% border="0" cellspacing="0" cellpadding="0">

<%-- establish columns --%>
  <tr>
    <td width="30%"><img src="<%=clearGif%>" height="1" width="190" border="0"></td>
    <td width="30%"><img src="<%=clearGif%>" height="1" width="100" border="0"></td>
    <td width="40%"><img src="<%=clearGif%>" height="1" width="100" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
	<td colspan=3 align=left>        

	<dsp:droplet name="Switch">
	<dsp:param name="value" bean="ProfileFormHandler.formError"/>
	<dsp:oparam name="true"><br><blockquote>
  		<font class="error"><STRONG><UL>
    		<dsp:droplet name="ProfileErrorMessageForEach">
      			<dsp:param name="exceptions" bean="ProfileFormHandler.formExceptions"/>
      			<dsp:oparam name="output">
     			<LI> <dsp:valueof param="message"/>
      			</dsp:oparam>
    		</dsp:droplet>
    		</UL></STRONG></font></blockquote>
	</dsp:oparam>
	</dsp:droplet>

        </td>

  </tr>


  <tr bgcolor="#<%= gearBGColor %>">
    <td colspan="2" align="center"><font class="medium_bold" color="#<%=gearTextColor%>"><i18n:message key="basic-info-header"/></font></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
  </tr>
	
  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="username-label"/>*&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="24" maxlength="20" bean="ProfileFormHandler.value.login"  required="<%=true%>" /></td>
     <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"> <i18n:message key="password-label"/>*&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="PASSWORD"  size="24" maxlength="35" bean="ProfileFormHandler.value.password"  required="<%=true%>"  /></td>
     <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td nowrap valign=middle align=right><font class="small" color="#<%=gearTextColor%>">&nbsp;&nbsp;<i18n:message key="password-conf-label"/>*&nbsp;&nbsp;</font></td>
    <td><dsp:input type="PASSWORD" size="24" maxlength="35" bean="ProfileFormHandler.value.confirmpassword"  required="<%=true%>" /></td>
     <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td nowrap valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="email-label"/>*&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT"  size="24" maxlength="30" bean="ProfileFormHandler.value.email" required="<%=true%>" /></td>
     <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="5" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="2" align="center"><font class="smaller" color="<%=gearTextColor%>"><i18n:message key="required-field-legend"/></font></td>
     <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="5" width="1" border="0"></td>
  </tr>

  <%-- ***********   Contact Info  ******************  --%>

  <tr>
    <td colspan="3" bgcolor="#<%= titleBGColor %>" ><img src="<%=clearGif%>"  height="1" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="5" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td colspan="2" align="center"><font class="medium_bold" color="#<%=gearTextColor%>"><i18n:message key="contact-info-header"/></font></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="fname-label"/>*&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT"  size="24" maxlength="40" bean="ProfileFormHandler.value.firstName" required="<%=true%>"/></td>
     <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="lname-label"/>*&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="24" maxlength="40" bean="ProfileFormHandler.value.lastName" required="<%=true%>"/></td>
     <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="addr1-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="24" maxlength="30" bean="ProfileFormHandler.value.homeAddress.address1"/></td>
     <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="addr2-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="24" maxlength="30" bean="ProfileFormHandler.value.homeAddress.address2"/></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="city-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="24" maxlength="30" bean="ProfileFormHandler.value.homeAddress.city"/></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="state-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="24" maxlength="20" bean="ProfileFormHandler.value.homeAddress.state"/></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="postal-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="10" maxlength="12" bean="ProfileFormHandler.value.homeAddress.postalCode"/></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="country-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="24" maxlength="40" bean="ProfileFormHandler.value.homeAddress.country"/></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="phone-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="15" maxlength="15" bean="ProfileFormHandler.value.homeAddress.phoneNumber"/></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="fax-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="15" maxlength="15" bean="ProfileFormHandler.value.homeAddress.faxNumber"/></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
  </tr>

<%--  **************** Submit Buttons *****************  --%>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>"  height="5" width="1" border="0"></td>
  </tr>
  <tr>
    <td colspan="3" bgcolor="#<%= titleBGColor %>" ><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="1" width="1" border="0"></td>
  </tr>
  <tr bgcolor="#<%= gearBGColor %>">
    <td>&nbsp;</td>
    <td colspan=2 align="left"><br>

<table><tr><td>



<i18n:message id="register01" key="register-button"/>
    <dsp:input type="Submit" value="<%= register01 %>" bean="ProfileFormHandler.create"/>
</td><td>
<i18n:message id="reset02" key="reset-button"/>
    <input type="reset" value="<%= reset02 %>">
</td>
</dsp:form>
<form action="<%=cancelURL%>">
<input type="hidden" name="successURL" value="<%=successURL%>">
<td>
<i18n:message id="cancel03" key="cancel-button"/>
<input type="submit" value="<%= cancel03 %>">
</td></tr></table>
</form> 

   </td>
  </tr>
  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%=clearGif%>" height="5" width="1" border="0"></td>
  </tr>

</table>
</center>

</td></tr></table></td></tr></table>
</blockquote>
</body>
</html>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/html/registrationForm.jsp#2 $$Change: 651448 $--%>
