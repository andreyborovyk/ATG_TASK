function refreshRightPane(obj) {
  var el = document.getElementById("tpoContentId");
  if (el && el.value && el.value.length > 0) {
    top.frames['rightPane'].location.reload();
  } else {
    var icons = getElementsByClassName('activeSearchProjectAsset');
    for (var i = 0; i < icons.length; i++) {
      icons[i].style.display = 'none';
    }
    if (obj.activeProjectId) {
      icons = getElementsByClassName(obj.activeProjectId);
      for (var i = 0; i < icons.length; i++) {
        icons[i].style.display = 'inline';
      }
    }
  }
}

function tpoTextareaLoad(pName) {
  var t = document.getElementById(pName + ".textarea");
  var d = document.getElementById(pName + ".inputs");
  var inputs = d.getElementsByTagName("input");
  for (var i = 0; i < inputs.length; i++) {
    if (inputs[i].name == pName && inputs[i].value.length > 0) {
      t.value += inputs[i].value + "\r\n";
    }
  }
  t.onchange = function() {tpoTextareaChange(pName)};
  tpoTextareaChange(pName);
}

function tpoTextareaChange(pName) {
  var d = document.getElementById(pName + ".inputs");
  var inputs = d.childNodes;
  while (d.childNodes.length > 0) {
    d.removeChild(d.childNodes[0]);
  }
  var t = document.getElementById(pName + ".textarea");
  var values = t.value.replace(/[\r]+/g, "").split("\n");
  for (var i = 0; i < values.length; i++) {
    if (values[i].length > 0) { // todo: trim each value
      tpoAddInputsForTextareaLine(d, pName, values[i]);
    }
  }
  // add empty element if there is no lines
  if (d.childNodes.length == 0) {
    tpoAddInputsForTextareaLine(d, pName, "");
  } 
}

function tpoCombineDropdownChange(pName){
  var view = document.getElementById(pName + ".combined");
  var options = document.getElementById(pName + '.inputs');
  options.innerHTML = '';
  var viewOptions = view.getElementsByTagName('option');
  for (var j = 0; j < viewOptions.length; j++) {
    if (viewOptions[j].selected) {
      var values = viewOptions[j].value.split('; ');
      for (var k = 0; k < values.length; k++){
        tpoAddInputsForTextareaLine(options, pName, values[k]);
      }
      break;
    }
  }
}

function tpoAddInputsForTextareaLine(d, pName, value) {
  var input = document.createElement("input");
  input.type = "hidden";
  input.name = pName;
  input.value = value;
  d.appendChild(input);
  var input2 = document.createElement("input");
  input2.type = "hidden";
  input2.name = "_D:" + pName;
  input2.value = " ";
  d.appendChild(input2);
}

// Javascript, used to switch divs on TPO content pages.
function switchTPODivs(divId, prefix) {
  var indexDiv = document.getElementById(divId + "_index");
  var contentDiv = document.getElementById(divId + "_content");
  
  if (prefix == "index") {
    indexDiv.style.display = "";
    contentDiv.style.display = "none";
  } else {
    indexDiv.style.display = "none";
    contentDiv.style.display = "";
  }
  return true;
}

// array of views for elements
var tpoViews = [];

function registerTPOView(viewId, submitFieldName) {
  var v = document.getElementById(viewId);
  tpoViews[tpoViews.length] = v;
  v.submitFieldName = submitFieldName;
}

function syncTPOViews() {
  for (var i = 0; i < tpoViews.length; i++) {
    var view = tpoViews[i];
    if (view.tagName.toLowerCase() == 'select') {
      // processes selects
      if (view.multiple) {
        var options = document.getElementById(view.submitFieldName + '.options');
        options.innerHTML = '';
        var viewOptions = view.getElementsByTagName('option');
        for (var j = 0; j < viewOptions.length; j++) {
          if (viewOptions[j].selected) {
            tpoAddInputsForTextareaLine(options, view.submitFieldName, viewOptions[j].value);
          }
        }
      } else {
        // if it is not a multiselect then find checked element and pick it's value
        var viewOptions = view.getElementsByTagName('option');
        for (var j = 0; j < viewOptions.length; j++) {
          if (viewOptions[j].selected) {
            var hidden = document.getElementById(view.submitFieldName + '.hidden');
            hidden.value = viewOptions[j].value;
            break;
          }
        }
      }
    } else if (view.tagName.toLowerCase() == 'input') {
      // processes text inputs and radio groups
      var hidden = document.getElementById(view.submitFieldName + '.hidden');
      if (view.type.toLowerCase() == 'text') {
        hidden.value = view.value;
      } else if (view.type.toLowerCase() == 'radio' && view.checked) {
        hidden.value = view.value;
      }
    } else if (view.tagName.toLowerCase() == 'table'){
      // processes ranked table
      var options = document.getElementById(view.submitFieldName + '.options');
      options.innerHTML = '';
      var checkboxes = document.getElementsByName("checkbox");
      var hiddens = document.getElementsByName("hidden");
      for (var j = 0; j < hiddens.length; j++){
        if (checkboxes[j].checked) {
          tpoAddInputsForTextareaLine(options, view.submitFieldName, hiddens[j].value);
        }
      }
    } 
  }
}

// array of tpos for switching tabs
var tpoElements = [];
var currentCategory = "basic";

function registerTPO(elementId) {
  var v = document.getElementById(elementId);
  tpoElements[tpoElements.length] = v;
  if (elementId.indexOf(currentCategory) >= 0){
    v.style.display = "";
  }
}

// Javascript, used to switch content and navigation tabs.
// Content can include a lot of divs with the same name.
// This method was added by Epam.

function switchMultiDivContent(category, showTable) {
  currentCategory = category;
  var navPane = document.getElementById("subNav");
  var navPaneElements = navPane.getElementsByTagName("LI");

  for (var i = 0; i < navPaneElements.length; i++) {
    var currentNavElement = navPaneElements[i];
    if (currentNavElement.id.indexOf(category) >= 0) {
      currentNavElement.className = "current";
    } else {
      currentNavElement.className = "";
    }
  }
  var tpoTable = document.getElementById("_options_table");
  if (tpoTable){
    if (showTable){
      tpoTable.style.display = "";
    } else {
      tpoTable.style.display = "none";
    }
  }
  switchDiv();
  return false;
}

// Javascript, used to switch divs.
// Content can include a lot of divs with the same name.
// This method was added by Epam.
function switchDiv(){
  for (var i = 0; i < tpoElements.length; i++) {
    var element = tpoElements[i];
    if (element.id) {
      if (element.id.indexOf(currentCategory) >= 0){
        element.style.display = "";
      } else {
        element.style.display = "none"
      }
    }
  }
}
