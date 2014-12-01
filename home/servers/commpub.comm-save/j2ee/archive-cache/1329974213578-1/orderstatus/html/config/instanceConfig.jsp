<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--
   Order Status Gear
   gearmode = instanceConfig 
  
   This page displays the order status gear in instanceConfig mode. The page allows 
   community leader to specify the look-and-feel for the gear content pages.    
--%>

<dsp:importbean bean="/atg/portal/gear/GearConfigFormHandler"/>
<dsp:importbean bean="/atg/commerce/states/OrderStates"/>

<paf:CommunityLeaderCheck>
<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">

<%
String origURI= request.getRequestURI(); 
String successUrl = request.getParameter("paf_success_url");
%>


<i18n:bundle baseName="atg.commerce.gears.orderstatus.OrderStatusResources" localeAttribute="userLocale" changeResponseLocale="false" />
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
    <i18n:message key="instanceConfigPageTitle">Configure the Order Status gear</i18n:message>
  </b>
  <p>
      
  <%-- Display configuration parameters to community leader --%>
      
<%--
  <dsp:getvalueof id="stateMap" idtype="java.util.Map" bean="OrderStates.stateValueMap">


      <core:if value='<%=stateMap.isEmpty()%>'>
        order states is empty.<br>
      </core:if>
      

     size is <%=stateMap.size()%> <br> 
            
  </dsp:getvalueof>
  --%>    
  
  <br>
  <dsp:getvalueof id="stateMap" idtype="java.util.Map" bean="OrderStates.stateValueMap">
  <i18n:message key="instanceCheckOrderState">Check the order state filters you want to appear on the full view page. For the gear to work, you should select only those states that are supported by your site.</i18n:message>
  <blockquote>
   <%-- Show check-boxes for each state and let community leader select whether the state
   should be shown or not. A state is shown only if that state is present in atg.commerce.states.OrderStates --%>
     <core:exclusiveIf>
      <core:if value='<%=stateMap.containsKey("submitted")%>'>            
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowSubmittedFilterFull" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowSubmittedFilterFull"))%>'/>
        <i18n:message key="instanceShowSubmittedOrders">submitted</i18n:message>
        <br>      
      </core:if>
      
      <core:defaultCase>
        <dsp:setvalue bean="GearConfigFormHandler.values.ShowSubmittedFilterFull" value="false"/> 
            <%-- <dsp:input type="hidden" bean="GearConfigFormHandler.values.ShowSubmittedFilterFull" value="<%=false%>"/> --%>           
      </core:defaultCase>
    </core:exclusiveIf>
              
    <core:exclusiveIf>
      <core:if value='<%=stateMap.containsKey("pending_approval")%>'>            
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowPendingApprovalFilterFull" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowPendingApprovalFilterFull"))%>'/>
        <i18n:message key="instanceShowPendingApprovedGears">pending approval</i18n:message>
        <br>
      </core:if>
      <core:defaultCase>
        <dsp:setvalue bean="GearConfigFormHandler.values.ShowPendingApprovalFilterFull" value="false"/> 
      </core:defaultCase>
    </core:exclusiveIf>
    
    <core:exclusiveIf>
      <core:if value='<%=stateMap.containsKey("pending_merchant_action")%>'>
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowPendingMerchantActionFilterFull" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowPendingMerchantActionFilterFull"))%>'/>
        <i18n:message key="instanceShowPendingMerchantActionGears">pending merchant action</i18n:message>
        <br>
      </core:if>
      <core:defaultCase>
        <dsp:setvalue bean="GearConfigFormHandler.values.ShowPendingMerchantActionFilterFull" value="false"/> 
      </core:defaultCase>
    </core:exclusiveIf>
       
    <core:exclusiveIf>
      <core:if value='<%=stateMap.containsKey("approved")%>'>      
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowApprovedFilterFull" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowApprovedFilterFull"))%>'/>
        <i18n:message key="instanceShowApprovedOrders">approved</i18n:message>           
        <br>
      </core:if>
      <core:defaultCase>
        <dsp:setvalue bean="GearConfigFormHandler.values.ShowApprovedFilterFull" value="false"/> 
      </core:defaultCase>
    </core:exclusiveIf>
      
    <core:exclusiveIf>
      <core:if value='<%=stateMap.containsKey("failed")%>'>            
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowRejectedFilterFull" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowRejectedFilterFull"))%>'/>
        <i18n:message key="instanceShowRejectedOrders">rejected</i18n:message>
        <br>
      </core:if>
      <core:defaultCase>
        <dsp:setvalue bean="GearConfigFormHandler.values.ShowRejectedFilterFull" value="false"/>
      </core:defaultCase>
    </core:exclusiveIf>
           
    <core:exclusiveIf>
      <core:if value='<%=stateMap.containsKey("no_pending_action")%>'>            
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowShippedFilterFull" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowShippedFilterFull"))%>'/>
        <i18n:message key="instanceShowShippedOrders">shipped</i18n:message>
        <br>
      </core:if>
      <core:defaultCase>
        <dsp:setvalue bean="GearConfigFormHandler.values.ShowShippedFilterFull" value="false"/> 
      </core:defaultCase>
    </core:exclusiveIf>

    <core:exclusiveIf>
      <core:if value='<%=stateMap.containsKey("pending_remove")%>'>            
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowPendingRemoveFull" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowPendingRemoveFull"))%>'/>
        <i18n:message key="instanceShowPendingRemoveOrders">pending removal</i18n:message>
        <br>
      </core:if>
      <core:defaultCase>
        <dsp:setvalue bean="GearConfigFormHandler.values.ShowPendingRemoveFull" value="false"/> 
      </core:defaultCase>
    </core:exclusiveIf>

    <core:exclusiveIf>
      <core:if value='<%=stateMap.containsKey("removed")%>'>            
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowRemovedFull" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowRemovedFull"))%>'/>
        <i18n:message key="instanceShowRemovedOrders">removed</i18n:message>
        <br>
      </core:if>
      <core:defaultCase>
        <dsp:setvalue bean="GearConfigFormHandler.values.ShowRemovedFull" value="false"/> 
      </core:defaultCase>
    </core:exclusiveIf>

    <core:exclusiveIf>
      <core:if value='<%=stateMap.containsKey("pending_customer_action")%>'>
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowPendingCustomerActionFull" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowPendingCustomerActionFull"))%>'/>
        <i18n:message key="instanceShowPendingCustomerActionOrders">pending customer action</i18n:message>
        <br>
      </core:if>
      <core:defaultCase>          
        <dsp:setvalue bean="GearConfigFormHandler.values.ShowPendingCustomerActionFull" value="false"/>
      </core:defaultCase>
    </core:exclusiveIf>

    <core:exclusiveIf>
      <core:if value='<%=stateMap.containsKey("pending_customer_return")%>'>            
        <dsp:input type="checkbox" bean="GearConfigFormHandler.values.ShowPendingCustomerReturnFull" checked='<%= "true".equals(gearEnv.getGearInstanceParameter("ShowPendingCustomerReturnFull"))%>'/>
        <i18n:message key="instanceShowPendingCustomerReturnOrders">pending customer return</i18n:message>
        <br>
      </core:if>
      <core:defaultCase>
        <dsp:setvalue bean="GearConfigFormHandler.values.ShowPendingCustomerReturnFull" value="false"/> 
      </core:defaultCase>
    </core:exclusiveIf>
        
  </dsp:getvalueof>  
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
<%-- @version $Id: //product/DCS/version/10.0.3/gears/OrderStatus/orderstatus.war/html/config/instanceConfig.jsp#2 $$Change: 651448 $--%>
