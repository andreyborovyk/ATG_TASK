<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<paf:InitializeGearEnvironment id="gearEnv">
<paf:hasCommunityRole roles="leader,gear-manager">

<i18n:bundle baseName="atg.portal.gear.alert.alert" localeAttribute="userLocale" changeResponseLocale="false" />

<% String origURI = gearEnv.getOriginalRequestURI();
   String gearId = gearEnv.getGear().getId();
   String pageId = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String communityId = request.getParameter("paf_community_id"); 
   String pafSuccessURL = request.getParameter("paf_success_url");
   String clearGif = gearEnv.getGear().getServletContext() + "/html/images/clear.gif";
%>


<dsp:importbean bean="/atg/portal/gear/alert/AlertGearConfigFormHandler" />
<dsp:setvalue bean="AlertGearConfigFormHandler.initializeDefaultValues" value="foo"/>

<%--
  <br><br><font class="error"><b><i18n:message key="<%= yourAlert.getErrorMessage()%>" /></b></font><br><br>

  <br><br><font class="info"><b><i18n:message key="<%= yourAlert.getInfoMessage()%>" /></b></font><br><br>
--%>

    
<i18n:message id="i18n_quote" key="html_quote"/>
<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="pageheader_edit">
 <i18n:message key="access-config-title">
 <i18n:messageArg value='<%= gearEnv.getGear().getName(response.getLocale()) %>'/>
  <i18n:messageArg value="<%=i18n_quote%>"/>
</i18n:message>&nbsp;</font>
</td></tr></table>
</td></tr></table>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%">
<tr>
<td><font class="smaller"><i18n:message key="admin_configure_instance_access"/>&nbsp;</font></td>
</tr></table>
<img src="<%=clearGif%>" height="1" width="1" border="0"><br>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>

<dsp:form enctype="multipart/form-data" method="post" action="<%= origURI %>" >
  <input type="hidden" name="config_page" value="accessParams" />
  <input type="hidden" name="paf_gear_id" value="<%= gearId %>" />
  <input type="hidden" name="paf_dm" value="<%= gearEnv.getDisplayMode() %>" />
  <input type="hidden" name="paf_gm" value="<%= gearEnv.getGearMode() %>" />
  <input type="hidden" name="paf_page_id" value="<%= pageId %>" />
  <input type="hidden" name="paf_page_url" value="<%= pageURL %>" />
  <input type="hidden" name="paf_community_id" value="<%= communityId %>" />
  <input type="hidden" name="paf_success_url" value="<%= pafSuccessURL %>" />

  <core:CreateUrl id="nextUrl" url="<%= origURI %>" >
  <core:UrlParam param="config_page" value="accessParams" />
    <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>" />
    <core:UrlParam param="paf_dm" value="<%= gearEnv.getDisplayMode() %>" />
    <core:UrlParam param="paf_gm" value="<%= gearEnv.getGearMode() %>" />
    <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>' />
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>' />
    <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>' />
    <paf:encodeUrlParam param="paf_success_url" value='<%= request.getParameter("paf_success_url") %>' />
    <dsp:input type="hidden" bean="AlertGearConfigFormHandler.successUrl" value="<%= nextUrl.getNewUrl() %>" />
    <dsp:input type="hidden" bean="AlertGearConfigFormHandler.failureUrl" value="<%= nextUrl.getNewUrl() %>" />
    <dsp:input type="hidden" bean="AlertGearConfigFormHandler.cancelUrl" value="<%= nextUrl.getNewUrl() %>" />
  </core:CreateUrl>
&nbsp;

</td><td>

<table cellpadding="2" cellspacing="0" border="0" width="98%">
<tr><td colspan="2">


   </td></tr>
    <tr>
      <td width="30%" align="left">
        <font class="smaller"><i18n:message key="aggregated-community-alerts-title"/></font>
      </td>
      
      <td WIDTH="70%"><dsp:input type="radio" bean="AlertGearConfigFormHandler.values.aggregatedCommunityEnabled" value="true" checked='<%= gearEnv.getGearInstanceParameter("aggregatedCommunityEnabled").equals("true") %>'/><font class="smaller"><i18n:message key="constant-true"/></font>&nbsp;&nbsp;&nbsp;

