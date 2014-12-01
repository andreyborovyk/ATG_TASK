<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<html><head><title>登録</title></head>
<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

<div align=center>
<dsp:form action="<%=ServletUtil.getDynamoRequest(request).getRequestURI()%>" method="POST">
<dsp:input bean="ProfileFormHandler.updateSuccessURL" type="HIDDEN" value="../index.jsp"/>
<table border=0 cellpadding=4 width=550>
  <tr>
    <td><img src="images/banner-signup.gif"><br><br></td>
  </tr>
  <tr>
    <td colspan=2><font size=+2>ようこそ： <dsp:valueof bean="Profile.login"/> </font><br>
    このサイトをできる限り効果的にご利用いただき、弊社およびブローカーが投資家としてのお客様に適切な情報をお届けすることができるように、個人情報の入力をお願いいたします。</td>
  </tr>

  <dsp:droplet name="Switch">
    <dsp:param bean="ProfileFormHandler.formError" name="value"/>
    <dsp:oparam name="true">
    
    <dsp:droplet name="ProfileErrorMessageForEach">
      <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
      <dsp:oparam name="output">
        <tr><td align=center><font color=cc0000><b><dsp:valueof param="message"/></b></td></tr>
      </dsp:oparam>
    </dsp:droplet>
    
    </dsp:oparam>
  </dsp:droplet>  
    <dsp:input bean="ProfileFormHandler.value.userType" type="hidden" value="investor"/>

  <tr>
    <td>
    <table border=0 cellpadding=4>
      <tr>  
        <td colspan=2><font size=+1><b>個人情報</b></font></td>
      </tr>
      <tr>
        <td align=right>名 </td>
        <td><dsp:input bean="ProfileFormHandler.value.firstname" maxlength="30" size="25" type="text"/></td>
      </tr>
      <tr>
        <td align=right>姓 </td>
        <td><dsp:input bean="ProfileFormHandler.value.lastname" maxlength="30" size="25" type="text"/></td>
      </tr>
      <tr>
        <td align=right>住所 </td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.address1" maxlength="30" size="25" type="TEXT"/></td>
      </tr>
      <tr>
        <td align=right> </td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.address2" maxlength="30" size="25" type="TEXT"/></td>
      </tr>
      <tr>
        <td valign=middle align=right>市町村 </td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.city" maxlength="30" size="25" type="TEXT"/> </td> 
      </tr>
      <tr>
        <td valign=middle align=right>都道府県</td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.state" maxlength="25" size="25" type="TEXT"/></td>
      </tr>
      
      <tr>
        <td align=right>郵便番号 </td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.postalcode" maxlength="10" size="10" type="TEXT"/></td>
      </tr>

      <tr>
        <td align=right>国 </td>
        <td><dsp:input bean="ProfileFormHandler.value.homeAddress.country" size="25" type="TEXT"/></td>
      </tr>
      <tr>
        <td align=right>電子メール </td>
        <td><dsp:input bean="ProfileFormHandler.value.email" maxlength="30" size="25" type="text"/></td>
      </tr>
      <tr>
        <td align=right>生年月日（YYYY/MM/DD）</td>
        <td><dsp:input bean="ProfileFormHandler.value.dateOfBirth" date="yyyy/MM/dd" maxlength="10" size="10" type="text"/></td>
      </tr>
      <tr>
        <td align=right>性別 </td>
        <td><dsp:input bean="ProfileFormHandler.value.gender" type="radio" value="male"/>男性
            <dsp:input bean="ProfileFormHandler.value.gender" type="radio" value="female"/>女性
        </td>
      </tr>
      <tr>
        <td valign=top align=right>ブローカーの選択 </td>  
        <td>
        <dsp:droplet name="/atg/targeting/TargetingForEach">
          <dsp:param bean="/atg/registry/RepositoryTargeters/UserProfiles/BrokerList" name="targeter"/>
          <dsp:param name="fireContentEvent" value="false"/>
          <dsp:param name="fireContentTypeEvent" value="false"/>
          <dsp:oparam name="output">
            <dsp:input bean="ProfileFormHandler.value.brokerId" paramvalue="element.repositoryId" type="radio"/>  
            <dsp:valueof param="element.lastname"/>
            <dsp:valueof param="element.firstname"/><br>
          </dsp:oparam>
        </dsp:droplet></td>
      </tr>
      <tr>
        <td align=right>言語</td>
        <td><dsp:select bean="ProfileFormHandler.value.locale">
              <dsp:option value="en_US"/>英語
              <dsp:option value="fr_FR"/>フランス語
              <dsp:option value="de_DE"/>ドイツ語
              <dsp:option value="ja_JP"/>日本語
            </dsp:select>
        </td>
      </tr>  
      <tr>
        <td>電子メールで情報受信を希望しますか？</td>
        <td><dsp:input bean="ProfileFormHandler.value.receiveEmail" type="radio" value="yes"/>はい
            <dsp:input bean="ProfileFormHandler.value.receiveEmail" type="radio" value="no"/>いいえ
        </td>
      </tr>
      <tr>
        <td valign=middle align=right></td>
        <td><dsp:input bean="ProfileFormHandler.update" type="SUBMIT" value=" 保存 "/>
        <INPUT TYPE="RESET" VALUE=" リセット "></td>
      </tr>
    </table> 
  </td> 
  </tr>
</table>
</dsp:form>
</BODY>
</HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/signup2.jsp#2 $$Change: 651448 $--%>
