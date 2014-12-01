<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ page import="atg.portal.servlet.*,atg.portal.framework.*" %>

<dsp:importbean bean="/atg/portal/gear/soapclient/SOAPConfigFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:page>
<i18n:bundle baseName="atg.gears.soapclient.soapclient" localeAttribute="userLocale" changeResponseLocale="false" />
<paf:InitializeGearEnvironment id="gearEnv">
<%
GearServletResponse gearServletResponse = 
     (GearServletResponse)request.getAttribute(Attribute.GEARSERVLETRESPONSE);
GearServletRequest gearServletRequest = 
     (GearServletRequest)request.getAttribute(Attribute.GEARSERVLETREQUEST);
String portalURI = gearServletRequest.getPortalRequestURI();
GearContextImpl gearContext  = null;

String successUrl = request.getParameter("paf_success_url");
if((gearServletResponse != null) &&
   (gearServletRequest != null)) {
  gearContext = new GearContextImpl(gearServletRequest);

  if(successUrl == null) {
   gearContext.setGearMode(GearMode.USERCONFIG);
   successUrl = gearServletResponse.encodeGearURL(gearServletRequest.getPortalRequestURI(),gearContext);
 }
}
  
%>

<dsp:form method="post" action="<%= gearServletRequest.getPortalRequestURI() %>">
  
  <dsp:input type="hidden" bean="SOAPConfigFormHandler.setServiceParametersSuccessURL"
                         value="<%= successUrl %>" />  
  <dsp:input type="hidden" bean="SOAPConfigFormHandler.userConfiguration"
                         value="true" />  
  <dsp:input type="hidden" bean="SOAPConfigFormHandler.gearDefId"
                         value="<%= gearServletRequest.getGear().getGearDefinition().getId() %>" />
  <dsp:input type="hidden" bean="SOAPConfigFormHandler.gearId"
                         value="<%= gearServletRequest.getGear().getId() %>" />  
  <dsp:input type="hidden" bean="SOAPConfigFormHandler.userLogin" beanvalue="Profile.login" />

  

  <dsp:droplet name="/atg/portal/gear/soapclient/SOAPUserServiceParametersDroplet">

    <dsp:param name="gearEnvironment" value="<%= gearEnv %>" />
    <dsp:oparam name="output">
      <dsp:getvalueof id="userParams" idtype="java.util.HashMap" param="result">
	  
	    <table border="0" cellpadding="4" cellspacing="2">
        <core:ForEach id="userParam"
                      values="<%= userParams.keySet()%>"
                      castClass="java.lang.String"
                      elementId="currentKey">
      		<tr><td><font class="small">
         
          <%
             String beanString = "SOAPConfigFormHandler.parameterValues." + currentKey;
             String paramVal = (String) userParams.get(currentKey);
          %>

          <%= currentKey %></td><td>
		  
          <dsp:input type="text" bean="<%= beanString %>" value="<%= paramVal %>"/>
		      </td></tr>
        </core:ForEach>
        <tr><td>
        <i18n:message id="done" key="done"/>
        <dsp:input type="submit" bean="SOAPConfigFormHandler.setServiceParameters" value="<%= done %>"/>&nbsp;
        </td><td>
        <i18n:message id="cancel" key="cancel"/>
        <input type="submit" value="<%= cancel %>" />
        </td></tr>
         </table>
      </dsp:getvalueof>
    </dsp:oparam>

    <dsp:oparam name="empty">
	    <table border="0" cellpadding="4" cellspacing="2">
       <tr><td><font class="small">
       <i18n:message key="emptyMessage"/> 
      </font></td></tr></table>
    </dsp:oparam>
  </dsp:droplet>


</dsp:form>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/soapclient/soapclient.war/html/userConfig.jsp#2 $$Change: 651448 $--%>
