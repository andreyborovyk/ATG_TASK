<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Format"/>
<dsp:importbean bean="/atg/projects/b2bstore/repository/B2BRepositoryFormHandler"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Edit Cost Center"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Company Admin</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
    <dsp:a href="cost_centers.jsp">Cost Centers</dsp:a> &gt Delete Cost Center
    </span> </td>
  </tr>

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  
    
    <%-- Display errors if any --%>

    <dsp:include page="../common/FormError.jsp"></dsp:include> 
        
   <dsp:form action="cost_centers.jsp" method="post">
   <dsp:input bean="B2BRepositoryFormHandler.repositoryId" paramvalue="ccId" type="hidden"/>
   <dsp:input bean="B2BRepositoryFormHandler.itemDescriptorName" type="hidden" value="cost-center"/>
   <dsp:input bean="B2BRepositoryFormHandler.updateItemDescriptorName" type="hidden" value="organization"/>
   <dsp:input bean="B2BRepositoryFormHandler.updateRepositoryId" beanvalue="Profile.currentOrganization.repositoryid" type="hidden"/>
   <dsp:input bean="B2BRepositoryFormHandler.updatePropertyName" type="hidden" value="costCenters"/>

   <%--  Prepare the URL to redirect to, in case of an error during form submission --%>
   <dsp:droplet name="Format">
    <dsp:param name="value1" param="ccId"/>
    <dsp:param name="value2" param="identifier"/>
    <dsp:param name="value3" param="description"/>
    <dsp:param name="format" value="cost_center_delete.jsp?ccId={value1}&identifier={value2}&description={value3}"/>
    <dsp:oparam name="output">
      <dsp:setvalue paramvalue="message" valueishtml="<%=true%>" param="deleteErrorURL"/>
      <dsp:input bean="B2BRepositoryFormHandler.deleteErrorURL" paramvalue="deleteErrorURL" type="hidden"/>
    </dsp:oparam>
   </dsp:droplet>



        <table border=0 cellpadding=4 width=80%>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td colspan=2 valign="top"><span class=big>Company Administration</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
          </tr>
          <tr> 
            <td colspna=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          
          <tr>
            <td colspan=2 valign="top"> 
              <table width=100% cellpadding=3 cellspacing=0 border=0>
                <tr> 
                  <td class=box-top>&nbsp;Delete Cost Center</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td valign="top" colspan=2 height="92"> 
              <table cellpadding=0 cellspacing=0 border="0">
                <tr>
                  <td><span class=smallb>ID #</span></td>
                  <td>&nbsp;<td><span class=smallb>Name</span></td> 
                </tr>        
                <tr>
                  <td><dsp:valueof param="identifier"/></td>
                  <td align=center>&nbsp;-&nbsp; </td><td><dsp:valueof param="description"/></td> 
                </tr>
                <tr><td colspan=4>&nbsp;</td></tr>
                </table>
                
          <dsp:input bean="B2BRepositoryFormHandler.delete" type="submit" value="Delete"/> &nbsp;<dsp:input bean="B2BRepositoryFormHandler.cancel" type="submit" value="Cancel"/>

            </td>
          </tr>
    
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
        
          <tr> 
            <td colspan=><dsp:img src="../images/d.gif" vspace="0"/></td>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/cost_center_delete.jsp#2 $$Change: 651448 $--%>
