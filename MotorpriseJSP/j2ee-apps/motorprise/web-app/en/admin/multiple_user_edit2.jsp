<%@ taglib uri="dsp" prefix="dsp" %>
<%@ taglib uri="core" prefix="core" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserUpdateFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>
<dsp:importbean bean="/atg/dynamo/droplet/Format"/>
<dsp:importbean bean="/atg/projects/b2bstore/userdirectory/ProfileLookUp"/>

<%-- Redirect to the previous page if no users were selected and display an error message --%> 
<%-- Do this before generating content into the page so we can send the redirect header   --%>

<dsp:droplet name="IsEmpty">
  <dsp:param name="value" bean="MultiUserUpdateFormHandler.repositoryIds"/>
  <dsp:oparam name="true">
    <core:createUrl id="errorPage" url="multiple_user_edit.jsp">
      <core:urlParam param="errorMessage" value="No users were selected"/>
      <core:redirect url="<%=errorPage.getNewUrl()%>"/>
    </core:createUrl>
  </dsp:oparam>
</dsp:droplet>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Edit Multiple Users"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
      <dsp:a href="business_units.jsp">Company Admin</dsp:a> &gt;
      <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt;
      Edit Multiple Users</span></td>
  </tr>

  <tr valign=top> 
   <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>

    <%--  main content area --%>
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
        <tr><td class=box-top>&nbsp;Edit Multiple Users</td></tr></table>
        </td>
      </tr>

      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr>
        <td><b>Selected users</b>
	<font color=cc0000> <STRONG><UL><dsp:valueof param="errorMessage"/> </STRONG></UL></font>
	<dsp:getvalueof id="formTarget" idtype="java.lang.String" param="targetPage">
	<dsp:form formid="users" action="<%=formTarget%>" method="post"> 
        <blockquote>
        
        <dsp:droplet name="ForEach">
        <dsp:param bean="MultiUserUpdateFormHandler.repositoryIds" name="array"/>
        <dsp:param name="elementName" value="userId"/>
        <dsp:oparam name="output">
        	<dsp:droplet name="ProfileLookUp">
		<dsp:param name="id" param="userId"/>
		<dsp:param name="elementName" value="user"/>
		<dsp:oparam name="output">
      <dsp:setvalue bean="Profile.currentUser" paramvalue="user"/>
			<dsp:valueof param="user.firstName"/>
			<dsp:valueof param="user.lastName"/><br>
             	</dsp:oparam>
             	</dsp:droplet>
         </dsp:oparam>
         </dsp:droplet>
         
          <br>
          <span class=smallb><dsp:a href="multiple_user_edit.jsp">Edit selected users list</dsp:a></span>
        </blockquote>
        </dsp:form>
	</dsp:getvalueof>
        
        <br>
        <b>Select attributes to edit</b>
        <blockquote>
          <dsp:form formid="attrs" action="multiple_user_edit3.jsp" method="post">
          <input type="checkbox" name="roles" value="true"> Roles<br>
          <input type="checkbox" name="shippingAddrs" value="true"> Shipping addresses<br>
           <input type="checkbox" name="paymentMethods" value="true"> Credit Cards<br>
          <input type="checkbox" name="costCenters" value="true"> Cost centers<br>
          <input type="checkbox" name="purchaseLimit" value="true"> Purchase limit<br>
          <br> 
          <input type="submit" value="Continue">
        </blockquote>
        </dsp:form>

        
          
        </td>      
      </tr>
  
    </table>

    </td>
  </tr>

</table>
</div>
</body>
</html>



</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/multiple_user_edit2.jsp#2 $$Change: 651448 $--%>
