<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ page import="java.io.*,java.util.*,java.io.IOException" %>

<paf:setFrameworkLocale />

<dsp:page>
<paf:hasCommunityRole roles="leader,gear-manager">

<admin:InitializeAdminEnvironment id="adminEnv">

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:importbean bean="/atg/portal/admin/GearFormHandler"/>


<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">
<dsp:getvalueof id="dsp_page_id"      idtype="java.lang.String" param="paf_page_id">
<dsp:getvalueof id="dsp_page_url"     idtype="java.lang.String" param="paf_page_url">
<dsp:getvalueof id="dsp_gear_id"      idtype="java.lang.String" param="paf_gear_id">


 <dsp:setvalue  bean="GearFormHandler.gearId" value="<%=dsp_gear_id%>" />
 <dsp:getvalueof id="gearItem" idtype="atg.portal.framework.Gear" bean="GearFormHandler.gear">
 <dsp:getvalueof id="gearName" bean="GearFormHandler.name">

 <i18n:message key="imageroot" id="i18n_imageroot"/>

 <dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<%
 
    atg.servlet.DynamoHttpServletRequest dynamoRequest=atg.servlet.ServletUtil.getDynamoRequest(request);
    String dsp_paf_community_id = dynamoRequest.getParameter("paf_community_id"); 

    String clearGif =  response.encodeURL("images/clear.gif");
    String mode          = "1";
    String outputStr     = "";

    if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    } else {
       mode = "15"; // default to gears display page
    }

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

<title><i18n:message key="title-community_gears"/></title>


<dsp:include page="nav_header_main.jsp" flush="false">
  <dsp:param  name="mode"             value="<%=mode%>" /> 
  <dsp:param  name="thisNavHighlight" value="gears" /> 
</dsp:include>



<br><center>
<table width="98%" border="0" cellpadding="0" cellspacing="0">
<tr>

<td width="10"><img src='<%=clearGif %>' height="1" width="10"  alt="" /></td>
<td width="150" valign="top"><font class="smaller">

 <dsp:include page="nav_sidebar.jsp" flush="false">
  <dsp:param  name="mode" value="1" /> 
  <dsp:param  name="sideBarIndex" value="3" /> 
 </dsp:include>

</td>
<td width="10"><img src='<%=clearGif %>' height="1" width="10"  alt="" /></td>

<td valign="top" width="80%" align="left">

<%@ include file="fragments/form_messages.jspf"%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="GearFormHandler.formError"/>
  <dsp:oparam name="true">
    <dsp:setvalue bean="GearFormHandler.resetFormExceptions"/>
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:setvalue bean="GearFormHandler.reset"/>
  </dsp:oparam>
</dsp:droplet>



<%
  String divider = "<font class='smaller'>&nbsp;&nbsp;|&nbsp;&nbsp;</font>";
  String callingPage = "additional"; 
%>
   <%@include file="fragments/community_gears_configure_nav.jspf" %>

  <% request.setAttribute("atg.paf.Gear", dsp_gear_id ); 
     request.setAttribute("atg.paf.Page", (String) request.getParameter("paf_page_id") ); 
     request.setAttribute("atg.paf.Community", (String) request.getParameter("paf_community_id") ); 
  %>

  <paf:InitializeGearEnvironment id="gearEnv">
    <paf:PrepareGearRenderers id="gearRenderers">

      <paf:PrepareGearRenderer gearRenderers="<%= gearRenderers.getGearRenderers() %>"
                               gear="<%= gearEnv.getGear() %>"
                               displayMode='full'
                               gearMode='instanceConfig' />
      <% if(atg.servlet.ServletUtil.isWebSphere() || atg.servlet.ServletUtil.isGenericJ2EEServer()) out.flush(); %>
      <paf:RenderPreparedGear gear="<%= gearEnv.getGear() %>"
                              gearRenderers="<%= gearRenderers.getGearRenderers() %>" />

    </paf:PrepareGearRenderers>
  </paf:InitializeGearEnvironment>
<%--
 <dsp:include page="community_gear_render.jsp" flush="false">
  <dsp:param  name="paf_dm" value="shared"  /> 
  <dsp:param  name="paf_gm" value="content" /> 
  <dsp:param  name="paf_gear_id" value="<%=dsp_gear_id%>" /> 
  <dsp:param  name="paf_community_id" value="<%=dsp_community_id%>" /> 
 </dsp:include>
--%>
</dsp:getvalueof>
</dsp:getvalueof>
</dsp:getvalueof>
</dsp:getvalueof>

</dsp:getvalueof>
</dsp:getvalueof>
</td></tr></table>
</body>
</html>
</admin:InitializeAdminEnvironment>
</paf:hasCommunityRole>

<paf:hasCommunityRole roles="leader,settings-manager,page-manager,gear-manager,user-manager" barrier="true"/>

</dsp:page>

<%
String redirectUrl = 
  (String) request.getAttribute(atg.taglib.core.RedirectTag.CORE_REDIRECT_URL_NAME);
if(redirectUrl != null) {
try {
response.sendRedirect(redirectUrl);
}
catch(IOException e) {
}
}
%>

<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_gears_configure_additional.jsp#2 $$Change: 651448 $--%>

