<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:importbean bean="/atg/userdirectory/droplet/HasFunction"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<%--
   Order Approval Gear
   gearmode = content 
   displaymode = full
  
   This page displays a full view of the order approval gear.    
--%>


<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">

<%-- The gear should be displayed only if the user is logged in. --%>
<i18n:bundle baseName="atg.commerce.gears.orderapproval.OrderApprovalResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:droplet name="HasFunction"> 
 <dsp:param bean="Profile.id" name="userId"/> 
 <dsp:param name="function" value="approver"/> 
 <dsp:oparam name="true"> 

  <dsp:droplet name="/atg/dynamo/droplet/Switch">
   <dsp:param name="value" param="orderApprovalPage"/>

   <dsp:oparam name="listOrders">
     <dsp:include page="orderList.jsp" flush="false"/>
   </dsp:oparam>

   <dsp:oparam name="detail">
     <dsp:include page="orderDetail.jsp" flush="false"/>
   </dsp:oparam>

   <dsp:oparam name="approve">
     <dsp:include page="approveOrder.jsp" flush="false"/>
   </dsp:oparam>

   <dsp:oparam name="approveConfirm">
    <dsp:include page="approveConfirm.jsp" flush="false"/>
   </dsp:oparam>

  </dsp:droplet>

 </dsp:oparam>
 <dsp:oparam name="false"> 
  <i18n:message key="you_are_not_authorized"/><p>
 </dsp:oparam> 
</dsp:droplet> 

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/gears/OrderApproval/orderapproval.war/html/content/OrderApprovalFull.jsp#2 $$Change: 651448 $--%>
