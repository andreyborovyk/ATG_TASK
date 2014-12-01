<%@ page import="javax.portlet.*,java.util.*,atg.workflow.WorkflowDescriptor, atg.epub.PublishingLicense" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<!-- Begin ProjectsPortlet's createProject.jsp -->

<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>
<fmt:message var="createButtonText" key="create-project-button" bundle="${projectsBundle}"/>

<dspel:page xml="true">

<portlet:defineObjects/>
<%@ include file="projectConstants.jspf" %>

<portlet:actionURL var="createActionURL"/>

  <%-- this bean used to get process portlet ID based on workflow type --%>
  <dspel:importbean bean="/atg/bizui/portlet/PortletConfiguration" var="bizuiPortletConfig"/>
  <c:set var="campaignPortletID" value='${bizuiPortletConfig.workflowPortletMap["campaign"]}'/>

  <dspel:importbean bean="/PublishingLicense" var="pubLicense"/>

<!-- begin content -->
<div id="createScreen">
	
<div id="intro">
	
  <h2><fmt:message key="create-project-page-title" bundle="${projectsBundle}"/> <em>| <fmt:message key="step-one-msg" bundle="${projectsBundle}"/></em></h2>

  <p><fmt:message key="step-one-helper-text" bundle="${projectsBundle}"/></p>
	
</div>
	
<pws:getWorkflowDefinitions var="results" userOnly="true"/>
<c:set var="defs" value="${results.workflowDefinitions}"/>

<c:if test="${empty defs}">
<span class="error"><fmt:message key="no-create-permissions-msg" bundle="${projectsBundle}"/><br /></span>
</c:if>

<%  // The following is a workaround for not yet having actual workflow categories:
    // Select CA, Campaign, and other workflows based on path names.

    Collection workflowDefs = (Collection)pageContext.getAttribute("defs");
    Collection contentAdminDefs = new ArrayList(workflowDefs.size());
    Collection campaignDefs = new ArrayList(workflowDefs.size());
    Collection otherDefs = new ArrayList(workflowDefs.size());

    Iterator iter=workflowDefs.iterator();
    while (iter.hasNext()) {
      WorkflowDescriptor wd=(WorkflowDescriptor)iter.next();

      if (wd.getProcessName().toLowerCase().indexOf("content") >= 0 ) {
        contentAdminDefs.add(wd);
      }
      else if (wd.getProcessName().toLowerCase().indexOf("campaign") >= 0 ) {
        campaignDefs.add(wd);
      }
      else {
        otherDefs.add(wd);
      }
    }

    // Don't include any CA processes if there isn't a full license
    PublishingLicense license = (PublishingLicense)pageContext.getAttribute("pubLicense");
    boolean fullCALicense = license.isLicensed("customRepositories"); 
    if (fullCALicense) 
      pageContext.setAttribute("caDefs", contentAdminDefs);
    else
      pageContext.setAttribute("caDefs", new ArrayList());

    pageContext.setAttribute("campaignDefs", campaignDefs);
    pageContext.setAttribute("otherDefs", otherDefs);

