
<%@ taglib uri="/rpv" prefix="rpv" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--

   Repository View Full List Configuration
 
   This page is included by installConfig.jsp 
   if the value of config_page == "fulllist". 
   It used to configure the appearance of the 
   list display on the gear's full view.

   Params to set
       + fullListDisplayPropertyNames
       + fullListTargeter
       

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
<i18n:message id="formTitle" key="config-fulllist-title"/>
<i18n:message id="formInstructions" key="about-fulllist"/>
<i18n:message id="targeterLabel" key="targeter-label"/>
<i18n:message id="propertyListLabel" key="fulllist-properties-instructions"/>
<i18n:message id="availableProperties" key="properties-available-for-display"/>
<i18n:message id="selectPropertiesInstructions" key="select-properties-instructions"/>

<%
  String targeterBeanName = "RepViewConfigFormHandler.values.fullListTargeter";
  String propListBeanName = "RepViewConfigFormHandler.values.fullListDisplayPropertyNames";
  String origURI= rpvpage.getRelativeUrl("/html/installConfig/installConfig.jsp");
%>

  <dsp:setvalue bean="RepViewConfigFormHandler.paramNames" 
	        value="fullListDisplayPropertyNames fullListTargeter"/>


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
    <core:UrlParam param="config_page" value="fulllist"/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.failureUrl" value="<%= failureUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <core:CreateUrl id="successUrl" url="<%= origURI %>">
    <core:UrlParam param="config_page" value="fulllist"/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <dsp:input type="hidden" bean="RepViewConfigFormHandler.settingDefaultValues" value="true"/>
  <dsp:input type="hidden" bean="RepViewConfigFormHandler.paramType" value="instance"/>
   <dsp:input type="hidden" bean="RepViewConfigFormHandler.mode" value="fullList"/>

  <%-- required parameters to get back to community settings --%>
  <input type="hidden" name="config_page" value="fulllist"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>

  <%@ include file="configPageTitleBar.jspf" %>

  <!-- form table -->
  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
  <tr><td>
    <table cellpadding="1" cellspacing="0" border="0">

    <!-- TARGETER -->
    <TR>
      <TD colspan=3 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
    </TR>

    <TR>
      <TD><nobr><font class="smaller"><%= targeterLabel %><%= separator %></font></nobr></TD>
      <TD width=5%><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
      <TD><font class="small">
          <rpv:repositoryInfo id="repinfo"
                         repositoryPath="<%= rpvpage.getRepositoryPath() %>" 
                         itemDescriptorName="<%= rpvpage.getItemDescriptorName() %>">
            <core:exclusiveIf>
              <core:ifNull value="<%= repinfo.getTargeters() %>">
                <i18n:message key="no-targeters-available">
                   <i18n:messageArg value="<%= rpvpage.getRepositoryPath() %>"/>
                   <i18n:messageArg value="<%= rpvpage.getItemDescriptorName() %>"/>
                </i18n:message>
              </core:ifNull>
              <core:defaultCase>   
                <dsp:select bean="<%= targeterBeanName %>">
                  <dsp:option value=""/>
                  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                    <dsp:param name="array" value="<%= repinfo.getTargeters() %>"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof id="ele" idtype="atg.repository.nucleus.RepositoryContainer.Entry" param="element">
                      <dsp:option value="<%= ele.getAbsoluteName() %>"/><%= ele.getName() %>
                      </dsp:getvalueof>
                    </dsp:oparam>
                  </dsp:droplet>
                </dsp:select>
              </core:defaultCase>
            </core:exclusiveIf>
          </rpv:repositoryInfo>
	</font>
      </TD>
    </TR>
	
    <TR>
      <TD colspan=3 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=15 width=1 border=0></TD>
    </TR>

    <!-- PROPERTY LIST -->

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
      <TD colspan=3><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>
  </TABLE>
  
  </td></tr></table><br><br>



</dsp:form>

</core:defaultCase>
</core:exclusiveIf>

</rpv:repViewPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/installConfig/fulllist.jsp#2 $$Change: 651448 $--%>
