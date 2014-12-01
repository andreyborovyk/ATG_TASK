<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="- オーダーのキャンセル"/></dsp:include>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" オーダーのキャンセル"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0>
  <tr>
    <td colspan=2>
      <dsp:include page="../common/BrandNav.jsp"></dsp:include>
    </td>
  </tr>
  <tr bgcolor="#CCCCCC">
    <td colspan=2>
      <dsp:img src="../images/d.gif" vspace="0"/>
    </td>
  </tr>

  <tr valign=top>
    <td width=25%>
    </td>

    <%-- main content area --%>
    <td valign="top" width=75%>
      <table border=0 cellpadding=4 width=100%>
        <tr>
          <td>
            <dsp:img src="../images/d.gif" vspace="0"/>
          </td>
        </tr>
        <tr valign=top>
          <td></td>
          <td colspan=2>
            <span class=big>オーダーのキャンセルの確認</span>
          </td>
        </tr>

        <tr>
          <td>
            <dsp:img src="../images/d.gif" vspace="0"/>
          </td>
        </tr>

        <tr valign=top>
          <td width=25>
            <dsp:img src="../images/d.gif" hspace="12"/>
          </td>
          <td>
            お客様のオーダーはキャンセルされました。
            <P>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/checkout/order_not_submitted_cancelled.jsp#2 $$Change: 651448 $--%>
