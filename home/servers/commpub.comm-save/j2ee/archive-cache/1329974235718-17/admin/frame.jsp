

<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />
<paf:setFrameworkLocale />
<dsp:page>


<html>
<head>
<title><i18n:message key="title-community_frames"/></title>


<core:CreateUrl id="headerNavURL" url="/portal/admin/frame_header.jsp" >
 <core:UrlParam param="navHighLight" value='<%=request.getParameter("navHighLight")%>'/>

<core:CreateUrl id="sidebarNavURL" url="/portal/admin/frame_sidebar.jsp" >
 <core:UrlParam param="navHighLight" value='<%=request.getParameter("navHighLight")%>'/>
 <core:UrlParam param="currPid" value='<%=request.getParameter("currPid")%>'/>

<frameset rows='83,*' frameBorder=NO border=0 frameSpacing=0 framecolor=#ffffff>

<frame name=header src="<%=headerNavURL.getNewUrl() %>"  marginHeight=0 marginWidth=8 scrolling=no valign=top align=LEFT noResize>

<frameset cols='190,*' frameBorder=NO border=0 frameSpacing=0 framecolor=#ffffff>

<frame name=ContentSideNav  src='<%=sidebarNavURL.getNewUrl()%>' marginHeight=0 marginWidth=0 scrolling=auto valign=TOP align=LEFT noResize>
<% 
  String contentTarget = "";
  if (  request.getParameter("contentTarget") != null ) {
   contentTarget =  request.getParameter("contentTarget");
   if ( contentTarget.indexOf(";") ==  -1 ) {
%>
<core:CreateUrl id="content" url="frame_loading.jsp" >
 <paf:encodeUrlParam param="target" value='<%= contentTarget%>'/>
 <frame name=ContentMain src='<%=content.getNewUrl() %>'  marginHeight=0 marginWidth=0 scrolling=auto valign=TOP align=LEFT noResize>
</core:CreateUrl>
<% 
   } else {
%>
 <frame name=ContentMain src='<%= contentTarget %>'  marginHeight=0 marginWidth=0 scrolling=auto valign=TOP align=LEFT noResize>
<%
  }
 }
%>

</frameset>

 <noframes>
 <body bgcolor="#ffffff">
 <p>
 <blockquote>
 <i18n:message key="browser_warning"/>
 </blockquote>
 </p>
 </body>
 </noframes>

</frameset>
</core:CreateUrl>
</core:CreateUrl>




</head>
</html>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/frame.jsp#2 $$Change: 651448 $--%>
