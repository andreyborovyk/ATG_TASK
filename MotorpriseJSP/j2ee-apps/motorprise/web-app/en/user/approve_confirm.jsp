<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/commerce/approval/ApprovalFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/profile/PersonLookup"/>

<%/*
Display the confirmation screen after the approver approves the message displaying order &
  buyer details.
*/%>


<DECLAREPARAM NAME="orderId" CLASS="java.lang.String" DESCRIPTION="The id of the order to reject">

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Approve"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="my_account.jsp">My Account</dsp:a> &gt;
    <dsp:a href="approvals.jsp">Approvals</dsp:a>
    </span></td>
  
    <tr valign=top> 
      <td width=55>  
      </td>
    
    <!-- main content area -->
    <td  valign="top" width=745>  
    <table border=0 cellpadding=4 width=80%>
      <tr>
        <td colspan="2"><img src="../images/d.gif" vspace=0></td>
      </tr>
      <tr valign=top>
        <td colspan=2><span class=big>My Account</span></td>
      </tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Approval Confirmation</td></tr></table>
        </td>
      </tr>
      <tr>
        <td><dsp:include page="ApprovalFormHandlerErrorMessages.jsp"></dsp:include></td>
      </tr>
      <tr>
        <td colspan=2>The order has been submitted.</td>
      </tr>

      <%/* Get the order that has been approved and display order/user info.*/%>
      <dsp:droplet name="OrderLookup">
        <dsp:param bean="ApprovalFormHandler.orderId" name="orderId"/>
        <dsp:oparam name="output">
          <tr>
            <td><img src="../images/d.gif" hspace=10></td>      
            <td>
              <table cellpadding=3 border=0>
              <tr>
                <td align=right><span class=smallb><nobr>Order number </nobr></span></td>
                <td><dsp:valueof bean="ApprovalFormHandler.orderId">No order Id</dsp:valueof></td>
              </tr>
              <tr>
                <td align=right><span class=smallb>Order placed </span></td>
                <td><dsp:valueof date="M/dd/yyyy K:mm a" param="result.submittedDate">No Submit Date</dsp:valueof></td>
              </tr>
              <dsp:droplet name="PersonLookup">
                <dsp:param name="id" param="result.profileId"/>
                <dsp:param name="elementName" value="Person"/>
                <dsp:oparam name="output">
                  <tr>
                    <td align=right><span class=smallb>Buyer </span></td>
                    <td><dsp:valueof param="Person.firstName">No First Name</dsp:valueof>
                    <dsp:valueof param="Person.lastName">No Last Name</dsp:valueof>, <dsp:valueof param="Person.parentOrganization.name"/></td>
                  </tr>
                </dsp:oparam>
              </dsp:droplet>
              <tr>
                <td align=right valign=top><span class=smallb>Message </span></td>
                <td><dsp:valueof bean="ApprovalFormHandler.approverMessage">no message sent.</dsp:valueof></td>
              </tr>
            </table>
            </td>
            </tr>
          </dsp:oparam>
        </dsp:droplet>
        <tr>
          <td><span class=smallb><dsp:a href="approvals.jsp">Back to approvals</dsp:a></span></td>
        </tr>
      </table>

    </td>
  </tr>
</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/user/approve_confirm.jsp#2 $$Change: 651448 $--%>
