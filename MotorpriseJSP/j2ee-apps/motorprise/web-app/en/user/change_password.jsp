<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" My Account"/></dsp:include>
<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="my_account.jsp">My Account</dsp:a> &gt; <dsp:a href="my_profile.jsp">My Profile</dsp:a> &gt; Change Password</td>
  </tr>

  <tr valign=top> 
    <td width=55><img src="../images/d.gif" hspace=27></td>

    <!-- main content area -->
    <td  valign="top" width=745>
    <dsp:form action="my_profile.jsp" method="post">

    <table border=0 cellpadding=4 width=80%>
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>My Account</span>
           <dsp:include page="../common/FormError.jsp"></dsp:include>
           </td>
      </tr>

      <!-- vertical space -->
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Change Password</td></tr></table>
        </td>
      </tr>

      <!-- vertical space -->
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <tr>
        <td align=right><span class=smallb>User</span></td>
        <td width=75%><dsp:valueof bean="Profile.firstName" /> 
        <dsp:valueof bean="Profile.middleName" /> <dsp:valueof bean="Profile.lastName" /></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Login</span></td>
        <td width=75%><dsp:valueof bean="Profile.login" /></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Old password</span></td>
        <td width=75%><dsp:input bean="ProfileFormHandler.value.oldPassword" size="30" type="password" value=""/></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>New password</span></td>
        <td><dsp:input bean="ProfileFormHandler.value.password" name="donotConfuseNetscape" size="30" type="password" value=""/></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Confirm password</span></td>
        <td><dsp:input bean="ProfileFormHandler.value.confirmPassword" size="30" type="password" value=""/>
        <dsp:input bean="ProfileFormHandler.confirmPassword" type="hidden" value="true"/></td> 
      </tr>
      <tr>
        <td></td>
        <td><br>
        <dsp:input bean="ProfileFormHandler.changePasswordErrorURL" name="errorurl" type="HIDDEN" value="change_password.jsp"/>
        <dsp:input bean="ProfileFormHandler.changePassword" type="submit" value=" Save "/> &nbsp;
        <input type="submit" value=" Cancel "></td>
      </tr>

    </table>
    </dsp:form>
    </td>
  </tr>
</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/user/change_password.jsp#2 $$Change: 651448 $--%>
