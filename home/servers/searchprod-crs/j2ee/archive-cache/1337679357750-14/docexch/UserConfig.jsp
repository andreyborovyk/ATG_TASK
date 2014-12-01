<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/docexch-taglib" prefix="dex" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<dsp:importbean bean="/atg/portal/gear/docexch/DocExchConfigFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">
<dex:DocExchPage id="dexpage" gearEnv="<%= pafEnv %>">

<i18n:bundle baseName="<%= dexpage.getResourceBundle() %>" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="userConfigPageTitle" key="userConfigPageTitle"/>
<i18n:message id="userConfigFullPageDocCountLabel" key="userConfigFullPageDocCountLabel"/>
<i18n:message id="userConfigFullListLabel" key="userConfigFullListLabel"/>
<i18n:message id="userConfigSharedPageDocCountLabel" key="userConfigSharedPageDocCountLabel"/>
<i18n:message id="userConfigShortListSize" key="userConfigShortListSize"/>
<i18n:message id="finishButton" key="finishButton"/>


<dsp:form method="post" action="<%= pafEnv.getOriginalRequestURI() %>">

  <%-- 2 params indicate we are setting user specific parameter values --%> 	
  <dsp:setvalue bean="DocExchConfigFormHandler.paramType" value="user"/>
  <dsp:setvalue bean="DocExchConfigFormHandler.settingDefaultValues" value="false"/>
  <dsp:setvalue bean="DocExchConfigFormHandler.paramNames" value="fullListPageSize shortListSize"/>

  <core:CreateUrl id="successUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
    <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
    <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <dsp:input type="hidden" bean="DocExchConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl() %>"/>
  </core:CreateUrl>

  <dsp:input type="hidden" bean="DocExchConfigFormHandler.successUrl" value="<%= pafEnv.getOriginalRequestURI() %>"/>
  <input type="hidden" name="paf_gear_id" value="<%= pafEnv.getGear().getId()%>"/>

  <TABLE WIDTH="667" BORDER="0" CELLSPACING="0" CELLPADDING="0">


    <TR>
      <TD colspan=2>
      <!-- display errors if any -->
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
         <dsp:param name="value" bean="DocExchConfigFormHandler.formError" />
         <dsp:oparam name="true">
            <dsp:droplet name="ForEach">
               <dsp:param name="array" bean="DocExchConfigFormHandler.formExceptions"/>
               <dsp:oparam name="output">
                  <dsp:getvalueof id="errorMsg" idtype="java.lang.String" param="element">
                  <font class="error"><img src='<%= dexpage.getRelativeUrl("/images/info.gif")%>'
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
                  <font class="info"><img src='<%= dexpage.getRelativeUrl("/images/info.gif")%>'
                  >&nbsp;&nbsp;<dsp:valueof param="element"/></font>
               </dsp:oparam>
            </dsp:droplet>
            <dsp:setvalue bean="DocExchConfigFormHandler.resetFormMessages"/> 
         </dsp:oparam>
      </dsp:droplet>
      </TD>
    </TR>  

    <TR>
      <TD colspan=2>
        <font class="medium_bold"><%= userConfigPageTitle %></font>
      </TD>
    </TR>
    <TR>
      <TD colspan=2 bgcolor="#000033"><img src="<%= dexpage.getRelativeUrl("/images/clear.gif") %>" height=1 width=1 border=0></TD>
    </TR>
    <TR>
      <TD colspan=2><img src="<%= dexpage.getRelativeUrl("/images/clear.gif") %>" height=10 width=1 border=0></TD>
    </TR>


    <TR>
      <TD colspan=2>
        <font class="small"><%= userConfigFullPageDocCountLabel %></font>
      </TD>
    </TR>

    <TR VALIGN="middle" ALIGN="left"> 
      <TD WIDTH="25%" align="right"><font class="small"><%= userConfigFullListLabel %>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="75%"><dsp:select
           bean="DocExchConfigFormHandler.values.fullListPageSize">
             <dsp:option value="10"/>10
             <dsp:option value="12"/>12
             <dsp:option value="15"/>15
             <dsp:option value="20"/>20
             <dsp:option value="30"/>30
             <dsp:option value="50"/>50
           </dsp:select>
	</TD>
    </TR>

    <TR>
      <TD colspan=2><img src="<%= dexpage.getRelativeUrl("/images/clear.gif") %>" height=10 width=1 border=0></TD>
    </TR>

    <TR>
      <TD colspan=2>
        <font class="small"><%= userConfigSharedPageDocCountLabel %></font> 
      </TD>
    </TR>

    <TR VALIGN="middle" ALIGN="left"> 
      <TD WIDTH="25%" align="right"><font class="small"><%= userConfigShortListSize %>&nbsp;&nbsp;</font></TD>
      <TD WIDTH="75%"><dsp:select
           bean="DocExchConfigFormHandler.values.shortListSize">
             <dsp:option value="1"/>1
             <dsp:option value="2"/>2
             <dsp:option value="3"/>3
             <dsp:option value="4"/>4
             <dsp:option value="5"/>5
             <dsp:option value="6"/>6
             <dsp:option value="7"/>7
             <dsp:option value="10"/>10
             <dsp:option value="12"/>12
             <dsp:option value="15"/>15
           </dsp:select>
	</TD>
    </TR>

    <TR>
      <TD colspan=2 ><img src="<%= dexpage.getRelativeUrl("/images/clear.gif") %>" height=4 width=1 border=0></TD>
    </TR>

    <TR>
      <TD colspan=2 ><img src="<%= dexpage.getRelativeUrl("/images/clear.gif") %>" height=4 width=1 border=0></TD>
    </TR>
    <TR VALIGN="top" ALIGN="left"> 
      <TD>&nbsp;</TD>
      <TD>
      <dsp:input type="submit" value="<%= finishButton %>" bean="DocExchConfigFormHandler.confirm"/>
      </TD>	
    </TR>
    <TR>
      <TD colspan=2><img src="<%= dexpage.getRelativeUrl("/images/clear.gif") %>" height=10 width=1 border=0></TD>
    </TR>
  </TABLE>

</dsp:form>
</dex:DocExchPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/UserConfig.jsp#2 $$Change: 651448 $--%>
