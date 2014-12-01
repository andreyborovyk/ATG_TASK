<%--
  Paging control for navigation (left pane) lists in the asset manager UI.

  Params: 

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/listPagingControl.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>
  <c:set var="debug" value="false"/>

  <%-- unpack the param(s) --%>
  <dspel:getvalueof var="maxResultSetSize" param="maxResultSetSize"/>
  <dspel:getvalueof var="resultSetSize" param="resultSetSize"/>
  <dspel:getvalueof var="itemsPerPage" param="itemsPerPage"/>
  <dspel:getvalueof var="currentPageIndex" param="currentPageIndex"/>
  <dspel:getvalueof var="listURL" param="listURL"/>
 
  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <%-- Hmm... If we want to be able to use this fragment inside the asset
       picker, the tabConfig won't be there.  It seems OK for this to use
       the global bundle, though. --%>
  <%--
  <fmt:setBundle basename="${requestScope.tabConfig.resourceBundle}"/>
  --%>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <script type="text/javascript" 
    src="<c:out value='${config.contextRoot}'/>/scripts/listPager.js">
  </script>

  <%--
  <math:floor var="numPages" value="${(resultSetSize + itemsPerPage - 1)/ itemsPerPage}"/>
  --%>
  <%{
    int numRepositoryItems = ((Integer) pageContext.getAttribute("resultSetSize")).intValue();
    int itemsPerPage = ((Integer) pageContext.getAttribute("itemsPerPage")).intValue();
    int numPages = (numRepositoryItems + itemsPerPage - 1) / itemsPerPage;
    pageContext.setAttribute("numPages", new Integer(numPages));
  }%>

  <c:set var="infoObj" value="listPager"/>
  <script type="text/javascript">
    // Create an instance of a ListPager object containing JavaScript
    // methods for doing the paging of this list.
    var <c:out value="${infoObj}"/> = new ListPager();
    // Initialize the properties of the ListPager object.
    <c:out value="${infoObj}"/>.name             = "<c:out value='${infoObj}'/>";
    <c:out value="${infoObj}"/>.idPrefix         = "<c:out value='${id}'/>";
    <c:out value="${infoObj}"/>.pageIndex        = <c:out value='${currentPageIndex}'/>;
    <c:out value="${infoObj}"/>.numPages         = <c:out value="${numPages}"/>;
    <c:out value="${infoObj}"/>.itemsPerPage     = <c:out value="${itemsPerPage}"/>;
    <c:out value="${infoObj}"/>.numItems         = <c:out value="${resultSetSize}"/>;
    <c:if test="${not empty maxResultSetSize}">
    <c:out value="${infoObj}"/>.maxResults       = <c:out value="${maxResultSetSize}"/>;
    </c:if>
    <c:out value="${infoObj}"/>.panelURL         = "<c:out value='${listURL}' escapeXml='false'/>";

    // This function is called after the containing page has fully loaded, in
    // order to initialize the ListPager object.
    registerOnLoad(function() {
      <c:out value="${infoObj}"/>.initialize();
    });

  </script>


  <table id="scrollFooter">
    <tr>
      <td id="footerCount">
        <div id="footerCountRight">
          <%-- Determine whether the controls should be initially hidden --%>
          <c:set var="pageControlsDisplay" value="inline"/>
          <c:if test="${resultSetSize le itemsPerPage}">
            <c:set var="pageControlsDisplay" value="none"/>
          </c:if>

          <%-- This element is toggled on and off, depending on whether multiple pages exist --%>
          <span id="<c:out value='${id}'/>pageControls" style="display:<c:out value='${pageControlsDisplay}'/>">

            <%-- VCR controls for jumping between pages --%>
            <a id="<c:out value='${id}'/>pageFirst" class="buttonPageFirstOff"
               title="Go to first page"
               href="<c:out value='javascript:${infoObj}.firstPage()'/>">&nbsp;</a>
            <a id="<c:out value='${id}'/>pagePrev" class="buttonPagePrevOff"
               title="Go to previous page"
               href="<c:out value='javascript:${infoObj}.previousPage()'/>">&nbsp;</a>
            <a id="<c:out value='${id}'/>pageNext" class="buttonPageNext"
               title="Go to next page"
               href="<c:out value='javascript:${infoObj}.nextPage()'/>">&nbsp;</a>
            <c:if test="${empty maxResultSetSize || (resultSetSize le maxResultSetSize)}">
            <a id="<c:out value='${id}'/>pageLast" class="buttonPageLast"
               title="Go to last page"
               href="<c:out value='javascript:${infoObj}.lastPage()'/>">&nbsp;</a>
            </c:if>
            <c:if test="${not empty maxResultSetSize && (resultSetSize gt maxResultSetSize)}">
              <a id="<c:out value='${id}'/>pageLast" class="buttonPageLastOff"
                 title="Paging limited due to large result set">&nbsp;</a>
            </c:if>
            &nbsp;&nbsp;

            <%-- Text field for selecting a page --%>
            Page
            <c:if test="${empty maxResultSetSize || (resultSetSize le maxResultSetSize)}">
              <input name="listpager" type="text" id="<c:out value='${id}'/>pageNumber"
                     size="2" value="<c:out value='${currentPageIndex}'/>"
                     onfocus="return <c:out value='${infoObj}.pageNumberFocus()'/>"
                     onkeypress="return <c:out value='${infoObj}.pageNumberKeyPress(event)'/>"/>
            </c:if>
            <c:if test="${not empty maxResultSetSize && (resultSetSize gt maxResultSetSize)}">
              <span id="<c:out value='${id}'/>pageNumber">
                <c:out value="${currentPageIndex}"/>
              </span>
            </c:if>
            of
            <span id="<c:out value='${id}'/>numPages">
              <c:out value="${numPages}"/>
            </span>
          </span>

        </div>
        <div id="footerCountLeft">

          <%-- Always display the number of search results. --%>
          <span id="<c:out value='${id}'/>numItems"><c:out value="${resultSetSize}"/></span>&nbsp;results

          <%-- 324 Results --%>
        </div>
      </td>
    </tr>
  </table>


</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/listPagingControl.jsp#2 $$Change: 651448 $--%>
