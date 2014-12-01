<%@ taglib uri="/dspTaglib" prefix="dsp" %> 
<%@ page contentType="text/html; charset=ISO-8859-1" %>
<%@ page import="atg.servlet.*"%> 
<dsp:page>

<dsp:importbean bean="/atg/dynamo/service/CurrentDate"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>


<dsp:img src="images/nav-phonenumber.gif"/>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="Profile.userType" name="value"/>
  <dsp:oparam name="guest">
  </dsp:oparam>
  <dsp:oparam name="default">
    <table border=0 cellpadding=0> 
      <tr>
        <td>&nbsp;&nbsp;</td>
        <td><font color=#ffffff>Bienvenue
        <dsp:droplet name="/atg/dynamo/droplet/Switch">
          <dsp:param bean="Profile.firstname" name="value"/>
          <dsp:oparam name="unset">
            <dsp:valueof bean="Profile.login"/>
          </dsp:oparam>
          <dsp:oparam name="default">
            <dsp:valueof bean="Profile.firstname"/>
          </dsp:oparam>
        </dsp:droplet>
        </font></td>
      </tr>
    </table>
  </dsp:oparam>
</dsp:droplet>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="Profile.usertype" name="value"/>
  
  <dsp:oparam name="broker">
    <dsp:a href="index.jsp"><dsp:img src="images/nav-myhome.gif" border="0" alt="Ma page d'accueil"/></dsp:a>
    <dsp:a href="mypreferences.jsp">
      <dsp:img src="images/nav-mypreferences.gif" border="0" alt="préférences"/></dsp:a>
    <dsp:a href="myprofile.jsp">
      <dsp:img src="images/nav-myprofile.gif" border="0" alt="profil"/></dsp:a>
    <dsp:a href="viewclients.jsp"><dsp:img src="images/nav-myclients.gif" alt="clients" border="0"/></dsp:a> 
    <dsp:a href="fundlist.jsp"><dsp:img src="images/nav-funds.gif" border="0" alt="fonds"/></dsp:a>
    <dsp:a href="email/index.jsp"><dsp:img src="images/nav-email.gif" alt="e-mail" border="0"/></dsp:a>
    <dsp:a href="logout.jsp"><dsp:img src="images/nav-logout.gif" alt="déconnexion" border="0"/></dsp:a>
  </dsp:oparam>

  <dsp:oparam name="investor">
    <!-- this is for known investor -->
    <dsp:a href="index.jsp"><dsp:img src="images/nav-myhome.gif" border="0" alt="Ma page d'accueil"/></dsp:a>
    <dsp:a href="mypreferences.jsp">
      <dsp:img src="images/nav-mypreferences.gif" border="0" alt="préférences"/></dsp:a>
    <dsp:a href="myprofile.jsp"><dsp:img src="images/nav-myprofile.gif" border="0" alt="profil"/></dsp:a> 
    <dsp:a href="fundlist.jsp"><dsp:img src="images/nav-funds.gif" alt="fonds" border="0"/></dsp:a>
    <dsp:a href="newslist.jsp"><dsp:img src="images/nav-news.gif" alt="infos" border="0"/></dsp:a>
    <dsp:a href="featurelist.jsp"><dsp:img src="images/nav-features.gif" alt="infos" border="0"/></dsp:a>
   
    <dsp:a href="logout.jsp">
    <dsp:img src="images/nav-logout.gif" border="0" alt="déconnexion"/></dsp:a>
  </dsp:oparam>
  
  <dsp:oparam name="default">
    <!-- this is for the anonymous investor -->
    <dsp:a href="login.jsp"><dsp:img src="images/nav-login.gif" alt="connexion" border="0"/></dsp:a><br>
    <dsp:a href="signup.jsp"><dsp:img src="images/nav-signup.gif" alt="inscrivez-vous" border="0"/></dsp:a><br>
    <dsp:img src="images/nav-company.gif" alt="société" border="0"/><br>  
    <dsp:a href="fundlist.jsp"><dsp:img src="images/nav-funds.gif" alt="fonds" border="0"/></dsp:a><br> 
    <dsp:a href="newslist.jsp"><dsp:img src="images/nav-news.gif" alt="infos" border="0"/></dsp:a><br>  
    <dsp:a href="featurelist.jsp"><dsp:img src="images/nav-features.gif"
    alt="fonctionnalités" border="0"/></dsp:a><br>   
 </dsp:oparam>
</dsp:droplet>

<br>
<font color="ffffff"><nobr>
<dsp:img src="images/d.gif" width="16" height="1"/><dsp:valueof bean="CurrentDate.timeAsDate" date="EEE-d-MMM"/>
</nobr>
</font><p>
<dsp:img src="images/market-today-1.gif" alt="le marché aujourd'hui"/>
<br>
<center><dsp:a href="index.jsp"><dsp:img src="images/quincy-tree.gif" alt="accueil" border="0"/></dsp:a>
<dsp:img src="images/dynamodriven.gif" alt="Basé sur Dynamo"/></center>
<br><br>
<%/* @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/nav.jsp#2 $$Change: 651448 $ */%> </dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/nav.jsp#2 $$Change: 651448 $--%>
