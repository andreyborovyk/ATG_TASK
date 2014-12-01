<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/docexch-taglib" prefix="dex" %>

<paf:hasCommunityRole roles="leader,gear-manager">
<%-- InstanceConfig.jsp --%>
<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">
<dex:DocExchPage id="dexpage" gearEnv="<%= pafEnv %>">

<dsp:importbean bean="/atg/portal/gear/docexch/DocExchConfigFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<% 
   String origURI= pafEnv.getOriginalRequestURI();
   String gearID = pafEnv.getGear().getId();
   String gearName = pafEnv.getGear().getName(response.getLocale());
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String successURL = request.getParameter("paf_success_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String communityID = request.getParameter("paf_community_id");

   String configPage = request.getParameter("config_page");
   if (configPage == null)
	configPage = "unset";
%>


<i18n:bundle baseName="<%= dexpage.getResourceBundle() %>" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="i18n_link_functionality" key="config-functionality-link"/>
<i18n:message id="i18n_link_alert" key="config-alerts-link"/>
<i18n:message id="i18n_link_appearance" key="config-appearance-link"/>
<i18n:message id="i18n_link_permission" key="config-permission-link"/>

<%
 String functionalityStyle   = (configPage.equals("functionality"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String alertStyle   = (configPage.equals("alert"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String appearanceStyle   = (configPage.equals("unset") || configPage.equals("appearance"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String permissionStyle   = (configPage.equals("permission"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
%>



<%-- Display config options --%>
  <table bgcolor="#cccccc" width="98%" border="0" cellspacing="0" cellpadding="4">

    <tr>
      <td><font class="smaller">

    <%-- APPEARANCE --%>
    <core:CreateUrl id="addURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
        <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
	<core:UrlParam param="config_page" value="appearance"/>
	<a href="<%= addURL.getNewUrl() %>"><font <%= appearanceStyle%> ><%= i18n_link_appearance %></font></a>
    </core:CreateUrl>

    <font class="small">&nbsp;|&nbsp;</font>
    
    <%-- PERMISSION --%>
    <core:CreateUrl id="addURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
        <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
	<core:UrlParam param="config_page" value="permission"/>
	<a href="<%= addURL.getNewUrl() %>"><font <%= permissionStyle%> ><%= i18n_link_permission %></font></a>
    </core:CreateUrl>

    <font class="small">&nbsp;|&nbsp;</font>
    
    <%-- FUNCTIONALITY --%>
    <core:CreateUrl id="addURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
        <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
	<core:UrlParam param="config_page" value="functionality"/>
	<a href="<%= addURL.getNewUrl() %>"><font <%= functionalityStyle%> ><%= i18n_link_functionality %></font></a>
    </core:CreateUrl>

    <font class="small">&nbsp;|&nbsp;</font>

    <%-- ALERT --%>
    <core:CreateUrl id="addURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
        <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
	<core:UrlParam param="config_page" value="alert"/>
	<a href="<%= addURL.getNewUrl() %>"><font <%= alertStyle%> ><%= i18n_link_alert %></font></a>
    </core:CreateUrl>

     </td>
    </tr>
   </table>


  <p>

      <!-- display errors if any -->
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
         <dsp:param name="value" bean="DocExchConfigFormHandler.formError" />
         <dsp:oparam name="true">
            <dsp:droplet name="ForEach">
               <dsp:param name="array" bean="DocExchConfigFormHandler.formExceptions"/>
               <dsp:oparam name="output">
                  <dsp:getvalueof id="errorMsg" idtype="java.lang.String" param="element">
                  <font class="error"><img src='<%= response.encodeURL("images/info.gif")%>'
                  >&nbsp;&nbsp;<i18n:message key="<%= errorMsg %>"/></font><br/>
                  </dsp:getvalueof>
               </dsp:oparam>
            </dsp:droplet>
            <dsp:setvalue bean="DocExchConfigFormHandler.resetFormExceptions"/>
         </dsp:oparam>
      </dsp:droplet>

      <%-- display info messages if any --%> 
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
         <dsp:param name="value" bean="DocExchConfigFormHandler.formInfo"/>
         <dsp:oparam name="true">
            <dsp:droplet name="ForEach">
               <dsp:param name="array" bean="DocExchConfigFormHandler.formMessages"/>
               <dsp:oparam name="output">
                  <font class="info"><img src='<%= response.encodeURL("images/info.gif")%>'
                  >&nbsp;&nbsp;<dsp:valueof param="element"/></font>
               </dsp:oparam>
            </dsp:droplet>
            <dsp:setvalue bean="DocExchConfigFormHandler.resetFormMessages"/> 
         </dsp:oparam>
      </dsp:droplet>
  
  



  <dsp:droplet name="/atg/dynamo/droplet/Switch">
    <dsp:param name="value" param="config_page" />

    <dsp:oparam name="default">
      <dsp:include page="configAppearance.jsp" flush="false" />
    </dsp:oparam>

    <dsp:oparam name="appearance">
      <dsp:include page="configAppearance.jsp" flush="false" />
    </dsp:oparam>

    <dsp:oparam name="permission">
      <dsp:include page="configPermissions.jsp" flush="false" />
    </dsp:oparam>

    <dsp:oparam name="functionality">
      <dsp:include page="configFunctionality.jsp" flush="false" />
    </dsp:oparam>

    <dsp:oparam name="alert">
      <dsp:include page="configAlerts.jsp" flush="false" />
    </dsp:oparam>

  </dsp:droplet>


</dex:DocExchPage>
</paf:InitializeGearEnvironment>

</dsp:page>
</paf:hasCommunityRole>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/InstanceConfig.jsp#2 $$Change: 651448 $--%>
