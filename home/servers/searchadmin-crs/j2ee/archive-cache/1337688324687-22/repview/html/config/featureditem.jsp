<%@ taglib uri="/rpv" prefix="rpv" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--

   Repository View Featured Item Configuration
 
   This page is included by instanceConfig.jsp 
   if the value of config_page == "featureditem". 
   It used to configure the appearance of the 
   featured item on the shared view of the gear.  

   Params to set
      + featuredItemDisplayPropertyNames
      + featuredItemTargeter
      + customFeaturedItemDisplayPage

--%>

<dsp:page>
<paf:hasCommunityRole roles="leader">
<paf:InitializeGearEnvironment id="pafEnv">
<rpv:repViewPage id="rpvpage" gearEnv="<%= pafEnv %>">
<dsp:importbean bean="/atg/portal/gear/repview/RepViewConfigFormHandler"/>

<i18n:bundle baseName="atg.portal.gear.repview.RepViewConfigResources" 
             localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="submitButton" key="submitButton"/>
<i18n:message id="separator" key="labelFieldSeparator"/>
<i18n:message id="formTitle" key="config-featureditem-title"/>
<i18n:message id="formInstructions" key="about-featureditem"/>
<i18n:message id="targeterLabel" key="featureditem-targeter-label"/>
<i18n:message id="displayInstructionsLabel" key="display-options"/>
<i18n:message id="customDisplayInstructions" key="custom-featureditem-instructions"/>
<i18n:message id="customPageLabel" key="custom-featured-item-page-label"/>
<i18n:message id="propertyListLabel" key="featureditem-properties-instructions"/>
<i18n:message id="availableProperties" key="properties-available-for-display"/>
<i18n:message id="or" key="or"/>
<i18n:message id="selectPropertiesInstructions" key="select-properties-instructions"/>

<%
  String targeterBeanName = "RepViewConfigFormHandler.values.featuredItemTargeter";
  String customPageBeanName = "RepViewConfigFormHandler.values.customFeaturedItemDisplayPage";
  String propListBeanName = "RepViewConfigFormHandler.values.featuredItemDisplayPropertyNames";
%>

  <dsp:setvalue bean="RepViewConfigFormHandler.paramType" value="instance"/>
  <dsp:setvalue bean="RepViewConfigFormHandler.settingDefaultValues" value="false"/>
  <dsp:setvalue bean="RepViewConfigFormHandler.paramNames" 
	        value="featuredItemDisplayPropertyNames featuredItemTargeter customFeaturedItemDisplayPage"/>


