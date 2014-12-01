<%-- 
Page:   	configSlot.jsp
Gear:  	 	Slot Gear
gearmode: 	instanceConfig
displayMode: 	full

Author:      	Jeff Banister
Description: 	This page is included by instanceConfig.jsp, and renders and handles a form to 
      		set the name of the Slot component to be used for this gear
--%>

<%@ page import="java.io.*,java.util.*,atg.servlet.*" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>


<dsp:page>

<i18n:bundle baseName="atg.gear.slotgear.SlotgearResources" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="submitButtonLabel" key="submit-button-label"/>
<i18n:message id="cancelButtonLabel" key="cancel-button-label"/>
<i18n:message id="slotTypeLabel" key="slot-type-label"/>
<i18n:message id="slotTypeInternalMedia" key="slot-type-internal-media"/>
<i18n:message id="slotTypeExternalMedia" key="slot-type-external-media"/>
<i18n:message id="slotTypeProduct" key="slot-type-product"/>
<i18n:message id="slotConfigTitle" key="slotConfigTitle"/>
<i18n:message id="slotConfigHelperText" key="slotConfigHelperText"/>

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

   String thisConfigPage="slot";

   // These are used to set the bean values explicitly so radio and select inputs
   // display the current value
   String currentSlotTypeVal=pafEnv.getGearInstanceParameter("slotType");
   String currentSlotNameVal=pafEnv.getGearInstanceParameter("slotComponent");
   boolean selectedVal=false;

   DynamoHttpServletRequest dynRequest=ServletUtil.getDynamoRequest(request);
   java.util.Dictionary slotDict= dynRequest.getNucleus().getConfigurations("/atg/registry/Slots");

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
  <font class="pageheader_edit"><%=slotConfigTitle%></td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
        <font class="smaller"><%=slotConfigHelperText%>
        </td></tr></table>
  <img src="<%=clearGIF%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>
<table cellpadding="1" cellspacing="0" border="0">

    <!-- XXXXXX  slotComponent  XXXXXXX -->
    <tr>
      <td width="50%"><font class="smaller"><i18n:message key="slotname-label"/></font></td>
    </tr>

    <tr>
      <td WIDTH="50%">
        <dsp:select bean="GearConfigFormHandler.values.slotComponent">
         <core:ForEach id="slotForEach"
             values="<%= slotDict.keys() %>"
             castClass="String"
             elementId="slot">
         <core:if value="<%= slot.equals(currentSlotNameVal) %>"><% selectedVal=true;%></core:if>
         <dsp:option value='<%=slot%>'selected="<%=selectedVal%>"/><%=slot%>
	   <% selectedVal=false;%>
         </core:ForEach>
        </dsp:select>
		<br><br>
      </td>
    </tr>

    <!-- XXXXXX  defaultImage  XXXXXXX -->
    <tr>
      <td width="50%"><font class="smaller"><i18n:message key="default-image-label"/></font></td>
    </tr>

    <tr>
      <td WIDTH="50%"><dsp:input type="text" size="50" maxlength="100" 
	bean="GearConfigFormHandler.values.defaultImage" 
	value='<%= pafEnv.getGearInstanceParameter("defaultImage")%>'/><br><br>
      </td>
    </tr>
	    <!-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -->



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
<%-- @version $Id: //app/portal/version/10.0.3/slotgear/slotgear.war/admin/configSlot.jsp#2 $$Change: 651448 $--%>
