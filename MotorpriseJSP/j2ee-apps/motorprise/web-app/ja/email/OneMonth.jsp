<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<!-- Title: OneMonthEmail -->
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:setvalue value="次回のオーダーから 10% 割引です。" param="messageSubject"/>
<%-- 
<dsp:setvalue value="orders@example.com" param="messageFrom"/>
<dsp:setvalue value="orders@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue beanvalue="Profile.email" param="messageTo"/>
<dsp:setvalue value="OneMonth" param="mailingName"/>





<p> <dsp:valueof bean="Profile.firstName">お客様へ</dsp:valueof>
<dsp:valueof bean="Profile.lastName"/>、
<p>

お客様より最後に<dsp:valueof bean="Profile.parentOrganization.name">会社</dsp:valueof>でのご注文を頂いてから１か月が経過いたしました。弊社では、お客様の会社のニーズを常に満たしたいと考えております。そこで、お客様の次回のご注文に 10% の値引きをさせていただくことにいたしました。割引は、次回お客様がログインし、ご注文の時点で自動的に適用されます。 


<p>敬具<br>
Motorprise, Inc. 


</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/email/OneMonth.jsp#2 $$Change: 651448 $--%>
