<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<%-- If the user hits this page with his or her request locale set to en_US --%>
<%-- we will automatically redirect to the English profile page instead.    --%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="/atg/dynamo/servlet/RequestLocale.locale" name="value"/>
  <dsp:oparam name="en_US">
    <dsp:droplet name="/atg/dynamo/droplet/Redirect">
      <dsp:param name="url" value="../../en/user/my_profile.jsp"/>
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

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="Mein Profil"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
      <dsp:a href="my_account.jsp">Mein Konto</dsp:a> &gt; 
      Mein Profil</td>
  </tr>
  
  <tr valign=top> 
    <td width=55><img src="../images/d.gif" hspace=27></td>

    <!-- main content area -->
    <td valign="top" width=745>  
    <table border=0 cellpadding=4 width=80%>
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>Mein Konto</span></td>
      </tr>

      <tr><td><img src="../images/d.gif" vspace=0></td></tr>

      <tr valign=top>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Kontaktdaten</td></tr></table>
        </td>
      </tr>

      <tr><td><img src="../images/d.gif" vspace=0></td></tr>

      <tr>
        <td align=right><span class=smallb>Name</span></td>
        <td width=75%><dsp:valueof bean="Profile.firstName" /> 
        <dsp:valueof bean="Profile.middleName" /> <dsp:valueof bean="Profile.lastName" /></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Anmeldung</span></td>
        <td width=75%><dsp:valueof bean="Profile.login" /></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>E-Mail</span></td>
        <td><dsp:valueof bean="Profile.email" /></td>
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb>Geschäftsadresse</span></td>
        <td>
          <dsp:getvalueof id="pval0" bean="Profile.businessAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
        </td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Telefon</span></td>
        <td><dsp:valueof bean="Profile.businessAddress.phoneNumber" /></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Fax</span></td>
        <td><dsp:valueof bean="Profile.businessAddress.faxNumber" /></td>
      </tr>
      <tr>
        <td align=right><span class=smallb>Sprache</span></td>
        <td>
          <dsp:droplet name="Switch">
            <dsp:param bean="Profile.locale" name="value"/>
            <dsp:oparam name="en_US">
              Englisch
            </dsp:oparam> 
            <dsp:oparam name="de_DE">
              Deutsch
            </dsp:oparam> 
            <dsp:oparam name="ja_JP">
              Japaner
            </dsp:oparam> 
            <dsp:oparam name="default">
              <dsp:valueof bean="Profile.locale">nicht festgelegt</dsp:valueof>
            </dsp:oparam>
          </dsp:droplet></td>
      </tr>
      <tr>
        <td></td>
        <td><span class=smallb><dsp:a href="edit_profile.jsp">Bearbeiten</dsp:a></span> |
        <span class=smallb><dsp:a href="change_password.jsp">Kennwort ändern</dsp:a></span></td>
      </tr>

      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
  
      <tr>
        <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Unternehmensdaten</td></tr></table>
        </td>
      </tr>

      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
 
      <tr valign=top>
        <td align=right><span class=smallb>Geschäftsbereich</span></td>
        <td><dsp:valueof bean="Profile.parentOrganization.name" /></td>
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb>Standard-Kostenstelle</span></td>
        <td><dsp:valueof bean="Profile.defaultCostCenter.identifier" /> -
             <dsp:valueof bean="Profile.defaultCostCenter.description">n/a</dsp:valueof></td>
      </tr>
            
      <tr valign=top>
        <td align=right><span class=smallb>Standard-Versandanschrift</span></td>
        <td>
          <dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" bean="Profile.defaultShippingAddress"/></dsp:include>
        </td>
      </tr>

      <dsp:droplet name="Switch">
      <dsp:param bean="Profile.invoiceRequestAuthorized" name="value"/>
      <dsp:oparam name="true">
      <tr valign=top>
        <td align=right><span class=smallb>Standard-Rechnungsanschrift</span></td>
        <td>
          <dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" bean="Profile.defaultBillingAddress"/></dsp:include>
        </td>
      </tr>
      </dsp:oparam>
      </dsp:droplet>


      <tr valign=top>
        <td align=right><span class=smallb>Bewilligungen erforderlich</span></td>
        <td><dsp:droplet name="Switch">
            <dsp:param bean="Profile.approvalRequired" name="value"/>
            <dsp:oparam name="false">
              Nein</td>
             </tr>
            </dsp:oparam>
            <dsp:oparam name="true">Ja</td>
              </tr>
              <tr>
                <td align=right><span class=smallb>Einkaufslimit</span></td>
                <td>
                <dsp:getvalueof id="pval0" bean="Profile.orderPriceLimit"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
            </dsp:oparam>
          </dsp:droplet>

      <tr valign=top>
        <td align=right>&nbsp;</td>
        <td><span class=smallb><dsp:a href="edit_company_info.jsp">Bearbeiten</dsp:a></span></td>
      </tr>
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
    </table>

    </td>
  </tr>

</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/user/my_profile.jsp#2 $$Change: 651448 $--%>
