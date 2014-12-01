<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/userdirectory/droplet/HasFunction"/>
<dsp:importbean bean="/atg/commerce/approval/ApprovalRequiredDroplet"/>
<dsp:importbean bean="/atg/commerce/approval/ApprovedDroplet"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" My Account"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
      My Account</span></td>
  </tr>

  <tr valign=top> 
    <td width=55><img src="../images/d.gif" hspace=27></td>

    <!-- main content area -->
    <td valign="top" width=745>  
    <table border=0 cellpadding=4 width=80%>
      <tr><td><img src="../images/d.gif"></td></tr>
      <tr>
        <td colspan=3><span class=big>My Account</span></td>
      </tr>
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <tr>
        <td>
        <table border=0 cellpadding=4 cellspacing=0>
          <tr valign=top>
            <td colspan=3>
            <table width=100% cellpadding=3 cellspacing=0 border=0>
              <tr><td class=box-top>&nbsp;Order Information</td></tr></table>
            </td>
          </tr>  
  
          <!--  display link if user has approver role -->
          <dsp:droplet name="HasFunction">
            <dsp:param bean="Profile.id" name="userId"/>
            <dsp:param name="function" value="approver"/>
            <dsp:oparam name="true">
              <tr valign=top>
                <td><dsp:a href="approvals.jsp">Approval requests</dsp:a></td>
                <td>
                <dsp:droplet name="ApprovalRequiredDroplet">
                  <dsp:param bean="Profile.id" name="approverid"/>
                  <dsp:param name="state" value="open"/>
                  <dsp:oparam name="output">
                    <nobr><dsp:valueof param="totalCount"/> &nbsp;</nobr>
                  </dsp:oparam>
                  <dsp:oparam name="empty">
                    0
                  </dsp:oparam>
                </dsp:droplet>
                 </td>
                <td>Orders requiring your approval.</td>
              </tr>
              <tr valign=top>
                <td><dsp:a href="approvals_past.jsp">Resolved approval requests</dsp:a></td>
                <td>
                <dsp:droplet name="ApprovedDroplet">
                  <dsp:param bean="Profile.id" name="approverid"/>
                  <dsp:param name="state" value="open"/>
                  <dsp:oparam name="output">
                    <nobr><dsp:valueof param="totalCount"/> &nbsp;</nobr>
                  </dsp:oparam>
                  <dsp:oparam name="empty">
                    0
                  </dsp:oparam>
                </dsp:droplet>
                 </td>
                <td>Orders you have approved or rejected.</td>
              </tr>
            </dsp:oparam>
          </dsp:droplet>
            
          <tr valign=top>
            <td><dsp:a href="orders_open.jsp">Open orders</dsp:a></td>
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
            <td>Orders that are in process.</td>
          </tr>
          <tr valign=top>
            <td><dsp:a href="orders_filled.jsp"><nobr>Fulfilled orders</dsp:a></nobr></td>
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
            <td>Orders that have been shipped.</td>
          </tr>
         
          <!-- only buyers who require approval see rejected orders -->
          <dsp:droplet name="HasFunction">
            <dsp:param bean="Profile.id" name="userId"/>
            <dsp:param name="function" value="buyer"/>
            <dsp:oparam name="true">
              <dsp:droplet name="Switch">
                <dsp:param bean="Profile.approvalRequired" name="value"/>
                <dsp:oparam name="true">
                  <tr valign=top>
                    <td><dsp:a href="orders_rejected.jsp"><nobr>Rejected orders</nobr></dsp:a></td>
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
                    <td>Orders requiring approval that were returned to you by the approver.</td>
                  </tr>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>

          <tr><td><img src="../images/d.gif"></td></tr>
          <tr valign=top>
            <td colspan=3>
            <table width=100% cellpadding=3 cellspacing=0 border=0>
              <tr><td class=box-top>&nbsp;Purchase Tools</td></tr></table>
            </td>
          </tr>
          <tr valign=top>
            <td><dsp:a href="purchase_lists.jsp"><nobr>Purchase lists</nobr></dsp:a></td>
            <td></td>
            <td>List of frequently ordered items. Items can be added to list
                from the catalog.</td>
          </tr>
          <tr valign=top>
            <td><dsp:a href="scheduled_orders.jsp"><nobr>Scheduled orders</nobr></dsp:a></td>
            <td></td>
            <td>Orders that are submitted automatically based on a pre-defined
               schedule.</td>
          </tr>
          <tr valign=top>
            <td><dsp:a href="saved_orders.jsp">Saved orders</dsp:a></td>
            <td></td>
            <td>If you are building an order and are not yet ready to place it you
             can save the order and place it at a later time.</td>
          </tr>
          <tr><td><img src="../images/d.gif"></td></tr>
          <tr valign=top>
            <td colspan=3>
            <table width=100% cellpadding=3 cellspacing=0 border=0>
              <tr><td class=box-top>&nbsp;Profile Information</td></tr></table>
            </td>
          </tr>
          <tr valign=top>
            <td><dsp:a href="my_profile.jsp">My profile</dsp:a></td>
            <td></td>
            <td>Edit your contact information and defaults.</td>
          </tr>
        </table>
        </td>
      </tr>
    </table>
    </td>
  </tr>
</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/user/my_account.jsp#2 $$Change: 651448 $--%>
