<%@ page import="javax.portlet.*,atg.portal.servlet.*,atg.portal.framework.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<!-- Begin ProjectsPortlet's projectAssetDetail.jsp -->

<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

<dspel:page xml="true">

<portlet:defineObjects/>
<dspel:importbean bean="/atg/web/ATGSession" var="atgSession"/>
<%@ include file="projectConstants.jspf" %>


<dspel:importbean bean="/atg/epub/servlet/ProjectDiffFormHandler" var="conflictFormHandler"/>

<%-- this bean used to get process portlet ID based on workflow type --%>
<dspel:importbean bean="/atg/bizui/portlet/PortletConfiguration" var="bizuiPortletConfig"/>

<%-- GET THE CURRENT PROJECT ID --%>
<pws:getCurrentProject var="projectContext"/>

<c:set var="project" value="${projectContext.project}"/>
<c:set var="process" value="${projectContext.process}"/>

<c:set var="currentProjectId" value="${project.id}"/>
<c:set var="projectView" value="${atgSession.projectView}"/>


<%-- 
  The AssetInfo component for these pages
--%>
<c:set var="assetInfoPath" scope="request"
  value="/atg/epub/servlet/ProcessAssetInfo"/>
<dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>

<%--
  The clearContext and clearAttributes parameters will be set to
  'true' when user comes to this page by picking an existing asset
  from the process assets list. 
--%>
<c:if test="${ 'true' == param.clearContext }">
  <c:set target="${assetInfo}" property="clearContext" value="true"/>
</c:if>

<c:if test="${ 'true' == param.clearAttributes }">
  <c:set target="${assetInfo}" property="clearAttributes" value="true"/>
</c:if>

<c:if test="${ 'true' == param.pushContext }">
  <c:set target="${assetInfo}" property="pushContext" value="true"/>
</c:if>

<c:choose>
  <%-- 
    If the asset URI has been modified by the form handler the
    assetURI attribute in the context will be correct
  --%>
  <c:when test="${ ! empty param.assetURI }">
    <c:set var="assetURI" scope="request" value="${param.assetURI}"/>
  </c:when>
  <c:when test="${ ! empty assetInfo.context.attributes.assetURI }">
    <c:set var="assetURI" scope="request"
      value="${ assetInfo.context.attributes.assetURI }"/>
  </c:when>
</c:choose>

<c:choose>
  <c:when test="${assetURI eq null}">
    <c:set var="assetURI" value="${sessionScope.projectAssetURI}"/>
  </c:when>
  <c:otherwise>
    <c:set var="projectAssetURI" value="${assetURI}" scope="session"/>
  </c:otherwise>
</c:choose>

<c:if test="${projectView eq ASSET_CONFLICT_VIEW || projectView eq ASSET_DIFF_VIEW}">
  <c:set var="assetURI" value="${conflictFormHandler.assetURI}"/>
</c:if>

<c:if test="${ ! empty assetURI }">
  <pws:getAsset var="thisAsset" uri="${assetURI}" workspaceName="${project.workspace}" />
</c:if>

<%-- If the working version is null, use the main version --%>
<c:choose>
  <c:when test="${ ! empty thisAsset.workingVersion }">
    <c:set var="asset" value="${thisAsset.workingVersion}"/>
  </c:when>
  <c:otherwise>
    <c:set var="asset" value="${thisAsset.asset.mainVersion}"/>
  </c:otherwise>
</c:choose>


<%-- BREADCRUMB/LINKS --%>

<%-- NOTE: portlet taglib not supporting EL at the moment...  --%>
<portlet:renderURL  var="listURL">
  <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("LIST_VIEW")%>'/>
</portlet:renderURL>

<portlet:renderURL  var="assetsURL">
  <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("ASSETS_VIEW")%>'/>
  <portlet:param name="tab" value='<%=(String)pageContext.getAttribute("ASSETS_VIEW")%>'/>
</portlet:renderURL>

