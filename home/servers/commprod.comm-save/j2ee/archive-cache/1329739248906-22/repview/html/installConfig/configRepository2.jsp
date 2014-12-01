<%--
   Repository View Gear
   gearmode = installConfig 
   displaymode = full
  
   This page fragment is included by installConfig.jsp.
   It follows configRepository1.jsp.  This form allows 
   the portal administrator to select a repository item 
   to display in this gear. This value will be used to 
   limit the list of targeters from which the community
   leader may select in the instance config. 
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

<paf:InitializeGearEnvironment id="pafEnv">
<rpv:repViewPage id="rpvpage" gearEnv="<%= pafEnv %>">

<i18n:bundle baseName="atg.portal.gear.repview.RepViewConfigResources" changeResponseLocale="false" />
<i18n:message id="configureRepositoryMainHeading" key="configureRepositoryMainHeading"/>
<i18n:message id="repositoryLabel" key="repositoryLabel"/>
<i18n:message id="itemDescriptorNameLabel" key="itemDescriptorNameLabel"/>
<i18n:message id="itemDescriptorHelpText" key="itemDescriptorHelpText"/>
<i18n:message id="submitButton" key="submitButton"/>
<i18n:message id="separator" key="labelFieldSeparator"/>


<dsp:form method="post" action='<%= rpvpage.getRelativeUrl("/html/installConfig/installConfig.jsp")%>'>

  <core:CreateUrl id="failureUrl" url='<%= rpvpage.getRelativeUrl("/html/installConfig/installConfig.jsp") %>'>
    <core:UrlParam param="config_page" value="Repository1"/>
    <core:UrlParam param="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.failureUrl" value="<%= failureUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <core:CreateUrl id="successUrl" url='<%= rpvpage.getRelativeUrl("/html/installConfig/installConfig.jsp") %>'>
    <core:UrlParam param="config_page" value="Repository1"/>
    <core:UrlParam param="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl() %>"/>
  </core:CreateUrl>
	
  <dsp:input type="hidden" bean="RepViewConfigFormHandler.settingDefaultValues" value="true"/>
  <dsp:input type="hidden" bean="RepViewConfigFormHandler.paramType" value="instance"/>
  <dsp:input type="hidden" bean="RepViewConfigFormHandler.mode" value="repository"/>

  <input type="hidden" name="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="config_page" value="Repository1"/>

  
        <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
        <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
        <font class="pageheader_edit"><img src='<%= rpvpage.getRelativeUrl("/images/write.gif") %>' height="15" width="28" alt="" border="0"><%= configureRepositoryMainHeading %>
        </td></tr></table>
        </td></tr></table>
        
        <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
        <font class="smaller"><%= itemDescriptorHelpText %>
        </td></tr></table>
		<img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=1 width=1 border=0><br>
  
 <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%">
  
     <TR>
      <TD colspan=2 VALIGN="top" ALIGN="left">
        <img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=2 width=1 border=0></TD>
    </TR>


    <TR> 
      <TD WIDTH="15%" VALIGN="top" align="left" NOWRAP><font class="smaller">
        <%= repositoryLabel %><%= separator %> &nbsp;&nbsp;</font></TD>
      <TD WIDTH="85%" VALIGN="top" ALIGN="left"><font class="smaller">
        <dsp:valueof bean="RepViewConfigFormHandler.values.repositoryPath"><%= "unset" %></dsp:valueof>
      </font></TD>
    </TR>

    <TR>
      <TD colspan=2 VALIGN="top" ALIGN="left">
        <img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=2 width=1 border=0></TD>
    </TR>

    <TR> 
      <TD align="left" valign="top" NOWRAP><font class="smaller">
        <%= itemDescriptorNameLabel %><%= separator %>&nbsp;&nbsp;</font></TD>
      <TD align="left" valign="top"><font class="small">        
        <dsp:getvalueof id="reppath" bean="RepViewConfigFormHandler.values.repositoryPath" idtype="java.lang.String">
          <rpv:repositoryInfo id="repinfo" repositoryPath="<%= reppath %>">
            <dsp:select bean="RepViewConfigFormHandler.values.itemDescriptorName">
              <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                <dsp:param name="array" value="<%= repinfo.getItemDescriptors() %>"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof id="ele" idtype="java.lang.String" param="element">
                  <dsp:option value="<%= ele %>"/><%= ele %>
                  </dsp:getvalueof>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:select>
           </rpv:repositoryInfo>
         </dsp:getvalueof>
      </font></TD>
    </TR>
    <TR> 
      <TD VALIGN="top" ALIGN="left">&nbsp;</TD>
      <TD VALIGN="top" ALIGN="left">
        <dsp:input type="submit" value="<%= submitButton %>" bean="RepViewConfigFormHandler.confirm"/>
      </TD>
    </TR>
    <TR>
      <TD colspan=2 VALIGN="top" ALIGN="left">
        <img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=2 width=1 border=0></TD>
    </TR>

  </TABLE>

</dsp:form>
</rpv:repViewPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/installConfig/configRepository2.jsp#2 $$Change: 651448 $--%>
