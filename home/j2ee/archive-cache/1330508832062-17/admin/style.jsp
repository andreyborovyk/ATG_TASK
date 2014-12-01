<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<paf:setFrameworkLocale />

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:importbean bean="/atg/portal/admin/RegisterStyle"/>
<dsp:importbean bean="/atg/portal/admin/RegisterPageLayout"/>
<dsp:importbean bean="/atg/portal/admin/RegisterPageTemplate"/>
<dsp:importbean bean="/atg/portal/admin/RegisterColor"/>
<dsp:importbean bean="/atg/portal/admin/RegisterGearTemplate"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

<i18n:message key="imageroot" id="i18n_imageroot"/>

<html>
<head>
<title><i18n:message key="title-admin-style"/></title>
<link rel="STYLESHEET" type="text/css" href="css/default.css" src="css/default.css">
</head>

<%
    String clearGif =  response.encodeURL("images/clear.gif");
    String thisNavHighlight     = "style";  // used for header_main
    String mode          = "1";          // used for sub page high light
    String currPid       = "2";          // used for sidebar sub selection
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


<core:demarcateTransaction id="demarcateXAouter">
<% try { %>

<%@ include file="form_messages.jspf"%>

<core:Switch value="<%= mode%>">

  <core:Case value="2">
  <%@ include file="style_layout_templates.jspf" %> 
  </core:Case>

  <core:Case value="3">
   <%@ include file="style_gear_title_template.jspf" %>
  </core:Case>

  <core:Case value="4">
   <%@ include file="style_color_palette.jspf" %>
  </core:Case>

  <core:Case value="5">
   <%@ include file="style_stylesheet.jspf" %>
  </core:Case>

  <core:Case value="6"><!--about styles-->
  <font class="small" >
    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
	<font class="pageheader"><i18n:message key="admin-styles-about"/>
	</td></tr></table>
	</td></tr></table>
	
	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
	<font class="smaller">
    <i18n:message key="admin-helpertext-styleA"/><br><br>
    <i18n:message key="admin-helpertext-styleB"/><br><br>
    <i18n:message key="admin-helpertext-styleC"/><br>
   </font>
   </td></tr></table>
  </core:Case>

  <core:DefaultCase>
   <%@ include file="style_page_template.jspf" %>
  </core:DefaultCase>

 </core:Switch>

</td></tr></table>

<% } catch (Exception e) { %>

  <core:setTransactionRollbackOnly id="rollbackOnlyXAouter">
    <core:ifNot value="<%= rollbackOnlyXAouter.isSuccess() %>">
      The following exception was thrown:
      <pre>
       <%= rollbackOnlyXAouter.getException() %>
      </pre>
    </core:ifNot>
  </core:setTransactionRollbackOnly>
<% } %>

</core:demarcateTransaction>

</body>
</html>



</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/style.jsp#2 $$Change: 651448 $--%>
