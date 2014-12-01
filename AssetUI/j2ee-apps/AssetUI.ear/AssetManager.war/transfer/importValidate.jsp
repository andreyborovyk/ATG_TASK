<%--
  Logout page for asset manager UI.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/importValidate.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="biz"      uri="http://www.atg.com/taglibs/bizui"                 %>

<dspel:page>

  <c:set var="importFormHandlerPath" value="/atg/web/assetmanager/transfer/ImportFormHandler"/>
  <c:set var="importAssetPath" value="/atg/web/assetmanager/transfer/ImportAsset"/>
  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="importFormHandler"
                    bean="${importFormHandlerPath}"/>
  <dspel:importbean var="importAsset"
                    bean="${importAssetPath}"/>

  <c:url var="thisURL" value="/transfer/importValidate.jsp"/>
  <c:url var="confirmURL" value="/transfer/importConfirmation.jsp"/>

  <c:set var="sessionInfo" value="${config.sessionInfo}"/>

  <%-- Get the configuration component for the current task --%>
  <c:set var="managerConfig" value="${sessionInfo.taskConfiguration}"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <!DOCTYPE html
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

  <html xmlns="http://www.w3.org/1999/xhtml">


    <head>
      <title>
        <fmt:message key="common.pageTitle"/>
      </title>
      <dspel:include page="/components/head.jsp"/>
    </head>

