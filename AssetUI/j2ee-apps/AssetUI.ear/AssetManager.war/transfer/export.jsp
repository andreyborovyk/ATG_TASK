<%--
  Export assets for asset manager UI.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/export.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <c:set var="exportFormHandlerPath" value="/atg/web/assetmanager/transfer/ExportFormHandler"/>
  <dspel:importbean var="exportFormHandler"
                    bean="${exportFormHandlerPath}"/>
  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- tell the exportable type list to initialize itself --%>
  <dspel:setvalue bean="${exportFormHandlerPath}.exportStatus.exportableTypesInitialized" value="false"/>

  <%-- if there are no exportable items, say so --%>
  <c:choose>
    <c:when test="${sessionInfo.checkedItemCount eq 0}">
      <script type="text/javascript" >
        errstring = "<fmt:message key='error.general.noAssetSelectedForOperation'/>";
        parent.messages.addError(errstring);
        parent.hideExportDialog();
      </script>
    </c:when>
    <c:when test="${sessionInfo.checkedItemCount eq exportFormHandler.exportStatus.nonExportableItemCount}">

      <div id="exportBody">
        <div id="selectedAssets">
          <fmt:message key="transfer.exportDialog.noExportableAssets"/>
        </div>

        <%-- Cancel button --%>
        <div id="popupActions">
          <div>
            <dspel:a iclass="buttonSmall" href="javascript:parent.hideExportDialog()">
            <fmt:message key="common.ok"/>
            </dspel:a>
          </div>
        </div>
      </div>
    </c:when> 
    <c:otherwise>


  <c:url var="previewSuccessURL" value="/transfer/preview.jsp"/>
  <c:url var="ajaxPreviewSuccessURL" value="previewJSON.jsp"/>

  <script type="text/javascript">
    atg.assetmanager.quietFormSubmitter.initialize();    
    var exportDownload;
    if (exportDownload == null)
      exportDownload = new atg.widget.exportDownload();
  </script>

  <div id="exportBody">

    <%-- Display an export form. --%>
    <dspel:form id="exportFormForm" name="exportForm" target="exportDownloadIframe" 
                action="${previewSuccessURL}" method="post">

    <%-- test error handling --%>
    <dspel:input type="hidden" bean="${exportFormHandlerPath}.testEarlyExportError" value="false"/>
    <dspel:input type="hidden" bean="${exportFormHandlerPath}.testLateExportError" value="false"/>
    <dspel:input type="hidden" bean="${exportFormHandlerPath}.testPreviewError" value="false"/>

    <dspel:input type="hidden" bean="${exportFormHandlerPath}.previewSuccessURL"
                 value="${previewSuccessURL}"/>
    <dspel:input type="hidden" bean="${exportFormHandlerPath}.ajaxPreviewSuccessURL"
                 value="${ajaxPreviewSuccessURL}"/>

    <%-- export body --%>
    <div id="selectedAssets">
      <fmt:message key="transfer.exportDialog.selectedAssetCountLabel">
        <fmt:param value="${sessionInfo.checkedItemCount}"/> 
      </fmt:message>
      <c:if test="${exportFormHandler.exportStatus.nonExportableItemCount ne 0}">
        <fmt:message key="transfer.exportDialog.nonExportableAssetCountLabel">
          <fmt:param value="${exportFormHandler.exportStatus.nonExportableItemCount}"/> 
        </fmt:message>
      </c:if>
    </div>
          
    <div id="exportForm">

      <table class="formTable" style="width:350px; border-spacing: 0;">

        <tr>
          <td class="formLabel">
            <fmt:message key="transfer.exportDialog.assetTypeLabel"/>
          </td>

          <td>
            <%-- a Map of property lists.  the key.name is the asset type --%>
            <c:set var="allTypesPropertiesMap" value="${exportFormHandler.exportStatus.properties}"/>
            
            <%-- if we have just one item type, make it read only --%>
            <dspel:test var="allTypesMapTest" value="${allTypesPropertiesMap}"/>
            <c:set var="numTypes" value="${allTypesMapTest.size}"/>
            <c:set var="typeSelectorDisabled" value="false"/>
            <c:if test="${numTypes == 1}">
              <c:set var="typeSelectorDisabled" value="true"/>
            </c:if>
  
            <dspel:select name="currentTypeSelector"  id="typeSelectBox"
                          bean="${exportFormHandlerPath}.currentType" 
                          onchange="exportDownload.changeType()"
                          disabled="${typeSelectorDisabled}">
              <c:forEach var="typeMapEntry" items="${allTypesPropertiesMap}">
                <c:set var="selected" value="${typeMapEntry.key.name == exportFormHandler.currentType}"/>
                <dspel:option value="${typeMapEntry.key.name}"  selected="${selected}">
                  <c:out value="${typeMapEntry.key.displayName}"/> (<c:out value="${typeMapEntry.key.totalCount}"/>)
                </dspel:option>
              </c:forEach>
            </dspel:select>
          </td>
        </tr>

        <tr>
          <td class="formLabel">
            <fmt:message key="transfer.exportDialog.propertiesToExportLabel"/>
          </td>

          <td>
            <c:forEach var="typeMapEntry" items="${allTypesPropertiesMap}">
              <c:set var="divStyle" value="none"/>
              <c:set var="itemTypeCount" value="${typeMapEntry.key}"/>
              <c:if test="${itemTypeCount.name == exportFormHandler.currentType}">
                <c:set var="divStyle" value="block"/>
              </c:if>

              <div id="propList_<c:out value='${typeMapEntry.key.name}'/>" 
                   name="exportPropDiv" style="display:<c:out value='${divStyle}'/>">

                <p class="exportCheckAll"><label><input type="checkbox" 
                   dojotype="atg.widget.checkAll" parentTag="div"/>
                  <fmt:message key="transfer.exportDialog.selectAllLabel"/>
                </label></p>

                <div class="exportPropList">

                  <ul>
                    <c:forEach var="propertyInfo" items="${allTypesPropertiesMap[typeMapEntry.key]}">
                      <li>
                        <label>
                          <c:set var="inputclass" value="singleprop"/>
                          <c:if test="${propertyInfo.isMulti}">
                             <c:set var="inputclass" value="multiprop"/>
                           </c:if>
                          <dspel:input iclass="${inputclass}" type="checkbox" 
                                       bean="${exportFormHandlerPath}.currentExportProperties" 
                                       value="${propertyInfo.propertyName}" 
                                       onclick="exportDownload.changeProperties()"/><c:if test="${propertyInfo.isMulti}">[</c:if><c:out value="${propertyInfo.displayName}"/><c:if test="${propertyInfo.isMulti}">]</c:if>
                        </label>
                      </li>
                    </c:forEach>
                  </ul>
                </div>
              </div>
            </c:forEach>

            <p class="footnote"><fmt:message key="transfer.exportDialog.idHelpText"/></p>

          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>
            <label id="verticalFormatLabel" class="disableShowCollections"><dspel:input id="useVerticalFormatCheckbox" type="checkbox" bean="${exportFormHandlerPath}.useVerticalFormat" disabled='true'/> <fmt:message key="transfer.exportDialog.verticalFormatLabel"/></label>
          </td>
        </tr>
      </table>


      <div id="preview">
        <div class="previewNote"><fmt:message key="transfer.exportDialog.previewTableLabel"/></div>
          <div id="previewData">
          </div>
        </div>
      </div>

      <div id="popupActions">
        <div>

          <%-- Export button --%>
          <dspel:a iclass="buttonSmall"
                   href="javascript:exportDownload.clearHiddenChecks();exportDownload.sendDownloadRequest()">
            <fmt:message key="transfer.exportDialog.exportButtonLabel"/>
          </dspel:a>
          <%-- Preview button --%>
          <dspel:a iclass="buttonSmall"
                   href="javascript:exportDownload.doPreview()">
            <fmt:message key="transfer.exportDialog.previewButtonLabel"/>
          </dspel:a>
          <%-- Cancel button --%>
          <dspel:a iclass="buttonSmall" href="javascript:parent.hideExportDialog()">
            <fmt:message key="common.cancel"/>
          </dspel:a>

          <%-- Hidden export and preview buttons that are clicked using JavaScript
               when the appropriate visible buttons are clicked. --%>
          <dspel:input id="exportr" type="submit" style="display:none"
                       bean="${exportFormHandlerPath}.export" value=""/>
          <dspel:input id="previewButton" type="submit" style="display:none" 
                       bean="${exportFormHandlerPath}.preview" value="preview"
                       iclass="quietformsubmitter"/>

        </div>
      </div>
    </dspel:form>


  </div>
  <iframe name="exportDownloadIframe" id="exportDownloadIframe" style="display:none"></iframe>
  <div id="screen" style="display:none"></div>

  <div id="exportStatusPanel" style="display:none">   


    <div id="exportStatusMessage" style="display:none">
    </div>

    <P> 

    <fmt:message key="export.status.amountComplete"/><span id="numExported">0</span> 

    <P>

    <div id="exportError" style="display:none">
    </div>

    <%-- OK button --%>
    <div id="exportDialogCloseButton" style="display:none">
      <dspel:a iclass="buttonSmall" href="javascript:parent.hideExportDialog()">
        <fmt:message key="common.ok"/>
      </dspel:a>
    </div>
  </div>


</c:otherwise>
</c:choose>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/export.jsp#2 $$Change: 651448 $ --%>
