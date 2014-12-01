<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  支払い方法の編集"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">会社管理</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp">
      <dsp:valueof bean="Profile.currentOrganization.name"/>
    </dsp:a> &gt; 
    <dsp:a href="payment_edit.jsp">支払い方法</dsp:a> &gt; 支払いの編集</span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>
<%--    <dsp:include page="../common/FormError.jsp" flush="true"></dsp:include>  --%>
   <dsp:form action="payment_edit.jsp" method="post">
   <dsp:input bean="B2BRepositoryFormHandler.itemDescriptorName" type="hidden" value="credit-card"/>
   <dsp:input bean="B2BRepositoryFormHandler.updateItemDescriptorName" type="hidden" value="organization"/>
   <dsp:input bean="B2BRepositoryFormHandler.updateRepositoryId" beanvalue="Profile.currentOrganization.repositoryid" type="hidden"/>
   <dsp:input bean="B2BRepositoryFormHandler.updatePropertyName" type="hidden" value="paymentTypes"/>
   <dsp:input bean="B2BRepositoryFormHandler.requireIdOnCreate" type="hidden" value="false"/>
   <dsp:input bean="B2BRepositoryFormHandler.value.billingAddress.repositoryid" paramvalue="billingAddress" type="hidden"/>
   <dsp:input bean="B2BRepositoryFormHandler.createSuccessURL" type="hidden" value="payment_edit.jsp"/>
   <dsp:input bean="B2BRepositoryFormHandler.createErrorURL" type="hidden" value="payment_details_edit.jsp"/>
  
        <table border=0 cellpadding=4 width=80%>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2 valign="top"><span class=big>会社管理</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
          </tr>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2 valign="top"> 
              <table width=100% cellpadding=3 cellspacing=0 border=0>
                <tr> 
                  <td class=box-top>&nbsp;支払い情報の編集</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2> 
            <!-- Account Information -->
            <table width="100%" border="0" cellpadding="4">
            <tr> 
                <td align=right width="18%"><span class=smallb>ニックネーム</span></td>
                <td width=67%> 
                    <dsp:input bean="B2BRepositoryFormHandler.updateKey" paramvalue="nickName" size="30" type="text"/>
                                         
                </td>
              </tr>
              <dsp:setvalue bean="B2BRepositoryFormHandler.value.creditCardType" paramvalue="creditCardType"/>
              <tr> 
                <td align=right width="18%"><span class=smallb>クレジットカードタイプ</span></td>
                <td width=67%> 
                <dsp:select bean="B2BRepositoryFormHandler.value.creditCardType">
                
                  <dsp:option value="Visa"/> VISA
                  <dsp:option value="MasterCard"/>MasterCard
                  <dsp:option value="AmericanExpress"/> アメリカン・エキスプレス
              
              </dsp:select>


                                         
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>カード保有者名</span></td>
                <td width=67%> 
                    <dsp:input bean="B2BRepositoryFormHandler.value.cardHolderLastName" paramvalue="lastName" size="10" type="text"/>
                    <dsp:input bean="B2BRepositoryFormHandler.value.cardHolderFirstName" paramvalue="firstName" size="10" type="text"/>
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>カード番号</span></td>
                <td width=67%> 
                    <dsp:input bean="B2BRepositoryFormHandler.value.creditCardNumber" paramvalue="creditCardNumber" size="30" type="text"/>
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>有効期限</span><span class=small> (yyyy/mm)</span></td>
                <td width=67%> 
                <dsp:setvalue param="Year" paramvalue="expirationYear"/>
                <dsp:setvalue param="Month" paramvalue="expirationMonth"/>
                <%@ include file="select_expiration_date.jspf" %>
                </td>
              </tr>
              <tr><td colspan=2><dsp:img src="../images/d.gif"/></td></tr>
        
              <tr>
          <td></td>
        <td>
        <dsp:a href="payment_address_edit.jsp">
          <span class=smallb>請求先住所の変更</span>
          <dsp:param name="creditCardType" param="creditCardType"/>
          <dsp:param name="nickName" param="nickName"/>
          <dsp:param name="firstName" param="firstName"/>
          <dsp:param name="lastName" param="lastName"/>
          <dsp:param name="creditCardNumber" param="creditCardNumber"/>
          <dsp:param name="expirationMonth" param="expirationMonth"/>
          <dsp:param name="expirationDayOfMonth" param="expirationDayOfMonth"/>
          <dsp:param name="expirationYear" param="expirationYear"/>
             
        </dsp:a></td>
        </tr>
        
        <tr>
          <td colspan=2><dsp:img src="../images/d.gif"/></td>
          </tr>
             <tr>
          <td></td>
                <td>
          <dsp:input bean="B2BRepositoryFormHandler.create" type="submit" value=" 保存 "/> &nbsp; 
         
          <input type="submit" value="キャンセル">
          </td>
              </tr>
          
            </table>
          </td>
        </tr>
        <!-- vertical space -->
        <tr> 
          <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
        </tr>
      </table>
    </dsp:form>
    </td>
  </tr>


</table>
</div>
</body>
</html>



</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/admin/payment_details_edit.jsp#2 $$Change: 651448 $--%>
