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
<dsp:importbean bean="/atg/workflow/portal/communityproposal/WorkflowTaskQuery"/>
<dsp:importbean bean="/atg/workflow/portal/communityproposal/WorkflowTaskFormHandler"/>
<dsp:importbean bean="/atg/workflow/portal/communityproposal/WorkflowSubjectLookup"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
 
<dsp:droplet name="WorkflowTaskQuery">
  <dsp:param name="accessRight" value="execute"/>
  <dsp:param name="ownership" value="selfOrUnowned"/>
  <dsp:oparam name="empty">
     <p><center><font color="cc0000"><strong><fmt:message key="content.message-notasks"/></strong></font></center></p>
  </dsp:oparam>
  <dsp:oparam name="error">
     <p><center><font color="cc0000"><strong><dsp:valueof param="errorMessage"/></strong></font></center></p>
  </dsp:oparam>
  <dsp:oparam name="output">

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
        
    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="tasks"/>
      <dsp:param name="elementName" value="taskInfo"/>
      <dsp:oparam name="outputStart">        
          <table>
            <tr>
             <th><fmt:message key="content.subject-label"/></th>
             <th><fmt:message key="content.task-label"/></th>
             <th><fmt:message key="content.owner-label"/></th>            
             <th></th>
            </tr>
      </dsp:oparam>
      <dsp:oparam name="output">

          <dsp:form action="<%= gearServletRequest.getPortalRequestURI() %>">
            <dsp:getvalueof id="processName" param="taskInfo.taskDescriptor.workflow.processName">
            <dsp:getvalueof id="segmentName" param="taskInfo.taskDescriptor.workflow.segmentName">
            <dsp:getvalueof id="subjectId" param="taskInfo.subjectId">
            <dsp:getvalueof id="taskElementId" param="taskInfo.taskDescriptor.taskElementId">
          
            <dsp:input bean="WorkflowTaskFormHandler.processName" type="hidden" value="<%=processName%>"/>
            <dsp:input bean="WorkflowTaskFormHandler.segmentName" type="hidden" value="<%=segmentName%>"/>
            <dsp:input bean="WorkflowTaskFormHandler.subjectId" type="hidden" value="<%=subjectId%>"/>
            <dsp:input bean="WorkflowTaskFormHandler.taskElementId" type="hidden" value="<%=taskElementId%>"/>

	    <tr>
               <dsp:droplet name="WorkflowSubjectLookup">
                 <dsp:param name="id" param="taskInfo.subjectId"/>
                 <dsp:param name="elementName" value="subject"/>
                 <dsp:oparam name="output">

                   <td>
                      <dsp:a href="<%= gearServletResponse.encodeGearURL(gearServletRequest.getPortalRequestURI()) %>">
                        <dsp:param name="pwf_viewName" value="subject"/>
                        <dsp:param name="pwf_subjectId" param="taskInfo.subjectId"/>
                        <dsp:valueof param="subject.itemDisplayName"><font color="cc0000"><fmt:message key="content.unknown-label"/></font></dsp:valueof>
                      </dsp:a>
                   </td>
                 </dsp:oparam>
               </dsp:droplet>
              
              <td>
                 <dsp:a href="<%= gearServletResponse.encodeGearURL(gearServletRequest.getPortalRequestURI()) %>">
                   <dsp:param name="pwf_viewName" value="task"/>
                   <dsp:param name="pwf_subjectId" param="taskInfo.subjectId"/>
                   <dsp:param name="pwf_taskElementId" param="taskInfo.taskDescriptor.taskElementId"/>
                   <dsp:param name="pwf_segmentName" param="taskInfo.taskDescriptor.workflow.segmentName"/>
                   <dsp:param name="pwf_processName" param="taskInfo.taskDescriptor.workflow.processName"/>
                   <dsp:valueof param="taskInfo.taskDescriptor.displayName"><dsp:valueof param="taskInfo.taskDescriptor.name"><font color="cc0000"><fmt:message key="content.unknown-label"/></font></dsp:valueof></dsp:valueof>
                 </dsp:a>
             </td>              
              <td><dsp:valueof param="taskInfo.owner.name"><font color="cc0000"><fmt:message key="content.unknown-label"/></font></dsp:valueof></td>
              

              <dsp:droplet name="IsNull">
                <dsp:param name="value" param="taskInfo.ownerName"/>
                <dsp:oparam name="true">
                  <fmt:message var="label" key="content.claim-label" scope="page"/>
                  <% String claimLabel = (String) pageContext.getAttribute("label"); %>
                  <td><dsp:input type="submit" bean="WorkflowTaskFormHandler.claimTask" value="<%= claimLabel %>" /></td>
                </dsp:oparam>
                <dsp:oparam name="false">
                  <fmt:message var="label" key="content.release-label" scope="page"/>
                  <% String releaseLabel = (String)pageContext.getAttribute("label"); %>
                  <td><dsp:input type="submit" bean="WorkflowTaskFormHandler.releaseTask" value="<%= releaseLabel %>" /></td>
                </dsp:oparam>
              </dsp:droplet>
            </tr>

            </dsp:getvalueof> 
            </dsp:getvalueof> 
            </dsp:getvalueof> 
            </dsp:getvalueof> 
          </dsp:form>

      </dsp:oparam>
      <dsp:oparam name="outputEnd">
          </table>
      </dsp:oparam>      
    </dsp:droplet>

  </dsp:oparam>
</dsp:droplet>

</dsp:page>
</fmt:bundle>
<%-- @version $Id: //app/portal/version/10.0.3/ppa/proposal/content/html/default.jsp#2 $$Change: 651448 $--%>
