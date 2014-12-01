<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
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
        <td><font color=#ffffff>ようこそ
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
    <dsp:a href="index.jsp"><dsp:img src="images/nav-myhome.gif" border="0" alt="私のホーム"/></dsp:a>
    <dsp:a href="mypreferences.jsp">
      <dsp:img src="images/nav-mypreferences.gif" border="0" alt="基本設定"/></dsp:a>
    <dsp:a href="myprofile.jsp">
      <dsp:img src="images/nav-myprofile.gif" border="0" alt="プロファイル"/></dsp:a>
    <dsp:a href="viewclients.jsp"><dsp:img src="images/nav-myclients.gif" alt="クライアント" border="0"/></dsp:a> 
    <dsp:a href="fundlist.jsp"><dsp:img src="images/nav-funds.gif" border="0" alt="ファンド"/></dsp:a>
    <dsp:a href="email/index.jsp"><dsp:img src="images/nav-email.gif" alt="電子メール" border="0"/></dsp:a>
    <dsp:a href="logout.jsp"><dsp:img src="images/nav-logout.gif" alt="ログアウト" border="0"/></dsp:a>
  </dsp:oparam>

  <dsp:oparam name="investor">
    <!-- this is for known investor -->
    <dsp:a href="index.jsp"><dsp:img src="images/nav-myhome.gif" border="0" alt="私のホーム"/></dsp:a>
    <dsp:a href="mypreferences.jsp">
      <dsp:img src="images/nav-mypreferences.gif" border="0" alt="基本設定"/></dsp:a>
    <dsp:a href="myprofile.jsp"><dsp:img src="images/nav-myprofile.gif" border="0" alt="プロファイル"/></dsp:a> 
    <dsp:a href="fundlist.jsp"><dsp:img src="images/nav-funds.gif" alt="ファンド" border="0"/></dsp:a>
    <dsp:a href="newslist.jsp"><dsp:img src="images/nav-news.gif" alt="ニュース" border="0"/></dsp:a>
    <dsp:a href="featurelist.jsp"><dsp:img src="images/nav-features.gif" alt="ニュース" border="0"/></dsp:a>
   
    <dsp:a href="logout.jsp">
    <dsp:img src="images/nav-logout.gif" border="0" alt="ログアウト"/></dsp:a>
  </dsp:oparam>
  
  <dsp:oparam name="default">
    <!-- this is for the anonymous investor -->
    <dsp:a href="login.jsp"><dsp:img src="images/nav-login.gif" alt="ログイン" border="0"/></dsp:a><br>
    <dsp:a href="signup.jsp"><dsp:img src="images/nav-signup.gif" alt="登録" border="0"/></dsp:a><br>
    <dsp:img src="images/nav-company.gif" alt="会社" border="0"/><br>  
    <dsp:a href="fundlist.jsp"><dsp:img src="images/nav-funds.gif" alt="ファンド" border="0"/></dsp:a><br> 
    <dsp:a href="newslist.jsp"><dsp:img src="images/nav-news.gif" alt="ニュース" border="0"/></dsp:a><br>  
    <dsp:a href="featurelist.jsp"><dsp:img src="images/nav-features.gif"
    alt="特別提供品" border="0"/></dsp:a><br>   
 </dsp:oparam>
</dsp:droplet>

<br>
<font color="ffffff"><nobr>
<dsp:img src="images/d.gif" width="16" height="1"/><dsp:valueof bean="CurrentDate.timeAsDate" date="MM'月' dd'日' '('E')'"/>
</nobr>
</font><p>
<dsp:img src="images/market-today-1.gif" alt="今日のマーケット"/>
<br>
<center><dsp:a href="index.jsp"><dsp:img src="images/quincy-tree.gif" alt="ホーム" border="0"/></dsp:a>
<dsp:img src="images/dynamodriven.gif" alt="Dynamo Driven"/></center>
<br><br>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/nav.jsp#2 $$Change: 651448 $--%>
