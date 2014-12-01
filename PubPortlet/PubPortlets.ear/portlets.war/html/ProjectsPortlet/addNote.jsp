<%@ page import="javax.portlet.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>


<!-- Begin addNote.jsp -->

<dspel:page>

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>
<style> 
   @import url("<c:url value='/templates/style/css/style.jsp' context="${initParam['atg.bizui.ContextPath']}"/>");
</style>

<script language="JavaScript" type="text/javascript">
  function setTextareaFocus() {
    if (document.getElementById("noteText")){
      document.getElementById("noteText").focus();
    }
  }
</script>

</head>

<body class="actionContent" onLoad="setTextareaFocus()" >
 <div id="confirmHeader"><h2><fmt:message key="add-note-header" bundle="${projectsBundle}"/></h2></div>


<dspel:importbean bean="/atg/epub/servlet/AddNoteFormHandler"/>


 <c:set var="formErrors" value="${false}"/>
 <c:forEach var="outcomeException" items="${fireOutcomeFormHandler.formExceptions}">
    <span class="error"><c:out value="${outcomeException.message}"/><br /></span>
    <c:set var="formErrors" value="${true}"/>
 </c:forEach>

<c:choose>
<c:when test="${ (param.formSubmitted ne null) && !formErrors}">
 <div id="actionContent">
   <p><h3><fmt:message key="add-note-success-msg" bundle="${projectsBundle}"/></h3></p>
   <br />
   <br />
  </div>
   
   <div class="actionOk">
     <table border="0" cellpadding="0" cellspacing="0">
       <tr>
       <td width="100%">&nbsp;</td>
         <td>
           <a href="javascript:parent.document.location=parent.document.location" class="mainContentButton close" onmouseover="status='';return true;"><fmt:message key="close-button" bundle="${projectsBundle}"/></a>
         </td>
       </tr>
     </table>
   </div>
</c:when> 
<c:otherwise>
 <div id="actionContent">

  <dspel:form formid="addForm"  action="addNote.jsp" method="post">

  <%-- set project or process ID, depending on parameter --%>
  <c:choose>
    <c:when test="${param.projectId ne null}">
       <dspel:input type="hidden" bean="AddNoteFormHandler.projectId" value="${param.projectId}"/>
    </c:when>
    <c:otherwise>
       <dspel:input type="hidden" bean="AddNoteFormHandler.processId" value="${param.processId}"/>
    </c:otherwise>
  </c:choose>
  <input type="hidden" name="formSubmitted" value="true" />
    <dspel:input type="hidden" priority="-1" bean="AddNoteFormHandler.addNote" value="1"/>

    <p><label><fmt:message key="add-note-label" bundle="${projectsBundle}"/>:<br />
    <dspel:textarea iclass="textStandard" id="noteText" bean="AddNoteFormHandler.note" rows="4" cols="60"></dspel:textarea>
    </label></p>
  </div>

 <div class="actionOk">
   <table border="0" cellpadding="0" cellspacing="0">
     <tr>
       <td width="100%">&nbsp;</td>
       <td>
         <a href="javascript:document.forms[0].submit();" class="mainContentButton go" onmouseover="status='';return true;"><fmt:message key="add-button-label" bundle="${projectsBundle}"/></a>
       </td>
 
       <td>
         <a href="javascript:parent.showIframe('actionAddNote')" class="mainContentButton delete" onmouseover="status='';return true;"><fmt:message key="cancel-button" bundle="${projectsBundle}"/></a>
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

<!-- End addNote.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/addNote.jsp#2 $$Change: 651448 $--%>
