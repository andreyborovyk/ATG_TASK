<%@ page import="atg.portal.servlet.*,atg.portal.framework.*"%>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/approval/ApprovalRequiredDroplet"/>
<dsp:importbean bean="/atg/userprofiling/servlet/ProfileRepositoryItemServlet"/>
<dsp:importbean bean="/atg/userdirectory/droplet/HasFunction"/>
<dsp:importbean bean="/atg/commerce/states/OrderStates"/>

<%--
   Order Approval Gear
   gearmode = content 
   displaymode = shared
  
   This page displays a summary view of the orders.    
--%>

<dsp:page>

<paf:InitializeGearEnvironment id="gearEnv">

<%-- The gear should be displayed only if the user is logged in and if the user
has permisions to approve orders. This check should be in layout template that 
includes this gear. However just to be sure we show the contents of the gear 
only if the user is logged in.
--%>

<%-- Include the message resources. --%>
<i18n:bundle baseName="atg.commerce.gears.orderapproval.OrderApprovalResources" localeAttribute="userLocale" changeResponseLocale="false" />

<%
  PortalServletResponse portalServletResponse = 
     (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);

  PortalContext portalContext = new PortalContextImpl(portalServletRequest);
  ((PortalContextImpl)portalContext).setDisplayMode(DisplayMode.FULL);
%>
<% String clearGif = gearEnv.getGear().getServletContext() + "/html/images/clear.gif"; %>

