<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: NewUserRegisteredEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/Format"/>
<dsp:importbean bean="/atg/dynamo/Configuration"/>
<dsp:importbean bean="/atg/projects/b2bstore/profile/PersonLookup"/>


<dsp:setvalue value="Willkommen bei Motorprise" param="messageSubject"/>
<%-- 
<dsp:setvalue value="admin@example.com" param="messageFrom"/>
<dsp:setvalue value="admin@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="NewUserRegistered" param="mailingName"/>


<dsp:droplet name="PersonLookup">
  <dsp:param name="id" param="message.profileId"/>
  <dsp:param name="elementName" value="Person"/>
  <dsp:oparam name="output">

    <dsp:setvalue paramvalue="Person.email" param="messageTo"/>

    <p><dsp:valueof param="Person.firstName">Sehr geehrter Kunde</dsp:valueof>
    <dsp:valueof param="Person.lastName"/>,

    <p> Willkommen bei Motorprise. Als Mitarbeiter von <dsp:valueof param="Person.parentOrganization.name"/> wurden Sie auf der Motorprise-Website mit folgenden Benutzerinformationen registriert: 

    <p> Anmeldung: <dsp:valueof param="Person.login"/><br> 

    Wenden Sie sich an Ihren Verwalter, um Ihr Kennwort in Erfahrung zu bringen.
    
    <%-- 
    Construct an absolute path to the Motorprise home page by looking up the
    site's HTTP server name and port number and the context path at which
    the application is deployed and formatting an appropriate URL.  This lets
    us avoid embedding assumptions about how and where the application is 
    deployed.
    --%>
    <dsp:droplet name="Format">
      <dsp:param name="format" value="http://{host}:{port,number,#}{path}/index.jsp"/>
      <dsp:param name="host" bean="Configuration.siteHttpServerName"/>
      <dsp:param name="port" bean="Configuration.siteHttpServerPort"/>
      <dsp:param name="path" value="<%=request.getContextPath()%>"/>
      <dsp:oparam name="output">
        <p><a href="<dsp:valueof param='message'/>">Besuchen Sie Motorprise hier</a>.
      </dsp:oparam>
    </dsp:droplet>

    <p>Mit freundlichen Grüßen,<br> 
    Motorprise, Inc.
  </dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/email/NewUserRegistered.jsp#2 $$Change: 651448 $--%>
