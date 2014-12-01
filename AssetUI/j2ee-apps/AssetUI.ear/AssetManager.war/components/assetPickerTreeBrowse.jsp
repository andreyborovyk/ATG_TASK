<%--
  Browse tab for the asset picker customized to work with the tree.
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="ee"     uri="http://www.atg.com/taglibs/expreditor_rt"         %>

<!-- Begin browse.jsp -->

<dspel:page>

  <dspel:importbean var="webUIConfig"   bean="/atg/web/Configuration"/>
  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <c:choose>
    <c:when test="${ ! empty param.assetInfo }">
      <c:set var="assetInfoPath" value="${param.assetInfo}"/>
    </c:when>
    <c:otherwise>
      <c:set var="assetInfoPath"
        value="/atg/epub/servlet/ProcessAssetInfo"/>
    </c:otherwise>
  </c:choose>
  <dspel:importbean bean="${assetInfoPath}" var="assetInfo"/>

  <c:set var="assetBrowserTreeComponentName" value="${config.assetPickerTreePath}"/>

  <%-- Clear the checked nodes every time we load the page.  
       This means that when you change asset picker tabs (e.g. search to browse to search)
       you will lose the state of the checked items. --%> 
  <dspel:setvalue bean="${assetBrowserTreeComponentName}.checkedNodeIds" value="${null}"/>
  <c:set var="debug" value="false"/>

  <dspel:importbean var="assetPickerTree"
                    bean="${assetBrowserTreeComponentName}"/>
  <dspel:importbean var="preferences"
                    bean="/atg/web/assetmanager/Preferences"/>

        <%-- setup some stuff in advance --%>
  <c:choose>
    <c:when test="${empty assetInfo.attributes.treeComponent}">
      <c:set var="notree" value="true"/>
    </c:when>
    <c:otherwise>
      <c:if test="${not empty assetInfo.attributes.getPickableTypesFromTree}">
        <dspel:importbean var="pickableTypesTree" bean="${assetInfo.attributes.getPickableTypesFromTree}"/>
        <c:if test="${empty pickableTypesTree.parentTypeForCheckedNodes}">
          <c:set var="novaliddest" value="true"/>
        </c:if>
      </c:if>
    </c:otherwise>
  </c:choose>


  <c:choose>
    <c:when test="${novaliddest}">
      <fmt:message var="nodest" key="assetPicker.error.noValidDestinations"/>
      <script type="text/javascript">
         parent.hideIframe( "browser" );
         parent.messages.addError("<c:out value='${nodest}'/>");
      </script>
    </c:when>

    <c:otherwise>

      <div id="assetBrowserContentBody">

        <%-- now getting sourceTree component path from assetInfo which is set in the asset picker container --%>
        <dspel:importbean var="sourceTree"
                          bean="${ assetInfo.attributes.treeComponent }"/>

        <%-- if we're displaying an extra field, then show the invisible assetListHeader --%> 
        <ee:isMultisiteMode var="multisiteMode"/>
        <web-ui:getSiteTreeNodeFilter var="siteTreeNodeFilter" tree="${sourceTree.globalTree}"/>
        <c:choose>
          <%-- site --%>
          <c:when test="${ multisiteMode and not empty siteTreeNodeFilter}">
            <c:set var="siteManagerPath" value="/atg/multisite/SiteManager" />
            <dspel:importbean var="siteManager" bean="${ siteManagerPath }" />
        
            <div id="assetListHeader">
              <div id="assetListHeaderRight"></div>
              <div id="assetListHeaderLeft">
                <div id="assetBrowserSite" style="font-weight: bold">
                  <c:url var="assetBrowserSiteChangeURL" value="/assetPicker/assetPicker.jsp">
                    <c:param name="assetInfo" value="${assetInfoPath}"/>
                  </c:url>
                  <dspel:form name="assetBrowserSiteForm" action="${assetBrowserSiteChangeURL}" method="post">
                    <fmt:message key="assetPicker.site"/>
                    <dspel:select id="assetBrowserSiteSelect" bean="${siteTreeNodeFilter.absoluteName}.siteId" onchange="this.form.submit()">
                      <dspel:option value=""><fmt:message key="assetPicker.site.allSites"/></dspel:option>
                      <c:forEach var="site" items="${siteManager.allSites}">
                        <dspel:option value="${site.repositoryId}"><c:out value="${site.itemDisplayName}" /></dspel:option>
                      </c:forEach>
                    </dspel:select>
                  </dspel:form>
                </div>
              </div>
            </div>
          </c:when>
          <c:when test="${ empty assetInfo.attributes.extraFieldId }">
            <div id="assetListHeader">
              <div id="assetListHeaderRight"></div>
              <div id="assetListHeaderLeft"></div>
            </div>
          </c:when>
          <c:otherwise>
            <div id="assetListHeaderInvisible">
            </div>
          </c:otherwise>
        </c:choose>

        <c:choose>
          <c:when test="${notree}">
            <div id="scrollContainer">
              <br />
              <br />
              <fmt:message key="assetPicker.error.typeNotBrowsable"/>
              <br />
              <br />
            </div>
          </c:when>
          <c:otherwise>

            <%-- set up the AssetBrowserTreeState component based on the treeComponent passed in  --%>
            <c:set var="dummy" value="${assetPickerTree.resetToDefault}"/>

            <c:set target="${assetPickerTree}" property="globalTree" value="${sourceTree.globalTree}"/>
            <c:set target="${assetPickerTree}" property="openNodeIds" value="${sourceTree.openNodeIds}"/>
            <c:set target="${assetPickerTree}" property="checkedNodeIdsString" value=""/>

            <c:if test="${debug}">
              DEBUG: pickableTypesTree = <c:out value="${assetInfo.attributes.getPickableTypesFromTree}"/>
            </c:if>

            <%-- decide whether to get checkable types from the tree or from the standard asset picker place --%>
            <c:choose>
              <c:when test="${not empty assetInfo.attributes.getPickableTypesFromTree}">
                <dspel:importbean var="pickableTypesTree"
                                  bean="${assetInfo.attributes.getPickableTypesFromTree}"/>
                <c:set target="${assetPickerTree}" property="checkableTypes"
                       value="${pickableTypesTree.parentTypeForCheckedNodes}"/>
              </c:when>
              <c:when test="${not empty assetInfo.attributes.itemTypes}">
                <c:forTokens var="itemType" items="${assetInfo.attributes.itemTypes}" delims=",">
                  <c:set target="${assetPickerTree}" property="addCheckableType" value="${itemType}"/>
                </c:forTokens>
              </c:when>
              <c:otherwise>
                <c:set target="${assetPickerTree}" property="addCheckableType" value="${ assetInfo.attributes.itemType }"/>
              </c:otherwise>
            </c:choose>


            <%-- find all subtypes of the checkable type(s) and make them checkable too  --%>
            <web-ui:invoke var="checkabletypes" bean="${assetPickerTree.checkableTypes}" method="clone"/>
            <c:forEach var="checkabletype" items="${checkabletypes}" varStatus="status">
              <web-ui:getTreeContainerPath var="containerPath" 
                                           treeDefinition="${assetPickerTree.treeDefinition}" 
                                           itemType="${checkabletype}" />
              <web-ui:getAssetSubTypes var="subtypes"
                                       containerPath="${containerPath}"
                                       assetType="${checkabletype}"/>
              <c:forEach var="subtype" items="${subtypes}" varStatus="loop">
                <c:set target="${assetPickerTree}" property="addCheckableType" value="${subtype}"/>
              </c:forEach>
            </c:forEach>

            <%-- determine if this is a content tree --%>
            <web-ui:isAssignableFrom var="isContentTree" className="${assetPickerTree.treeDefinition.treeNodeFactory.class.name}" instanceOfClassName="atg.web.tree.repository.content.ContentRepositoryTreeNodeFactory"/>

            <%-- determine if you want checkboxes/radio buttons --%>
            <c:if test="${ ! empty assetInfo.attributes.isAllowMultiSelect }">
              <c:set var="isAllowMultiSelect" value="${assetInfo.attributes.isAllowMultiSelect}"/>
            </c:if>

            <script type="text/javascript" >
              //
              // checkedItems - array of checkedItem objects
              //
              var checkedItems = new Array();

              //
              // This is the method called by the asset picker container
              // which returns the action string in as a parameter to the
              // parent Javascript method defined by picker.onSelect
              //
              // The reason that it's passing an array, is that there are
              // other users of the asset picker that expect an array of
              // objects
              //
              // We can change the name of "getSelectedAssets()" to something
              // more generic if you like
              //
              function getSelectedAssets() {
                //alert("DEBUG assetPickerTreeBrowse.jsp: What will the asset picker return? " + checkedItems[0].uri);
                return checkedItems;
              }

              //
              // this is invoked when a tree node is checked or unchecked
              //
              function checkNode(path, info) {
                // if the node is checked, we are unchecking it, so search for the item
                  var newcheckeditems = new Array();
                  var jj = 0;
                  var founditem = false;
				for (var ii=0; ii<checkedItems.length; ii++) {
                  if (checkedItems[ii].uri == info.URI) {
                    checkedItems[ii] = null;
                    founditem = true;
                  }
                  else {
                    newcheckeditems[jj++] = checkedItems[ii];
                  }
                }

                if (founditem) {
                  checkedItems = newcheckeditems;
                  return;
                }

                var selectedAsset = getAssetType();

                // item was not found among the checked nodes.
                var checkedItem = new Object();
                checkedItem.uri = info.URI;
                checkedItem.displayName = info.label;
                if (selectedAsset.typeCode == "file") {
                  checkedItem.id = info.itemPath;
                 } else {
                  checkedItem.id = info.id;
                }
                <c:choose>
                  <c:when test="${ isAllowMultiSelect }">
                    checkedItems[checkedItems.length] = checkedItem;
                  </c:when>
                  <c:otherwise>
                    checkedItems[0] = checkedItem;
                 </c:otherwise>
                </c:choose>
              }
            </script>

            <form id="treeBrowseForm" name="treeBrowseForm" action="#">
              <c:url var="treeURL" value="amTreeFrame.jsp">
              <%-- <c:url var="treeURL" context="${webUIConfig.contextRoot}" value="/tree/treeFrame.jsp"> --%>
                <c:param name="emptyTreeViewContextRoot" value="${config.contextRoot}"/>
                <c:param name="emptyTreeView"          value="/components/assetPickerTreeEmpty.jsp"/>
                <c:param name="headIncludeContextRoot" value="${config.contextRoot}"/>
                <c:param name="headIncludePage"        value="/components/head.jsp"/>
                <c:param name="treeComponent"          value="${assetBrowserTreeComponentName}"/>
                <c:param name="onCheck"                value="checkNode"/>
                <c:param name="onUncheck"              value="checkNode"/>
                <c:param name="showToolTips"           value="${preferences.showIdToolTips}"/>
                <c:choose>
                  <c:when test="${isContentTree}">
                    <c:param name="onCheckProperties"  value="id,label,URI,itemPath"/>
                  </c:when>
                  <c:otherwise>
                    <c:param name="onCheckProperties"  value="id,label,URI"/>
                  </c:otherwise>
                </c:choose>
                <c:choose>
                  <c:when test="${ isAllowMultiSelect }">
                    <c:param name="selectorControl" value="checkbox"/>
                  </c:when>
                  <c:otherwise>
                    <c:param name="selectorControl" value="radio"/>
                  </c:otherwise>
                </c:choose>
              </c:url>

              <dspel:iframe id="scrollContainer" src="${treeURL}" style="border: none"/>
            </form>

          </c:otherwise> <%-- test for no sourceTree --%>
        </c:choose>



        <table id="scrollFooter">
          <tr>
            <td id="footerCount">
              <div id="footerCountRight"></div>
              <div id="footerCountLeft"></div>
            </td>
          </tr>
        </table>
      </div>
    </c:otherwise> <%-- test for no checkable types --%>
  </c:choose>

</dspel:page>
<!-- End browse.jsp -->
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/assetPickerTreeBrowse.jsp#2 $$Change: 651448 $--%>
