<dsp:page>
  <%-- This page is a gadget to render display of sort element.
     Parameters:
     - sortTitle - Sort element title.
     - liCss - li tag style.
     - addFacet - Add facet element
     - categoryId - Category id
     - start - Product start index 
     - q_pageNum - Current page number
   --%>
  <%-- Set sort by --%>
  <dsp:getvalueof var="sortTitle" param="sortTitle"/>
  <dsp:getvalueof id="originatingRequestURL" bean="/OriginatingRequest.requestURI"/>

  <dsp:getvalueof var="liCss" param="liCss"/>
  <dsp:getvalueof id="q_docSort" param="sortParam"/>
  <dsp:getvalueof id="addFacet" param="addFacet"/>
  <dsp:getvalueof id="removeFacet" param="removeFacet"/>
  <dsp:getvalueof id="trail" param="trail"/>
  <dsp:getvalueof id="mode" param="mode"/>
  <dsp:getvalueof id="trailSize" param="trailSize"/>
  <dsp:getvalueof id="categoryId" param="categoryId"/>
  <dsp:getvalueof id="selectedHowMany" param="size"/>
  <dsp:getvalueof id="start" param="start"/>
  <dsp:getvalueof id="q_pageNum" param="q_pageNum"/>
  <dsp:getvalueof id="order" param="sortOrder"/>
  <dsp:getvalueof id="viewAll" param="viewAll"/>
  <dsp:getvalueof id="arraySplitSize" param="arraySplitSize"/>
  <dsp:getvalueof id="isATGFacetSearch" param="isATGFacetSearch"/>

  <dsp:getvalueof id="q_facetTrail" param="q_facetTrail"/>
  <dsp:getvalueof id="facetOrder" param="facetOrder"/>
  <dsp:getvalueof var="question" param="question"/>
  <dsp:getvalueof var="q_docSortOrder" param="q_docSortOrder"/>

  <li class="${liCss}">

    <c:set var="url" value="${pageContext.request.requestURL}"/>
    <c:set var="facetTrailVar" value="${q_facetTrail}"/>
    <c:set var="pageNum" value="${param.q_pageNum}"/>
    <c:if test="${viewAll eq true}">
      <c:set var="viewAllLink" value="true"/>
    </c:if>
    <%@include file="/facet/gadgets/navLinkHelper.jspf" %>

    <c:choose>

      <c:when test="${isATGFacetSearch}">


        <dsp:a href="${url}"
               onclick="javascript:atg.store.facet.loadDataPagination('${start}','${addFacet}','${q_pageNum}','${trail}','${trailSize}','${categoryId}','${q_docSort}','','${viewAll}','${order}', '${arraySplitSize}',
               generateNavigationFragmentIdentifier('${q_docSort}', '${q_pageNum}', '${viewAll}','${q_facetTrail}', '${categoryId}', 'handleProductsLoad', true));return false;">
          <fmt:message key="${sortTitle}"/>
        </dsp:a>

      </c:when>

      <c:otherwise>

        <%-- category browsing --%>
        <dsp:a href="${url}"
               onclick="ajaxNavigation('${categoryId}', '${q_pageNum}', '${not empty viewAll}', '${q_docSort}',
               generateNavigationFragmentIdentifier('${q_docSort}', '${q_pageNum}', '${not empty viewAll}','${q_facetTrail}', '${categoryId}', 'categoryProducts', false));return false;">
          <fmt:message key="${sortTitle}"/>
        </dsp:a>

      </c:otherwise>
    </c:choose>

  </li>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/global/gadgets/sortGadget.jsp#3 $$Change: 635816 $--%>
