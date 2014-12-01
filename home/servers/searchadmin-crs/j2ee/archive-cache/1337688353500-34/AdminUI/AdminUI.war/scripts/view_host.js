// work with search engines, hosts, and partitions
function onPartitionOver(partitionId) {
  setClass(partitionId, 'altCell');
}

function onPartitionOut(partitionId) {
  setClass(partitionId, '');
}

function onHostOver(hostId) {
  setClass(hostId, 'altCell');
}

function onHostOut(hostId) {
  setClass(hostId, '');
}

function setClass(parentId, newClassName) {
  var cells = document.getElementsByTagName('td');

  for(var i=0; i<cells.length;i++) {
    var cellId = cells[i].id;
    if(cellId==parentId) {
      cells[i].className = newClassName;
    }
  }
}

var classes = new Object();

function select(contentSetId){
  var elements = getElementsByClassName(contentSetId);
  for(var i = 0; i<elements.length; i++){
    var id = elements[i].id;
    classes[id] = elements[i].className;
    elements[i].className = "highlighted";
  }
}

function getClassNameFromObject(id){
  var str = "";
  for (var i in classes){
    if(i==id){
      str = classes[i];
    }
  }
  return str;
}

function restore(contentSetId){
  var elements = getElementsByClassName("highlighted");
  for(var i = 0; i<elements.length; i++){
    var id = elements[i].id;
    elements[i].className = getClassNameFromObject(id) + " common";
  }
}
function onContentSetOver(contentSetId){
  select(contentSetId);
}
function onContentSetOut(contentSetId){
  restore(contentSetId);
}
