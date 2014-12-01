<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ArrayIncludesValue"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserAddFormHandler"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Create Multiple Users"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Company Admin</dsp:a> &gt;
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
    Create Multiple Users</span></td>
  </tr>

  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  

     <dsp:include page="../common/FormError.jsp"></dsp:include> 
    
      <dsp:form action="create_multiple_users.jsp" method="post">

      <dsp:input bean="MultiUserAddFormHandler.confirmPassword" type="hidden" value="true"/>
      <dsp:input bean="MultiUserAddFormHandler.createErrorURL" type="hidden" value="create_users2.jsp"/>
      <dsp:input bean="MultiUserAddFormHandler.createSuccessURL" type="hidden" value="create_users3.jsp"/>
      <dsp:input bean="MultiUserAddFormHandler.organizationId" beanvalue="Profile.currentOrganization.repositoryid" type="hidden"/>
      <dsp:input bean="MultiUserAddFormHandler.value.member" type="hidden" value="true"/>

      <table border=0 cellpadding=4 width=100%>
        <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
        <tr valign=top>
          <td colspan=2><span class=big>Company Administration</span><br><span class=little></span></td>
        </tr>
        <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

        <tr valign=top>
          <td colspan=2>
          <table width=100% cellpadding=3 cellspacing=0 border=0>
            <tr><td class=box-top>&nbsp;Create New Users</td></tr></table>
          </td>
        </tr>
        <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

         <dsp:droplet name="/atg/dynamo/droplet/For">
          <dsp:param bean="MultiUserAddFormHandler.count" name="howMany"/>
           <dsp:oparam name="output">
             <tr>
               <td align=right><span class=smallb>User <dsp:valueof param="count"/> </span></td>
             </tr>
             <tr>
               <td align=right><span class=smallb>Name</span></td>
               <td width=75%><dsp:input bean="MultiUserAddFormHandler.users[param:index].value.firstName" size="15" type="text" required="<%=true%>"/>
                <dsp:input bean="MultiUserAddFormHandler.users[param:index].value.middleName" size="4" type="text"/>
                <dsp:input bean="MultiUserAddFormHandler.users[param:index].value.lastName" size="15" type="text" required="<%=true%>"/></td>
             </tr>
             <tr>
               <td align=right><span class=smallb>Login</span></td>
               <td><dsp:input bean="MultiUserAddFormHandler.users[param:index].value.login" size="30" type="text"/></td> 
             </tr>
             <tr>
               <td align=right><span class=smallb>Password</span></td>
               <td><dsp:input bean="MultiUserAddFormHandler.users[param:index].value.Password" size="30" type="password" value=""/></td>
             </tr>
             <tr>
               <td align=right><span class=smallb>Confirm</span></td>
               <td>
                 <dsp:input bean="MultiUserAddFormHandler.users[param:index].value.CONFIRMPASSWORD" size="30" type="password" value=""/> 
               </td>
             </tr>
             <tr>
               <td align=right><span class=smallb>Email</span></td>
               <td><dsp:input bean="MultiUserAddFormHandler.users[param:index].value.email" size="30" type="text"/></td>
             </tr>
             <tr valign=top>
               <td align=right><span class=smallb>Role</span></td>
               <td>

                    <%/* Check if the roleIds already exist.  This property will not be set when the page is displayed for the first time.  In this case, display all the roles as unchecked boxes.  If this property is set, that means this page has been displayed already and that an error has occured.  In this case, each role in the currentOrganization is traversed to see if any of their ids exist in the roleIds property and if so, display that role as a checked box. */ %>

                    <dsp:droplet name="IsEmpty">
                    <dsp:param bean="MultiUserAddFormHandler.users[param:index].roleIds" name="value"/>
                    <dsp:oparam name="true">
    
        	             <%/* List organization roles, allowing admin to check off roles for each new user */%>
                       <dsp:droplet name="ForEach">
                       <dsp:param bean="Profile.currentOrganization.relativeRoles" name="array"/>
                       <dsp:param name="elementName" value="role"/>
                       <dsp:param name="indexName" value="roleIndex"/>
                       <dsp:oparam name="output">
                      
                          <dsp:input bean="MultiUserAddFormHandler.users[param:index].roleIds" paramvalue="role.repositoryId" type="checkbox" />  <dsp:valueof param="role.name">No name</dsp:valueof>
                       </BR>
                       </dsp:oparam>
                       </dsp:droplet>
                   </dsp:oparam>
                    
                   <dsp:oparam name="false">

                       <dsp:droplet name="ForEach">
                       <dsp:param bean="Profile.currentOrganization.relativeRoles" name="array"/>
                       <dsp:param name="elementName" value="role"/>
                       <dsp:param name="indexName" value="roleIndex"/>
                       <dsp:oparam name="output">

                          <dsp:droplet name="ArrayIncludesValue">
                          <dsp:param bean="MultiUserAddFormHandler.users[param:index].roleIds" name="array"/>
                          <dsp:param name="value" param="role.repositoryId"/>
                          <dsp:oparam name="true">
                            <dsp:input bean="MultiUserAddFormHandler.users[param:index].roleIds" paramvalue="role.repositoryId" type="checkbox" checked="<%=true%>"/>  <dsp:valueof param="role.name">No name</dsp:valueof>
                          </dsp:oparam>
                          <dsp:oparam name="false">
                  	        <dsp:input bean="MultiUserAddFormHandler.users[param:index].roleIds" paramvalue="role.repositoryId" type="checkbox" />  <dsp:valueof param="role.name">No name</dsp:valueof>
                          </dsp:oparam>
                          </dsp:droplet>
                        </br>
                        </dsp:oparam>
                        </dsp:droplet>
              
		              </dsp:oparam>
		              </dsp:droplet>
               </td>
             </tr>
             <tr>
               <td align=right><span class=smallb>Language</span></td>
               <td><dsp:select bean="MultiUserAddFormHandler.users[param:index].value.locale">
                     <dsp:option value="en_US"/> English
                     <dsp:option value="de_DE"/> German
                     <dsp:option value="ja_JP"/> Japanese
                   </dsp:select></td>
             </tr>

             <tr>
               <td colspan=2><hr size=1 color="#666666"></td>
             </tr>
             <tr><td><dsp:img src="../images/d.gif" vspace="6"/></td></tr>
           </dsp:oparam>
         </dsp:droplet>
         <tr>
           <td></td>
           <td><b><dsp:input bean="MultiUserAddFormHandler.create" type="submit" value=" Save "/> &nbsp;
                    <input type="submit" value=" Cancel " ></td>
         </tr>
         <%--  End of add new user action --%>

    </table>
    
    </dsp:form>
    </td>
  </tr>
</table>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/create_users2.jsp#2 $$Change: 651448 $--%>
