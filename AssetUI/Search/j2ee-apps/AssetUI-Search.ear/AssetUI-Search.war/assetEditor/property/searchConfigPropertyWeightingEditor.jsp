<%--
  Property Editor for SearchConfiguration.rankingProperties which
  is a Set of repository items of type rankingProperty.

  The following request-scoped variables are expected to be set:

  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/searchConfigPropertyWeightingEditor.jsp#2 $$Change: 651448 $
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

   <!--Unpack request-scoped parameters into page parameters-->
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="formHandler" value="${requestScope.formHandler}"/>
  <c:set var="componentItemDescriptor" value="${propertyView.componentRepositoryItemDescriptor}"/>
  <c:set var="repPath" value="${componentItemDescriptor.repository.absoluteName}"/>
  <c:set var="repName" value="${componentItemDescriptor.repository.repositoryName}"/>

   <!--Derive IDs for some form elements and data objects-->
  <c:set var="id" value="${requestScope.atgPropertyViewId}"/>
  <c:set var="inputId"               value="propertyValue_${id}"/>
  <c:set var="varCurrentConfigId"    value="varCurrentConfigId_${id}"/>
  <c:set var="onClickCopyFromButton" value="onClickCopyFromButton_${id}"/>
  <c:set var="onCopyFromOK"          value="onCopyFromOK_${id}"/>
  <c:set var="copyFromSourceInputId" value="copyFromSourceInput_${id}"/>
  <c:set var="copyFromSubmitId"      value="copyFromSubmit_${id}"/>


  <%-- A hidden input that is used to pass the selected source config ID to
       the handleCopyPropertyWeightings method --%>
  <dspel:input type="hidden" id="${copyFromSourceInputId}"
      bean="${requestScope.formHandlerPath}.propertyWeightingSourceUri"/>

  <%-- A hidden submit button that is programmatically clicked in order to
       invoke the handleCopyPropertyWeightings method --%>
  <dspel:input type="submit" style="display:none" id="${copyFromSubmitId}"
      bean="${requestScope.formHandlerPath}.copyPropertyWeightings"/>

  <%--
       Get the list of rankingProperties for this search config.
   --%>
  <dspel:getvalueof var="rankingPropertiesList" bean="${requestScope.formHandlerPath}.value.rankingProperties"/>

  <script type="text/javascript">

    //
    // This function is called when a new weighting value is selected from the pulldown.
    // It causes the "graphic equalizer" image to update immediately to reflect the
    // newly selected value, and toggles the visibility of the rankvalues appropriately
    //
    function updateWeightingValue(fieldId, val)
    {
       //update the horizontal bar
       imgId = document.getElementById("rankingImage_" + fieldId);
       imgId.src="<c:out value='${config.contextRoot}'/>/images/ranking/ranking_" + val + ".gif";

       //update the visibility of the ranking values field
       rvElement = document.getElementById("rankingValues_" + fieldId);
       if (val == 0)
       {
         rvElement.style.display = "none";
       }
       else
       {
         rvElement.style.display = "block";
       }
    }


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

    //
    // This function is called when the user changes the value of the checkbox
    // "Use Base Search Configuration"  It will cause the asset to be marked
    // modified in order to activate the save button, and will refresh the form
    // in order to flip the display between read-only values inherited from
    // baseconfig and editable values in this search config.
    //
    function submitPropertyWeightingCheck() {
      markAssetModified();
      swapMyBaseView();
    }

    // Swap the view to make visible the base search config's
    // weightings or the search config's weightings, depending
    // on the value of the checkbox.
    function swapMyBaseView() {

//       if there are no ranking properites, then there is nothing to do
      <c:choose>
        <c:when test="${empty rankingPropertiesList}">
          // no-op because there are no ranking properties
        </c:when>
        <c:otherwise>
          var checkbox = document.getElementById("useBaseCheckbox");
          var checked = false;
          if (checkbox)
          {
            checked = document.getElementById("useBaseCheckbox").checked;
          }
          var basePWDiv = document.getElementById("readonlyPWFromBase");
          var copyFromDiv = document.getElementById("copyFromAnotherSCDiv");
          var myPWDiv = document.getElementById("editablePWFromThisItem");
          if (checked)
          {
            basePWDiv.style.display="block";
            myPWDiv.style.display="none";
            copyFromDiv.style.display="none";
          }
          else
          {
            basePWDiv.style.display="none";
            myPWDiv.style.display="block";
            copyFromDiv.style.display="block";
          }
        </c:otherwise>
      </c:choose>
    }

  // Store the ID of the currently edited searchConfig in a JavaScript variable.
  var <c:out value="${varCurrentConfigId}"/> = "<c:out value='${requestScope.atgCurrentAsset.asset.repositoryId}'/>";

  //
  // This function is called when the user clicks the "Copy From..." button.
  // It allows the user to use the asset picker to specify a searchConfig
  // from which to copy property weightings.
  //
  function <c:out value="${onClickCopyFromButton}"/>() {

    var allowableTypes = new Array();
    var assetType = new Object();
    assetType.typeCode       = "repository";
    assetType.displayName    = "NotUsed";
    assetType.repositoryName = "<c:out value='${repName}'/>";
    assetType.createable     = "false";

    // The following two properties determine which view is to be displayed in the asset picker.
    // NOTE: the type name needs to exist in the repository but it is not used in our asset picker plugin.
    assetType.typeName       = "<c:out value='${propManager.searchConfigItemName}'/>";
    assetType.componentPath  = "<c:out value='${repPath}'/>";
    allowableTypes[allowableTypes.length] = assetType;

    var viewConfiguration = new Array();

//     get the tree component
    <web-ui:getTree var="treePath"
                    repository="${repPath}"
                    itemType="${propManager.searchConfigItemName}"
                    treeRegistry="${sessionInfo.treeRegistryPath}"/>
    viewConfiguration.treeComponent = "<c:out value='${treePath}'/>";
    viewConfiguration.itemTypes = "<c:out value='${propManager.searchConfigItemName}'/>";

    var encodedItemType = "<c:out value='${propertyView.encodedComponentType}'/>";
    var decodedItemType = encodedItemType.split(":");

    var picker = new AssetBrowser();
    picker.mapMode                 = "AssetManager.assetPicker";
    picker.clearAttributes         = "true";
    picker.pickerURL               = "<c:out value='${assetManagerConfig.assetPickerRoot}${assetManagerConfig.assetPicker}'/>?apView=1&";
    <fmt:message var="assetPickerTitle" bundle="${assetManagerBundle}" key="propertyEditor.assetPickerTitle"/>
    <web-ui:encodeParameterValue var="assetPickerTitle" value="${assetPickerTitle}"/>
    picker.assetPickerTitle        = "<c:out value='${assetPickerTitle}'/>";
    picker.browserMode             = "pick";
    picker.createMode              = "none";
    picker.onSelect                = "<c:out value='${onCopyFromOK}'/>";
    picker.closeAction             = "hide";
    picker.assetTypes              = allowableTypes;
    picker.assetPickerParentWindow = window;
    picker.assetPickerFrameElement = parent.document.getElementById("iFrame");
    picker.assetPickerDivId        = "browser";
    picker.isAllowMultiSelect      = "false";
    picker.defaultView             = "Browse";
    picker.viewConfiguration       = viewConfiguration;
    picker.invoke();
  }

  //
  // This function is called when the user makes a selection from the asset
  // picker that is displayed when the "Copy From..." button is clicked.
  //
  // @param  selected  An object containing info about the selected asset
  //
  function <c:out value="${onCopyFromOK}"/>(selected) {
    if (selected.id == <c:out value="${varCurrentConfigId}"/>) {
      alert("<fmt:message key='propertyWeighting.noCopyFromCurrentConfig'/>");
    }
    else {
      markAssetModified();
      document.getElementById("<c:out value='${copyFromSourceInputId}'/>").value = selected.uri;
      document.getElementById("<c:out value='${copyFromSubmitId}'/>").click();
    }
  }


  function setSaveButtonEnabled(enable) {
      var saveButton = document.getElementById('saveButtonLink');
      if (null == saveButton) {
        return;
      }
      saveButton.onclick = function() { return enable; };
      if (enable) {
        saveButton.title = "<fmt:message key="propertyWeighting.saveButton.title.save"/>";
        saveButton.className = "button";
      } else {
        saveButton.title = "<fmt:message key="propertyWeighting.saveButton.title.errorsOnThePage"/>";
        saveButton.className = "button disabled";
      }
  }

  </script>


  <%--
      If the rankingPropertiesList is empty, that means that there are no available
      properties (the ranking properties are created automatically behind the scenes).
      Show a helpful message to the user if there are none of these.
  --%>
  <c:choose>

    <%--show this message and nothing more--%>
    <c:when test="${empty rankingPropertiesList}">
      <fmt:message key="propertyWeighting.noAvailableRankingProperties" />
    </c:when>

    <%--show the whole form--%>
    <c:otherwise>

      <%--
          Get the baseSearchConfig, and if not null, give the opportunity to inherit values from it.
      --%>
      <dspel:getvalueof var="baseSearchConfigId"
          bean="${requestScope.formHandlerPath}.value.baseSearchConfig.repositoryId"/>
      <c:if test="${baseSearchConfigId != null}">
        <div style="padding: 10px">
          <dspel:input type="checkbox" bean="${requestScope.formHandlerPath}.value.useBaseRankingProperties"
              onclick="submitPropertyWeightingCheck()" disabled="${!requestScope.atgIsAssetEditable}"
              id="useBaseCheckbox"/>
          <fmt:message key="propertyWeighting.useBaseSearchConfiguration" />
          <dspel:getvalueof var="baseConfigName"
              bean="${requestScope.formHandlerPath}.value.baseSearchConfig.displayName"/>
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

      <%--
          Display the "Copy From" button unless we are inherting weightings from
          our base searchConfig, and if were not in a read only situation
      --%>
      <c:if test="${requestScope.atgIsAssetEditable}">
        <div id="copyFromAnotherSCDiv">
          <!--FIXME: Add a style-->
          <div style="padding: 10px">
            <a href="<c:out value='javascript:${onClickCopyFromButton}()'/>" class="buttonSmall">
              <fmt:message key="propertyWeighting.copyFromSearchConfiguration"/>
            </a>
          </div>
        </div>
      </c:if>


        <div id="readonlyPWFromBase">

          <c:set var="baseRankingValues" value="${formHandler.resultPrioritizationValuesForBase}" />

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
              <tbody>


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
                      <a href="javascript:drillDownFunc('<c:out value="${rankPropURI}"/>');">
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
                <td colspan="2">
                  <span>100%</span>
                </td>
              </tr>
            </tfoot>
          </table>

      </div><%-- readonlyPWFromBase --%>

      <div id="editablePWFromThisItem">

      <style type="text/css" media="screen">
        #propPriorityForm input{display: block; max-width: 100px;}
      </style>

      <script type="text/javascript" charset="utf-8">
        dojo.require("dijit._Widget");
        dojo.require("dijit._Templated");
        dojo.require("dijit._Container");
        dojo.require("dijit.form.Slider");

        // scan page for widgets and instantiate them
        dojo.require("dojo.parser");

        // Register  widget namespace
        dojo.registerModulePath("atg.widget.propPriority", "/AssetUI-Search/javascript/dijit/propPriority");
        dojo.require("atg.widget.propPriority");
        dojo.require("atg.widget.propPriority.allotter");
        dojo.require("atg.widget.propPriority.weightSetter");
      </script>

      <dspel:include page="/assetEditor/property/propertyPrioritizationJSON.jsp"/>

      <script type="text/javascript">
        // Hook the contextHelp creation to the page load
        dojo.addOnLoad(function() {
          propPriority = new atg.widget.propPriority.allotter(
            {
                ppData: atg.ppData,
                doOnError: function() {
                  setSaveButtonEnabled(false);
                },
                doOnSuccess: function() {
                  setSaveButtonEnabled(true);
                },
                i18n:{
                  "ignoreText":"<fmt:message key='propertyWeightingWidget.ignoreText'/>",
                  "errorZero":"<fmt:message key='propertyWeightingWidget.errorZero'/>",
                  "errorNoWeights":"<fmt:message key='propertyWeightingWidget.errorNoWeights'/>",
                  "errorNoPropertyWeights":"<fmt:message key='propertyWeightingWidget.errorNoPropertyWeights'/>",
                  "errorTotalNo100":"<fmt:message key='propertyWeightingWidget.errorTotalNo100'/>"
                }
            },
            dojo.byId("pp"));
          });
      </script>

      <div id="pp"></div>

      <table id="atg_smerch_propWeightAllocation" class="atg_dataTable">
        <thead>
          <tr>
            <th>
              <fmt:message key="propertyWeightingWidget.headerText.property"/>
            </th>
            <th width="200px">
              <fmt:message key="propertyWeightingWidget.headerText.searchResultsWeight"/>
            </th>
            <th width="190px">
              <fmt:message key="propertyWeightingWidget.headerText.relativeWeight"/>
            </th>
            <th>
              <fmt:message key="propertyWeightingWidget.headerText.propertyValueRanking"/>
            </th>
          </tr>
        </thead>

        <tbody id="atg_smerch_propertyWeightSetters"><tr style="visibility:hidden;">
              <td colspan="2">
                <fmt:message key="propertyWeightingWidget.noPrioritizationProperties"/>
              </td>
              <td colspan="2">
                <span style="float: left; width: 5em; text-align:right">0%</span>
              </td>
            </tr>
        </tbody>

        <tbody id="weightTotals">
          <tr>
            <td colspan="2" class="totalLabel">
              <fmt:message key="propertyWeightingWidget.propertyPrioritization"/>:
            </td>
            <td colspan="2" id="propertyTotal">
              <span>0%</span>
              <span class="weightBar"></span>
            </td>
          </tr>
          <tr>
            <td colspan="2" class="totalLabel">
              <fmt:message key="propertyWeightingWidget.searchTermRelevance"/>:
            </td>
            <td colspan="2" id="textRelevanceTotal">
              <span>0%</span>
              <span class="weightBar"></span>
            </td>
          </tr>
          <tr>
            <td colspan="2" class="totalLabel">
              <fmt:message key="propertyWeightingWidget.searchTermPropertyMatch"/>:
            </td>
            <td id="textPropertyMatchTotal">
              <span>0%</span>
              <span class="weightBar"></span>
            </td>
            <td>
              <dspel:getvalueof bean="${requestScope.formHandlerPath}.fieldRankingProperty.repositoryId"
                  var="fieldRankingPropertyRepositoryId"/>
              <asset-ui:createAssetURI
                  var="rankPropURI"
                  componentName="${repName}"
                  itemDescriptorName="${componentItemDescriptor.itemDescriptorName}"
                  itemId="${fieldRankingPropertyRepositoryId}"/>
              <a id="searchTermPropertyMatchLink" href="javascript:drillDownFunc('<c:out value="${rankPropURI}"/>');">
                <fmt:message key="propertyWeightingWidget.rankedListOfSearchablePropertiesLink"/>
              </a>
            </td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="2" class="totalLabel">
              <fmt:message key="propertyWeightingWidget.total"/>
            </td>
            <td colspan="2" id="totalTotal">
              <span>100%</span>
            </td>
          </tr>
        </tfoot>
      </table>

      <input type="hidden" id="weightPropertiesId" value="0"/><br/>

      <dspel:input type="hidden" id="weightFieldId"
      bean="${requestScope.formHandlerPath}.fieldRankingProperty.weighting"/>

      <dspel:input type="hidden" id="weightRelevanceId"
      bean="${requestScope.formHandlerPath}.relevanceRankingProperty.weighting"/>

      <dspel:getvalueof var="rankProperties"
          bean="${propertyView.formHandlerProperty}"/>
      <c:forEach var="rankProperty" items="${rankProperties}" varStatus="status">
        <dspel:getvalueof var="repositoryId"
            bean="${propertyView.formHandlerProperty}[${status.index}].repositoryId"/>
        <dspel:getvalueof var="app"
            bean="${propertyView.formHandlerProperty}[${status.index}].availableRankingProperty.application"/>
        <c:if test="${app ne 'Any'}">
          <dspel:input id="pp_${repositoryId}"
              type="hidden"
              bean="${requestScope.formHandlerPath}.value.rankingProperties[${status.index}].weighting"
              converter="nullable"
              onclick="markAssetModified();"/>
        </c:if>
      </c:forEach>
    </div> <%-- editablePWFromThisItem --%>

    </c:otherwise>
  </c:choose>



    <script type="text/javascript">
      swapMyBaseView();
    </script>


</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/searchConfigPropertyWeightingEditor.jsp#2 $$Change: 651448 $--%>
