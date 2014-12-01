<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>


<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=3>
      <dsp:include page="../common/BrandNav.jsp"></dsp:include>
    </td>
  </tr>
  <tr bgcolor="#DBDBDB">
    <%-- put breadcrumbs here --%>
    <td colspan=3 height=18><span class=small>
       &nbsp; <dsp:a href="cart.jsp">Current Order</dsp:a> &gt;
   <dsp:a href="shipping.jsp">Shipping</dsp:a> &gt; Billing &nbsp;</span>
    </td>
  </tr>
  
  <tr>
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>
    <td valign="top" width=745>
    <table border=0 cellpadding=4 width=80%>
      <tr><td><dsp:img src="../images/d.gif"/></td></tr>
      <tr>
        <td><span class="big">Billing</span>
      </tr>
      <tr><td><dsp:img src="../images/d.gif"/></td></tr>
      <tr><td><span class=smallb>You have no authorized payment types to complete this transaction. Please contact your system administrator for more information.</span><p><span class=small>You may return to your <dsp:a href="cart.jsp">Current Order</dsp:a> and save it for future reference.</span></td></tr>
    </table>
    </td>
  </tr>
</table>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/checkout/no_billing.jsp#2 $$Change: 651448 $--%>
