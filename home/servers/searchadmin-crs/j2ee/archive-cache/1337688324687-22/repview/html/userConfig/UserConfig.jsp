<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/rpv" prefix="rpv" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<dsp:importbean bean="/atg/portal/gear/repview/RepViewConfigFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<rpv:repViewPage id="rpvPage" gearEnv="<%= gearEnv %>">

<i18n:bundle baseName="<%= rpvPage.getResourceBundle() %>" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="userConfigPageTitle" key="userConfigPageTitle"/>
<i18n:message id="userConfigFullListItemCountLabel" key="userConfigFullListItemCountLabel"/>
<i18n:message id="userConfigFullListLabel" key="userConfigFullListLabel"/>
<i18n:message id="userConfigShortListItemCountLabel" key="userConfigShortListItemCountLabel"/>
<i18n:message id="userConfigShortListLabel" key="userConfigShortListLabel"/>
<i18n:message id="userConfigShortListSortProperty" key="userConfigShortListSortProperty"/>
<i18n:message id="userConfigShortListSortOrder" key="userConfigShortListSortOrder"/>
<i18n:message id="sortAscending" key="sortAscending"/>
<i18n:message id="sortDecending" key="sortDecending"/>
<i18n:message id="cancelButton" key="cancelButton"/>
<i18n:message id="updateButton" key="updateButton"/>
<i18n:message id="finishButton" key="finishButton"/>

<%
   String clearImageUrl = rpvPage.getRelativeUrl("/images/clear.gif");
%>

<dsp:setvalue bean="RepViewConfigFormHandler.initializeDefaultValues" value="foo"/>

<dsp:form method="post" action="<%= gearEnv.getOriginalRequestURI() %>">

  <core:CreateUrl id="successUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="config_page" value="functionality"/>
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
    <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
    <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
    <core:UrlParam param="paf_dm" value="<%=gearEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=gearEnv.getGearMode() %>"/>
    <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl() %>"/>
    <dsp:input type="hidden" bean="RepViewConfigFormHandler.failureUrl" value="<%= successUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <dsp:input type="hidden" bean="RepViewConfigFormHandler.mode" value="userconfig"/>

  <%-- 3 hidden params indicate which user specific parameter values we are setting --%> 	
  <dsp:setvalue bean="RepViewConfigFormHandler.paramType" value="user"/>
  <dsp:setvalue bean="RepViewConfigFormHandler.settingDefaultValues" value="false"/>
  <dsp:setvalue bean="RepViewConfigFormHandler.paramNames" value="shortListSize fullListSize"/>

  <input type="hidden" name="paf_gear_id" value="<%= gearEnv.getGear().getId()%>"/>
  
  <TABLE WIDTH="667" BORDER="0" CELLSPACING="0" CELLPADDING="0">

    <TR>
      <TD colspan=2>
        <font class="medium_bold"><%= userConfigPageTitle %></font>
      </TD>
    </TR>

    <TR>
      <TD colspan=2 bgcolor="#000033"><img src="<%= clearImageUrl %>" height=1 width=1 border=0></TD>
    </TR>

    <TR>
      <TD colspan=2>
     <!-- display errors if any -->
     <%-- Page fragment that renders form success and failure messages. --%>

   <dsp:importbean bean="/atg/portal/admin/SuccessMessageProcessor"/>
   <dsp:importbean bean="/atg/portal/admin/FailureMessageProcessor"/>

   <dsp:getvalueof id="failureMessageProcessor" idtype="atg.portal.admin.I18nMessageProcessor" bean="FailureMessageProcessor">
     <dsp:getvalueof id="successMessageProcessor" idtype="atg.portal.admin.I18nMessageProcessor" bean="SuccessMessageProcessor">
<%
 failureMessageProcessor.localizeMessages(request, response);
 successMessageProcessor.localizeMessages(request, response);
%>

       <%-- Previous submission success/failure reporting --%>
       <dsp:getvalueof id="successMessages" idtype="java.util.List" bean="SuccessMessageProcessor.messages">
         <core:ForEach id="successIterator"
                       values="<%=successMessages%>"
                       castClass="String"
                       elementId="successMessage">
            <img src='<%= rpvPage.getRelativeUrl("/images/info.gif") %>'/>&nbsp;&nbsp;<font class="info"><%=successMessage%></font><br>
         </core:ForEach>
       </dsp:getvalueof><%-- successMessages --%>

       <dsp:getvalueof id="failureMessages" idtype="java.util.List" bean="FailureMessageProcessor.messages">
         <core:ForEach id="failureIterator"
                       values="<%=failureMessages%>"
                       castClass="String"
                       elementId="failureMessage">
           <img src='<%= rpvPage.getRelativeUrl("/images/error.gif") %>'/>&nbsp;&nbsp;<font class="error"><%=failureMessage%></font><br>
         </core:ForEach>
       </dsp:getvalueof><%-- failureMessages --%>

