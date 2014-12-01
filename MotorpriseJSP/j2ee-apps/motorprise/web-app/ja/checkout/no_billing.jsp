<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
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
       &nbsp; <dsp:a href="cart.jsp">現在のオーダー</dsp:a> &gt; 
       <dsp:a href="shipping.jsp">配達</dsp:a> &gt; 請求 &nbsp;</span>
    </td>
  </tr>
  
  <tr>
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>
    <td valign="top" width=745>
    <table border=0 cellpadding=4 width=80%>
      <tr><td><dsp:img src="../images/d.gif"/></td></tr>
      <tr>
        <td><span class="big">請求</span>
      </tr>
      <tr><td><dsp:img src="../images/d.gif"/></td></tr>
      <tr><td><span class=smallb>このトランザクションを完了するための認証済み支払いタイプがありません。詳細についてはシステム管理者に問い合わせてください。</span><p><span class=small><dsp:a href="cart.jsp">現在のオーダー</dsp:a>に戻ってオーダーを保存し、後で参照できるようにしておくことができます。</span></td></tr>
    </table>
    </td>
  </tr>
</table>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/checkout/no_billing.jsp#2 $$Change: 651448 $--%>
