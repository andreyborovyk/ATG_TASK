// javascript necessary for project_index.jsp page

function checkAllMove_setChildren(pId, pChildrenName) {
  var mainCheckbox = document.getElementById("checkall_" + pId);
  checkAll_setChildren(mainCheckbox, document.getElementById(pId), pChildrenName);
  var moveButton = document.getElementById("move_" + pId);
  moveButton.disabled = !mainCheckbox.checked;
}
function checkAllMove_setMain(pId, pChildrenName) {
  var mainCheckbox = document.getElementById("checkall_" + pId);
  var childrenContainer = document.getElementById(pId);
  checkAll_setMain(mainCheckbox, childrenContainer, pChildrenName);

  var allUnchecked = true;
  var inputElems = childrenContainer.getElementsByTagName("input");
  for (var i = 0; i < inputElems.length; i++) {
    if (inputElems[i].type == "checkbox" && inputElems[i].name == pChildrenName && inputElems[i].checked) {
      allUnchecked = false;
      break;
    }
  }
  var moveButton = document.getElementById("move_" + pId);
  moveButton.disabled = allUnchecked;
}

function contentSourceSetOnSort(pTableId, pColumnName, pSortDirection) {
  var sortFieldValue = document.getElementById('hiddenContentSetSortValue' + pTableId);
  if (sortFieldValue) {
    sortFieldValue.value = pColumnName + " " + pSortDirection;
  }
  var sortButton = document.getElementById('sortContentSetInput');
  sortButton.click();
}

function customizationItemsOnSort(pTableId, pColumnName, pSortDirection) {  
  document.getElementById('hiddenCustValue' + pTableId).value = pColumnName + " " + pSortDirection;
  document.getElementById('sortCustomizationItemsInput').click();
}

function moveContent(pPartitionId) {
  document.getElementById("fromPartitionId").value = pPartitionId;
  var selector = document.getElementById("moveSelector" + pPartitionId);
  document.getElementById("toPartitionId").value = selector.options[selector.selectedIndex].value;
  document.getElementById("move").click();
}

function checkAll_setChildren(pMainCheckbox, pChildrenContainer, pChildrenName) {
  var inputElems = pChildrenContainer.getElementsByTagName("input");
  for (var i = 0; i < inputElems.length; i++) {
    if (inputElems[i].type == "checkbox" && inputElems[i].name == pChildrenName) {
      inputElems[i].checked = pMainCheckbox.checked;
    }
  }
}

function checkAll_setMain(pMainCheckbox, pChildrenContainer, pChildrenName) {
  var allChecked = true;
  var inputElems = pChildrenContainer.getElementsByTagName("input");
  for (var i = 0; i < inputElems.length; i++) {
    if (inputElems[i].type == "checkbox" && inputElems[i].name == pChildrenName && !inputElems[i].checked) {
      allChecked = false;
      break;
    }
  }
  pMainCheckbox.checked = allChecked;
}

function contentSetSitesOver(el) {
  var fromPos = dijit.byId('assistanceBase').positionHelp(el);
  var ea = dijit.byId("EA_"+el.id);
  if (!ea) {
    ea = new atg.widget.assistance.Popup({
      id: "EA_"+el.id, 
      content: document.getElementById(el.id + "_content").innerHTML,
      position: fromPos,
      target: el}
    );
    dojo.body().appendChild(ea.domNode);
  }
  ea.helpContent.innerHTML = document.getElementById(el.id + "_content").innerHTML;
  ea.target = el;
  ea.show(fromPos);
}
function contentSetSitesOut(el) {
  var ea = dijit.byId("EA_"+el.id);
  if(ea){
    ea.hide();
  }
}
