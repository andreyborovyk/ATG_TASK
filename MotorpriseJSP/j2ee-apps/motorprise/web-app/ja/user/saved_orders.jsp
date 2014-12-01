<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="保存済みオーダー"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
   <dsp:a href="my_account.jsp">私のアカウント</dsp:a> &gt; 
   保存済みオーダー</span>

  <tr valign=top> 
    <td width=55><img src="../images/d.gif" hspace=27></td>  
    <!-- main content area -->
    <td valign="top" width=745>  


    <table border=0 cellpadding=4 width=80%>
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <tr valign=top>
        <td colspan=3><span class=big>私のアカウント</span></td>
      </tr>
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <tr>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;保存済みオーダー</td></tr></table>
        </td>
      </tr>

      <!-- vertical space -->
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <dsp:droplet name="IsEmpty">
        <dsp:param name="value" bean="ShoppingCart.saved"/>
        <dsp:oparam name="true">
          <tr><td>保存済みオーダーはありません。</td></tr>
        </dsp:oparam>
        <dsp:oparam name="false">
          <tr valign=top>
            <td>
            <table border=0 cellpadding=4 width=100%>
              <tr>
                <td><b><span class=small>オーダー名</span></b></td>
                <td>&nbsp; </td>
                <td><b><span class=small>オーダー番号</span></b></td>
                <td>&nbsp; </td>
                <td><b><span class=small>保存日</span></b></td>
              </tr>
              <dsp:droplet name="ForEach">
                <dsp:param name="array" bean="ShoppingCart.saved"/>
                <dsp:oparam name="output">  
                  <tr>
                    <td><dsp:a href="saved_order.jsp"><dsp:param name="orderId" param="element.id"/><dsp:valueof  param="element.description"/></dsp:a></td>
                    <td></td>
                    <td><dsp:valueof  param="element.id"/></td>
                    <td></td>
                    <td><dsp:valueof date="yyyy'年' MM'月' d'日' HH:mm"  param="element.creationDate"/></td>
                  </tr>
                </dsp:oparam>
              </dsp:droplet>    
            </table>
            </td>
          </tr>
        </dsp:oparam>
      </dsp:droplet><!--end IsEmpty-->
      <!-- vertical space -->
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
    </table>
    </td>
  </tr>
</table>
</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/user/saved_orders.jsp#2 $$Change: 651448 $--%>
