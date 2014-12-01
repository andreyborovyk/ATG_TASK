/**
 * Tests if the string starts with the specified prefix.
 *
 * @param str     string for testing
 * @param prefix  the prefix
 * @return <code>true</code> if the character sequence represented by the argument is a prefix of the character sequence represented by this string;
 *         <code>false</code> otherwise.
 */
function startsWith(str, prefix) {
  return (str && str.indexOf(prefix) == 0);
}

/**
 * Search for tree node element which contains the specified element.
 *
 * @param el  element for searching
 * @return the tree node
 */
function findNodeByElement(el) {
  while (el != null && !startsWith(el.className, "treeNode ")) {
    el = el.parentNode;
  }
  return el;
}

/**
 * Main handler for any onclick events on the tree.
 *
 * @param evt     Instance of Event (null for IE)
 * @param action  What should happen when clicking on any subelement of the node:<br/>
 *   <code>"skip"</code> - nothing (it works only if first child of node is not text!);<br/>
 *   <code>"open"</code> - always open the node;<br/>
 *   <code>"default"</code> or <code>null</code> - the same as clicking on node.
 */
function treeOnClick(evt, action) {
  if (!evt) { // IE event
    evt = window.event;
  }
  var el = evt.target ? evt.target : evt.srcElement;
  var notNode = !startsWith(el.className, "treeNode ");
  if (notNode && action == "skip") {
    return;
  }
  if (notNode) { // find div element for clicked node
    el = findNodeByElement(el);
  }
  // check for non-leaf nodes
  if (!startsWith(el.className, "treeNode leafTreeNode")) {
    if (action == "skip") { // skip any click except [+]/[-]
      var e = null;
      for (var i = 0; i < el.childNodes.length; i++) {
        if (el.childNodes[i].nodeType != 3) {
          e = el.childNodes[i];
          break;
        }
      }
      if (e == null) { // create fake span element for calculating of padding
        var n = document.createElement("span");
        el.insertBefore(n, e);
        e = n;
      }
      if (evt.clientX >= findPosX(e)) {
        return;
      }
    }
    var opened = startsWith(el.className, "treeNode openedTreeNode");
    if (opened) { // fix "left space clicking" problem
      for (var i = 0; i < el.childNodes.length; i++) {
        var e = el.childNodes[i];
        if (startsWith(e.className, "treeNode ")) {
          if (evt.clientY >= findPosY(e)) {
            return;
          }
          break;
        }
      }
    }
    if (notNode && action == "open") {
      opened = false;
    }
    el.className = opened ? el.className.replace("treeNode openedTreeNode", "treeNode closedTreeNode") :
                   el.className.replace("treeNode closedTreeNode", "treeNode openedTreeNode");
  }
}

/**
 * Sets current node for the specified tree
 *
 * @param treeId id of the DIV which contains the tree
 * @param url url of the node which should be selected
 */
function setCurrentNode(url) {
  var len = url.length;
  var t = top.document.getElementById("projectsTree");
  if (!t) {
    return;
  }
  var aa = t.getElementsByTagName("a");
  for (var i = 0; i < aa.length; i++) {
    var a = aa[i];
    var el = findNodeByElement(a.parentNode);
    if (!el) return;
    el.className = el.className.replace(" currentTreeNode", "");
    if (len > 0) {
      var h = a.href;
      h = h.substring(h.length - len);
      if (h == url) {
        el.className += " currentTreeNode";
        do {
          el.className = el.className.replace(" closedTreeNode", " openedTreeNode");
          el = el.parentNode;
        } while (startsWith(el.className, "treeNode "));
      }
    }
  }
}

function initTree(parent) {
  var node = (parent != null) ? parent : top.document.getElementById("projectsTree");
  var children = node.childNodes;
  var count = 0;
  for (var i = 0; i < children.length; i++) {
    if (startsWith(children[i].className, "treeNode ")) {
      count++;
      initTree(children[i]);
    }
  }
  if (parent != null) {
    if (count == 0 && !startsWith(parent.className, "treeNode leafTreeNode")) {
      parent.className = "treeNode leafTreeNode";
    }
  }
}

/**
 * Resets the navigation tree.
 * @param newTreeEl element which contains new navigation tree
 * @todo preserve all already opened nodes and maybe scrolling position
 */
function resetTree(newTreeEl) {
  top.document.getElementById("projectsTree").innerHTML = newTreeEl.innerHTML;
  initTree();
}

/**
 * Resets the topic sets tree.
 * @param newTreeEl element which contains new topic sets tree
 * @todo preserve all already opened nodes and maybe scrolling position
 */
function resetTreeCustomization(newTreeEl) {
  top.document.getElementById("projectsTree").innerHTML = newTreeEl.innerHTML;
}

function hideAllWorkbenchTrees() {
  for (var custType in workbenchItemsLinks) {
    var el = document.getElementById("tree_" + custType);
    if (el) {
      el.style.display = 'none';
    }
  }
}
function workbenchItemChange(pDropdown) {
  hideAllWorkbenchTrees();
  loadRightPanel(workbenchItemsLinks[pDropdown.value]);
}
function changeWorkbenchItemsDropdown(pValue) {
  document.getElementById("workbench_items_dropdown").value = pValue;
}

/**
 * Switch trees divs.
 * @param divClass class of divs to switch.
 * @param divId id of div that will be shown.
 */
function switchTree(divId, needRefresh) {
  var treeToDisplay = top.document.getElementById("tree_" + divId);
  if (treeToDisplay && treeToDisplay.style.display == "none") {
    hideAllWorkbenchTrees();
    treeToDisplay.style.display = "";
    currentTree = dijit.byId(divId + "_dojo_Tree");
    if (!currentTree){
      var treeContentPane = dijit.byId("tree_" + divId);
      if (treeContentPane) {
        treeContentPane.refresh();
      }
      currentTree = dijit.byId(divId + "_dojo_Tree");
    }
    changeWorkbenchItemsDropdown(divId);
    if (needRefresh){
      var projContentPane = dijit.byId("activeProjectPane");
      if(projContentPane) {
        projContentPane.refresh();
      }
    }
  } else if (!currentTree){
    currentTree = dijit.byId(divId + "_dojo_Tree");
  }
}
