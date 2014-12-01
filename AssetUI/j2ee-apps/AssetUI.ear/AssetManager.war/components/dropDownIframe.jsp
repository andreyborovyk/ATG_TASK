<%--
  dropdown menu

  params:
    section      this value comes from the id or a portion of the href of the link that was used to open this page
                 For example, this one uses the id because the href was not specified.
                  <a class="newButtonOn iconDD" id="newButton" title="<c:out value='${createButtonText}'/>">
                 This one has an href, and the section will be "foo", the portion between the last / and the .
                  <a href="/blarg/foo.jsp" class="newButtonOn iconDD" title="<fmt:message key='foobutton'/>"/>


  This page renders a view selector and includes the appropriate page with the appropriate
  tree component.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/dropDownIframe.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="biz"      uri="http://www.atg.com/taglibs/bizui"                 %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>

<dspel:page>


  <%-- unpack the param(s) --%>
  <dspel:getvalueof var="section" param="section"/>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="sessionInfo"
                    bean="/atg/web/assetmanager/SessionInfo"/>
  <dspel:importbean var="profile"
                    bean="/atg/userprofiling/Profile"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set var="debug" value="false"/>

  <c:set scope="request" var="managerConfig" value="${sessionInfo.taskConfiguration}"/>

  <pws:getCurrentProject var="projectContext"/>

  <c:set var="project" value="${projectContext.project}"/>

  <c:set var="checkedItemCount" value="${sessionInfo.checkedItemCount}"/>

  <%-- set up some stuff for multiedit --%>
  <c:if test="${sessionInfo.assetManagerTab eq 'multiEdit'}">

    <%-- edit or view mode, depending on project status --%>
    <c:choose>
      <c:when test="${empty project or project.editable}">
        <c:set var="mode" value="AssetManager.multiEdit"/>
        <c:set var="editable" value="true"/>
      </c:when>
      <c:otherwise>
        <c:set var="mode" value="AssetManager.view"/>
        <c:set var="editable" value="false"/>
      </c:otherwise>
    </c:choose>

    <c:if test="${debug}">
      [dropDownIFrame] project: <c:out value="${project}"/><br/>
      [dropDownIFrame] mode: <c:out value="${mode}"/><br/>
    </c:if>

    <%--
      get the property groups for the given item type.  if the list is unfiltered, then look for a common type
    --%>
    <c:set var="currentFilterType" value="${sessionInfo.currentFilterType}"/>
    <c:set var="currentFilterTypeContainerPath" value="${sessionInfo.currentFilterTypeContainerPath}"/>
    <c:set var="currentFilterTypeName" value="${sessionInfo.currentFilterTypeName}"/>
    <c:choose>
      <c:when test="${empty currentFilterType}">
        <c:set var="commonTypes" value="${sessionInfo.commonCheckedTypes}"/>
        <dspel:droplet name="/atg/dynamo/droplet/ForEach">
          <dspel:param name="array" value="${commonTypes}" />
          <dspel:oparam name="output">
            <dspel:getvalueof var="typeInfo" param="element"/>

            <c:set var="repositoryPath" value="${typeInfo[0]}"/>
            <c:set var="theType" value="${typeInfo[1]}"/>
            <!-- filter was set so there just one type to worry about -->
            <asset-ui:getItemMappingInfo itemDescriptorName="${theType}"
                                         repositoryPath="${repositoryPath}"
                                         mode="${mode}"
                                         taskConfig="${managerConfig}"
                                         var="imapInfo"/>

            <c:if test="${debug}">
              [dropDownIFrame] imapInfo mode: <c:out value="${imapInfo.mode}"/><br/>
              [dropDownIFrame] imapInfo name: <c:out value="${imapInfo.name}"/><br/>
            </c:if>

            <c:catch var="exception">
              <biz:getItemMapping itemPath="${repositoryPath}"
                                  itemName="${theType}"
                                  var="imap"
                                  mode="${imapInfo.mode}"
                                  readOnlyMode="AssetManager.view"
                                  showExpert="true"
                                  displayId="true"
                                  mappingName="${imapInfo.name}"/>
            </c:catch>
            <c:if test="${not empty exception}">
              <fmt:message key="assetEditor.noItemMapping.error"/> <c:out value="${exception.message}"/>
            </c:if>
            <c:if test="${debug && empty imap}">
              [dropDownIFrame]return empty imap <c:out value="${itemDescriptorName}"/> <c:out value="${repositoryPath}"/>
            </c:if>
            <c:if test="${not empty imap}">
              <asset-ui:addMappedItemViewsToSet currentSet="${propertyGroups}" newItems="${imap.viewMappings}"
                          var="propertyGroups"/>
            </c:if>
          </dspel:oparam>
        </dspel:droplet>
      </c:when>
      <c:otherwise>

        <!-- filter was set so there just one type to worry about -->
        <asset-ui:getItemMappingInfo itemDescriptorName="${currentFilterTypeName}"
                                     repositoryPath="${currentFilterTypeContainerPath}"
                                     mode="${mode}"
                                     taskConfig="${managerConfig}"
                                     var="imapInfo"/>

        <c:if test="${debug}">
          [dropDownIFrame] imapInfo mode: <c:out value="${imapInfo.mode}"/><br/>
          [dropDownIFrame] imapInfo name: <c:out value="${imapInfo.name}"/><br/>
        </c:if>

        <c:catch var="exception">
          <biz:getItemMapping itemPath="${currentFilterTypeContainerPath}"
                              itemName="${currentFilterTypeName}"
                              var="imap"
                              mode="${imapInfo.mode}"
                              readOnlyMode="AssetManager.view"
                              showExpert="true"
                              displayId="true"
                              mappingName="${imapInfo.name}"/>
        </c:catch>
        <c:if test="${not empty exception}">
          <fmt:message key="assetEditor.noItemMapping.error"/> <c:out value="${exception.message}"/>
        </c:if>
        <c:if test="${debug && empty imap}">
          [dropDownIFrame]return empty imap <c:out value="${itemDescriptorName}"/> <c:out value="${repositoryPath}"/>
        </c:if>
        <c:if test="${not empty imap}">

          <c:set var="propertyGroups" value="${imap.viewMappings}"/>

          <c:if test="${debug}">
            [dropDownIFrame] formHandlerPath <c:out value="${formHandlerPath}" /><br/>
            [dropDownIFrame] formHandler <c:out value="${formHandler}"/><br/>
            [dropDownIFrame] imap: <c:out value=" descr:   ${imap.description}"/><br/>
            [dropDownIFrame] <c:out value=" imap.itemPath: ${imap.itemPath}"/><br/>
            [dropDownIFrame] <c:out value=" imap.itemName: ${imap.itemName}"/><br/>
          </c:if>

        </c:if>

      </c:otherwise>
    </c:choose>

    <%-- sort the property groups list --%>
    <%-- NOTE: If you change this sorting, please make the same change in editAsset.jsp where 
         the list is sorted and the index is used to find the selected property group. --%>
    <c:if test="${not empty propertyGroups}">
      <dspel:sort var="sorter" values="${propertyGroups}">
        <dspel:orderBy property="displayName"/>
      </dspel:sort>
      <c:set var="propertyGroups" value="${sorter.sortedArray}"/>
    </c:if>

  </c:if> <%-- end of multiEdit tab set up --%>


  <c:set scope="request"
         var="tabConfig"
         value="${requestScope.managerConfig.tabs[sessionInfo.assetManagerTab]}"/>


  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <html xmlns="http://www.w3.org/1999/xhtml">

  <head>
          <title><fmt:message key="browseTab.listHeader.create"/></title>
          <link rel="stylesheet" href="../css/dropDownIframe.css" />

    <script type="text/javascript">
      function closeTheDD(where){
        parent.document.getElementById(where).style.display = 'none';
        parent.currentTarg = null;
        return true;
      }
    </script>
  </head>

  <body id="createNewDD">

    <c:choose>
      <c:when test='${section eq "newButton"}'>

        <%-- "Create New" menu --%>
          
        <%-- See if there is a tree state component for the current view.  This
             will be needed by the asset creation page to handle specification
             of the parent. --%>
        <c:set var="treeComponent" value="${sessionInfo.treeStatePath}"/>

        <script type="text/javascript">
          // Called when the user selects an item from the menu. Hides the
          // dropdown menu, and navigates the right-pane to the given
          // asset-creation URL.
          function createAsset(url) {
            closeTheDD("divDDiconDD");
            parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(url);
          }

        </script>

        <%-- Menu title --%>
        <h4><fmt:message key="browseTab.listHeader.create"/></h4>

        <div id="newDropDown">
          <dl>

            <%-- Loop through the list of createable asset types from the
                 BrowseViewConfiguration for the current browse view. --%>

            <c:set var="viewConfig" value="${sessionInfo.currentAssetBrowser}"/>
            <c:forEach var="entry" items="${viewConfig.createableTypes}">
              <c:set var="assetName" value="${entry.displayName}"/>
              <c:set var="assetType" value="${entry.typeName}"/>
              <c:set var="containerPath" value="${entry.containerPath}"/>

              <%-- Get the create-mode item mapping for this asset type --%>
              <asset-ui:getItemMappingInfo var="imapInfo"
                                           repositoryPath="${containerPath}"
                                           itemDescriptorName="${assetType}"
                                           mode="AssetManager.create"
                                           taskConfig="${requestScope.managerConfig}"/>
              <c:catch var="exception">
                <biz:getItemMapping var="imap"
                                    mappingName="${imapInfo.name}"
                                    itemPath="${containerPath}"
                                    itemName="${assetType}"
                                    mode="${imapInfo.mode}"/>
              </c:catch>
              <c:if test="${not empty exception}">
                <dd><fmt:message key="assetEditor.noItemMapping.error"/> <c:out value="${exception.message}"/></dd>
              </c:if>

              <%-- Derive a URL for the page that creates a new asset for this type,
                   and display a menu item. --%>              
              <c:if test="${not empty imap}">
                <c:set var="view" value="${imap.viewMappings[0]}"/>

                <c:url var="creationURL" context="/${view.contextRoot}" value="${view.uri}">
                  <c:param name="containerPath" value="${containerPath}"/>
                  <c:param name="assetType" value="${assetType}"/>
                  <c:if test="${not empty treeComponent}">
                    <c:param name="treeComponent" value="${treeComponent}"/>
                  </c:if>
                </c:url>

                <dd><a href="javascript:createAsset('<c:out value="${creationURL}"/>')"><c:out value="${assetName}"/></a></dd>
              </c:if>
  
              <%-- NOTE: The template uses the following technique for
                         allowing sub-types to be created:
                     <dd><a href="#">SKU</a></dd>
                     <dd class="sub"><a href="#">Configurable SKU</a></dd>
              --%>
            </c:forEach>
          </dl>
        </div>

      </c:when>

      <c:when test='${section eq "unlinkDropDownButton"}'>
        <div id="newDropDown">
          <dl>
            <%-- disable this link if we are not in a tree --%>
            <dd>
            <c:choose>
              <c:when test="${sessionInfo.assetManagerTab ne 'browse'}">
                <span class="disabled"><fmt:message key="browseTab.dropdown.unlinkFromSelectedLocation"/></span>
              </c:when>
              <c:otherwise>
                <a href="javascript:parent.doNonDestinationAction('unlink'); closeTheDD('divDDlinkDD');"><fmt:message key="browseTab.dropdown.unlinkFromSelectedLocation"/></a>
              </c:otherwise>
            </c:choose>
            </dd>
            <dd><a href="javascript:parent.doNonDestinationAction('unlinkFromAll'); closeTheDD('divDDlinkDD');"><fmt:message key="browseTab.dropdown.unlinkFromAllLocations"/></a></dd>
          </dl>
        </div>
      </c:when>

      <c:when test='${section eq "moveDropDownButton"}'>
        <div id="newDropDown">

          <dl>
            <%-- disable this link if we are not in a tree --%>
            <dd>
            <c:choose>
              <c:when test="${sessionInfo.assetManagerTab ne 'browse'}">
                <span class="disabled"><fmt:message key="browseTab.dropdown.moveFromSelectedLocation"/></span>
              </c:when>
              <c:otherwise>
                <a href="javascript:parent.doDestinationAction('move');  closeTheDD('divDDlinkDD');"><fmt:message key="browseTab.dropdown.moveFromSelectedLocation"/></a>
              </c:otherwise>
            </c:choose>
            </dd>
            <dd><a href="javascript:parent.launchMoveFromAll('moveFromAll'); closeTheDD('divDDlinkDD');"><fmt:message key="browseTab.dropdown.moveFromAllLocations"/></a></dd>
          </dl>
        </div>
      </c:when>


      <c:when test='${section eq "stepEdit"}'>

        <c:choose>
          <c:when test="${checkedItemCount eq 0}">
            <fmt:message key="multiEdit.error.noCheckedAssets"/>
          </c:when>
          <c:when test="${empty currentFilterType && empty commonTypes}">
            <div id="newDropDown">
              <dl>
                <dd><a href="javascript:parent.changeMode('Multi', 'Step', '-1')">
                  <fmt:message key="multiEdit.allPropertyGroups"/>
                </a></dd>
              </dl>
            </div>
          </c:when>
          <c:otherwise>

            <div id="newDropDown">
              <dl>
                <c:forEach items="${propertyGroups}" var="propertyGroup" varStatus="vstat">
                  <c:if test="${not empty propertyGroup.displayName}">
                    <dd><dspel:a href="javascript:parent.changeMode('Multi', 'Step', '${vstat.index}')">
                      <c:set var="bundleName" value="${propertyGroup.attributes.resourceBundle}"/>
                      <c:choose>
                        <c:when test="${not empty bundleName}">
                          <fmt:setBundle var="resBundle" basename="${bundleName}"/>
                          <fmt:message key="${propertyGroup.displayName}" bundle="${resBundle}"/>
                        </c:when>
                        <c:otherwise>
                          <c:out value="${propertyGroup.displayName}"/>
                        </c:otherwise>
                      </c:choose>
                    </dspel:a></dd>
                  </c:if>
                </c:forEach>
                <dd><a href="javascript:parent.changeMode('Multi', 'Step', '-1')">
                  <fmt:message key="multiEdit.allPropertyGroups"/>
                </a></dd>
              </dl>
            </div>
          </c:otherwise>
        </c:choose>

      </c:when> <%-- end stepEdit --%>

      <c:when test='${section eq "applyToAll"}'>

        <c:choose>
          <c:when test="${checkedItemCount eq 0}">
            <fmt:message key="multiEdit.error.noCheckedAssets"/>
          </c:when>
          <c:when test="${empty currentFilterType && empty commonTypes}">
            <fmt:message key="multiEdit.selectFilter"/>
          </c:when>
          <c:when test="${empty propertyGroups}">
            <fmt:message key="multiEdit.noMappingsForType"/>
          </c:when>
          <c:otherwise>
            <div id="newDropDown">
              <dl>
                <c:forEach items="${propertyGroups}" var="propertyGroup" varStatus="vstat">
                  <c:if test="${not empty propertyGroup.displayName}">
                    <dd><dspel:a href="javascript:parent.changeMode('Multi', 'ApplyToAll', '${vstat.index}')">
                      <c:set var="bundleName" value="${propertyGroup.attributes.resourceBundle}"/>
                      <c:choose>
                        <c:when test="${not empty bundleName}">
                          <fmt:setBundle var="resBundle" basename="${bundleName}"/>
                          <fmt:message key="${propertyGroup.displayName}" bundle="${resBundle}"/>
                        </c:when>
                        <c:otherwise>
                          <c:out value="${propertyGroup.displayName}"/>
                        </c:otherwise>
                      </c:choose>
                    </dspel:a></dd>
                  </c:if>
                </c:forEach>
              </dl>
            </div>
          </c:otherwise>
        </c:choose>

      </c:when> <%-- end applyToAll --%>

      <c:when test='${section eq "listEdit"}'>

        <c:choose>
          <c:when test="${checkedItemCount eq 0}">
            <fmt:message key="multiEdit.error.noCheckedAssets"/>
          </c:when>
          <c:when test="${checkedItemCount > sessionInfo.multiEditSessionInfo.maxListEditSize}"> 
            <fmt:message key="multiEdit.error.overMaxListEditSizeMessage">
              <fmt:param value="${sessionInfo.multiEditSessionInfo.maxListEditSize}"/>
            </fmt:message>
          </c:when>
          <c:when test="${empty currentFilterType && empty commonTypes}">
            <fmt:message key="multiEdit.selectFilter"/>
          </c:when>
          <c:when test="${empty propertyGroups}">
            <fmt:message key="multiEdit.noMappingsForType"/>
          </c:when>
          <c:otherwise>
            <div id="newDropDown">
              <dl>
                <c:forEach items="${propertyGroups}" var="propertyGroup" varStatus="vstat">
                  <c:if test="${not empty propertyGroup.displayName}">
                    <dd><dspel:a href="javascript:parent.changeMode('Multi', 'List', '${vstat.index}')">
                      <c:set var="bundleName" value="${propertyGroup.attributes.resourceBundle}"/>
                      <c:choose>
                        <c:when test="${not empty bundleName}">
                          <fmt:setBundle var="resBundle" basename="${bundleName}"/>
                          <fmt:message key="${propertyGroup.displayName}" bundle="${resBundle}"/>
                        </c:when>
                        <c:otherwise>
                          <c:out value="${propertyGroup.displayName}"/>
                        </c:otherwise>
                      </c:choose>
                    </dspel:a></dd>
                  </c:if>
                </c:forEach>
              </dl>
            </div>
          </c:otherwise>
        </c:choose>

      </c:when> <%-- end listEdit --%>

      <c:when test='${section eq "transformations"}'>

        <c:choose>
          <c:when test="${checkedItemCount eq 0}">
            <fmt:message key="multiEdit.error.noCheckedAssets"/>
          </c:when>
          <c:when test="${empty currentFilterType && empty commonTypes}">
            <fmt:message key="multiEdit.selectFilter"/>
          </c:when>
          <c:when test="${empty propertyGroups}">
            <fmt:message key="multiEdit.noMappingsForType"/>
          </c:when>
          <c:otherwise>
            <div id="newDropDown">
              <dl>
              <%--
                <c:forEach items="${propertyGroups}" var="propertyGroup" varStatus="vstat">
                  <c:if test="${not empty propertyGroup.displayName}">
                    <dd><dspel:a href="javascript:parent.changeMode('Multi', 'Transformations', '${vstat.index}')">
                      <c:set var="bundleName" value="${propertyGroup.attributes.resourceBundle}"/>
                      <c:choose>
                        <c:when test="${not empty bundleName}">
                          <fmt:setBundle var="resBundle" basename="${bundleName}"/>
                          <fmt:message key="${propertyGroup.displayName}" bundle="${resBundle}"/>
                        </c:when>
                        <c:otherwise>
                          <c:out value="${propertyGroup.displayName}"/>
                        </c:otherwise>
                      </c:choose>
                    </dspel:a></dd>
                  </c:if>
                </c:forEach>--%>
                <dd>Not Yet Implemented</dd>
              </dl>
            </div>
          </c:otherwise>
        </c:choose>

      </c:when> <%-- end transformations --%>

      <c:otherwise>
        <%-- There is a page coding error if we get here. --%>
        <h4><fmt:message key="dropDownIFrame.unknownSection.error"/></h4>
      </c:otherwise>

    </c:choose>

  </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/dropDownIframe.jsp#2 $$Change: 651448 $ --%>
