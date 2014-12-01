<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<paf:InitializeGearEnvironment id="gearEnv">
<paf:hasCommunityRole roles="leader,gear-manager">

<i18n:bundle baseName="atg.portal.gear.alert.alert" localeAttribute="userLocale" changeResponseLocale="false" />

<i18n:message key="constant-cancel" id="dsp_constant_cancel"/>
<i18n:message key="constant-finish" id="dsp_constant_finish"/>

<dsp:importbean bean="/atg/portal/gear/alert/AlertGearConfigFormHandler" />

<% String origURI = gearEnv.getOriginalRequestURI();
   String gearId = gearEnv.getGear().getId();
   String pageId = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String communityId = request.getParameter("paf_community_id");
   String successURL =  request.getParameter("paf_success_url");
   String clearGif = gearEnv.getGear().getServletContext() + "/html/images/clear.gif";
%>

<%--
  <br><br><font class="error"><b><i18n:message key="<%= yourAlert.getErrorMessage()%>" /></b></font><br><br>

  <br><br><font class="info"><b><i18n:message key="<%= yourAlert.getInfoMessage()%>" /></b></font><br><br>
--%>

<i18n:message id="i18n_quote" key="html_quote"/>
<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="pageheader_edit">
 <i18n:message key="config-display-param-title">
  <i18n:messageArg value='<%= gearEnv.getGear().getName(response.getLocale()) %>'/>
   <i18n:messageArg value="<%=i18n_quote%>"/>
 </i18n:message></font>
</td></tr></table>
</td></tr></table>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
<font class="smaller"><i18n:message key="admin_configure_instance_count"/>&nbsp;</font>
</td></tr></table>
<img src="<%=clearGif%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>&nbsp;
<dsp:form enctype="multipart/form-data" method="post" action="<%= origURI %>" >
  <input type="hidden" name="config_page" value="displayParams" />
  <input type="hidden" name="paf_gear_id" value="<%= gearId %>" />
  <input type="hidden" name="paf_dm" value="<%= gearEnv.getDisplayMode() %>" />
  <input type="hidden" name="paf_gm" value="<%= gearEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_page_id" value="<%= pageId %>" />
  <input type="hidden" name="paf_page_url" value="<%= pageURL %>" />
  <input type="hidden" name="paf_community_id" value="<%= communityId %>" />
  <input type="hidden" name="paf_success_url" value="<%= successURL %>" />

  <core:CreateUrl id="successUrl" url="<%= origURI %>">
  <core:UrlParam param="config_page" value="displayParams" />
    <core:UrlParam param="paf_dm" value="<%= gearEnv.getDisplayMode() %>" />
    <core:UrlParam param="paf_gm" value="instanceConfig" />
    <core:UrlParam param="paf_gear_id" value="<%= gearId %>" />
    <core:UrlParam param="paf_page_id" value="<%= pageId %>" />
    <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>" />
    <core:UrlParam param="paf_community_id" value="<%= communityId %>" />
    <dsp:input type="hidden" bean="AlertGearConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl() %>" />
    <dsp:input type="hidden" bean="AlertGearConfigFormHandler.failureUrl" value="<%= successUrl.getNewUrl() %>" />
    <dsp:input type="hidden" bean="AlertGearConfigFormHandler.cancelUrl" value="<%= successUrl.getNewUrl() %>" />
  </core:CreateUrl>
</td><td>
<table cellpadding="2" cellspacing="0" border="0" width="60%">
    <tr>
      <td width="50%" align="left">
        <font class="smaller"><i18n:message key="shared-display-count-title"/></font>:&nbsp;
      </td>
      
      <td WIDTH="50%"><dsp:input type="text" size="5" maxlength="5" bean="AlertGearConfigFormHandler.values.sharedDisplayCount" value='<%= gearEnv.getGearInstanceParameter("sharedDisplayCount")%>'/></td>
    </tr>

    <tr valign="top" align="left"> 
      <td align="right">&nbsp;</td>
      <td align="left"> 

<i18n:message id="submit_update" key="constant-update"/>
<i18n:message id="submit_reset"  key="constant-reset"/>
<dsp:input type="submit" bean="AlertGearConfigFormHandler.confirm" value="<%=submit_update%>"/>&nbsp;&nbsp;&nbsp;<input type="reset" value="<%=submit_reset%>"/></td>
    </tr>
</table>
<br><br>
</dsp:form>
</td>
</tr>
</table>


</paf:hasCommunityRole>
</paf:InitializeGearEnvironment>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/alert/alert.war/html/admin/configDisplayParams.jsp#1 $$Change: 651360 $--%>

