<%--
  selectionManagment.jsp
  Fragment for managing selection state in list views.

  Params: 
      sessionInfoName     the nucleus path to the SessionInfo component

--%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <%-- unpack the param(s) --%>
  <dspel:getvalueof var="sessionInfoName" param="sessionInfoName"/>
  <dspel:getvalueof var="assetEditorURL" param="assetEditorURL"/>

  <%-- get the assetEditor which tells us the currently selected item --%>
  <dspel:importbean var="sessionInfo" bean="${sessionInfoName}"/>
  <dspel:importbean var="assetEditor" bean="${sessionInfo.assetEditorViewPath}"/>

  <script type="text/javascript">

    // this is the version manager uri of the currently selected asset  
    <c:choose>
      <c:when test="${assetEditor.assetContext.assetURI ne null}">
        atg_selectionManagement_assetContextURI = "<c:out value='${assetEditor.assetContext.assetURI}'/>";
      </c:when>
      <c:otherwise>
        atg_selectionManagement_assetContextURI = null;
      </c:otherwise>
    </c:choose>

    // Indicate if the Multi-Edit tab is currently visible.
    atg_selectionManagement_isMultiEdit = <c:out value="${sessionInfo.assetEditorViewID eq 'multiEdit'}"/>;

    // Set the version manager uri of the currently selected asset.
    atg_selectionManagement_assetEditorURL = "<c:out value='${assetEditorURL}'/>";

  </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/selectionManagement.jsp#2 $$Change: 651448 $--%>
