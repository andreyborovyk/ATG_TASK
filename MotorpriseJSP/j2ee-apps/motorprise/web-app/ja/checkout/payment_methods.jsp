<%@ taglib uri="dsp" prefix="dsp" %>
<%@ taglib uri="core" prefix="core" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/projects/b2bstore/order/AuthorizedPaymentTypesDroplet"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CreateInvoiceRequestFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupDroplet"/>
<dsp:importbean bean="/atg/commerce/order/purchase/PaymentGroupFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/purchase/PaymentAddressFormHandler"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/commerce/gifts/IsGiftShippingGroup"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>



<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" チェックアウト"/></dsp:include>

<dsp:getvalueof id="linkname"   idtype="java.lang.String" param="link">

<%--
If specifically requested, we'll clear the PaymentGroups. This is useful if a previous page
initialized an InvoiceRequest that wasn't used, so we can remove it from the PaymentGroupMapContainer.
--%>
<dsp:droplet name="Switch">
  <dsp:param name="value" param="init"/>
  <dsp:oparam name="unset"></dsp:oparam>
  <dsp:oparam name="default">
    <dsp:droplet name="PaymentGroupDroplet">
      <dsp:param name="clearPaymentGroups" param="init"/>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>

<%--
Do this to expose the PaymentGroups in the PaymentGroupMapContainer.
--%>
<dsp:droplet name="PaymentGroupDroplet">
  <dsp:oparam name="output">

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=3>
      <dsp:include page="../common/BrandNav.jsp"></dsp:include>
    </td>
  </tr>

  <tr bgcolor="#DBDBDB">
    <%/* put breadcrumbs here */%>
    <td colspan=3 height=18><span class=small>
       &nbsp; <dsp:a href="cart.jsp">現在のオーダー</dsp:a> &gt; 
       <dsp:a href="shipping.jsp">配達</dsp:a> &gt; 
       <dsp:a href="billing.jsp">請求</dsp:a> &gt; 支払い方法 &nbsp;</span>
    </td>
  </tr>
  
  <tr>
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>
    <td valign="top" width=745>
    <dsp:form action="payment_methods.jsp" method="POST">
    <%--
    Depending on where we came from, the request parameter link represents where we want to go after
    we select a default PaymentGroup.
    --%>
    
    
    
    <core:CreateUrl id="successURL" url="payment_methods.jsp">
      <core:UrlParam param="link" value="<%=linkname%>"/>
        <dsp:setvalue param="successStr"  value="<%= successURL.getNewUrl() %>" />
    </core:CreateUrl>



    <%-- <dsp:input bean="CreateInvoiceRequestFormHandler.NewInvoiceRequestSuccessURL" type="hidden" value="payment_methods.jsp?link=`request.getParameter(" link")`"/>
    <dsp:input bean="CreateInvoiceRequestFormHandler.NewInvoiceRequestErrorURL" type="hidden" value="payment_methods.jsp?link=`request.getParameter(" link")`"/> --%> 
    <dsp:input bean="PaymentGroupFormHandler.SpecifyDefaultPaymentGroupSuccessURL" paramvalue="link" type="hidden"/>
    <%-- <dsp:input bean="PaymentGroupFormHandler.SpecifyDefaultPaymentGroupErrorURL" type="hidden" value="payment_methods.jsp?link=`request.getParameter(" link")`"/> --%>
    <dsp:input bean="CreateInvoiceRequestFormHandler.NewInvoiceRequestSuccessURL" type="hidden" paramvalue="successStr"/>
    <dsp:input bean="CreateInvoiceRequestFormHandler.NewInvoiceRequestErrorURL" type="hidden" paramvalue="successStr"/> 
    <dsp:input bean="PaymentGroupFormHandler.SpecifyDefaultPaymentGroupErrorURL" type="hidden" paramvalue="successStr"/>


    <table border=0 cellpadding=4 width=80%>
      <tr><td><dsp:img src="../images/d.gif"/></td></tr>
      <tr>
        <td colspan=2><span class="big">支払い方法</span>
        <dsp:include page="../common/FormError.jsp"></dsp:include></td>
      </tr>

      <tr>
        <td colspan=2>
        <%--
        Determine which PaymentGroup types are authorized for the current user.
        --%>
        <dsp:droplet name="AuthorizedPaymentTypesDroplet">
          <dsp:param bean="Profile" name="profile"/>
          <dsp:oparam name="output">
            <dsp:droplet name="Switch">
              <dsp:param name="value" param="potentialPaymentTypes.creditCard"/>
              <dsp:oparam name="true">
                <dsp:droplet name="Switch">
                  <dsp:param name="value" param="potentialPaymentTypes.invoiceRequest"/>
                  <dsp:oparam name="true">
                    <span class=small>このオーダーに使用する発注番号または請求番号を入力してください。 </span>
                  </dsp:oparam>
                  <dsp:oparam name="false">
                     <span class=small>認証済みクレジットカードは次のとおりです。 
                    </span>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
              <dsp:oparam name="false">
                <dsp:droplet name="Switch">
                  <dsp:param name="value" param="potentialPaymentTypes.invoiceRequest"/>
                  <dsp:oparam name="true">
                    <span class=small>このオーダーに使用する発注番号または請求番号を入力してください。 </span>
                  </dsp:oparam>
                  
                </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>

      <%--
      Display the billing address associated with each PaymentGroup, and for
      InvoiceRequests permit the user to change that address.
      --%>
      <dsp:droplet name="ForEach">
        <dsp:param name="array" param="paymentGroups"/>
        <dsp:oparam name="output">
          <tr><td><dsp:img src="../images/d.gif"/></td></tr>
     
          <tr valign=top>
            <td align=right width=25%>
            <span class=smallb>支払い方法 <dsp:valueof param="count"/></span></td>
            <td><dsp:valueof param="key"/></td>
          </tr>
          <tr valign=top>
            <td align=right><span class=smallb>請求先住所</span></td>
            <td><dsp:droplet name="IsNull">
                  <dsp:param name="value" param="element.billingAddress"/>
                  <dsp:oparam name="true">
                    <dsp:getvalueof id="pval0" bean="Profile.defaultBillingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof><br>
                  </dsp:oparam>
                  <dsp:oparam name="false">
                    <dsp:getvalueof id="pval0" param="element.billingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof><br>
                  </dsp:oparam>
                </dsp:droplet>
                <dsp:droplet name="Switch">
                  <dsp:param name="value" param="element.paymentGroupClassType"/>
                  <dsp:oparam name="creditCard"></dsp:oparam>
                  <dsp:oparam name="invoiceRequest">
                    <dsp:droplet name="IsEmpty">
                      <dsp:param bean="Profile.billingAddrs" name="value"/>
                      <dsp:oparam name="false">
