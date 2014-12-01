<%--
  Browse Picker item type selection page. 

  @version $Id: //product/PubPortlet/version/10.0.3/AssetBrowse.war/assetBrowseItemTypeSelection.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ page import="java.io.*,java.util.*,java.util.*" errorPage="/error.jsp"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %> 

<!-- Begin itemTypeSelection.jsp -->

<dspel:page>

<c:catch var="ex">

  <%@ include file="assetPickerConstants.jsp" %>

  <dspel:importbean var="config"
    bean="/atg/assetui/Configuration"/>

  <%-- set to true to see lots of debugging information --%>
  <c:set var="debug" value="false" scope="request"/>

  <%-- get BrowseAssetInfo --%>
  <c:choose>
    <c:when test="${ ! empty param.assetInfo }">
      <c:set var="assetInfoPath" value="${param.assetInfo}"/>
    </c:when>
    <c:otherwise>
      <c:set var="assetInfoPath" 
        value="/atg/epub/servlet/BrowseAssetInfo"/>
    </c:otherwise>
  </c:choose>
  <dspel:importbean bean="${assetInfoPath}" var="assetInfo"/>

  <%-- default view --%>
  <c:if test="${ ! empty param.defaultView }">
    <c:set target="${assetInfo.attributes}" 
      property="defaultView" value="${param.defaultView}"/>
  </c:if>
  <c:set var="defaultView" value="${assetInfo.attributes.defaultView}"/>

  <%-- Get the AssetPickerFormHandler --%>
  <c:set var="assetPickerFormHandlerPath" 
    value="/atg/web/assetbrowse/servlet/AssetPickerFormHandler"/>
  
  <dspel:importbean bean="${assetPickerFormHandlerPath}"
    var="assetPickerFormHandler"/>

  <c:url var="assetBrowserURL" value="${BROWSE_TAB_URL}">
    <c:param name="assetInfo" value="${assetInfoPath}"/>
      <c:param name="apView" value="${AP_CONTAINER}"/>
  </c:url>

  <%-- save asset view to session --%>
  <c:set target="${assetInfo.attributes}" property="assetview" value="${SEARCH_INITIAL}"/>
  <c:set target="${assetInfo.attributes}" property="asseturi" value=""/>

  <fmt:setBundle var="projectsBundle" 
    basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

  <div id="mainContent">
    <div id="intro">
      <h2><span class="pathSub">Browse :</span> </h2>
      <p>To create or find assets, first choose a Repository and Asset Type :</p>

    </div>  <!-- intro -->

    <div id="browseForm">
      <dspel:form formid="selectionForm" name="selectionForm" action="${assetBrowserURL}" method="post">

        <table cellpadding="0" cellspacing="0" border="0" width="100%" class="formTable">
	  <tr>
	  <td>
	    <table cellpadding="2" cellspacing="0" border="0" id="searchOptions">
              <tr>
	      <td class="formLabel formLabelRequired">Search in repository:</td>
              <td>
                <select class="formElement" name="selectedRepository" id="selectedRepository"
	          onchange="populateTypePullDown(
                  document.selectionForm.selectedRepository[ document.selectionForm.selectedRepository.options.selectedIndex ].value);">
                  <%-- populate options via JavaScript --%>
                </select>

	      </td>

	      </tr>
	      <tr>
	      <td class="formLabel">Show assets of type:</td>
	      <td>
                <select class="formElement" name="selectedItemType" id="selectedItemType" onchange="">
                  <%-- populate options via JavaScript --%>
                </select>
              </td>
	      </tr>
              <tr>
	      <td>&nbsp;</td>
	      <td>
                &nbsp;<a href="javascript:submitSearchAssets();" class="button" title="Search"><span>Search</span></a>
              </td>
	      </tr>
            </table>		
          </td>
          </tr>
	</table

        <dspel:input type="hidden" priority="10"
          bean="${assetPickerFormHandlerPath}.view"
          value="${defaultView}"
          id="view" name="view" />
        <dspel:input type="hidden" priority="10"
          bean="${assetPickerFormHandlerPath}.componentPath"
          id="componentPath" name="componentPath" />
        <dspel:input type="hidden" priority="10"
          bean="${assetPickerFormHandlerPath}.itemType"
          id="itemType" name="itemType" />
        <dspel:input type="hidden" priority="10"
          bean="${assetPickerFormHandlerPath}.isAllowMultiSelect"
          id="isAllowMultiSelect" name="isAllowMultiSelect" value="true" />

        <dspel:input type="hidden" priority="10"
          bean="${assetPickerFormHandlerPath}.viewConfiguration"
          value=""
          id="viewConfiguration" name="viewConfiguration" />
        <dspel:input type="hidden" priority="100"
          bean="${assetPickerFormHandlerPath}.clearSearchResults"
          id="clearSearchResults" name="clearSearchResults" 
          value="true" />
        <dspel:input type="hidden" priority="10"
          bean="${assetPickerFormHandlerPath}.assetInfoPath"
          value="${assetInfoPath}"
          id="assetInfoPath" name="assetInfoPath" />
        <dspel:input type="hidden" priority="-10" value="1"
          bean="${assetPickerFormHandlerPath}.configureAssetInfo"  />
      </dspel:form>

    </div>
  </div> <!-- mainContent -->


  <script language="JavaScript" type="text/javascript">

    //
    // Search existing assets button
    //
    function submitSearchAssets() {
      document.selectionForm.componentPath.value = document.selectionForm.selectedRepository.value;
      document.selectionForm.itemType.value = document.selectionForm.selectedItemType.value;

      document.selectionForm.submit();

    }

    loadAssetPicker();

  </script>

</c:catch>

<%
  Throwable tt = (Throwable) pageContext.getAttribute("ex");
  if ( tt != null ) {
    System.out.println("Caught exception in itemTypeSelection.jsp:");
    tt.printStackTrace();
  }
%>

</dspel:page>

<!-- End itemTypeSelection.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/AssetBrowse.war/assetBrowseItemTypeSelection.jsp#2 $$Change: 651448 $--%>
