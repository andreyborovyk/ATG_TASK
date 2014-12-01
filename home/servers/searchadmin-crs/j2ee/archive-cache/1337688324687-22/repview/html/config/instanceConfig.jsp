<%@ taglib uri="/rpv" prefix="rpv" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--

   Repository View Instance Configuration
 
   This page should be configured to be the page for 
   instanceConfig mode in the gear manifest and will
   be linked to from the "configure" link on the 
   Current Gears Instances Page.

   This page, displays menu options and includes the 
   appropriate configuration form page based on the value of  
   the request parameter "config_page".  

   This page and the pages it includes are for setting 
   gear instance parameter values. 

--%>

<paf:hasCommunityRole roles="leader,gear-manager">
<dsp:page>

<i18n:bundle baseName="atg.portal.gear.repview.RepViewConfigResources" localeAttribute="userLocale" changeResponseLocale="false" />
<paf:InitializeGearEnvironment id="pafEnv">
<rpv:repViewPage id="rpvPage" gearEnv="<%= pafEnv %>">

<core:demarcateTransaction id="repviewInstanceConfigXA">

<core:exclusiveIf>
  <core:ifNull value="<%= rpvPage.getRepositoryPath() %>">
    <p><i18n:message key="instanceConfigIncomplete"/><p>
  </core:ifNull>  

  <core:ifNull value="<%= rpvPage.getItemDescriptorName() %>">
    <p><i18n:message key="instanceConfigIncomplete"/><p>
  </core:ifNull>  

  <core:defaultCase>
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


