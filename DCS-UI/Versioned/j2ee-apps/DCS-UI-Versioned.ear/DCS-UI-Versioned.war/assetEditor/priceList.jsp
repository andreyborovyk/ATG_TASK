<%--
  Asset editor Price list tab for products and skus

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>
  <dspel:importbean var="config"
                  bean="/atg/commerce/web/Configuration"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>


  <script type="text/javascript"
              src="<c:out value='${config.contextRoot}'/>/scripts/priceListScripts.js">
  </script>

<c:set var="formHandler" value="${requestScope.formHandler}"/>
<c:set var="priceListHandlerPath" value="/atg/commerce/web/assetmanager/PriceListViewFormHandler"/>
<c:set var="catalogview" value="${false}" />

<c:choose>

  <c:when test="${empty requestScope.atgCurrentAssetURI}">
    <br><br><br><div align="center"><fmt:message key='priceLists.transientItemMessage'/></div>
  </c:when>

  <c:otherwise>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <dspel:importbean var="priceListFormHandler" bean="${priceListHandlerPath}"/>
  <c:set target="${priceListFormHandler}" property="formHandler" value="${formHandler}"/>
  <c:set target="${formHandler}" property="priceListFormHandler" value="${priceListFormHandler}"/>
  <c:set target="${priceListFormHandler}" property="initializePriceListTable" value="${true}"/>

<script type="text/javascript">

  var productQueryable = <c:out value='${priceListFormHandler.isProductDisplayNameQueryable}'/>;
  var skuQueryable = <c:out value='${priceListFormHandler.isSkuDisplayNameQueryable}'/>;

function updateChildLabels(rowId, index, priceStr) {
      if (index > 0)
        updateLabel(rowId,index-1, priceStr);
}

function selectType() {

  var selectBox = document.getElementById("typeSelect");
  var selectValue = selectBox.options[selectBox.selectedIndex].value;

  var assetTypeInput = document.getElementById("assetType");
  var showSubSkus = document.getElementById("showSubSku");
  var searchTypeLabel = document.getElementById("searchTypeLabel");

  if (selectValue == 0) {
    assetTypeInput.value="product";
    showSubSkus.checked=false;
   searchTypeLabel.innerHTML = "<fmt:message key='priceLists.product'/>";
  }

  else if (selectValue == 1) {
    assetTypeInput.value="product";
    showSubSkus.checked=true;
   searchTypeLabel.innerHTML = "<fmt:message key='priceLists.product'/>";
  }

  else if (selectValue == 2) {
    assetTypeInput.value="sku";
    showSubSkus.checked=false;
    searchTypeLabel.innerHTML = "<fmt:message key='priceLists.sku'/>";
  }

  showSearchSpan(assetTypeInput.value);
}

function loadSelectType() {
  var selectBox = document.getElementById("typeSelect");
  var selectValue = selectBox.options[selectBox.selectedIndex].value;

  var assetTypeInput = document.getElementById("assetType");
  var showSubSkus = document.getElementById("showSubSku");
  var searchTypeLabel = document.getElementById("searchTypeLabel");

  if (assetTypeInput.value == "product" && !showSubSkus.checked) {
     selectBox.selectedIndex = 0;
     searchTypeLabel.innerHTML = "<fmt:message key='priceLists.product'/>";
  }

  else if (assetTypeInput.value == "product" && showSubSkus.checked) {
   selectBox.selectedIndex = 1;
   searchTypeLabel.innerHTML = "<fmt:message key='priceLists.product'/>";
  }

  else  {
   selectBox.selectedIndex = selectBox.length -1;
   searchTypeLabel.innerHTML = "<fmt:message key='priceLists.sku'/>";
  }

  showSearchSpan(assetTypeInput.value);
}


function showSearchSpan(assetTypeString) {
  var searchSpan = document.getElementById("searchSpan");
  if (searchSpan != null) {
    if ( (assetTypeString == "product" && productQueryable) ||
       (assetTypeString == "sku" && skuQueryable) ) {
       searchSpan.style.display = "inline";
    }  else {
       searchSpan.style.display = "none";
    }
  }
}

function startNewSearch() {
  var modCB = document.getElementById("modifiedTable");
  var saveAndFilter = document.getElementById("saveAndFilter");
  var clearAndFilter = document.getElementById("clearAndFilter");
  if (modCB.checked && confirm("<fmt:message key='priceLists.confirmStartNewSearch'/>")) {
    saveAndFilter.click();
  }
  // TODO:  should the 'else' be removed.
  else clearAndFilter.click();

}

  registerOnLoad( function() { loadSelectType();});

