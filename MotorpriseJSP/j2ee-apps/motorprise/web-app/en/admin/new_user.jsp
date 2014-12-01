<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/MultiUserAddFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Create New User"/></dsp:include>
  
<table border=0 cellpadding=0 cellspacing=0 width=800> 
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Company Admin</dsp:a> &gt;
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
    Create New User</span> </td>
  </tr>
  
  <tr>
    <td>
    </td>
    <td width=745>
      <dsp:include page="../common/FormError.jsp"></dsp:include>
    </td>
  </tr>

  <tr valign=top> 
    <td width="55"><dsp:img src="../images/d.gif" hspace="27"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>  



    <dsp:form action="company_admin.jsp" method="post">

    <dsp:input bean="MultiUserAddFormHandler.count" type="hidden" value="1"/>

    <dsp:input bean="MultiUserAddFormHandler.confirmPassword" type="hidden" value="true"/>
    <dsp:input bean="MultiUserAddFormHandler.createErrorURL" type="hidden" value="new_user.jsp"/>
    <dsp:input bean="MultiUserAddFormHandler.createSuccessURL" type="hidden" value="new_user2.jsp"/>
    <dsp:input bean="MultiUserAddFormHandler.users[0].organizationId" beanvalue="Profile.currentOrganization.repositoryid" type="hidden"/>
                         
    <table border=0 cellpadding=4 width=80%>
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>Company Administration</span></td>
      </tr>
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Create New User</td></tr></table>
        </td>
      </tr>
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr>
        <td align=right><span class=smallb>Name</span></td>
        <td width=75%>
       
        <dsp:input bean="MultiUserAddFormHandler.users[0].value.firstName" size="15" type="text" value="" required="<%=true%>"/>
        <dsp:input bean="MultiUserAddFormHandler.users[0].value.middleName" size="4" type="text" value=""/>
        <dsp:input bean="MultiUserAddFormHandler.users[0].value.lastName" size="15" type="text" value="" required="<%=true%>"/></td>
      </tr>
      
      <tr>
        <td align=right><span class=smallb>Login</span></td>
        <td><dsp:input bean="MultiUserAddFormHandler.users[0].value.login" size="30" type="text" value=""/></td> 
      </tr>
      <tr>
        <td align=right><span class=smallb>Password</span></td>
        <td><dsp:input bean="MultiUserAddFormHandler.users[0].value.Password" size="30" type="password" value=""/></td>
      </tr>
     
      <tr>
        <td align=right><span class=smallb>Confirm Password</span></td>
        <td>
<dsp:input bean="MultiUserAddFormHandler.users[0].value.CONFIRMPASSWORD" size="30" type="password" value=""/></td>
      </tr>
     
      <tr>
        <td align=right><span class=smallb>Email</span></td>
        <td><dsp:input bean="MultiUserAddFormHandler.users[0].value.email" size="30" type="text" value=""/></td>
      </tr>



 
      <tr valign=top>
        <td align=right><span class=smallb>Role</span></td>
        <td>
          <dsp:droplet name="ForEach">
            <dsp:param name="array" bean="Profile.currentOrganization.relativeRoles"/>
            <dsp:param name="elementName" value="role"/>
            <dsp:oparam name="output">
	              <dsp:input type="checkbox"
	             bean="MultiUserAddFormHandler.users[0].roleIds"
		     paramvalue="role.repositoryId" checked="<%=false%>"/>
	      <dsp:valueof param="role.name">unnamed role</dsp:valueof><br>
            </dsp:oparam>
          </dsp:droplet>
        </td>
      </tr>

        <tr>
        <td align=right><span class=smallb>Language</span></td>
        <td><dsp:select bean="MultiUserAddFormHandler.users[0].value.locale" nodefault="true">
              <dsp:option value="en_US" selected="<%=true%>"/> English
              <dsp:option value="de_DE" selected="<%=false%>"/> German
              <dsp:option value="ja_JP" selected="<%=false%>"/> Japanese
            </dsp:select></td>
      </tr>


      

    
      
  
     
    <tr>
      <td></td>
      <td><br>
      <dsp:input bean="MultiUserAddFormHandler.users[0].value.member" type="hidden" value="true"/>
      <dsp:input bean="MultiUserAddFormHandler.create" type="submit" value=" Create user "/> &nbsp;
              <input type="submit" value=" Cancel ">
        
</td>
    </tr>
    <tr>
      <td></td>
      <td>
      </td>      
    </tr>
    
    <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
  </table>
  </dsp:form>
  </td>
</tr>

</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/new_user.jsp#2 $$Change: 651448 $--%>
