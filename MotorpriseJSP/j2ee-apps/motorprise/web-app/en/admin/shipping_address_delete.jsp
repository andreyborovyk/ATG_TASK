<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/TableForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Format"/>
<dsp:importbean bean="/atg/projects/b2bstore/userdirectory/AddressLookUp"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>


<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Shipping Addresses"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Company Admin</dsp:a> &gt;
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt; <dsp:a href="shipping_edit.jsp">Shipping Addresses</dsp:a> &gt;
     Delete Shipping Address
    </span></td>
  </tr>
  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  
    
    <table border=0 cellpadding=4 width=80%>
      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr>
        <td colspan=2 valign="top"><span class=big>Company Administration</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span>
<dsp:include page="../common/FormError.jsp"></dsp:include>

        <dsp:form action="shipping_edit.jsp" method="POST">
	        <dsp:input bean="B2BRepositoryFormHandler.repositoryId" paramvalue="shippingId" type="hidden"/>
	        <dsp:input bean="B2BRepositoryFormHandler.itemDescriptorName" type="hidden" value="contactInfo"/>
          <dsp:input bean="B2BRepositoryFormHandler.updateItemDescriptorName" type="hidden" value="organization"/>
          <dsp:input bean="B2BRepositoryFormHandler.updateRepositoryId" beanvalue="Profile.currentOrganization.repositoryid" type="hidden"/>
        	<dsp:input bean="B2BRepositoryFormHandler.updatePropertyName" type="hidden" value="shippingAddrs"/>
        	<dsp:input bean="B2BRepositoryFormHandler.updateKey" paramvalue="nickName" type="hidden"/>
          
          <% /*  Prepare the URL to redirect to, in case of an error during form submission */ %>
          <dsp:droplet name="Format">
          <dsp:param name="value1" param="shippingId"/>
          <dsp:param name="value2" param="nickName"/>
          <dsp:param name="format" value="shipping_address_delete.jsp?shippingId={value1}&nickName={value2}"/>
          <dsp:oparam name="output">
            <dsp:setvalue paramvalue="message" valueishtml="<%=true%>" param="deleteErrorURL"/>
            <dsp:input bean="B2BRepositoryFormHandler.deleteErrorURL" paramvalue="deleteErrorURL" type="hidden"/>
          </dsp:oparam>
          </dsp:droplet>
        
        
          <blockquote>
          <dsp:droplet name="AddressLookUp">
            <dsp:param name="id" param="shippingId"/>
            <dsp:param name="elementName" value="shippingAddress"/>
            <dsp:oparam name="output">
              <dsp:valueof param="nickName"/><br>             
              <dsp:getvalueof id="pval0" param="shippingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
              <p>
               <dsp:input bean="B2BRepositoryFormHandler.delete" type="submit" value="Delete address"/> &nbsp;
               <input type="submit" value="Cancel">
              
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/shipping_address_delete.jsp#2 $$Change: 651448 $--%>
