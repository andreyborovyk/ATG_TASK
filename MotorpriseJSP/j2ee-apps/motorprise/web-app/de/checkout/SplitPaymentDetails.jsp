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
    The PaymentGroupDroplet initializes the CommerceIdentifierPaymentInfo objects based on
    the value of the request parameter "init". CreditCard PaymentGroups are also initialized
    if the user is authorized to use them.
    --%>
    <dsp:droplet name="PaymentGroupDroplet">
      <dsp:param name="initItemPayment" param="init"/>
      <dsp:param name="initShippingPayment" param="init"/>
      <dsp:param name="initTaxPayment" param="init"/>
      <dsp:param name="paymentGroupTypes" value="creditCard"/>
      <dsp:param name="initPaymentGroups" param="potentialPaymentTypes.creditCard"/>
      <dsp:oparam name="output">

        <table border=0 cellpadding=0 cellspacing=0 width=800>
          <tr>
            <td colspan=3>
              <dsp:include page="../common/BrandNav.jsp"></dsp:include>
            </td>
        
            <dsp:include page="../common/FormError.jsp"></dsp:include>
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
            <td width=55><dsp:img src="../images/d.gif"/></td>
            <td valign=top width=745>
            <table border=0 cellpadding=4 width=90%>
              <tr><td><dsp:img src="../images/d.gif"/></td>
              <tr>
                <td colspan=2><span class="big">Rechnung</span></td>
              </tr>
             <tr><td><dsp:img src="../images/d.gif"/></td>
       
              <tr valign=top>
                <td>            
                <table border=0 cellpadding=4 cellspacing=1>
                  <tr valign=top>
                    <td colspan=9><b>Zahlung nach Einzelposten teilen</b></td>
                    <td colspan=6 align=right>
                     <dsp:droplet name="Switch">
                       <dsp:param name="value" param="potentialPaymentTypes.invoiceRequest"/>
                       <dsp:oparam name="true">
                         <span class=smallb><dsp:a href="payment_methods.jsp?link=split_payment.jsp">Geben Sie die neue Zahlungsweise ein</dsp:a></span>
                       </dsp:oparam>
                     </dsp:droplet>
                    </td>
                  </tr>
          <tr><td colspan=15><span class=small>Um einen Einzelposten einer anderen Zahlungsweise zuzuordnen, ändern Sie die Zahlungsweise und klicken Sie auf "Speichern". Um einen Teilbetrag einer anderen Zahlungsweise zuzuordnen, geben Sie den entsprechenden Betrag ein und ändern Sie die Zahlungsweise. Sie müssen die Änderungen individuell speichern, bevor Sie auf "Weiter" klicken.</span></td></tr>
                  <tr><td><dsp:img src="../images/d.gif"/></td></tr>
        
                  <tr valign=bottom bgcolor="#666666">
                    <td colspan=2><span class=smallbw>Produktnr.</span></td>
                    <td colspan=2><span class=smallbw>Name</span></td>
                    <td colspan=3><span class=smallbw>Menge</span></td>
                    <td colspan=2><span class=smallbw>Preis</span></td>
                    <td colspan=2><span class=smallbw>Zu verschiebender Betr.</span></td>
                    <td colspan=2><span class=smallbw>Zahlungsweise</span></td>
                    <td colspan=2><span class=smallbw>Änderung speichern</span></td>
                   
                  </tr>
        
                  <%--
                  For each CommerceItem, get its list of CommerceItemPaymentInfos
                  --%>

                  <dsp:droplet name="ForEach">
                    <dsp:param name="array" param="order.commerceItems"/>
                    <dsp:oparam name="output">
                      <dsp:setvalue paramvalue="element" param="commerceItem"/>
                      <dsp:setvalue bean="PaymentGroupFormHandler.ListId" paramvalue="commerceItem.id"/>

                      <%--
                      For each CommerceItemPaymentInfo, let the user modify the amount or PaymentGroup.
                      --%>

                      <dsp:droplet name="ForEach">
                        <dsp:param bean="PaymentGroupFormHandler.currentList" name="array"/>
                        <dsp:oparam name="output">
                          <dsp:form formid="item" action="split_payment.jsp" method="post">
                          <tr valign=top>
                            <td><dsp:valueof param="commerceItem.auxiliaryData.catalogRef.manufacturer_part_number"/></td>
                            <td></td>
                            <td><dsp:a href="../catalog/product.jsp?navAction=jump">
                                   <dsp:param name="id" param="commerceItem.auxiliaryData.productId"/>
                                   <dsp:valueof param="commerceItem.auxiliaryData.catalogRef.displayName"/></dsp:a></td>
                            <td></td>
                         
                            <td>&nbsp;</td>
                            <td align=right><dsp:valueof param="element.quantity"/></td>
                            <td>&nbsp;</td>
                
                            <td align=right><nobr>
                              <dsp:getvalueof id="pval0" param="element.amount"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                            </nobr></td>

                            <td>&nbsp;</td>
                
                            <td>
