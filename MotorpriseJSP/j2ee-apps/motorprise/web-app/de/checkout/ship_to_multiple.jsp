<%@ taglib uri="dsp" prefix="dsp" %>
<%@ taglib uri="core" prefix="core" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/BeanProperty"/>
<dsp:importbean bean="/atg/commerce/gifts/IsGiftShippingGroup"/>
<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupDroplet"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Checkout"/></dsp:include>

<%--
The ShippingGroupDroplet potentially resets the user's HardgoodShippingGroups based 
on the value of the request parameter init.
--%>

<dsp:droplet name="ShippingGroupDroplet">
  <dsp:param name="clearShippingGroups" param="init"/>
  <dsp:param name="initShippingGroups" param="init"/>
  <dsp:param name="shippingGroupTypes" value="hardgoodShippingGroup"/>
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
       &nbsp; <dsp:a href="cart.jsp">Aktuelle Bestellung</dsp:a> &gt; 
     <dsp:a href="shipping.jsp">Versand</dsp:a> &gt; Versand an mehrere Adressen &nbsp;</span>
    </td>
  </tr>
  
  <tr>
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>
  <td valign="top" width=745>
  <table border=0 cellpadding=4 width=80%>
    <tr><td><dsp:img src="../images/d.gif"/></td></tr>
    <tr>
      <td colspan=2><span class="big">Versand an mehrere Adressen</span>
     <dsp:include page="../common/FormError.jsp"></dsp:include></td>
    </tr>
    <tr><td><dsp:img src="../images/d.gif"/></td></tr>
    <tr valign=top>
        <td>
        <%-- table with multiple rows with eleven cells --%>
        <table border=0 cellpadding=4 cellspacing=1 width=100%>
          <tr> 
            <td colspan=12><span class=small>Um einen Einzelposten an eine weitere Adresse zu versenden, wählen Sie die Adresse aus und klicken Sie auf ""Speichern"". Um nur einen Teil der Positionen an eine weitere Adresse zu versenden, ändern Sie die Mengenangabe und wählen Sie die Adresse. Sie müssen die Änderungen individuell speichern, bevor Sie auf "Weiter" klicken.
            </span></td>
          </tr>
          <tr><td><dsp:img src="../images/d.gif"/></td></tr>
          <tr bgcolor="#666666" valign=bottom>
            <td colspan=2><span class=smallbw>Produktnr.</span></td>
            <td colspan=2><span class=smallbw>Name</span></td>
            <td colspan=2 align=middle><span class=smallbw>Menge</span></td>
            <td colspan=2 align=middle><span class=smallbw>Zu verschiebende Menge</span></td>
            <td colspan=2 align=middle><span class=smallbw>Versandanschrift</span></td>
            <td colspan=2><span class=smallbw>Änderungen speichern</span></td>

          </tr>
          <%-- 
          For each CommerceItem in the Order, we obtain a List of CommerceItemShippingInfo objects.
          Each CommerceItemShippingInfo object associates the CommerceItem to a particular ShippingGroup.
          --%>

          <dsp:droplet name="ForEach">
            <dsp:param name="array" param="order.commerceItems"/>
            <dsp:oparam name="output">
              <dsp:setvalue paramvalue="element" param="commerceItem"/>
              <dsp:setvalue bean="ShippingGroupFormHandler.listId" paramvalue="commerceItem.id"/>
              <dsp:droplet name="ForEach">
                <dsp:param bean="ShippingGroupFormHandler.currentList" name="array"/>
                <dsp:oparam name="output">
                  <dsp:setvalue paramvalue="element" param="cisiItem"/>
                  <dsp:form formid="item" action="ship_to_multiple.jsp" method="post">
                  <tr valign=top>
                   <td><nobr><dsp:valueof param="commerceItem.auxiliaryData.catalogRef.manufacturer_part_number"/></nobr></td>
                   <td></td>
                   <td><dsp:a href="../catalog/product.jsp?navAction=jump">
                         <dsp:param name="id" param="commerceItem.auxiliaryData.productId"/>
                         <dsp:valueof param="commerceItem.auxiliaryData.catalogRef.displayName"/></dsp:a></td>
                   <td></td>
        
                   <td align=right><dsp:valueof param="element.quantity"/></td>
                   <td>&nbsp;</td>
                   <td>

                  <%--
                  These form elements permit the user to assign ShippingGroups by name and for
                  a specific quantity to a CommerceItem.
                  --%>

                   <dsp:input bean="ShippingGroupFormHandler.currentList[param:index].splitQuantity" paramvalue="element.quantity" size="4" type="text"/></td>
                   <td>&nbsp;</td>
                   <td>
                     <dsp:select bean="ShippingGroupFormHandler.currentList[param:index].splitShippingGroupName">
                     <dsp:droplet name="ForEach">
                       <dsp:param name="array" param="shippingGroups"/>
                       <dsp:oparam name="output">
                       
                         <dsp:droplet name="Switch">
                           <dsp:param name="value" param="key"/>
                           <dsp:getvalueof id="SGName" idtype="String" param="cisiItem.shippingGroupName">
                           <dsp:getvalueof id="keyname" idtype="String" param="key">
                           <dsp:oparam name="<%=SGName%>">
                             <dsp:option selected="<%=true%>" value="<%=keyname%>"/><dsp:valueof param="key"/>
                           </dsp:oparam>
                           <dsp:oparam name="default">
                             <dsp:option selected="<%=false%>" value="<%=keyname%>"/><dsp:valueof param="key"/>
                           </dsp:oparam>
                           </dsp:getvalueof>
                           </dsp:getvalueof>
                           
                         </dsp:droplet> 
                         
                         <%-- equivalent code to switch droplet using core:case. Both of these work correctly.
                         <dsp:getvalueof id="keyname" idtype="String" param="key">
                         <dsp:getvalueof id="SGName" idtype="String" param="cisiItem.shippingGroupName">
                           <core:switch value="<%=keyname%>">
                             <core:case value="<%=SGName %>">
                               <dsp:option selected="<%=true%>" value="<%=keyname%>"/><dsp:valueof param="key"/>
                             </core:case>


                             <core:defaultCase>
                               <dsp:option selected="<%=false%>" value="<%=keyname%>"/><dsp:valueof param="key"/>
                             </core:defaultCase>
                           </core:switch>
                         </dsp:getvalueof>
                         </dsp:getvalueof>
                         --%>
                         
                       </dsp:oparam>
                     </dsp:droplet>
                     </dsp:select>
                   </td>
                   <td></td>
                   <td>
                     <%--
                     Split the CommerceItemShippingInfos and redirect right back here with init=false.
                     --%>
                  
                     <dsp:input bean="ShippingGroupFormHandler.splitShippingInfosSuccessURL" type="hidden" value="ship_to_multiple.jsp?init=false"/>
                     <dsp:input bean="ShippingGroupFormHandler.ListId" paramvalue="commerceItem.id" priority="<%=(int) 9%>" type="hidden"/>
                     <dsp:input bean="ShippingGroupFormHandler.splitShippingInfos" type="submit" value=" Speichern "/>
                   </td>
                  </tr>
                  </dsp:form>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>

        <tr>
          <td colspan=12>
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
      <td>
        <%--
        Invoke the applyShippingGroups handler and redirect to shipping_method.jsp upon success.
        --%>

        <dsp:form formid="apply" action="ship_to_multiple.jsp" method="post">
        <dsp:input bean="ShippingGroupFormHandler.applyShippingGroupsSuccessURL" type="hidden" value="shipping_method.jsp"/>
        <dsp:input bean="ShippingGroupFormHandler.applyShippingGroups" type="submit" value="Weiter"/>
        </dsp:form>
     </td>
   </tr>
 </table>
 </td>
</tr>
</table>

  </dsp:oparam>
</dsp:droplet>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/checkout/ship_to_multiple.jsp#2 $$Change: 651448 $--%>
