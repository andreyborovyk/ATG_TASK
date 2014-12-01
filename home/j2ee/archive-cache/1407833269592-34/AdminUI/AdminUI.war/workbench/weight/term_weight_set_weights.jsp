<%--
  JSP, showing term weight set weights table.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set_weights.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>
<d:page>
  <%-- Getting Stop Word Threshold value --%>
  <d:getvalueof param="stopWordThreshold" var="stopWordThreshold"/>
  <%-- Getting Term Retrieval Threshold value --%>
  <d:getvalueof param="termRetrievalThreshold" var="termRetrievalThreshold"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/ManageTermWeightSetFormHandler"/>
  <%-- Collection of all existing term weights --%>
  <d:getvalueof bean="ManageTermWeightSetFormHandler.termWeights" var="termWeights"/>
  
  <h3>
    <fmt:message key="term_weight_set_weights.message"/>
  </h3>
  
  <fmt:message key="term_weight_set_weights.table.stop_word" var="stopWordTitle"/>
  <div id="stopWordDivIcon" style="display:none">
    <a class="icon termWeight_stopWord" href="#" title="<c:out value="${stopWordTitle}"/>"
       onclick="return false;">stop</a>
  </div>
  <div id="stopWordDivDesc" style="display:none">
    <c:out value="${stopWordTitle}"/>
  </div>
  <fmt:message key="term_weight_set_weights.table.partially_indexed" var="partIndexTitle"/>
  <div id="partIndexDivIcon" style="display:none">
    <a class="icon termWeight_partiallyIndexed" href="#" title="<c:out value="${partIndexTitle}"/>"
       onclick="return false;">part</a>
  </div>
  <div id="partIndexDivDesc" style="display:none">
    <c:out value="${partIndexTitle}"/>
  </div>
  <fmt:message key="term_weight_set_weights.table.fully_indexed" var="fullyIndexTitle"/>
  <div id="fullyIndexDivIcon" style="display:none">
    <a class="icon termWeight_fullyIndexed" href="#" title="<c:out value="${fullyIndexTitle}"/>"
       onclick="return false;">full</a>
  </div>
  <div id="fullyIndexDivDesc" style="display:none">
    <c:out value="${fullyIndexTitle}"/>
  </div>
  <div id="wrongWeightDiv" style="display:none">
    <c:out value="" escapeXml="false"/>
  </div>
  
  <%-- Retrieving current value for table sorting --%>
  <d:getvalueof bean="ManageTermWeightSetFormHandler.sort" var="sortValue"/>
  
  <%-- Table sorting --%>
  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.termWeightBeanComparator" var="comparator"/>
  <admin-ui:sort var="termWeights" items="${termWeights}" comparator="${comparator}" sortMode="${sortValue}"/>
  
  <%-- Table, allowing to add, delete and change term weights --%>
  <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                  var="termWeight" items="${termWeights}" tableId="termWeightsTable"
                  sort="${sortValue}" onSort="tableOnSort" emptyRow="true">
    <admin-ui:column type="sortable" title="term_weight_set_weights.table.term" width="60%" name="term">
      <d:input bean="ManageTermWeightSetFormHandler.termIds" type="hidden" name="termIds" value="${termWeight.termId}"/>
      <d:input type="text" value="${termWeight.term}"
               iclass="textField" style="width:95%" name="terms"
               onkeyup="addEmptyField(this);" onchange="addEmptyField(this);"
               bean="ManageTermWeightSetFormHandler.terms"/>
    </admin-ui:column>
    <admin-ui:column type="sortable" title="term_weight_set_weights.table.weight" width="60" name="weight">
      <d:input type="text" value="${termWeight.weight}"
               iclass="textField weight" name="weights" size="3" maxlength="3"
               onkeyup="addEmptyField(this);changeIcon(this);" onchange="addEmptyField(this);changeIcon(this);"
               bean="ManageTermWeightSetFormHandler.weights"/>
    </admin-ui:column>
  
    <admin-ui:column type="icon" title="">
      <div class="termWeightEffectDivIcon"></div>
    </admin-ui:column>
  
  
    <admin-ui:column type="static" title="term_weight_set_weights.table.weighting_effect">
      <div class="termWeightEffectDivDesc"></div>
    </admin-ui:column>
  
    <admin-ui:column type="icon" width="20">
      <fmt:message key="term_weight_set_weights.table.delete.tooltip" var="deleteTitle"/>
      <a class="icon propertyDelete" href="#" title="${deleteTitle}"
         onclick="return deleteField(this)">del</a>
    </admin-ui:column>
  </admin-ui:table>
  
  <d:input bean="ManageTermWeightSetFormHandler.sort" value="field mode"
           type="submit" id="sortInput" iclass="formsubmitter" style="display:none"/>

  <script type="text/javascript">
    initDefaultWeightEffect();
    initTable(document.getElementById("termWeightsTable"));
    reInitTable("termWeightsTable");

    function initDefaultWeightEffect() {
      var table = document.getElementById("termWeightsTable");
      var weightElements = findElements(table, "input", "weight");
      if (weightElements.length > 0) {
        var defaultWeightElement = weightElements[weightElements.length - 1];
        changeIcon(defaultWeightElement);
      }
    }

    function tableOnSort(tableId, columnName, sortDirection) {
      var sortButton = document.getElementById('sortInput');
      sortButton.value = columnName + " " + sortDirection;
      sortButton.click();
    }

    // checks if value is integer
    function isInteger(val) {
      for (var i = 0; i < val.length; i++) {
        if ("1234567890".indexOf(val.charAt(i)) == -1) {
          return false;
        }
      }
      return true;
    }

    function changeIcon(weightInput) {
      var weightValue = weightInput.value;
      var trElement = getParentByChildElement(weightInput, "tr");
      var aElements = findElements(trElement, "div", "termWeightEffectDivIcon");
      var tElements = findElements(trElement, "div", "termWeightEffectDivDesc");
      if (!isInteger(weightValue)) {
        for (i = 0; i < aElements.length; i++) {
          aElements[i].innerHTML = document.getElementById("wrongWeightDiv").innerHTML;
          tElements[i].innerHTML = document.getElementById("wrongWeightDiv").innerHTML;
        }
      } else {
        var intValue = parseInt(weightValue);
        if (intValue < stopWordThresholdVar && intValue >= 0) {
          for (i = 0; i < aElements.length; i++) {
            aElements[i].innerHTML = document.getElementById("stopWordDivIcon").innerHTML;
            tElements[i].innerHTML = document.getElementById("stopWordDivDesc").innerHTML;
          }
        } else if (intValue < termRetrievalThresholdVar && intValue >= stopWordThresholdVar) {
          for (i = 0; i < aElements.length; i++) {
            aElements[i].innerHTML = document.getElementById("partIndexDivIcon").innerHTML;
            tElements[i].innerHTML = document.getElementById("partIndexDivDesc").innerHTML;
          }
        } else if (intValue >= termRetrievalThresholdVar && intValue <= 100) {
          for (i = 0; i < aElements.length; i++) {
            aElements[i].innerHTML = document.getElementById("fullyIndexDivIcon").innerHTML;
            tElements[i].innerHTML = document.getElementById("fullyIndexDivDesc").innerHTML;
          }
        } else {
          for (i = 0; i < aElements.length; i++) {
            aElements[i].innerHTML = document.getElementById("wrongWeightDiv").innerHTML;
            tElements[i].innerHTML = document.getElementById("wrongWeightDiv").innerHTML;
          }
        }
      }
    }

    function reInitTable(tableId) {
      var table = document.getElementById(tableId);
      var aElements = findElements(table, "input", "weight");
      for (k = 0; k < aElements.length; k++) {
        changeIcon(aElements[k]);
      }
    }
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set_weights.jsp#2 $$Change: 651448 $--%>
