<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ page import="java.util.*" %>
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

<i18n:bundle baseName="atg.portal.admin.UserSettingsResource" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>

<paf:RegisteredUserBarrier/>


<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/portal/alert/AlertFormHandler" />


<admin:InitializeUserAdminEnvironment id="userAdminEnv">

<html>
<head>
 <title><i18n:message key="title-community_users_alerts"/></title>
</head>

 <core:ExclusiveIf>
  <core:IfNotNull value="<%=  userAdminEnv.getCommunity().getStyle() %>">

   <core:CreateUrl id="ccc_encoded_url" url="<%= userAdminEnv.getCommunity().getStyle().getCSSURL() %>">
    <link rel="STYLESHEET" type="text/css" href="<%= ccc_encoded_url.getNewUrl() %>" src="<%= ccc_encoded_url.getNewUrl() %>">
  </core:CreateUrl>

  </core:IfNotNull>

  <core:DefaultCase>
    <style type="text/css">
      <!--
       body { font-family:verdana,arial,geneva,helvetica,san-serif ; }
       font {font-family:verdana,arial,geneva,helvetica,san-serif ; }      
      -->
    </style>
  </core:DefaultCase>
 </core:ExclusiveIf>

 <style type="text/css">
 
 a          { font-family:verdana,arial,geneva,helvetica,sans-serif ; font-size: 10px; }
 a:hover    { text-decoration:underline; }
 a:active   { text-decoration:underline; color: #0000CC;}
 
 a.useradmin_nav       { text-decoration: none; }
 a.useradmin_leftnav   { text-decoration: none; }
 
 a.breadcrumb   {  font-size: 10px; text-decoration:none; }
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
 
  .error  {font-size:10px; color : cc0000;  }
  .info   {font-size:10px; color : 000000;  }
 
  .helpertext {font-size:10px; color : 333333}
  .adminbody {font-size:10px; color : 333333}
  .subheader{font-size:10px; color : 333333; font-weight:700}
  .pageheader {font-size:12px; color : FFFFFF; font-weight:700}
  .pageheader_edit {font-size:12px; color : #4D4E4F; font-weight:700}
 
  .side-reg       { color:0000cc; font-size:8pt; }
  .side-on-main   { color:000000; font-size:8pt; font-weight:700; text-decoration:none;  }
  .side-on-return { color:0000cc; font-size:8pt; font-weight:700; }

</style>

<%

  atg.portal.framework.Page currPage = userAdminEnv.getSourceCommunity().getDefaultPage();
  String gearTitleBackgroundColor = currPage.getColorPalette().getGearTitleBackgroundColor() ;
  String gearTitleTextColor       =  currPage.getColorPalette().getGearTitleTextColor();
//  String gearBackgroundColor    = currPage.getColorPalette().getGearBackgroundColor() ;
//  String pageBackgroundColor    = currPage.getColorPalette().getPageBackgroundColor() ;
  String pageTextColor          = currPage.getColorPalette().getPageTextColor();
//  String pageLinkColor          = currPage.getColorPalette().getPageLinkColor();
//  String gearTextColor          = currPage.getColorPalette().getGearTextColor() ;

    String clearGif =  response.encodeURL("images/clear.gif");

%>


<body  bgcolor="#ffffff" text="#333333" link="#3366ff" vlink="#3366ff"
 marginheight="0" marginwidth="0" leftmargin="0" rightmargin="0" topmargin="0" bottommargin="0"> 

<div class="user_admin_header_bg_img" ><img src="<%=clearGif%>" height="61" width="680" border="0" alt="" /></div>



<dsp:setvalue bean="AlertFormHandler.community"  value="<%= userAdminEnv.getCommunity() %>" /> 
<dsp:getvalueof id="profile" bean="Profile">
<dsp:setvalue bean="AlertFormHandler.repositoryId" value="<%= ((atg.userprofiling.Profile) profile).getRepositoryId() %>"/>
</dsp:getvalueof>

<table border="0" cellpadding="8" cellspacing="0" width="100%">
<tr>
<td width="50%" nowrap align="left"><font class="user_admin_header_community_name" color="#<%=pageTextColor%>">&nbsp;&nbsp;

<i18n:message key="nav-header-title-alerts-param">
<i18n:messageArg value="<%= userAdminEnv.getSourceCommunity().getName(response.getLocale())%>"/>
</i18n:message>
</font>

</td>
<td width="50%" align="right" valign="bottom"><font class="user_admin_header_links" color="#<%=pageTextColor%>">&nbsp;
&nbsp;&nbsp;
<%--  LINK TO Community or not--%>
<core:ExclusiveIf>
 <core:If value="<%=userAdminEnv.getSourceCommunity().isEnabled()%>">
  <i18n:message id="linkToComm" key="nav-header-link-go-to-param">
   <i18n:messageArg value="<%=userAdminEnv.getSourceCommunity().getName(response.getLocale())%>"/>
  </i18n:message>
  <core:CreateUrl id="commURL" url="<%=  userAdminEnv.getCommunityURI(userAdminEnv.getSourceCommunity().getId())%>">
  &nbsp;<dsp:a iclass="useradmin_nav" href="<%=commURL.getNewUrl()%>" target="_top"><font color="#<%=pageTextColor%>"><%=linkToComm%></font></dsp:a>&nbsp;
   </core:CreateUrl>
   
  <core:IfNotNull value="<%=userAdminEnv.getPage()%>">
    <i18n:message id="linkToPage" key="nav-header-link-go-to-param">
      <i18n:messageArg value="<%=userAdminEnv.getPage().getName()%>"/>
    </i18n:message>  
    <core:CreateUrl id="pageURL" url="<%= userAdminEnv.getPageURI(userAdminEnv.getSourcePage().getId())%>">
      <br/>&nbsp;<dsp:a iclass="useradmin_nav" href="<%=pageURL.getNewUrl()%>" target="_top"><font color="#<%=pageTextColor%>"><%=linkToPage%></font></dsp:a>&nbsp;    
    </core:CreateUrl>    
  </core:IfNotNull>
  
 </core:If>
 <core:DefaultCase>
   <font color="#<%=pageTextColor%>"  >
    <i18n:message key="nav-header-community-offline-param"> 
     <i18n:messageArg value="<%= userAdminEnv.getSourceCommunity().getName(response.getLocale())%>"/>
    </i18n:message>&nbsp;&nbsp;</font>
 </core:DefaultCase>
</core:ExclusiveIf>
</td></tr>
</table>


<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
<td bgcolor="#B7B8B7"><img src='<%=clearGif%>' height="1" width="1" alt=""></td>
</tr>
<tr>
<td  bgcolor="#677886"><img src='<%=clearGif%>' height="1" width="1" alt=""></td>
</tr>
</table>

<blockquote>
<font class="pageheader">
 <i18n:message key="alerts_channel_config_title_param">
  <i18n:messageArg value="<%= userAdminEnv.getCommunity().getName(response.getLocale())%>"/>
 </i18n:message>
</font>
 
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="AlertFormHandler.formError"/>
  <dsp:oparam name="true">
    <font class="error">
      <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
        <dsp:param name="exceptions" bean="AlertFormHandler.formExceptions"/>
        <dsp:oparam name="output">
           <img src='<%= response.encodeURL("images/info.gif")%>' >&nbsp;&nbsp; <dsp:valueof param="message"></dsp:valueof>
        </dsp:oparam>
      </dsp:droplet>
    </font>
  </dsp:oparam>
</dsp:droplet>

<br>
  <table width="80%" border="0" cellspacing="2" cellpadding="0">
<dsp:form action="alerts.jsp" method="post">   
    <tr>
      <td colspan=3 bgcolor="#000033"><img src="<%=clearGif%>" height="1" width="1" border="0"></td>
    </tr>

    <dsp:input type="hidden" bean="AlertFormHandler.repositoryId"/>
	<dsp:getvalueof id="userPreferences" bean='<%= "AlertFormHandler.value.alertNotifyPreferences" %>'  idtype="List">
	<dsp:getvalueof id="gears" bean="AlertFormHandler.gears">

      <core:createUrl id="successURL" url='<%= request.getParameter("paf_page_url") %>'>
        <dsp:input type="hidden" bean="AlertFormHandler.updateSuccessURL" value="<%= successURL.getNewUrl() %>" />  
      </core:createUrl>

      <input type="hidden" name="paf_page_url" value="<%= request.getParameter("paf_page_url") %>" />

      <core:createUrl id="errorURL" url="alerts.jsp">
        <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>' />
        <core:urlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>' />
        <dsp:input type="hidden" bean="AlertFormHandler.updateErrorURL" value="<%= errorURL.getNewUrl() %>" />
      </core:createUrl>

      <core:createUrl id="cancelURL" url='<%= request.getParameter("paf_page_url") %>'>
        <dsp:input type="hidden" bean="AlertFormHandler.cancelURL" value="<%= cancelURL.getNewUrl() %>" />  
      </core:createUrl>

	  <%-- Go through the list of gears in the AlertFormHandler gear property --%>
      <core:ifNot value="<%= ((Collection)gears).isEmpty() %>">
        <core:forEach id="selectedGears"
                      values="<%= gears %>"
                      castClass="atg.portal.framework.Gear"
                      elementId="gear">
          <tr>
            <td colspan="3"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
          </tr>

          <%-- Display the Gear information needed for the form. --%>
          <tr>
            <td valign="top" align="left"><font class="adminbody"><%= gear.getName(response.getLocale()) %></font></td>
            <td valign="top" align="left">
        <table border="0" cellpadding="0" cellspacing="0">

		<%-- Loop through the list of alertNotifyPreference settings this user has --%>
		<% for(int valIndex=0; valIndex<userPreferences.size(); valIndex++) 
		    { 
		%>
			<dsp:getvalueof id="prefGearId" bean='<%= "AlertFormHandler.value.alertNotifyPreferences[" + valIndex +"].sourceId" %>'>

			<%-- If this preference is for the current gear, then display it's info --%>
			<core:if value="<%= (gear.getId().equals(prefGearId)) %>">
			  <tr><td width="172"><font class="adminbody">
              <dsp:valueof bean='<%= "AlertFormHandler.value.alertNotifyPreferences[" + valIndex +"].name" %>'/>
              </font></td>
              <dsp:input type="hidden" bean='<%= "AlertFormHandler.value.alertNotifyPreferences[" + valIndex + "].channels.updateMode" %>'  value="replace"/>
              <dsp:input type="hidden" bean='<%= "AlertFormHandler.value.alertNotifyPreferences[" + valIndex + "].channels.numNewValues" %>'  value="0"/>

              <%-- Display a checkbox for each channel in repository --%>
              <dsp:droplet name="/atg/dynamo/droplet/RQLQueryForEach">
                <dsp:param name="repository" value="/atg/portal/alert/AlertRepository"/>
                <dsp:param name="queryRQL" value="ALL"/>
                <dsp:param name="itemDescriptor" value="alert_channel"/>
                <dsp:param name="castClass" value="atg.repository.RepositoryItem"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof id="elementId" idtype="java.lang.String" param="element.repositoryId">

                  <%-- Display the channel --%>
                  <td><font class="adminbody"><dsp:input type="checkbox" value="<%= elementId %>"  bean='<%= "AlertFormHandler.value.alertNotifyPreferences[" + valIndex + "].channels.repositoryIds" %>' />&nbsp;<dsp:valueof param="element.display_name"/>&nbsp;&nbsp;</font></td>
                  </dsp:getvalueof>
                </dsp:oparam>               
              </dsp:droplet>

              </tr>
            </core:if>
            </dsp:getvalueof>
      <%  } 
      %>
      </table>
      </font>

           </td>
         </tr>

         <tr>
            <td colspan="2"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
          </tr>

          <tr>
            <td colspan=2 bgcolor="#000033"><img src="<%=clearGif%>" height="1" width="1" border="0"></td>
          </tr>

        </core:forEach>
      </core:ifNot>

      <core:if value="<%= ((Collection)gears).isEmpty() %>">
        <tr>
          <td valign="top" align="left" colspan="2">
           <font class="adminbody"><i18n:message key="alerts_text1"/></font>  
          </td>
        </tr>

         <tr>
            <td colspan="2"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
          </tr>

          <tr>
            <td colspan=2 bgcolor="#000033"><img src="<%=clearGif%>" height="1" width="1" border="0"></td>
          </tr>
      </core:if>

      <tr>
        <td colspan="2"><img src="<%=clearGif%>" height="2" width="1" border="0"></td>
      </tr>

      <core:ifNot value="<%= ((Collection)gears).isEmpty() %>"> 
        <tr valign="top" align="left">
        <i18n:message id="save01" key="save" />
          <td align="right"><dsp:input type="submit" value="<%= save01 %>" bean="AlertFormHandler.update"/></td>
          <i18n:message id="cancel01" key="cancel" />
          <td align="left">&nbsp;&nbsp;&nbsp;<dsp:input type="submit" value="<%= cancel01 %>" bean="AlertFormHandler.cancel"/>
          </td>
        </tr>
      </core:ifNot>

      <core:if value="<%= ((Collection)gears).isEmpty() %>">
        <tr valign="top" align="left">
          <i18n:message id="cancel02" key="cancel" /> 
          <td align="left" colspan="2"><dsp:input type="submit" value="<%= cancel02 %>" bean="AlertFormHandler.cancel"/>
          </td>
        </tr>
      </core:if>
    </dsp:getvalueof>
    </dsp:getvalueof>
  </dsp:form>
 </table>
 </blockquote>





</body>
</html>
</admin:InitializeUserAdminEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/alerts.jsp#2 $$Change: 651448 $--%>