<%


 String functionalityStyle   = (configPage.equals("unset") || configPage.equals("functionality"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String repositoryStyle   = (configPage.equals("Repository1") || 
                             configPage.equals("Repository2"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String delegateStyle   = (configPage.equals("delegate"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String appearanceStyle   = (configPage.equals("appearance"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String shortlistStyle   = (configPage.equals("shortlist"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String featureditemStyle   = (configPage.equals("featureditem"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String fulllistStyle   = (configPage.equals("fulllist"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
 String displayitemStyle   = (configPage.equals("displayitem"))   ?  
     " style='text-decoration:none;color:#000000;font-weight:700'" :" class='smaller'";
%>


<%-- Display config options --%>
  <table bgcolor="#cccccc" width="98%" border="0" cellspacing="0" cellpadding="4">

    <tr>
      <td>


    
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
	<a href="<%= addURL.getNewUrl() %>"><font <%= functionalityStyle %> ><i18n:message key="config-functionality-link"/></font></a>
    </core:CreateUrl>

    <font class="small">&nbsp;|&nbsp;</font>

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
	<a href="<%= addURL.getNewUrl() %>"><font <%= appearanceStyle %> ><i18n:message key="config-appearance-link"/></font></a>
    </core:CreateUrl>

<core:if value="<%= rpvPage.getDelegateConfig() %>">

    <font class="small">&nbsp;|&nbsp;</font>
    <%-- Repository (allow configuration of featured item only if will be used) --%>
    <core:exclusiveIf>
        <core:if value="<%= rpvPage.getDisplayFeaturedItem() %>">
           <core:CreateUrl id="addURL" url="<%= origURI %>">
         <core:UrlParam param="paf_dm" value="full"/>
	 <core:UrlParam param="paf_gm" value="instanceConfig"/>
	 <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	 <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
         <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	 <paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
              <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
	      <core:UrlParam param="config_page" value="configRepository1"/>
	      <a href="<%= addURL.getNewUrl() %>"><font <%= repositoryStyle %>><i18n:message key="config-repository-link"/></font></a>
           </core:CreateUrl>
        </core:if>
      <core:defaultCase>
	<font class="smaller"><i18n:message key="config-repository-link"/></font>
      </core:defaultCase>
    </core:exclusiveIf>

    <font class="small">&nbsp;|&nbsp;</font>
    <%-- Delegate (allow configuration of featured item only if will be used) --%>
    <core:exclusiveIf>
      <core:if value="<%= rpvPage.getDisplayFeaturedItem() %>">
        <core:CreateUrl id="addURL" url="<%= origURI %>">
	  <core:UrlParam param="paf_dm" value="full"/>
	  <core:UrlParam param="paf_gm" value="instanceConfig"/>
	  <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	  <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	  <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	  <paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
          <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
	  <core:UrlParam param="config_page" value="delegate"/>
	  <a href="<%= addURL.getNewUrl() %>"><font <%= delegateStyle %>><i18n:message key="config-delegate-link"/></font></a>
        </core:CreateUrl>
      </core:if>
      <core:defaultCase>
	<font class="smaller"><i18n:message key="config-delegate-link"/></font>
      </core:defaultCase>
    </core:exclusiveIf>

    <font class="small">&nbsp;|&nbsp;</font>
    <%-- FEATURED ITEM (allow configuration of featured item only if will be used) --%>
    <core:exclusiveIf>
      <core:if value="<%= rpvPage.getDisplayFeaturedItem() %>">
        <core:CreateUrl id="addURL" url="<%= origURI %>">
	  <core:UrlParam param="paf_dm" value="full"/>
	  <core:UrlParam param="paf_gm" value="instanceConfig"/>
	  <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	  <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	  <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	  <paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
          <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
	  <core:UrlParam param="config_page" value="featureditem"/>
	  <a href="<%= addURL.getNewUrl() %>"><font <%= featureditemStyle %>><i18n:message key="config-featureditem-link"/></font></a>
        </core:CreateUrl>
      </core:if>
      <core:defaultCase>
	<font class="smaller"><i18n:message key="config-featureditem-link"/></font>
      </core:defaultCase>
    </core:exclusiveIf>
    <font class="small">&nbsp;|&nbsp;</font>


    <%-- SHORTLIST (allow configuration of short list only if will be used) --%>
    <core:exclusiveIf>
      <core:if value="<%= rpvPage.getDisplayShortList() %>">
        <core:CreateUrl id="addURL" url="<%= origURI %>">
	  <core:UrlParam param="paf_dm" value="full"/>
	  <core:UrlParam param="paf_gm" value="instanceConfig"/>
	  <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	  <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	  <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	  <paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
          <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
	  <core:UrlParam param="config_page" value="shortlist"/>
	  <a href="<%= addURL.getNewUrl() %>"><font <%= shortlistStyle %> ><i18n:message key="config-shortlist-link"/></font></a>
        </core:CreateUrl>
      </core:if>
      <core:defaultCase>
	<font class="smaller"><i18n:message key="config-shortlist-link"/></font>
      </core:defaultCase>
    </core:exclusiveIf>

    <font class="small">&nbsp;|&nbsp;</font>


    <%-- FULLLIST (allow configuration of short list only if will be used) --%>
    <core:exclusiveIf>
      <core:if value="<%= rpvPage.getDisplayMoreItemsLink() %>">
        <core:CreateUrl id="addURL" url="<%= origURI %>">
	  <core:UrlParam param="paf_dm" value="full"/>
	  <core:UrlParam param="paf_gm" value="instanceConfig"/>
	  <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	  <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	  <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	  <paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
          <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
	  <core:UrlParam param="config_page" value="fulllist"/>
	  <a href="<%= addURL.getNewUrl() %>"><font <%= fulllistStyle %> ><i18n:message key="config-fulllist-link"/></font></a>
        </core:CreateUrl>
      </core:if>
      <core:defaultCase>
	<font class="smaller"><i18n:message key="config-fulllist-link"/></font>
      </core:defaultCase>
    </core:exclusiveIf>

    <font class="small">&nbsp;|&nbsp;</font>

    <%-- DISPLAYITEM --%>
    <core:CreateUrl id="addURL" url="<%= origURI %>">
	<core:UrlParam param="paf_dm" value="full"/>
	<core:UrlParam param="paf_gm" value="instanceConfig"/>
	<core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
	<core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
	<paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
	<paf:encodeUrlParam param="paf_success_url" value="<%= successURL %>"/>
        <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
	<core:UrlParam param="config_page" value="displayitem"/>
	<a href="<%= addURL.getNewUrl() %>"><font <%= displayitemStyle %> ><i18n:message key="config-displayitem-link"/></font></a>
    </core:CreateUrl>
</core:if>
    </td>
    </tr>
   </table>


  <p>

  <dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
  <dsp:importbean bean="/atg/portal/admin/SuccessMessageProcessor"/>
  <dsp:importbean bean="/atg/portal/admin/FailureMessageProcessor"/>
  <dsp:importbean bean="/atg/portal/gear/repview/RepViewConfigFormHandler"/>

  <dsp:getvalueof id="failureMessageProcessor" 
                  idtype="atg.portal.admin.I18nMessageProcessor"
                  bean="RepViewConfigFormHandler.failureMessageProcessor">
  <dsp:getvalueof id="successMessageProcessor" 
                  idtype="atg.portal.admin.I18nMessageProcessor"
                  bean="RepViewConfigFormHandler.successMessageProcessor">

<%
   failureMessageProcessor.localizeMessages(request, response);
   successMessageProcessor.localizeMessages(request, response);
%>

      <dsp:droplet name="ForEach">
         <dsp:param name="array" bean="RepViewConfigFormHandler.failureMessageProcessor.messages"/>
         <dsp:oparam name="output">
            <dsp:getvalueof id="errorMsg" idtype="java.lang.String" param="element">
            <font class="error"><dsp:img src='<%= rpvPage.getRelativeUrl("/images/error.gif")%>'
            />&nbsp;&nbsp;<%= errorMsg %></font><br/>
            </dsp:getvalueof>
         </dsp:oparam>
      </dsp:droplet>

      <%-- display info messages if any --%> 
      <dsp:droplet name="ForEach">
         <dsp:param name="array" bean="RepViewConfigFormHandler.successMessageProcessor.messages"/>
         <dsp:oparam name="output">
            <dsp:getvalueof id="errorCode" idtype="java.lang.String" param="element">
            <font class="info"><dsp:img src='<%= rpvPage.getRelativeUrl("/images/info.gif")%>'
            />&nbsp;&nbsp;<dsp:valueof param="element"/></font>
            </dsp:getvalueof>
         </dsp:oparam>
      </dsp:droplet>

<%
   failureMessageProcessor.clear();
   successMessageProcessor.clear();
%>

</dsp:getvalueof>
</dsp:getvalueof>
   <%-- Include the appropriate page according to the value of configPage --%>
   <core:ExclusiveIf>

     <core:If value='<%="appearance".equals(configPage)%>'>
        <jsp:include page="/html/config/appearance.jsp" flush="true" />
     </core:If>

      <core:If value='<%="configRepository1".equals(configPage)%>'>
        <jsp:include page="/html/config/configRepository1.jsp" flush="true" />
      </core:If>

      <core:If value='<%="configRepository2".equals(configPage)%>'>
        <jsp:include page="/html/config/configRepository1.jsp" flush="true" />
      </core:If>

      <core:If value='<%="delegate".equals(configPage)%>'>
        <jsp:include page="/html/config/delegate.jsp" flush="true" />
      </core:If>

      <core:If value='<%="shortlist".equals(configPage)%>'>
        <jsp:include page="/html/config/shortlist.jsp" flush="true" />
      </core:If>

      <core:If value='<%="featureditem".equals(configPage)%>'>
	<jsp:include page="/html/config/featureditem.jsp" flush="true" />
      </core:If>

      <core:If value='<%="fulllist".equals(configPage)%>'>
        <jsp:include page="/html/config/fulllist.jsp" flush="true" />
      </core:If>

      <core:If value='<%="displayitem".equals(configPage)%>'>
        <jsp:include page="/html/config/displayitem.jsp" flush="true" />
      </core:If>

      <core:DefaultCase>
        <jsp:include page="/html/config/functionality.jsp" flush="true" />
      </core:DefaultCase>
 
  </core:ExclusiveIf>

</core:defaultCase>
</core:exclusiveIf>

</core:demarcateTransaction>

</rpv:repViewPage>
</paf:InitializeGearEnvironment>
</dsp:page>
</paf:hasCommunityRole>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/config/instanceConfig.jsp#2 $$Change: 651448 $--%>
