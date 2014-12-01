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
  
   This page fragment gives confirms the approval
   or rejection of an order.
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

<table border=0 cellpadding=2 cellspacing=0 width=100%>
 <tr bgcolor=#BBBBBB><td><span class=small><b>
 <core:switch value='<%=request.getParameter("action")%>'>
  <core:case value="approve">
   <i18n:message key="approvalConfirmationTitle"/>
  </core:case>
  <core:case value="reject">
   <i18n:message key="rejectionConfirmationTitle"/>
  </core:case>
 </core:switch>
 </b></span></td></tr>
</table>
<br>

<table border=0 cellpadding=2 cellspacing=0 width=100%>
 <tr><td><span class=small>
 <core:switch value='<%=request.getParameter("action")%>'>
  <core:case value="approve">
   <p><i18n:message key="theOrderHasBeenApproved"/></p>
  </core:case>
  <core:case value="reject">
   <p><i18n:message key="theOrderHasBeenRejected"/></p>
  </core:case>
 </core:switch>
 </span></td></tr>
</table>
<br>

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
            <i18n:messageArg value="<%=first_name%>" />
            <i18n:messageArg value="<%=last_name%>" />
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

  <tr valign="top">
   <td align=right><span class=small><b><i18n:message key="sharedMessageTitle"/></b></span></td>
   <td><span class=small><dsp:valueof bean="ApprovalFormHandler.approverMessage"/></span></td>
  </tr>
  
  <tr valign="top">
   <td>&nbsp;</td>
   <td><br>
	<core:CreateUrl id="fullview" url="<%= portalServletRequest.getPortalContextPath() %>">
	 <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
	 <core:UrlParam param="orderApprovalPage" value="listOrders"/>
	 <core:UrlParam param="pendingApproval" value="true"/>
	 <dsp:a href="<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>"><i18n:message key="backToOrdersRequiringApproval"/></dsp:a>
	</core:CreateUrl>
	|
	<%
	  ((PortalContextImpl)portalContext).setDisplayMode(DisplayMode.SHARED);
	%>
	<core:CreateUrl id="fullview" url="<%= portalServletRequest.getPortalContextPath() %>">
	 <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
	 <core:UrlParam param="orderApprovalPage" value="listOrders"/>
	 <core:UrlParam param="pendingApproval" value="true"/>
	 <dsp:a href="<%= portalServletResponse.encodePortalURL(fullview.toString(), portalContext) %>"><i18n:message key="backToCommunityHomePage"/></dsp:a>
	</core:CreateUrl>
   </td>
  </tr>
</table>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/gears/OrderApproval/orderapproval.war/html/content/approveConfirm.jsp#2 $$Change: 651448 $--%>
