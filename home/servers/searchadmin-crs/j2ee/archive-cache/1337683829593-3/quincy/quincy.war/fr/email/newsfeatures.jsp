<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<HTML> <HEAD>
<TITLE>Dernières infos et fonctionnalités de Quincy Funds</TITLE>
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

<p>

<dsp:droplet name="/atg/targeting/TargetingFirst">
  <dsp:param bean="/atg/registry/RepositoryTargeters/Email/newsfeatureemail" name="targeter"/>
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
<!-- news -->
<b>Infos :</b>
<ul>
<dsp:droplet name="/atg/targeting/TargetingRange">
  <dsp:param bean="/atg/registry/RepositoryTargeters/News/NewsEmail" name="targeter"/>
  <dsp:param name="fireContentEvent" value="false"/>
  <dsp:param name="fireContentTypeEvent" value="false"/>
  <dsp:param bean="Profile.numbernewsitems" name="howMany"/>
  <dsp:param name="sortProperties" value="-date"/>

  <dsp:oparam name="output">
   <li><dsp:a href='<%=atg.servlet.ServletUtil.getRequestURL(request, "../news.jsp")%>'>
    <dsp:param name="ElementId" param="element.repositoryId"/>
    <dsp:valueof param="element.headline"/></dsp:a>
  </dsp:oparam>
  <dsp:oparam name="empty">  
   <li> Aucune info aujourd'hui !
  </dsp:oparam>
</dsp:droplet>
</ul>

<b>Fonctionnalités :</b>
<ul>
   <dsp:droplet name="/atg/targeting/TargetingRange">
     <dsp:param bean="/atg/registry/RepositoryTargeters/Features/FeaturesEmail" name="targeter"/>
     <dsp:param name="fireContentEvent" value="false"/>
     <dsp:param name="fireContentTypeEvent" value="false"/>
     <dsp:param bean="Profile.numberfeatureitems" name="howMany"/>
     <dsp:param name="sortProperties" value="+title"/>

     <dsp:oparam name="output">
       <li><b><dsp:a href='<%=atg.servlet.ServletUtil.getRequestURL(request, "../feature.jsp")%>'> 
       <dsp:param name="ElementId" param="element.repositoryId"/>
       <dsp:valueof param="element.title"/></dsp:a></b><br>
       <dsp:valueof param="element.headline"/></li>
     </dsp:oparam>
     <dsp:oparam name="empty">
       <li><font size=2>Aucune fonctionnalité aujourd'hui !</li>
     </dsp:oparam>
   </dsp:droplet>
</ul>

<p><br>
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
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/email/newsfeatures.jsp#2 $$Change: 651448 $--%>
