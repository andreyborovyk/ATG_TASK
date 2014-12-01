<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/TableForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/projects/b2bstore/userdirectory/paymentTypeLookUp"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>


<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Payment Methods"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Company Admin</dsp:a> &gt;
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt; <dsp:a href="payment_edit.jsp">Credit Cards</dsp:a> &gt;
     Delete Credit Card
    </span></td>
  </tr>
  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <% /*  main content area */ %>
    <td valign="top" width=745>  
    
    <table border=0 cellpadding=4 width=80%>
      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr>
        <td colspan=2 valign="top"><span class=big>Company Administration</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span>
        <dsp:include page="../common/FormError.jsp"></dsp:include>
        

</td>
      </tr>
      <tr> 
        <td colspan=2><img src="../images/d.gif" vspace=0></td>
      </tr>
      <tr>
        <td colspan=2 valign="top"> 
        <table width=100% cellpadding=3 cellspacing=0 border=0>
          <tr> 
            <td class=box-top>&nbsp;Delete Payment Method</td>
          </tr>
        </table>
        </td>
      </tr>
      <tr>
        <td>Delete this credit card?</td>
      </tr>
           
      <tr> 
        <td valign="top" colspan=2 height="92"> 
        <dsp:form action="payment_edit.jsp" method="POST">
	<dsp:input bean="B2BRepositoryFormHandler.repositoryId" paramvalue="paymentId" type="hidden"/>
	<dsp:input bean="B2BRepositoryFormHandler.itemDescriptorName" type="hidden" value="credit-card"/>
        <dsp:input bean="B2BRepositoryFormHandler.updateItemDescriptorName" type="hidden" value="organization"/>
        <dsp:input bean="B2BRepositoryFormHandler.updateRepositoryId" beanvalue="Profile.currentOrganization.repositoryid" type="hidden"/>
	<dsp:input bean="B2BRepositoryFormHandler.updatePropertyName" type="hidden" value="paymentTypes"/>
	<dsp:input bean="B2BRepositoryFormHandler.updateKey" paramvalue="nickName" type="hidden"/>

          <blockquote>
          <dsp:droplet name="paymentTypeLookUp">
            <dsp:param name="id" param="paymentId"/>
            <dsp:param name="elementName" value="paymentType"/>
            <dsp:oparam name="output">
             
<table width="100%" border="0">
                      <tr> 
                       <td><span class=smallb><dsp:valueof param="nickName"/></span></td>
                      </tr>
                      <tr>
                        <td>
                          <dsp:valueof param="paymentType.cardHolderFirstName"/>
                          <dsp:valueof param="paymentType.cardHolderLastName"/>
                        </td>
                      </tr>
                      <tr> 
                        <td><dsp:valueof param="paymentType.creditCardNumber"/></td>
                      </tr>
                      <tr> 
                        <td><dsp:valueof param="paymentType.expirationMonth"/>/<dsp:valueof param="paymentType.expirationYear"/></td>
                      </tr>
                      <tr> 
                        <td><dsp:valueof param="paymentType.billingAddress.address1"/></td>
                      </tr>
                      <tr> 
                        <td><dsp:valueof param="paymentType.billingAddress.address2"/></td>
                      </tr>
                      <tr> 
                        <td><dsp:valueof param="paymentType.billingAddress.city"/>, <dsp:valueof param="paymentType.billingAddress.state"/> <dsp:valueof param="paymentType.billingAddress.postalCode"/></td>
                      </tr>
                      <tr> 
                        <td><dsp:valueof param="paymentType.billingAddress.country"/></td>
                      </tr>
                     
                </table>



              <tr>
              <td>
              <p>
               <dsp:input bean="B2BRepositoryFormHandler.delete" type="submit" value="Delete payment method"/> &nbsp;
               <input type="submit" value="Cancel">
              </td>
              </tr>
            </dsp:oparam>
          </dsp:droplet>        
        
        
          </blockquote>
          </dsp:form>
          </td>
        </tr>

     </table>
     </td>
   </tr>
 </table>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/payment_method_delete.jsp#2 $$Change: 651448 $--%>
