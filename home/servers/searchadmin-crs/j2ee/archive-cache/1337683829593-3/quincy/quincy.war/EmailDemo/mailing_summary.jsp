<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/demo/QuincyFunds/EmailDemo/MailingApplication"/>
<dsp:importbean bean="/atg/demo/QuincyFunds/EmailDemo/MailingApplicationForm"/>

<HTML> 
 <HEAD>
  <TITLE>Mailing Summary</TITLE>
 </HEAD>

 <BODY BGCOLOR="#FFFFFF" TEXT="#00214A" VLINK="#637DA6" LINK="#E87F02">
 <dsp:include page="header.jsp" />
 <br>
 <P>
  <font size=5>
   <b>Summary of Mailings</b>
  </font>
 <br>

 <p>
 <font size=3>
  <b>Scheduled Mailings</b>
 </font>
 
 <blockquote>
  <dsp:droplet name="ForEach">
  <dsp:param name="array" bean="MailingApplication.scheduledMailings" />
  
  <dsp:oparam name="outputStart">
   <table border=1 cellpadding=1>
    <tr>	
     <td bgcolor=cccccc>
      <font size=2><b>Name</b></font>
     </td>	     <td bgcolor=cccccc>
      <font size=2><b>Schedule</b></font>
     </td>
     <td bgcolor=cccccc>
      <font size=2><b>Group</b></font>
     </td>
     <td bgcolor=cccccc>
      <font size=2><b>Template</b></font>
     </td>
    </tr>
   </dsp:oparam>

   <dsp:oparam name="outputEnd">
    </table>
   </dsp:oparam>

   <dsp:oparam name="output">
    <tr>
     <td>
      <font size=2>
       <dsp:a href="mailing_details.jsp" 
	bean="MailingApplicationForm.mailingID"
        paramvalue="element.mailingID">
	 <dsp:valueof param="element.mailingName"></dsp:valueof>
       </dsp:a>	
      </font>
     </td>	
     <td>
      <font size=2>
       <dsp:valueof param="element.schedule"></dsp:valueof>	
      </font>
     </td>
     <td>
      <font size=2>
       <dsp:valueof param="element.profileGroup.groupName"></dsp:valueof>
      </font>
     </td>
     <td>
      <font size=2>
       <dsp:valueof param="element.templateEmailInfo.templateURL">
       </dsp:valueof>	
      </font>
     </td>
    </tr>
   </dsp:oparam>

   <dsp:oparam name="empty">	
    <font size=2>There are currently no scheduled mailings.</font>
    <p>
   </dsp:oparam> 
  </dsp:droplet>
 </blockquote>
 
 <p>
 <font size=3><b>Event-Driven Mailings</b></font>

 <blockquote>
  <dsp:droplet name="ForEach">
  <dsp:param name="array" bean="MailingApplication.eventDrivenMailings" />

  <dsp:oparam name="outputStart">
   <table border=1 cellpadding=1>
    <tr>	
     <td bgcolor=cccccc>
      <font size=2><b>Name</b>
     </td>
     <td bgcolor=cccccc>
      <font size=2><b>Event Channels</b>
     </td>
     <td bgcolor=cccccc>
      <font size=2><b>Template</b>
     </td>
    </tr>
   </dsp:oparam>

   <dsp:oparam name="outputEnd">
    </table>
   </dsp:oparam>
  
   <dsp:oparam name="output">
    <tr>	
     <td>
      <font size=2>
       <dsp:a href="mailing_details.jsp" 
              bean="MailingApplicationForm.mailingID" 
	      paramvalue="element.mailingID">
	<dsp:valueof param="element.mailingName"></dsp:valueof>
      </dsp:a>
     </td>
     <td>
      <dsp:droplet name="ForEach">
       <dsp:param name="array" param="element.eventChannels" />
 
       <dsp:oparam name="output">
	<font size=2>
	 <dsp:valueof param="element"></dsp:valueof>
	</font>
       </dsp:oparam>

       <dsp:oparam name="empty">
	<font size=2>none</font>
       </dsp:oparam>
      </dsp:droplet>
     </td>
     <td>
      <font size=2>
       <dsp:valueof param="element.templateEmailInfo.templateURL">
       </dsp:valueof>
      </font>
     </td>	
    </tr>	
   </dsp:oparam>

   <dsp:oparam name="empty">
    <font size=2>There are currently no event-driven mailings.</font>
    <p>
   </dsp:oparam>
  </dsp:droplet>
 </blockquote>

 <p>
 <font size=3><b>Completed Mailings</b></font>

 <blockquote>
  <dsp:droplet name="ForEach">
   <dsp:param name="array" bean="MailingApplication.completedMailings" />
   <dsp:oparam name="outputStart">
    <table border=1 cellpadding=1>
     <tr>	
      <td bgcolor=cccccc>
       <font size=2><b>Result</b></font>
      </td>
      <td bgcolor=cccccc>
       <font size=2><b>When Completed</b></font>
      </td>
      <td bgcolor=cccccc>
       <font size=2><b># Emails Sent</b></font>
      </td>
      <td bgcolor=cccccc>
       <font size=2><b># Emails Failed</b></font>
      </td>
     </tr>
    </dsp:oparam>
    
    <dsp:oparam name="outputEnd">
     </table>
     <font size=2>
      <dsp:form action="mailing_summary.jsp" method="post">
       <dsp:input type="submit" bean="MailingApplicationForm.clearCompleted" 
                  value="Clear Completed Mailings" />	
      </dsp:form>	
     </font>
    </dsp:oparam>

    <dsp:oparam name="output">
     <tr>	
      <td>
       <font size=2> 
        <dsp:a href="result_details.jsp"
	       bean="MailingApplicationForm.resultID"
	       paramvalue="element.resultID">	
	 <dsp:valueof param="element.request.mailingName"> 
	 </dsp:valueof>Result
        </dsp:a>
       </font>
      </td>
      <td>
       <font size=2><dsp:valueof param="element.whenCompleted"></dsp:valueof>
      </td>
      <td>
       <font size=2><dsp:valueof param="element.numberSent"></dsp:valueof>
      </td>
      <td>
       <font size=2><dsp:valueof param="element.numberUnsent"></dsp:valueof>
      </td>
     </tr>	
    </dsp:oparam>
  
    <dsp:oparam name="empty">
     <font size=2>No mailings have been completed.</font>
     <p>
    </dsp:oparam>
   </dsp:droplet>
  </blockquote>

  <font size=3><b>Failed Mailings</b></font>

  <blockquote>
  <dsp:droplet name="ForEach">
   <dsp:param name="array" bean="MailingApplication.failedMailings" />
   <dsp:oparam name="outputStart">
    <table border=1 cellpadding=1>
     <tr>
      <td bgcolor=cccccc>
       <font size=2><b>Result</b>
      </td>		
      <td bgcolor=cccccc>
       <font size=2><b>When Failed</b>
      </td>
      <td bgcolor=cccccc>
	<font size=2><b>Exception</b>
      </td>	
     </tr>
   </dsp:oparam>
 
   <dsp:oparam name="outputEnd">
    </table>
    <font size=2>
     <dsp:form action="mailing_summary.jsp" method="post">
      <dsp:input type="submit" bean="MailingApplicationForm.clearFailed" 
	         value="Clear Failed Mailings" />	
     </dsp:form>
    </font>	
   </dsp:oparam>
  
   <dsp:oparam name="output">
    <tr>
     <td>	
      <font size=2>
       <dsp:a href="result_details.jsp" 
              bean="MailingApplicationForm.resultID"
              paramvalue="element.resultID">	
	 <dsp:valueof param="element.request.mailingName"></dsp:valueof>
         Result
       </dsp:a>	
      </font>
     </td>
    <td>
     <font size=2>
      <dsp:valueof param="element.whenFailed"></dsp:valueof>
     </font>
    </td>
    <td>
     <font size=2>
      <dsp:valueof param="element.exception"></dsp:valueof>
     </font>
    </td>
   </tr>	
  </dsp:oparam>
  
  <dsp:oparam name="empty">
   <font size=2>No mailings have failed.</font>
  </dsp:oparam>
 </dsp:droplet>
 </blockquote>
 
 <p>
 <dsp:include page="../footer.jsp" />

 </BODY>
</HTML>

</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/EmailDemo/mailing_summary.jsp#2 $$Change: 651448 $--%>
