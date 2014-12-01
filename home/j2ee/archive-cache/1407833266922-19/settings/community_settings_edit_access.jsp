<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
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

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>
<paf:hasCommunityRole roles="leader,settings-manager">

<core:demarcateTransaction id="demarcateXA">
  <% try { %>

<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/portal/admin/CommunityFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<i18n:message key="imageroot" id="i18n_imageroot"/>

<%
    String clearGif =  response.encodeURL("images/clear.gif");
    String thisNavHighlight = "settings";  // used for header_main
    String currPid       = "0";           // used for sidebar sub selection
    String mode          = "1";          // used for sub page with area
                                         // this copies an index array to a rendering array 
                                         // that provides the side links
    String outputStr     = "";           // the output for header and sidebar includes

    if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    }
%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="CommunityFormHandler.formError"/>
  <dsp:oparam name="true">
    <dsp:setvalue bean="CommunityFormHandler.resetFormExceptions"/>      
  </dsp:oparam>        
  <dsp:oparam name="false">
    <%--     reset the session scoped form handler --%>
    <dsp:setvalue bean="CommunityFormHandler.reset"/>
  </dsp:oparam>        
</dsp:droplet>

<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
<font class="pageheader">
<i18n:message id="i18n_bold" key="html-bold"/>
 <i18n:message id="i18n_end_bold" key="html-end-bold"/>
 <i18n:message key="community_settings_access_toplabel_params"> 
  <i18n:messageArg value="<%=adminEnv.getCommunity().getName() %>"/>
  <i18n:messageArg value="<%=i18n_bold%>"/>
  <i18n:messageArg value="<%=i18n_end_bold%>"/>
 </i18n:message>	
 </td></tr></table>
</td></tr></table>

<img src='<%=clearGif %>' height="1" width="20" ><BR />
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%"><tr><td>

<dsp:form action="community_settings.jsp" method="POST" name="newcommunity" synchronized="/atg/portal/admin/CommunityFormHandler">
 <dsp:setvalue bean="CommunityFormHandler.setLocale"/>
 <dsp:setvalue bean="CommunityFormHandler.id" value="<%= communityId %>"/>

<dsp:getvalueof id="dsp_page_url"     idtype="java.lang.String" param="paf_page_url">

<%--  ACCESS LEVELS --%>
<font class="adminbody"><b><i18n:message key="community_settings_access_title"/></b><br />
&nbsp;<dsp:input type="radio" bean="CommunityFormHandler.accessLevel" value="0"/>&nbsp;<i18n:message key="community_settings_access_any"/>
<br />
&nbsp;<dsp:input type="radio" bean="CommunityFormHandler.accessLevel" value="1"/>&nbsp;<i18n:message key="community_settings_access_all"/>
<br />
&nbsp;<dsp:input type="radio" bean="CommunityFormHandler.accessLevel" value="2"/>&nbsp;<i18n:message key="community_settings_access_guestandmeberandleader"/>
<br />
&nbsp;<dsp:input type="radio" bean="CommunityFormHandler.accessLevel" value="3"/>&nbsp;<i18n:message key="community_settings_access_memberandleader"/>
<br />
&nbsp;<dsp:input type="radio" bean="CommunityFormHandler.accessLevel" value="4"/>&nbsp;<i18n:message key="community_settings_access_leader"/>
<br /> 


</font>


<%-- SUCCESS and FAILURE URLS --%>
<core:CreateUrl id="thisPageURL" url="/portal/settings/community_settings.jsp">
 <core:UrlParam param="paf_community_id" value="<%= communityId %>"/>
 <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
 <core:UrlParam  param="mode" value="<%=mode%>"/>


<dsp:input type="hidden"  bean="CommunityFormHandler.successURL"  value='<%=thisPageURL.getNewUrl()%>'/>
<dsp:input type="hidden"  bean="CommunityFormHandler.failureURL"  value='<%=thisPageURL.getNewUrl()%>'/>

</core:CreateUrl>

<%-- SUBMIT BUTTONS --%>
<br /><br /><i18n:message id="i18n_update" key="update"/>
&nbsp;<dsp:input type="submit" bean="CommunityFormHandler.update"  value="<%=i18n_update%>"   />
<br /><br /> 
</dsp:getvalueof><%-- id="dsp_page_url"     --%>

</dsp:form>
</td></tr></table>

</admin:InitializeAdminEnvironment>

  <% } catch (Exception e) { %>
  <core:setTransactionRollbackOnly id="rollbackOnlyXA">
    <core:ifNot value="<%= rollbackOnlyXA.isSuccess() %>">
      The following exception was thrown:
      <pre>
       <%= rollbackOnlyXA.getException() %>
      </pre>
    </core:ifNot>
  </core:setTransactionRollbackOnly>
  <% } %>
</core:demarcateTransaction>

</paf:hasCommunityRole>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_settings_edit_access.jsp#2 $$Change: 651448 $--%>
