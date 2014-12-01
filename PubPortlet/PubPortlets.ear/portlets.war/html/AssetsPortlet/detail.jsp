<%--
  Asset Detail Page

  Session Variables:
    assetURI

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %> 

<!-- Begin AssetPortlet's detail.jsp -->

<fmt:setBundle var="assetsBundle" 
  basename="atg.epub.portlet.AssetsPortlet.Resources"/>

<dspel:page xml="true">
  <dspel:importbean bean="/atg/web/ATGSession" var="atgSession"/>
  <c:set var="assetView" value="${atgSession.assetView}" scope="request"/>
  <c:set var="assetURI"  value="${atgSession.assetURI}" scope="request"/>

  <c:set var="contextFormHandlerPath" 
    value="/atg/epub/servlet/ContextFormHandler"/>
  <dspel:importbean var="contextFormHandler" 
    bean="${contextFormHandler}"/>

  <c:set var="assetInfoPath" scope="request"
    value="/atg/epub/servlet/BrowseAssetInfo"/>
  <dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>

  <%--
    Set to true to see debugging output on page and in console
  --%>
  <c:set var="debug" value="false" scope="request"/>

  <c:set target="${assetInfo}" property="loggingDebug" value="${debug}"/>

  <script language="JavaScript">

    function getContextForm() { return document.getElementById("contextForm"); }

    function popContext() {
      var form = getContextForm();
      form.elements[ "<c:out value="${contextFormHandlerPath}.contextOp"/>" ].value = 
        <c:out value="${assetInfo.CONTEXT_POP}"/>;
      form.submit();
    }

    function pushContext( assetURI ) {
      var form = getContextForm();
      form.elements[ "<c:out value="${contextFormHandlerPath}.assetURI"/>" ].value = 
        assetURI;
      form.elements[ "<c:out value="${contextFormHandlerPath}.contextOp"/>" ].value = 
        <c:out value="${assetInfo.CONTEXT_PUSH}"/>;
      form.submit();
    }

    function actionNoteWindow(url) {
      var subwindow=window.open(url,"","height=300,width=300")
    }

  </script>

  <%@ page import="atg.epub.portlet.asset.AssetPortlet" %>

  <dspel:importbean bean="/atg/epub/servlet/AssetSearchFormHandler"
    var="assetSearchHandler"/>

  <jsp:useBean id="assetPortlet" class="atg.epub.portlet.asset.AssetPortlet"/>

  <%-- Get the repository item or virtual file --%>
  <c:if test="${ ! empty assetURI }">
    <pws:getAsset uri="${assetURI}" var="assetVersion">
      <c:set value="${assetVersion.asset.mainVersion}" 
        var="mainVersion" scope="page"/>
      <c:choose>
        <c:when test="${ ! empty mainVersion.virtualFile }">
          <c:set value="${mainVersion.virtualFile}"
            var="item" scope="page"/>
          <c:if test="${debug}">
            virtual file: <c:out value="${mainVersion.virtualFile}"/>
            <br />
            id : <c:out value="${mainVersion.virtualFile}"/>
            <br />
          </c:if>
        </c:when>
        <c:otherwise>
          <c:set value="${mainVersion.repositoryItem}"
            var="item" scope="page"/>
          <c:set var="isRepositoryItem" value="true"/>
          <c:if test="${debug}">
            repository item: <c:out value="${item}"/>
            <br />
          </c:if>
        </c:otherwise>
      </c:choose>
    </pws:getAsset>
  </c:if>

  <!-- get the mapping for the asset -->
  <biz:getItemMapping item="${item}" var="imap" mode="view"
    showExpert="${expert}"/>

  <c:set value="${imap.itemName}" var="assetTypeName"
    scope="page"/>

  <c:if test="${debug}">
    item type: <c:out value="${assetTypeName}"/>
    <br />
  </c:if>

  <div id="intro">
    <h2>
      <span class="pathSub">
          <fmt:message key="browse" bundle="${assetsBundle}"/>
      </span>

      <%-- iterate asset types to display currently viewed type --%>
      <pws:getVersionedAssetTypes var="assetTypes">
        <c:forEach items="${assetTypes}" var="result">
          <c:forEach items="${result.types}" var="type">
            <c:if test="${assetTypeName==type.typeName }">
              <c:set var="itemTypeDisplayName" value="${type.displayName}"/>
            </c:if>
          </c:forEach>
        </c:forEach>
      </pws:getVersionedAssetTypes>

      <c:out value="${itemTypeDisplayName}"/>

    </h2>

    <p class="path">

      <!-- Link to external web-app, hardcoded apViews ... -->
      <a href='/AssetBrowse/assetBrowse.jsp?apView=2'>&laquo;&nbsp; <fmt:message key="back-to-browse-link" bundle="${assetsBundle}"/></a>

    </p>
    <p>
      <fmt:message key="detail-helper-text" bundle="${assetsBundle}"/>
    </p>
  </div> <!-- end intro -->

  <!-- begin content -->
  <table cellpadding="0" border="0" cellspacing="0">
    <tr>

      <c:if test="${assetInfo.contextLevel > 1}">
        <td class="backToPage">
          <a href="javascript:popContext();" onmouseover="status='';return true;">&laquo; back to parent Asset</a>
        </td>
      </c:if>

      <%-- If we're showing asset diffs, insert a link to return to Asset History --%>
      <c:choose>
        <c:when test="${ assetView == assetPortlet.ASSET_DIFF }">
          <portlet:renderURL var="assetHistoryURL">
            <portlet:param name="assetView" value='<%= ""+AssetPortlet.ASSET_HISTORY %>'/>
          </portlet:renderURL>
          <td class="backToPage">&nbsp;
            <a href='<c:out value="${assetHistoryURL}"/>' onmouseover="status='';return true;">
              &laquo;&nbsp;
              <fmt:message key="back-to-asset-history-link" bundle="${assetsBundle}"/>
            </a>&nbsp;
          </td>
        </c:when>
      </c:choose>

      <td class="contentTab contentTabAsset" style="width:0">
        <c:choose>
          <c:when test="${ assetView != assetPortlet.ASSET_PROPERTIES }">
            <portlet:renderURL var="viewURL">
              <portlet:param name="assetView"
                value='<%= ""+AssetPortlet.ASSET_PROPERTIES %>'/>
            </portlet:renderURL>
            <a href='<c:out value="${viewURL}"/>' class="tabOff" onmouseover="status='';return true;">
              <fmt:message key="asset-properties" bundle="${assetsBundle}"/>
            </a>
          </c:when>
          <c:otherwise>
            <span class="tabOn">
              <fmt:message key="asset-properties" bundle="${assetsBundle}"/>
            </span>
          </c:otherwise>
        </c:choose>
      </td>
      <td class="contentTab contentTabAsset" style="width:0">
        <c:choose>
          <c:when test="${ assetView != assetPortlet.ASSET_HISTORY }">
            <portlet:renderURL var="viewURL">
              <portlet:param name="assetView" value='<%= ""+AssetPortlet.ASSET_HISTORY %>'/>
            </portlet:renderURL>
            <a href='<c:out value="${viewURL}"/>' class="tabOff" onmouseover="status='';return true;">
              <fmt:message key="asset-history" bundle="${assetsBundle}"/>
            </a>
          </c:when>
          <c:otherwise>
            <span class="tabOn">
              <fmt:message key="asset-history" bundle="${assetsBundle}"/>
            </span>
          </c:otherwise>
        </c:choose>
      </td>
      <td class="contentTabShadow" width="100%">&nbsp;</td>
    </tr>
  </table>

  <table cellpadding="0" cellspacing="0" border="0" width="100%" 
    id="attributeBarAsset">
    <tr>
      <td>
        <div class="attributes">
          <p>
            <fmt:message key="asset-name" bundle="${assetsBundle}"/>
            <em>
              <c:choose>
                <c:when test="${isRepositoryItem == 'true'}">
                  <c:out value="${item.itemDisplayName}"/>
                </c:when>
                <c:otherwise>
                  <c:out value="${item.canonicalPath}"/>
                </c:otherwise>
              </c:choose>
            </em>
          </p>
          <p>
            <fmt:message key="asset-type" bundle="${assetsBundle}"/>
            <em>
              <c:out value="${assetTypeName}"/>
            </em>
          </p>
          <p>
            <fmt:message key="version" bundle="${assetsBundle}"/>
            <em>
              <c:out value="${mainVersion.version}"/>
            </em>
          </p>
        </div>
      </td>
    </tr>
  </table>

  <c:choose>
    <c:when test="${assetView == assetPortlet.ASSET_PROPERTIES}">
      <dspel:include page="assetProperties.jsp"/>
    </c:when>
    <c:when test="${assetView == assetPortlet.ASSET_HISTORY}">
      <dspel:include page="assetHistory.jsp"/>
    </c:when>
    <c:when test="${assetView == assetPortlet.ASSET_DIFF}">
      <dspel:include page="assetDiff.jsp"/>
    </c:when>
  </c:choose>

  <portlet:actionURL var="contextActionURL"/>

  <dspel:form id="contextForm" formid="contextForm" method="post"
    action="${contextActionURL}">
    <dspel:input type="hidden" value="${debug}" priority="100"
      bean="${contextFormHandlerPath}.loggingDebug"/>
    <dspel:input type="hidden" value="${assetInfoPath}" priority="100"
      bean="${contextFormHandlerPath}.assetInfoPath"/>
    <dspel:input type="hidden" value="${assetInfo.CONTEXT_POP}" 
      priority="-1" bean="${contextFormHandlerPath}.contextOp"/>
    <dspel:input type="hidden" value="1"
      bean="${contextFormHandlerPath}.assetURI"/>
    <dspel:input type="hidden" priority="-10" value="1"
      bean="${contextFormHandlerPath}.contextAction"/>
  </dspel:form>

</dspel:page>

<!-- End AssetPortlet's detail.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/AssetsPortlet/detail.jsp#2 $$Change: 651448 $--%>
