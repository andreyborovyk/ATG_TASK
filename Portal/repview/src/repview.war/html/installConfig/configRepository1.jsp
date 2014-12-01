<%--
   Repository View Gear
   gearmode = installConfig 
   displaymode = full
  
   This page fragment is included by installConfig.jsp.
   This form allows the portal administrator to select a 
   repository view in this gear.
   Navigate to here from portal administrator
   UI (/portal/admin) then click on "gears" and then on 
   repository view "configure" link.  
--%>

<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/rpv" prefix="rpv" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/core-taglib" prefix="core" %>

<dsp:page>
<dsp:importbean bean="/atg/portal/gear/repview/RepViewConfigFormHandler"/>

<%-- 
  Set the GEAR DEFINITION ID so that the InitializeGearEnvironment 
  tag will work.  This is required on all installConfig pages. 
 --%>   
<% request.setAttribute(atg.portal.framework.RequestAttributes.GEAR_DEFINITION, (String) request.getParameter("paf_gear_def_id") ); %>

<%-- Required initialization for all RepView pages --%>
<paf:InitializeGearEnvironment id="pafEnv">
<rpv:repViewPage id="rpvpage" gearEnv="<%= pafEnv %>">

<%-- get all internationalized strings that will be used on this page --%> 
<i18n:bundle baseName="atg.portal.gear.repview.RepViewConfigResources" changeResponseLocale="false" />
<i18n:message id="configureRepositoryMainHeading" key="configureRepositoryMainHeading"/>
<i18n:message id="separator" key="labelFieldSeparator"/>
<i18n:message id="repositoryLabel" key="repositoryLabel"/>
<i18n:message id="repositoryHelpText" key="repositoryHelpText"/>
<i18n:message id="continueButton" key="continueButton"/>

<%
   String installConfigURI = rpvpage.getRelativeUrl("/html/installConfig/installConfig.jsp");
   String clearImgURI = rpvpage.getRelativeUrl("/images/clear.gif");
   String writeImgURI = rpvpage.getRelativeUrl("/images/write.gif");
%>


<!-- begin form -->
<dsp:form method="post" action="<%= installConfigURI %>">

  <core:CreateUrl id="failureUrl" url="<%= installConfigURI %>">
    <core:UrlParam param="config_page" value="Repository2"/>
    <core:UrlParam param="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.failureUrl" value="<%= failureUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <core:CreateUrl id="successUrl" url="<%= installConfigURI %>">
    <core:UrlParam param="config_page" value="Repository2"/>
    <core:UrlParam param="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <dsp:setvalue bean="RepViewConfigFormHandler.settingDefaultValues" value="true"/>
  <dsp:setvalue bean="RepViewConfigFormHandler.paramType" value="instance"/>
  <dsp:setvalue bean="RepViewConfigFormHandler.paramNames" value="repositoryPath itemDescriptorName"/>

  <dsp:input type="hidden" bean="RepViewConfigFormHandler.settingDefaultValues" value="true"/>
  <dsp:input type="hidden" bean="RepViewConfigFormHandler.paramType" value="instance"/>
   <dsp:input type="hidden" bean="RepViewConfigFormHandler.mode" value="repository"/>

  <input type="hidden" name="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="config_page" value="Repository2"/>

  
      <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
        <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
        <font class="pageheader_edit"><img src="<%= writeImgURI %>" height="15" width="28" alt="" border="0"><%= configureRepositoryMainHeading %>
        </td></tr></table>
        </td></tr></table>
        
        <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
        <font class="smaller"><%= repositoryHelpText %>
        </td></tr></table>
		<img src="<%= clearImgURI %>" height=1 width=1 border=0><br>
  
 <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%">
        <TR>
      <TD colspan=2 VALIGN="top" ALIGN="left">
        <img src="<%= clearImgURI %>" height=2 width=1 border=0></TD>
    </TR>
    <TR> 
      <TD WIDTH="10%" align="left" valign="top">
        <font class="smaller"><%= repositoryLabel %><%= separator %>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="90%" align="left" valign="top"><font class="small">
        <dsp:select bean="RepViewConfigFormHandler.values.repositoryPath">
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" bean="/atg/registry/ContentRepositories.initialRepositories"/>
            <dsp:oparam name="output">
              <dsp:getvalueof id="repComponent" idtype="atg.nucleus.GenericService" param="element">
              <dsp:droplet name="/atg/dynamo/droplet/Switch">
                <dsp:param name="value" value="<%= repComponent.getAbsoluteName() %>"/>
                <dsp:oparam name='<%= pafEnv.getGearInstanceDefaultValue("repositoryPath") %>'>
                  <dsp:option value="<%= repComponent.getAbsoluteName() %>" selected="<%=true%>"/>
                  <%= repComponent.getAbsoluteName() %>
                </dsp:oparam>
                <dsp:oparam name="default">
                  <dsp:option value="<%= repComponent.getAbsoluteName() %>"/><%= repComponent.getAbsoluteName() %>
                </dsp:oparam>
              </dsp:droplet>
              </dsp:getvalueof>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:select></font>
      </TD>
    </TR>

    
    <TR> 
      <TD VALIGN="top" ALIGN="left">&nbsp;</TD>
      <TD VALIGN="top" ALIGN="left">
        <dsp:input type="submit" value="<%= continueButton %>" bean="RepViewConfigFormHandler.collectValues"/>
      </TD>
    </TR>
    <TR>
      <TD colspan=2><img src="<%= clearImgURI %>" height=10 width=1 border=0></TD>
    </TR>
  </TABLE>

</dsp:form>

</rpv:repViewPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/installConfig/configRepository1.jsp#2 $$Change: 651448 $--%>
