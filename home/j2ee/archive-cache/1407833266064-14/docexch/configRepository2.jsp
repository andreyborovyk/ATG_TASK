<%--
   Document Exchange Gear
   gearmode = installConfig 
   displaymode = full
  
   This page fragment is included by InstallConfig.jsp.
   It follows configRepository1.jsp.  This form allows 
   the portal administrator to select a repository item 
   type from to hold the documents managed by the document
   exchange gear.
--%>

<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/docexch-taglib" prefix="dex" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<dsp:importbean bean="/atg/portal/gear/docexch/DocExchConfigFormHandler"/>

<%-- 
  Set the GEAR DEFINITION ID so that the InitializeGearEnvironment 
  tag will work.  This is required on all installConfig pages. 
 --%>   
<% request.setAttribute(atg.portal.framework.RequestAttributes.GEAR_DEFINITION, (String) request.getParameter("paf_gear_def_id") ); %>

<paf:InitializeGearEnvironment id="pafEnv">
<dex:DocExchPage id="dexpage" gearEnv="<%= pafEnv %>">

<i18n:bundle baseName="atg.portal.gear.docexch.DocExchInstallConfigResources" changeResponseLocale="false" />
<i18n:message id="repositoryLabel" key="repositoryLabel"/>
<i18n:message id="itemDescriptorNameLabel" key="itemDescriptorNameLabel"/>
<i18n:message id="itemDescriptorHelpText" key="itemDescriptorHelpText"/>
<i18n:message id="continueButton" key="continueButton"/>
<i18n:message id="unset" key="unset"/>
<i18n:message id="separator" key="separator"/>

  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
      <font class="pageheader_edit">
      <i18n:message key="repositoryMappingMainHeading"/>
    </td></tr></table>
  </td></tr></table>

  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>

<dsp:form method="post" action='<%= dexpage.getRelativeUrl("/InstallConfig.jsp")%>'>
	
  <input type="hidden" name="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="config_page" value="Repository3"/>

        
        <font class="smaller"><%= itemDescriptorHelpText %>
        </td></tr></table>
		<img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=1 width=1 border=0><br>
  
 <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%">
  
     <TR>
      <TD colspan=2 VALIGN="top" ALIGN="left">
        <img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=2 width=1 border=0></TD>
    </TR>


    <TR> 
      <TD WIDTH="15%" VALIGN="top" align="left" NOWRAP><font class="smaller">
        <%= repositoryLabel %><%= separator %> &nbsp;&nbsp;</font></TD>
      <TD WIDTH="85%" VALIGN="top" ALIGN="left"><font class="smaller">
        <dsp:valueof bean="DocExchConfigFormHandler.values.repositoryPath"><%= "unset" %></dsp:valueof>
      </font></TD>
    </TR>

    <TR>
      <TD colspan=2 VALIGN="top" ALIGN="left">
        <img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=2 width=1 border=0></TD>
    </TR>

    <TR> 
      <TD align="left" valign="top" NOWRAP><font class="smaller">
        <%= itemDescriptorNameLabel %><%= separator %>&nbsp;&nbsp;</font></TD>
      <TD align="left" valign="top"><font class="small">        
        <dsp:select bean="DocExchConfigFormHandler.values.itemDescriptorName">
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" bean="DocExchConfigFormHandler.itemDescriptorChoices"/>
            <dsp:oparam name="output">
              <dsp:getvalueof id="ele" idtype="java.lang.String" param="element">
              <dsp:option value="<%= ele %>"/><%= ele %>
              </dsp:getvalueof>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:select>
      </font></TD>
    </TR>
    <TR> 
      <TD VALIGN="top" ALIGN="left">&nbsp;</TD>
      <TD VALIGN="top" ALIGN="left">
        <dsp:input type="submit" value="<%= continueButton %>" bean="DocExchConfigFormHandler.collectValues"/>
      </TD>
    </TR>
    <TR>
      <TD colspan=2 VALIGN="top" ALIGN="left">
        <img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=2 width=1 border=0></TD>
    </TR>

  </TABLE>

</dsp:form>
</dex:DocExchPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/configRepository2.jsp#2 $$Change: 651448 $--%>