</script>


  <br/>
  <br/>

  <table class="data" id="priceListsTable">

  <thead>

    <tr>
      <td colspan="10" class="tableFilterHeader">
         <table border="0" class="topTableFilter">
           <tr>
             <td colspan="3">
               <fmt:message key="priceLists.show"/>
               <select id="typeSelect" onchange="selectType()" >
                 <option value="0">
                   <fmt:message key="priceLists.products"/>
                 </option>
                 <c:if test="${priceListFormHandler.displaySubSkusOption}">
                   <option value="1">
                     <fmt:message key="priceLists.productsubskus"/>
                   </option>
                 </c:if>
                 <option value="2">
                   <fmt:message key="priceLists.skus"/>
                 </option>
               </select>
               <span id="searchSpan">
                 &nbsp &nbsp <span id="searchTypeLabel"><fmt:message key='priceLists.product'/></span> <fmt:message key="priceLists.startswith"/>
                 <dspel:input  bean="${priceListHandlerPath}.filterString" />

               &nbsp &nbsp <input type="button" onclick="startNewSearch()" class="small" value="<fmt:message key='priceLists.list'/>" />
               </span>
             </td>
           </tr>
         </table>
       </td>

    <tr>
      <th class="itemHeader">

      </th>
      <c:forEach items="${priceListFormHandler.priceLists}" var="priceList"  >
        <th colspan="3" class="item leftSeparator"><c:out value="${priceList.itemDisplayName}"/></th>
      </c:forEach>
    </tr>
    <tr class="subHeader">
      <th></th>
      <c:forEach items="${priceListFormHandler.priceLists}" var="priceList">
        <th class="item center leftSeparator"><fmt:message key="priceLists.inheritSubHeader"/></th>
        <th class="center"><fmt:message key="priceLists.priceSubHeader"/></th>
        <th class="center"><fmt:message key="priceLists.volSubHeader"/></th>
      </c:forEach>
  </thead>

  <tbody>


    <%--
         Set up price list page loop
    --%>
    <dspel:test var="assetCountInfo" value="${priceListFormHandler.catalogAssets}"/>
    <c:set var="totalNumberRows" value="${priceListFormHandler.numberRows}"/>
    <c:if test="${totalNumberRows < 0}">
      <c:set var="totalNumberRows" value="${0}"/>
    </c:if>
    <c:set var="numRowsPerPage" value="${priceListFormHandler.maxRowsPerPage}"/>
    <c:set var="formpriority" value="${2*assetCountInfo.size}"/>
    <c:set var="paging" value="false"/>
    <c:if test="${priceListFormHandler.resultPageCount > 1}">
      <c:set var="paging" value="true"/>
      <c:set var="currentPageIndex" value="${priceListFormHandler.currentPageNumber -1}"/>
    </c:if>
    <c:set var="beginIndex" value="0"/>
    <c:set var="endIndex" value="${numRowsPerPage-1}"/>

   <%--
          Loop through each asset on this page
    --%>
    <c:forEach items="${priceListFormHandler.catalogAssets}" var="rowasset" begin="${beginIndex}" end="${endIndex}" varStatus="loop">

      <%-- if the pricelist's acl isn't writable, make this column read only --%>
      <c:set var="editable" value="${requestScope.atgIsAssetEditable}"/>

      <c:set var="rowIndex" value="${loop.index}"/>
      <c:set var="rowid" value="${rowasset.repositoryId}"/>
      <c:set var="rowname" value="${rowasset.itemDisplayName}"/>

      <dspel:setvalue bean="${priceListHandlerPath}.currentPriceCellInfoKey[${rowIndex}]" value="${rowid}"/>
      <c:set var="formpriority" value="${formpriority -1}"/>
      <dspel:input type="hidden" bean="${priceListHandlerPath}.currentPriceCellInfoKey[${rowIndex}]" value="${rowid}" id="plistkey${formpriority}" priority="${formpriority}"/>
      <c:set var="formpriority" value="${formpriority -1}"/>

      <%@ include file="priceTableRow.jspf" %>

    </c:forEach>

  </tbody>

  <tfoot>
    <tr>

      <td colspan="1" class="left"><c:out value="${totalNumberRows}"/>
        <c:choose>
          <c:when test="${priceListFormHandler.assetType == 'product' && priceListFormHandler.showSubSkus}">
            <fmt:message key="priceLists.productsubskus"/>
          </c:when>
          <c:when test="${priceListFormHandler.assetType == 'product'}">
            <fmt:message key="priceLists.products"/>
          </c:when>
          <c:otherwise>
            <fmt:message key="priceLists.skus"/>
          </c:otherwise>
        </c:choose>
      </td>

      <%-- Paging --%>
      <td colspan="10" class="right">

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

<%-- hidden input for asset type --%>
<dspel:input id="assetType" type="hidden" bean="${priceListHandlerPath}.assetType"/>

<%-- hidden show subskus --%>
<dspel:input id="showSubSku" type="checkbox" bean="${priceListHandlerPath}.showSubSkus" style="display:none"/>

<%-- hidden submit clear and filter the table --%>
<dspel:input id="clearAndFilter" type="submit" bean="${priceListHandlerPath}.clearAndFilter" style="display:none"/>

<%-- hidden submit clear and filter the table --%>
<dspel:input id="saveAndFilter" type="submit" bean="${priceListHandlerPath}.saveAndFilter" style="display:none"/>


  </c:otherwise>

</c:choose>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/Versioned/src/web-apps/DCS-UI-Versioned/assetEditor/priceList.jsp#2 $$Change: 651448 $--%>
