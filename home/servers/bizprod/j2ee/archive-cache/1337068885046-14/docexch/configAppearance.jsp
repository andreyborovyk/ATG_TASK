<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/docexch-taglib" prefix="dex" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<dsp:page>
<paf:InitializeGearEnvironment id="pafEnv">
<dex:DocExchPage id="dexpage" gearEnv="<%= pafEnv %>">
<i18n:bundle baseName="<%= dexpage.getResourceBundle() %>" localeAttribute="userLocale" changeResponseLocale="false" />

<i18n:message id="formTitle" key="configAppearanceFormTitle"/>
<i18n:message id="formInstructions" key="configAppearanceFormInstructions"/>
<i18n:message id="shouldDisplayColumnHeadersLabel" key="shouldDisplayColumnHeadersLabel"/>
<i18n:message id="shouldDisplayCreateDateLabel" key="shouldDisplayCreateDateLabel"/>
<i18n:message id="trueText" key="true"/>
<i18n:message id="falseText" key="false"/>
<i18n:message id="dateLabel" key="dateLabel"/>
<i18n:message id="timeLabel" key="timeLabel"/>
<i18n:message id="shouldDisplayAuthorLabel" key="shouldDisplayAuthorLabel"/>
<i18n:message id="shouldDisplayTitleLabel" key="shouldDisplayTitleLabel"/>
<i18n:message id="shouldDisplayDescriptionLabel" key="shouldDisplayDescriptionLabel"/>
<i18n:message id="authorDisplayPropertiesLabel" key="authorDisplayPropertiesLabel"/>
<i18n:message id="dateTimeDisplayLabel" key="dateTimeDisplayLabel"/>
<i18n:message id="shortDateLabel" key="shortDateLabel"/>
<i18n:message id="mediumDateLabel" key="mediumDateLabel"/>
<i18n:message id="longDateLabel" key="longDateLabel"/>
<i18n:message id="fullDateLabel" key="fullDateLabel"/>
<i18n:message id="notShownDateLabel" key="notShownDateLabel"/>
<i18n:message id="shortTimeLabel" key="shortTimeLabel"/>
<i18n:message id="mediumTimeLabel" key="mediumTimeLabel"/>
<i18n:message id="longTimeLabel" key="longTimeLabel"/>
<i18n:message id="fullTimeLabel" key="fullTimeLabel"/>
<i18n:message id="notShownTimeLabel" key="notShownTimeLabel"/>
<i18n:message id="shouldDisplayStatusLabel" key="shouldDisplayStatusLabel"/>
<i18n:message id="finishButton" key="finishButton"/>
<i18n:message id="cancelButton" key="cancelButton"/>
<i18n:message id="separator" key="labelFieldSeparator"/>
<i18n:message id="resourceBundleLabel" key="resourceBundleLabel"/>

<dsp:importbean bean="/atg/portal/gear/docexch/DocExchConfigFormHandler"/>

