<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%@ page import="java.io.*,java.util.*,atg.portal.framework.*" %>

<paf:setFrameworkLocale />

<dsp:page>

<i18n:bundle baseName="atg.portal.templates.DefaultTemplateResources" localeAttribute="userLocale" changeResponseLocale="false" />

<core:demarcateTransaction id="headerMainPageXA">

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<paf:InitializeEnvironment id="pafEnv">

<%
  atg.portal.framework.Page currPage=pafEnv.getPage();
  String contextPath = request.getContextPath() ;
  String gearTitleBackgroundColor = currPage.getColorPalette().getGearTitleBackgroundColor() ;
  String gearTitleTextColor =  currPage.getColorPalette().getGearTitleTextColor();
  String gearBackgroundColor = currPage.getColorPalette().getGearBackgroundColor() ;
  String pageBackgroundColor = currPage.getColorPalette().getPageBackgroundColor() ;
  String pageTextColor = currPage.getColorPalette().getPageTextColor();
  String pageLinkColor = currPage.getColorPalette().getPageLinkColor();
  String gearTextColor = currPage.getColorPalette().getGearTextColor() ;

  boolean isLoggedIn=false;
  String greetingName=null;
  
  String displayMode=pafEnv.getDisplayMode();
%>

<dsp:getvalueof id="currentUser" bean="Profile">
  <% isLoggedIn=pafEnv.isRegisteredUser();
     
     if (isLoggedIn) {
       greetingName=(String)((atg.userprofiling.Profile)currentUser).getPropertyValue("firstName");
       if (greetingName==null) {
         greetingName=(String)((atg.userprofiling.Profile)currentUser).getPropertyValue("login");
       }
     }
  %>
</dsp:getvalueof>

<%--  BRANDING --%>

<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr>
    <td bgcolor="#<%= gearTitleBackgroundColor%>"
        background='<%=contextPath + "/images/bg_table_trans_03.gif"%>'><font color="#<%= gearTitleTextColor %>" class="large"><b><img align="left" src="/portal/layoutTemplates/images/clear.gif" width="1" height="61" border="0">
      <%= pafEnv.getCommunity().getName() %><i18n:message key="header_commname_pagename_seperator"/><font class="small" color="#<%= gearTitleTextColor %>" ><%= currPage.getName() %></font></b>
      </font></td>
  </tr>
  <tr>
    <td bgcolor="#666666"><img src="/portal/layoutTemplates/images/clear.gif" height="1" width="1" border="0"></td>
  </tr>
</table>

<%--  END BRANDING --%>


<core:IfNot value='<%= displayMode.equals("full")%>'>

<core:exclusiveIf>
  <core:if value="<%= isLoggedIn%>">
    <%@ include file="header_links_logged_in.jspf" %>
  </core:if>
  <core:defaultCase>
     <%@ include file="header_links_logged_out.jspf" %>
  </core:defaultCase>
</core:exclusiveIf>
  
             
</core:IfNot><%-- if not equal full page --%>

<core:If value='<%= displayMode.equals("full")%>'>
  <core:CreateUrl id="fullChildPageUrl" url="<%= pafEnv.getPageURI(currPage) %>">
    &nbsp;&nbsp;<a href="<%=fullChildPageUrl.getNewUrl()%>" style="text-decoration:none"><font class="small" color="#<%= pageLinkColor%>"><b><i18n:message key="back-arrow"/></b>&nbsp;<%= currPage.getName() %>
</font></a>
  </core:CreateUrl>
</core:If>

<br>
<!-- END HEADER_MAIN -->
</paf:InitializeEnvironment>

</core:demarcateTransaction>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/templates/layoutTemplates.war/web/html/header_main.jsp#2 $$Change: 651448 $--%>