<dsp:droplet name="HasFunction"> 
 <dsp:param bean="Profile.id" name="userId"/> 
 <dsp:param name="function" value="approver"/> 
 <dsp:oparam name="true"> 

  <dsp:droplet name="Switch">
    <dsp:param bean="Profile.transient" name="value"/>
    <dsp:oparam name="false">
    
     <dsp:getvalueof id="profileId" idtype="java.lang.String" bean="Profile.id">   

      <%-- padding table --%>
      <table border=0 cellpadding=2 width=100%>
        <tr>
          <td>
          <%-- content table. Normally this would go in the outputStart oparam but 
               because we can show or not show the different sections with user
               preferences we want the table to appear if there are no order orders. --%>
          <table border=0 cellpadding=0 cellspacing=4 width=100%>
          
	   <dsp:droplet name="ApprovalRequiredDroplet">
            <dsp:param name="approverid" value="<%=profileId%>"/>
            <dsp:param name="startIndex" value="0"/>
            <dsp:param name="numOrders" value='<%=gearEnv.getGearUserParameter("NumberOfOrdersShared")%>'/>
            <dsp:oparam name="output">
	     <dsp:droplet name="ForEach">
              <dsp:param name="array" param="result"/>
              <dsp:param name="elementName" value="order"/>
              <dsp:oparam name="outputStart">         
                  
                  <%-- Display all titles. --%>
                  <tr valign=bottom>
                    <td><span class=small>
                      <i18n:message key="sharedOrderTitle"/></span></td>
                    <td><span class=small>
                      <i18n:message key="sharedDateTitle"/></span></td>
                    <td><span class=small>
                      <i18n:message key="sharedTotalTitle"/></span></td>
                    <td><span class=small>
                      <i18n:message key="sharedBuyerTitle"/></span></td>
                    <td><span class=small>
                      <i18n:message key="sharedStatusTitle"/></span></td>
                  </tr>
		  <tr>
		    <td colspan=5 bgcolor="#A2AE9E"><img src="<%=clearGif%>" height=1></td>
		  </tr>
		    
              </dsp:oparam>

              <dsp:oparam name="output">
                  <%-- Display order_id, submitted_date, state for all the orders. --%>
                  <tr>
                    <td><span class="smaller">&nbsp;
                     <core:if value='<%=gearEnv.getGearInstanceParameter("UseOrderApprovalOfGear")%>'>
                      <core:CreateUrl id="fullview" url="<%= gearEnv.getOriginalRequestURI() %>">
                       <core:UrlParam param="orderApprovalPage" value="detail"/>
		       <dsp:getvalueof id="orderId" param="order.id">
		         <core:UrlParam param="orderId" value='<%= orderId %>'/>
		       </dsp:getvalueof>
		       <core:UrlParam param="startIndex" value="0"/>
                       <dsp:a href='<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>'>
                        <dsp:valueof param="order.id"/></dsp:a>
                      </core:CreateUrl></span></td>
		     </core:if>
                     <core:ifNot value='<%=gearEnv.getGearInstanceParameter("UseOrderApprovalOfGear")%>'>
		      <dsp:a href='<%=gearEnv.getGearInstanceParameter("PendingApprovalOrderPageURL")%>'>
                       <dsp:param name="orderId" param="order.id"/>
	               <dsp:valueof param="order.id"/></dsp:a>
		     </core:ifNot>
                    <td><span class="smaller">
											  <dsp:getvalueof id="submitDate" param="order.submittedDate">
        									<i18n:formatDate value='<%= submitDate %>' style="short"/>
        								</dsp:getvalueof>
												</span></td>
                    <td><span class="smaller">
                        <dsp:valueof converter="currency" param="order.priceInfo.rawSubtotal"/></span></td>
                    <td><span class="smaller">
                        <dsp:droplet name="ProfileRepositoryItemServlet">
 			 <dsp:param name="id" param="order.profileId"/>
                         <dsp:oparam name="output">
                          <dsp:getvalueof id="first_name" param="item.firstName">
                          <dsp:getvalueof id="last_name" param="item.lastName">
                            <i18n:message key="fullname">
                              <i18n:messageArg value="<%=first_name%>" />
                              <i18n:messageArg value="<%=last_name%>" />
                            </i18n:message>
                          </dsp:getvalueof>
                          </dsp:getvalueof>
	                 </dsp:oparam>
                        </dsp:droplet></span></td>
                    <td><span class="smaller">                                      
                                      
                          <dsp:getvalueof id="stateAsString" idtype="String" param="order.state">
                                      
                            <%= atg.commerce.states.StateDefinitions.ORDERSTATES.getStateDescriptionAsUserResource(stateAsString) %>
                          </dsp:getvalueof>
                    
                        </span>                                        
                    
                        <%--  <dsp:valueof param="order.state"/></span> --%>
                    </td>
                  </tr>                                                                
                </dsp:oparam>
                  
             </dsp:droplet><%-- end ForEach --%>

           </dsp:oparam><%-- end ApprovalRequiredDroplet output--%>

           
         
           <dsp:oparam name="empty">
	     <%-- no orders for user --%>
              <br><span class=small>
                <i18n:message key="sharedNoOrders"/></span><br>
            </dsp:oparam>
            <dsp:oparam name="error">
              <dsp:valueof param="errorMsg"/>
            </dsp:oparam>
                        
          </dsp:droplet><%-- end ApprovalRequiredDroplet droplet --%> 
          
          <%-- End of the code to show the list of recent orders. --%>

          <tr>
	    <td><img src="<%=clearGif%>" height=1></td>
          <tr>
          <%-- Show the count of total open orders. User can disable this. --%>
          <tr>
            <td colspan=3>
            <core:if value='<%=gearEnv.getGearUserParameter("ShowApprovedCountShared")%>'>
              <dsp:droplet name="ApprovalRequiredDroplet">
                <dsp:param value="<%=profileId%>" name="approverid"/>
                <dsp:oparam name="output">
                  <span class=smaller><i18n:message key="sharedTotalRequests"/>
		  <dsp:getvalueof id="totalRequestCount" idtype="java.lang.Integer" param="totalCount">
                  <core:switch value='<%= gearEnv.getGearUserParameter("NumberOfOrdersFull") %>'>
                   <core:case value="-1">
                    <%= totalRequestCount %>
                   </core:case>
                   <core:defaultCase>
                    <core:ifGreaterThan int1='<%= totalRequestCount.intValue() %>'
                      int2='<%= Integer.parseInt(gearEnv.getGearUserParameter("NumberOfOrdersFull"))%>'>
                     <%= gearEnv.getGearUserParameter("NumberOfOrdersFull") %>
                    </core:ifGreaterThan>
                    <core:ifGreaterThanOrEqual 
		      int1='<%= Integer.parseInt(gearEnv.getGearUserParameter("NumberOfOrdersFull")) %>'
                      int2='<%= totalRequestCount.intValue() %>'>
                     <%= totalRequestCount %>
                    </core:ifGreaterThanOrEqual>
                   </core:defaultCase>
                  </core:switch>
		  </dsp:getvalueof>
                  <i18n:message key="sharedOpenRequests"/></span><br>
                </dsp:oparam>
              </dsp:droplet>
            </core:if>
          
            </td>
          </tr> 
          <tr> 
            <td colspan=5>
            
            <%-- Show link to full view of the gear. --%>
            <core:CreateUrl id="fullview" url="<%= gearEnv.getOriginalRequestURI() %>">
              <core:UrlParam param="startIndex" value="0"/>
	      <core:UrlParam param="orderApprovalPage" value="listOrders"/>
	      <core:UrlParam param="pendingApproval" value="true"/>
                <span class=small>
                   <a href="<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>">
 		    <i18n:message key="sharedViewApprovalRequests"/>
                   </a></span>
            </core:CreateUrl>

	    |

            <core:CreateUrl id="fullview" url="<%= gearEnv.getOriginalRequestURI() %>">
              <core:UrlParam param="startIndex" value="0"/>
	      <core:UrlParam param="orderApprovalPage" value="listOrders"/>
	      <core:UrlParam param="pendingApproval" value="false"/>
                <span class=small>
                   <a href="<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>">
                    <i18n:message key="sharedViewResolved"/>
                   </a></span>
            </core:CreateUrl>

            </td>
          </tr>
        </table>

      </dsp:getvalueof>
      
    </td></tr></table>
    </dsp:oparam>
  </dsp:droplet> 

 </dsp:oparam>
 <dsp:oparam name="false"> 
  <i18n:message key="you_are_not_authorized"/><p>
 </dsp:oparam> 
</dsp:droplet> 

</paf:InitializeGearEnvironment> 

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/gears/OrderApproval/orderapproval.war/html/content/OrderApprovalShared.jsp#2 $$Change: 651448 $--%>
