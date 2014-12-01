<%@ page import="javax.portlet.*" %>
<%@ page import="atg.portal.framework.*" %>
<%@ page import="atg.portal.servlet.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<!-- Begin ProjectsPortlet's projectNotes.jsp -->
<dspel:page>
<portlet:defineObjects/>
<%@ include file="projectConstants.jspf" %>

<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>
<fmt:message var="addNoteButtonLabel" key="add-note-button-label" bundle="${projectsBundle}"/>

<%-- GET THE CURRENT PROJECT --%>
<pws:getCurrentProject var="projectContext"/>

<dspel:importbean bean="/atg/epub/servlet/AddNoteFormHandler"/>


<portlet:actionURL  var="actionURL"/>

<portlet:renderURL  var="projectNotesURL">
  <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("NOTES_VIEW")%>'/>
</portlet:renderURL>

<portlet:renderURL  var="processNotesURL">
  <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("PROCESS_NOTES_VIEW")%>'/>
</portlet:renderURL>



<c:if test="${projectContext.project ne null}">
  <c:set var="currentProjectId" value='${projectContext.project.id}'/>

<c:choose>
  <c:when test="${projectView eq PROCESS_NOTES_VIEW}">
    <c:set var="viewProcessNotes" value="${true}"/>
  </c:when>
  <c:otherwise>
    <c:set var="viewProcessNotes" value="${false}"/>
  </c:otherwise>
</c:choose>
    
<!-- Add note form -->
<div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tr>
      <td class="blankSpace">&nbsp;</td>

      <%-- REMOVED PROCESS HISTORY LINK 
      <c:choose>
         <c:when test="${viewProcessNotes}">
             <td><h2><fmt:message key="current-view-label" bundle="${projectsBundle}"/>: &nbsp;<fmt:message key="process-notes-link" bundle="${projectsBundle}"/>&nbsp;&nbsp;|&nbsp;&nbsp;<a href='<c:out value="${projectNotesURL}"/>' onmouseover="status='';return true;"><fmt:message key="project-notes-link" bundle="${projectsBundle}"/></a></h2></td>
         </c:when>
         <c:otherwise>
             <td><h2><fmt:message key="current-view-label" bundle="${projectsBundle}"/>: <fmt:message key="project-notes-link" bundle="${projectsBundle}"/>&nbsp;&nbsp;|&nbsp;&nbsp;<a href='<c:out value="${processNotesURL}"/>' onmouseover="status='';return true;"><fmt:message key="process-notes-link" bundle="${projectsBundle}"/></a></h2></td>
         </c:otherwise>
      </c:choose>
      --%>

      <c:url var="iframeUrl" context="/PubPortlets" value="/html/ProjectsPortlet/addNote.jsp">
          <c:choose>
            <c:when test="${viewProcessNotes}">
              <c:param name="processId" value="${projectContext.process.id}"/>
            </c:when>
            <c:otherwise>
              <c:param name="projectId" value="${currentProjectId}"/>
            </c:otherwise>
	   </c:choose>
      </c:url>
      <td class="alertMessage">&nbsp;</td>
      <td class="toolBar_button"><a href="javascript:document.getElementById('processNoteIframe').src='<c:out value="${iframeUrl}"/>';showIframe('actionAddNote')" class="add" onmouseover="status='';return true;"><img src='<c:url context="/PubPortlets" value="/html/ProjectsPortlet/images/icon_note.gif"/>'  alt="Add Note" width="16" height="16" />&nbsp;<c:out value="${addNoteButtonLabel}"/></a></td>

    </tr>
  </table>
</div>

<c:choose>
  <c:when test="${projectView eq PROCESS_NOTES_VIEW}">
    <c:set var="historyItems" value="${projectContext.process.history}"/>
  </c:when>
  <c:otherwise>
    <c:set var="historyItems" value="${projectContext.project.history}"/>
  </c:otherwise>
</c:choose>
    
<div id="nonTableContent">
  <c:forEach var="history" items="${historyItems}">
    <dspel:tomap var="creator" value="${history.profile}"/>
    <p><fmt:message key="note-author" bundle="${projectsBundle}"/>:&nbsp;<em><c:out value="${creator.firstName}"/>&nbsp;<c:out value="${creator.lastName}"/></em><br />
    <fmt:message key="note-date-entered" bundle="${projectsBundle}"/>:&nbsp;<em><fmt:formatDate type="both" timeStyle="short" value="${history.timestamp}"/></em><br />
    <fmt:message key="note-action" bundle="${projectsBundle}"/>:&nbsp;<em>
    <c:choose>
      <c:when test="${history.action eq 'rejectDeployment'}">
        <c:forEach items="${history.actionParams}" var="actionParam" varStatus="st">
         <c:if test="${st.count == 1}">
          <c:set var="firstParam" value="${actionParam}"/>
         </c:if>
        </c:forEach>
        <fmt:message key="reject-deployment-action" bundle="${projectsBundle}">
         <fmt:param value="${firstParam}"/>
        </fmt:message>
    </c:when>
    <c:otherwise>
      <c:out value="${history.action}"/>
    </c:otherwise>
    </c:choose>
    </em>
    </p>
    
    <p><c:out value="${history.description}"/></p>
        
<p class="sep"></p>

  </c:forEach>

</div>

<div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0">
    <tr>
      <td width="100%" class="blankSpace">&nbsp;</td>
    </tr>
  </table>
</div>

</c:if>

<c:if test="${projectContext.project eq null}">
   <fmt:message key="no-current-project-message" bundle="${projectsBundle}"/>
</c:if> 
</dspel:page>
<!-- End ProjectsPortlet's projectNotes.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/projectNotes.jsp#2 $$Change: 651448 $--%>
