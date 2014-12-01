<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CancelOrderFormHandler"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>


<dsp:include page="../common/HeadBody.jsp">
  <dsp:param name="pagetitle" value=" Checkout"/>
</dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2>
      <dsp:include page="../common/BrandNav.jsp"></dsp:include>
    </td>
    <!- this needs to be put somewhere -->
    <dsp:include page="../common/FormError.jsp"></dsp:include>
  </tr>
  <tr bgcolor="#DBDBDB">
    <td colspan=2 height=18><span class=small>
       &nbsp; <dsp:a href="cart.jsp">Warenkorb</dsp:a> &gt; Checkout &nbsp;</span>
    </td>
  </tr>
  
  <tr>
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>
    <td valign="top" width=745>
    <table border=0 cellpadding=4 cellspacing=0 width=80%>
      <tr valign=top>
        <td>
        <dsp:form action="thank_you.jsp">  
          <table border=0 cellpadding=4 width=100%>
            <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr>
              <td colspan=3><span class="big">Bestellungsbestätigung</span></td>
            </tr>
            <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

            <tr valign=top>
              <td colspan=2>
                <table width=100% cellpadding=3 cellspacing=0 border=0>
                  <tr><td class=box-top>&nbsp;Kostenstelle - Details</td></tr></table>
              </td>
            </tr>

            <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

            <%/*display all Cost Centers*/%>
            <dsp:droplet name="ForEach">
              <dsp:param bean="ShoppingCart.current.costCenters" name="array"/>
              <dsp:param name="elementName" value="costCenter"/>
              <dsp:oparam name="output">
                <tr valign=top>
                  <td align=right><span class=smallb>Kostenstelle</span></td>
                  <td width=75%>
                  <dsp:valueof param="costCenter.identifier"/> -
                    <dsp:droplet name="ForEach">
                      <dsp:param bean="Profile.costCenters" name="array"/>
                      <dsp:param name="elementName" value="CC"/>
                      <dsp:oparam name="output">
                        <dsp:droplet name="Switch">
                          <dsp:param name="value" param="CC.identifier"/>
                          <dsp:getvalueof id="ccId" idtype="String" param="costCenter.Identifier">
                            <dsp:oparam name="<%=ccId%>">
                              <dsp:valueof param="CC.description"/>
                            </dsp:oparam>
                          </dsp:getvalueof>
                        </dsp:droplet>
                      </dsp:oparam>
                    </dsp:droplet>
                  </td>
                </tr>


                <tr valign=top>
                  <td align=right><span class=smallb>Positionen</span></td>
                  <td>
                      <table border=0 cellpadding=0 cellspacing=5 width=100%>
                        <tr valign=bottom>
                          <td><span class=smallb>Menge</span></td>
                          <td><span class=smallb>Produktnr.</span></td>
                          <td><span class=smallb>Name</span></td>
                          <td><span class=smallb></span></td>
                        </tr>
                        <tr>
                          <td bgcolor="#666666" colspan=4><dsp:img src="../images/d.gif"/></td>
                        </tr>
                
                        <dsp:droplet name="ForEach">
                          <dsp:param name="array" param="costCenter.CommerceItemRelationships"/>
                          <dsp:param name="elementName" value="CCRel"/>
                          <dsp:oparam name="output">
                            <tr valign=top>
                              <td align=center><dsp:valueof param="CCRel.quantity"/></td>
                              <td><dsp:valueof param="ccrel.commerceitem.auxiliarydata.catalogref.manufacturer_part_number"/></td>
                              <td><dsp:a href="../catalog/product.jsp">
                                  <dsp:param name="id" param="CCRel.commerceItem.auxiliaryData.productId"/>
                                  <dsp:valueof param="CCRel.commerceItem.auxiliaryData.productRef.displayName"/></dsp:a></td>
                              <td align=right><%-- <dsp:valueof currency param="CCRel.amount"/> --%>
                                </td>
                            </tr>
                         </dsp:oparam><!-- End: output -->
                         </dsp:droplet> <!-- End: ForEach on CIrelationships -->

                         <dsp:droplet name="ForEach">
                           <dsp:param name="array" param="costCenter.ShippingGroupRelationships"/>
                           <dsp:param name="elementName" value="SGRel"/>
                           <dsp:oparam name="output">
														 <dsp:droplet name="Switch">
															 <dsp:param name="value" param="size"/>
															 <dsp:oparam name="1">
																 <tr>
																	 <td colspan=3> Versandgruppe
															     </td>
                                   <td align=right>
                                   </td>
                                 </tr>
															 </dsp:oparam>
															 <dsp:oparam name="default">
																 <tr>
																	 <td colspan=3> Versandgruppe -
																	 <dsp:valueof param="count"/>
															     </td>
                                   <td align=right>
                                   </td>
                                 </tr>
															 </dsp:oparam>
														 </dsp:droplet>
                           </dsp:oparam>
                         </dsp:droplet> <!-- End: ForEach on SGRelationships -->
                         <%-- 
                                 There is no exclusive relationship for tax, unlike
                                 shipping group and comerce items. We use costCenterOrderrlation
                                 ship, so get orderRelationShip from the order, and check the
                                 relationshipType of it, if it is B2BRelationShipTypes.CCTAXAMOUNT(703)
                                 then the relation ship represents tax
                                 --%>

                         <dsp:droplet name="/atg/dynamo/droplet/Switch">
                            <dsp:param name="value" param="costCenter.orderRelationshipCount"/>
                            <dsp:oparam name="1">

                              <dsp:droplet name="/atg/dynamo/droplet/Switch">
                                <dsp:param name="value" param="costCenter.orderRelationship.relationShipType"/>

                                 <dsp:oparam name="703">
                                    <tr>
                                      <td colspan=4> Steuer
                                      </td>
                                    </tr>
                                  </dsp:oparam>

                                </dsp:droplet>

                              </dsp:oparam>
                            </dsp:droplet>


                         <tr>
                          <td bgcolor="#666666" colspan=4><dsp:img src="../images/d.gif"/></td>
                        </tr>
                        
                          
                         </table>
                         </td>
                         </tr>
                </dsp:oparam>

                </dsp:droplet>

            <tr>
              <td></td>
              <td><span class=smallb><dsp:a href="cost_centers_line_item.jsp?init=false">Kostenstelle-Info bearbeiten</dsp:a></span><br> 
              <span class=smallb><dsp:a href="confirmation.jsp">Zurück zur Bestellungsbestätigung</dsp:a></span><br>

             </td>
            </tr>
            <tr><td><dsp:img src="../images/d.gif"/></td></tr>

            <tr>
              <td></td>
              <td>
                <dsp:input bean="CommitOrderFormHandler.commitOrder" type="submit" value="Bestellung aufgeben"/>

                <dsp:input bean="CommitOrderFormHandler.commitOrderSuccessURL" type="hidden" value="thank_you.jsp"/>

                <dsp:input bean="CommitOrderFormHandler.commitOrderErrorURL" type="hidden" value="confirmation.jsp"/>

                <dsp:input bean="CancelOrderFormHandler.cancelOrder" type="submit" value="Bestellung stornieren"/>

                <dsp:input bean="CancelOrderFormHandler.orderIdToCancel" beanvalue="ShoppingCart.current.id" type="hidden"/>

                <dsp:input bean="CancelOrderFormHandler.cancelOrderSuccessURL" type="hidden" value="../checkout/order_not_submitted_cancelled.jsp"/>

                <dsp:input bean="CancelOrderFormHandler.cancelOrderErrorURL" type="hidden" value="../checkout/confirmation.jsp"/>
                </td>
              </tr>
            </table>
          </dsp:form>
        </td>
      </tr>
    </table>
  </td>
</tr>
</table>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/checkout/confirm_cc_details.jsp#2 $$Change: 651448 $--%>
