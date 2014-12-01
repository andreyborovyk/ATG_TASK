<%@ page import="atg.portal.servlet.*,atg.portal.framework.*"%>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/gears/orderstatus/OrderStatusGear"/>

<%--
   Order Status Gear
   gearmode = content 
   displaymode = shared
  
   This page displays a summary view of the orders.    
--%>

<dsp:page>

<paf:InitializeGearEnvironment id="gearEnv">

<%-- The gear should be displayed only if the user is logged in. This check should 
be in layout template that includes this gear. However just to be sure we show the
contents of the gear only if the user is logged in.
--%>

<%-- Include the message resources. --%>
<i18n:bundle baseName="atg.commerce.gears.orderstatus.OrderStatusResources" localeAttribute="userLocale" changeResponseLocale="false" />

 <%
   PortalServletResponse portalServletResponse = 
      (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
   PortalServletRequest portalServletRequest = 
      (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
 
   PortalContext portalContext = new PortalContextImpl(portalServletRequest);
   ((PortalContextImpl)portalContext).setDisplayMode(DisplayMode.FULL);
 %>
 <%
   String clearGif = gearEnv.getGear().getServletContext() + "/html/content/images/clear.gif";
 %>

  <dsp:droplet name="/atg/dynamo/droplet/Switch">
    <dsp:param bean="Profile.transient" name="value"/>
    <dsp:oparam name="false">
    
      <dsp:getvalueof id="profileId" idtype="java.lang.String" bean="Profile.id">   

      <%-- padding table --%>
      <table border=0 cellpadding=2 cellspacing=1 width=100%>
        <tr>
          <td>
          <%-- content table. Normally this would go in the outputStart oparam but 
               because we can show or not show the different sections with user
               preferences we want the table to appear if there are no order orders. --%>
          <table border=0 cellpadding=0 cellspacing=4 width=100%>
          
          <%-- Show the list of recent orders. --%>
          <dsp:droplet name="OrderLookup">
            <dsp:param value="<%=profileId%>" name="userId"/>
            
            <%-- Show orders that have states specified in sharedViewStates property of Order Status gear component. --%>
            <dsp:param name="state" value="open"/>
            <dsp:getvalueof id="stateList" idtype="java.lang.String[]" bean="OrderStatusGear.sharedViewStates">
               <dsp:param value="<%=stateList%>" name="openStates"/>
            </dsp:getvalueof>
            
            <%-- Sort the orders here by descending value of sorted date. --%>
            <dsp:param value="submittedDate" name="sortBy"/>
	    <dsp:param name="startIndex" value="0"/>
	    
	    <%-- The number of orders shown is based on userConfig paramteter. --%>
            <dsp:param name="numOrders" value='<%=gearEnv.getGearUserParameter("NumberOfOrdersShared")%>'/>

            <dsp:oparam name="output">
              <dsp:droplet name="ForEach">
                <dsp:param name="array" param="result"/>
      
                <dsp:oparam name="outputStart" >         
                  <%-- Display all titles. --%>
                  <tr valign=bottom>
                    <td width=18%><span class=small>
                      <i18n:message key="sharedOrderTitle"/></span></td>
                    <td width=32%><span class=small>
                      <i18n:message key="sharedDateTitle"/></span></td>
                    <td width=50%><span class=small>
                      <i18n:message key="sharedStatusTitle"/></span></td>
                  </tr>
		  <tr>
		    <td colspan=3 bgcolor="#A2AE9E"><img
		    src="<%=clearGif%>" height=1></td>
		  </tr>
                </dsp:oparam>
                <dsp:oparam name="outputEnd" >
                  <tr>
		    <td colspan=3 bgcolor="#A2AE9E"><img
		    src="<%=clearGif%>" height=1></td>
		  </tr>
		</dsp:oparam>
                <dsp:oparam name="output">
                  <%-- Display order_id, submitted_date, state for all the orders. --%>
                  <tr>
                    <td><span class="smaller">&nbsp;
                        <dsp:a href='<%=gearEnv.getGearInstanceParameter("OrderPage")%>'>
                        <dsp:param name="orderId" param="element.id"/>
                        <dsp:valueof param="element.id"/></dsp:a></span></td>
                    <td><span class="smaller">
                        <i18n:message id="date_format" key="dateFormatLong"/>
                        <dsp:valueof date="<%=date_format%>" param="element.submittedDate"/></span></td>
                    <td><span class="smaller">
                        <dsp:droplet name="/atg/commerce/order/OrderStatesDetailed">
                          <dsp:param name="state" param="element.state"/>
                          <dsp:oparam name="output">
                            <dsp:valueof param="detailedState"><i18n:message key="unknown"/></dsp:valueof>
                          </dsp:oparam>
                        </dsp:droplet></span>
                    </td>
                  </tr>                                                                
                </dsp:oparam>
                  
             </dsp:droplet><%-- end ForEach --%>

           </dsp:oparam><%-- end OrderLookup output--%>
         
           <dsp:oparam name="empty">
              <%-- no orders for user --%>
              <br><span class=small>
                <i18n:message key="sharedNoOrders"/></span><br>
            </dsp:oparam>
            <dsp:oparam name="error">
              <dsp:valueof param="errorMsg"/>
            </dsp:oparam>
                        
          </dsp:droplet><%-- end OrderLookup droplet --%> 
          
          <%-- End of the code to show the list of recent orders. --%>
          
          <%-- Show the count of total open orders. User can disable this. --%>
          <tr>
            <td colspan=3>
            <core:if value='<%=gearEnv.getGearUserParameter("ShowOpenOrdersShared")%>'>
              <dsp:droplet name="OrderLookup">
                <dsp:param value="<%=profileId%>" name="userId"/>
                <dsp:param name="state" value="open"/>
                <dsp:oparam name="output">
                  <span class=smaller><i18n:message key="sharedTotalOpen"/>
                  <dsp:valueof param="total_count"/> 
                  <i18n:message key="sharedOpenOrders"/></span><br>
                </dsp:oparam>
              </dsp:droplet>
            </core:if>
          
            </td>
          </tr> 
          <tr> 
            <td colspan=3>
            
            <%-- Show link to full view of the gear. --%>
            <core:CreateUrl id="fullview" url="<%= gearEnv.getOriginalRequestURI() %>">
              <core:UrlParam param="startIndex" value="0"/>
                <span class=small>
                   <a href="<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>"><i18n:message key="sharedListAllOrders"/></a></span>
            </core:CreateUrl>
            </td>
          </tr>
        </table>

      </dsp:getvalueof>
      
    </td></tr></table>
    </dsp:oparam>
  </dsp:droplet> 

</paf:InitializeGearEnvironment> 

</dsp:page>
<%-- @version $Id: //product/DCS/version/10.0.3/gears/OrderStatus/orderstatus.war/html/content/OrderStatusShared.jsp#2 $$Change: 651448 $--%>
