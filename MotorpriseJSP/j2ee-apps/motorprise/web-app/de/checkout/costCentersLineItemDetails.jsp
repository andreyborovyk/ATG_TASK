<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/BeanProperty"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CostCenterDroplet"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CostCenterFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/commerce/gifts/IsGiftShippingGroup"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<%--
Displays items,shipping & tax groups of the order and also the available cost centers
  in a select box so that user can choose/split the order items across the cost centers.
  We use CostCenterDroplet to initialize and create costcenters/COmmerceItentifierCostCeter
  objects, and assign these objects to CostCenterFormHandler and provide options to
  split the items across cost centers and finally apply these different CommerceIdentifierCostCenters
  to the order.
  Managing costcenters is almost similar to payment groups
--%>

<dsp:include page="../common/HeadBody.jsp">
  <dsp:param name="pagetitle" value=" Checkout"/>
</dsp:include>

<%--
CostCenterDroplet initializes the CommerceIdentifierCostCenter objects based on the
  requested init parameter and also creates costcenter objects.
--%>

<dsp:droplet name="CostCenterDroplet">
  <dsp:param name="clear" param="init"/>
  <dsp:param name="initCostCenters" value="true"/>
  <dsp:param name="initItemCostCenters" param="init"/>
  <dsp:param name="initShippingCostCenters" param="init"/>
  <dsp:param name="initTaxCostCenters" param="init"/>
  <dsp:param name="useAmount" value="false"/>
  <dsp:oparam name="output">

