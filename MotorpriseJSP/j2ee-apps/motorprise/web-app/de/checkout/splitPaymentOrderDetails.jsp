<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/BeanProperty"/>
<dsp:importbean bean="/atg/projects/b2bstore/order/AuthorizedPaymentTypesDroplet"/>
<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupDroplet"/>
<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<%--
Any InvoiceRequest PaymentGroups have been created and added to the PaymentGroupMapContainer.
This uses the AuthorizedPaymentTypesDroplet to determine whether CreditCards need to be
initialized.
--%>

<dsp:setvalue param="order" paramvalue="ShoppingCart.current"/>

<dsp:droplet name="AuthorizedPaymentTypesDroplet">
  <dsp:param bean="Profile" name="profile"/>
  <dsp:oparam name="output">
    <%--
    The PaymentGroupDroplet initializes an OrderPaymentInfo object based on
    the value of the request parameter "init". CreditCard PaymentGroups are also initialized
    if the user is authorized to use them.
    --%>
    <dsp:droplet name="PaymentGroupDroplet">
      <dsp:param name="initOrderPayment" param="init"/>
      <dsp:param name="paymentGroupTypes" value="creditCard"/>
      <dsp:param name="initPaymentGroups" param="potentialPaymentTypes.creditCard"/>
      <dsp:oparam name="output">

        <table border=0 cellpadding=0 cellspacing=0 width=800>
          <tr>
            <td colspan=3>
              <dsp:include page="../common/BrandNav.jsp"></dsp:include>
            </td>
          </tr>
        
          <tr bgcolor="#DBDBDB">
            <%-- put breadcrumbs here --%>
            <td colspan=3 height=18><span class=small>
              &nbsp; <dsp:a href="cart.jsp">Aktuelle Bestellung</dsp:a> &gt;
              <dsp:a href="shipping.jsp">Versand</dsp:a> &gt;
              <dsp:a href="billing.jsp">Rechnung</dsp:a> &gt;
              <dsp:a href="payment_methods.jsp">Zahlungsweisen</dsp:a> &gt; Zahlung teilen &nbsp;</span>
            </td>
          </tr>
          
          <tr>
            <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>
            <td valign="top" width=745>
            <table border=0 cellpadding=4 width=80%>
              <tr><td><dsp:img src="../images/d.gif"/></td></tr>
              <tr>
                <td colspan=2><span class="big">Rechnung</span>
                <dsp:include page="../common/FormError.jsp"></dsp:include></td>
              </tr>
              <tr><td><dsp:img src="../images/d.gif"/></td></tr>
              <tr>
                <td colspan=2><b>Zahlung nach Bestellwert teilen</b><br>
                Gesamtbestellsumme: 
		<dsp:include page="../common/DisplayCurrencyType.jsp">
		  <dsp:param name="currency" param="order.priceInfo.total"/>
		</dsp:include>
                <br>

                <span class=small>Geben Sie den Betrag ein, den Sie einer anderen Zahlungsweise zuordnen m�chten und w�hlen Sie die neue Methode. F�r den verbleibenden Betrag gilt weiterhin die Standard-Zahlungsweise. <P>Sie m�ssen die �nderungen speichern, bevor Sie auf "Weiter" klicken.</span></td>
              </tr>
              <tr valign=top>
                <td>
                <table border=0 cellpadding=4 cellspacing=1>
                  <tr valign=top>
                    <td colspan=9 align=right>
                     <dsp:droplet name="Switch">
                       <dsp:param name="value" param="potentialPaymentTypes.invoiceRequest"/>
                       <dsp:oparam name="true">
                         <span class=smallb><dsp:a href="payment_methods.jsp?link=split_payment_order.jsp">Geben Sie die neue Zahlungsweise ein</dsp:a></span>
                       </dsp:oparam>
                     </dsp:droplet>
                    </td>
                  </tr>
        
                  <tr valign=bottom bgcolor="#666666">
                    <td colspan=2><span class=smallbw>Betrag</span></td>
                    <td colspan=2><span class=smallbw>Zu verschiebender Betr. &nbsp;</span></td>
                    <td colspan=2><span class=smallbw>Zahlungsweise</span></td>
                    <td colspan=3><span class=smallbw>�nderungen speichern</span></td>
        
                  </tr>

                  <%--
                  Get the list of OrderPaymentInfos for the Order
                  --%>
                  <dsp:setvalue bean="PaymentGroupFormHandler.ListId" paramvalue="order.id"/><br>

                  <%--
                  For each OrderPaymentInfo, let the user modify the amount or PaymentGroup.
                  --%>

                  <dsp:droplet name="ForEach">
                    <dsp:param bean="PaymentGroupFormHandler.currentList" name="array"/>
                    <dsp:oparam name="output">
                      <dsp:form formid="order" action="split_payment_order.jsp" method="post">
                      <tr valign=top>
                        <td>
			  <dsp:include page="../common/DisplayCurrencyType.jsp">
			    <dsp:param name="currency" param="element.amount"/>
			  </dsp:include>
                        </td>
                        <td>&nbsp;</td>
                        <td>
                        
                        <dsp:setvalue paramvalue="element.amount" param="amountFormatted"/>
                        <dsp:input euro="true" symbol="&euro;" converter="currency" paramvalue="element.amount" bean="PaymentGroupFormHandler.currentList[param:index].splitAmount" paramvalue="amountFormatted" size="6" type="text"/></td>
                        <td>&nbsp;</td>
                        <td>
                          <dsp:getvalueof id="payMethod" idtype="String" param="element.paymentMethod" >
                          <dsp:select bean="PaymentGroupFormHandler.currentList[param:index].splitPaymentMethod">
                          <dsp:droplet name="ForEach">
                            <dsp:param name="array" param="paymentGroups"/>
                            <dsp:oparam name="output">
                              <dsp:droplet name="Switch">
                                <dsp:param name="value" param="key"/>
                                
                                <dsp:getvalueof id="keyname" idtype="String" param="key">
                                  <%-- <dsp:oparam name="param:...element.paymentMethod"> --%>
                                    <dsp:oparam name="<%=payMethod%>">
                                    <dsp:option selected="<%=true%>" value="<%=keyname%>"/><dsp:valueof param="key"/>
                                  </dsp:oparam>
                                  <dsp:oparam name="default">
                                    <dsp:option selected="<%=false%>" value="<%=keyname%>"/><dsp:valueof param="key"/>
                                  </dsp:oparam>
                                </dsp:getvalueof>
                                
                              </dsp:droplet>
                            </dsp:oparam>
                          </dsp:droplet>
                          </dsp:select>
                          </dsp:getvalueof>
                        </td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                        <td>
                         <dsp:input bean="PaymentGroupFormHandler.ListId" beanvalue="PaymentGroupFormHandler.ListId" priority="<%= (int) 9%>" type="hidden"/>
                         <dsp:input bean="PaymentGroupFormHandler.splitPaymentInfosSuccessURL" type="hidden" value="split_payment_order.jsp?init=false"/>
                         <dsp:input bean="PaymentGroupFormHandler.splitPaymentInfos" type="submit" value=" Speichern "/>
                        </td>
                      </tr>
                      </dsp:form>
                    </dsp:oparam>
                  </dsp:droplet>
        <tr>
          <td colspan=9>
          <%-- table with one row with one cell --%>
          <table border=0 cellpadding=0 cellspacing=0 width=100%>
            <tr bgcolor="#666666">
              <td><dsp:img src="../images/d.gif"/></td>
            </tr>
          </table>
          </td>
        </tr>

              </table>
              </td>
            </tr>
            <tr>
              <td><br>
                <dsp:form formid="apply" action="split_payment_order.jsp" method="post">
                <dsp:input bean="PaymentGroupFormHandler.applyPaymentGroupsSuccessURL" type="hidden" value="IsEmptyCostCenters.jsp?link=split_payment_order.jsp"/>
                <dsp:input bean="PaymentGroupFormHandler.applyPaymentGroups" type="submit" value="Weiter"/>
                </dsp:form>
              </td>
           </tr>
         </table>
         </td>
        </tr>
        </table>
        
        </div>
        
      </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/checkout/splitPaymentOrderDetails.jsp#2 $$Change: 651448 $--%>
