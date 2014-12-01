<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/TableForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Format"/>
<dsp:importbean bean="/atg/projects/b2bstore/userdirectory/AddressLookUp"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>


<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Rechnungsanschriften"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Unternehmensverwaltung</dsp:a> &gt;
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt; <dsp:a href="billing_addresses.jsp">Rechnungsanschriften</dsp:a> &gt;
     Rechnungsanschrift löschen
    </span></td>
  </tr>
  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  
    
    <table border=0 cellpadding=4 width=80%>
      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr>
        <td colspan=2 valign="top"><span class=big>Unternehmensverwaltung</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span>
       <dsp:include page="../common/FormError.jsp"></dsp:include>
        

        <dsp:form action="billing_addresses.jsp" method="POST">
	      <dsp:input bean="B2BRepositoryFormHandler.repositoryId" paramvalue="billingId" type="hidden"/>
	      <dsp:input bean="B2BRepositoryFormHandler.itemDescriptorName" type="hidden" value="contactInfo"/>
        <dsp:input bean="B2BRepositoryFormHandler.updateItemDescriptorName" type="hidden" value="organization"/>
        <dsp:input bean="B2BRepositoryFormHandler.updateRepositoryId" beanvalue="Profile.currentOrganization.repositoryId" type="hidden"/>
      	<dsp:input bean="B2BRepositoryFormHandler.updatePropertyName" type="hidden" value="billingAddrs"/>
      	<dsp:input bean="B2BRepositoryFormHandler.updateKey" paramvalue="nickName" type="hidden"/>

          <%--  Prepare the URL to redirect to, in case of an error during form submission --%>
          <dsp:droplet name="Format">
          <dsp:param name="value1" param="billingId"/>
          <dsp:param name="value2" param="nickName"/>
          <dsp:param name="format" value="billing_addr_delete.jsp?billingId={value1}&nickName={value2}"/>
          <dsp:oparam name="output">
            <dsp:setvalue paramvalue="message" valueishtml="<%=true%>" param="deleteErrorURL"/>
            <dsp:input bean="B2BRepositoryFormHandler.deleteErrorURL" paramvalue="deleteErrorURL" type="hidden"/>
          </dsp:oparam>
          </dsp:droplet>
         

          <blockquote>
          <dsp:droplet name="AddressLookUp">
            <dsp:param name="id" param="billingId"/>
            <dsp:param name="elementName" value="billingAddress"/>
            <dsp:oparam name="output">
              <dsp:valueof param="nickName"/><br>                          
              <dsp:getvalueof id="pval0" param="billingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
              <p>
               <dsp:input bean="B2BRepositoryFormHandler.delete" type="submit" value="Adresse löschen"/> &nbsp;
               <input type="submit" value="Abbrechen">
              
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/admin/billing_addr_delete.jsp#2 $$Change: 651448 $--%>
