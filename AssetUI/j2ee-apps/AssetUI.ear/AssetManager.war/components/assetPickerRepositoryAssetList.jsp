<%--
  An asset picker plug-in which displays a flat list of repository assets for
  a given repository and item type.

  @param  assetInfoPath        - The session scoped AssetInfo Nucleus path
  @param  currentPageNumber    - The current page number
  @param  performedSearch      - A flag set if a search has been performed

  Javascript methods implemented:
    getSelectedAssets()
      Returns an array of of associative arrays, each consisting of the
      following keys and values:
        id          - the asset's ID
        displayName - the asset's display name
        uri         - the asset's version manager URI

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/assetPickerRepositoryAssetList.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %> 
<%@ taglib prefix="web-ui"  uri="http://www.atg.com/taglibs/web-ui" %>

<dspel:page>

<dspel:importbean var="config"
                  bean="/atg/web/assetmanager/ConfigurationInfo"/>
<fmt:setBundle var="bundle" basename="${config.resourceBundle}"/>

<%-- Unpack parameters --%>
<dspel:getvalueof var="assetInfoPath"     param="assetInfo"/>
<dspel:getvalueof var="currentPageNumber" param="pageNumber"/>
<dspel:getvalueof var="performedSearch"   param="performedSearch"/>

<dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>

<c:set var="itemType"      value="${assetInfo.attributes.itemType}"/>
<c:set var="componentPath" value="${assetInfo.attributes.componentPath}"/>

<dspel:importbean var="preferences"
                  bean="/atg/web/assetmanager/Preferences"/>

