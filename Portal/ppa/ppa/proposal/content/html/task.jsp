<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*" errorPage="/error.jsp" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib uri="dsp" prefix="dsp" %>

<%
 //Obtain request/response
 GearServletResponse gearServletResponse = 
     (GearServletResponse)request.getAttribute(Attribute.GEARSERVLETRESPONSE);
 GearServletRequest gearServletRequest = 
     (GearServletRequest)request.getAttribute(Attribute.GEARSERVLETREQUEST);
%>
<fmt:bundle basename="atg.portlet.workflow.Resources">
<dsp:page>

<dsp:importbean bean="/atg/workflow/portal/communityproposal/WorkflowTaskFormHandler"/>
<dsp:importbean bean="/atg/workflow/portal/communityproposal/WorkflowSubjectLookup"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>

    <table>      
     <tr align="right"><td><a href="<%= gearServletResponse.encodeGearURL(gearServletRequest.getPortalRequestURI()) %>"><fmt:message key="content.taskinfo-label"/></a></td></tr>
    </table> 
    <dsp:droplet name="Switch">
      <dsp:param name="value" bean="WorkflowTaskFormHandler.formError"/>
      <dsp:oparam name="true">
        <ul>
           <dsp:droplet name="ErrorMessageForEach">
           <dsp:param name="exceptions" bean="WorkflowTaskFormHandler.formExceptions"/>
           <dsp:oparam name="output">
              <li><font color="cc0000"><strong><dsp:valueof param="message"/></strong></font></li>
           </dsp:oparam>
           </dsp:droplet>
        </ul>
      </dsp:oparam>
    </dsp:droplet>
        

    <dsp:form action="<%= gearServletRequest.getPortalRequestURI() %>">
      <dsp:getvalueof id="processName" param="pwf_processName">
      <dsp:getvalueof id="segmentName" param="pwf_segmentName">
      <dsp:getvalueof id="subjectId" param="pwf_subjectId">
      <dsp:getvalueof id="taskElementId" param="pwf_taskElementId">
 
      <dsp:setvalue bean="WorkflowTaskFormHandler.processName" value="<%= processName %>"/>
      <dsp:setvalue bean="WorkflowTaskFormHandler.segmentName" value="<%= segmentName %>"/>
      <dsp:setvalue bean="WorkflowTaskFormHandler.subjectId" value="<%= subjectId %>"/>
      <dsp:setvalue bean="WorkflowTaskFormHandler.taskElementId" value="<%= taskElementId %>"/>

         
      <dsp:input bean="WorkflowTaskFormHandler.processName" type="hidden" value="<%=processName%>"/>
      <dsp:input bean="WorkflowTaskFormHandler.segmentName" type="hidden" value="<%=segmentName%>"/>
      <dsp:input bean="WorkflowTaskFormHandler.subjectId" type="hidden" value="<%=subjectId%>"/>
      <dsp:input bean="WorkflowTaskFormHandler.taskElementId" type="hidden" value="<%=taskElementId%>"/>

	    
               <dsp:droplet name="WorkflowSubjectLookup">
                 <dsp:param name="id" bean="WorkflowTaskFormHandler.subjectId"/>
                 <dsp:param name="elementName" value="subject"/>
                 <dsp:oparam name="output">

                      <p><center><em><fmt:message key="content.subject-label"/></em>:<font color="cc0000"><strong>
                      <dsp:a href="<%= gearServletResponse.encodeGearURL(gearServletRequest.getPortalRequestURI()) %>">
                        <dsp:param name="pwf_viewName" value="subject"/>
                        <dsp:param name="pwf_subjectId" bean="WorkflowTaskFormHandler.subjectId"/>
                        <dsp:valueof param="subject.itemDisplayName"><font color="cc0000"><fmt:message key="content.unknown-label"/></font></dsp:valueof>
                      </dsp:a>
                      </strong></font></center></p>

                 </dsp:oparam>
               </dsp:droplet>


                      <p><center><em><fmt:message key="content.task-label"/></em>:
                        <dsp:valueof bean="WorkflowTaskFormHandler.taskDescriptor.displayName"><fmt:message key="content.unknown-label"/></dsp:valueof>
                      </center></p>
                      <p><center><em><fmt:message key="content.taskdesc-label"/></em>:
                        <dsp:valueof bean="WorkflowTaskFormHandler.taskDescriptor.description"></dsp:valueof>
                      </center></p>

              <dsp:droplet name="ForEach">
              <dsp:param name="array" bean="WorkflowTaskFormHandler.outcomeDescriptors"/>
              <dsp:param name="elementName" value="outcomeDescriptor"/>
              <dsp:oparam name="outputStart">
                <p><center><table>
                  <tr>
              </dsp:oparam>
              <dsp:oparam name="output">
                  <td>
                    <dsp:getvalueof id="outcomeDescriptor" param="outcomeDescriptor" idtype="atg.workflow.OutcomeDescriptor">
                      <dsp:input type="submit" bean="WorkflowTaskFormHandler.fireOutcome" 
                                 value="<%= outcomeDescriptor.getDisplayName() %>"
                                 submitvalue="<%= outcomeDescriptor.getOutcomeElementId() %>"
                                 name="<%= outcomeDescriptor.getName() %>"/>
                    </dsp:getvalueof>
                  <td>
              </dsp:oparam>
              <dsp:oparam name="outputEnd">
                  </tr>
                </table></center></p>
              </dsp:oparam>
              </dsp:droplet>

      </dsp:getvalueof> 
      </dsp:getvalueof> 
      </dsp:getvalueof> 
      </dsp:getvalueof> 
    </dsp:form>

   
</dsp:page>
</fmt:bundle>
<%-- @version $Id: //app/portal/version/10.0.3/ppa/proposal/content/html/task.jsp#2 $$Change: 651448 $--%>
