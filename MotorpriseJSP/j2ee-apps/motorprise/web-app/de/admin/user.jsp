<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/userdirectory/ProfileLookUp"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/TableForEach"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserUpdateFormHandler"/>

<DECLAREPARAM NAME="userId" CLASS="java.lang.String" DESCRIPTION="Determines whether page is for new user or not">

<dsp:setvalue bean="MultiUserUpdateFormHandler.clear" value=""/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Benutzerkonten-Info"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>

      <tr bgcolor="#DBDBDB" > 
        <td colspan=2 height=18> &nbsp; <span class=small>
        <dsp:a href="business_units.jsp">Unternehmensverwaltung</dsp:a> &gt; 
        <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
        <dsp:a href="users.jsp">Benutzer anzeigen</dsp:a> &gt; Benutzerkonto
      </tr>
  
 
  <tr valign=top> 
    <td width=55><img src="../images/d.gif"></td>

    <% /*  main content area */ %>
    <td valign="top" width=745>
    <table border=0 cellpadding=4 width=80%>
      <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>Unternehmensverwaltung</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
      </tr>

      <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Kontaktdaten</td></tr></table>
        </td>
      </tr>

      <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>
     
      <% /* Set the Profile.currentUser property to the user whose link was selected in the view users page */ %>

      <dsp:droplet name="IsEmpty">
        <dsp:param name="value" param="userId"/>
        <dsp:oparam name="false">
          <dsp:droplet name="ProfileLookUp">
            <dsp:param name="id" param="userId"/>
            <dsp:param name="elementName" value="user"/>
            <dsp:oparam name="output">
              <dsp:setvalue bean="Profile.currentUser" paramvalue="user"/>
            </dsp:oparam>
          </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
      
      <tr>
        <td align=right><span class=smallb>Name</span></td>
        <td width=75%><dsp:valueof bean="Profile.currentUser.firstName"/>
        <dsp:valueof bean="Profile.currentUser.middleName"/>
        <dsp:valueof bean="Profile.currentUser.lastName"/> </td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Anmeldung</span></td>
        <td width=75%><dsp:valueof bean="Profile.currentUser.login"/></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>E-Mail</span></td>
        <td><dsp:valueof bean="Profile.currentUser.email"/></td>
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb>Gesch‰ftsadresse</span></td>
        <td>
        <dsp:droplet name="IsEmpty">
          <dsp:param bean="Profile.currentUser.businessAddress" name="value"/>
          <dsp:oparam name="true">
          Keine Gesch‰ftsadresse fÅE Benutzer angegeben
          </dsp:oparam>
          <dsp:oparam name="false">
        <dsp:getvalueof id="pval0" bean="Profile.currentUser.businessAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include> </dsp:getvalueof>
        </dsp:oparam>
        </dsp:droplet>
        
        </td>
      </tr>

      <tr>
        <td align=right><span class=smallb>Telefon</span></td>
        <td><dsp:valueof bean="Profile.currentUser.defaultBillingAddress.phoneNumber"/></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Fax</span></td>
        <td><dsp:valueof bean="Profile.currentUser.defaultBillingAddress.faxNumber"/></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Sprache</span></td>
        <td>
          <dsp:droplet name="Switch">
            <dsp:param bean="Profile.currentUser.locale" name="value"/>
            <dsp:oparam name="en_US">
              Englisch
            </dsp:oparam> 
            <dsp:oparam name="de_DE">
              Deutsch
            </dsp:oparam> 
            <dsp:oparam name="ja_JP">
              Japaner
            </dsp:oparam> 
            <dsp:oparam name="default">
              <dsp:valueof bean="Profile.currentUser.locale">nicht festgelegt</dsp:valueof>
            </dsp:oparam>
          </dsp:droplet></td>
      </tr>
      <tr>
        <td></td>
        <td><span class=smallb><dsp:a href="user_edit.jsp">Bearbeiten
        <dsp:param bean="Profile.currentUser.id" name="userId"/>
        </dsp:a></span> |
        <span class=smallb><dsp:a href="user_password.jsp">Kennwort ‰ndern
        <dsp:param bean="Profile.currentUser.id" name="userId"/>
        </dsp:a></span></td>
      </tr>

      <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>
  
      <tr>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Unternehmensdaten</td></tr></table>
        </td>
      </tr>
      <tr>
        <td colspan=2><span class=small>
        <img src="../images/inherited.gif" align=left hspace=6>
        Diese Information ist im Unternehmensprofil Ihrer Firma angegeben.<br>
        <img src="../images/sethere.gif" align=left hspace=6 vspace=2>
        Diese Information betrifft den aktiven Benutzer.
        </span>
        </td>
      </tr>

      <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>
 
      <tr valign=top>
        <td align=right><span class=smallb>Gesch‰ftsbereich</span></td>
        <td><dsp:valueof bean="Profile.currentUser.parentOrganization.name">Keine Wertangabe</dsp:valueof></td>
      </tr>
      
      <tr valign=top>
        <td align=right><span class=smallb>Rolle(n)</span></td>
        <td>
          <dsp:droplet name="ForEach">
            <dsp:param bean="Profile.currentUser.roles" name="array"/>
            <dsp:param name="elementName" value="role"/>
            <dsp:oparam name="output">
              <dsp:valueof param="role.name"/> - 
              <dsp:valueof param="role.relativeTo.name"/>
               <BR>     
            </dsp:oparam>
          </dsp:droplet>
        </td>
      </tr>
            
      <tr valign=top>
        <td align=right>
        <span class=smallb>Versandanschrift(en)</span></td>

        <td><dsp:droplet name="IsEmpty">
          <dsp:param bean="Profile.currentUser.myShippingAddrs" name="value"/>
          <dsp:oparam name="false">
            <img src="../images/sethere.gif" align=left>
          </dsp:oparam>
          <dsp:oparam name="true">
            <img src="../images/inherited.gif" align=left>
          </dsp:oparam>  
        </dsp:droplet>

          <dsp:droplet name="TableForEach">
            <dsp:param bean="Profile.currentUser.shippingAddrs" name="array"/>
            <dsp:param name="elementName" value="shippingAddress"/>
            <dsp:param name="numColumns" value="2"/>

            <dsp:oparam name="outputStart">
              <table border=0 cellpadding=0 cellspacing=0 width=100%>
            </dsp:oparam>
            <dsp:oparam name="outputEnd"></table></dsp:oparam>
            <dsp:oparam name="outputRowStart"><tr valign="top"></dsp:oparam>
            <dsp:oparam name="outputRowEnd"></tr></dsp:oparam>
            <dsp:oparam name="output">
              <% /* check to see if address is blank */ %>
              <dsp:droplet name="IsEmpty">
                <dsp:param name="value" param="shippingAddress"/>
                <dsp:oparam name="true">
                  <td></td>
                </dsp:oparam>
                <dsp:oparam name="false">
                  <td>
                  <table border=0 cellpadding=0 cellspacing=0>
                    <tr>
                      <td>
                        <dsp:getvalueof id="pval0" idtype="atg.repository.RepositoryItem" param="shippingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                      </td>
                    </tr>
                    <tr><td><img src="../images/d.gif" vspace=3></td></tr>
                  </table>
                  </td>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>
        </td>
      </tr>

     <tr valign=top>
        <td align=right><span class=smallb>Standard-Versandanschrift</span></td>
        <td>
        <dsp:droplet name="IsEmpty">
          <dsp:param bean="Profile.currentUser.myDefaultShippingAddress" name="value"/>
          <dsp:oparam name="false">
            <img src="../images/sethere.gif" align=left>
          </dsp:oparam>
          <dsp:oparam name="true">
            <img src="../images/inherited.gif" align=left>
          </dsp:oparam>  
        </dsp:droplet>

           <dsp:droplet name="IsEmpty">
            <dsp:param bean="Profile.currentUser.defaultShippingAddress" name="value"/>
            <dsp:oparam name="false">
             <dsp:getvalueof id="pval0" idtype="atg.repository.RepositoryItem" bean="Profile.currentUser.defaultShippingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
            </dsp:oparam>
           </dsp:droplet>
        </td>
       </tr>

       <tr valign=top>
         <td align=right><span class=smallb>Rechnungsanschrift(en)</span></td>
         <td>
         <dsp:droplet name="IsEmpty">
           <dsp:param bean="Profile.currentUser.myBillingAddrs" name="value"/>
           <dsp:oparam name="false">
             <img src="../images/sethere.gif" align=left>
           </dsp:oparam>
           <dsp:oparam name="true">
             <img src="../images/inherited.gif" align=left>
           </dsp:oparam>  
         </dsp:droplet>

          <dsp:droplet name="TableForEach">
            <dsp:param bean="Profile.currentUser.billingAddrs" name="array"/>
            <dsp:param name="elementName" value="billingAddress"/>
            <dsp:param name="numColumns" value="2"/>
            <dsp:oparam name="outputStart">
              <table border=0 cellpadding=0 cellspacing=0 width=100%>
            </dsp:oparam>
            <dsp:oparam name="outputEnd"></table></dsp:oparam>
            <dsp:oparam name="outputRowStart"><tr valign="top"></dsp:oparam>
            <dsp:oparam name="outputRowEnd"></tr></dsp:oparam>
            <dsp:oparam name="output">
              <% /* check to see if address is blank */ %>
              <dsp:droplet name="IsEmpty">
                <dsp:param name="value" param="billingAddress"/>
                <dsp:oparam name="true">
                  <td></td>
                </dsp:oparam>
                <dsp:oparam name="false">
                  <td>
                  <table border=0 cellpadding=0 cellspacing=0>
                    <tr>
                      <td>
                        <dsp:getvalueof id="pval0" idtype="atg.repository.RepositoryItem" param="billingAddress">
			<dsp:include page="../common/DisplayAddress.jsp">
			  <dsp:param name="address" value="<%=pval0%>"/>
			</dsp:include>
			</dsp:getvalueof>
                      </td>
                    </tr>
                    <tr><td><img src="../images/d.gif" vspace=3></td></tr>
                  </table>
                  </td>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>
        </td>
      </tr>

      <% /* Display the default billing address only if the invoiceRequestAuthorized property is true */ %>

      <dsp:droplet name="Switch">
        <dsp:param bean="Profile.currentUser.invoiceRequestAuthorized" name="value"/>
        <dsp:oparam name="true">
           <tr valign=top>
             <td align=right><span class=smallb>Standard-Rechnungsanschrift</span></td>
             <td>
             <dsp:droplet name="IsEmpty">
               <dsp:param bean="Profile.currentUser.myDefaultBillingAddress" name="value"/>
               <dsp:oparam name="false">
                 <img src="../images/sethere.gif" align=left>
               </dsp:oparam>
               <dsp:oparam name="true">
                 <img src="../images/inherited.gif" align=left>
               </dsp:oparam>  
             </dsp:droplet>
             <dsp:droplet name="IsEmpty">
               <dsp:param bean="Profile.currentUser.defaultBillingAddress" name="value"/>
               <dsp:oparam name="false">
                 <dsp:getvalueof id="pval0"  idtype="atg.repository.RepositoryItem" bean="Profile.currentUser.defaultBillingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
               </dsp:oparam>
               <dsp:oparam name="true">
                 n/a
               </dsp:oparam>
             </dsp:droplet>
       </td>
      </tr>

      </dsp:oparam>
      </dsp:droplet>

      <tr valign=top>
        <td align=right>
        
      <dsp:droplet name="Switch">
      <dsp:param bean="Profile.currentUser.creditCardAuthorized" name="value"/>
      <dsp:oparam name="true">  
        <span class=smallb>Kreditkarte(n)</span></td>
        <td><dsp:droplet name="IsEmpty">
          <dsp:param bean="Profile.currentUser.myPaymentTypes" name="value"/>
          <dsp:oparam name="false">
            <img src="../images/sethere.gif" align=left>
          </dsp:oparam>
          <dsp:oparam name="true">
            <img src="../images/inherited.gif" align=left>
          </dsp:oparam>  
        </dsp:droplet>
      

        <dsp:droplet name="ForEach">
           <dsp:param bean="Profile.currentUser.paymentTypes" name="array"/>
           <dsp:param name="elementName" value="paymentType"/>
            <dsp:oparam name="outputStart">
              <table border=0 cellpadding=0 cellspacing=0><tr><td>
            </dsp:oparam>
            <dsp:oparam name="outputEnd">
              </td></tr></table>
            </dsp:oparam>
           <dsp:oparam name="output">
             <dsp:valueof param="key"/> <br>
           </dsp:oparam>
           <dsp:oparam name="empty">
              n/a
           </dsp:oparam>
        </dsp:droplet>
     </dsp:oparam>
     </dsp:droplet>

        </td>
      </tr>

      <tr valign=top>
        <td align=right>
        <span class=smallb>Kreditkartenautorisierung </span></td>
        <td><dsp:droplet name="IsEmpty">
          <dsp:param bean="Profile.currentUser.myCreditCardAuthorized" name="value"/>
          <dsp:oparam name="false">
            <img src="../images/sethere.gif" align=left>
          </dsp:oparam>
          <dsp:oparam name="true">
            <img src="../images/inherited.gif" align=left>
          </dsp:oparam>  
        </dsp:droplet>

        <dsp:droplet name="Switch">
           <dsp:param bean="Profile.currentUser.creditCardAuthorized" name="value"/>
           <dsp:oparam name="true">
              Die Verwendung von Kreditkarten ist fÅE diesen Benutzer autorisiert.
           </dsp:oparam>
           <dsp:oparam name="false">
              Die Verwendung von Kreditkarten ist fÅE diesen Benutzer nicht autorisiert.
           </dsp:oparam>
        </dsp:droplet>
  
        </td>
      </tr>

      <tr valign=top>
        <td align=right>
        <span class=smallb>Rechnungsautorisierung </span></td>
        <td><dsp:droplet name="IsEmpty">
          <dsp:param bean="Profile.currentUser.myCreditCardAuthorized" name="value"/>
          <dsp:oparam name="false">
            <img src="../images/sethere.gif" align=left>
          </dsp:oparam>
          <dsp:oparam name="true">
            <img src="../images/inherited.gif" align=left>
          </dsp:oparam>  
        </dsp:droplet>
      
        <dsp:droplet name="Switch">
           <dsp:param bean="Profile.currentUser.invoiceRequestAuthorized" name="value"/>
           <dsp:oparam name="true">
              Die Verwendung von Rechnungen ist fÅE diesen Benutzer autorisiert.
           </dsp:oparam>
           <dsp:oparam name="false">
              Die Verwendung von Rechnungen ist fÅE diesen Benutzer nicht autorisiert.
           </dsp:oparam>
        </dsp:droplet>
  
        </td>
      </tr>




      <tr valign=top>
        <td align=right><span class=smallb>Kostenstelle(n)</span></td>
              
        <td>
        <dsp:droplet name="IsEmpty">
          <dsp:param bean="Profile.currentUser.myCostCenters" name="value"/>
          <dsp:oparam name="false">
            <img src="../images/sethere.gif" align=left>
          </dsp:oparam>
          <dsp:oparam name="true">
            <img src="../images/inherited.gif" align=left>
          </dsp:oparam>  
        </dsp:droplet>
          <dsp:droplet name="ForEach">
            <dsp:param bean="Profile.currentUser.costCenters" name="array"/>
            <dsp:param name="elementName" value="costCenter"/>
            <dsp:oparam name="outputStart">
              <table border=0 cellpadding=0 cellspacing=0><tr><td>
            </dsp:oparam>
            <dsp:oparam name="outputEnd">
              </td></tr></table>
            </dsp:oparam>
            <dsp:oparam name="output">
              <dsp:valueof param="costCenter.identifier"/> -
              <dsp:valueof param="costCenter.description"/> <br>
            </dsp:oparam>
            <dsp:oparam name="empty">
              n/a
            </dsp:oparam>
          </dsp:droplet>  
        </td>
      </tr>
      <tr valign=top>
        <td align=right>
          <span class=smallb>Standard-Kostenstelle</span>
        </td>
              
        <td>
        <dsp:droplet name="IsEmpty">
          <dsp:param bean="Profile.currentUser.myDefaultCostCenter" name="value"/>
          <dsp:oparam name="false">
            <img src="../images/sethere.gif" align=left>
          </dsp:oparam>
          <dsp:oparam name="true">
            <img src="../images/inherited.gif" align=left>
          </dsp:oparam>  
        </dsp:droplet>
            <dsp:droplet name="IsEmpty">
            <dsp:param bean="Profile.currentUser.defaultCostCenter" name="value"/>
            <dsp:oparam name="true">
              n/a
            </dsp:oparam>
            <dsp:oparam name="false">
              <dsp:valueof bean="Profile.currentUser.defaultCostCenter.identifier"/> -
              <dsp:valueof bean="Profile.currentUser.defaultCostCenter.description"/>
            </dsp:oparam>
            </dsp:droplet>
        </td>
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb><font color=red></font>Bewilligungen</span></td>
        <td>
        <dsp:droplet name="IsEmpty">
          <dsp:param bean="Profile.currentUser.myApprovalRequired" name="value"/>
          <dsp:oparam name="false">
            <img src="../images/sethere.gif" align=left>
          </dsp:oparam>
          <dsp:oparam name="true">
            <img src="../images/inherited.gif" align=left>
          </dsp:oparam>  
        </dsp:droplet>
        <dsp:droplet name="Switch">
          <dsp:param bean="Profile.currentUser.approvalRequired" name="value"/>
          <dsp:oparam name="true">
            Bewilligungen fÅE alle K‰ufe anfordern, die ÅEer dem Einkaufslimit liegen.
          </dsp:oparam>
          <dsp:oparam name="false">
            Nicht erforderlich
           </dsp:oparam>
        </dsp:droplet>
        </td>
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb>Einkaufslimit</span></td>
        <td><dsp:droplet name="IsEmpty">
          <dsp:param bean="Profile.currentUser.myorderPriceLimit" name="value"/>
          <dsp:oparam name="false">
            <img src="../images/sethere.gif" align=left>
          </dsp:oparam>
          <dsp:oparam name="true">
            <img src="../images/inherited.gif" align=left>
          </dsp:oparam>  
        </dsp:droplet>
        <dsp:getvalueof id="pval0" bean="Profile.currentUser.orderPriceLimit"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof> 
         </td>
      </tr>
      

      <tr valign=top>
        <td align=right>&nbsp;</td>
        <td><span class=smallb><dsp:a href="user_edit_info.jsp">Bearbeiten
        <dsp:param bean="Profile.currentUser.id" name="userId"/>
        </dsp:a></span> |
        <span class=smallb><dsp:a href="user_edit_defaults.jsp">Standardvorgaben fÅE Benutzer festlegen
        <dsp:param bean="Profile.currentUser.id" name="userId"/>
        </dsp:a></span> </td>
      </tr>
     
      <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>
    </table>
    </td>
  </tr>

</table>
</div>
</body>
</html>
<%/* Version: $Change: 651448 $$DateTime: 2011/06/07 13:55:45 $*/%>


</dsp:page>
<%-- Version: $Change: 651448 $$DateTime: 2011/06/07 13:55:45 $--%>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/admin/user.jsp#2 $$Change: 651448 $--%>