<body id="framePage" onload="layoutAssetEditor(); " onresize="layoutAssetEditor()" class="<c:out value='${config.dojoThemeClass}'/>">

      <div id="importContainer">

        <%--  import form. --%>
        <dspel:form id="validateForm" enctype="multipart/form-data" name="validateForm" action="${thisURL}" method="post">

          <dspel:input type="hidden" bean="${importFormHandlerPath}.importAssetsSuccessURL"
                       value="${confirmURL}"/>
         <dspel:input type="hidden" bean="${importFormHandlerPath}.importAssetsErrorURL"
                       value="${confirmURL}"/>

 
        <%-- All of this is just to get the form handler from viewmapping.  --%>

          <asset-ui:getItemMappingInfo itemDescriptorName="${importFormHandler.importAsset.itemDescriptorName}"
                                       repositoryPath="${importFormHandler.importAsset.repositoryPathName}"
                                       mode="AssetManager.edit"
                                       taskConfig="${managerConfig}"
                                       var="imapInfo"/>

          <biz:getItemMapping itemPath="${importFormHandler.importAsset.repositoryPathName}"
                                itemName="${importFormHandler.importAsset.itemDescriptorName}"
                                var="imap"
                                mode="${imapInfo.mode}"
                                readOnlyMode="AssetManager.view"
                                showExpert="true"
                                displayId="true"
                                transient="${transient}"
                                mappingName="${imapInfo.name}"/>

          <c:set var="singleFormHandlerPath" value="${imap.formHandler.path}"/>
          <dspel:importbean var="singleFormHandler" bean="${singleFormHandlerPath}"/>
           
          <dspel:input bean="${importAssetPath}.singleFormHandler" style="display:none"
                       value="${singleFormHandler}"/>

          <c:set target="${importFormHandler.importAsset}" property="singleFormHandler" value="${singleFormHandler}"/>


          <%-- Hidden submit button that is clicked using JavaScript
               when the OK button is clicked. --%>
          <dspel:input id="importButton" type="submit" style="display:none"
                       bean="${importFormHandlerPath}.importAssets" value="Test"/>


         <%-- Hidden submit button that is clicked using JavaScript
               when the OK button is clicked. --%>
          <dspel:input id="validateAllButton" type="submit" style="display:none"
                       bean="${importFormHandlerPath}.validateAll" value="Test"/>


         <%-- Hidden submit button that is clicked using JavaScript
               when the Cancel button is clicked. --%>
          <dspel:input id="cancelButton" type="submit" style="display:none"
                       bean="${importFormHandlerPath}.cancel" value="Cancel"/>


         <fmt:message key='transfer.importDialog.importing'/> <c:out value="${importAsset.fileName}"/> <c:out value="${importAsset.fileSize}"/> bytes
          <c:if test="${importAsset.finishedValidation == true}">
            <ul class="exportValidation">
              <li><span class="validationLabel"><fmt:message key='transfer.importDialog.changeCount'/></span> <c:out value="${importAsset.validateModifiedCount}"/></li>
              <li><span class="validationLabel"><fmt:message key='transfer.importDialog.addedCount'/></span> <c:out value="${importAsset.validateNewCount}"/></li>
              <li><span class="validationLabel"><fmt:message key='transfer.importDialog.errorCount'/></span> <c:out value="${importAsset.validateErrorCount}"/></li>
              <li><span class="validationLabel"><fmt:message key='transfer.importDialog.warningCount'/></span> <c:out value="${importAsset.validateWarningCount}"/></li>
              <li><span class="validationLabel"><fmt:message key='transfer.importDialog.unchangedCount'/></span> <c:out value="${importAsset.validateUnchangedCount}"/></li>
            </ul>
          </c:if>

              <%-- Use a grid widget to display the property. --%>
              <dspel:include page="/assetEditor/grid/basicGrid.jsp">
                <dspel:param name="componentContextRoot" value="/AssetManager"/>
                <dspel:param name="componentURI" value="/transfer/assetValidateComponent.jsp"/>
                <dspel:param name="modelPageSize" value="20"/>
                <dspel:param name="useColumnHeaders" value="true"/>
                <dspel:param name="keepRows" value="100"/>
                <dspel:param name="numElements" value="${importAsset.totalAssetCount}"/> 
                <dspel:param name="uniqueId" value="validateId"/>
                <dspel:param name="containsRepositoryItems" value="false"/>
                <dspel:param name="collectionBeanProp" value="/atg/web/assetmanager/transfer/ImportAsset.assetValidateInfos"/>
                <dspel:param name="containsURI"  value="false"/>
                <dspel:param name="allowEdit" value="false"/>
              </dspel:include>

         <fmt:message key='transfer.importDialog.validateTableText'/>

          <div class="importActions">

            <%-- Validate All --%>
           <c:if test="${!importAsset.finishedValidation}">
            <dspel:a iclass="buttonSmall go"
                     href="javascript:document.validateForm.validateAllButton.click()">
              <fmt:message key='transfer.importDialog.validateAll'/>
            </dspel:a>
           </c:if>

            <%-- Import button --%>
            <dspel:a iclass="buttonSmall go"
                     href="javascript:document.validateForm.importButton.click();dojo.byId('screen').style.display='block'">
              <fmt:message key='transfer.importDialog.import'/>
            </dspel:a>

             <%-- Cancel button --%>
            <dspel:a iclass="buttonSmall" href="javascript:parent.hideImportDialog()">
              <fmt:message key="common.cancel"/>
            </dspel:a>


            <%-- If get rid of iframes, use code below.
            <dspel:a iclass="buttonSmall go"
                     href="javascript:submitDojoForm(dojo.byId('importButton'),dojo.byId('importIframe'), true)">
              <fmt:message key='transfer.importDialog.import'/>
            </dspel:a>

            <dspel:a iclass="buttonSmall go"
                     href="javascript:submitDojoForm(dojo.byId('validateAllButton'),dojo.byId('importIframe'), true)">
              <fmt:message key='transfer.importDialog.validateAll'/>
            </dspel:a>
          
            <dspel:a iclass="buttonSmall" href="javascript:parent.hideImportDialog()">
              <fmt:message key="common.cancel"/>
            </dspel:a>
            --%>

          </div>
        </dspel:form>

      </div>
      <div id="screen" style="display: none;"></div>

    </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/importValidate.jsp#2 $$Change: 651448 $ --%>
