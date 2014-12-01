<%@ taglib uri="/dspTaglib" prefix="dsp" %> 
<%@ page contentType="text/html; charset=ISO-8859-1" %>
<%@ page import="atg.servlet.*"%> 
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<html><head><title>Mes préférences</title></head>
<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

<dsp:droplet name="Switch">
  <dsp:param bean="ProfileFormHandler.profile.userType" name="value"/>
  <!-- not logged in -->
  <dsp:oparam name="guest">
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
            <td> <h2>Mes préférences</h2>
              Vous n'êtes pas connecté. Si vous désirez modifier vos préférences, <dsp:a href="login.jsp">connectez-vous</dsp:a>.</td>
          </tr>
        </table></td>
      </tr>
    </table>
  </dsp:oparam>

  <dsp:oparam name="default">

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
       <td><h2>Mes préférences</h2>
      <dsp:droplet name="Switch">
        <dsp:param bean="ProfileFormHandler.formError" name="value"/>
        <dsp:oparam name="true">
          <font color=cc0000><STRONG><UL>
          <dsp:droplet name="ProfileErrorMessageForEach">
            <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
            <dsp:oparam name="output">
              <LI><dsp:valueof param="message"/>
            </dsp:oparam>
          </dsp:droplet>
        </UL></STRONG></font>
      </dsp:oparam>
    </dsp:droplet>
        

        <dsp:form action="<%=ServletUtil.getRequestURI(request)%>" method="POST">
        <dsp:input bean="ProfileFormHandler.updateSuccessURL" type="HIDDEN" value="../index.jsp"/>

        <table border=0 cellpadding=4>

          <tr valign=top>
            <td align=right>Rubriques d'infos </td>
             <td><dsp:input bean="ProfileFormHandler.value.interests" type="checkbox" value="market"/>Infos boursières générales<BR>
             <dsp:input bean="ProfileFormHandler.value.interests" type="checkbox" value="international"/>Marchés internationaux <BR>
             <dsp:input bean="ProfileFormHandler.value.interests" type="checkbox" value="growth"/>Marchés en croissance<BR>
             <dsp:input bean="ProfileFormHandler.value.interests" type="checkbox" value="tax"/>Taxes</td>
          </tr>
          <tr valign=top>
            <td align=right>Page d'accueil </td>
            <td> Afficher <dsp:input bean="ProfileFormHandler.value.numberNewsItems" maxlength="2" size="2" type="text"/> nouveaux articles.<br>
             Afficher <dsp:input bean="ProfileFormHandler.value.numberFeatureItems" maxlength="2" size="2" type="text"/> présentations.</td>
          </tr>
          <tr valign=top>   
            <td align=right>Courtier </td>
            <td>
            <dsp:droplet name="/atg/targeting/TargetingForEach">
              <dsp:param bean="/atg/registry/RepositoryTargeters/UserProfiles/BrokerList" name="targeter"/>
              <dsp:param name="fireContentEvent" value="false"/>
              <dsp:param name="fireContentTypeEvent" value="false"/>
              <dsp:oparam name="output">
                 <dsp:input bean="ProfileFormHandler.value.brokerId" paramvalue="element.repositoryId" type="radio"/>
                 <dsp:valueof param="element.firstName"/> 
                 <dsp:valueof param="element.lastName"/><br>
             </dsp:oparam>
            </dsp:droplet>
            </td>
          </tr>
          <tr>
            <td align=right>Langue </td>
            <td><dsp:select bean="ProfileFormHandler.value.locale">
                 <dsp:option value="en_US"/>Anglais
                 <dsp:option value="fr_FR"/>Français
                 <dsp:option value="de_DE"/>Allemand
                 <dsp:option value="ja_JP"/>Japonais
               </dsp:select>
            </td>
          </tr>  
          <tr>
            <td align=right>Je souhaite recevoir des<br>informations par e-mail.</td>
        <td><dsp:input bean="ProfileFormHandler.value.receiveEmail" type="radio" value="yes"/>Oui
            <dsp:input bean="ProfileFormHandler.value.receiveEmail" type="radio" value="no"/>Non
        </td>
          </tr>

          <tr>
             <td></td>
             <td><br><dsp:input bean="ProfileFormHandler.update" type="submit" value=" Enregistrer "/>
             <input type="reset" value=" Redéfinir "></td>
           </tr>
         </table>
         </dsp:form>
         </td>
       </tr>
     </table>
     </td>
      </tr>
    </table>
  </dsp:oparam>
</dsp:droplet>
</body>
</html>
<%/* @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/mypreferences.jsp#2 $$Change: 651448 $ */%> </dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/mypreferences.jsp#2 $$Change: 651448 $--%>
