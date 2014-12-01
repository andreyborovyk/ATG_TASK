<%-- 
Page:   	configTargetingParams.jsp
Gear:  	 	Slot Gear
gearmode: 	instanceConfig
displayMode: 	full

Author:      	Jeff Banister
Description: 	This page is included by instanceConfig.jsp, and renders and handles a form to 
		set targeting parameters.
--%>

<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<i18n:bundle baseName="atg.gear.slotgear.SlotgearResources" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="submitButtonLabel" key="submit-button-label"/>
<i18n:message id="cancelButtonLabel" key="cancel-button-label"/>
<i18n:message id="targetingLabel" key="targeting-bean-label"/>
<i18n:message id="targetingFirstLabel" key="targeting-first-label"/>
<i18n:message id="targetingForEachLabel" key="targeting-foreach-label"/>
<i18n:message id="targetingRangeLabel" key="targeting-range-label"/>
<i18n:message id="targetingRandomLabel" key="targeting-random-label"/>
<i18n:message id="trueLabel" key="true-label"/>
<i18n:message id="falseLabel" key="false-label"/>
<i18n:message id="targetingConfigTitle" key="targetingConfigTitle"/>
<i18n:message id="targetingConfigHelperText" key="targetingConfigHelperText"/>

<paf:InitializeGearEnvironment id="pafEnv">

<% String origURI= pafEnv.getOriginalRequestURI();
   String gearID=pafEnv.getGear().getId();
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String communityID = request.getParameter("paf_community_id");
   String pafSuccessURL = request.getParameter("paf_success_url");
   String pafSuccessURLEncoded = atg.servlet.ServletUtil.escapeURLString(pafSuccessURL);

   String clearGIF = "/gear/slotgear/images/clear.gif";
   String infoGIF = "/gear/slotgear/images/info.gif";

   String thisConfigPage="targeting";
%>

<dsp:importbean bean="/atg/portal/gear/GearConfigFormHandler"/>



<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="GearConfigFormHandler.formError"/>
  <dsp:oparam name="true">
    <font class="error"><UL>
      <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
        <dsp:param name="exceptions" bean="GearConfigFormHandler.formExceptions"/>
        <dsp:oparam name="output">

          <LI> <dsp:valueof param="message"></dsp:valueof>

        </dsp:oparam>
      </dsp:droplet>
    </UL></font>
  </dsp:oparam>
  <dsp:oparam name="false">
    <core:ifNotNull value='<%=request.getParameter("formSubmit")%>'>

      &nbsp;<img src="<%=infoGIF%>">&nbsp;&nbsp;<font class="smaller"><i18n:message key="update-feedback-msg"/></font>
    </core:ifNotNull>
  </dsp:oparam>
</dsp:droplet>

<dsp:setvalue bean="GearConfigFormHandler.initializeDefaultValues" value="foo"/>

<dsp:form enctype="multipart/form-data" method="post" action="<%= origURI %>">
	

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
    <core:UrlParam param="config_page" value="<%=thisConfigPage%>"/>
    <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
    <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
    <core:UrlParam param="formSubmit" value="true"/>
    <paf:encodeUrlParam param="paf_success_url" value="<%= pafSuccessURL%>"/>
    <dsp:input type="hidden" bean="GearConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl()%>"/>
    <dsp:input type="hidden" bean="GearConfigFormHandler.cancelUrl" value="<%= successUrl.getNewUrl()%>"/>
  </core:CreateUrl>


  <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit"><%=targetingConfigTitle%> </td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
        <font class="smaller"><%=targetingConfigHelperText%></td></tr></table>
  <img src="<%=clearGIF%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>
