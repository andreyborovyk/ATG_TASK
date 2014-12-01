<%--
  Browse tab left pane tree for asset manager UI.

  params:
      projectEditable whether the current project exists and is editable
      dataEditable    whether the data on this page is editable or not


  This page renders a view selector and includes the appropriate page with the appropriate
  tree component.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/browse/tree.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"            %>


<dspel:page>
  <c:set var="debug" value="false"/>

  <%-- unpack the param(s) --%>
  <dspel:getvalueof var="dataEditable" param="dataEditable"/>
  <dspel:getvalueof var="projectEditable" param="projectEditable"/>
  <dspel:getvalueof var="assetURI" param="assetURI"/>
  <dspel:getvalueof var="siteId" param="siteId"/>
 
  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>
  <c:set var="viewConfig" value="${requestScope.tabConfig.views[sessionInfo.browseView]}"/>
  <c:set var="formHandlerPath" value="${viewConfig.configuration.formHandlerPath}"/>
  <dspel:importbean var="formHandler"
                    bean="${formHandlerPath}"/>

  <dspel:importbean var="preferences"
                    bean="/atg/web/assetmanager/Preferences"/>

  <fmt:setBundle basename="${requestScope.tabConfig.resourceBundle}"/>


  <c:set var="treeState" value="${sessionInfo.treeState}"/>
  <c:set var="treePath" value="${sessionInfo.treeStatePath}"/>
  <c:set var="treeDef" value="${treeState.globalTree.treeDefinition}"/>

  <c:if test="${not empty assetURI}">
  	<asset-ui:openTreeTag assetURI="${assetURI}" siteId="${siteId}" treeDefinition="${treeDef}" treeState="${treeState}" />
  </c:if>
  <c:url var="assetEditorURL" value="${requestScope.tabConfig.editorConfiguration.page}"/>

  <script type="text/javascript"
    src="<c:out value='${config.contextRoot}'/>/browse/tree.js">
  </script>

  <c:url var="uncheckAllURL" context="/WebUI" value="/tree/treeActionFrame.jsp">
    <c:param name="treeComponent" value="${treePath}"/>
    <c:param name="action"        value="uncheckAll"/>
  </c:url>

  <%-- Special case: some trees specify a node that is to be used as
       the parent of all nodes which are displayed without a parent. --%>
  <c:set var="rootNodeId" value="${null}"/>
  <c:if test="${treeDef.useRootNodeAsDefaultParent && !treeDef.hideRootNodes}">
    <web-ui:getTreeChildren var="rootNodes"
                            tree="${treePath}"
                            nodeId="${null}"/>
    <c:if test="${not empty rootNodes}">
      <c:set var="rootNodeId" value="${rootNodes[0].path}"/>
    </c:if>
  </c:if>

  <c:url var="treeURL" context="/AssetManager" value="/components/amTreeFrame.jsp">
  <%-- <c:url var="treeURL" context="/WebUI" value="/tree/treeFrame.jsp"> --%>
    <c:param name="headIncludeContextRoot" value="${config.contextRoot}"/>
    <c:param name="headIncludePage"        value="/components/head.jsp"/>
    <c:param name="treeComponent"          value="${treePath}"/>
    <c:param name="onUnselect"             value="nodeUnselect"/>
    <c:param name="onSelect"               value="nodeSelected"/>
    <c:param name="onSelectProperties"     value="URI,path"/>
    <c:param name="onLoad"             value="treeLoaded"/>
    <%-- Note: The icon registry now gives the full path to the icon including 
         the context root, so now we can pass a blank nodeIconRoot. --%>   
    <c:param name="nodeIconRoot"         value=""/>
    <c:param name="showToolTips"         value="${preferences.showIdToolTips}"/>
    <c:if test="${dataEditable}">
      <c:param name="onCheck"            value="checkNode"/>
      <c:param name="onUncheck"          value="uncheckNode"/>
      <c:param name="onCheckProperties"  value="childTypes,path"/>
      <c:param name="selectorControl"    value="checkbox"/>
    </c:if>
    <c:if test="${not empty rootNodeId}">
      <c:param name="rootNodeId"         value="${rootNodeId}"/>
    </c:if>
  </c:url>

  <script type="text/javascript" > 
    initializeTree({
      treeURL:                "<c:out value='${treeURL}' escapeXml='false'/>",
      numChecks:              "<c:out value='${treeState.numberOfCheckedNodes}'/>",
      assetEditorURL:         "<c:out value='${assetEditorURL}' escapeXml='false'/>",
      uncheckAllURL:          "<c:out value='${uncheckAllURL}' escapeXml='false'/>"
    });

  </script>


