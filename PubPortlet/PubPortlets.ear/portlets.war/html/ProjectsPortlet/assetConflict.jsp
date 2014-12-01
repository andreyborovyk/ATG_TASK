<%--
  Asset Conflict Resolution Page
  Displays the diff between 2 conflicting asset versions and allows a merge.

  @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/assetConflict.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %> 

<!-- Begin assetConflict.jsp -->
<dspel:page>
<portlet:defineObjects/>

<%@ include file="projectConstants.jspf" %>

<%-- Wrap the whole page in a transaction.  This helps performance 
     when one or more properties cannot be cached. --%>
<dspel:demarcateTransaction var="conflictPrint">

<pws:getCurrentProject var="projectContext"/>

<c:set var="project" value="${projectContext.project}"/>
<c:set var="process" value="${projectContext.process}"/>
<c:set var="currentProjectId" value="${project.id}"/>

<dspel:importbean bean="/atg/epub/servlet/ProjectDiffFormHandler" var="conflictFormHandler"/>
<c:set value="${conflictFormHandler.item1}" var="item1"/>
<c:set value="${conflictFormHandler.item2}" var="item2"/>
<c:set var="assetURI" value="${conflictFormHandler.assetURI}"/>

<pws:getAsset var="thisAsset" uri="${assetURI}" workspaceName="${project.workspace}" />

<c:choose>
  <c:when test="${! empty thisAsset.workingVersion.virtualFile}"> 
    <c:set var="isRepositoryItem" value="false"/>
  </c:when>
  <c:otherwise>
    <c:set var="isRepositoryItem" value="true"/>
  </c:otherwise>
</c:choose>

<c:set var="isDeleted" value="false"/>
<c:if test="${thisAsset.asset.mainVersion.deleted}">
  <c:set var="isDeleted" value="true"/>
</c:if>
<c:if test="${empty thisAsset.workingVersion}">
  <c:set var="isDeleted" value="true"/>
</c:if>

<%-- Item 1 view mapping --%>
<biz:getItemMapping item="${item1}" var="imap1" showExpert="true" mode="conflict" propertyList="${conflictFormHandler.diffProperties}">

  <!-- Set the item mapping into a request scoped variable -->
  <c:set value="${imap1}" var="imap1" scope="request"/>

  <!-- Set the name of the Form's value dictionary property -->
  <c:set target="${imap1}" property="valueDictionaryName" value="item1"/>

</biz:getItemMapping>

<%-- Item 2 view mapping --%>
<biz:getItemMapping item="${item2}" var="imap2" showExpert="true" mode="conflict" propertyList="${conflictFormHandler.diffProperties}">

  <!-- Set the item mapping into a request scoped variable -->
  <c:set value="${imap2}" var="imap2" scope="request"/>

  <!-- Set the name of the Form's value dictionary property -->
  <c:set target="${imap2}" property="valueDictionaryName" value="item2"/>

</biz:getItemMapping>

<fmt:setBundle var="propertiesBundle" basename="atg.epub.PublishingRepositoryResources"/>
<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

<portlet:actionURL  var="actionURL"/>
<portlet:renderURL  var="cancelMergeActionURL">
  <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("ASSETS_VIEW")%>'/>
</portlet:renderURL>



<%-- BREADCRUMB/LINKS --%>

<%-- NOTE: portlet taglib not supporting EL at the moment...  --%>
<portlet:renderURL  var="listURL">
  <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("LIST_VIEW")%>'/>
</portlet:renderURL>

<portlet:renderURL  var="assetsURL">
  <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("ASSETS_VIEW")%>'/>
</portlet:renderURL>

<div id="intro">
  <h2><span class="pathSub"><fmt:message key="process-detail-label" bundle="${projectsBundle}"/> - </span><c:out value="${process.displayName}"/> - <c:out value="${thisAsset.workingVersion.repositoryItem.itemDisplayName}"/></h2>
  <p class="path"><a href='<c:out value="${listURL}"/>' onmouseover="status='';return true;">&laquo;&nbsp;<fmt:message key="project-list-link-text" bundle="${projectsBundle}"/></a>&nbsp;&nbsp;
  <a href='<c:out value="${assetsURL}"/>' onmouseover="status='';return true;">&laquo;&nbsp;<fmt:message key="back-to-process-link" bundle="${projectsBundle}"/></a>&nbsp;
  </p>
  <p><fmt:message key="asset-merge-helper-text" bundle="${projectsBundle}"/></p>
