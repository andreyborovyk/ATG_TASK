<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/demo/QuincyFunds/FormHandlers/RepositoryErrorMessageForEach"/>
<dsp:importbean bean="/atg/demo/QuincyFunds/FormHandlers/EmailRepositoryFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>


<!-- show current item property values -->
<dsp:setvalue bean="EmailRepositoryFormHandler.extractDefaultValuesFromItem" value="true"/>

<HTML> <HEAD><TITLE>Edit Mailing</TITLE></HEAD>
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
        <td colspan=2><img src="../images/banner-quincy-small.gif" hspace=20 vspace=4></td>
      </tr>
      <tr valign=top>
        <td>
        <table border=0 cellpadding=4 cellspacing=0>
          <tr>
            <td><img src="../images/d.gif" width=10></td>
            <td>
            <h2>View Mailing</h2>
           
            <dsp:form action="<%=ServletUtil.getRequestURI(request)%>" method="post">
            <dsp:input bean="EmailRepositoryFormHandler.updateSuccessURL" type="hidden" value="index.jsp"/>
            <dsp:input bean="EmailRepositoryFormHandler.updateErrorURL" type="hidden" value="editmail.jsp"/> 
           <!-- setting repositoryId clears the value attributes, so we must do that first -->
           <dsp:input bean="EmailRepositoryFormHandler.repositoryId" type="hidden"/>

            Mailing Title: <dsp:valueof bean="EmailRepositoryFormHandler.value.title"/>

            <!-- display errors -->
            <dsp:droplet name="Switch">
              <dsp:param bean="EmailRepositoryFormHandler.formError" name="value"/>
              <dsp:oparam name="true">
                <font color=cc0000><strong><ul>
                <dsp:droplet name="RepositoryErrorMessageForEach">
                  <dsp:param bean="EmailRepositoryFormHandler.formExceptions" name="exceptions"/>
                  <dsp:oparam name="output">
        	    <li> <dsp:valueof param="message"/>
                  </dsp:oparam>
                </dsp:droplet>
                </ul></strong></font>
              </dsp:oparam>
            </dsp:droplet>
            <p>  
            Conservative content:<br>
            <table border=1 width="400"><tr><td><font size=2><dsp:valueof bean="EmailRepositoryFormHandler.value.ConsContent"></dsp:valueof></font></td></tr></table>
            <p>
            Aggressive content:<br>
            <table border=1 width="400"><tr><td><font size=2><dsp:valueof bean="EmailRepositoryFormHandler.value.AggContent"></dsp:valueof></font></td></tr></table>
            <p>
            Signature:<br>
            <table border=1 width="90"><tr><td><font size=2><dsp:valueof bean="EmailRepositoryFormHandler.value.BrokerSignature"></dsp:valueof></font></td></tr></table>
            </td>
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
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/de/email/editmail.jsp#2 $$Change: 651448 $--%>
