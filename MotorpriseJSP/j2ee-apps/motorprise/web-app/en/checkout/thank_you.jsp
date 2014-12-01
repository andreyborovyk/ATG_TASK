<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>

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
       &nbsp; &nbsp;</span>
    </td>
  </tr>

  <tr><td><dsp:img src="../images/d.gif" vspace="10"/></td></tr>

  <tr>
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>
    <td valign="top" width=745>
    <table border=0 cellpadding=4 width=80%>
      <tr> 
        <td align="top" colspan=3>  
        <span class="big">Thank you for your order</span><p>
        <dsp:include page="../common/FormError.jsp"></dsp:include>
        </td>
      </tr>
      <dsp:droplet name="Switch">
        <dsp:param bean="ShoppingCart.last.state" name="value"/>
        <dsp:oparam name="5000">
          <tr>
            <td><span class=smallb>Your order requires approval. A message has been sent to your approver to review your order.</span><p></td>
          </tr>
        </dsp:oparam>
        <dsp:oparam name="default">
        </dsp:oparam>
      </dsp:droplet>
      <tr>
        <td>Your order number is <dsp:a href="../user/order.jsp">
        <dsp:param bean="ShoppingCart.last.id" name="orderId"/><dsp:valueof bean="ShoppingCart.last.id" /></dsp:a>.
        </td>
      </tr>
      <tr>
        <td>You will receive an email confirmation with all your order information soon. To make last minute changes to your order, track shipments, or manage other aspects of your account, use the <dsp:a href="../user/my_account.jsp">My Account</dsp:a> area of the site. 
        </td>
      </tr>
    </table>
    </td>
  </tr>
</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/checkout/thank_you.jsp#2 $$Change: 651448 $--%>
