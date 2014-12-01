<%@ page import="javax.portlet.*,atg.portal.servlet.*,atg.portal.framework.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="web-ui"  uri="http://www.atg.com/taglibs/web-ui" %>

<!-- Begin ProjectsPortlet's projectDetail.jsp -->

<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

<dspel:page xml="true">

<portlet:defineObjects/>

<%@ include file="projectConstants.jspf" %>

<dspel:importbean bean="/atg/epub/servlet/FireWorkflowOutcomeFormHandler" var="fireOutcomeFormHandler"/>
<dspel:importbean bean="/atg/web/ATGSession" var="atgSession"/>

<%-- GET THE CURRENT PROJECT ID --%>
<c:catch var="cpEx">
<pws:getCurrentProject var="projectContext"/>
</c:catch>

<c:if test="${projectContext.project ne null}">

<c:set var="currentProjectId" value="${projectContext.project.id}"/>
<c:set var="projectView" value="${atgSession.projectView}"/>

<%-- Show properties tablet iff there is a projectData item
<c:set var="showPropertiesTablet" value="${true}"/>
--%>
<c:set var="showPropertiesTablet" value="false"/>

 <%-- TABLET URLS --%>
 <%-- NOTE: portlet taglib not supporting EL at the moment...  --%>
 <portlet:renderURL  var="listURL">
   <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("LIST_VIEW")%>'/>
 </portlet:renderURL>

 <portlet:renderURL  var="tasksURL">
   <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("TASKS_VIEW")%>'/>
 </portlet:renderURL>

 <portlet:renderURL  var="processTasksURL">
   <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("PROCESS_TASKS_VIEW")%>'/>
 </portlet:renderURL>

 <portlet:renderURL  var="propertiesURL">
   <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("PROCESS_PROPERTIES_VIEW")%>'/>
 </portlet:renderURL>

 <portlet:renderURL  var="assetsURL">
   <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("ASSETS_VIEW")%>'/>
 </portlet:renderURL>

 <portlet:renderURL  var="notesURL">
   <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("NOTES_VIEW")%>'/>
 </portlet:renderURL>

 <portlet:renderURL  var="lockedAssetsURL">
   <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("LOCKED_ASSETS_VIEW")%>'/>
 </portlet:renderURL>

 <portlet:renderURL  var="unsuppliedAssetsURL">
   <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("UNSUPPLIED_ASSETS_VIEW")%>'/>
   <portlet:param name="clearContext" value="true" />
</portlet:renderURL>

<c:if test="${(!empty param.project) || (!empty param.process)}">
   <portlet:renderURL var="thisPageURL" />
   <%-- used to refresh page without process/project ID --%>
  <script language="JavaScript" type="text/javascript">
    <!--
     document.location.replace('<c:out value="${thisPageURL}"/>');
    // -->
  </script>
</c:if>

<%-- LINK TO PROJECT LIST/SEARCH PAGE --%>
<div id="intro">
  <h2>
      <span class="pathSub">
        <fmt:message key="process-detail-label" bundle="${projectsBundle}" /> :
      </span>
      <c:out value="${projectContext.process.displayName}" />
    </h2>
  <p class="path">
      <a href='<c:out value="${listURL}"/>' onmouseover="status='';return true;">
        &laquo;&nbsp;<fmt:message key="project-list-link-text" bundle="${projectsBundle}" />
      </a>
      &nbsp;&nbsp;
    </p>
  <p>
      <fmt:message key="project-detail-helper-text" bundle="${projectsBundle}" />
  </p>
</div>

<%-- PROCESS TABLETS --%>

