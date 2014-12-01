<%--
  Asset editor Price list tab for catalog tree

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                  %>

<dspel:page>
  <dspel:importbean var="config"
                  bean="/atg/commerce/web/Configuration"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <script type="text/javascript"
              src="<c:out value='${config.contextRoot}'/>/scripts/priceListScripts.js">
  </script>

<c:set var="formHandler" value="${requestScope.formHandler}"/>
<c:set var="priceListHandlerPath" value="/atg/commerce/web/assetmanager/PriceListFormHandler"/>
<c:set var="catalogview" value="${true}" />

<c:choose>

  <c:when test="${empty requestScope.atgCurrentAssetURI}">
    <br><br><br><div align="center"><fmt:message key='priceLists.transientItemMessage'/></div>
  </c:when>

  <c:otherwise>

  <%-- set and import variables used in the page --%>
  <dspel:importbean var="priceListFormHandler" bean="${priceListHandlerPath}"/>
  <c:set target="${priceListFormHandler}" property="formHandler" value="${formHandler}"/>
  <c:set target="${formHandler}" property="priceListFormHandler" value="${priceListFormHandler}"/>
  <c:set target="${priceListFormHandler}" property="initializePriceListTable" value="${true}"/>


  <c:set var="numRowsPerPage" value="${priceListFormHandler.maxRowsPerPage}"/>
  <c:choose>
    <c:when test="${priceListFormHandler.lazyLoadingOff}">
      <c:set var="beginIndex" value="${priceListFormHandler.startIndex}"/>
      <c:set var="endIndex" value="${priceListFormHandler.endIndex}"/>
      <c:if test="${endIndex < 0}">
        <c:set var="endIndex" value="${0}"/>
      </c:if>
    </c:when>
    <c:otherwise>
      <c:set var="beginIndex" value="0"/>
      <c:set var="endIndex" value="${numRowsPerPage-1}"/>
    </c:otherwise>
  </c:choose>

<script type="text/javascript">

function updateChildLabels(rowId, index, priceStr) {
  // for each first level child of this price list, update its inherit label
  var mapKey = rowId + "." + index;
  if (priceListMap[mapKey]) {
    for (var i = 0; i < priceListMap[mapKey].length; i++) {
      updateLabel(priceListMap[mapKey][i],index, priceStr);
    }
  }
}

