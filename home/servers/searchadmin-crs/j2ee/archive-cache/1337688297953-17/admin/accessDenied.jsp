<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<paf:setFrameworkLocale />

<dsp:page>

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false"/>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>

<%
  Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
  Style style = null;
  String cssURL = "";
  if(cm != null) {
   style = cm.getStyle();
   if(style != null)
     cssURL = style.getCSSURL();
  }
  
  Page pg = (Page)request.getAttribute(Attribute.PAGE);
  ColorPalette colorPalette = null;
  String bodyTagData = "";
  if(pg != null) {
    colorPalette = pg.getColorPalette();
    if(colorPalette != null)
      bodyTagData = colorPalette.getBodyTagData();
  }

   String clearGif =  response.encodeURL("images/clear.gif");
   String greetingName = "";

%>
<html>
  <head>
    <title><i18n:message key="title-authen-access-denied"/></title>
    <link rel="stylesheet" type="text/css"  href="css/default.css" src="css/default.css" />
  </head>

<body bgcolor="#FFFFFF" background="images/background.gif" text="#333333" link="#3366ff" vlink="#3366ff" topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" marginwidth="0" marginheight="0">

<table cellpadding="0" cellspacing="0" border="0" bgcolor="#0090f1" width="100%">
  <tr>

<i18n:message id="root_image_dir" key="banner_image_root_directory"/>

   <td align="left" width="95%"><img src='<%= response.encodeUrl(request.getContextPath()+"/"+root_image_dir+"/portaladmin_banner.jpg")%>' alt='<i18n:message key="admin-nav-header-portaladmin-banner"/>'></td>
   <td width="40" align="right"><img src='<%= response.encodeUrl(request.getContextPath()+"/images/atg_logo.jpg")%>' alt='<i18n:message key="admin-nav-header-atg-logo"/>'></td>
  </tr>
  <!--two rows of background image to form border between top navigation and portal admin banner -->
  <tr>
    <td  colspan="2" bgcolor="#B7B8B7"><img src='<%=clearGif%>' height="1" width="1" alt=""></td>
  </tr>
</table>

<table border="0" cellpadding="0" cellspacing="0" width="100%">

  <tr>
    <td colspan="2" bgcolor="#EAEEF0" width="100%"><table cellpadding="2" border="0" width="100%">
        <tr>
          <td NOWRAP><font class="smaller">&nbsp;&nbsp;&nbsp;

            </font></td>
          <td align="right"><font class="smaller">

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

              <i18n:message id="i18n_bold"     key="admin-nav-header-bold"/>
              <i18n:message id="i18n_end_bold" key="admin-nav-header-endbold"/>
              <i18n:message key="admin-nav-header-logged-comp"> 
                <i18n:messageArg value="<%= greetingName%>"/>
                <i18n:messageArg value="<%=i18n_bold%>"/>
                <i18n:messageArg value="<%=i18n_end_bold%>"/>
              </i18n:message>

            </font>
          </td>
        </tr>
      </table></td>
  </tr>

<!--two rows of background image to form border between top navigation and portal admin banner -->
  <tr>
    <td  colspan="2" bgcolor="#B7B8B7"><img src='<%=clearGif%>' height="1" width="1" alt=""></td>
  </tr>
  <tr>
    <td colspan="2" bgcolor="#677886"><img src='<%=clearGif%>' height="1" width="1" alt=""></td>
  </tr>	
</table>


<blockquote>
    <h3><i18n:message key="access_denied"/></h3>
<font size="-1">
    <p><i18n:message key="access_sorry_message"/></p>
    <dsp:form action='<%= request.getContextPath() + "/accessDenied.jsp"  %>' method="POST">
    <core:CreateUrl id="logoutSuccessURL" url='<%= request.getContextPath() +"/index.jsp"%>'>
      <dsp:input bean="ProfileFormHandler.logoutSuccessURL" type="HIDDEN" value="<%= logoutSuccessURL.getNewUrl() %>" />
      <i18n:message id="login01" key="access-denied-login-button-label"/>
        <dsp:input type="SUBMIT" value="<%= login01 %>" bean="ProfileFormHandler.logout"/>
    </core:CreateUrl>
    </dsp:form>
</font>
</blockquote>
  </body>
</html>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/accessDenied.jsp#2 $$Change: 651448 $--%>
