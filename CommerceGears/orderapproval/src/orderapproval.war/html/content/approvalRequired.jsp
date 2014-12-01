<%@ page import="atg.portal.servlet.*,atg.portal.framework.*"%>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/commerce/catalog/ProductLookup"/>
<dsp:importbean bean="/atg/commerce/approval/ApprovalRequiredDroplet"/>
<dsp:importbean bean="/atg/userprofiling/servlet/ProfileRepositoryItemServlet"/>

<%--
   Order Approval Gear
   gearmode = content 
   displaymode = full
  
   This page fragment displays those orders that require the
   user's approval. 
--%>


<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">

<%-- The gear should be displayed only if the user is logged in. --%>
<i18n:bundle baseName="atg.commerce.gears.orderapproval.OrderApprovalResources" localeAttribute="userLocale" changeResponseLocale="false" />

<%
  PortalServletResponse portalServletResponse = 
     (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);

  PortalContext portalContext = new PortalContextImpl(portalServletRequest);
%>

   <dsp:getvalueof id="profileId" idtype="java.lang.String" bean="Profile.id">   

     <dsp:droplet name="ApprovalRequiredDroplet">
       <dsp:param name="approverid" value="<%=profileId%>"/>
       <dsp:param name="startIndex" value='<%=request.getParameter("startIndex")%>'/>
       <dsp:param name="numOrders" value='<%=request.getParameter("numOrdersDisplay")%>'/>
       <dsp:oparam name="output">
       
       <dsp:droplet name="ForEach">
           <dsp:param name="array" param="result"/>
           <dsp:param name="elementName" value="order"/>

           <dsp:oparam name="outputStart">                    
              <%-- Display all titles. --%>
              <tr valign=top bgcolor=#BBBBBB><td colspan=6><i18n:message key="sharedTitle"/></td></tr>
              <tr><td colspan="6">&nbsp;</td></tr>
              <tr valign=top>
               <td><span class=small><b><i18n:message key="fullOrderTitle"/></b></span></td>
               <td><span class=small><b><i18n:message key="fullDateTitle"/></b></span></td>
               <td><span class=small><b><i18n:message key="fullTotalTitle"/></b></span></td>
               <td><span class=small><b><i18n:message key="fullBuyerTitle"/></b></span></td>
               <td><span class=small><b><i18n:message key="fullDescriptionTitle"/></b></span></td>
               <td><span class=small><b><i18n:message key="fullStatusTitle"/></b></span></td>
              </tr>
              <tr><td colspan="6"><hr size="1" noshade color="#666666"></td></tr>
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
                  <dsp:a href='<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>'>
                   <dsp:valueof param="order.id"/></dsp:a>
                 </core:CreateUrl>
        </core:if>
                <core:ifNot value='<%=gearEnv.getGearInstanceParameter("UseOrderApprovalOfGear")%>'>
          <dsp:a href='<%=gearEnv.getGearInstanceParameter("PendingApprovalOrderPageURL")%>'>
                   <dsp:param name="orderId" param="order.id"/><dsp:valueof param="order.id"/></dsp:a>
        </core:ifNot>
        </span></td>
               <td><span class="smaller">
           <dsp:getvalueof id="orderDate" param="order.submittedDate">
               <i18n:formatDateTime value='<%= orderDate %>' dateStyle="long" timeStyle="long"/> 
               </dsp:getvalueof><br>
          </span></td>
               <td><span class="smaller"></span><dsp:valueof converter="currency" param="order.priceInfo.rawSubtotal"/></span></td>
               <td><span class="smaller">
                   <dsp:droplet name="ProfileRepositoryItemServlet">
             <dsp:param name="id" param="order.profileId"/>
                     <dsp:oparam name="output">
                       <dsp:getvalueof id="first_name" param="item.firstName">
                       <dsp:getvalueof id="last_name" param="item.lastName">
                         <i18n:message key="fullname">
                           <i18n:messageArg value="<%=first_name%>"/>
                           <i18n:messageArg value="<%=last_name%>"/>
                         </i18n:message>
                       </dsp:getvalueof>
                       </dsp:getvalueof>
           </dsp:oparam>
                   </dsp:droplet>
         </span></td>
               <td><span class=smaller>
                      <%-- Show the description - Print the product name and quantity. --%>
                      <dsp:droplet name="IsEmpty">
          <dsp:param name="value" param="order.commerceItems" />
        <dsp:oparam name="false">
       <dsp:getvalueof id="productId" param="order.commerceItems[0].productId">
                   <dsp:droplet name="ProductLookup">
                          <dsp:param name="id" value="<%=productId%>"/>
                          <dsp:param name="elementName" value="product"/>
                          <dsp:oparam name="output">
                           <dsp:valueof param="product.displayName"/> -
                          </dsp:oparam>
                         </dsp:droplet>
                        </dsp:getvalueof>
                        <dsp:valueof param="order.commerceItems[0].quantity"/>
                       </dsp:oparam>
                      </dsp:droplet>
               </span></td>
         <td><span class="smaller"><dsp:valueof param="order.state"/></span></td>
             </tr>                                                                
           </dsp:oparam>         

           <dsp:oparam name="outputEnd">
             <tr><td colspan="6"><hr size="1" noshade color="#666666"></td></tr>  
       <tr><td colspan="6">
               <table border=0 cellpadding=0 cellspacing=0 width=100%>
                 <tr>
                  <%-- Show count of orders. --%>
                  <td><span class=smaller>
                        <i18n:message key="fullViewOrder"/>
                        <dsp:valueof param="startRange"/> - <dsp:valueof param="endRange"/> 
                        <i18n:message key="fullOutOf"/>
                        
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
                </span>
               </td>
               
               <%-- Show Previous and Next links if required. --%>
               <td><span class=smaller>
                 <dsp:droplet name="IsEmpty">
                   <dsp:param name="value" param="previousIndex"/>
                   <dsp:oparam name="false">
                    <dsp:getvalueof id="prevId" idtype="Integer" param="previousIndex">
                     <core:CreateUrl id="fullview" url="<%= gearEnv.getOriginalRequestURI() %>">
                       <core:UrlParam param="orderApprovalPage" value="listOrders"/>
                       <core:UrlParam param="pendingApproval" value='<%= request.getParameter("pendingApproval")%>'/>
             <core:UrlParam param="startIndex" value="<%= prevId%>"/>
                         <dsp:a href="<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>">
                           <i18n:message key="fullPrevious"/></dsp:a>
                     </core:CreateUrl>
                    </dsp:getvalueof>
                   </dsp:oparam>
              </dsp:droplet>

              <dsp:droplet name="IsEmpty">
               <dsp:param name="value" param="nextIndex"/>
               <dsp:oparam name="false">
  
              <core:switch value='<%=gearEnv.getGearUserParameter("NumberOfOrdersFull")%>'>
                     <core:case value="-1">
                      <dsp:getvalueof id="nextId" idtype="Integer" param="nextIndex">
                       <core:CreateUrl id="fullview" url="<%= gearEnv.getOriginalRequestURI() %>">
                        <core:UrlParam param="orderApprovalPage" value="listOrders"/>
                        <core:UrlParam param="pendingApproval" value='<%= request.getParameter("pendingApproval") %>'/>
                        <core:UrlParam param="startIndex" value="<%= nextId%>"/>
                        <dsp:a href="<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>">
                         <i18n:message key="fullNext"/></dsp:a>
                       </core:CreateUrl>
                      </dsp:getvalueof>
                     </core:case>
                     <core:defaultCase>
                      <core:ifGreaterThan int1='<%=Integer.parseInt(gearEnv.getGearUserParameter("NumberOfOrdersFull"))%>'
                        int2='<%=Integer.parseInt(request.getParameter("nextIndex"))%>'>
                       <dsp:getvalueof id="nextId" idtype="Integer" param="nextIndex">
                        <core:CreateUrl id="fullview" url="<%= gearEnv.getOriginalRequestURI() %>">
                         <core:UrlParam param="orderApprovalPage" value="listOrders"/>
                         <core:UrlParam param="pendingApproval" value='<%= request.getParameter("pendingApproval") %>'/>
                         <core:UrlParam param="startIndex" value="<%= nextId%>"/>
                         <dsp:a href="<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>">
                          <i18n:message key="fullNext"/></dsp:a>
                        </core:CreateUrl>
                       </dsp:getvalueof>
                      </core:ifGreaterThan>
                     </core:defaultCase>
                    </core:switch>
                   </dsp:oparam>
                  </dsp:droplet>
                 </span>&nbsp;
                </td>
               </tr>
              </table>
        </td></tr>
             </dsp:oparam>
             </dsp:droplet><%-- end ForEach --%>

             </dsp:oparam><%-- end ApprovalRequiredDroplet output--%>

             <dsp:oparam name="empty">
                <%-- no orders for user --%>
              <tr><td colspan="6"><br><span class=small><i18n:message key="sharedNoOrders"/></span><br></td></tr>
             </dsp:oparam>
             <dsp:oparam name="error">
              <tr><td colspan="6"><span class="error"><dsp:valueof param="errorMsg"/></span></td></tr>
             </dsp:oparam>                                          
            </dsp:droplet><%-- end ApprovalRequiredDroplet droplet --%> 
          
          <%-- Show the count of total open orders. User can disable this. --%>
          <tr>
            <td colspan=6>
             <dsp:droplet name="ApprovalRequiredDroplet">
               <dsp:param value="<%=profileId%>" name="approverid"/>
               <dsp:oparam name="output">
                 <span class=smaller><i18n:message key="fullTotalRequests"/>
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
                 <i18n:message key="fullOpenRequests"/></span><br>
               </dsp:oparam>
             </dsp:droplet>
            </td>
          </tr> 

      </dsp:getvalueof>
      
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/gears/OrderApproval/orderapproval.war/html/content/approvalRequired.jsp#2 $$Change: 651448 $--%>
