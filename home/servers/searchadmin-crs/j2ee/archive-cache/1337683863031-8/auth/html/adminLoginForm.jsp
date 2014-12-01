<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<dsp:page>

<i18n:bundle baseName="atg.portal.authentication.AuthenticationResources" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="loginbutton" key="login-title"/>

<%
   String clearGif = response.encodeURL("../images/clear.gif");
   String userMessage = request.getParameter("userMessage");
   if ( userMessage == null) {
        userMessage   = "";
   }
   String greetingName="";

   String successURL=request.getParameter("successURL");
   if (successURL==null) {
      successURL = request.getContextPath() + "/html/adminLoginForm.jsp";
   }
   
%>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

<html>
<head>
<title><i18n:message key="admin-login-title"/></title> 
<link rel="STYLESHEET" type="text/css" href="css/default.css" src="css/default.css">
</head>



<body bgcolor="#ffffff" text="#333333" link="#3366ff" vlink="#3366ff" marginheight=0 marginwidth=0 leftmargin=0 rightmargin=0 topmargin=0  onload="document.adminForm.login.focus()" >
<dsp:form action='<%= request.getContextPath() + "/html/adminLoginForm.jsp" %>' method="POST" name="adminForm">
<table cellpadding="0" cellspacing="0" border="0" bgcolor="#0090f1" width="100%">
  <tr>
   <td align="left" width="95%"><img src='<%=response.encodeURL("../images/portaladmin_banner.jpg")%>' alt='<i18n:message key="admin-nav-header-title"/>'></td>
   <td width="40" align="right"><img src='<%=response.encodeURL("../images/atg_logo.jpg")%>' alt='ATG Logo'></td>
   <td width="1">&nbsp;</td>
 </tr>
 <!--two rows of background image to form border between top navigation and portal admin banner -->
 <tr>
  <td  colspan="3" bgcolor="#B7B8B7"><img src='<%=response.encodeURL("../images/clear.gif")%>' height="1" width="1" alt=""></td>
 </tr>
</table>


<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr><td colspan="2" bgcolor="#EAEEF0" width="100%"><table cellpadding="2" border="0" width="100%"><tr><td NOWRAP><font class="smaller">
&nbsp;&nbsp;&nbsp;

</font></td>

<td align="right">
<font class="smaller"><nobr>

</td>

</tr></table></td></tr>

<!--two rows of background image to form border between top navigation and portal admin banner -->
<tr>
<td  colspan="2" bgcolor="#B7B8B7"><img src='<%=clearGif%>' height="1" width="1" alt=""><br></td>
</tr>
<tr>
<td  colspan="2" bgcolor="#677886"><img src='<%=clearGif%>' height="1" width="1" alt=""></td>
</tr>	
</table>







<br>
<table width="98%" border="0" cellpadding="0" cellspacing="0">
<tr>
<td valign="top" width="20"><img src='<%=clearGif %>' height="1" width="20" ></td>

<td width="150" valign="top"><font class="smaller">


  <TABLE BORDER="0" CELLPADDING="0" CELLSPACING="0"><TR><TD BGCOLOR="#B7B8B7">
<TABLE WIDTH="150" BORDER="0" CELLPADDING="4" CELLSPACING="1">
<TR><TD ALIGN=left BGCOLOR=#CBD1D7>

 <font class="smaller"><b><i18n:message key="login-title"/>

 </td></tr></table>
 </td></tr></table>
</td>

<td><img src='<%=response.encodeURL("../images/clear.gif")%>' height="1" width="20" ><br></td>


<td valign="top" width="90%" align="left">

<!-- Display any errors processing form -->
<dsp:droplet name="Switch">
<dsp:param name="value" bean="ProfileFormHandler.formError"/>
<dsp:oparam name="true">
  <font class="error">
    <dsp:droplet name="ErrorMessageForEach">
      <dsp:param name="exceptions" bean="ProfileFormHandler.formExceptions"/>
      <dsp:oparam name="output">
       <img src='<%=response.encodeURL("../images/error.gif")%>'>&nbsp;&nbsp;<i18n:message key="login_failure_error_msg"/><br>
      </dsp:oparam>
    </dsp:droplet>
    </font>
</dsp:oparam>
</dsp:droplet>


    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
	<font class="small"><b><font color="#FFFFFF"><i18n:message key="login-title"/>
	</td></tr></table>
	</td></tr></table>
	
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
	<FONT class="smaller"><i18n:message key="admin-login-welcome"/></FONT>
	</td></tr></table>


<!-- This form should not show what the current profile attributes are so we will
     disable the ability to extract default values from the profile. -->
<dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>


<dsp:input bean="ProfileFormHandler.loginSuccessURL" type="HIDDEN" value="<%= successURL %>" />
<input type="hidden" name="successURL" value="<%=successURL%>" >


<img src='<%=response.encodeURL("../images/clear.gif")%>' height="1" width="1" ><br>
<TABLE  BORDER="0" CELLSPACING="0" CELLPADDING="0" width="100%" bgcolor="#c9d1d7">
<TR>
<TD NOWRAP width="100"><FONT  class="smaller">&nbsp;<i18n:message key="login-label"/>:&nbsp;</FONT></TD>
<TD NOWRAP width="90%"><FONT class="smaller">
<dsp:input type="TEXT" name="login" size="10" maxlength="20" bean="ProfileFormHandler.value.login"/></font></td>
</TR>
<TR>
<TD NOWRAP><FONT  class="smaller">&nbsp;<i18n:message key="password-label"/>:&nbsp;</FONT></TD>
<TD NOWRAP><FONT class="smaller">
<dsp:input type="PASSWORD" name="password" size="10" maxlength="35" bean="ProfileFormHandler.value.password"/></font></TD>
</TR>
<TR>
<td>&nbsp;</td>
<td><br>
<font class="small">
<dsp:input type="SUBMIT" value="<%= loginbutton %>" bean="ProfileFormHandler.login"/></font>
</td>
</TR>
</TABLE>



</dsp:form>
<td></tr></table>
</body>
</html>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/html/adminLoginForm.jsp#2 $$Change: 651448 $--%>
