<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/demo/QuincyFunds/FormHandlers/EmailRepositoryFormHandler"/>
<dsp:importbean bean="/atg/demo/QuincyFunds/email/MailingApplication"/>
<dsp:importbean bean="/atg/demo/QuincyFunds/email/MailingApplicationForm"/>
<HTML> <HEAD><TITLE>対象指定電子メールのサマリ</TITLE></HEAD>
<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

<table border=0 cellpadding=4 cellspacing=0>
  <tr valign=top>
    <td width=120 bgcolor=#003366 rowspan=2>
    <!-- left bar navigation -->
    <dsp:include page="/ja/nav.jsp" />
    </td>

    <td>
    <table border=0>
      <tr>
        <td colspan=2><img src="../images/banner-quincy-small.gif" hspace=20 vspace=4><br><img src="../images/brokerconnection.gif"></td>
      </tr>
      <tr valign=top>
        <td>
        <table border=0 width=500 cellpadding=4 cellspacing=0>
          <tr>
            <td><img src="../images/d.gif" width=10></td>
            <td>
            <h2>対象指定電子メールのサマリ</h2>
            
            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
              <dsp:param bean="MailingApplication.completedMailings" name="array"/>
              <dsp:oparam name="outputStart">
                完了したメール
                <table border=0>
                  <tr>  
                    <td bgcolor=cccccc><font size=2><b>メール名</b></font></td>
                    <td bgcolor=cccccc><font size=2><b>完了時刻</b></font></td>
                    <td bgcolor=cccccc><font size=2><b>送信済みの電子メール件数</b></font></td>
                    <td bgcolor=cccccc><font size=2><b>失敗した電子メール件数</b></font></td>
                  </tr>
                </dsp:oparam>
                <dsp:oparam name="outputEnd">
                  </table>
                  <dsp:form action="./index.jsp" method="get">
                  <dsp:input bean="MailingApplicationForm.clearCompleted" type="submit" value="失敗したメールのクリア"/>  
                 </dsp:form>  
               </dsp:oparam>
               <dsp:oparam name="output">
                 <tr>  
                   <td><font size=2>
                   <dsp:valueof param="element.request.mailingName">名前なし</dsp:valueof></font></td>
                   <td><font size=2><dsp:valueof param="element.whenCompleted"/></font></td>
                   <td align=center><font size=2>
                   <dsp:valueof param="element.numberSent"/></font></td>
                   <td align=center><font size=2>
                   <dsp:valueof param="element.numberUnsent"/></font></td>
                 </tr>  
               </dsp:oparam>
              <dsp:oparam name="empty">
                <p>完了したメールはありません。
              </dsp:oparam>
            </dsp:droplet>
            <p>
            
            <!-- failed mailings -->
            <dsp:droplet name="/atg/dynamo/droplet/ForEach">
              <dsp:param bean="MailingApplication.failedMailings" name="array"/>
              <dsp:oparam name="outputStart">
                失敗したメール
                <table border=0 cellpadding=1>
                  <tr> 
                    <td bgcolor=cccccc><font size=2><b>結果</b></td>    
                    <td bgcolor=cccccc><font size=2><b>失敗時刻</b></td>
                    <td bgcolor=cccccc><font size=2><b>例外</b></td>  
                  </tr>
              </dsp:oparam>
             <dsp:oparam name="outputEnd">
               </table>
               <dsp:form action="./index.jsp" method="get">
               <dsp:input bean="MailingApplicationForm.clearFailed" type="submit" value="失敗したメールのクリア"/>  
               </dsp:form>
             </dsp:oparam>
             <dsp:oparam name="output">
               <tr>
                 <td>  
                 <font size=2>  
                 <dsp:valueof param="element.request.mailingName"/></td>
                 <td><font size=2>
                 <dsp:valueof param="element.whenFailed"/></font></td>
                 <td>
                 <font size=2><dsp:valueof param="element.exception"/>
                 </font></td>
               </tr>  

             </dsp:oparam>
             <dsp:oparam name="empty">
               
             </dsp:oparam>
           </dsp:droplet>
            <p><br>
            <ul>
              <li><dsp:a href="newmailing.jsp">メールの新規作成</dsp:a>
              <li>コンテンツの編集
              
            <dsp:droplet name="/atg/targeting/TargetingForEach">
              <dsp:param bean="/atg/registry/RepositoryTargeters/Email/email" name="targeter"/>
              <dsp:param name="fireContentEvent" value="false"/>
              <dsp:param name="fireContentTypeEvent" value="false"/>
              <dsp:oparam name="outputStart">
                <ul>
              </dsp:oparam>
              <dsp:oparam name="output">
                <li><dsp:a bean="/atg/demo/QuincyFunds/FormHandlers/EmailRepositoryFormHandler.repositoryId" href="editmail.jsp" paramvalue="element.relativePath">
                <dsp:valueof param="element.title"/></dsp:a>
              </dsp:oparam>
              <dsp:oparam name="empty">
                <li>電子メールリポジトリにコンテンツがありません。
              </dsp:oparam>
              <dsp:oparam name="outputEnd">
                </ul>
              </dsp:oparam>
                   
            </dsp:droplet>
            </ul>
  
            <!-- if the email handler is not configured -->
            <dsp:droplet name="/atg/dynamo/droplet/Switch">
              <dsp:param bean="/atg/dynamo/service/SMTPEmail.emailHandlerHostName" name="value"/>

              <dsp:oparam name="localhost"><hr>
               設定：電子メールを送信するには、<b>SMTPEmail</b>
                コンポーネントを設定する必要があります。このコンポーネントを設定するには、Dynamo ACC
                を使用して次のプロパティを変更します。
                <ul>
                  <li>emailHandlerHostName
                  <li>emailHandlerPort
                </ul>
                このコンポーネントのフルパスは /atg/dynamo/service/SMTPEmail です。
              </dsp:oparam>
            </dsp:droplet> 
<%--
  Commented out for now: not yet implemented.
            <hr>
            See the <dsp:a href="../../EmailDemo/index.jsp">Targeted Email Demo</dsp:a> for a
               complete demonstration of Dynamo's targeted email functionality.
            
--%>
        </table>
        </td>
      </tr>
    </table>
    </td>
  </tr>
</table>

</BODY> </HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/email/summary.jsp#2 $$Change: 651448 $--%>
