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

<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String"     param="paf_page_url">

<dsp:form action="user.jsp" method="POST" synchronized="/atg/portal/admin/PageFormHandler">
<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="pageheader_edit"> <img src='images/write.gif' height="15" width="28" alt="" border="0"><i18n:message key="user_pages_delete_header"/>
</td></tr></table>
</td></tr></table>
<img src='images/clear.gif' height="1" width="1" alt="" border="0"><br>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="smaller">
<dsp:setvalue bean="PageFormHandler.page" value='<%= userAdminEnv.getPage() %>'/>

<dsp:getvalueof id="pageName" bean="PageFormHandler.name">

<i18n:message id="i18n_bold" key="bold"/>
<i18n:message id="i18n_bold_end" key="bold_end"/>
<i18n:message key="confirm_message_delete">
 <i18n:messageArg value="<%=userAdminEnv.getPage().getName()%>"/>
 <i18n:messageArg value="<%=i18n_bold%>"/>
 <i18n:messageArg value="<%=i18n_bold_end%>"/>
</i18n:message>

</dsp:getvalueof>
<br><br>
<core:CreateUrl id="CpagesURLfailure"       url="/portal/settings/user.jsp">
    <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
    <core:UrlParam param="mode"               value='<%=request.getParameter("mode")%>'/>
    <dsp:input type="hidden"  bean="PageFormHandler.failureURL" value="<%=portalServletResponse.encodePortalURL(CpagesURLfailure.getNewUrl())%>"/>
</core:CreateUrl>

<core:CreateUrl id="CpagesURLcancel"       url="/portal/settings/user.jsp">
    <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
    <dsp:input type="hidden"  bean="PageFormHandler.successURL" value="<%=portalServletResponse.encodePortalURL(CpagesURLcancel.getNewUrl())%>"/>
    <dsp:input type="hidden"  bean="PageFormHandler.cancelURL"  value="<%=portalServletResponse.encodePortalURL(CpagesURLcancel.getNewUrl())%>"/>
</core:CreateUrl>


</font>
<i18n:message id="submitLabel" key="delete"/>
<dsp:input type="SUBMIT" value="<%=submitLabel%>"  bean="PageFormHandler.deletePageUserMode" />
<i18n:message id="cancelLabel" key="cancel"/>
&nbsp;&nbsp;&nbsp;<dsp:input type="SUBMIT" value="<%=cancelLabel%>"  bean="PageFormHandler.cancel" />
</td></tr></table>
</dsp:form>

</dsp:getvalueof><%-- id="dsp_page_url"     --%>
  
</admin:InitializeUserAdminEnvironment>
</dsp:page>  
     
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/user_page_delete.jsp#2 $$Change: 651448 $--%>
