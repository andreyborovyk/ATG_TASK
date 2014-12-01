//Does some macros functions

function setMacroUpdeted(field) {
  var trEl = getParentByChildElement(field, "tr");
  findElements(trEl, "input", "macroChanged")[0].value = true;
}

// Deleted table row, and add id of deleted macro to hidden field
function deleteMacroField(inputBox, macroId) {
  if (macroId && macroId != "") {
    var deletedField = document.getElementById("deletedIds");
    var value = deletedField.value;
    if (value != "") {
      value += ",";
    }
    deletedField.value = value + macroId;
  }
  return deleteField(inputBox);
}

function deleteMacroFieldById(componentId, macroId) {
  var inputBox = document.getElementById(componentId);

  if (inputBox != null) {
   deleteMacroField(inputBox, macroId);
  }
  else
  {
    alert("NULL");
  }
  return false;
}

// Sorting table
function tableOnSort(tableId, columnName, sortDirection){
  var sortButton = document.getElementById('sortInput');
  document.getElementById('sortDirection').value = sortDirection;
  document.getElementById('sortColumn').value = columnName;
  sortButton.click();
}
