<%@ taglib uri="/dspTaglib" prefix="dsp" %> 
<%@ page contentType="text/html; charset=ISO-8859-1" %>
<%@ page import="atg.servlet.*"%> 
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<HTML><HEAD><TITLE>Mon profil</TITLE></HEAD>
<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

<dsp:droplet name="Switch">
  <dsp:param bean="ProfileFormHandler.profile.userType" name="value"/>
  <!-- if not logged in -->
  <dsp:oparam name="guest">
    <table border=0 cellpadding=4 cellspacing=0 width=400>
      <tr valign=top>
        <td width=120 bgcolor=#003366 rowspan=2>
        <!-- left bar navigation -->
        <dsp:include page="nav.jsp" ></dsp:include></td>
        <td>
        <table border=0>
          <tr>
            <td colspan=2><img src="images/banner-editgoals.gif"></td>
          </tr>
          <tr valign=top>
            <td><h2>Mon profil</h2>
            Vous n'Ítes pas connectÅE Si vous dÈsirez modifier votre profil, <dsp:a href="login.jsp">connectez-vous</dsp:a>.</td>
          </tr>
        </table></td>
      </tr>
    </table>
  </dsp:oparam>

<dsp:oparam name="default">
<!-- Use the pathInfo of this request as the action for the form -->
<dsp:form action="<%=ServletUtil.getRequestURI(request)%>" method="POST">
<dsp:input bean="ProfileFormHandler.updateSuccessURL" type="HIDDEN" value="index.jsp"/>
<table border=0 cellpadding=4 cellspacing=0>
  <tr valign=top>
    <td width=120 bgcolor=#003366 rowspan=2>
    <!-- left bar navigation -->
    <dsp:include page="nav.jsp" ></dsp:include></td>
      
    <td>
    <table border=0>
      <tr>
        <td colspan=2><img src="images/banner-editgoals.gif"></td>
      </tr>
      <tr valign=top>
        <td><h2>Mon profil</h2>

        <dsp:droplet name="Switch">
          <dsp:param bean="ProfileFormHandler.formError" name="value"/>
          <dsp:oparam name="true">
            <font color=cc0000><STRONG><UL>
            <dsp:droplet name="ProfileErrorMessageForEach">
              <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
              <dsp:oparam name="output">
                <LI> <dsp:valueof param="message"/>
              </dsp:oparam>
            </dsp:droplet>
            </UL></STRONG></font>
          </dsp:oparam>
        </dsp:droplet>

        <!--
           This ensures that we update the same profile that we edit.
           It guards against the session expiring while this form is
           displayed (and updating the anonymous profile) or the user
           logging out and in again as a different user in a different
           window
          -->

        <dsp:input bean="ProfileFormHandler.updateRepositoryId" beanvalue="ProfileFormHandler.repositoryId" type="HIDDEN"/>

        <table border=0 cellpadding=2>
          <tr>
            <td align=right>PrÈnom </td>
            <td><dsp:input bean="ProfileFormHandler.value.firstName" maxlength="35" size="30" type="TEXT"/></td>
          </tr>

          <tr>
            <td align=right>Nom </td>
            <td><dsp:input bean="ProfileFormHandler.value.lastName" maxlength="30" size="30" type="TEXT"/></td>
          </tr>

          <tr>
            <td align=right>E-mail </td>
            <td><dsp:input bean="ProfileFormHandler.value.email" maxlength="30" size="30" type="TEXT"/></td>
          </tr>

          <tr>
            <td align=right>Adresse </td>
            <td><dsp:input bean="ProfileFormHandler.value.homeAddress.address1" maxlength="30" size="30" type="TEXT"/></td>
          </tr>

          <tr>
            <td align=right></td>
            <td><dsp:input bean="ProfileFormHandler.value.homeAddress.address2" maxlength="30" size="30" type="TEXT"/></td>
          </tr>
          <tr>
            <td align=right>Ville </td>
            <td><dsp:input bean="ProfileFormHandler.value.homeAddress.city" maxlength="30" size="30" type="TEXT"/></td>
          </tr>

          <tr valign=top>
            <td align=right>…tat/Province/DÈpartement </td>
            <td><dsp:input bean="ProfileFormHandler.value.homeAddress.state" maxlength="30" size="30" type="TEXT"/></td>
          </tr>

          <tr>
            <td align=right>Code postal </td>
            <td><dsp:input bean="ProfileFormHandler.value.homeAddress.postalCode" maxlength="10" size="10" type="TEXT"/></td>
          </tr>
          <tr>
            <td align=right>Pays </td>
            <td><dsp:input bean="ProfileFormHandler.value.homeAddress.country" maxlength="30" size="30" type="TEXT"/></td>
          </tr>

          <tr>
            <td align=right>NumÈro de tÈlÈphone </td>
            <td><dsp:input bean="ProfileFormHandler.value.homeAddress.phoneNumber" maxlength="20" size="30" type="TEXT"/>
            </td>
          </tr>

      <tr>
        <td align=right>Date de naissance (dd/mm/yyyy)</td>
        <td><dsp:input bean="ProfileFormHandler.value.dateOfBirth" date="dd/M/yyyy" maxlength="10" size="10" type="text"/></td>
      </tr>
   
          <tr>
            <td align=right>Sexe </td>
            <td>
            <dsp:input bean="ProfileFormHandler.value.gender" type="radio" value="male"/>Masculin
            <dsp:input bean="ProfileFormHandler.value.gender" type="radio" value="female"/>FÈminin
            </td>
          </tr>

          <tr>
            <td align=right>Type d'utilisateur </td>
            <td>
            <dsp:input bean="ProfileFormHandler.value.userType" type="radio" value="investor"/>Investisseur
            <dsp:input bean="ProfileFormHandler.value.userType" type="radio" value="broker"/>Courtier
            </td>
          </tr>

          <tr>
            <td align=right>Indice offensif </td>
            <td>
            <dsp:input bean="ProfileFormHandler.value.aggressiveIndex" maxlength="5" size="5" type="TEXT"/></td>
          </tr>
<dsp:droplet name="Switch">
  <dsp:param bean="ProfileFormHandler.profile.userType" name="value"/>
	<dsp:oparam name="investor">
	     <tr>
            <td align=right>Valeur des actifs </td>
            <td>
            <dsp:input bean="ProfileFormHandler.profile.assetValue" maxlength="8" size="8" type="TEXT"/></td>
          </tr>
	</dsp:oparam>
   	<dsp:oparam name="broker">
	     <tr>
            <td align=right>Commission (%) </td>
            <td>
            <dsp:input bean="ProfileFormHandler.profile.commissionPercentage" maxlength="5" size="5" type="TEXT"/></td>
          </tr>
	</dsp:oparam>
</dsp:droplet>


          <tr valign=bottom>
            <td align=right></td>
            <td><br><dsp:input bean="ProfileFormHandler.update" type="SUBMIT" value=" Enregistrer "/>
            <INPUT TYPE="reset" VALUE=" RedÈfinir "></td>
          </tr>

        </table>
        </td>
      </tr>
    </table>
    </td>
  </tr>
</table>
</dsp:form>
</dsp:oparam>
</dsp:droplet>
</BODY>
</HTML>
<%/* @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/myprofile.jsp#2 $$Change: 651448 $*/%> </dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/myprofile.jsp#2 $$Change: 651448 $--%>
