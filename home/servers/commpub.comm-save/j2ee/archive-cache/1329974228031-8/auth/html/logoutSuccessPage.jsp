<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<dsp:page>

<i18n:bundle baseName="atg.portal.authentication.<dsp:page>" localeAttribute="userLocale" changeResponseLocale="false" />

<%--  logoutSuccessPage.jsp:  This page was created as the default page to render after a user
      			      logs out.  This page can be replaced, and is referred to in the
			      logoutForm.jsp page as the "successURL".
--%>



<html>
<head>
<title>
<i18n:message key="logout-title"/>
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
   // always redirect to login page, pass successURL onto login page.
   String loginSuccessURL=request.getParameter("successURL");
   
%>

<body   bgcolor='#ffffff' text='#000000' link='#6666ff' vlink='#336699' alink='#996633' marginheight='0' marginwidth='0' topmargin='0' bottommargin='0' leftmargin='0' rightmargin='0'>



      <%-- ************ LOGOUT SUCCESS MESSAGE ************ --%>

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

<%-- show message, and link to log back in --%>
<core:CreateUrl id="loginURL" url='<%= request.getContextPath() +"/html/loginForm.jsp"%>'>
  <paf:encodeUrlParam param="successURL" value="<%=loginSuccessURL%>"/>
<center><br><br>
<font class="smaller" color="#<%=gearTextColor%>"><i18n:message key="logoutSuccessMsg"/><br><br><br>
<i18n:message key="logBackInPreMsg"/>&nbsp;<a href="<%=loginURL.getNewUrl()%>"><i18n:message key="logBackInLink"/></a>&nbsp;?
<br><br>
<br><br>
</font>
</center>
</core:CreateUrl>

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

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/html/logoutSuccessPage.jsp#2 $$Change: 651448 $--%>
