<%--
  Asset diff page for asset manager UI.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/diff/reviewChanges.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="biz"      uri="http://www.atg.com/taglibs/bizui"                 %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>

<dspel:page>

  <fmt:setBundle basename="${requestScope.managerConfig.resourceBundle}"/>

  <pws:getCurrentProject var="projectContext"/>
  <c:set var="project" value="${projectContext.project}"/>

  <asset-ui:resolveAsset var="assetWrapper" uri="${param.assetURI}"/>
  <pws:createVersionManagerURI var="versionManagerURI"
                               componentName="${assetWrapper.containerName}"
                               itemDescriptorName="${assetWrapper.type}"
                               itemId="${assetWrapper.id}"/>

  <%-- Set a request-scoped variable to indicate if the editing controls for the
       asset should be enabled.  This depends on the editability of the project
       and the asset itself. --%>
  <c:set var="editable" value="${project.editable}"/>
  <asset-ui:getAssetAccess var="itemAccess" item="${assetWrapper.asset}"/>
  <c:if test="${not itemAccess.write}">
    <c:set var="editable" value="false"/>
  </c:if>
  <c:set scope="request" var="atgIsAssetEditable" value="${editable}"/>

  <%-- Get the form handler path from the ItemMapping for the asset given by
       assetURI, in AssetManager.diff mode.  Note that this assumes that the
       same form handler is used for both diff and conflict mode!! --%>
  <pws:getAsset var="asset" uri="${versionManagerURI}" workspaceName="${project.workspace}"
                allowFileRepositoryItems="true"/>
  <asset-ui:getItemMappingInfo var="imapInfo"
                            item="${asset.repositoryItem}"
                            mode="AssetManager.diff"
                            taskConfig="${requestScope.managerConfig}"/>
  <biz:getItemMapping var="itemMapping"
                      item="${asset.repositoryItem}"
                      mappingName="${imapInfo.name}"
                      mode="${imapInfo.mode}"/>
  <c:set var="formHandlerPath"
         value="${itemMapping.formHandler.path}"/>

  <dspel:importbean var="formHandler"
                    bean="${formHandlerPath}"/>

  <%-- Load the asset into the form handler --%>
  <dspel:setvalue bean="${formHandlerPath}.assetURI" value="${versionManagerURI}"/>

  <c:url var="thisURL" value="${requestScope.managerConfig.page}">
    <c:param name="reviewChanges" value="1"/>
    <c:param name="assetURI" value="${param.assetURI}"/>
  </c:url>

  <dspel:form name="diffForm" action="${thisURL}" method="post">

    <%-- The following hidden input is needed for two reasons:
         1) It allows the assetURI to be passed to the form handler when the
            user submits the form (since the form handler is only request scoped)
         2) It triggers the formHandler's beforeGet method, which does the work
            of calculating the list of diff properties.  Note that, if the value
            were specified using the attribute value="${param.assetURI}", then
            that would also prevent the beforeGet method from being called. --%>
    <dspel:input type="hidden"
                 bean="${formHandlerPath}.assetURI"/>

    <%-- Display any form errors. --%>
    <c:if test="${formHandler.formError}">
      <script type="text/javascript">
        <c:forEach var="error" items="${formHandler.formExceptions}">
          messages.addError("<c:out value='${error.message}'/>");
        </c:forEach>
      </script>
    </c:if>
    
    <table id="contentTable" cellpadding="0" cellspacing="0">
      <tbody>
        <tr>
          <td class="reviewChanges" style="height: 0px">
              <p class="returnLink">
                <c:url var="backURL" value="${requestScope.managerConfig.page}">
                  <c:param name="resetTab" value="${false}"/>
                </c:url>
                <dspel:a href="${backURL}">
                  <fmt:message key="reviewChanges.back"/>
                </dspel:a>
              </p>

              <%--
              <p id="toggleDetails">
                <a href="#">
                  &nbsp;
                </a>
              </p>
              --%>

              <h3 class="contentHeader">
                <fmt:message key="reviewChanges.review"/>
                <c:choose>
                  <c:when test="${not formHandler.assetExistent}">
                    <fmt:message key="reviewChanges.unknownAsset"/>
                  </c:when>
                  <c:when test="${formHandler.assetDeleted}">
                    <fmt:message key="reviewChanges.deletedAsset"/>
                  </c:when>
                  <c:otherwise>
                    <c:out value="${formHandler.localItem.itemDescriptor.itemDescriptorName}"/>:
                    <c:out value="${formHandler.localItem.itemDisplayName}"/>
                  </c:otherwise>
                </c:choose>
              </h3>

              <%--
              <div class="positionBox">
                <div class="noBox" id="box">
                  <h3>
                    This is The Title of the Toggled Box
                  </h3>
                  <p>
                    Lorem ipsum dolor sit amet...
                  </p>
                  <div>
                    <a class="more" href="#" id="closePageInfo">
                      Close More Details
                    </a>
                  </div>
                </div>
              </div>
              --%>

              <c:choose>
                <c:when test="${not formHandler.assetExistent}">
                </c:when>
                <c:when test="${formHandler.assetAdded}">
                  <p class="reviewChangesStatus">
                    <fmt:message key="reviewChanges.createdNoChange"/>
                  </p>
                </c:when>
                <c:when test="${formHandler.assetDeleted}">
                  <%-- NB: Is this always true???  What if you are trying to delete
                       something, but someone has edited it in the meantime? --%>
                  <p class="reviewChangesStatus">
                    <fmt:message key="reviewChanges.deletedNoChange"/>
                  </p>
                </c:when>
                <c:otherwise>
                  <c:if test="${formHandler.resolveNeeded}">
                    <p class="attention">
                      <fmt:message key="reviewChanges.resolveNeeded"/>
                    </p>
                    <c:choose>
                      <c:when test="${empty formHandler.conflictingPropertyNames}">
                        <c:if test="${editable}">

                          <p class="reviewChangesStatus">
                            <fmt:message key="reviewChanges.nullMerge"/>

                            <dspel:input id="nullMerge"
                                         type="submit"
                                         style="display:none"
                                         bean="${formHandlerPath}.merge"/>

                            <a href="javascript:document.diffForm.nullMerge.click()"
                               class="buttonSmall" title="<fmt:message key='reviewChanges.resolve.title'/>">
                              <span>
                                <fmt:message key="reviewChanges.resolve"/>
                              </span>
                            </a>
                          </p>

                        </c:if>
                      </c:when>
                      <c:otherwise>

                        <asset-ui:getItemMappingInfo var="imapInfo"
                                                  item="${formHandler.originalItem}"
                                                  mode="AssetManager.conflict"
                                                  taskConfig="${requestScope.managerConfig}"/>
                        <biz:getItemMapping var="originalItemMapping"
                                            item="${formHandler.originalItem}"
                                            mappingName="${imapInfo.name}"
                                            mode="${imapInfo.mode}"
                                            propertyList="${formHandler.conflictingPropertyNames}"
                                            showExpert="true"/>
                        <c:set target="${originalItemMapping}"
                               property="valueDictionaryName"
                               value="originalItem"/>
                        <c:set var="originalItemViewMapping"
                               value="${originalItemMapping.viewMappings[0]}"/>

                        <asset-ui:getItemMappingInfo var="imapInfo"
                                                  item="${formHandler.headItem}"
                                                  mode="AssetManager.conflict"
                                                  taskConfig="${requestScope.managerConfig}"/>
                        <biz:getItemMapping var="headItemMapping"
                                            item="${formHandler.headItem}"
                                            mappingName="${imapInfo.name}"
                                            mode="${imapInfo.mode}"
                                            propertyList="${formHandler.conflictingPropertyNames}"
                                            showExpert="true"/>
                        <c:set target="${headItemMapping}"
                               property="valueDictionaryName"
                               value="headItem"/>
                        <c:set var="headItemViewMapping"
                               value="${headItemMapping.viewMappings[0]}"/>

                        <asset-ui:getItemMappingInfo var="imapInfo"
                                                  item="${formHandler.localItem}"
                                                  mode="AssetManager.conflict"
                                                  taskConfig="${requestScope.managerConfig}"/>
                        <biz:getItemMapping var="localItemMapping"
                                            item="${formHandler.localItem}"
                                            mappingName="${imapInfo.name}"
                                            mode="${imapInfo.mode}"
                                            propertyList="${formHandler.conflictingPropertyNames}"
                                            showExpert="true"/>
                        <c:set target="${localItemMapping}"
                               property="valueDictionaryName"
                               value="localItem"/>
                        <c:set var="localItemViewMapping"
                               value="${localItemMapping.viewMappings[0]}"/>

                        <c:if test="${formHandler.suggestItem ne null && not empty formHandler.conflictingCollectionPropertyNames}">
                          <asset-ui:getItemMappingInfo var="imapInfo"
                                                    item="${formHandler.suggestItem}"
                                                    mode="AssetManager.suggest"
                                                    taskConfig="${requestScope.managerConfig}"/>
                          <biz:getItemMapping var="suggestItemMapping"
                                              item="${formHandler.suggestItem}"
                                              mappingName="${imapInfo.name}"
                                              mode="${imapInfo.mode}"
                                              propertyList="${formHandler.conflictingCollectionPropertyNames}"
                                              showExpert="true"/>
                          <c:set target="${suggestItemMapping}"
                                 property="valueDictionaryName"
                                 value="suggestItem"/>
                          <c:set var="suggestItemViewMapping"
                                 value="${suggestItemMapping.viewMappings[0]}"/>
                        </c:if>

                        <dspel:include otherContext="${originalItemViewMapping.contextRoot}" page="${originalItemViewMapping.uri}">
                          <dspel:param name="formHandler"             value="${formHandler}"/>
                          <dspel:param name="formHandlerPath"         value="${formHandlerPath}"/>
                          <dspel:param name="headItemViewMapping"     value="${headItemViewMapping}"/>
                          <dspel:param name="localItemViewMapping"    value="${localItemViewMapping}"/>
                          <dspel:param name="originalItemViewMapping" value="${originalItemViewMapping}"/>
                          <dspel:param name="suggestItemViewMapping"  value="${suggestItemViewMapping}"/>
                        </dspel:include>

                      </c:otherwise>
                    </c:choose>
                  </c:if>

                  <c:choose>
                    <c:when test="${empty formHandler.modifiedPropertyNames}">
                      <p class="reviewChangesStatus">
                        <fmt:message key="reviewChanges.noProperties"/>
                      </p>
                    </c:when>
                    <c:otherwise>

                      <asset-ui:getItemMappingInfo var="imapInfo"
                                                item="${formHandler.originalItem}"
                                                mode="AssetManager.diff"
                                                taskConfig="${requestScope.managerConfig}"/>
                      <biz:getItemMapping var="originalItemMapping"
                                          item="${formHandler.originalItem}"
                                          mappingName="${imapInfo.name}"
                                          mode="${imapInfo.mode}"
                                          propertyList="${formHandler.modifiedPropertyNames}"
                                          showExpert="true"/>
                      <c:set target="${originalItemMapping}"
                             property="valueDictionaryName"
                             value="originalItem"/>
                      <c:set var="originalItemViewMapping"
                             value="${originalItemMapping.viewMappings[0]}"/>

                      <asset-ui:getItemMappingInfo var="imapInfo"
                                                item="${formHandler.localItem}"
                                                mode="AssetManager.diff"
                                                taskConfig="${requestScope.managerConfig}"/>
                      <biz:getItemMapping var="localItemMapping"
                                          item="${formHandler.localItem}"
                                          mappingName="${imapInfo.name}"
                                          mode="${imapInfo.mode}"
                                          propertyList="${formHandler.modifiedPropertyNames}"
                                          showExpert="true"/>
                      <c:set target="${localItemMapping}"
                             property="valueDictionaryName"
                             value="localItem"/>
                      <c:set var="localItemViewMapping"
                             value="${localItemMapping.viewMappings[0]}"/>

                      <dspel:include otherContext="${originalItemViewMapping.contextRoot}" page="${originalItemViewMapping.uri}">
                        <dspel:param name="formHandler"             value="${formHandler}"/>
                        <dspel:param name="formHandlerPath"         value="${formHandlerPath}"/>
                        <dspel:param name="localItemViewMapping"    value="${localItemViewMapping}"/>
                        <dspel:param name="originalItemViewMapping" value="${originalItemViewMapping}"/>
                      </dspel:include>

                    </c:otherwise>
                  </c:choose>
                </c:otherwise>
              </c:choose>
          </td>
        </tr>
      </tbody>
    </table>
  </dspel:form>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/diff/reviewChanges.jsp#2 $$Change: 651448 $ --%>
