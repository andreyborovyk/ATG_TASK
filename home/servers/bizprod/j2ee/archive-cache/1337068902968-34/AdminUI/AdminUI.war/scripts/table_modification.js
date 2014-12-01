// Right now this object contains only internal functions.
// TODO: move all other functions inside of the tableModification object.
var tableModification = {
  //get all cell at the current row
  _getRowCellsHTML: function(pRow) {
    var a = new Array(pRow.cells.length);
    for (var i = 0; i < pRow.cells.length; i++) {
      a[i] = pRow.cells[i].innerHTML;
    }
    return a;
  },
  //get all cell value
  _getRowInputValues: function(pRow) {
    var a = new Object();
    var inputs = pRow.getElementsByTagName("input");
    for (var i = 0; i < inputs.length; i++) {
      // TODO need to check different types of input
      var el = inputs[i];
      if (el.type == "checkbox"){
        a[el.name] = el.checked;
      }
      else{
        a[el.name] = el.value;
      }
    }
    return a;
  },
  //set all
  _setRowCellsHTML: function(pRow, pCellsHTML) {
    for (var i = 0; i < pCellsHTML.length; i++) {
      var cell = (i < pRow.cells.length) ? pRow.cells[i] : pRow.insertCell(i);
      cell.innerHTML = pCellsHTML[i];
    }
  },
  //set values to cell
  _setRowInputValues: function(pRow, pInputValues) {
    var inputs = pRow.getElementsByTagName("input");
    for (var i = 0; i < inputs.length; i++) {
      // TODO need to check different types of input
      var el = inputs[i];
      if (el.type == "checkbox"){
        el.checked = pInputValues[el.name];
      }
      else{
        el.value = pInputValues[el.name];
      }
    }
  },
  //set link active
  _setActiveLinks: function(rw, activate, elClassName) {
    var lnks = rw.getElementsByTagName("a");
    for (var i = 0; i < lnks.length; i++) {
      if (elClassName == null || lnks[i].className.indexOf(elClassName) >= 0) {
        var cn = lnks[i].className;
        var p = cn.indexOf(" inactive");
        if (activate && cn.indexOf(" disabled") < 0) {
          if (p > -1) {
            cn = cn.substring(0, p);
          }
        } else {
          if (p < 0) {
            cn += " inactive";
          }
        }
        lnks[i].className = cn;
      }
    }
  }
};

// set active/inactive class for image at edit macros
function initTable(tb)
{
  var lstRow = tb.rows[tb.rows.length - 1];
  var l = tb.rows.length;
  for (var i = 1; i < l; i++) {
    var r = tb.rows[i];
    tableModification._setActiveLinks(r, i > 1 && i < l - 1 && l > 3, "propertyUp");
    tableModification._setActiveLinks(r, i < l - 2 && l > 3, "propertyDown");
    tableModification._setActiveLinks(r, i < l - 1, "propertyDelete");
  }

  if (tb.macrosTableEmptyRow == null) {
    tb.macrosTableEmptyRow = tableModification._getRowCellsHTML(lstRow);
  }

  if (tb.onchange) {
    if (typeof tb.onchange != "function") {
      eval("tb.onchange = function() {" + tb.onchange + "}");
    }
    tb.onchange();
  }
}

// get parent element by tag Name
function getParentByChildElement(el, tagName) {
  tagName = tagName.toLowerCase();
  while (el != null && el.tagName.toLowerCase() != tagName) {
    el = el.parentNode;
  }
  return el;
}

//add new empty row to table
function addEmptyField(inputBox) {
  if (inputBox.type == 'checkbox') {
    setChangeFlag();
    var inputHidden = inputBox.nextSibling;
    while (inputHidden && inputHidden.type != "hidden") {
      inputHidden = inputHidden.nextSibling;
    }
    if (inputHidden && inputHidden.type == "hidden") {
      inputHidden.value = inputBox.checked;
    }
  } else if (inputBox.value.trim().length == 0) {
    return false;
  }
  var theRow = getParentByChildElement(inputBox, "tr");
  var table = getParentByChildElement(theRow, "table");
  var len = table.rows.length;

  if (len != theRow.rowIndex + 1) {
    return false;
  }
  var newRow = table.insertRow(len);
  tableModification._setRowCellsHTML(newRow, table.macrosTableEmptyRow);

  initTable(table);
  return false;
}

//delete row
function deleteField(inputBox) {
  setChangeFlag();
  if (inputBox.className.indexOf(" inactive") < 0) {
    var table = getParentByChildElement(inputBox, "table");
    var delRow = getParentByChildElement(inputBox, "tr");
    table.deleteRow(delRow.rowIndex);
    initTable(table);
  }
  return false;
}

//delete row by id
function deleteFieldById(id) {
  var inputBox = document.getElementById(id);

  if (inputBox != null) {
    deleteField(inputBox);
  }
  return false;
}

//move row Up or Down
function moveField(inputBox, isUp) {
  if (inputBox.className.indexOf(" inactive") >= 0) {
    return false;
  }
  setChangeFlag();
  var table = getParentByChildElement(inputBox, "table");
  var moveRow = getParentByChildElement(inputBox, "tr");

  var firstSwapRowIndex = moveRow.rowIndex;
  if (isUp) {
    firstSwapRowIndex--;
  }
  if (firstSwapRowIndex < 1 || firstSwapRowIndex >= table.rows.length - 1) {
    return false;
  }

  //change
  var firstSwapRow = table.rows[firstSwapRowIndex];
  var secondSwapRow = table.rows[firstSwapRowIndex + 1];
  var firstSwapRowCellsHTML = tableModification._getRowCellsHTML(firstSwapRow);
  var firstSwapRowInputValues = tableModification._getRowInputValues(firstSwapRow);
  tableModification._setRowCellsHTML(firstSwapRow, tableModification._getRowCellsHTML(secondSwapRow));
  tableModification._setRowInputValues(firstSwapRow, tableModification._getRowInputValues(secondSwapRow));
  tableModification._setRowCellsHTML(secondSwapRow, firstSwapRowCellsHTML);
  tableModification._setRowInputValues(secondSwapRow, firstSwapRowInputValues);

  initTable(table);
  return false;
}
