<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--
   Order Approval Gear
   gearmode = instanceConfig 
  
   This page displays the order approval gear in instanceConfig mode. The page allows 
   community leader to specify the look-and-feel for the gear content pages.    
--%>

<dsp:importbean bean="/atg/portal/gear/GearConfigFormHandler"/>

<paf:CommunityLeaderCheck>
<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">

<%
String origURI= request.getRequestURI(); 
String successUrl = request.getParameter("paf_success_url");
%>


<i18n:bundle baseName="atg.commerce.gears.orderapproval.OrderApprovalResources" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="finishButton" key="finishButton"/>
<i18n:message id="cancelButton" key="cancelButton"/>

<%-- Do not reinitialize the instance parameters each time this page is shown. --%>
<dsp:setvalue bean="GearConfigFormHandler.initializeDefaultValues" value="foo"/>

<dsp:form method="post" action="<%= origURI %>">

  <dsp:input type="hidden" bean="GearConfigFormHandler.successUrl" value="<%= successUrl %>"/>
  
  <%-- 2 hidden params indicate we are setting instanceConfig specific parameter values --%> 
  <dsp:input type="hidden" bean="GearConfigFormHandler.paramType" value="instance"/>
  <dsp:input type="hidden" bean="GearConfigFormHandler.settingDefaultValues" value="false"/>

  <input type="hidden" name="paf_gear_id" value="<%= gearEnv.getGear().getId()%>"/>

  <b>
    <i18n:message key="instanceConfigPageTitle">Configure the Order Approval gear</i18n:message>
  </b>
  <p>
      
  <%-- Display configuration parameters to community leader --%>
  <br><br>
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowOrderInfoInDetails" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowOrderInfoInDetails"))%>'/>
        <i18n:message key="instanceShowOrderInfoInDetails">Show the basic order information in the Order Details page</i18n:message>
      
  <br><br>
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowBillingInfoInDetails" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowBillingInfoInDetails"))%>'/>
        <i18n:message key="instanceShowBillingInfoInDetails">Show the billing information of order in the Order Details page</i18n:message>
      
  <br><br>
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowShippingInfoInDetails" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowShippingInfoInDetails"))%>'/>
        <i18n:message key="instanceShowShippingInfoInDetails">Show the shipping information of order in the Order Details page</i18n:message>
      
  <br><br>
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowOrderInfoInApprove" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowOrderInfoInApprove"))%>'/>
        <i18n:message key="instanceShowOrderInfoInApprove">Show the basic order information in the Order Approval page</i18n:message>
      
  <br><br>
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowMessageInApprove" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowMessageInApprove"))%>'/>
        <i18n:message key="instanceShowMessageInApprove">Show the message box in the Order Approval page </i18n:message>
      
  <br><br>
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowOrderInfoInReject" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowOrderInfoInReject"))%>'/>
        <i18n:message key="instanceShowOrderInfoInReject">Show the basic order information in the Order Rejection page</i18n:message>
      
  <br><br>
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowMessageInReject" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowMessageInReject"))%>'/>
        <i18n:message key="instanceShowMessageInfoInReject">Show the message box in the Order Rejection page</i18n:message>
      
  <br><br>


  <%-- Finally show the submit buttons. --%>
  <dsp:input type="submit" value="<%= finishButton %>" bean="GearConfigFormHandler.confirm"/>
  &nbsp;&nbsp;&nbsp;
  <dsp:input type="submit" value="<%= cancelButton %>" bean="GearConfigFormHandler.cancel"/>
  </blockquote>
  <br>
      
</dsp:form>
</paf:InitializeGearEnvironment>
</dsp:page>
</paf:CommunityLeaderCheck>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/gears/OrderApproval/orderapproval.war/html/config/instanceConfig.jsp#2 $$Change: 651448 $--%>
