<%@ taglib uri="/dspTaglib" prefix="dsp" %> <%@ page import="atg.servlet.*"%> <dsp:page>

<HTML> <HEAD>
<TITLE>Bienvenue chez Quincy Funds</TITLE>
</HEAD>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:setvalue value="Bulletin d'informations mensuel de Quincy" param="messageSubject"/>
<!--
Set the value of these parameters to valid email addresses

<dsp:setvalue value="Dynamo5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="Dynamo5Team@example.com" param="messageReplyTo"/>
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
{Les informations mensuelles seront placées ici, via l'édition directe de ce fichier ou l'insertion d'un article de contenu.}
<p>
Cordialement,<br>
Joe B. Juan Canobe<br>
Président de <dsp:a href='<%=ServletUtil.getRequestURL(request,"../../index.jsp")%>'>Quincy Funds</dsp:a></BODY> </HTML>

<%/* @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/email/monthlynewsletter.jsp#2 $$Change: 651448 $*/%> </dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/email/monthlynewsletter.jsp#2 $$Change: 651448 $--%>
