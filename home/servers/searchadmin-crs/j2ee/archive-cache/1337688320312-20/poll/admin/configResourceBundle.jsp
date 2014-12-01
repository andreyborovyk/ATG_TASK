<%-- 
Page:   	configResourceBundle.jsp
Gear:  	 	Poll Gear
gearmode: 	instanceConfig
displayMode: 	full

Author:      	Jeff Banister
Description: 	This page is included by instanceConfig.jsp, and renders and handles a form to 
      		set the resourceBundle parameter.
--%>

<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<i18n:bundle baseName="atg.gear.poll.PollResources" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="submitButtonLabel" key="submit-button-label"/>

<paf:InitializeGearEnvironment id="pafEnv">

<% String origURI= pafEnv.getOriginalRequestURI();
   String gearID=pafEnv.getGear().getId();
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String communityID = request.getParameter("paf_community_id");
   String pafSuccessURL = request.getParameter("paf_success_url");
   String pafSuccessURLEncoded = atg.servlet.ServletUtil.escapeURLString(pafSuccessURL);
%>

<dsp:importbean bean="/atg/portal/gear/poll/PollConfigFormHandler"/>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="PollConfigFormHandler.formError"/>
  <dsp:oparam name="true">
    <font class="error"><UL>
      <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
        <dsp:param name="exceptions" bean="PollConfigFormHandler.formExceptions"/>
        <dsp:oparam name="output">
          <LI> <dsp:valueof param="message"></dsp:valueof>
        </dsp:oparam>
      </dsp:droplet>
    </UL></font>
  </dsp:oparam>
  <dsp:oparam name="false">
    <core:ifNotNull value='<%=request.getParameter("formSubmit")%>'>
      &nbsp;<img src="/gear/poll/images/info.gif">&nbsp;&nbsp;<font class="smaller"><i18n:message key="resource-bundle-feedback-msg"/></font>
    </core:ifNotNull>
  </dsp:oparam>
</dsp:droplet>


<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit">
<i18n:message key="admin_resource_config_title"/>
</td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
        <font class="smaller"><i18n:message key="admin_resource_config_intro"/>
        </td></tr></table>
  <img src="/gear/poll/images/clear.gif" height=1 width=1 border=0><br>


<%-- Taking back link out 
<tr>
  <core:CreateUrl id="selectURL" url="<%= origURI%>">
         <core:UrlParam param="paf_dm" value="full"/>
         <core:UrlParam param="paf_gm" value="instanceConfig"/>
	 <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
         <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
         <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
         <paf:encodeUrlParam param="paf_success_url" value="<%= pafSuccessURL %>"/>
         <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
    <td colspan="2" align="left">
    <font class="small"><a href="<%= selectURL.getNewUrl() %>">&laquo;&nbsp;<i18n:message key="backtogear-link"/></font></a>
    </td>
  </core:CreateUrl>
</tr>
--%>


<dsp:setvalue bean="PollConfigFormHandler.paramType" value="instance"/>
<dsp:setvalue bean="PollConfigFormHandler.settingDefaultValues" value="false"/>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">

<dsp:form enctype="multipart/form-data" method="post" action="<%= origURI %>">

<dsp:input type="hidden" bean="PollConfigFormHandler.paramType" value="instance"/>
<dsp:input type="hidden" bean="PollConfigFormHandler.settingDefaultValues" value="false"/>
<dsp:setvalue bean="PollConfigFormHandler.paramNames" value="resourceBundle"/>
  <input type="hidden" name="paf_gear_id" value="<%= gearID %>"/>
  <input type="hidden" name="paf_page_id" value="<%= pageID %>"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_page_url" value="<%= pageURLEncoded %>"/>
  <input type="hidden" name="paf_community_id" value="<%= communityID %>"/>
  <input type="hidden" name="paf_success_url" value="<%= pafSuccessURLEncoded %>"/>
  <input type="hidden" name="formSubmit" value="true"/>

  <core:CreateUrl id="successUrl" url="<%= origURI %>">
    <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
    <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="config_page" value="resourceBundle"/>
    <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
    <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
    <core:UrlParam param="formSubmit" value="true"/>
    <paf:encodeUrlParam param="paf_success_url" value="<%= pafSuccessURL%>"/>
    <dsp:input type="hidden" bean="PollConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl()%>"/>
  </core:CreateUrl>



<tr><td>
<table cellpadding="1" cellspacing="0" border="0">



    <tr>
      <td width="50%"><font class="smaller"><i18n:message key="resourceBundleLabel"/></font></td>
    </tr>
    <tr>
      <td colspan=2><img src="/gear/poll/images/clear.gif" height=5 width=1 border=0></td>
    </tr>
    <tr>
      <td WIDTH="50%"><dsp:input type="text" size="50" maxlength="100" 
	bean="PollConfigFormHandler.values.resourceBundle"/>
      </td>
    </tr>

    <tr>
      <td colspan=2 ><img src="/gear/poll/images/clear.gif" height=10 width=1 border=0></td>
    </tr>
    <tr VALIGN="top" ALIGN="left"> 
      <td><dsp:input type="submit" value="<%=submitButtonLabel%>" bean="PollConfigFormHandler.confirm"/></td>
      <td align="left">&nbsp;&nbsp;&nbsp;
      </td>
    </tr>
</dsp:form>
  </TABLE>



</td></tr></table><br><br>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/poll/poll.war/admin/configResourceBundle.jsp#2 $$Change: 651448 $--%>
