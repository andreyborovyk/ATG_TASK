<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Cost Centers"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Company Admin</dsp:a> &gt;
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt; Cost Centers
    </span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  
    
   <dsp:form action="company_admin.jsp" method="post">
        <table border=0 cellpadding=4 width=80%>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr> 
            <td colspan=2 valign="top"><span class=big>Company Administration</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
          </tr>
          <tr> 
            <td colspa=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          
          <tr>
            <td colspan=2 valign="top"> 
              <table width=100% cellpadding=3 cellspacing=0 border=0>
                <tr> 
                  <td class=box-top>&nbsp;Cost Centers</td>
                </tr>
              </table>
            </td>
          </tr>
          
          <tr>
            <td colspan=2>
            <%--  check if inherited --%>
            <dsp:droplet name="IsEmpty">
              <dsp:param bean="Profile.currentOrganization.myCostCenters" name="value"/>
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
              <table width="100%" border="0">
                <dsp:droplet name="ForEach">
             <dsp:param bean="Profile.currentOrganization.costCenters" name="array"/>
             <dsp:param name="elementName" value="costCenter"/>
             <dsp:oparam name="output">
                <tr>
                  <td>
                  <dsp:valueof param="costCenter.identifier"/> -
                  <dsp:valueof param="costCenter.description"/>
                  &nbsp;
                  
                  <span class=small><dsp:a href="cost_center_edit.jsp">Edit
                  <dsp:param name="ccId" param="costCenter.id"/>
                  <dsp:param name="identifier" param="costCenter.identifier"/>
                  <dsp:param name="description" param="costCenter.description"/>
                            </dsp:a></span>|
                            <span class=small><dsp:a href="cost_center_delete.jsp">
                  <dsp:param name="ccId" param="costCenter.id"/>
                  <dsp:param name="identifier" param="costCenter.identifier"/>
                  <dsp:param name="description" param="costCenter.description"/>
                            Delete</dsp:a></span>

</td>
                </tr>
                </dsp:oparam>
                <dsp:oparam name="empty">
                <tr><td>There are currently no cost centers assigned to this business unit.</td></tr>
                </dsp:oparam>
                </dsp:droplet>
                
                <tr><td>&nbsp;</td></tr>
                <tr>
                  <td><span class=smallb><dsp:a href="cost_center_create.jsp">Add new cost center</dsp:a></span></td>
                </tr>
                <tr><td><br></td></tr>
              </table>
            </dsp:form>
            </td>
          </tr>
    
        
          <%--  vertical space --%>
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
        </table>

    </td>
  </tr>


</table>
</div>
</body>
</html>



</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/cost_centers.jsp#2 $$Change: 651448 $--%>