<%--                            <dsp:setvalue converter="currency" paramvalue="element.amount" param="amountFormatted"/>
                            <dsp:input converter="currency" bean="PaymentGroupFormHandler.currentList[param:index].splitAmount" paramvalue="amountFormatted" size="6" type="text"/></td> --%>
                            <dsp:setvalue paramvalue="element.amount" param="amountFormatted"/>
                            <dsp:input converter="currency" paramvalue="element.amount" bean="PaymentGroupFormHandler.currentList[param:index].splitAmount" paramvalue="amountFormatted" size="6" type="text"/></td>
                            <td>&nbsp;</td>
                
                            <td>
                            
                            <dsp:setvalue paramvalue="element.paymentMethod" param="paymentMethod"/>
                              <dsp:getvalueof id="payMethod1" idtype="String" param="element.paymentMethod">
                              <dsp:select bean="PaymentGroupFormHandler.currentList[param:index].splitPaymentMethod">
                              <dsp:droplet name="ForEach">
                                <dsp:param name="array" param="paymentGroups"/>
                                <dsp:oparam name="output">
                                  <dsp:droplet name="Switch">
                                    <dsp:param name="value" param="key"/>
                                    <dsp:getvalueof id="keyname" idtype="String" param="key">
                                    <%-- <dsp:oparam name="param:...element.paymentMethod"> --%>
                                    <dsp:oparam name="<%=payMethod1%>">
                                    <%-- <dsp:oparam name="param:paymentMethod">  --%>
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
                            <td><dsp:input bean="PaymentGroupFormHandler.splitPaymentInfosSuccessURL" type="hidden" value="split_payment.jsp?init=false"/>
                                <dsp:input bean="PaymentGroupFormHandler.ListId" paramvalue="commerceItem.id" priority="<%= (int)9 %>" type="hidden"/>
                                <dsp:input bean="PaymentGroupFormHandler.splitPaymentInfos" type="submit" value=" Speichern "/>
                             </td>
                          </tr>
                          </dsp:form>
                        </dsp:oparam>
                      </dsp:droplet>
                    </dsp:oparam>
                  </dsp:droplet>
        
                  <%--
                  For each ShippingGroup, get its list of ShippingGroupPaymentInfos
                  --%>

                  <dsp:droplet name="ForEach">
                    <dsp:param name="array" param="order.shippingGroups"/>
                    <dsp:oparam name="output">
                      <dsp:setvalue paramvalue="element" param="shippingGroup"/>
                      <dsp:setvalue bean="PaymentGroupFormHandler.ListId" paramvalue="shippingGroup.id"/>

                      <%--
                      For each ShippingGroupPaymentInfo, let the user modify the amount or PaymentGroup.
                      --%>

                      <dsp:droplet name="ForEach">
                        <dsp:param bean="PaymentGroupFormHandler.currentList" name="array"/>
                        <dsp:oparam name="output">
                          <dsp:form formid="shipping" action="split_payment.jsp" method="post">
                          <tr valign=top>
                            <td colspan=7>Versandkostengruppe <dsp:valueof param="...count"/></td>

                            <td align="right">
                              <dsp:getvalueof id="pval0" param="element.amount"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                            </td>
                            <td></td>
                
                            <td>
                            <dsp:setvalue paramvalue="element.amount" param="amountFormatted"/>
                            <dsp:input bean="PaymentGroupFormHandler.currentList[param:index].splitAmount" paramvalue="amountFormatted" size="8" type="text"/></td>

                            <dsp:setvalue paramvalue="element.paymentMethod" param="paymentMethod"/>
                            <dsp:getvalueof id="payMethod2" idtype="String" param="element.paymentMethod">
                            
                            <td></td>
                            <td>
                              <dsp:select bean="PaymentGroupFormHandler.currentList[param:index].splitPaymentMethod">
                              <dsp:droplet name="ForEach">
                                <dsp:param name="array" param="paymentGroups"/>
                                <dsp:oparam name="output">
                                  <dsp:droplet name="Switch">
                                    <dsp:param name="value" param="key"/>
