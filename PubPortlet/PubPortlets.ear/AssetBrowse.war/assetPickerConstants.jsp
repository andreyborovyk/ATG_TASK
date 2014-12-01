<!-- Begin assetPickerConstants.jspf -->
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<c:set var="MAX_SEARCH_RESULTS" value="20"/>
<c:set var="AP_CONTAINER" value="2" />
<c:set var="AP_CONTAINER_PAGE" value="assetPickerContainer.jsp" />
<c:set var="AP_ITEM_TYPE_SELECT" value="1" />
<c:set var="AP_ITEM_TYPE_SELECT_PAGE" value="itemTypeSelection.jsp" />
<c:set var="REPOSITORY_SEARCH_RESULTS" value="1" />
<c:set var="VFS_SEARCH_RESULTS" value="2" />
<c:set var="TREE_VIEW" value="5" />
<c:set var="ASSET_DETAILS_CONDENSED" value="3" />
<c:set var="ASSET_DETAILS" value="2" />
<c:set var="REPOSITORY_SEARCH_RESULTS_PAGE" value="repositorySearchResults.jsp" />
<c:set var="VFS_SEARCH_RESULTS_PAGE" value="vfsSearchResults.jsp" />
<c:set var="TREE_VIEW_PAGE" value="treeView.jsp" />
<c:set var="ASSET_DETAILS_CONDENSED_PAGE" value="assetDetailsCondensed.jsp" />
<c:set var="ASSET_DETAILS_PAGE" value="assetDetails.jsp" />
<c:set var="ASSET_PICKER_IFRAME" value="10" />
<c:set var="BROWSE_TAB" value="11" />
<c:set var="ASSET_PICKER_IFRAME_URL" value="/assetPicker/assetPicker.jsp" />
<c:set var="BROWSE_TAB_URL" value="/assetBrowse.jsp" />
<c:set var="ASSET_PORTLET_URL" value="/bcc/assets" />

<c:set var="BROWSE_CONTAINER_PAGE" value="assetBrowseContainer.jsp" />
<c:set var="BROWSE_ITEM_TYPE_SELECT_PAGE" value="assetBrowseItemTypeSelection.jsp" />
<c:set var="BCC_CONTEXT" value="/atg" />
<c:set var="ASSETBROWSE_CONTEXT" value="/AssetBrowse" />
<c:set var="ASSETUI_CONTEXT" value="/AssetUI" />

<%-- 
     These constants match those in atg.epub.portlet.asset.AssetPortlet
     and are used for determining which view to display in the AssetBrowse webapp
--%>
<c:set var="SEARCH_INITIAL" value="0" />
<c:set var="SEARCH_RESULTS" value="1" />
<c:set var="ASSET_PROPERTIES" value="2" />
<c:set var="ASSET_HISTORY" value="3" />

</dsp:page>



<!-- End assetPickerConstants.jspf -->
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/assetPickerConstants.jsp#2 $$Change: 651448 $--%>
