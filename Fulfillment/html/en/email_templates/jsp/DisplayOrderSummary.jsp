<%@ taglib uri="http://www.atg.com/dsp.tld" prefix="dsp" %>
<dsp:page>


<%/* A shopping cart-like display of order information */%>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>

<table cellspacing=2 cellpadding=0 border=0>
  <tr>
    <td><b>Quantity</b></td>
    <td></td>
    <td>&nbsp;&nbsp;</td>
    <td><b>Product</b></td>
    <td>&nbsp;&nbsp;</td>
    <td><b>SKU</b></td>
    <td>&nbsp;&nbsp;</td>

    <dsp:droplet name="Switch">
     <dsp:param name="value" param="displayStockStatus"/>
      <dsp:oparam name="true">    
        <td><b>Instock?</b></td>
        <td>&nbsp;&nbsp;</td>
      </dsp:oparam>
    </dsp:droplet>

    <td align=right><b>List Price</b></td>
    <td>&nbsp;&nbsp;</td>
    <td align=right><b>Sale Price</b></td>
    <td>&nbsp;&nbsp;</td>
    <td align=right><b>Total Price</b></td>
  </tr>

  <dsp:droplet name="Switch">
    <dsp:param name="value" param="displayStockStatus"/>
    <dsp:oparam name="true">    
      <tr><td colspan=14><hr size=0></td></tr>
    </dsp:oparam>
    <dsp:oparam name="default">
      <tr><td colspan=12><hr size=0></td></tr>
    </dsp:oparam>
  </dsp:droplet>

  <dsp:droplet name="ForEach">
    <dsp:param name="array" param="order.commerceItems"/>
    <dsp:param name="elementName" value="item"/>
    <dsp:oparam name="output">
      <tr valign=top>
        <td>
          <dsp:valueof param="item.quantity">No Quantity</dsp:valueof>
        </td>
        <td></td>
        <td>&nbsp;&nbsp;</td>
        <td>
          <dsp:valueof param="item.auxiliaryData.productRef.displayName">Unknown</dsp:valueof>
        </td>
        <td>&nbsp;&nbsp;</td>
        <td>
          <dsp:valueof param="item.auxiliaryData.catalogRef.displayName">Unknown</dsp:valueof>
        </td>
        <td>&nbsp;&nbsp;</td>

          <dsp:droplet name="Switch">
            <dsp:param name="value" param="displayStockStatus"/>
            <dsp:oparam name="true">    
              <td>
              <dsp:droplet name="InventoryLookup">
                <dsp:param name="itemId" param="item.catalogRefId"/>
                <dsp:param name="useCache" value="true"/>
                <dsp:oparam name="output">
                  <dsp:droplet name="Switch">
                    <dsp:param name="value" param="inventoryInfo.availabilityStatus"/>
                    <dsp:oparam name="1000">
                      <b>Yes</b>
                    </dsp:oparam>
                    <dsp:oparam name="default">
                      No
                    </dsp:oparam>
                  </dsp:droplet>
                </dsp:oparam>
              </dsp:droplet>
              </td>
              <td>&nbsp;&nbsp;</td>
            </dsp:oparam>
          </dsp:droplet>

          <td align=right>
          <dsp:valueof converter="currency" param="item.priceInfo.listPrice">No Price</dsp:valueof>
          </td>
          <td>&nbsp;&nbsp;</td>
          <td align=right>
            <dsp:droplet name="Switch">
              <dsp:param name="value" param="item.priceInfo.onSale"/>
              <dsp:oparam name="true">
                <dsp:valueof converter="currency" param="item.priceInfo.salePrice"/>
              </dsp:oparam>
              <dsp:oparam name="false">
                No Sale Price
              </dsp:oparam>
            </dsp:droplet>
          </td>
          <td>&nbsp;&nbsp;</td>
          <td align=right>
            <dsp:valueof converter="currency" param="item.priceInfo.amount">No Price</dsp:valueof>
          </td>
        </tr>
      </dsp:oparam>
    
      <dsp:oparam name="empty"><tr colspan=10 valign=top><td>No Items</td></tr></dsp:oparam>
      </dsp:droplet>

    <dsp:droplet name="Switch">
      <dsp:param name="value" param="displayStockStatus"/>
      <dsp:oparam name="true">    
        <tr><td colspan=14><hr size=0></td></tr>
        <tr>
          <td colspan=13 align=right>Subtotal</td>
      </dsp:oparam>
      <dsp:oparam name="default">
        <tr><td colspan=12><hr size=0></td></tr>
        <tr>
          <td colspan=11 align=right>Subtotal</td>
      </dsp:oparam>
    </dsp:droplet>
    
    <td align=right>
      <dsp:valueof converter="currency" param="order.priceInfo.amount">no price</dsp:valueof>
    </td>
  </tr>

  <tr>
  <dsp:droplet name="Switch">
    <dsp:param name="value" param="displayStockStatus"/>
    <dsp:oparam name="true">    
      <td colspan=13 align=right>Shipping</td>
    </dsp:oparam>
    <dsp:oparam name="default">
      <td colspan=11 align=right>Shipping</td>
    </dsp:oparam>
  </dsp:droplet>

    <td align=right>
      <dsp:valueof converter="currency" param="order.priceInfo.shipping">no price</dsp:valueof>
    </td>
  </tr>

  <tr>
  <dsp:droplet name="Switch">
    <dsp:param name="value" param="displayStockStatus"/>
    <dsp:oparam name="true">    
      <td colspan=13 align=right>Tax</td>
    </dsp:oparam>
    <dsp:oparam name="default">
      <td colspan=11 align=right>Tax</td>
    </dsp:oparam>
  </dsp:droplet>

    <td align=right>
      <dsp:valueof converter="currency" param="order.priceInfo.tax">no price</dsp:valueof>
    </td>
  </tr>
        
  <tr>
  <dsp:droplet name="Switch">
    <dsp:param name="value" param="displayStockStatus"/>
    <dsp:oparam name="true">    
      <td colspan=13 align=right><b>Total</b></td>
    </dsp:oparam>
    <dsp:oparam name="default">
      <td colspan=11 align=right><b>Total</b></td>
    </dsp:oparam>
  </dsp:droplet>

    <td align=right>
      <b><dsp:valueof converter="currency" param="order.priceInfo.total">no price</dsp:valueof></b>
    </td>
  </tr>
</table>
<br>


</dsp:page>
<%-- @version $Id: //product/DCS/version/10.0.3/release/Fulfillment/html/en/email_templates/jsp/DisplayOrderSummary.jsp#2 $$Change: 651448 $--%>
