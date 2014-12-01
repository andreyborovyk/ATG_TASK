<%--
  Asset History (for the Project Portlet)

  @param assetURI     The publishing asset's URI.
  @param assetVersion (optional) asset version

  @version $Id $$Change $
  @updated $DateTime $ $Author $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>

<!-- Begin ProjectsPortlet' assetHistory.jsp -->

<portlet:defineObjects/>

<dspel:page>

  <pws:getCurrentProject var="projectContext"/>

  <fmt:setBundle var="projectsBundle"
                 basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

  <%-- Context for the asset edit page --%>
  <c:set var="assetInfoPath" value="/atg/epub/servlet/ProcessAssetInfo"/>
  <dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>
  <c:set var="projectAssetURI" value="${assetInfo.context.attributes.assetURI}"/>

  <%-- Likewise for asset version --%>
  <c:if test="${ ! empty param.projectAssetVersion }">
    <c:choose>
      <c:when test="${ param.projectAssetVersion == '-1' }">
        <c:remove var="projectAssetVersion" scope="session"/>
      </c:when>
      <c:otherwise>
        <c:set var="projectAssetVersion" value="${param.projectAssetVersion}"
          scope="session"/>
      </c:otherwise>
    </c:choose>
  </c:if>

  <%@ include file="projectConstants.jspf" %>

  <%-- Define page parameters to be accessed from the details fragment, specific to this portlet --%>
  <c:set var="assetHistoryDetailsURI" value="${sessionScope.projectAssetURI}" scope="page"/>
  <c:set var="formHandlerPath" value="/atg/epub/servlet/ProjectDiffFormHandler" scope="page"/>
  <c:set var="portletViewAttribute" value="projectView" scope="page"/>
  <c:set var="diffSuccessView" value="${ASSET_DIFF_VIEW}" scope="page"/>

  <c:choose>
    <c:when test="${ empty sessionScope.projectAssetVersion }">
      <%@ include file="../AssetsPortlet/assetHistoryDetails.jspf" %>
    </c:when>
    <c:when test="${ ! empty sessionScope.projectAssetVersion }">
      <pws:getAsset var="theAsset" uri="${projectAssetURI}"
        workspaceName="${projectContext.project.workspace}"/>
      <c:forEach items="${theAsset.asset.allVersions}" var="av">
        <c:if test="${av.version == sessionScope.projectAssetVersion}">
          <c:set var="assetVersion" value="${av.version}"/>
          <c:if test="${ ! empty av.repositoryItem }">
            <c:set var="item" value="${av.repositoryItem}"/>
            <c:set var="assetVersionURI" value="${av.URI.URIString}"/>
          </c:if>
          <c:if test="${ ! empty av.virtualFile }">
            <c:set var="item" value="${av.virtualFile}"/>
          </c:if>
        </c:if>
      </c:forEach>

      <%--
        Set to true to see debugging output on page and in console
      --%>
      <c:set var="debug" value="false" scope="request"/>

      <%-- the asset context for this page --%>
      <c:set var="assetInfoPath"
        value="/atg/epub/servlet/ProjectHistoryAssetInfo"/>

      <%@ include file="../AssetsPortlet/assetPropertyView.jspf" %>

      <dspel:importbean bean="/atg/epub/servlet/ProjectFormHandler"
        var="projectFormHandler"/>

      <pws:getCurrentProject var="projectContext"/>
      <c:set var="project" value="${projectContext.project}"/>

      <portlet:actionURL var="revertAction"/>
      <dspel:form id="revertForm" formid="revertForm" action="${revertAction}" method="post">
        <dspel:input type="hidden" bean="ProjectFormHandler.asset" value="${projectAssetURI}"/>
        <dspel:input type="hidden" bean="ProjectFormHandler.version" value="${assetVersion}"/>
        <dspel:input type="hidden" bean="ProjectFormHandler.projectId" value="${project.id}"/>
        <dspel:input type="hidden" bean="ProjectFormHandler.revertAsset" value="1" priority="-1"/>
      </dspel:form>

      <script language="JavaScript">
        function submitRevertAsset() {
          var form = document.getElementById("revertForm");
          form.submit();
        }
      </script>

      <div class="contentActions">
        <table cellpadding="0" cellspacing="0" border="0">
          <tr>

            <td width="100%" class="blankSpace">&nbsp;
            </td>

            <td>
              <c:if test="${project.editable}">
                <a href="javascript:submitRevertAsset();" class="mainContentButton modify" onmouseover="status='';return true;">
                  Revert Asset to This Version
                </a>
              </c:if>
            </td>

            <td>
              <c:if test="${ ! empty imap.attributes.atgPreviewURL }">

                <%-- If the PreviewProfileRequestProcessor is running, allow the user to
                     choose who to Preview As. Otherwise just open the
                     previewURL. Note: the preview methods are defined
                     in the previewURLParser.jspf file. --%>
                <dspel:importbean bean="/atg/userprofiling/PreviewProfileRequestProcessor" var="previewParams"/>
                <c:choose>
                  <c:when test="${not empty previewParams}">
                    <a href="javascript:atg.preview.pickUserForPreview('previewAsUser');" class="mainContentButton modify" onmouseover="status='';return true;">
                      <fmt:message key="preview" bundle="${projectsBundle}"/>
                    </a>
                  </c:when>
                  <c:otherwise>
                    <a href="javascript:preview(previewURL);" class="mainContentButton modify" onmouseover="status='';return true;">
                      <fmt:message key="preview" bundle="${projectsBundle}"/>
                    </a>
                  </c:otherwise>
                </c:choose>
              </c:if>
            </td>

            <td>
              <portlet:renderURL var="clearAssetVersionURL">
                <portlet:param name="projectAssetVersion" value="-1"/>
              </portlet:renderURL>
              <a href='<c:out value="${clearAssetVersionURL}"/>'
                class="mainContentButton modify" onmouseover="status='';return true;">
                Cancel
              </a>
            </td>

            <td class="blankSpace">&nbsp;
            </td>

          </tr>
        </table>
      </div>

      <%@ include file="../ProjectsPortlet/previewURLParser.jspf" %>

    </c:when>
  </c:choose>

</dspel:page>

<!-- End ProjectsPortlet' assetHistory.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/assetHistory.jsp#2 $$Change: 651448 $--%>
