<%@ taglib uri="dsp" prefix="dsp" %>
<%@ taglib uri="core" prefix="core" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<%-- 
This fragment takes a CommerceItem as a page parameter, and creates
four columns of a table filled with information about that item.  

The first column displays the manufacturer's part number for the item.  

The second column is always empty.

The third column displays a link to the item's product template in the
catalog.  

The fourth column displays some inventory status information about that
item if the item is out of stock or the stock level is smaller than the
number of units in the order.  If the item is in stock and there are enough
units to fill the order available, the fourth column will be empty.
--%>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<%-- expect a CommerceItem to be passed in --%>
<DECLAREPARAM NAME="item" CLASS="atg.commerce.order.CommerceItem"
    DESCRIPTION="Shopping Cart Item">

  <td align=left>
    <dsp:valueof param="item.auxiliaryData.catalogRef.manufacturer_part_number"/>
  </td>

  <td></td>

  <dsp:droplet name="/atg/commerce/inventory/InventoryLookup">
    <dsp:param name="itemId" param="item.catalogRefId"/>
    <dsp:oparam name="output">
      <td>
        <dsp:getvalueof id="templateURL" param="item.auxiliaryData.productRef.template.url" idtype="java.lang.String">
        <dsp:a page="<%=templateURL%>">
          <dsp:param name="id" param="item.auxiliaryData.productRef.repositoryId"/>
          <dsp:param name="navAction" value="jump"/>
          <dsp:valueof param="item.auxiliaryData.catalogRef.displayName"/>
        </dsp:a>
	</dsp:getvalueof>
      </td>

      <td>
        <%-- Display inventory information if any skus are not available --%>
        <dsp:getvalueof id="inventoryStatus" idtype="java.lang.Integer" param="inventoryInfo.availabilityStatus">
        <dsp:droplet name="Switch">
          <dsp:param name="value" value="<%=inventoryStatus%>"/>
          <dsp:oparam name="1001">
            （在庫切れ）
          </dsp:oparam>
          <dsp:oparam name="1002">
            （プレオーダー）
          </dsp:oparam>
          <dsp:oparam name="1003">
            （バックオーダー）
          </dsp:oparam>
	  <dsp:oparam name="1005">
	    （販売終了）
	  </dsp:oparam>
          <dsp:oparam name="1000">

	    <%-- Item is in stock, so see if we have enough inventory to cover the order. --%>
	    <%-- If we have inventory info for the item (stockLevel is not null) and the  --%>
	    <%-- user is requesting more than stockLevel units, and stockLevel is not     --%>
	    <%-- equal to - -1 (the code meaning unlimited supply), then tell the user    --%>
	    <%-- how many units are left in stock.                                        --%>

	    <dsp:getvalueof id="stockLevel" param="inventoryInfo.stockLevel" idtype="java.lang.Long">
	    <dsp:getvalueof id="quantityOrdered" param="item.quantity" idtype="java.lang.Long">
	    <core:ifNotNull value="<%=stockLevel%>">
	      <core:ifNotEqual object1="<%=stockLevel%>" long2="-1">
	        <core:ifLessThan object1="<%=stockLevel%>" object2="<%=quantityOrdered%>">
                  （残り<%=stockLevel%> のみ）
		</core:ifLessThan>
              </core:ifNotEqual>
            </core:ifNotNull>
	    </dsp:getvalueof>
	    </dsp:getvalueof>
          </dsp:oparam>
        </dsp:droplet>
        </dsp:getvalueof>
      </td>
    </dsp:oparam>    
  </dsp:droplet>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/checkout/cart_line_item.jsp#2 $$Change: 651448 $--%>
