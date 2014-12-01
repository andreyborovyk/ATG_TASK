<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/docexch-taglib" prefix="dex" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">
<dex:DocExchPage id="dexpage" gearEnv="<%= pafEnv %>">

<i18n:bundle baseName="<%= dexpage.getResourceBundle() %>" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="guestLabel" key="guestLabel"/>
<i18n:message id="memberLabel" key="memberLabel"/>
<i18n:message id="adminLabel" key="adminLabel"/>
<i18n:message id="anyoneLabel" key="anyoneLabel"/>
<i18n:message id="anyRegUserLabel" key="anyRegUserLabel"/>
<i18n:message id="noAccessMsgLabel" key="noAccessMsgLabel"/>
<i18n:message id="backToGearAdminLink" key="backToGearAdminLink"/>
<i18n:message id="instanceConfigTitle" key="instanceConfigTitle"/>
<i18n:message id="discussPermissionLabel" key="discussPermissionLabel"/>
<i18n:message id="writePermissionLabel" key="writePermissionLabel"/>
<i18n:message id="writePermissionHelpText" key="writePermissionHelpText"/>
<i18n:message id="updateStatusPermissionLabel" key="updateStatusPermissionLabel"/>
<i18n:message id="finishButton" key="finishButton"/>
<i18n:message id="formTitle" key="configPermissionsFormTitle"/>
<i18n:message id="formInstructions" key="configPermissionsFormInstructions"/>

<dsp:importbean bean="/atg/portal/gear/docexch/DocExchConfigFormHandler"/>

<dsp:form method="post" action="<%= pafEnv.getOriginalRequestURI() %>">
  <dsp:input type="hidden" bean="DocExchConfigFormHandler.paramType" value="instance"/>
  <dsp:input type="hidden" bean="DocExchConfigFormHandler.settingDefaultValues" value="false"/>
  <dsp:setvalue bean="DocExchConfigFormHandler.paramNames" 
	        value="discussPermissionRole writePermissionRole updateStatusPermissionRole"/>

  <core:CreateUrl id="successUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="config_page" value="permission"/>
    <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
    <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
    <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <dsp:input type="hidden" bean="DocExchConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <%-- required parameters to get back to community settings --%>
  <input type="hidden" name="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
  <input type="hidden" name="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>    
  <input type="hidden" name="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>	  
  <input type="hidden" name="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
  <input type="hidden" name="config_page" value="permission"/>
  

  <%@ include file="configPageTitleBar.jspf" %>


  <!-- form table -->
  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
  <tr><td>

    <table cellpadding="1" cellspacing="0" border="0">

    <%-- **************** --%>
    <tr>
      <td width="50%" align="left" valign="top"><font class="smaller"><%= discussPermissionLabel %></font></td>

      <td width="50%" valign="top">&nbsp;&nbsp;
        <dsp:select bean="DocExchConfigFormHandler.values.discussPermissionRole">
          <dsp:option value="anyone"/><%= anyoneLabel %>
          <dsp:option value="anyRegUser"/><%= anyRegUserLabel %>
          <dsp:option value="guest"/><%= guestLabel %>
          <dsp:option value="member"/><%= memberLabel %>
          <dsp:option value="leader"/><%= adminLabel %>
        </dsp:select>
      </td>
    </tr>

    <tr>
      <td colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=2 width=1 border=0></td>
    </tr>

    <%-- **************** --%>
    <tr>
      <td width="50%" align="left" valign="top"><font class="smaller"><%= writePermissionLabel %></font></td>

      <td width="50%" valign="top">&nbsp;&nbsp;
        <dsp:select bean="DocExchConfigFormHandler.values.writePermissionRole">
          <dsp:option value="anyone"/><%= anyoneLabel %>
          <dsp:option value="anyRegUser"/><%= anyRegUserLabel %>
          <dsp:option value="guest"/><%= guestLabel %>
          <dsp:option value="member"/><%= memberLabel %>
          <dsp:option value="leader"/><%= adminLabel %>
        </dsp:select>
      </td>
    </tr>

    <tr>
      <td colspan=2 align="left" valign="top"><font class="smaller"><i><%= writePermissionHelpText %></i></font>
      </td>
    </tr>

    <tr>
      <td colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=2 width=1 border=0></td>
    </tr>

    <%-- **************** --%>
    <tr>
      <td width="50%" align="left" valign="top"><font class="smaller"><%= updateStatusPermissionLabel %></font></td>

      <td width="50%">&nbsp;&nbsp;
        <dsp:select bean="DocExchConfigFormHandler.values.updateStatusPermissionRole">
          <dsp:option value="anyone"/><%= anyoneLabel %>
          <dsp:option value="anyRegUser"/><%= anyRegUserLabel %>
          <dsp:option value="guest"/><%= guestLabel %>
          <dsp:option value="member"/><%= memberLabel %>
          <dsp:option value="leader"/><%= adminLabel %>
        </dsp:select>
      </td>
    </tr>

    <tr>
      <td colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=2 width=1 border=0></td>
    </tr>

    <%-- **************** --%>
    <tr>
      <td colspan=2 ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></td>
    </tr>
    <tr VALIGN="top" ALIGN="left">
    	<td colspan=2>
       <dsp:input type="submit" value="<%= finishButton %>" bean="DocExchConfigFormHandler.confirm"/>
      </td>
    </tr>
    <tr>
      <td colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></td>
    </tr>
  </TABLE>
  
  </td></tr></table>

</dsp:form>
</dex:DocExchPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/configPermissions.jsp#2 $$Change: 651448 $--%>
