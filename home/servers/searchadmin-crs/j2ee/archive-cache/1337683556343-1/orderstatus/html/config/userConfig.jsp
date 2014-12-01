<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:importbean bean="/atg/portal/gear/GearConfigFormHandler"/>

<%--
   Order Status Gear
   gearmode = userConfig 
  
   This page displays userConfig mode of order status gear. It allows the 
   end-user to configure the display of the gear.    
--%>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">


<%
String origURI= request.getRequestURI(); 
String successUrl = request.getParameter("paf_success_url");
%>


<i18n:bundle baseName="atg.commerce.gears.orderstatus.OrderStatusResources" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="finishButton" key="finishButton"/>
<i18n:message id="cancelButton" key="cancelButton"/>

<%-- Do not reinitialize the userConfig parameters each time this page is shown. --%>
<dsp:setvalue bean="GearConfigFormHandler.initializeDefaultValues" value="foo"/>

<dsp:form method="post" action="<%= gearEnv.getOriginalRequestURI() %>">

  <%-- 2 hidden params indicate we are setting user specific parameter values --%> 	
  <dsp:input type="hidden" bean="GearConfigFormHandler.successUrl" value="<%= gearEnv.getOriginalRequestURI() %>"/>
  <dsp:input type="hidden" bean="GearConfigFormHandler.paramType" value="user"/>
  <dsp:input type="hidden" bean="GearConfigFormHandler.settingDefaultValues" value="false"/>

  <input type="hidden" name="paf_gear_id" value="<%= gearEnv.getGear().getId()%>"/>

  <span class=medium_bold>
  <i18n:message key="userConfigPageTitle">User configuration - Order Status gear</i18n:message></span><p>

  <%-- Display a list of options for the end-user to edit --%>
  <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowOpenOrdersShared" checked='<%= "true".equals(gearEnv.getGearUserParameter("ShowOpenOrdersShared"))%>'/>
  <i18n:message key="userShowTotalOrders">Show number of open orders on shared page</i18n:message><br><br>

  <i18n:message key="userShowNumOrdersShared">Number of orders to show on the shared page</i18n:message>
  <dsp:input type="text" size="5" bean="GearConfigFormHandler.values.NumberOfOrdersShared" value='<%= gearEnv.getGearUserParameter("NumberOfOrdersShared")%>'/><br><br>

  <i18n:message key="userShowNumOrdersFull">Number of orders to show in the full view page</i18n:message>
  <dsp:input type="text" size="5" bean="GearConfigFormHandler.values.NumberOfOrdersFull" value='<%= gearEnv.getGearUserParameter("NumberOfOrdersFull")%>'/><br>


  <dsp:input type="submit" value="<%= finishButton %>" bean="GearConfigFormHandler.confirm"/>
  &nbsp;&nbsp;&nbsp;
  <dsp:input type="submit" value="<%= cancelButton %>" bean="GearConfigFormHandler.cancel"/>

</dsp:form>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //product/DCS/version/10.0.3/gears/OrderStatus/orderstatus.war/html/config/userConfig.jsp#2 $$Change: 651448 $--%>
