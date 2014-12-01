<dsp:page>

  <%--  This page is a global page for displaying pagination related items and
        their links (viz. pageNumber, viewAll etc.)

      Parameters
      - arraySplitSize - Number of items to be displayed per page.
      - start - Start index of the item to be rendered on this page.
      - viewAll (optional) - Set to true if 'view all' has been requested.
      - size - Size of the product listing to be displayed
      - top - Set to true for top set of links, false for the bottom set.
      - itemList - List of items for selected page.
      - q_pageNum - Current page number.
  --%>

  <dsp:getvalueof id="q_facetTrail" param="q_facetTrail"/>
  <dsp:getvalueof id="size" idtype="java.lang.Integer" param="size"/>
  <dsp:getvalueof id="arraySplitSize" idtype="java.lang.Integer" param="arraySplitSize"/>
  <dsp:getvalueof id="start" idtype="java.lang.String" param="start"/>
  <dsp:getvalueof id="viewAll" param="viewAll"/>
  <dsp:getvalueof id="top" param="top"/>
  <dsp:getvalueof id="itemList" param="itemList"/>
  <dsp:getvalueof id="q_pageNum" param="q_pageNum"/>
  <dsp:getvalueof id="question" param="question"/>

  <dsp:getvalueof var="q_docSort" param="q_docSort"/>
  <dsp:getvalueof var="q_docSortOrder" param="q_docSortOrder"/>

  <dsp:getvalueof var="searchFeatures" param="searchFeatures"/>
  <dsp:getvalueof var="searchCategoryId" param="searchCategoryId"/>
  <dsp:getvalueof var="sSearchInput" param="sSearchInput"/>
  <dsp:getvalueof var="isSimpleSearchResults" param="isSimpleSearchResults"/>


  <dsp:getvalueof id="addFacet" param="addFacet"/>
  <dsp:getvalueof id="removeFacet" param="removeFacet"/>
  <dsp:getvalueof id="trail" param="trail"/>
  <dsp:getvalueof id="trailSize" param="trailSize"/>
  <dsp:getvalueof id="categoryId" param="categoryId"/>
  <dsp:getvalueof id="selectedHowMany" param="size"/>
  <dsp:getvalueof var="currentPage" param="currentPage"/>
  <dsp:getvalueof var="isSimpleSearchResults" param="isSimpleSearchResults"/>
  <dsp:getvalueof var="isATGFacetSearch" param="isATGFacetSearch"/>
  
  <c:if test="${empty start && not empty q_pageNum}">
    <c:set var="start" value="${(q_pageNum - 1) * arraySplitSize + 1}"/>
  </c:if>

  <c:if test="${size > arraySplitSize}">
    <%--no need to show the pagination when the number of items is less or equal to the defined number of items per page --%>

    <c:if test="${not isSimpleSearchResults}">
      <c:set var="searchCategoryId" value="${categoryId}"/>
      <c:set var="targetElement" value="atg_store_catSubProdList"/>
    </c:if>


    <c:choose>
      <c:when test="${q_docSort == 'title'}">
        <dsp:getvalueof id="order" value="ascending"/>
      </c:when>
      <c:when test="${q_docSort == 'price'}">
        <dsp:getvalueof id="order" value="ascending"/>
      </c:when>
    </c:choose>
    <crs:pagination size="${size}" arraySplitSize="${arraySplitSize}" start="${start}" viewAll="${viewAll}"
                    top="${top}" itemList="${itemList}">
  
       <jsp:attribute name="pageLinkRenderer">
  
         <c:set var="url" value="${pageContext.request.requestURL}"/>
         <c:set var="pageNum" value="${linkText}"/>
         <%@include file="/facet/gadgets/navLinkHelper.jspf" %>
  
             <c:choose>
               <c:when test="${isATGFacetSearch}">

                 <a href="${url}"
                    onclick="javascript:atg.store.facet.loadDataPagination('${startValue}', '${addFacet}','${linkText}','${trail}','${trailSize}','${categoryId}','${q_docSort}','${selectedHowMany}','false','${order}', '${arraySplitSize}',
                      generateNavigationFragmentIdentifier('${q_docSort}', '${linkText}', 'false','${q_facetTrail}', '${categoryId}', 'handleProductsLoad', true));return false;"
                    title="${linkTitle}">
                     ${linkText}
                 </a>
               </c:when>
               <c:when test="${isSimpleSearchResults}">
                 <a href="${url}"
                    onclick="javascript:simplePagination('${arraySplitSize}', '${searchCategoryId}' , '${searchFeatures}', 'false', '${sSearchInput}', '${linkText}');return false;"
                    title="${linkTitle}">
                     ${linkText}
                 </a>
               </c:when>
               <c:otherwise>

                 <a href="${url}" 
                    onclick="ajaxNavigation('${categoryId}', '${linkText}', 'false', '${q_docSort}',
                      generateNavigationFragmentIdentifier('${q_docSort}', '${linkText}', 'false','${q_facetTrail}', '${categoryId}', 'categoryProducts', false));return false;"
                    title="${linkTitle}">
                     ${linkText}
                 </a>

               </c:otherwise>
             </c:choose>
      </jsp:attribute>
      <jsp:attribute name="viewAllLinkRenderer">
        <c:set var="viewAllLink" value="true"/>
        <c:set var="url" value="${pageContext.request.requestURL}"/>
        <c:set var="pageNum" value=""/>
        <%@include file="/facet/gadgets/navLinkHelper.jspf" %>
        
       <c:choose>
         <c:when test="${isATGFacetSearch}">


           <a href="${url}"
              onclick="javascript:atg.store.facet.loadDataPagination('', '${addFacet}',-1,'${trail}','${trailSize}','${categoryId}','${q_docSort}','${selectedHowMany}','true','${order}', '${arraySplitSize}',
                generateNavigationFragmentIdentifier('${q_docSort}', '-1', 'true','${q_facetTrail}', '${categoryId}', 'handleProductsLoad', true));return false;"
              title="${linkTitle}" class="${viewAllLinkClass}">
               ${linkText}
           </a>

         </c:when>

         <c:when test="${isSimpleSearchResults}">
           <a href="${url}"
              onclick="javascript:simplePagination('-1', '${searchCategoryId}', '${searchFeatures}', 'true', '${sSearchInput}', '${linkText}');return false;"
              title="${linkTitle}" class="${viewAllLinkClass}">
               ${linkText}
           </a>
         </c:when>

         <c:otherwise>


           <a href="${url}"
              onclick="ajaxNavigation('${categoryId}', '', 'true',  '${q_docSort}',
              generateNavigationFragmentIdentifier('${q_docSort}', '${linkText}', 'true','${q_facetTrail}', '${categoryId}', 'categoryProducts', false));return false;"
              title="${linkTitle}">
               ${linkText}
           </a>

         </c:otherwise>


       </c:choose>
      </jsp:attribute>


    </crs:pagination>
    <%-- </c:when>
   </c:choose> --%>
  </c:if>
</dsp:page>

<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/global/gadgets/productListRangePagination.jsp#3 $$Change: 635816 $ --%>