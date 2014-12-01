<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<i18n:bundle baseName="atg.gear.user_registration.user_registration" localeAttribute="userLocale" changeResponseLocale="false" />

<paf:InitializeGearEnvironment id="pafEnv">

<% 
   String origURI= pafEnv.getOriginalRequestURI(); 
   String gearID= pafEnv.getGear().getId();
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String communityID = request.getParameter("paf_community_id");
   String pafSuccessURL = request.getParameter("paf_success_url");

   String imageDocRoot = pafEnv.getGear().getServletContext() + "/images/";
   String clearGifURL = "clear.gif";
   String infoGifURL = "info.gif";
   String clearGifFullURL = imageDocRoot + clearGifURL;
   String infoGifFullURL = imageDocRoot + infoGifURL;
%>
<dsp:importbean bean="/atg/portal/gear/user_registration/LoginConfigFormHandler"/>

<i18n:message id="finishButtonLabel" key="finish-button-label"/>
<i18n:message id="cancelButtonLabel" key="cancel-button-label"/>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="LoginConfigFormHandler.formError"/>
  <dsp:oparam name="true">
    <font class="error"><UL>
      <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
        <dsp:param name="exceptions" bean="LoginConfigFormHandler.formExceptions"/>
        <dsp:oparam name="output">
          <LI> <dsp:valueof param="message"></dsp:valueof>
        </dsp:oparam>
      </dsp:droplet>
    </UL></font>
  </dsp:oparam>
  <dsp:oparam name="false">
    <core:ifNotNull value='<%=request.getParameter("formSubmit")%>'>
      &nbsp;<img src="<%=infoGifFullURL%>">&nbsp;&nbsp;<img src="<%=clearGifFullURL%>"><font class="smaller"><i18n:message key="instance-config-feedback-msg"/></font>
    </core:ifNotNull>
  </dsp:oparam>
</dsp:droplet>

<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
<font class="pageheader_edit"><i18n:message key="displayTextTitle"/></font>
</td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
<font class="smaller"><i18n:message key="instance-config-helper-text"/>
</td></tr></table>
<img src="<%=clearGifFullURL%>" height="1" width="1" border="0"><br>
  
  
  
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>
<table cellpadding="1" cellspacing="0" border="0">

<dsp:form method="post" action="<%= origURI %>">
  <input type="hidden" name="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_page_id" value="<%= pageID %>"/>
  <input type="hidden" name="paf_page_url" value="<%= pageURL %>"/>
  <input type="hidden" name="paf_community_id" value="<%= communityID %>"/>
  <input type="hidden" name="paf_success_url" value="<%= pafSuccessURL %>"/>
  <input type="hidden" name="formSubmit" value="true"/>

  <core:createUrl id="successUrl" url="<%= origURI %>">
    <core:urlParam param="paf_community_id" value="<%= communityID %>"/>
    <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
    <core:urlParam param="paf_page_id" value="<%= pageID %>"/>
    <core:urlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:urlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:urlParam param="paf_gear_id" value="<%= gearID %>"/>
    <core:UrlParam param="formSubmit" value="true"/>
    <paf:encodeUrlParam param="paf_success_url" value="<%= pafSuccessURL%>"/>
    <dsp:input type="hidden" bean="LoginConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl()%>"/>
    <dsp:input type="hidden" bean="LoginConfigFormHandler.cancelUrl" value="<%= successUrl.getNewUrl()%>"/>
  </core:createUrl>

    <%-- **************** --%>
    <tr>
      <td width="50%" align="left"><font class="smaller"><i18n:message key="showRegLabel"/></font></td>
          <dsp:setvalue bean="LoginConfigFormHandler.values.showRegistration" value='<%=pafEnv.getGearInstanceParameter("showRegistration")%>' />
      
      <td WIDTH="50%"><dsp:select bean="LoginConfigFormHandler.values.showRegistration" >
   	   <dsp:option value="true"/><i18n:message key="trueLabel"/>
   	   <dsp:option value="false"/><i18n:message key="falseLabel"/>
	</dsp:select>      
      </td>
    </tr>

    <%-- **************** --%>
    <tr>
      <td width="50%" align="left"><font class="smaller"><i18n:message key="showNameLoginLabel"/></font></td>
      
          <dsp:setvalue bean="LoginConfigFormHandler.values.showNameLogin" value='<%=pafEnv.getGearInstanceParameter("showNameLogin")%>' />
      <td WIDTH="50%"><dsp:select bean="LoginConfigFormHandler.values.showNameLogin" >
   	   <dsp:option value="true"/><i18n:message key="trueLabel"/>
   	   <dsp:option value="false"/><i18n:message key="falseLabel"/>
	</dsp:select>      
      </td>
    </tr>

    <%-- **************** --%>
    <tr>
      <td width="50%" align="left"><font class="smaller"><i18n:message key="successUrlLabel"/></font></td>
      
      <td WIDTH="50%"><dsp:input type="text" size="35" maxlength="50" 
	bean="LoginConfigFormHandler.values.successURL" 
	value='<%= pafEnv.getGearInstanceParameter("successURL")%>'/>
      </td>
    </tr>

    <tr>
      <td colspan=2 ><img src="<%=clearGifFullURL%>" height=4 width=1 border=0></td>
    </tr>
    <tr VALIGN="top" ALIGN="left"> 
      <td>&nbsp;</td>
      <td>
        <dsp:input type="submit" value="<%=finishButtonLabel%>" bean="LoginConfigFormHandler.confirm"/>
      </td>
    </tr>
</dsp:form>
  </TABLE>


</td></tr></table><br><br>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/user_registration/user_registration.war/admin/configDisplayText.jsp#2 $$Change: 651448 $--%>