<%-- begin output --%>
<table border=0 cellpadding=0 cellspacing=0>
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
    <dsp:a href="cost_centers.jsp">Kostenstellen</dsp:a> &gt; Kostenstellen aufteilen &nbsp;</span>
    </td>
  </tr>
  <tr bgcolor="#CCCCCC">
    <td colspan=3><dsp:img src="../images/d.gif" vspace="0"/></td>
  </tr>
  <tr>
    <td><br>
    <table border=0 cellpadding=6 cellspacing=0 width=800>
      <tr>
        <td></td>
        <td colspan=2><span class="big">Kostenstellen</span>
        <dsp:include page="../common/FormError.jsp"></dsp:include></td>
      </tr>

      <tr valign=top>
        <td width=40><dsp:img src="../images/d.gif" hspace="20"/></td>
        <td>
    
        <table border=0 cellpadding=4 cellspacing=1 width=80%>
          <tr>
            <td colspan=12><span class=small>Jeden Einzelposten einer Kostenstelle zuweisen. Sie können einen Einzelposten auch zwischen Kostenstellen aufteilen, indem Sie die Anzahl der Artikel eingeben, die der neuen Kostenstelle zugeordnet werden sollen.
            </span></td>
          </tr>
          <tr><td><dsp:img src="../images/d.gif"/></td></tr>
          
          <tr valign=bottom bgcolor="#666666">
            <td colspan=2><span class=smallbw>Produktnr.</span></td>
            <td colspan=2><span class=smallbw>Name</span></td>
            <td colspan=2><span class=smallbw>Menge</span></td>
            <td colspan=2><span class=smallbw>Zu verschiebende Menge</span></td>
            <td colspan=1><span class=smallbw>Kostenstelle</span></td>
            <td colspan=2><span class=smallbw>Änderung speichern</span></td>
          </tr>

          <%--
            Iterate through the commerceitems of the order and assign that particular commerceIdentifierCostCenter
            to the CostCenterFormHandler to edit the costcenter information. Since we provide <dsp:form></dsp:form> elements
            across each item we will be editing only one item at any given time.
            If user splits any item across cost centers, we create extra CommerceIdentifierCostCenter to
            accomodate the split qty.
            --%>
          
          <dsp:droplet name="ForEach">
            <dsp:param name="array" param="order.commerceItems"/>
            <dsp:oparam name="output">
              <%--
                Set the current item id to CostCenterFormHandler
                --%>
              <dsp:setvalue paramvalue="element" param="commerceItem"/>
              <dsp:setvalue bean="CostCenterFormHandler.listId" paramvalue="commerceItem.id"/>
                  <dsp:droplet name="ForEach">
                    <dsp:param bean="CostCenterFormHandler.currentList" name="array"/>
                    <dsp:oparam name="output">
                      <%-- begin line item --%>
                      
                      <tr valign=top>
                        <dsp:form formid="item" action="cost_centers_line_item.jsp" method="post">
                        <td><dsp:valueof param="commerceItem.catalogRefId"/></td>
                        <td></td>
                        <td><dsp:a href="../catalog/product.jsp?navAction=jump">
                               <dsp:param name="id" param="commerceItem.auxiliaryData.productId"/>
                               <dsp:valueof param="commerceItem.auxiliaryData.productRef.displayName"/></dsp:a></td>
                        <td></td>
                     
                       
                        <td align=middle><dsp:valueof param="element.quantity"/></td>
                        <td>&nbsp;</td>
            
                       
                        <td>
                        <dsp:input bean="CostCenterFormHandler.currentList[param:index].splitQuantity" paramvalue="element.quantity" size="4" type="text"/></td>
                        <td>&nbsp;</td>
                        

                        <td align=center>
                            <%--
                              Set the cost center to be used for the current item
                              --%>
                          <dsp:getvalueof id="itemCenterName" idtype="String" param="element.costCenterName">                              
                          <dsp:select bean="CostCenterFormHandler.currentList[param:index].splitCostCenterName">
                            <%--
                              Iterate through the available cost centers so that user can choose
                              one among them to assign for the current item
                              --%>
                          <dsp:droplet name="ForEach">
                            <dsp:param name="array" param="costCenters"/>
                            <dsp:oparam name="output">
                              <dsp:droplet name="Switch">
                                <dsp:param name="value" param="key"/>
                                <dsp:getvalueof id="keyname" idtype="String" param="key">
                                <%-- <dsp:oparam name="param:...element.costCenterName"> --%>
                                  <dsp:oparam name="<%=itemCenterName%>">
                                  <dsp:option selected="<%=true%>" value="<%=keyname%>"/><dsp:valueof param="key"/>
                                  <dsp:droplet name="ForEach">
                                   <dsp:param bean="Profile.costCenters" name="array"/>
                                   <dsp:param name="elementName" value="costCenter"/>
                                   <dsp:oparam name="output">
                                    <dsp:droplet name="Switch">
                                     <dsp:param name="value" param="costCenter.identifier"/>
                                     <dsp:oparam name="<%=keyname%>">
                                      <dsp:valueof param="costCenter.description"/>
                                     </dsp:oparam>
                                    </dsp:droplet>
                                   </dsp:oparam>
                                  </dsp:droplet>
                                </dsp:oparam>
                                <dsp:oparam name="default">
                                  <dsp:option selected="<%=false%>" value="<%=keyname%>"/><dsp:valueof param="key"/>
                                  <dsp:droplet name="ForEach">
                                   <dsp:param bean="Profile.costCenters" name="array"/>
                                   <dsp:param name="elementName" value="costCenter"/>
                                   <dsp:oparam name="output">
                                    <dsp:droplet name="Switch">
                                     <dsp:param name="value" param="costCenter.identifier"/>
                                     <dsp:oparam name="<%=keyname%>">
                                      <dsp:valueof param="costCenter.description"/>
                                     </dsp:oparam>
                                    </dsp:droplet>
                                   </dsp:oparam>
                                  </dsp:droplet>
                                </dsp:oparam>
                                </dsp:getvalueof>
                              </dsp:droplet>
                            </dsp:oparam>
                          </dsp:droplet>
                          </dsp:select>

                          </dsp:getvalueof>
                        </td>
                        <td align=center>
                          <dsp:input bean="CostCenterFormHandler.listId" paramvalue="commerceItem.id" priority="<%=(int) 9%>" type="hidden"/>
                          <dsp:input bean="CostCenterFormHandler.splitCostCentersSuccessURL" type="hidden" value="cost_centers_line_item.jsp?init=false"/>
                          <dsp:input bean="CostCenterFormHandler.splitCostCentersErrorURL" type="hidden" value="cost_centers_line_item.jsp?init=false"/>
                          <dsp:input bean="CostCenterFormHandler.splitCostCenters" type="submit" value="Speichern"/>
                        </td>
                      </tr>
                      </dsp:form>
                      <%-- end line item --%>
                    </dsp:oparam>
                  </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>

          <%-- Iterate over the ShippingGroups and allow the user to select a CostCenter for each --%>
          <dsp:droplet name="ForEach">
            <dsp:param name="array" param="order.shippingGroups"/>
            <dsp:oparam name="output">
              <dsp:setvalue paramvalue="element" param="shippingGroup"/>
              <dsp:setvalue bean="CostCenterFormHandler.listId" paramvalue="shippingGroup.id"/>
              <tr valign=top><td colspan=8>
              <dsp:droplet name="Switch">
                <dsp:param name="value" param="size"/>
                <dsp:oparam name="1">
                  Versand
                </dsp:oparam>
                <dsp:oparam name="default">
                  Versandgruppe -<dsp:valueof param="count"/> 
                </dsp:oparam>
              </dsp:droplet></td>
              <dsp:droplet name="ForEach">
                <dsp:param bean="CostCenterFormHandler.currentList" name="array"/>
                <dsp:oparam name="output">
                  <dsp:form formid="shipping" action="cost_centers_line_item.jsp" method="post">
                  <td align=center>
                  <dsp:getvalueof id="SGCenterName" idtype="String" param="element.costCenterName">
                  <dsp:select bean="CostCenterFormHandler.currentList[param:index].costCenterName">
                          <dsp:droplet name="ForEach">
                            <dsp:param name="array" param="costCenters"/>
                            <dsp:getvalueof id="keyname" idtype="String" param="key">                            
                            <dsp:oparam name="output">
                              <dsp:droplet name="Switch">
                                <dsp:param name="value" param="key"/>
