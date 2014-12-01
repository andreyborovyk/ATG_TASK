/*
  This javascript used on syncronization_manual.jsp
*/
var isIncremental = false;
function setIncremental(task){
  if ('incremental' == task){
    isIncremental = true;
  } else {
    isIncremental = false;
  }
}

function postCustomSwitch (allCustomizations, postCustomStep){
  document.getElementById('isAllCustomizationsHidden').value = allCustomizations;
  document.getElementById('postIndexStep').value = postCustomStep;
}

function contentSwitch (allContent){
  document.getElementById('searchIsAllContentSetsHidden').value = allContent;
  var show = false;
  if (allContent == "true"){
    show = true;
  }
  showCustomizationOptions(show);
  disableIndexingButtons(allContent == "true");
}
                
function showCustomizationOptions(pShow){
  if (isIncremental){
    document.getElementById("customizationLabel").style.display = (pShow) ? "none" : "";
    document.getElementById("postCustomizationSelect").style.display = (pShow) ? "" : "none";
    
    var container = document.getElementById("postCustomizationSelect");
    var elements = findElementsByName(container, "input", "customization");
    for (var i = 0; i < elements.length; i++) {
      if (elements[i].checked){
        if (pShow){
          elements[i].click();
        } else {
          var radioFamilyID = elements[i].parentNode.parentNode.id;
          var pos = i + 1;
          document.getElementById(radioFamilyID + pos).style.display = "none";
        }
      }
    }
    if (!pShow){
      postCustomSwitch('true', 'PostIndexCustomization_UseExisting');
    }
  }
}

function setAllContentCheckboxState(checked) {
  setChildCheckboxesState('contentTableAll', 'contentSetsList', checked);
  var container = document.getElementById('contentTableAll');
  var elements = findElementsByName(container, "input", 'contentSetsList');
  for (var i = 0; i < elements.length; i++) {
    setPartitionChildCheckboxesDisabled('contentTableAll', 'contentList', checked, elements[i].id);
  }
}

function setAllContentCheckboxStateByPartition(partitionId, checked){
  setPartitionChildCheckboxesDisabled('contentTableAll', 'contentList', checked, partitionId);
  document.getElementById('csAllPartitions').checked = getChildCheckboxesState('contentTableAll', 'contentSetsList');
}

function setPartitionChildCheckboxesDisabled(pContainerId, pCheckboxesName, pDisabled, partitionId) {
  var container = document.getElementById(pContainerId);
  var elements = findElementsByName(container, "input", pCheckboxesName);
  for (var i = 0; i < elements.length; i++) {
    if (elements[i].id.indexOf(pCheckboxesName + partitionId) >= 0){
      elements[i].disabled = pDisabled;
    }
  }
  return true;
}

function showAllContentCheckboxes(pShow) {
  var container = document.getElementById('contentTableAll');
  var elements = findElementsByName(container, "input", 'contentList');
  for (var i = 0; i < elements.length; i++) {
    if (pShow){
      elements[i].style.display = "";
    } else {
      elements[i].style.display = "none";
    }
  }
  return true;
}

function disableIndexingButtons(show){
  var contentSelected = false;
  if (show) {
    contentSelected = show;
  }
  if (!contentSelected){
    contentSelected = isSelectedCheckboxes('contentTableAll', 'contentSetsList');
  }
  if (!contentSelected && isIncremental){
    contentSelected = isSelectedCheckboxes('contentTableAll', 'contentList');
  }
  document.getElementById("build_button").disabled=!contentSelected;
  document.getElementById("save_rule_button").disabled=!contentSelected;
  var footerCopy = document.getElementById("paneFooterCopy");
  var elements = footerCopy.getElementsByTagName("input");
  for (var i = 0; i < elements.length; i++) {
    elements[i].disabled=!contentSelected;
  }
}

function isSelectedCheckboxes(pContainerId, pCheckboxesName){
  var container = document.getElementById(pContainerId);
  var elements = findElementsByName(container, "input", pCheckboxesName);
  for (var i = 0; i < elements.length; i++) {
    if (elements[i].checked) {
      return true;
    }
  }
  return false;
}

function getChildEnvCheckboxesState(pContainerId, pCheckboxesName) {
  var container = document.getElementById(pContainerId);
  var elements = findElementsByName(container, "input", pCheckboxesName);
  var allChecked = true;
  var allDisabled = true;
  for (var i = 0; i < elements.length; i++) {
    if (!elements[i].disabled){
      allDisabled = false;
      if (!elements[i].checked) {
        allChecked = false;
        break;
      }
    }
  }
  return (allChecked && !allDisabled);
}

function setChildEnvCheckboxesState(pContainerId, pCheckboxesName, pChecked, pFunc) {
  var container = document.getElementById(pContainerId);
  var elements = findElementsByName(container, "input", pCheckboxesName);
  for (var i = 0; i < elements.length; i++) {
    if (!elements[i].disabled){
      elements[i].checked = pChecked;
    }
  }
  return true;
}

function enableDisabledCheckboxes(){
  var envList = document.getElementsByName("searchEnvironmentsList");
  for (var i = 0; i < envList.length; i++) {
    envList[i].disabled = false;
  }  
  var contentList = document.getElementsByName("contentList");
  for (var i = 0; i < contentList.length; i++) {
    contentList[i].disabled = false;
  }
}
