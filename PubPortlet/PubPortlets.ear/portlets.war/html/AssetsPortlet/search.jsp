<%--
  Initial Asset Search (no results shown)

  @param   projectView        (currently not used)  
  @param   assetURI           the publishing asset's URI
  @param   workspaceName      the workspace's name

  @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/AssetsPortlet/search.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<!-- Begin AssetPortlet's search.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<fmt:setBundle var="assetsBundle" basename="atg.epub.portlet.AssetsPortlet.Resources"/>

<c:set var="assetSearchHandlerPath" 
  value="/atg/epub/servlet/AssetSearchFormHandler"/>

<dspel:importbean bean="${assetSearchHandlerPath}"
  var="assetSearchHandler"/>

<%@ page import="atg.epub.portlet.asset.AssetPortlet" %>

<script language="JavaScript" type="text/javascript" 
  src="<c:url value='/html/scripts/search.jsp'><c:param name="searchHandlerPath" value="${assetSearchHandlerPath}"/></c:url>">
</script>

<portlet:defineObjects/>

<div id="intro">
  <h2>
    <fmt:message key="intro-text" bundle="${assetsBundle}"/>
  </h2>
  <p>
    <fmt:message key="search-helper-text" bundle="${assetsBundle}"/>
  </p>
</div>      

<div id="browseForm">

  <table cellpadding="0" cellspacing="3" border="0">
  
    <%-- list search form handler errors --%>
    <c:forEach items="${assetSearchHandler.formExceptions}" var="ex">
      <br />
      <b><c:out value="${ex.message}"/></b>
    </c:forEach>

    <portlet:actionURL var="searchResultsActionURL">
      <portlet:param name="assetView"
        value='<%= ""+AssetPortlet.SEARCH_RESULTS %>'/>
    </portlet:actionURL>

    <portlet:actionURL var="searchActionURL">
      <portlet:param name="assetView"
        value='<%= ""+AssetPortlet.SEARCH_INITIAL %>'/>
    </portlet:actionURL>

    <dspel:form formid="searchform" name="searchform" id="searchform" 
      action="${searchResultsActionURL}" method="post">

      <script language="JavaScript">

        registerOnLoad( function() { focusTo("keywordInput") } );

        function submitSearch() {
          setFormElement('searchform','handlerAction','<dspel:valueof value="${assetSearchHandler.ACTION_SEARCH}"/>');
          if ( document.searchform.assetTypePulldown.value == null ||
            document.searchform.repositoryPulldown.value == null )
            alert( "You must enter a repository and item type." );
          else 
            submitForm('searchform');
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


      <dspel:input type="hidden" value="${assetSearchHandler.ACTION_SEARCH}" 
        name="handlerAction" id="handlerAction"
        priority="10" bean="${assetSearchHandlerPath}.action"/>

      <!-- Initial sort on displayName -->
      <dspel:input type="hidden" value="displayName" priority="10"
        bean="${assetSearchHandlerPath}.sortProperty"/>

      <!-- Initial sort order is ascending (1) -->
      <dspel:input type="hidden" value="1" priority="10"
        bean="${assetSearchHandlerPath}.sortDirection"/>

      <dspel:input type="hidden" value="false" priority="10"
        bean="${assetSearchHandlerPath}.doTextSearch"/>
      <dspel:input type="hidden" value="true" priority="10"
        bean="${assetSearchHandlerPath}.doKeywordSearch"/>
      <dspel:input type="hidden" value="true" priority="10"
        bean="${assetSearchHandlerPath}.ignoreCase"/>
      <dspel:input type="hidden" value="true" priority="10"
        bean="${assetSearchHandlerPath}.allowRefine"/>
      <dspel:input type="hidden" value="true" priority="10"
        bean="${assetSearchHandlerPath}.clearQueries"/>
      <dspel:input type="hidden" value="true" priority="10"
        bean="${assetSearchHandlerPath}.allowEmptySearch"/>

      <!-- the handler invoking the specified action -->
      <dspel:input type="hidden" bean="${assetSearchHandlerPath}.performAction"
        value="1" priority="-10"/>
        
      <tbody>

        <!-- Asset Type -->

        <tr>
          <td class="formLabel formLabelRequired">
           <fmt:message key="show-asset-types-from-repository" bundle="${assetsBundle}"/>
          </td>
          <td>

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
          <td class="formLabel formLabelRequired">
           <fmt:message key="show-assets-of-type" bundle="${assetsBundle}"/>
          </td>
          <td>
            <dspel:input type="hidden" 
	      bean="${assetSearchHandlerPath}.itemType"
	      id="repositoryItemType"
	      name="repositoryItemType" />
	    
	    <dspel:input type="hidden" 
	      bean="${assetSearchHandlerPath}.encodedPathAndItemType"
	      id="searchInput"
	      name="searchInput" />
	    
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

        <!-- Folder browser -->
        <tr>
          <td id="inFolderLabel" class="formLabelHidden formLabelRequiredHidden">
            <div id="hierarchyPaneLabel" class="hierarchyPaneClosed">In Folder:</div>
          </td>
          <td>
            <div id="hierarchyPane" class="hierarchyPaneClosed">
              <%@ include file="hierarchyFrame.jspf" %>
            </div>
          </td>
        </tr>

        <script class="alternateRowHighlight" type="text/javascript">
          processHierarchyPane(document.getElementById('repositoryPulldown'));
        </script>

        <!-- Search String -->
        <tr>
          <td class="formLabel">
            <fmt:message key="containing" bundle="${assetsBundle}"/>
          </td>
          <td>
            <dspel:input iclass="formElement" id="keywordInput"
              size="40" type="text" priority="10"
              bean="${assetSearchHandlerPath}.keywordInput"/>
          </td>
        </tr>

        <tr>
          <td class="formLabel">
            <fmt:message key="results-per-page" bundle="${assetsBundle}"/>
          </td>
          <td>
            <dspel:select bean="${assetSearchHandlerPath}.maxResultsPerPage"
              iclass="formElement formElementSmallSelect">
              <dspel:option label="10" value="10">10</dspel:option>
              <dspel:option label="20" value="20">20</dspel:option>
              <dspel:option label="50" value="50">50</dspel:option>
              <dspel:option label="100" value="100">100</dspel:option>
            </dspel:select>
            <a href="javascript:submitSearch('searchform')" class="goButton" onmouseover="status='';return true;">
              <fmt:message key="go" bundle="${assetsBundle}"/>
            </a>
          </td>
        </tr>

      </tbody>
     
    </table>

  </dspel:form>

</div> <!-- end browseForm -->

</dsp:page>


<!-- End AssetPortlet's search.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/AssetsPortlet/search.jsp#2 $$Change: 651448 $--%>
