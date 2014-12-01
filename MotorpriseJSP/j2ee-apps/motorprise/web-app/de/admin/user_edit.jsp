<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/MultiUserUpdateFormHandler"/>
<dsp:importbean bean="/atg/projects/b2bstore/userdirectory/ProfileLookUp"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="  Benutzerkonto bearbeiten"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <dsp:a href="business_units.jsp">Unternehmensverwaltung</dsp:a> &gt;
    <dsp:a href="company_admin.jsp"><dsp:valueof bean="Profile.currentOrganization.name"/></dsp:a> &gt; 
    <dsp:a href="users.jsp">Benutzer anzeigen</dsp:a> &gt;
    <dsp:a href="user.jsp">Benutzerkonto</dsp:a> &gt;
    Benutzerkonto bearbeiten
    </span></td>
  </tr>
  

  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>

    <% /*  main content area */ %>
    <td valign="top" width=745>
    <dsp:form action="user.jsp" method="post">

	<dsp:input bean="MultiUserUpdateFormHandler.repositoryIds" beanvalue="Profile.currentUser.id" type="hidden"/>
	<dsp:input bean="MultiUserUpdateFormHandler.confirmPassword" type="hidden" value="false"/>
	<dsp:input bean="MultiUserUpdateFormHandler.UpdateSuccessURL" type="hidden" value="user.jsp"/>
	<dsp:input bean="MultiUserUpdateFormHandler.UpdateErrorURL" type="hidden" value="user_edit.jsp"/>
	
     
    <table border=0 cellpadding=4 width=80%>
      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>Unternehmensverwaltung</span><br>
        <span class=little><dsp:valueof bean="Profile.currentOrganization.name" /></span>
     <% /*<dsp:include page="../common/FormError.jsp" flush="true"></dsp:include> */ %></td>
      </tr>

      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Kontaktinformationen bearbeiten</td></tr></table>
        </td>
      </tr>

      <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

      <tr>
        <td align=right><span class=smallb>Name</span></td>
        <td width=75%><dsp:input bean="MultiUserUpdateFormHandler.value.firstName" beanvalue="Profile.currentUser.firstName" size="15" type="text"/>
        <dsp:input bean="MultiUserUpdateFormHandler.value.middleName" beanvalue="Profile.currentUser.middleName" size="4" type="text"/>
        <dsp:input bean="MultiUserUpdateFormHandler.value.lastName" beanvalue="Profile.currentUser.lastName" size="15" type="text"/></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>E-Mail</span></td>
        <td><dsp:input bean="MultiUserUpdateFormHandler.value.email" beanvalue="Profile.currentUser.email" size="30" type="text"/></td> 
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb>Firmenname</span></td>
        <td>
        <dsp:input bean="MultiUserUpdateFormHandler.value.businessAddress.companyName" beanvalue="Profile.currentUser.businessAddress.companyName" size="30" type="text" /></td>
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb>Adresse 1</span></td>
        <td><dsp:input bean="MultiUserUpdateFormHandler.value.businessAddress.address1" beanvalue="Profile.currentUser.businessAddress.address1" size="30" type="text" /></td>
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb>Adresse 2</span></td>
        <td><dsp:input bean="MultiUserUpdateFormHandler.value.businessAddress.address2" beanvalue="Profile.currentUser.businessAddress.address2" size="30" type="text" /></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Stadt, Bundesland, PLZ</span></td>
        <td>
          <dsp:input bean="MultiUserUpdateFormHandler.value.businessAddress.city" beanvalue="Profile.currentUser.businessAddress.city" size="15" type="text" />,&nbsp;
          <dsp:input bean="MultiUserUpdateFormHandler.value.businessAddress.state" beanvalue="Profile.currentUser.businessAddress.state" size="4" type="text" />&nbsp;
          <dsp:input bean="MultiUserUpdateFormHandler.value.businessAddress.postalCode" beanvalue="Profile.currentUser.businessAddress.postalCode" size="8" type="text" /></td>
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb>Land</span></td>
        <td>
          <dsp:select bean="MultiUserUpdateFormHandler.value.businessAddress.country">
             <%@ include file="../common/CountryPicker.jspf" %>
            </dsp:select></td>
      </tr>
    
      <tr>
        <td align=right><span class=smallb>Telefon</span></td>
        <td><dsp:input bean="MultiUserUpdateFormHandler.value.defaultBillingAddress.phoneNumber" beanvalue="Profile.currentUser.defaultBillingAddress.phoneNumber" size="30" type="text"/></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Fax</span></td>
        <td><dsp:input bean="MultiUserUpdateFormHandler.value.defaultBillingAddress.faxNumber" beanvalue="Profile.currentUser.defaultBillingAddress.faxNumber" size="30" type="text"/></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Sprache</span></td>
        <td>

              <dsp:select bean="MultiUserUpdateFormHandler.value.locale">
                                  
              <dsp:droplet name="Switch">
              <dsp:param bean="Profile.currentUser.locale" name="value"/>
              <dsp:oparam name="en_US">

                <dsp:option selected="<%=true%>" value="en_US"/>Englisch
                <dsp:option value="de_DE"/> Deutsch
                <dsp:option value="ja_JP"/> Japaner
              </dsp:oparam>
      
              <dsp:oparam name="de_DE">
                <dsp:option value="en_US"/> Englisch
                <dsp:option selected="<%=true%>" value="de_DE"/> Deutsch
                <dsp:option value="ja_JP"/> Japaner
              </dsp:oparam>

              <dsp:oparam name="ja_JP">
                <dsp:option value="en_US"/> Englisch
                <dsp:option value="de_DE"/> Deutsch
                <dsp:option selected="<%=true%>" value="ja_JP"/> Japaner
              </dsp:oparam>

              </dsp:droplet>
              
              </dsp:select>
       
        </td>
      </tr>
      
      <tr>
        <td><dsp:img src="../images/d.gif"/></td>
	<td><br>
        <dsp:input bean="MultiUserUpdateFormHandler.update" type="submit" value=" Speichern "/> &nbsp;
        <input type="submit" value=" Abbrechen "></td>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/admin/user_edit.jsp#2 $$Change: 651448 $--%>
