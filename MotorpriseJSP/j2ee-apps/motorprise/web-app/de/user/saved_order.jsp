<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>


<%/* expect a order number to be passed in, we could be acceepting a orderid but run the risk of users trying random orderId's and
seeing other people's orders. We might use the obfuscated id generator, but this is safter then even that*/%>

<DECLAREPARAM NAME="orderId" CLASS="java.lang.Integer" DESCRIPTION="number of the saved order">



<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Gespeicherte Bestellung"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" >
    <dsp:droplet name="OrderLookup">
    <dsp:param name="orderId"  param="orderId"/>
  <dsp:oparam name="output">
    <dsp:getvalueof id="resultParam" param="result">
    <dsp:setvalue  value="<%=resultParam%>"  param="order"/>
    <td colspan=2 height=18> &nbsp; <span class=small>
     <dsp:a href="my_account.jsp">Mein Konto</dsp:a> &gt;
     <dsp:a href="saved_orders.jsp">Gespeicherte Bestellungen</dsp:a> &gt;
    <dsp:valueof  param="order.description"/></span>
    </td>
    </dsp:getvalueof>
  </dsp:oparam> </dsp:droplet>
  </tr>


  <tr valign=top> 
    <td width=55><img src="../images/d.gif" hspace=27></td>
    <td valign="top" width=745>


    <!-- if nothing is in the cart say there is nothing in the cart -->
    <!-- put errors here -->
    <!-- this table just for indent -->

    <table border=0 cellpadding=4 width=80%>
      <tr><td><img src="../images/d.gif"></td></tr>
      <tr>
        <td colspan=2><span class="big">Mein Konto</span></td>
      </tr>
      <tr><td><img src="../images/d.gif"></td></tr>
      <tr>
        <td colspan=2><table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Gespeicherte Bestellung</td></tr></table></td>
      </tr>
      <tr>

      <dsp:droplet name="OrderLookup">
        <dsp:param name="orderId" param= "orderId"/>
        <dsp:param name="userId"  bean= "Profile.repositoryId"/>
        <dsp:param value="incomplete" name="state"/>
        <dsp:oparam name="error">
          Die angegebene Bestellung konnte nicht abgerufen werden: <dsp:valueof  param="errorMsg"/>
        </dsp:oparam>
        <dsp:oparam name="output">
         <dsp:getvalueof id="resultParam" param="result">
         <dsp:setvalue value="<%=resultParam%>" param="order"/>
         </dsp:getvalueof>

         <td><br><b><dsp:valueof  param="order.description"/></b><br>
         <span class=small>Bestellnr.<dsp:valueof  param="order.Id"/>
         - Gespeichert <dsp:valueof date="MMMMM d, yyyy h:mm a"  param="order.creationDate"/></span> 

          <table border=0 cellpadding=4 cellspacing=1>
            <tr bgcolor="#666666">
              <td colspan=2><span class=smallbw>Produktnr.</span></td>
              <td colspan=2><span class=smallbw>Name</span></td>
              <td colspan=2 align=middle><span class=smallbw>Menge</span></td>
              <td colspan=2align=middle><span class=smallbw>Summe</span></td>

            </tr>

	    <dsp:droplet name="ForEach">
	      <dsp:param name="array" param="order.commerceItems"/>
	      <dsp:oparam name="output">
		<dsp:getvalueof id="currentItem" param="element" idtype="atg.commerce.order.CommerceItem">
		  <tr valign=top>

		  <%-- Display part number, product name/link, inventory info columns --%>
		  <dsp:include page="../checkout/cart_line_item.jsp">
		    <dsp:param name="item" value="<%=currentItem%>"/>
		  </dsp:include>

		  <%-- Display quantity --%>
		  <td align=center><dsp:valueof param="element.quantity"/></td>
		  <td></td>

		  <%-- display total list price and any discounted total price --%>
		  <dsp:include page="../common/DisplayAmount.jsp">
		    <dsp:param name="item" value="<%=currentItem%>"/>
		  </dsp:include>

		  </tr>
		</dsp:getvalueof>
	      </dsp:oparam>
	    </dsp:droplet>

            <tr>
              <td colspan=8><table border=0 cellpadding=0 cellspacing=0 width=100%>
                <tr bgcolor="#666666"><td><img src="../images/d.gif"></td></tr></table>
            </tr>
            <tr>
              <td colspan=4>&nbsp; </td>
              <td>Zwischensumme</td>
              <td></td>
              <td align="right">
                <b>
                <dsp:getvalueof id="pval0" param="order.priceInfo.amount"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>              
                </b>
              </td>
            </tr>

	    <tr><td><br></td></tr>
	    <tr>
	    	<td colspan=4 align=right>
	    	  <dsp:form formid="use" action="../checkout/cart.jsp" method="post">
	    	  <dsp:input bean="ShoppingCart.handlerOrderId" paramvalue="order.id" type="hidden"/>
	    	  <dsp:input bean="ShoppingCart.switch" type="submit" value="Bestellung aktivieren"/> &nbsp;
	    	  </dsp:form>
	    	</td>
	    	<td colspan=2 align=left>
	         <dsp:form formid="delete" action="../user/saved_orders.jsp" method="post">
		 <dsp:input bean="ShoppingCart.handlerOrderId" paramvalue="order.id" type="hidden"/>
                <dsp:input bean="ShoppingCart.delete" type="submit" value="Löschen"/> &nbsp;
                </dsp:form>
		</td>
	    </tr>
          <!-- end indent table -->
	 </table>
         </td>
       </dsp:oparam> <%/*orderlookup*/%>
     </dsp:droplet>
 
     </tr>
    </table>
    </td>
  </tr>
</table>
</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/user/saved_order.jsp#2 $$Change: 651448 $--%>
