<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:setvalue bean="/atg/dynamo/servlet/RequestLocale.refresh" value=""/>

<%-- If the user hits this page with his or her request locale set to de_DE --%>
<%-- we will automatically redirect to the German profile page instead.     --%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="/atg/dynamo/servlet/RequestLocale.locale" name="value"/>
  <dsp:oparam name="de_DE">
    <dsp:droplet name="/atg/dynamo/droplet/Redirect">
      <dsp:param name="url" value="../../de/user/my_profile.jsp"/>
    </dsp:droplet>
  </dsp:oparam>
  <dsp:oparam name="ja_JP">
    <dsp:droplet name="/atg/dynamo/droplet/Redirect">
      <dsp:param name="url" value="../../ja/user/my_profile.jsp"/>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="My Profile"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
      <dsp:a href="my_account.jsp">My Account</dsp:a> &gt; 
      My Profile</td>
  </tr>
  
  <tr valign=top> 
    <td width=55><img src="../images/d.gif" hspace=27></td>

    <!-- main content area -->
    <td valign="top" width=745>  
    <table border=0 cellpadding=4 width=80%>
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>My Account</span></td>
      </tr>

      <tr><td><img src="../images/d.gif" vspace=0></td></tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Contact Information</td></tr></table>
        </td>
      </tr>

      <tr><td><img src="../images/d.gif" vspace=0></td></tr>

      <tr>
        <td align=right><span class=smallb>Name</span></td>
        <td width=75%><dsp:valueof bean="Profile.firstName" /> 
        <dsp:valueof bean="Profile.middleName" /> <dsp:valueof bean="Profile.lastName" /></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Login</span></td>
        <td width=75%><dsp:valueof bean="Profile.login" /></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Email</span></td>
        <td><dsp:valueof bean="Profile.email" /></td>
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb>Business address</span></td>
        <td>
          <dsp:getvalueof id="pval0" bean="Profile.businessAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
        </td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Phone</span></td>
        <td><dsp:valueof bean="Profile.businessAddress.phoneNumber" /></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Fax</span></td>
        <td><dsp:valueof bean="Profile.businessAddress.faxNumber" /></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Language</span></td>
        <td>
          <dsp:droplet name="Switch">
            <dsp:param bean="Profile.locale" name="value"/>
            <dsp:oparam name="en_US">
              English
            </dsp:oparam> 
            <dsp:oparam name="de_DE">
              German
            </dsp:oparam> 
            <dsp:oparam name="ja_JP">
              Japanese
            </dsp:oparam>
            <dsp:oparam name="default">
              <dsp:valueof bean="Profile.locale">not set</dsp:valueof>
            </dsp:oparam>
          </dsp:droplet></td>
      </tr>
      <tr>
        <td></td>
        <td><span class=smallb><dsp:a href="edit_profile.jsp">Edit</dsp:a></span> |
        <span class=smallb><dsp:a href="change_password.jsp">Change password</dsp:a></span></td>
      </tr>

      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
  
      <tr>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Company Information</td></tr></table>
        </td>
      </tr>

      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
 
      <tr valign=top>
        <td align=right><span class=smallb>Business unit</span></td>
        <td><dsp:valueof bean="Profile.parentOrganization.name" /></td>
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb>Default cost center</span></td>
        <td><dsp:valueof bean="Profile.defaultCostCenter.identifier" /> -
             <dsp:valueof bean="Profile.defaultCostCenter.description">N/A</dsp:valueof></td>
      </tr>
            
      <tr valign=top>
        <td align=right><span class=smallb>Default shipping address</span></td>
        <td>
          <dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" bean="Profile.defaultShippingAddress"/></dsp:include>
        </td>
      </tr>

      <dsp:droplet name="Switch">
      <dsp:param bean="Profile.invoiceRequestAuthorized" name="value"/>
      <dsp:oparam name="true">
      <tr valign=top>
        <td align=right><span class=smallb>Default billing address</span></td>
        <td>
          <dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" bean="Profile.defaultBillingAddress"/></dsp:include>
        </td>
      </tr>
      </dsp:oparam>
      </dsp:droplet>


      <tr valign=top>
        <td align=right><span class=smallb>Approvals required</span></td>
        <td><dsp:droplet name="Switch">
            <dsp:param bean="Profile.approvalRequired" name="value"/>
            <dsp:oparam name="false">
              No</td>
             </tr>
            </dsp:oparam>
            <dsp:oparam name="true">Yes</td>
              </tr>
              <tr>
                <td align=right><span class=smallb>Purchase limit</span></td>
                <td>
                <dsp:getvalueof id="pval0" bean="Profile.orderPriceLimit"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
            </dsp:oparam>
          </dsp:droplet>

      <tr valign=top>
        <td align=right>&nbsp;</td>
        <td><span class=smallb><dsp:a href="edit_company_info.jsp">Edit</dsp:a></span></td>
      </tr>
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
    </table>

    </td>
  </tr>

</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/user/my_profile.jsp#2 $$Change: 651448 $--%>