<div id="assetBrowserContentBody">

  <div id="assetListHeader">
    <div id="assetListHeaderRight"></div>
    <div id="assetListHeaderLeft"></div>
  </div>

  <div id="scrollContainer">

    <script type="text/javascript">
      //
      // results - array of result objects
      //
      var results = new Array();
    </script>

    <form id="resultsForm" name="resultsForm" action="#">
    <dspel:droplet name="/atg/web/assetmanager/list/RepositoryAssetListDroplet">
      <%-- Input Parameters --%>
      <dspel:param name="componentPath"     value="${componentPath}"/>
      <dspel:param name="assetType"         value="${itemType}"/>
      <dspel:param name="performedSearch"   value="${performedSearch}"/>
      <dspel:param name="currentPageNumber" value="${currentPageNumber}"/>

      <%-- Render "outputStart" open parameter --%>
      <dspel:oparam name="outputStart">
        <table id="assetListContentTable" cellpadding="0" cellspacing="0">
      </dspel:oparam>

      <%-- Render "output" open parameter once for each item --%>
      <dspel:oparam name="output">
        <%-- Determine the asset icon --%>
        <web-ui:getIcon var="iconUrl"
                        container="${componentPath}"
                        type="${itemType}"
                        assetFamily="RepositoryItem" />
        <tr>
        <td class="checkboxCell"><input type="radio" name="selectedAssets" value="<dspel:valueof param='element.id'/>"/></td>
        <td class="nameCell">
          <c:if test="${preferences.showIdToolTips}">
            <dspel:getvalueof var="tooltip" param="element.id"/>
          </c:if>
          <a title="<c:out value='${tooltip}'/>">
            <dspel:img otherContext="${iconUrl.contextRoot}" src="${iconUrl.relativeUrl}" border="0" align="absmiddle"/>&nbsp;<span class="asset"><dspel:valueof param='element.displayName'/></span>
          </a>
        </td>
        </tr>
        <script type="text/javascript">
          var result = new Object();
            result.id          = "<dspel:valueof param='element.id'/>";
            result.displayName = "<dspel:valueof param='element.displayName'/>";
            result.uri         = "<dspel:valueof param='element.uri'/>";
          results[ "<dspel:valueof param='element.id'/>" ] = result;
        </script>
      </dspel:oparam>

      <%-- Render "outputEnd" open parameter --%>
      <dspel:oparam name="outputEnd">
        </table>
        </div> <%-- id="scrollContainer" --%>
        <dspel:getvalueof var="isPageResults"     param="isPageResults"/>
        <dspel:getvalueof var="size"              param="size"/>
        <dspel:getvalueof var="maxResultsPerPage" param="maxResultsPerPage"/>
        <dspel:getvalueof var="currentPageNumber" param="currentPageNumber"/>
        <dspel:getvalueof var="lastPageNumber"    param="lastPageNumber"/>
        
        <c:url var="prevPageURL" context="/AssetUI" value="/assetPicker/assetPicker.jsp">
          <c:param name="assetInfo"       value="${assetInfoPath}"/>
          <c:param name="performedSearch" value="1"/>
          <c:param name="pageNumber"      value="${currentPageNumber-1}"/>
        </c:url>
        <c:url var="nextPageURL" context="/AssetUI" value="/assetPicker/assetPicker.jsp">
          <c:param name="assetInfo"       value="${assetInfoPath}"/>
          <c:param name="performedSearch" value="1"/>
          <c:param name="pageNumber"      value="${currentPageNumber+1}"/>
        </c:url>

        <table id="scrollFooter">
          <tr>
          <td id="footerCount">
            <c:if test="${isPageResults && (size > maxResultsPerPage)}">
              <div id="footerCountRight" class="pagination">
                <div class="pagingControls">
                  <%-- <input type="text" size="3" class="pageInput" /> <input type="button" value="Go" /> --%>
                  <c:choose>
                    <c:when test="${currentPageNumber == 1}">
                      <a title="<fmt:message key='assetPickerRepositoryAssetList.firstPage' bundle='${bundle}'/>" class="icon pageBack inactive"></a>
                    </c:when>
                    <c:otherwise>
                      <a href="<c:out value='${prevPageURL}'/>" title="<fmt:message key='assetPickerRepositoryAssetList.goToPage' bundle='${bundle}'/> <c:out value='${currentPageNumber-1}'/>" class="icon pageBack"></a>
                    </c:otherwise>
                  </c:choose>
                  <span><dspel:valueof param="currentPageNumber"/> <fmt:message key='assetPickerRepositoryAssetList.of' bundle='${bundle}'/> <dspel:valueof param="lastPageNumber"/></span>
                  <c:choose>
                    <c:when test="${currentPageNumber == lastPageNumber}">
                      <a href="#" title="<fmt:message key='assetPickerRepositoryAssetList.lastPage' bundle='${bundle}'/>" class="icon pageForward inactive"></a>
                    </c:when>
                    <c:otherwise>
                      <a href="<c:out value='${nextPageURL}'/>" title="<fmt:message key='assetPickerRepositoryAssetList.goToPage' bundle='${bundle}'/> <c:out value='${currentPageNumber+1}'/>" class="icon pageForward"></a>
                    </c:otherwise>
                  </c:choose>
                </div>
              </div>
              <div id="footerCountLeft"><dspel:valueof param="size"/> <fmt:message key='assetPickerRepositoryAssetList.assets' bundle='${bundle}'/></div>
            </c:if>
          </td>
          </tr>
        </table>
      </dspel:oparam>

      <%-- Render "empty" open parameter when there are no search results --%>
      <dspel:oparam name="empty">
        <table id="assetListContentTable" cellpadding="0" cellspacing="0">
          <tr>
            <td class="nameCell">
              <fmt:message key='assetPickerRepositoryAssetList.noAssetsToChooseFrom' bundle='${bundle}'/>
            </td>
          </tr>
        </table>
        </div> <%-- id="scrollContainer" --%>
        <table id="scrollFooter">
          <tr>
          <td id="footerCount">
            <div id="footerCountRight" class="pagination">
              <div class="pagingControls"></div>
            </div>
            <div id="footerCountLeft">
              <dspel:valueof param="size"/> <fmt:message key='assetPickerRepositoryAssetList.assets' bundle='${bundle}'/>
            </div>
          </td>
          </tr>
        </table>
      </dspel:oparam>

      <%-- Render "error" open parameter when there are errors in the droplet --%>
      <dspel:oparam name="error">
        <table id="assetListContentTable" cellpadding="0" cellspacing="0">
          <tr>
            <td class="nameCell">
              <dspel:valueof param="element"/>
            </td>
          </tr>
        </table>
        </div> <%-- id="scrollContainer" --%>
        <table id="scrollFooter">
          <tr>
          <td id="footerCount">
            <div id="footerCountRight" class="pagination">
              <div class="pagingControls"></div>
            </div>
            <div id="footerCountLeft">
            </div>
          </td>
          </tr>
        </table>
      </dspel:oparam>
    </dspel:droplet>
    </form>

    <script type="text/javascript">
      //
      // Returns an array of of associative arrays, each consisting of the
      // following keys and values:
      //   id          - the asset's ID
      //   displayName - the asset's display name
      //   uri         - the asset's version manager URI
      //
      function getSelectedAssets() 
      {
        var selectedResults = new Array();
        var resultsForm     = document.resultsForm;

        if ( resultsForm.selectedAssets != null ) 
        {
          // this is the condition where there is one result
          if ( resultsForm.selectedAssets.length == null ) 
          {
            if ( resultsForm.selectedAssets.checked == true) 
            {
              var result = results[ resultsForm.selectedAssets.value ];
              selectedResults[0] = result;
            }
          }
          // multiple results
          else 
          {
            var j = 0;
            for (var i=0; i < resultsForm.selectedAssets.length; i++) 
            {
              if (resultsForm.selectedAssets[i].checked == true) 
              {
                var result = results[ resultsForm.selectedAssets[i].value ];
                selectedResults[j] = result;
                j++;
              }
            }
          }
        }
        return selectedResults;
      }
    </script>

</div> <%-- id="assetBrowserContentBody" --%>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/assetPickerRepositoryAssetList.jsp#2 $$Change: 651448 $--%>
