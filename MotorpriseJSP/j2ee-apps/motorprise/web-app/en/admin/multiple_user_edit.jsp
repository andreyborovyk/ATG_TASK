<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserUpdateFormHandler"/>
<dsp:importbean bean="/atg/userdirectory/droplet/UserList"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<%-- clear the form handler since it's session scoped --%>

<dsp:setvalue bean="/atg/userprofiling/MultiUserUpdateFormHandler.clear" value=""/>
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
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/>
    

    <p><span class=small>
    </td>

    <%--  main content area --%>
    <td valign="top" width=745>
   <font color=cc0000> <STRONG><UL><dsp:valueof param="errorMessage"/> </STRONG></UL></font>
    <dsp:form action="multiple_user_edit2.jsp" method="post">
    <table border=0 cellpadding=4 width=80%>
      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>Company Administration</span><br><span class=little></span></td>
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
        <td><b>Select users to edit</b>
        <blockquote>         
        
    <dsp:droplet name="UserList">
      <dsp:param bean="Profile.currentOrganization.repositoryid" name="organizationId"/>
      <dsp:oparam name="output">
    
        <dsp:droplet name="ForEach">
          <dsp:param name="array" param="users"/>
          <dsp:param name="sortProperties" value="+name"/>
          <dsp:oparam name="output">
            <dsp:input bean="MultiUserUpdateFormHandler.repositoryIds" paramvalue="element.repositoryItem.id" type="checkbox" /><dsp:valueof param="element.repositoryItem.firstName"/>&nbsp;<dsp:valueof param="element.repositoryItem.lastName"/><BR>
          </dsp:oparam>
        </dsp:droplet> 
            <br>
            <input name="targetPage" type="hidden" value="multiple_user_edit.jsp">
            <input type="submit" value="Continue">
           
        
       

      </dsp:oparam>

      <dsp:oparam name="error">
      </dsp:oparam>
    </dsp:droplet>
  
  
           </blockquote>

        </td>      
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/admin/multiple_user_edit.jsp#2 $$Change: 651448 $--%>
