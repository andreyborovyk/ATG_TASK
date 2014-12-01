<%--
  Search Configuration section of the outputs.
  Displays dimension tree path to the selected search config.

  Included into results.jsp.

  The following request-scoped variables are expected to be set:
  
  @param formHandler     SearchTestingFormHandler instance

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/searchConfig.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set value="/atg/search/config/SearchDimensionManagerService" var="searchDimensionManagerServicePath" />
  <dspel:importbean bean="${searchDimensionManagerServicePath}" var="searchDimensionManagerService" />

  <c:set var="searchConfigurationOutputModel" value="${formHandler.searchConfigurationOutputModel}"/>

  <c:if test="${altFieldSet}">
    <fieldset class="altGroup">
  </c:if>
  <c:if test="${not altFieldSet}">
    <fieldset>
  </c:if>
  <c:set var="altFieldSet" value="${!altFieldSet}" scope="request" />

    <legend><span><fmt:message key="searchTestingResultSearchConfig.fieldset.legend"/></span></legend>
    <dl class="atg_smerch_configList">
      <c:choose>

        <c:when test="${formHandler.selectDirectlyValue eq searchConfigurationOutputModel.selectDimensionType}">

          <dt><fmt:message key="searchTestingResultSearchConfig.configSelected.text"/></dt>
          <dd>
            <a href="#" onclick="return searchConfig_drillDown('<c:out value="${searchConfigurationOutputModel.searchConfigURI}"/>')">
              <c:out value="${searchConfigurationOutputModel.searchConfigName}" />
            </a>
          </dd>
          <dt><fmt:message key="searchTestingResultSearchConfig.correspondingValues.text"/></dt>
          <dd>
            <c:set var="wasItem" value="${false}" />
            <c:forEach items="${formHandler.searchConfigurationOutputModel.correspondingDimensionValues}" var="entry">
              <c:set value="${entry.key}" var="dimensionName" />
              <c:set value="${entry.value}" var="dimensionValue" />
              <c:choose>
                <c:when test="${empty dimensionValue}">
                  <fmt:message key="searchTestingDimensionValuesPicker.all.others.option" var="displayValue" />
                </c:when>
                <c:otherwise>
                  <assetui-search:getDimensionDisplayValue var="displayValue" service="${dimensionName}" value="${dimensionValue}" />
                </c:otherwise>
              </c:choose>
              <c:if test="${wasItem}">
                <fmt:message key="searchTestingResultSearchConfig.delimeter.text" />
              </c:if>
              <dspel:getvalueof bean="${searchDimensionManagerServicePath}.searchDimensionMap.${dimensionName}" var="searchDimension" />
              <fmt:message key="searchTestingResultSearchConfig.dimensionValues">
                <fmt:param value="${searchDimension.displayName}"/>
                <fmt:param value="${displayValue}"/>
              </fmt:message>
              <c:set var="wasItem" value="${true}" />
            </c:forEach>
          </dd>

        </c:when>
        <c:when test="${formHandler.selectByUserProfileValue eq searchConfigurationOutputModel.selectDimensionType or formHandler.selectByDimensionValuesValue eq searchConfigurationOutputModel.selectDimensionType}">

          <c:if test="${formHandler.selectByUserProfileValue eq searchConfigurationOutputModel.selectDimensionType}">
            <dt><fmt:message key="searchTestingResultSearchConfig.previewProfile.text"/></dt>
            <dd><c:out value="${searchConfigurationOutputModel.previewUserName}" /></dd>
          </c:if>
          <dt><fmt:message key="searchTestingResultSearchConfig.dimensionValues.text"/></dt>
          <dd>
            <c:set var="wasItem" value="${false}" />
            <c:forEach items="${formHandler.searchConfigurationOutputModel.correspondingDimensionValues}" var="entry">
              <c:set value="${entry.key}" var="dimensionName" />
              <c:set value="${entry.value}" var="dimensionValue" />
              <c:choose>
                <c:when test="${empty dimensionValue}">
                  <fmt:message key="searchTestingDimensionValuesPicker.all.others.option" var="displayValue" />
                </c:when>
                <c:otherwise>
                  <assetui-search:getDimensionDisplayValue var="displayValue" service="${dimensionName}" value="${dimensionValue}" />
                </c:otherwise>
              </c:choose>
              <c:if test="${wasItem}">
                <fmt:message key="searchTestingResultSearchConfig.delimeter.text" />
              </c:if>
              <dspel:getvalueof bean="${searchDimensionManagerServicePath}.searchDimensionMap.${dimensionName}" var="searchDimension" />
              <fmt:message key="searchTestingResultSearchConfig.dimensionValues">
                <fmt:param value="${searchDimension.displayName}"/>
                <fmt:param value="${displayValue}"/>
              </fmt:message>
              <c:set var="wasItem" value="${true}" />
            </c:forEach>
          </dd>
          <dt><fmt:message key="searchTestingResultSearchConfig.configProcessing.text"/></dt>
          <dd>
            <table class="atg_dataTable atg_smerch_summaryTable">
              <tr>
                <th class="atg_smerch_summaryTitle"><fmt:message key="searchTestingResultSearchConfig.configConsidered.text"/></th>
                <th class="atg_smerch_summaryTitle"><fmt:message key="searchTestingResultSearchConfig.dimensionValuesChecked.text"/></th>
                <th class="atg_smerch_summaryTitle"><fmt:message key="searchTestingResultSearchConfig.match.text"/></th>
              </tr>
              <c:set var="wasFolder" value="${false}" />
              <c:set var="subIndex" value="${0}" />
              <c:forEach var="visitedSearchDimensionNode" items="${searchConfigurationOutputModel.visitedSearchDimensionNodes}" varStatus="loop">
                <c:choose>
                  <c:when test="${loop.index % 2 eq 0}">
                    <c:set var="evenStyle" value="" />
                  </c:when>
                  <c:otherwise>
                    <c:set var="evenStyle" value="atg_altRow" />
                  </c:otherwise>
                </c:choose>
                <c:choose>
                  <c:when test="${visitedSearchDimensionNode.match}">
                    <c:set var="matchStyle" value="atg_subConfigurationMatch" />
                  </c:when>
                  <c:otherwise>
                    <c:set var="matchStyle" value="" />
                  </c:otherwise>
                </c:choose>
                <c:choose>
                  <c:when test="${!empty evenStyle or !empty matchStyle}">
                    <tr class='<c:out value="${evenStyle} ${matchStyle}" />'>
                  </c:when>
                  <c:otherwise>
                    <tr>
                  </c:otherwise>
                </c:choose>

                  <c:if test="${!visitedSearchDimensionNode.folder and wasFolder}">
                    <c:set var="subIndex" value="${subIndex+1}" />
                  </c:if>
                  <c:set var="wasFolder" value="${visitedSearchDimensionNode.folder}" />
                  <c:set var="subStyle" value="" />
                  <c:if test="${subIndex gt 0}">
                    <c:forEach begin="1" end="${subIndex}">
                      <c:set var="subStyle" value="${subStyle}sub" />
                    </c:forEach>
                    <c:set var="subStyle" value="atg_${subStyle}Configuration" />
                  </c:if>
                  <c:choose>
                    <c:when test="${!empty subStyle}">
                      <td class='<c:out value="${subStyle}" />'>
                    </c:when>
                    <c:otherwise>
                      <td>
                    </c:otherwise>
                  </c:choose>

                    <dspel:getvalueof bean="${formHandlerPath}.searchConfigurationOutputModel.visitedSearchDimensionNodes[${loop.index}].node.displayName" var="displayName" />
                    <c:out value="${displayName}" />
                  </td>
                  <td>
                    <assetui-search:getDimensionValueChoices var="dimensionValueChoicesResult" item="${visitedSearchDimensionNode.node}" />
                    <c:choose>
                      <c:when test="${!empty dimensionValueChoicesResult}">
                        <dspel:getvalueof bean="${formHandlerPath}.searchConfigurationOutputModel.visitedSearchDimensionNodes[${loop.index}].node.dimensionValue" var="dimensionValue" />
                        <c:choose>
                          <c:when test="${empty dimensionValue}">
                            <fmt:message key="searchTestingDimensionValuesPicker.all.others.option" var="displayValue" />
                          </c:when>
                          <c:otherwise>
                            <assetui-search:getDimensionDisplayValue var="displayValue" service="${dimensionValueChoicesResult.searchDimensionName}" value="${dimensionValue}" />
                          </c:otherwise>
                        </c:choose>
                        <c:out value="${dimensionValueChoicesResult.searchDimensionDisplayName} = ${displayValue}" />
                      </c:when>
                      <c:otherwise>
                        &nbsp;
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    <c:choose>
                      <c:when test="${visitedSearchDimensionNode.match}">
                        <fmt:message key="searchTestingResultSearchConfig.yes.select"/>
                      </c:when>
                      <c:otherwise>
                        <fmt:message key="searchTestingResultSearchConfig.no.select"/>
                      </c:otherwise>
                    </c:choose>
                  </td>
                </tr>
              </c:forEach>
            </table>
          </dd>
          <dt><fmt:message key="searchTestingResultSearchConfig.configUsed.text"/></dt>
          <dd>
            <c:choose>
              <c:when test="${!empty searchConfigurationOutputModel.searchConfigName}">
                <a href="#" onclick="return searchConfig_drillDown('<c:out value="${searchConfigurationOutputModel.searchConfigURI}"/>')">
                  <c:out value="${searchConfigurationOutputModel.searchConfigName}" />
                </a>
              </c:when>
              <c:otherwise>
                <fmt:message key="searchTestingResultSearchConfig.none.text"/>
              </c:otherwise>
            </c:choose>
          </dd>

        </c:when>
        <c:otherwise>

          <dt><fmt:message key="searchTestingResultSearchConfig.configSelected.text"/></dt>
          <dd><fmt:message key="searchTestingResultSearchConfig.none.text"/></dd>

        </c:otherwise>

      </c:choose>
    </dl>
  </fieldset>
  
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/searchConfig.jsp#2 $$Change: 651448 $--%>
