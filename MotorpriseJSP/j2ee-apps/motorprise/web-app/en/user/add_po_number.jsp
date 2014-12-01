<%@ taglib uri="dsp" prefix="dsp" %>
<%@ taglib uri="core" prefix="core" %>
<dsp:page>

<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/userprofiling/servlet/ProfileRepositoryItemServlet"/>
<dsp:importbean bean="/atg/projects/b2bstore/order/B2BOrderRepositoryFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<%--
  Display all payment groups with requisition numbers so the order
  approver can enter the purchase order numbers for each payment group
  and then update the order.
--%>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Approvals"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>

  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="my_account.jsp">My Account</dsp:a> &gt;
    <dsp:a href="approvals.jsp">Orders Pending Approval</dsp:a> &gt;
    Order</span></td>
  </tr>

  <tr>
    <td width=55><dsp:img hspace="27" src="../images/d.gif"/></td>
    <td valign=top width=745>
    <table border=0 cellpadding=4 width=80%>
      <tr>
        <td colspan="2"><dsp:img vspace="0" src="../images/d.gif"/></td>
      </tr>
      <tr valign=top>
        <td colspan=2><span class=big>My Account</span></td>
      </tr>
      <tr><td colspan=2><dsp:img src="../images/d.gif"/></td></tr>
      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Add PO Numbers</td></tr></table>
        </td>
        <%/*Get the order using OrderLookup droplet */%>
        <dsp:droplet name="OrderLookup">
          <dsp:param name="orderId" param="orderId"/>
          <dsp:oparam name="output">
            <dsp:setvalue paramvalue="result" param="order"/>
            <tr> <td colspan=2><span class=categoryhead>Order # <dsp:valueof param="orderId"/></span></td>
            </tr>
            <tr> <td>
              <%/* Iterate through payment groups to find one with requistion no. */%>
              <dsp:droplet name="ForEach">
              <dsp:param name="array" param="order.paymentGroups"/>
              <dsp:param name="elementName" value="pGroup"/>
                <dsp:oparam name="output">
                <dsp:droplet name="Switch">
                  <dsp:param name="value" param="pGroup.requisitionNumber"/>
                  <dsp:oparam name="unset"></dsp:oparam>
                  <dsp:oparam name=""></dsp:oparam>
                  <dsp:oparam name="default">
                    <dsp:form formid="paymentGroup" action="add_po_number.jsp">
                      <dsp:input bean="B2BOrderRepositoryFormHandler.itemDescriptorName" type="hidden" value="invoiceRequest"/>
                      <dsp:input bean="B2BOrderRepositoryFormHandler.repositoryId" paramvalue="pGroup.id" type="hidden"/>
                      <tr><td><dsp:img vspace="0" src="../images/d.gif"/></td></tr>
                      <tr valign=top>
                      <td align=right><span class=smallb>Payment method
                      <dsp:valueof param="count"/></span></td>
                      <td width=75%>Requisition Number&nbsp;<dsp:valueof param="pGroup.requisitionNumber"/>&nbsp;</td>
                      </tr>
                      <tr valign=top>
                        <td align=right><span class=smallb>Amount</span></td>
                        <td width=75%><dsp:valueof converter="currency" locale="Profile.priceList.locale" param="pGroup.amount"/></td>
                      </tr>
                      <tr valign=top>
                      <td align=right><span class=smallb>Enter PO #</span></td>
                      <td width=75%>
		        <dsp:input bean="B2BOrderRepositoryFormHandler.value.PONumber" paramvalue="pGroup.PONumber" type="text"/>
			<dsp:getvalueof id="order_id" idtype="String" param="orderId">
			<core:createUrl id="this_page" url="add_po_number.jsp">
			  <core:urlParam param="orderId" value="<%=order_id%>"/>
			  <dsp:input type="hidden" bean="B2BOrderRepositoryFormHandler.updateSuccessURL" value="<%=this_page.getNewUrl()%>"/>
			  <dsp:input type="hidden" bean="B2BOrderRepositoryFormHandler.updateErrorURL"   value="<%=this_page.getNewUrl()%>"/>
			</core:createUrl>
			</dsp:getvalueof>
                        <dsp:input bean="B2BOrderRepositoryFormHandler.update" type="submit" value="Save"/>
                      </td>
                      </tr>
                    </dsp:form>
                  </dsp:oparam> <%/* End oparam 'default' of Switch */ %>

                </dsp:droplet><%/* End switch droplet */%>
              </dsp:oparam><%/* End ouput oparam of ForEach */%>
           </dsp:droplet><%/* End ForEach droplet*/%>
          </td>
        </tr>

        <tr>
          <td></td>
          <dsp:form formid="apply" action="../user/approve_order.jsp">
            <input name="orderId" type="hidden" value='<dsp:valueof param="orderId"/>'>
          <td><br><input type="submit" value="Continue"></td>
          </dsp:form>
       </tr>
     </table>
         
      </dsp:oparam> <%/* End output of OrderLookup */%>
      </dsp:droplet><%/* End OrderLookup */%>
    </td>
  </tr>
</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/user/add_po_number.jsp#2 $$Change: 651448 $--%>
