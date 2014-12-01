<%--
  component property editor for property value ranks

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/rangeMethodEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>

  <dspel:getvalueof var="valueType" bean="${requestScope.formHandlerPath}.value.availableRankingProperty.valueType"/>
  <dspel:getvalueof var="dataType" bean="${requestScope.formHandlerPath}.value.dataType"/>

  <c:set var="id" value="${requestScope.uniqueAssetID}${propertyView.uniqueId}"/>
  <c:set var="highToLowId" value="highToLow_${id}"/>
  <c:set var="lowToHighId" value="lowToHigh_${id}"/>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set var="setupCalendarFunc"     value="setupCalendar_${id}"/>

  <dspel:importbean var="assetManagerConfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle var="assetManagerBundle" basename="${assetManagerConfig.resourceBundle}"/>

  <%-- PUT THE FOLLOWING SECTION IN A FRAGMENT --%>
  
  <c:set var="sessionInfo" value="${assetManagerConfig.sessionInfo}"/>

  <dspel:importbean var="propManager" bean="/atg/search/repository/SearchConfigurationPropertyManager"/>

  <c:set var="repository" value="${requestScope.atgCurrentAsset.asset.repository}"/>

  <%-- Derive IDs for some form elements and data objects --%>
  <c:set var="varCurrentConfigId"    value="varCurrentConfigId_${id}"/>
  <c:set var="onClickCopyFromButton" value="onClickCopyFromButton_${id}"/>
  <c:set var="onCopyFromOK"          value="onCopyFromOK_${id}"/>
  <c:set var="copyFromSourceInputId" value="copyFromSourceInput_${id}"/>
  <c:set var="copyFromSubmitId"      value="copyFromSubmit_${id}"/>

  <%-- A hidden input that is used to pass the selected source config ID to
       the handleCopyRankings method --%>
  <dspel:input type="hidden" id="${copyFromSourceInputId}"
               bean="${requestScope.formHandlerPath}.sourceConfig"/>

  <%-- A hidden submit button that is programmatically clicked in order to
       invoke the handleCopyRankings method --%>
  <dspel:input type="submit" style="display:none" id="${copyFromSubmitId}"
               bean="${requestScope.formHandlerPath}.copyRankings"/>

  <script type="text/javascript">

    // Store the ID of the currently edited searchConfig in a JavaScript variable.
    // TBD: Can't do this, since we are editing a rankingProperty, not a searchConfig.
    // We may have to go "up the context" to find the searchConfig.
    //var <c:out value="${varCurrentConfigId}"/> = "<c:out value='${requestScope.atgCurrentAsset.asset.repositoryId}'/>";
    var <c:out value="${varCurrentConfigId}"/> = "___TBD___";

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
      assetType.repositoryName = "<c:out value='${repository.repositoryName}'/>";
      assetType.createable     = "false";

      // The following two properties determine which view is to be displayed in the asset picker.
      // NOTE: the type name needs to exist in the repository but it is not used in our asset picker plugin.
      assetType.typeName       = "<c:out value='${propManager.searchConfigItemName}'/>";
      assetType.componentPath  = "<c:out value='${repository.absoluteName}'/>";
      allowableTypes[allowableTypes.length] = assetType;

      var viewConfiguration = new Array();

      <%-- get the tree component --%>
      <web-ui:getTree var="treePath"
                      repository="${repository.absoluteName}"
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
        alert("<fmt:message key='propertyValueRankingEditor.noCopyFromCurrentConfig'/>");
      }
      else {
        markAssetModified();
        document.getElementById("<c:out value='${copyFromSourceInputId}'/>").value = selected.uri;
        document.getElementById("<c:out value='${copyFromSubmitId}'/>").click();
      }
    }
    
  </script>

  <%-- Display the "Copy From" button --%>    
  <%-- FIXME: Add a style --%>
  <div style="padding: 10px">
    <a href="<c:out value='javascript:${onClickCopyFromButton}()'/>" class="buttonSmall">
      <fmt:message key="propertyWeighting.copyFromSearchConfiguration"/>
    </a>
  </div>

  <%-- END OF SECTION THAT SHOULD BE MOVED TO A FRAGMENT --%>

  <%-- if its a date, set the tag converter which goes inside the dspel:input inside colletionEditor.jsp --%>
  <fmt:message var="dateFormat" bundle="${assetManagerBundle}" key="timestampEditor.dateFormat"/>  
  <c:if test="${dataType == 'date'}">
    <c:set var="dateConverter" value="DateRange"/> 
    <c:set var="dateConverterAttributes" value="daterange=${dateFormat}"/> 
  </c:if>

  <c:if test="${valueType == 'range'}">
 
    <script type="text/javascript">

      function markRangeModified(field) {
        markAssetModified();
        <c:choose>
          <c:when test="${dataType == 'integer' or dataType == 'float'}">
            return isValidNumber(field, <c:out value="${dataType == 'float'}"/>);
          </c:when>
          <c:otherwise>
            return true;
          </c:otherwise>
        </c:choose>
      }
      
      //
      // when the rangeMethod radio buttons are selected, hide/show the blocks below them.
      //
      function changeRangeMethod(range) {
       
        var highToLowSpan = document.getElementById("<c:out value='${highToLowId}'/>");
        var lowToHighSpan = document.getElementById("<c:out value='${lowToHighId}'/>");

        if (range == 'high-to-low') {
          highToLowSpan.style.display = "block";
          lowToHighSpan.style.display = "none";
          if (window['valueRanks_info'] != undefined) {
            valueRanks_info.setVisibility(false);
          }
        } 

        else if (range == 'low-to-high') {
          highToLowSpan.style.display = "none";
          lowToHighSpan.style.display = "block";
          if (window['valueRanks_info'] != undefined) {
            valueRanks_info.setVisibility(false);
          }
        } 

        else if (range == 'manual') {
           highToLowSpan.style.display = "none";
          lowToHighSpan.style.display = "none";
          if (window['valueRanks_info'] != undefined) {
            valueRanks_info.setVisibility(true);
          }
        }
      }

      dojo.require("dijit.form.DateTextBox");

      // set up the calendar widget for the id  passed in.
      function <c:out value="${setupCalendarFunc}"/>(pId) {
        var field = dojo.byId(pId);
        var name = field.name;
        var value = field.value;
        new dijit.form.DateTextBox({
          constraints: { datePattern: '<c:out value="${dateFormat}"/>' },
          onChange: markAssetModified
        }, field);
        
        field = dojo.byId(pId);
        field.name = name;
        field.value = value;
      }
  <c:choose>
    <c:when test="${dataType == 'date'}">
      registerOnLoad(function() {
        setupCalendar_<c:out value='${id}'/>("lowToHighMin_<c:out value='${id}'/>");
        setupCalendar_<c:out value='${id}'/>("lowToHighMax_<c:out value='${id}'/>");
        setupCalendar_<c:out value='${id}'/>("highToLowMin_<c:out value='${id}'/>");
        setupCalendar_<c:out value='${id}'/>("highToLowMax_<c:out value='${id}'/>");
      });
    </c:when>
    <c:when test="${dataType == 'integer' or dataType == 'float'}">
      /**
        * Check for valid number.(JJ Javascript)
        */
      function isValidNumber(pTextFieldName, pIsInteger) {
        var textFieldName = pTextFieldName;
        var booleanValidValues = true;
        var PATTERN_NON_NUMERIC = "[^0-9.]";
        if (pIsInteger) {
          PATTERN_NON_NUMERIC = "[^0-9]";
        }

         // Validating the values in text fields.
        if (textFieldName.value == '' ||
           ((textFieldName.value).match(PATTERN_NON_NUMERIC) != null) || textFieldName.value > 2147483647) {

           parent.messages.addError("<fmt:message key='propertyValueRankingEditor.pleaseEnterNumber'/>");

           booleanValidValues = false;
        }

        if (!booleanValidValues) {
          textFieldName.select();
          return false;
        }

        return booleanValidValues;
      }
    </c:when>
  </c:choose>
    </script>

    <dspel:getvalueof var="rangeMethod" bean="${requestScope.formHandlerPath}.value.rangeMethod"/>       

    <c:set var="lowHighStyle" value="none"/>
    <c:if test="${rangeMethod == 'low-to-high'}">
      <c:set var="lowHighStyle" value="block"/>
    </c:if>
    <c:set var="highLowStyle" value="none"/>
    <c:if test="${rangeMethod == 'high-to-low'}">
      <c:set var="highLowStyle" value="block"/>
    </c:if>

    <table>
      <tr>
        <td colspan="2">
          <dspel:input type="radio" onclick="changeRangeMethod('low-to-high');markAssetModified()" 
                       bean="${requestScope.formHandlerPath}.value.rangeMethod" value="low-to-high"/>
          <c:choose>
            <c:when test="${dataType == 'date'}">
              <fmt:message key='rangeMethodEditor.dateLowToHighRangeMethodText'/>
            </c:when>
            <c:otherwise>
              <fmt:message key='rangeMethodEditor.numericLowToHighRangeMethodText'/>
            </c:otherwise>
          </c:choose>
         </td>     
      </tr>

      <tr>
        <td>
          <span id="<c:out value='${lowToHighId}'/>" style="display:<c:out value='${lowHighStyle}'/>" >
            <table>
              <tr>
                <td style="text-align: right;">
                  <c:choose>
                    <c:when test="${dataType == 'date'}">
                      <fmt:message key='rangeMethodEditor.dateMaxLabel'/>
                    </c:when>
                    <c:otherwise>
                      <fmt:message key='rangeMethodEditor.numericMaxLabelLowToHigh'/>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td style="text-align: left;">
                  <dspel:input type="text" id="lowToHighMax_${id}"
                      bean="/atg/search/web/assetmanager/PropertyRankingFormHandler.lowToHighMax" size="10"
                      converter="${dateConverter}" converterattributes="${dateConverterAttributes}"
                      onchange="return markRangeModified(this)"
                      oninput="markAssetModified()"/>
                </td>
              </tr>
              <tr>
                <td style="text-align: right;">
                  <c:choose>
                    <c:when test="${dataType == 'date'}">
                      <fmt:message key='rangeMethodEditor.dateMinLabel'/>
                      <fmt:message var="imgAlt" bundle="${assetManagerBundle}" key="dateEditor.calendarIconText"/>
                    </c:when>
                    <c:otherwise>
                      <fmt:message key='rangeMethodEditor.numericMinLabelLowToHigh'/>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td style="text-align: left;">
                  <dspel:input type="text" id="lowToHighMin_${id}"
                      bean="/atg/search/web/assetmanager/PropertyRankingFormHandler.lowToHighMin" size="10"
                      converter="${dateConverter}" converterattributes="${dateConverterAttributes}"
                      onchange="return markRangeModified(this)"
                      oninput="markAssetModified()"/>
                </td>
              </tr>
            </table>
	        </span>
        </td>
      </tr>

      <tr>
        <td colspan="2">
	        <dspel:input type="radio" onclick="changeRangeMethod('high-to-low');markAssetModified()"
              bean="${requestScope.formHandlerPath}.value.rangeMethod" value="high-to-low"/>
          <c:choose>
            <c:when test="${dataType == 'date'}">
              <fmt:message key='rangeMethodEditor.dateHighToLowRangeMethodText'/>
            </c:when>
            <c:otherwise>
              <fmt:message key='rangeMethodEditor.numericHighToLowRangeMethodText'/>
            </c:otherwise>
          </c:choose>
        </td>
      </tr>

      <tr>
        <td>
	        <span id="<c:out value='${highToLowId}'/>"  style="display:<c:out value='${highLowStyle}'/>" >
            <table border="0">
              <tr>
                <td style="text-align: right;">
                  <c:choose>
                   <c:when test="${dataType == 'date'}">
                     <fmt:message key='rangeMethodEditor.dateMinLabel'/>
                   </c:when>
                   <c:otherwise>
                     <fmt:message key='rangeMethodEditor.numericMinLabelHighToLow'/>
                   </c:otherwise>
                 </c:choose>
                </td>
                <td style="text-align: left;">
                  <dspel:input id="highToLowMin_${id}"
                      type="text" bean="/atg/search/web/assetmanager/PropertyRankingFormHandler.highToLowMin" size="10"
                      converter="${dateConverter}" converterattributes="${dateConverterAttributes}"
                      onchange="return markRangeModified(this)"
                      oninput="markAssetModified()"/>
                </td>
              </tr>
              <tr>
                <td style="text-align: right;">
                  <c:choose>
                   <c:when test="${dataType == 'date'}">
                     <fmt:message key='rangeMethodEditor.dateMaxLabel'/>
                   </c:when>
                   <c:otherwise>
                     <fmt:message key='rangeMethodEditor.numericMaxLabelHighToLow'/>
                   </c:otherwise>
                 </c:choose>
                </td>
                <td style="text-align: left;">
                  <dspel:input id="highToLowMax_${id}"
                     type="text" bean="/atg/search/web/assetmanager/PropertyRankingFormHandler.highToLowMax" size="10"
                     converter="${dateConverter}" converterattributes="${dateConverterAttributes}"
                     onchange="return markRangeModified(this)"
                     oninput="markAssetModified()" />
                </td>
              </tr>
            </table>
            </span>
         </td>
      </tr>

      <tr>
        <td>
          <dspel:input type="radio" onclick="changeRangeMethod('manual');markAssetModified()"
              bean="${requestScope.formHandlerPath}.value.rangeMethod" value="manual"/>
           <c:choose>
             <c:when test="${dataType == 'date'}">
               <fmt:message key='rangeMethodEditor.dateManualRangeMethodText'/>
             </c:when>
             <c:otherwise>
               <fmt:message key='rangeMethodEditor.numericManualRangeMethodText'/>
             </c:otherwise>
           </c:choose>
        </td>
      </tr>
    </table>

  </c:if>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/rangeMethodEditor.jsp#2 $$Change: 651448 $--%>
