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


<paf:setFrameworkLocale/>

<i18n:bundle baseName="atg.portal.admin.UserSettingsResource" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="i18n_newpage" key="user_pages_nav_new_page"/>


<dsp:page>

<paf:RegisteredUserBarrier/>

<admin:InitializeUserAdminEnvironment id="userAdminEnv">

<dsp:getvalueof id="dsp_page_url" idtype="java.lang.String"     param="paf_page_url">

<html>
<head>

 <i18n:message id="titleSeperator" key="titleSeperator"/>

 <core:IfNotNull value="<%=userAdminEnv.getPage()%>">
  <title>
  <i18n:message key="window-title-user-admin_long">
     <i18n:messageArg  value="<%=userAdminEnv.getSourceCommunity().getName(response.getLocale())%>"/>
     <i18n:messageArg  value="<%=userAdminEnv.getPage().getName()%>"/>
     <i18n:messageArg  value="<%=titleSeperator%>"/>
   </i18n:message></title>
 </core:IfNotNull>
 <core:IfNull value="<%=userAdminEnv.getPage()%>">
   <title>
   <i18n:message key="window-title-user-admin_short">
    <i18n:messageArg  value="<%=userAdminEnv.getSourceCommunity().getName(response.getLocale())%>"/>
    <i18n:messageArg  value="<%=titleSeperator%>"/>
   </i18n:message></title>
 </core:IfNull> 



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

