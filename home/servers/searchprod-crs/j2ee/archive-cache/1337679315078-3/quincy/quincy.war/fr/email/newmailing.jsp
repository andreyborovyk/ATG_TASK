<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/demo/QuincyFunds/email/MailingRequestForm_fr"/>
<HTML> <HEAD><TITLE>Créer un publipostage</TITLE></HEAD>
<body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

<table border=0 cellpadding=4 cellspacing=0>
  <tr valign=top>
    <td width=120 bgcolor=#003366 rowspan=2>
    <!-- left bar navigation -->
    <dsp:include page="/fr/nav.jsp" />
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
            <h3>Créer un publipostage</h3>
	    <dsp:form action="<%=ServletUtil.getRequestURI(request)%>" method="post">

            <dsp:input bean="MailingRequestForm_fr.submitSuccessURL" type="hidden" value="confirm.jsp"/>
            <dsp:input bean="MailingRequestForm_fr.submitFailureURL" type="hidden" value="newmailing.jsp"/>

            <!-- Display any errors that occurred in the last submit --> 
            <dsp:droplet name="/atg/dynamo/droplet/Switch">
              <dsp:param bean="MailingRequestForm_fr.formError" name="value"/>
              <dsp:oparam name="true">
                <b>Votre publipostage contient les erreurs suivantes :</b><p>
                <ul>
                <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
                  <dsp:param bean="MailingRequestForm_fr.formExceptions" name="exceptions"/>
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
                <td valign=top align=right>Envoyer à</td>
                <td>
                <dsp:select bean="MailingRequestForm_fr.profileGroupName" name="profileGroup">
                <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                  <dsp:param bean="/atg/registry/RepositoryGroups/UserProfiles.servletNames" name="array"/>
                  <dsp:oparam name="output">
                    <dsp:getvalueof id="groupName" idtype="java.lang.String" param="element">
                      <dsp:option value="<%=groupName%>"/> 
                    </dsp:getvalueof>
                    <dsp:valueof param="element"/>
                  </dsp:oparam>
                  <dsp:oparam name="empty">
                    Aucun groupe de profils n'est disponible. Créez des groupes de profils à l'aide d' ATG Control Center (ACC). 
                  </dsp:oparam>
                </dsp:droplet>
                </dsp:select></td>
              </tr>

              <tr>
                <td align=right><b>Et</b> adresses e-mail</td>
                <td><dsp:input bean="MailingRequestForm_fr.emailAddresses" beanvalue="MailingRequestForm_fr.emailAddresses" name="emailAddresses" size="25" type="text" required="<%=true%>"/></td>
              </tr>

              <tr>
                <td align=right>CC</td>
                <td><dsp:input bean="MailingRequestForm_fr.emailInfo.messageCc" beanvalue="MailingRequestForm_fr.emailInfo.messageCc" name="messageCc" size="25" type="text" required="<%=true%>"/></td>
              </tr>

              <tr>
                <td align=right>BCC</td>
                <td><dsp:input bean="MailingRequestForm_fr.emailInfo.messageBcc" beanvalue="MailingRequestForm_fr.emailInfo.messageBcc" name="messageBcc" size="25" type="text" required="<%=true%>"/></td>
              </tr>

              <tr>
                <td align=right>Objet</td>
                <td><dsp:input bean="MailingRequestForm_fr.emailInfo.messageSubject" beanvalue="MailingRequestForm_fr.emailInfo.messageSubject" name="messageSubject" size="25" type="text" required="<%=true%>"/></td>
              </tr>
              <tr valign=top>
                <td align=right>Adresse de l'expéditeur</td>
                <td><dsp:input bean="MailingRequestForm_fr.emailInfo.messageFrom" beanvalue="MailingRequestForm_fr.emailInfo.messageFrom" name="messageFrom" size="25" type="text" required="<%=true%>"/></td>
              </tr>
 
              <tr>
                <td align=right>Nom du mailing*</td>
                <td><dsp:input bean="MailingRequestForm_fr.mailingName" beanvalue="MailingRequestForm_fr.mailingName" name="mailingName" size="25" type="text" required="<%=true%>"/></td>
              </tr>

              <tr valign=top>
                <td align=right>Modèle</td>
                <td>Cliquez sur un modèle pour afficher un aperçu du mailing.<br>
                <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                  <dsp:param bean="MailingRequestForm_fr.demoTemplates" name="array"/>
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
		    %> <dsp:input bean="MailingRequestForm_fr.emailInfo.templateURL"
	                       value="<%=templateURI%>" 
	                       name="templateURL" type="radio"/> 
	            <dsp:a href="<%=fullTemplatePath%>" target="template">
                     <%=fullTemplatePath%> </dsp:a></font>
                    <br>
                  </dsp:oparam>
                  <dsp:oparam name="empty">
                    Aucun modèle de démonstration d'e-mail n'est disponible.
                  </dsp:oparam>
         
              </dsp:droplet>
 
              <dsp:input bean="MailingRequestForm_fr.sendAsHtml" name="sendAsHtml" type="hidden" value="true"/> 
              <p>
              <dsp:input bean="MailingRequestForm_fr.submit" type="submit" value="Envoyer un e-mail"/>
              <dsp:input bean="MailingRequestForm_fr.reset" type="reset" value="Redéfinir"/>
              </td>
            </tr>       

            <tr>
              <td></td>
              <td>* Le nom du mailing permet de l'identifier dans le récapitulatif.</td>
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
<%/* @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/email/newmailing.jsp#2 $$Change: 651448 $*/%> </dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/fr/email/newmailing.jsp#2 $$Change: 651448 $--%>
