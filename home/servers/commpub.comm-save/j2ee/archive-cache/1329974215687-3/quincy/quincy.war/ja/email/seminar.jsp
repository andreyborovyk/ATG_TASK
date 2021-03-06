<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<HTML> <HEAD>
<TITLE>Quincy Funds へようこそ</TITLE>
</HEAD>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:setvalue value="Quincy Funds セミナー" param="messageSubject"/>
<!-- 
Set the value of these parameters to valid email addresses
<dsp:setvalue value="Dynamo5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="Dynamo5Team@example.com" param="messageReplyTo"/>
-->
<dsp:setvalue value="Welcome" param="mailingName"/>


<font face="verdana" size=2>拝啓
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="Profile.firstname" name="value"/>
  <dsp:oparam name="unset">
     <dsp:droplet name="/atg/dynamo/droplet/Switch">
        <dsp:param bean="Profile.login" name="value"/>
          <dsp:oparam name="unset">
                                    
          </dsp:oparam>
          <dsp:oparam name="default">
             <dsp:valueof bean="Profile.login"/>,
          </dsp:oparam>
     </dsp:droplet>

   </dsp:oparam>
  <dsp:oparam name="default">
    <dsp:valueof bean="Profile.lastName"/>
    <dsp:valueof bean="Profile.firstName"/>,
  </dsp:oparam>
</dsp:droplet>

<p>
<p>
お客様の地域で、投資計画に役立つ弊社のセミナーを近々開催いたしますのでご連絡いたします。

詳細については、次のリンクをクリックしてください。 
<dsp:a href='<%=ServletUtil.getRequestURL(request,"../login.jsp?offercode=seminar")%>'>Quincy Funds のサイトへ</dsp:a>
<p>
敬具<br>
Joe B. Juan Canobe<br>
Quincy Funds 代表取締役社長


</BODY> </HTML>

</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/email/seminar.jsp#2 $$Change: 651448 $--%>
