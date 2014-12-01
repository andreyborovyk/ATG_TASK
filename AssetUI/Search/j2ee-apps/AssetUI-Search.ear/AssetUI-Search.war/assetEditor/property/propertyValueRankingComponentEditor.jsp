<%--
  component property editor for property value ranks

  The following request-scoped variables are expected to be set:

  @param  mpv          A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/propertyValueRankingComponentEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"              uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"            uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="asset-ui"       uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search"        %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramInitialize"     param="initialize"/>
  <dspel:getvalueof var="paramRenderHeader"   param="renderHeader"/>
  <dspel:getvalueof var="paramValueId"        param="valueId"/>
  <dspel:getvalueof var="paramValueIdPrefix"  param="valueIdPrefix"/>
  <dspel:getvalueof var="paramKeyId"          param="keyId"/>
  <dspel:getvalueof var="paramKeyIdPrefix"    param="keyIdPrefix"/>
  <dspel:getvalueof var="paramRowId"          param="rowId"/>
  <dspel:getvalueof var="paramRowIdPrefix"    param="rowIdPrefix"/>
  <dspel:getvalueof var="paramDeleteId"       param="deleteId"/>
  <dspel:getvalueof var="paramDeleteIdPrefix" param="deleteIdPrefix"/>
  <dspel:getvalueof var="paramInfoObj"        param="infoObj"/>
  <dspel:getvalueof var="paramIndex"          param="index"/>
  <dspel:getvalueof var="paramTemplate"       param="template"/>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:importbean var="assetManagerConfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle var="assetManagerBundle" basename="${assetManagerConfig.resourceBundle}"/>

  <c:set var="id" value="${uniqueAssetID}${propertyView.uniqueId}"/>
  <c:set var="indicatorCellIdPrefix"  value="${id}_indicatorCellId_"/>
  <c:set var="sortName" value="${id}_sort"/>

  <dspel:getvalueof var="dataType" bean="${requestScope.formHandlerPath}.value.dataType"/>
  <dspel:getvalueof var="valueType" bean="${requestScope.formHandlerPath}.value.availableRankingProperty.valueType"/>
  <dspel:getvalueof var="propertyName" bean="${requestScope.formHandlerPath}.value.availableRankingProperty.propertyName"/>

  <c:set var="sessionInfo" value="${assetManagerConfig.sessionInfo}"/>
  <dspel:importbean var="assetEditor" bean="${sessionInfo.assetEditorViewPath}"/>
  <c:set var="inheritBaseConfigReadOnly" value="false"/>
  <c:if test="${assetEditor.size > 1}">
    <c:set var="drillUpAssetContext" value="${assetEditor.assetContexts[assetEditor.size -2]}"/>
    <asset-ui:resolveAsset var="drillUpAsset" uri="${drillUpAssetContext.assetURI}"/>
    <dspel:getvalueof var="parentConfig" bean="${requestScope.formHandlerPath}.parentSearchConfig"/>
    <c:if test="${parentConfig != null}">
      <c:set var="drillUpAssetId" value="${drillUpAsset.asset.repositoryId}"/>
      <c:set var="parentAssetId"  value="${parentConfig.repositoryId}"/>
      <c:if test="${drillUpAssetId != parentAssetId}">
        <c:set var="inheritBaseConfigReadOnly" value="true"/>
      </c:if>
    </c:if>
  </c:if>

  <c:choose>

    <%--
                                           INITIALIZE

         This section is only included once by the collection editor, before the component rows are rendered
                                                                                                              --%>
    <c:when test="${paramInitialize}">

       <c:choose>
         <c:when test="${requestScope.atgIsAssetEditable && inheritBaseConfigReadOnly == 'false'}">
           <dspel:include otherContext="${propertyView.componentContextRoot}" page="/assetEditor/property/rangeMethodEditor.jsp"/>
         </c:when>
         <c:otherwise>
           <dspel:include otherContext="${propertyView.componentContextRoot}" page="/assetEditor/property/rangeMethodViewer.jsp"/>
         </c:otherwise>
       </c:choose>

       <c:set var="transferComponentUserValuesFunc" value="${id}_transCompUserValues"/>
       <c:set var="transferComponentOriginalValuesFunc" value="${id}_transCompOriginalValues"/>
       <c:set var="onAddRowFunc"  value="${id}_onAddRow"/>
       <c:set var="setupCalendarFunc"  value="${id}_setupCalendar"/>

       <%-- the initial state of this property editor is hidden, unless the rangeMethod is 'manual' --%>
       <dspel:getvalueof var="rangeMethod" bean="${requestScope.formHandlerPath}.value.rangeMethod"/>
       <c:if test="${valueType == 'range' && rangeMethod != 'manual'}">
         <c:set scope="request" var="atgCollectionEditorVisibility" value="none"/>
       </c:if>

       <%-- if its a date, set the tag converter which goes inside the dspel:input inside colletionEditor.jsp --%>
       <fmt:message var="dateFormat" bundle="${assetManagerBundle}" key="timestampEditor.dateFormat"/>
       <c:if test="${dataType == 'date'}">
         <c:set scope="request" var="atgCollectionInputConverter" value="DateRange"/>
         <c:set scope="request" var="atgCollectionInputConverterAttributes" value="daterange=${dateFormat}"/>
       </c:if>

       <script type="text/javascript">

        // Since we need to allow arbitrary ranking values, we can't use the
        // default collection editor sort function, which uses uniform sorting.
        // So we override the function locally.
        <c:out value="${paramInfoObj}"/>.sortRows = function() {

          var totalCount = <c:out value="${paramInfoObj}"/>.getRowCount();
          for (var i = 0; i < totalCount; i++) {
            var keyInput = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i);
            if (keyInput.value != "All other values") {
              <%--var vals = document.getElementById("select_<c:out value='${paramKeyIdPrefix}'/>" + i);--%>
              <%--if (null == vals) {--%>
                <%--continue;--%>
              <%--}--%>
              <%--vals.value = keyInput.value;--%>
            } else {
              var keySpan = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i + "_keySpan");
              var keySpanFrom = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i + "_keySpanFrom");
              var keySpanTo = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i + "_keySpanTo");
              var allOthersSpan = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i + "_allOthersSpan");
              if (keySpan) {
                keySpan.style.display="none";
              }
              if (keySpanFrom) {
                keySpanFrom.style.display="none";
              }
              if (keySpanTo) {
                keySpanTo.style.display="none";
              }
              allOthersSpan.style.display="block";

              var deleteSpan = document.getElementById("<c:out value='${paramDeleteIdPrefix}'/>" + i);
              if (deleteSpan) {
                deleteSpan.style.display="none";
              }
            }
          }

          // Create an array of objects containing info about the current index
          // and requested rank for each value.
          var sortNameArray = document.getElementsByName("<c:out value='${sortName}'/>");
          var sortInfos = new Array();
          for (var i = 0; i < sortNameArray.length; i++) {
            var valueInput = sortNameArray[i];
            var sortInfo = new Object();
            sortInfo.index = i;
            sortInfo.requestedRank = parseInt(valueInput.value);
            sortInfos[i] = sortInfo;
          }

          // Sort the array by requested rank.
          sortInfos.sort(function(a, b) {
            var aloc = a.requestedRank;
            var bloc = b.requestedRank;
            if (aloc < bloc)
              return -1;
            if (aloc > bloc)
              return 1;
            return 0;
          });

          // Rebuild the table using the new sequence of indices.
          var indices = new Array();
          for (var i = 0; i < sortNameArray.length; i++) {
            indices[i] = sortInfos[i].index;
          }
          <c:out value="${paramInfoObj}"/>.rearrangeTable(indices);

          <c:if test="${dataType == 'date'}">
            function updateWidgetDOMNode(id) {
              var w = dijit.byId(id);
              if (w) {
                w.srcNodeRef = w.textbox = w.focusNode = dojo.byId(id);
                w.domNode = dojo.byId(w.domNode.id);
              }
            }
            for (var i = 0; i < totalCount; i++) {
              <c:choose>
                <c:when test="${valueType == 'range'}">
                  updateWidgetDOMNode("<c:out value='${paramKeyIdPrefix}'/>" + i + "_from");
                  updateWidgetDOMNode("<c:out value='${paramKeyIdPrefix}'/>" + i + "_to");
                </c:when>
                <c:otherwise>
                  updateWidgetDOMNode("<c:out value='${paramKeyIdPrefix}'/>" + i);
                </c:otherwise>
              </c:choose>
            }
          </c:if>

          // Find the min and max rank values.
          var maxRank = -1;
          var minRank = -1;
          sortNameArray = document.getElementsByName("<c:out value='${sortName}'/>");
          for (var i=0; i < sortNameArray.length; i++) {
            var sortInput = sortNameArray[i];
            var rankValue = parseInt(sortInput.value);
            if ( minRank == -1 || rankValue < minRank )
              minRank = rankValue;
            if ( maxRank == -1 || rankValue > maxRank)
              maxRank = rankValue;
          }

          // Set the appropriate color grade on the good/better/best indicator cell.
          var redBest=36;   // 'Best' color
          var greenBest=125;
          var blueBest=202;

          var redGood=230;   // 'Good' color
          var greenGood=240;
          var blueGood=249;

          var span = maxRank - minRank;

          // loop through each indicator cell and set the color and class.
          var bestCell = null;
          var goodCell = null;
          var currentRank = null;
          for (var i=0; i < sortNameArray.length; i++) {
            var sortInput = sortNameArray[i];
            var rowid = sortInput.id;
            var index = rowid.replace(new RegExp("<c:out value='${paramValueIdPrefix}'/>"), "");
            var rankValue = parseInt(sortInput.value,10);
            var indCell = document.getElementById("<c:out value='${indicatorCellIdPrefix}'/>" + index);
            if (indCell && rankValue == minRank && !bestCell) {
              bestCell = indCell;
            }
            else if (indCell && rankValue == maxRank) {
              goodCell = indCell;
            }
            if (indCell) {
              indCell.className = "atg_smerch_rankIndicator";
              var rankFraction;
              if (span != 0.0)
                rankFraction = (rankValue - minRank)/span;
              else
                rankFraction = 0.0;
              indCell.style.background=getGradientColor(redBest,greenBest,blueBest,rankFraction, redGood, greenGood, blueGood);
              indCell.innerHTML = "";
              var rowTr = document.getElementById("<c:out value='${paramRowIdPrefix}'/>" + index);
              if (rowTr) {
                if (currentRank && currentRank == rankValue) {
                  rowTr.className = "atg_combinedRow";
                } else {
                  rowTr.className = "";
                }
              }
              currentRank = rankValue;
            }
          }
          if (bestCell) {
            bestCell.className = "atg_smerch_rankIndicatorBest";
            bestCell.innerHTML = "<fmt:message key='propertyValueRankingEditor.best'/>";
          }
          if (goodCell) {
             goodCell.className = "atg_smerch_rankIndicatorGood";
             goodCell.innerHTML = "<fmt:message key='propertyValueRankingEditor.good'/>";
          }

          for (var i = 0; i < totalCount; i++) {
            var keyInput = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i);
            if ("All other values" == keyInput.value) {
              continue;
            }
            var vals = document.getElementById("select_<c:out value='${paramKeyIdPrefix}'/>" + i);
            if (vals != null) {
              vals.value = keyInput.value;
            }
          }
        }


         //
         // This function is called after the transferOriginalValues method of collectionEditor.js.
         // It will hide/show the 'all other values' label.
         // And if its a range, it will split apart the hidden key field into the 'from' and 'to' fields.
         //
         function <c:out value="${transferComponentOriginalValuesFunc}"/>() {
           var totalCount = <c:out value="${paramInfoObj}"/>.getRowCount();

           for (var i=0; i < totalCount; i++) {
             var keyInput = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i);
             if (keyInput.value == "All other values") {
               var keySpan = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i + "_keySpan");
               var keySpanFrom = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i + "_keySpanFrom");
               var keySpanTo = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i + "_keySpanTo");
               var allOthersSpan = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i + "_allOthersSpan");
               if (keySpan) {
                 keySpan.style.display="none";
               }
               if (keySpanFrom) {
                 keySpanFrom.style.display="none";
               }
               if (keySpanTo) {
                 keySpanTo.style.display="none";
               }
               allOthersSpan.style.display="block";

               var deleteSpan = document.getElementById("<c:out value='${paramDeleteIdPrefix}'/>" + i);
               if (deleteSpan) {
                  deleteSpan.style.display="none";
               }

               return;
              } else {
                var vals = document.getElementById("select_<c:out value='${paramKeyIdPrefix}'/>" + i);
                if (vals != null) {
                  vals.value = keyInput.value;
                }
              }

              <c:if test="${((dataType == 'enum') && (not (propertyName eq '$field')))
                  || (dataType == 'boolean')
                  || (!requestScope.atgIsAssetEditable)
                  || (inheritBaseConfigReadOnly =='true')}">
                var deleteSpan = document.getElementById("<c:out value='${paramDeleteIdPrefix}'/>" + i);
                if (deleteSpan) {
                   deleteSpan.style.display="none";
                }
              </c:if>

             var fromInput = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i + "_from");
             var toInput = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i + "_to");
             if (fromInput && toInput) {
               var fromTo = keyInput.value.split('-');
               fromInput.value = fromTo[0];
               if (fromTo[1] != undefined) {
                 toInput.value = fromTo[1];
               }
             }

             var valueInput = document.getElementById("<c:out value='${paramValueIdPrefix}'/>" + i);
             if (valueInput.value != null && valueInput.value.replace(/^\s+|\s+$/g, '') == '0')
               valueInput.value = '';
           }
         }


        function <c:out value="${onAddRowFunc}"/>(index) {
           <c:if test="${(dataType == 'enum')
              && (propertyName eq '$field')}">
            var input = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + index);
            var vals = document.getElementById("select_<c:out value='${paramKeyIdPrefix}'/>" + index);
            input.value = vals.value;
           </c:if>
          <c:if test="${dataType == 'date'}">
            <c:choose>
              <c:when test="${valueType == 'range'}">
                <c:out value='${id}'/>_setupCalendar("<c:out value='${paramKeyIdPrefix}'/>" + index + "_from");
                <c:out value='${id}'/>_setupCalendar("<c:out value='${paramKeyIdPrefix}'/>" + index + "_to");
             </c:when>
             <c:otherwise>
                <c:out value='${id}'/>_setupCalendar("<c:out value='${paramKeyIdPrefix}'/>" + index);
              </c:otherwise>
            </c:choose>
          </c:if>
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

      //----------------------------------------------------------------------------//
      /**
        * validate Field based on the dataType selected. (JJ Javascript)
        */
      function validateField(pObj) {
        var dataType = '<c:out value="${dataType}"/>';
        var bFlag = true;
        if (dataType == 'date') {
          // this is done inside converter (on save)
        } else if (dataType == 'float' ) {
          bFlag = isValidNumber(pObj, false);
        } else if (dataType == 'integer') {
          bFlag = isValidNumber(pObj, true);
        }
        return bFlag;
      }

      //----------------------------------------------------------------------------//
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

        function onFieldKeyPress(pEvent) {

          // Determine which key was pressed.
          if (pEvent == null)
            pEvent = event;
          var charCode = pEvent.charCode;
          if (charCode == null || charCode == 0)
            charCode = pEvent.keyCode;
          if (charCode == null)
            charCode = pEvent.which;

          var target = null;
          if (pEvent.srcElement) {
            target = pEvent.srcElement;
          } else if (pEvent.target) {
            target = pEvent.target;
          }
          if (!target) {
            return false;
          }

         var currentValue = target.value;
         if ((("" == currentValue)
            || (null == currentValue))
            && (48 == charCode)) {
           return false;
         }

          // Don't allow keys that represent text other than digits.
          if (!pEvent.ctrlKey && !pEvent.altKey && charCode > 31 && (charCode < 48 || charCode > 57)) {
            return false;
          }

          // Always return false, because the Enter key submits the form on Firefox.
          if (charCode == 13 || charCode == 3) {
            return false;
          }

          markAssetModified();
          return true;
        }


        function onFieldKeyUp(pEvent) {
          // Determine which key was pressed.
          if (pEvent == null)
            pEvent = event;

          var target = null;
          if (pEvent.srcElement) {
            target = pEvent.srcElement;
          } else if (pEvent.target) {
            target = pEvent.target;
          }
          if (!target) {
            return false;
          }

         var currentValue = target.value;
         if ((("" == currentValue)
            || (null == currentValue)
            || (0 == parseInt(currentValue)))) {
           target.value = "";
         }

          return true;
        }


          function updateStrengthValue() {
            var elems = document.getElementsByTagName('input');
            var totalElems = elems.length;
            var maxRankValue = getMaxRankValue();
            for (var elemNo = 0; elemNo < totalElems; elemNo++) {
              var elem = elems[elemNo];
              if (('undefined' == elem.getAttribute('fooId'))
                  || (null == elem.getAttribute('fooId'))) {
                continue;
              }
              var rankValue = parseInt(elem.value);
              if (isNaN(rankValue)) {
                rankValue = 0;
              }
              var strengthValue = 100 - (100 / maxRankValue) * (rankValue - 1);
              if (isNaN(strengthValue)) {
                strengthValue = 0;
              }
              var targetOutputId = elem.getAttribute('fooId');
              var targetOutput = document.getElementById(targetOutputId);
              var textNode;
              if ((0 == targetOutput.childNodes)) {
                textNode = document.createTextNode("");
                targetOutput.appendChild(textNode);
              } else {
                textNode = targetOutput.childNodes[0];
              }
              textNode.nodeValue = '' + Math.round(strengthValue);
            }
          }


          function getMaxRankValue() {
            var elems = document.getElementsByTagName('input');
            var totalElems = elems.length;
            var maxRankValue = null;
            for (var elemNo = 0; elemNo < totalElems; elemNo++) {
              var elem = elems[elemNo];
              if (('undefined' == elem.getAttribute('fooId'))
                  || (null == elem.getAttribute('fooId'))) {
                continue;
              }
              var rankValue = parseInt(elem.value);
              if (isNaN(rankValue)) {
                rankValue = 0;
              }
              if ((null == maxRankValue)
                || (maxRankValue < rankValue)) {
                maxRankValue = rankValue;
              }
            }

            return maxRankValue;
          }

        <c:out value="${paramInfoObj}"/>.onAddRow = <c:out value='${onAddRowFunc}'/>;

         registerOnLoad(function() {
            <c:out value="${paramInfoObj}"/>.sortRows();
            <c:out value="${transferComponentOriginalValuesFunc}"/>();
           updateStrengthValue();
         });


            //
            // This function is called just before the transferUserValues method of collectionEditor.js.
            // It will combine the 'from' and 'to' fields into the key field which is used by the transferUserValues method.
            // Unless its the 'all other values' position, then do nothing.
            //
            function <c:out value="${transferComponentUserValuesFunc}"/>() {
              var totalCount = <c:out value="${paramInfoObj}"/>.getRowCount();
              for (var i=0; i < totalCount; i++) {

         <c:if test="${valueType == 'range'}">
                var fromInput = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i + "_from");
                var toInput = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i + "_to");
                var keyInput = document.getElementById("<c:out value='${paramKeyIdPrefix}'/>" + i);
                if (keyInput && fromInput && toInput) {
                  if (keyInput.value != "All other values")
                    keyInput.value = fromInput.value + "-" + toInput.value;
                  }
         </c:if>

                var valueInput = document.getElementById("<c:out value='${paramValueIdPrefix}'/>" + i);
                if (valueInput && (valueInput.value == null || valueInput.value.replace(/^\s+|\s+$/g, '').length == 0))
                  valueInput.value = '0';

              }
            }



            <c:out value="${paramInfoObj}"/>.onTransferUserValues = <c:out value='${transferComponentUserValuesFunc}'/>;


         <c:if test="${((dataType == 'enum') && (not (propertyName eq '$field')))
              || (dataType == 'boolean')
              || (!requestScope.atgIsAssetEditable)
              || (inheritBaseConfigReadOnly =='true')}">
            registerOnLoad(function() {
              <c:out value="${paramInfoObj}"/>.hideAddButtons();
              <c:if test="${!requestScope.atgIsAssetEditable || inheritBaseConfigReadOnly =='true'}">
                <c:out value="${paramInfoObj}"/>.hideSortButton();
              </c:if>
            });
         </c:if>

       </script>

    </c:when>

    <%-- Render the table header if requested --%>
    <c:when test="${paramRenderHeader}">

      <thead>
        <tr class="header">
          <th class="hidden">
          </th>
          <th class="formValueCell">
            &nbsp; <%-- this goes above good better best --%>
          </th>
          <th class="formValueCell">
            <fmt:message key='propertyValueRankingEditor.rank'/>
          </th>
          <c:choose>
            <c:when test="${valueType == 'range' && dataType == 'date'}">
              <th class="formValueCell">
                <fmt:message key='propertyValueRankingEditor.valueRangeDateStart'/>
              </th>
              <th class="formValueCell">
                <fmt:message key='propertyValueRankingEditor.valueRangeDateEnd'/>
              </th>
            </c:when>
            <c:when test="${valueType == 'range'}">
              <th class="formValueCell">
                <fmt:message key='propertyValueRankingEditor.valueRangeNumericStart'/>
              </th>
              <th class="formValueCell">
                <fmt:message key='propertyValueRankingEditor.valueRangeNumericEnd'/>
              </th>
              </c:when>
              <c:otherwise>
                <th class="formValueCell">
                  <fmt:message key='propertyValueRankingEditor.value'/>
                </th>
              </c:otherwise>
            </c:choose>
          <th class="formValueCell">
            <fmt:message key='propertyValueRankingEditor.strength'/>
          </th>
          <th class="formValueCell">
            &nbsp;<%-- this goes above the delete icon --%>
          </th>
        </tr>
      </thead>

    </c:when>


    <%--
                             A SINGLE ROW

         This is included for each component row of collection editor
                                                                           --%>
    <c:otherwise>

      <c:set var="readOnlyAttr" value=""/>
      <c:if test="${!requestScope.atgIsAssetEditable || inheritBaseConfigReadOnly == 'true'}">
        <c:set var="readOnlyAttr" value="readonly"/>
      </c:if>

      <td id="<c:out value='${indicatorCellIdPrefix}${paramIndex}'/>">

        <%-- This cell is programmatically filled in by "sortRows" --%>
        &nbsp;

      </td>

      <td class="atg_smerch_propertyRankingCell">

        <%-- Rank (map value) --%>

        <%-- This text field specifies the actual value for the map entry in this
             row.  The field value is transfered to and from a hidden DSP form
             input by the CollectionEditor._transferElementValues function. --%>

        <input type="text" id="<c:out value='${paramValueId}'/>" <c:out value="${readOnlyAttr}"/>
               name="<c:out value='${sortName}'/>" maxlength="4"
               onpropertychange="formFieldModified()"
               oninput="markAssetModified()"
               onchange="updateStrengthValue();return isValidNumber(this,true);"
               onkeypress="return onFieldKeyPress(event);"
               onkeyup="return onFieldKeyUp(event);"
               class="formTextField" value="" size="4"
               fooId="foo_<c:out value='${paramIndex}'/>"/>
      </td>


      <%-- Property Value ( map key ) --%>

      <c:choose>

        <%--    Boolean, Enum     --%>

        <c:when test="${dataType == 'enum' || dataType == 'boolean'}">

          <td class="formValueCell">
            <span id="<c:out value='${paramKeyId}'/>_allOthersSpan" style="display:none">
              <fmt:message key='propertyValueRankingEditor.allOtherValues'/>
            </span>
            <%--  text input for the property key, which is a string --%>
            <span id="<c:out value='${paramKeyId}'/>_keySpan">
              <c:choose>
                <c:when test="${'$field' eq propertyName}">
                  <select id="select_<c:out value='${paramKeyId}'/>"
                      onchange="
                        var input = document.getElementById('<c:out value='${paramKeyId}'/>');
                        input.value = this.options[this.selectedIndex].value;">
                    <c:forEach items="${formHandler.fieldPropertyValues}" var="pv">
                      <option value="<c:out value="${pv.key}"/>">
                        <c:out value="${pv.value.displayKey}"/>
                      </option>
                    </c:forEach>
                  </select>
                  <input type="hidden" id="<c:out value='${paramKeyId}'/>" readonly
                         onpropertychange="formFieldModified()"
                         oninput="markAssetModified()"
                         class="formTextField" value=""/>
                </c:when>
                <c:when test="${dataType == 'enum'}">
                  <input type="hidden" id="<c:out value='${paramKeyId}'/>" readonly
                         onpropertychange="formFieldModified()"
                         oninput="markAssetModified()"
                         class="formTextField" value=""/>
                  <c:if test="${not paramTemplate}">
                    <dspel:getvalueof var="propertyValue" bean="${requestScope.formHandlerPath}.value.valueRanks.keys[${paramIndex}]"/>
                    <assetui-search:propertyValueDisplayTextResolver propertyName="${propertyName}" propertyValue="${propertyValue}" var="propertyValueDisplayName"/>
                    <input type="text" readonly
                         onpropertychange="formFieldModified()"
                         oninput="markAssetModified()"
                         class="formTextField" value="<c:out value='${propertyValueDisplayName}'/>"/>
                  </c:if>
                </c:when>
                <c:otherwise>
                  <input type="text" id="<c:out value='${paramKeyId}'/>" readonly
                         onpropertychange="formFieldModified()"
                         oninput="markAssetModified()"
                         class="formTextField" value=""/>
                </c:otherwise>
              </c:choose>
            </span>
          </td>

        </c:when>

        <%--    Range (Float, Integer, Date   [ FROM ]  "-"  [ TO ]) --%>

        <c:when test="${valueType == 'range'}">

          <td class="formValueCell">
            <span id="<c:out value='${paramKeyId}'/>_allOthersSpan" style="display:none">
              <fmt:message key='propertyValueRankingEditor.allOtherValues'/>
            </span>

            <div id="<c:out value='${paramKeyId}'/>_keySpanFrom" style="float: left; white-space: nowrap; width: auto">

            <%-- figure out if this range is an overlapping range --%>
            <c:if test="${not paramTemplate}">
              <dspel:getvalueof var="key" bean="${propertyView.formHandlerProperty}.keys[${paramIndex}]"/>
              <dspel:importbean var="fh" bean="${formHandlerPath}"/>
              <c:set var="overlapField" value="${fh.overlappingRanges[key]}"/>
            </c:if>

            <%--  text input for the property key, which is "from-to" string formed by two non-hidden inputs --%>

            <c:if test="${dataType == 'date'}">
              <fmt:message var="imgAlt" bundle="${assetManagerBundle}" key="dateEditor.calendarIconText"/>
            </c:if>

            <c:if test="${'from' eq overlapField}">
              <span class="atg_smerch_property_conflictField"/>
            </c:if>

            <input type="text" id="<c:out value='${paramKeyId}'/>_from" <c:out value="${readOnlyAttr}"/>
                   onpropertychange="formFieldModified()"
                   oninput="markAssetModified()"
                   onchange="return validateField(this)"
                   onkeypress="return onFieldKeyPress(event);"
                   onkeyup="return onFieldKeyUp(event);"
                   class="formTextField" value="" size="15"/>

            </div>
          </td>
          <td class="formValueCell">

            <div id="<c:out value='${paramKeyId}'/>_keySpanTo" style="float: left; white-space: nowrap; width: auto">

            <c:if test="${'to' eq overlapField}">
              <span class="atg_smerch_property_conflictField"/>
            </c:if>
            <input type="text" id="<c:out value='${paramKeyId}'/>_to" <c:out value="${readOnlyAttr}"/>
                   onpropertychange="formFieldModified()"
                   oninput="markAssetModified()"
                   onchange="return validateField(this)"
                   onkeypress="return onFieldKeyPress(event)"
                   onkeyup="return onFieldKeyUp(event);"
                   class="formTextField" value="" size="15"/>
            <input type="hidden" id="<c:out value='${paramKeyId}'/>"
                   class="formTextField" value=""/>
            </div>
          </td>
        </span>


        </c:when>

        <%--    Element   ( String or Numeric Element ) --%>

        <c:otherwise>

          <td class="formValueCell">
            <span id="<c:out value='${paramKeyId}'/>_allOthersSpan" style="display:none">
              <fmt:message key='propertyValueRankingEditor.allOtherValues'/>
            </span>

            <%--  text input for the property key, which is a string --%>
            <span id="<c:out value='${paramKeyId}'/>_keySpan">

              <c:if test="${dataType == 'date'}">
                <fmt:message var="imgAlt" bundle="${assetManagerBundle}" key="dateEditor.calendarIconText"/>
              </c:if>

              <input type="text" id="<c:out value='${paramKeyId}'/>" <c:out value="${readOnlyAttr}"/>
                     onpropertychange="formFieldModified()"
                     oninput="markAssetModified()"
                     onchange="return validateField(this)"
                     class="formTextField" value=""/>
            </span>
          </td>

        </c:otherwise>

      </c:choose>

      <td>
        <span id="foo_<c:out value="${paramIndex}"/>">&nbsp;</span>
      </td>


      <c:if test="${dataType == 'date' && not paramTemplate}">
        <%-- Get the date format for the request locale..--%>
        <fmt:message var="dateFormat" bundle="${assetManagerBundle}" key="timestampEditor.dateFormat"/>
        <script type="text/javascript">


        <%-- Configure the dynarch.com calendar widget so that it is displayed when
            the calendar icon is clicked. --%>

         registerOnLoad(function() {

          <c:choose>
            <c:when test="${valueType == 'range'}">
              <c:out value='${id}'/>_setupCalendar("<c:out value='${paramKeyId}'/>_from");
              <c:out value='${id}'/>_setupCalendar("<c:out value='${paramKeyId}'/>_to");
           </c:when>
           <c:otherwise>
              <c:out value='${id}'/>_setupCalendar("<c:out value='${paramKeyId}'/>");
            </c:otherwise>
          </c:choose>
         });

        </script>
      </c:if>

    </c:otherwise>

  </c:choose>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/propertyValueRankingComponentEditor.jsp#2 $$Change: 651448 $--%>
