<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/service/CurrentDate"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>


<dsp:img src="images/nav-phonenumber.gif"/>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="Profile.userType" name="value"/>
  <dsp:oparam name="guest">
  </dsp:oparam>
  <dsp:oparam name="default">
    <table border=0 cellpadding=0> 
      <tr>
        <td>&nbsp;&nbsp;</td>
        <td><font color=#ffffff>Welcome
        <dsp:droplet name="/atg/dynamo/droplet/Switch">
          <dsp:param bean="Profile.firstname" name="value"/>
          <dsp:oparam name="unset">
            <dsp:valueof bean="Profile.login"/>
          </dsp:oparam>
          <dsp:oparam name="default">
            <dsp:valueof bean="Profile.firstname"/>
          </dsp:oparam>
        </dsp:droplet>
        </font></td>
      </tr>
    </table>
  </dsp:oparam>
</dsp:droplet>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="Profile.usertype" name="value"/>
  
  <dsp:oparam name="broker">
    <dsp:a href="index.jsp"><dsp:img src="images/nav-myhome.gif" border="0" alt="My Home"/></dsp:a>
    <dsp:a href="mypreferences.jsp">
      <dsp:img src="images/nav-mypreferences.gif" border="0" alt="preferences"/></dsp:a>
    <dsp:a href="myprofile.jsp">
      <dsp:img src="images/nav-myprofile.gif" border="0" alt="profile"/></dsp:a>
    <dsp:a href="viewclients.jsp"><dsp:img src="images/nav-myclients.gif" alt="clients" border="0"/></dsp:a> 
    <dsp:a href="fundlist.jsp"><dsp:img src="images/nav-funds.gif" border="0" alt="funds"/></dsp:a>
    <dsp:a href="email/index.jsp"><dsp:img src="images/nav-email.gif" alt="email" border="0"/></dsp:a>
    <dsp:a href="logout.jsp"><dsp:img src="images/nav-logout.gif" alt="log out" border="0"/></dsp:a>
  </dsp:oparam>

  <dsp:oparam name="investor">
    <!-- this is for known investor -->
    <dsp:a href="index.jsp"><dsp:img src="images/nav-myhome.gif" border="0" alt="My Home"/></dsp:a>
    <dsp:a href="mypreferences.jsp">
      <dsp:img src="images/nav-mypreferences.gif" border="0" alt="preferences"/></dsp:a>
    <dsp:a href="myprofile.jsp"><dsp:img src="images/nav-myprofile.gif" border="0" alt="profile"/></dsp:a> 
    <dsp:a href="fundlist.jsp"><dsp:img src="images/nav-funds.gif" alt="funds" border="0"/></dsp:a>
    <dsp:a href="newslist.jsp"><dsp:img src="images/nav-news.gif" alt="news" border="0"/></dsp:a>
    <dsp:a href="featurelist.jsp"><dsp:img src="images/nav-features.gif" alt="news" border="0"/></dsp:a>
   
    <dsp:a href="logout.jsp">
    <dsp:img src="images/nav-logout.gif" border="0" alt="log out"/></dsp:a>
  </dsp:oparam>
  
  <dsp:oparam name="default">
    <!-- this is for the anonymous investor -->
    <dsp:a href="login.jsp"><dsp:img src="images/nav-login.gif" alt="log in" border="0"/></dsp:a><br>
    <dsp:a href="signup.jsp"><dsp:img src="images/nav-signup.gif" alt="sign up" border="0"/></dsp:a><br>
    <dsp:img src="images/nav-company.gif" alt="company" border="0"/><br>  
    <dsp:a href="fundlist.jsp"><dsp:img src="images/nav-funds.gif" alt="funds" border="0"/></dsp:a><br> 
    <dsp:a href="newslist.jsp"><dsp:img src="images/nav-news.gif" alt="news" border="0"/></dsp:a><br>  
    <dsp:a href="featurelist.jsp"><dsp:img src="images/nav-features.gif"
    alt="features" border="0"/></dsp:a><br>   
 </dsp:oparam>
</dsp:droplet>

<br>
<font color="ffffff"><nobr>
<dsp:img src="images/d.gif" width="16" height="1"/><dsp:valueof bean="CurrentDate.timeAsDate" date="EEE, MMM d"/>
</nobr>
</font><p>
<dsp:img src="images/market-today-1.gif" alt="market today"/>
<br>
<center><dsp:a href="index.jsp"><dsp:img src="images/quincy-tree.gif" alt="home" border="0"/></dsp:a>
<dsp:img src="images/dynamodriven.gif" alt="Dynamo Driven"/></center>
<br><br>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/en/nav.jsp#2 $$Change: 651448 $--%>
