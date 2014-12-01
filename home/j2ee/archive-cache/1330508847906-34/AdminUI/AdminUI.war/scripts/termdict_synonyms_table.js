function tableOnSort(tableId, columnName, sortDirection) {
  var sortButton = document.getElementById('sortInput');
  document.getElementById('sortDirection').value = sortDirection;
  document.getElementById('sortColumn').value = columnName;
  sortButton.click();
}

// table initialization, set disable select boxes if relationship is "equivalent"
function initSynonymTable() {
  initTable(document.getElementById("editSynonymTable"));

  var relationshipBosex = document.getElementsByName("relationship");
  for (var i = 0; i < relationshipBosex.length; i++) {
    if (relationshipBosex[i].value == "equivalent") {
      var parentElement = getParentByChildElement(relationshipBosex[i], "tr");
      enableDisableSelectBox(parentElement, true)
    }
  }
}

// add new empty field to table.
function addEmptySynonymField(inputBox) {
  addEmptyField(inputBox);
  enableDisableTableWeights();
}

//sets true to 'changed' field.
function setChanged(inputBox) {
  var parentElement = getParentByChildElement(inputBox, "tr");
  findElements(parentElement, "input", "synChanged")[0].value = true;
}

// disable or enable Language and Part of Speech select box, depends on Relation select box value
function enableDisableSelectBox(parrentField, disable) {
  var selectBoxes = findElements(parrentField, "select", "small");
  var relationBoxName = "relationship";
  for (var i = 0; i < selectBoxes.length; i++)
  {
    if (selectBoxes[i].name != relationBoxName) {
      var selectBox = selectBoxes[i];
      if (disable) {
        var blankOption = document.createElement("option");
        blankOption.text = "";
        blankOption.value = "";
        blankOption.selected = "true";
        selectBox.options.add(blankOption, 0);
      }
      else
      {
        var firstOption = selectBox.item(0);
        if (firstOption.text == "") {
          selectBox.remove(0);
          saveSelectBoxValue(selectBox, "partOfSpeech", false);
          saveSelectBoxValue(selectBox, "language", false);
          saveSelectBoxValue(selectBox, "phraseTypes", false);
        }
      }
      selectBox.disabled = disable;
    }
  }
  var enWeightField = findElements(parrentField, "div", "enableWeightField");
  var disWeightField = findElements(parrentField, "div", "disableWeightField");
  if (disable) {
    enWeightField[0].style.display = "none";
    disWeightField[0].style.display = "inline";
  }
  else {
    enWeightField[0].style.display = "inline";
    disWeightField[0].style.display = "none";
  }

}

// set select box value to hidden filed and vice versa
function saveSelectBoxValue(selectBox, inputName, changeInput) {
  var tdEl = getParentByChildElement(selectBox, "td");
  var intupEl = tdEl.getElementsByTagName("input");
  for (var i = 0; i < intupEl.length; i++) {
    if (intupEl[i].name == inputName) {
      if (changeInput) {
        intupEl[i].value = selectBox.value;
      }
      else {
        selectBox.value = intupEl[i].value;
      }
      break;
    }
  }
}

// deleted table row, and add id of deleted synonym to hidden field
function deleteSynonymField(inputBox, id) {
  if (id && id != "") {
    var deletedField = document.getElementById("deletedIds");
    var value = deletedField.value;
    if (value != "") {
      value += ",";
    }
    deletedField.value = value + id;
  }
  return deleteField(inputBox);
}

var relationIconClassNames = ["term_normal", "term_related", "term_equivalent", "term_non_reciprocal"];
// Real values for relationIconTitles should be defined on the page
var relationIconTitles = {normal: null, related: null, equivalent: null, non_reciprocal: null};

// change relation icons
function changeRelationIcon(selectBox) {
  var name = selectBox.value;
  var termClassName = "icon_syn term_" + name;
  var trElement = getParentByChildElement(selectBox, "tr");
  for (var i = 0; i < relationIconClassNames.length; i++) {
    var aElements = findElements(trElement, "a", relationIconClassNames[i]);
    if (aElements != null && aElements != "") {
      aElements[0].className = termClassName;
      aElements[0].title = relationIconTitles[name];
      break;
    }
  }
  enableDisableSelectBox(trElement, (name == "equivalent"));
  return false;
}

// set weight column visible or invisible.
function enableDisableTableWeights()
{
  // get value from  term_basics.jsp
  var enable = document.getElementById("enableWeight").value;
  var tableElement = document.getElementById("editSynonymTable");
  var disableDivs = findElements(tableElement, "div", "disableWeight");
  var weightDivs = findElements(tableElement, "div", "enableWeight");
  if (enable == 'true') {
    for (var i = 0; i < disableDivs.length; i++) {
      disableDivs[i].style.display = "none";
      weightDivs[i].style.display = "inline";
    }
  }
  else {
    for (var i = 0; i < disableDivs.length; i++) {
      disableDivs[i].style.display = "inline";
      weightDivs[i].style.display = "none";
    }
  }
  setAllSynonymsChanged();
}

function setAllSynonymsChanged(){
  var synonyms = document.getElementsByName("TermFormHandler.changed");
  for (var i = 0; i < synonyms.length; i++){
    synonyms[i].value = true;
  }
}
