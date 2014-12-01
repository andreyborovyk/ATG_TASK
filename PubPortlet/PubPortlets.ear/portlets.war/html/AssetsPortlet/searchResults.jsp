<%--
  Asset Search Results

  @param   projectView        (currently not used)  
  @param   assetURI           the publishing asset's URI
  @param   workspaceName      the workspace's name

  @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/AssetsPortlet/searchResults.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<!-- Begin AssetPortlet's searchResults.jsp -->

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<fmt:setBundle var="assetsBundle" 
  basename="atg.epub.portlet.AssetsPortlet.Resources"/>

<c:set var="assetSearchHandlerPath" 
  value="/atg/epub/servlet/AssetSearchFormHandler"/>
<dspel:importbean bean="${assetSearchHandlerPath}"
  var="assetSearchHandler"/>

<script language="JavaScript" type="text/javascript" src="<c:url value='/html/scripts/search.jsp'><c:param name="searchHandlerPath" value="${assetSearchHandlerPath}"/></c:url>"></script>

<c:if test="${param.moreResults ne null}">
  <dspel:setvalue bean="${assetSearchHandlerPath}.moreResults" 
    value="${true}"/>
</c:if>
<c:if test="${param.resultsPageNum ne null}">
  <dspel:setvalue bean="${assetSearchHandlerPath}.currentResultPageNum" 
    value="${param.resultsPageNum}"/>
</c:if>

<c:set var="projectHandlerPath"
  value="/atg/epub/servlet/ProjectFormHandler"/>

<dspel:importbean bean="${projectHandlerPath}" 
  var="projectForm"/>

<c:set var="debug" value="false" scope="request"/>

<%@ page import="atg.epub.portlet.asset.AssetPortlet" %>

<script language="JavaScript">

  // URLs to bring user to various processes
  var projectURLs = new Object();

  registerOnLoad( function() { focusTo("keywordInput") } );

  function okToSubmit() {
    var ok = false;
    var form = document.getElementById( "projectForm" );
    var projectId = document.getElementById( "gotoProjectId" ).value;
    if ( projectId ) {
      for ( var ii=0; (! ok) && (ii < form.elements.length); ++ii ) {
        if ( "checkbox" == form.elements[ii].type ) {
          if ( '<c:out value="${projectHandlerPath}.assets"/>' == form.elements[ii].name ) {
            ok = form.elements[ii].checked;
          }                
        }
      }
    }

    return ok;
  }

  function submitProjectAction() {
    if ( ! okToSubmit() ) {
      alert( "<fmt:message key="no-process-or-asset-selected" bundle="${assetsBundle}"/>" );
    }
    else {
      var form = document.getElementById( "projectForm" );
      var projectId = document.getElementById( "gotoProjectId" ).value;
      var changeToProcess = document.getElementById("toProcess").checked;
      if ( changeToProcess ) {
        form.action = projectURLs[projectId].replace( /&amp;/g, "&" ) + 
          "&_DARGS=" + form.elements["_DARGS"].value;
        var pafPsPos = form.action.indexOf( "paf_ps" );
        if ( pafPsPos != -1 ) {
          form.action = form.action.substring( 0, pafPsPos-1 ) +
            form.action.substring( form.action.indexOf("&", pafPsPos+1) );
        }
      }
      form.submit();
    }
  }

  function submitSearch() {
    var form = document.getElementById( 'searchform' );
    form.elements["<c:out value="${assetSearchHandlerPath}.sortProperty"/>"].value =
      '<c:out value="${assetSearchHandler.sortProperty}"/>';
    form.elements["<c:out value="${assetSearchHandlerPath}.sortDirection"/>"].value =
      '<c:out value="${assetSearchHandler.sortDirection}"/>';
    form.elements["<c:out value="${assetSearchHandlerPath}.action"/>"].value =
      "<c:out value="${assetSearchHandler.ACTION_SEARCH}"/>";
    form.submit();
  }

  function submitSortedSearch( sortProperty ) {
    var form = document.getElementById( 'searchform' );
    form.elements["<c:out value="${assetSearchHandlerPath}.sortProperty"/>"].value =
      sortProperty;
    form.elements["<c:out value="${assetSearchHandlerPath}.sortDirection"/>"].value =
      '<c:out value="${assetSearchHandler.sortDirection}"/>';
    form.elements["<c:out value="${assetSearchHandlerPath}.action"/>"].value =
      "<c:out value="${assetSearchHandler.ACTION_SORT_ORDER}"/>";
    form.submit();
  }

  var repository     = new Array(0);

  var typeName       = new Array(0);
  var displayName    = new Array(0);
  var typeCode       = new Array(0);
  var repositoryName = new Array(0);
  var componentPath  = new Array(0);
        
  function newRepository( repo ) {
    repository[repository.length] = repo;
  }

  function populateArrays( pTypeName, 
    pDisplayName, 
    pTypeCode,
    pRepositoryName,
    pComponentPath ) {
         
    typeName[typeName.length]             = pTypeName;
    displayName[displayName.length]       = pDisplayName;
    typeCode[typeCode.length]             = pTypeCode;
    repositoryName[repositoryName.length] = pRepositoryName;
    componentPath[componentPath.length]   = pComponentPath;
  }
       
  function populateTypeMenu( repo, form, url ) {
    var j = 1;
    var item;
    var listArray = repo.split("|");
    var compPath = listArray[0];
                   
    document.searchform.assetTypePulldown.options.length = 0;

    for ( var i=0; i<typeName.length; i++ ) {
       
      if ( componentPath[i] == compPath ) {
        document.searchform.assetTypePulldown.options.length = j;
        document.searchform.assetTypePulldown.options[j-1] = new Option( displayName[i] );
        item = componentPath[i] + ":" + typeName[i];
        document.searchform.assetTypePulldown.options[j-1].value = item;
        if (item == document.searchform.repositoryItemType.value)
          document.searchform.assetTypePulldown.options[j-1].selected = true;
        j++;
      }
    }
          
    setSearchValue( document.searchform.assetTypePulldown.value, form, url );
  }
        
  function setSearchValue( val, form, url ) {
    if ( val != null || val != "") {
      document.searchform.searchInput.value = val;
      document.searchform.repositoryItemType.value = val;
      changeSearchType(form, url);
    }
  }
       
  function initializeAssetPulldown() {
    if ( document.searchform.repositoryPulldown.value != null ) {
      populateTypeMenu( document.searchform.repositoryPulldown.value );
    }
  }

