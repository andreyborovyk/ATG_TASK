<%--
  Some jsp at the bottom of the dimensionFolder edit page.

  The following request-scoped variables are expected to be set:
    atgCurrentAsset

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/dimensionFolderAddPage.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"              uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="fmt"            uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search"        %>

<dspel:page>
  <dspel:importbean var="config"
                  bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle var="resourceBundle" basename="${config.resourceBundle}"/>

  <assetui-search:getMaxFolderChildren       var="maxFolderChildCount" item="${requestScope.atgCurrentAsset.asset}"/> 
  <assetui-search:getAllOthersId var="allOthersId"   item="${requestScope.atgCurrentAsset.asset}"/> 
  <c:choose>
    <c:when test="${not empty requestScope.atgCurrentParentAsset}">
      <assetui-search:isChildDimensionServiceAvailable var="canCreateChildFolder" item="${requestScope.atgCurrentAsset.asset}" parentItem="${requestScope.atgCurrentParentAsset.asset}"/>
    </c:when>
    <c:otherwise>
      <assetui-search:isChildDimensionServiceAvailable var="canCreateChildFolder" item="${requestScope.atgCurrentAsset.asset}"/>
    </c:otherwise>
  </c:choose>

  <script type="text/javascript">

       <c:if test="${not empty allOthersId && not empty allOthersId.searchConfigId}">
         if (window['searchConfigs_info'] != undefined) {
           searchConfigs_info.setStickLastRowCount(1);
         }
       </c:if>

       <c:if test="${not empty allOthersId && not empty allOthersId.folderId}">
         if (window['childDimensionFolders_info'] != undefined) {
           childDimensionFolders_info.setStickLastRowCount(1);
         }
       </c:if>


        // This variable holds the max count of config and folder children.  
        var maxChildCount = <c:out value="${maxFolderChildCount}"/>;
 
        //
        // This function is called after adding/deleting a new row.
        function onChangeTotalChildrenFunc() {

           var currentCount = 0;
           if (window['searchConfigs_info'] != undefined) currentCount = searchConfigs_info.getRowCount();
           if (window['childDimensionFolders_info'] != undefined) currentCount = currentCount + childDimensionFolders_info.getRowCount();

           // the dimensionvalue becomes editable if all children are removed.
           if (currentCount == 0) {
             var theselector = document.getElementById("searchDimensionSelector");
             var readonlyvalue = document.getElementById("searchDimensionReadOnlyValue");
             if (theselector)
               theselector.style.display="inline";
             if (readonlyvalue) 
               readonlyvalue.style.display="none";
           }

        }


        // If al dimension values have been used, warn user and don't allow the add.
        function onCreateSearchConfigFunc(index) {

          var currentCount = 0;
          if (window['searchConfigs_info'] != undefined) currentCount = searchConfigs_info.getRowCount();
          if (window['childDimensionFolders_info'] != undefined) currentCount = currentCount + childDimensionFolders_info.getRowCount();
          if (maxChildCount != -1 && currentCount >= maxChildCount) {
            alert("<fmt:message bundle='${resourceBundle}' key='dimensionFolderAddPage.createConfigTotalReached'/>");
            return false;
          }
          // if there is only 'all others' then warn user and don't allow create search config
          <c:if test="${empty allOthersId.searchConfigId && allOthersId.mixedContent}">
            if(currentCount == (maxChildCount -1)) {
              alert("<fmt:message bundle='${resourceBundle}' key='dimensionFolderAddPage.createConfigTotalReached'/>");
              return false;
            }
          </c:if>

          return true;
        }


        // There are several reasons the user might not be allowed to add a new dimension folder. 
        // Warn the user and dissallow the add.
        function onCreateDimFolderFunc(index) {
          var currentCount = 0;
          if (window['searchConfigs_info'] != undefined) currentCount = searchConfigs_info.getRowCount();
          if (window['childDimensionFolders_info'] != undefined) currentCount = currentCount + childDimensionFolders_info.getRowCount();
          if (maxChildCount != -1 && currentCount >= maxChildCount) {
            alert("<fmt:message bundle='${resourceBundle}' key='dimensionFolderAddPage.createFolderTotalReached'/>");
            return false;
          }

          // if there is only 'all others' then warn user and don't allow create search config
          <c:if test="${not empty allOthersId.searchConfigId}">
            var allOthersConfigId = "<c:out value="${allOthersId.searchConfigId}"/>";
            var inputValues = document.getElementsByTagName("input");
            for (var i =0; inputValues != null && i < inputValues.length; i++) {
             var inputid = inputValues[i].id;
             var isCollectionInput = false;
             if (inputid != null && inputid.indexOf("_dspInput_") != -1) isCollectionInput = true;
             if (isCollectionInput && allOthersConfigId == inputValues[i].value) {
               alert("<fmt:message bundle='${resourceBundle}' key='dimensionFolderAddPage.createFolderAllOtherConfig'/>");
               return false;
             }
            }
          </c:if>

           // child dimensionFolders can only be added if there are available dimension services
           var canCreateChildFolder = <c:out value="${canCreateChildFolder}"/>;
           if (!canCreateChildFolder) {
             alert("<fmt:message bundle='${resourceBundle}' key='dimensionFolderAddPage.createFolderNoDimensions'/>");
             return false;
           }

           return true;
        }
     

        //
        // This function is called after deleting a row in the dimension folder row
        function onDeleteDimFolderRowFunc() {
          var delConfirm = confirm("<fmt:message bundle='${resourceBundle}' key='dimensionFolderAddPage.confirmDimFolderDelete'/>");
          if (delConfirm)
            return true;
          else
            return false;
        }
        
        //
        //
        function onDeleteSearchConfigRowFunc() {
          var delConfirm = confirm("<fmt:message bundle='${resourceBundle}' key='dimensionFolderAddPage.confirmSearchConfigDelete'/>");
          if (delConfirm)
            return true;
          else
            return false;
        }
        
        if (window['searchConfigs_info'] != undefined) {
          searchConfigs_info.onTotalCountChange = onChangeTotalChildrenFunc;
          searchConfigs_info.onDeleteRow = onDeleteSearchConfigRowFunc;
          searchConfigs_info.onCreateAsset = onCreateSearchConfigFunc;
        }

        if (window['childDimensionFolders_info'] != undefined) {
          childDimensionFolders_info.onTotalCountChange = onChangeTotalChildrenFunc;
          childDimensionFolders_info.onDeleteRow = onDeleteDimFolderRowFunc;
          childDimensionFolders_info.onCreateAsset = onCreateDimFolderFunc;
        }

        registerOnLoad(function() {
          onChangeTotalChildrenFunc();  
        });       
   </script>
 
</dspel:page>

<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/dimensionFolderAddPage.jsp#2 $$Change: 651448 $--%>