%>
  <div id="nonTableContent" class="noborder">

  <!-- begin basic table for borderless content -->

  <table border="0" cellpadding="0" cellspacing="0" id="plainView">

     <tr>
	
	<!-- this td contains the process choices -->
		
		<td class="alignment">
		
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
			<td>
				<table border="0" cellpadding="0" cellspacing="0" class="borderTopper" class="borderTopper">
				<tr>
				 <c:if test="${!empty caDefs}">
				   <td nowrap id="processCat0" class="formPadding cat" onmousedown="showProcessOptions('processOption','0');" onmouseover="this.style.cursor = 'pointer'">
				   &rsaquo;<a href="javascript:showProcessOptions('processOption','0');"> <fmt:message key="select-ca-process-link" bundle="${projectsBundle}"/></a>
				 </c:if>
				 <c:if test="${empty caDefs}">
				  <td>&nbsp;</td>
				 </c:if>
                                </td>
				</tr>
				<tr>
				 <c:if test="${!empty campaignDefs}">
				   <td id="processCat1" class="formPadding cat" onmousedown="showProcessOptions('processOption','1');" onmouseover="this.style.cursor = 'pointer'">
				   &rsaquo;<a href="javascript:showProcessOptions('processOption','1');"> <fmt:message key="select-campaign-process-link" bundle="${projectsBundle}"/></a>
				 </c:if>
				 <c:if test="${empty campaignDefs}">
				  <td>&nbsp;</td>
				 </c:if>
				</td>
				</tr>
				<tr>
				 <c:if test="${!empty otherDefs}">
				   <td id="processCat2" class="formPadding cat" onmousedown="showProcessOptions('processOption','2');" onmouseover="this.style.cursor = 'pointer'">
				   &rsaquo; <a href="javascript:showProcessOptions('processOption','2');"><fmt:message key="select-other-process-link" bundle="${projectsBundle}"/></a>
				 </c:if>
				 <c:if test="${empty otherDefs}">
				  <td>&nbsp;</td>
				 </c:if>
				</td>
				</tr>
				</table>
			</td>
			</tr>
			</table>
		
		</td>

	<!-- this td contains the process options -->

	<%-- counter for all processes - use to correlate to descriptions --%>
	<c:set var="processCounter" value="${0}"/>
		
		<td class="alignment">
		
			<table border="0" cellpadding="0" cellspacing="0" id="processOption">
			<tr>
			<td style="width: 260px; height: 160px;">
				<table border="0" cellpadding="0" cellspacing="0" id="processOption0">
				<tr>
				<td class="formPadding options">
				<div class="processType">
				<ul>
				<c:forEach var="workflow" items="${caDefs}">
	                           <c:set var="processTypeNum" value="processType${processCounter}"/>
                                   <c:set var="workflowDefinition" value="${workflow.processName}"/>
                                   <c:set var="workflowName" value="${workflow.displayName}"/>
                                   <portlet:renderURL var="createProcessURL">
                                     <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("CREATE_PROCESS_FORM")%>'/>
                                     <portlet:param name="workflowDefinition" value='<%=(String)pageContext.getAttribute("workflowDefinition")%>'/>
                                     <portlet:param name="workflowDefName" value='<%=(String)pageContext.getAttribute("workflowName")%>'/>
                                   </portlet:renderURL>
				<li onmouseover="switchDesc2('processDescription','<c:out value="${processCounter}"/>');" onmouseout="switchDesc3('processDescription','0');" onmousedown="doRedirect('<c:out value="${createProcessURL}"/>')" id='<c:out value="${processTypeNum}"/>'><a href='<c:out value="${createProcessURL}"/>'><c:out value="${workflow.displayName}"/></a></li>
	                           <c:set var="processCounter" value="${processCounter+1}"/>
				</c:forEach>
				</ul>
				</div>
				</td>
				</tr>
				</table>


				<table border="0" cellpadding="0" cellspacing="0" id="processOption1">
				<tr>
				<td class="formPadding options">
				<div class="processType">
				<ul>
				<c:forEach var="workflow" items="${campaignDefs}">
	                           <c:set var="processTypeNum" value="processType${processCounter}"/>
                                   <c:set var="workflowDefinition" value="${workflow.processName}"/>
                                   <c:set var="workflowName" value="${workflow.displayName}"/>
                                   <portlet:renderURL var="createProcessUrl"/>
	                           <c:set var="createCampaignURL"><c:out value="${createProcessUrl}"/>&processPortlet=<c:out value="${campaignPortletID}"/>&tab=<c:out value="${CREATE_PROCESS_FORM}"/>&workflowDefinition=<c:out value="${workflowDefinition}"/></c:set>
				<li onmouseover="switchDesc2('processDescription','<c:out value="${processCounter}"/>');" onmouseout="switchDesc3('processDescription','0');" onmousedown="doRedirect('<c:out value="${createCampaignURL}"/>')" id='<c:out value="${processTypeNum}"/>'><a href='<c:out value="${createCampaignURL}"/>'><c:out value="${workflow.displayName}"/></a></li>
	                           <c:set var="processCounter" value="${processCounter+1}"/>
				</c:forEach>
				</ul>
				</div>
				</td>
				</tr>
				</table>

				<table border="0" cellpadding="0" cellspacing="0" id="processOption2">
				<tr>
				<td class="formPadding options">
				<div class="processType">
				<ul>
				<c:forEach var="workflow" items="${otherDefs}" >
	                           <c:set var="processTypeNum" value="processType${processCounter}"/>
                                   <c:set var="workflowDefinition" value="${workflow.processName}"/>
                                   <c:set var="workflowName" value="${workflow.displayName}"/>
                                   <portlet:renderURL var="createProcessURL">
                                     <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("CREATE_PROCESS_FORM")%>'/>
                                     <portlet:param name="workflowDefinition" value='<%=(String)pageContext.getAttribute("workflowDefinition")%>'/>
                                     <portlet:param name="workflowDefName" value='<%=(String)pageContext.getAttribute("workflowName")%>'/>
                                   </portlet:renderURL>
				<li onmouseover="switchDesc2('processDescription','<c:out value="${processCounter}"/>');" onmouseout="switchDesc3('processDescription','0');" onmousedown="doRedirect('<c:out value="${createProcessURL}"/>')" id='<c:out value="${processTypeNum}"/>'><a href='<c:out value="${createProcessURL}"/>'><c:out value="${workflow.displayName}"/></a></li>
	                           <c:set var="processCounter" value="${processCounter+1}"/>
				</c:forEach>
				</ul>
				</div>
				</td>
				</tr>
				</table>
			</td>
			</tr>
			</table>
		
		</td>

	<!-- this section will reveal a basic description of each choice when a user hovers over items in the lists to the left-->
	
		<td class="description" id="processDescription">
		<div class="processTypeDesc">
		  <c:forEach var="workflow" items="${caDefs}">
		    <p style="padding-right: 15px; padding-left: 15px;"><span class="descriptionHeader">Description:</span><c:out value="${workflow.description}"/></p>
		  </c:forEach>
		  <c:forEach var="workflow" items="${campaignDefs}">
		    <p style="padding-right: 15px; padding-left: 15px;"><span class="descriptionHeader">Description:</span><c:out value="${workflow.description}"/></p>
	  	  </c:forEach>
		  <c:forEach var="workflow" items="${otherDefs}" >
		    <p style="padding-right: 15px; padding-left: 15px;"><span class="descriptionHeader">Description:</span><c:out value="${workflow.description}"/></p>
		  </c:forEach>
		</div>
		</td>
		
		</tr>
		
		<!-- begin inline buttons -->
   <%-- Create link back to list page (i.e. Cancel) --%>
    <portlet:renderURL  var="listURL">
      <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("LIST_VIEW")%>'/>
    </portlet:renderURL>
		
    <tr>
      <td class="inlineButtons" colspan="2"></td>
      <td>
        <a class="cancelButton" href='<c:out value="${listURL}"/>' onmouseover="status='';return true;"><fmt:message key="cancel-button" bundle="${projectsBundle}"/></a>
      </td>
	<td>&nbsp;</td>
    </tr>

		<!-- end inline buttons -->
		
		</table>
     <!-- end basic table for borderless content -->		
     </div>
			
</div>

  <!-- end content -->
</dspel:page>

<!-- End ProjectsPortlet's createProject.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/createProcessStep1.jsp#2 $$Change: 651448 $--%>