<dsp:form method="post" action="<%= pafEnv.getOriginalRequestURI() %>">

  <core:CreateUrl id="successUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
    <core:UrlParam param="config_page" value="appearance"/>
    <core:UrlParam param="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
    <paf:encodeUrlParam param="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
    <core:UrlParam param="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
    <dsp:input type="hidden" bean="DocExchConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl() %>"/>
  </core:CreateUrl>


  <%-- this is a workaround until default attribute works in dsp:select 
  <dsp:setvalue bean="DocExchConfigFormHandler.values.authorDisplayProp1"
              value='<%= pafEnv.getGearInstanceParameter("authorDisplayProp1") %>' />	
  <dsp:setvalue bean="DocExchConfigFormHandler.values.authorDisplayProp2"
              value='<%= pafEnv.getGearInstanceParameter("authorDisplayProp2") %>' />	
  end workaround --%>

  <%-- required parameters to get back to community settings --%>
  <input type="hidden" name="config_page" value="appearance"/>
  <input type="hidden" name="paf_community_id" value='<%= request.getParameter("paf_community_id") %>'/>
  <input type="hidden" name="paf_page_url" value='<%= request.getParameter("paf_page_url") %>'/>
  <input type="hidden" name="paf_page_id" value='<%= request.getParameter("paf_page_id") %>'/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>

  <dsp:input type="hidden" bean="DocExchConfigFormHandler.paramType" value="instance"/>
  <dsp:input type="hidden" bean="DocExchConfigFormHandler.settingDefaultValues" value="false"/>
  <dsp:setvalue bean="DocExchConfigFormHandler.paramNames" 
	        value="displayColumnHeaders displayTitle displayDescription displayAuthor authorPropertyName authorDisplayProp1 authorDisplayProp2 displayCreateDate dateStyle timeStyle displayStatus resourceBundle"/>
  
  
  <%@ include file="configPageTitleBar.jspf" %>
  
  
  <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">


    <TR>
      <TD width="40%" align="left" valign="top"><font class="smaller"><%= 
          resourceBundleLabel %><%= separator %> &nbsp;&nbsp;</font></TD>
      <TD width="60%" valign="top"><font class="smaller">
         <dsp:input type="text" size="40"
               bean="DocExchConfigFormHandler.values.resourceBundle"/>
       </TD>
    </TR>

    <TR>
      <TD width="40%" align="left" valign="top"><font class="smaller"><%= 
          shouldDisplayColumnHeadersLabel %><%= separator %> &nbsp;&nbsp;</font></TD>
      <TD width="60%" valign="top"><font class="smaller">
         <dsp:input type="radio"
               bean="DocExchConfigFormHandler.values.displayColumnHeaders" 
               value="true"/><%= trueText %>  &nbsp;&nbsp&nbsp;
         <dsp:input type="radio"
               bean="DocExchConfigFormHandler.values.displayColumnHeaders" 
               value="false"/><%= falseText %>&nbsp;&nbsp&nbsp;
       </TD>
    </TR>


    <TR>
      <TD align="left" valign="top"><font class="smaller"><%= shouldDisplayTitleLabel %><%= 
          separator %> &nbsp;&nbsp;</font></TD>
      <TD valign="top"><font class="smaller">
         <dsp:input type="radio"
               bean="DocExchConfigFormHandler.values.displayTitle" 
               value="true"/><%= trueText %>  &nbsp;&nbsp&nbsp;
         <dsp:input type="radio"
               bean="DocExchConfigFormHandler.values.displayTitle" 
               value="false"/><%= falseText %> &nbsp;&nbsp&nbsp;
      </TD>
    </TR>


    <TR>
      <TD align="left" valign="top"><font class="smaller"><%= 
          shouldDisplayDescriptionLabel %><%= separator %> &nbsp;&nbsp;</font></TD>
      <TD valign="top"><font class="smaller">
         <dsp:input type="radio"
               bean="DocExchConfigFormHandler.values.displayDescription" 
               value="true"/><%= trueText %>  &nbsp;&nbsp&nbsp;
         <dsp:input type="radio"
               bean="DocExchConfigFormHandler.values.displayDescription" 
               value="false"/><%= falseText %> &nbsp;&nbsp&nbsp;
      </TD>
    </TR>


    <TR>
      <TD align="left" valign="top"><font class="smaller"><%= 
          shouldDisplayAuthorLabel %><%= separator %> &nbsp;&nbsp;</font></TD>
      <TD valign="top"><font class="smaller">
            <dsp:input type="radio"
               bean="DocExchConfigFormHandler.values.displayAuthor"
               value="true"/><%= trueText %>  &nbsp;&nbsp&nbsp;
            <dsp:input type="radio"
               bean="DocExchConfigFormHandler.values.displayAuthor" 
               value="false"/><%= falseText %> &nbsp;&nbsp&nbsp;
      </TD>
    </TR>


    <TR>
      <TD align="left" valign="top" nowrap><font class="smaller"><%= authorDisplayPropertiesLabel %><%= separator %><nobr>&nbsp;&nbsp;&nbsp;</font></TD>
      <TD valign="top"><font class="smaller">
        <dsp:droplet name="/atg/portal/gear/docexch/PropertyNameLister">
          <dsp:param name="pafEnv" value="<%= pafEnv %>"/>
          <dsp:param name="property" value='<%= pafEnv.getGearInstanceParameter("authorPropertyName") %>'/>
          <dsp:param name="type" value="java.lang.String"/>
          <dsp:oparam name="output">

            <dsp:select bean="DocExchConfigFormHandler.values.authorDisplayProp1">
              <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                <dsp:param name="array" param="results"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof id="propname" param="key">
                  <dsp:getvalueof id="propdisplayname" param="element" idtype="java.lang.String">
                    <dsp:option value="<%= propname %>"/><%= propdisplayname %>
                  </dsp:getvalueof>
                  </dsp:getvalueof>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:select>

            <dsp:select bean="DocExchConfigFormHandler.values.authorDisplayProp2">
              <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                <dsp:param name="array" param="results"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof id="propname" param="key" idtype="java.lang.String">
                  <dsp:getvalueof id="propdisplayname" param="element" idtype="java.lang.String">
                    <dsp:option value="<%= propname %>"/><%= propdisplayname %>
                  </dsp:getvalueof>
                  </dsp:getvalueof>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:select>
          </dsp:oparam>
        </dsp:droplet>
      </TD>
    </TR>


    <TR>
      <TD align="left" valign="top"><font class="smaller"><%= 
          shouldDisplayCreateDateLabel %><%= separator %> &nbsp;&nbsp;</font></TD>
      <TD valign="top"><font class="smaller">
          <dsp:input type="radio"
               bean="DocExchConfigFormHandler.values.displayCreateDate" 
               value="true"/><%= trueText %>  &nbsp;&nbsp&nbsp;
          <dsp:input type="radio"
               bean="DocExchConfigFormHandler.values.displayCreateDate" 
               value="false"/><%= falseText %> &nbsp;&nbsp&nbsp;
      </TD>
    </TR>

    <TR>
      <TD align="left" valign="top"><font class="smaller"><%= 
          dateTimeDisplayLabel %><%= separator %> &nbsp;&nbsp;<br></font></TD>
      <TD align="left" valign="top"><font class="smaller">

        <%= dateLabel %><%= separator %>&nbsp;&nbsp;
        <dsp:select bean="DocExchConfigFormHandler.values.dateStyle">
          <dsp:option value="short"/><%= shortDateLabel %>
          <dsp:option value="medium"/><%= mediumDateLabel %>
          <dsp:option value="long"/><%= longDateLabel %>
          <dsp:option value="full"/><%= fullDateLabel %>
          <dsp:option value="notShown"/><%= notShownDateLabel %>
        </dsp:select>

        <BR>
        <%= timeLabel %><%= separator %>&nbsp;&nbsp;
        <dsp:select bean="DocExchConfigFormHandler.values.timeStyle">
          <dsp:option value="short"/><%= shortTimeLabel %>
          <dsp:option value="medium"/><%= mediumTimeLabel %>
          <dsp:option value="long"/><%= longTimeLabel %>
          <dsp:option value="full"/><%= fullTimeLabel %>
          <dsp:option value="notShown"/><%= notShownTimeLabel %>
        </dsp:select>
       </TD>
    </TR>




    <TR>
      <TD align="left"><font class="smaller"><%= 
          shouldDisplayStatusLabel %><%= separator %> &nbsp;&nbsp;</font></TD>
      <TD valign="top"><font class="smaller">
          <dsp:input type="radio"
               bean="DocExchConfigFormHandler.values.displayStatus" 
               value="true"/><%= trueText %>  &nbsp;&nbsp&nbsp;
          <dsp:input type="radio"
               bean="DocExchConfigFormHandler.values.displayStatus" 
               value="false"/><%= falseText %> &nbsp;&nbsp&nbsp;
      </TD>       
    </TR>


    <TR>
      <TD colspan=2 ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=4 width=1 border=0></TD>
    </TR>

    <TR>
      <TD colspan=2 ><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=4 width=1 border=0></TD>
    </TR>
    <TR>
      <TD valign="bottom" colspan=2>
        <dsp:input type="submit" value="<%= finishButton %>" bean="DocExchConfigFormHandler.confirm"/>
      </TD>
    </TR>
    <TR>
      <TD colspan=2><img src='<%= dexpage.getRelativeUrl("/images/clear.gif") %>' height=10 width=1 border=0></TD>
    </TR>
  </TABLE>

</dsp:form>
</dex:DocExchPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/configAppearance.jsp#2 $$Change: 651448 $--%>
