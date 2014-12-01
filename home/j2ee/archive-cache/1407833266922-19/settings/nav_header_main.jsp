<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*" errorPage="/error.jsp"%>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>

<dsp:page>

<admin:InitializeAdminEnvironment id="adminEnv">

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<dsp:getvalueof id="thisNavHighlight" param="thisNavHighlight">
<dsp:getvalueof id="mode"             param="mode">
<dsp:getvalueof id="dsp_page_id"      idtype="java.lang.String"      param="paf_page_id">
<dsp:getvalueof id="dsp_gear_id"      idtype="java.lang.String"      param="paf_gear_id">
<dsp:getvalueof id="dsp_page_url"     idtype="java.lang.String"      param="paf_page_url">
<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">

<%
 Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);
 String communityId = null;
 if(cm != null) {
     communityId = cm.getId();
 }
 if ( dsp_community_id == null ) dsp_community_id = communityId;

 String clearGif =  response.encodeURL("images/clear.gif");
 atg.portal.framework.Page currPage= adminEnv.getCommunity().getDefaultPage();
 String gearTitleBackgroundColor = currPage.getColorPalette().getGearTitleBackgroundColor() ;
 String gearTitleTextColor =  currPage.getColorPalette().getGearTitleTextColor();
 String CsettingsURLStr = null;
 String CusersURLStr = null;
 String CpagesURLStr = null;
 String CgearsURLStr = null;

  //Request/Response
  PortalServletResponse portalServletResponse = 
     (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
%>

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<i18n:message id="i18navLabelSettings"        key="nav-header-link-community-settings"/>
<i18n:message id="i18navLabelGears"           key="nav-header-link-community-gears"/>
<i18n:message id="i18navLabelPages"           key="nav-header-link-community-pages"/>
<i18n:message id="i18navLabelUsers"           key="nav-header-link-community-users"/>

<core:CreateUrl id="CsettingsURL"   url="/portal/settings/community_settings.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id"  value='<%= communityId %>'/>
    <% CsettingsURLStr = CsettingsURL.getNewUrl();%>
</core:CreateUrl>
    
<core:CreateUrl id="CusersURL"      url="/portal/settings/community_users.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id"  value='<%= communityId %>'/>
    <% CusersURLStr = CusersURL.getNewUrl();%>
</core:CreateUrl>

<core:CreateUrl id="CpagesURL"      url="/portal/settings/community_pages.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id"  value='<%= communityId %>'/>
    <% CpagesURLStr = CpagesURL.getNewUrl();%>
</core:CreateUrl>
  
<core:CreateUrl id="CgearsURL"      url="/portal/settings/community_gears.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <core:UrlParam param="paf_community_id"  value='<%= communityId %>'/>
    <% CgearsURLStr = CgearsURL.getNewUrl();%>
</core:CreateUrl>

<%
  String topLevel[][] = {
     {i18navLabelSettings ,"1",CsettingsURLStr ,"settings", "settings-manager", "hiddenSettings"},
     {i18navLabelUsers    ,"2",CusersURLStr    ,"users",    "user-manager",     "hiddenUsers"},
     {i18navLabelPages    ,"3",CpagesURLStr    ,"pages",    "page-manager",     "hiddenPages"},
     {i18navLabelGears    ,"4",CgearsURLStr    ,"gears",    "gear-manager",     "hiddenGears"}
     };

%>




<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
<META NAME="GENERATOR" CONTENT="Dynamo/6 [en]">
   
<script type="text/javascript" language="Javascript">

  var ATGAgent = window.navigator.userAgent;
  function IsIE() { return ATGAgent.indexOf("MSIE") > 0;}
  function blockNet(e) {
   if( e.which == 13 ) {return false;} else { return true; }
   // var keyChar = String.fromCharCode(e.which);
  }
  function blockIE(keyCode) {
   if( keyCode == "13" ) { return false;} else {  return true; }
  }
  if( ! IsIE() ) {
   document.captureEvents(Event.KEYPRESS);
   document.onkeypress = blockNet;
  }
  function portalPageInit() {
   var ww = ( IsIE() ) ?  document.body.offsetWidth-20  :  window.innerWidth-16  ;
   var wh = ( IsIE() ) ?  document.body.offsetHeight-20 :  window.innerHeight-16 ;
   var sw = ( IsIE() ) ?  screen.width                  :  screen.availWidth  ;
   var sh = ( IsIE() ) ?  screen.height                 :  screen.availHeight ;
  }

 </script>

 <style type="text/css">
  <!--

  a      { font-family:verdana,arial,geneva,helvetica,sans-serif ; font-size: 10px; }
  font   { font-family:verdana,arial,geneva,helvetica,sans-serif ; font-size: 10px; }


  .hiddenSideNav { font-family:verdana,arial,geneva,helvetica,sans-serif;
                 font-size: 10px; 
                 color: #333333;
                 line-height:1.2em;
                 text-decoration:none;
               }

  .hidden        {  
    padding:2px; bottommargin:0px;  text-transform: uppercase; 
               }

  .nav           {  
    padding-top: 1px;   padding-bottom: 1px;
    text-transform: uppercase; 
               }
  a:hover       { text-decoration:underline; }
  a:active      { text-decoration:underline; color: #0000CC;}

  .commadmin_nav       {  font-family:verdana,arial,geneva,helvetica,sans-serif ;color: #4D4E4F; text-decoration: none; }
  .commadmin_leftnav   {  font-family:verdana,arial,geneva,helvetica,sans-serif ;color: #4D4E4F; text-decoration: none; }
 
  a.breadcrumb  {  font-size: 10px; text-decoration:none; }
  a.gear_nav     { font-weight:700; text-transform:lowercase; text-decoration:none; }
  a.gear_content { font-weight:300 }
  a.admin_link { font-size: 10px; font-weight:300; text-decoration:none; }

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
  .info   {font-size:10px; color : 003300;  }
 
  .helpertext {font-size:14px; color : 333333}
  .adminbody  {font-size:10px; color : 333333}
  .subheader  {font-size:10px; color : 333333; font-weight:700}
  .pageheader {font-size:12px; color : FFFFFF; font-weight:700}
  .pageheader_edit {font-size:12px; color : #4D4E4F; font-weight:700}

-->
</style>


</head>

<body bgcolor="#ffffff" text="#333333" link="#3366ff" vlink="#3366ff" 
      onload="portalPageInit();"  
      marginheight="0" marginwidth="0" leftmargin="0" rightmargin="0" topmargin="0" > 


<table border="0" cellpadding="8" cellspacing="0" width="100%">
<tr>
<td width="50%" nowrap bgcolor="<%= "#" + gearTitleBackgroundColor%>" align="left"><font class="large_bold" color="<%= "#" + gearTitleTextColor%>">&nbsp;&nbsp;<i18n:message key="nav-header-title"/>&nbsp;<%= adminEnv.getCommunity().getName()%></font></td>

<td width="50%" bgcolor="<%= "#" + gearTitleBackgroundColor%>" align="right" valign="bottom"><font class="smaller" color="<%= "#" + gearTitleTextColor%>">&nbsp;&nbsp;&nbsp;

<%--  LINK TO PORTAL ADMIN --%>
<%
  if (adminEnv.isPortalAdministrator()){
%>
<core:CreateUrl id="adminURL" url="/portal/admin/">
 <dsp:a iclass="commadmin_nav" href="<%=adminURL.getNewUrl()%>" target="_top"><font color="<%= "#" + gearTitleTextColor%>"><i18n:message key="nav-header-link-portal-admin"/></font></dsp:a>&nbsp;<br><img src='<%=clearGif %>' width="1" height="3" alt=""><br>
</core:CreateUrl>
<% }%>


<%--  LINK TO Community --%>
<%
  if ( adminEnv.getCommunity().isEnabled()  ){
%>
<core:CreateUrl id="commURL" url="<%=  adminEnv.getCommunityURI(adminEnv.getCommunity().getId())%>">
<dsp:a iclass="commadmin_nav" href="<%=commURL.getNewUrl()%>" target="_top"><font color="<%= "#" + gearTitleTextColor%>"><i18n:message key="nav-header-link-go-to"/>&nbsp;<%= adminEnv.getCommunity().getName()%></font></dsp:a>&nbsp;
</core:CreateUrl>
<% } else { %>

<font color="<%= "#" + gearTitleTextColor%>">
<i18n:message key="nav-header-community-offline-param"> 
  <i18n:messageArg value="<%= adminEnv.getCommunity().getName()%>"/>
 </i18n:message>&nbsp;&nbsp;
</font>

<% } %>
</font>
</td>
</tr>
</table>


<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
 <td colspan="2" bgcolor="#B7B8B7"><img src='<%=clearGif %>' width="1" height="1" alt=""></td>
</tr>

<tr>
<td colspan="2" bgcolor="#EAEEF0" nowrap>

<table cellpadding="2" border="0" width="100%">
 <tr>
  <td NOWRAP>
<%--      admin links --%>
<%

 String tempOutSTR   = "<table border='0' cellpadding='0' cellspacing='0'>\n<tr>";
 String startTD = "\n<td NOWRAP><font>&nbsp;";
 String endTD   = "</font></td>\n";

 String nonbreakingspacer = "<td><font>&nbsp;&nbsp;<img src='"+
                             response.encodeUrl("images/separator.gif") +
                             "' width='1' height='11' alt='' border='0' />&nbsp;&nbsp;</font></td>";

 for ( int i = 0 ; i < topLevel.length ; i++ ) {
   // see if they have permission to see this link
   if ((cm == null) ||
       (!cm.isLeader(null) &&
        !cm.hasRole(topLevel[i][4])))
    continue;

   tempOutSTR += startTD;
   if (! topLevel[i][3].equals(thisNavHighlight) ) {
    tempOutSTR += "<a  class='commadmin_nav' target='_top' href='"+topLevel[i][2];
    tempOutSTR += "'>";
    tempOutSTR += topLevel[i][0].trim();
    tempOutSTR += "</a>";
   } else {
    tempOutSTR  += "<a  target='_top' class='commadmin_nav' style='text-decoration:none;color:#000000;' ";
    tempOutSTR  += "href='"+ topLevel[i][2];
    tempOutSTR  += "'><img src='";
    tempOutSTR += response.encodeUrl("images/selected.gif") +"' alt='' border='0' width='9' height='8'>&nbsp;<b>";
    tempOutSTR  += topLevel[i][0].trim();
    tempOutSTR  += "</b></a>";
   }
   tempOutSTR +=  endTD;
   tempOutSTR += nonbreakingspacer;
 }
 tempOutSTR += "</tr>\n</table>";
%>

<%=tempOutSTR %>

<%-- end admin links --%>


</td>
<td align="right" nowrap>

<font class="smaller">
<%
   String greetingName = "";
%>
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

 <i18n:message id="i18n_boldgn"     key="nav-header-bold"/>
 <i18n:message id="i18n_end_boldgn" key="nav-header-endbold"/>
 <i18n:message key="nav-header-logged-comp"> 
  <i18n:messageArg value="<%= greetingName%>"/>
  <i18n:messageArg value="<%=i18n_boldgn%>"/>
  <i18n:messageArg value="<%=i18n_end_boldgn%>"/>
 </i18n:message>
 
  &nbsp;&nbsp;<img src='<%=response.encodeUrl("images/separator.gif")%>' width="1" height="11" alt="" border="0">&nbsp;&nbsp;
 <%--  LINK TO LOG OUT --%>


   <a class='commadmin_nav' href="<%= portalServletResponse.encodePortalURL(adminEnv.getLogoutURI()) %>" target="_top"><i18n:message key="nav-header-link-logout"/>&nbsp;<img src="images/logout_arrow.gif" border="0" alt="" width="11" height="7"></a>&nbsp;&nbsp;


 </font>
 </td></tr>
</table>

</td></tr>
 
  <%-- two rows of background image to form border between top navigation and portal admin banner --%>
<tr>
 <td  colspan="2" bgcolor="#B7B8B7"><img src='<%=clearGif%>' height="1" width="1" alt=""></td>
</tr>
<tr>
  <td  colspan="2" bgcolor="#677886"><img src='<%=clearGif%>' height="1" width="1" alt=""></td>
</tr>	
</table>

</dsp:getvalueof><%-- --%>
</dsp:getvalueof><%-- --%>
</dsp:getvalueof><%-- --%>
</dsp:getvalueof><%-- --%>
</dsp:getvalueof><%-- mode            --%>
</dsp:getvalueof><%-- thisNavHighlight--%>

</admin:InitializeAdminEnvironment>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/nav_header_main.jsp#2 $$Change: 651448 $--%>
