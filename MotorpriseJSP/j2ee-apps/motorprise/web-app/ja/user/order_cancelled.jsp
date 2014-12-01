<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<DECLAREPARAM NAME="orderId" CLASS="java.lang.Integer" DESCRIPTION="number of the saved order">

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" オーダーのキャンセル"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="my_account.jsp">私のアカウント</dsp:a> &gt; 
    オーダーのキャンセル</td>
  </tr>

  <tr valign=top> 
    <td width=55><img src="../images/d.gif" hspace=27></td>  
  <td valign="top" width=745>

    <!-- main content area -->  
    <table border=0 cellpadding=4 width=80%>
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>私のアカウント</span></td>
      </tr>
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
    <tr valign=top>
            <td colspan=3>
            <table width=100% cellpadding=3 cellspacing=0 border=0>
              <tr><td class=box-top>&nbsp;オーダーのキャンセル</td></tr></table>
            </td>
          </tr>
    <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <tr valign=top>
        <td>

          オーダーのキャンセルの指示は、オーダープロセッサに送られました。  
          <P>
          <dsp:a href="orders_open.jsp">処理中のオーダー</dsp:a>ページのオーダーリストからオーダーが消去されるまで、数分かかる場合があります。

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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/user/order_cancelled.jsp#2 $$Change: 651448 $--%>
