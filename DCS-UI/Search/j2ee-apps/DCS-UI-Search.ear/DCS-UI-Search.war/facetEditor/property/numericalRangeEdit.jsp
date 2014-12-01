<%--
  A property editor to specify numerical ranges.
  
    @param  mpv          A MappedPropertyView item for this view
    @param  formHandler  The form handler for the form that displays this view
  
  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/property/numericalRangeEdit.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>
<dspel:importbean var="config"
                  bean="/atg/commerce/search/web/Configuration"/>
<fmt:setBundle var="bundle" basename="${config.resourceBundle}"/>

<%-- Unpack request-scoped parameters into page parameters --%>
<c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>
<c:set var="formHandler"     value="${requestScope.formHandler}"/>
<c:set var="view" value="${requestScope.view}"/>

<%-- determine which mode we're in --%>
<c:choose>
  <c:when test="${view.itemMapping.mode == 'AssetManager.edit'}">
    <c:set var="disabled" value="false"/>
  </c:when>
  <c:otherwise>
    <c:set var="disabled" value="true"/>
  </c:otherwise>
</c:choose>

<c:set var="dspInputIdPrefix"  value="range_"/>
<c:set var="rowIdPrefix"       value="range_row_"/>
<%-- This value is stored as the "index" in the template HTML, so that it can
     be substituted with the real index when it is copied into the DOM. --%>
<c:set var="templateRowIndex" value="__INDEX__"/>
  
<script type="text/Javascript">
var rangeList = new Array();

function addRangePoint () 
{
  // get the new rangePoint value
  var rangePoint = document.getElementById("rangePoint").value;
  if (rangePoint == null || rangePoint == "" || !IsPositiveNumeric(rangePoint)) {
    alert("Please enter a valid range point");
    return;
  }
  // figure out where in the rangeList array to insert the new 
  // rangePoint value
  var i=0;
  if (rangeList[i] != null) {
    for (i; i<rangeList.length; i++) {
      if (parseFloat(rangePoint) == parseFloat(rangeList[i].rangePointValue)) {
        alert("This value already exists.");
        return;
      }
      if (parseFloat(rangePoint) <= parseFloat(rangeList[i].rangePointValue)) {
        break; 
      }
    }
    // create a new range point object to add to the rangeList
    var newRangePoint = new Object();
    var newRangeIndex = i;
    newRangePoint.rangePointValue = rangePoint;
    newRangePoint.dspInputId = '<c:out value="${dspInputIdPrefix}"/>' + newRangeIndex;
    // create a temporary array in which to store rangePoint objects
    // after the array point we're going to insert the new one
    var tempRangeList = new Array();
    // store the rangePoint objects after the point we're going to 
    // insert the new one
    var j=0;
    for (i; i<rangeList.length; i++) {
      tempRangeList[j] = rangeList[i];
      j++;
    }
    rangeList[newRangeIndex] = newRangePoint;
    i = newRangeIndex + 1;
    for (j=0; j<tempRangeList.length; j++) {
      var shiftedRangePoint = new Object();
      shiftedRangePoint.rangePointValue = tempRangeList[j].rangePointValue;
      shiftedRangePoint.dspInputId = '<c:out value="${dspInputIdPrefix}"/>' + i;
      rangeList[i] = shiftedRangePoint;
      i++;
    }
  }
  else {
    var newRangePoint = new Object();
    newRangePoint.rangePointValue = rangePoint;
    newRangePoint.dspInputId = '<c:out value="${dspInputIdPrefix}"/>' + '0';
    newRangePoint.rowId      = '<c:out value="${rowIdPrefix}"/>' + '0';
                               
    rangeList[0] = newRangePoint;
  }
  markAssetModified();
  drawRangeList();
  document.getElementById("rangePoint").value = "";
}


function removeRangePoint (index)
{
  // remove specified index from array
  var newRangeList = new Array();
  var j=0;
  for (var i=0; i<rangeList.length; i++) {
    if (i != index) {
      var newRangePoint = new Object();
      newRangePoint.rangePointValue = rangeList[i].rangePointValue;
      newRangePoint.dspInputId      = '<c:out value="${dspInputIdPrefix}"/>' + j;
      newRangeList[j] = newRangePoint;
      j++;
    }
  }
  rangeList = newRangeList;
  markAssetModified();
  drawRangeList();
}

function initializeRangeList ()
{
  // populate rangeList with values from bean
  <dspel:droplet name="/atg/dynamo/droplet/ForEach">
    <dspel:param name="array" bean="${formHandlerPath}.value.ranges" />
    <dspel:setvalue param="rangePointValue" paramvalue="element"/>
    <dspel:oparam name="output">
      var newRangePoint<dspel:valueof param="index"/> = new Object();
      newRangePoint<dspel:valueof param="index"/>.rangePointValue = '<dspel:valueof param="rangePointValue"/>';
      newRangePoint<dspel:valueof param="index"/>.dspInputId      = '<c:out value="${dspInputIdPrefix}"/>' + '<dspel:valueof param="index"/>';
      rangeList[<dspel:valueof param="index"/>] = newRangePoint<dspel:valueof param="index"/>;
    </dspel:oparam>
  </dspel:droplet>
  drawRangeList();
}

