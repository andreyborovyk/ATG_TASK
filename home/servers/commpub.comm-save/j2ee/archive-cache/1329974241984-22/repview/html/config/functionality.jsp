<%@ taglib uri="/rpv" prefix="rpv" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--

   Repository View Functionality Configuration
 
   This page is included by instanceConfig.jsp 
   if the value of config_page == "functionality". 
   It used to configure which parts of the possible 
   gear functionality to use.  

--%>

<dsp:page>
<paf:hasCommunityRole roles="leader">
<paf:InitializeGearEnvironment id="pafEnv">
<rpv:repViewPage id="rpvpage" gearEnv="<%= pafEnv %>">
<dsp:importbean bean="/atg/portal/gear/repview/RepViewConfigFormHandler"/>

<i18n:bundle baseName="atg.portal.gear.repview.RepViewConfigResources" 
             localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="formTitle" key="config-functionality-title"/>
<i18n:message id="formInstructions" key="about-functionality"/>
<i18n:message id="display_shortlist_label" key="display-shortlist-label"/>
<i18n:message id="display_shortlist_instructions" key="display-shortlist-instructions"/>
<i18n:message id="display_featureditem_label" key="display-featureditem-label"/>
<i18n:message id="display_featureditem_instructions" key="display-featureditem-instructions"/>
<i18n:message id="display_fulllistlink_label" key="display-fulllistlink-label"/>
<i18n:message id="display_fulllistlink_instructions" key="display-fulllistlink-instructions"/>
<i18n:message id="submitButton" key="submitButton"/>
<i18n:message id="separator" key="labelFieldSeparator"/>
<i18n:message id="trueText" key="true"/>
<i18n:message id="falseText" key="false"/>

<dsp:form method="post" action="<%= pafEnv.getOriginalRequestURI() %>">

  <core:CreateUrl id="failureUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="config_page" value="functionality"/>
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
    <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
    <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.failureUrl" value="<%= failureUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <core:CreateUrl id="successUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="config_page" value="functionality"/>
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
    <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
    <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <%-- required parameters to get back to community settings --%>
  <input type="hidden" name="config_page" value="functionality"/>
  <input type="hidden" name="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
  <input type="hidden" name="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
  <input type="hidden" name="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>

  <dsp:input type="hidden" bean="RepViewConfigFormHandler.settingDefaultValues" value="false"/>
  <dsp:input type="hidden" bean="RepViewConfigFormHandler.paramType" value="instance"/>
  <dsp:input type="hidden" bean="RepViewConfigFormHandler.mode" value="functionality"/>

  <dsp:setvalue bean="RepViewConfigFormHandler.paramType" value="instance"/>
  <dsp:setvalue bean="RepViewConfigFormHandler.settingDefaultValues" value="false"/>
  <dsp:setvalue bean="RepViewConfigFormHandler.paramNames" 
	        value="displayShortList displayFeaturedItem displayMoreItemsLink"/>

  <%@ include file="configPageTitleBar.jspf" %>

  <!-- form table -->
  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
  <tr><td>
    <table cellpadding="1" cellspacing="0" border="0">

    <!-- DISPLAY SHORTLIST? -->
    <TR>
      <TD colspan=2 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
    </TR>

    <TR>
      <TD><font class="smaller"><%= display_shortlist_label %><%= separator %></font></TD>
    </TR>
	
    <TR> 
      <TD width="75%"><font class="smaller">
         <dsp:input type="checkbox" bean="RepViewConfigFormHandler.values.displayShortList" 
          value="true"/><%= display_shortlist_instructions %>
      </font></td>
    </TR>

    <TR>
      <TD colspan=2 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>

    <!-- DISPLAY FEATURED ITEM? -->

    <TR>
      <TD colspan=2><font class="smaller"><%= display_featureditem_label %><%= separator %></font></TD>
    </TR>	
	
    <TR> 
      <TD width="75%" valign="top" align="left"><font class="smaller">
         <dsp:input type="checkbox" bean="RepViewConfigFormHandler.values.displayFeaturedItem" 
           value="true"/><%= display_featureditem_instructions %>
      </font></td>
    </TR>

    <TR>
      <TD colspan=2 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>

    <!-- DISPLAY LINK TO FULLLIST? -->

    <TR>
      <TD colspan=2><font class="smaller"><%= display_fulllistlink_label %><%= separator %></font></TD>
    </TR>	
	
    <TR> 
      <TD width="75%" valign="top" align="left"><font class="smaller">
        <dsp:input type="checkbox" bean="RepViewConfigFormHandler.values.displayMoreItemsLink" 
           value="true"/><%= display_fulllistlink_instructions %>
      </font></td>
    </TR>

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
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/config/functionality.jsp#2 $$Change: 651448 $--%>
