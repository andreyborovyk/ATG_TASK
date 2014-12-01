<%@ page import="atg.portal.servlet.*,atg.portal.framework.*"%>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/servlet/ProfileRepositoryItemServlet"/>
<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/commerce/order/OrderStatesDetailed"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<%--
   Order Approval Gear
   gearmode = content 
   displaymode = full
  
   This page fragment displays the details of the order selected.  
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

<dsp:droplet name="OrderLookup">
 <dsp:param name="orderId" param="orderId"/>
 <dsp:oparam name="output">
  
  <table border=0 cellpadding=2 cellspacing=0 width=90%>

   <dsp:droplet name="Switch">
    <dsp:param name="value" param="result.state"/>
    <dsp:oparam name="5000">
     <tr>
      <td align=right><span class="medium"><b>
       <core:CreateUrl id="fullview" url="<%= portalServletRequest.getPortalContextPath() %>">
        <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
        <core:UrlParam param="orderApprovalPage" value="approve"/>
        <core:UrlParam param="orderId" value='<%= request.getParameter("result.id") %>'/>
        <core:UrlParam param="action" value="approve"/>
        <dsp:a href='<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>'><i18n:message key="sharedApproveOrderTitle"/></dsp:a>
       </core:CreateUrl> |
       <core:CreateUrl id="fullview" url="<%= portalServletRequest.getPortalContextPath() %>">
        <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
        <core:UrlParam param="orderApprovalPage" value="approve"/>
        <core:UrlParam param="orderId" value='<%= request.getParameter("result.id") %>'/>
        <core:UrlParam param="action" value="reject"/>
        <dsp:a href='<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>'><i18n:message key="sharedRejectOrderTitle"/></dsp:a>
       </core:CreateUrl>
			 </span></b></td>
     </tr>
    </dsp:oparam>
   </dsp:droplet>

   <tr bgcolor=#BBBBBB><td><span class=small><b>
    <dsp:droplet name="Switch">
     <dsp:param name="value" param="result.state"/>
     <dsp:oparam name="5000">
      <i18n:message key="orderPendingApprovalTitle"/>
     </dsp:oparam>
     <dsp:oparam name="5002">
      <i18n:message key="orderRejectedTitle"/>
     </dsp:oparam>
     <dsp:oparam name="default">
      <i18n:message key="orderApprovedTitle"/>
     </dsp:oparam>
    </dsp:droplet>
   </b></span></td></tr>

 <tr><td><br>  
 	 <table border=0 cellpadding=2 cellspacing=0 width=100%>	 

   <core:if value='<%=gearEnv.getGearInstanceParameter("ShowOrderInfoInDetails")%>'>
     <tr><td colspan="2">
		     <span class="medium"><b><i18n:message key="orderInfo">Order Info</i18n:message></b></span>
				 <hr size="1" noshade color="#666666">
		 </td></tr> 
     <tr><td width="25%" align="right"><span class=small><b><i18n:message key="fullOrderTitle"/></b></span></td>
         <td width="60%"><span class=small><dsp:valueof param="orderId"/></span></td>
     </tr>
     <tr><td align="right"><span class=small><b><i18n:message key="fullDateTitle"/></b></span></td>
         <td><span class=small><dsp:getvalueof id="orderDate" param="result.submittedDate">
		   <i18n:formatDateTime value='<%= orderDate %>' dateStyle="long" timeStyle="long"/> 
           </dsp:getvalueof></span></td>
     </tr>
     <tr><td align="right"><span class=small><b><i18n:message key="fullStatusTitle"/></b></span></td>                         
         <td><span class=small>                        
	      <dsp:droplet name="OrderStatesDetailed">
	       <dsp:param name="state" param="result.state"/>
	       <dsp:oparam name="output">
	        <dsp:valueof param="detailedState"><i18n:message key="unknown"/></dsp:valueof>
	       </dsp:oparam>
	      </dsp:droplet></span></td>
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
	            <i18n:messageArg value="<%=first_name%>" />
	            <i18n:messageArg value="<%=last_name%>" />
	          </i18n:message>
	        </dsp:getvalueof>
	        </dsp:getvalueof>
	        <br>
	        <dsp:valueof param="item.email"/><br>
	        <dsp:valueof param="item.parentOrganization.name"/><br>
	       </dsp:oparam>
	      </dsp:droplet>
		   </span></td>
		  </tr>
			<tr><td>&nbsp;</td></tr>
   </core:if>

   <core:if value='<%=gearEnv.getGearInstanceParameter("ShowBillingInfoInDetails")%>'> 
     <tr><td colspan="2">
		     <span class="medium"><b><i18n:message key="billingInfoTitle"/></b></span>
				 <hr size="1" noshade color="#666666">
		 </td></tr>

     <dsp:droplet name="ForEach">
      <dsp:param name="array" param="result.paymentGroups"/>
      <dsp:oparam name="output">
       <tr>
        <td width="25%" align=right><span class=small><b>
          <dsp:getvalueof id="count" param="count">
            <i18n:message key="paymentMethod">
              <i18n:messageArg value="<%=count%>"/>
            </i18n:message>
          </dsp:getvalueof>
        </b></span></td>
        <td width="60%"><span class=small>
         <dsp:droplet name="Switch">
          <dsp:param name="value" param="element.paymentMethod"/>
          <dsp:oparam name="creditCard">
            <dsp:valueof param="element.creditCardType"/> <dsp:valueof param="element.creditCardNumber"/>
            <dsp:getvalueof id="exp_month" param="element.expirationMonth">
            <dsp:getvalueof id="exp_year" param="element.expirationYear">
	      <i18n:message key="expirationDate">
	        <i18n:messageArg value="<%=exp_month%>" />
	        <i18n:messageArg value="<%=exp_year%>" />
	      </i18n:message>
	    </dsp:getvalueof>
	    </dsp:getvalueof>
          </dsp:oparam>
          <dsp:oparam name="giftCertificate">
            <i18n:message key="giftCertificateNumber"/> <dsp:valueof param="element.giftCertificateNumber"/>
          </dsp:oparam>
	  <dsp:oparam name="storeCredit">
            <i18n:message key="storeCreditNumber"/> <dsp:valueof param="element.storeCreditNumber"/>
          </dsp:oparam>
          <dsp:oparam name="invoiceRequest">
            <i18n:message key="poNumber"/> <dsp:valueof param="element.PONumber"/>
          </dsp:oparam>
          <dsp:oparam name="default">
            <i18n:message key="unknownPaymentGroupType"/>
          </dsp:oparam>
         </dsp:droplet>
        </span></td>
       </tr>
       <tr>
        <td align=right><span class=small><b><i18n:message key="amountTitle"/></b></span></td>
        <td><span class=small><dsp:valueof converter="currency" param="element.amount"/></span></td>
       </tr>
       <tr valign="top">
        <td align=right><span class=small><b><i18n:message key="billingAddressTitle"/></b></span></td>
        <td><span class=small>
	 <dsp:droplet name="Switch">
          <dsp:param name="value" param="element.paymentMethod"/>
          <dsp:oparam name="creditCard">
            <dsp:getvalueof id="pval0" param="element.billingAddress"><dsp:include page="DisplayAddress.jsp" flush="false"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
          </dsp:oparam>
          <dsp:oparam name="invoiceRequest">
            <dsp:getvalueof id="pval0" param="element.billingAddress"><dsp:include page="DisplayAddress.jsp" flush="false"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
          </dsp:oparam>
         </dsp:droplet>
        </span></td>
       </tr>
      </dsp:oparam>
     </dsp:droplet>

     <tr valign="top">
      <td align=right><span class=small><b><i18n:message key="costCenters"/></b></span></td>
      <td><span class=small>
       <dsp:droplet name="ForEach">
        <dsp:param name="array" param="result.costCenters"/> 
         <dsp:oparam name="output">
          <dsp:valueof param="element.Identifier"><i18n:message key="not_applicable"/></dsp:valueof> -
          <dsp:droplet name="ForEach">
	   <dsp:param name="array" bean="Profile.costCenters"/>
           <dsp:param name="elementName" value="CC"/>
	   <dsp:oparam name="output">
	    <dsp:droplet name="Switch">
	     <dsp:param name="value" param="CC.identifier"/>
       <dsp:getvalueof id="ccId" param="element.Identifier" idtype="String">
        <dsp:oparam name="<%=ccId%>">
         <dsp:valueof param="CC.description"/>
        </dsp:oparam>
       </dsp:getvalueof>
      </dsp:droplet>
	   </dsp:oparam>
          </dsp:droplet>
					<BR>
         </dsp:oparam>
       </dsp:droplet>
      &nbsp;</span></td>
     </tr>
		<tr><td>&nbsp;</td></tr>
   </core:if>

   <core:if value='<%=gearEnv.getGearInstanceParameter("ShowShippingInfoInDetails")%>'>
     <dsp:droplet name="ForEach">
      <dsp:param name="array" param="result.shippingGroups"/>
      <dsp:param name="elementName" value="sGroup"/>
      <dsp:oparam name="output">

       <dsp:droplet name="Switch">
        <dsp:param name="value" param="size"/>
        <dsp:oparam name="1">
         <dsp:getvalueof id="pval0" param="sGroup">        
          <dsp:include page="displaySingleShipping.jsp" flush="false">
            <dsp:param name="shippingGroup" value="<%=pval0%>"/>
            <dsp:param name="order" param="result"/>
          </dsp:include>
         </dsp:getvalueof>
        </dsp:oparam>
        <dsp:oparam name="default">
         <dsp:getvalueof id="pval0" param="sGroup">
          <dsp:include page="displayMulShipping.jsp" flush="false">
           <dsp:param name="shippingGroup" value="<%=pval0%>"/>
           <dsp:param name="order" param="result"/>
          </dsp:include>
         </dsp:getvalueof>
        </dsp:oparam>
       </dsp:droplet>
      </dsp:oparam>
     </dsp:droplet>
   </core:if>
     </table>
		 <br>
	   
		 </td>
   </tr>  
   
	 <dsp:droplet name="Switch">
    <dsp:param name="value" param="result.state"/>
    <dsp:oparam name="5000">
     <tr>
      <td align=right><span class="medium"><b>
       <core:CreateUrl id="fullview" url="<%= portalServletRequest.getPortalContextPath() %>">
        <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
        <core:UrlParam param="orderApprovalPage" value="approve"/>
        <core:UrlParam param="orderId" value='<%= request.getParameter("result.id") %>'/>
        <core:UrlParam param="action" value="approve"/>
        <dsp:a href='<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>'><i18n:message key="sharedApproveOrderTitle"/></dsp:a>
       </core:CreateUrl> |
       <core:CreateUrl id="fullview" url="<%= portalServletRequest.getPortalContextPath() %>">
        <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
        <core:UrlParam param="orderApprovalPage" value="approve"/>
        <core:UrlParam param="orderId" value='<%= request.getParameter("result.id") %>'/>
        <core:UrlParam param="action" value="reject"/>
        <dsp:a href='<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>'><i18n:message key="sharedRejectOrderTitle"/></dsp:a>
       </core:CreateUrl>
			 </span></b></td>
     </tr>
    </dsp:oparam>
   </dsp:droplet>

  </table>

 </dsp:oparam>
</dsp:droplet>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/gears/OrderApproval/orderapproval.war/html/content/orderDetail.jsp#2 $$Change: 651448 $--%>