<%@ page import="atg.portal.servlet.*,atg.portal.framework.*"%>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--
   Order Status Gear
   gearmode = content 
   displaymode = full
  
   This page displays a full view of the order status gear.    
--%>

<dsp:importbean bean="/atg/commerce/gears/orderstatus/OrderStatusGear"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">

<%-- The gear should be displayed only if the user is logged in. --%>
<i18n:bundle baseName="atg.commerce.gears.orderstatus.OrderStatusResources" localeAttribute="userLocale" changeResponseLocale="false" />

 <%
   PortalServletResponse portalServletResponse = 
      (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
   PortalServletRequest portalServletRequest = 
      (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
 
   PortalContext portalContext = new PortalContextImpl(portalServletRequest);
   ((PortalContextImpl)portalContext).setDisplayMode(DisplayMode.FULL);
 %>

  <i18n:message id="showOrderButton" key="showOrders"/>

  
  <dsp:droplet name="/atg/dynamo/droplet/Switch">
    <dsp:param bean="Profile.transient" name="value"/>
    <dsp:oparam name="false">    
      <core:CreateUrl id="currentpage" url="<%= gearEnv.getOriginalRequestURI() %>">
        <core:ifNotNull value='<%= request.getParameter("startIndex")%>'>
          <core:UrlParam param="startIndex" value='<%= request.getParameter("startIndex")%>'/>
        </core:ifNotNull>
        
      <%-- This may be used to redirect to current page and to remove the handler method. 
      <dsp:form action="<%= currentpage.getNewUrl() %>" method="post"> --%>
      <dsp:form action="<%= portalServletResponse.encodePortalURL(currentpage.toString(), portalContext) %>" method="post"> 

        <%-- Show the gear title --%>

        <table border=0 cellpadding=4>
          <tr>
            <td><span class=smaller><i18n:message key="showOrders"/></span></td> 
            <td>

            <%-- Show a list of states and let user select which state she wants to see.
                 Each state is shown only if it was selected by community leader.
            --%>
            <dsp:select bean="OrderStatusGear.orderStates">
              
              <core:if value='<%=gearEnv.getGearInstanceParameter("ShowSubmittedFilterFull")%>'>
                <dsp:option value="<%=atg.commerce.states.OrderStates.SUBMITTED%>"/>
                <i18n:message key="fullSubmitted"/>
              </core:if>
      
              <core:if value='<%=gearEnv.getGearInstanceParameter("ShowPendingMerchantActionFilterFull")%>'>
                <dsp:option value="<%=atg.commerce.states.OrderStates.PENDING_MERCHANT_ACTION%>"/>
                <i18n:message key="fullPendingMerchantAction"/>
              </core:if>
      
<%--      This state is only shown if B2BCommerce module is included. The instanceConfig will remove this 
          state if B2BCommerce module is not included --%>
              <core:if value='<%=gearEnv.getGearInstanceParameter("ShowPendingApprovalFilterFull")%>'>       
                <dsp:option value="<%=atg.commerce.states.OrderStates.PENDING_APPROVAL%>"/>
                <i18n:message key="fullPendingApproval"/>
              </core:if>


<%--      This state is only shown if B2BCommerce module is included. The instanceConfig will remove this 
          state if B2BCommerce module is not included --%>
              <core:if value='<%=gearEnv.getGearInstanceParameter("ShowApprovedFilterFull")%>'>
                <dsp:option value="<%=atg.commerce.states.OrderStates.APPROVED%>"/>
                <i18n:message key="fullApproved"/>
              </core:if>
                
<%--      This state is only shown if B2BCommerce module is included. The instanceConfig will remove this 
          state if B2BCommerce module is not included --%>
              <core:if value='<%=gearEnv.getGearInstanceParameter("ShowRejectedFilterFull")%>'>
                <dsp:option value="<%=atg.commerce.states.OrderStates.FAILED_APPROVAL%>"/>
                <i18n:message key="fullRejected"/>
              </core:if>


              <core:if value='<%=gearEnv.getGearInstanceParameter("ShowShippedFilterFull")%>'>
                <dsp:option value="<%=atg.commerce.states.OrderStates.NO_PENDING_ACTION%>"/>
                <i18n:message key="fullShipped"/>
              </core:if>

              <core:if value='<%=gearEnv.getGearInstanceParameter("ShowPendingRemoveFull")%>'>
                <dsp:option value="<%=atg.commerce.states.OrderStates.PENDING_REMOVE%>"/>
                <i18n:message key="fullPendingRemove"/>
              </core:if>

              <core:if value='<%=gearEnv.getGearInstanceParameter("ShowRemovedFull")%>'>       
                <dsp:option value="<%=atg.commerce.states.OrderStates.REMOVED%>"/>
                <i18n:message key="fullRemoved"/>
              </core:if>

              <core:if value='<%=gearEnv.getGearInstanceParameter("ShowPendingCustomerActionFull")%>'>       
                <dsp:option value="<%=atg.commerce.states.OrderStates.PENDING_CUSTOMER_ACTION%>"/>
                <i18n:message key="fullPendingCustomerAction"/>
              </core:if>

              <core:if value='<%=gearEnv.getGearInstanceParameter("ShowPendingCustomerReturnFull")%>'>       
                <dsp:option value="<%=atg.commerce.states.OrderStates.PENDING_CUSTOMER_RETURN%>"/>
                <i18n:message key="fullPendingCustomerReturn"/>
              </core:if>
              
              <dsp:option value='all'/>
              <i18n:message key="fullAllOrders"/>
              
              
            </dsp:select>
            

            </td>
         </tr>
 
         <%-- Let the user select the sorting order of the orders displayed.
         --%>
         <tr>
           <td><span class=smaller><i18n:message key="fullSortBy"/></span></td>
           <td>
              <dsp:select bean="OrderStatusGear.sortBy">
                <dsp:option value="id"/><span class=smaller><i18n:message key="fullOrderNumber"/>
                <dsp:option value="state"/><i18n:message key="fullState"/>
                <dsp:option value="submittedDate"/><i18n:message key="fullSubDate"/>
              </dsp:select></td>
           <td><span class=smaller>
             <dsp:input type="checkbox" bean="OrderStatusGear.sortAscending"/>
             <i18n:message key="fullSortAscending"/></td>
          </tr> 
         <tr>
         </tr> 
          
          <tr> 
            <td></td>
            <td>
              <dsp:input type="hidden" bean="OrderStatusGear.successURL" value="<%= portalServletResponse.encodePortalURL(currentpage.toString(), portalContext) %>"/>
              <dsp:input type="hidden" bean="OrderStatusGear.failureURL" value="<%= portalServletResponse.encodePortalURL(currentpage.toString(), portalContext) %>"/>
              
              <dsp:input type="submit" bean="OrderStatusGear.showOrders" value="<%=showOrderButton%>"/>
              <%-- <input type="submit" value="<%=showOrderButton%>">  --%>
            </td>
          </tr>
        </table>
        

        <%-- Show the list of recent orders based on the states selected by the user. --%>
        <dsp:getvalueof id="profileId" idtype="java.lang.String" bean="Profile.id">
        <dsp:getvalueof id="statesList" idtype="java.lang.String" bean="OrderStatusGear.orderStates[0]">
        <dsp:getvalueof id="sortBy" idtype="String" bean="OrderStatusGear.sortBy">
        <dsp:getvalueof id="sortAscending" idtype="Boolean" bean="OrderStatusGear.sortAscending">
        <dsp:getvalueof id="gearOpenStates" idtype="java.lang.String[]" bean="OrderStatusGear.sharedViewStates">                             
                    
          <dsp:droplet name="OrderLookup">
            <dsp:param value="<%=profileId%>" name="userId"/>
          
            <dsp:param name="state" value="open"/>

            <%if((statesList != null) && (statesList.equals("all"))) {%>
                <dsp:param value="<%=gearOpenStates%>" name="openStates"/> 
            <%} else {
                String[] listOfStates = {statesList};%>
                <dsp:param value="<%=listOfStates%>" name="openStates"/>
            <%}%>


            <dsp:param value="<%=sortAscending%>" name="sortAscending"/> 
            <dsp:param value="<%=sortBy%>" name="sortBy"/>
            
            <%-- Number of orders per page is specified by the community leader. --%>
            <dsp:param name="numOrders" value='<%=gearEnv.getGearUserParameter("NumberOfOrdersFull")%>'/>

            <dsp:oparam name="output">
              <dsp:droplet name="ForEach">
                <dsp:param name="array" param="result"/>
                <dsp:oparam name="outputStart">
                <p>
                <table border=0 cellpadding=2>
                  <tr valign=bottom bgcolor="#BBBBBB">
                    <td colspan=2><span class=small>
                      <i18n:message key="fullOrderNumberTitle"/></span></td>
                    <td colspan=2><span class=small>
                      <i18n:message key="fullOrderDateTitle"/></span></td>
                    <td colspan=2><span class=small>
                      <i18n:message key="fullShipDateTitle"/></span></td>
                    <td colspan=2><span class=small>
                      <i18n:message key="fullStatusTitle"/></span></td>
                    <td align=middle colspan=2><span class=small>
                      <i18n:message key="fullAmountTitle"/></span></td>
                    <td colspan=2><span class=small>
                      <i18n:message key="fullDescriptionTitle"/></span></td>
                  </tr>
 
                </dsp:oparam>

                <dsp:oparam name="output">
                  <%-- Show the order id of the order. --%>
                  <tr valign=top>
                    <td><span class=smaller>
                      <dsp:a href='<%=gearEnv.getGearInstanceParameter("OrderPage")%>'>
                        <dsp:param name="orderId" param="element.id"/>
                        <dsp:valueof param="element.id"/></dsp:a>
                        
                        <dsp:droplet name="Switch">
                          <dsp:param name="value" param="element.originOfOrder"/>
                          <dsp:oparam name="default">
                          </dsp:oparam>
                          <dsp:oparam name="scheduledOrder">
                            <font size=-2>*</font>                            
                          </dsp:oparam> 
                        </dsp:droplet>
                        </span>
                      </td>
                      <td></td>
                      
                      <%-- Show the submitted date, completed date, state and the amount of the order. --%>
                      <i18n:message id="date_format" key="dateFormatShort"/>
                      <td><span class=smaller>
                          <dsp:valueof date="<%=date_format%>" param="element.submittedDate"/></span></td>
                      <td></td>
                      <td><span class=smaller>
                          <dsp:valueof date="<%=date_format%>" param="element.completedDate"><i18n:message key="not_applicable"/></dsp:valueof></span></td>
                      <td></td>
                      <td><%-- this should use the OrderStatesDetailed droplet but I want to add a
                               short display so it will fit in the table --%>
                       <span class=smaller><dsp:valueof param="element.stateAsString"/></span></td>
                      <td></td>
                      <td align=right><span class=smaller>
                          <dsp:valueof converter="currency" param="element.priceInfo.amount"/></span></td>
                      <td></td>
                      <%-- Show the description - Print the product name and quantity. --%>
                      <td><span class=smaller>
                        <dsp:getvalueof id="commItems" param="element.commerceItems" idtype="java.util.List">

                          <core:ifNotNull value="commItems">
			    <dsp:valueof param="element.commerceItems[0].quantity"/> -
                            <dsp:valueof param="element.commerceItems[0].auxiliaryData.productRef.displayName"/>
                          </core:ifNotNull>
                        </dsp:getvalueof>
                        </span>
                      </td>                                                                                                        
                  </dsp:oparam>
                  <dsp:oparam name="outputEnd">
                    <tr>
                       <td colspan=11><hr size=1 color="#666666"></td>
                    </tr>
                    </table>
                    <table>
		    
		    <tr>
                      <%-- Show count of orders. --%>
                      <td colspan=11><span class=smaller>
                        <dsp:getvalueof id="start_range" param="startRange">
                        <dsp:getvalueof id="end_ragne"   param="endRange">
                        <dsp:getvalueof id="count"       param="total_count"> 
                          <i18n:message key="fullViewOrder">
                            <i18n:messageArg value="<%=start_range%>" />
                            <i18n:messageArg value="<%=end_ragne%>" />
                            <i18n:messageArg value="<%=count%>" />
                          </i18n:message>
                        </dsp:getvalueof>
                        </dsp:getvalueof>
                        </dsp:getvalueof>
                        </span>
                      </td>
                      
                      <%-- Show Previous and Next links if required. --%>
                      <td><span class=smaller>
                        <dsp:droplet name="IsEmpty">
                          <dsp:param name="value" param="previousIndex"/>
                          <dsp:oparam name="false">
                          <dsp:getvalueof id="prevId" idtype="Integer" param="previousIndex">
                            <core:CreateUrl id="fullview" url="<%= gearEnv.getOriginalRequestURI() %>">
                              <core:UrlParam param="startIndex" value="<%= prevId%>"/>
                                <dsp:a href="<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>">
                                  <i18n:message key="fullPrevious"/>
                                </dsp:a>
                            </core:CreateUrl>
                          </dsp:getvalueof>
                          </dsp:oparam>
                          
                        </dsp:droplet>

                        <dsp:droplet name="IsEmpty">
                          <dsp:param name="value" param="nextIndex"/>
                          <dsp:oparam name="false">
                          
                          <dsp:getvalueof id="nextId" idtype="Integer" param="nextIndex">
                            <core:CreateUrl id="fullview" url="<%= gearEnv.getOriginalRequestURI() %>">
                              <core:UrlParam param="startIndex" value="<%= nextId%>"/>
                                <dsp:a href="<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>">
                                  <i18n:message key="fullNext"/>
                                </dsp:a>
                            </core:CreateUrl>
                          </dsp:getvalueof>
                          
                          </dsp:oparam>
                        </dsp:droplet>
                        </font>
                      </td>
                    </tr>
		    <tr>
		      <td colspan=11><span class=smaller>
		      <i18n:message key="fullScheduledOrder"/>
		      </span></td>
                    </tr>
                  </table>
                  </dsp:oparam>

                
                </dsp:droplet>
              </dsp:oparam>
              <dsp:oparam name="empty">
                <%-- no orders for user --%>
                <br>
                <i18n:message key="fullNoOrders"/>
                <br>
              </dsp:oparam>
              <dsp:oparam name="error">
                <dsp:valueof param="errorMsg"/>
              </dsp:oparam>
            </dsp:droplet>           
         </dsp:getvalueof>        
         </dsp:getvalueof>        
         </dsp:getvalueof>
         </dsp:getvalueof>
         </dsp:getvalueof>
        
        </dsp:form>
      </core:CreateUrl>
              

    </dsp:oparam>
  </dsp:droplet>  

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //product/DCS/version/10.0.3/gears/OrderStatus/orderstatus.war/html/content/OrderStatusFull.jsp#2 $$Change: 651448 $--%>
