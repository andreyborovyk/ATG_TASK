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
       &nbsp; <dsp:a href="cart.jsp">Aktuelle Bestellung</dsp:a> &gt;
   <dsp:a href="shipping.jsp">Versand</dsp:a> &gt; Rechnung &nbsp;</span>
    </td>
  </tr>
  
  <tr>
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>
    <td valign="top" width=745>
    <table border=0 cellpadding=4 width=80%>
      <tr><td><dsp:img src="../images/d.gif"/></td></tr>
      <tr>
        <td><span class="big">Rechnung</span>
      </tr>
      <tr><td><dsp:img src="../images/d.gif"/></td></tr>
      <tr><td><span class=smallb>Es liegen keine autorisierten Zahlungsweisen für Sie vor, um diese Transaktion abzuschließen. Weitere Informationen erhalten Sie bei Ihrem Systemadministrator.</span><p><span class=small>Sie können zu Ihrer <dsp:a href="cart.jsp">Aktuellen Bestellung</dsp:a> zurückkehren und sie zur späteren Verwendung speichern.</span></td></tr>
    </table>
    </td>
  </tr>
</table>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/checkout/no_billing.jsp#2 $$Change: 651448 $--%>
