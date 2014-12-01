<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/userdirectory/AddressLookUp"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Format"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Edit Shipping Address"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Company Admin</dsp:a> &gt;
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
	<dsp:a href="shipping_edit.jsp">Shipping Address</dsp:a> &gt; Edit Shipping Address</span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  
    <dsp:include page="../common/FormError.jsp"></dsp:include> 
   
    <dsp:form action="shipping_edit.jsp" method="post">
    
    <dsp:input bean="B2BRepositoryFormHandler.itemDescriptorName" type="hidden" value="contactInfo"/>
    <dsp:input bean="B2BRepositoryFormHandler.repositoryId" paramvalue="shippingId" type="hidden"/>
    
    <% /*  Costruct the URL to redirect to, in case of an error during form submission */ %>
    <dsp:droplet name="Format">
    <dsp:param name="value1" param="shippingId"/>
    <dsp:param name="value2" param="nickName"/>
    <dsp:param name="format" value="shipping_details_edit.jsp?shippingId={value1}&nickName={value2}"/>
    <dsp:oparam name="output">
      <dsp:setvalue paramvalue="message" valueishtml="<%=true%>" param="updateErrorURL"/>
      <dsp:input bean="B2BRepositoryFormHandler.updateErrorURL" paramvalue="updateErrorURL" type="hidden"/>
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
        <td colspan=2 valign="top" height="27"> 
        <table width=100% cellpadding=3 cellspacing=0 border=0>
          <tr> 
            <td class=box-top>&nbsp;Edit Shipping Address</td>
          </tr>
        </table>
        </td>
      </tr>
      <tr> 
        <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
      </tr>
      <tr> 
        <td colspan=2> 
              
          <dsp:droplet name="AddressLookUp">
            <dsp:param name="id" param="shippingId"/>
            <dsp:param name="elementName" value="shippingAddress"/>
            <dsp:oparam name="output">
        
     
            <table border=0 cellpadding=3 width="100%">
              <tr> 
                <td align=right width="18%"><span class=smallb>Nickname</span></td>
                <td width=67%><dsp:valueof param="nickName">no nickname</dsp:valueof></td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>Company name</span></td>
                <td width=67%><dsp:input bean="B2BRepositoryFormHandler.value.companyName" paramvalue="shippingAddress.companyName" size="30" type="text"/></td>
              </tr>
              <tr> 
                <td valign="top" align="right" width="18%"><span class=smallb>Address</span></td>
                <td width="67%">
          <dsp:input bean="B2BRepositoryFormHandler.value.address1" paramvalue="shippingAddress.address1" size="30" type="text"/>
                  <br>
                  <dsp:input bean="B2BRepositoryFormHandler.value.address2" paramvalue="shippingAddress.address2" name="text" size="30" type="text"/>
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>City</span></td>
                <td width="67%"> 
                  <dsp:input bean="B2BRepositoryFormHandler.value.city" paramvalue="shippingAddress.city" size="30" type="text"/>
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>State/Province</span></td>
                <td width="67%"> 
                  <dsp:input bean="B2BRepositoryFormHandler.value.state" paramvalue="shippingAddress.state" size="10" type="text"/>
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>Zip/Postal Code</span></td>
                <td valign="top" width="67%"> 
                  <dsp:input bean="B2BRepositoryFormHandler.value.postalCode" paramvalue="shippingAddress.postalCode" name="password" size="10" type="text"/>
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>Country</span></td>
                <td width="67%"> 
                  <dsp:select bean="B2BRepositoryFormHandler.value.country">
                    <%@ include file="../common/CountryPicker.jspf" %>
                  </dsp:select>
                </td>
              </tr>
              <tr> 
                <td height="56" width="18%"></td>
                <td height="56" width="67%"> 
                
                  <dsp:input bean="B2BRepositoryFormHandler.update" type="submit" value=" Save "/>
                  &nbsp; 

                  <input type="submit" value=" Cancel " name="submit">
                </td>
              </tr>         
              </table>
              
              </dsp:oparam>
              <dsp:oparam name="empty">
              The shipping id is : <dsp:valueof param="shippingId">nothing </dsp:valueof>
              </dsp:oparam>
              </dsp:droplet>
              
            </td>
          </tr>
        </table>
    </dsp:form>
    </td>
  </tr>


</table>

</body>
</html>



</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/shipping_details_edit.jsp#2 $$Change: 651448 $--%>
