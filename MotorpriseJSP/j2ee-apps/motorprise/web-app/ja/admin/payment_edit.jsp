<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/TableForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  支払い方法"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">会社管理</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt; 
    クレジットカード</span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>

    <td valign="top" width=745>
      <table border=0 cellpadding=4 width=80%>
        <tr> 
          <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
        </tr>
        <tr> 
          <td colspan=2 valign="top"><span class=big>会社管理</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
        </tr>
        <tr> 
          <td height="10"><dsp:img src="../images/d.gif" vspace="0"/></td>
        </tr>
        <tr> 
          <td valign="top"> 
          <table width=100% cellpadding=3 cellspacing=0 border=0>
            <tr> 
              <td class=box-top>&nbsp;クレジットカード</td>
            </tr>
          </table>
          </td>
        </tr>
          <tr>
            <td colspan=2>
            <%--  check if inherited --%>
            <dsp:droplet name="IsEmpty">
              <dsp:param bean="Profile.currentOrganization.myPaymentTypes" name="value"/>
              <dsp:oparam name="false">
                <dsp:img src="../images/sethere.gif" align="left" hspace="6"/>
                <span class=small>この情報は、このビジネスユニットの固有情報です。</span>
              </dsp:oparam>
              <dsp:oparam name="true">
                <dsp:droplet name="IsEmpty">
                <dsp:param bean="Profile.currentOrganization.parentOrganization.paymentTypes" name="value"/>
                <dsp:oparam name="false">
                <dsp:img src="../images/inherited.gif" align="left" hspace="6"/>
                <span class=small>この情報は、会社の企業プロファイルで指定されています。</span>
                </dsp:oparam>
                <dsp:oparam name="true">
                   <span class=small>この組織に対するクレジットカードはありません。</span>
                </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>  
            </dsp:droplet>
            </td>
          </tr>
        <tr>
          <td valign="top" align="left">

            <dsp:droplet name="TableForEach">
              <dsp:param bean="Profile.currentOrganization.paymentTypes" name="array"/>
              <dsp:param name="elementName" value="paymentType"/>
              <dsp:param name="numColumns" value="2"/>
              <dsp:oparam name="outputStart"><table border=0 cellpadding=3 width=100%></dsp:oparam>
              <dsp:oparam name="outputEnd"></table></dsp:oparam>
              <dsp:oparam name="outputRowStart"><tr></dsp:oparam>
              <dsp:oparam name="outputRowEnd"></tr></dsp:oparam>
              <dsp:oparam name="output">
                <dsp:droplet name="IsEmpty">
                  <dsp:param name="value" param="paymentType.creditCardNumber"/>
                  <dsp:oparam name="true">
                    <td></td>
                  </dsp:oparam>
                  <dsp:oparam name="false">
                    <td valign=top>
                    <% /* individual table for each address */ %>
                    <table cellpadding=3 border="0">
                      <tr>
                       <td align=right valign=top><span class=smallb>ニックネーム</td>
                       <td><dsp:valueof param="key"/></td>
                      </tr>
                      <tr>
                       <td align=right><span class=smallb>カードのタイプ</td>
                       <td><dsp:valueof param="paymentType.creditCardType"/></td>
                      </tr>

                      <tr> 
                        <td align=right><span class=smallb><nobr>カード番号</nobr></td>
                        <td valign=top><dsp:valueof param="paymentType.creditCardNumber"/></td>
                      </tr>
                      <tr> 
                        <td align=right><span class=smallb>有効期限</td>
                        <td valign=top><dsp:valueof param="paymentType.expirationMonth"/>/<dsp:valueof param="paymentType.expirationYear"/></td>
                      </tr>
                      <tr> 
                        <td align=right valign=top><span class=smallb>請求先住所</td>
                        <td>

                       <dsp:getvalueof id="pval0" param="paymentType.billingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                        </td>
                      </tr>
               
                      <tr><td></td>
                        <td>
                       <span><dsp:a href="payment_details_edit2.jsp"><span class=smallb>編集</span>
                       <dsp:param name="paymentId" param="paymentType.id"/>
                       <dsp:param name="nickName" param="key"/>
                       <dsp:param name="billingAddressId" param="paymentType.billingAddress.repositoryid"/>
                       </dsp:a>
                       </span>|
                       <span class=smallb><dsp:a href="payment_method_delete.jsp">
                       <dsp:param name="paymentId" param="paymentType.id"/>
                       <dsp:param name="nickName" param="key"/>削除</dsp:a></span>
                    </td>
                  </tr>
                </table>
                </td>
                </dsp:oparam>
               </dsp:droplet> <% /* end IsEmpty */ %>
               </dsp:oparam>
            </dsp:droplet>            
          </td>
        </tr>
        <tr> 
          <td><br></td>
        </tr>
        <tr>
          <td colspan="3"><dsp:a href="payment_method_create.jsp"><span class=smallb>クレジットカードの新規作成</span></dsp:a></td>
        </tr>
      </table>
    </td>
    </tr>
</table>

</body>
</html>



</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/admin/payment_edit.jsp#2 $$Change: 651448 $--%>
