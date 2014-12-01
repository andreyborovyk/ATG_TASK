<%--
  Redirection rules section of the query rule output

  Included into queryRules.jsp.

  The following parameters are passed into the page:

  @param redirectionRules   redirection rules collection from search response object

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/redirectionRules.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<dspel:page>
  <dspel:getvalueof param="redirectionRules" var="redirectionRules"/>
  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <table class="atg_dataTable atg_smerch_summaryTable">
    <tr>
      <th class="atg_smerch_summaryTitle"><fmt:message key="searchTestingResultRedirectionRules.title"/></th>
    </tr>
    <c:forEach items="${redirectionRules}" var="redirectionRule" varStatus="status">
      <c:choose>
        <c:when test="${status.first}">
          <c:set var="clazz" value="atg_rowHighlight"/>
        </c:when>
        <c:otherwise>
          <c:set var="clazz" value=""/>
        </c:otherwise>
      </c:choose>
      <tr class="<c:out value='${clazz}'/>">
        <td><c:out value="${redirectionRule.description}"/></td>
      </tr>
    </c:forEach>
  </table>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/redirectionRules.jsp#2 $$Change: 651448 $--%>