<table cellpadding="0" border="0" cellspacing="0">
  <tr>
    <td class="contentTabShadow" style="width: 20px;"></td>
      <%-- properties tablet --%>
      <c:if test="${showPropertiesTablet}">
        <c:choose>
          <c:when test="${(projectView eq PROCESS_PROPERTIES_VIEW)}">
            <td class="contentTab contentTabProcess">
              <span class="tabOn">
                <fmt:message key="properties-tablet-title" bundle="${projectsBundle}" />
              </span>
            </td>
          </c:when>
          <c:otherwise>
            <td class="contentTab contentTabProcess">
              <a href='<c:out value="${propertiesURL}"/>' class="tabOff" onmouseover="status='';return true;">
                <fmt:message key="properties-tablet-title" bundle="${projectsBundle}" />
              </a>
            </td>
          </c:otherwise>
        </c:choose>
      </c:if>

      <%-- tasks tablet --%>
      <c:choose>
        <c:when test="${((projectView eq PROJECT_DETAIL_VIEW) && (projectContext.process.status ne 'Completed')) || (projectView eq TASKS_VIEW) || (projectView eq PROJECT_TASK_DETAIL_VIEW)}">
          <td class="contentTab contentTabProcess">
            <span class="tabOn">
              <fmt:message key="task-tablet-title" bundle="${projectsBundle}" />
            </span>
          </td>
        </c:when>
        <c:otherwise>
          <td class="contentTab contentTabProcess">
            <a href='<c:out value="${tasksURL}"/>' class="tabOff" onmouseover="status='';return true;">
              <fmt:message key="task-tablet-title" bundle="${projectsBundle}" />
            </a>
          </td>
        </c:otherwise>
      </c:choose>

      <%-- assets tablet --%>
      <c:choose>
        <c:when test="${((projectView eq PROJECT_DETAIL_VIEW) && (projectContext.process.status eq 'Completed')) || (projectView eq ASSETS_VIEW) || (projectView eq UNSUPPLIED_ASSETS_VIEW) }">
          <td class="contentTab contentTabProcess">
            <span class="tabOn">
              <fmt:message key="assets-tablet-title" bundle="${projectsBundle}" />
            </span>
          </td>
        </c:when>
        <c:otherwise>
          <td class="contentTab contentTabProcess">
            <a href='<c:out value="${assetsURL}"/>' class="tabOff" onmouseover="status='';return true;">
              <fmt:message key="assets-tablet-title" bundle="${projectsBundle}" />
            </a>
          </td>
        </c:otherwise>
      </c:choose>

      <%-- notes tablet --%>
      <c:choose>
        <c:when test="${(projectView eq NOTES_VIEW) || (projectView eq PROCESS_NOTES_VIEW)}">
          <td class="contentTab contentTabProcess">
            <span class="tabOn">
              <fmt:message key="notes-tablet-title" bundle="${projectsBundle}" />
            </span>
          </td>
        </c:when>
        <c:otherwise>
          <td class="contentTab contentTabProcess">
            <a href='<c:out value="${notesURL}"/>' class="tabOff" onmouseover="status='';return true;">
              <fmt:message key="notes-tablet-title" bundle="${projectsBundle}" />
            </a>
          </td>
        </c:otherwise>
      </c:choose>

      <%-- locked assets tablet --%>
      <c:choose>
        <c:when test="${projectView eq LOCKED_ASSETS_VIEW}">
          <td class="contentTab contentTabProcess">
            <span class="tabOn">
              <fmt:message key="locked-assets-table-title" bundle="${projectsBundle}" />
            </span>
          </td>
        </c:when>
        <c:otherwise>
          <td class="contentTab contentTabProcess">
            <a href='<c:out value="${lockedAssetsURL}"/>' class="tabOff" onmouseover="status='';return true;">
              <fmt:message key="locked-assets-table-title" bundle="${projectsBundle}" />
            </a>
          </td>
        </c:otherwise>
      </c:choose>

    <td class="contentTabShadow tabNote">
      <%-- show process status --%>
      <fmt:message key="status-label" bundle="${projectsBundle}" />:
      <em>
        <c:out value="${projectContext.project.status}" />
      </em>
    </td>
  </tr>

     <!-- under-tab shadows -->
      <tr>
         <td class="contentTabOffBorder"></td>
     <%-- properties tablet --%>
     <c:if test="${showPropertiesTablet}">
       <c:choose>
         <c:when test="${(projectView eq PROCESS_PROPERTIES_VIEW)}">
           <td class="contentTabOnBorderProcess"></td>
         </c:when>
         <c:otherwise>
           <td class="contentTabOffBorder"></td>
         </c:otherwise>
       </c:choose>
     </c:if>
     <%-- tasks tablet --%>
     <c:choose>
       <c:when test="${((projectView eq PROJECT_DETAIL_VIEW) && (projectContext.process.status ne 'Completed')) || (projectView eq TASKS_VIEW) || (projectView eq PROJECT_TASK_DETAIL_VIEW) || (projectView eq PROCESS_TASK_DETAIL_VIEW)}">
         <td class="contentTabOnBorderProcess"></td>
       </c:when>
       <c:otherwise>
         <td class="contentTabOffBorder"></td>
       </c:otherwise>
     </c:choose>
     <%-- assets tablet --%>
     <c:choose>
       <c:when test="${((projectView eq PROJECT_DETAIL_VIEW) && (projectContext.process.status eq 'Completed')) || (projectView eq ASSETS_VIEW) || (projectView eq UNSUPPLIED_ASSETS_VIEW) }">
         <td class="contentTabOnBorderProcess"></td>
       </c:when>
       <c:otherwise>
         <td class="contentTabOffBorder"></td>
       </c:otherwise>
     </c:choose>
     <%-- notes tablet --%>
     <c:choose>
       <c:when test="${(projectView eq NOTES_VIEW) || (projectView eq PROCESS_NOTES_VIEW)}">
         <td class="contentTabOnBorderProcess"></td>
       </c:when>
       <c:otherwise>
         <td class="contentTabOffBorder"></td>
       </c:otherwise>
     </c:choose>
     <%-- locked assets tablet --%>
     <c:choose>
       <c:when test="${projectView eq LOCKED_ASSETS_VIEW}">
         <td class="contentTabOnBorderProcess"></td>
       </c:when>
       <c:otherwise>
         <td class="contentTabOffBorder"></td>
       </c:otherwise>
     </c:choose>

         <td class="contentTabOffBorder"></td>
      </tr>
   </table>


