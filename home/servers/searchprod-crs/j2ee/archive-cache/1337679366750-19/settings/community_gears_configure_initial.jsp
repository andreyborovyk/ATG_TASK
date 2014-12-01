<%@page import="java.io.*,java.util.*"%>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<dsp:page>
<paf:hasCommunityRole roles="leader,gear-manager">

<admin:InitializeAdminEnvironment id="adminEnv">

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:importbean bean="/atg/portal/admin/GearFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">
<dsp:getvalueof id="dsp_page_id"      idtype="java.lang.String" param="paf_page_id">
<dsp:getvalueof id="dsp_page_url"     idtype="java.lang.String" param="paf_page_url">
<dsp:getvalueof id="dsp_gear_id"      idtype="java.lang.String" param="paf_gear_id">
<dsp:getvalueof id="paf_preDirect"    idtype="java.lang.String" param="paf_preDirect">

 <dsp:setvalue  bean="GearFormHandler.gearId" value="<%=dsp_gear_id%>" />
 <dsp:getvalueof id="gearItem" idtype="atg.portal.framework.Gear" bean="GearFormHandler.gear">
 <dsp:getvalueof id="gearName" bean="GearFormHandler.name">
 <%
  if ( gearItem != null && dsp_gear_id == null )  dsp_gear_id = (String) gearItem.getId();
 %>
 <core:ExclusiveIf>
  <core:If value="<%=paf_preDirect%>">
   <core:CreateUrl id="withGearIdUrl" url="community_gears_configure_initial.jsp" >
        <core:UrlParam param="mode" value="2"/>
        <core:UrlParam param="paf_page_id"       value="<%=dsp_page_id  %>"/>
        <core:UrlParam param="paf_gear_id"       value="<%=dsp_gear_id  %>"/>
        <paf:encodeUrlParam param="paf_page_url" value="<%=dsp_page_url  %>"/>
        <core:UrlParam param="paf_community_id"  value="<%=dsp_community_id  %>"/>
           <dsp:droplet name="/atg/dynamo/droplet/Redirect">
            <dsp:param name="url" value="<%=withGearIdUrl.getNewUrl()%>"/>
          </dsp:droplet>
   </core:CreateUrl>
  </core:If>
  <core:DefaultCase>
    <dsp:setvalue  bean="GearFormHandler.gearId" value="<%= dsp_gear_id %>"/>
  </core:DefaultCase>
 </core:ExclusiveIf>

<%
// System.out.print("\n\n\tpaf_preDirect="+paf_preDirect+" \t\tdsp_gear_id="+dsp_gear_id+"\t\tgearItem="+gearItem+"\n");
%>

 <i18n:message key="imageroot" id="i18n_imageroot"/>


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


<core:CreateUrl id="EgearsURL"      url="/portal/settings/community_gears.jsp">
  <paf:encodeUrlParam param="paf_page_url"  value='<%=request.getParameter("paf_page_url")%>'/>
  <paf:encodeUrlParam param="paf_page_id"  value='<%=request.getParameter("paf_page_id")%>'/>
  <core:UrlParam param="paf_community_id"  value='<%=request.getParameter("paf_community_id")%>'/>
  <core:UrlParam param="paf_gear_id"  value='<%=request.getParameter("paf_gear_id")%>'/>


  
<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="pageheader_edit">
<img src='<%= response.encodeURL("images/write.gif")%>' height="15" width="28" alt="" border="0"> 
<i18n:message key="community_pages_gears_initial_title" >Initial Coniguration</i18n:message>&nbsp;:&nbsp;<%=gearName%></font>
</td></tr></table>
</td></tr></table>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#96C1DF" width="98%">
<tr>
<td NOWRAP><nobr><font class="smaller">&nbsp;&nbsp;<i18n:message key="community_pages_gears_initial_helper">use the form below to complete the initial configuration for this gear</i18n:message></font></td>
</tr>
</table>

<img src="<%= response.encodeURL("images/clear.gif")%>" height="1" width="1" border="0" alt="" /><br />

</core:CreateUrl>


  <% request.setAttribute("atg.paf.Gear", dsp_gear_id ); 
     request.setAttribute("atg.paf.Page", (String) request.getParameter("paf_page_id") ); 
     request.setAttribute("atg.paf.Community", (String) request.getParameter("paf_community_id") ); 
  %>

  <paf:InitializeGearEnvironment id="gearEnv">
    <paf:PrepareGearRenderers id="gearRenderers">

      <paf:PrepareGearRenderer gearRenderers="<%= gearRenderers.getGearRenderers() %>"
                               gear="<%= gearEnv.getGear() %>"
                               displayMode='full'
                               gearMode='initialConfig' />
      <paf:RenderPreparedGear gear="<%= gearEnv.getGear() %>"
                              gearRenderers="<%= gearRenderers.getGearRenderers() %>" />

    </paf:PrepareGearRenderers>
  </paf:InitializeGearEnvironment>


</dsp:getvalueof>
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

<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/community_gears_configure_initial.jsp#2 $$Change: 651448 $--%>

