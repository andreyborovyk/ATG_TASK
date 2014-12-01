<%--
  Default CA repository search results plugin page
  
  @param assetInfo
    Client path to nucleus AssetInfo context object. 
    Source: client/container
  
  @param clientView
    Constant which determines the client URL (see: atg.web.assetpicker.Constants
    Source: client/container

  @param isCondensed
    Boolean value which determines if the plugin should display a condensed
    or full view of the asset properties
    Source: client/container
  
  @param moreResults
    Boolean value, which determines if there are more results beyond the 
    number of pages listed. If there are, "more" will be displayed
    Source: repositorySearchResults.jsp
  
  @param resultsPageNum
    Parameter containing the page number of the results to display
    Source: repositorySearchResults.jsp
    
  @param clearContext
    Parameter which determines if the asset context in the asset picker
    should be cleared
    Source: assetDetailsCondensed.jsp
  
  @param clearAttributes
    Parameter which determines if the asset context attributes in the 
    asset picker should be cleared
    Source: assetDetailsCondensed.jsp
    
  Javascript methods implemented:
    getSelectedAssets()
      Returns an array of of associative arrays, each consisting of the
      following keys and values:
        id          - the asset's ID
        displayName - the asset's display name
        uri         - the asset's version manager URI
    
  Container Javascript methods called:
    getAssetTypes()
      Returns an associative array of the asset type which to search
      consisting of the following keys and values:
        itemType            - asset type
        componentPath       - component path to repository/vfs
        itemDisplayProperty - item's display name property used for sorting
        encodedType         - assetType:componentPath


  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/plugins/repositorySearchResults.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %> 
<%@ taglib prefix="web-ui"  uri="http://www.atg.com/taglibs/web-ui" %>

<!-- Begin repositorySearchResults.jsp -->

<dspel:page>

<c:catch var="ex">

  <dspel:importbean var="bizUIConfig" bean="/atg/bizui/Configuration" />
  <dspel:importbean var="config"      bean="/atg/assetui/Configuration"/>  
  <fmt:setBundle var="assetuiBundle" basename="${config.resourceBundle}"/>

  <%@ include file="/assetPicker/assetPickerConstants.jsp" %>

  <%-- set to true to see lots of debugging information --%>
  <c:set var="debug" value="false" scope="request"/>

  <c:choose>
    <c:when test="${ ! empty param.assetInfo }">
      <c:set var="assetInfoPath" value="${param.assetInfo}"/>
    </c:when>
    <c:otherwise>
      <c:set var="assetInfoPath" 
        value="/atg/epub/servlet/ProcessAssetInfo"/>
    </c:otherwise>
  </c:choose>
  <dspel:importbean bean="${assetInfoPath}" var="assetInfo"/>

  <%-- repository --%>
  <c:if test="${ ! empty assetInfo.attributes.componentPath }">
    <c:set var="componentPath" value="${assetInfo.attributes.componentPath}"/>
  </c:if>

  <%-- item type --%>
  <c:if test="${ ! empty assetInfo.attributes.itemType }">
    <c:set var="itemType" value="${assetInfo.attributes.itemType}"/>
  </c:if>

  <%-- item type display name--%>
  <c:choose>
    <c:when test="${ ! empty assetInfo.attributes.itemTypeDisplayName }">
      <c:set var="itemTypeDisplayName" value="${assetInfo.attributes.itemTypeDisplayName}"/>
    </c:when>
    <c:otherwise>
      <c:set var="itemTypeDisplayName" value="${itemType}"/>
    </c:otherwise>
  </c:choose>

  <%-- isAllowMultiSelect --%>
  <c:if test="${ ! empty assetInfo.attributes.isAllowMultiSelect }">
    <c:set var="isAllowMultiSelect" value="${assetInfo.attributes.isAllowMultiSelect}"/>
  </c:if>

  <%-- project assets --%>
  <pws:getCurrentProject var="projectContext"/>

  <%-- calling client URL --%>
  <c:choose>
    <c:when test="${ param.clientView eq ASSET_PICKER_IFRAME }">
      <c:set var="clientURL" value="${ ASSET_PICKER_IFRAME_URL }"/>
      <c:set var="assetURL" value="${ ASSET_PICKER_IFRAME_URL }"/>  
    </c:when>
    <c:when test="${ param.clientView eq BROWSE_TAB }">
      <c:set var="clientURL" value="${ BROWSE_TAB_URL }"/>
      <c:set var="assetURL" value="${ ASSET_PORTLET_URL }"/>  
    </c:when>
    <c:otherwise>
      <c:set var="clientURL" value="${ ASSET_PICKER_IFRAME_URL }"/>
      <c:set var="assetURL" value="${ ASSET_PICKER_IFRAME_URL }"/>  
    </c:otherwise>
  </c:choose>

  <%-- Get the KeywordRepositorySearchFormHandler --%>
  <c:choose>
    <c:when test="${ ! empty param.searchForm }">
      <c:set var="searchFormHandlerPath" 
        value="${param.searchForm}"/>
    </c:when>
    <c:otherwise>
      <c:set var="searchFormHandlerPath" 
        value="/atg/epub/servlet/AssetBrowserSearchFormHandler"/>
    </c:otherwise>
  </c:choose>

  <dspel:importbean bean="${searchFormHandlerPath}"
    var="searchFormHandler"/>

  <script language="JavaScript">
    
    //
    // results - array of result objects
    //
    var results = new Array();
       
    //
    // refresh the results by obtaining the last search keyword and submitting the form
    // 
    function refreshResults() {
      document.repositorySearchForm.keyword.value = '<c:out value="${searchFormHandler.keywordInput}"/>' ;  
      performSearch();

    }

    // flag that a search has been performed on the page
    var searchPerformed=false;

    //
    // getting search relevant info from container page
    //
    function performSearch() {

      // prevent multiple submissions
      if (searchPerformed==true) {
        return;
      }
      searchPerformed=true;

      var assetType           = getAssetType();
      
      var subTypesForm = document.subTypesForm;

      var componentPath       = assetType.componentPath;
      var itemDisplayProperty = assetType.itemDisplayProperty;
      var encodedType         = assetType.encodedType;
      if (subTypesForm != null ) {
        var itemType            = subTypesForm.selectedItemType.value;
        encodedType             = componentPath + ":" + itemType;
        
      }
      else {
        var itemType            = assetType.itemType;      
      }
      
      
      if (itemDisplayProperty == null) {
        itemDisplayProperty = getSubItemDisplayProperty(componentPath,itemType);
      }

      var repositorySearchForm = document.repositorySearchForm;
      
      repositorySearchForm.componentPath.value = componentPath;
      repositorySearchForm.itemType.value      = itemType;
      repositorySearchForm.encodedType.value   = encodedType;

      if ( itemDisplayProperty != null ) {
        repositorySearchForm.sortProperty.value = 
          itemDisplayProperty;
      }
      else {
        repositorySearchForm.sortProperty.value = 
          "displayName";
      }
      
      repositorySearchForm.submit();
      return false;
    }
    
    //
    // Helper method to get item display property
    //
    function getSubItemDisplayProperty( repository, itemType ) {
      var itemDisplayProperty = null;

      for (var i=0; i<assetTypes.length; i++) {
        if ( assetTypes[i].componentPath == repository && 
             assetTypes[i].typeName == itemType )

          itemDisplayProperty = assetTypes[i].displayNameProperty ;
      }
      return itemDisplayProperty;
    }
  </script>

  <c:if test="${debug}">
    <p>fh: <c:out value="${searchFormHandler.class.name}"/>
    <p>Hi, my name is repositorySearch.jsp. I'm the default search page for repository assets
  </c:if>
 
  <div id="repositorySearchBody" style="display: block;">

    <div id="assetBrowserContentBody">
      <div id="assetListHeader">
        <div id="assetListHeaderRight"></div>
        <div id="assetListHeaderLeft">
        <c:if test="${ ! empty componentPath }">
          <%-- get list of subtypes, if any --%>
          <pws:getItemSubTypes var="subtypes"
            repositoryPath="${componentPath}" itemType="${itemType}">
            <c:choose>
              <c:when test="${ ! empty subtypes[1] }">
                <%-- ok, we have some subtypes --%>
                <fmt:message key='searching-for' bundle='${assetuiBundle}' />:&nbsp;
                <form name="subTypesForm" action="#">
                  <select id="selectedItemType" name="selectedItemType">
                    <c:forEach items="${subtypes}" var="subtype">
                      <%-- display the subtype in a dropdown --%>
                      <option value='<c:out value="${subtype.itemDescriptorName}"/>'>
                        <c:choose>
                         <c:when test="${ ! empty subtype.beanDescriptor.displayName }">
                           <c:out value="${subtype.beanDescriptor.displayName}"/>
                         </c:when>
                         <c:otherwise>
                           <c:out value="${subtype.itemDescriptorName}"/>
                         </c:otherwise>
                        </c:choose>
                      </option>
                    </c:forEach>
                  </select>
                </form>
              </c:when>
              <%-- no subtypes --%>
              <c:otherwise>
                <fmt:message key='searching-for' bundle='${assetuiBundle}' />&nbsp;<strong><c:out value="${itemTypeDisplayName}"/></strong>
              </c:otherwise>
            </c:choose>
          </pws:getItemSubTypes>
        </c:if>
        </div>
      </div>

      <%-- Search form action--%>
      <c:choose>
        <c:when test="${ !empty param.assetBrowseContext }">
          <%-- Search form action--%>
          <c:url var="searchFormActionURL" context="${ASSETBROWSE_CONTEXT}" value="${ clientURL }">
            <c:param name="assetInfo" value="${assetInfoPath}"/>
            <c:param name="searchForm" value="${searchFormHandlerPath}"/>
            <c:param name="apView" value="${AP_CONTAINER}"/>
          </c:url>
        </c:when>
        <c:otherwise>
          <%-- Search form action--%>
          <c:url var="searchFormActionURL" value="${ clientURL }">
            <c:param name="assetInfo" value="${assetInfoPath}"/>
      <c:param name="apView" value="${AP_CONTAINER}"/>
          </c:url>
        </c:otherwise>
      </c:choose>

      <%-- unset project context if not in pick mode --%>
      <dspel:setvalue value="${ 'pick' != assetInfo.attributes.browserMode }" 
        bean="${searchFormHandlerPath}.unsetProjectContext"/>
       
      <dspel:form formid="search" name="repositorySearchForm" id="repositorySearchForm" onsubmit="return performSearch();"
        action="${searchFormActionURL}" method="post">

        <div id="assetListSubHeader">
          <div id="assetListHeaderRightRefresh">
            <c:if test="${param.showRefresh ne null}">
              <span class="label"><a href='javascript:refreshResults();' ><fmt:message key='refresh-results' bundle='${assetuiBundle}' /></a></span>
            </c:if>
          </div>
          <div id="assetListSubHeaderLeft">
            <span class="label">
              <fmt:message key='keyword' bundle='${assetuiBundle}' />
            </span>
            <span class="subHeaderText">
              <dspel:input size="40" type="text" id="keyword" value="" priority="10"
                bean="${searchFormHandlerPath}.keywordInput" iclass="formTextField"/>
              <a href="javascript:performSearch();" class="buttonSmall go" title="<fmt:message key='go-button' bundle='${assetuiBundle}' />">
                <span>
                  <fmt:message key='go-button' bundle='${assetuiBundle}' />
                </span>
              </a>
            </span>
          </div>
        </div>

        <%-- search form handler fields --%>
        <dspel:input type="hidden" value="true"
          bean="${searchFormHandlerPath}.doKeywordSearch"/>
        <dspel:input type="hidden" value="false" 
          bean="${searchFormHandlerPath}.doTextSearch"/>
        <dspel:input type="hidden" value="true"
          bean="${searchFormHandlerPath}.ignoreCase"/>
        <dspel:input type="hidden" value="false" 
          bean="${searchFormHandlerPath}.allowRefine"/>

        <%-- Sort order is ascending by default (1) --%>
        <dspel:input type="hidden" value="1" 
          bean="${searchFormHandlerPath}.sortDirection"/>
        <dspel:input type="hidden"
          id="sortProperty" bean="${searchFormHandlerPath}.sortProperty"/>

        <dspel:input type="hidden" value="${searchFormHandler.ACTION_SEARCH}" 
          priority="10" bean="${searchFormHandlerPath}.action"/>
        <dspel:input type="hidden" value="1" 
          priority="-10" bean="${searchFormHandlerPath}.performAction"/>

        <dspel:input type="hidden"
          id="componentPath" bean="${searchFormHandlerPath}.componentPath"/>
        <dspel:input type="hidden"
          id="itemType" bean="${searchFormHandlerPath}.itemType"/>
        <dspel:input type="hidden"
          id="encodedType" bean="${searchFormHandlerPath}.encodedPathAndItemType"/>

        <dspel:input type="hidden" value="${MAX_SEARCH_RESULTS}"
          bean="${searchFormHandlerPath}.maxResultsPerPage"/>
      </dspel:form>

      <c:if test="${param.moreResults ne null}">
        <c:set target="${searchFormHandler}" property="moreResults"
          value="${true}"/>
      </c:if>
      <c:if test="${param.resultsPageNum ne null}">
        <c:set target="${searchFormHandler}" property="currentResultPageNum"
          value="${param.resultsPageNum}"/>
      </c:if>

      <c:set var="startCount" value="${searchFormHandler.startCount}"/>
      <c:set var="endCount" value="${searchFormHandler.endCount}"/>
      <c:set var="currentPageNum" value="${searchFormHandler.currentResultPageNum}"/>
      <c:set var="resultPageCount" value="${searchFormHandler.resultPageCount}"/>
      <c:set var="resultSetSize" value="${searchFormHandler.resultSetSize}"/>
      <c:set var="resultSetSizeDisplay" value="${resultSetSize}"/>

      <c:set var="moreResults" value="${false}"/>
      <c:if test="${(searchFormHandler.maxRowCount ne -1) && resultSetSize > searchFormHandler.maxRowCount}">
        <c:set var="moreResults" value="${true}"/>
        <%-- don't show last page (with partial list) if more results --%>
        <c:set var="resultPageCount" value="${resultPageCount-1}"/>
      </c:if>

      <div id="scrollContainer" style="height: 240px">
        <table id="assetListContentTable" cellpadding="0" cellspacing="0">
          <c:choose>
            <c:when test="${ isAllowMultiSelect }">
              <c:set var="inputType" value="checkbox"/>
            </c:when>
            <c:otherwise>
              <c:set var="inputType" value="radio"/>
            </c:otherwise>
          </c:choose>
          <form id="resultsForm" name="resultsForm" action="#">
            <dspel:droplet name="/atg/dynamo/droplet/Switch">
              <dspel:param bean="${searchFormHandlerPath}.formError" name="value"/>
              <dspel:oparam name="true">
                <ul>
                <dspel:droplet name="/atg/dynamo/droplet/ForEach">
                  <dspel:param name="array" bean="${searchFormHandlerPath}.formExceptions"/>
                  <dspel:oparam name="output">
                    <li><div class="error"><dspel:valueof param="element"/></div>
                  </dspel:oparam>
                </dspel:droplet>
                </ul>
              </dspel:oparam>
              <dspel:oparam name="false">
                <c:if test="${ not empty searchFormHandler.searchResults }">
                    <c:forEach var="item" items="${searchFormHandler.searchResults}" varStatus="status">
                      <pws:createVersionManagerURI var="uri" object="${item}">
                        <tr>
                          <td class="checkboxCell"> 
                          <% atg.repository.RepositoryItem item = (atg.repository.RepositoryItem) pageContext.findAttribute("item");
                               pageContext.setAttribute("disabled", "");
                               if (item instanceof atg.repository.SecuredRepositoryItem) {
                                 if (! ((atg.repository.SecuredRepositoryItem) item).hasAccess(atg.security.StandardAccessRights.WRITE))
                                   pageContext.setAttribute("disabled", "disabled=\"true\"");
                               }
                            %>
                            <input type="<c:out value='${inputType}' />" name="selectedAssets"
                              value="<c:out value='${item.repositoryId}'/>" <c:out value='${disabled}'/> />
                          </td>
                          <td class="nameCell">

                            <c:choose>
                              <c:when test="${ !empty param.bccContext }">
                                <c:url var="assetDetailURL" context="${BCC_CONTEXT}" value="${ assetURL }">
                                  <c:choose>
                                    <c:when test="${ param.isCondensed }">
                                      <c:param name="assetView" value="${ ASSET_DETAILS_CONDENSED }"/>
                                    </c:when>
                                    <c:otherwise>
                                      <c:param name="assetView" value="${ ASSET_DETAILS }"/>
                                    </c:otherwise>
                                  </c:choose>
                                  <c:param name="assetInfo" value="${assetInfoPath}"/>
                                  <c:param name="assetURI" value="${uri}"/>
                                  <c:param name="clearContext" value="true"/>
                                  <c:param name="clearAttributes" value="true"/>
                                </c:url>
                              </c:when>
                              <c:otherwise>
                                <c:url var="assetDetailURL" value="${ assetURL }">
                                  <c:choose>
                                    <c:when test="${ param.isCondensed }">
                                      <c:param name="assetView" value="${ ASSET_DETAILS_CONDENSED }"/>
                                    </c:when>
                                    <c:otherwise>
                                      <c:param name="assetView" value="${ ASSET_DETAILS }"/>
                                    </c:otherwise>
                                  </c:choose>
                                  <c:param name="assetInfo" value="${assetInfoPath}"/>
                                  <c:param name="assetURI" value="${uri}"/>
                                  <c:param name="clearContext" value="true"/>
                                  <c:param name="clearAttributes" value="true"/>
                                </c:url>
                              </c:otherwise>
                            </c:choose>

                            <%-- Determine the asset icon --%>
       <web-ui:getIcon
                               var="iconUrl"
                               container="${item.repository.absoluteName}"
                               type="${item.itemDescriptor.itemDescriptorName}"
                               assetFamily="RepositoryItem" />

                            <%-- Display each asset in the result set as a link that
                                 displays the asset in the asset editor. --%>          
                            <a href="<c:out value='${ assetDetailURL }'/>">
                              <dspel:img otherContext="${iconUrl.contextRoot}" src="${iconUrl.relativeUrl}" border="0" align="absmiddle"/>&nbsp;<span class="asset"><c:out value="${item.itemDisplayName}"/></span>
                            </a>
                          </td>
                        </tr>

                        <c:set var="itemDisplayName" value="${item.itemDisplayName}" />
                        <script language="JavaScript">
                          var result = new Object();
                          result.id          = '<c:out value="${item.repositoryId}"/>';
                          result.displayName = decodeJavaURIComponent( '<%=java.net.URLEncoder.encode( ""+pageContext.findAttribute("itemDisplayName"), "UTF-8" ) %>' );
                          result.uri         = '<c:out value="${uri}"/>';
                          <c:set var="resultPropertyNames" value="${assetInfo.attributes.resultPropertyNames}"/>
                          <%
                              String resultPropertyNames = (String) pageContext.findAttribute("resultPropertyNames");
                              Object bean = pageContext.findAttribute("item");
                              if(resultPropertyNames != null) {
                                 String [] propertyNames = atg.core.util.StringUtils.splitStringAtCharacter(resultPropertyNames,';');
                                 for(int i = 0 ; i < propertyNames.length ; i++) {
                                   String propertyName = propertyNames[i];
                                   Object propertyValue = "";
                                   try {
                                     propertyValue = atg.beans.DynamicBeans.getSubPropertyValue(bean,propertyName);
                                   } catch(Exception e) {
                                   }
                                   out.println("result."+propertyName+" = '" + propertyValue +"';");
                                 }
                              }
                          %>
                          results[ '<c:out value="${item.repositoryId}"/>' ] = result;
                        </script>
                      </pws:createVersionManagerURI>
                    </c:forEach>
                </c:if>
              </dspel:oparam>
            </dspel:droplet>
          </form>
        </table>
      </div>

      <div id="footerCount" style="height: 40px; overflow: auto">
            <div id="footerCountRight">
              <c:if test="${resultPageCount gt 1}">
                page 
                <c:forEach varStatus="loopInfo" begin="0" end="${resultPageCount-1}">
                  <c:set var="pageNum" value="${loopInfo.count}"/>
                  <c:choose>
                    <c:when test="${ !empty param.assetBrowseContext }">
                      <c:url var="pageNumURL" context="${ASSETBROWSE_CONTEXT}" value="${ clientURL }">
                        <c:param name="resultsPageNum" value="${pageNum}"/>
                        <c:param name="assetInfo" value="${assetInfoPath}"/>
                      </c:url>
                    </c:when>
                    <c:otherwise>
                      <c:url var="pageNumURL" value="${ clientURL }">
                        <c:param name="resultsPageNum" value="${pageNum}"/>
                        <c:param name="assetInfo" value="${assetInfoPath}"/>
                      </c:url>
                    </c:otherwise>
                  </c:choose>
                  <c:choose>
                    <c:when test="${currentPageNum == pageNum}">
                      <a href='<c:out value="${pageNumURL}"/>' class="current">
                        <c:out value="${currentPageNum}"/>
                      </a>
                    </c:when>
                    <c:otherwise>
                      <a href='<c:out value="${pageNumURL}"/>'>
                        <c:out value="${pageNum}"/>
                      </a>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
                <c:if test="${moreResults}">
                  <c:choose>
                    <c:when test="${ !empty param.assetBrowseContext }">
                      <c:url var="moreURL" context="${ASSETBROWSE_CONTEXT}" value="${ clientURL }">
                        <c:param name="assetInfo" value="${assetInfoPath}"/>
                        <c:param name="resultsPageNum" value="${pageNum}"/>
                        <c:param name="moreResults" value="true" />
                      </c:url>
                    </c:when>
                    <c:otherwise>
                      <c:url var="moreURL" value="${ clientURL }">
                        <c:param name="assetInfo" value="${assetInfoPath}"/>
                        <c:param name="resultsPageNum" value="${pageNum}"/>
                        <c:param name="moreResults" value="true" />
                      </c:url>
                    </c:otherwise>
                  </c:choose>
                  <a href='<c:out value="${moreURL}"/>'>
                    more
                  </a>
                </c:if>
              </c:if>
            </div>
            <div id="footerCountLeft">
              <dspel:droplet name="/atg/dynamo/droplet/Switch">
                <dspel:param bean="${searchFormHandlerPath}.formError" name="value"/>
                <dspel:oparam name="false">
                  <c:out value="${searchFormHandler.resultSetSize}"/> <fmt:message key='results' bundle='${assetuiBundle}' />
                </dspel:oparam>
              </dspel:droplet>
            </div>
      </div>

    </div>
  </div>

  <%--
    This is the AssetInfo used exclusively to display information
    about the currently selected asset in the picker
  --%>
  <c:set var="assetPickerAssetInfoPath"
    value="/atg/epub/servlet/AssetBrowserAssetInfo"/>
  <dspel:importbean var="assetPickerAssetInfo"
     bean="${assetPickerAssetInfoPath}"/>

  <%-- clear the asset picker context if necessary --%>
  <c:if test="${ 'y' == param.clearContext }">
    <c:set target="${assetPickerAssetInfo}" property="clearContext" value="true"/>
  </c:if>
  <c:if test="${ 'y' == param.clearAttributes }">
    <c:set target="${assetPickerAssetInfo}" property="clearAttributes" value="true"/>
  </c:if>

  <script language="JavaScript">
    //
    // Returns an array of of associative arrays, each consisting of the
    // following keys and values:
    //   id          - the asset's ID
    //   displayName - the asset's display name
    //   uri         - the asset's version manager URI
    //
    function getSelectedAssets() {
      
      var selectedResults = new Array();
      var resultsForm     = document.resultsForm;
      
      if ( resultsForm.selectedAssets != null ) {
        // this is the condition where there is one result
        if ( resultsForm.selectedAssets.length == null ) {
          if ( resultsForm.selectedAssets.checked == true) {
            var result = results[ resultsForm.selectedAssets.value ];
            selectedResults[0] = result;
          } // if
        } // if
        // multiple results
        else {
          var j = 0;
          for (var i=0; i < resultsForm.selectedAssets.length; i++) {
            if (resultsForm.selectedAssets[i].checked == true) {
              var result = results[ resultsForm.selectedAssets[i].value ];
              selectedResults[j] = result;
              j++;
            }  //if
          } // for
        } // else
      }

      return selectedResults;
    }
  </script>

</c:catch>

<%
  Throwable tt = (Throwable) pageContext.getAttribute("ex");
  if ( tt != null ) {
    System.out.println("Caught exception in repositorySearchResults.jsp:");
    tt.printStackTrace();
  }
%>

</dspel:page>

<!-- End repositorySearchResults.jsp -->
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/plugins/repositorySearchResults.jsp#2 $$Change: 651448 $--%>