</script>

<portlet:defineObjects/>
<portlet:actionURL  var="actionURL"/>

<%-- RESULTS DISPLAY --%>
<c:set var="resultSet" value="${assetSearchHandler.searchResults}"/>
<c:set var="startCount" value="${assetSearchHandler.startCount}"/>
<c:set var="endCount" value="${assetSearchHandler.endCount}"/>
<c:set var="currentPageNum" value="${assetSearchHandler.currentResultPageNum}"/>
<c:set var="resultPageCount" value="${assetSearchHandler.resultPageCount}"/>
<c:set var="resultSetSize" value="${assetSearchHandler.resultSetSize}"/>
<c:set var="resultSetSizeDisplay" value="${resultSetSize}"/>
<c:set var="moreResults" value="${false}"/>
<c:if test="${(assetSearchHandler.maxRowCount ne -1) && resultSetSize > assetSearchHandler.maxRowCount}">
  <fmt:message var="moreSymbol" key="more-results-symbol" bundle="${assetsBundle}"/>
  <c:set var="resultSetSizeDisplay" value="${(resultSetSize-1)}${moreSymbol}"/>
  <c:set var="moreResults" value="${true}"/>
  <%-- don't show last page (with partial list) if more results --%>
  <c:set var="resultPageCount" value="${resultPageCount-1}"/>
</c:if>

<div id="intro">
  <portlet:renderURL var="searchURL">
    <portlet:param name="assetView"
      value='<%= ""+AssetPortlet.SEARCH_INITIAL %>'/>
  </portlet:renderURL>
  <h2>
    <span class="pathSub">Browse :</span>
    <pws:getVersionedAssetTypes var="vats">
      <c:set var="currentItemType"
        value="${assetSearchHandler.encodedPathAndItemType}"/>
      <c:forEach items="${vats}" var="vat">
        <c:forEach items="${vat.types}" var="type">
          <c:set var="itemType" value="${vat.componentPath}:${type.typeName}"/>
          <c:if test="${ currentItemType == itemType }">
            <c:out value="${type.displayName}"/>
            <c:set value="${type.displayName}" var="assetTypeName"
              scope="request"/>
          </c:if>
        </c:forEach>
      </c:forEach>
    </pws:getVersionedAssetTypes>
  </h2>
  <p class="path">
    <a href='<c:out value="${searchURL}"/>' onmouseover="status='';return true;">&laquo;&nbsp;<fmt:message key="back-to-browse-link" bundle="${assetsBundle}"/></a>
  </p>

  <p>
    <fmt:message key="search-helper-text" bundle="${assetsBundle}"/>
  </p>
