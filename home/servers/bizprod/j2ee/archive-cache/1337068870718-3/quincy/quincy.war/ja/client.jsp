<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<DECLAREPARAM NAME="ElementId" CLASS="java.lang.String" DESCRIPTION="Id of the repository element to retrieve">
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<HTML> <HEAD>

<dsp:droplet name="/atg/targeting/RepositoryLookup">
  <dsp:param bean="/atg/userprofiling/ProfileAdapterRepository" name="repository"/>
  <dsp:param name="id" param="ElementId"/>
  <dsp:oparam name="output">
    <TITLE>クライアント</TITLE>
    </HEAD>
    <body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

<table border=0 cellpadding=0 cellspacing=0>
  <tr valign=top>
    <td width=120 bgcolor=#003366 rowspan=2>
    <!-- left bar navigation -->
    <dsp:include page="nav.jsp" ></dsp:include></td>
    <td>
    
    <table border=0 width=500 cellpadding=2 cellspacing=0>
      <tr>
        <td colspan=3><img src="images/banner-quincy-small.gif" hspace=20 vspace=4><br>
        <img src="images/brokerconnection.gif" hspace=3></td>
      </tr>
      
      <tr valign=top>
        <td>
        <!-- left column -->
        <!-- client profile info -->
        <img src="images/banner-clientinfo.gif"><br>
        <img src="images/d.gif" hspace=2 align=left>
        <table cellpadding=2 cellspacing=0 border=0 width=250>
          <tr valign=top>
            <td>名前<br>住所</td>
            <td></td>
            <td><dsp:valueof param="element.lastname"/> 
                <dsp:valueof param="element.firstname"/><br>
                <dsp:valueof param="element.homeAddress.address1"/><br>
                <dsp:droplet name="/atg/dynamo/droplet/Switch">
                  <dsp:param name="value" param="element.homeAddress.address2"/>
                  <dsp:oparam name="unset">
                  </dsp:oparam>
                  <dsp:oparam name="default">
                    <dsp:valueof param="element.homeAddress.address2"/><br>
                  </dsp:oparam>
                </dsp:droplet>
                <dsp:valueof param="element.homeAddress.city"/>,
                <dsp:valueof param="element.homeAddress.state"/><br>
                <dsp:valueof param="element.homeAddress.postalCode"/><br>
                <dsp:droplet name="/atg/dynamo/droplet/Switch">
                  <dsp:param name="value" param="element.homeAddress.phonenumber"/>
                  <dsp:oparam name="default">
                    <dsp:valueof param="element.homeAddress.phonenumber"/><br>
                  </dsp:oparam>
                  <dsp:oparam name="unset">
                  </dsp:oparam>
                </dsp:droplet>
                <dsp:valueof param="element.email"/></td>
          </tr>
          <tr>
            <td></td>
          </tr>
          <tr valign=top>
            <td>実際のポートフォリオ</td>
            <td></td>
            <td><dsp:valueof param="element.actualstrategy"/><br>
                <dsp:valueof param="element.actualgoals"/></td>
          </tr>
          <tr valign=top>
            <td>目標ポートフォリオ</td>
            <td></td>
            <td><dsp:valueof param="element.strategy"/><br>
                <dsp:valueof param="element.goals"/></td>
          </tr>

          <tr valign=top>
            <td>積極型<br>インデックス</td>
            <td></td>
            <td><dsp:valueof param="element.aggressiveindex"/></td>

          </tr>
          <tr valign=top>
            <td>関心のあるニュース</td>
            <td></td>
            <td>
            <dsp:setvalue param="interestList" paramvalue="element.interests"/>
            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
              <dsp:param name="array" param="interestList"/>
              <dsp:oparam name="output">
                <dsp:valueof param="element"/><br>
              </dsp:oparam>
              <dsp:oparam name="empty">
                選択なし
              </dsp:oparam>
            </dsp:droplet></td>
          </tr>
          <tr valign=top>
            <td>最後のアクティビティ</td>
            <td></td>
            <td><dsp:valueof date="yyyy/MM/dd" param="element.lastActivity"/></td>
          </tr>
          <tr valign=top>
            <td>登録日</td>
            <td></td>
            <td><dsp:valueof date="yyyy/MM/dd" param="element.registrationDate"/></td>
          </tr>
          <tr valign=top>
            <td>生年月日</td>
            <td></td>
            <td><dsp:valueof date="yyyy/MM/dd" param="element.dateOfBirth"/></td>
          </tr>
          <tr valign=top>
            <td>電子メールの受信</td>
            <td></td>
            <td>
            <dsp:droplet name="/atg/dynamo/droplet/Switch">
              <dsp:param name="value" param="element.receiveEmail"/>
              <dsp:oparam name="unset">設定なし</dsp:oparam>
              <dsp:oparam name="no">受信しない</dsp:oparam>
              <dsp:oparam name="yes">受信する</dsp:oparam>
            </dsp:droplet>
            </td>
          </tr>
          <tr>
            <td>言語</td>
            <td></td>
            <td>
            <dsp:droplet name="/atg/dynamo/droplet/Switch">
              <dsp:param name="value" param="element.locale"/>
              <dsp:oparam name="unset">
                選択なし
              </dsp:oparam>
              <dsp:oparam name="default">
                <dsp:valueof param="element.locale"/>
              </dsp:oparam>
              <dsp:oparam name="en_US">
                英語
              </dsp:oparam>
              <dsp:oparam name="fr_FR">
                フランス語
              </dsp:oparam>
              <dsp:oparam name="ja_JP">
                日本語
              </dsp:oparam>
              <dsp:oparam name="de_DE">
                ドイツ語
              </dsp:oparam>
            </dsp:droplet>
            </td>
          </tr>
          <tr>
            <td>ブローカー</td>
            <td></td>
            <td>
            <dsp:droplet name="/atg/targeting/RepositoryLookup">
              <dsp:param bean="/atg/userprofiling/ProfileAdapterRepository" name="repository"/>
              <dsp:param name="id" param="element.brokerId"/>
              <dsp:oparam name="output">
                <dsp:valueof param="element.lastname"/>
                <dsp:valueof param="element.firstname"/>
              </dsp:oparam>
            </dsp:droplet></td>
          </tr>
        </table>
        <img src="images/d.gif" vspace=4><br>
        <img src="images/d.gif" hspace=2 align=left>
        <table cellpadding=0 cellspacing=0 border=0>
          <tr>
            <td colspan=2><b>最近閲覧したファンド</b></td>
          </tr>
          <tr valign=top>
            <td width=15><img src="images/clear.gif"></td>
            <td width=235><dsp:droplet name="/atg/dynamo/droplet/Range">
                  <dsp:param name="array" param="element.fundsviewed"/>
                  <dsp:param name="howMany" value="4"/>
                  <dsp:param name="reverseOrder" value="true"/>
                  <dsp:oparam name="output">
                  <dsp:valueof param="element"/><br>
                 
                  </dsp:oparam>
                  <dsp:oparam name="empty">
                    クライアントはどのファンドも閲覧していません。
                 </dsp:oparam>
                </dsp:droplet>
              </td>
           </tr>
           <tr>
            <td><img src="images/clear.gif" vspace=4></td>
          </tr>
        </table>    
        </td>
        <td></td>
        <!-- right column -->
        <!-- list of all clients -->
        <td><dsp:include page="clientlist.jsp" ></dsp:include>
        <img src="images/clear.gif" vspace=4><br>

        <table width=250 cellspacing=0 cellpadding=0 border=0>
	  <tr> 
	    <td colspan=8><img src="images/banner-clientholdings.gif"></td> 
	  </tr>
          <tr bgcolor=#ffff99>
            <td colspan=8><img src="images/clear.gif" vspace=1></td>
          </tr>
          <!-- client's portfolio -->
          <dsp:getvalueof id="pval0" param="ElementId"><dsp:include page="portfoliodynamic.jsp" ><dsp:param name="ElementId" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
            
            
        </td>
      </tr>
    </table>

    </td>
  </tr>
 </table>
</table>

</dsp:oparam>

<!-- display if no client id was passed to this page -->
<dsp:oparam name="empty">
  
<TITLE>クライアント</TITLE>
</HEAD>
<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

<table border=0 cellpadding=4 cellspacing=0>
  <tr valign=top>
    <td width=120 bgcolor=#003366 rowspan=2>
    <!-- left bar navigation -->
    <dsp:include page="nav.jsp" ></dsp:include></td>
    <td>
    <table border=0>
      <tr>
        <td colspan=3><img src="images/banner-broker-red.gif" hspace=20
          vspace=4><br><img src="images/brokerconnection.gif"></td>
      </tr>
      <tr valign=top>
        <td><img src="images/banner-clientinfo.gif">
             このページにクライアント ID が渡されていません。</td>
      </tr>
    </table>
    </td>
  </tr>
</table>
</dsp:oparam>
</dsp:droplet>


</BODY> </HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/client.jsp#2 $$Change: 651448 $--%>
