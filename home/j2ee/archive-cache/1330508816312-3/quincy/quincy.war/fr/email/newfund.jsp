<%@ taglib uri="/dspTaglib" prefix="dsp" %> <dsp:page>

<HTML> <HEAD>
<TITLE>Nouveau fond Quincy Funds</TITLE>
</HEAD>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<BODY BGCOLOR="#FFFFFF" VLINK="#637DA6" LINK="#E87F02">
<font face="verdana" size=2>Cher/Chère
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="Profile.firstname" name="value"/>
  <dsp:oparam name="unset">
   Monsieur ou Madame,
  </dsp:oparam>

  <dsp:oparam name="default">
    <dsp:valueof bean="Profile.firstName"/>
    <dsp:valueof bean="Profile.lastName"/>,
  </dsp:oparam>
</dsp:droplet>

<p>J'ai le plaisir de vous présenter un nouveau fond qui correspondra à votre profil d'investisseur <dsp:valueof bean="Profile.strategy"/>. 	
<p>

<dsp:droplet name="/atg/targeting/TargetingFirst">
  <dsp:param bean="/atg/registry/RepositoryTargeters/Email/NewFundEmail" name="targeter"/>
  <dsp:param name="fireContentEvent" value="false"/>
  <dsp:param name="fireContentTypeEvent" value="false"/>
  <dsp:param name="howMany" value="1"/>
  <dsp:oparam name="output">
  <dsp:droplet name="/atg/dynamo/droplet/Switch">
    <dsp:param bean="Profile.strategy" name="value"/>
    <dsp:oparam name="unset">
      Aucune stratégie d'investissement.
    </dsp:oparam>
    <dsp:oparam name="default">
      <pre><dsp:valueof param="element.ConsContent"/></pre> 
    </dsp:oparam>
    <dsp:oparam name="conservative">
      <pre><dsp:valueof param="element.ConsContent"/></pre>
    </dsp:oparam>
    <dsp:oparam name="aggressive">
      <pre><dsp:valueof param="element.AggContent"/></pre>
    </dsp:oparam>
  </dsp:droplet>

  </dsp:oparam>
</dsp:droplet>
<p>
Notre gamme complète de fonds sait répondre à tous vos objectifs en matière d'investissements.
<p>
<dsp:droplet name="/atg/targeting/TargetingFirst">
  <dsp:param bean="/atg/registry/RepositoryTargeters/Email/NewFundEmail" name="targeter"/>
  <dsp:param name="fireContentEvent" value="false"/>
  <dsp:param name="fireContentTypeEvent" value="false"/>
  <dsp:param name="howMany" value="1"/>
  <dsp:oparam name="output">
    <pre><dsp:valueof param="element.BrokerSignature"/></pre>
  </dsp:oparam>
</dsp:droplet>
</font>
</body>
</html>
<%/* @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/email/newfund.jsp#2 $$Change: 651448 $*/%> </dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/email/newfund.jsp#2 $$Change: 651448 $--%>