<%-- PROJECT INFO --%>

    <table cellpadding="0" cellspacing="0" border="0" width="100%" id="attributeBarProcess">
  <tr>
    <td>

      <div class="attributes">
    <p>
          <fmt:message key="project-name-label" bundle="${projectsBundle}" />:
          <em>
            <c:out value="${projectContext.process.displayName}" />
          </em>
        </p>

        <%-- GET WORKFLOW TYPE --%>
        <c:catch var="e">
          <pws:getWorkflowDescriptor var="workflow" processId="${projectContext.process.id}"/>
          <c:if test="${workflow.projectWorkflow != null}">
      <p>
              <fmt:message key="project-workflow-definition-label" bundle="${projectsBundle}" />:
              <em>
                <web-ui:getWorkflowElementDisplayName var="processWorkflowDisplayName" element="${workflow.processWorkflow}"/>
                <web-ui:getWorkflowElementDisplayName var="projectWorkflowDisplayName" element="${workflow.projectWorkflow}"/>

                <c:out value="${processWorkflowDisplayName}" />

                <%-- Only display the inner workflow name if it is not the same as the outer workflow --%>
                <c:if test='${processWorkflowDisplayName != projectWorkflowDisplayName}'>
                  - (<c:out value="${projectWorkflowDisplayName}" />)
                </c:if>
              </em>
            </p>
          </c:if>
        </c:catch>

    <p>
          <fmt:message key="start-date" bundle="${projectsBundle}" />:
          <em>
            <fmt:formatDate type="both" dateStyle="medium" timeStyle="short" value="${projectContext.project.creationDate}" />
          </em>
        </p>
        <dspel:tomap var="creator" value="${projectContext.project.creator}"/>
    <p>
          <fmt:message key="creator-label" bundle="${projectsBundle}" />:
          <em>
            <c:out value="${creator.firstName}" />&nbsp;
            <c:out value="${creator.lastName}" />
          </em>
        </p>
      </div>

      <div class="attributeExtras">
        <c:if test="${projectView eq PROJECT_TASK_DETAIL_VIEW}">
     <p>
            <a href='<c:out value="${tasksURL}"/>' class="yellow" onmouseover="status='';return true;">&laquo;&nbsp;
              <fmt:message key="back-to-tasks-link" bundle="${projectsBundle}" />
            </a>
          </p>
 </c:if>
        <c:if test="${projectView eq PROCESS_TASK_DETAIL_VIEW}">
     <p>
            <a href='<c:out value="${processTasksURL}"/>' class="yellow" onmouseover="status='';return true;">&laquo;&nbsp;
              <fmt:message key="back-to-tasks-link" bundle="${projectsBundle}" />
            </a>
          </p>
 </c:if>
        <c:if test="${projectView eq ASSETS_VIEW}">
          <pws:getUnsuppliedAssets var="uaResults"  processId="${projectContext.process.id}"/>
            <c:if test="${uaResults.allUnsuppliedAssetCount gt 0}">
        <p>
                <a href='<c:out value="${unsuppliedAssetsURL}"/>' class="yellow" onmouseover="status='';return true;">
                  <fmt:message key="unsupplied-assets-link" bundle="${projectsBundle}" />:
                  <em>
                    <c:out value="${uaResults.requiredUnsuppliedAssetCount}" />
                  </em>&nbsp;&raquo;
                </a>
              </p>
            </c:if>
 </c:if>
        <c:if test="${projectView eq UNSUPPLIED_ASSETS_VIEW}">
       <p>
              <a href='<c:out value="${assetsURL}"/>' class="yellow" onmouseover="status='';return true;">&laquo;&nbsp;
                <fmt:message key="back-to-process-link" bundle="${projectsBundle}" />
              </a>
            </p>
 </c:if>
      </div>

   <%-- Unsupplied assets
   <div class="attributeExtras">
     <table cellpadding="0" cellspacing="0" border="0" width="50%">
       <tr>
         <td class="attributeExtras rightAligned" valign="top">
           unsupplied assets:
         </td>
         <pws:getUnsuppliedAssets var="uaResults"  projectId="${currentProjectId}"/>
         <td class="attributeExtras leftAligned">
             <em><c:out value="${uaResults.allUnsuppliedAssetCount}"/></em><br />
             <em><c:out value="${uaResults.requiredUnsuppliedAssetCount}"/></em><br />
         </td>

       </tr>
     </table>
   </div>
