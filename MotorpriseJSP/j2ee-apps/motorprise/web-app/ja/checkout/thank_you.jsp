<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" チェックアウト"/></dsp:include>
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
        <span class="big">ご注文ありがとうございました。</span><p>
        <dsp:include page="../common/FormError.jsp"></dsp:include>
        </td>
      </tr>
      <dsp:droplet name="Switch">
        <dsp:param bean="ShoppingCart.last.state" name="value"/>
        <dsp:oparam name="5000">
          <tr>
            <td><span class=smallb>オーダーには承認が必要です。オーダーの承認をいただくために、承認者にメッセージを送信しました。</span><p></td>
          </tr>
        </dsp:oparam>
        <dsp:oparam name="default">
        </dsp:oparam>
      </dsp:droplet>
      <tr>
        <td>オーダー番号： <dsp:a href="../user/order.jsp">
        <dsp:param bean="ShoppingCart.last.id" name="orderId"/><dsp:valueof bean="ShoppingCart.last.id" /></dsp:a>.
        </td>
      </tr>
      <tr>
        <td>すべてのオーダー情報を確認する電子メールが直ちに送信されます。オーダーの変更、配達のトラッキング、およびその他のアカウント操作をする場合は、このサイトの <dsp:a href="../user/my_account.jsp">[私のアカウント]</dsp:a> エリアをご利用ください。 
        </td>
      </tr>
    </table>
    </td>
  </tr>
</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/checkout/thank_you.jsp#2 $$Change: 651448 $--%>
