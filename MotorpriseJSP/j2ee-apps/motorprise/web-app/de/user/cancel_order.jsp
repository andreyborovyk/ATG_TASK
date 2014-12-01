<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CancelOrderFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>

<DECLAREPARAM NAME="orderId" CLASS="java.lang.Integer" DESCRIPTION="number of the saved order">

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Bestellung stornieren"/></dsp:include>
<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="my_account.jsp">Mein Konto</dsp:a> &gt;
      Bestellung stornieren</td>
  </tr>
  <tr>
    <td width=55><img src="../images/d.gif" hspace=27></td>  

    <!-- main content area -->
    <td valign="top" width=745>  


    <table border=0 cellpadding=4 width=80%>
      <!-- vertical space -->
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>Mein Konto</span></td>
      </tr>
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <tr valign=top>
            <td colspan=3>
            <table width=100% cellpadding=3 cellspacing=0 border=0>
              <tr><td class=box-top>&nbsp;Bestellung stornieren</td></tr></table>
            </td>
       </tr>

      <!-- vertical space -->
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>

      <tr valign=top>
        <td>
       <%/* Display any error messages while cancelling order  */%>
       
        <dsp:droplet name="ErrorMessageForEach">
        <dsp:param name="array" bean="CancelOrderFormHandler.formexceptions"/>
        <dsp:oparam name="output">
          <font color="red"><dsp:valueof param="propertyName"/>
                            <dsp:valueof param="message"/></font>
        </dsp:oparam>
      </dsp:droplet>
    
      <dsp:droplet name="OrderLookup">
        <dsp:param name="orderId" param="orderId"/>
        <dsp:param name="userId" bean="Profile.repositoryid"/>
        <dsp:param name="state" value="incomplete"/>
        <dsp:oparam name="error">
          Die angegebene Bestellung konnte nicht abgerufen werden: <dsp:valueof param="errorMsg"/>
        </dsp:oparam>
          
        <dsp:oparam name="output">
         <dsp:setvalue param="order" paramvalue="result"/>
          
          <dsp:form action="my_account.jsp" method="post">
          
          Bestellung <dsp:valueof param="order.id"/> stornieren?<p><br>


         <%/* If you do not wish to cancel your order, you may use your browser's
              back button to return to the order. */%>

          <dsp:input bean="CancelOrderFormHandler.orderIdToCancel" paramvalue="order.id" type="hidden"/>
          <dsp:input bean="CancelOrderFormHandler.cancelOrderErrorURL" type="hidden" value="cancel_order.jsp"/>
          <dsp:input bean="CancelOrderFormHandler.cancelOrderSuccessURL" type="hidden" value="order_cancelled.jsp"/>
          <dsp:input bean="CancelOrderFormHandler.cancelOrder" type="submit" value="Bestellung stornieren"/>
          <input type="submit" value="Bestellung nicht stornieren">
          <%/* cancel url goes back to order */%>
          
          </dsp:form>
      
        </dsp:oparam>
      </dsp:droplet>
          
        </td>
      </tr>
        
      </table>
    </td>
  </tr>

</table>
</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/user/cancel_order.jsp#2 $$Change: 651448 $--%>
