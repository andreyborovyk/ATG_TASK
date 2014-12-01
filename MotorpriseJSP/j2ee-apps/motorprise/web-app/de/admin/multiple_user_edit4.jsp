<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserUpdateFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/TableForEach"/>
<dsp:importbean bean="/atg/projects/b2bstore/userdirectory/ProfileLookUp"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Mehrere Benutzer bearbeiten"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
        <dsp:a href="company_admin.jsp">Unternehmensverwaltung</dsp:a> &gt; 
        Mehrere Benutzer bearbeiten</span></td>
  </tr>
  <tr valign=top> 
    <td width=55></td>

    <%--  main content area --%>
    <td valign="top" width=745>
    <table border=0 cellpadding=4 width=80%>
      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>Unternehmensverwaltung</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
      </tr>

      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Mehrere Benutzer bearbeiten</td></tr></table>
        </td>
      </tr>

      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr>
        <td><b>Für die folgenden Benutzer</b>
        <blockquote>
        <dsp:droplet name="ForEach">
        <dsp:param bean="MultiUserUpdateFormHandler.repositoryIds" name="array"/>
        <dsp:param name="elementName" value="userId"/>
        <dsp:oparam name="output">
        <dsp:droplet name="ProfileLookUp">
    <dsp:param name="id" param="userId"/>
    <dsp:param name="elementName" value="user"/>
    <dsp:oparam name="output">
      <dsp:a href="user.jsp"><dsp:valueof param="user.firstName"/>
  
      <dsp:valueof param="user.lastName"/>
      <dsp:param name="userId" param="user.id"/>
      </dsp:a><br>
               </dsp:oparam>
               </dsp:droplet>
         </dsp:oparam>
         </dsp:droplet>
       
        </blockquote></td>
      </tr>
      <tr>
        <td><b>Die folgenden Attribute wurden auf die aufgeführten Werte gesetzt</b>
        <blockquote>
        <table border=0 cellpadding=4 cellspacing=0>
        
       
        <dsp:droplet name="IsEmpty">
        <dsp:param name="value" param="userRoles"/>
        <dsp:oparam name="false">
        
          <tr valign=top> 
            <td align=right><span class=smallb>Rollen</span></td>
            <td>
         <dsp:droplet name="ForEach">
          <dsp:param bean="Profile.currentUser.roles" name="array"/>
        <dsp:param name="elementName" value="role"/>
        <dsp:oparam name="output">
         <dsp:valueof param="role.name"/><BR>
        </dsp:oparam>
        </dsp:droplet>    
        
          </td>
          </tr> 
          </dsp:oparam>
          </dsp:droplet>
          
          
        <dsp:droplet name="IsEmpty">
        <dsp:param name="value" param="userShippingAddresses"/>
        <dsp:oparam name="false">
         <tr valign=top>
        <td align=right><span class=smallb>Versandanschrift(en)</span></td>
        <td>          
 
         
            <dsp:droplet name="TableForEach">
              <dsp:param bean="Profile.currentUser.shippingAddrs" name="array"/>
              <dsp:param name="elementName" value="shippingAddress"/>
              <dsp:param name="numColumns" value="2"/>
              <dsp:oparam name="outputStart">
                <table border=0 cellpadding=6 cellspacing=0>
              </dsp:oparam>
              <dsp:oparam name="outputEnd"></table></dsp:oparam>
              <dsp:oparam name="outputRowStart"><tr></dsp:oparam>
              <dsp:oparam name="outputRowEnd"></tr></dsp:oparam>
              <dsp:oparam name="output">
                <dsp:droplet name="IsEmpty">
                  <dsp:param name="value" param="shippingAddress"/>
                  <dsp:oparam name="false">
                    <td>
                    <dsp:getvalueof id="pval0" param="shippingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                    </td>
                   </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
         
         

        </td>
        </tr>
        </dsp:oparam>
        </dsp:droplet>
         
         
   
        <dsp:droplet name="IsEmpty">
        <dsp:param name="value" param="shippingMethods"/>
        <dsp:oparam name="false">
        <tr valign=top>
        <td align=right><span class=smallb>Versandart(en)</span></td>
        <td>
        
        <dsp:droplet name="ForEach">
           <dsp:param bean="MultiUserUpdateFormHandler.value.shippingTypes" name="array"/>
           <dsp:param name="elementName" value="shippingType"/>
           <dsp:oparam name="output">
                   <dsp:valueof param="shippingType"> n/a </dsp:valueof> <br>
           </dsp:oparam>
        </dsp:droplet>
        
        </td>
      </tr>
      </dsp:oparam>
      </dsp:droplet>   
   
         
         
        <dsp:droplet name="IsEmpty">
        <dsp:param name="value" param="userPaymentTypes"/>
        <dsp:oparam name="false">  
        <tr valign=top>
        <td align=right><span class=smallb>Kreditkarte(n)</span></td>
        <td>
        <dsp:droplet name="ForEach">
           <dsp:param bean="Profile.currentUser.paymentTypes" name="array"/>
           <dsp:param name="elementName" value="paymentType"/>
           <dsp:oparam name="output">
                   <dsp:valueof param="key"> n/a </dsp:valueof> <br>
           </dsp:oparam>
        </dsp:droplet>
            
        </td>
      </tr>
       </dsp:oparam>
      </dsp:droplet>   
           
       
        <dsp:droplet name="IsEmpty">
        <dsp:param name="value" param="userCostCenters"/>
        <dsp:oparam name="false">  
        <tr valign=top>
        <td align=right><span class=smallb>Kostenstelle(n)</span></td>
        <td>
        <dsp:droplet name="ForEach">
           <dsp:param bean="Profile.currentUser.costCenters" name="array"/>
           <dsp:param name="elementName" value="costCenter"/>
           <dsp:oparam name="output">
                   <dsp:valueof param="costCenter.description"> n/a </dsp:valueof> <br>
           </dsp:oparam>
        </dsp:droplet>
        
      </td>
      </tr>  
      </dsp:oparam>
      </dsp:droplet>   
      
      <dsp:droplet name="IsEmpty">
        <dsp:param name="value" param="userOrderPriceLimit"/>
        <dsp:oparam name="false">  
      <tr valign=top>
        <td align=right>
          <span class=smallb>Einkaufslimit</span></td>
        <td>
          <dsp:valueof bean="Profile.currentUser.orderPriceLimit" converter="currency" locale="Profile.priceList.locale"/>
        </td>
      </tr>
      </dsp:oparam>
      </dsp:droplet>    
         
         </table>
        </blockquote></td>
         
          </tr>
        </table>
 



        
          
        </td>      
      </tr>
  
    </table>

    </td>
  </tr>

</table>
</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/admin/multiple_user_edit4.jsp#2 $$Change: 651448 $--%>
