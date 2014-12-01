<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<HTML> <HEAD>
<TITLE>Welcome to Quincy Funds</TITLE>
</HEAD>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:setvalue value="Quincy Funds Seminar" param="messageSubject"/>
<!-- 
Set the value of these parameters to valid email addresses

<dsp:setvalue value="Dynamo5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="Dynamo5Team@example.com" param="messageReplyTo"/>
-->
<dsp:setvalue value="Welcome" param="mailingName"/>


<font face="verdana" size=2>Dear
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="Profile.firstname" name="value"/>
  <dsp:oparam name="unset">
     <dsp:droplet name="/atg/dynamo/droplet/Switch">
        <dsp:param bean="Profile.login" name="value"/>
          <dsp:oparam name="unset">
                 Sir or Madam,
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
<p>
Just a quick note to let you know that our exciting seminar series will be visiting your area soon.

For more details, click the following link: 
<dsp:a href='<%=ServletUtil.getRequestURL(request,"../login.jsp?offercode=seminar")%>'>Go to Quincy Funds</dsp:a>
<p>
Sincerly,<br>
Joe B. Juan Canobe<br>
President, Quincy Funds


</BODY> </HTML>

</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/de/email/seminar.jsp#2 $$Change: 651448 $--%>