</div>

<!-- begin content -->

<table cellpadding="0" border="0" cellspacing="0" width="100%">
  <tr>
    <td class="backToPage"></td>
    <td class="contentTab contentTabAsset"><span class="tabOn">Merge Assets</span></td>
    <td class="contentTabShadow" width="100%">&nbsp;</td>
  </tr>

  <tr>
    <td class="contentTabOffBorder"></td>
    <td class="contentTabOnBorderAsset"></td>
    <td class="contentTabOffBorder"></td>
  </tr>
</table>

<%-- ASSET INFO  --%>

<table cellpadding="0" cellspacing="0" border="0" width="100%" id="attributeBarAsset">
  <tr>
    <td>
      <div class="attributes">
        <p><fmt:message key="asset-name" bundle="${projectsBundle}"/>: <em><c:out value="${thisAsset.workingVersion.repositoryItem.itemDisplayName}"/></em></p>
        <p><fmt:message key="asset-type-detail-label" bundle="${projectsBundle}"/>: <em><c:out value="${thisAsset.workingVersion.repositoryItem.itemDescriptor.itemDescriptorName}"/></em></p>
        <p><fmt:message key="current-version-detail-label" bundle="${projectsBundle}"/>: <em><c:out value="${thisAsset.workingVersion.version}"/></em></p>
      </div>
    </td>
  </tr>
</table>

<%-- Display any errors. --%>
<div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0">
    <tr>
      <td class="blankSpace">&nbsp;</td>
      <td><h2><fmt:message key="asset-conflict-title" bundle="${projectsBundle}"/></h2></td>
      <td width="100%" class="error rightAlign">
        <c:forEach var="exception" items="${conflictFormHandler.formExceptions}">
          <c:out value="${exception.message}"/><br />
        </c:forEach>
      </td>
      <td class="blankSpace">&nbsp;</td>
    </tr>
  </table>
</div>

<%-- Descriptive text for each of the item versions. --%>
<fmt:message key="asset-conflict-mine" bundle="${projectsBundle}" var="item1Title"/>
<fmt:message key="asset-conflict-theirs" bundle="${projectsBundle}" var="item2Title"/>
<c:set var="deletedWarning" value="${conflictFormHandler.deletedWarning}"/>

<%-- Use the viewmapped .jsp to display the property diffs side by side --%>
<dspel:form id="assetConflictForm" formid="assetConflictForm" action="${actionURL}" method="post">

  <%-- Call merge() on the form handler to handle the submit. --%>
  <dspel:input type="hidden" priority="-1" bean="ProjectDiffFormHandler.mergeConflicts" value="1"/>
  <dspel:input type="hidden" bean="ProjectDiffFormHandler.projectId" value="${currentProjectId}"/> 

  <%-- If the merge succeeds, go back to the asset view. --%>
  <dspel:input type="hidden" bean="ProjectDiffFormHandler.viewAttribute" value="projectView"/>
  <dspel:input type="hidden" bean="ProjectDiffFormHandler.successView" value="${ASSETS_VIEW}"/>

  <%-- Display a warning message if the parent asset version is deleted --%>
  <c:if test="${deletedWarning ne null}">
    <div class="contentActions">
      <table cellpadding="0" cellspacing="0" border="0">
        <tr>
          <td class="blankSpace">&nbsp;</td>
          <td width="100%" class="error leftAlign">
            <c:out value="${deletedWarning}"/><br />
          </td>
          <td class="blankSpace">&nbsp;</td>
        </tr>
      </table>
    </div>
  </c:if>

  <c:forEach items="${imap1.viewMappings}" var="view1" varStatus="i">
    <c:set value="${view1}" var="view1" scope="request"/>
    <c:set value="${imap2.viewMappings[i.index]}" var="view2" scope="request"/>
    <c:set value="${item1}" var="item1" scope="request"/>
    <c:set value="${item2}" var="item2" scope="request"/>
    <c:set value="${item1Title}" var="item1Title" scope="request"/>
    <c:set value="${item2Title}" var="item2Title" scope="request"/>
    <c:set value="${true}" var="conflictMode" scope="request"/>
    <c:set value="/atg/epub/servlet/ProjectDiffFormHandler" var="conflictFormHandlerPath" scope="request"/>
    <dspel:include otherContext="${view1.contextRoot}" page="${view1.uri}"/>
  </c:forEach>

