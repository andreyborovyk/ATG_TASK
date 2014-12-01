<%@ taglib uri="/rpv" prefix="rpv" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--

   Repository View Appearance Configuration
 
   This page is included by instanceConfig.jsp 
   if the value of config_page == "appearance". 
   It used to configure settings that govern appearance  
   throughout the content mode of the gear.  

--%>

<dsp:page>
<paf:hasCommunityRole roles="leader">
<paf:InitializeGearEnvironment id="pafEnv">
<rpv:repViewPage id="rpvpage" gearEnv="<%= pafEnv %>">
<dsp:importbean bean="/atg/portal/gear/repview/RepViewConfigFormHandler"/>

<i18n:bundle baseName="atg.portal.gear.repview.RepViewConfigResources" 
             localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="formTitle" key="config-appearace-title"/>
<i18n:message id="formInstructions" key="about-appearance"/>
<i18n:message id="dateTimeLabel" key="datetime-label"/>
<i18n:message id="dateTimeInstructions" key="datetime-instructions"/>
<i18n:message id="dateLabel" key="date-label"/>
<i18n:message id="timeLabel" key="time-label"/>
<i18n:message id="shortDateLabel" key="short-date-label"/>
<i18n:message id="mediumDateLabel" key="medium-date-label"/>
<i18n:message id="longDateLabel" key="long-date-label"/>
<i18n:message id="fullDateLabel" key="full-date-label"/>
<i18n:message id="notShownDateLabel" key="not-shown-date-label"/>
<i18n:message id="shortTimeLabel" key="short-time-label"/>
<i18n:message id="mediumTimeLabel" key="medium-time-label"/>
<i18n:message id="longTimeLabel" key="long-time-label"/>
<i18n:message id="fullTimeLabel" key="full-time-label"/>
<i18n:message id="notShownTimeLabel" key="not-shown-time-label"/>
<i18n:message id="submitButton" key="submitButton"/>
<i18n:message id="separator" key="labelFieldSeparator"/>
<i18n:message id="resourceBundleLabel" key="resource-bundle-label"/>
<i18n:message id="resourceBundleInstructions" key="resource-bundle-instructions"/>
<i18n:message id="displayColumnHeadersLabel" key="display-column-headers-label"/>
<i18n:message id="displayColumnHeadersInstructions" key="display-column-headers-instructions"/>
<i18n:message id="displayMainItemLinkLabel" key="display-main-item-link-label"/>
<i18n:message id="displayMainItemLinkInstructions" key="display-main-item-link-instructions"/>
<i18n:message id="trueText" key="true"/>
<i18n:message id="falseText" key="false"/>

