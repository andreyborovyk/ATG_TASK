<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<HTML> <HEAD>
<TITLE>Quincy Funds へようこそ</TITLE>
</HEAD>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:setvalue value="新規メンバへのお知らせ" param="messageSubject"/>
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
Quincy Funds にご登録いただき、ありがとうございます。弊社のサイトをご覧ください。
弊社の情報サービスおよび弊社でご提供する投資信託は、他の投資情報サービス業界では匹敵するものはないと自負しています。 <p>

弊社のサイトをご覧いただいた方には、初めて
投資を行う方にお勧めの参考資料を特別提供しております。資料のリストをご希望の場合は、
次のリンクをクリックしてください。
<!-- Here's the URL that will take the user to our login page with offercode 001
     The login page takes the offercode from the URL and puts it into the transient profile
		 property offerCode, which is then used to redirect the user to the appropriate offer
		 in the scenario MemberOffers.
     Note that email has not been localized in this demo so we just assume english -->
<dsp:a href='<%=ServletUtil.getRequestURL(request,"../login.jsp?offercode=001")%>'>Quincy Funds のサイトへ</dsp:a>

<p>
ご登録ありがとうございました。<br>
Joe B. Juan Canobe<br>
Quincy Funds 代表取締役社長

</BODY> </HTML>

</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/email/newmemberoffer.jsp#2 $$Change: 651448 $--%>
