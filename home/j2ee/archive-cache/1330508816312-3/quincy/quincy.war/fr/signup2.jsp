<%@ taglib uri="/dspTaglib" prefix="dsp" %> 
<%@ page contentType="text/html; charset=ISO-8859-1" %>
<%@ page import="atg.servlet.*"%> 
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<html><head><title>Inscrivez-vous</title></head>
<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

<div align=center>
<dsp:form action="<%=ServletUtil.getRequestURI(request)%>" method="POST">
<dsp:input bean="ProfileFormHandler.updateSuccessURL" type="HIDDEN" value="index.jsp"/>
<table border=0 cellpadding=4 width=550>
  <tr>
    <td><img src="images/banner-signup.gif"><br><br></td>
  </tr>
  <tr>
    <td colspan=2><font size=+2>Bonjour <dsp:valueof bean="Profile.login"/> </font><br>
    Pour mieux vous connaître et répondre de manière optimale à vos besoins, nous souhaitons recueillir quelques informations vous concernant. Elles nous aideront à vous fournir les informations les plus utiles pour faire fructifier vos investissements.</td>
  </tr>

  <dsp:droplet name="Switch">
    <dsp:param bean="ProfileFormHandler.formError" name="value"/>
    <dsp:oparam name="true">
    
    <dsp:droplet name="ProfileErrorMessageForEach">
      <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
      <dsp:oparam name="output">
        <tr><td align=center><font color=cc0000><b><dsp:valueof param="message"/></b></td></tr>
      </dsp:oparam>
    </dsp:droplet>
    
    </dsp:oparam>
  </dsp:droplet>  
    <dsp:input bean="ProfileFormHandler.value.userType" type="hidden" value="investor"/>

  <tr>
    <td>
    <table border=0 cellpadding=4>
      <tr>  
        <td colspan=2><font size=+1><b>Informations personnelles</b></font></td>
      </tr>
      <tr>
        <td align=right>Prénom </td>
        <td><dsp:input bean="ProfileFormHandler.value.firstname" maxlength="30" size="25" type="text"/></td>
      </tr>
      <tr>
        <td align=right>Nom </td>
        <td><dsp:input bean="ProfileFormHandler.value.lastname" maxlength="30" size="25" type="text"/></td>
      </tr>
      <tr>
        <td align=right>Adresse </td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.address1" maxlength="30" size="25" type="TEXT"/></td>
      </tr>
      <tr>
        <td align=right> </td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.address2" maxlength="30" size="25" type="TEXT"/></td>
      </tr>
      <tr>
        <td valign=middle align=right>Ville </td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.city" maxlength="30" size="25" type="TEXT"/> </td> 
      </tr>
      <tr>
        <td valign=middle align=right>État/Province/Département</td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.state" maxlength="25" size="25" type="TEXT"/></td>
      </tr>
      
      <tr>
        <td align=right>Code postal </td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.postalcode" maxlength="10" size="10" type="TEXT"/></td>
      </tr>

      <tr>
        <td align=right>Pays </td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.country" size="25" type="TEXT"/></td>
      </tr>
      <tr>
        <td align=right>E-mail </td>
        <td><dsp:input bean="ProfileFormHandler.value.email" maxlength="30" size="25" type="text"/></td>
      </tr>
      <tr>
        <td align=right>Date de naissance (dd/mm/yyyy)</td>
        <td><dsp:input bean="ProfileFormHandler.value.dateOfBirth" date="dd/M/yyyy" maxlength="10" size="10" type="text"/></td>
      </tr>
      <tr>
        <td align=right>Sexe </td>
        <td><dsp:input bean="ProfileFormHandler.value.gender" type="radio" value="male"/>Masculin
            <dsp:input bean="ProfileFormHandler.value.gender" type="radio" value="female"/>Féminin
        </td>
      </tr>
      <tr>
        <td valign=top align=right>Choisissez un courtier </td>  
        <td>
        <dsp:droplet name="/atg/targeting/TargetingForEach">
          <dsp:param bean="/atg/registry/RepositoryTargeters/UserProfiles/BrokerList" name="targeter"/>
          <dsp:param name="fireContentEvent" value="false"/>
          <dsp:param name="fireContentTypeEvent" value="false"/>
          <dsp:oparam name="output">
            <dsp:input bean="ProfileFormHandler.value.brokerId" paramvalue="element.repositoryId" type="radio"/>  
            <dsp:valueof param="element.firstname"/>
            <dsp:valueof param="element.lastname"/><br>
          </dsp:oparam>
        </dsp:droplet></td>
      </tr>
      <tr>
        <td align=right>Langue</td>
        <td><dsp:select bean="ProfileFormHandler.value.locale">
              <dsp:option value="en_US"/>Anglais
              <dsp:option value="fr_FR"/>Français
              <dsp:option value="de_DE"/>Allemand
              <dsp:option value="ja_JP"/>Japonais
            </dsp:select>
        </td>
      </tr>  
      <tr>
        <td>Je souhaite recevoir des informations par e-mail.</td>
        <td><dsp:input bean="ProfileFormHandler.value.receiveEmail" type="radio" value="yes"/>Oui
            <dsp:input bean="ProfileFormHandler.value.receiveEmail" type="radio" value="no"/>Non
        </td>
      </tr>
      <tr>
        <td valign=middle align=right></td>
        <td><dsp:input bean="ProfileFormHandler.update" type="SUBMIT" value=" Enregistrer "/>
        <INPUT TYPE="RESET" VALUE=" Redéfinir "></td>
      </tr>
    </table> 
  </td> 
  </tr>
</table>
</dsp:form>
</BODY>
</HTML>
<%/* @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/signup2.jsp#2 $$Change: 651448 $ */%> </dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/signup2.jsp#2 $$Change: 651448 $--%>
