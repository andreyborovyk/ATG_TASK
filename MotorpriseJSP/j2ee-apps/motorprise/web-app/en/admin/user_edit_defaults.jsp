<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/TableForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserUpdateFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ArrayIncludesValue"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Edit User Defaults"/></dsp:include>
<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>

   <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Company Admin</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
    <dsp:a href="user.jsp">User Account</dsp:a> &gt; Edit User Defaults
    </span></td>
  </tr>

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>

    <!-- main content area -->
    <td  valign="top" width=745>
    <dsp:form action="user.jsp" method="post">
    <dsp:input bean="MultiUserUpdateFormHandler.repositoryIds" paramvalue="userId" type="hidden"/>  
    <input name="userId" type="hidden" value="<dsp:valueof param="userId"/>">
    <table border=0 cellpadding=4 width=80%>
      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>Company Administration</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
      </tr>

      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr>
        <td colspan=2>
     <dsp:include page="../common/FormError.jsp"></dsp:include></td>
      </tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Edit User Defaults</td></tr></table>
        </td>
      </tr>


      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr>
        <td colspan=2><span class=small>Select defaults for this user.</span></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Name</span></td>
        <td width=75%><dsp:valueof bean="Profile.currentUser.firstName"/>&nbsp;<dsp:valueof bean="Profile.currentUser.lastName"/></td>
      </tr>


      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr valign=top>
        <td align=right><span class=smallb>Default cost center</span></td>
        <td>
        <dsp:droplet name="IsEmpty">
          <dsp:param bean="Profile.costCenters" name="value"/>
          <dsp:oparam name="true">
            No cost centers are available.
          </dsp:oparam>
          <dsp:oparam name="false">
            <dsp:getvalueof id="costCenters" bean="Profile.costcenters"> 
            <dsp:getvalueof id="defaultCostCenterId" idtype="java.lang.String" bean="Profile.currentUser.defaultCostCenter.repositoryId">
            <dsp:select bean="MultiUserUpdateFormHandler.value.defaultCostCenter.repositoryId">

              <dsp:droplet name="ForEach">
                <dsp:param value="<%=costCenters%>" name="array" />
                <dsp:oparam name="output">
               
                
                <dsp:getvalueof id="costCenterId" idtype="java.lang.String" param="element.repositoryId">  
                  <dsp:droplet name="Switch">
                    <dsp:param value="costCenterId" name="value"/>
                    <dsp:oparam name="<%=defaultCostCenterId%>">
                      <dsp:option selected="<%=true%>" value="<%=costCenterId%>"/> 
                      <dsp:valueof param="element.identifier"/> - <dsp:valueof  param="element.description"/>
                    </dsp:oparam>
                    <dsp:oparam name="default">
                      <dsp:option value="<%=costCenterId%>"/> 
                      <dsp:valueof param="element.identifier"/> - <dsp:valueof  param="element.description"/>
                    </dsp:oparam>
                  </dsp:droplet>
                </dsp:getvalueof>

                </dsp:oparam>
              </dsp:droplet>

            </dsp:select>
            </dsp:getvalueof>
            </dsp:getvalueof>
          </dsp:oparam>
        </dsp:droplet></td>

      </tr>
      
      <tr valign=top>
        <td align=right><span class=smallb>Default shipping address</span></td>
        <td>
        <!--table to format shipping addresses-->
        <dsp:droplet name="TableForEach">
          <dsp:param bean="Profile.currentOrganization.shippingAddrs" name="array"/>
          <dsp:param name="numColumns" value="2"/>
          <dsp:oparam name="outputStart"><table border=0 cellpadding=3 width=100%></dsp:oparam>
          <dsp:oparam name="outputEnd"></table></dsp:oparam>
          <dsp:oparam name="outputRowStart"><tr valign="top"></dsp:oparam>
          <dsp:oparam name="outputRowEnd"></tr></dsp:oparam>
          <dsp:oparam name="output">
            <!--check to see if address is blank-->
            <dsp:droplet name="IsEmpty">
              <dsp:param name="value" param="element"/>
              <dsp:oparam name="true">
                <td></td>
              </dsp:oparam>
              <dsp:oparam name="false">
                <td>
                <table border=0>
                  <tr valign=top>
                    <td>
                    
                    <dsp:getvalueof id="defaultShippingId"  idtype="java.lang.String" bean="Profile.currentUser.defaultShippingAddress.repositoryId"> 
                    <dsp:getvalueof id="shippingId" idtype="java.lang.String" param="element.repositoryId"> 

                      <dsp:droplet name="Switch">
                        <dsp:param name="value" value="<%=shippingId%>"/>
                        <dsp:oparam name="<%=defaultShippingId%>">
      <dsp:input bean="MultiUserUpdateFormHandler.value.defaultShippingAddress.repositoryid" paramvalue="element.repositoryid" type="radio" checked="<%=true%>"/>
            </dsp:oparam>
                        <dsp:oparam name="default">
      <dsp:input bean="MultiUserUpdateFormHandler.value.defaultShippingAddress.repositoryid" paramvalue="element.repositoryid" type="radio"/>
      </dsp:oparam>
                      </dsp:droplet>

                    </dsp:getvalueof>
                    </dsp:getvalueof>

                    </td>
                    <td>
                     <dsp:getvalueof id="pval1" idtype="atg.repository.RepositoryItem"  param="element">
                      <dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval1%>"/></dsp:include>
                      </dsp:getvalueof>
                    </td>
                  </tr>
                </table>
                </td>
              </dsp:oparam>
            </dsp:droplet><!--end IsEmpty-->
          </dsp:oparam>
        </dsp:droplet><!--end TableForEach-->
        </td>
      </tr>
      

      <dsp:droplet name="Switch">
      <dsp:param bean="Profile.currentUser.invoiceRequestAuthorized" name="value"/>
      <dsp:oparam name="true">
      
     
       <tr valign=top>
        <td align=right><span class=smallb>Default billing address</span></td>
        <td>
        <!--table to format billing addresses-->
        <dsp:droplet name="TableForEach">
          <dsp:param bean="Profile.currentOrganization.billingAddrs" name="array"/>
          <dsp:param name="numColumns" value="2"/>
          <dsp:oparam name="outputStart"><table border=0 width=100%></dsp:oparam>
          <dsp:oparam name="outputEnd"></table></dsp:oparam>
          <dsp:oparam name="outputRowStart"><tr valign="top"></dsp:oparam>
          <dsp:oparam name="outputRowEnd"></tr></dsp:oparam>
          <dsp:oparam name="output">
            <!--check to see if address is blank-->
            <dsp:droplet name="IsEmpty">
              <dsp:param name="value" param="element"/>
              <dsp:oparam name="true">
                <td></td>
              </dsp:oparam>
              <dsp:oparam name="false">
                <td>
                <table border=0 cellpadding=3>
                  <tr valign=top>
                    <td>

                    <dsp:getvalueof id="defaultBillingAddressId" idtype="java.lang.String" bean="Profile.currentUser.defaultBillingAddress.repositoryId">
                    
                      <dsp:droplet name="Switch">
                        <dsp:param param="element.repositoryid" name="value" />
                        <dsp:oparam name="<%=defaultBillingAddressId%>">
                          <dsp:input bean="MultiUserUpdateFormHandler.value.defaultBillingAddress.repositoryid" paramvalue="element.repositoryid" checked="<%=true%>" type="radio"/>
                          
                           </dsp:oparam>
                         <dsp:oparam name="default">
                                  <dsp:input bean="MultiUserUpdateFormHandler.value.defaultBillingAddress.repositoryid" paramvalue="element.repositoryid" type="radio"/>
                         </dsp:oparam>
                      </dsp:droplet>

                   </dsp:getvalueof>
                    </td>
                    <td>
                      <dsp:getvalueof id="pval0" idtype="atg.repository.RepositoryItem" param="element">
                      <dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                    </td>
                  </tr>
                </table>
                </td>
              </dsp:oparam>
            </dsp:droplet><!--end IsEmpty-->
          </dsp:oparam>
        </dsp:droplet><!--end TableForEach-->
        </td>
      </tr>
       
      </dsp:oparam>
      </dsp:droplet> <!-- end of Switch -->


      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="4"/></td></tr>
      <tr>
        <td><dsp:img src="../images/d.gif"/></td>
        <td>
       <dsp:input bean="MultiUserUpdateFormHandler.update" type="submit" value=" Save "/>
       <input type="submit" value="Cancel">
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/user_edit_defaults.jsp#2 $$Change: 651448 $--%>
