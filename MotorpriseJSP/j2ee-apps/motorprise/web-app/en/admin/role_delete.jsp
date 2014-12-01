<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>



<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Company Admin"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Company Admin</dsp:a> &gt; 
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
    <dsp:a href="users.jsp">View Users</dsp:a> &gt;
    <dsp:a href="user.jsp">User Account</dsp:a> &gt; 
    <dsp:a href="user_edit_info.jsp">Edit User Profile</dsp:a> &gt;
    Remove Role
    </span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  
    
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
                  <td class=box-top>&nbsp;Remove Roles</td>
                </tr>
              </table>
            </td>
          </tr>
          
          <dsp:form action="roles.jsp" method="post">
          <tr><td><span class=help>Check roles to remove for this user</span></td></tr>
        
          <tr valign=top>
            <td>
              &nbsp;<input type=checkbox>&nbsp;Buyer - USMW<br>
              &nbsp;<input type=checkbox>&nbsp;Approver - USMW<br>
              &nbsp;<input type=checkbox>&nbsp;Admin - USMW</td>
            </td> 
          </tr>
          <tr> 
            <td><input type="submit" value="Remove">&nbsp;<input type="reset" value="Cancel"></td>
          </tr>   
          </dsp:form>
    
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/role_delete.jsp#2 $$Change: 651448 $--%>
