<%@ page import="javax.portlet.*" %>
<%@ page import="atg.portal.servlet.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="web-ui"  uri="http://www.atg.com/taglibs/web-ui" %>

<!-- Begin CampaignPortlet's createProcessStep2.jsp -->

<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>
<fmt:message var="createButtonText" key="create-project-button" bundle="${projectsBundle}"/>

<dspel:page xml="true">

<portlet:defineObjects/>

<%@ include file="projectConstants.jspf" %>


<dspel:importbean bean="/atg/epub/servlet/CreateProcessFormHandler" var="createProcessFormHandler"/>

<% // check non-portlet parameters
  PortalServletRequest portalServletRequest =
    (PortalServletRequest) request.getAttribute( Attribute.PORTALSERVLETREQUEST );
  String wfDefParam = portalServletRequest.getParameter( "workflowDefinition" );
  request.setAttribute("workflowDef", wfDefParam);
%>

<c:if test='${workflowDef eq null}'>
  <c:set var="workflowDef" value="${param.workflowDefinition}"/>
</c:if>
<c:if test="${workflowDef eq null && createProcessFormHandler.workflowName ne null}">
  <c:set var="workflowDef" value="${createProcessFormHandler.workflowName}"/>
</c:if>
<c:if test="${workflowDef eq null}">
   <% // check non-portlet parameters
      String wfParam = portalServletRequest.getParameter( "workflowDefinition" );
   %>
   <c:set var="workflowDef"><%=wfParam%></c:set>
</c:if>

<% // check non-portlet parameters
  String wfDefName = portalServletRequest.getParameter( "workflowDefName" );
  request.setAttribute("workflowDisplayName", wfDefName);
%>
<c:if test='${workflowDisplayName == null}'>
  <c:set var="workflowDisplayName" value="${param.workflowDefName}"/>
</c:if>
<web-ui:decodeParameterValue var="workflowDisplayName" value="${workflowDisplayName}"/>

<portlet:actionURL var="createActionURL">
  <portlet:param name="workflowDefinition" value='<%=(String)pageContext.getAttribute("workflowDef")%>'/>
</portlet:actionURL>

<%-- See if a successURL parameter was specified.  If so, save it as a request
     attribute, and replace the action URL with the URL that invoked this page.
     This is necessary in order to prevent the PortletActionFilter from attempting
     a redirect after the form handler has redirected to the success URL. --%>
<%
  String successURL = portalServletRequest.getParameter("successURL");
  if (successURL != null) {
    pageContext.setAttribute("successURL", successURL);
    pageContext.setAttribute("createActionURL",
                             portalServletRequest.getPortalRequestURI() + "?" +
                             portalServletRequest.getPortalQueryString());
  }
%>

<br />
<!-- begin content -->
<div id="createScreen">

 <div id="intro">
  
  <h2><fmt:message key="create-project-page-title" bundle="${projectsBundle}"/> - <em><c:out value="${workflowDisplayName}"/></em></h2>

  <p><fmt:message key="step-two-helper-text" bundle="${projectsBundle}"/></p>
  
 </div>

 <div id="nonTableContent" class="noborder">
    
  <table cellpadding="0" cellspacing="3" border="0">

    <tr>
      <td colspan="2" class="error" style="text-align: left;">
  <c:if test="${!empty createProcessFormHandler.formExceptions}">
    <fmt:message key="create-process-failed-msg" bundle="${projectsBundle}"/><br />
  </c:if>
        <c:forEach var="exception" items="${createProcessFormHandler.formExceptions}">
    <c:choose>
      <c:when test="${exception.message ne null}">
              <c:out value="${exception.message}"/><br />
      </c:when>
      <c:otherwise>
              <c:out value="${exception}"/><br />
      </c:otherwise>
    </c:choose>
        </c:forEach>
      &nbsp;
      </td>
    </tr>
     <%-- CREATE PROCESS FORM --%>
     <dspel:form formid="createForm" name="createForm" action="${createActionURL}" method="post">
       <dspel:input type="hidden" priority="-1" bean="CreateProcessFormHandler.createProcess" value="1"/>
       <dspel:input type="hidden"  bean="CreateProcessFormHandler.workflowName" value="${workflowDef}"/>
       <dspel:input type="hidden"  bean="CreateProcessFormHandler.successProjectView" value="${PROJECT_DETAIL_VIEW}"/>
       <c:if test="${not empty successURL}">
         <dspel:input type="hidden" bean="CreateProcessFormHandler.createSuccessURL" value="${successURL}"/>
       </c:if>
    <tr>
      <td class="formLabel formLabelRequired">
        <fmt:message key="project-name-create-label" bundle="${projectsBundle}"/>:
      </td>
      <td>
        <dspel:input id="projectNameInput" iclass="formElement formElementInputText" bean="CreateProcessFormHandler.displayName" size="40" maxlength="50" type="text" />
      </td>
    </tr>

    <script language="JavaScript">
      registerOnLoad( function() { focusTo("projectNameInput") } );
      
      function doCreateProcess() {
        // To prevent users from accidentally submitting the form twice,
        // disable the OK/Cancel buttons if the user clicks the OK button.
        document.body.style.cursor = "wait";
        var elem = document.getElementById("createProcessButton");
        elem.className = "goButton disabled";
        elem.onclick = function() { return false; };
        elem = document.getElementById("cancelButton");
        elem.className = "cancelButton disabled";
        elem.onclick = function() { return false; };
        document.forms["createForm"].submit();
      }
    </script>
          
    <tr>
      <td class="formLabel">
        <fmt:message key="description-create-label" bundle="${projectsBundle}"/>:  
      </td>
      <td> 
        <dspel:input iclass="formElement formElementInputText" bean="CreateProcessFormHandler.description" size="40" maxlength="255" type="text" />
      </td>
    </tr>

    <tr>
      <td style="height: 40px;"></td>
      <td>
        <a id="createProcessButton" class="goButton" href='javascript:doCreateProcess();' onmouseover="status='';return true;"><fmt:message key="create-process-button" bundle="${projectsBundle}"/></a>&nbsp;&nbsp;<a id="cancelButton" class="cancelButton" href='/atg/bcc/home' onmouseover="status='';return true;"><fmt:message key="cancel-button" bundle="${projectsBundle}"/></a>
      </td>
    </tr>
      
   </dspel:form>
  </table>
            
 </div>

</div>
  <!-- end content -->
</dspel:page>

<!-- End ProjectsPortlet's createProject.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/createProcessStep2.jsp#2 $$Change: 651448 $--%>
