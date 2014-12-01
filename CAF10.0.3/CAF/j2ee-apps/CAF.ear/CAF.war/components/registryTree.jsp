<%--
  registryTree.jsp -- Displays a tree view of an asset registry.  It is
  intended to be included as an iframe in an enclosing page.

  Each folder is displayed as a node that (unless the folder is empty) can be
  opened by clicking on a plus sign icon.  At most one folder in each level of
  the hierarchy may be open at any time.  If a folder is open, it can be
  closed by clicking on a minus sign icon; additionally, its contents are
  displayed immediately below it as an indented list.

  The root folder is always displayed in an open state.

  An asset is displayed as an item that can be "selected" by clicking on it.
  Only one item can be selected at a time.  The currently selected item is
  displayed using a highlighted background.  Optionally, folders can also be
  selected.

  It is possible to specify parameters that pre-select an asset item, with the
  folder tree opened to display its parent folder.  It is also possible to
  specify the name of a JavaScript function in the enclosing page, which should
  be invoked when an item is selected, including a specified set of the selected
  item's properties.  See below for more details.

  @param  registry  (required)
  The name of the registry to be displayed.  Registries are defined as entries
  in the "registries" map of the associated RegistryManager component.

  @param  registryManagerPath  (optional; default: ${config.registryManagerPath})
  The Nucleus path of the AssetRegistryManager component to be used for
  accessing the given registry.

  @param  selectProperty  (optional; default: "id")
  The name of the asset property whose value should be compared with the given
  "selectValue" when determining whether to pre-select an asset item.

  @param  selectValue  (optional; default: null)
  The value with which an asset's "selectProperty" value is compared when
  determining whether to pre-select an asset item.

  @param  onSelect  (optional; default: null)
  The name of a JavaScript function in the enclosing page, which should be
  invoked when an asset is selected, either by the user or when pre-selecting
  during initialization.

  @param  assetProperties  (optional; default: "id,name")
  A list of names of asset properties whose values should be stored in an
  object passed to the "onSelect" function as a parameter.

  @param  nucleusProperties  (optional; default: null)
  A list of names of Nucleus component properties whose values should be stored
  in an object passed to the "onSelect" function as a parameter.  The selected
  asset is assumed to be a Nucleus component whose path is given by the "path"
  property of the asset.

  @param  assetRegistryFilter (optional; default: null)
  A nucleus component which is a filter for RegistryAssets in the tree.
  It implements AssetRegistryFilter.

  @param  foldersSelectable  (optional; default: false)
  If specified, then folders can be selected in addition to items.

  @param  onFolderSelect  (optional; default: null)
  The name of a JavaScript function in the enclosing page, which should be
  invoked when a folder is selected by the user.  (Pre-selection of a folder is
  currently not supported).

  @param  showRoot  (optional; default: false)
  If specified, then the root folder is displayed.

  @param  foldersOnly  (optional; default: false)
  If specified, then only folders, not assets, are displayed.

  @param  open  (used internally)
  The path of a folder to be displayed in an open state.  Used for managing the
  mechanics of opening and closing folders.

  @param  x, y  (used internally)
  The offset, in pixels, to which the tree window should be scrolled within its
  parent iframe.  Used for maintaining the proper scroll position when opening
  and closing folders.

  Example of usage:

    <c:url var="treeURL" value="/components/registryTree.jsp">
      <c:param name="registry"        value="promotions"/>
      <c:param name="selectProperty"  value="repositoryId"/>
      <c:param name="selectValue"     value="${promotionId}"/>
      <c:param name="onSelect"        value="itemSelected"/>
      <c:param name="assetProperties" value="repositoryId,name,description"/>
    </c:url>

    <script language="JavaScript">
      function itemSelected(item) {
        document.getElementById("promotionIdField").value = item.repositoryId;
        document.getElementById("promotionNameField").innerText = item.name;
        document.getElementById("promotionDescriptionField").innerText = item.description;
      }
    </script>

  @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/components/registryTree.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core"           %>
