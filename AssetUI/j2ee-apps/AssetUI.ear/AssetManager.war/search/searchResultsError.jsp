<%--
  This page renders the search results error message.

  @param validationExceptions The request scope variable that stores a collection
                              of search expression validation exceptions.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/search/searchResultsError.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>
  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <script type="text/javascript">
        function atgSearchResultsErrorLoaded() {
          parent.stopWait();
          parent.hideLeftLoadingIcon();
        }
      </script>

      <dspel:include page="/components/head.jsp"/>
    </head>
    <body style="background: #fff" onload="atgSearchResultsErrorLoaded()">

      <div style="text-align: left">
        <fmt:message key="searchResults.searchCriteriaErrorMessage"/>
      </div>
      <%--
      < % - - Validation Error display  - START - - % >
      <ul>
        < % - -  check if there are any errors - - % >
        <c:forEach items="${requestScope.validationExceptions}" var="ex">
          <li><c:out value="${ex.message}"/></li>
        </c:forEach>
      </ul>
      < % - - Validation Error display  - END - - % >
      --%>

    </body>
  </html>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/search/searchResultsError.jsp#2 $$Change: 651448 $ --%>
