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

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  User Account Info"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>

      <tr bgcolor="#DBDBDB" > 
        <td colspan=2 height=18> &nbsp; <span class=small>
        <dsp:a href="business_units.jsp">Company Admin</dsp:a> &gt; 
        <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
        <dsp:a href="users.jsp">View Users</dsp:a> &gt; User Account
      </tr>
  
 
  <tr valign=top> 
    <td width=55><img src="../images/d.gif"></td>

    <% /*  main content area */ %>
    <td valign="top" width=745>
    <table border=0 cellpadding=4 width=80%>
      <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>Company Administration</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
      </tr>

      <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Contact Information</td></tr></table>
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
        <td align=right><span class=smallb>Login</span></td>
        <td width=75%><dsp:valueof bean="Profile.currentUser.login"/></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Email</span></td>
        <td><dsp:valueof bean="Profile.currentUser.email"/></td>
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb>Business address</span></td>
        <td>
        <dsp:droplet name="IsEmpty">
          <dsp:param bean="Profile.currentUser.businessAddress" name="value"/>
          <dsp:oparam name="true">
          User has no specified business address.
          </dsp:oparam>
          <dsp:oparam name="false">
        <dsp:getvalueof id="pval0" bean="Profile.currentUser.businessAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include> </dsp:getvalueof>
        </dsp:oparam>
        </dsp:droplet>
        
        </td>
      </tr>

      <tr>
        <td align=right><span class=smallb>Phone</span></td>
        <td><dsp:valueof bean="Profile.currentUser.defaultBillingAddress.phoneNumber"/></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Fax</span></td>
        <td><dsp:valueof bean="Profile.currentUser.defaultBillingAddress.faxNumber"/></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Language</span></td>
        <td>
          <dsp:droplet name="Switch">
            <dsp:param bean="Profile.currentUser.locale" name="value"/>
            <dsp:oparam name="en_US">
              English
            </dsp:oparam> 
            <dsp:oparam name="de_DE">
              German
            </dsp:oparam> 
            <dsp:oparam name="ja_JP">
              Japanese
            </dsp:oparam> 
            <dsp:oparam name="default">
              <dsp:valueof bean="Profile.currentUser.locale">not set</dsp:valueof>
            </dsp:oparam>
          </dsp:droplet></td>
      </tr>
      <tr>
        <td></td>
        <td><span class=smallb><dsp:a href="user_edit.jsp">Edit
        <dsp:param bean="Profile.currentUser.id" name="userId"/>
        </dsp:a></span> |
        <span class=smallb><dsp:a href="user_password.jsp">Change password
        <dsp:param bean="Profile.currentUser.id" name="userId"/>
        </dsp:a></span></td>
      </tr>

      <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>
  
      <tr>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Company Information</td></tr></table>
        </td>
      </tr>
      <tr>
        <td colspan=2><span class=small>
        <img src="../images/inherited.gif" align=left hspace=6>
        This information is specified in your company's corporate profile.<br>
        <img src="../images/sethere.gif" align=left hspace=6 vspace=2>
        This information is specific to the current user.
        </span>
        </td>
      </tr>

      <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>
 
      <tr valign=top>
        <td align=right><span class=smallb>Business unit</span></td>
        <td><dsp:valueof bean="Profile.currentUser.parentOrganization.name">No value</dsp:valueof></td>
      </tr>
      
      <tr valign=top>
        <td align=right><span class=smallb>Role(s)</span></td>
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
        <span class=smallb>Shipping address(es)</span></td>

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
        <td align=right><span class=smallb>Default shipping address</span></td>
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
         <td align=right><span class=smallb>Billing address(es)</span></td>
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
             <td align=right><span class=smallb>Default billing address</span></td>
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
                 N/A
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
        <span class=smallb>Credit Card(s)</span></td>
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
              N/A
           </dsp:oparam>
        </dsp:droplet>
     </dsp:oparam>
     </dsp:droplet>

        </td>
      </tr>

      <tr valign=top>
        <td align=right>
        <span class=smallb>Credit card authorization </span></td>
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
              Credit cards are authorized for this user.
           </dsp:oparam>
           <dsp:oparam name="false">
              Credit cards are not authorized for this user.
           </dsp:oparam>
        </dsp:droplet>
  
        </td>
      </tr>

      <tr valign=top>
        <td align=right>
        <span class=smallb>Invoice authorization </span></td>
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
              Invoices are authorized for this user.
           </dsp:oparam>
           <dsp:oparam name="false">
              Invoices are not authorized for this user.
           </dsp:oparam>
        </dsp:droplet>
  
        </td>
      </tr>




      <tr valign=top>
        <td align=right><span class=smallb>Cost center(s)</span></td>
              
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
              N/A
            </dsp:oparam>
          </dsp:droplet>  
        </td>
      </tr>
      <tr valign=top>
        <td align=right>
          <span class=smallb>Default cost center</span>
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
              N/A
            </dsp:oparam>
            <dsp:oparam name="false">
              <dsp:valueof bean="Profile.currentUser.defaultCostCenter.identifier"/> -
              <dsp:valueof bean="Profile.currentUser.defaultCostCenter.description"/>
            </dsp:oparam>
            </dsp:droplet>
        </td>
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb><font color=red></font>Approvals</span></td>
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
            Require approval for all purchases above purchase limit.
          </dsp:oparam>
          <dsp:oparam name="false">
            Not required
           </dsp:oparam>
        </dsp:droplet>
        </td>
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb>Purchase limit</span></td>
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
        <td><span class=smallb><dsp:a href="user_edit_info.jsp">Edit
        <dsp:param bean="Profile.currentUser.id" name="userId"/>
        </dsp:a></span> |
        <span class=smallb><dsp:a href="user_edit_defaults.jsp">Set user defaults
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/user.jsp#2 $$Change: 651448 $--%>
