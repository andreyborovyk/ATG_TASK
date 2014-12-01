<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserUpdateFormHandler"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Shipping Methods"/></dsp:include>

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
    <dsp:a href="user_edit_info.jsp"><dsp:param bean="Profile.currentUser.id" name="userId"/>Edit User Profile</dsp:a> &gt;
    Add Role
    </span> </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <!-- main content area -->
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
                  <td class=box-top>&nbsp;Add Role</td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <dsp:form action="user_edit_info.jsp" method="post">
            <dsp:input bean="MultiUserUpdateFormHandler.repositoryIds" beanvalue="Profile.currentUser.id" type="hidden"/>  
            <dsp:input bean="MultiUserUpdateFormHandler.roleUpdateMethod" type="hidden" value="append"/>
            <input name="userId" type="hidden" value="<dsp:valueof bean="Profile.currentUser.id"/>">
         
            <td valign="top" colspan=2 height="92">
 
            <table border="0" cellpadding="3" width=100%>
              <tr><td colspan=2><span class=small>Select from the available roles for this user. You can only add roles that relate to this organization. Contact Motorprise to give a user the admin role for another organization.</span><p></td></tr>
              
              <tr  valign=top>
                <td align=right><span class=smallb>Roles</span></td>
                <td>
                  <dsp:select bean="MultiUserUpdateFormHandler.roleIds">
                   <dsp:droplet name="ForEach">
                    <dsp:param bean="Profile.currentOrganization.relativeRoles" name="array"/>
                    <dsp:param name="elementName" value="role"/>
                    <dsp:param name="indexName" value="roleIndex"/>
                    <dsp:oparam name="output">
                     <dsp:getvalueof id="rolename" idtype="java.lang.String" param="role.repositoryId">
                     <dsp:option value="<%=rolename%>"/>
                        <dsp:valueof param="role.name">unnamed role</dsp:valueof> - <dsp:valueof param="role.relativeTo.name"/>
                     </dsp:getvalueof>
                    </dsp:oparam>
                   </dsp:droplet>
                  </dsp:select>
                </td>
              </tr>
              <tr> 
                <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
              </tr>
              <tr> 
                <td></td>
                <td><dsp:input bean="MultiUserUpdateFormHandler.update" type="submit" value=" Add "/>&nbsp;
                    <input type="submit" value="Cancel"></td>
              </tr>
             
            </table>
            </td>
            </dsp:form>
          </tr>
   
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
    
          <tr> 
            <td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
        </table>

    </td>
  </tr>
</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/user_add_role.jsp#2 $$Change: 651448 $--%>