</dspel:form>

<%-- call handleMergeCurrentVersion() in ProjectDiffFormHandler --%>
<dspel:form id="selectCurrentVersionForm" formid="selectCurrentVersionForm" action="${actionURL}" method="post">
  <dspel:input type="hidden" priority="-1" bean="ProjectDiffFormHandler.mergeCurrentVersion" value="1"/>
  <%-- If the merge succeeds, go back to the asset view. --%>
  <dspel:input type="hidden" bean="ProjectDiffFormHandler.viewAttribute" value="projectView"/>
  <dspel:input type="hidden" bean="ProjectDiffFormHandler.successView" value="${ASSETS_VIEW}"/>
  <dspel:input type="hidden" bean="ProjectDiffFormHandler.projectId" value="${currentProjectId}"/>

</dspel:form>

<%-- call handleMergeCurrentVersion() in ProjectDiffFormHandler --%>
<dspel:form id="selectLatestVersionForm" formid="selectLatestVersionForm" action="${actionURL}" method="post">
  <dspel:input type="hidden" priority="-1" bean="ProjectDiffFormHandler.mergeLatestVersion" value="1"/>
  <%-- If the merge succeeds, go back to the asset view. --%>
  <dspel:input type="hidden" bean="ProjectDiffFormHandler.viewAttribute" value="projectView"/>
  <dspel:input type="hidden" bean="ProjectDiffFormHandler.successView" value="${ASSETS_VIEW}"/>
  <dspel:input type="hidden" bean="ProjectDiffFormHandler.projectId" value="${currentProjectId}"/>

</dspel:form>

<!-- Button actions -->
<div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0">
    <tr>
      <td class="blankSpace" width="100%">&nbsp;</td>
      <%-- Use my version button --%>
      <td class="buttonImage"><a href='javascript:document.getElementById("selectCurrentVersionForm").submit();' class="mainContentButton merge" onmouseover="status='';return true;">
        <fmt:message key="resolve-use-my-version" bundle="${projectsBundle}"/></a>
      </td>
      <%-- Use latest version button --%>
      <td class="buttonImage"><a href='javascript:document.getElementById("selectLatestVersionForm").submit();' class="mainContentButton merge" onmouseover="status='';return true;">
        <fmt:message key="resolve-use-latest-version" bundle="${projectsBundle}"/></a>
      </td>
      <%-- Don't show merge button if an asset has been deleted --%>
      <c:if test="${isDeleted == 'false'}">
        <%-- Merge assets button -if repository item only --%>
        <c:if test="${isRepositoryItem == 'true'}">
          <td class="buttonImage"><a href='javascript:document.getElementById("assetConflictForm").submit();' class="mainContentButton merge" onmouseover="status='';return true;">
            <fmt:message key="merge-properties-button" bundle="${projectsBundle}"/></a>
          </td>
        </c:if>
      </c:if>
      <%-- Cancel button --%>
      <td class="buttonImage"><a href='<c:out value="${cancelMergeActionURL}"/>' class="mainContentButton cancel" onmouseover="status='';return true;">
        <fmt:message key="cancel-merge-button" bundle="${projectsBundle}"/></a>
      </td>
      <td class="blankSpace">&nbsp;</td>
    </tr>
  </table>
</div>

</dspel:demarcateTransaction>

</dspel:page>
<!-- End assetConflict.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/assetConflict.jsp#2 $$Change: 651448 $--%>
