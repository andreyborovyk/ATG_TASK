<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<dspel:page>

  <dspel:getvalueof var="pagingSearchComponentPath" param="pagingSearchComponentPath"/>
  <dspel:getvalueof var="pagingAnchorName" param="pagingAnchorName"/>
  <dspel:getvalueof var="pagingAlign" param="pagingAlign"/>
  
  <dspel:importbean bean="${pagingSearchComponentPath}" var="pagingSearchComponent"/>
  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <div class="atg_resultTotal">
    <fmt:message key="tablePaging.results">
      <fmt:param value="${pagingSearchComponent.firstItemNumber}"/>
      <fmt:param value="${pagingSearchComponent.lastItemNumber}"/>
      <fmt:param value="${pagingSearchComponent.totalItemsCount}"/>
    </fmt:message>
  </div>

  <dspel:getvalueof bean="/atg/search/web/assetmanager/paging/SearchTestingResultsPaging.pagesCount" var="pagesCount"/>

  <c:if test="${pagesCount > 1}">
    <dspel:input bean="${pagingSearchComponentPath}.currentPageNumber" id="currentPageNumber" name="currentPageNumber"
               type="hidden"/>
    <dspel:input style="display:none" id="openPageButton" type="submit" bean="${pagingSearchComponentPath}.openPage"/>
    
    <div class="atg_pagingControls">
      <span><fmt:message key="tablePaging.page"/></span>
      <%-- 
      this page could be included more then 1 time in some page, e.g. over and under results table in results page. All element ids 
      and method names need to have different names to distinguish between different included pages. Paging align value is
      added to each id and
      --%> 
      <input type="text" size="3" id="<c:out value="${pagingAlign}"/>gotoPageInput"/> <input type="button" value="<fmt:message key='tablePaging.go'/>"
         onclick="goToPageNumber<c:out value="${pagingAlign}"/>(document.getElementById('<c:out value="${pagingAlign}"/>gotoPageInput').value);"/>
      <c:choose>
        <c:when test="${pagingSearchComponent.currentPageNumber == 1}">
          <a href="#" class="atg_icon atg_pageBack atg_inactive" title="<fmt:message key='tablePaging.firstPage'/>"></a>
        </c:when>
        <c:otherwise>
          <fmt:message key="tablePaging.gotoPage" var="gotoMessage">
            <fmt:param>
              <c:out value='${pagingSearchComponent.currentPageNumber - 1}'/>
            </fmt:param>
          </fmt:message>
          <a href="#" onclick="goToPageNumber<c:out value="${pagingAlign}"/>(<c:out value='${pagingSearchComponent.currentPageNumber - 1}'/>)"
             title="<c:out value='${gotoMessage}'/>" class="atg_icon atg_pageBack"></a>
        </c:otherwise>
      </c:choose>
      <span>
        <fmt:message key="tablePaging.pageOfPage">
          <fmt:param>
            <c:out value="${pagingSearchComponent.currentPageNumber}"/>
          </fmt:param>
          <fmt:param>
            <c:out value="${pagingSearchComponent.pagesCount}"/>
          </fmt:param>
        </fmt:message>
      </span>
      <c:choose>
        <c:when test="${pagingSearchComponent.currentPageNumber == pagingSearchComponent.pagesCount}">
          <a href="#" title="<fmt:message key='tablePaging.lastPage'/>"
             class="atg_icon atg_pageForward atg_inactive"></a>
        </c:when>
        <c:otherwise>
          <fmt:message key="tablePaging.gotoPage" var="gotoMessage">
            <fmt:param>
              <c:out value='${pagingSearchComponent.currentPageNumber + 1}'/>
            </fmt:param>
          </fmt:message>
          <a href="#" onclick="goToPageNumber<c:out value="${pagingAlign}"/>(<c:out value='${pagingSearchComponent.currentPageNumber + 1}'/>)"
             title="<c:out value='${gotoMessage}'/>" class="atg_icon atg_pageForward"></a>
        </c:otherwise>
      </c:choose>
    </div>
    <%-- 
      this page could be included more then 1 time in some page, e.g. over and under results table in results page. All element ids 
      and method names need to have different names to distinguish between different included pages. Paging align value is
      added to each id and
    --%> 
    <script type="text/javascript">
      function goToPageNumber<c:out value="${pagingAlign}"/>(pageNum) {
        document.forms[0].action += "#<c:out value='${pagingAnchorName}'/>";
        document.getElementById('currentPageNumber').value = pageNum;
        document.getElementById('openPageButton').click();
      }
    </script>
  </c:if>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/paging/tablePaging.jsp#2 $$Change: 651448 $--%>
