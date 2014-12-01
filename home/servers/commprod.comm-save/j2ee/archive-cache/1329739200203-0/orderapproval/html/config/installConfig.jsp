<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%--
   Order Approval Gear
   gearmode = installConfig 
  
   This page displays the order approval gear in installConfig mode. The page allows 
   Portal Admin to specify whether the approval process of the gear will be used or
   not, and the URLs for the order details page and the order approval process, if
   the process in the gear is not being used.
--%>

<dsp:page>
<paf:PortalAdministratorCheck>
<dsp:importbean bean="/atg/portal/gear/GearConfigFormHandler"/>
<paf:InitializeGearEnvironment id="gearEnv">

<html>
<head>
<title>Install Config</Title>

<%request.setAttribute(atg.portal.framework.RequestAttributes.GEAR_DEFINITION,(String) request.getParameter("paf_gear_def_id") );%>


<i18n:bundle baseName="atg.commerce.gears.orderapproval.OrderApprovalResources" localeAttribute="userLocale" changeResponseLocale="false" />
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
  
  
  <%-- Checkbox determining whether the approval process of the gear will be used. --%>
  <dsp:input type="checkbox" bean="GearConfigFormHandler.values.UseOrderApprovalOfGear" checked='<%= "true".equals(gearEnv.getGearInstanceDefaultValue("UseOrderApprovalOfGear"))%>'/>
  <i18n:message key="installUseOrderApprovalOfGear">Use the Order Approval Process of this Gear</i18n:message>
  <br><br>

  <%-- The URL that should be used for the order page. --%>
  <i18n:message key="installApprovedOrderPageUrl">URL of the order page</i18n:message>
  <dsp:input type="text" size="45" bean="GearConfigFormHandler.values.ApprovedOrderPageURL" value='<%= gearEnv.getGearInstanceDefaultValue("ApprovedOrderPageURL") %>'/>
  <br><br>

  <%-- The URL that should be used for the approval process, if the gear's is not being used. --%>
  <i18n:message key="installPendingApprovalOrderPageUrl">URL of the order approval process (only relevant if not using gear's approval process)</i18n:message>
  <dsp:input type="text" size="45" bean="GearConfigFormHandler.values.PendingApprovalOrderPageURL" value='<%= gearEnv.getGearInstanceDefaultValue("PendingApprovalOrderPageURL") %>'/>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/gears/OrderApproval/orderapproval.war/html/config/installConfig.jsp#2 $$Change: 651448 $--%>
