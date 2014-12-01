<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/Switch" />

<HTML>
 <HEAD>
  <TITLE>Targeted Email Demo</TITLE>
 </HEAD>

 <BODY BGCOLOR="#FFFFFF" TEXT="#00214A" VLINK="#637DA6" LINK="#E87F02">
  <dsp:include page="header.jsp" />
  
  <br>
  <p>

  <h2>Overview of the Targeted Email Demo</h2>
  This demo provides a working example of how you can use the 
  Template Email API to create and send targeted email to your web 
  site visitors.  It comes with some pre-defined example mailings, 
  and has an interface to let you create your own mailings.

  <br>
  <br>

  <blockquote>
   <table border=0>
    <tr valign=top>
     <td> <a href="mailing_summary.jsp"> Summary of mailings</a> </td>
     <td> Browse existing mailings and results</td>
    </tr>
    <tr valign=top>
     <td> <a href="simple_mailing.jsp">Create a new mailing</a></td>
     <td> Define a new mailing </td>
    </tr>   
    <tr valign=top>
     <td> <a href="doc.jsp"> Documentation</a></td>
     <td> Documentation on how this demo works, how to extend it, etc.</td>
    </tr>
   </table>
  </blockquote>	

  <%--
  <dsp:droplet name="Switch">
   <dsp:param name="value" bean="/DPSLicense.templateEmailEnabled" />
   <dsp:oparam name="false">
    <br>
    <h3> License Restriction </h3>
    You do not have a license to use the targeted email functionality.
    This demo will operate in evaluation mode, allowing you to send 
    up to 5 email messages per mailing.  For more information 
    about a license for targeted email, please contact 
    <a href="mailto:sales@atg.com">sales@atg.com</a>.
   </dsp:oparam>
   <dsp:oparam name="true" />
  </dsp:droplet>
  --%>

  <dsp:droplet name="Switch">
   <dsp:param name="value" 
              bean="/atg/dynamo/service/SMTPEmail.emailHandlerHostName" />
   <dsp:oparam name="localhost">
    <font color="ff1223">
     <br> 
     <h3> Configuration Required </h3>
     The EmailDemo requires that the <b>/atg/dynamo/service/SMTPEmail</b>
     component be configured.  You can do this through the Dynamo ACC
     by changing the <b>emailHandlerHostName</b> and <b>emailHandlerPort</b>
     properties of the <b>/atg/dynamo/service/SMTPEmail</b> component.
    </font> 	
   </dsp:oparam>
  </dsp:droplet> 

  <dsp:include page="../footer.jsp" />
 </BODY>
</HTML>

</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/EmailDemo/index.jsp#2 $$Change: 651448 $--%>