<%
  failureMessageProcessor.clear();
  successMessageProcessor.clear();
%>
     </dsp:getvalueof><%-- successMessages --%>
   </dsp:getvalueof><%-- failureMessages --%>

      </TD>
    </TR>  

    <TR>
      <TD colspan=2><img src="<%= clearImageUrl %>" height=10 width=1 border=0></TD>
    </TR>


    <TR>
      <TD colspan=2>
        <font class="small"><%= userConfigFullListItemCountLabel %></font>
      </TD>
    </TR>

    <TR VALIGN="middle" ALIGN="left"> 
      <TD WIDTH="25%" align="right"><font class="small"><%= userConfigFullListLabel %>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="75%"><dsp:input type="text" size="5"
           bean="RepViewConfigFormHandler.values.fullListSize"/></TD>
    </TR>

    <TR>
      <TD colspan=2><img src="<%= clearImageUrl %>" height=10 width=1 border=0></TD>
    </TR>

    <TR>
      <TD colspan=2>
        <font class="small"><%= userConfigShortListItemCountLabel %></font> 
      </TD>
    </TR>

    <TR VALIGN="middle" ALIGN="left"> 
      <TD WIDTH="25%" align="right"><font class="small"><%= userConfigShortListLabel %>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="75%"><dsp:input type="text" size="5"
           bean="RepViewConfigFormHandler.values.shortListSize"/></TD>
    </TR>

<%-- 
REMOVE SORTING BECAUSE DYNAMIC SORTING DOESN'T WORK AS EXPECTED WITH TARGETING

    <TR>
      <TD colspan=2><img src="<%= clearImageUrl %>" height=10 width=1 border=0></TD>
    </TR>

    <TR VALIGN="middle" ALIGN="left"> 
      <TD WIDTH="25%" align="right"><font class="small"><%= userConfigShortListSortProperty %>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="75%">
         <dsp:select bean="RepViewConfigFormHandler.values.shortListSortProperty">
           <rpv:propertyList id="proplist"
                           itemDescriptorName="<%= rpvPage.getItemDescriptorName() %>"
                           displayType="shortlist"
                           gearEnv="<%= gearEnv %>">
           <core:forEach id="properties" 
                         values="<%= proplist.getDisplayProperties() %>"
                         castClass="atg.beans.DynamicPropertyDescriptor"
                         elementId="prop">
                      <dsp:option value="<%= prop.getName() %>"/><%= prop.getDisplayName() %>
	       </core:forEach>
            </rpv:propertyList>
        </dsp:select>
      </TD>
    </TR>

    <TR>
      <TD colspan=2><img src="<%= clearImageUrl %>" height=10 width=1 border=0></TD>
    </TR>

    <TR VALIGN="middle" ALIGN="left"> 
      <TD WIDTH="25%" align="right"><font class="small"><%= userConfigShortListSortOrder %>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="75%">
           <dsp:input type="radio"
           bean="RepViewConfigFormHandler.values.shortListReverseSort"
           value="false"/><%= sortAscending %>
           <dsp:input type="radio"
           bean="RepViewConfigFormHandler.values.shortListReverseSort"
           value="true"/><%= sortDecending %>

      </TD>
    </TR>
--%>


    <TR>
      <TD colspan=2 ><img src="<%= clearImageUrl %>" height=4 width=1 border=0></TD>
    </TR>

    <TR>
      <TD colspan=2 ><img src="<%= clearImageUrl %>" height=4 width=1 border=0></TD>
    </TR>
    <TR VALIGN="top" ALIGN="left"> 
      <TD>&nbsp;</TD>
      <TD>
      <dsp:input type="submit" value="<%= updateButton %>" bean="RepViewConfigFormHandler.confirm"/>
      </TD>	
    </TR>
    <TR>
      <TD colspan=2><img src="<%= clearImageUrl %>" height=10 width=1 border=0></TD>
    </TR>
  </TABLE>

</dsp:form>
</rpv:repViewPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/userConfig/UserConfig.jsp#2 $$Change: 651448 $--%>
