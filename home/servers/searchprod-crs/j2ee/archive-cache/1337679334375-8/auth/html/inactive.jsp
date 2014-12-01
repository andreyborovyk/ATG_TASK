<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<dsp:page>

<i18n:bundle baseName="atg.portal.authentication.AuthenticationResources" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="loginbutton" key="login-title"/>
<html>
<head>
<title><i18n:message key="title-authen-login"/></title>
<link rel="STYLESHEET" type="text/css" href="css/default.css" src="css/default.css">

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
            <td bgcolor="#d9d9d9"><font color="#333333" class="small">

<table width="100%" cellpadding="0" cellspacing="0">
<tr>
<td bgcolor="white" width="1"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="1" height="1" border="0"></td>
<td width="100%"><center><table width="85%"><tr><td valig="top"><img src="<%= request.getContextPath() +"/images/alert_11x11.gif"%>" width="11" height="11" border="0"></td><td valign="top"><font size="-2"  class="small" color="#cc0000"><i18n:message key="community_unavailable"/></font></td></tr></table></center></td>
<td bgcolor="white" width="1"><img src="<%= request.getContextPath() +"/images/clear.gif"%>" width="1" height="1" border="0"></td>
</tr>
</table></td>
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
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/html/inactive.jsp#2 $$Change: 651448 $--%>
