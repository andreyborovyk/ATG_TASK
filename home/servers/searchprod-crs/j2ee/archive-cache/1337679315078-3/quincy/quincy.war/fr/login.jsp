<%@ taglib uri="/dspTaglib" prefix="dsp" %> 
<%@ page contentType="text/html; charset=ISO-8859-1" %>
<%@ page import="atg.servlet.*"%> 
<dsp:page>

<html><head><title>Connexion</title></head>

<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:setvalue bean="Profile.offerCode" paramvalue="offercode"/>
<dsp:setvalue bean="ProfileFormHandler.extractDefaultValuesFromProfile" value="false"/>

<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

<div align=center>
<img src="images/banner-login.gif">
<br><br><br>

<dsp:droplet name="Switch">
  <dsp:param bean="Profile.userType" name="value"/>
  <dsp:oparam name="default">
    Vous �tes d�j� connect�. Si vous d�sirez vous connecter en tant que nouvel utilisateur, <dsp:a href="logout.jsp">d�connectez-vous</dsp:a>.
  </dsp:oparam>

  <dsp:oparam name="guest">

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

  <dsp:form action="<%=ServletUtil.getRequestURI(request)%>" method="POST">
  <dsp:input bean="ProfileFormHandler.loginSuccessURL" type="HIDDEN" value="../index.jsp"/>
  <table border=0 cellpadding=4>
    <tr>
      <td colspan=2><font size=+2>Connectez-vous ou <dsp:a href="signup.jsp">inscrivez-vous</dsp:a>.</font></td>
    <tr>
    <tr>
      <td>&nbsp;<br></td>
    </tr>
    <tr>
      <td align=right>Nom d'utilisateur : </td>
      <td><dsp:input bean="ProfileFormHandler.value.login" maxlength="20" size="20" type="text"/></td>
    <tr>
      <td align=right>Mot de passe : </td>
      <td><dsp:input bean="ProfileFormHandler.value.password" type="password"/></td>
    </tr>
    <tr>
      <td></td>
      <td><br><dsp:input bean="ProfileFormHandler.login" type="submit" value=" Connexion "/></td>
    </tr>
  </table>
  </dsp:form>
  <br><br><br>
  <table width=400 border=0>
    <tr>
      <td><font size=-1>
        Remarque : Si vous disposez de la base de donn�es de d�monstration, vous pouvez vous connecter en utilisant les identifiants suivants. Le mot de passe et le nom d'utilisateur sont identiques.
       <p>
       Investisseurs : marc (fran�ais), eric (anglais), sandy (anglais), sally (allemand), frank (japonais)<br>
       Courtiers : jack, airto, hermeta
       </font>
      </td>
    </tr>
  </table> 
  </div>
  
  </dsp:oparam>
</dsp:droplet>
</body>
</html>
<%/* @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/login.jsp#2 $$Change: 651448 $*/%> </dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/login.jsp#2 $$Change: 651448 $--%>