</script>

  <br/>
  <br/>

  <table class="data" id="priceListsTable">

  <thead>

    <tr>

      <c:if test="${priceListFormHandler.displayHideInheritedCheckbox}">
        <web-ui:collectionPropertySize var="numParentProducts" collection="${priceListFormHandler.parentProducts}"/>
        <c:set var="colSpanSize" value="${(numParentProducts * 3) + 4}"/>
        <td colspan="<c:out value='${colSpanSize}'/>" class="tableFilterHeader">
           <table border="0" class="topTableFilter">
             <tr>
               <td colspan="3">
                 <dspel:input type="checkbox" bean="${priceListHandlerPath}.hideInherited" onclick="submitPriceListTable()"/> <fmt:message key="priceLists.hideInherited"/>
               </td>
             </tr>
           </table>
         </td>
       </c:if>



    <tr>
      <th class="itemHeader"><fmt:message key="priceLists.header"/></th>
      <th colspan="3" class="item leftSeparator">
        <c:choose>
          <c:when test="${priceListFormHandler.skuSelected}">
            <fmt:message key="priceLists.skuHeader"/>
          </c:when>
          <c:when test="${priceListFormHandler.productSelected}">
            <fmt:message key="priceLists.prodHeader"/>
          </c:when>
          <c:otherwise>
            <fmt:message key="priceLists.otherHeader"/>
          </c:otherwise>
        </c:choose>
      </th>
      <c:forEach items="${priceListFormHandler.parentProducts}" var="parentProduct"  >
        <th colspan="3" class="item leftSeparator"><fmt:message key="priceLists.inProductHeader"/> <c:out value="${parentProduct.itemDisplayName}"/></th>
      </c:forEach>
    </tr>
    <tr class="subHeader">
      <th></th>
      <th class="item center leftSeparator"><fmt:message key="priceLists.inheritSubHeader"/></th>
      <th class="center"><fmt:message key="priceLists.priceSubHeader"/></th>
      <th class="center"><fmt:message key="priceLists.volSubHeader"/></th>
      <c:forEach items="${priceListFormHandler.parentProducts}" var="parentProd" >
        <th class="item center leftSeparator"><fmt:message key="priceLists.inheritSubHeader"/></th>
        <th class="center"><fmt:message key="priceLists.priceSubHeader"/></th>
        <th class="center"><fmt:message key="priceLists.volSubHeader"/></th>
      </c:forEach>
  </thead>


  <tbody>


  <%-- Set up javascript variables --%>
  <script type="text/javascript">
    <c:forEach items="${priceListFormHandler.displayPriceLists}" var="aplist" begin="${beginIndex}" end="${endIndex}"  varStatus="aloop">
    <c:set var="plistid" value="${aplist.repositoryId}"/>
      <c:set var="aCellInfoPath" value="${priceListHandlerPath}.priceCellInfo.${plistid}"/>
      <dspel:getvalueof var="aCellInfoBeans" bean="${aCellInfoPath}"/>

      // for each item in row
      <c:forEach items="${aCellInfoBeans}" var="${aCellInfo}" varStatus="inLoop">
        <c:set var="iIndex" value="${inLoop.index}"/>
        <dspel:getvalueof var="cellpid" bean="${aCellInfoPath}[${iIndex}].priceListId"/>
        <dspel:getvalueof var="ainherit" bean="${aCellInfoPath}[${iIndex}].inherit"/>
        <dspel:getvalueof var="volumeInfo" bean="${aCellInfoPath}[${iIndex}].volumePriceInfo"/>
        <c:set var="uid" value="${cellpid}.${iIndex}"/>

        inheritCellList["<c:out value='${uid}'/>"] = <c:out value="${ainherit}"/>;
        volumeInfoCellList["<c:out value='${uid}'/>"] = "<c:out value='${volumeInfo}'/>";

        childPriceLists = new Array();
        <c:forEach items="${priceListFormHandler.priceListChildren[uid]}" var="pListChildId" varStatus="cLoop">
          childPriceLists["<c:out value='${cLoop.index}'/>"] = "<c:out value="${pListChildId}"/>";
        </c:forEach>
        priceListMap["<c:out value='${uid}'/>"] = childPriceLists;
      </c:forEach>

    </c:forEach>
  </script>


    <%--
         Set up price list page loop
    --%>
    <dspel:test var="priceListCountInfo" value="${priceListFormHandler.displayPriceLists}"/>
    <c:set var="totalNumberRows" value="${priceListFormHandler.numberRows}"/>

    <c:if test="${totalNumberRows < 0}">
      <c:set var="totalNumberRows" value="${0}"/>
    </c:if>
    <c:set var="formpriority" value="${2*priceListCountInfo.size}"/>
    <c:set var="paging" value="false"/>
    <c:if test="${priceListFormHandler.resultPageCount > 1}">
      <c:set var="paging" value="true"/>
      <c:set var="currentPageIndex" value="${priceListFormHandler.currentPageNumber -1}"/>
    </c:if>

   <%--
          Loop through each price list on this page
    --%>
    <c:forEach items="${priceListFormHandler.displayPriceLists}" var="rowasset" begin="${beginIndex}" end="${endIndex}" varStatus="loop">
      <%-- if the pricelist's acl isn't writable, make this row read only --%>
      <c:set var="editable" value="${requestScope.atgIsAssetEditable}"/>
      <asset-ui:getAssetAccess item="${rowasset}" var="itemAccess"/>
      <c:if test="${! itemAccess.write}">
        <c:set var="editable" value="false"/>
      </c:if>

      <c:set var="rowIndex" value="${loop.count-1}"/>
      <c:set var="rowid" value="${rowasset.repositoryId}"/>
      <c:set var="rowname" value="${rowasset.itemDisplayName}"/>
      <c:set var="propertyNameTest" value=""/>
      <dspel:setvalue bean="${priceListHandlerPath}.currentPriceCellInfoKey[${rowIndex}]" value="${rowid}"/>
      <c:set var="formpriority" value="${formpriority -1}"/>
      <dspel:input type="hidden" bean="${priceListHandlerPath}.currentPriceCellInfoKey[${rowIndex}]" value="${rowid}" id="key${formpriority}" priority="${formpriority}"/>
      <c:set var="formpriority" value="${formpriority -1}"/>
      <%@ include file="priceTableRow.jspf" %>

    </c:forEach>

  </tbody>

  <tfoot>
    <tr>

      <td colspan="1" class="left"><c:out value="${totalNumberRows}"/> <fmt:message key="priceLists.totalLabel"/></td>

      <%-- Paging --%>
      <c:set var="colSpanSize" value="${(numParentProducts * 3) + 3}"/>
      <td colspan="<c:out value='${colSpanSize}'/>" class="right">

        <c:if test="${paging}">
          <%@ include file="priceTablePaging.jspf" %>
        </c:if>

      </td>
    </tr>
  </tfoot>
  </table>

<%-- hidden input to indicate if any modifications were made to the table --%>
<dspel:input id="modifiedTable" type="checkbox" bean="${priceListHandlerPath}.modifiedPriceTable" style="display:none"/>

<%-- hidden input for current page number --%>
<dspel:input id="currentPageInput" type="hidden" bean="${priceListHandlerPath}.currentPageNumber"/>

  </c:otherwise>

</c:choose>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/Versioned/src/web-apps/DCS-UI-Versioned/assetEditor/catalogPriceList.jsp#2 $$Change: 651448 $--%>