<%--                                <dsp:oparam name="param:...element.costCenterName"> --%>
                                  <dsp:oparam name="<%=SGCenterName%>">
                                  <dsp:option selected="<%=true%>" value="<%=keyname%>"/><dsp:valueof param="key"/>
                                  <dsp:droplet name="ForEach">
                                   <dsp:param bean="Profile.costCenters" name="array"/>
                                   <dsp:param name="elementName" value="costCenter"/>
                                   <dsp:oparam name="output">
                                    <dsp:droplet name="Switch">
                                     <dsp:param name="value" param="costCenter.identifier"/>
                                     <dsp:oparam name="<%=keyname%>">
                                      <dsp:valueof param="costCenter.description"/>
                                     </dsp:oparam>
                                    </dsp:droplet>
                                   </dsp:oparam>
                                  </dsp:droplet>
                                </dsp:oparam>
                                <dsp:oparam name="default">
                                  <dsp:option selected="<%=false%>" value="<%=keyname%>"/><dsp:valueof param="key"/>
                                  <dsp:droplet name="ForEach">
                                   <dsp:param bean="Profile.costCenters" name="array"/>
                                   <dsp:param name="elementName" value="costCenter"/>
                                   <dsp:oparam name="output">
                                    <dsp:droplet name="Switch">
                                     <dsp:param name="value" param="costCenter.identifier"/>
                                     <dsp:oparam name="<%=keyname%>">
                                      <dsp:valueof param="costCenter.description"/>
                                     </dsp:oparam>
                                    </dsp:droplet>
                                   </dsp:oparam>
                                  </dsp:droplet>
                                </dsp:oparam>
                              </dsp:droplet>
                            </dsp:oparam>
                            </dsp:getvalueof>
                          </dsp:droplet>
                </dsp:select>
                </dsp:getvalueof>
                </td>
                <td align=center>
                <dsp:input bean="CostCenterFormHandler.listId" paramvalue="shippingGroup.id" priority="<%=(int) 9%>" type="hidden"/>
                <dsp:input bean="CostCenterFormHandler.splitCostCentersSuccessURL" type="hidden" value="cost_centers_line_item.jsp?init=false"/>
                <dsp:input bean="CostCenterFormHandler.splitCostCentersErrorURL" type="hidden" value="cost_centers_line_item.jsp?init=false"/>
                <dsp:input bean="CostCenterFormHandler.splitCostCenters" type="submit" value="Speichern"/>
                </td>
                </tr>
                </dsp:form>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>

          <%-- Let the user select a CostCenter for the order's tax --%>
          <dsp:setvalue bean="CostCenterFormHandler.listId" paramvalue="order.id"/>
              <dsp:droplet name="ForEach">
                <dsp:param bean="CostCenterFormHandler.currentList" name="array"/>
                <dsp:oparam name="output">
                  <dsp:form formid="tax" action="cost_centers_line_item.jsp" method="post">

                <dsp:droplet name="Switch">
                  <dsp:param name="value" param="element.RelationshipType"/>
                    <dsp:oparam name="CCTAXAMOUNT">
                        
                      <tr valign=top>
                    <td colspan=8>Steuer</td>
                    <td align=center>
                      <dsp:getvalueof id="taxCenterName" idtype="String" param="element.costCenterName">
                      <dsp:select bean="CostCenterFormHandler.currentList[param:index].costCenterName">
                          <dsp:droplet name="ForEach">
                            <dsp:param name="array" param="costCenters"/>
                            <dsp:oparam name="output">
                              <dsp:droplet name="Switch">
                                <dsp:param name="value" param="key"/>
                                <dsp:getvalueof id="keyname" idtype="String" param="key">
                                <dsp:oparam name="<%= taxCenterName%>">