<div id="intro">
  <h2>
    <span class="pathSub">
      <fmt:message key="process-detail-label" bundle="${projectsBundle}"/> : 
      <c:out value="${process.displayName}"/> : 
    </span>
    <c:if test="${asset ne null}">
      <c:out value="${asset.repositoryItem.itemDisplayName}"/>
    </c:if>
  </h2>
  <p class="path">

    <c:choose>
      <c:when test="${ ! empty assetInfo.attributes.firstAssetEditContextLevel }">
        <c:set var="firstAssetEditContextLevel"
          value="${ assetInfo.attributes.firstAssetEditContextLevel }"/>
      </c:when>
      <c:otherwise>
        <c:set var="firstAssetEditContextLevel" value="1"/>
      </c:otherwise>
    </c:choose>

    <c:choose>
      <%-- 
        If there's a special client of this page, allow it to specifiy
        its own back-to URL and text
      --%>
      <c:when test="${ ! empty assetInfo.attributes.backToText }">
        <c:if test="${ assetInfo.contextLevel == firstAssetEditContextLevel }">
          <a href='<c:out value="${assetInfo.attributes.backToURL}"/>' onmouseover="status='';return true;">
            &laquo;&nbsp;
            <c:out value="${ assetInfo.attributes.backToText }"/>
          </a>
        </c:if>
      </c:when>
      <c:otherwise>
        <a href='<c:out value="${listURL}&processPortlet=${bizuiPortletConfig.defaultProcessPortletID}&projectView=${LIST_VIEW}"/>' onmouseover="status='';return true;">
          &laquo;&nbsp;
          <fmt:message key="project-list-link-text" bundle="${projectsBundle}"/>
        </a>
        <a href='<c:out value="${assetsURL}"/>' onmouseover="status='';return true;">
          &laquo;&nbsp;<fmt:message key="back-to-process-link" bundle="${projectsBundle}"/>
        </a>
      </c:otherwise>
    </c:choose>
    &nbsp;
  </p>
  <p>
    <fmt:message key="asset-detail-helper-text" bundle="${projectsBundle}"/>
  </p>
</div>

<c:if test="${ ! empty assetInfo.attributes.contextActionURL }">
  <c:set var="contextActionURL" scope="request"
    value="${assetInfo.attributes.contextActionURL}"/>
</c:if>


<%-- ASSET DETAIL TABLETS --%>

 <portlet:renderURL  var="assetEditURL">
   <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("ASSET_EDIT_VIEW")%>'/>
   <portlet:param name="assetURI" value='<%=(String)pageContext.getAttribute("assetURI")%>'/>
 </portlet:renderURL>
 <portlet:renderURL  var="assetHistoryURL">
   <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("ASSET_HISTORY_VIEW")%>'/>
   <portlet:param name="assetURI" value='<%=(String)pageContext.getAttribute("assetURI")%>'/>
   <c:if test="${ 'true' == param.clearContext }">
     <portlet:param name="projectAssetVersion" value='-1'/>
   </c:if>
   </portlet:renderURL>
 <portlet:renderURL  var="assetNotesURL">
   <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("ASSET_NOTES_VIEW")%>'/>
   <portlet:param name="assetURI" value='<%=(String)pageContext.getAttribute("assetURI")%>'/>
 </portlet:renderURL>

