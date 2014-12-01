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
    } else {
       mode = "1"; // default to community settings
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
 <i18n:message key="community_settings_toplabel_params"> 
  <i18n:messageArg value="<%=adminEnv.getCommunity().getName() %>"/>
  <i18n:messageArg value="<%=i18n_bold%>"/>
  <i18n:messageArg value="<%=i18n_end_bold%>"/>
 </i18n:message>	
 </font></td></tr></table>
</td></tr></table>


<img src='<%=clearGif %>' height="1" width="20" alt=""><BR />

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%"><tr><td>
<%--   COMMUNITY NAME and URL --%>

<dsp:form action="community_settings.jsp" method="POST" name="newcommunity" synchronized="/atg/portal/admin/CommunityFormHandler">
 <dsp:setvalue bean="CommunityFormHandler.setLocale"/>
 <dsp:setvalue bean="CommunityFormHandler.id" value="<%= communityId %>"/>

<dsp:getvalueof id="dsp_page_url"     idtype="java.lang.String" param="paf_page_url">
 <table width="350" border="0" cellspacing="0" cellpadding="0">
  <tr>
   <td><font class="subheader"><i18n:message key="community_settings_communityname_title"/></font><br />
   <font class="small"><dsp:input type="text" bean="CommunityFormHandler.name" size="30"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font></td>
   <td NOWRAP><font class="subheader"><i18n:message key="community_settings_communityurl_title"/></font>
   <br /><font class="small"><%= adminEnv.getCommunityURI(adminEnv.getCommunity()) %></font>
   </td>
  </tr>
  
   <tr>
   <td><br /><font class="subheader"><i18n:message key="community_settings_communityurlname_title"/></font><br />
   <font class="small"><dsp:input type="text" bean="CommunityFormHandler.url" size="20"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font></td>
   <br />
   </tr>
 </table>

<br />

<%-- ONLINE STATUS --%>
<table border="0" cellspacing="0" cellpadding="0">
 <tr>
   <td valign="top"><font class="subheader"><i18n:message key="community_settings_status_title"/></font><br />
<font class="small">
<dsp:select bean="CommunityFormHandler.active">
<dsp:option value="true"/><i18n:message key="community_settings_status_active"/>
<dsp:option value="false"/><i18n:message key="community_settings_status_inactive"/>
</dsp:select></font></td>
    <td>&nbsp;&nbsp;</td>
    <td valign="top"><font class="adminbody">
    <br /><i18n:message key="community_settings_status_notation"/></font></td>
  </tr>
</table>
<br />
<%-- DESCRIPTION --%>
<font class="subheader"><i18n:message key="community_settings_discription"/></font><br />
<font class="small">
<dsp:textarea bean="CommunityFormHandler.description" cols="38" rows="3" />
</font>
<%-- MEMBERSHIP --%>
<br /><br />
<font class="subheader"><i18n:message key="community_settings_requestmembership_title"/></font><br />
<dsp:input type="radio" bean="CommunityFormHandler.membershipRequest" value="automatic"/>&nbsp;
<font class="adminbody"><i18n:message key="community_settings_requestmembership_autoexcept"/></font>
<br />
<dsp:input type="radio" bean="CommunityFormHandler.membershipRequest" value="allow"/>&nbsp;
<font class="adminbody"><i18n:message key="community_settings_requestmembership_notify"/></font>
<br />
<dsp:input type="radio" bean="CommunityFormHandler.membershipRequest" value= "disallow"/>&nbsp;
<font class="adminbody"><i18n:message key="community_settings_requestmembership_noexcept"/></font>
<br /><br />

<admin:GetAllItems id="items">
<%-- PAGE TEMPLATES    --%>
<font class="subheader"><i18n:message key="community_settings_pagetemplate_title"/></font><br />
<font class="small">
<dsp:select bean="CommunityFormHandler.pageTemplate">
<core:ForEach id="allTemplates"
              values="<%= items.getPageTemplates(atg.portal.framework.Comparators.getPageTemplateComparator()) %>"
              castClass="atg.portal.framework.PageTemplate"
              elementId="templateItem">
<dsp:option value="<%= templateItem.getId() %>"/> <%= templateItem.getName() %>
</core:ForEach>
</dsp:select>
</font>  
<br />
<br />
<%--   STYLE   --%>
<font class="subheader"><i18n:message key="community_settings_style_title"/></font><br />
<font class="small">
<dsp:select  bean="CommunityFormHandler.style">
<% boolean noStyle = false; %>

<dsp:getvalueof id="crntStyle" bean="CommunityFormHandler.style">
  <core:ifNull value="<%= crntStyle %>">
    <% noStyle = true; %>  
 </core:ifNull>
 <dsp:option value="" selected="<%= noStyle %>"/><i18n:message key="community-nostylesheet"/>
<core:ForEach id="allStyles"
              values="<%= items.getStyles(atg.portal.framework.Comparators.getStyleComparator()) %>"
              castClass="atg.portal.framework.Style"
              elementId="styleItem">
 <dsp:option value="<%= styleItem.getId() %>"/> <%= styleItem.getName() %>
</core:ForEach>
</dsp:getvalueof><%--  id="crntStyle"  --%>
</dsp:select>
</font>
<br /><br />

<%--  GEAR TITLE TEMPLATES   --%>
<font class="subheader"><i18n:message key="community_settings_geartemplate_title"/></font><br />
<font class="small">
<dsp:select bean="CommunityFormHandler.gearTitleTemplate">
<core:ForEach id="allTemplates"
              values="<%= items.getGearTitleTemplates(atg.portal.framework.Comparators.getGearTitleTemplateComparator()) %>"
              castClass="atg.portal.framework.GearTitleTemplate"
              elementId="templateItem">
 <dsp:option value="<%= templateItem.getId() %>"/> <%= templateItem.getName() %>
</core:ForEach>
</dsp:select>
</font>


</admin:GetAllItems>


<br /><br />

<%-- CUSTOMIZE /PERSONALIZE --%>
<font class="adminbody"><b><i18n:message key="community_settings_personalization_title"/></b><br />

&nbsp;<dsp:input type="radio" bean="CommunityFormHandler.personalization" value="community"/>&nbsp;<i18n:message key="community_settings_personalization_membersandcreate"/>
<br />
&nbsp;<dsp:input type="radio" bean="CommunityFormHandler.personalization" value="page"/>&nbsp;<i18n:message key="community_settings_personalization_membersnocreate"/>
<br />
&nbsp;<dsp:input type="radio" bean="CommunityFormHandler.personalization" value="none"/>&nbsp;<i18n:message key="community_settings_personalization_noallow"/>
</font>

<br />
<br />

<%-- WIRELESS --%>
<font class="adminbody"><b><i18n:message key="community_settings_wireless_title"/></b></font><br />
<font class="small">
<dsp:select bean="CommunityFormHandler.wirelessPage">
<dsp:option value="-1"/><i18n:message key="community_settings_wireless_none"/>
<core:ForEach id="communityPages"
              values="<%= adminEnv.getCommunity().getPageSet() %>"
              castClass="atg.portal.framework.Page"
              elementId="pageItem">
 <dsp:option value="<%= (String) pageItem.getId() %>"/> <%= (String) pageItem.getName() %>
</core:ForEach>
</dsp:select>
</font><font class="smaller">&nbsp;&nbsp;<i18n:message key="community_settings_wireless_page"/>
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
<dsp:input type="submit" bean="CommunityFormHandler.update"  value="<%=i18n_update%>"   />

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
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_settings_edit.jsp#2 $$Change: 651448 $--%>
