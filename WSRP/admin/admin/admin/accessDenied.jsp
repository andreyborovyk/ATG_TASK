<%@ page import="java.io.*,java.util.*" errorPage="/error.jsp"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"  %>

<dspel:page>

<fmt:setBundle var="producerAdminbundle" basename="atg.wsrp.producer.admin.Resources" />

<dspel:importbean bean="/atg/userprofiling/Profile"/>
<dspel:importbean bean="/atg/userprofiling/ProfileFormHandler"/>

<%
   String clearGif =  response.encodeURL("images/clear.gif");
%>

<html>
  <head>
    <title>
        <fmt:message key="title-authen-access-denied" bundle="${producerAdminbundle}"/>
    </title>
    <link rel="stylesheet" type="text/css"  href="css/default.css" src="css/default.css" />
  </head>

<body bgcolor="#FFFFFF" background="images/background.gif" text="#333333" link="#3366ff" vlink="#3366ff" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" marginwidth="0" marginheight="0">

  <fmt:message var="root_image_dir" key="banner_image_root_directory" bundle="${producerAdminbundle}"/>
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
        <td  colspan="2" bgcolor="#B7B8B7"><img src='<%=clearGif%>' height="1" width="1" alt=""></td>
    </tr>
  </table>

    <table border="0" cellpadding="0" cellspacing="0" width="100%">

        <tr>
            <td colspan="2" bgcolor="#EAEEF0" width="100%"><table cellpadding="2" border="0" width="100%">
        <tr>
            <td NOWRAP>&nbsp;&nbsp;&nbsp;</td>
            <td align="right"><font class="smaller">
                  
                        <dspel:getvalueof id="login" idtype="java.lang.String" bean="Profile.login">

              <fmt:message var="i18n_bold" key="admin-nav-header-bold" bundle="${producerAdminbundle}"/>
              <fmt:message var="i18n_end_bold" key="admin-nav-header-endbold" bundle="${producerAdminbundle}"/>
              <fmt:message key="admin-nav-header-logged-comp" bundle="${producerAdminbundle}">
                <fmt:param value="${login}"/>
                <fmt:param value="${i18n_bold}"/>
                <fmt:param value="${i18n_end_bold}"/>
              </fmt:message>
			  </dspel:getvalueof>
            </font>
          </td>
        </tr>
      </table></td>
  </tr>

<!--two rows of background image to form border between top navigation and wsrp producer admin banner -->
        <tr>
            <td  colspan="2" bgcolor="#B7B8B7"><img src='<%=clearGif%>' height="1" width="1" alt=""></td>
        </tr>
        <tr>
            <td colspan="2" bgcolor="#677886"><img src='<%=clearGif%>' height="1" width="1" alt=""></td>
        </tr>
    </table>


    <blockquote>
        <h3>
            <fmt:message key="access_denied" bundle="${producerAdminbundle}"/>
        </h3>
           <font size="-1">
                <p>
                    <fmt:message key="access_sorry_message" bundle="${producerAdminbundle}"/>
                </p>
		   </font>
         <dspel:form action="/WSRP/admin/accessDenied.jsp" method="POST">
                <c:url var="acessDeniedURL" value="/index.jsp"/>
                <c:set var="noAccessUrl" value="${acessDeniedURL}"/>
                <dspel:input bean="ProfileFormHandler.logoutSuccessURL" type="HIDDEN" value="${noAccessUrl}"/>
                    <dspel:input type="SUBMIT" value="Login" bean="ProfileFormHandler.logout"/>
        </dspel:form>
    </blockquote>
  </body>
</html>
</dspel:page>
<%-- @version $Id: //product/WSRP/version/10.0.3/admin/admin/admin/accessDenied.jsp#2 $$Change: 651448 $--%>
