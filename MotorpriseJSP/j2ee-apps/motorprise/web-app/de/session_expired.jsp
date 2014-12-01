<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE"> 

<dsp:include page="common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Sitzung abgelaufen"/></dsp:include>

<div align=center>
<table border="0" cellpadding="0" cellspacing="0" bgcolor="#F7D774">
  <tr>
    <td><dsp:img src="images/splash-motorprise-top.gif"/></td>
  </tr>

  <!--error message indicator bar-->
  <tr>
  <td>
  <dsp:droplet name="Switch">
    <dsp:param bean="Profile.transient" name="value"/>
    <dsp:oparam name="false">
    <table border="0" cellpadding="0" cellspacing="0" width="100%">
      <tr>
      <td bgcolor="#F7D774" align="right" width=40%><dsp:img src="images/d.gif"/></td>
        <td bgcolor="#F7D774" align="right"><dsp:img src="images/splash-motorprise-error-edge.gif"/></td>
        <td bgcolor="#00B2EB" align="right">
        <span class=bw>Sie sind bereits angemeldet als
        <dsp:valueof bean="Profile.firstName" />
        <dsp:valueof bean="Profile.lastName" /></span>&nbsp; &nbsp;
        </td>
      </tr>
      <tr>
        <td bgcolor="#F7D774" height="1" colspan="3"><dsp:img src="images/d.gif"/></td>
      </tr>
    </table>
    </dsp:oparam>
  
  <!-- session expired-->  
    <dsp:oparam name="true"> 
	    <table border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr>
              <td bgcolor="#F7D774" width="40%" align="left"><dsp:img src="images/d.gif"/></td>
              <td bgcolor="#F7D774" align="right"><dsp:img src="images/splash-motorprise-error-edge.gif"/></td>
              <td bgcolor="#00B2EB" align="right">
              <span class=bw><font color="#FFFF00">Sitzung abgelaufen: </font></span><font color="#FFFFFF">Bitte melden Sie sich erneut an, um die Sitzung wiederaufzunehmen. &nbsp;&nbsp;</font></td>
			</tr>
			<tr><td bgcolor="#F7D774"><dsp:img src="images/d.gif"/></td></tr>
	    </table>
    </dsp:oparam>
    </dsp:droplet>
    </td></tr>

  
  
  <dsp:form action="index.jsp">
  <tr valign="middle">
    <td background="images/splash-motorprise-gray-bar.gif">
  <table border="0" cellpadding="4" cellspacing="0" width="100%">
  <tr>
    <td align="center"><dsp:img src="images/splash-motorprise-parts.jpg"/></td>
  <td>

    

    <!-- switch to see if they are logged in -->
    <dsp:droplet name="Switch">
      <dsp:param bean="Profile.transient" name="value"/>
     
      <dsp:oparam name="false">
        <table border=0 cellpadding=0 cellspacing=0 width="100%">
          <tr valign=top>
            <td width="70"><dsp:a href="home.jsp"><dsp:img src="images/splash-motorprise-continue.gif" border="0"/></dsp:a></td>
      <td width="10"><dsp:img src="images/d.gif"/></td>
      <td><dsp:a bean="ProfileFormHandler.logout" href="../index.jsp" value=""><img src="images/splash-motorprise-logout.gif" border="0"></dsp:a></td>
        </tr>
        </table>
      </dsp:oparam>

      <!-- standard log in page -->
      <dsp:oparam name="true"> 
    <table border="0" cellpadding="3" cellspacing="0">
      <tr>
        <td>
      <table border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td><span class=splash>Benutzername: &nbsp;</span></td>
            <td><dsp:input bean="ProfileFormHandler.value.login" beanvalue="Profile.login" size="15" type="text"/></td>
        </tr>
          <tr>
            <td><span class=splash>Kennwort: </span></td>
            <td><dsp:input bean="ProfileFormHandler.value.password" beanvalue="Profile.password" size="15" type="password"/>
            <dsp:input bean="ProfileFormHandler.loginErrorURL" type="hidden" value="index.jsp"/>
                <dsp:input bean="ProfileFormHandler.loginSuccessURL" type="hidden" value="home.jsp"/></td>
          </tr>
      </table></td>
      <td align="center" width="75"><dsp:input bean="ProfileFormHandler.login" border="0" type="image" src="images/splash-motorprise-login.gif"/></td>
        </tr>
    </table>
      </dsp:oparam>
    </dsp:droplet>
    
    </td>
  </tr>
  </dsp:form>
  </table></td>
  </tr>
  <tr>
    <td bgcolor="#F7D774" height="1" colspan="2"><dsp:img src="images/d.gif"/></td></tr>
  <tr>
    <td background="images/splash-motorprise-orange-bar.gif" height="51">
    <table border="0" cellpadding="0" cellspacing="0" width="100%">
      <tr>
      <td width="50%"><dsp:img src="images/d.gif"/></td>
      <td>
      <!-- user will not have option to browse catalog if already logged in-->
      
      <dsp:droplet name="Switch">
      <dsp:param bean="Profile.transient" name="value"/>
        <dsp:oparam name="false">
        <span class=splash><font color="#000000">&nbsp;</font>
        </dsp:oparam>
      
        <dsp:oparam name="true">
          <span class=smallb>Oder melden Sie sich als Gast an und stöbern Sie in unserem Katalog<br>
            auf <dsp:a bean="Profile.locale" href="../en/home.jsp" value="en_US"><font color="#FF3300">Englisch</font></dsp:a>, 
            auf <dsp:a bean="Profile.locale" href="../de/home.jsp" value="de_DE"><font color="#FF3300">Deutsch</font></dsp:a> 
            oder auf <dsp:a bean="Profile.locale" href="../ja/home.jsp" value="ja_JP"><font color="#FF3300">Japaner</font></dsp:a>.
             </span>

            </dsp:oparam>
          </dsp:droplet>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td><dsp:img src="images/splash-motorprise-bottom.gif"/></td>
  </tr>
  <tr>
    <td height=16 bgcolor="#666666" align=middle>
      <dsp:include page="common/Copyright.jsp"></dsp:include>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/session_expired.jsp#2 $$Change: 651448 $--%>