</div> <!-- intro -->

<%-- list search form handler errors --%>
<c:forEach items="${assetSearchHandler.formExceptions}" var="ex">
  <br />
  <b>
    <c:out value="${ex.message}"/>
  </b>
</c:forEach>

<%-- list search form handler errors --%>
<c:forEach items="${projectForm.formExceptions}" var="ex">
  <br />
  <b>
    <c:out value="${ex.message}"/>
  </b>
</c:forEach>

<!-- begin content -->
<table cellpadding="0" cellspacing="3" border="0">

  <tr>
    <td width="100%" valign="top">

      <!-- display results per page tools -->
      <div class="displayResults displayResultsTop">
        <portlet:renderURL var="refreshURL"/>
        <a href="<c:out value="${refreshURL}"/>" class="refreshButton" onmouseover="status='';return true;">
          Refresh results
        </a>
        <c:if test="${resultSetSize > assetSearchHandler.maxResultsPerPage}">
          <c:set var="pageNum" value="${0}"/>
          <fmt:message key="paging-info" bundle="${assetsBundle}">
            <fmt:param value="${startCount}"/>
            <fmt:param value="${endCount}"/>
            <fmt:param value="${resultSetSizeDisplay}"/>
          </fmt:message>&nbsp;&nbsp;
          <fmt:message key="pages-label" bundle="${assetsBundle}"/>
          <c:forEach varStatus="loopInfo" begin="0" end="${resultPageCount-1}">
            <c:set var="pageNum" value="${loopInfo.count}"/>
            <portlet:renderURL  var="pageURL">
              <portlet:param name="resultsPageNum" 
                value='<%=pageContext.getAttribute("pageNum").toString()%>'/>
            </portlet:renderURL>
            <c:choose>
              <c:when test="${currentPageNum == pageNum}">
                [<c:out value="${currentPageNum}"/>]
              </c:when>
              <c:otherwise>
                <a href='<c:out value="${pageURL}"/>' onmouseover="status='';return true;">
                  <c:out value="${pageNum}"/>
                </a>
                &nbsp;&nbsp;
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </c:if>
        <c:if test="${moreResults}">
          <c:set var="pageNum" value="${pageNum+1}"/>
          <portlet:renderURL  var="moreURL">
            <portlet:param name="resultsPageNum"
              value='<%=pageContext.getAttribute("pageNum").toString()%>'/>
            <portlet:param name="moreResults" value="true" />
          </portlet:renderURL>
          <a href='<c:out value="${moreURL}"/>' onmouseover="status='';return true;">
            <fmt:message key="more-results-link" bundle="${assetsBundle}"/>
          </a>
          &nbsp;
        </c:if>
        &nbsp;
      </div>
      <!-- end display results per page tools -->

      <table cellpadding="0" cellspacing="0" border="0" width="100%" 
        id="attributeBarAsset">

        <tr>
          <td>
            <div class="attributes">
              <p>
                <em><fmt:message key="you-searched-for" bundle="${assetsBundle}"/></em>
              </p>
              <p>
                <fmt:message key="asset-type" bundle="${assetsBundle}"/>&nbsp;
                <em>
                  <c:out value="${assetTypeName}"/>
                </em>
              </p>
              <p>
                <fmt:message key="containing-title" bundle="${assetsBundle}"/>&nbsp;
                <em>
                  <c:out value="${assetSearchHandler.keywordInput}"/>
                </em>
              </p>
              <p>
                <fmt:message key="items-found" bundle="${assetsBundle}"/>&nbsp;
                <em>
                  <c:out value="${assetSearchHandler.resultSetSize}"/>
                </em>
              </p>
            </div>
            <div class="attributeExtras">
              <br /><br />
            </div>
          </td>
        </tr>
      </table>

      <portlet:actionURL var="addAssetActionURL">
        <portlet:param name="assetView"
          value='<%= ""+AssetPortlet.SEARCH_RESULTS %>'/>
      </portlet:actionURL>

      <dspel:form formid="projectForm" id="projectForm" action="${addAssetActionURL}" method="post">

        <dspel:input type="hidden" value="${debug}" priority="100"
          bean="ProjectFormHandler.loggingDebug"/>

        <dspel:input type="hidden" value="1" priority="-10"
          bean="ProjectFormHandler.performAssetAction"/>

        <table cellpadding="0" cellspacing="0" border="0" class="dataTable">
          <tr>
            <th class="centerAligned" style="width: 4%;">
              <img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() +
                "/html/ProjectsPortlet/images/checkMark1.gif")%>'
                alt="select multiple column" width="7" height="8" border="0" />
            </th>
            <th class="leftAligned">
              <a href="javascript:submitSortedSearch('displayName')" onmouseover="status='';return true;">
                <fmt:message key="asset-name-header" bundle="${assetsBundle}"/>
              </a>
              &nbsp;&nbsp;
              <c:choose>
                <c:when test="${assetSearchHandler.sortDirection == '2'}">
                  <img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() +
                    "/html/ProjectsPortlet/images/sort_arrowAscending.gif")%>' 
                    alt="sorted by" width="7" height="5" border="0" />
                </c:when>
                <c:when test="${assetSearchHandler.sortDirection == '1'}">
                  <img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() +
                    "/html/ProjectsPortlet/images/sort_arrowDescending.gif")%>' 
                    alt="sorted by" width="7" height="5" border="0" />
                </c:when>
              </c:choose>                
            </th>
            <th class="centerAligned" style="width: 7%;">
              <span class="tableHeader">
                <fmt:message key="version-header" bundle="${assetsBundle}"/>
              </span>
            </th>
          </tr>

          <!-- results -->
          <c:choose>
            <c:when test="${ ! empty resultSet }">
              <c:forEach items="${resultSet}" var="result">
                <pws:createVersionManagerURI object="${result}" var="uri">
                  <tr>
                    <td class="centerAligned">
                      <dspel:input bean="ProjectFormHandler.assets"
                        type="checkbox" value="${uri}" iclass="checkbox"/>
                    </td>
                    <td class="leftAligned">
                      <portlet:renderURL var="assetDetailView">
                        <portlet:param name="assetView" 
                          value='<%= ""+AssetPortlet.ASSET_PROPERTIES %>'/>
                        <portlet:param name="assetURI" 
                          value='<%= (String) pageContext.getAttribute("uri") %>'/>
                        <portlet:param name="clearContext" value="true"/>
                      </portlet:renderURL>
                      <a href='<c:out value="${assetDetailView}"/>' iclass="tableInfo" onmouseover="status='';return true;">
                        <c:choose>
                          <c:when test="${ result.class.name == 'atg.vfs.repository.RepositoryVirtualFile' }">
                            <c:choose>
                              <c:when test="${'fileFolder' == assetSearchHandler.itemTypes[0]}">
                                <img src="/atg/images/icon_folder.gif" 
                                  alt="File Asset" width="16" height="16" border="0" class="assetIcon" />
                              </c:when>
                              <c:otherwise>
                                <img src="/atg/images/icon_fileAsset.gif" 
                                  alt="Folder Asset" width="16" height="16" border="0" class="assetIcon" />
                              </c:otherwise>
                            </c:choose>
                            <c:out value="${result.canonicalPath}"/>
                          </c:when>
                          <c:otherwise>
                            <img src="/atg/images/icon_fileAsset.gif" 
                              alt="Repository Asset" width="16" height="16" border="0" class="assetIcon" />
                            <c:out value="${result.itemDisplayName}"/>
                          </c:otherwise>
                        </c:choose>
                      </a>
                    </td>
                    <td class="rightAligned">
                      <pws:getAsset var="av" uri="${uri}">
                        <span class="tableInfo"><c:out value="${av.asset.mainVersion.version}"/></span>
                      </pws:getAsset>
                    </td>
                  </tr>
                </pws:createVersionManagerURI>
              </c:forEach>
            </c:when>
            <c:otherwise>
              <tr>
                <td colspan="7" class="centerAligned error">
                  <fmt:message key="no-assets-message" bundle="${assetsBundle}"/>
                </td>
              </tr>
            </c:otherwise>
          </c:choose>
        </table>

        <div class="contentActions">
          <table cellpadding="0" cellspacing="0" border="0">
            <tr>	
              <td class="paddingLeft" width="100%">
                <dspel:select iclass="medium" priority="-1" bean="ProjectFormHandler.assetAction">
                  <dspel:option label="Modify" value="${projectForm.ADD_ASSET_ACTION}">
                    <fmt:message key="modify-asset" bundle="${assetsBundle}"/>
                  </dspel:option>
                  <dspel:option label="Delete" value="${projectForm.DEL_ASSET_ACTION}">
                    <fmt:message key="delete-asset" bundle="${assetsBundle}"/>
                  </dspel:option>
                </dspel:select>

                <fmt:message key="selected-assets" bundle="${assetsBundle}"/>
                <pws:getProjects var="projects" sortProperties="displayName">
                  <dspel:select bean="ProjectFormHandler.projectId" id="gotoProjectId" priority="10">
                    <dspel:option value=""><fmt:message key="select-one" bundle="${assetsBundle}"/></dspel:option>
                    <c:forEach items="${projects.projects}" var="project">
                      <c:if test="${project.editable}">
                        <dspel:option value="${project.id}">
                          <c:out value="${project.displayName}"/> -
                          <dspel:tomap var="creator" value="${project.creator}"/>
                          - <c:out value="${creator.firstName} ${creator.lastName}"/>
                          (<fmt:formatDate value="${project.creationDate}" 
                            type="BOTH" dateStyle="SHORT" timeStyle="SHORT"/>)
                        </dspel:option>
                        <dspel:tomap var="projectWorkflow" value="${project.workflow}"/>
                        <dspel:include page="/includes/createProjectURL.jsp" flush="true">
                          <dspel:param name="outputAttributeName" value="projectURL"/>
                          <dspel:param name="inputProjectId" value="${project.id}"/>
			  <dspel:param name="workflowPath" value="${projectWorkflow.processName}"/>
                        </dspel:include>
                        <script language="JavaScript">
                          projectURLs[ '<c:out value="${project.id}"/>' ] = 
                            '<c:out value="${projectURL}" escapeXml="false"/>';
                        </script>
                      </c:if>
                    </c:forEach>
                  </dspel:select>
                </pws:getProjects>

                <input type="checkbox" class="checkbox" id="toProcess" priority="10"/>
                <label for="toProcess">
                  <fmt:message key="bring-to-process" bundle="${assetsBundle}"/>
                  &nbsp;&nbsp;
                </label>
                <!-- Submit -->
                <a href="javascript:submitProjectAction()" class="goButton" id="addToProces" onmouseover="status='';return true;">
                  <fmt:message key="go" bundle="${assetsBundle}"/>
                </a>
              </td>	
              <td class="blankSpace">
              </td>
            </tr>
          </table>
        </div> <!-- contentActions -->
      </dspel:form>
	
      <!-- display results per page tools -->
      <div class="displayResults displayResultsTop">
        <c:if test="${resultSetSize > assetSearchHandler.maxResultsPerPage}">
          <c:set var="pageNum" value="${0}"/>
          <fmt:message key="paging-info" bundle="${assetsBundle}">
            <fmt:param value="${startCount}"/>
            <fmt:param value="${endCount}"/>
            <fmt:param value="${resultSetSizeDisplay}"/>
          </fmt:message>&nbsp;&nbsp;
          <fmt:message key="pages-label" bundle="${assetsBundle}"/>
          <c:forEach varStatus="loopInfo" begin="0" end="${resultPageCount-1}">
            <c:set var="pageNum" value="${loopInfo.count}"/>
            <portlet:renderURL  var="pageURL">
              <portlet:param name="resultsPageNum" 
                value='<%=pageContext.getAttribute("pageNum").toString()%>'/>
            </portlet:renderURL>
            <c:choose>
              <c:when test="${currentPageNum == pageNum}">
                [<c:out value="${currentPageNum}"/>]
              </c:when>
              <c:otherwise>
                <a href='<c:out value="${pageURL}"/>' onmouseover="status='';return true;">
                  <c:out value="${pageNum}"/>
                </a>
                &nbsp;&nbsp;
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </c:if>
        <c:if test="${moreResults}">
          <c:set var="pageNum" value="${pageNum+1}"/>
          <portlet:renderURL  var="moreURL">
            <portlet:param name="resultsPageNum" 
              value='<%=pageContext.getAttribute("pageNum").toString()%>'/>
            <portlet:param name="moreResults" value="true" />
          </portlet:renderURL>
          <a href='<c:out value="${moreURL}"/>' onmouseover="status='';return true;">
            <fmt:message key="more-results-link" bundle="${assetsBundle}"/>
          </a>
          &nbsp;
        </c:if>
        &nbsp;
      </div>
      <!-- end display results per page tools -->

    </td>

    <td id="sideBar">
      <p>&nbsp;</p>	

      <portlet:actionURL var="searchActionURL">
        <portlet:param name="assetView"
          value='<%= ""+AssetPortlet.SEARCH_RESULTS %>'/>
      </portlet:actionURL>

      <%--
        Search Form
      --%>
      <dspel:form formid="searchform" name="searchform" id="searchform" 
        action="${searchActionURL}" method="post">
          
        <dspel:input type="hidden" value="${assetSearchHandler.sortProperty}"
          priority="10" bean="${assetSearchHandlerPath}.sortProperty" />
        <dspel:input type="hidden" value="${assetSearchHandler.sortDirection}" 
          priority="10" bean="${assetSearchHandlerPath}.sortDirection"/>
        <dspel:input type="hidden" value="true" priority="10"
          bean="${assetSearchHandlerPath}.allowRefine"/>
        <dspel:input type="hidden" 
	  bean="${assetSearchHandlerPath}.itemType"
	  id="repositoryItemType"
	  name="repositoryItemType" />
	<dspel:input type="hidden" 
	  bean="${assetSearchHandlerPath}.encodedPathAndItemType"
	  id="searchInput"
	  name="searchInput" />
	      
        <div>
          <table cellpadding="0" cellspacing="0" border="0">

            <tr>
              <td class="title"><fmt:message key="search-options" bundle="${assetsBundle}"/></td>
            </tr>

            <tr>
              <td class="" 
                style="text-align: left; border-bottom: dotted 1px #A4A4A4; padding: 4px;">
                <dspel:input type="radio" value="true" iclass="formElementRadio"
                  priority="10" bean="${assetSearchHandlerPath}.clearQueries"/>
                <fmt:message key="new-search" bundle="${assetsBundle}"/>
                <br />
                <dspel:input type="radio" value="false" iclass="formElementRadio"
                  priority="10" bean="${assetSearchHandlerPath}.clearQueries"/>
                <fmt:message key="search-within-results" bundle="${assetsBundle}"/>
              </td>
            </tr>

            <tr>
              <td class="formLabel">
                <fmt:message key="show-asset-types-from-repository" bundle="${assetsBundle}"/>
              </td>
            </tr>
            
            <tr>
              <td>
                <%-- Repository dropdown selection --%>
            <pws:getVersionedAssetTypes var="results">
              <dspel:select iclass="formElement"
                id="repositoryPulldown"
                name="repositoryPulldown"
                bean="${assetSearchHandlerPath}.componentPath"
                onchange="populateTypeMenu(document.searchform.repositoryPulldown[ document.searchform.repositoryPulldown.options.selectedIndex ].value, 'searchform', '${searchActionURL}');">

                <c:forEach items="${results}" var="result">
	          <script language="JavaScript">
                    newRepository( '<c:out value="${result.componentPath}"/>' );
                  </script>

                  <c:forEach items="${result.types}" var="type">
                    <c:choose>
                      <c:when test="${result.repository}">
                        <c:set var="typeCode" value="repository"/>
                      </c:when>
                      <c:when test="${result.fileSystem}">
                        <c:set var="typeCode" value="file"/>
                      </c:when>
                    </c:choose>
	            <script language="JavaScript">
                      populateArrays( '<c:out value="${type.typeName}"/>',
                        '<c:out value="${type.displayName}"/>',
                        '<c:out value="${typeCode}"/>',
                        '<c:out value="${result.repositoryName}"/>',
                        '<c:out value="${result.componentPath}"/>' );
                    </script>
                  </c:forEach>
                  <dspel:option value="${result.componentPath}|${typeCode}">
                    <c:out value="${result.repositoryName}"/>
                  </dspel:option>

                </c:forEach>
              </dspel:select>
            </pws:getVersionedAssetTypes>
              </td>
            </tr>

            <tr>
              <td class="formLabel">
                <fmt:message key="show-assets-of-type" bundle="${assetsBundle}"/>
              </td>
            </tr>
            
            <tr>
              <td>
                <%-- Asset type dropdown selection --%>
            <select class="formElement"
              id="assetTypePulldown"
              name="assetTypePulldown"
              priority="10"
              onchange="setSearchValue(document.searchform.assetTypePulldown[ document.searchform.assetTypePulldown.options.selectedIndex ].value, 'searchform', '<c:out value="${searchActionURL}"/>' );">
	    </select>
             
	    <script language="JavaScript">
              initializeAssetPulldown();
            </script>
              </td>
            </tr>

            <script class="alternateRowHighlight" type="text/javascript">
              function searchOptionChange(f) {
               if (f ==1) {
                 document.getElementById("assetTypePulldown").disabled = true;
                 document.getElementById("hierarchyPane").className = "hierarchyPaneClosed";
               } else {
                 document.getElementById("assetTypePulldown").disabled = false;
                 processHierarchyPane(document.getElementById("assetTypePulldown"));
               }
             }
            </script>

            <!-- Folder browser -->
            <tr>
              <td style="border-bottom: dotted 1px #A4A4A4;">
                <div id="inFolderLabel" class="hierarchyPaneClosed" />
                <div id="hierarchyPaneLabel" class="hierarchyPaneClosed" />
                <div id="hierarchyPane" class="hierarchyPaneClosed">
                  <%@ include file="hierarchyFrame.jspf" %>
                </div>
              </td>
            </tr>

            <script class="alternateRowHighlight" type="text/javascript">
              processHierarchyPane(document.getElementById('repositoryPulldown'));
            </script>

            <tr>
              <td class="formLabel">
                <fmt:message key="containing-letters-words" bundle="${assetsBundle}"/>
              </td>
            </tr>

            <tr>
              <td class="formElement">
                <dspel:input iclass="formElementInputText"
                  size="40" type="text" priority="10" id="keywordInput"
                  bean="${assetSearchHandlerPath}.keywordInput"/>
              </td>
            </tr>

            <tr>
              <td class="action">
                <a href="javascript:submitSearch()" class="goButton" onmouseover="status='';return true;">
                  <fmt:message key="go" bundle="${assetsBundle}"/>
                </a>
              </td>
            </tr>
          </table>
        </div> <!-- end no-class div -->
  	
