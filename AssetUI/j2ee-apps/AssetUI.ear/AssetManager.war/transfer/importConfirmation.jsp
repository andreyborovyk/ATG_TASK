<%--
  The last page of the import dialog.  Reports on changed/new/unchanged/error assets.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/importConfirmation.jsp#2 $$Change: 651448 $
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

  <c:url var="thisURL" value="/transfer/importConfirmation.jsp"/>

  <c:set var="sessionInfo" value="${config.sessionInfo}"/>


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

    <body id="framePage" onload="layoutAssetEditor();conditionalRefresh()"  onresize="layoutAssetEditor()" class="<c:out value='${config.dojoThemeClass}'/>">
<div id="importConfirmContainer">
      <div id="importConfirmation">

        <%--  import form. --%>
        <dspel:form id="validateForm" enctype="multipart/form-data" name="validateForm" action="${thisURL}" method="post">

          <div class="importFilename"><c:out value="${importAsset.fileName}"/> <fmt:message key='transfer.importDialog.imported'/></div>

          <fieldset>
            <legend><fmt:message key="transfer.importDialog.successful.header"/></legend>
            <p class="fieldsetIntro">
              <fmt:message  key="transfer.importDialog.successful.intro">
                <fmt:param value="${importAsset.importSuccessCount}"/>
              </fmt:message>
             </p>
            <%-- Use a grid widget to display the property. --%>
            <c:if test="${importAsset.importSuccessCount > 0}">
              <dspel:include page="/assetEditor/grid/basicGrid.jsp">
                <dspel:param name="componentContextRoot" value="/AssetManager"/>
                <dspel:param name="componentURI" value="/transfer/assetConfirmationComponent.jsp"/>
                <dspel:param name="modelPageSize" value="20"/>
                <dspel:param name="keepRows" value="100"/>
                <dspel:param name="numElements" value="${importAsset.importSuccessCount}"/>
                <dspel:param name="uniqueId" value="importSuccessId"/>
                <dspel:param name="containsRepositoryItems" value="true"/>
                <dspel:param name="collectionBeanProp" value="/atg/web/assetmanager/transfer/ImportFormHandler.importAsset.importSuccessList"/>
                <dspel:param name="containsURI"  value="true"/>
              </dspel:include>
            </c:if>
          </fieldset>


          <fieldset>
            <legend><fmt:message key="transfer.importDialog.unsuccessful.header"/></legend>
            <p class="fieldsetIntro">
              <fmt:message  key="transfer.importDialog.unsuccessful.intro">
                <fmt:param value="${importAsset.importErrorCount}"/>
              </fmt:message>
            </p>
            <%-- Use a grid widget to display the property. --%>
            <c:if test="${importAsset.importErrorCount > 0}">
              <dspel:include page="/assetEditor/grid/basicGrid.jsp">
                <dspel:param name="componentContextRoot" value="/AssetManager"/>
                <dspel:param name="componentURI" value="/transfer/assetConfirmationComponent.jsp"/>
                <dspel:param name="modelPageSize" value="20"/>
                <dspel:param name="keepRows" value="100"/>
                <dspel:param name="showIdColumn" value="true"/>
                <dspel:param name="numElements" value="${importAsset.importErrorCount}"/>
                <dspel:param name="uniqueId" value="importErrorId"/>
                <dspel:param name="containsRepositoryItems" value="true"/>
                <dspel:param name="collectionBeanProp" value="/atg/web/assetmanager/transfer/ImportFormHandler.importAsset.importErrorList"/>
                <dspel:param name="containsURI"  value="true"/>
              </dspel:include>
            </c:if>
          </fieldset>

 
           <fieldset>
            <legend><fmt:message key="transfer.importDialog.noop.header"/></legend>
            <p class="fieldsetIntro">
              <fmt:message  key="transfer.importDialog.noop.intro">
                <fmt:param value="${importAsset.importNoopCount}"/>
              </fmt:message>
            </p>
            <%-- Use a grid widget to display the property. --%>
            <c:if test="${importAsset.importNoopCount > 0}">
              <dspel:include page="/assetEditor/grid/basicGrid.jsp">
                <dspel:param name="componentContextRoot" value="/AssetManager"/>
                <dspel:param name="componentURI" value="/transfer/assetConfirmationComponent.jsp"/>
                <dspel:param name="modelPageSize" value="20"/>
                <dspel:param name="keepRows" value="100"/>
                <dspel:param name="numElements" value="${importAsset.importNoopCount}"/>
                <dspel:param name="uniqueId" value="importNoopId"/>
                <dspel:param name="containsRepositoryItems" value="true"/>
                <dspel:param name="collectionBeanProp" value="/atg/web/assetmanager/transfer/ImportFormHandler.importAsset.importNoopList"/>
                <dspel:param name="containsURI"  value="true"/>
              </dspel:include>
            </c:if>
          </fieldset>

           <fieldset>
            <legend><fmt:message key="transfer.importDialog.new.header"/></legend>
            <p class="fieldsetIntro">
              <fmt:message  key="transfer.importDialog.new.intro">
                <fmt:param value="${importAsset.importNewCount}"/>
              </fmt:message>
            </p>
            <%-- Use a grid widget to display the property. --%>
            <c:if test="${importAsset.importNewCount > 0}">
              <dspel:include page="/assetEditor/grid/basicGrid.jsp">
                <dspel:param name="componentContextRoot" value="/AssetManager"/>
                <dspel:param name="componentURI" value="/transfer/assetConfirmationComponent.jsp"/>
                <dspel:param name="modelPageSize" value="20"/>
                <dspel:param name="keepRows" value="100"/>
                 <dspel:param name="numElements" value="${importAsset.importNewCount}"/>
                <dspel:param name="uniqueId" value="importNewId"/>
                <dspel:param name="containsRepositoryItems" value="true"/>
                <dspel:param name="collectionBeanProp" value="/atg/web/assetmanager/transfer/ImportFormHandler.importAsset.importNewList"/>
                <dspel:param name="containsURI"  value="true"/>
              </dspel:include>
            </c:if>
          </fieldset>



          <fieldset>
            <legend><fmt:message key="transfer.importDialog.newerror.header"/></legend>
            <p class="fieldsetIntro">
              <fmt:message  key="transfer.importDialog.newerror.intro">
                <fmt:param value="${importAsset.importNewErrorCount}"/>
              </fmt:message>
            </p>
            <%-- Use a grid widget to display the property. --%>
            <c:if test="${importAsset.importNewErrorCount > 0}">
              <dspel:include page="/assetEditor/grid/basicGrid.jsp">
                <dspel:param name="componentContextRoot" value="/AssetManager"/>
                <dspel:param name="componentURI" value="/transfer/assetConfirmationComponent.jsp"/>
                <dspel:param name="modelPageSize" value="20"/>
                <dspel:param name="keepRows" value="100"/>
                <dspel:param name="showIdColumn" value="true"/>
                <dspel:param name="numElements" value="${importAsset.importNewErrorCount}"/>
                <dspel:param name="uniqueId" value="importNewErrorId"/>
                <dspel:param name="containsRepositoryItems" value="true"/>
                <dspel:param name="collectionBeanProp" value="/atg/web/assetmanager/transfer/ImportFormHandler.importAsset.importNewErrorList"/>
              </dspel:include>
            </c:if>
          </fieldset>

        </dspel:form>

      </div>

      <div class="importBottom">

         <%-- Close button --%>
        <dspel:a iclass="buttonSmall" href="javascript:parent.hideImportDialog()">
          <fmt:message key="transfer.importDialog.close"/>
        </dspel:a>
      </div>
</div>
    </body>

   <script type="text/javascript">
      function conditionalRefresh() {
        <c:if test="${importAsset.importNewCount > 0 || importAsset.importSuccessCount > 0}">
           if (parent.refresh) {
              parent.refresh();
           }
          if (parent.displaySelectedListAssetRightPane) {
             parent.displaySelectedListAssetRightPane();
          }
         <c:set var="treeComponent" value="${sessionInfo.treeStatePath}"/>
 
          <c:if test="${treeComponent != null}">
               <dspel:importbean var="treeState" bean="${treeComponent}"/>
               <c:set var="selTreeNodeURI" value="${treeState.selectedNode.URI}"/>
               var url = "<c:out value='${config.contextRoot}'/>/assetEditor/editAsset.jsp?assetURI=<c:out value='${selTreeNodeURI}'/>";
               var rightframe = parent.rightPaneIframe;
               if (rightframe != null) {
                   rightframe.document.location = url;
               }
          </c:if>
          
       </c:if> 
     }
   </script>

  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/importConfirmation.jsp#2 $$Change: 651448 $ --%>
