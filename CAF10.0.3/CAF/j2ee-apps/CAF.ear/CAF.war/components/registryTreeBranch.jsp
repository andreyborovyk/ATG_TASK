<%--
  This page fragment is called by registryTree.jsp, and recursively by itself,
  to display a specified registry folder, along with all open folders below it.
  
  See registryTree.jsp for an explanation of the page parameters that are used
  by this fragment.
  
  @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/components/registryTreeBranch.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core"           %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="caf"    uri="http://www.atg.com/taglibs/caf"          %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/web/assetregistry/Configuration"/>

  <%-- Get the contents of the registry folder whose path is given by the
       "displayedFolder" request attribute.  The "openFolder" attribute is
       also passed to the tag in order to determine how to set the "open"
       property on each of the returned folder items. --%>
  <caf:getRegistryItems var="items"
                        registryManagerPath="${requestScope.registryManagerPath}"
                        registry="${param.registry}"
                        parentFolder="${requestScope.displayedFolder}"
                        openFolder="${requestScope.openFolder}"
                        filter="${param.assetRegistryFilter}"/>

  <script language="JavaScript">
    // utility function to unescape HTML
    function unescapeHTML(html) {
      var htmlNode = document.createElement("DIV");
      htmlNode.innerHTML = html;
      if(htmlNode.innerText)
        return htmlNode.innerText; // IE
      return htmlNode.textContent; // FF
    }
  </script>

  <%-- Display each item in the folder --%>
  <c:forEach var="item" items="${items}">

    <%-- For a folder item, store the id, name, and path in the JavaScript
         folder data array. --%>
    <c:if test="${item.folder}">      
      <c:set var="folder" value="${item.asset}"/>
      <c:set var="folderPath" value="${requestScope.displayedFolder}${item.id}"/>
      <c:set var="folderPathName" value="${requestScope.displayedFolderName}${item.displayName}"/>
      <script language="JavaScript">
        defineFolderProperty(<c:out value="${requestScope.folderIndex}"/>,
                             "id",
                             "<c:out value='${folder.id}'/>");
        defineFolderProperty(<c:out value="${requestScope.folderIndex}"/>,
                             "name",
                             "<c:out value='${folder.name}'/>");
        defineFolderProperty(<c:out value="${requestScope.folderIndex}"/>,
                             "path",
                             "<c:out value='${folderPath}'/>");
        defineFolderProperty(<c:out value="${requestScope.folderIndex}"/>,
                             "pathDisplayName",
                             "<c:out value='${folderPathName}'/>");
      </script> 
      <c:if test="${folderPath eq param.selectedFolderPath}">
        <script language="JavaScript">
          selectedFolder = <c:out value="${requestScope.folderIndex}"/>;
        </script>
      </c:if>
    </c:if>
    
    <c:choose>
      <c:when test="${item.open}">
      
        <%-- The item is an open folder; when the user clicks the minus-sign
             icon, the tree is redisplayed with the item's parent folder as
             the deepest open folder. --%>
        <c:url var="openURL" value="/components/registryTree.jsp">
          <c:param name="open"                value="${requestScope.displayedFolder}"/>
          <c:param name="assetProperties"     value="${param.assetProperties}"/>
          <c:param name="assetRegistryFilter" value="${param.assetRegistryFilter}"/>
          <c:param name="foldersOnly"         value="${param.foldersOnly}"/>
          <c:param name="foldersSelectable"   value="${param.foldersSelectable}"/>
          <c:param name="nucleusProperties"   value="${param.nucleusProperties}"/>
          <c:param name="onFolderSelect"      value="${param.onFolderSelect}"/>
          <c:param name="onSelect"            value="${param.onSelect}"/>
          <c:param name="registry"            value="${param.registry}"/>
          <c:param name="registryManagerPath" value="${param.registryManagerPath}"/>
          <c:param name="showRoot"            value="${param.showRoot}"/>
        </c:url>

        <table width="100%" cellpadding="0" cellspacing="0">
          <tr>
            <td nowrap>
              <%-- Minus-sign icon --%>
              <dspel:a href="javascript:go('${openURL}')">
                <dspel:img src="${config.imageRoot}/folderActionMinus.gif" border="0" align="absmiddle"/>
              </dspel:a>
              <%-- If folders are selectable, use a hyperlink for the folder name --%>
              <c:choose>
                <c:when test="${param.foldersSelectable}">
                  <dspel:img src="${config.imageRoot}/folderOpen.gif" border="0" align="absmiddle"
                             onclick="folderSelected(${requestScope.folderIndex}, true)"/>
                  <dspel:a id="folder${requestScope.folderIndex}"
                           href="javascript:folderSelected(${requestScope.folderIndex}, true)">
                    <c:out value="${item.displayName}"/>
                  </dspel:a>
                </c:when>
                <c:otherwise>
                  <dspel:img src="${config.imageRoot}/folderOpen.gif" border="0" align="absmiddle"/>
                  <c:out value="${item.displayName}"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
        </table>
        
        <%-- Increment the sequence number for folder items. --%>
        <c:set scope="request" var="folderIndex" value="${requestScope.folderIndex + 1}"/>

        <%-- Temporarily append the folder ID to the displayedFolder path, so
             that we can display the folder's contents by recursively displaying
             this page fragment in the table below. --%>
        <c:set var="originalFolder" value="${requestScope.displayedFolder}"/>
        <c:set scope="request" var="displayedFolder" value="${originalFolder}${item.id}/"/>
        <c:set var="originalFolderName" value="${requestScope.displayedFolderName}"/>
        <c:set scope="request" var="displayedFolderName" value="${originalFolderName}${item.displayName}/"/>
        <table border="0" cellpadding="0" cellspacing="0" width="100%">
          <tr>
            <td width="15">
              &nbsp;
            </td>
            <td class="registryTreeFolderCell">              
              <dspel:include page="/components/registryTreeBranch.jsp"/>
            </td>
          </tr>
        </table>
        <c:set scope="request" var="displayedFolder" value="${originalFolder}"/>
        <c:set scope="request" var="displayedFolderName" value="${originalFolderName}"/>

      </c:when>
      <c:when test="${item.folder}">

        <%-- The item is a closed folder; when the user clicks the plus-sign
             icon, the tree is redisplayed with the item's folder as the
             deepest open folder. --%>
        <c:url var="openURL" value="/components/registryTree.jsp">
          <c:param name="open"                value="${requestScope.displayedFolder}${item.id}/"/>
          <c:param name="assetProperties"     value="${param.assetProperties}"/>
          <c:param name="assetRegistryFilter" value="${param.assetRegistryFilter}"/>
          <c:param name="foldersOnly"         value="${param.foldersOnly}"/>
          <c:param name="foldersSelectable"   value="${param.foldersSelectable}"/>
          <c:param name="nucleusProperties"   value="${param.nucleusProperties}"/>
          <c:param name="onFolderSelect"      value="${param.onFolderSelect}"/>
          <c:param name="onSelect"            value="${param.onSelect}"/>
          <c:param name="registry"            value="${param.registry}"/>
          <c:param name="registryManagerPath" value="${param.registryManagerPath}"/>
          <c:param name="showRoot"            value="${param.showRoot}"/>
        </c:url>

        <%-- Whether the folder is openable depends on if we are displaying
             child assets as well as child folders --%>
        <c:choose>
          <c:when test="${param.foldersOnly}">
            <c:set var="openable" value="${item.containsFolders}"/>
          </c:when>
          <c:otherwise>
            <c:set var="openable" value="${item.containsItems}"/>
          </c:otherwise>
        </c:choose>
        
        <table width="100%" cellpadding="0" cellspacing="0">
          <tr>
            <td nowrap>
              <c:choose>
                <%-- Plus-sign or blank icon --%>
                <c:when test="${openable}">
                  <dspel:a href="javascript:go('${openURL}')">
                    <dspel:img src="${config.imageRoot}/folderActionPlus.gif" border="0" align="absmiddle"/>
                  </dspel:a>
                </c:when>
                <c:otherwise>
                  <dspel:img src="${config.imageRoot}/folderActionNone.gif" border="0" align="absmiddle"/>
                </c:otherwise>
              </c:choose>
              <%-- If folders are selectable, use a hyperlink for the folder name --%>
              <c:choose>
                <c:when test="${param.foldersSelectable}">
                  <dspel:img src="${config.imageRoot}/folder.gif" border="0" align="absmiddle"
                             onclick="folderSelected(${requestScope.folderIndex}, true)"/>
                  <dspel:a id="folder${requestScope.folderIndex}"
                           href="javascript:folderSelected(${requestScope.folderIndex}, true)">
                    <c:out value="${item.displayName}"/>
                  </dspel:a>
                </c:when>
                <c:otherwise>
                  <dspel:img src="${config.imageRoot}/folder.gif" border="0" align="absmiddle"/>
                  <c:out value="${item.displayName}"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
        </table>
        
        <%-- Increment the sequence number for folder items. --%>
        <c:set scope="request" var="folderIndex" value="${requestScope.folderIndex + 1}"/>

      </c:when>
      <c:when test="${not param.foldersOnly}">
      
        <%-- The item is an asset rather than a folder.  First, call a
             JavaScript function to store the values of all the reqested
             properties for the asset. --%>
        <c:set var="asset" value="${item.asset}"/>
        <script language="JavaScript">
          <c:forTokens var="propertyName" items="${requestScope.assetProperties}" delims=",">
              var value = unescapeHTML("${asset[propertyName]}");
              defineAssetProperty(<c:out value="${requestScope.assetIndex}"/>,
                                  '<c:out value="${propertyName}"/>',
                                  value);
          </c:forTokens>
        </script> 

        <%-- If any Nucleus component properties were requested, store those
             as well. --%>
        <c:if test="${not empty param.nucleusProperties}">
          <script language="JavaScript">
            <c:forTokens var="propertyName" items="${param.nucleusProperties}" delims=",">
              <dspel:getvalueof var="propertyValue" bean="${asset.path}.${propertyName}"/>
              defineAssetProperty(<c:out value="${requestScope.assetIndex}"/>,
                                  '<c:out value="${propertyName}"/>',
                                  '<c:out value="${propertyValue}"/>');
            </c:forTokens>
          </script> 
        </c:if>


        <%-- If we are trying to pre-select an item, test this asset to see if
             it contains a property that matches the specified criteria, and,
             if so, save the item's index in JavaScript. --%>
        <c:if test="${not empty param.selectValue and asset[requestScope.selectProperty] eq param.selectValue}">
          <script language="JavaScript">
            selectedAsset = <c:out value="${requestScope.assetIndex}"/>;
          </script>
        </c:if>

        <table width="100%" cellpadding="0" cellspacing="0">
          <tr>
            <td nowrap>
              <c:if test="${requestScope.rootContainsFolders}">
                <%-- Blank icon --%>
                <dspel:img src="${config.imageRoot}/folderActionNone.gif" border="0" align="absmiddle"/>
                <%-- Item icon --%>
                <dspel:img src="${config.imageRoot}/registryItem.gif" border="0" align="absmiddle"
                           onclick="if (!event.shiftKey) assetSelected(${requestScope.assetIndex}, true)"/>
              </c:if>
              <%-- Hyperlink for selecting the item --%>
              <dspel:a id="asset${requestScope.assetIndex}" onclick="return !event.shiftKey" href="javascript:assetSelected(${requestScope.assetIndex}, true)">
                <c:out value="${item.displayName}"/>
              </dspel:a>
            </td>
          </tr>
        </table>
        
        <%-- Increment the sequence number for asset items. --%>
        <c:set scope="request" var="assetIndex" value="${requestScope.assetIndex + 1}"/>
      </c:when>
    </c:choose>
  </c:forEach>

</dspel:page>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/components/registryTreeBranch.jsp#2 $$Change: 651448 $--%>
