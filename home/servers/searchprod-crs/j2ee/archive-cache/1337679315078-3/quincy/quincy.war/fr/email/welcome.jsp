<%@ taglib uri="/dspTaglib" prefix="dsp" %> <%@ page import="atg.servlet.*"%> <dsp:page>

<HTML> <HEAD>
<TITLE>Bienvenue chez Quincy Funds</TITLE>
</HEAD>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:setvalue value="Bienvenue chez Quincy Funds" param="messageSubject"/>
<!-- 
Set the value of these parameters to valid email addresses

<dsp:setvalue value="DynamoTeam@example.com" param="messageFrom"/>
<dsp:setvalue value="DynamoTeam@example.com" param="messageReplyTo"/>
-->
<dsp:setvalue value="Welcome" param="mailingName"/>


<font face="verdana" size=2>Cher/Chère
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="Profile.firstname" name="value"/>
  <dsp:oparam name="unset">
     <dsp:droplet name="/atg/dynamo/droplet/Switch">
        <dsp:param bean="Profile.login" name="value"/>
          <dsp:oparam name="unset">
                 Monsieur ou Madame,
          </dsp:oparam>
          <dsp:oparam name="default">
             <dsp:valueof bean="Profile.login"/>,
          </dsp:oparam>
     </dsp:droplet>

   </dsp:oparam>
  <dsp:oparam name="default">
    <dsp:valueof bean="Profile.firstName"/>
    <dsp:valueof bean="Profile.lastName"/>,
  </dsp:oparam>
</dsp:droplet>

<p>
Bienvenue chez Quincy Funds. Nous sommes heureux de vous accueillir et espérons devenir bientôt votre centre d'investissements.<p>

Quincy Funds a souvent été reconnu comme l'un des centres d'investissements en ligne les plus performants. Je suis sûr que nos services financiers performants sauront répondre à l'ensemble de vos besoins.<p>

Merci encore d'avoir choisi Quincy Funds.

<p>
Cordialement,<br>
Joe B. Juan Canobe<br>
Président de <dsp:a href='<%=ServletUtil.getRequestURL(request,"../../index.jsp")%>'>Quincy Funds</dsp:a></BODY> </HTML>

<%/* @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/email/welcome.jsp#2 $$Change: 651448 $*/%> </dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/email/welcome.jsp#2 $$Change: 651448 $--%>