<dsp:form method="post" action="<%= pafEnv.getOriginalRequestURI() %>">

  <core:CreateUrl id="failureUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="config_page" value="featureditem"/>
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
    <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
    <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.failureUrl" value="<%= failureUrl.getNewUrl() %>"/>
  </core:CreateUrl>


  <core:CreateUrl id="successUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="config_page" value="featureditem"/>
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
    <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
    <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl() %>"/>
  </core:CreateUrl>
   <dsp:input type="hidden" bean="RepViewConfigFormHandler.mode" value="featuredItem"/>

  <%-- required parameters to get back to community settings --%>
  <input type="hidden" name="config_page" value="featureditem"/>
  <input type="hidden" name="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
  <input type="hidden" name="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
  <input type="hidden" name="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>

  <dsp:input type="hidden" bean="RepViewConfigFormHandler.settingDefaultValues" value="false"/>
  <dsp:input type="hidden" bean="RepViewConfigFormHandler.paramType" value="instance"/>

  <%@ include file="configPageTitleBar.jspf" %>

  <!-- form table -->
  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
  <tr><td>
    <table cellpadding="1" cellspacing="0" border="0">

    <!-- TARGETER -->
    <TR>
      <TD colspan=3 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
    </TR>

    <TR>
      <TD><nobr><font class="smaller"><%= targeterLabel %><%= separator %></font></nobr></TD>
      <TD width=5%><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
      <TD><font class="small">
          <rpv:repositoryInfo id="repinfo"
                         repositoryPath="<%= rpvpage.getRepositoryPath() %>" 
                         itemDescriptorName="<%= rpvpage.getItemDescriptorName() %>">
            <core:exclusiveIf>
              <core:ifNull value="<%= repinfo.getTargeters() %>">
                <i18n:message key="no-targeters-available">
                   <i18n:messageArg value="<%= rpvpage.getRepositoryPath() %>"/>
                   <i18n:messageArg value="<%= rpvpage.getItemDescriptorName() %>"/>
                </i18n:message>
              </core:ifNull>
              <core:defaultCase>   
                <dsp:select bean="<%= targeterBeanName %>">
                  <dsp:option value=""/>
                  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                    <dsp:param name="array" value="<%= repinfo.getTargeters() %>"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof id="ele" idtype="atg.repository.nucleus.RepositoryContainer.Entry" param="element">
                      <dsp:option value="<%= ele.getAbsoluteName() %>"/><%= ele.getName() %>
                      </dsp:getvalueof>
                    </dsp:oparam>
                  </dsp:droplet>
                </dsp:select>
              </core:defaultCase>
            </core:exclusiveIf>
          </rpv:repositoryInfo>
	</font>
      </TD>
    </TR>
	
    <TR>
      <TD colspan=3 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=15 width=1 border=0></TD>
    </TR>

    <!-- CUSTOM DISPLAY OPTIONS -->

    <TR>
      <TD valign="top"><font class="smaller"><%= displayInstructionsLabel %><%= separator %></font></TD>
      <TD width=5%><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
      <TD><font class="smaller"><%= customDisplayInstructions %></font></TD>
    </TR>

    <TR>
      <TD colspan=3 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
    </TR>

    <TR>
      <TD><font class="smaller"><%= customPageLabel %><%= separator %></font></TD>
      <TD width=5%><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
      <TD><font class="smaller">
         <dsp:input type="text" size="45" bean="<%= customPageBeanName %>"/></font></TD>
    </TR>

    <TR>
      <TD colspan=3 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
    </TR>

    <TR>
      <TD colspan=3 ><font class="small_bold"><%= or %></font></TD>
    </TR>

    <TR>
      <TD colspan=3 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
    </TR>

    <TR>
      <TD valign="top"><font class="smaller"><%= propertyListLabel %><%= separator %></font></TD>
      <TD width=5%><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=7 width=1 border=0></TD>
      <TD>
	<font class="smaller">
          <%= selectPropertiesInstructions %>
	  <p>
          <core:ForEach id="foreach"
             values="<%= rpvpage.getBasePropertyList() %>"
             castClass="java.lang.String"
             elementId="propName">
             <code><%= propName %>&nbsp;</code>
          </core:ForEach>
	  <p>
           <dsp:textarea cols="35" rows="4"
             bean="<%= propListBeanName %>"
             wrap="true"></dsp:textarea>
       </font>
      </TD>
    </TR>
     
    <TR>
      <TD colspan=3 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>

     <TR>
      <TD colspan=3 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=4 width=1 border=0></TD>
    </TR>

    <TR>
      <TD colspan=3 ><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height="10" width="1" border=0></TD>
    </TR>

    <TR VALIGN="top" ALIGN="left"> 
      <TD colspan=3>
        <dsp:input type="submit" value="<%= submitButton %>" bean="RepViewConfigFormHandler.confirm"/>
      </TD>
    </TR>
    <TR>
      <TD colspan=3><img src='<%= rpvpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>
  </TABLE>
  
  </td></tr></table><br><br>



</dsp:form>
</rpv:repViewPage>
</paf:InitializeGearEnvironment>
</paf:hasCommunityRole>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/config/featureditem.jsp#2 $$Change: 651448 $--%>
