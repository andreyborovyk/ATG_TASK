<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<html><head><title>私の基本設定</title></head>
<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

<dsp:droplet name="Switch">
  <dsp:param bean="ProfileFormHandler.profile.userType" name="value"/>
  <!-- not logged in -->
  <dsp:oparam name="guest">
    <table border=0 cellpadding=4 cellspacing=0>
      <tr valign=top>
        <td width=120 bgcolor=#003366 rowspan=2>
        <!-- left bar navigation -->
        <dsp:include page="nav.jsp" ></dsp:include></td>
        <td>
        <table border=0>
          <tr>
            <td colspan=2><img src="images/banner-editgoals.gif"></td>
          </tr>
          <tr valign=top>
            <td> <h2>私の基本設定</h2>
              現在ログインしていません。基本設定を
              変更するには、最初に<dsp:a href="login.jsp">ログイン</dsp:a>してください。</td>
          </tr>
        </table></td>
      </tr>
    </table>
  </dsp:oparam>

  <dsp:oparam name="default">

<table border=0 cellpadding=4 cellspacing=0>
  <tr valign=top>
    <td width=120 bgcolor=#003366 rowspan=2>
    <!-- left bar navigation -->
    <dsp:include page="nav.jsp" ></dsp:include></td>
      
    <td>
    <table border=0>
      <tr>
        <td colspan=2><img src="images/banner-editgoals.gif"></td>
     </tr>
     <tr valign=top>
       <td><h2>私の基本設定</h2>
      <dsp:droplet name="Switch">
        <dsp:param bean="ProfileFormHandler.formError" name="value"/>
        <dsp:oparam name="true">
          <font color=cc0000><STRONG><UL>
          <dsp:droplet name="ProfileErrorMessageForEach">
            <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
            <dsp:oparam name="output">
              <LI><dsp:valueof param="message"/>
            </dsp:oparam>
          </dsp:droplet>
        </UL></STRONG></font>
      </dsp:oparam>
    </dsp:droplet>
        

        <dsp:form action="<%=ServletUtil.getDynamoRequest(request).getRequestURI()%>" method="POST">
        <dsp:input bean="ProfileFormHandler.updateSuccessURL" type="HIDDEN" value="../index.jsp"/>

        <table border=0 cellpadding=4>

          <tr valign=top>
            <td align=right>ニューストピック </td>
             <td><dsp:input bean="ProfileFormHandler.value.interests" type="checkbox" value="market"/>マーケット一般ニュース<BR>
             <dsp:input bean="ProfileFormHandler.value.interests" type="checkbox" value="international"/>国際マーケット <BR>
             <dsp:input bean="ProfileFormHandler.value.interests" type="checkbox" value="growth"/>グロースマーケット<BR>
             <dsp:input bean="ProfileFormHandler.value.interests" type="checkbox" value="tax"/>税金</td>
          </tr>
          <tr valign=top>
            <td align=right>ホームページ </td>
            <td> 
             <dsp:input bean="ProfileFormHandler.value.numberNewsItems" maxlength="2" size="2" type="text"/> 新規アイテムの表示<br>
             <dsp:input bean="ProfileFormHandler.value.numberFeatureItems" maxlength="2" size="2" type="text"/> 特別提供品の表示
            </td>
          </tr>
          <tr valign=top>   
            <td align=right>ブローカー </td>
            <td>
            <dsp:droplet name="/atg/targeting/TargetingForEach">
              <dsp:param bean="/atg/registry/RepositoryTargeters/UserProfiles/BrokerList" name="targeter"/>
              <dsp:param name="fireContentEvent" value="false"/>
              <dsp:param name="fireContentTypeEvent" value="false"/>
              <dsp:oparam name="output">
                 <dsp:input bean="ProfileFormHandler.value.brokerId" paramvalue="element.repositoryId" type="radio"/>
                 <dsp:valueof param="element.lastName"/> 
                 <dsp:valueof param="element.firstName"/><br>
             </dsp:oparam>
            </dsp:droplet>
            </td>
          </tr>
          <tr>
            <td align=right>言語 </td>
            <td><dsp:select bean="ProfileFormHandler.value.locale">
                 <dsp:option value="en_US"/>英語
                 <dsp:option value="fr_FR"/>フランス語
                 <dsp:option value="de_DE"/>ドイツ語
                 <dsp:option value="ja_JP"/>日本語
               </dsp:select>
            </td>
          </tr>  
          <tr>
            <td align=right>電子メールで情報受信を希望しますか？</td>
        <td><dsp:input bean="ProfileFormHandler.value.receiveEmail" type="radio" value="yes"/>はい
            <dsp:input bean="ProfileFormHandler.value.receiveEmail" type="radio" value="no"/>いいえ
        </td>
          </tr>

          <tr>
             <td></td>
             <td><br><dsp:input bean="ProfileFormHandler.update" type="submit" value=" 保存 "/>
             <input type="reset" value=" リセット "></td>
           </tr>
         </table>
         </dsp:form>
         </td>
       </tr>
     </table>
     </td>
      </tr>
    </table>
  </dsp:oparam>
</dsp:droplet>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/mypreferences.jsp#2 $$Change: 651448 $--%>
