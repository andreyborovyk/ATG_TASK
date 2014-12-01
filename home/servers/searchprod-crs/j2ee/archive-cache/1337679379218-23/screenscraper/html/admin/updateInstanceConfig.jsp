<%@ taglib uri="/dsp-taglib" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>
<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">
<%@ include file="../includeBundle.jspf" %>
<i18n:message id="cancelButton" key="updateConfigCancelButton"/>
<i18n:message id="finishButton" key="updateConfigFinishButton"/>
<i18n:message id="configureHeading" key="updateConfigConfigureHeading">
  <i18n:messageArg value="<%=pafEnv.getGear().getName(response.getLocale())%>" /> 
</i18n:message> 
<i18n:message id="sharedURLLabel" key="updateConfigSharedURLTextBoxLabel"/>
<i18n:message id="fullURLLabel" key="updateConfigFullURLTextBoxLabel"/>
<i18n:message id="fullModeLinkTextLabel" key="updateConfigFullModeLinkTextLabel"/>
<i18n:message id="resourceBundleLabel" key="updateResourceBundleLabel"/>

<%
   String origURI= pafEnv.getOriginalRequestURI(); 
   String gearID=pafEnv.getGear().getId();
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String communityID = request.getParameter("paf_community_id");
   String pafSuccessURL = request.getParameter("paf_success_url");
   String communityAdminURI="/portal/settings/community_gears.jsp";
   String contextPath = pafEnv.getGearDefinition().getServletContext();
%>
<dsp:importbean bean="/atg/portal/gear/screenscraper/ScreenScraperGearConfigFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>


  <!-- Display any information messages  -->
  <dsp:droplet name="/atg/dynamo/droplet/Switch">
    <dsp:param name="value" bean="ScreenScraperGearConfigFormHandler.formInfo"/>
    <dsp:oparam name="true">
     <br>
      <dsp:droplet name="/atg/dynamo/droplet/ForEach">
       <font class="info">   
        <dsp:param name="array" bean="ScreenScraperGearConfigFormHandler.formMessages"/>
        <dsp:oparam name="output">
          <img src='<%= response.encodeURL("images/info.gif")%>' >&nbsp;&nbsp;<dsp:valueof param="element"/><br><br>
        </dsp:oparam>
       </font>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
  
  <!-- display errors if any -->
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
         <dsp:param name="value" bean="ScreenScraperGearConfigFormHandler.formError" />
         <dsp:oparam name="true">
         <font class="error"><STRONG><UL>
            <dsp:droplet name="ErrorMessageForEach">
               <dsp:param name="exceptions" bean="ScreenScraperGearConfigFormHandler.formExceptions"/>
               <dsp:oparam name="output">
               <LI> <dsp:valueof param="message"/>
               </dsp:oparam>
            </dsp:droplet>
            </UL></STRONG></font>
         </dsp:oparam>
      </dsp:droplet>
  
  
<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="pageheader_edit"><%= configureHeading %></font>
</td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
<font class="smaller"><i18n:message key="helperTextInstanceConfig"/></font>
</td></tr></table>
		
<img src="<%= contextPath %>/images/clear.gif" height=1 width=1 border=0><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">


<dsp:form method="post" action="<%= origURI %>">
  <dsp:input type="hidden" bean="ScreenScraperGearConfigFormHandler.settingDefaultValues" value="false"/>

  <input type="hidden" name="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_page_id" value="<%= pageID %>"/>
  <input type="hidden" name="paf_page_url" value="<%= pageURLEncoded %>"/>
  <input type="hidden" name="paf_community_id" value="<%= communityID %>"/>
  <input type="hidden" name="paf_success_url" value="<%= pafSuccessURL %>"/>
  <dsp:input type="hidden" bean="ScreenScraperGearConfigFormHandler.paramType" value="instance"/>

  <core:CreateUrl id="thisPageURL" url="<%= origURI %>">
    <core:UrlParam param="paf_dm" value="full"/>
    <core:UrlParam param="paf_gm" value="instanceConfig"/>
    <core:UrlParam param="config_page" value="Update"/>
    <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
    <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
    <paf:encodeUrlParam param="paf_success_url" value="<%= pafSuccessURL %>"/> 
    <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
    <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
    <dsp:input type="hidden" bean="ScreenScraperGearConfigFormHandler.successUrl" value="<%= thisPageURL.getNewUrl()%>"/>    
    <dsp:input type="hidden" bean="ScreenScraperGearConfigFormHandler.failureUrl" value="<%= thisPageURL.getNewUrl()%>"/>
  </core:CreateUrl>
  
<tr><td>
<table cellpadding="3" cellspacing="0" border="0">

<tr>
      <td><font class="smaller"><%= sharedURLLabel %></font><br>
	  <dsp:input type="text" size="60"
 bean="ScreenScraperGearConfigFormHandler.instanceSharedURL"  maxlength="254"/></b></td>
</tr>

<tr>
      <td><font class="smaller"><%= fullURLLabel %></font><br>
	  <dsp:input type="text" size="60"
 bean="ScreenScraperGearConfigFormHandler.instanceFullURL" maxlength="254"/></b>
</td>
</tr>

<tr>
      <td><font class="smaller"><%= fullModeLinkTextLabel %></font><br>
	  <dsp:input type="text" size="60"
 bean="ScreenScraperGearConfigFormHandler.fullModeLinkText" maxlength="254"/></b>
</td>
</tr>
<tr>
<td><font class="smaller"><%= resourceBundleLabel %></font><br>
    <dsp:input type="text" size="60"
 bean="ScreenScraperGearConfigFormHandler.resourceBundleName" maxlength="254"/>
</b></td>
</tr>
  <tr>
     <td><dsp:input type="submit" value="<%=  finishButton %>" bean="ScreenScraperGearConfigFormHandler.confirm"/>
&nbsp;&nbsp;&nbsp;
<dsp:input type="submit" value="<%=  cancelButton %>" bean="ScreenScraperGearConfigFormHandler.cancel"/></td>
  </tr>

</table>


</td></tr>
</dsp:form>
</table>
<br><br>
 
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/screenscraper/screenscraper.war/html/admin/updateInstanceConfig.jsp#2 $$Change: 651448 $--%>