<!-- this ccs codee should migrate to the individual .css files since the above code imports it from community -->
<style type="text/css">
 
 a          { font-family:verdana,arial,geneva,helvetica,sans-serif ; font-size: 10px; }
 a:hover    { text-decoration:underline; }
 a:active   { text-decoration:underline; color: #0000CC;}
 
 a.useradmin_nav       { color: #4D4E4F; text-decoration: none; }
 a.useradmin_leftnav   { color: #4D4E4F; text-decoration: none; }
 
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
<script language="Javascript1.1">
 <%-- 
  // use this script anywhere there is a need to disable the enter key causing the submission of a dsp form 
  // also in the text input field of the form in question include...
  //       onkeypress="return blockIE(event.keyCode)"
  // as an attribute for IE
 --%>
 function blockNet(e) {
  if( e.which == 13 ) {return false;} else { return true; }
  // var keyChar = String.fromCharCode(e.which);
 }
 function blockIE(keyCode) {
  if( keyCode == "13" ) { return false;} else {  return true; }
 }
 if(navigator.appVersion.indexOf("MSIE") ==  -1) {
  document.captureEvents(Event.KEYPRESS);
  document.onkeypress = blockNet;
 }
</script>

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
    // without a page id change default to listing pages
    String mode = (pageId != null) ?  "2":"1";
    if ( request.getParameter("mode") != null)  mode = request.getParameter("mode");
%>
</head>
<body bgcolor="#ffffff" text="#333333" link="#3366ff" vlink="#3366ff" marginheight="0" marginwidth="0" leftmargin="0" rightmargin="0" topmargin="0" >

<div class="user_admin_header_bg_img" ><img src="<%=clearGif%>" height="61" width="680" border="0" alt="" /></div>

<core:CreateUrl id="currentPagesURL" url="./user.jsp">
  <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>

  
<table border="0" cellpadding="8" cellspacing="0" width="100%">
<tr>
<td width="50%" nowrap align="left"><font class="user_admin_header_community_name" color="#<%=pageTextColor%>">&nbsp;&nbsp;

<i18n:message key="nav-header-title-param">
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
</div><!-- header nav -->
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
	<td  colspan="2" bgcolor="#B7B8B7">
	<img src='images/clear.gif' height="1" width="1" alt=""><br>
	</td>
</tr>
<tr>
	<td  colspan="2" bgcolor="#677886">
	<img src='images/clear.gif' height="1" width="1" alt=""><br>
	</td>
</tr>
</table>


<br>

<!-- end header nav -->
<!-- end header     -->
<table width="98%" border="0" cellpadding="0" cellspacing="0"><%-- 5 coloumns  w/3 spacer coloumns--%>

<tr>
<!-- spacer column -->
<td valign="top" width="20"><img src="<%=clearGif%>" width="20" height"1" border="0"></td>
<!-- side bar links -->
<td width="150" valign="top"><font class="smaller">
<table border="0" cellpadding="0" cellspacing="0">
<tr><td bgcolor="#000000"><img src='images/clear.gif' height="1" width="150" alt=""></td></tr>
<tr><td bgcolor="#EAEEF0"><img src='images/clear.gif' height="3" width="150" alt=""></td></tr>
<tr><td bgcolor="#EAEEF0">
<table border="0" cellpadding="2" cellspacing="0" width="100%">

<core:CreateUrl id="urlmode6" url="./user.jsp">
  <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
  <core:urlParam param="mode" value="6"/>
   <%
   String newPageLink = ( userAdminEnv.getSourceCommunity().isAllowPersonalizedCommunities() ) ?
    "<a href='"+ portalServletResponse.encodePortalURL(urlmode6.getNewUrl()) + "' class='useradmin_leftnav'>"+i18n_newpage+"</a><br>" : "";
   String newPageLinkSelected = ( userAdminEnv.getSourceCommunity().isAllowPersonalizedCommunities() ) ? 
    "<a href='"+ portalServletResponse.encodePortalURL(urlmode6.getNewUrl()) + "' class='useradmin_leftnav' style='text-decoration:none;color:#000000;'><b>"+i18n_newpage+"</b></a><br>" : "";
   %>
  <core:CreateUrl id="urlmode1" url="./user.jsp">
    <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
    <core:urlParam param="mode" value="1"/>  
  <core:CreateUrl id="urlmode7" url="./user.jsp">
    <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
    <core:urlParam param="mode" value="7"/> 
 <core:Switch value="<%=mode%>">
   <core:Case value="1">
   <tr><td><a href='<%= portalServletResponse.encodePortalURL(urlmode1.getNewUrl())%>' class='useradmin_leftnav' style='text-decoration:none;color:#000000;'><b><i18n:message key="user_pages_nav_current"/></b></a></td></tr>
   <tr><td><%=newPageLink%></td></tr>
   <tr><td><a href='<%= portalServletResponse.encodePortalURL(urlmode7.getNewUrl())%>' class='useradmin_leftnav'><i18n:message key="user_pages_nav_about"/></a></td></tr>
  </core:Case>
  <core:Case value="6">
   <tr><td><a href='<%= portalServletResponse.encodePortalURL(urlmode1.getNewUrl())%>' class='useradmin_leftnav'><i18n:message key="user_pages_nav_current"/></a></td></tr>
   <tr><td><%=newPageLinkSelected%></td></tr>
   <tr><td><a href='<%= portalServletResponse.encodePortalURL(urlmode7.getNewUrl())%>' class='useradmin_leftnav'><i18n:message key="user_pages_nav_about"/></a></td></tr>
  </core:Case>
  <core:Case value="7">
   <tr><td><a href='<%= portalServletResponse.encodePortalURL(urlmode1.getNewUrl())%>' class='useradmin_leftnav'><i18n:message key="user_pages_nav_current"/></a></td></tr>
   <tr><td><%=newPageLink%></td></tr>
  <tr><td> <a href='<%= portalServletResponse.encodePortalURL(urlmode7.getNewUrl())%>' class='useradmin_leftnav' style='text-decoration:none;color:#000000;'><b><i18n:message key="user_pages_nav_about"/></b></a></td></tr>
  </core:Case>
  <core:DefaultCase>
   <tr><td><a href='<%= portalServletResponse.encodePortalURL(urlmode1.getNewUrl())%>' class='useradmin_leftnav'><b><i18n:message key="user_pages_nav_current"/><b></a></td></tr>
   <tr><td><%=newPageLink%></td></tr>
   <tr><td><a href='<%= portalServletResponse.encodePortalURL(urlmode7.getNewUrl())%>' class='useradmin_leftnav'><i18n:message key="user_pages_nav_about"/></a></td></tr>
  </core:DefaultCase>  
 </core:Switch>
   </core:CreateUrl>
  </core:CreateUrl>
  </core:CreateUrl>
</table>


</font></td></tr>
<tr><td bgcolor="#EAEEF0"><img src='images/clear.gif' height="3" width="150" alt=""></td></tr>
<tr><td bgcolor="#000000"><img src='images/clear.gif' height="1" width="150" alt=""></td></tr>
</table>

</td>
<!-- end side bar links -->

<!-- spacer column -->
<td width="20"><img src="<%=clearGif%>" width="20" height"1" border="0"></td>
<td valign="top" width="90%"><font class="smaller">

<%@ include file="fragments/form_messages.jspf"%>

<dsp:importbean bean="/atg/portal/admin/PageFormHandler"/>
 
<!-- context includes   -->

 <core:Switch value="<%=mode%>">

  <core:Case value="1"><%-- page list with delete and edit links --%>
 <%
   boolean hasMoreThanOne = false;
   boolean isInOrigList = false;
   String deleteLink = "";  
   String editLink   = "";  
   atg.portal.framework.Page[] allPages = userAdminEnv.getPages();
   hasMoreThanOne = ( allPages.length > 1 );
  %>

<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td><table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td><font class="pageheader">
<i18n:message key="user_pages_current_header"/>
</font></td></tr></table></td></tr></table>
	
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
<font class="smaller">
    <i18n:message key="user_pages_current_helpertext"/></font>
</td></tr></table>
<img src="<%=clearGif%>" width="1" height"1" border="0"><br>
  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#D7DEE5" width="100%"><tr><td>
  
    <table border="0" cellpadding="2" cellspacing="0">
    <core:ForEach id="childpages"
                          values="<%= userAdminEnv.getPages() %>"
                          castClass="atg.portal.framework.Page"
                          elementId="childPage">
<%
  portalContext.setPage(childPage);
%>
      <core:CreateUrl id="basePageURL" url="user.jsp">
       <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
       <tr>
        <td NOWRAP><font class="smaller"><%= childPage.getName() %>&nbsp;&nbsp;</font></td>
<i18n:message id="i18n_link_revert" key="user_pages_current_link_revert"/>
<i18n:message id="i18n_link_edit" key="user_pages_current_link_edit"/>
<i18n:message id="i18n_link_delete" key="user_pages_current_link_delete"/>
<i18n:message id="i18n_link_visit" key="user_pages_current_link_visit"/>
  <core:CreateUrl id="urlmode2" url="./user.jsp">
    <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
    <core:urlParam param="mode" value="2"/> 
  <core:CreateUrl id="urlmode8" url="./user.jsp">
      <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
      <core:urlParam param="mode" value="8"/> 
  <core:CreateUrl id="urlmode9" url="./user.jsp">
      <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url%>"/>
      <core:urlParam param="mode" value="9"/> 
 <%
   editLink = "locked";
   deleteLink = "locked";
   if(!childPage.isFixed() || childPage.isPersonalizedPage())
     editLink = "<a href='"+portalServletResponse.encodePortalURL(urlmode2.getNewUrl(),portalContext) + "'>"+i18n_link_edit+"</a>";
   deleteLink = "<!--leave blank if cannot delete-->";
   if (childPage.isPersonalizedPage()){
     if(!childPage.isUserCreated()) deleteLink = "<a href='"+portalServletResponse.encodePortalURL(urlmode9.getNewUrl(),portalContext) +"'>"+i18n_link_revert+"</a>";
     else deleteLink = "<a href='"+portalServletResponse.encodePortalURL(urlmode8.getNewUrl(),portalContext) +"'>"+i18n_link_delete+"</a>";
   }
 %>
   <td NOWRAP><font class="smaller">&nbsp;<%=editLink%>&nbsp;</font></td>
   <td NOWRAP><font class="smaller">&nbsp;<a href="<%= userAdminEnv.getPageURI(childPage) %>"><%=i18n_link_visit%></a>&nbsp;</font></td>
   <td NOWRAP><font class="smaller">&nbsp;<%=deleteLink%>&nbsp;</font></td>
   </tr>
   </core:CreateUrl><%-- mode 9  --%>  
   </core:CreateUrl><%-- mode 8  --%>  
   </core:CreateUrl><%-- mode 2  --%>

      </core:CreateUrl>
    </core:ForEach>

    
  </table>
  
  </td></tr></table>
  </core:Case>
  <core:Case value="2"><%-- edit page basics , requires paf_page_id --%>
    <dsp:include page="user_page_basic.jsp" flush="false"/>
  </core:Case>
  <core:Case value="3"><%-- edit page gears  , requires paf_page_id --%>
    <dsp:include page="user_page_gears.jsp" flush="false"/>
  </core:Case>
  <core:Case value="4"><%-- edit page layout , requires paf_page_id --%>
    <dsp:include page="user_page_layout.jsp" flush="false"/>
  </core:Case>
  <core:Case value="5"><%-- edit page color  , requires paf_page_id --%>
    <dsp:include page="user_page_color.jsp" flush="false"/>
  </core:Case>
  <core:Case value="6"><%-- Create new page --%>
    <dsp:include page="user_page_new.jsp" flush="false"/>
  </core:Case>
  <core:Case value="7"><%-- About pages --%>
       	<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
<font class="pageheader">
<i18n:message key="user_pages_about_header"/>
	</td></tr></table>
    </td></tr></table>
	
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
<font class="smaller">
    <i18n:message key="user_pages_about_helpertext"/>
</td></tr></table>
  </core:Case>
  <core:Case value="8">
    <dsp:include page="user_page_delete.jsp" flush="false"/>
  </core:Case>
  <core:Case value="9">
    <dsp:include page="user_page_revert.jsp" flush="false"/>
  </core:Case>
 </core:Switch>





<!-- end context includes -->
</td>
<!-- spacer column -->
<td width="15"><img src="<%=clearGif%>" width="15" height"1" border="0"></td>
</tr>
</table>


</core:CreateUrl><%-- currentPages --%>

</body>
</html>
</dsp:getvalueof><%-- id="dsp_page_url"     --%>

</admin:InitializeUserAdminEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/user.jsp#2 $$Change: 651448 $--%>
