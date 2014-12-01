<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
<dsp:importbean bean="/atg/demo/QuincyFunds/EmailDemo/MailingApplicationForm"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

<HTML> 
 <HEAD>
  <TITLE>Mailing Details</TITLE>
 </HEAD>

 <BODY BGCOLOR="#FFFFFF" TEXT="#00214A" VLINK="#637DA6" LINK="#E87F02">
 <dsp:include page="header.jsp" />

 &nbsp;<BR><P>
 <font size=3>
  <b>Details of Mailing </b>
 </font>

 <blockquote>
 
 <table border=1 cellpadding=1>
  <tr>
   <td>
    <font size=2>
     <b>Mailing Name:
    </font>
   </td>
   <td>
    <font size=2>
     <dsp:valueof bean="MailingApplicationForm.mailingRequest.mailingName">
     </dsp:valueof>
    </font>
   </td>
  </tr>
  <tr>
   <td>
    <font size=2>
     <b>Profile Group:
    </font>
   </td>
   <td>
    <font size=2>
     <dsp:valueof 
          bean="MailingApplicationForm.mailingRequest.profileGroup.name">
     </dsp:valueof>
   </td>
  </tr>
  <tr>
   <td>
    <font size=2>
     <b>Template URL:
    </font>	
   </td>
   <td>
    <font size=2>
     <dsp:valueof 
       bean="MailingApplicationForm.mailingRequest.templateEmailInfo.templateURL">
     </dsp:valueof>
   </td>
  </tr>
  <tr>
   <td>
    <font size=2>
     <b>Message Subject:
  </td>
  <td>
   <font size=2>
    <dsp:valueof 
	bean="MailingApplicationForm.mailingRequest.templateEmailInfo.messageSubject">
    </dsp:valueof>
   </td>
  </tr>
  <tr>
   <td>
    <font size=2>
     <b>Message From:
   </td>
   <td>
    <font size=2>
     <dsp:valueof bean="MailingApplicationForm.mailingRequest.templateEmailInfo.messageFrom"></dsp:valueof>
   </td>
  </tr>
  <tr>
   <td>
    <font size=2>
     <b>Send As Text:
   </td>
   <td>
    <font size=2>
     <dsp:valueof bean="MailingApplicationForm.mailingRequest.templateEmailInfo.contentProcessor.sendAsText"></dsp:valueof>
   </td>
  </tr>
  <tr>
   <td>
    <font size=2>
     <b>Send As HTML:
   </td>
   <td>
    <font size=2>
     <dsp:valueof bean="MailingApplicationForm.mailingRequest.templateEmailInfo.contentProcessor.sendAsHtml"></dsp:valueof>
   </td>
  </tr>

  <dsp:droplet name="Switch">
   <dsp:param name="value" bean="MailingApplicationForm.runWhen" />
   <dsp:oparam name="scheduled">
    <tr>
     <td>
      <font size=2>
       <b>Schedule:
     </td>	
     <td>
      <font size=2>
       <dsp:valueof bean="MailingApplicationForm.mailingRequest.schedule">
       </dsp:valueof>
     </td>
    </tr>
   </dsp:oparam>
	
   <dsp:oparam name="eventDriven">	
    <tr>
     <td>
      <font size=2>
       <b>Event Channels:
     </td>	
     <td>
      <dsp:droplet name="ForEach">
      <dsp:param name="array" 
                 bean="MailingApplicationForm.mailingRequest.eventChannels" />
      <dsp:oparam name="output">
       <font size=2>
	<dsp:valueof param="element"></dsp:valueof>
      </dsp:oparam>
  		
      <dsp:oparam name="empty">
       <font size=2>none
      </dsp:oparam>
     </dsp:droplet>
    </td>
   </tr>
   <tr>
    <td>
     <font size=2><B>Event Type:
    </td>	
    <td>
     <font size=2>
      <dsp:valueof bean="MailingApplicationForm.mailingRequest.eventType">
      </dsp:valueof>
    </td>
   </tr>
   <tr>
    <td>
     <font size=2><b>Event Handler Name:
    </td>	
    <td>
     <font size=2>
      <dsp:valueof bean="MailingApplicationForm.mailingRequest.handlerName">
      </dsp:valueof>
    </td>
   </tr>
  </dsp:oparam>
 </dsp:droplet>	

</table>
</blockquote>

<font size=2>
<dsp:form action="mailing_summary.jsp" method="get">
 <dsp:input type="submit" bean="MailingApplicationForm.cancel" value="Cancel Mailing" />
</dsp:form>
</font>

<P>

<font size=2><dsp:a href="mailing_summary.jsp"> Back to Summary of Mailings</dsp:a>


<p>
<dsp:include page="../footer.jsp" />
</BODY>
</HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/EmailDemo/mailing_details.jsp#2 $$Change: 651448 $--%>
