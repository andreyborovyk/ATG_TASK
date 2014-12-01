<%@ page import="javax.portlet.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui" %>

<!-- Begin actionNote.jsp -->


<dspel:page>

<dspel:importbean var="profile" bean="/atg/userprofiling/Profile"/>

<%-- if not logged in (session expired) close window and refresh parent --%>

<c:if test="${profile.transient}">
  <script language="JavaScript" type="text/javascript">
    parent.showIframe('approveAction');
    parent.location=parent.location;
   </script>
</c:if>

<c:set var="showCustomForm" value="${param.outcomeFormURI != null}"/>

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

<style>
   @import url("<c:url value='/templates/style/css/style.jsp' context="${initParam['atg.bizui.ContextPath']}"/>");
</style>

<script language="JavaScript" type="text/javascript">
  function setTextareaFocus() {
    if (document.getElementById("noteText") && parent.document.getElementById("approveAction").style.display == "block"){
      document.getElementById("noteText").focus();
    }
  }
</script>

</head>

<body class="actionContent" onLoad="setTextareaFocus()" >
 <div id="confirmHeader">
   <h2><fmt:message key="confirmation-header" bundle="${projectsBundle}"/></h2>
 </div>

  <dspel:importbean bean="/atg/epub/servlet/FireWorkflowOutcomeFormHandler" var="fireOutcomeFormHandler"/>


 <c:set var="formErrors" value="${false}"/>
 <c:if test="${!empty fireOutcomeFormHandler.formExceptions}">
    <c:set var="formErrors" value="${true}"/>
 </c:if>

<c:choose>
<c:when test="${ (param.formSubmitted != null) && !formErrors}">
<script language="JavaScript" type="text/javascript">
 parent.showIframe('approveAction');
 parent.location=parent.location;
</script>
</c:when> 
<c:when test="${ (param.formSubmitted != null) && formErrors}">
 <div id="actionContent">
    <c:forEach var="outcomeException" items="${fireOutcomeFormHandler.formExceptions}">
       <c:set var="exMessage" value="${outcomeException.message}"/>
       <c:if test="${exMessage eq null}">
         <c:set var="exMessage" value="${outcomeException}"/>
       </c:if>
       <p><span class="error"><c:out value="${exMessage}"/></span></p>
    </c:forEach>
   <br />
 </div>
   
   <div class="actionOK">
     <table border="0" cellpadding="0">
       <tr>
         <td width="100%">&nbsp;</td>
         <td class="buttonImage">
           <a href="javascript:parent.document.location=parent.document.location" class="mainContentButton close" onmouseover="status='';return true;"><fmt:message key="close-button" bundle="${projectsBundle}"/></a>
         </td>
         <td class="blankSpace"></td>
       </tr>
     </table>
   </div>
</c:when> 
<c:otherwise>

 <div id="actionContent">
  <c:if test="${!formErrors}">
    <c:choose>
      <c:when test="${showCustomForm}">
         <dspel:include page="${param.outcomeFormURI}">
         <dspel:param name="subjectId" value="${param.projectId}"/>
         </dspel:include>
      </c:when>
      <c:otherwise>
        <h3>
          <web-ui:decodeParameterValue var="outcomeName" value="${param.outcomeName}"/>
          <fmt:message key="action-confirmation-msg" bundle="${projectsBundle}">
            <fmt:param value="${outcomeName}"/>
          </fmt:message>
        </h3>
      </c:otherwise>
    </c:choose>
   <br />
  </c:if>

  <dspel:form name="actionFormName" formid="actionFormName"  action="actionNote.jsp" method="post">
    <input type="hidden" name="formSubmitted" value="true" />
    <dspel:input type="hidden" bean="FireWorkflowOutcomeFormHandler.processId" value="${param.processId}"/>
    <c:if test="${param.projectId != null}">
      <dspel:input type="hidden" bean="FireWorkflowOutcomeFormHandler.projectId" value="${param.projectId}"/>
    </c:if>
    <dspel:input type="hidden" bean="FireWorkflowOutcomeFormHandler.taskElementId" 
                 value="${param.taskId}"/>
    <dspel:input type="hidden" bean="FireWorkflowOutcomeFormHandler.outcomeElementId" 
                 value="${param.outcomeId}"/>
    <dspel:input type="hidden" priority="-1" bean="FireWorkflowOutcomeFormHandler.fireWorkflowOutcome" 
                 value="1"/>
    <p>
      <label for="noteText">
        <fmt:message key="action-note-label" bundle="${projectsBundle}"/>:<br />
      <dspel:textarea id="noteText" bean="FireWorkflowOutcomeFormHandler.actionNote" rows="8" cols="40"></dspel:textarea>
      </label>
    </p>
   </div>

 <div class="actionOK">
   <table border="0" cellpadding="0">
     <tr>
       <td width="100%">&nbsp;</td>
       <td class="buttonImage">
         <a id="okActionButton" href="javascript:okButton=document.getElementById('okActionButton');okButton.disabled=true;document.actionFormName.submit();" class="mainContentButton go" onmouseover="status='';return true;"><fmt:message key="ok-button" bundle="${projectsBundle}"/></a>
       </td>
 
       <td class="buttonImage">
         <a href="javascript:parent.showIframe('approveAction')" class="mainContentButton cancel" onmouseover="status='';return true;"><fmt:message key="cancel-button" bundle="${projectsBundle}"/></a>
       </td>
       <td class="blankSpace"></td>
     </tr>
   </table>
 </div>

  </dspel:form>
</c:otherwise>
</c:choose>


</body>
</html>

</dspel:page>
<!-- End actionNote.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/actionNote.jsp#2 $$Change: 651448 $--%>
