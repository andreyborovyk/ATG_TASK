<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
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
               &nbsp; <dsp:a href="cart.jsp">現在のオーダー</dsp:a> &gt; 
               <dsp:a href="shipping.jsp">配達</dsp:a> &gt; 
               <dsp:a href="billing.jsp">請求</dsp:a> &gt; 
               <dsp:a href="payment_methods.jsp">支払い方法</dsp:a> &gt; 支払いの分割 &nbsp;</span>
            </td>
          </tr>
          
          <tr>
            <td width=55><dsp:img src="../images/d.gif"/></td>
            <td valign=top width=745>
            <table border=0 cellpadding=4 width=90%>
              <tr><td><dsp:img src="../images/d.gif"/></td>
              <tr>
                <td colspan=2><span class="big">請求</span></td>
              </tr>
             <tr><td><dsp:img src="../images/d.gif"/></td>
       
              <tr valign=top>
                <td>            
                <table border=0 cellpadding=4 cellspacing=1>
                  <tr valign=top>
                    <td colspan=9><b>品目で支払いを分割</b></td>
                    <td colspan=6 align=right>
                     <dsp:droplet name="Switch">
                       <dsp:param name="value" param="potentialPaymentTypes.invoiceRequest"/>
                       <dsp:oparam name="true">
                         <span class=smallb><dsp:a href="payment_methods.jsp?link=split_payment.jsp">新しい支払い方法を入力</dsp:a></span>
                       </dsp:oparam>
                     </dsp:droplet>
                    </td>
                  </tr>
          <tr><td colspan=15><span class=small>品目を別の支払い方法に移動するには、支払方法を変更して [保存] をクリックします。金額の一部を別の支払い方法に移動するには、移動する金額を入力して支払方法を変更します。続行する前に、変更をそれぞれ保存する必要があります。</span></td></tr>
                  <tr><td><dsp:img src="../images/d.gif"/></td></tr>
        
                  <tr valign=bottom bgcolor="#666666">
                    <td colspan=2><span class=smallbw>部品番号</span></td>
                    <td colspan=2><span class=smallbw>名前</span></td>
                    <td colspan=3><span class=smallbw>数量</span></td>
                    <td colspan=2><span class=smallbw>価格</span></td>
                    <td colspan=2><span class=smallbw>移動する金額</span></td>
                    <td colspan=2><span class=smallbw>支払い方法</span></td>
                    <td colspan=2><span class=smallbw>変更内容の保存</span></td>
                   
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
                            
                            <dsp:getvalueof id="useLocale" bean="Profile.priceList.locale">
                              <dsp:input converter="currency" locale="<%=useLocale%>" bean="PaymentGroupFormHandler.currentList[param:index].splitAmount" paramvalue="amountFormatted" size="6" type="text"/>
                            </dsp:getvalueof>
                            </td>
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
                            <td>
                                <dsp:input bean="PaymentGroupFormHandler.splitPaymentInfosSuccessURL" type="hidden" value="split_payment.jsp?init=false"/>
                                <dsp:input bean="PaymentGroupFormHandler.ListId" paramvalue="commerceItem.id" priority="<%= (int)9 %>" type="hidden"/>
                                <dsp:input bean="PaymentGroupFormHandler.splitPaymentInfos" type="submit" value=" 保存 "/>
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
                            <td colspan=7>送料グループ <dsp:valueof param="...count"/></td>

                            <td align="right">
                              <dsp:getvalueof id="pval0" param="element.amount"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                            </td>
                            <td></td>
                
                            <td>
                            <dsp:setvalue paramvalue="element.amount" param="amountFormatted"/>
                            <dsp:getvalueof id="useLocale2" bean="Profile.priceList.locale">
                              <dsp:input converter="currency" locale="<%=useLocale2%>" 
                                bean="PaymentGroupFormHandler.currentList[param:index].splitAmount" paramvalue="amountFormatted" size="6" type="text"/>
                            </dsp:getvalueof>
                            </td>
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
                                <dsp:input bean="PaymentGroupFormHandler.splitPaymentInfos" type="submit" value=" 保存 "/>
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
                        <td colspan=7>税</td>
                        <td align="right">
                          <dsp:getvalueof id="pval0" param="element.amount"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                        </td>
                        <td></td>
            
                        <td>
<%--                        <dsp:setvalue converter="currency" paramvalue="element.amount" param="amountFormatted"/>
                        <dsp:input converter="currency" bean="PaymentGroupFormHandler.currentList[param:index].splitAmount" paramvalue="amountFormatted" size="6" type="text"/></td> --%> 
                        <dsp:setvalue paramvalue="element.amount" param="amountFormatted"/>
                        <dsp:getvalueof id="useLocale3" bean="Profile.priceList.locale">
                            <dsp:input converter="currency" locale="<%=useLocale3%>" 
                                bean="PaymentGroupFormHandler.currentList[param:index].splitAmount" paramvalue="amountFormatted" size="6" type="text"/>
                        </dsp:getvalueof>
                        </td>
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
                            <dsp:input bean="PaymentGroupFormHandler.splitPaymentInfos" type="submit" value=" 保存 "/>
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
                <dsp:input bean="PaymentGroupFormHandler.applyPaymentGroups" type="submit" value="続行"/>
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
<%/* Version: $Change: 651448 $$DateTime: 2011/06/07 13:55:45 $*/%>


</dsp:page>
<%-- Version: $Change: 651448 $$DateTime: 2011/06/07 13:55:45 $--%>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/checkout/SplitPaymentDetails.jsp#2 $$Change: 651448 $--%>
