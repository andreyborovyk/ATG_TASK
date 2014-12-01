<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<%--  This page displays a list of business units for which the currently logged in user is an admin of --%>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userdirectory/droplet/TargetPrincipals"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Company Admin"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
     Company Admin</span></td>
  </tr>

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>

    <%--  main content area --%>
    <td valign="top" width=745>
    <table border=0 cellpadding=4 width=100%>
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr valign=top>
        <td><span class=big>Company Administration</span></td>
      </tr>

      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr valign=top>
        <td>
        <table width=80% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Business Units</td></tr></table>
        </td>
      </tr>
      
      <tr>
         <td><span class=small>Please select a business unit to administer.
         </span></td>
      </tr>
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr>
        <td>

          <%-- This droplet is used to get a list of organizations for which the user is an admin of.  The role name and user id are passed in as parameters. --%>

          <dsp:droplet name="TargetPrincipals">
            <dsp:param bean="Profile.id" name="userId"/>
            <dsp:param name="roleName" value="admin"/>
            <dsp:oparam name="output">

              <dsp:droplet name="ForEach">
                <dsp:param name="array" param="principals"/>
                <dsp:param name="sortProperties" value="+name"/>
                <dsp:oparam name="output">
                  <dsp:a href="company_admin.jsp">
	          <dsp:valueof param="element.repositoryItem.name"/>
                  <dsp:param name="organizationId" param="element.repositoryItem.repositoryid"/></dsp:a><br>
                    </dsp:oparam>
                  </dsp:droplet>
              
             

            </dsp:oparam>
          </dsp:droplet>
          <br>
          <span class=smallb><dsp:a href="business_unit_new.jsp">Create new business unit</dsp:a></span></td>      
      </tr>
    </table>
    </td>
  </tr>
</table>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/business_units.jsp#2 $$Change: 651448 $--%>
