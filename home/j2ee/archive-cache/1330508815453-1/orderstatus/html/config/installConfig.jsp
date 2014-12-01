<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%--
   Order Status Gear
   gearmode = installConfig 
  
   This page displays the order status gear in installConfig mode. The page allows 
   Portal Admin to specify the URL for the order details page.    
--%>

<dsp:page>
<paf:PortalAdministratorCheck>
<dsp:importbean bean="/atg/portal/gear/GearConfigFormHandler"/>
<paf:InitializeGearEnvironment id="gearEnv">

<html>
<head>
<title>Install Config</Title>

<%request.setAttribute(atg.portal.framework.RequestAttributes.GEAR_DEFINITION,(String) request.getParameter("paf_gear_def_id") );%>


<i18n:bundle baseName="atg.commerce.gears.orderstatus.OrderStatusResources" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="finishButton" key="finishButton"/>
<i18n:message id="cancelButton" key="cancelButton"/>

<dsp:form method="post" action="<%= request.getRequestURI() %>">
  <dsp:input type="hidden" bean="GearConfigFormHandler.successUrl" value='<%= request.getParameter("paf_success_url") %>'/>
  
  <%-- 2 hidden params indicate we are setting installConfig specific parameter values --%> 
  <dsp:input type="hidden" bean="GearConfigFormHandler.settingDefaultValues" value="true"/>
  <dsp:input type="hidden" bean="GearConfigFormHandler.paramType" value="instance"/>

  <input type="hidden" name="paf_dm" value="<%=gearEnv.getDisplayMode()%>"/>
  <input type="hidden" name="paf_gm" value="<%=gearEnv.getGearMode()%>"/>
  <input type="hidden" name="paf_gear_def_id" value="<%=gearEnv.getGearDefinition().getId() %>"/>
  
  
  <%-- The URL that should be used for the order page. --%>
  <i18n:message key="instanceUrlOrderPage">URL of the order page</i18n:message>
  <dsp:input type="text" size="45" bean="GearConfigFormHandler.values.OrderPage" value='<%= gearEnv.getGearInstanceDefaultValue("OrderPage") %>'/>
  <br><br>


  <%-- Finally show the submit buttons. --%>
  <dsp:input type="submit" value="<%= finishButton %>" bean="GearConfigFormHandler.confirm"/>
  &nbsp;&nbsp;&nbsp;
  <dsp:input type="submit" value="<%= cancelButton %>" bean="GearConfigFormHandler.cancel"/>
  <br>
      
</dsp:form>

</paf:InitializeGearEnvironment>

</body>
</html>

</paf:PortalAdministratorCheck>

</dsp:page>
<%-- @version $Id: //product/DCS/version/10.0.3/gears/OrderStatus/orderstatus.war/html/config/installConfig.jsp#2 $$Change: 651448 $--%>