<%--                                    <dsp:oparam name="param:...element.paymentMethod"> --%>
<%--                                    <dsp:oparam name="param:paymentMethod"> --%>
                                        <dsp:getvalueof id="keyname2" idtype="String" param="key">

                                        <dsp:oparam name="<%=payMethod2%>">
                                          <dsp:option selected="<%=true%>" value="<%=keyname2%>"/><dsp:valueof param="key"/>
                                        </dsp:oparam>
                                        <dsp:oparam name="default">
                                          <dsp:option selected="<%=false%>" value="<%=keyname2%>"/><dsp:valueof param="key"/>
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
                            <td><dsp:input bean="PaymentGroupFormHandler.splitPaymentInfosSuccessURL" type="hidden" value="split_payment.jsp?init=false"/>
                                <dsp:input bean="PaymentGroupFormHandler.ListId" paramvalue="shippingGroup.id" priority="<%=(int) 9%>" type="hidden"/>
                                <dsp:input bean="PaymentGroupFormHandler.splitPaymentInfos" type="submit" value=" Speichern "/>
                             </td>
                          </tr>
                          </dsp:form>
                        </dsp:oparam>
                      </dsp:droplet>
                    </dsp:oparam>
                  </dsp:droplet>
        
                  <%--
                  Get the list of TaxPaymentInfos for the Order
                  --%>

                  <dsp:setvalue bean="PaymentGroupFormHandler.ListId" paramvalue="order.id"/>

                  <%--
                  For each TaxPaymentInfo, let the user modify the amount or PaymentGroup.
                  --%>

                  <dsp:droplet name="ForEach">
                    <dsp:param bean="PaymentGroupFormHandler.currentList" name="array"/>
                    <dsp:oparam name="output">
                      <dsp:form formid="tax" action="split_payment.jsp" method="post">
                      <tr valign=top>
                        <td colspan=7>Steuer</td>
                        <td align="right">
                          <dsp:getvalueof id="pval0" param="element.amount"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                        </td>
                        <td></td>
            
                        <td>
<%--                        <dsp:setvalue converter="currency" paramvalue="element.amount" param="amountFormatted"/>
                        <dsp:input converter="currency" bean="PaymentGroupFormHandler.currentList[param:index].splitAmount" paramvalue="amountFormatted" size="6" type="text"/></td> --%>
                        <dsp:setvalue paramvalue="element.amount" param="amountFormatted"/>
                        <dsp:input converter="currency" paramvalue="element.amount" bean="PaymentGroupFormHandler.currentList[param:index].splitAmount" paramvalue="amountFormatted" size="6" type="text"/></td>
                        <td></td>
                        <td>
                        
                          <dsp:setvalue paramvalue="element.paymentMethod" param="paymentMethod"/>
                          <dsp:getvalueof id="payMethod3" idtype="String" param="element.paymentMethod">
                        
                          <dsp:select bean="PaymentGroupFormHandler.currentList[param:index].splitPaymentMethod">
                          <dsp:droplet name="ForEach">
                            <dsp:param name="array" param="paymentGroups"/>
                            <dsp:oparam name="output">
                              <dsp:droplet name="Switch">
                                <dsp:param name="value" param="key"/>
                                
                                <dsp:getvalueof id="keyname3" idtype="String" param="key">
                                
                                <dsp:oparam name="<%=payMethod3%>">
                                <%-- <dsp:oparam name="param:paymentMethod"> --%>
                                <%-- <dsp:oparam name="param:...element.paymentMethod"> --%>
                                  <dsp:option selected="<%=true%>" value="<%=keyname3%>"/><dsp:valueof param="key"/>
                                </dsp:oparam>
                                <dsp:oparam name="default">
                                  <dsp:option selected="<%=false%>" value="<%=keyname3%>"/><dsp:valueof param="key"/>
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
                        <td><dsp:input bean="PaymentGroupFormHandler.splitPaymentInfosSuccessURL" type="hidden" value="split_payment.jsp?init=false"/>
                            <dsp:input bean="PaymentGroupFormHandler.ListId" paramvalue="order.id" priority="<%=(int) 9%>" type="hidden"/>
                            <dsp:input bean="PaymentGroupFormHandler.splitPaymentInfos" type="submit" value=" Speichern "/>
                         </td>
                      </tr>
                      </dsp:form>
                    </dsp:oparam>
                  </dsp:droplet>

                  <tr>
                    <td colspan=15>
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
              <td>
                <dsp:form formid="apply" action="split_payment.jsp" method="post">
                <dsp:input bean="PaymentGroupFormHandler.applyPaymentGroupsSuccessURL" type="hidden" value="IsEmptyCostCenters.jsp?link=split_payment.jsp"/>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/checkout/SplitPaymentDetails.jsp#2 $$Change: 651448 $--%>
