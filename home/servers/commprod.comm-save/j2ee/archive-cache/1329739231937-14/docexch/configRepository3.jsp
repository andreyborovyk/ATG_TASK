<%--
   Document Exchange Gear
   gearmode = installConfig 
   displaymode = full
  
   This page fragment is included by InstallConfig.jsp.
   It follows configRepository2.jsp.  This form allows 
   the portal administrator to map the properties of 
   the chosen repository item descriptor to the properties
   required by this gear.
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
<i18n:message id="continueButton" key="continueButton"/>
<i18n:message id="finishButton" key="finishButton"/>
<i18n:message id="unset" key="unset"/>
<i18n:message id="filenamePropertyNameLabel" key="filenamePropertyNameLabel"/>
<i18n:message id="fileDataPropertyNameLabel" key="fileDataPropertyNameLabel"/>
<i18n:message id="mimeTypePropertyNameLabel" key="mimeTypePropertyNameLabel"/>
<i18n:message id="fileSizePropertyNameLabel" key="fileSizePropertyNameLabel"/>
<i18n:message id="descriptionPropertyNameLabel" key="descriptionPropertyNameLabel"/>
<i18n:message id="titlePropertyNameLabel" key="titlePropertyNameLabel"/>
<i18n:message id="authorPropertyNameLabel" key="authorPropertyNameLabel"/>
<i18n:message id="createDatePropertyNameLabel" key="createDatePropertyNameLabel"/>
<i18n:message id="statusPropertyNameLabel" key="statusPropertyNameLabel"/>
<i18n:message id="gearIdPropertyNameLabel" key="gearIdPropertyNameLabel"/>
<i18n:message id="annotationRefPropertyNameLabel" key="annotationRefPropertyNameLabel"/>
<i18n:message id="separator" key="separator"/>
<i18n:message id="helptext" key="propertyMappingHelpText"/>

  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
      <font class="pageheader_edit">
      <i18n:message key="repositoryMappingMainHeading"/>
    </td></tr></table>
  </td></tr></table>

  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>