<%@ taglib prefix="caf"    uri="http://www.atg.com/taglibs/caf"          %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetregistry/Configuration"/>

  <%-- Determine the registry manager component. --%>
  <c:set scope="request" var="registryManagerPath" value="${param.registryManagerPath}"/>
  <c:if test="${empty requestScope.registryManagerPath}">
    <c:set scope="request" var="registryManagerPath" value="${config.registryManagerPath}"/>
  </c:if>

  <%-- Always begin the display with the root folder --%>
  <c:set scope="request" var="displayedFolder" value="/"/>
  <c:set scope="request" var="displayedFolderName" value="/"/>
  <caf:getRegistryRootFolder var="root"
                             registryManagerPath="${requestScope.registryManagerPath}"
                             registry="${param.registry}"/>
  <c:set scope="request" var="rootContainsFolders" value="${root.containsFolders}"/>

  <%-- Initialize the sequence number for asset items --%>
  <c:set scope="request" var="assetIndex" value="0"/>

  <%-- Initialize the sequence number for folder items --%>
  <c:set scope="request" var="folderIndex" value="0"/>

  <%-- Use default assetProperties if unspecified --%>
  <c:set scope="request" var="assetProperties" value="${param.assetProperties}"/>
  <c:if test="${empty requestScope.assetProperties}">
    <c:set scope="request" var="assetProperties" value="id,name"/>
  </c:if>

  <%-- Determine the path of the deepest folder that should be displayed in an
       open state.  If an item is being pre-selected, we determine this using
       a tag that finds an asset item which matches the given property and
       returns its parent folder path.  Otherwise, we just use the path
       specified by the "open" parameter (if any). --%>
  <c:choose>
    <c:when test="${not empty param.selectValue}">

      <%-- Use default selectProperty if unspecified --%>
      <c:set scope="request" var="selectProperty" value="${param.selectProperty}"/>
      <c:if test="${empty requestScope.selectProperty}">
        <c:set scope="request" var="selectProperty" value="id"/>
      </c:if>

      <caf:findRegistryFolderPath scope="request" var="openFolder"
                                  registryManagerPath="${requestScope.registryManagerPath}"
                                  registry="${param.registry}"
                                  property="${requestScope.selectProperty}"
                                  value="${param.selectValue}"/>
    </c:when>
    <c:otherwise>
      <c:set scope="request" var="openFolder" value="${param.open}"/>
    </c:otherwise>
  </c:choose>

  <%-- Just open the root folder if no other folder was specified --%>
  <c:if test="${empty requestScope.openFolder}">
    <c:set scope="request" var="openFolder" value="/"/>
  </c:if>

  <%-- Ensure that we have values for the scroll coordinates --%>
  <c:set var="scrollX" value="${param.x}"/>
  <c:if test="${empty scrollX}">
    <c:set var="scrollX" value="0"/>
  </c:if>
  <c:set var="scrollY" value="${param.y}"/>
  <c:if test="${empty scrollY}">
    <c:set var="scrollY" value="0"/>
  </c:if>

  <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
  <html>

    <head>
      <title></title>
      <dspel:link href="${config.styleSheet}" rel="stylesheet" type="text/css"/>

      <script language="JavaScript">

        // Array that stores one object per selectable asset item.
        var selectableAssets = new Array();

        // The index of the currently selected asset item.
        var selectedAsset = null;

        // Array that stores one object per selectable folder item.
        var selectableFolders = new Array();

        // The index of the currently selected folder item.
        var selectedFolder = null;

        // This function is called after the page is fully loaded, with the
        // page body specified as a parameter.
        function pageLoaded(bodyElement) {

          // If required, scroll the page within its parent frame.
          var x = <c:out value="${scrollX}"/>;
          var y = <c:out value="${scrollY}"/>;
          if (x != 0 || y != 0)
            bodyElement.scroll(x, y);

          // Select an asset or folder, if necessary.
          var selectedElem = null;
          if (selectedAsset != null) {
            assetSelected(selectedAsset, false);
            selectedElem = document.getElementById("asset" + selectedAsset);
          }
          else if (selectedFolder != null) {
            folderSelected(selectedFolder, false);
            selectedElem = document.getElementById("folder" + selectedFolder);
          }
          if (selectedElem)
            selectedElem.scrollIntoView();

          return true;
        }

        // This function is used to redisplay this page using its current
        // scroll coordinates.  It should be called by anchor tags that need
        // to redisplay this page.  The URL is assumed to point back to this
        // page, and is also assumed to contain at least one parameter.
        // Additional parameters are added to the URL to specify the current
        // scroll coordinates.
        function go(url) {
          document.location = url +
                              '&x=' + document.body.scrollLeft +
                              '&y=' + document.body.scrollTop;
        }

        // Add a property name/value pair to the asset data array entry with
        // the given index.
        function defineAssetProperty(index, propertyName, propertyValue) {

          // Create the array entry if not already done.
          if (! selectableAssets[index])
            selectableAssets[index] = new Array();
          selectableAssets[index][propertyName] = propertyValue;
        }

        // Add a property name/value pair to the folder data array entry with
        // the given index.
        function defineFolderProperty(index, propertyName, propertyValue) {

          // Create the array entry if not already done.
          if (! selectableFolders[index])
            selectableFolders[index] = new Array();
          selectableFolders[index][propertyName] = propertyValue;
        }

        // This function clears the currently selected item.
        function clearSelection() {

          var selectedElem = null;

          if (selectedAsset != null)
            selectedElem = document.getElementById("asset" + selectedAsset);
          else if (selectedFolder != null)
            selectedElem = document.getElementById("folder" + selectedFolder);

          if (selectedElem)
            selectedElem.className = "registryTreeItem";

          selectedAsset = null;
          selectedFolder = null;
        }

        // This function is called when an asset item of a given index is
        // selected.
        function assetSelected(index, callHandler) {

          var selectedElem;

          clearSelection();

          // Highlight the asset with the selected index, and save the index.
          selectedElem = document.getElementById("asset" + index);
          if (selectedElem)
            selectedElem.className = "registryTreeHighlight";
          selectedAsset = index;

          // Call our event handler, passing the object corresponding to the
          // selected item.
          <c:if test="${not empty param.onSelect}">
            if (callHandler)
              <c:out value="parent.${param.onSelect}(selectableAssets[index]);"/>
          </c:if>
        }

        // This function is called when a folder item of a given index is
        // selected.
        function folderSelected(index, callHandler) {

          var selectedElem;

          clearSelection();

          // Highlight the folder with the selected index, and save the index.
          selectedElem = document.getElementById("folder" + index);
          if (selectedElem)
            selectedElem.className = "registryTreeHighlight";
          selectedFolder = index;

          // Call our event handler, passing the object corresponding to the
          // selected item.
          <c:if test="${not empty param.onFolderSelect}">
            if (callHandler)
              <c:out value="parent.${param.onFolderSelect}(selectableFolders[index]);"/>
          </c:if>
        }

      </script>
    </head>

    <body class="browseTree" onload="pageLoaded(this)">

      <c:if test="${not empty param.showRoot}">

        <%-- Store the root folder id, name, and path in the JavaScript
             folder data array. --%>
        <script language="JavaScript">
          defineFolderProperty(0, "root", "true");
          defineFolderProperty(0, "id",
                               "<c:out value='${root.id}'/>");
          defineFolderProperty(0, "name",
                               "<c:out value='${root.displayName}'/>");
          defineFolderProperty(0, "path", "/");
          defineFolderProperty(0, "pathDisplayName", "/");
        </script>
        <c:if test="${'/' eq param.selectedFolderPath}">
          <script language="JavaScript">
            selectedFolder = 0;
          </script>
        </c:if>

        <table width="100%" cellpadding="0" cellspacing="0">
          <tr>
            <td nowrap>
              <%-- If folders are selectable, use a hyperlink for the folder name --%>
              <c:choose>
                <c:when test="${param.foldersSelectable}">
                  <dspel:img src="${config.imageRoot}/folder.gif" border="0" align="absmiddle"
                             onclick="folderSelected(0, true)"/>
                  <dspel:a id="folder0" href="javascript:folderSelected(0, true)">
                    /&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                  </dspel:a>
                </c:when>
                <c:otherwise>
                  <dspel:img src="${config.imageRoot}/folder.gif" border="0" align="absmiddle"/>
                  /
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
        </table>

        <%-- Increment the sequence number for folder items --%>
        <c:set scope="request" var="folderIndex" value="1"/>
      </c:if>

      <%-- Use the registryTreeBranch fragment to display the tree, starting at
           the root folder. --%>
      <dspel:include page="/components/registryTreeBranch.jsp"/>
    </body>
  </html>
</dspel:page>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/components/registryTree.jsp#2 $$Change: 651448 $--%>