<dsp:input type="radio" bean="AlertGearConfigFormHandler.values.aggregatedCommunityEnabled" value="false" checked='<%= gearEnv.getGearInstanceParameter("aggregatedCommunityEnabled").equals("false") %>'/><font class="smaller"><i18n:message key="constant-false"/></font><br></td>

    </tr>

    <tr>
      <td width="50%" align="left">
        <font class="smaller"><i18n:message key="community-enabled-title"/></font>
      </td>
      
      <td WIDTH="50%"><dsp:input type="radio" bean="AlertGearConfigFormHandler.values.communityEnabled" value="true" checked='<%= gearEnv.getGearInstanceParameter("communityEnabled").equals("true") %>'/><font class="smaller"><i18n:message key="constant-true"/></font>&nbsp;&nbsp;&nbsp;

<dsp:input type="radio" bean="AlertGearConfigFormHandler.values.communityEnabled" value="false" checked='<%= gearEnv.getGearInstanceParameter("communityEnabled").equals("false") %>'/><font class="smaller"><i18n:message key="constant-false"/></font><br></td>

    </tr>


    <tr>
      <td width="50%" align="left">
        <font class="smaller"><i18n:message key="role-enabled-title"/></font>
      </td>
      
      <td WIDTH="50%"><dsp:input type="radio" bean="AlertGearConfigFormHandler.values.roleEnabled" value="true" checked='<%= gearEnv.getGearInstanceParameter("roleEnabled").equals("true") %>'/><font class="smaller"><i18n:message key="constant-true"/></font>&nbsp;&nbsp;&nbsp;

<dsp:input type="radio" bean="AlertGearConfigFormHandler.values.roleEnabled" value="false" checked='<%= gearEnv.getGearInstanceParameter("roleEnabled").equals("false") %>'/><font class="smaller"><i18n:message key="constant-false"/></font><br></td>

    </tr>

    <tr>
      <td width="50%" align="left">
        <font class="smaller"><i18n:message key="organization-enabled-title"/></font>
      </td>
      
      <td WIDTH="50%"><dsp:input type="radio" bean="AlertGearConfigFormHandler.values.organizationEnabled" value="true" checked='<%= gearEnv.getGearInstanceParameter("organizationEnabled").equals("true") %>' /><font class="smaller"><i18n:message key="constant-true"/></font>&nbsp;&nbsp;&nbsp;

<dsp:input type="radio" bean="AlertGearConfigFormHandler.values.organizationEnabled" value="false" checked='<%= gearEnv.getGearInstanceParameter("organizationEnabled").equals("false") %>'/><font class="smaller"><i18n:message key="constant-false"/></font><br></td>

    </tr>
    <tr>
      <td width="50%" align="left">
        <font class="smaller"><i18n:message key="user-enabled-title"/></font>
      </td>
      
      <td WIDTH="50%"><dsp:input type="radio" bean="AlertGearConfigFormHandler.values.userEnabled" value="true" checked='<%= gearEnv.getGearInstanceParameter("userEnabled").equals("true") %>'/><font class="smaller"><i18n:message key="constant-true"/></font>&nbsp;&nbsp;&nbsp;

<dsp:input type="radio" bean="AlertGearConfigFormHandler.values.userEnabled" value="false" checked='<%= gearEnv.getGearInstanceParameter("userEnabled").equals("false") %>'/><font class="smaller"><i18n:message key="constant-false"/></font><br></td>

    </tr>

     <tr valign="top" align="left"> 
      <td align="left">&nbsp;</td>
      <td align="left">
<i18n:message id="submit_update" key="constant-update"/>
<i18n:message id="submit_reset"  key="constant-reset"/>
<dsp:input type="submit" value="<%=submit_update%>" bean="AlertGearConfigFormHandler.confirm"/>&nbsp;&nbsp;&nbsp;<input type="reset" value="<%=submit_reset%>"/>
</dsp:form>
</td>
    </tr>
  </table></td>
 </tr>
</table>
<br><br>

    
</paf:hasCommunityRole>
</paf:InitializeGearEnvironment>

</dsp:page>

<%-- @version $Id: //app/portal/version/10.0.3/alert/alert.war/html/admin/configAccessParams.jsp#1 $$Change: 651360 $--%>
