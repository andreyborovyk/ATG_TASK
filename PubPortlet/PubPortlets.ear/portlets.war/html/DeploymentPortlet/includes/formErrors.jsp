<!-- BEGIN FILE formErrors.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>
 <dspel:importbean scope="request" var="topologyEditFormHandler" bean="/atg/epub/deployment/TopologyEditFormHandler"/>  
 <dspel:importbean scope="request" var="deploymentFormHandler" bean="/atg/epub/deployment/DeploymentFormHandler"/>  

 <c:set var="deploymentFormHandlerName" value="/atg/epub/deployment/DeploymentFormHandler" scope="request"/>
 <c:set var="topologyFormHandlerName" value="/atg/epub/deployment/TopologyEditFormHandler" scope="request"/>
                                                                                                                                                 
<dspel:droplet name="/atg/dynamo/droplet/Switch">
  <dspel:param bean="${topologyFormHandlerName}.formError" name="value"/>
    <dspel:oparam name="true">
        <dspel:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
          <dspel:param bean="${topologyFormHandlerName}.formExceptions" name="exceptions"/>
          <dspel:oparam name="output">
            <span class='error rightAlign'>
             <dspel:valueof param="message"/></span><br>
          </dspel:oparam>
        </dspel:droplet>
    </dspel:oparam>
</dspel:droplet>

<dspel:droplet name="/atg/dynamo/droplet/Switch">
  <dspel:param bean="${deploymentFormHandlerName}.formError" name="value"/>
    <dspel:oparam name="true">
        <dspel:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
          <dspel:param bean="${deploymentFormHandlerName}.formExceptions" name="exceptions"/>
          <dspel:oparam name="output">
            <span class='error rightAlign'>
             <dspel:valueof param="message"/></span><br>
          </dspel:oparam>
        </dspel:droplet>
      </p>
    </dspel:oparam>
</dspel:droplet>
                                                                                                                                                  
</dspel:page>
                                                                                                                                                   

<!-- END FILE formErrors.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/includes/formErrors.jsp#2 $$Change: 651448 $--%>
