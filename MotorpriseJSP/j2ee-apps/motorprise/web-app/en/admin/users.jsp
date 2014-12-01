<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<% /* This page displays all the users for the organization selected by the admin in the business_units.jsp page */ %>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Range"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserUpdateFormHandler"/>
<dsp:importbean bean="/atg/userdirectory/droplet/UserList"/>

<% /* Clear the form handler */ %>

<dsp:setvalue bean="MultiUserUpdateFormHandler.clear" value=""/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Users"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
        <dsp:a href="business_units.jsp">Company Admin</dsp:a> &gt;
        <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt; 
        View Users</span></td>
  </tr>
  
  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif"/></td>

    <% /*  main content area */ %>
    <td valign="top" width=745>
    <table border=0 cellpadding=4 width=80%>
      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>Company Administration</span><br><span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span></td>
      </tr>

      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Users</td></tr></table>
        </td>
      </tr>

      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr>
        <td>
        
    <dsp:droplet name="UserList">
      <dsp:param bean="Profile.currentOrganization.repositoryid" name="organizationId"/>
      <dsp:oparam name="output">

         <dsp:droplet name="Range">
          <dsp:param name="array" param="users"/>
          <dsp:param name="start" param="startIndex"/>
          <dsp:param name="howMany" value="10"/>
          <dsp:param name="sortProperties" value="+name"/>
          <dsp:oparam name="output">
             <dsp:a href="user.jsp">
	            <dsp:valueof param="element.repositoryItem.firstName"/>
              <dsp:valueof param="element.repositoryItem.lastName"/>
              <dsp:param name="userId" param="element.repositoryItem.id"/>
	           </dsp:a><br>
      	  </dsp:oparam>
           <dsp:oparam name="outputEnd">
            <dsp:droplet name="IsEmpty">
             <dsp:param name="value" param="nextHowMany"/>
             <dsp:oparam name="false"><BR>
               <dsp:a href="users.jsp">  Next 10 <dsp:param name="startIndex" param="nextStart"/></dsp:a>
             </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>        
         </dsp:droplet>
		
      </dsp:oparam>
   
    <dsp:oparam name="empty">
      There are no users for this organization
    </dsp:oparam>
	</dsp:droplet>
      </td>      
      </tr>
      <% /*  vertical space */ %>
      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
  
    </table>

    </td>
  </tr>

</table>
</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/users.jsp#2 $$Change: 651448 $--%>
