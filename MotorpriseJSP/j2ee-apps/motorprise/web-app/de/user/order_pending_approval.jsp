<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/servlet/ProfileRepositoryItemServlet"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Bewilligungen"/></dsp:include>
<table border=0 cellpadding=0 cellspacing=0>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="my_account.jsp">Mein Konto</dsp:a> &gt;
    <dsp:a href="approvals.jsp">Bewilligungspflichtige Bestellungen</dsp:a> &gt;
    Bestellung</span></td>
  </tr>
  <tr>
    <td width=55><img src="../images/d.gif" hspace=27></td>
  <td valign=top width=745>
    <table border=0 cellpadding=4 width=80%>
      <tr>
        <td colspan="2"><img src="../images/d.gif" vspace=0></td>
      </tr>
      <tr valign=top>
        <td colspan=2><span class=big>Mein Konto</span></td>
      </tr>
      <tr><td colspan=2><img src="../images/d.gif"></td></tr>
      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Bewilligungspflichtige Bestellung</td></tr></table>
        </td>

    <dsp:droplet name="OrderLookup">
      <dsp:param name="orderId" param="orderId"/>
      <dsp:oparam name="output">
              <dsp:setvalue paramvalue="result" param="order"/>
              <tr>
                <td colspan=2><span class=categoryhead>Bestellnr.
                <dsp:valueof param="orderId"/></span></td>
              </tr>
              <tr>                              
                <!-- general order info -->
                <td colspan=2>Bestellung aufgegeben <dsp:valueof date="MMMMM d, yyyy" param="order.submittedDate"/></td>
              </tr>

              <tr>
                <td colspan=2>
                <dsp:droplet name="/atg/dynamo/droplet/Switch">
                  <dsp:param name="value" param="order.stateAsString"/>
                  <dsp:oparam name="NO_PENDING_ACTION">
                    Bestellstatus: VERSANDT
                  </dsp:oparam>
                  <dsp:oparam name="default">
                    Bestellstatus: <dsp:valueof param="order.stateAsString"/>
                  </dsp:oparam>
                </dsp:droplet>
                </td>
              </tr>
              <tr>
                <td colspan=2>
                    <dsp:droplet name="ProfileRepositoryItemServlet">
                      <dsp:param name="id" param="order.profileId"/>
                      <dsp:oparam name="output">
                        <table border=0><tr valign=top>
                          <td>Käufer: </td>
                          <td> <dsp:valueof param="item.firstName"/> 
                               <dsp:valueof param="item.lastName"/><br>
                               <dsp:valueof param="item.email">Keine E-Mail-Adresse</dsp:valueof><br>
                               <dsp:valueof param="item.parentOrganization.name"/></td>
                          </tr>
                        </table>
                      </dsp:oparam>
                    </dsp:droplet>
                  </td>
                </tr>
                
              <tr>
                <td>
                    <dsp:getvalueof id="pval0"  param="order"><dsp:include page="../checkout/displayPaymentInfo.jsp"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                  <dsp:getvalueof id="pval0" param="order">	<dsp:include page="../checkout/displayCostCenterInfo.jsp"><dsp:param name="order" value="<%=pval0%>"/></dsp:include>
 </dsp:getvalueof> 

<dsp:getvalueof id="pval0" param="order">
<dsp:include page="../checkout/displayShippingInfo.jsp">
    <dsp:param name="order" value="<%=pval0%>"/>
</dsp:include>
</dsp:getvalueof> 
 


                </td>
              </tr>
              <tr>
                <td></td>
                <td colspan=2><span class=smallb><dsp:a href="../user/IsOrderWithRequisition.jsp">Bestellung bewilligen
                      <dsp:param name="orderId" param="order.id"/></dsp:a></span>
                 | 

                <span class=smallb><dsp:a href="reject_order.jsp">Bestellung nicht bewilligen
                      <dsp:param name="orderId" param="order.id"/></dsp:a></span>

                </td>
                <td></td>
              </tr>
              <tr>
                <td colspan="3"></td>
              </tr>
            </table>
        </dsp:oparam>
      </dsp:droplet>
    </td>
  </tr>
</table>
</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/user/order_pending_approval.jsp#2 $$Change: 651448 $--%>
