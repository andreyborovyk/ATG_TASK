<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Create Shipping Address"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Company Admin</dsp:a> &gt;
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
    <dsp:a href="shipping_edit.jsp">Shipping Addresses</dsp:a> &gt; Create Shipping Address</span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  
    <dsp:include page="../common/FormError.jsp"></dsp:include> 
     <dsp:form action="shipping_edit.jsp" method="post">
       <dsp:input bean="B2BRepositoryFormHandler.itemDescriptorName" type="hidden" value="contactInfo"/>
       <dsp:input bean="B2BRepositoryFormHandler.updateItemDescriptorName" type="hidden" value="organization"/>
       <dsp:input bean="B2BRepositoryFormHandler.updateRepositoryId" beanvalue="Profile.currentOrganization.repositoryid" type="hidden"/>
       <dsp:input bean="B2BRepositoryFormHandler.updatePropertyName" type="hidden" value="shippingAddrs"/>
       <dsp:input bean="B2BRepositoryFormHandler.requireIdOnCreate" type="hidden" value="false"/>
       <dsp:input bean="B2BRepositoryFormHandler.requiredFields" name="hiddenFields" type="hidden" value="COMPANYNAME"/>
       <dsp:input bean="B2BRepositoryFormHandler.requiredFields" name="hiddenFields" type="hidden" value="ADDRESS1"/> 
       <dsp:input bean="B2BRepositoryFormHandler.requiredFields" name="hiddenFields" type="hidden" value="CITY"/> 
       <dsp:input bean="B2BRepositoryFormHandler.requiredFields" name="hiddenFields" type="hidden" value="POSTALCODE"/> 
       <dsp:input bean="B2BRepositoryFormHandler.createErrorURL" type="hidden" value="shipping_address_create.jsp"/>
       <dsp:input bean="B2BRepositoryFormHandler.createSuccessURL" type="hidden" value="shipping_edit.jsp"/>
        
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
                  <td class=box-top>&nbsp;Create Shipping Address</td>
                </tr>
              </table>
            </td>
          </tr>
      <tr>
        <td colspan=2><span class=small>The nickname field is used to identify this address when you don't have access to the full address. It should be unique from all other shipping address nicknames. It can be the same as the company name.</span></td>
      </tr>

          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr> 
            <td align=right><span class=smallb>Nickname</span></td>
            <td width=75%><dsp:input bean="B2BRepositoryFormHandler.updateKey" size="30" type="text" value=" "/></td>
          </tr>

          <tr> 
            <td align=right><span class=smallb>Company name</span></td>
            <td width=75%><dsp:input bean="B2BRepositoryFormHandler.value.companyName" size="30" type="text" value=""/></td>
          </tr>
          <tr>
            <td align=right valign="top"><span class=smallb>Address</span></td>
            <td> 
              <dsp:input bean="B2BRepositoryFormHandler.value.address1" size="30" type="text" value=""/>
              <br>
              <dsp:input bean="B2BRepositoryFormHandler.value.address2" name="text" size="30" type="text" value=""/>
            </td>
          </tr>
          <tr>
            <td align=right><span class=smallb>City</span></td>
            <td> 
              <dsp:input bean="B2BRepositoryFormHandler.value.city" size="30" type="text" value=""/>
            </td>
          </tr>
          <tr> 
            <td align=right><span class=smallb>State/Province</span></td>
            <td> 
              <dsp:input bean="B2BRepositoryFormHandler.value.state" size="10" type="text" value=""/>
            </td>
          </tr>
          <tr>
            <td align=right><span class=smallb>Zip/Postal Code</span></td>
            <td valign="top"> 
              <dsp:input bean="B2BRepositoryFormHandler.value.postalCode" size="10" type="text" value=""/>
            </td>
          </tr>
          <tr>
            <td align=right><span class=smallb>Country</span></td>
            <td> 
              <dsp:select bean="B2BRepositoryFormHandler.value.country">
                <%@ include file="../common/CountryPicker.jspf" %>
              </dsp:select>
            </td>
          </tr>
          <tr> 
            <td><dsp:img src="../images/d.gif"/></td>
            <td><br>
              <dsp:input bean="B2BRepositoryFormHandler.create" type="submit" value="Save"/>
              &nbsp; 
              <input type="submit" value="Cancel">
            </td>


        </table>
      </dsp:form>
      </td>
    </tr>
</table>

</body>
</html>



</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/shipping_address_create.jsp#2 $$Change: 651448 $--%>
