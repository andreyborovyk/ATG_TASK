<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<%-- This fragment displays a line item in the shopping cart.
         It renders the commerce item set in param:item.
--%>

<dsp:importbean bean="/atg/commerce/order/purchase/CostCenterFormHandler"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>

<%-- expect a CommerceItem to be passed in --%>
<DECLAREPARAM NAME="item" CLASS="atg.commerce.order.CommerceItem"
    DESCRIPTION="Shopping Cart Item">

<tr valign=top>
  <td align=left>
    <dsp:valueof param="item.auxiliaryData.catalogRef.manufacturer_part_number"/>
  </td>
  <dsp:droplet name="/atg/commerce/inventory/InventoryLookup">
    <dsp:param name="itemId" param="item.catalogRefId"/>
    <dsp:oparam name="output">
      <td>
        <dsp:a page="param:item.auxiliaryData.productRef.template.url">
          <dsp:param name="id" param="item.auxiliaryData.productRef.repositoryId"/>
          <dsp:param name="navAction" value="jump"/>
          <dsp:valueof param="item.auxiliaryData.catalogRef.displayName"/>
        </dsp:a>
      </td>
      <td>
        <%-- use the cost center form handler to manipulate the cost center instead of setting it directly, this is similar to the shopping cart form handler
        in how it sets quanitity. what is param= in a select input tag anyways? --%>
        <select name="item.id" param="item.costCenter">   
         the value of the cc is: <dsp:valueof param="item.costCenter">no value</dsp:valueof>
          <%--
            Add each Cost Center to the dropdown list.
            We want to automatically select the Cost Center.
          --%>
          <dsp:droplet name="ForEach">
            <dsp:param bean="Profile.costCenters" name="array"/>
            <dsp:param name="elementName" value="CostCenter"/>
            <dsp:oparam name="output">
              <dsp:droplet name="Switch">
                <dsp:param name="value" param="CostCenter"/>
                <%-- if this cost center matches the item's cost center, select it --%>
                
                <dsp:getvalueof id="costcenter" idtype="String" param="CostCenter">
                  <dsp:getvalueof id="ccItem" idtype="String" param="item.costCenter">
                    <dsp:oparam name="<%=ccItem%>">
                      <option selected="<%=true%>" value="<%=costcenter%>">
                    </dsp:oparam>
                  </dsp:getvalueof>
                  <%-- otherwise, don't select this cost center --%>
                  <dsp:oparam name="default">
                    <dsp:option selected="<%=false%>" value="<%=costcenter%>"/>
                  </dsp:oparam>
                </dsp:getvalueof>  
                
              </dsp:droplet>
              <dsp:valueof param="CostCenter"/>
            </dsp:oparam>
          </dsp:droplet>
        </select>
      </td>
    </dsp:oparam>
  </dsp:droplet>

  <dsp:input bean="CostCenterFormHandler.updateCostCenters" name="updateCostCenters" type="hidden"/>

</tr>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/checkout/cost_center_line_item.jsp#2 $$Change: 651448 $--%>
