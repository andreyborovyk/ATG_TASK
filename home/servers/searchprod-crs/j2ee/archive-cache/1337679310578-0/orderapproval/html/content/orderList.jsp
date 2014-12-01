<%@ page import="atg.portal.servlet.*,atg.portal.framework.*"%>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<%--
   Order Approval Gear
   gearmode = content 
   displaymode = full
  
   This page fragment displays a full list of orders, either
   pending approval or resolved. 
--%>


<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">

<%-- The gear should be displayed only if the user is logged in. --%>
<i18n:bundle baseName="atg.commerce.gears.orderapproval.OrderApprovalResources" localeAttribute="userLocale" changeResponseLocale="false" />

 <dsp:droplet name="Switch">
  <dsp:param bean="Profile.transient" name="value"/>
  <dsp:oparam name="false">
    
       <%-- content table. Normally this would go in the outputStart oparam but 
            because we can show or not show the different sections with user
            preferences we want the table to appear if there are no order orders. --%>
       <table border=0 cellpadding=0 cellspacing=5 width=100%>
          
        <core:switch value='<%=gearEnv.getGearUserParameter("NumberOfOrdersFull")%>'>
         <core:case value="-1">
          <dsp:setvalue param="numOrdersDisplay" value='<%=gearEnv.getGearUserParameter("NumberOfOrdersPerPageFull")%>'/>
         </core:case>
         <core:defaultCase>
          <core:ifGreaterThan
            int1='<%=Integer.parseInt(gearEnv.getGearUserParameter("NumberOfOrdersFull"))
		     - Integer.parseInt(request.getParameter("startIndex"))%>'
	    int2='<%=Integer.parseInt(gearEnv.getGearUserParameter("NumberOfOrdersPerPageFull"))%>'>
           <dsp:setvalue param="numOrdersDisplay" value='<%=gearEnv.getGearUserParameter("NumberOfOrdersPerPageFull")%>'/>
          </core:ifGreaterThan>
	  <core:ifGreaterThanOrEqual
            int1='<%=Integer.parseInt(gearEnv.getGearUserParameter("NumberOfOrdersPerPageFull"))%>'
	    int2='<%=Integer.parseInt(gearEnv.getGearUserParameter("NumberOfOrdersFull")) 
	             - Integer.parseInt(request.getParameter("startIndex"))%>'>
           <dsp:setvalue param="numOrdersDisplay" 
             value='<%=Integer.toString(Integer.parseInt(gearEnv.getGearUserParameter("NumberOfOrdersFull")) 
                     - Integer.parseInt(request.getParameter("startIndex")))%>'/>
          </core:ifGreaterThanOrEqual>
         </core:defaultCase>
        </core:switch>

        <core:switch value='<%=request.getParameter("pendingApproval")%>'>
         <core:case value="true">    
          <dsp:include page="approvalRequired.jsp" flush="false"/>   
         </core:case>
				 <core:case value="false">
          <dsp:include page="approvalResolved.jsp" flush="false"/>
         </core:case>
         </core:switch>
          <%-- End of the code to show the list of recent orders. --%>
	     </table>
    </dsp:oparam>
  </dsp:droplet> 


</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/gears/OrderApproval/orderapproval.war/html/content/orderList.jsp#2 $$Change: 651448 $--%>