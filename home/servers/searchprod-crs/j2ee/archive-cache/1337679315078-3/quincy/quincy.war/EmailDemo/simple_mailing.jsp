<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
<dsp:importbean bean="/atg/demo/QuincyFunds/EmailDemo/MailingRequestForm"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />

<HTML> 
 <HEAD>
  <TITLE>SimpleMailing</TITLE>
 </HEAD>

 <BODY BGCOLOR="#FFFFFF" TEXT="#00214A" VLINK="#637DA6" LINK="#E87F02">
 <dsp:include page="header.jsp" />
 <br>
 <p>

 <dsp:form action="simple_mailing.jsp" method="post">
  <dsp:input type="hidden" bean="MailingRequestForm.submitSuccessURL" 
	     value="mailing_summary.jsp" />
  <dsp:input type="hidden" bean="MailingRequestForm.submitFailureURL" 
	     value="simple_mailing.jsp" />

  <font size=5><b>Create a New Mailing</b></font><br>
  <br>
  <br>
  <!-- 
  Display any errors that occurred in the last submit
  --> 
  <dsp:droplet name="Switch">
   <dsp:param name="value" bean="MailingRequestForm.formError" />
   <dsp:oparam name="true">
    <b>
     <font size=3>
      The following Errors were found in your new mailing:
     </font>
    </b>
    <p>
    <ul>
     <dsp:droplet name="ErrorMessageForEach">
      <dsp:param name="exceptions" bean="MailingRequestForm.formExceptions" />
      <dsp:oparam name="output">
       <li>
        <font size=2>
         <dsp:valueof param="message"></dsp:valueof>
        </font>
       </li>
       <p>
      </dsp:oparam>
     </dsp:droplet>
    </ul>
    <p><p>
   </dsp:oparam>
  </dsp:droplet>

  <font size=3>
   <b>Step 1: Select a Profile Group to mail</b>
  </font>
  <br>

  <font size=2>Choose a profile group to send the mail to:</font>
  <blockquote>
   <dsp:select name="profileGroup" bean="MailingRequestForm.profileGroupName">
    <dsp:droplet name="ForEach">
     <dsp:param name="array" 
                bean="/atg/registry/RepositoryGroups/UserProfiles.servletNames"/>
     <dsp:oparam name="outputStart">
     </dsp:oparam>
  
     <dsp:oparam name="outputEnd">
     </dsp:oparam>

     <dsp:oparam name="output">
      <dsp:getvalueof id="elementItem" idtype="java.lang.String" 
		      param="element">
       <dsp:option value="<%=elementItem%>" /> 
      </dsp:getvalueof>
      <dsp:valueof param="element"></dsp:valueof>
     </dsp:oparam>

     <dsp:oparam name="empty">
      There are no profile groups available. Please use the Personalization
      Control Center (PCC) to create some. 
     </dsp:oparam>

    </dsp:droplet>
   </dsp:select>
  </blockquote>	  
  <br>

  <font size=3>
   <b>Step 2: Select a template to use.</b>
  </font>
  <br>

  <dsp:droplet name="ForEach">
  <dsp:param name="array" bean="MailingRequestForm.demoTemplates" />

  <dsp:oparam name="outputStart">
   <font size=2>
    Choose a demo template (click on the URL to preview the email message):
   </font>
   <p>
   <blockquote>
    <font size=2><b>Template URL</b></font>
    <table>
  </dsp:oparam>

  <dsp:oparam name="outputEnd">
    </table>
   </blockquote>
  </dsp:oparam>

  <dsp:oparam name="output">
   <%
     // We'll concatenate the context path with the
     // template path so that we can get the correct
     // path regardless of what application we're in
     String templateURI = atg.servlet.ServletUtil.getDynamoRequest(request).getParameter("element");
     String templatePath = request.getContextPath() +
		           templateURI;
   %>
   <tr>
    <td> 
     <dsp:input type="radio" name="templateURL" 
                bean="MailingRequestForm.emailInfo.templateURL" 
                value="<%=templateURI%>" /> 		
    </td>
    <td>
     <font size=2>
       <dsp:a href="<%=templatePath%>" target="template"> 
	<%=templatePath%>
       </dsp:a>
     </font>
    </td>	 
   </tr>	 
  </dsp:oparam>

  <dsp:oparam name="empty">
   <tr>
    <td>
     <font size=2>
      <b>There are no demo templates available</b>
     </font>
    </td>
   </tr> 
  </dsp:oparam>
 </dsp:droplet>
	
 <p>  
 <blockquote>	
  <font size=2>
   <b>OR</b> enter your own template URL:
  </font>
  <br>
  
   <dsp:input type="text" name="templateURLString" 
	      bean="MailingRequestForm.templateURLString"
	      beanvalue="MailingRequestForm.templateURLString" size="48" />
  </blockquote>
 </ul>
 <br>

 <p>
 <font size=3>
  <b>Step 3: Select a Name for this Mailing.</b>
 </font>
 <br>

 <font size=2>
  This is the name by which you can identify this mailing on the Summary page.
 </font>

 <blockquote>
   <dsp:input type="text" name="mailingName" 
              bean="MailingRequestForm.mailingName" 
              beanvalue="MailingRequestForm.mailingName" 
              required="<%=true%>" />	
 </blockquote>
 <br>

 <font size=3>
  <b>Step 4: Enter the subject of the Email.</b>
 </font>
 <br>

 <font size=2>
  This will appear in the "Subject:" line of each email.
 </font>

 <blockquote>
   <dsp:input type="text" name="messageSubject" 
              bean="MailingRequestForm.emailInfo.messageSubject" 
              beanvalue="MailingRequestForm.emailInfo.messageSubject" 
	      required="<%=true%>" />	
 </blockquote>
 <br>

 <font size=3>
  <b>Step 5: Enter email address of the sender.</b>
 </font>
 <br>

 <font size=2>
  This will appear in the "From:" line of each email.
 </font>

 <blockquote>
   <dsp:input type="text" name="messageFrom" 
	      bean="MailingRequestForm.emailInfo.messageFrom" 
              beanvalue="MailingRequestForm.emailInfo.messageFrom" 
	      required="<%=true%>" />	
 </blockquote>
 <br>

 <font size=3>
  <b>Step 6: Select the message content types to send.</b>
 </font>
 <br>

 <font size=2>
  Select "send as html", or "send as text", or both.
 </font>  

 <blockquote>
  <table>
   <tr>
    <td> 
     <dsp:input type="checkbox" name="sendAsText" 
                bean="MailingRequestForm.sendAsText" 
                value="true" />
     <font size=2>Send as text.</font>
   </td>
   <td>
    <dsp:input type="checkbox" name="sendAsHtml" 
               bean="MailingRequestForm.sendAsHtml" 
               value="true" checked="<%=true%>" /> 
    <font size=2>Send as HTML.</font> 					
   </td>
  </tr>
 </table>
 </blockquote>
 <br>

 <font size=3>
  <b>Step 7: Define when this Mailing Should Run.</b>
 </font>
 <br>

 <font size=2>
  Choose one of the following three options:
 </font>

 <blockquote>
  <table>
   <tr>
    <td valign=top> 
     <dsp:input type="radio" name="runWhen" 
                bean="MailingRequestForm.runWhen" value="runNow" />
     <font size=2><b>Now</b></font>
     <br><br>
     <font size=2>
      If you select this option the mailing will take place immediately.
     </font>					
    </td>
    <td valign=top>
     <img src="images/bluedot.gif" width=1 height=350>
    </td>
    <td valign=top> 
     <dsp:input type="radio" name="runWhen" 
                bean="MailingRequestForm.runWhen" value="scheduled" />
     <font size=2><b>Scheduled Mailing</b></font>
     <br><br>		
     <font size=2>
      If you select this option you can schedule the mailing to take
      place in the future or periodically.
     </font>
     <br><br>		
     <font size=2>Enter a schedule:<br></font>
     <dsp:input type="text" name="schedule" 
                bean="MailingRequestForm.schedule" 
                beanvalue="MailingRequestForm.schedule" />
     <br>		
     <font color=637DA6 size=2>
      e.g. every 1 hour<br>
      e.g. in 20 minutes<br>
      e.g. calendar * * * 15 15
     </font>
    </td>
    <td valign=top>
     <img src="images/bluedot.gif" width=1 height=350>
    </td>
    <td valign=top> 
     <dsp:input type="radio" name="runWhen" bean="MailingRequestForm.runWhen"
                value="eventDriven" />	
      <font size=2><b>Event-Driven Mailing</b></font>
      <br><br>				
      <font size=2>
       If you select this option you can schedule a mailing to be sent to
       a single visitor when he causes some event to happen in Dynamo.
      </font>
      <br><br>
      <font size=2>
       Enter a name for this event channel handler:
      </font>
      <br>

      <dsp:input type="text" name="eventHandlerName" 
                 bean="MailingRequestForm.eventHandlerName" 
                 beanvalue="MailingRequestForm.eventHandlerName" />
      <br>
      <font color=637DA6 size=2>
       e.g. MyPageHandler
      </font>
      <br><br>

      <font size=2>Select an event channel:</font>
      <br>
      <dsp:input type="radio" name="eventChannelRoot" 
                 bean="MailingRequestForm.eventChannelRoot" 
	         value="/session" />
      <font size=2><b>Session Events:</b>
       <dsp:select name="eventType" bean="MailingRequestForm.eventType">
	 <dsp:option value="1" /> Start a Session
	 <dsp:option value="2" /> End a Session
	 <dsp:option value="3" /> Log In
	 <dsp:option value="4" /> Log Out
       </dsp:select>	
      </font>
      <p>
      <dsp:input type="radio" name="eventChannelRoot"  
                 bean="MailingRequestForm.eventChannelRoot" 
	         value="/page" />
      <font size=2><b>Page Events</b></font>
      <br>
      <font size=2>Enter the path to the page:</font><br>
      <dsp:input type="text" name="eventChannelDetail" 
                 bean="MailingRequestForm.eventChannelDetail"
	         beanvalue="MailingRequestForm.eventChannelDetail" />
      <br>
      <font color=637DA6 size=2>
       e.g. /QuincyFunds/en/fundlist.jsp
      </font>
     </td>
    </tr>
   </table>
  </td>
 </tr>
</table>
</blockquote>

<p>

<dsp:input type="submit" bean="MailingRequestForm.submit" 
           value="CreateMailing" />
<dsp:input type="submit" bean="MailingRequestForm.reset" value="Reset" />
</dsp:form>  


<p>
<dsp:include page="../footer.jsp" />
</BODY> 
</HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/EmailDemo/simple_mailing.jsp#2 $$Change: 651448 $--%>
