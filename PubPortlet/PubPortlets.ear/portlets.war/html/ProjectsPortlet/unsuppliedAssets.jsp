<%@ page import="javax.portlet.*" %>
<%@ page import="atg.portal.framework.*" %>
<%@ page import="atg.portal.servlet.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<!-- Begin ProjectsPortlet's unsuppliedAssets.jsp -->
<dspel:page>
<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

<fmt:message var="addAssetButton" key="add-asset-button" bundle="${projectsBundle}"/>
<fmt:message var="discardAssetButton" key="discard-asset-button" bundle="${projectsBundle}"/>

<portlet:defineObjects/>

<portlet:actionURL  var="actionURL"/>

<%@ include file="projectConstants.jspf" %>

<%-- GET THE CURRENT PROJECT & ID --%>
<pws:getCurrentProject var="projectContext"/>
<c:set var="project" value="${projectContext.project}"/>

<c:if test="${project ne null}">

  <%-- begin asset picker stuff --%>

  <%-- 
    The AssetInfo component for these pages
  --%>
  <c:set var="assetInfoPath" scope="request"
    value="/atg/epub/servlet/ProcessAssetInfo"/>
  <dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>

  <%--
    Clear the context stack
  --%>
  <c:if test="${ 'true' == param.clearContext }">
    <c:set target="${assetInfo}" property="clearContext" value="true"/>
    <c:set target="${assetInfo}" property="clearAttributes" value="true"/>
  </c:if>

  <%-- the URL to use to return to this task --%>
  <portlet:actionURL var="taskURL">
    <portlet:param name="copyParams" value="true"/>
    <portlet:param name="projectView" 
      value='<%=(String)pageContext.getAttribute("UNSUPPLIED_ASSETS_VIEW")%>'/>
    <portlet:param name="taskId" 
      value='<%=(String)pageContext.getAttribute("taskId")%>'/>
  </portlet:actionURL>

  <%--
    The text, context action (backToURL) and context URL to use to 
    return to this page from the top-level created asset
  --%>
  <c:set target="${assetInfo.attributes}" property="backToText"
    value="Back to required assets list"/>
  <c:set target="${assetInfo.attributes}" property="backToURL"
    value="javascript:popContext();"/>
  <c:set target="${assetInfo.attributes}" property="firstAssetEditContextLevel"
    value="2"/>
  <c:set target="${assetInfo.attributes}" property="contextActionURL"
    value="${taskURL}"/>

  <c:set var="projectHandlerPath" value="/atg/epub/servlet/ProjectFormHandler"/>
  <dspel:importbean bean="${projectHandlerPath}" var="projectFormHandler"/>

  <%-- 
    Project Form
  --%>  
  <dspel:form formid="projectForm" id="projectForm" method="post" action="${actionURL}">
    <dspel:input type="hidden" value="false" priority="100"
      bean="${projectHandlerPath}.loggingDebug"/>
    <dspel:input type="hidden" value="" priority="100" id="placeholderName"
      bean="${projectHandlerPath}.placeholderName"/>
    <dspel:input type="hidden" value="null" id="asset" priority="100"
      bean="${projectHandlerPath}.asset"/>
    <dspel:input type="hidden" value="${project.id}" priority="100"
      bean="${projectHandlerPath}.projectId"/>
    <dspel:input type="hidden" value="${projectFormHandler.ADD_ASSET_ACTION}" priority="-1"
      bean="${projectHandlerPath}.assetAction"/>
    <dspel:input type="hidden" value="1" priority="-10"
      bean="${projectHandlerPath}.performAssetAction"/>
  </dspel:form>

  <script language="JavaScript">

    // Not using form saving on this page
    function fsOnSubmit() {
      return false;
    }

    function getAssetBrowserConfig() {
      var options = new Object();
      options.contextOp = '<c:out value="${assetInfo.CONTEXT_PUSH}"/>';
      return options;
    }

    function setPlaceholder( selected, callerData ) {
      var form = document.getElementById( "projectForm" );
      form.elements[ '<c:out value="${projectHandlerPath}.asset"/>' ].value = 
        selected.uri;
      form.elements[ '<c:out value="${projectHandlerPath}.placeholderName"/>' ].value = 
        callerData;
      form.submit();
    }

    function pickPlaceholderAsset( placeholderName, encodedAssetType ) {
      var assetTypesList = new Array(0);
      
      var decodedItemType = encodedAssetType.split(":");
      
      var assetType = new Object();
       assetType.typeCode            = "repository";
       assetType.displayName         = decodedItemType[1];
       assetType.typeName            = decodedItemType[1];
       assetType.repositoryName      = decodedItemType[0];
       assetType.componentPath       = decodedItemType[0];
       assetType.createable          = "true";
      
      assetTypesList[ assetTypesList.length ] = assetType;
    
      var picker = new AssetBrowser( '<c:out value="${assetInfoPath}"/>' );
       picker.browserMode        = "pick";
       picker.createMode         = "nested";
       picker.isAllowMultiSelect = "false";
       picker.onSelect           = "setPlaceholder";
       picker.closeAction        = "hide";
       picker.callerData         = placeholderName;
       picker.assetTypes         = assetTypesList;
       picker.defaultView        = "Search";
       picker.invoke();
    }

    <c:if test="${ ! empty assetInfo.context.attributes.newAssetAssetURI }">

      function updatePickerValue() {
        var selected = new Object();
        selected.id = '<c:out value="${assetInfo.context.attributes.newAssetID}"/>';
        selected.displayName = '<c:out value="${assetInfo.context.attributes.newAssetDisplayName}"/>';
        selected.uri = '<c:out value="${assetInfo.context.attributes.newAssetAssetURI}"/>';
        var data = '<c:out value="${assetInfo.context.attributes.assetPickerCallerData}"/>'
        setPlaceholder( selected, data );
      }

      registerOnLoad( updatePickerValue );

      <c:set target="${assetInfo.context.attributes}" property="onSelect" value=""/>
      <c:set target="${assetInfo.context.attributes}" property="newAssetID" value=""/>
      <c:set target="${assetInfo.context.attributes}" property="newAssetAssetURI" value=""/>
      <c:set target="${assetInfo.context.attributes}" property="newAssetDisplayName" value=""/>
    </c:if>

  </script>


  <%-- end asset picker stuff --%>

  <c:set var="currentProjectId" value="${project.id}"/>

  <c:url var="assetBrowserURL" value="/html/ProjectsPortlet/assetBrowser.jsp">
    <c:param name="projectView" value="${ASSET_EDIT_VIEW}"/>
  </c:url>

  <%--
    Create a URL for the asset browser to use to redirect to
    assetEditorPage.jsp when an asset is created.
   --%>

  <portlet:renderURL var="assetBrowserCreateURL">
    <portlet:param name="projectView" value='<%=(String)pageContext.getAttribute("NEW_ASSET_VIEW")%>'/>
  </portlet:renderURL>

  <c:set value="${assetBrowserCreateURL}" scope="session"
    var="assetBrowserCreateURL"/>

  <c:set value="true" scope="session"
    var="assetBrowserAllowCreate"/>
  <c:set value="${currentProjectId}" scope="session"
    var="assetBrowserProjectId"/>
  <c:set value="single" scope="session"
    var="assetBrowserSelectMode"/>

  <div class="contentActions">
    <table cellpadding="0" cellspacing="0" border="0">
      <tr>
        <td class="blankSpace">&nbsp;</td>
        <td><h2><fmt:message key="unsupplied-assets-title" bundle="${projectsBundle}"/></h2></td>
        <td width="100%">&nbsp;</td>
      </tr>
    </table>
  </div>

  <pws:getUnsuppliedAssets var="uaResults"  processId="${projectContext.process.id}"/>

  <c:if test="${uaResults.allUnsuppliedAssetCount gt 0}">

    <%-- ASSET COLUMN HEADERS --%>
    <table cellpadding="0" cellspacing="0" border="0" class="dataTable">
    		
      <tr>
        <th class="leftAligned" style="width: 100%;">
          <span class="tableHeader">
            <fmt:message key="placeholder-name-header" bundle="${projectsBundle}"/>&nbsp;&nbsp;
          </span>
        </th>

        <th class="centerAligned">
          <span class="tableHeader">
            <a href="#" onmouseover="status='';return true;">
              <fmt:message key="placeholder-type-header" bundle="${projectsBundle}"/>
            </a>
          </span>
        </th>
      </tr>

      <c:forEach var="placeholderName" items="${uaResults.allUnsuppliedAssetNames}">
        <tr>
          <td>
            <a href="javascript:pickPlaceholderAsset( '<c:out value="${placeholderName}"/>',
              '<c:out value="${uaResults.encodedTypes[placeholderName]}"/>')" 
              class="add" onmouseover="status='';return true;">
              &nbsp;<c:out value="${placeholderName}"/>
            </a>
          </td>
          <td>&nbsp;</td>
        </tr>
      </c:forEach>
    </table>

  </c:if>

</c:if>  <%-- project is not null --%>

<c:if test="${projectContext.project eq null}">
  <fmt:message key="no-current-project-message" bundle="${projectsBundle}"/>
</c:if>
</dspel:page>
<!-- End ProjectsPortlet's unsuppliedAssets.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/unsuppliedAssets.jsp#2 $$Change: 651448 $--%>
