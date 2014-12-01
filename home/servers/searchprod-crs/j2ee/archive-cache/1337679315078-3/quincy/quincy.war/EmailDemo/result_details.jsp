<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/demo/QuincyFunds/EmailDemo/MailingApplicationForm"/>

<HTML>
 <HEAD>
  <TITLE>Result Details</TITLE>
 </HEAD>

 <BODY BGCOLOR="#FFFFFF" TEXT="#00214A" VLINK="#637DA6" LINK="#E87F02">
  <dsp:include page="header.jsp" />

  &nbsp;<BR><P>
  <font size=3>
   <b>Results of a Mailing</b>
  </font>

  <blockquote>
  <table border=1 cellpadding=1>
   <tr>
    <td>
     <font size=2><b>Mailing Definition:</font>
    </td>
    <td>
     <font size=2>
      <dsp:a href="mailing_details.jsp" 
             bean="MailingApplicationForm.mailingID" 
	     beanvalue="MailingApplicationForm.mailingResult.request.mailingID">
      <dsp:valueof bean="MailingApplicationForm.mailingResult.request.mailingName"></dsp:valueof>
      </dsp:a>
     </font>
    </td>
   </tr>
   
   <dsp:droplet name="Switch">
    <dsp:param name="value" 
	       bean="MailingApplicationForm.mailingResult.hasFailed" />
    <dsp:oparam name="true">
     <tr>
      <td>
	<font size=2><b>When Failed:</font>
      </td>
      <td>
       <font size=2>
	<dsp:valueof bean="MailingApplicationForm.mailingResult.whenFailed">
	</dsp:valueof>
       </font>
      </td>
     </tr>
     <tr>
      <td>
       <font size=2><b>Exception</font>
      </td>
      <td>
       <font size=2>
	<dsp:valueof bean="MailingApplicationForm.mailingResult.exception">
	</dsp:valueof>
       </font>
      </td>
     </tr>
     <tr>
      <td>
       <font size=2><b>Root Cause</font>
      </td>
      <td>
       <font size=2>
	<dsp:valueof bean="MailingApplicationForm.mailingResult.exception.rootCause"></dsp:valueof>
       </font>
      </td>
     </tr>
    </dsp:oparam>

    <dsp:oparam name="false">
     <tr>
      <td>
	<font size=2><b>When Completed:</font>
      </td>
      <td>
       <font size=2>
	<dsp:valueof bean="MailingApplicationForm.mailingResult.whenCompleted">
        </dsp:valueof>
       </font>
      </td>
     </tr>
     <tr>
      <td>
       <font size=2><b>Emails Sent:</font>
      </td>
      <td>
       <font size=2>
	<dsp:valueof bean="MailingApplicationForm.mailingResult.numberSent">
	</dsp:valueof>
       </font>
      </td>
     </tr>
     <tr>
      <td>
       <font size=2><b>Emails Failed:</font>
      </td>
      <td>
       <font size=2>
	<dsp:valueof bean="MailingApplicationForm.mailingResult.numberUnSent">
	</dsp:valueof>
       </font>
      </td>
     </tr>
    </dsp:oparam>
   </dsp:droplet>
  </tr>
  <tr>
   <td>
    <font size=2><b>Error Messages:</font>
   </td>
   <td>
    <dsp:droplet name="ForEach">
     <dsp:param name="array" 
	        bean="MailingApplicationForm.mailingResult.errorMessages" />
     <dsp:oparam name="output">
      <font size=2>
       <dsp:valueof valueishtml="<%=true%>" param="element"></dsp:valueof>
	<br>
      </font>
     </dsp:oparam>

     <dsp:oparam name="empty">
      <font size=2>none</font>
     </dsp:oparam>
    </dsp:droplet>
   </td>
  </tr>
 </table>
</blockquote>

<P>
<font size=2>
 <a href="mailing_summary.jsp">Back to Summary of Mailings</a>
</font>

<p>
<dsp:include page="../footer.jsp" />
</BODY>
</HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/EmailDemo/result_details.jsp#2 $$Change: 651448 $--%>
