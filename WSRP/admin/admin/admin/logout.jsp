<%@page import="java.io.*,java.util.*,atg.servlet.ServletUtil" errorPage="/error.jsp" %>

<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"  %>

<dspel:page>
<fmt:setBundle var="producerAdminbundle" basename="atg.wsrp.producer.admin.Resources" />

<dspel:importbean bean="/atg/userprofiling/Profile"/>
<dspel:importbean bean="/atg/userprofiling/ProfileFormHandler"/>

<fmt:message var="imageroot" key="i18n_imageroot" bundle="${producerAdminbundle}"/>

<html>
    <head>
        <title>
            <fmt:message key="title-admin-logout" bundle="${producerAdminbundle}"/>
        </title>
        <link rel="STYLESHEET" type="text/css" href='<%= response.encodeURL("css/default.css") %>' src='<%= response.encodeURL("css/default.css") %>'>
    </head>

<%
    String successURL= request.getRequestURI();
    String clearGif =  response.encodeURL("images/clear.gif");
%>

    <body bgcolor="#FFFFFF" background="images/background.gif" text="#333333" link="#3366ff"
          vlink="#3366ff" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" marginwidth="0" marginheight="0">

            <fmt:message var="root_image_dir" key="banner_image_root_directory" bundle="${producerAdminbundle}"/>

                <table cellpadding="0" cellspacing="0" border="0" bgcolor="#0090f1" width="100%">
                    <tr>
                            <td align="left" width="95%"><img src='<%=response.encodeURL(request.getContextPath()+"/images/wsrpadmin_banner.jpg")%>
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
                    <td colspan="2" bgcolor="#EAEEF0" width="100%">
                        <table cellpadding="2" border="0" width="100%">
                            <tr>
                                <td NOWRAP>
                                <font class="smaller">&nbsp;&nbsp;&nbsp;
                                </font>
                                </td>

                                <td align="right"></td>

                            </tr>
                        </table>
                </td></tr>

                <tr>
                    <td  colspan="2" bgcolor="#B7B8B7"><img src='<%=clearGif%>' height="1" width="1" alt=""></td>
                </tr>
                <tr>
                    <td colspan="2" bgcolor="#677886"><img src='<%=clearGif%>' height="1" width="1" alt=""></td>
                </tr>
            </table>

    <table width="98%" border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td valign="top" width="20"><img src='<%=clearGif %>' height="1" width="20" ></td>

            <td width="150" valign="top"><font class="smaller">
                <td width="20"><img src='<%=clearGif %>' height="1" width="20" ></td>
                <td valign="top" width="90%" align="left">

                    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
	                    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
	                        <font class="pageheader">
                                <fmt:message key="admin-body-header-title" bundle="${producerAdminbundle}"/>
	                    </td></tr></table>
	                </td></tr></table>
	
        	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>

             <dspel:getvalueof id="login" idtype="java.lang.String" bean="Profile.login">
      <font class="smaller">&nbsp;
              <fmt:message key="admin-logout-message" bundle="${producerAdminbundle}">
					<fmt:param value="${login}"/>
              </fmt:message>
			  </dspel:getvalueof>
      </font>

        <dspel:form action="${successURL}" method="POST">
            <dspel:input bean="ProfileFormHandler.logoutSuccessURL" type="HIDDEN" value="${successURL}" />
                <br>&nbsp;<br>
            <dspel:input type="SUBMIT" value="Logout" bean="ProfileFormHandler.logout"/>
        </dspel:form>

      </td></tr></table>

     </td></tr></table>
   </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/WSRP/version/10.0.3/admin/admin/admin/logout.jsp#2 $$Change: 651448 $--%>
