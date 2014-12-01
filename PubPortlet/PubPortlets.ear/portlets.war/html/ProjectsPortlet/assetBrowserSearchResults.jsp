<%--
  Initial Asset Search (no results shown)

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<!-- Begin assetBrowserSearchResults.jsp -->

<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*,java.util.*" errorPage="/error.jsp"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %> 

<dspel:page>
<style>
  <dspel:include otherContext="${initParam['atg.bizui.ContextPath']}"
    page="/templates/style/css/style.jsp"/>
</style>

<script language="JavaScript">
  <dspel:include otherContext="${initParam['atg.bizui.ContextPath']}"
    page="/templates/page/html/scripts/scripts.js"/>
</script>

<fmt:setBundle var="projectsBundle" 
  basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

  <dspel:demarcateTransaction var="transactionStatus">

  <%-- get asset info --%>
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

  <c:set var="assetSearchHandlerPath" 
    value="/atg/epub/servlet/AssetBrowserSearchFormHandler"/>
  <dspel:importbean bean="${assetSearchHandlerPath}" 
    var="assetSearchHandler"/>

  <c:set var="projectHandlerPath"
    value="/atg/epub/servlet/ProjectFormHandler"/>
  <dspel:importbean bean="${projectHandlerPath}"
    var="projectFormHandler"/>

  <c:set var="assetPickerHandlerPath"
    value="/atg/epub/servlet/AssetPickerFormHandler"/>
  <dspel:importbean bean="${assetPickerHandlerPath}"
    var="assetPickerHandler"/>

  <%-- set the browse mode: 'project' | 'pick' --%>
  <c:set var="mode" value="${assetInfo.attributes.browserMode}"
    scope="page"/>

  <%-- unset project context if not in pick mode --%>
  <dspel:setvalue value="${ 'pick' != assetInfo.attributes.browserMode }"
    bean="${assetSearchHandlerPath}.unsetProjectContext"/>

  <%-- Set target form handler information based on mode --%>
  <c:choose>
    <c:when test="${'pick' == mode}">
      <c:set var="formHandlerPath" value="${assetPickerHandlerPath}"/>
      <c:set var="formHandlerAction" value="${assetPickerHandlerPath}.action"/>
      <c:set var="apPropertyName" 
        value="${assetInfo.attributes.assetPickerPropertyName}"/>
    </c:when>
    <c:otherwise>
      <c:set var="formHandlerPath" value="${projectHandlerPath}"/>
      <c:set var="formHandlerAction" value="${projectHandlerPath}.assetAction"/>
    </c:otherwise>
  </c:choose>

  <c:if test="${param.moreResults ne null}">
    <c:set target="${assetSearchHandler}" property="moreResults"
      value="${true}"/>
  </c:if>
  <c:if test="${param.resultsPageNum ne null}">
    <c:set target="${assetSearchHandler}" property="currentResultPageNum"
      value="${param.resultsPageNum}"/>
  </c:if>

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
    <fmt:message var="moreSymbol" key="more-results-symbol" 
      bundle="${projectsBundle}"/>
    <c:set var="resultSetSizeDisplay" value="${(resultSetSize-1)}${moreSymbol}"/>
    <c:set var="moreResults" value="${true}"/>
    <%-- don't show last page (with partial list) if more results --%>
    <c:set var="resultPageCount" value="${resultPageCount-1}"/>
  </c:if>

  <html>

    <script type="text/javascript">

      var results = new Object();

      function closeAssetBrowserWindow() {
        if ( "hide" == '<c:out value="${assetInfo.attributes.assetPickerCloseAction}"/>' )
          parent.hideIframe( "assetBrowser" );
        else 
          refreshParentWindow();
      }

      function submitAddSelectedAssets() {
        var form = document.getElementById("projectForm");
        <c:choose>
          <c:when test="${mode == 'pick'}">
            var elements = form.elements;
            for( var ii=0; ii < elements.length; ++ii ) {
              if ( elements[ii].type == "radio" ) {
                if ( elements[ii].checked ) {
                  var result = results[ elements[ii].value ];
                  parent[ parent.document.onSelect ]( result, 
		    '<c:out value="${assetInfo.context.attributes.assetPickerCallerData}"/>' ); 
                }
              }
            }
            form.elements['<c:out value="${formHandlerAction}"/>'].value = "1";
          </c:when>
          <c:when test="${ mode == 'project' }">
            form.elements['<c:out value="${formHandlerAction}"/>'].value = 
              '<c:out value="${projectFormHandler.ADD_ASSET_ACTION}"/>';
          </c:when>
        </c:choose>
        form.submit();
        if ( "pick" == '<c:out value="${mode}"/>' )
          closeAssetBrowserWindow();
      }

      function submitRemoveSelectedAssets() {
        var form = document.getElementById("projectForm");
        form.elements['<c:out value="${formHandlerAction}"/>'].value = 
          '<c:out value="${projectFormHandler.DEL_ASSET_ACTION}"/>';
        form.submit();
      }

      function submitSortedSearch( sortProperty ) {
        var form = document.getElementById( "searchForm" );
        form.elements["<c:out value="${assetSearchHandlerPath}.sortProperty"/>"].value =
          sortProperty;
        form.elements["<c:out value="${assetSearchHandlerPath}.sortDirection"/>"].value =
          '<c:out value="${assetSearchHandler.sortDirection}"/>';
        form.elements["<c:out value="${assetSearchHandlerPath}.action"/>"].value =
          "<c:out value="${assetSearchHandler.ACTION_SORT_ORDER}"/>";
        form.submit();
      }

    </script>

    <body class="assetBrowser" onload="fireOnLoad();">

      <div id="assetBrowserHeader">
        <h2>
          <fmt:message key="asset-browser-title" bundle="${projectsBundle}"/>
        </h2>
      </div>

      <div class="contentActions">

        <c:url var="searchActionURL" value="assetBrowserSearchResults.jsp">
          <c:param name="assetInfo" value="${assetInfoPath}"/>
        </c:url>

        <dspel:form formid="searchForm" id="searchForm" action="${searchActionURL}"
          method="post">

          <dspel:input type="hidden" bean="${assetSearchHandlerPath}.unsetProjectContext" 
            id="handlerAction" priority="10" value="${'pick' != assetInfo.attributes.browserMode}"/>
          <dspel:input type="hidden" value="${assetSearchHandler.sortProperty}"
            priority="10" bean="${assetSearchHandlerPath}.sortProperty" />
          <dspel:input type="hidden" value="${assetSearchHandler.sortDirection}" 
            priority="10" bean="${assetSearchHandlerPath}.sortDirection"/>
          <dspel:input type="hidden" bean="${assetSearchHandlerPath}.action" 
            id="handlerAction" priority="-1" value="${assetSearchHandler.ACTION_SORT_ORDER}"/>
          <dspel:input type="hidden" bean="${assetSearchHandlerPath}.performAction" 
            id="handlerAction" priority="-10" value="${assetSearchHandler.ACTION_SORT_ORDER}"/>

          <table cellpadding="0" cellspacing="0" border="0">
            <tr>
              <td class="blankSpace">
                &nbsp;
              </td>
              <td width="100%" class="rightAlign">
                <c:url var="newSearchURL" value="assetBrowser.jsp">
                  <c:param name="assetInfo" value="${assetInfoPath}"/>
                </c:url>
                <a href='<c:out value="${newSearchURL}"/>' onmouseover="status='';return true;">
                  &laquo;New Search
                </a>
                &nbsp;&nbsp;
              </td>
            </tr>
          </table>
        </dspel:form>
      </div>

      <table cellpadding="0" cellspacing="0" border="0" class="assetBrowserDisplayResults">
        <tr>
          <td class="blankSpace">
            &nbsp;
          </td>
          <td class="assetBrowserAlertMessage">
            <fmt:message key="search-attributes-label" bundle="${projectsBundle}"/>:
            '<c:out value="${assetSearchHandler.keywordInput}"/>'
          </td>
          <td class="rightAlign">
            <div class="displayResults">
              <c:if test="${resultSetSize > assetSearchHandler.maxResultsPerPage}">
                <c:set var="pageNum" value="${0}"/>
                <fmt:message key="paging-info" bundle="${projectsBundle}">
                  <fmt:param value="${startCount}"/>
                  <fmt:param value="${endCount}"/>
                  <fmt:param value="${resultSetSizeDisplay}"/>
                </fmt:message>&nbsp;&nbsp;
                <fmt:message key="pages-label" bundle="${projectsBundle}"/>
                <c:forEach varStatus="loopInfo" begin="0" end="${resultPageCount-1}">
                  <c:set var="pageNum" value="${loopInfo.count}"/>
                  <c:url value="assetBrowserSearchResults.jsp" var="pageURL">
                    <c:param name="resultsPageNum" value="${pageNum}"/>
                    <c:param name="assetInfo" value="${assetInfoPath}"/>
                  </c:url>
                  <c:choose>
                    <c:when test="${currentPageNum == pageNum}">
                      [<c:out value="${currentPageNum}"/>]
                    </c:when>
                    <c:otherwise>
                      <a href='<c:out value="${pageURL}"/>' onmouseover="status='';return true;">
                        <c:out value="${pageNum}"/>
                      </a>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </c:if>
              <c:if test="${moreResults}">
                <c:set var="pageNum" value="${pageNum+1}"/>
                <c:url value="assetBrowserSearchResults.jsp" var="moreURL">
                  <c:param name="assetInfo" value="${assetInfoPath}"/>
                  <c:param name="resultsPageNum" value="${pageNum}"/>
                  <c:param name="moreResults" value="true" />
                </c:url>
                <a href='<c:out value="${moreURL}"/>' onmouseover="status='';return true;">
                  <fmt:message key="more-results-link" bundle="${projectsBundle}"/>
                </a>
                &nbsp;
              </c:if>
              &nbsp;
            </div>
          </td>
        </tr>
      </table>

      <%-- Results --%>
      <div id="assetBrowserResults">

        <c:url var="projectAction" value="assetBrowserSearchResults.jsp">
          <c:param name="assetInfo" value="${assetInfoPath}"/>
        </c:url>

        <dspel:form formid="projectForm" id="projectForm" action='${projectAction}' method="post">
          <c:if test="${ not empty assetSearchHandler.searchResults }">
            <table cellpadding="0" cellspacing="0" border="0" 
              class="assetBrowserDataTable">

              <!-- header -->
              <tr>
                <th class="centerAligned" style="width: 4%;">
                  <img src="images/checkMark1.gif" alt="select multiple column" 
                    width="7" height="8" border="0" />
                </th>
                <th class="leftAligned">
                  <a href="javascript:submitSortedSearch('displayName')" class="tableHeader" onmouseover="status='';return true;">
                    Asset Name
                  </a>
                  &nbsp;&nbsp;
                  <c:choose>
                    <c:when test="${assetSearchHandler.sortDirection == '2'}">
                      <img src="images/sort_arrowAscending.gif"
                        alt="sorted by" width="7" height="5" border="0" />
                    </c:when>
                    <c:when test="${assetSearchHandler.sortDirection == '1'}">
                      <img src="images/sort_arrowDescending.gif"
                        alt="sorted by" width="7" height="5" border="0" />
                    </c:when>
                  </c:choose>                
                </th>
                <th class="centerAligned" style="width: 7%;">
                  <span class="tableHeader">
                    Version
                  </span>
                </th>
              </tr>

              <c:if test="${'project'==mode}">
                <dspel:input type="hidden" bean="ProjectFormHandler.projectId"
                  beanvalue="/atg/web/ATGSession.assetBrowserProjectId"/>
                <dspel:input type="hidden" bean="ProjectFormHandler.assetAction" 
                  value="1" priority="-1"/>
                <dspel:input type="hidden" bean="ProjectFormHandler.performAssetAction" 
                  value="1" priority="-10"/>
              </c:if>
              <c:if test="${'pick'==mode}">
                <dspel:input type="hidden" bean="${assetPickerHandlerPath}.actionType"
                  value="${assetPickerHandler.HANDLE_PICK}" priority="-1"/>
                <dspel:input type="hidden" bean="${assetPickerHandlerPath}.action"
                  value="${assetPickerHandler.HANDLE_PICK}" priority="-10"/>
                <dspel:input type="hidden" bean="${assetPickerHandlerPath}.assetInfoPath"
                  value="${assetInfoPath}" priority="-1"/>
              </c:if>

              <%-- 
                Decide on the input type and CSS class outside the loop. It just so happens
                that the CSS class names for radio and checkbox are radio and checkbox
                respectively. If they change, this will break.
              --%>
              <c:choose>
                <c:when test="${ 'single' == assetInfo.attributes.assetBrowserSelectMode }">
                  <c:set var="inputType" value="radio"/>
                </c:when>
                <c:when test="${ 'multi' == assetInfo.attributes.assetBrowserSelectMode }">
                  <c:set var="inputType" value="checkbox"/>
                </c:when>
              </c:choose>

              <!-- asset list -->

              <c:forEach items="${assetSearchHandler.searchResults}" var="result">
                <pws:createVersionManagerURI object="${result}" var="uri"> 
                  <pws:getAsset var="av" uri="${uri}">
                    <tr>
                      <td class="centerAligned">
                        <c:choose>
                          <c:when test="${'pick'==mode}">
                            <c:choose>
                              <c:when test="${ result.class.name == 'atg.vfs.repository.RepositoryVirtualFile' }">
                                <dspel:input bean="${assetPickerHandlerPath}.resultURIs"
                                  type="${inputType}" iclass="${inputType}" value="${result.canonicalPath}"/>
                                <script language="JavaScript">
                                  var result = new Object();
                                  result.id = '<c:out value="${result.canonicalPath}"/>';
                                  result.displayName = '<c:out value="${result.canonicalPath}"/>';
                                  result.uri = '<c:out value="${uri}"/>';
                                  results[ '<c:out value="${result.canonicalPath}"/>' ] = result;
                                </script>
                              </c:when>
                              <c:otherwise>
                                <dspel:input bean="${assetPickerHandlerPath}.resultURIs"
                                   type="${inputType}" iclass="${inputType}" value="${result.repositoryId}"/>
                                <script language="JavaScript">
                                  var result = new Object();
                                  result.id = '<c:out value="${result.repositoryId}"/>';
                                  result.displayName = '<c:out value="${result.itemDisplayName}"/>';
                                  result.uri = '<c:out value="${uri}"/>';
                                  results[ '<c:out value="${result.repositoryId}"/>' ] = result;
                                </script>
                              </c:otherwise>
                            </c:choose>
                          </c:when>
                          <c:when test="${ 'project' == mode }">
                            <dspel:input bean="ProjectFormHandler.assets"
                              type="${inputType}" value="${uri}" iclass="${inputType}"/>
                          </c:when>
                        </c:choose>
                      </td>
                      <td class="leftAligned">
                        <c:url var="assetDetailURL" value="assetBrowserAssetDetail.jsp">
                          <c:param name="assetInfo" value="${assetInfoPath}"/>
                          <c:param name="assetURI" value="${uri}"/>
                          <c:param name="clearContext" value="true"/>
                          <c:param name="clearAttributes" value="true"/>
                        </c:url>
                        <a href='<c:out value="${assetDetailURL}"/>' onmouseover="status='';return true;">
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
                        <c:out value="${av.asset.mainVersion.version}"/>
                      </td>
                    </tr>
                  </pws:getAsset>
                </pws:createVersionManagerURI>
              </c:forEach>
            </table>
          </c:if>
        </dspel:form>
      </div>

      <div class="okAlt">
        <table border="0" cellpadding="0" cellspacing="0" width="100%">
          <tr>
            <td width="100%">
              &nbsp;
            </td>
            <c:if test="${ 'project' == mode }">
              <td>
                <a href="javascript:submitRemoveSelectedAssets()" 
                  class="mainContentButton go" onmouseover="status='';return true;">
                  <c:choose>
                    <c:when test="${ 'single' == assetInfo.attributes.assetBrowserSelectMode }">
                      <fmt:message key="delete-asset-in-process" bundle="${projectsBundle}"/>
                    </c:when>
                    <c:when test="${ 'multi' == assetInfo.attributes.assetBrowserSelectMode }">
                      <fmt:message key="delete-assets-in-process" bundle="${projectsBundle}"/>
                    </c:when>
                  </c:choose>
                </a>
              </td>
            </c:if>
            <td>
              <a href="javascript:submitAddSelectedAssets()" 
                class="mainContentButton go" onmouseover="status='';return true;">
                <c:choose>
                  <c:when test="${ 'pick' == mode }">
                    <fmt:message key="select-asset" bundle="${projectsBundle}"/>
                  </c:when>
                  <c:when test="${ 'single' == assetInfo.attributes.assetBrowserSelectMode }">
                    <fmt:message key="add-selected-asset" bundle="${projectsBundle}"/>
                  </c:when>
                  <c:when test="${ 'multi' == assetInfo.attributes.assetBrowserSelectMode }">
                    <fmt:message key="add-selected-assets" bundle="${projectsBundle}"/>
                  </c:when>
                </c:choose>
              </a>
            </td>
            <td class="buttonImage">
              <a href="javascript:closeAssetBrowserWindow()" class="mainContentButton cancel" onmouseover="status='';return true;">
                <c:choose>
                  <c:when test="${'pick' == assetInfo.attributes.browserMode}">
                    <fmt:message key="cancel-button" bundle="${projectsBundle}"/>
                  </c:when>
                  <c:otherwise>
                    <fmt:message key="done-button" bundle="${projectsBundle}"/>
                  </c:otherwise>
                </c:choose>
              </a>
            </td>
            <td>
              &nbsp;
            </td>
          </tr>
        </table>
      </div>	

    </body>
  </html>

  </dspel:demarcateTransaction>
		
</dspel:page>
<!-- End assetBrowserSearchResults.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/assetBrowserSearchResults.jsp#2 $$Change: 651448 $--%>
