<%-- 
Page:   	configDisplayParams.jsp
Gear:  	 	Slot Gear
gearmode: 	instanceConfig
displayMode: 	full

Author:      	Jeff Banister
Description: 	This page is included by instanceConfig.jsp, and displays a form to set the
		property names for  URL and text field.  This applies only to internal 
		media (text or binary), and external media, and will allow other itemDescriptors
		to be used which potentially have different property names. The alternative to
		this configuration would be to hard-code the names "url" and "data" for
		these properties.

		As currently implemented, if the configured slot is a "product" slot, then
		a different form will render which will set the catalogURL parameter.  
	
--%>

<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.scenario.targeting.RepositoryItemSlot" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<i18n:bundle baseName="atg.gear.slotgear.SlotgearResources" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="submitButtonLabel" key="submit-button-label"/>
<i18n:message id="cancelButtonLabel" key="cancel-button-label"/>
<i18n:message id="urlPropertyLabel" key="url-property-label"/>
<i18n:message id="textPropertyLabel" key="text-property-label"/>
<i18n:message id="urlPropertyNotes" key="url-property-notes"/>
<i18n:message id="textPropertyNotes" key="text-property-notes"/>
<i18n:message id="contextPropertyLabel" key="context-property-label"/>
<i18n:message id="contextPropertyNotes" key="context-property-notes"/>
<i18n:message id="noPropertiesMsg" key="no-properties-msg"/>
<i18n:message id="yesLabel" key="yes-label"/>
<i18n:message id="noLabel" key="no-label"/>
<i18n:message id="paramConfigTitle" key="paramConfigTitle"/>
<i18n:message id="paramConfigHelperText" key="paramConfigHelperText"/>
<i18n:message id="catalogUrlLabel" key="catalog-url-label"/>
<i18n:message id="catalogUrlNotes" key="catalog-url-notes"/>

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

   String thisConfigPage="displayParams";

   
   // Check itemDescriptor for the currently configured slot - if this is a "product" slot,
   // display different set of params
   String currentSlotVal=pafEnv.getGearInstanceParameter("slotComponent");

   String fullyQualifiedSlotName="dynamo:/atg/registry/Slots/" + currentSlotVal;
   PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);

   atg.scenario.targeting.RepositoryItemSlot gearSlot = (atg.scenario.targeting.RepositoryItemSlot) portalServletRequest.lookup(fullyQualifiedSlotName);
   String slotItemDescriptor=gearSlot.getItemDescriptorName();
   String productItem="product";
   String externalMediaItem="media-external";
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
  <font class="pageheader_edit"><%=paramConfigTitle%> </td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
        <font class="smaller"><%=paramConfigHelperText%>
        </td></tr></table>
  <img src="<%=clearGIF%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>
<table cellpadding="1" cellspacing="0" border="0">
  
  

  <core:exclusiveIf>
   <core:ifNot value="<%= slotItemDescriptor.equals(productItem) %>">
    <tr>
      <td colspan=2><img src="<%=clearGIF%>" height=10 width=1 border=0></td>
    </tr>

    <!-- XXXXXX  URL display  XXXXXXX -->
    <tr>
      <td colspan=2 nowrap valign="top"><font class="smaller"><%=urlPropertyLabel%></font>&nbsp;<dsp:input type="text" size="30" maxlength="100" 
	bean="GearConfigFormHandler.values.urlPropertyName" 
	value='<%= pafEnv.getGearInstanceParameter("urlPropertyName")%>'/>
      </td>
    </tr>
 
    <tr>
      <td colspan=2 nowrap valign="top"><font class="smaller"><%=urlPropertyNotes%></font></td>
    </tr>
    <tr>
      <td colspan=2 ><img src="<%=clearGIF%>" height=20 width=1 border=0></td>
    </tr>

    <!-- XXXXXX  text display  XXXXXXX -->
    <tr>
      <td colspan=2 nowrap valign="top"><font class="smaller"><%=textPropertyLabel%></font>&nbsp;<dsp:input type="text" size="30" maxlength="100" 
	bean="GearConfigFormHandler.values.textPropertyName" 
	value='<%= pafEnv.getGearInstanceParameter("textPropertyName")%>'/>
      </td>
    </tr>

    <tr>
      <td colspan=2 nowrap valign="top"><font class="smaller"><%=textPropertyNotes%></font></td>
    </tr>
    <tr>
      <td colspan=2 ><img src="<%=clearGIF%>" height=20 width=1 border=0></td>
    </tr>

    <core:if value="<%= slotItemDescriptor.equals(externalMediaItem) %>">
      <!-- XXXXXX  Media Context Root XXXXXXX -->
      <tr>
        <td colspan=2 nowrap valign="top"><font class="smaller"><%=contextPropertyLabel%></font>&nbsp;<dsp:input type="text" size="30" maxlength="100" 
	bean="GearConfigFormHandler.values.mediaContextRoot" 
	value='<%= pafEnv.getGearInstanceParameter("mediaContextRoot")%>'/>
        </td>
      </tr>

      <tr>
        <td colspan=2 nowrap valign="top"><font class="smaller"><%=contextPropertyNotes%></font></td>
      </tr>
      <tr>
        <td colspan=2 ><img src="<%=clearGIF%>" height=20 width=1 border=0></td>
      </tr>
    </core:if>
   </core:ifNot>
    <!-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -->
   <core:defaultCase>
    <tr>
      <td colspan=2><img src="<%=clearGIF%>" height=10 width=1 border=0></td>
    </tr>
    <!-- XXXXXX  Product Slot catalogUrl XXXXXXX -->
    <tr>
      <td colspan=2 nowrap valign="top"><font class="smaller"><%=catalogUrlLabel%></font>&nbsp;<dsp:input type="text" size="30" maxlength="100" 
	bean="GearConfigFormHandler.values.catalogUrl" 
	value='<%= pafEnv.getGearInstanceParameter("catalogUrl")%>'/>
      </td>
    </tr>
 
    <tr>
      <td colspan=2 nowrap valign="top"><font class="smaller"><%=catalogUrlNotes%></font></td>
    </tr>
    <tr>
      <td colspan=2 ><img src="<%=clearGIF%>" height=20 width=1 border=0></td>
    </tr>
   </core:defaultCase>
  </core:exclusiveIf>
    <!-- XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX -->

    <tr VALIGN="top" ALIGN="left"> 
      <td><dsp:input type="submit" value="<%=submitButtonLabel%>" bean="GearConfigFormHandler.confirm"/></td>
      <td align="left">&nbsp;&nbsp;&nbsp;
      </td>
    </tr>
    <tr>
      <td colspan=2><img src="<%=clearGIF%>" height=10 width=1 border=0></td>
    </tr>
  </TABLE>

</dsp:form>
</td></tr></table><Br><br>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/slotgear/slotgear.war/admin/configDisplayParams.jsp#2 $$Change: 651448 $--%>
