<%@ page import="atg.portal.servlet.*,atg.portal.framework.*"%>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/userprofiling/servlet/ProfileRepositoryItemServlet"/>
<dsp:importbean bean="/atg/commerce/approval/ApprovalFormHandler"/>

<%--
   Order Approval Gear
   gearmode = content 
   displaymode = full
  
   This page fragment gives the user the ability to
   approve or reject an order.
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
  ((PortalContextImpl)portalContext).setDisplayMode(DisplayMode.FULL);
%>

    <!-- Show any errors -->
	<dsp:droplet name="/atg/dynamo/droplet/Switch">
	 <dsp:param bean="ApprovalFormHandler.formError" name="value"/>
	 <dsp:oparam name="true">
	 &nbsp;<br>
	  <span class=content_alert><i18n:message key="theFollowingErrorsOccurred"/></span><br>
	     <span class=content_alert><UL>
	    <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
	      <dsp:param bean="ApprovalFormHandler.formExceptions" name="exceptions"/>
	      <dsp:oparam name="output">
	        <LI> <dsp:valueof param="message"/>
	      </dsp:oparam>
	    </dsp:droplet>
	    </UL></span> 
	 </dsp:oparam>
	</dsp:droplet>

<table border=0 cellpadding=2 cellspacing=0 width=100%>
 <tr bgcolor=#BBBBBB><td><span class=small><b>
 <core:switch value='<%=request.getParameter("action")%>'>
  <core:case value="approve">
   <i18n:message key="sharedApproveOrderTitle"/>
   <dsp:setvalue param="showOrderInfo" value='<%=gearEnv.getGearInstanceParameter("ShowOrderInfoInApprove")%>'/>
   <dsp:setvalue param="showMessage" value='<%=gearEnv.getGearInstanceParameter("ShowMessageInApprove")%>'/>
  </core:case>
  <core:case value="reject">
   <i18n:message key="sharedRejectOrderTitle"/>
   <dsp:setvalue param="showOrderInfo" value='<%=gearEnv.getGearInstanceParameter("ShowOrderInfoInReject")%>'/>
   <dsp:setvalue param="showMessage" value='<%=gearEnv.getGearInstanceParameter("ShowMessageInReject")%>'/>
  </core:case>
 </core:switch>
 </b></span></td></tr>
</table>

 <core:if value='<%=request.getParameter("showOrderInfo")%>'>

 
 
<table border=0 cellpadding=2 cellspacing=0 width=100%>	 
  <tr><td width="25%" align="right"><span class=small><b><i18n:message key="fullOrderTitle"/></b></span></td>
      <td width="75%"><span class=small><dsp:valueof param="orderId"/></span></td>
  </tr>
<dsp:droplet name="OrderLookup">
 <dsp:param name="orderId" param="orderId"/>
 <dsp:oparam name="output">
     <tr><td width="25%" align="right"><span class=small><b><i18n:message key="fullDateTitle"/></b></span></td>
         <td width="75%"><span class=small><dsp:getvalueof id="orderDate" param="result.submittedDate">
		   <i18n:formatDateTime value='<%= orderDate %>' dateStyle="long" timeStyle="long"/> 
           </dsp:getvalueof></span></td>
     </tr>
	 <tr valign="top">
	    <td align="right"><span class=small><b><i18n:message key="fullBuyerTitle"/></b></span></td>
	    <td><span class=small>
		   <dsp:droplet name="ProfileRepositoryItemServlet">
		    <dsp:param name="id" param="result.profileId"/>
		    <dsp:oparam name="output">
		      <dsp:getvalueof id="first_name" param="item.firstName">
		      <dsp:getvalueof id="last_name" param="item.lastName">
		        <i18n:message key="fullname">
		          <i18n:messageArg value="<%=first_name%>"/>
		          <i18n:messageArg value="<%=last_name%>"/>
		        </i18n:message>
		      </dsp:getvalueof>
		      </dsp:getvalueof>
		      , <dsp:valueof param="item.parentOrganization.name"/>
		    </dsp:oparam>
		   </dsp:droplet>
		</span></td>
		</tr>
 </dsp:oparam>
</dsp:droplet>
</core:if>

 <core:CreateUrl id="fullview" url="<%= portalServletRequest.getPortalContextPath() %>">
  <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
  <core:UrlParam param="orderApprovalPage" value="detail"/>
  <core:UrlParam param="orderId" value='<%= request.getParameter("order.id") %>'/>

   <dsp:form action="<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>" method="post">

  <core:CreateUrl id="fullview2" url="<%= portalServletRequest.getPortalContextPath() %>">
   <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
   <core:UrlParam param="orderApprovalPage" value="approveConfirm"/>
   <core:UrlParam param="orderId" value='<%= request.getParameter("orderId") %>'/>
   <core:UrlParam param="action" value='<%= request.getParameter("action") %>'/>
   <dsp:input type="hidden" bean="ApprovalFormHandler.approveOrderSuccessURL" value="<%= portalServletResponse.encodePortalURL(fullview2.toString(), portalContext) %>"/>
   <dsp:input type="hidden" bean="ApprovalFormHandler.rejectOrderSuccessURL" value="<%= portalServletResponse.encodePortalURL(fullview2.toString(), portalContext) %>"/>
  </core:CreateUrl>

  <core:CreateUrl id="fullview2" url="<%= portalServletRequest.getPortalContextPath() %>">
   <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
   <core:UrlParam param="orderApprovalPage" value="approve"/>
   <core:UrlParam param="orderId" value='<%= request.getParameter("orderId") %>'/>
   <core:UrlParam param="action" value='<%= request.getParameter("action") %>'/>
   <dsp:input type="hidden" bean="ApprovalFormHandler.approveOrderErrorURL" value="<%= portalServletResponse.encodePortalURL(fullview2.toString(), portalContext) %>"/>
   <dsp:input type="hidden" bean="ApprovalFormHandler.rejectOrderErrorURL" value="<%= portalServletResponse.encodePortalURL(fullview2.toString(), portalContext) %>"/>
  </core:CreateUrl>

  <dsp:input type="hidden" bean="ApprovalFormHandler.orderId" paramvalue="orderId"/>
 
 <tr valign="top"> 
   <td align=right><span class=small><b><i18n:message key="sharedMessageTitle"/></b></span></td>
   <td>
    <core:if value='<%=request.getParameter("showMessage")%>'>
	   <span class=small><dsp:textarea bean="ApprovalFormHandler.approverMessage" rows="4" cols="40"></dsp:textarea></span>  
    </core:if>
   </td>
 </tr>
  
  <tr valign="top">
   <td>&nbsp;</td>
   <td><br>
          <i18n:message id="approve_order" key="sharedApproveOrderTitle"/>
          <i18n:message id="reject_order" key="sharedRejectOrderTitle"/>
          <i18n:message id="cancel" key="cancelButton"/>
          
	  <core:switch value='<%=request.getParameter("action")%>'>
	   <core:case value="approve">
	    <dsp:input type="submit" bean="ApprovalFormHandler.approveOrder" value="<%=approve_order%>"/>
	   </core:case>
	   <core:case value="reject">
	    <dsp:input type="submit" bean="ApprovalFormHandler.rejectOrder" value="<%=reject_order%>"/>
	   </core:case>
	  </core:switch>

	   <input type="submit" value="<%=cancel%>">  
   </td>
 </tr>

   </dsp:form>
 </core:CreateUrl>


</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/gears/OrderApproval/orderapproval.war/html/content/approveOrder.jsp#2 $$Change: 651448 $--%>
