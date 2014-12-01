<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<dsp:page>

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />
   
<paf:PortalAdministratorBarrier/>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:importbean bean="/atg/portal/admin/RegisterGear"/>
<dsp:importbean bean="/atg/portal/admin/GearFolder"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

<i18n:message key="imageroot" id="i18n_imageroot"/>
   
<html>
<head>
<title><i18n:message key="title-admin-gears"/></title>
<script language="Javascript">
if (parent.frames.length > 0 ) {
  parent.document.location.href = document.location.href;
}
</script>
<link rel="STYLESHEET" type="text/css" href="css/default.css" src="css/default.css">
</head>
   
<dsp:param name="thisNavHighlight" value="gear"/>
   
<% String thisNavHighlight = "gear"; %>

<%
    String clearGif =  response.encodeURL("images/clear.gif");
    thisNavHighlight     = "gears";  // used for header_main
    String mode          = "1";          // used for sub page high light
    String currPid       = "1";          // used for sidebar sub selection
                                         // this copies an index array to a rendering array 
                                         // that provides the side links
    String outputStr     = "";           // the output for header and sidebar includes

    if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    } else {
       mode = "1"; // default to available gears
    }
    String greetingName = "";
%>
<%@ include file="nav_header_main.jspf"%>


<br>
<table width="98%" border="0" cellpadding="0" cellspacing="0">
<tr>
<td valign="top" width="20"><img src='<%=clearGif %>' height="1" width="20" ></td>

<td width="150" valign="top"><font class="smaller">
 <%@ include file="nav_sidebar.jspf"%><br></td>

<td width="20"><img src='<%=clearGif %>' height="1" width="20" ></td>
<td valign="top" width="90%" align="left">

<%@ include file="form_messages.jspf"%>

<%--    
   <dsp:a href="gear_new.jsp"><i18n:message key="admin-gear-new"/></dsp:a>
--%>



 <core:Switch value="<%= mode%>">

  <core:Case value="2">
   <%@ include file="gear_new.jspf" %>
  </core:Case>

  <core:Case value="3">
   <%@ include file="gear_new_folder.jspf" %>
  </core:Case>

  <core:Case value="4"><!--about gears-->
  <font class="small" >
    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
	<font class="pageheader"><i18n:message key="admin-gear-about"/>
	</td></tr></table>
	</td></tr></table>
	
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
	<font class="smaller">
   <i18n:message key="admin-helpertext-gearsA"/><br><br>
   <i18n:message key="admin-helpertext-gearsB"/><br><br>
   <i18n:message key="admin-helpertext-gearsC"/>
   
    </font>
   </td></tr></table><br>
  </core:Case>


  <core:Case value="5">
   <%@ include file="gear_delete_confirm.jspf" %> 
  </core:Case> 


  <core:DefaultCase>

   <%@include file="community_gears_fragment_listing.jspf"%>

  </core:DefaultCase>
 </core:Switch>
<!--end table generating list-->

</td></tr></table>
<!--end table bounding list-->


</td></tr></table>

   </body>
   </html>


   </dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/gear.jsp#2 $$Change: 651448 $--%>
