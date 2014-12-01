<%@ taglib uri="dsp" prefix="dsp"%>
<dsp:page>

<dsp:setvalue bean="/atg/dynamo/servlet/RequestLocale.refresh" value=""/>

<%-- If the user hits this page with his or her request locale set to en_US --%>
<%-- we will automatically redirect to the English home page instead.       --%>

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="/atg/dynamo/servlet/RequestLocale.locale" name="value"/>
  <dsp:oparam name="en_US">
    <dsp:droplet name="/atg/dynamo/droplet/Redirect">
      <dsp:param name="url" value="../en/index.jsp"/>
    </dsp:droplet>
  </dsp:oparam>
  <dsp:oparam name="ja_JP">
    <dsp:droplet name="/atg/dynamo/droplet/Redirect">
      <dsp:param name="url" value="../ja/index.jsp"/>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE"> 

<dsp:include page="common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Anmeldung - DEUTSCH"/></dsp:include>
<br><br>

<div align=center>
<table border="0" cellpadding="0" cellspacing="0" bgcolor="#F7D774">
  <tr>
    <td><dsp:img src="images/splash-motorprise-top.gif"/></td>
  </tr>

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
            <dsp:valueof bean="Profile.firstName"/>
            <dsp:valueof bean="Profile.lastName"/></span>&nbsp; &nbsp;
            </td>
          </tr>
          <tr>
            <td bgcolor="#F7D774" height="1" colspan="3"><dsp:img src="images/d.gif"/></td>
          </tr>
        </table>
      </dsp:oparam>
  
      <!-- for standard login-->  
      <dsp:oparam name="true"> 
        <dsp:droplet name="Switch">
          <dsp:param bean="ProfileFormHandler.formError" name="value"/>
          <dsp:oparam name="true">
            <table border="0" cellpadding="0" cellspacing="0" width="100%">
              <tr>
                <td bgcolor="#F7D774" width="40%" align="left"><dsp:img src="images/d.gif"/></td>
                <td bgcolor="#F7D774" align="right"><dsp:img src="images/splash-motorprise-error-edge.gif"/></td>
                <td bgcolor="#00B2EB" align="right">
                  <span class=bw><font color="#FFFF00">Anmeldung fehlgeschlagen: </font></span>
                  <dsp:droplet name="ProfileErrorMessageForEach">
                    <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
                    <dsp:oparam name="output">
                      <font color="#FFFFFF"><dsp:valueof param="message"/></font>
                    </dsp:oparam>
                  </dsp:droplet>&nbsp; &nbsp;</td>
              </tr>
              <tr><td bgcolor="#F7D774" height=1 colspan=3><dsp:img src="images/d.gif"/></td></tr>  
            </table>  
          </dsp:oparam>

          <dsp:oparam name="false">
            <table border="0" cellpadding="0" cellspacing="0" width=100%>
              <tr>
                <td bgcolor="#F7D774" width="100%" align="left"><dsp:img src="images/splash-orange-square.gif"/></td>
              <tr>
              <tr><td bgcolor="#F7D774" height=1><dsp:img src="images/d.gif"/></td></tr>
            </table>
          </dsp:oparam>
               
        </dsp:droplet>
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
                <td width="70"><dsp:a href="home.jsp"><dsp:img border="0" alt="Weiter" src="images/splash-motorprise-continue.gif"/></dsp:a></td>
                <td width="10"><dsp:img src="images/d.gif"/></td>
               <td><dsp:a href="index.jsp" bean="ProfileFormHandler.logout" value=""><img border="0" alt="Abmelden" src="images/splash-motorprise-logout.gif"></dsp:a></td>
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
                </table>
                </td>
                <td align="center" width="75"><dsp:input bean="ProfileFormHandler.login" name="Log in" border="0" type="image" src="images/splash-motorprise-login.gif"/></td>
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
        <dsp:droplet name="Switch">
          <dsp:param bean="Profile.transient" name="value"/>
          <dsp:oparam name="false">
            <span class=splash><font color="#000000">&nbsp;</font>
          </dsp:oparam>
      
          <dsp:oparam name="true">
            <span class=smallb>Oder melden Sie sich als Gast an und stöbern Sie in unserem Katalog
            auf <dsp:a bean="Profile.locale" href="../en/home.jsp" value="en_US"><font color="#FF3300">Englisch</font></dsp:a>, 
            oder auf <dsp:a bean="Profile.locale" href="../ja/home.jsp" value="ja_JP"><font color="#FF3300">Japanische</font></dsp:a> 
            oder auf <dsp:a bean="Profile.locale" href="home.jsp" value="de_DE"><font color="#FF3300">Deutsch</font></dsp:a>.
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
      <dsp:include page="common/Copyright.jsp"></dsp:include></td>
  </tr>
</table>

</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/index.jsp#2 $$Change: 651448 $--%>
