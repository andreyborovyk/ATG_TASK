<%--
  Property Editor for SearchConfiguration.rankingProperties which 
  is a Set of repository items of type rankingProperty.  

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/searchConfigPropertyWeightingViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

  <%@ taglib prefix="c"              uri="http://java.sun.com/jstl/core"                    %>
  <%@ taglib prefix="fmt"            uri="http://java.sun.com/jstl/fmt"                     %>
  <%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
  <%@ taglib prefix="web-ui"         uri="http://www.atg.com/taglibs/web-ui"                %>
  <%@ taglib prefix="asset-ui"       uri="http://www.atg.com/taglibs/asset-ui"              %>
  <%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search"        %>

  <dspel:page>

    <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
    <fmt:setBundle basename="${config.resourceBundle}"/>

    <dspel:importbean var="assetManagerConfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
    <c:set var="sessionInfo" value="${assetManagerConfig.sessionInfo}"/>
    <fmt:setBundle var="assetManagerBundle" basename="${assetManagerConfig.resourceBundle}"/>

    <dspel:importbean var="propManager" bean="/atg/search/repository/SearchConfigurationPropertyManager"/>

    <%-- Unpack request-scoped parameters into page parameters --%>
    <c:set var="propertyView" value="${requestScope.mpv}"/>
    <c:set var="formHandler" value="${requestScope.formHandler}"/>
    <c:set var="componentItemDescriptor" value="${propertyView.componentRepositoryItemDescriptor}"/>
    <c:set var="repPath" value="${componentItemDescriptor.repository.absoluteName}"/>
    <c:set var="repName" value="${componentItemDescriptor.repository.repositoryName}"/>

    <%-- Derive IDs for some form elements and data objects --%>
    <c:set var="id" value="${requestScope.atgPropertyViewId}"/>

    <%-- 
         Get the list of rankingProperties for this search config.  
     --%>
    <dspel:getvalueof var="searchConfig" bean="${requestScope.formHandlerPath}.repositoryItem"/>
    <dspel:tomap var="sc" value="${searchConfig.versionItem}"/>
    <dspel:getvalueof var="rankingPropertiesList" bean="${requestScope.formHandlerPath}.readOnlyRankingProperties"/>
    <dspel:getvalueof var="availableRankingProperties" bean="${requestScope.formHandlerPath}.availableRankingProperties"/>

    <script type="text/javascript">

      //
      // This function is called when the user clicks on the name of the rankingProperty 
      // asset or on the name of the baseSearchConfig to go to the detail view of that asset.
      //
      function drillDownFunc(uri, tab) {
        var linkURL = "/AssetManager/assetEditor/editAsset.jsp?pushContext=true&linkPropertyName=baseSearchConfig&assetURI=" + uri;
        if (tab != null)
        {
          linkURL += "&overrideViewNumber=" + tab;
        }
        // Force a save of the current asset, then move to the URL.
        parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(linkURL,null,null,null,null,true);
      }

    </script>

    <%-- 
         If the rankingPropertiesList is empty, that means that there are no available 
         properties (the ranking properties are created automatically behind the scenes).
         Show a helpful message to the user if there are none of these.
     --%>
    <c:choose> 

      <%-- show this message and nothing more --%>
      <c:when test="${empty availableRankingProperties}">
        <fmt:message key="propertyWeighting.noAvailableRankingProperties" />
      </c:when>

      <%-- show the whole form --%>
      <c:otherwise>

        <%-- 
             Get the baseSearchConfig, and if not null, show whether or not we are inheriting values from it
        --%>
        <dspel:getvalueof var="baseSearchConfigId" 
	    bean="${requestScope.formHandlerPath}.value.baseSearchConfig.repositoryId"/> 
        <c:if test="${baseSearchConfigId != null}">
          <div style="padding: 10px">
            <dspel:input type="checkbox" bean="${requestScope.formHandlerPath}.value.useBaseRankingProperties"
              onclick="submitPropertyWeightingCheck()" disabled="true">
            </dspel:input>
            <fmt:message key="propertyWeighting.useBaseSearchConfiguration" />
            <dspel:getvalueof var="baseConfigName" bean="${requestScope.formHandlerPath}.value.baseSearchConfig.displayName"/>
            <asset-ui:createAssetURI var="linkedURI" 
              componentName="${repName}"
              itemDescriptorName="baseSearchConfig"
              itemId="${baseSearchConfigId}"/>

            <c:set var="currentTabNumber" value="${sessionInfo.propertyTabs['searchConfig']}"/>
            <a href="javascript:<c:out value='drillDownFunc("${linkedURI}",${currentTabNumber})'/>">
              <c:out value="${baseConfigName}"/> 
            </a>
          </div>
        </c:if>

        <dspel:getvalueof var="usingBaseProps" bean="${requestScope.formHandlerPath}.value.useBaseRankingProperties"/> 

        <c:choose>
          <c:when test="${usingBaseProps eq true}">
            <c:set var="baseRankingValues" value="${formHandler.resultPrioritizationValuesForBase}" />
          </c:when>
          <c:otherwise>
            <c:set var="baseRankingValues" value="${formHandler.resultPrioritizationValues}" />
          </c:otherwise>
        </c:choose>
        
          <table id="atg_smerch_basePropWeightAllocation" class="atg_dataTable">
            <thead>
              <tr>
                <th width="25%">
                  <fmt:message key="propertyWeightingWidget.headerText.property"/>
                </th>
                <th width="25%">
                  <fmt:message key="propertyWeightingWidget.headerText.searchResultsWeight"/>
                </th>
                <th width="25%">
                  <fmt:message key="propertyWeightingWidget.headerText.relativeWeight"/>
                </th>
                <th width="25%">
                  <fmt:message key="propertyWeightingWidget.headerText.propertyValueRanking"/>
                </th>
              </tr>
            </thead>
            <tbody id="atg_smerch_basePropertyWeightSetters">
              <tr>
                <td>
                  <span>
                    <c:out value="${baseRankingValues.propertiesRankingProperty.relativeWeight}"/>%
                  </span>
                </td>
                <td>
                  <span>
                    <c:out value="${baseRankingValues.relevanceRankingProperty.relativeWeight}"/>%
                  </span>
                </td>
                <td>
                  <span>
                    <c:out value="${baseRankingValues.fieldRankingProperty.relativeWeight}"/>%
                  </span>
                </td>
                <td>
                  <span>100%</span>
                </td>
              </tr>
            </tbody>
            
          </table>
          <!-- end of the header table -->

          <!-- start of the property table -->

          <table id="atg_smerch_baseRankingProperties_table" class="atg_dataTable">
            
            <!-- table header -->
            <thead>
              <tr>
                <th>
	          <fmt:message key="propertyWeighting.propertyNameTitle" />
                </th>
                <th width="200px">
                  <fmt:message key="propertyWeightingWidget.headerText.relativeWeight"/>
                </th>
                <th>
                  <fmt:message key="propertyWeighting.rankValuesTitle" />
                </th>
              </tr>
            </thead>

            <!-- table content -->
            <tbody id="atg_smerch_basePropertyWeightSetters">
              <c:forEach var="rankProperty" items="${baseRankingValues.individualPropertyRankingProperties}" varStatus="itemLoop">
                <tr id="baseRankingProperties_row_${itemLoop.index}">
                  <td class="formValueCell">
                    <c:out value="${rankProperty.displayName}"/> 
                  </td>
                  <td>
                    <c:choose>
                      <c:when test="${rankProperty.absoluteWeight != 0}">
                        <span style="float: left; width: 4em; text-align: right;">
                          <fmt:formatNumber value="${rankProperty.relativeWeight}" maxFractionDigits="1"/>%
                        </span>
                        <span class="weightBar" style="display: block; width: <fmt:formatNumber value="${rankProperty.relativeWeight}" maxFractionDigits="1"/>px;"/>
                        
                      </c:when>
                      <c:otherwise>
                        <span style="float: left; width: 4em; text-align: right;">
                          <em><fmt:message key="propertyWeightingWidget.ignore" /></em>
                        </span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    <c:if test="${rankProperty.absoluteWeight != 0}">
                      <asset-ui:createAssetURI 
                         var="rankPropURI" 
                         componentName="${repName}"
                         itemDescriptorName="${componentItemDescriptor.itemDescriptorName}"
                         itemId="${rankProperty.repositoryId}"/>
                      <dspel:include page="/assetEditor/property/valueRankSummary.jsp">
                        <dspel:param name="assetURI" value="${rankPropURI}"/>
                      </dspel:include>
                    </c:if>
                  </td>
                </tr>
              </c:forEach>

              </tbody>
              <tbody id="weightTotals">


                <tr>
                  <td class="totalLabel">
                    <fmt:message key="propertyWeightingWidget.propertyPrioritization"/>  : 
                  </td>
                  <td>
                    <c:set var="weightingValue" value="${baseRankingValues.propertiesRankingProperty.relativeWeight}"/>
                    <span style="float: left; width: 4em; text-align: right;"><c:out value="${weightingValue}"/>%</span>
                    <span class="weightBar" style="display: block; width: <c:out value="${weightingValue}"/>px;"/>
                  </td>
                  <td>
                     &nbsp;
                  </td>
                </tr>

                <tr>
                  <td class="totalLabel">
                    <fmt:message key="propertyWeightingWidget.searchTermRelevance"/>  : 
                  </td>
                  <td>
                    <c:set var="weightingValue" value="${baseRankingValues.relevanceRankingProperty.relativeWeight}"/> 
                    <span style="float: left; width: 4em; text-align: right;"><c:out value="${weightingValue}"/>%</span>
                    <span class="weightBar" style="display: block; width: <c:out value="${weightingValue}"/>px;">
                  <td>
                     &nbsp;
                  </td>
                </tr>

                <tr>
                  <td class="totalLabel">
                    <fmt:message key="propertyWeightingWidget.searchTermPropertyMatch"/>  : 
                  </td>
                  <td>
                    <c:set var="weightingValue" value="${baseRankingValues.fieldRankingProperty.relativeWeight}"/> 
                    <span style="float: left; width: 4em; text-align: right;"><c:out value="${weightingValue}"/>%</span>
                    <span class="weightBar" style="display: block; width: <c:out value="${weightingValue}"/>px;"/>
                  </td>
                  <td>
                    <c:if test="${weightingValue != 0}">
                      <asset-ui:createAssetURI 
                         var="rankPropURI" 
                         componentName="${repName}"
                         itemDescriptorName="${componentItemDescriptor.itemDescriptorName}"
                         itemId="${baseRankingValues.fieldRankingProperty.repositoryId}"/>
                      <a id="searchTermPropertyMatchLink" href="javascript:drillDownFunc('<c:out value="${rankPropURI}"/>');">
                        <fmt:message key="propertyWeightingWidget.rankedListOfSearchablePropertiesLink"/>
                      </a>
                    </c:if>
                  </td>
                </tr>
            </tbody>

            <tfoot>
                <tr>
                <td class="totalLabel">
                  <fmt:message key="propertyWeightingWidget.total"/>
                  </td>
                <td colspan="2" id="totalTotal">
                    <span>100%</span>
                </td>
              </tr>
            </tfoot>
          </table>


      </c:otherwise>
    </c:choose>

  </dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/searchConfigPropertyWeightingViewer.jsp#2 $$Change: 651448 $--%>
