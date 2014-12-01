 <%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>

<DECLAREPARAM NAME="ElementId" CLASS="java.lang.String" DESCRIPTION="Id of the repository element to retrieve">
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<HTML> <HEAD>
<TITLE>Send Prospectus</TITLE>
</HEAD>

<BODY BGCOLOR="#FFFFFF">
<div align=center>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param bean="Profile.member" name="value"/>
  <dsp:oparam name="true">
    <dsp:form action="index.jsp" method="POST">
    <table border=0 cellpadding=4 width=500>
      <tr>
        <td><img src="images/banner-quincy-small.gif"><br><br></td>
      </tr>
      <tr>
        <td colspan=2>
          You have requested the application and prospectus for the
          <dsp:droplet name="/atg/targeting/RepositoryLookup">
            <dsp:param bean="/atg/demo/QuincyFunds/repositories/Funds/Funds" name="repository"/>
            <dsp:param name="itemDescriptor" value="fund"/>
            <dsp:param name="id" param="ElementId"/>
            <dsp:oparam name="output">
              <b><nobr><dsp:valueof param="element.fundName"/></b>.</nobr>
            </dsp:oparam>
            
          </dsp:droplet>
          This is the address we have on record for you.
        </td>
      </tr>

      <tr>
        <td>
          <table border=0 cellpadding=4>
            <tr>
              <td width=40></td>
              <td>
                <dsp:valueof bean="Profile.firstname"/> 
                <dsp:valueof bean="Profile.lastname"/><br>
                <dsp:valueof bean="Profile.homeAddress.address1"/><br>
                <dsp:droplet name="/atg/dynamo/droplet/Switch">
                  <dsp:param bean="Profile.homeAddress.address2" name="value"/>
                  <dsp:oparam name="unset">
                  </dsp:oparam>
                  <dsp:oparam name="default">
                    <dsp:valueof param="element.homeAddress.address2"/><br>
                  </dsp:oparam>
                </dsp:droplet>
                <dsp:valueof bean="Profile.homeAddress.city"/>, 
                <dsp:valueof bean="Profile.homeAddress.state"/> 
                <dsp:valueof bean="Profile.homeAddress.postalCode"/>
              </td>
            </tr>
            <tr>
              <td></td>
              <td><dsp:droplet name="/atg/dynamo/droplet/Switch">
                  <dsp:param bean="Profile.receiveEmail" name="value"/>
                  <dsp:oparam name="no"><input type="checkbox"></dsp:oparam>
                  <dsp:oparam name="default"><input type="checkbox" checked></dsp:oparam>
                </dsp:droplet>Send me an email message when the info is sent.</td>
            </tr>
            <tr>
              <td colspan=2>If this is correct hit "OK" and we'll send it right out.
                            If not, you can <dsp:a href="myprofile.jsp">enter another address.</dsp:a></td>
            </tr>
            <tr>
              <td>&nbsp;</td>
            </tr>
            <tr>
              <td></td>
              <td>
                <input type="submit" value=" OK ">
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
    </dsp:form>
  </dsp:oparam>
  <dsp:oparam name="false">
	<br><b>You must be a member to receive a prospectus.</b><br><br>
    <dsp:include page="login.jsp" ></dsp:include>
  </dsp:oparam>
</dsp:droplet>
</div>
</BODY> </HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/de/sendprospectus.jsp#2 $$Change: 651448 $--%>
