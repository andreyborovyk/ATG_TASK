<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Zahlungsweisen bearbeiten"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Unternehmensverwaltung</dsp:a> &gt;
  <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
    <dsp:a href="payment_edit.jsp">Zahlungsmethoden</dsp:a> &gt; Zahlung bearbeiten</span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <% /*  main content area */ %>
    <td valign="top" width=745>
<% /*    <dsp:include page="../common/FormError.jsp" flush="true"></dsp:include>  */ %>
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
            <td colspan=2 valign="top"><span class=big>Unternehmensverwaltung</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
          </tr>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2 valign="top"> 
              <table width=100% cellpadding=3 cellspacing=0 border=0>
                <tr> 
                  <td class=box-top>&nbsp;Zahlungsdaten bearbeiten</td>
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
                <td align=right width="18%"><span class=smallb>Kurzname</span></td>
                <td width=67%> 
                    <dsp:input bean="B2BRepositoryFormHandler.updateKey" paramvalue="nickName" size="30" type="text"/>
                                         
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>Kreditkartentyp</span></td>
                <td width=67%> 
                <dsp:select bean="B2BRepositoryFormHandler.value.creditCardType">
                  <dsp:droplet name="Switch">
                    <dsp:param name="value" param="creditCardType"/>
                    <dsp:oparam name="Visa">
                      <dsp:option selected="<%=true%>" value="Visa"/> Visa
                      <dsp:option value="MasterCard"/> Master Card
                      <dsp:option value="AmericanExpress"/> American Express
                    </dsp:oparam>
                    <dsp:oparam name="MasterCard">
                      <dsp:option value="Visa"/> Visa
                      <dsp:option selected="<%=true%>" value="MasterCard"/> Master Card
                      <dsp:option value="AmericanExpress"/> American Express
                    </dsp:oparam>
                    <dsp:oparam name="AmericanExpress">
                      <dsp:option value="Visa"/> Visa
                      <dsp:option value="MasterCard"/> Master Card
                      <dsp:option selected="<%=true%>" value="AmericanExpress"/> American Express
                    </dsp:oparam>
                    <dsp:oparam name="unset">
                      <dsp:option value="Visa"/> Visa
                      <dsp:option value="MasterCard"/> Master Card
                      <dsp:option value="AmericanExpress"/> American Express
                    </dsp:oparam>
                    
                  </dsp:droplet>
              </dsp:select>


                                         
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>Name des Karteninhabers</span></td>
                <td width=67%> 
                    <dsp:input bean="B2BRepositoryFormHandler.value.cardHolderFirstName" paramvalue="firstName" size="10" type="text"/>
                    <dsp:input bean="B2BRepositoryFormHandler.value.cardHolderLastName" paramvalue="lastName" size="10" type="text"/>
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>Kartennummer</span></td>
                <td width=67%> 
                    <dsp:input bean="B2BRepositoryFormHandler.value.creditCardNumber" paramvalue="creditCardNumber" size="30" type="text"/>
                </td>
              </tr>
              <tr> 
                <td align=right width="18%"><span class=smallb>Ablaufdatum</span><span class=small>(mm/jjjj)</span></td>
                <td width=67%> 
                <dsp:setvalue param="Month" paramvalue="expirationMonth"/>
                <dsp:setvalue param="Year" paramvalue="expirationYear"/>
                <%@ include file="select_expiration_date.jspf" %>             
                </td>
              </tr>
              <tr><td colspan=2><dsp:img src="../images/d.gif"/></td></tr>
        
              <tr>
          <td></td>
        <td><dsp:a href="payment_address_edit.jsp"><span class=smallb>Rechnungsanschrift �ndern</span>
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
          <dsp:input bean="B2BRepositoryFormHandler.create" type="submit" value=" Speichern "/> &nbsp; 
         
          <input type="submit" value="Abbrechen">
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/admin/payment_details_edit.jsp#2 $$Change: 651448 $--%>
