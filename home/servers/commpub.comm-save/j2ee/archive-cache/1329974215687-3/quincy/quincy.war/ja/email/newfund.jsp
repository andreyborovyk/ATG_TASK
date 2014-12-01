<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<HTML> <HEAD>
<TITLE>Quincy の新しいファンド</TITLE>
</HEAD>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<BODY BGCOLOR="#FFFFFF" VLINK="#637DA6" LINK="#E87F02">
<font face="verdana" size=2>拝啓
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="Profile.firstname" name="value"/>
  <dsp:oparam name="unset">
                      
   </dsp:oparam>

  <dsp:oparam name="default">
    <dsp:valueof bean="Profile.lastName"/>
    <dsp:valueof bean="Profile.firstName"/>,
  </dsp:oparam>
</dsp:droplet>

<p>弊社の商品に、
<dsp:valueof bean="Profile.strategy"/>お客様の投資スタイルに合う新しいファンドが加わりました。 	
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
      投資戦略がありません。
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
<p> 弊社では、お客様の目的に合わせて、ご希望に沿ったファンドを用意しております。 <p>
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
<%/* @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/email/newfund.jsp#2 $$Change: 651448 $*/%> </dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/email/newfund.jsp#2 $$Change: 651448 $--%>
