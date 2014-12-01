<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/demo/QuincyFunds/email/MailingRequestForm_de"/>
<HTML> <HEAD><TITLE>Create New Mailing</TITLE></HEAD>
<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

<table border=0 cellpadding=4 cellspacing=0>
  <tr valign=top>
    <td width=120 bgcolor=#003366 rowspan=2>
    <!-- left bar navigation -->
    <dsp:include page="/de/nav.jsp" />
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
            <h3>Create New Mailing</h3>
	    <dsp:form action="<%=ServletUtil.getRequestURI(request)%>" method="post">

            <dsp:input bean="MailingRequestForm_de.submitSuccessURL" type="hidden" value="confirm.jsp"/>
            <dsp:input bean="MailingRequestForm_de.submitFailureURL" type="hidden" value="newmailing.jsp"/>

            <!-- Display any errors that occurred in the last submit --> 
            <dsp:droplet name="/atg/dynamo/droplet/Switch">
              <dsp:param bean="MailingRequestForm_de.formError" name="value"/>
              <dsp:oparam name="true">
                <b>The following Errors were found in your 
                  new mailing:</b><p>
                <ul>
                <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
                  <dsp:param bean="MailingRequestForm_de.formExceptions" name="exceptions"/>
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
                <td valign=top align=right>Send to</td>
                <td>
                <dsp:select bean="MailingRequestForm_de.profileGroupName" name="profileGroup">
                <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                  <dsp:param bean="/atg/registry/RepositoryGroups/UserProfiles.servletNames" name="array"/>
                  <dsp:oparam name="output">
                    <dsp:getvalueof id="groupName" idtype="java.lang.String" param="element">
                      <dsp:option value="<%=groupName%>"/> 
                    </dsp:getvalueof>
                    <dsp:valueof param="element"/>
                  </dsp:oparam>
                  <dsp:oparam name="empty">
                    There are no profile groups available. Please use the
                    Personalization Control Center (PCC) to create some. 
                  </dsp:oparam>
                </dsp:droplet>
                </dsp:select></td>
              </tr>

              <tr>
                <td align=right><b>And</b> email addresses</td>
                <td><dsp:input bean="MailingRequestForm_de.emailAddresses" beanvalue="MailingRequestForm_de.emailAddresses" name="emailAddresses" size="25" type="text" required="<%=true%>"/></td>
              </tr>

              <tr>
                <td align=right>CC</td>
                <td><dsp:input bean="MailingRequestForm_de.emailInfo.messageCc" beanvalue="MailingRequestForm_de.emailInfo.messageCc" name="messageCc" size="25" type="text" required="<%=true%>"/></td>
              </tr>

              <tr>
                <td align=right>BCC</td>
                <td><dsp:input bean="MailingRequestForm_de.emailInfo.messageBcc" beanvalue="MailingRequestForm_de.emailInfo.messageBcc" name="messageBcc" size="25" type="text" required="<%=true%>"/></td>
              </tr>

              <tr>
                <td align=right>Subject</td>
                <td><dsp:input bean="MailingRequestForm_de.emailInfo.messageSubject" beanvalue="MailingRequestForm_de.emailInfo.messageSubject" name="messageSubject" size="25" type="text" required="<%=true%>"/></td>
              </tr>
              <tr valign=top>
                <td align=right>Sender's address</td>
                <td><dsp:input bean="MailingRequestForm_de.emailInfo.messageFrom" beanvalue="MailingRequestForm_de.emailInfo.messageFrom" name="messageFrom" size="25" type="text" required="<%=true%>"/></td>
              </tr>
 
              <tr>
                <td align=right>Mailing name*</td>
                <td><dsp:input bean="MailingRequestForm_de.mailingName" beanvalue="MailingRequestForm_de.mailingName" name="mailingName" size="25" type="text" required="<%=true%>"/></td>
              </tr>

              <tr valign=top>
                <td align=right>Template</td>
                <td>Click on a tempate to preview the mailing.<br>
                <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                  <dsp:param bean="MailingRequestForm_de.demoTemplates" name="array"/>
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

                    <dsp:input bean="MailingRequestForm_de.emailInfo.templateURL"
	                       value="<%=templateURI%>" 
	                       name="templateURL" type="radio"/> 
	            <dsp:a href="<%=fullTemplatePath%>" target="template">
                     <%=fullTemplatePath%>
                    </dsp:a></font>
                    <br>
                  </dsp:oparam>
                  <dsp:oparam name="empty">
                    There are no email demo templates available
                  </dsp:oparam>
         
              </dsp:droplet>
 
              <dsp:input bean="MailingRequestForm_de.sendAsHtml" name="sendAsHtml" type="hidden" value="true"/> 
              <p>
              <dsp:input bean="MailingRequestForm_de.submit" type="submit" value="Send Mail"/>
              <dsp:input bean="MailingRequestForm_de.reset" type="reset" value="Reset"/>
              </td>
            </tr>       

            <tr>
              <td></td>
              <td>* The mailing name is used to identify the mailing in the
               summary.</td>
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
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/de/email/newmailing.jsp#2 $$Change: 651448 $--%>