<dsp:form method="post" action='<%= dexpage.getRelativeUrl("/InstallConfig.jsp") %>'>

  <input type="hidden" name="paf_gear_def_id" value="<%= pafEnv.getGearDefinition().getId() %>"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="config_page" value="Repository1"/>



  <font class="smaller"><%= helptext %>
  </td></tr></table>
  <img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=1 width=1 border=0><br>


 <!-- form table --> 
 <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%">

    <TR>
      <TD WIDTH="15%" VALIGN="top" align="left" NOWRAP><font class="smaller">
        <%= repositoryLabel %><%= separator %> &nbsp;</font></TD>
      <TD WIDTH="85%" VALIGN="top" ALIGN="left">
        <font class="smaller">
          <dsp:valueof bean="DocExchConfigFormHandler.values.repositoryPath"><%= unset %></dsp:valueof>
        </font>
      </TD>
    </TR>

    <TR>
      <TD VALIGN="top" align="left" NOWRAP><font class="smaller">
        <%= itemDescriptorNameLabel %><%= separator %> &nbsp;</font></TD>
      <TD VALIGN="top" ALIGN="left"><font class="smaller">
        <dsp:valueof bean="DocExchConfigFormHandler.values.itemDescriptorName"><%= unset %></dsp:valueof>
	</font></TD>
    </TR>

    <TR>
      <TD VALIGN="top" align="left" NOWRAP><font class="smaller">
        <%= filenamePropertyNameLabel %><%= separator %> &nbsp;</font></TD>
      <TD VALIGN="top" ALIGN="left"><font class="small">
        <dsp:select bean="DocExchConfigFormHandler.values.filenamePropertyName">
          <dsp:option value=""/>
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" bean="DocExchConfigFormHandler.stringProperties"/>
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
      <TD VALIGN="top" align="left" NOWRAP><font class="smaller">
        <%= fileDataPropertyNameLabel %><%= separator %> &nbsp;</font></TD>
      <TD VALIGN="top" ALIGN="left"><font class="small">
        <dsp:select bean="DocExchConfigFormHandler.values.fileDataPropertyName">
          <dsp:option value=""/>
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" bean="DocExchConfigFormHandler.blobProperties"/>
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
      <TD VALIGN="top" align="left" NOWRAP><font class="smaller">
        <%= mimeTypePropertyNameLabel %><%= separator %> &nbsp;</font></TD>
      <TD VALIGN="top" ALIGN="left"><font class="small">
        <dsp:select bean="DocExchConfigFormHandler.values.mimeTypePropertyName">
          <dsp:option value=""/>
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" bean="DocExchConfigFormHandler.stringProperties"/>
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
      <TD VALIGN="top" align="left" NOWRAP><font class="smaller">
        <%= fileSizePropertyNameLabel %><%= separator %> &nbsp;</font></TD>
      <TD VALIGN="top" ALIGN="left"><font class="small">
        <dsp:select bean="DocExchConfigFormHandler.values.fileSizePropertyName">
          <dsp:option value=""/>
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" bean="DocExchConfigFormHandler.intProperties"/>
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
      <TD VALIGN="top" align="left" NOWRAP><font class="smaller">
        <%= descriptionPropertyNameLabel %><%= separator %> &nbsp;</font></TD>
      <TD VALIGN="top" ALIGN="left"><font class="small">
        <dsp:select bean="DocExchConfigFormHandler.values.descriptionPropertyName">
          <dsp:option value=""/>
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" bean="DocExchConfigFormHandler.stringProperties"/>
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
      <TD VALIGN="top" align="left" NOWRAP><font class="smaller">
        <%= titlePropertyNameLabel %><%= separator %> &nbsp;</font></TD>
      <TD VALIGN="top" ALIGN="left"><font class="small">
        <dsp:select bean="DocExchConfigFormHandler.values.titlePropertyName">
          <dsp:option value=""/>
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" bean="DocExchConfigFormHandler.stringProperties"/>
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
      <TD VALIGN="top" align="left" NOWRAP><font class="smaller">
        <%= authorPropertyNameLabel %><%= separator %> &nbsp;</font></TD>
      <TD VALIGN="top" ALIGN="left"><font class="small">
        <dsp:select bean="DocExchConfigFormHandler.values.authorPropertyName">
          <dsp:option value=""/>
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" bean="DocExchConfigFormHandler.repositoryItemProperties"/>
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
      <TD VALIGN="top" align="left" NOWRAP><font class="smaller">
        <%= createDatePropertyNameLabel %><%= separator %> &nbsp;</font></TD>
      <TD VALIGN="top" ALIGN="left"><font class="small">
        <dsp:select bean="DocExchConfigFormHandler.values.createDatePropertyName">
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" bean="DocExchConfigFormHandler.timestampProperties"/>
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
      <TD VALIGN="top" align="left" NOWRAP><font class="smaller">
        <%= statusPropertyNameLabel %><%= separator %> &nbsp;</font></TD>
      <TD VALIGN="top" ALIGN="left"><font class="small">
        <dsp:select bean="DocExchConfigFormHandler.values.statusPropertyName">
          <dsp:option value=""/>
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" bean="DocExchConfigFormHandler.intProperties"/>
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
      <TD VALIGN="top" align="left" NOWRAP><font class="smaller">
        <%= gearIdPropertyNameLabel %><%= separator %> &nbsp;</font></TD>
      <TD VALIGN="top" ALIGN="left"><font class="small">
        <dsp:select bean="DocExchConfigFormHandler.values.gearIdPropertyName">
          <dsp:option value=""/>
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" bean="DocExchConfigFormHandler.stringProperties"/>
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
      <TD VALIGN="top" align="left" NOWRAP><font class="smaller">
        <%= annotationRefPropertyNameLabel %><%= separator %> &nbsp;</font></TD>
      <TD VALIGN="top" ALIGN="left"><font class="small">
        <dsp:select bean="DocExchConfigFormHandler.values.annotationRefPropertyName">
          <dsp:option value=""/>
          <dsp:droplet name="/atg/dynamo/droplet/ForEach">
            <dsp:param name="array" bean="DocExchConfigFormHandler.stringProperties"/>
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
        <dsp:input type="submit" value="<%= finishButton %>" bean="DocExchConfigFormHandler.confirm"/>
      </TD>
    </TR>

      <TD colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>
  </TABLE>

</dsp:form>

</dex:DocExchPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/configRepository3.jsp#2 $$Change: 651448 $--%>
