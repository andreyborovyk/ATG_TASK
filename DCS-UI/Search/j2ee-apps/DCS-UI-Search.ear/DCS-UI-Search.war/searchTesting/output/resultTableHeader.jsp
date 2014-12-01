<%--
  This page is a header for the result table.

  This page is included in resultTable.jsp and can be overridden in 
  /atg/search/web/assetmanager/SearchTestingFormHandler.pageConfigMap.resultTableHeaderPage

  @param  multiLanguage     true if results can be in different languages

  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/searchTesting/output/resultTableHeader.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="ee"    uri="http://www.atg.com/taglibs/expreditor"       %>

<dspel:page>

  <dspel:getvalueof var="multiLanguage" param="multiLanguage"/>
  <dspel:getvalueof var="areThereTrackedItems" param="areThereTrackedItems"/>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <tr>
    <th class="atg_smerch_summaryTitle">
      <fmt:message key="searchTestingResultBaseTable.baseTable.noTitle"/>
    </th>    
    <!-- column with trace icon of a result item -->
    <c:if test="${! formHandler.subitemsValid && areThereTrackedItems}">
      <th class="atg_smerch_summaryTitle"></th>
    </c:if>
    <th class="atg_smerch_summaryTitle">
      <fmt:message key="searchTestingResultBaseTable.baseTable.nameTitle"/>
    </th>
    <c:if test="${multiLanguage}">
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestingResultBaseTable.baseTable.nameLanguage"/>
      </th>
    </c:if>
    <th class="atg_smerch_summaryTitle">
      <fmt:message key="searchTestingResultBaseTable.baseTable.idTitle"/>
    </th>
    <ee:isMultisiteMode var="multisiteMode"/>
    <c:if test="${multisiteMode}">
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestingResultBaseTable.baseTable.siteTitle"/>
      </th>
    </c:if>
    <th class="atg_smerch_summaryTitlea atg_numberValue">
      <fmt:message key="searchTestingResultBaseTable.baseTable.finalScoreTitle"/>
    </th>
  </tr>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/searchTesting/output/resultTableHeader.jsp#2 $$Change: 651448 $--%>
