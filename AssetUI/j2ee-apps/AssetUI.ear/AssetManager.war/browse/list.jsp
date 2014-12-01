<%--
  Browse tab left pane asset list view for asset manager UI.

  params:
      editable    whether this page is editable or not

  This page renders a view selector and includes the appropriate page with the appropriate
  list component.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/browse/list.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="pws"    uri="http://www.atg.com/taglibs/pws"                   %>

<dspel:page>
  <c:set var="debug" value="false"/>

  <%-- unpack the param(s) --%>
  <dspel:getvalueof var="dataEditable" param="dataEditable"/>
  <dspel:getvalueof var="projectEditable" param="projectEditable"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>
  <c:set var="sessionInfoName" value="${config.sessionInfoPath}"/>
  <c:set var="viewConfig" value="${requestScope.tabConfig.views[sessionInfo.browseView]}"/>

  <c:set var="formHandlerPath" value="${viewConfig.configuration.formHandlerPath}"/>
  <dspel:importbean var="formHandler" bean="${formHandlerPath}"/>

  <fmt:setBundle basename="${requestScope.tabConfig.resourceBundle}"/>

  <c:url var="assetEditorURL" value="${requestScope.tabConfig.editorConfiguration.page}"/>

  <%-- if the viewConfig is filterable, then enable filtering --%>
	<c:set var="enableFilter" value="false"/>
  <web-ui:isAssignableFrom var="enableFilter" className="${viewConfig.configuration.class.name}"
     instanceOfClassName="atg.web.assetmanager.configuration.FilterableBrowseListViewConfiguration"/>

  <%-- Get the current project --%>
  <pws:getCurrentProject var="projectContext"/>
  <c:set var="project" value="${projectContext.project}"/>

  <%-- Reset the asset caches on the current asset browser --%>
  <web-ui:invoke bean="${sessionInfo.currentAssetBrowser}" method="reset"/>

  <c:catch var="ex">

    <%@ include file="/components/formStatus.jspf" %>
    <%@ include file="assetListHeader.jspf" %>

    <c:url var="actionURL" value="${requestScope.managerConfig.page}"/>

    <dspel:form name="listActionForm" action="${actionURL}" method="post">

     <%-- set up what operations are allowed --%>
     <c:if test="${dataEditable}">
       <dspel:contains var="allowDuplicate" object="duplicate" values="${viewConfig.operations}"/>
       <dspel:contains var="allowDelete" object="delete" values="${viewConfig.operations}"/>
       <dspel:contains var="allowAddToMultiEdit" object="addToMultiEdit" values="${viewConfig.operations}"/>
       <dspel:contains var="allowCreate" object="create" values="${viewConfig.operations}"/>
       <dspel:contains var="allowExport" object="export" values="${viewConfig.operations}"/>
       <c:set var="createableTypes" value="${viewConfig.configuration.createableTypes}"/>
       <c:set var="allowCheckAll" value="${true}"/>
       <c:if test="${projectEditable}">
         <dspel:contains var="allowAddToProject" object="addToProject" values="${viewConfig.operations}"/>
       </c:if>
     </c:if>

     <%-- render the button toolbar --%>
     <dspel:include page="/components/toolbar.jsp">
       <dspel:param name="formHandlerPath"           value="${formHandlerPath}"/>
       <dspel:param name="allowDuplicate"            value="${allowDuplicate}"/>
       <dspel:param name="allowDelete"               value="${allowDelete}"/>
       <dspel:param name="allowDuplicateAndMove"     value="${false}"/>
       <dspel:param name="allowAddToProject"         value="${allowAddToProject}"/>
       <dspel:param name="allowRemoveFromProject"    value="${false}"/>
       <dspel:param name="allowAddToMultiEdit"       value="${allowAddToMultiEdit}"/>
       <dspel:param name="allowRemoveFromMultiEdit"  value="${false}"/>
       <dspel:param name="allowLink"                 value="${false}"/>
       <dspel:param name="allowMove"                 value="${false}"/>
       <dspel:param name="allowUnlink"               value="${false}"/>
       <dspel:param name="allowCheckAll"             value="${allowCheckAll}"/>
       <dspel:param name="allowCreate"               value="${allowCreate}"/>
       <dspel:param name="allowExport"               value="${allowExport}"/>
     </dspel:include>

     <%-- render the scrollable, pagable list of assets --%>
     <dspel:include page="/components/pageableList.jsp">
       <dspel:param name="shouldRenderCheckboxes"   value="${dataEditable}"/>
       <dspel:param name="scrollContainerHeightKey" value="browseListScrollContainer${enableFilter}"/>
       <dspel:param name="enableFilter"              value="${enableFilter}"/>
     </dspel:include>

    </dspel:form>  <%-- Discard Assets form --%>

  </c:catch>

<%
  Throwable tt = (Throwable) pageContext.getAttribute("ex");
  if ( tt != null ) {
    System.out.println("Caught exception in browse/list.jsp:");
    tt.printStackTrace();
  }
%>

     <c:if test="${ex ne null}">
            <script type="text/javascript" >
              messages.addError('<c:out value="${ex.message}"/>');
            </script>
      </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/browse/list.jsp#2 $$Change: 651448 $ --%>

