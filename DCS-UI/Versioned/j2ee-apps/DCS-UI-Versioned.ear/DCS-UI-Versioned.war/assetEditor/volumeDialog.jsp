<%--
  Asset editor Price list tab for products and skus

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="dcsui"    uri="http://www.atg.com/taglibs/dcsui"        %>

<dspel:page>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>Asset Browser</title>
  <dspel:include src="/components/head.jsp" otherContext="/AssetManager"  />
</head>

<c:set var="uniqueId" value="${param.uniqueId}"/>
<c:set var="priceId" value="${param.priceId}"/>
<c:set var="volPriceInfo" value="${param.volPriceInfo}"/>
<c:set var="readOnly" value="${param.readOnly}"/>

<dspel:importbean var="config"
                  bean="/atg/commerce/web/Configuration"/>

<fmt:setBundle basename="${config.resourceBundle}"/>

<script type="text/javascript">

function copyValues() {

 var levelString;
 var bulkRadio = document.getElementById("bulkRadio");
 if (bulkRadio && bulkRadio.checked)
  levelString = "bulkPrice";
 else levelString = "tieredPrice";

 var levels = document.getElementById("levelTable").getElementsByTagName('INPUT');
 if (levels != null) {
   for (var i = 0; i < levels.length; i++) {
     if (i == 0)
       levelString = levelString + ":" + levels[i].value;
     else if (i%2 == 0)
       levelString = levelString + ";" + levels[i].value;
     else 
       levelString = levelString + "," + levels[i].value;
   }
 }
 
 // write the volume info back into the table
 var volumeInfoText = parent.rightPaneIframe.document.getElementById("volPriceInfo_<c:out value='${uniqueId}'/>");
 if (volumeInfoText) {
   volumeInfoText.value = levelString;
   // mark table modified
   parent.rightPaneIframe.modifiedTable("<c:out value='${uniqueId}'/>"); 
 }
}

function addRow() {

  var tableBody = document.getElementById("levelTable");

  // add header if this is first row.
  var numRows = tableBody.rows.length;
  if (numRows == 0) {
    var hRow = tableBody.insertRow(-1);
    var th1 = hRow.appendChild(document.createElement("th"));
    var th2 = hRow.appendChild(document.createElement("th"));
    var th3 = hRow.appendChild(document.createElement("th"));
    th1.innerHTML = "<fmt:message key='priceLists.volDialogQuantity'/>";
    th2.innerHTML = "<fmt:message key='priceLists.volDialogPrice'/>";
  }

  // add new row
  var tr = tableBody.insertRow(-1);
  var td1 = tr.insertCell(0);
  var td2 = tr.insertCell(1);
  var td3 = tr.insertCell(2);
  td3.className = "iconCell";

  var inputHTML = "<input type='text' class='formTextField number' />";
  td1.innerHTML = inputHTML;
  td2.innerHTML = inputHTML;
  td3.innerHTML = "<a href='#' onclick='deleteRow(event)' class='icon propertyDelete' title='Delete'></a>";

}

function deleteRow(e) {

  // get the target out of the event
  if (!e){ var e = window.event;}

  if (e.target){ targ = e.target;}
  else if (e.srcElement){ targ = e.srcElement;}

  // get this row in the table
  var tr = targ.parentNode.parentNode;
   
  // get the index of this row, and delete it
  var tableBody = document.getElementById("levelTable");
  for (var i = 0; i < tableBody.rows.length; i++) {
    if (tableBody.rows[i] == tr) {
      tableBody.deleteRow(i);
      break;
    }
  }
  // delete header if no more rows
  var numRows = tableBody.rows.length;
  if (numRows == 1)
    tableBody.deleteRow(0);
}

</script>

