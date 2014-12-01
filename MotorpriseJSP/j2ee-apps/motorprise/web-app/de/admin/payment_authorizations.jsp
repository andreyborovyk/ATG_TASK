<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Zahlungsweisen autorisieren"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Unternehmensverwaltung</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
    Zahlungsweisen autorisieren
    </span> </td>
  </tr>
  
  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>

    <% /*  main content area */ %>
    <td valign="top" width=745>  
    
    <% /* Display errors if any */ %>

    <dsp:include page="../common/FormError.jsp"></dsp:include> 
    
    <dsp:form action="company_admin.jsp" method="post">
    <dsp:input bean="B2BRepositoryFormHandler.itemDescriptorName" type="hidden" value="organization"/>
    <dsp:input bean="B2BRepositoryFormHandler.repositoryId" beanvalue="Profile.currentOrganization.repositoryId" type="hidden"/>
    
    <dsp:input bean="B2BRepositoryFormHandler.updateErrorURL" type="hidden" value="payment_authorizations.jsp"/>
   
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
                  <td class=box-top>&nbsp;Zahlungsautorisierung</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td valign="top" colspan=2 height="92"> 
              <table border="0" cellpadding=4 width=100%>
                <tr>
                  <td colspan=3><span class=small>
                  <dsp:img src="../images/inherited.gif" align="left"/> 
                       Diese Information ist im Unternehmensprofil Ihrer Firma angegeben.<br>
                  <dsp:img src="../images/sethere.gif" align="left" vspace="2"/>
                       Diese Information betrifft den vorliegenden Geschäftsbereich.
             
                  </span></td>
                </tr>
            
                <tr><td><dsp:img src="../images/d.gif"/></td></tr>
                
                <tr>
                  <td width=25% align="right"><span class=smallb>Rechnungsautorisierung</span></td>
                  <td>
                  <% /*  check if inherited */ %>
                  <dsp:droplet name="IsEmpty">
                    <dsp:param bean="Profile.currentOrganization.myInvoiceRequestAuthorized" name="value"/>
                    <dsp:oparam name="false">
                      <dsp:img src="../images/sethere.gif" align="left" hspace="6"/>
                    </dsp:oparam>
                    <dsp:oparam name="true">
                      <dsp:img src="../images/inherited.gif" align="left" hspace="6"/>
                    </dsp:oparam>  
                  </dsp:droplet>
                  <dsp:droplet name="Switch">
                  <dsp:param bean="Profile.currentOrganization.invoiceRequestAuthorized" name="value"/>
                  <dsp:oparam name="true">
                    <dsp:input bean="B2BRepositoryFormHandler.value.invoiceRequestAuthorized" type="checkbox" checked="<%=true%>"/>
                  </dsp:oparam>
                  <dsp:oparam name="default">
                    <dsp:input bean="B2BRepositoryFormHandler.value.invoiceRequestAuthorized" type="checkbox"/>
                  </dsp:oparam>
                  </dsp:droplet>
                  </td>
                </tr>
                <tr>
                  <td width=25% align="right"><span class=smallb>Kreditkartenautorisierung</span></td>
                  <td>
                  <% /*  check if inherited */ %>

                  <dsp:droplet name="IsEmpty">
                    <dsp:param bean="Profile.currentOrganization.myCreditCardAuthorized" name="value"/>
                    <dsp:oparam name="false">
                      <dsp:img src="../images/sethere.gif" align="left" hspace="6"/>
                    </dsp:oparam>
                    <dsp:oparam name="true">
                      <dsp:img src="../images/inherited.gif" align="left" hspace="6"/>
                    </dsp:oparam>  
                  </dsp:droplet>
                  <dsp:droplet name="Switch">
                  <dsp:param bean="Profile.currentOrganization.myCreditCardAuthorized" name="value"/>
                  <dsp:oparam name="true">
                    <dsp:input bean="B2BRepositoryFormHandler.value.creditCardAuthorized" type="checkbox" checked="<%=true%>"/>
                  </dsp:oparam>
                  <dsp:oparam name="default">
                    <dsp:input bean="B2BRepositoryFormHandler.value.creditCardAuthorized" type="checkbox"/>
                  </dsp:oparam>
                  </dsp:droplet>
                   
                  </td>
                </tr>
                <tr>
                   <td></td>
                   <td>
                   <dsp:input bean="B2BRepositoryFormHandler.update" type="submit" value=" Speichern "/>
                    &nbsp; 
                   <input type="submit" value=" Abbrechen " name="submit">

                </tr>
              </table>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/admin/payment_authorizations.jsp#2 $$Change: 651448 $--%>
