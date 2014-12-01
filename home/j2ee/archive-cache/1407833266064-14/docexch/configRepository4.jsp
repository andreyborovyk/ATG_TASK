<%--
   Document Exchange Gear
   gearmode = installConfig 
   displaymode = full
  
   This page fragment is included by InstallConfig.jsp.
   It follows configRepository3.jsp.  This form allows 
   the portal administrator to configure a a few last 
   repository settings. 
--%>

<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
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
<i18n:message id="readOnlyLabel" key="readOnlyLabel"/>
<i18n:message id="readOnlyValue" key="readOnlyValue"/>
<i18n:message id="readWriteValue" key="readWriteValue"/>
<i18n:message id="finishButton" key="finishButton"/>
<i18n:message id="maxFileSizeLabel" key="maxFileSizeLabel"/>
<i18n:message id="megabytes" key="megabytes"/>
<i18n:message id="separator" key="separator"/>
<i18n:message id="helptext" key="repositoryLimitsHelpText"/>

  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
      <font class="pageheader_edit">
      <i18n:message key="repositoryLimitationsMainHeading"/>
    </td></tr></table>
  </td></tr></table>

  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>

<dsp:form method="post" action='<%= dexpage.getRelativeUrl("/InstallConfig.jsp") %>'>

  <dsp:input type="hidden" bean="DocExchConfigFormHandler.paramType" value="instance"/>
  <dsp:input type="hidden" bean="DocExchConfigFormHandler.settingDefaultValues" value="true"/>
  <dsp:input type="hidden" bean="DocExchConfigFormHandler.paramType" value="instance"/>
  <dsp:setvalue bean="DocExchConfigFormHandler.paramNames" value="readOnly maxFileSize"/>

  <input type="hidden" name="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>


  <%-- required fields for navigation and processing --%>
  <core:CreateUrl id="successUrl" url='<%= dexpage.getRelativeUrl("/InstallConfig.jsp") %>'>
    <core:UrlParam param="config_page" value="limitations"/>
    <core:UrlParam param="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <dsp:input type="hidden" bean="DocExchConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl() %>"/>
  </core:CreateUrl>
	
  <%-- required fields for navigation and processing --%>
  <core:CreateUrl id="successUrl" url='<%= dexpage.getRelativeUrl("/InstallConfig.jsp") %>'>
    <core:UrlParam param="config_page" value="limitations"/>
    <core:UrlParam param="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <dsp:input type="hidden" bean="DocExchConfigFormHandler.failureUrl" value="<%= successUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <font class="smaller"><%= helptext %>
  </td></tr></table>
  <img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=1 width=1 border=0><br>
  
<!-- form table -->
 <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%">
    <TR>
      <TD colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=2 width=1 border=0></TD>
    </TR>



    <TR> 
      <TD WIDTH="15%" VALIGN="top" align="left"><font  class="smaller">
        <%= readOnlyLabel %><%= separator %> &nbsp;</font></TD>
      <TD WIDTH="85%" VALIGN="top" ALIGN="left"><font  class="smaller">
	 <dsp:input type="radio" bean="DocExchConfigFormHandler.values.readOnly" value="true"/>
         <font  class="smaller"><%= readOnlyValue %>&nbsp;&nbsp&nbsp;</font>
          <dsp:input type="radio" bean="DocExchConfigFormHandler.values.readOnly" value="false"/>
            <font  class="smaller"><%= readWriteValue %></font>
      </TD>
    </TR>

    <TR>
      <TD colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>

    <TR> 
      <TD VALIGN="top" align="left" NOWRAP><font  class="smaller">
        <%= maxFileSizeLabel %><%= separator %> &nbsp;</font></TD>
      <TD VALIGN="top" ALIGN="left">
        <dsp:input type="text" size="15" bean="DocExchConfigFormHandler.values.maxFileSize"/>
          <font  class="smaller"> &nbsp;<%= megabytes %>
      </font></TD>
    </TR>

    <TR>
      <TD colspan=2 ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=8 width=1 border=0></TD>
    </TR>

    <TR> 
      <TD VALIGN="top" ALIGN="left">&nbsp;</TD>
      <TD VALIGN="top" ALIGN="left"><font class="small">
        <dsp:input type="submit" value="<%= finishButton %>" bean="DocExchConfigFormHandler.confirm"/>
      </TD>
    </TR>
    <TR>
      <TD colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>
  </TABLE>

</dsp:form>

</dex:DocExchPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/configRepository4.jsp#2 $$Change: 651448 $--%>
