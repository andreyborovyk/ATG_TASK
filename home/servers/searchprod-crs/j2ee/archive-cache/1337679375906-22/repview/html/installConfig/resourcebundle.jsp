<%@ taglib uri="/rpv" prefix="rpv" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--

   Repository View Resource Bundle Configuration
 
   This page is included by installConfig.jsp 
   if the value of config_page == "resourcebundle". 
   It used to configure the resource bundle setting which 
   is used throughout the content mode of the gear.  

--%>

<dsp:page>
<%-- 
  Set the GEAR DEFINITION ID so that the InitializeGearEnvironment 
  tag will work.  This is required on all installConfig pages. 
 --%>   
<% request.setAttribute(atg.portal.framework.RequestAttributes.GEAR_DEFINITION, (String) request.getParameter("paf_gear_def_id") ); %>
<paf:InitializeGearEnvironment id="pafEnv">
<rpv:repViewPage id="rpvpage" gearEnv="<%= pafEnv %>">
<dsp:importbean bean="/atg/portal/gear/repview/RepViewConfigFormHandler"/>
<% 
  String origURI= rpvpage.getRelativeUrl("/html/installConfig/installConfig.jsp");
%>
<i18n:bundle baseName="atg.portal.gear.repview.RepViewConfigResources" 
             localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="formTitle" key="config-resourcebundle-title"/>
<i18n:message id="formInstructions" key="about-resourcebundle"/>
<i18n:message id="submitButton" key="submitButton"/>
<i18n:message id="separator" key="labelFieldSeparator"/>
<i18n:message id="resourceBundleLabel" key="resource-bundle-label"/>
<i18n:message id="resourceBundleInstructions" key="resource-bundle-instructions"/>

<dsp:form method="post" action="<%= origURI %>">

  <core:CreateUrl id="failureUrl" url="<%= origURI %>">
    <core:UrlParam param="config_page" value="resourcebundle"/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.failureUrl" value="<%= failureUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <core:CreateUrl id="successUrl" url="<%= origURI %>">
    <core:UrlParam param="config_page" value="resourcebundle"/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <dsp:input type="hidden" bean="RepViewConfigFormHandler.settingDefaultValues" value="true"/>
  <dsp:input type="hidden" bean="RepViewConfigFormHandler.paramType" value="instance"/>
  <dsp:input type="hidden" bean="RepViewConfigFormHandler.mode" value="resourceBundle"/>

  <input type="hidden" name="config_page" value="resourcebundle"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>

  <dsp:setvalue bean="RepViewConfigFormHandler.paramNames" 
	        value="resourceBundle"/>

  <%@ include file="configPageTitleBar.jspf" %>

  <!-- form table -->
  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
  <tr><td>
    <table cellpadding="1" cellspacing="0" border="0">

    <!-- RESOURCE BUNDLE -->
    <TR>
      <TD colspan=2 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
    </TR>

    <TR>
      <TD><font class="smaller_bold"><%= resourceBundleLabel %><%= separator %></font></TD>
      <TD><font class="smaller">
         <dsp:input type="text" size="45" bean="RepViewConfigFormHandler.values.resourceBundle"/></font></TD>
    </TR>
	
    <TR> 
      <TD colspan=2>
        <font class="smaller"><%= resourceBundleInstructions %></font></TD>
    </TR>

    <TR>
      <TD colspan=2 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>


    <%-- spacer row and SUBMIT BUTTON --%>
    <TR>
      <TD colspan=2 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=4 width=1 border=0></TD>
    </TR>

    <TR>
      <TD colspan=2 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height="10" width="1" border=0></TD>
    </TR>

    <TR VALIGN="top" ALIGN="left"> 
      <TD colspan=2>
        <dsp:input type="submit" value="<%= submitButton %>" bean="RepViewConfigFormHandler.confirm"/>
      </TD>
    </TR>
    <TR>
      <TD colspan=2><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>
  </TABLE>
  
  </td></tr></table><br><br>

</dsp:form>
</rpv:repViewPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/installConfig/resourcebundle.jsp#2 $$Change: 651448 $--%>