<table cellpadding="0" border="0" cellspacing="0" width="100%">
  <tr>
    <td class="backToPage"></td>
      <c:if test="${ assetInfo.contextLevel > firstAssetEditContextLevel }">
        <td class="backToPage">
          <a href="javascript:popContext();" onmouseover="status='';return true;">&laquo; 
            <fmt:message key="backToParentAsset" bundle="${projectsBundle}"/> : 
          </a>
        </td>
      </c:if>

      <c:choose>
        <c:when test="${assetInfo.contextLevel > firstAssetEditContextLevel && !empty assetInfo.context.attributes.pageView}">
          <c:set var="assetContextPageView" scope="request" value="${assetInfo.context.attributes.pageView}"/>
        </c:when>
        <c:when test="${assetInfo.contextLevel > firstAssetEditContextLevel  && !empty atgSession.assetContextPageView}">
          <c:set var="assetContextPageView" scope="request" value="${ASSET_EDIT_VIEW}"/>
        </c:when>
        <c:otherwise>
          <c:set var="assetContextPageView" scope="request" value="${projectView}"/>
        </c:otherwise>
      </c:choose>
      <c:set target="${atgSession}" property="assetContextPageView" value="${null}"/>

     <%-- asset properties tablet --%>
     <c:choose>
       <c:when test="${assetContextPageView eq ASSET_EDIT_VIEW}">
         <td class="contentTab contentTabAsset"><span class="tabOn"><fmt:message key="asset-properties-tablet" bundle="${projectsBundle}"/></span></td>
       </c:when>
       <c:otherwise>
         <td class="contentTab contentTabAsset"><a href='<c:out value="${assetEditURL}"/>' class="tabOff" onmouseover="status='';return true;"><fmt:message key="asset-properties-tablet" bundle="${projectsBundle}"/></a></td>
       </c:otherwise>
     </c:choose>
     <%-- asset history tablet --%>
     <c:choose>
       <c:when test="${assetContextPageView eq ASSET_HISTORY_VIEW || assetContextPageView eq ASSET_DIFF_VIEW}">
         <td class="contentTab contentTabAsset"><span class="tabOn"><fmt:message key="asset-history-tablet" bundle="${projectsBundle}"/></span></td>
       </c:when>
       <c:otherwise>
         <td class="contentTab contentTabAsset"><a href='<c:out value="${assetHistoryURL}"/>' class="tabOff" onmouseover="status='';return true;"><fmt:message key="asset-history-tablet" bundle="${projectsBundle}"/></a></td>
       </c:otherwise>
     </c:choose>
    <td class="contentTabShadow" width="100%">&nbsp;</td>
  </tr>
  <tr>
     <td class="contentTabOffBorder"></td>
     
     <c:if test="${ assetInfo.contextLevel > firstAssetEditContextLevel }">
       <td class="contentTabOffBorder"></td>
     </c:if>
     <%-- asset properties tablet --%>
     <c:choose>
       <c:when test="${assetContextPageView eq ASSET_EDIT_VIEW}">
  <td class="contentTabOnBorderAsset"></td>
       </c:when>
       <c:otherwise>
         <td class="contentTabOffBorder"></td>
       </c:otherwise>
     </c:choose>
     <%-- asset history tablet --%>
     <c:choose>
       <c:when test="${assetContextPageView eq ASSET_HISTORY_VIEW || assetContextPageView eq ASSET_DIFF_VIEW}">
  <td class="contentTabOnBorderAsset"></td>
       </c:when>
       <c:otherwise>
         <td class="contentTabOffBorder"></td>
       </c:otherwise>
     </c:choose>
    <td class="contentTabOffBorder"></td>
  </tr>
</table>


  <%-- ASSET INFO  --%>
  <table cellpadding="0" cellspacing="0" border="0" width="100%" id="attributeBarAsset">
    <tr>
      <td>
        <c:if test="${asset ne null}">
        <div class="attributes">
          <p>
            <fmt:message key="asset-name" bundle="${projectsBundle}"/>: 
            <em>
              <c:out value="${asset.repositoryItem.itemDisplayName}"/>
            </em>
          </p>
          <p>
            <fmt:message key="asset-type-detail-label" bundle="${projectsBundle}"/>: 
            <em><c:out value="${asset.repositoryItem.itemDescriptor.itemDescriptorName}"/></em>
          </p>
          <p><fmt:message key="current-version-detail-label" bundle="${projectsBundle}"/>: 
            <em><c:out value="${asset.version}"/></em>
          </p>
        </div>
        </c:if>
      </td>
    </tr>
  </table>

  <%-- display either assets or properties depending on projectView parameter --%>
  <c:choose>
    <c:when test="${assetContextPageView eq  ASSET_EDIT_VIEW}">
      <dspel:include page="assetEditPage.jsp"/>
    </c:when>
    <c:when test="${assetContextPageView eq  ASSET_NOTES_VIEW}">
      <dspel:include page="assetNotes.jsp"/>
    </c:when>
    <c:when test="${assetContextPageView eq  ASSET_HISTORY_VIEW}">
      <dspel:include page="assetHistory.jsp"/>
    </c:when>
    <c:when test="${assetContextPageView eq  ASSET_DIFF_VIEW}">
      <dspel:include page="assetDiff.jsp"/>
    </c:when>
    <c:when test="${assetContextPageView eq  ASSET_CONFLICT_VIEW}">
      <dspel:include page="assetConflict.jsp"/>
    </c:when>
    <c:otherwise>
      <dspel:include page="assetEditPage.jsp"/>
    </c:otherwise>
  </c:choose>

  <%@ include file="contextForm.jspf" %>

</dspel:page>

<!-- End ProjectsPortlet's projectAssetDetail.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/projectAssetDetail.jsp#2 $$Change: 651448 $--%>
