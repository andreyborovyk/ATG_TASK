<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/Format"/>
<dsp:importbean bean="/atg/projects/b2bstore/userprofiling/CreditCardLookUp"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Edit Payment Methods"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Company Admin</dsp:a> &gt;
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
    <dsp:a href="payment_edit.jsp">Credit Cards</dsp:a> &gt; Edit Credit Card</span> </td>
  </tr>
  
 
  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>
 
    <%--  main content area --%>
    <td valign="top" width=745>

<% /*      <dsp:include page="../common/FormError.jsp" flush="true"></dsp:include> */ %>


<dsp:droplet name="CreditCardLookUp">
<dsp:param name="id" param="paymentId"/>
<dsp:param name="elementName" value="paymentType"/>
<dsp:oparam name="output">
   <dsp:form action="payment_edit.jsp" method="post">
   <dsp:input bean="B2BRepositoryFormHandler.itemDescriptorName" type="hidden" value="credit-card"/>
   <dsp:input bean="B2BRepositoryFormHandler.repositoryId" paramvalue="paymentId" type="hidden"/>
   <dsp:input bean="B2BRepositoryFormHandler.value.billingAddress.repositoryid" paramvalue="billingAddressId" type="hidden"/>
   <dsp:input bean="B2BRepositoryFormHandler.requiredFields" name="hiddenRequiredFields" type="hidden" value="CREDITCARDNUMBER"/>

  <%/* Construct an error URL that refers back to this page, preserving important parameters */%>
  <dsp:droplet name="Format">
    <dsp:param name="format" value="{thisPage}?paymentId={myPaymentId}&nickName={myNickName}&billingAddressId={myBillingAddrId}"/>
    <dsp:param name="thisPage" value="payment_details_edit2.jsp"/>
    <dsp:param name="myPaymentId" param="paymentId"/>
    <dsp:param name="myNickName" param="nickName"/>
    <dsp:param name="myBillngAddrId" param="billingAddressId"/>
    <dsp:oparam name="output">
      <dsp:input bean="B2BRepositoryFormHandler.updateErrorURL" paramvalue="message" type="hidden"/>
    </dsp:oparam>
  </dsp:droplet>

        <table border=0 cellpadding=4 width=80%>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2 valign="top"><span class=big>Company Administration</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
          </tr>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2 valign="top"> 
              <table width=100% cellpadding=3 cellspacing=0 border=0>
                <tr> 
                  <td class=box-top>&nbsp;Edit Credit Card</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2> 
            <% /*  Account Information */ %>
          
            <table width="100%" border="0" cellpadding="4">

              <tr> 
               <td align=right><span class=smallb>Nickname</span></td>
               <td width=75% colspan="2"> 
                 <dsp:valueof param="nickName"/>
               </td>
              </tr>
            
              <tr> 
                <td align=right width="18%"><span class=smallb>Credit card type</span></td>
                <td width=67%> 
                <dsp:select bean="B2BRepositoryFormHandler.value.creditCardType">
                  <dsp:droplet name="Switch">
                    <dsp:param name="value" param="paymentType.creditCardType"/>
                    <dsp:oparam name="Visa">
                      <dsp:option selected="<%=true%>" value="Visa"/> Visa
                      <dsp:option value="MasterCard"/>Master Card
                      <dsp:option value="AmericanExpress"/> American Express
                    </dsp:oparam>
                    <dsp:oparam name="MasterCard">
                      <dsp:option value="Visa"/> Visa
                      <dsp:option selected="<%=true%>" value="MasterCard"/>Master Card
                      <dsp:option value="AmericanExpress"/> American Express
                    </dsp:oparam>
                    <dsp:oparam name="AmericanExpress">
                      <dsp:option value="Visa"/> Visa
                      <dsp:option value="MasterCard"/>Master Card
                      <dsp:option selected="<%=true%>" value="AmericanExpress"/> American Express
                    </dsp:oparam>
                    <dsp:oparam name="unset">
                      <dsp:option value="Visa"/> Visa
                      <dsp:option value="MasterCard"/>Master Card
                      <dsp:option value="AmericanExpress"/> American Express
                    </dsp:oparam>
                    
                  </dsp:droplet>
              </dsp:select>

                </td>
              </tr>
             <tr> 
                <td align=right width="18%"><span class=smallb>Card holder name</span></td>
                <td width=67%> 
                    <dsp:input bean="B2BRepositoryFormHandler.value.cardHolderFirstName" paramvalue="paymentType.cardHolderFirstName" size="10" type="text"/>
                    <dsp:input bean="B2BRepositoryFormHandler.value.cardHolderLastName" paramvalue="paymentType.cardHolderLastName" size="10" type="text"/>
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>Card number</span></td>
                <td width=67%> 
                    <dsp:input bean="B2BRepositoryFormHandler.value.creditCardNumber" paramvalue="paymentType.creditCardNumber" size="30" type="text"/>
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>Expiration date</span><span class=small>(mm/yyyy)</span></td>
                <td width=67%> 
                <dsp:setvalue param="Month" paramvalue="paymentType.expirationMonth"/>
                <dsp:setvalue param="Year" paramvalue="paymentType.expirationYear"/>
                <%@ include file="select_expiration_date.jspf" %>             
                </td>
              </tr>
              <tr><td colspan=2><dsp:img src="../images/d.gif"/></td></tr>
        
              <tr>
          <td></td>
        <dsp:droplet name="IsEmpty">
          <dsp:param bean="Profile.billingAddrs" name="value"/>
          <dsp:oparam name="false">
            <td>
            <dsp:a href="payment_address_edit2.jsp"><span class=smallb>Change billing address</span>
            <dsp:param name="paymentId" param="paymentId"/>
            <dsp:param name="nickName" param="nickName"/>
            </dsp:a></td>
          </dsp:oparam>
        </dsp:droplet>

        </tr>
        
        <tr>
          <td colspan=2><dsp:img src="../images/d.gif"/></td>
          </tr>
             <tr>
          <td></td>

                <td>

          <dsp:input bean="B2BRepositoryFormHandler.update" type="submit" value=" Save "/> &nbsp; 
          <input type="submit" name="submit" value="Cancel">
          </td>
              </tr>
         
            </table> 
          </td>
        </tr>
      </table>
      
    </dsp:form>

</dsp:oparam>
</dsp:droplet>
    </td>
  </tr>


</table>

</body>
</html>


</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/payment_details_edit2.jsp#2 $$Change: 651448 $--%>
