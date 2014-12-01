<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<HTML> <HEAD>
<TITLE>Quincy Funds へようこそ</TITLE>
</HEAD>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:setvalue value="新商品 - Quincy Funds Growth Fund" param="messageSubject"/>
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
Quincy Funds の優れた投資信託商品ラインアップに、新しく
Quincy Growth Fund を追加いたしました。この画期的な投資信託の詳細については、
<dsp:a href='<%=ServletUtil.getRequestURL(request,"../login.jsp?offercode=growth")%>'>ここをクリックしてください。</dsp:a>.
<p>
敬具<br>
Joe B. Juan Canobe<br>
Quincy Funds 代表取締役社長

</BODY> </HTML>

</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/email/growthfundpromo.jsp#2 $$Change: 651448 $--%>
