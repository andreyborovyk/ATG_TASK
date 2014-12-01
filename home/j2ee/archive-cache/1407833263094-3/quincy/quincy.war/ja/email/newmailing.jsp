<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/demo/QuincyFunds/email/MailingRequestForm_ja"/>
<HTML> <HEAD><TITLE>メールの新規作成</TITLE></HEAD>
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
        <table border=0 cellpadding=4 cellspacing=0>
          <tr>
            <td><img src="../images/d.gif" width=10></td>
            <td>
            <h3>メールの新規作成</h3>
	    <dsp:form action="<%=ServletUtil.getRequestURI(request)%>" method="post">

            <dsp:input bean="MailingRequestForm_ja.submitSuccessURL" type="hidden" value="confirm.jsp"/>
            <dsp:input bean="MailingRequestForm_ja.submitFailureURL" type="hidden" value="newmailing.jsp"/>

            <!-- Display any errors that occurred in the last submit --> 
            <dsp:droplet name="/atg/dynamo/droplet/Switch">
              <dsp:param bean="MailingRequestForm_ja.formError" name="value"/>
              <dsp:oparam name="true">
                <b>新しいメールで次のエラーが
                  発生しました。</b><p>
                <ul>
                <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
                  <dsp:param bean="MailingRequestForm_ja.formExceptions" name="exceptions"/>
                  <dsp:oparam name="output">
                    <li><font size=2>
                    <dsp:valueof param="message"/></font></li><p>
                  </dsp:oparam>
                </dsp:droplet>
                </ul>
                <P>
                <P>
              </dsp:oparam>
            </dsp:droplet>

            <!-- display section of form -->
            <table border=0 cellpadding=4>
              <tr>
                <td valign=top align=right>送信先</td>
                <td>
                <dsp:select bean="MailingRequestForm_ja.profileGroupName" name="profileGroup">
                <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                  <dsp:param bean="/atg/registry/RepositoryGroups/UserProfiles.servletNames" name="array"/>
                  <dsp:oparam name="output">
                    <dsp:getvalueof id="groupName" idtype="java.lang.String" param="element">
                      <dsp:option value="<%=groupName%>"/> 
                    </dsp:getvalueof>
                    <dsp:valueof param="element"/>
                  </dsp:oparam>
                  <dsp:oparam name="empty">
                    使用できるプロファイルグループがありません。ATG Control Center（ACC）を使用して
                    作成してください。 
                  </dsp:oparam>
                </dsp:droplet>
                </dsp:select></td>
              </tr>

              <tr>
                <td align=right><b>および</b>電子メールアドレス</td>
                <td><dsp:input bean="/atg/demo/QuincyFunds/email/MailingRequestForm_ja.emailAddresses" beanvalue="/atg/demo/QuincyFunds/email/MailingRequestForm_ja.emailAddresses" name="emailAddresses" size="25" type="text" required="<%=true%>"/></td>
              </tr>

              <tr>
                <td align=right>CC</td>
                <td><dsp:input bean="MailingRequestForm_ja.emailInfo.messageCc" beanvalue="MailingRequestForm_ja.emailInfo.messageCc" name="messageCc" size="25" type="text" required="<%=true%>"/></td>
              </tr>

              <tr>
                <td align=right>BCC</td>
                <td><dsp:input bean="MailingRequestForm_ja.emailInfo.messageBcc" beanvalue="MailingRequestForm_ja.emailInfo.messageBcc" name="messageBcc" size="25" type="text" required="<%=true%>"/></td>
              </tr>

              <tr>
                <td align=right>件名</td>
                <td><dsp:input bean="MailingRequestForm_ja.emailInfo.messageSubject" beanvalue="MailingRequestForm_ja.emailInfo.messageSubject" name="messageSubject" size="25" type="text" required="<%=true%>"/></td>
              </tr>
              <tr valign=top>
                <td align=right>送信者の電子メールアドレス</td>
                <td><dsp:input bean="MailingRequestForm_ja.emailInfo.messageFrom" beanvalue="MailingRequestForm_ja.emailInfo.messageFrom" name="messageFrom" size="25" type="text" required="<%=true%>"/></td>
              </tr>
 
              <tr>
                <td align=right>メール名 *</td>
                <td><dsp:input bean="MailingRequestForm_ja.mailingName" beanvalue="MailingRequestForm_ja.mailingName" name="mailingName" size="25" type="text" required="<%=true%>"/></td>
              </tr>

              <tr valign=top>
                <td align=right>テンプレート</td>
                <td>メールをプレビューするには、テンプレートをクリックしてください。<br>
                <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                  <dsp:param bean="MailingRequestForm_ja.demoTemplates" name="array"/>
                  <dsp:oparam name="output">
		    <%
	                // We'll concatenate the context path with the
	                // template path so that we can get the correct
                        // path regardless of what application we're in
	                String templateURI = 
	                  atg.servlet.ServletUtil.getDynamoRequest(request).getParameter
                           ("element");
 			String fullTemplatePath = request.getContextPath() + 
	                                          templateURI;
		    %>

                    <dsp:input bean="MailingRequestForm_ja.emailInfo.templateURL"
	                       value="<%=templateURI%>" 
	                       name="templateURL" type="radio"/> 
	            <dsp:a href="<%=fullTemplatePath%>" target="template">
                     <%=fullTemplatePath%>
                    </dsp:a></font>
                    <br>
                  </dsp:oparam>
                  <dsp:oparam name="empty">
                    使用できる電子メールデモテンプレートがありません。
                  </dsp:oparam>
         
              </dsp:droplet>
 
              <dsp:input bean="MailingRequestForm_ja.sendAsHtml" name="sendAsHtml" type="hidden" value="true"/> 
              <p>
              <dsp:input bean="MailingRequestForm_ja.submit" type="submit" value="メールの送信"/>
              <dsp:input bean="MailingRequestForm_ja.reset" type="reset" value="リセット"/>
              </td>
            </tr>       

            <tr>
              <td></td>
              <td>* メール名は、サマリ内でメールを識別するために
               使用されます。</td>
            </tr>
          </table></td>
        </tr>
      </table>
      </td>
    </tr>
  </table>
  </td>
</tr>
</table>

</dsp:form>  

</BODY> </HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/email/newmailing.jsp#2 $$Change: 651448 $--%>