<dsp:form method="post" action="<%= pafEnv.getOriginalRequestURI() %>">

  <core:CreateUrl id="failureUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="config_page" value="appearance"/>
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
    <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
    <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.failureUrl" value="<%= failureUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <core:CreateUrl id="successUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="config_page" value="appearance"/>
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
    <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
    <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <%-- required parameters to get back to community settings --%>
  <input type="hidden" name="config_page" value="appearance"/>
  <input type="hidden" name="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
  <input type="hidden" name="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
  <input type="hidden" name="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>

  <dsp:setvalue bean="RepViewConfigFormHandler.paramType" value="instance"/>
  <dsp:setvalue bean="RepViewConfigFormHandler.settingDefaultValues" value="false"/>
  <dsp:setvalue bean="RepViewConfigFormHandler.paramNames" 
	        value="dateStyle timeStyle resourceBundle displayColumnHeaders displayMainItemLink"/>

  <dsp:input type="hidden" bean="RepViewConfigFormHandler.settingDefaultValues" value="false"/>
  <dsp:input type="hidden" bean="RepViewConfigFormHandler.paramType" value="instance"/>
  <dsp:input type="hidden" bean="RepViewConfigFormHandler.mode" value="appearance"/>


  <%@ include file="configPageTitleBar.jspf" %>

  <!-- form table -->
  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
  <tr><td>
    <table cellpadding="1" cellspacing="0" border="0">
    
    <!-- RESOURCE BUNDLE -->
    <core:if value="<%= rpvpage.getDelegateConfig() %>">
    <TR>
      <TD colspan=2 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
    </TR>

    <TR>
      <TD><font class="smaller_bold"><%= resourceBundleLabel %><%= separator %></font></TD>
      <TD><font class="smaller">
         <dsp:input type="text" size="45" bean="RepViewConfigFormHandler.values.resourceBundle"/></font></TD>
    </TR>
	
    <TR> 
      <TD colspan=2>
        <font class="smaller"><%= resourceBundleInstructions %></font></TD>
    </TR>

    <TR>
      <TD colspan=2 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>
    </core:if>

    <!-- DATE / TIME FORMAT-->

    <TR>
      <TD><font class="smaller_bold"><%= dateTimeLabel %><%= separator %></font></TD>

      <TD valign="top" align="left"><font class="smaller">
        <%= dateLabel %><%= separator %>&nbsp;&nbsp;
        <dsp:select bean="RepViewConfigFormHandler.values.dateStyle">
          <dsp:option value="short"/><%= shortDateLabel %>
          <dsp:option value="medium"/><%= mediumDateLabel %>
          <dsp:option value="long"/><%= longDateLabel %>
          <dsp:option value="full"/><%= fullDateLabel %>
          <dsp:option value="notShown"/><%= notShownDateLabel %>
        </dsp:select>

        <BR>
        <%= timeLabel %><%= separator %>&nbsp;&nbsp;
        <dsp:select bean="RepViewConfigFormHandler.values.timeStyle">
          <dsp:option value="short"/><%= shortTimeLabel %>
          <dsp:option value="medium"/><%= mediumTimeLabel %>
          <dsp:option value="long"/><%= longTimeLabel %>
          <dsp:option value="full"/><%= fullTimeLabel %>
          <dsp:option value="notShown"/><%= notShownTimeLabel %>
        </dsp:select>
        </TD>
    </TR>
	
    <TR>
       <TD colspan=2><font class="smaller"><%= dateTimeInstructions %>
      </font></TD>
    </TR>

    <TR>
      <TD colspan=2 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>

    <!-- DISPLAY COLUMN HEADERS -->
    <TR>
      <TD><font class="smaller_bold"><%= displayColumnHeadersLabel %><%= separator %></font></TD>
      <TD><font class="smaller">
         <dsp:input type="radio" size="45" bean="RepViewConfigFormHandler.values.displayColumnHeaders" value="true"/><%= trueText %>
         <dsp:input type="radio" size="45" bean="RepViewConfigFormHandler.values.displayColumnHeaders" value="false"/><%= falseText %>
      </font></TD>
    </TR>
	
    <TR> 
      <TD colspan=2>
        <font class="smaller"><%= displayColumnHeadersInstructions %></font></TD>
    </TR>

    <TR>
      <TD colspan=2 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>

    <!-- DISPLAY MAIN ITEM LINK -->
    <TR>
      <TD><font class="smaller_bold"><%= displayMainItemLinkLabel %><%= separator %></font></TD>
      <TD><font class="smaller">
         <dsp:input type="radio" size="45" bean="RepViewConfigFormHandler.values.displayMainItemLink" value="true"/><%= trueText %>
         <dsp:input type="radio" size="45" bean="RepViewConfigFormHandler.values.displayMainItemLink" value="false"/><%= falseText %>
      </font></TD>
    </TR>
	
    <TR> 
      <TD colspan=2>
        <font class="smaller"><%= displayMainItemLinkInstructions %></font></TD>
    </TR>

    <TR>
      <TD colspan=2 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>


    <%-- spacer row and SUBMIT BUTTON --%>
    <TR>
      <TD colspan=2 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=4 width=1 border=0></TD>
    </TR>

    <TR>
      <TD colspan=2 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height="10" width="1" border=0></TD>
    </TR>

    <TR VALIGN="top" ALIGN="left"> 
      <TD colspan=2>
        <dsp:input type="submit" value="<%= submitButton %>" bean="RepViewConfigFormHandler.confirm"/>
      </TD>
    </TR>
    <TR>
      <TD colspan=2><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>
  </TABLE>
  
  </td></tr></table><br><br>

</dsp:form>
</rpv:repViewPage>
</paf:InitializeGearEnvironment>
</paf:hasCommunityRole>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/config/appearance.jsp#2 $$Change: 651448 $--%>