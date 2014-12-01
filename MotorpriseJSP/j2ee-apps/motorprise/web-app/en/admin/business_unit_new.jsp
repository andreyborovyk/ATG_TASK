<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/userdirectory/CreateOrganizationFormHandler"/>
<dsp:importbean bean="/atg/userdirectory/droplet/TargetPrincipals"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" My Account"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
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
    <table border=0 cellpadding=4 width=80%>
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr valign=top>
        <td><span class=big>Company Administration</span><br>
           <dsp:include page="../common/FormError.jsp"></dsp:include> 
      </td>
      </tr>

      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr valign=top>
        <td>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Create New Business Unit</td></tr></table>
        </td>
      </tr>

      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr>
        <td><dsp:form action="business_units.jsp" method="post">
        
        <dsp:input bean="CreateOrganizationFormHandler.userId" beanvalue="Profile.id" type="hidden"/>
        <table border=0 cellpadding=4 cellspacing=0>
          <tr valign=top>
            <td align=right><span class=smallb>Name</span></td>
            <td><dsp:input bean="CreateOrganizationFormHandler.organizationName" size="30" type="text"/></td>
          </tr>
          <tr valign=top>
            <td align=right><span class=smallb>Parent organization</td>
            <td>
            <dsp:select bean="CreateOrganizationFormHandler.parentOrganizationId">
	    <dsp:getvalueof id="parentId" idtype="java.lang.String" bean="Profile.parentOrganization.repositoryId">
            <dsp:option selected="<%=true%>" value="<%=parentId%>"/>Select Parent Organization
	    </dsp:getvalueof>
	    <dsp:droplet name="TargetPrincipals">
              <dsp:param bean="Profile.id" name="userId"/>
              <dsp:param name="roleName" value="admin"/>
              <dsp:oparam name="output">
              <dsp:droplet name="ForEach">
                <dsp:param name="array" param="principals"/>
                <dsp:param name="sortProperties" value="+name"/>
                <dsp:oparam name="output">
		   <dsp:getvalueof id="parentId" idtype="java.lang.String" param="element.repositoryItem.repositoryId">
	           <dsp:option value="<%=parentId%>"/>
		   <dsp:valueof param="element.repositoryItem.name"/>
		   </dsp:getvalueof>
                </dsp:oparam>
              </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet>
            </dsp:select>
            </td>
          </tr>
          <tr valign=top>
            <td></td>
            <td><br>
                <dsp:input bean="CreateOrganizationFormHandler.createOrganizationSuccessURL" type="hidden" value="business_units.jsp"/>          

                <dsp:input bean="CreateOrganizationFormHandler.createOrganizationErrorURL" type="hidden" value="business_unit_new.jsp"/>          
                <dsp:input bean="CreateOrganizationFormHandler.createOrganization" type="submit" value=" Save "/> &nbsp;
                <input type="submit" value=" Cancel "></td>

          </tr>
    
        </table>
        </dsp:form>
        </td>      
      </tr>
    </table>
    </td>
  </tr>
</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/business_unit_new.jsp#2 $$Change: 651448 $--%>
