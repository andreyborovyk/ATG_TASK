<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%
  //Community
  Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
  String communityId = null;
  if(cm != null) {
    communityId = cm.getId();
  }

  //Page
  Page pg = (Page)request.getAttribute(Attribute.PAGE);
  String pageId = null;
  if(pg != null) {
    pageId = pg.getId();
  }

  //Request/Response
  PortalServletResponse portalServletResponse = 
     (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
  PortalContextImpl portalContext = new PortalContextImpl(portalServletRequest);
%>

<paf:setFrameworkLocale/>

<i18n:bundle baseName="atg.portal.admin.UserSettingsResource" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>
<paf:RegisteredUserBarrier/>
<admin:InitializeUserAdminEnvironment id="userAdminEnv">

<dsp:importbean bean="/atg/portal/admin/PageFormHandler"/>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="PageFormHandler.formError"/>
  <dsp:oparam name="true">
    <dsp:setvalue bean="PageFormHandler.resetFormExceptions"/>
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:setvalue bean="PageFormHandler.reset"/>
  </dsp:oparam>
</dsp:droplet> 

<dsp:form action="user.jsp" method="POST" synchronized="/atg/portal/admin/PageFormHandler">

<% String highLight = "basic"; %>
<%@include file="user_page_nav.jspf" %>



<%
 boolean isAdmin = false;
 boolean isShared = false;
 boolean isCommunity = false;
 String i18n_imageroot = "images";


{
  // if the page is either undefined or inaccessible, tell them they
  // can't do this.
  atg.portal.framework.Page checkPage = userAdminEnv.getPage();
  if ((checkPage == null) ||
      !checkPage.hasAccess(atg.portal.framework.PortalAccessRights.READ)) {
    response.sendRedirect(userAdminEnv.getAccessDeniedURI());
    return;
  }
}

%>

<DECLAREPARAM NAME="pageId" CLASS="java.lang.String"
      DESCRIPTION="The repository id of the page we are working on.">
<DECLAREPARAM NAME="pageURL" CLASS="java.lang.String"
      DESCRIPTION="The URL where we need to go after personalization.">
<DECLAREPARAM NAME="communityId" CLASS="java.lang.String"
      DESCRIPTION="repository id for the Community that this page is part of.">
<DECLAREPARAM NAME="successStr" CLASS="java.lang.String"
      DESCRIPTION="the string corresponding to page redirect URL.">



 <dsp:setvalue bean="PageFormHandler.community"  value="<%= userAdminEnv.getCommunity() %>" />
 <dsp:setvalue bean="PageFormHandler.page"  value="<%=userAdminEnv.getPage() %>" />   
<img src="images/clear.gif" width="1" height"1" border="0"><br>
 <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
 <font class="smaller"><i18n:message key="user_pages_basic_pagename"/></b><br>
  <dsp:input bean="PageFormHandler.name" type="text" value="<%= userAdminEnv.getPage().getName() %>"/>
 </font>
<core:CreateUrl id="CpagesURLsuccess"       url="/portal/settings/user.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="mode"               value='<%=request.getParameter("mode")%>'/>

   <dsp:input type="hidden"  bean="PageFormHandler.successURL" value="<%=portalServletResponse.encodePortalURL(CpagesURLsuccess.getNewUrl())%>"/>
   <dsp:input type="hidden"  bean="PageFormHandler.failureURL" value="<%=portalServletResponse.encodePortalURL(CpagesURLsuccess.getNewUrl())%>"/>

</core:CreateUrl>



<br><br>

<i18n:message id="submitLabel" key="update"/>
<dsp:input type="SUBMIT" value="<%=submitLabel%>"  bean="PageFormHandler.updatePageUserMode"/>
&nbsp;&nbsp;
</td></tr></table>
<!-- end inc_gears  -->
</dsp:form>
  
</admin:InitializeUserAdminEnvironment>
</dsp:page>  
     
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/user_page_basic.jsp#2 $$Change: 651448 $--%>
