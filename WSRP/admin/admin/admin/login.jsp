<%@ page import="java.io.*,java.util.*" errorPage="/error.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"  %>

<dspel:page>

<fmt:setBundle var="producerAdminbundle" basename="atg.wsrp.producer.admin.Resources" />

<%
   String clearGif = response.encodeURL(request.getContextPath()+"/images/clear.gif");
   String successURL = response.encodeURL(request.getContextPath()+"/index.jsp");
   String requestURI = request.getRequestURI();
%>
<dspel:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dspel:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler"/>
<dspel:importbean bean="/atg/wsrp/producer/admin/ProducerAdminFormHandler"/>

<html>
    <head>
        <title>
            <fmt:message key="title-admin-login" bundle="${producerAdminbundle}"/>
        </title>
        <link rel="STYLESHEET" type="text/css" href="css/default.css" src="css/default.css">
    </head>

<fmt:message var="root_image_dir" key="banner_image_root_directory" bundle="${producerAdminbundle}"/>
    <body bgcolor="#ffffff" text="#333333" link="#3366ff" vlink="#3366ff" marginheight=0
          marginwidth=0 leftmargin=0 rightmargin=0 topmargin=0  onload="document.adminForm.login.focus()"  background="images/background.gif" >

        <dspel:form action="${requestURI}" method="POST" name="adminForm">
            <table cellpadding="0" cellspacing="0" border="0" bgcolor="#0090f1" width="100%">
                <tr>
                   <c:set var="logo_atg_admin" value="${request}${root_image_dir}/wsrpadmin_banner.jpg" />
                    <td align="left" width="95%"><img src='<c:out value="${logo_atg_admin}"/>
                       ' alt='<fmt:message key="admin-nav-header-title" bundle="${producerAdminbundle}"/>'></td>
                    <td width="40" align="right"><img src='<%=response.encodeURL(request.getContextPath()+"/images/atg_logo.jpg")%>
                        ' alt='<fmt:message key="admin-nav-header-atg-logo" bundle="${producerAdminbundle}"/>'></td>
               </tr>
 <!--two rows of background image to form border between top navigation and wsrp producer admin banner -->

               <tr>
                    <td  colspan="3" bgcolor="#B7B8B7"><img src='<%=clearGif%>' height="1" width="1" alt=""></td>
               </tr>
        </table>


        <table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr><td colspan="2" bgcolor="#EAEEF0" width="100%">
                <table cellpadding="2" border="0" width="100%">
                    <tr><td NOWRAP>&nbsp;&nbsp;&nbsp;</td>
					<td align="right"><nobr></td>
					</tr></table>
            </td></tr>

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

                <font class="smaller"><b>
                    <fmt:message key="login-title" bundle="${producerAdminbundle}"/>
            </td></tr></table>
        </td></tr></table>
    </td>

<td><img src='<%=clearGif%>' height="1" width="20" ><br></td>

    <td valign="top" width="90%" align="left">

<!-- Display any errors processing form -->

                <c:if test="${ProfileFormHandler.formError == true}">
                <font class="error">
                <dspel:droplet name="ErrorMessageForEach">
                    <dspel:param name="exceptions" bean="ProfileFormHandler.formExceptions"/>
                        <dspel:oparam name="output">
                        <img src='<%=response.encodeURL(request.getContextPath()+"/images/error.gif")%>'>&nbsp;&nbsp;
                        <fmt:message key="login_failure_error_msg" bundle="${producerAdminbundle}"/>
                        <br><br>
                    </dspel:oparam>
                   </dspel:droplet>
                </font>
		            </c:if>
			            


    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
        <table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
            <font class="small"><b><font color="#FFFFFF">
                <fmt:message key="login-title_wsrp" bundle="${producerAdminbundle}"/>
        </td></tr></table>
    </td></tr></table>

    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%">
            <tr><td>
                <FONT class="smaller">
                <fmt:message key="admin-login-welcome" bundle="${producerAdminbundle}"/>
                &nbsp;</FONT>
            </td></tr>

<!-- This form should not show what the current profile attributes are so we will
     disable the ability to extract default values from the profile. -->

        <dspel:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>
             <c:url var="profileFormSuccessUrl" value="/index.jsp">
			 <c:param name="mode" value="1"/></c:url>
             <c:set var="loginFormSuccessUrlVar" value="${profileFormSuccessUrl}"/>
             <dspel:input bean="ProfileFormHandler.loginSuccessURL" type="HIDDEN" value="${loginFormSuccessUrlVar}" />
    </table>

    <img src='<%=clearGif%>' height="1" width="380"  border=0><br>
        <TABLE  BORDER="0" CELLSPACING="0" CELLPADDING="0" width="100%" bgcolor="#c9d1d7">
            <TR>
                <TD NOWRAP width="100"><FONT  class="smaller">&nbsp;
                    <fmt:message key="login-label" bundle="${producerAdminbundle}"/>
                    :&nbsp;</FONT></TD>
                <TD NOWRAP width="90%"><FONT class="smaller">
                    <dspel:input type="TEXT" name="login" size="10" maxlength="20" bean="ProfileFormHandler.value.login"/></font></TD>
            </TR>
            <TR>
                <TD NOWRAP><FONT  class="smaller">&nbsp;
                    <fmt:message key="password-label" bundle="${producerAdminbundle}"/>
                    :&nbsp;</FONT></TD>
                <TD NOWRAP><FONT class="smaller">
                    <dspel:input type="PASSWORD" name="password" size="10" maxlength="35" bean="ProfileFormHandler.value.password"/></font></TD>
            </TR>
            <TR>
              <td>&nbsp;</td>
              <td><br>
                <font class="small">
                <dspel:input type="SUBMIT" value="Login" bean="ProfileFormHandler.login"/><br><br><br></font>
        </dspel:form>

      </td></TR></TABLE>

    <td></tr></table>
  </body>
 </html>
</dspel:page>
<%-- @version $Id: //product/WSRP/version/10.0.3/admin/admin/admin/login.jsp#2 $$Change: 651448 $--%>