--%></td>
  </tr>
</table>

 <c:choose>
   <c:when test="${((projectView eq PROJECT_DETAIL_VIEW) && (projectContext.process.status eq 'Completed')) || (projectView eq ASSETS_VIEW)}">
    <dspel:include src="projectAssets.jsp" />
   </c:when>
   <c:when test="${projectView eq UNSUPPLIED_ASSETS_VIEW}">
    <dspel:include src="unsuppliedAssets.jsp"  />
   </c:when>
   <c:when test="${(projectView eq NOTES_VIEW) || (projectView eq PROCESS_NOTES_VIEW)}">
    <dspel:include src="projectNotes.jsp"  />
   </c:when>
   <c:when test="${projectView eq PROCESS_PROPERTIES_VIEW}">
    <dspel:include src="processProperties.jsp"  />
   </c:when>
   <c:when test="${(projectView eq PROJECT_TASK_DETAIL_VIEW) || (projectView eq PROCESS_TASK_DETAIL_VIEW)}">
    <dspel:include src="taskDetail.jsp"  />
   </c:when>
   <c:when test="${projectView eq LOCKED_ASSETS_VIEW}">
    <dspel:include src="projectLockedAssetsIndex.jsp"  />
   </c:when>
   <c:otherwise>
    <dspel:include src="projectTasks.jsp"  />
   </c:otherwise>
 </c:choose>

</c:if>
<c:if test="${projectContext.project eq null}">
  <dspel:include src="invalidProject.jsp"  />
</c:if>

</dspel:page>

<!-- End ProjectsPortlet's projectDetail.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/projectDetail.jsp#2 $$Change: 651448 $--%>