<table cellpadding="1" cellspacing="0" border="0">

    <!-- XXXXXXXX Targeter XXXXXXXXX -->
    <tr>
      <td width="50%"><font class="smaller"><i18n:message key="targeting-bean-label"/></font></td>
    </tr>

    <tr><td width="50%"><font class="smaller">
     <dsp:setvalue bean="GearConfigFormHandler.values.targetingBean"
               value='<%= pafEnv.getGearInstanceParameter("targetingBean") %>' />
     <dsp:select bean="GearConfigFormHandler.values.targetingBean">
       <dsp:option value="/atg/targeting/TargetingFirst"/><%= targetingFirstLabel %>
       <dsp:option value="/atg/targeting/TargetingForEach"/><%= targetingForEachLabel %>
       <dsp:option value="/atg/targeting/TargetingRange"/><%= targetingRangeLabel %>
       <dsp:option value="/atg/targeting/TargetingRandom"/><%= targetingRandomLabel %>
     </dsp:select></font></td>
    </tr>
    <tr>
      <td colspan=2><img src="<%=clearGIF%>" height=10 width=1 border=0></td>
    </tr>

    <!-- XXXXXXXX filter XXXXXXXXX -->
    <tr>
      <td width="50%"><font class="smaller"><i18n:message key="filter-label"/></font></td>
    </tr>
    <tr>
      <td WIDTH="50%"><dsp:input type="text" size="50" maxlength="100" 
	bean="GearConfigFormHandler.values.filter" 
	value='<%= pafEnv.getGearInstanceParameter("filter")%>'/>
      </td>
    </tr>
    <tr>
      <td colspan=2 ><img src="<%=clearGIF%>" height=10 width=1 border=0></td>
    </tr>
    <!-- XXXXXXXX Source Map XXXXXXXXX -->
    <tr>
      <td width="50%"><font class="smaller"><i18n:message key="sourcemap-label"/></font></td>
    </tr>
    <tr>
      <td WIDTH="50%"><dsp:input type="text" size="50" maxlength="100" 
	bean="GearConfigFormHandler.values.sourceMap" 
	value='<%= pafEnv.getGearInstanceParameter("sourceMap")%>'/>
      </td>
    </tr>
    <tr>
      <td colspan=2 ><img src="<%=clearGIF%>" height=10 width=1 border=0></td>
    </tr>
    <!-- XXXXXXXX How Many XXXXXXXXX -->
    <tr>
      <td width="50%"><font class="smaller"><i18n:message key="howmany-label"/></font></td>
    </tr>
    <tr>
      <td WIDTH="50%"><dsp:input type="text" size="50" maxlength="100" 
	bean="GearConfigFormHandler.values.howMany" 
	value='<%= pafEnv.getGearInstanceParameter("howMany")%>'/>
      </td>
    </tr>
    <tr>
      <td colspan=2 ><img src="<%=clearGIF%>" height=10 width=1 border=0></td>
    </tr>
    <!-- XXXXXXXX Sort Properties XXXXXXXXX -->

    <tr>
      <td width="50%"><font class="smaller"><i18n:message key="sort-properties-label"/></font></td>
    </tr>
    <tr>
      <td WIDTH="50%"><dsp:input type="text" size="50" maxlength="100" 
	bean="GearConfigFormHandler.values.sortProperties" 
	value='<%= pafEnv.getGearInstanceParameter("sortProperties")%>'/>
      </td>
    </tr>
    <tr>
      <td colspan=2 ><img src="<%=clearGIF%>" height=10 width=1 border=0></td>
    </tr>
    <!-- XXXXXXXX fire content event XXXXXXXXX -->

    <tr>
      <td width="50%"><font class="smaller"><i18n:message key="content-event-label"/></font></td>
    </tr>
    <dsp:setvalue bean="GearConfigFormHandler.values.fireContentEvent" value='<%=pafEnv.getGearInstanceParameter("fireContentEvent")%>' />
    <tr>
      <td WIDTH="50%">
        <dsp:select bean="GearConfigFormHandler.values.fireContentEvent">
         <dsp:option value="true" /><%=trueLabel%>
         <dsp:option value="false" /><%=falseLabel%>
        </dsp:select>
      </td>
    </tr>
    <tr>
      <td colspan=2 ><img src="<%=clearGIF%>" height=10 width=1 border=0></td>
    </tr>
    <!-- XXXXXXXX fire content type event XXXXXXXXX -->

    <tr>
      <td width="50%"><font class="smaller"><i18n:message key="content-type-event-label"/></font></td>
    </tr>
    <dsp:setvalue bean="GearConfigFormHandler.values.fireContentTypeEvent" value='<%=pafEnv.getGearInstanceParameter("fireContentTypeEvent")%>' />
    <tr>
      <td WIDTH="50%">
        <dsp:select bean="GearConfigFormHandler.values.fireContentTypeEvent">
         <dsp:option value="true" /><%=trueLabel%>
         <dsp:option value="false" /><%=falseLabel%>
        </dsp:select>
      </td>
    </tr>
    <tr>
      <td colspan=2 ><img src="<%=clearGIF%>" height=10 width=1 border=0></td>
    </tr>


    <!-- XXXXXXXX submit XXXXXXXXX -->

    <tr VALIGN="top" ALIGN="left"> 
      <td><dsp:input type="submit" value="<%=submitButtonLabel%>" bean="GearConfigFormHandler.confirm"/></td>
      <td align="left">&nbsp;&nbsp;&nbsp;
      </td>
    </tr>
  </TABLE>

</dsp:form>

</td></tr></table><br><br>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/slotgear/slotgear.war/admin/configTargetingParams.jsp#2 $$Change: 651448 $--%>
