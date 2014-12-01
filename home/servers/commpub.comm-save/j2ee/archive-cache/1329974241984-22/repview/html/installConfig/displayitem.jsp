<%@ taglib uri="/rpv" prefix="rpv" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--

   Repository View Display Item Configuration
 
   This page is included by installConfig.jsp 
   if the value of config_page == "displayitem". 
   It used to configure the appearance of the 
   single item display on the gear's full view.

   Params to set
      + detailDisplayPropertyNames
      + customItemDisplayPage

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

<i18n:bundle baseName="atg.portal.gear.repview.RepViewConfigResources" 
             localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="submitButton" key="submitButton"/>
<i18n:message id="separator" key="labelFieldSeparator"/>
<i18n:message id="formTitle" key="config-displayitem-title"/>
<i18n:message id="formInstructions" key="about-displayitem"/>
<i18n:message id="displayInstructionsLabel" key="display-options"/>
<i18n:message id="customPageLabel" key="custom-item-page-label"/>
<i18n:message id="propertyListLabel" key="displayitem-properties-instructions"/>
<i18n:message id="availableProperties" key="properties-available-for-display"/>
<i18n:message id="or" key="or"/>
<i18n:message id="selectPropertiesInstructions" key="select-properties-instructions"/>

<%
  String customPageBeanName = "RepViewConfigFormHandler.values.customItemDisplayPage";
  String propListBeanName = "RepViewConfigFormHandler.values.detailDisplayPropertyNames";
  String origURI= rpvpage.getRelativeUrl("/html/installConfig/installConfig.jsp");
%>

  <dsp:setvalue bean="RepViewConfigFormHandler.paramNames" 
	        value="detailDisplayPropertyNames customItemDisplayPage"/>


<core:exclusiveIf>
  <core:ifNull value="<%= rpvpage.getRepositoryPath() %>">
    <p><i18n:message key="instanceConfigIncomplete"/><p>
  </core:ifNull>  

  <core:ifNull value="<%= rpvpage.getItemDescriptorName() %>">
    <p><i18n:message key="instanceConfigIncomplete"/><p>
  </core:ifNull>  

  <core:defaultCase>

<dsp:form method="post" action="<%= origURI %>">

  <core:CreateUrl id="failureUrl" url="<%= origURI %>">
    <core:UrlParam param="config_page" value="displayitem"/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.failureUrl" value="<%= failureUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <core:CreateUrl id="successUrl" url="<%= origURI %>">
    <core:UrlParam param="config_page" value="displayitem"/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <dsp:input type="hidden" bean="RepViewConfigFormHandler.settingDefaultValues" value="true"/>
  <dsp:input type="hidden" bean="RepViewConfigFormHandler.paramType" value="instance"/>
  <dsp:input type="hidden" bean="RepViewConfigFormHandler.mode" value="displayItem"/>

  <%-- required parameters to get back to community settings --%>
  <input type="hidden" name="config_page" value="displayitem"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>

  <%@ include file="configPageTitleBar.jspf" %>

  <!-- form table -->
  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
  <tr><td>
    <table cellpadding="1" cellspacing="0" border="0">

    <!-- CUSTOM DISPLAY OPTIONS -->

    <TR>
      <TD width=15%><font class="smaller"><%= customPageLabel %><%= separator %></font></TD>
      <TD width=5%><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
      <TD><font class="smaller">
         <dsp:input type="text" size="45" bean="<%= customPageBeanName %>"/></font></TD>
    </TR>

    <TR>
      <TD colspan=3 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
    </TR>

    <TR>
      <TD colspan=3 ><font class="small_bold"><%= or %></font></TD>
    </TR>

    <TR>
      <TD colspan=3 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
    </TR>

    <TR>
      <TD valign="top"><font class="smaller"><%= propertyListLabel %><%= separator %></font></TD>
      <TD width=5%><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
      <TD>
	<font class="smaller">
          <%= selectPropertiesInstructions %>
	  <p>
          <core:ForEach id="foreach"
             values="<%= rpvpage.getBasePropertyList() %>"
             castClass="java.lang.String"
             elementId="propName">
             <code><%= propName %>&nbsp;</code>
          </core:ForEach>
	  <p>
           <dsp:textarea cols="35" rows="4"
             bean="<%= propListBeanName %>"
             wrap="true"></dsp:textarea>
       </font>
      </TD>
    </TR>
     
    <TR>
      <TD colspan=3 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>

     <TR>
      <TD colspan=3 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=4 width=1 border=0></TD>
    </TR>

    <TR>
      <TD colspan=3 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height="10" width="1" border=0></TD>
    </TR>

    <TR VALIGN="top" ALIGN="left"> 
      <TD colspan=3>
        <dsp:input type="submit" value="<%= submitButton %>" bean="RepViewConfigFormHandler.confirm"/>
      </TD>
    </TR>
    <TR>
      <TD colspan=2><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>
  </TABLE>
  
  </td></tr></table><br><br>



</dsp:form>

</core:defaultCase>
</core:exclusiveIf>

</rpv:repViewPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/installConfig/displayitem.jsp#2 $$Change: 651448 $--%>
