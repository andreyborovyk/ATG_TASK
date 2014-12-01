<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<!-- Title: OneMonthEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>


<dsp:setvalue value="次回のオーダーから 10% 割引です。" param="messageSubject"/>
<%-- 
<dsp:setvalue value="orders@example.com" param="messageFrom"/>
<dsp:setvalue value="orders@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue paramvalue="message.profile.email" param="messageTo"/>
<dsp:setvalue value="25% Over Average" param="mailingName"/>


<dsp:setvalue paramvalue="message.profile" param="recipient"/>


<p> <dsp:valueof param="recipient.firstName">お客様へ</dsp:valueof><dsp:valueof param="recipient.lastName"/>、

<p>先日は、これまでにも増して多くのご注文をいただき、ありがとうございました。今後も変わらぬご愛顧を賜りますよう、次回のご注文では 10% 割引とさせていただきます。

<p>敬具<br>
Motorprise, Inc. 
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/email/25OverAverage.jsp#2 $$Change: 651448 $--%>