<c:catch var="ex">

  <%@ include file="/components/formStatus.jspf" %>
  <%@ include file="assetListHeader.jspf" %>

          <c:url var="actionURL" value="${requestScope.managerConfig.page}"/>
          <dspel:form name="listActionForm" action="${actionURL}" method="post">

            <dspel:input id="pickedAssetURI"
                         type="hidden"
                         bean="${formHandlerPath}.destinationAssetURI"
                         value=""/>
            <dspel:input id="pickedAssetURIs"
                         type="hidden"
                         bean="${formHandlerPath}.destinationAssetURIs"
                         value=""/>


            <%-- set up what operations are allowed --%>
            <c:if test="${dataEditable}">
              <%-- NB: duplicate in a tree is a dupAndMove operation --%>
              <dspel:contains var="allowDuplicateAndMove" object="duplicate" values="${viewConfig.operations}"/>
              <dspel:contains var="allowDelete" object="delete" values="${viewConfig.operations}"/>
              <dspel:contains var="allowMove" object="move" values="${viewConfig.operations}"/>
              <dspel:contains var="allowLink" object="link" values="${viewConfig.operations}"/>
              <dspel:contains var="allowUnlink" object="unlink" values="${viewConfig.operations}"/>
              <dspel:contains var="allowAddToMultiEdit" object="addToMultiEdit" values="${viewConfig.operations}"/>
              <dspel:contains var="allowCreate" object="create" values="${viewConfig.operations}"/>
              <dspel:contains var="allowExport" object="export" values="${viewConfig.operations}"/>
              <c:set var="allowUncheckAll" value="${true}"/>
              <c:if test="${projectEditable}">
                <dspel:contains var="allowAddToProject" object="addToProject" values="${viewConfig.operations}"/>
              </c:if>
            </c:if>

            <%-- render the button toolbar --%>
            <dspel:include page="/components/toolbar.jsp">
              <dspel:param name="treeComponent"             value="${treePath}"/>
              <dspel:param name="formHandlerPath"           value="${formHandlerPath}"/>
              <dspel:param name="allowDuplicate"            value="false"/>
              <dspel:param name="allowDuplicateAndMove"     value="${allowDuplicateAndMove}"/>
              <dspel:param name="allowDelete"               value="${allowDelete}"/>
              <dspel:param name="allowAddToProject"         value="${allowAddToProject}"/>
              <dspel:param name="allowRemoveFromProject"    value="${false}"/>
              <dspel:param name="allowAddToMultiEdit"       value="${allowAddToMultiEdit}"/>
              <dspel:param name="allowRemoveFromMultiEdit"  value="false"/>
              <dspel:param name="allowLink"                 value="${allowLink}"/>
              <dspel:param name="allowMove"                 value="${allowMove}"/>
              <dspel:param name="allowUnlink"               value="${allowUnlink}"/>
              <dspel:param name="allowCheckAll"             value="false"/>
              <dspel:param name="allowUncheckAll"           value="${allowUncheckAll}"/>
              <dspel:param name="allowCreate"               value="${allowCreate}"/>
              <dspel:param name="allowExport"               value="${allowExport}"/>
            </dspel:include>

            <%-- include any topFragment that was configured for this view --%>
            <c:set var="assetBrowser" value="${sessionInfo.currentAssetBrowser}"/>
            <c:if test="${not empty assetBrowser.topFragmentPage}">
              <dspel:include otherContext="${assetBrowser.topFragmentContextRoot}" page="${assetBrowser.topFragmentPage}">
              </dspel:include>

              <%-- Since the top fragment occupies extra vertical space, this
                   view must use a unique key for the resize handler --%>
              <c:set var="heightKeySuffix" value="${assetBrowser.topFragmentContextRoot}${assetBrowser.topFragmentPage}"/>
            </c:if>

            <c:if test="${not testbuttons}">

              <c:set scope="request" var="resizableStyle" value="border: none;"/>

              <dspel:include page="/components/resizeHandler.jsp">
                <dspel:param name="resizableElementId" value="scrollContainer"/>
                <dspel:param name="heightKey"          value="browseTreeScrollContainer${heightKeySuffix}"/>
              </dspel:include>

              <%-- Display the tree inside a scrolling subwindow --%>
              <dspel:iframe id="scrollContainer" name="scrollContainer" frameborder="0" src="${treeURL}" style="${requestScope.resizableStyle}"/>
             
            </c:if>

          </dspel:form>

</c:catch>

<%
  Throwable tt = (Throwable) pageContext.getAttribute("ex");
  if ( tt != null ) {
    System.out.println("Caught exception in browse/tree.jsp:");
    tt.printStackTrace();
  }
%>

     <c:if test="${ex ne null}">
            <script type="text/javascript" >
              messages.addError('<c:out value="${ex.message}"/>');
            </script>
      </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/browse/tree.jsp#2 $$Change: 651448 $ --%>