<body id="assetBrowser" >
  <%-- Title Header : Includes browser title 
   include file="includes/AssetBrowser_titleHeader_volumePricing.jsp"  --%>
  <div id="assetBrowserClose"><a href="#" onclick="maximize();" title="Maximize Browser" id="assetBrowserSize" class="maximizeButton"></a> <a href="#" onclick="parent.showIframe('volumeDialog');" title="Close Asset Browser" class="closeButton"></a></div>
  <h1><fmt:message key="priceLists.volDialogTitle"/></h1>

  <div id="assetBrowserContentWrapper">
    <div id="assetListHeader">
      <%-- include file="includes/AssetBrowser_subHeader_volumePricing.jsp" --%>
      <div id="assetListHeaderRight"></div>
      <div id="assetListHeaderLeft"><fmt:message key="priceLists.volDialogHeaderLeft"/></div>
    </div>
    <div id="scrollContainer">
      <%-- Scrollable Content : Main Content within the scrollable area 
       include file="includes/AssetBrowser_scrollableContent_volumePricing.jsp" --%>

      <form action="#">
     
        <c:choose>
          <c:when test="${readOnly}">
            <c:set var="disabledVar" value="disabled"/>
          </c:when>
          <c:otherwise>
            <c:set var="disabledVar" value=""/>
          </c:otherwise>
        </c:choose>
              
        <dcsui:getVolumePriceInfo var="volumePriceInfo" volumePriceString="${volPriceInfo}" priceId="${priceId}"/>
        <ul class="formVerticalList selectionBullets">
        <c:choose>
          <c:when test="${volumePriceInfo.tiered}">
            <li><input type="radio" id="bulkRadio" class="radioBullet" name="bulkTiered" <c:out value='${disabledVar}'/> /><fmt:message key="priceLists.volDialogBulk"/></li>
            <li><input type="radio" id="tieredRadio" class="radioBullet" name="bulkTiered" <c:out value='${disabledVar}'/> checked /><fmt:message key="priceLists.volDialogTiered"/></li>
          </c:when>
          <c:otherwise>
            <li><input type="radio" id="bulkRadio" class="radioBullet" name="bulkTiered" <c:out value='${disabledVar}'/> checked /><fmt:message key="priceLists.volDialogBulk"/></li>
            <li><input type="radio" id="tieredRadio" class="radioBullet" name="bulkTiered"  <c:out value='${disabledVar}'/> /><fmt:message key="priceLists.volDialogTiered"/></li>
          </c:otherwise>
        </c:choose>
        </ul>
      

        <table id="levelTable" class="data">
          <c:forEach var="priceLevel" items="${volumePriceInfo.levels}" varStatus="loop">
            <c:if test="${loop.first}">
              <tr>
                <th><fmt:message key="priceLists.volDialogQuantity"/></th>
                <th><fmt:message key="priceLists.volDialogPrice"/></th>
                <th></th>
              </tr>
            </c:if>
            <tr>
              <c:choose>
                <c:when test="${readOnly}">
                  <td><c:out value="${priceLevel.quantity}"/></td>
                  <td><c:out value="${priceLevel.price}"/></td>
                </c:when>
                <c:otherwise>
                  <td><input type="text" class="formTextField number" value="<c:out value='${priceLevel.quantity}'/>"/></td>
                  <td><input type="text" class="formTextField number" value="<c:out value='${priceLevel.price}'/>"/></td>
                  <td class="iconCell"><a href="#" onclick="deleteRow(event)" class="icon propertyDelete" title="Delete"></a></td>
                </c:otherwise>
              </c:choose>
            </tr>
          </c:forEach>
        </table>
        <c:if test="${!readOnly}">
          <div id="formSubmitButtonsLeft">
            <a href="javascript:addRow()" class="button" title="<fmt:message key='priceLists.volDialogAddPrice'/>"><span><fmt:message key="priceLists.volDialogAddPrice"/></span></a>
          </div>
        </c:if>
      </form>

    </div>

    <table id="scrollFooter">
      <tr>
        <td id="footerCount">
          <%-- Footer Pagination : Includes Item Count and Pagintation 
           include file="includes/AssetBrowser_footerPagination.jsp" --%>
          <div id="assetBrowserFooterCountRight"></div>
          <div id="assetBrowserFooterCountLeft"></div>
	</td>
      </tr>
    </table>
  </div>

  <%-- Footer Action Buttons : Action buttons at the bottome of the Asset Browser 
   include file="includes/AssetBrowser_footerActionButtons_okCancel.jsp" --%>
  <div id="assetBrowserFooterRight">
    <c:choose>
      <c:when test="${readOnly}">
        <a href="javascript:parent.showIframe('volumeDialog')" class="button abTrigger" title="<fmt:message key='priceLists.volDialogClose'/>"><span><fmt:message key="priceLists.volDialogClose"/></span></a>
      </c:when>
      <c:otherwise>
        <a href="javascript:copyValues();parent.showIframe('volumeDialog')" class="button abTrigger" title="<fmt:message key='priceLists.volDialogOK'/>"><span><fmt:message key="priceLists.volDialogOK"/></span></a> 
        <a href="javascript:parent.showIframe('volumeDialog')" class="button abTrigger" title="<fmt:message key='priceLists.volDialogCancel'/>"><span><fmt:message key="priceLists.volDialogCancel"/></span></a>
      </c:otherwise>
    </c:choose>
  </div>
  <div id="assetBrowserFooterLeft"></div>

  </body>
  </html>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/Versioned/src/web-apps/DCS-UI-Versioned/assetEditor/volumeDialog.jsp#2 $$Change: 651448 $--%>