<%--
        <div style="margin-top: 12px;">
          <table cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td class="title">Modify Results</td>
            </tr>
            <tr>
              <td class="formLabel">Results per page:</td>
            </tr>
            <tr>
              <td>
                <dspel:select bean="${assetSearchHandlerPath}.maxResultsPerPage"
                  iclass="formElementSmallSelect" priority="10">
                  <dspel:option label="10" value="10">10</dspel:option>
                  <dspel:option label="20" value="20">20</dspel:option>
                  <dspel:option label="50" value="50">50</dspel:option>
                  <dspel:option label="100" value="100">100</dspel:option>
                </dspel:select>
              </td>
            </tr>

          </table>
        </div>
--%>

        <%--
        <dspel:input type="hidden" value="1" priority="-1"
          bean="${assetSearchHandlerPath}.search"/>
        <!-- sorted search value -->
        <dspel:input type="hidden" bean="${assetSearchHandlerPath}.sortedSearch" 
          value="" priority="-1"/>
        --%>

        <!-- 
          Search action to take, Note: ACTION_SEARCH is the defualt
          so that a search is performed when user types CR        
         -->
        <dspel:input type="hidden" bean="${assetSearchHandlerPath}.action" 
          id="handlerAction" priority="-1" 
          value="${assetSearchHandler.ACTION_SEARCH}"/>
        <!-- the handler invoking the specified action -->
        <dspel:input type="hidden" bean="${assetSearchHandlerPath}.performAction"
          value="1" priority="-10"/>

      </dspel:form>

    </td>
  </tr>
</table> <!-- end content -->

</dsp:page>


<!-- End AssetPortlet's searchResults.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/AssetsPortlet/searchResults.jsp#2 $$Change: 651448 $--%>
