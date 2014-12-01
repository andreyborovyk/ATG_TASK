<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/userdirectory/AddressLookUp"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Format"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Versandanschrift bearbeiten"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Unternehmensverwaltung</dsp:a> &gt;
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
	<dsp:a href="shipping_edit.jsp">Versandanschrift</dsp:a> &gt; Versandanschrift bearbeiten</span> </td>
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
        <td colspan=2 valign="top"><span class=big>Unternehmensverwaltung</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
      </tr>
      <tr> 
        <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
      </tr>
      <tr> 
        <td colspan=2 valign="top" height="27"> 
        <table width=100% cellpadding=3 cellspacing=0 border=0>
          <tr> 
            <td class=box-top>&nbsp;Versandanschrift bearbeiten</td>
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
                <td align=right width="18%"><span class=smallb>Kurzname</span></td>
                <td width=67%><dsp:valueof param="nickName">Kein Kurzname</dsp:valueof></td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>Firmenname</span></td>
                <td width=67%><dsp:input bean="B2BRepositoryFormHandler.value.companyName" paramvalue="shippingAddress.companyName" size="30" type="text"/></td>
              </tr>
              <tr> 
                <td valign="top" align="right" width="18%"><span class=smallb>Adresse</span></td>
                <td width="67%">
          <dsp:input bean="B2BRepositoryFormHandler.value.address1" paramvalue="shippingAddress.address1" size="30" type="text"/>
                  <br>
                  <dsp:input bean="B2BRepositoryFormHandler.value.address2" paramvalue="shippingAddress.address2" name="text" size="30" type="text"/>
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>Ort</span></td>
                <td width="67%"> 
                  <dsp:input bean="B2BRepositoryFormHandler.value.city" paramvalue="shippingAddress.city" size="30" type="text"/>
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>Bundesland</span></td>
                <td width="67%"> 
                  <dsp:input bean="B2BRepositoryFormHandler.value.state" paramvalue="shippingAddress.state" size="10" type="text"/>
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>PLZ</span></td>
                <td valign="top" width="67%"> 
                  <dsp:input bean="B2BRepositoryFormHandler.value.postalCode" paramvalue="shippingAddress.postalCode" name="password" size="10" type="text"/>
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>Land</span></td>
                <td width="67%"> 
                  <dsp:select bean="B2BRepositoryFormHandler.value.country">
                    <%@ include file="../common/CountryPicker.jspf" %>
                  </dsp:select>
                </td>
              </tr>
              <tr> 
                <td height="56" width="18%"></td>
                <td height="56" width="67%"> 
                
                  <dsp:input bean="B2BRepositoryFormHandler.update" type="submit" value=" Speichern "/>
                  &nbsp; 

                  <input type="submit" value=" Abbrechen " name="submit">
                </td>
              </tr>         
              </table>
              
              </dsp:oparam>
              <dsp:oparam name="empty">
              Die Versandkennung lautet: <dsp:valueof param="shippingId">nichts </dsp:valueof>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/admin/shipping_details_edit.jsp#2 $$Change: 651448 $--%>
