<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<paf:InitializeGearEnvironment id="gearEnv">
<i18n:bundle baseName="atg.gears.contacts.contacts" localeAttribute="userLocale" changeResponseLocale="false" />

<% 
   String clearGif = gearEnv.getGear().getServletContext() + "/html/images/clear.gif";

%>

<dsp:importbean bean="/atg/portal/gear/GearConfigFormHandler" />
<dsp:setvalue bean="GearConfigFormHandler.initializeDefaultValues" value="foo"/>


<%--
  <br><br><font class="error"><b><i18n:message key="<%= yourContact.getErrorMessage()%>" /></b></font><br><br>

  <br><br><font class="info"><b><i18n:message key="<%= yourContact.getInfoMessage()%>" /></b></font><br><br>
--%>



<i18n:message id="i18n_quote" key="html_quote"/>
<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr>
<td><font class="pageheader_edit">
<i18n:message key="sort-param-title">
 <i18n:messageArg value='<%= gearEnv.getGear().getName(response.getLocale()) %>'/>
  <i18n:messageArg value="<%=i18n_quote%>"/>
</i18n:message>
</font></td>
</tr></table>
</td></tr></table>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr>
<td><font class="smaller">
<i18n:message key="admin_instance_header_helper_sort"/>&nbsp;
</font></td>
</tr></table>
<img src="<%=clearGif%>" height="1" width="1" border="0"><br>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr>
<td>&nbsp;

<dsp:form enctype="multipart/form-data" method="post" action="<%= gearEnv.getOriginalRequestURI() %>" >
  <input type="hidden" name="config_page" value="sortParams" />
  <input type="hidden" name="paf_gear_id" value="<%= gearEnv.getGear().getId() %>" />
  <input type="hidden" name="paf_dm" value="<%= gearEnv.getDisplayMode() %>" />
  <input type="hidden" name="paf_gm" value="<%=  gearEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_page_id" value="<%= request.getParameter("paf_page_id") %>" />
  <input type="hidden" name="paf_page_url" value="<%= request.getParameter("paf_page_url") %>" />
  <input type="hidden" name="paf_community_id" value="<%= request.getParameter("paf_community_id") %>" />
  <input type="hidden" name="paf_success_url" value="<%= request.getParameter("paf_success_url") %>" />

  <core:CreateUrl id="nextUrl" url="<%= gearEnv.getOriginalRequestURI() %>" >
    <core:UrlParam param="config_page" value="sortParams" />
    <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>" />
    <core:UrlParam param="paf_dm" value="<%= gearEnv.getDisplayMode() %>" />
    <core:UrlParam param="paf_gm" value="<%= gearEnv.getGearMode() %>" />
    <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>' />
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>' />
    <paf:encodeUrlParam param="paf_success_url" value='<%= request.getParameter("paf_success_url") %>' />
    <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>' />
    <dsp:input type="hidden" bean="GearConfigFormHandler.successUrl" value="<%= nextUrl.getNewUrl() %>" />
    <dsp:input type="hidden" bean="GearConfigFormHandler.cancelUrl" value="<%= nextUrl.getNewUrl() %>" />
  </core:CreateUrl>
  

</td>

<td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
  <tr>
   <td width="20%" align="left" valign="top">
       <font class="smaller"><i18n:message key="sort-field-label"/></font>
    </td>
    <td width="80%" valign="top">
<%-- 
        <dsp:setvalue bean="GearConfigFormHandler.values.defaultSortField"
                      value='<%= gearEnv.getGearInstanceParameter("defaultSortField") %>'/>
	<dsp:select bean="GearConfigFormHandler.values.defaultSortField">
          <dsp:option value="lastName"/><i18n:message key="lastname-sort-label"/>
          <dsp:option value="firstName"/><i18n:message key="firstname-sort-label"/>
          <dsp:option value="email"/><i18n:message key="email-sort-label"/>
          <dsp:option value="login"/><i18n:message key="login-sort-label"/>
        </dsp:select>
--%>

<dsp:input type="radio" bean="GearConfigFormHandler.values.defaultSortField" value="lastName" checked='<%= gearEnv.getGearInstanceParameter("defaultSortField").equals("lastName") %>'/><font class="smaller"><i18n:message key="lastname-sort-label"/></font><br>

<dsp:input type="radio" bean="GearConfigFormHandler.values.defaultSortField" value="firstName" checked='<%= gearEnv.getGearInstanceParameter("defaultSortField").equals("firstName") %>' /><font class="smaller"><i18n:message key="firstname-sort-label"/></font><br>

<dsp:input type="radio" bean="GearConfigFormHandler.values.defaultSortField" value="email" checked='<%= gearEnv.getGearInstanceParameter("defaultSortField").equals("email") %>'/><font class="smaller"><i18n:message key="email-sort-label"/></font><br>

<dsp:input type="radio" bean="GearConfigFormHandler.values.defaultSortField" value="login" checked='<%= gearEnv.getGearInstanceParameter("defaultSortField").equals("login") %>' /><font class="smaller"><i18n:message key="login-sort-label"/></font><br>

      <br></td>
    </tr>

    <tr>
      <td width="20%" align="left" valign="top">
        <font class="smaller"><i18n:message key="sort-order-label"/></font>
      </td>
      
      <td WIDTH="80%" valign="top">
<dsp:input type="radio" bean="GearConfigFormHandler.values.defaultSortOrder" value="ascending" checked='<%= gearEnv.getGearInstanceParameter("defaultSortOrder").equals("ascending") %>'/><font class="smaller"><i18n:message key="radio-label-asc"/><br>

<dsp:input type="radio" bean="GearConfigFormHandler.values.defaultSortOrder" value="descending" checked='<%= gearEnv.getGearInstanceParameter("defaultSortOrder").equals("descending") %>' /><font class="smaller"><i18n:message key="radio-label-desc"/>
      </td>
    </tr>

    <tr valign="top" align="left">


      <td align="left">&nbsp;</td>


      <td align="left">

<i18n:message id="updateTextString" key="update-button-text"/>
<i18n:message id="resetTextString" key="reset-button-text"/>

<dsp:input type="submit" value="<%= updateTextString %>" bean="GearConfigFormHandler.confirm"/>&nbsp;&nbsp;&nbsp;
<input type="reset" value="<%=resetTextString%>" />
      </td>
    </tr>

  </table>   
</dsp:form>
</td>
</tr>
</table>
<br><br>
    
</paf:InitializeGearEnvironment>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/contacts/contacts.war/html/admin/configSortParams.jsp#2 $$Change: 651448 $--%>