<%--                                <dsp:oparam name="param:...element.costCenterName"> --%>
                                  <dsp:option selected="<%=true%>" value="<%=keyname%>"/><dsp:valueof param="key"/>
                                  <dsp:droplet name="ForEach">
                                   <dsp:param bean="Profile.costCenters" name="array"/>
                                   <dsp:param name="elementName" value="costCenter"/>
                                   <dsp:oparam name="output">
                                    <dsp:droplet name="Switch">
                                     <dsp:param name="value" param="costCenter.identifier"/>
                                     <dsp:oparam name="<%=keyname%>">
                                      <dsp:valueof param="costCenter.description"/>
                                     </dsp:oparam>
                                    </dsp:droplet>
                                   </dsp:oparam>
                                  </dsp:droplet>
                                </dsp:oparam>
                                <dsp:oparam name="default">
                                  <dsp:option selected="<%=false%>" value="<%=keyname%>"/><dsp:valueof param="key"/>
                                  <dsp:droplet name="ForEach">
                                   <dsp:param bean="Profile.costCenters" name="array"/>
                                   <dsp:param name="elementName" value="costCenter"/>
                                   <dsp:oparam name="output">
                                    <dsp:droplet name="Switch">
                                     <dsp:param name="value" param="costCenter.identifier"/>
                                     <dsp:oparam name="<%=keyname%>">
                                      <dsp:valueof param="costCenter.description"/>
                                     </dsp:oparam>
                                    </dsp:droplet>
                                   </dsp:oparam>
                                  </dsp:droplet>
                                </dsp:oparam>
                                </dsp:getvalueof>
                              </dsp:droplet>
                            </dsp:oparam>
                          </dsp:droplet>
                      </dsp:select>
                      </dsp:getvalueof>
                    </td>
                    <td align=center>
                      <%-- end tax line item --%>
                  </dsp:oparam>
                </dsp:droplet>
                <dsp:input bean="CostCenterFormHandler.listId" paramvalue="order.id" priority="<%= (int) 9%>" type="hidden"/>
                <dsp:input bean="CostCenterFormHandler.splitCostCentersSuccessURL" type="hidden" value="cost_centers_line_item.jsp?init=false"/>
                <dsp:input bean="CostCenterFormHandler.splitCostCentersErrorURL" type="hidden" value="cost_centers_line_item.jsp?init=false"/>
                <dsp:input bean="CostCenterFormHandler.splitCostCenters" type="submit" value="Speichern"/>
                   </td>
                  </tr>
                  </dsp:form>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>


          <tr>
            <td colspan=13>
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
      <td></td>
      <td><span class=help>Sie müssen die Änderungen individuell speichern, bevor Sie auf "Weiter" klicken.</span><p>
          <dsp:form formid="apply" action="cost_centers_line_item.jsp" method="post">
          <dsp:input bean="CostCenterFormHandler.applyCostCentersSuccessURL" type="hidden" value="confirmation.jsp"/>
          <dsp:input bean="CostCenterFormHandler.applyCostCentersErrorURL" type="hidden" value="cost_centers_line_item.jsp?init=false"/>
          <dsp:input bean="CostCenterFormHandler.applyCostCenters" type="submit" value="Weiter"/>
        </dsp:form>
     </td>
   </tr>
 </table>
 </td>
</tr>
</table>

<%-- end output --%>
  <%-- </dsp:oparam>
</dsp:droplet> --%>
<%/* Version: $Change: 651448 $$DateTime: 2011/06/07 13:55:45 $*/%>


</dsp:page>
<%-- Version: $Change: 651448 $$DateTime: 2011/06/07 13:55:45 $--%>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/checkout/costCentersLineItemDetails.jsp#2 $$Change: 651448 $--%>
