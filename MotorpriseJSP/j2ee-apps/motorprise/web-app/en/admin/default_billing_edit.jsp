<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/TableForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ArrayIncludesValue"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Select Billing Address"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Company Admin</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
         Select Default Billing Address
    </span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <%-- main content area --%>
    <td valign="top" width=745>  
    
   <dsp:form action="company_admin.jsp" method="post">
      
    <dsp:input bean="B2BRepositoryFormHandler.itemDescriptorName" type="hidden" value="organization"/>
    <dsp:input bean="B2BRepositoryFormHandler.repositoryId" beanvalue="Profile.currentOrganization.repositoryid" type="hidden"/>
    <dsp:input bean="B2BRepositoryFormHandler.value.defaultBillingAddress.updateMode" type="hidden" value="replace"/>
  
               
     
        <table border=0 cellpadding=4 width=80%>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2 valign="top"><span class=big>Company Administration</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
          </tr>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          
          <tr>
            <td colspan=2 valign="top"> 
              <table width=100% cellpadding=3 cellspacing=0 border=0>
                <tr> 
                  <td class=box-top>&nbsp;Select Default Billing Address</td>
                </tr>
              </table> 
            </td>
          </tr>
          <tr>
            <td colspan=2>
            <%--  check if inherited --%>
            <dsp:droplet name="IsEmpty">
              <dsp:param bean="Profile.currentOrganization.myDefaultBillingAddress" name="value"/>
              <dsp:oparam name="false">
                <dsp:img src="../images/sethere.gif" align="left" hspace="6"/>
                <span class=small>This information is specific to this business unit.</span>
              </dsp:oparam>
              <dsp:oparam name="true">
                <dsp:img src="../images/inherited.gif" align="left" hspace="6"/>
                 <span class=small>This information is specified in your company's corporate profile.</span>
              </dsp:oparam>  
            </dsp:droplet>
            </td>
          </tr>          
          
          <tr>
            <td valign="top" colspan=2 height="92"> 
              <dsp:droplet name="TableForEach">
                <dsp:param bean="Profile.currentOrganization.billingAddrs" name="array"/>
                <dsp:param name="elementName" value="billingAddress"/>
                <dsp:param name="numColumns" value="2"/>
                <dsp:oparam name="outputStart"><table width="100%" border="0"></dsp:oparam>
                <dsp:oparam name="outputEnd"></table></dsp:oparam>
                <dsp:oparam name="outputRowStart"><tr></dsp:oparam>
                <dsp:oparam name="outputRowEnd"></tr></dsp:oparam>
                <dsp:oparam name="output">
                  <dsp:droplet name="IsEmpty">
                    <dsp:param name="value" param="billingAddress"/>
                    <dsp:oparam name="true">
                      <td></td>
                    </dsp:oparam>
                    <dsp:oparam name="false">

                    <dsp:droplet name="IsEmpty">
                      <dsp:param bean="Profile.currentOrganization.defaultBillingAddress" name="value"/>
                      <dsp:oparam name="true">
                        <td valign="top"><dsp:input bean="B2BRepositoryFormHandler.value.defaultBillingAddress.repositoryid" paramvalue="billingAddress.repositoryid" name="billingAddress" type="radio"/></td>
                      </dsp:oparam>

                      <dsp:oparam name="false">
                        <dsp:droplet name="Compare">
                          <dsp:param name="obj1" param="billingAddress.repositoryid"/>
                          <dsp:param bean="Profile.currentOrganization.defaultBillingAddress.repositoryid" name="obj2"/>
                          <dsp:oparam name="equal">
                            <td valign="top"><dsp:input bean="B2BRepositoryFormHandler.value.defaultBillingAddress.repositoryid" paramvalue="billingAddress.repositoryid" name="billingAddress" type="radio" checked="<%=true%>"/></td>
                          </dsp:oparam>
                   
                          <dsp:oparam name="default">
                            <td valign="top"><dsp:input bean="B2BRepositoryFormHandler.value.defaultBillingAddress.repositoryid" paramvalue="billingAddress.repositoryid" name="billingAddress" type="radio"/></td>
                          
                          </dsp:oparam>
        
                        </dsp:droplet>  <% /* End of Compare */ %>
                      </dsp:oparam>
                    </dsp:droplet>  <% /* End of IsEmpty */ %>


                  <td>
                    <dsp:getvalueof id="pval0" param="billingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                  </td>
                  </dsp:oparam>
                </dsp:droplet>  <%-- End of IsEmpty --%>
              </dsp:oparam>
            </dsp:droplet>  <%--  End of TableForEach --%>
            </td>
          </tr>
          <tr><td>&nbsp;</td></tr>
          <tr>
            <td colspan="3" align="left"><dsp:input bean="B2BRepositoryFormHandler.update" type="submit" value=" Save "/>
            <input type="submit" name="cancel" value="Cancel"></td>
            
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/default_billing_edit.jsp#2 $$Change: 651448 $--%>
