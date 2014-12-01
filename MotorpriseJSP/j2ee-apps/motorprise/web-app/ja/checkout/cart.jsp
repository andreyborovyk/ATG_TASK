<%@ taglib uri="dsp" prefix="dsp" %> 
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>

<DECLAREPARAM NAME="noCrumbs" 
              CLASS="java.lang.String" 
              DESCRIPTION="This is for deciding what kind of breadcrumbs to display.
              If this is true, then breadcrumbs will show: Store Home|Checkout,
              instead of nav history. Default is true."
              OPTIONAL>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" チェックアウト"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=3>
      <dsp:include page="../common/BrandNav.jsp"></dsp:include>
    </td>
  </tr>

  <tr bgcolor="#DBDBDB">    
    <td colspan=3 height=18><span class=small>
      <dsp:droplet name="Switch">
        <dsp:param name="value" param="noCrumbs"/>
        <dsp:oparam name="false">          
          <dsp:include page="../common/breadcrumbs.jsp"><dsp:param name="displaybreadcrumbs" value="true"/><dsp:param name="no_new_crumb" value="true"/></dsp:include> &gt; チェックアウト
        </dsp:oparam>
        <dsp:oparam name="default">
          <dsp:param name="noCrumbs" value="true"/>
          &nbsp; 現在のオーダー
        </dsp:oparam>        
      </dsp:droplet>  
    </td>
  </tr>


  <tr valign=top>
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>

    <td valign="top" width=545>
      <%-- put errors here --%>
      <P>
      <%-- this table just for indent --%> 
      <table border=0 cellpadding=4 width=90%>
        <tr><td><dsp:img src="../images/d.gif"/></td></tr>
        <tr>
            <td><span class="big">現在のオーダー</span>
            <dsp:include page="../common/FormError.jsp"></dsp:include></td>
        </tr>
        <tr>
          <td>
        <dsp:droplet name="Switch">
          <dsp:param bean="ShoppingCart.current.CommerceItemCount" name="value"/>
          <dsp:oparam name="0">
            現在のオーダーにアイテムがありません。
          </dsp:oparam>
          <dsp:oparam name="default">
            <dsp:form action="../user/save_order.jsp" method="post">
              <table border=0 cellpadding=4 cellspacing=1 width=100%>
                <tr bgcolor="#666666">
                  <td colspan=2><span class=smallbw>部品番号</span></td>
                  <td colspan=3><span class=smallbw>名前</span></td>
                  <td colspan=1><span class=smallbw>数量</span></td>
                  <td align=center colspan=1><span class=smallbw>合計</span></td>
                  <td align=center><b><span class=smallbw>削除</span><b></td>
                </tr>

                <%-- get the real shopping cart items --%>
                <dsp:droplet name="ForEach">
                  <dsp:param name="array" bean="ShoppingCart.current.CommerceItems"/>
                  <dsp:param name="elementName" value="CommerceItem"/>
                  <dsp:oparam name="output">
                    <dsp:getvalueof id="currentItem" param="CommerceItem" idtype="atg.commerce.order.CommerceItem">
		      <tr valign=top>

		      <%-- Display part number, product name/link, inventory info columns --%> 
		      <dsp:include page="cart_line_item.jsp">
                        <dsp:param name="item" value="<%=currentItem%>"/>
		      </dsp:include>

		      <%-- Display editable quantity column --%>
		      <td>&nbsp;</td>
                      <td>
                        <input type="text" size="3"
			       name="<dsp:valueof param='CommerceItem.id'/>" 
			       value="<dsp:valueof param='CommerceItem.quantity'/>">
                      </td>

		      <%-- display total list price and any discounted total price --%>
		      <dsp:include page="../common/DisplayAmount.jsp">
		        <dsp:param name="item" value="<%=currentItem%>"/>
	              </dsp:include>

		      <%-- Display "remove" checkbox column --%>
		      <td align=middle>
		        <dsp:getvalueof id="commerceItemId" param="CommerceItem.id">
                          <dsp:input type="checkbox" bean="CartModifierFormHandler.removalCommerceIds" value="<%=commerceItemId%>"/>
		        </dsp:getvalueof>
                      </td>

		      </tr>
                    </dsp:getvalueof>
                  </dsp:oparam>
                  
                </dsp:droplet> <%-- end ForEach over shipping groups --%>
                <tr>
                  <td colspan=9>
                  <table border=0 cellpadding=0 cellspacing=0 width=100%>
                    <tr bgcolor="#666666">
                      <td><dsp:img src="../images/d.gif"/></td>
                    </tr>
                  </table>
                  <td>
                </tr>
                <dsp:droplet name="Compare">
                  <dsp:param bean="ShoppingCart.current.priceInfo.amount" name="obj1"/>
                  <dsp:param bean="ShoppingCart.current.priceInfo.rawSubtotal" name="obj2"/>
                  <dsp:oparam name="equal">
                   <tr>
                      <td colspan=5>&nbsp;</td>
                      <td valign=top>小計： </td>
                      <td align=right>
                       <b>
                       <dsp:getvalueof id="pval0" bean="ShoppingCart.current.priceInfo.amount"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                       </b>
                       </td>
                     </tr>
                  </dsp:oparam>
                  <dsp:oparam name="default">
								 <tr>
                  <td colspan=5>&nbsp;</td>
                  <td>小計： </td>
                  <td align=right>
                  <b>
                  <dsp:getvalueof id="pval0" bean="ShoppingCart.current.priceInfo.rawSubtotal"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof></b>
                  </td>
                </tr>
                <tr>
                  <td colspan=5>&nbsp;</td>
                  <td>割引： </td>
                  <td align=right>
                  <b>
                  <dsp:getvalueof id="pval0" bean="ShoppingCart.current.priceInfo.discountAmount"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                  </b>
                  </td>
                </tr>
                <tr>
                  <td colspan=5>&nbsp;</td>
                  <td>合計： </td>
                  <td align=right>
                  <b>
                  <dsp:getvalueof id="pval0" bean="ShoppingCart.current.priceInfo.amount"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                 </b>
                  </td>
                </tr>

                  </dsp:oparam>
               </dsp:droplet>
              </table>
              <p>

              <%-- SAVEOrder button: --%> 
              <input type="submit" value="オーダーの保存">

              <%-- Update Order button: --%>
              <dsp:input bean="CartModifierFormHandler.setOrderByCommerceId" type="submit" value="更新"/>

              <%--
                GoTo this URL if user pushes RECALCULATE button and there are no errors:
              --%>
              <dsp:input bean="CartModifierFormHandler.setOrderSuccessURL" type="hidden" value="../checkout/cart.jsp"/> <%/* stay here */%>
              
              <%--
              GoTo this URL if user pushes RECALCULATE button and there are errors:
              --%>
              <dsp:input bean="CartModifierFormHandler.setOrderErrorURL" type="hidden" value="../checkout/cart.jsp"/> <%/* stay here */%>
              
              <%-- CHECKOUT Order button: --%> 
              &nbsp; &nbsp;   <dsp:input bean="CartModifierFormHandler.moveToPurchaseInfoByCommerceId" type="submit" value="チェックアウト"/>

              <dsp:input bean="CartModifierFormHandler.moveToPurchaseInfoSuccessURL" type="hidden" value="../checkout/shipping.jsp"/> <%-- move on to shipping --%>

              <dsp:input bean="CartModifierFormHandler.moveToPurchaseInfoErrorURL" type="hidden" value="../checkout/cart.jsp"/> <%-- stay here --%>
            </dsp:form>
          </dsp:oparam>
        
        </dsp:droplet>
        </td>
      </tr>
      <tr>
        <td><dsp:include page="../common/DisplayActivePromotions.jsp"></dsp:include>
        
        </td>
      </tr>
    </table> <%-- end indent table --%>
    </td>
        <td width=200><dsp:include page="../checkout/relatedItemSlot.jsp" flush="true"></dsp:include></td>

  </tr>
</table>

</body>
</html>

        
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/checkout/cart.jsp#2 $$Change: 651448 $--%>
