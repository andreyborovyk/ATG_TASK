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

<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String"     param="paf_page_url">

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
<dsp:setvalue bean="PageFormHandler.community"  value="<%= adminEnv.getCommunity() %>" />

   <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
<font class="pageheader">
<i18n:message key="user_pages_newpage_header"/>
        </td></tr></table>
    </td></tr></table>
        
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
<font class="smaller">
    <i18n:message key="user_pages_newpage_helpertext"/>
</td></tr></table>
<img src="images/clear.gif" width="1" height"1" border="0"><br>
  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%"><tr><td>
  <font class="smaller">
 <dsp:getvalueof id="crntName"  bean="PageFormHandler.name">
  <i18n:message key="user_pages_basic_pagename"/></font><br>
  <dsp:input bean="PageFormHandler.name" type="text" value="<%= crntName %>"/>

 </dsp:getvalueof>
 <br><br>

 <dsp:getvalueof id="crntUrl"  bean="PageFormHandler.url">
  <font class="smaller"><i18n:message key="user_pages_basic_pageurl"/></font><br>
  <dsp:input bean="PageFormHandler.url" type="text" value="<%= crntUrl %>"/>
 </font>
 </dsp:getvalueof>
<core:CreateUrl id="CpagesURLsuccess"       url="/portal/settings/user.jsp">
    <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
    <core:UrlParam param="mode"               value='<%=request.getParameter("mode")%>'/>
     <dsp:input type="hidden"  bean="PageFormHandler.failureURL" value="<%=portalServletResponse.encodePortalURL(CpagesURLsuccess.getNewUrl())%>"/>
</core:CreateUrl>

<core:CreateUrl id="CpagesURLcancel"       url="/portal/settings/user.jsp">
     <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
     <core:UrlParam param="mode"               value="1"/>
     <dsp:input type="hidden"  bean="PageFormHandler.successURL" value="<%=portalServletResponse.encodePortalURL(CpagesURLcancel.getNewUrl())%>"/>
     <dsp:input type="hidden"  bean="PageFormHandler.cancelURL" value="<%=portalServletResponse.encodePortalURL(CpagesURLcancel.getNewUrl())%>"/>
</core:CreateUrl>

<br><br>

<i18n:message id="submitLabel" key="create"/>
<dsp:input type="SUBMIT" value="<%=submitLabel%>"  bean="PageFormHandler.createPageUserMode"/>&nbsp;&nbsp;&nbsp;
<i18n:message id="cancelLabel" key="cancel"/>
<dsp:input type="SUBMIT" value="<%=cancelLabel%>"  bean="PageFormHandler.cancel" />
</td></tr></table>

</dsp:form>


</dsp:getvalueof><%-- id="dsp_page_url"     --%>



</admin:InitializeAdminEnvironment>
</dsp:page>  
     
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/user_page_new.jsp#2 $$Change: 651448 $--%>
