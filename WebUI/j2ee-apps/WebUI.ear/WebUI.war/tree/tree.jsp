<%@ page pageEncoding="UTF-8" %>

<%--
  This JSP fragment implements a control that displays a tree model obtained
  from the server using the interface atg.web.tree.TreeState.
  
  Currently, it is included by treeFrame.jsp, which is a complete HTML page that
  is intended to be enclosed in an iframe.  Eventually, it should be possible
  for clients that wish to avoid the use of iframes to include tree.jsp directly,
  but this doesn't work yet.
  
  Note that any page which includes this page must add to its body.onload
  handler a function with the signature "atgTreeFrameLoaded()".

  @param  treeComponent
  The path of the server-side component that manages the tree model.
  
  @param  styleSheet
  The context path of the style sheet for the UI elements.  This page uses the
  following style elements:
    .treeFrame:        The body of the iframe container
    .treeCell:         A cell in the tree
    .treeNode:         The label of a tree node
    .treeNodeSelected: The label of a tree node that is selected
    .openIcon:         An <a> tag with a plus-sign icon
    .closeIcon:        An <a> tag with a minus-sign icon
  
  @param  onUnselect
  (optional) The name of a JavaScript function to be called when the user
  clicks a node to select it causing the currently selected node to be unselected.

  @param  onSelect
  (optional) The name of a JavaScript function to be called when the user
  clicks a node to select it.
  
  @param  onSelectProperty
  A single property of the selected item that is to be passed to the onSelect
  function.  NB: We need to change this to a list of properties.
  
  @param  rootNodeId
  (optional) The ID of the tree node that is to be displayed as the root node.
  
  @param  indent
  Used internally to indicate that the node should be displayed indented.
  
  @param  onLoad
  The name of a JavaScript function to be called after the page has loaded.
  
  @param  nodeIconRoot
  This is a temporary parameter that specifies the context path of the root
  folder where all node images are located.  NB: We will replace this with a
  more flexible system.
  
  @param selectorControl 
  Set this string to one of radio, checkbox, or none to determine how tree nodes 
  are selected.  

  @param showToolTips
  Set this to true to enable a node's toolTipText property to be displayed when 
  the user hovers the mouse over the node.  Leave blank or set to false to show 
  no tool tips. 
  
  @param shouldScrollIntoView
  Set this to false if you do not want the tree to auto scroll to the current
  selected node.  This should be used if the tree is experiencing issues with 
  the layout - for example if other page content is being scrolled.

  -------
  
  This page traverses the entire server-side representation of the tree model
  and displays every node that is visible because its parent is currently
  expanded.  It provides buttons for opening and closing nodes, as well as for
  selecting nodes.
  
  When the user closes a node, its associated display elements are hidden from
  view.  When the user opens a node that was previously closed, the hidden
  display elements are brought back into view.  However, if the node has never
  been opened, its contents must be obtained from the server.  This is
  accomplished by asking the server to render the contents of the specified
  child node (i.e. a subset of the tree) using treeFrame.jsp.  The rendering is
  performed in a hidden iframe located at the bottom of this page.  After the
  iframe is finished rendering, it calls a JavaScript function (actionFrameLoaded)
  in this page, which copies the contents of the iframe into a div on this page
  which is designated as the parent container for displaying the node's children.
  
  Whenever the user performs any action that affects the state of the tree
  model, the action must be transmitted back to the server.  This is
  accomplished by asking the server to render in a hidden iframe a special
  page (treeActionFrame.jsp), whose sole purpose is to invoke a tag that
  performs a specified action on a specified tree model node.
  
  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/tree/tree.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<dspel:page>
  
  <dspel:importbean var="config" bean="/atg/web/Configuration"/>
  
  <%-- Unless a root node is specified, begin the display with the root folder --%>
  <c:set scope="request" var="nodePath" value="${null}"/>
  <c:if test="${not empty param.rootNodeId}">
    <c:set scope="request" var="nodePath" value="${param.rootNodeId}"/>
  </c:if>
  
  <%-- Create action URLs --%>
  <c:url var="populateNodeURL" context="/WebUI" value="/tree/treeFrame.jsp">
    <c:param name="treeComponent"    value="${param.treeComponent}"/>
    <c:param name="indent"           value="1"/>
    <c:param name="onLoad"           value="actionFrameLoaded"/>
    <c:param name="styleSheet"       value="${param.styleSheet}"/>
    <c:param name="onSelectProperties" value="${param.onSelectProperties}"/>
    <c:param name="onCheckProperties"  value="${param.onCheckProperties}"/>
    <c:param name="nodeIconRoot"     value="${param.nodeIconRoot}"/>
    <c:param name="selectorControl"  value="${param.selectorControl}"/>
    <c:param name="showToolTips"     value="${param.showToolTips}"/>
  </c:url>
  <c:url var="openURL" context="/WebUI" value="/tree/treeActionFrame.jsp">
    <c:param name="treeComponent" value="${param.treeComponent}"/>
    <c:param name="action"        value="open"/>
  </c:url>
  <c:url var="closeURL" context="/WebUI" value="/tree/treeActionFrame.jsp">
    <c:param name="treeComponent" value="${param.treeComponent}"/>
    <c:param name="action"        value="close"/>
  </c:url>
  <c:url var="checkURL" context="/WebUI" value="/tree/treeActionFrame.jsp">
    <c:param name="treeComponent" value="${param.treeComponent}"/>
    <c:param name="action"        value="check"/>
  </c:url>
  <c:url var="clearAllAndCheckURL" context="/WebUI" value="/tree/treeActionFrame.jsp">
    <c:param name="treeComponent" value="${param.treeComponent}"/>
    <c:param name="action"        value="clearAllAndCheck"/>
  </c:url>
  <c:url var="uncheckURL" context="/WebUI" value="/tree/treeActionFrame.jsp">
    <c:param name="treeComponent" value="${param.treeComponent}"/>
    <c:param name="action"        value="uncheck"/>
  </c:url>
  <c:url var="selectURL" context="/WebUI" value="/tree/treeActionFrame.jsp">
    <c:param name="treeComponent" value="${param.treeComponent}"/>
    <c:param name="action"        value="select"/>
  </c:url>

  <script type="text/javascript"
          src="<c:out value='${config.contextRoot}'/>/scripts/tree.js">
  </script>

  <script type="text/javascript">
  
    var tree = new WebTree();

    tree.populateNodeURL = "<c:out value='${populateNodeURL}' escapeXml='false'/>";
    tree.openURL = "<c:out value='${openURL}' escapeXml='false'/>";
    tree.closeURL = "<c:out value='${closeURL}' escapeXml='false'/>";
    tree.checkURL = "<c:out value='${checkURL}' escapeXml='false'/>";
    tree.clearAllAndCheckURL = "<c:out value='${clearAllAndCheckURL}' escapeXml='false'/>";
    tree.uncheckURL = "<c:out value='${uncheckURL}' escapeXml='false'/>";
    tree.selectURL = "<c:out value='${selectURL}' escapeXml='false'/>";
    tree.shouldScrollIntoView = ("<c:out value="${param.shouldScrollIntoView}"/>" == "false")? false : true;
    

    <c:if test="${not empty param.onLoad}">
      if (parent.<c:out value="${param.onLoad}"/>)
        tree.onLoad = parent.<c:out value="${param.onLoad}"/>;
    </c:if>

    <c:if test="${not empty param.onSelect}">
      if (parent.<c:out value="${param.onSelect}"/>)
        tree.onSelect = parent.<c:out value="${param.onSelect}"/>;
    </c:if>

    <c:if test="${not empty param.onUnselect}">
      if (parent.<c:out value="${param.onUnselect}"/>)
        tree.onUnselect = parent.<c:out value="${param.onUnselect}"/>;
    </c:if>

    <c:if test="${not empty param.onCheck}">
      if (parent.<c:out value="${param.onCheck}"/>)
        tree.onCheck = parent.<c:out value="${param.onCheck}"/>;
    </c:if>

    <c:if test="${not empty param.onUncheck}">
      if (parent.<c:out value="${param.onUncheck}"/>)
        tree.onUncheck = parent.<c:out value="${param.onUncheck}"/>;
    </c:if>

    //------------------------------------------------------------------------//
    // This is called after the given node's children have been pulled over
    // from the server and displayed in the hidden iframe.  We need to copy
    // the display from the iframe into the proper location in the main window.        
    function actionFrameLoaded(path) {
      tree.populatePath(path);
    }

    //------------------------------------------------------------------------//
    // This function is called after the page is fully loaded.  It must be
    // invoked by the body.onload handler of any page that includes this
    // fragment.
    function atgTreeFrameLoaded() {

      tree.initializeAfterLoad();
      
      <c:if test="${not empty param.onLoad}">
        // If this tree is being displayed in the hidden iframe, notify the
        // main tree that we are ready to have our contents copied.
        if (parent.<c:out value="${param.onLoad}"/>)
          parent.<c:out value="${param.onLoad}"/>("<c:out value='${param.rootNodeId}'/>");
      </c:if>
    }

  </script>

  <%-- Use a treeBranch fragment to display the tree, starting at
       the specified root folder. --%>
  <c:choose>
    <c:when test="${not empty param.indent}">
      <dspel:include page="/tree/treeBranchIndented.jsp"/>
    </c:when>
    <c:otherwise>
      <dspel:include page="/tree/treeBranch.jsp"/>
    </c:otherwise>
  </c:choose>

  <%-- This is a hidden div that allows tree elements to send information
       back to the server --%>
  <div style="display: none">
    <dspel:iframe id="actionFrame" page="/tree/treeActionFrame.jsp"/>
  </div>

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/tree/tree.jsp#2 $$Change: 651448 $--%>
