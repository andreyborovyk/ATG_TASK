<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<HTML> <HEAD>
<TITLE>Quincy Funds へようこそ</TITLE>
</HEAD>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:setvalue value="Quincy Funds へようこそ" param="messageSubject"/>
<!-- 
Set the value of these parameters to valid email addresses

<dsp:setvalue value="DynamoTeam@example.com" param="messageFrom"/>
<dsp:setvalue value="DynamoTeam@example.com" param="messageReplyTo"/>
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
Quincy Funds へようこそ。ご参加いただき、ありがとうございました。投資センタとしてお手伝いできることを光栄に思っております。
<p>

Quincy Funds は、一貫してトップのオンライン投資センタとして評価されてきています。
お客様を中心とした弊社の投資情報サービスは、他では見られないものと確信しております。
<p>

Quincy Funds にご参加いただき、ありがとうございました。

<p>
敬具<br>
Joe B. Juan Canobe<br>
代表取締役社長、 <dsp:a href='<%=ServletUtil.getRequestURL(request,"../../index.jsp")%>'>Quincy Funds</dsp:a>

</BODY> </HTML>

</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/email/welcome.jsp#2 $$Change: 651448 $--%>
