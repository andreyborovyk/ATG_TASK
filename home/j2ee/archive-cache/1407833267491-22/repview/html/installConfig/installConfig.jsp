<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/rpv" prefix="rpv" %>
<dsp:importbean bean="/atg/portal/gear/repview/RepViewConfigFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/portal/admin/SuccessMessageProcessor"/>
<dsp:importbean bean="/atg/portal/admin/FailureMessageProcessor"/>
<dsp:page>


<%-- 
  Set the GEAR DEFINITION ID so that the InitializeGearEnvironment 
  tag will work.  This is required on all installConfig pages. 
 --%>   
<% request.setAttribute(atg.portal.framework.RequestAttributes.GEAR_DEFINITION, (String) request.getParameter("paf_gear_def_id") ); %>

<paf:InitializeGearEnvironment id="pafEnv">
<paf:PortalAdministratorCheck>
<rpv:repViewPage id="rpvpage" gearEnv="<%= pafEnv %>">

<html>
<head>
<title>Install Config</Title>
<style  type="text/css">
 <!--

 body { font-family:verdana,arial,geneva,helvetica,sans-serif ; }
 font { font-family:verdana,arial,geneva,helvetica,sans-serif ; }

		
 a          { font-family:verdana,arial,geneva,helvetica,sans-serif ; font-size: 10px; }
 a:hover    { text-decoration:underline; }
 a:active   { text-decoration:underline; color: #0000CC;}

 a.portaladmin_nav       { color: #4D4E4F; text-decoration: none; }
 a.portaladmin_leftnav   { color: #4D4E4F; text-decoration: none; }
 
 a.breadcrumb   { font-size: 10px; text-decoration:none; }
 a.gear_nav     { font-weight:700; text-transform:lowercase; text-decoration:none; }
 a.gear_content { font-weight:300 }
 a.admin_link   { font-size: 10px; font-weight:300; text-decoration:none; }

 .smaller {font-size:10px; }
 .small   {font-size:12px; }
 .medium  {font-size:13px; }
 .large   {font-size:15px; }
 .larger  {font-size:17px; }

 .smaller_bold   {font-size:10px; font-weight:700 }
 .small_bold     {font-size:12px; font-weight:700 }
 .medium_bold    {font-size:13px; font-weight:700 }
 .large_bold     {font-size:15px; font-weight:700 }
 .larger_bold    {font-size:17px; font-weight:700 }
 .humungous_bold { font-size:22px;font-weight:700}
 
 .breadcrumb_link {font-size:10px; color : 0000cc}
 .small_link {font-size:12px; color : 0000cc}
 
 
 .info   {font-size:10px; color : 000000}
 .error  {font-size:10px; color : cc0000}
 
 .helpertext {font-size:10px; color : 333333}
 .adminbody  {font-size:10px; color : 333333}
 .subheader  {font-size:10px; color : 333333; font-weight:700}
 .pageheader {font-size:12px; color : FFFFFF; font-weight:700}
 .pageheader_edit {font-size:12px; color : #4D4E4F; font-weight:700}

 -->
 </style>

</head>

<body bgcolor="#ffffff">
<br>


<i18n:bundle baseName="atg.portal.gear.repview.RepViewConfigResources" changeResponseLocale="false" />


<% 
   String origURI= rpvpage.getRelativeUrl("/html/installConfig/installConfig.jsp");
   String geardefID = pafEnv.getGearDefinition().getId();
   String successURL = request.getParameter("paf_success_url");

   String configPage = request.getParameter("config_page");
   if (configPage == null)
	configPage = "unset";

%>

<%
 String repositoryStyle   = (configPage.equals("unset") || 
                             configPage.equals("Repository1") || 
                             configPage.equals("Repository2"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String resourcebundleStyle   = (configPage.equals("resourcebundle"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String delegateStyle   = (configPage.equals("delegate"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String shortlistStyle   = (configPage.equals("shortlist"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String featureditemStyle   = (configPage.equals("featureditem"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String fulllistStyle   = (configPage.equals("fulllist"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String displayitemStyle   = (configPage.equals("displayitem"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
%>


<%-- Display config options --%>
  <table bgcolor="#cccccc" width="98%" border="0" cellspacing="0" cellpadding="4">

    <tr>
      <td>


    
    <%-- REPOSITORY --%>
    <core:CreateUrl id="addURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="installConfig"/>
	<core:UrlParam param="paf_gear_def_id" value="<%= geardefID %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
	<core:UrlParam param="config_page" value="Repository1"/>
	<a href="<%= addURL.getNewUrl() %>"><font <%= repositoryStyle %> ><i18n:message key="config-repository-link"/></font></a>
    </core:CreateUrl>

    <font class="small">&nbsp;|&nbsp;</font>

    <%-- DELEGATE --%>
    <core:CreateUrl id="addURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="installConfig"/>
	<core:UrlParam param="paf_gear_def_id" value="<%= geardefID %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
	<core:UrlParam param="config_page" value="delegate"/>
	<a href="<%= addURL.getNewUrl() %>"><font <%= delegateStyle %> ><i18n:message key="config-delegate-link"/></font></a>
    </core:CreateUrl>

    <font class="small">&nbsp;|&nbsp;</font>

    <%-- RESOURCE BUNDLE --%>
    <core:CreateUrl id="addURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="installConfig"/>
	<core:UrlParam param="paf_gear_def_id" value="<%= geardefID %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
	<core:UrlParam param="config_page" value="resourcebundle"/>
	<a href="<%= addURL.getNewUrl() %>"><font <%= resourcebundleStyle %> ><i18n:message key="config-resourcebundle-link"/></font></a>
    </core:CreateUrl>

    <font class="small">&nbsp;|&nbsp;</font>
    
    <%-- FEATURED ITEM --%>
    <core:CreateUrl id="addURL" url="<%= origURI %>">
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gm" value="installConfig"/>
      <core:UrlParam param="paf_gear_def_id" value="<%= geardefID %>"/>
      <paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
      <core:UrlParam param="config_page" value="featureditem"/>
      <a href="<%= addURL.getNewUrl() %>"><font <%= featureditemStyle %>><i18n:message key="config-featureditem-link"/></font></a>
    </core:CreateUrl>

    <font class="small">&nbsp;|&nbsp;</font>


    <%-- SHORTLIST --%>
    <core:CreateUrl id="addURL" url="<%= origURI %>">
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gm" value="installConfig"/>
      <core:UrlParam param="paf_gear_def_id" value="<%= geardefID %>"/>
      <paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
      <core:UrlParam param="config_page" value="shortlist"/>
      <a href="<%= addURL.getNewUrl() %>"><font <%= shortlistStyle %> ><i18n:message key="config-shortlist-link"/></font></a>
    </core:CreateUrl>

    <font class="small">&nbsp;|&nbsp;</font>


    <%-- FULLLIST --%>
    <core:CreateUrl id="addURL" url="<%= origURI %>">
      <core:UrlParam param="paf_dm" value="full"/>
      <core:UrlParam param="paf_gm" value="installConfig"/>
      <core:UrlParam param="paf_gear_def_id" value="<%= geardefID %>"/>
      <paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
      <core:UrlParam param="config_page" value="fulllist"/>
      <a href="<%= addURL.getNewUrl() %>"><font <%= fulllistStyle %> ><i18n:message key="config-fulllist-link"/></font></a>
    </core:CreateUrl>

    <font class="small">&nbsp;|&nbsp;</font>

    <%-- DISPLAYITEM --%>
    <core:CreateUrl id="addURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="installConfig"/>
	<core:UrlParam param="paf_gear_def_id" value="<%= geardefID %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
	<core:UrlParam param="config_page" value="displayitem"/>
	<a href="<%= addURL.getNewUrl() %>"><font <%= displayitemStyle %> ><i18n:message key="config-displayitem-link"/></font></a>
    </core:CreateUrl>
    </td>
    </tr>
   </table>


      <!-- display errors if any -->
<dsp:getvalueof id="failureMessageProcessor" 
                idtype="atg.portal.admin.I18nMessageProcessor"
                bean="RepViewConfigFormHandler.failureMessageProcessor">
<dsp:getvalueof id="successMessageProcessor" 
                idtype="atg.portal.admin.I18nMessageProcessor"
                bean="RepViewConfigFormHandler.successMessageProcessor">

<%
   failureMessageProcessor.localizeMessages(request, response);
   successMessageProcessor.localizeMessages(request, response);
%>

      <dsp:droplet name="ForEach">
         <dsp:param name="array" bean="RepViewConfigFormHandler.failureMessageProcessor.messages"/>
         <dsp:oparam name="output">
            <dsp:getvalueof id="errorMsg" idtype="java.lang.String" param="element">
            <font class="error"><dsp:img src='<%= rpvpage.getRelativeUrl("/images/error.gif")%>'
            />&nbsp;&nbsp;<%= errorMsg %></font><br/>
            </dsp:getvalueof>
         </dsp:oparam>
      </dsp:droplet>

      <%-- display info messages if any --%> 
      <dsp:droplet name="ForEach">
         <dsp:param name="array" bean="RepViewConfigFormHandler.successMessageProcessor.messages"/>
         <dsp:oparam name="output">
            <dsp:getvalueof id="errorCode" idtype="java.lang.String" param="element">
            <font class="info"><dsp:img src='<%= rpvpage.getRelativeUrl("/images/info.gif")%>'
            />&nbsp;&nbsp;<dsp:valueof param="element"/></font>
            </dsp:getvalueof>
         </dsp:oparam>
      </dsp:droplet>

<%
   failureMessageProcessor.clear();
   successMessageProcessor.clear();
%>
</dsp:getvalueof>
</dsp:getvalueof>
  <%-- tell the form handler that this is instance config --%>
  <dsp:setvalue bean="RepViewConfigFormHandler.paramType" value="instance"/>
  <dsp:setvalue bean="RepViewConfigFormHandler.settingDefaultValues" value="true"/>


  <dsp:droplet name="/atg/dynamo/droplet/Switch">
    <dsp:param name="value" param="config_page" />

    <dsp:oparam name="default">
        <jsp:include page="/html/installConfig/configRepository1.jsp" flush="true" />
    </dsp:oparam>
    <dsp:oparam name="Repository1">
	<jsp:include page="/html/installConfig/configRepository1.jsp" flush="true" />
    </dsp:oparam>
    <dsp:oparam name="Repository2">
	<jsp:include page="/html/installConfig/configRepository2.jsp" flush="true" />
    </dsp:oparam>
    <dsp:oparam name="shortlist">
	<jsp:include page="/html/installConfig/shortlist.jsp" flush="true" />
    </dsp:oparam>
    <dsp:oparam name="fulllist">
	<jsp:include page="/html/installConfig/fulllist.jsp" flush="true" />
    </dsp:oparam>
    <dsp:oparam name="featureditem">
	<jsp:include page="/html/installConfig/featureditem.jsp" flush="true" />
    </dsp:oparam>
    <dsp:oparam name="displayitem">
	<jsp:include page="/html/installConfig/displayitem.jsp" flush="true" />
    </dsp:oparam>
    <dsp:oparam name="resourcebundle">
	<jsp:include page="/html/installConfig/resourcebundle.jsp" flush="true" />
    </dsp:oparam>
    <dsp:oparam name="delegate">
	<jsp:include page="/html/installConfig/delegate.jsp" flush="true" />
    </dsp:oparam>
  </dsp:droplet>

</blockquote>
</font>
</body>
</html>

</rpv:repViewPage>
</paf:PortalAdministratorCheck>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/installConfig/installConfig.jsp#2 $$Change: 651448 $--%>