function drawRangeList ()
{
  var rangePointDiv = document.getElementById("rangePointTable");
  var rangePointHTML = new String();
  
  var disabled = "<c:out value='${disabled}'/>";
  
  rangePointHTML  = '<table class="formStringList noLabelBorder">';
  for (var i=0; i<rangeList.length; i++) {
    rangePointHTML += '<tr>';
    if (i == 0)
      rangePointHTML += '<th class="formValueCell"><fmt:message key="fromRange" bundle="${bundle}"/></th>';
    else 
      rangePointHTML += '<th class="formValueCell"><fmt:message key="toRange" bundle="${bundle}"/></th>';
    rangePointHTML += '<td class="formValueCell">' + rangeList[i].rangePointValue + '</td>';
    rangePointHTML += '<td class="iconCell">';
    if (disabled == "false")
      rangePointHTML += '<a href="javascript:removeRangePoint(' + i + ');" class="icon propertyDelete" title="<fmt:message key='deleteRange' bundle='${bundle}'/>"></a>';
    rangePointHTML += '</td>';
    rangePointHTML += '</tr>';
  }
  rangePointHTML += '</table>';
  rangePointDiv.innerHTML = rangePointHTML;
}

function transferUserValues ()
{
  var tbody = document.getElementById("range_tbody");
  // Get the template row element.
  var rowElem = document.getElementById("<c:out value='${rowIdPrefix}${templateRowIndex}'/>");
  if (rowElem) {
    // Extract the contents of all of the cells in the row, and then delete the
    // row from the table.
    var templateRowData = new Array();
    // Loop through all of the cells in the row.
    for (var i = 0; i < rowElem.cells.length; i++) {
      var cell = rowElem.cells[i];
      // Add a data object for the current cell.
      var cellData = new Object();
      cellData.className = cell.className;
      cellData.innerHTML = cell.innerHTML;
      templateRowData[templateRowData.length] = cellData;
    }
    // Remove the template row
    rowElem.parentNode.removeChild(rowElem);

    if (templateRowData) {
      if (rangeList.length > 0) {
        // remove the null div element
        var div = document.getElementById("<c:out value='${dspInputIdPrefix}nullSubDivId'/>");
        if (div) {
          div.parentNode.removeChild(div);
        }
        for (var i=0; i<rangeList.length; i++) {
          // alert("rangeList["+i+"].rangePointValue: "+rangeList[i].rangePointValue);
          // Add a new row to the table for each element in rangeList
          var index = tbody.rows.length; // or should we just start with zero?
          var tr = tbody.insertRow(index);
          // Determine the index number to be used for IDs of elements in the row.
          var rowIndex = i;
          // Assign the appropriate ID to the row.
          tr.id = '<c:out value="${rowIdPrefix}"/>' + rowIndex;
          // Copy the template row cell data into cells in the newly-created row.  Note
          // that all instances of the template row index in the cell HTML are replaced
          // with the actual index.
          for (var j=0; j<templateRowData.length; j++) {
            var cell = templateRowData[j];
            var td = tr.insertCell(tr.cells.length);
            td.className = cell.className;
            td.innerHTML = cell.innerHTML.replace(new RegExp("<c:out value='${templateRowIndex}'/>", "g"), rowIndex);
          }
        }
      }
      else {
       // perform any operations that are necessary if the rangeList is empty
        var div    = document.getElementById("<c:out value='${dspInputIdPrefix}nullDivId'/>");
        var subDiv = document.getElementById("<c:out value='${dspInputIdPrefix}nullSubDivId'/>");
        subDiv.parentNode.removeChild(subDiv);
        div.innerHTML = subDiv.innerHTML
      }
    }
    var test = document.getElementById("test");
  }
  // now copy the values over
  for (var k=0; k<rangeList.length; k++) {
    var rowId = tbody.rows[k].id;
    var rowIndex = rowId.replace(new RegExp("<c:out value='${rowIdPrefix}'/>"), "");
    // Get the DSP input field and the user input field for the current row.
    var dspInputElem = document.getElementById("<c:out value='${dspInputIdPrefix}'/>" + k);
    dspInputElem.value = rangeList[k].rangePointValue;
  }
}

registerOnLoad(initializeRangeList);
registerOnSubmit(transferUserValues);
</script>

<div id="rangePointTable" style="display:inline">
</div>

<div class="dataTableActionsFooter">
<c:if test="${disabled eq 'false'}">
<input id="rangePoint" type="text" class="formTextField numberSmall"/> <a href="javascript:addRangePoint();" class="buttonSmall" title="Add Range Point"><span><fmt:message key="addRangePoint" bundle="${bundle}"/></span></a>
</c:if>
</div>

<%-- Get the number of elements in the current property value --%>
<dspel:getvalueof var="ranges" bean="${formHandlerPath}.value.ranges"/>
<web-ui:collectionPropertySize var="elements" collection="${ranges}"/>

<div id="inputTemplate" style="display: none;">
  <table id="ranges_table" class="formStringList">
    <tbody id="range_tbody">
      <tr id="<c:out value='${rowIdPrefix}${templateRowIndex}'/>" class="hidden">
        <td>
          <dspel:input type="hidden" id="${dspInputIdPrefix}${templateRowIndex}" iclass="formTextField"
                       bean="${formHandlerPath}.value.ranges"
                       value=""/>
        </td>
      </tr>
    </tbody>
  </table>
</div>

<%-- When present, the following hidden input causes the property value to be set to null.
     The "nullSubDivId" div will be removed from the document by the controlling JavaScript
     if the rangeList is not actually empty. --%>
<div id="<c:out value='${dspInputIdPrefix}nullDivId'/>" style="display: none;">
  <div id="<c:out value='${dspInputIdPrefix}nullSubDivId'/>">
    <dspel:input type="hidden"
                 bean="${formHandlerPath}.value.ranges"
                 value="null"/>
  </div>
</div>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/property/numericalRangeEdit.jsp#2 $$Change: 651448 $--%>
