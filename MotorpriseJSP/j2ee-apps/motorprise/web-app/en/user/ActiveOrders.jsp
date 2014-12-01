<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/userdirectory/droplet/HasFunction"/>
<dsp:importbean bean="/atg/commerce/approval/ApprovalRequiredDroplet"/>

<b>Recent Orders</b> 

<table border=0 cellspacing=0>
  <dsp:droplet name="HasFunction">
    <dsp:param bean="Profile.id" name="userId"/>
    <dsp:param name="function" value="approver"/>
    <dsp:oparam name="true">
      <tr valign=top> 
        <td> &nbsp; <dsp:a href="approvals.jsp">Approval requests</dsp:a></td>
        <td> &nbsp; &nbsp; </td>
        <td>
        <dsp:droplet name="ApprovalRequiredDroplet">
          <dsp:param bean="Profile.id" name="approverid"/>
          <dsp:param name="state" value="open"/>
          <dsp:oparam name="output">
            <dsp:valueof param="totalCount"/>
          </dsp:oparam>
          <dsp:oparam name="empty">
            0
          </dsp:oparam>
       </dsp:droplet>
        </td>
      </tr>
    </dsp:oparam>
  </dsp:droplet>
  <tr valign=top> 
    <td> &nbsp; <dsp:a href="orders_open.jsp">Open orders</dsp:a></td>
    <td> &nbsp; &nbsp; </td>
    <td>
      <dsp:droplet name="OrderLookup">
        <dsp:param bean="Profile.id" name="userId"/>
        <dsp:param name="state" value="open"/>
        <dsp:param name="queryTotalOnly" value="true"/>
        <dsp:oparam name="output">
          <dsp:valueof param="total_count"/>
        </dsp:oparam>
        <dsp:oparam name="empty">
          0
        </dsp:oparam>
      </dsp:droplet>
    </td>
  </tr>
  <tr valign=top> 
    <td> &nbsp; <dsp:a href="orders_filled.jsp">Fulfilled orders</dsp:a></td>
    <td> &nbsp; &nbsp; </td>
    <td>
      <dsp:droplet name="OrderLookup">
        <dsp:param bean="Profile.id" name="userId"/>
        <dsp:param name="state" value="no_pending_action"/>
        <dsp:param name="queryTotalOnly" value="true"/>
        <dsp:oparam name="output">
          <dsp:valueof param="total_count"/>
        </dsp:oparam>
        <dsp:oparam name="empty">
          0
        </dsp:oparam>
      </dsp:droplet>
    </td>
  </tr>

  <dsp:droplet name="Switch">
    <dsp:param bean="Profile.approvalRequired" name="value"/>
    <dsp:oparam name="true">
      <dsp:droplet name="HasFunction">
        <dsp:param bean="Profile.id" name="userId"/>
        <dsp:param name="function" value="buyer"/>
        <dsp:oparam name="true">
          <tr valign=top> 
            <td> &nbsp; <dsp:a href="orders_rejected.jsp">Rejected orders</dsp:a></td>
            <td> &nbsp; &nbsp; </td>
            <td>
            <dsp:droplet name="OrderLookup">
              <dsp:param bean="Profile.id" name="userId"/>
              <dsp:param name="state" value="failed_approval"/>
              <dsp:param name="queryTotalOnly" value="true"/>
              <dsp:oparam name="output">
                <dsp:valueof param="total_count"/>
              </dsp:oparam>
              <dsp:oparam name="empty">
                0
              </dsp:oparam>
            </dsp:droplet>
            </td>
          </tr> 
        </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>    
</table>
<p>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/user/ActiveOrders.jsp#2 $$Change: 651448 $--%>
