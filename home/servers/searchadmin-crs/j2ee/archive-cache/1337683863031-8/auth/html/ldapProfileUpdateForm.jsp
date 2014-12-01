<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<dsp:page>

<i18n:bundle baseName="atg.portal.authentication.AuthenticationResources" localeAttribute="userLocale" changeResponseLocale="false" />

<html>
<head>
<title>
<i18n:message key="profile-update-title"/>
</title>

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
     <td colspan="2" align="center"><font class="large_bold" color="#<%=titleTextColor%>"><i18n:message key="profile-update-title"/></font></td>
  </tr>

  <tr bgcolor="#33333">
     <td colspan="2"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="1" width="1" border="0"></td>
  </tr>
</table>

<table width=100% border="0" cellspacing="0" cellpadding="0">

<dsp:form action="<%= request.getRequestURI() %>" method="POST">

<dsp:input bean="ProfileFormHandler.updateSuccessURL" type="HIDDEN" value="<%=successURL%>" />
<input type="hidden" name="successURL" value="<%=successURL%>" >
	<input type="hidden" name="col_pb" value="<%=pageBGColor%>" >
	<input type="hidden" name="col_gtt" value="<%=titleTextColor%>" >
	<input type="hidden" name="col_gtb" value="<%=titleBGColor%>" >
	<input type="hidden" name="col_gb" value="<%=gearBGColor%>" >
	<input type="hidden" name="col_gt" value="<%=gearTextColor%>" >


 <tr>
    <td width="30%"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="1" width="190" border="0"></td>
    <td width="30%"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="1" width="100" border="0"></td>
    <td width="40%"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="1" width="100" border="0"></td>
  </tr>

    
 <dsp:droplet name="Switch">
  <dsp:param name="value" bean="ProfileFormHandler.formError"/>
  <dsp:oparam name="true">

  <tr bgcolor="#<%= gearBGColor %>">
	<td colspan=3 align=left>    <blockquote><br>
  		<font class="error"><STRONG><UL>
    		<dsp:droplet name="ProfileErrorMessageForEach">
      			<dsp:param name="exceptions" bean="ProfileFormHandler.formExceptions"/>
      			<dsp:oparam name="output">
     			<LI> <dsp:valueof param="message"/>
      			</dsp:oparam>
    		</dsp:droplet>
    		</UL></STRONG></font></blockquote>
	
        </td>
  </tr>
  </dsp:oparam>
 </dsp:droplet>
  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="5" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td colspan="2" align="center"><font class="medium_bold" color="#<%=gearTextColor%>"><b><i18n:message key="basic-info-header"/></font></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="username-label"/>&nbsp;&nbsp;</font></td>
    <td><font class="small" color="#<%=gearTextColor%>">&nbsp;<b><dsp:valueof bean="ProfileFormHandler.value.login"/></b></font></td>
     <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="email-label"/>*&nbsp;&nbsp;</font></td>
    <td><font class="small" color="#<%=gearTextColor%>">&nbsp;<b><dsp:valueof bean="ProfileFormHandler.value.email" /></b></font></td>
     <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="5" width="1" border="0"></td>
  </tr>
  <%-- ***********   Contact Info  ******************  --%>
  <tr>
    <td colspan="3" bgcolor="#<%= titleBGColor %>" ><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="1" width="1" border="0"></td>
  </tr>
  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="5" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td colspan="2" align="center"><font class="medium_bold" color="#<%=gearTextColor%>"><i18n:message key="contact-info-header"/></font></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="fname-label"/>&nbsp;&nbsp;</font></td>
    <td><font class="small" color="#<%=gearTextColor%>">&nbsp;<b><dsp:valueof bean="ProfileFormHandler.value.firstName"/></b></font></td>
     <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>"  height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="lname-label"/>&nbsp;&nbsp;</font></td>
    <td><font class="small" color="#<%=gearTextColor%>">&nbsp;<b><dsp:valueof  bean="ProfileFormHandler.value.lastName"/></b></font></td>
     <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="addr1-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="24" bean="ProfileFormHandler.value.homeAddress.address1"/></td>
     <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="addr2-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="24" bean="ProfileFormHandler.value.homeAddress.address2"/></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="city-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="24" bean="ProfileFormHandler.value.homeAddress.city"/></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="state-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="24" bean="ProfileFormHandler.value.homeAddress.state"/></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="postal-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="24" bean="ProfileFormHandler.value.homeAddress.postalCode"/></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="country-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="24" bean="ProfileFormHandler.value.homeAddress.country"/></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="phone-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="24" bean="ProfileFormHandler.value.homeAddress.phoneNumber"/></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="2" width="1" border="0"></td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
    <td valign=middle align=right><font class="small" color="#<%=gearTextColor%>"><i18n:message key="fax-label"/>&nbsp;&nbsp;</font></td>
    <td>  <dsp:input type="TEXT" size="24" bean="ProfileFormHandler.value.homeAddress.faxNumber"/></td>
    <td>&nbsp;</td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="2" width="1" border="0"></td>
  </tr>

<%--  **************** Submit Buttons *****************  --%>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="5" width="1" border="0"></td>
  </tr>
  <tr>
    <td colspan="3" bgcolor="#<%= titleBGColor %>" ><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="1" width="1" border="0"></td>
  </tr>
  <tr bgcolor="#<%= gearBGColor %>">
    <td>&nbsp;</td>
    <td colspan=2 align="left"><br>
<table><tr><td>


<i18n:message id="save01" key="save-button"/>
 <dsp:input type="Submit" value="<%= save01 %>" bean="ProfileFormHandler.update"/>
</td><td>
<i18n:message id="reset02" key="reset-button"/>
    <input type="Reset" value="<%= reset02 %>">
</td>
</dsp:form>
<core:CreateUrl id="cancelUrl" url="<%= successURL %>">
  <form action="<%=cancelUrl.getNewUrl()%>">
</core:CreateUrl>
<td>
<i18n:message id="cancel03" key="cancel-button"/>
<input type="submit" value="<%= cancel03 %>">
</td></tr></table>
</form> 
   </td>
  </tr>

  <tr bgcolor="#<%= gearBGColor %>">
     <td colspan="3"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" height="5" width="1" border="0"></td>
  </tr>

</table>
</center>


</td></tr></table></td></tr></table>
</blockquote>
</body>
</html>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/html/ldapProfileUpdateForm.jsp#2 $$Change: 651448 $--%>
