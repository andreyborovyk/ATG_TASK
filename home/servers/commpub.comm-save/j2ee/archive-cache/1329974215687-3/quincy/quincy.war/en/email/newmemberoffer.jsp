<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<HTML> <HEAD>
<TITLE>Welcome to Quincy Funds</TITLE>
</HEAD>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:setvalue value="New member offer" param="messageSubject"/>
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
We're very excited that you signed up with Quincy Funds.  Please come visit us - we think our
information services and fund offerings are unequaled in the financial services sector. <p>

As a further incentive to visit our site, we'd like to extend the following special offer.  We've put
together a collection of suggested reading for beginning investors.  To review the list, simply
click the following link:
<!-- Here's the URL that will take the user to our login page with offercode 001
     The login page takes the offercode from the URL and puts it into the transient profile
		 property offerCode, which is then used to redirect the user to the appropriate offer
		 in the scenario MemberOffers.
     Note that email has not been localized in this demo so we just assume english -->
<dsp:a href='<%=ServletUtil.getRequestURL(request,"../login.jsp?offercode=001")%>'>Go to Quincy Funds</dsp:a>

<p>
Thank you for your support,<br>
Joe B. Juan Canobe<br>
President, Quincy Funds

</BODY> </HTML>

</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/en/email/newmemberoffer.jsp#2 $$Change: 651448 $--%>