<%--                        <span class=smallb><dsp:a bean="PaymentAddressFormHandler.paymentGroupKey" href="billing_address.jsp" paramvalue="key"><dsp:param name="source" value="payment_methods.jsp?link=`request.getParameter(" link")`"/>Change billing address</dsp:a></span> --%>
                        <span class=smallb>
                          <dsp:a bean="PaymentAddressFormHandler.paymentGroupKey" href="billing_address.jsp" paramvalue="key">
                            <dsp:param name="source" param="successStr"/>請求先住所の変更</dsp:a>
                        </span>
                      </dsp:oparam>
                    </dsp:droplet>
                  </dsp:oparam>
                </dsp:droplet>
                </td>
            </tr>
        </dsp:oparam>
      </dsp:droplet>
 
      <tr><td><dsp"img src="../images/d.gif"/></td></tr>

      <%--
      If the user is authorized to use InvoiceRequests, they can enter as many 
      requisitionNumber or PONumbers as they want here.
      --%>
      <dsp:droplet name="AuthorizedPaymentTypesDroplet">
        <dsp:param bean="Profile" name="profile"/>
        <dsp:oparam name="output">
          <dsp:droplet name="Switch">
            <dsp:param name="value" param="potentialPaymentTypes.invoiceRequest"/>
            <dsp:oparam name="true">
              <tr valign=top>
                <td align=right><span class=smallb>新しい発注番号</span></td>
                <td><dsp:input bean="CreateInvoiceRequestFormHandler.invoiceRequest.PONumber" type="text" value="" maxlength="40"/>
                    <dsp:input bean="CreateInvoiceRequestFormHandler.newInvoiceRequest" type="submit" value=" 追加 "/></td>
              </tr>
              <tr valign=top>
                <td align=right><span class=smallb>新しい請求番号</span></td>
                <td><dsp:input bean="CreateInvoiceRequestFormHandler.invoiceRequest.requisitionNumber" type="text" value="" maxlength="40"/>
                    <dsp:input bean="CreateInvoiceRequestFormHandler.newInvoiceRequest" type="submit" value=" 追加 "/></td>
              </tr>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>


      <%--
      Permits the user to select a default PaymentGroup.
      --%>
      <dsp:droplet name="ForEach">
        <dsp:param name="array" param="paymentGroups"/>
        <dsp:oparam name="outputStart">
          <dsp:droplet name="Switch">
            <dsp:param name="value" param="size"/>
            <dsp:oparam name="0">
            </dsp:oparam>
            <dsp:oparam name="1">
              <tr valign=top>
                <td align=right><span class="smallb">デフォルトの支払い方法</span></td>
                <td><span class=help>オーダーは、基本的にはデフォルトの支払い方法で支払うように設定されています。オーダーを分割して支払うには、分割する分の金額をこの支払い方法から、選択した別の支払い方法に移動させます。別の支払い方法に移動しなかった金額は、この支払い方法で支払われます。
            </span><p>
            </dsp:oparam>
            <dsp:oparam name="default">
              <tr valign=top>
                <td align=right><span class="smallb">デフォルトの支払い方法</span></td>
                <td><span class=help>オーダーは、基本的にはデフォルトの支払い方法で支払うように設定されています。オーダーを分割して支払うには、分割する分の金額をこの支払い方法から、選択した別の支払い方法に移動させます。別の支払い方法に移動しなかった金額は、この支払い方法で支払われます。
            </span><p>
                <dsp:select bean="PaymentGroupFormHandler.defaultPaymentGroupName">
                  <dsp:droplet name="ForEach">
                    <dsp:param name="array" param="paymentGroups"/>
                    <dsp:oparam name="output">
                      <dsp:getvalueof id="keyname" idtype="String" param="key">
                        <dsp:option value="<%=keyname%>"/><dsp:valueof param="key"/>
                      </dsp:getvalueof>
                    </dsp:oparam>
                  </dsp:droplet>
                </dsp:select>
                </td>
              </tr>
              <tr valign=top>
                <td></td>
                <td><br><dsp:input bean="PaymentGroupFormHandler.specifyDefaultPaymentGroup" type="submit" value="続行"/></td>
              </tr>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
        <dsp:oparam name="outputEnd">
          <dsp:droplet name="Switch">
            <dsp:param name="value" param="size"/>
            <dsp:oparam name="0">
            </dsp:oparam>
            <dsp:oparam name="1">
                </td>
              </tr>
              <tr valign=top>
                <td></td>
                <td><br><dsp:input bean="PaymentGroupFormHandler.specifyDefaultPaymentGroup" type="submit" value="続行"/></td>
              </tr>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
        <dsp:oparam name="output">
          <dsp:droplet name="Switch">
            <dsp:param name="value" param="size"/>
            <dsp:oparam name="1">
              <dsp:input bean="PaymentGroupFormHandler.defaultPaymentGroupName" paramvalue="key" type="hidden"/>
              <dsp:valueof param="key"/>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>

    </table>
    </dsp:form>
    </td>
  </tr>
</table>

  </dsp:oparam>
</dsp:droplet>

</dsp:getvalueof>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/checkout/payment_methods.jsp#2 $$Change: 651448 $--%>
