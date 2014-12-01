<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CostCenterDroplet"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CostCenterFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<%--
  Displays the payment information of the order, and also displays cost centers in
  a select box so user can choose any of the cost center for the order. 
--%>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Checkout"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
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
        <dsp:a href="billing.jsp">Rechnung</dsp:a> &gt; Kostenstellen &nbsp;</span>
    </td>
  </tr>
  <tr>
    <td valign="top" colspan=3>
    <br>

    <dsp:form action="billing.jsp" method="post">

    <P>
    <table border=0 cellpadding=6 cellspacing=0>
      <tr>
        <td width=40><dsp:img src="../images/d.gif" hspace="20"/></td>
        <td colspan=3><span class="big">Kostenstelle</span><p>
        <span class=small>Sie können eine Kostenstelle für die gesamte Bestellung
        oder eine für jeden Einzelposten auswählen.</span>
   
       <dsp:include page="../common/FormError.jsp"></dsp:include></td>
      </tr>
      <tr><td><dsp:img src="../images/d.gif"/></td></tr>


      <dsp:getvalueof id="pval0" bean="ShoppingCart.current"><dsp:include page="pmtGroupWithoutLineItems.jsp"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

     <tr>
        <td colspan=2></td>
        <td><span class=smallb>
            <dsp:getvalueof id="linkname" idtype="String" param="link">
            <dsp:a href="<%=linkname%>">
	      <dsp:param name="init" value="false"/>
	      Rechnung bearbeiten
	    </dsp:a>
            </dsp:getvalueof>  
            </span></td>
      </tr>

    
      <tr><td><dsp:img src="../images/d.gif"/></td></tr>
      <%--
        CostCenterDroplet initializes cost centers available to user, and also CostCenters objects
        corresponding to the order to associate cost centers to the order
      --%>

            <dsp:droplet name="CostCenterDroplet">
            <dsp:param name="clear" value="true"/>
            <dsp:param name="initCostCenters" value="true"/>
            <dsp:param name="initItemCostCenters" value="false"/>
            <dsp:param name="initShippingCostCenters" value="false"/>
            <dsp:param name="initTaxCostCenters" value="false"/>
            <dsp:param name="initOrderCostCenters" value="true"/>
            <dsp:param name="useAmount" value="false"/>
            <dsp:oparam name="output">

              <tr valign=top>
                <td></td>
                    <td align=right><span class=smallb>Kostenstelle</span></td>
                <td>
                  <dsp:setvalue bean="CostCenterFormHandler.listId" paramvalue="order.id"/>
                  <dsp:input bean="CostCenterFormHandler.listId" paramvalue="order.id" priority="<%=(int) 9%>" type="hidden"/>
                    <%-- we only expect this to have 1 element at [0], but we put this in a ForEach to be safe --%>
                  <dsp:droplet name="ForEach">
                    <dsp:param bean="CostCenterFormHandler.currentList" name="array"/>
                    <dsp:oparam name="output">
                      <dsp:select bean="CostCenterFormHandler.currentList[param:index].CostCenterName"><br>

                        <%--
                          List all the cost centers available to the user in select
                          box, so he can choose one among them.
                          --%>
                      <dsp:droplet name="ForEach">
                        <dsp:param bean="Profile.costCenters" name="array"/>
                        <dsp:param name="elementName" value="CostCenter"/>
                        <dsp:oparam name="output">
                          <dsp:droplet name="Switch">
                            <dsp:param name="value" param="CostCenter.repositoryId"/>
                            <dsp:getvalueof id="costCenterId" idtype="String" param="CostCenter.identifier">
			    <dsp:getvalueof id="defaultCostCenter" idtype="String" bean="Profile.defaultCostCenter.repositoryId">
                              <dsp:oparam name="<%=defaultCostCenter%>">
                                <dsp:option selected="<%=true%>" value="<%=costCenterId%>"/>
                                  <dsp:valueof param="CostCenter.identifier"/> -
                                  <dsp:valueof param="CostCenter.description"/>
                              </dsp:oparam>
                              <dsp:oparam name="default">
                                <dsp:option selected="<%=false%>" value="<%=costCenterId%>"/>
                                  <dsp:valueof param="CostCenter.identifier"/>
                                  <dsp:valueof param="CostCenter.description"/>
                              </dsp:oparam>
                            </dsp:getvalueof>
			    </dsp:getvalueof>
                            
                      </dsp:droplet> <%-- Switch --%>
                </dsp:oparam><%-- End: ForEach.oparam --%>
          </dsp:droplet> <%-- End: ForEach --%>
                      </dsp:select>
                    </dsp:oparam>
                  </dsp:droplet>
              </td></tr>
                </dsp:oparam>
          </dsp:droplet> <%-- End: CostCenterDroplet --%>


      <tr>
        <td></td>
      <tr>
        <td colspan=2></td>
        <td><br><span class=smallb>
        <dsp:a href="cost_centers_line_item.jsp">Mehrere Kostenstellen wählen</dsp:a></span></td>
      </tr>

      <tr valign="top">
        <td colspan=2></td>
        <td><br>
            <%-- CONTINUE button: --%>
                        <dsp:input bean="CostCenterFormHandler.applyCostCenters" type="submit" value="Weiter"/>
            <%-- Goto this URL if NO errors are found during the CONTINUE button processing: --%>                              
              <dsp:input bean="CostCenterFormHandler.applyCostCentersSuccessURL" type="hidden" value="confirmation.jsp"/>

            <%-- Goto this URL if errors are found during the CONTINUE button processing: --%>
            <dsp:input bean="CostCenterFormHandler.applyCostCentersErrorURL" type="hidden" value="cost_centers.jsp"/> <%/* stay on same page */%>
        <p>

        </td>
      </tr>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/checkout/cost_centers.jsp#2 $$Change: 651448 $--%>
