<!-- ******** Begin Slot Gear display ******** -->
<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.scenario.targeting.RepositoryItemSlot" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<paf:InitializeGearEnvironment id="pafEnv">

 <%
   String currentSlotVal=pafEnv.getGearInstanceParameter("slotComponent");
   String thisSlotPage=null;

String fullyQualifiedSlotName="dynamo:/atg/registry/Slots/" + currentSlotVal;
PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);

atg.scenario.targeting.RepositoryItemSlot gearSlot = (atg.scenario.targeting.RepositoryItemSlot) portalServletRequest.lookup(fullyQualifiedSlotName);
   String slotItemDescriptor=gearSlot.getItemDescriptorName();
 %>


 <%-- Determine which page to include based on itemDescriptorName of the 
      Slot configured for this gear
  --%>
 <dsp:importbean bean="/atg/portal/gear/slotgear/SlotConfiguration"/>
 <dsp:getvalueof id="slotConfig" bean="SlotConfiguration">
    <% 
       thisSlotPage=((atg.portal.gear.slotgear.SlotConfiguration)slotConfig).getSlotPage(slotItemDescriptor);
     %>
 </dsp:getvalueof>


   <core:ifNotNull value="<%=thisSlotPage%>">
      <jsp:include page="<%=thisSlotPage%>" flush="true"/>
   </core:ifNotNull>


</paf:InitializeGearEnvironment>
</dsp:page>
<!-- ******** End of Slot Gear display ******** -->
<%-- @version $Id: //app/portal/version/10.0.3/slotgear/slotgear.war/sharedSlot.jsp#2 $$Change: 651448 $--%>
