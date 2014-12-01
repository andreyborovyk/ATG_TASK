<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" My Account"/></dsp:include>
<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="my_account.jsp">My Account</dsp:a> &gt;
  <dsp:a href="my_profile.jsp">My Profile</dsp:a> &gt; 
  Edit Profile</td>
  </tr>

  <tr valign=top> 
    <td width=55><img src="../images/d.gif" hspace=27></td>

    <!-- main content area -->
    <td valign="top" width=745>
    <dsp:form action="my_profile.jsp" method="post">
      
    <table border=0 cellpadding=4 width=80%>
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>My Account</span>
         <dsp:include page="../common/FormError.jsp"></dsp:include> 
        </td>
      </tr>

      <tr><td><img src="../images/d.gif" vspace=0></td></tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Edit Personal Account Information</td></tr></table>
        </td>
      </tr>

      <tr><td><img src="../images/d.gif" vspace=0></td></tr>

      <dsp:input bean="ProfileFormHandler.checkForRequiredParameters" type="HIDDEN" value="false"/>
      
      <tr>
        <td align=right valignwidth=25%><span class=smallb>Name</span></td>
        <td width=75%><dsp:input bean="ProfileFormHandler.value.firstName"  size="15" type="text"/>
            <dsp:input bean="ProfileFormHandler.value.middleName" size="4"  type="text"/>
            <dsp:input bean="ProfileFormHandler.value.lastName" size="15"   type="text"/></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Email</span></td>
        <td><dsp:input bean="ProfileFormHandler.value.email" size="30" type="text"/></td> 
      </tr>
      <tr>
        <td align=right><span class=smallb>Company Name</span></td>
        <td><dsp:input bean="ProfileFormHandler.value.businessAddress.companyName" size="30" type="text" /></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Address 1</span></td>
        <td><dsp:input bean="ProfileFormHandler.value.businessAddress.address1" size="30" type="text" /></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Address 2</span></td>
        <td><dsp:input bean="ProfileFormHandler.value.businessAddress.address2" size="30" type="text" /></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>City, State/Province Zip/Postal Code</span></td>
        <td><dsp:input bean="ProfileFormHandler.value.businessAddress.city" size="15" type="text" />,&nbsp;
        <dsp:input bean="ProfileFormHandler.value.businessAddress.state" size="4" type="text" />&nbsp;
        <dsp:input bean="ProfileFormHandler.value.businessAddress.postalCode" size="8" type="text" /></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Country</span></td>
        <td>
          <dsp:select bean="ProfileFormHandler.value.businessAddress.country">
            <%@ include file="../common/CountryPicker.jspf" %>
          </dsp:select>
        </td>
      </tr>

      <tr>
        <td align=right><span class=smallb>Phone</span></td>
        <td><dsp:input bean="ProfileFormHandler.value.businessAddress.phoneNumber" size="30" type="text"/></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Fax</span></td>
        <td><dsp:input bean="ProfileFormHandler.value.businessAddress.faxNumber" size="30" type="text"/></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Language</span></td>
        <td><dsp:select bean="ProfileFormHandler.value.locale">
              <dsp:option value="en_US"/>English
              <dsp:option value="de_DE"/>German
              <dsp:option value="ja_JP"/>Japanese
            </dsp:select></td>
      </tr>       
      <tr>
        <td></td>
          <dsp:input bean="ProfileFormHandler.updateErrorURL" type="HIDDEN" value="edit_profile.jsp"/>
        <td><br><dsp:input bean="ProfileFormHandler.update" type="submit" value=" Save "/> &nbsp;
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/user/edit_profile.jsp#2 $$Change: 651448 $--%>
