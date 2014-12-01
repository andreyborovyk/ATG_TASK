<%--
  Search Tab main body for Search Tab in Asset Manager UI.
  
  @param  operation   An optional operation to be performed on the search form
                      prior to rendering it.  Valid values are "changeItemType" or "displayResults".

  @param  resetSearchList An optional flag to reset the search list. Default is true.

  Request scope parameters

  @param searchResultsViewConfig The request scope variable that holds the configuration
                                 for the form view in search tab.

  @param searchFormViewConfig The request scope variable that holds the configuration
                              for the form view in search tab.

  @param tabConfig            The request scope variable that holds the configuration
                              for the search tab.

  @param managerConfig        The request scope variable that holds the configuration
                              for the asset manager UI.

  @param resultsListVisible   The request scope variable if set to true the results
                              list would be displayed unless there are exceptions.
                              There is no special handling on this page, but it
                              is passed on to the results page.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/search/searchTabBody.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>
  <%-- set the debug flag --%>
  <c:set var="debug" value="${false}"/>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>

  <%-- Unpack all page parameters and set the page-level or request-level parameters --%>
  <dspel:getvalueof var="operationParam" param="operation"/>
  <dspel:getvalueof var="resetSearchListParam" param="resetSearchList"/>

  <%-- compute the value for resetSearchList.
     if none was specified set it to true, otherwise whatever
     is the value of the page parameter.
    --%>
  <c:set var="resetSearchList" value="${empty resetSearchListParam || resetSearchListParam}"/>

  <c:if test="${debug}">
    <!-- 
    [searchTabBody.jsp] operation: <c:out value="${operationParam}"/><br/>
    [searchTabBody.jsp] SessionInfo.currentSearchItemType: <c:out value="${sessionInfo.currentSearchItemType}"/><br/>
    -->
  </c:if>  

  <%-- Display the correct page for the search form. --%>
  <div id="searchTabExpressionPanel">
    <c:if test="${not empty searchFormViewConfig}">
      <dspel:include otherContext="${searchFormViewConfig.contextRoot}" page="${searchFormViewConfig.page}"/>
    </c:if>
  </div><!--  id="searchTabExpressionPanel" -->

  <%-- Display the correct page for the search results. --%>
  <div id="searchTabResultsPanel">
    <c:if test="${not empty searchResultsViewConfig}">
      <dspel:include otherContext="${searchResultsViewConfig.contextRoot}" page="${searchResultsViewConfig.page}">
        <dspel:param name="operation"      value="${operationParam}"/>
        <dspel:param name="resetList"      value="${resetSearchList}"/>
      </dspel:include>
    </c:if>
  </div><!--  id="searchTabResultsPanel" -->
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/search/searchTabBody.jsp#2 $$Change: 651448 $ --%>
  