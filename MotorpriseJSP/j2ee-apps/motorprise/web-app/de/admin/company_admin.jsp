<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/userdirectory/droplet/TargetPrincipals"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserUpdateFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserAddFormHandler"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Unternehmensverwaltung"/></dsp:include>

<%-- Clear the values of these formhandlers since they are session scoped --%>

<dsp:setvalue bean="MultiUserUpdateFormHandler.clear" value=""/>
<dsp:setvalue bean="MultiUserAddFormHandler.clear" value=""/>

<%-- Set the Profile.currentOrganization property to the organization which was selected in the business_units.jsp page --%>

<dsp:droplet name="IsEmpty">
  <dsp:param name="value" param="organizationId"/>
  <dsp:oparam name="false">
    <dsp:droplet name="TargetPrincipals">
	  <dsp:param bean="Profile.id" name="userId"/>
	  <dsp:param name="roleName" value="admin"/>
	  <dsp:oparam name="output">
        <dsp:droplet name="ForEach">
        <dsp:param name="array" param="principals"/>
        <dsp:oparam name="output">
	    <dsp:droplet name="Compare">
	       <dsp:param name="obj1" param="organizationId"/>
  	       <dsp:param name="obj2" param="element.repositoryItem.repositoryid"/>
	  	<dsp:oparam name="equal"> 
		  <dsp:setvalue bean="Profile.currentOrganization" paramvalue="element.repositoryItem"/>
   
		</dsp:oparam>
	    </dsp:droplet>
	
          </dsp:oparam>
        </dsp:droplet>  <%-- End of ForEach --%>
      </dsp:oparam>
    </dsp:droplet>  <%-- End of TargetPricipals --%>
  </dsp:oparam>
</dsp:droplet> <%-- End of IsEmpty --%>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Unternehmensverwaltung</dsp:a> &gt;
  <dsp:valueof bean="Profile.currentOrganization.name"/></span></td>
  </tr>

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  
    
    <table border=0 cellpadding=4 width=100%>
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr valign=top>
        <td><span class=big>Unternehmensverwaltung</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
      </tr>
    
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr valign=top>
        <td>
        <table width=80% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Benutzer</td></tr></table>
        </td>
      </tr>
      <tr>
        <td><dsp:a href="users.jsp">Benutzer anzeigen
            <dsp:param name="startIndex" value="1"/>
            </dsp:a><br>
            <dsp:a href="new_user.jsp">Neuen Benutzer erstellen</dsp:a><br>
            <dsp:a href="create_multiple_users.jsp">Mehrere Benutzer erstellen</dsp:a><br>
            <dsp:a href="multiple_user_edit.jsp">Mehrere Benutzer bearbeiten</dsp:a><br>
            <dsp:a href="users_delete.jsp">Benutzer löschen
            <dsp:param name="startIndex" value="1"/>
            </dsp:a>
        </td>      
      </tr>
      
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr valign=top>
        <td>
        <table width=80% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Firmeninfo</td></tr></table>
        </td>
      </tr>
      <tr>
        <td>
          <dsp:a href="shipping_edit.jsp">Versandanschriften</dsp:a><br>

          <dsp:droplet name="IsEmpty">
            <dsp:param bean="Profile.shippingAddrs" name="value"/>
            <dsp:oparam name="false">
              <dsp:a href="default_shipping_edit.jsp">Standard-Versandanschrift </dsp:a><br>
            </dsp:oparam>
          </dsp:droplet>

          <dsp:a href="billing_addresses.jsp">Rechnungsanschriften</dsp:a><br>
          
          <%-- Display the Default billing address only if the invoiceRequestAuthorized is true for the current user --%>
  
          <dsp:droplet name="Switch">
            <dsp:param bean="Profile.invoiceRequestAuthorized" name="value"/>
            <dsp:oparam name="true">
              <dsp:droplet name="IsEmpty">
                <dsp:param bean="Profile.billingAddrs" name="value"/>
                <dsp:oparam name="output">
                  <dsp:a href="default_billing_edit.jsp">Standard-Rechnungsanschrift </dsp:a><br>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>

          <dsp:droplet name="Switch">
            <dsp:param bean="Profile.creditCardAuthorized" name="value"/>
            <dsp:oparam name="true">
               <dsp:a href="payment_edit.jsp">Kreditkarten</dsp:a><br>
            </dsp:oparam>
          </dsp:droplet>
          <dsp:a href="cost_centers.jsp">Kostenstellen</dsp:a><br>
          <dsp:a href="purchase_limit_edit.jsp">Bewilligungen</dsp:a><br>   
          <dsp:a href="payment_authorizations.jsp">Zahlungsweisen autorisieren</dsp:a>
        </td>      
      </tr>
    </table>
    </td>
  </tr>
</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/admin/company_admin.jsp#2 $$Change: 651448 $--%>
