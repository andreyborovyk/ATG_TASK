<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<DECLAREPARAM NAME="ElementId" CLASS="java.lang.String" DESCRIPTION="Id of the repository element to retrieve">
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<HTML> <HEAD>
<TITLE>目論見書の送付</TITLE>
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
          次の申込書および目論見書の送信依頼を受け取りました：
          <dsp:droplet name="/atg/targeting/RepositoryLookup">
            <dsp:param bean="/atg/demo/QuincyFunds/repositories/Funds/Funds" name="repository"/>
            <dsp:param name="id" param="ElementId"/>
            <dsp:oparam name="output">
              <b><nobr><dsp:valueof param="element.fundName"/></b>.</nobr>
            </dsp:oparam>
            
          </dsp:droplet>
          弊社で記録されているお客様のご住所は次のとおりです。
        </td>
      </tr>

      <tr>
        <td>
          <table border=0 cellpadding=4>
            <tr>
              <td width=40></td>
              <td>
                <dsp:valueof bean="Profile.lastname"/> 
                <dsp:valueof bean="Profile.firstname"/><br>
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
                <%-- <br><dsp:valueof bean="Profile.homeAddress.country"> --%>
              </td>
            </tr>
            <tr>
              <td></td>
              <td><dsp:droplet name="/atg/dynamo/droplet/Switch">
                  <dsp:param bean="Profile.receiveEmail" name="value"/>
                  <dsp:oparam name="no"><input type="checkbox"></dsp:oparam>
                  <dsp:oparam name="default"><input type="checkbox" checked></dsp:oparam>
                </dsp:droplet>書類の送付後、その旨電子メールメッセージで知らせてください。</td>
            </tr>
            <tr>
              <td colspan=2>住所が正しければ [OK] をクリックしてください。書類を直ちに送付いたします。
                            誤りがある場合は、<dsp:a href="myprofile.jsp">正しいご住所を入力してください。</dsp:a></td>
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
	<br><b>目論見書をお受け取りになるには、メンバである必要があります。</b><br><br>
    <dsp:include page="login.jsp" ></dsp:include>
  </dsp:oparam>
</dsp:droplet>
</div>
</BODY> </HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/sendprospectus.jsp#2 $$Change: 651448 $--%>
