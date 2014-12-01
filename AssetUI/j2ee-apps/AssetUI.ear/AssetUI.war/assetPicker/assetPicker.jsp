<%--
  Asset Picker initial page

  @param apView
    Determine's which page to include in the Asset Picker iframe.
    Will either be repository/asset type selection page, or the
    plugin container page.

  @param assetPickerTitle
    Optional parameter passed in via invoking Javascript to set
    the title of the Asset Picker

  @param assetPickerHeader
    Optional parameter passed in via invoking Javascript to set
    the header of the Asset Picker

  @param assetInfo
    Client path to nucleus AssetInfo context object.
    Source: client/container

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/assetPicker.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>
<%@ taglib prefix="web-ui"  uri="http://www.atg.com/taglibs/web-ui" %>

<!-- Begin assetPicker.jsp -->

<dspel:page>

<dspel:importbean var="config"      bean="/atg/assetui/Configuration"/>
<dspel:importbean var="bizuiConfig" bean="/atg/bizui/Configuration"/>
<dspel:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dspel:importbean bean="/atg/dynamo/droplet/Switch"/>
<dspel:importbean bean="/atg/epub/servlet/RepositoryAssetFormHandler"/>

<fmt:setBundle var="assetuiBundle" basename="${config.resourceBundle}"/>

<%-- get ProcessAssetInfo --%>
<c:choose>
  <c:when test="${ ! empty param.assetInfo }">
    <c:set var="assetInfoPath" value="${param.assetInfo}"/>
  </c:when>
  <c:otherwise>
    <c:set var="assetInfoPath"
      value="/atg/epub/servlet/ProcessAssetInfo"/>
  </c:otherwise>
</c:choose>
<dspel:importbean bean="${assetInfoPath}" var="assetInfo"/>

<c:choose>
  <c:when test="${ ! empty assetInfo.attributes.styleSheet}">
    <c:set var="styleSheet" value="${assetInfo.attributes.styleSheet}"/>
  </c:when>
  <c:otherwise>
    <c:set var="styleSheet" value="${config.contextRoot}${config.styleSheet}"/>
  </c:otherwise>
</c:choose>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<head>
  <title>Asset Picker</title>
  <dspel:link href="${styleSheet}" rel="stylesheet" type="text/css"/>

  <%-- include the head file if specified so the AssetPicker can use the custom head file --%>
  <c:if test="${! empty assetInfo.attributes.headIncludeFile}">
    <c:choose>
      <c:when test="${!empty assetInfo.attributes.headIncludeContextRoot}">
        <dspel:include otherContext="${assetInfo.attributes.headIncludeContextRoot}" page="${assetInfo.attributes.headIncludeFile}"/>
      </c:when>
      <c:otherwise>
        <dspel:include page="${assetInfo.attributes.headIncludeFile}"/>
      </c:otherwise>
    </c:choose>
  </c:if>

  <script language="JavaScript" type="text/javascript" src="<c:out value='${config.contextRoot}'/>/scripts/scripts.js"></script>
  <script language="JavaScript" type="text/javascript" src="<c:url context='${bizuiConfig.contextRoot}' value='/templates/page/html/scripts/scripts.js'/>"></script>

</head>

<c:catch var="ex">

  <%@ include file="assetPickerConstants.jsp" %>

  <%-- assetPickerTitle --%>
  <c:if test="${ ! empty param.assetPickerTitle }">
    <c:set target="${assetInfo.attributes}"
      property="assetPickerTitle" value="${param.assetPickerTitle}"/>
  </c:if>
  <web-ui:decodeParameterValue var="assetPickerTitle" value="${assetInfo.attributes.assetPickerTitle}"/>

  <%-- assetPickerHeader --%>
  <c:if test="${ ! empty param.assetPickerHeader }">
    <c:set target="${assetInfo.attributes}"
      property="assetPickerHeader" value="${param.assetPickerHeader}"/>
  </c:if>
  <web-ui:decodeParameterValue var="assetPickerHeader" value="${assetInfo.attributes.assetPickerHeader}"/>

  <script language="Javascript">
    // Get the current AssetBrowser object
    var picker = parent.currentPicker;

    // Sets the lists of assetTypes and viewConfiguration for use
    // by the picker
    var assetTypes;
    var viewConfiguration;
    if (picker != undefined) {
      assetTypes        = picker.assetTypes;
      viewConfiguration = picker.viewConfiguration;
    }

    //
    // Returns a JavaScript object that contains configuration
    // options available to plugins
    //
    function getViewConfiguration() {
      return viewConfiguration;
    }


    //
    // Returns the viewConfiguration as a string in the format:
    // key1::value1||key2::value2
    //
    function getViewConfigurationAsString() {
      if ( viewConfiguration == null ) {
        return null;
      }
      else {
        var strViewConfig = new String();

        for ( keys in viewConfiguration ) {
          strViewConfig += keys +
                           "::" +
                           viewConfiguration[keys] +
                           "||";
        }
        return strViewConfig;
      }
    }

    //
    // Returns a JavaScript array of assetTypes available to
    // plugins
    //
    function getAssetTypes() {
      if (picker != undefined)
        return assetTypes;
    }

    //
    // Helper method to get item display property
    //
    function getItemDisplayProperty( repository, itemType ) {
      var itemDisplayProperty = null;

      for (var i=0; i<assetTypes.length; i++) {
        if ( assetTypes[i].componentPath == repository &&
             assetTypes[i].typeName == itemType )

          itemDisplayProperty = assetTypes[i].displayNameProperty ;
      }
      return itemDisplayProperty;
    }

    //
    // Helper method to display div element
    //
    function show( elementID ) {
      document.getElementById( elementID ).style.display = "block";
    }

    //
    // Helper method to hide div element
    //
    function hide( elementID ) {
      document.getElementById( elementID ).style.display = "none";
    }

    //
    // Close Asset Picker Window
    //
    function submitClose() {
      hide("includedContainer");
      if ( "hide" == '<c:out value="${assetInfo.attributes.closeAction}"/>' ) {
        parent.hideIframe( "browser" );
        if ( picker.onHide )
          picker.assetPickerParentWindow[ picker.onHide ]('<c:out value="${assetInfo.context.attributes.assetPickerOnHideData}"/>');
      }
      else
        refreshParentWindow();

    }
  </script>

  <c:set var="apView" value="${ param.apView }"/>

  <body id="assetBrowser" onload="loadAssetPicker();">

    <div id="assetBrowserClose">
      <a href="javascript:submitClose();" onclick="parent.showIframe('browser');" title="<fmt:message key='close-picker-button' bundle='${assetuiBundle}' />" class="closeButton"></a>
    </div>

    <c:choose>
      <c:when test="${ ! empty assetPickerTitle }">
        <h1><c:out value="${ assetPickerTitle }"/></h1>
      </c:when>
      <c:otherwise>
        <h1><fmt:message key="default-title" bundle="${assetuiBundle}" /></h1>
      </c:otherwise>
    </c:choose>

    <%--
      Show header
      split the header into explanatory text (under colon) and item
    --%>
    <c:if test="${!empty assetPickerHeader}">
      <c:forTokens items="${assetPickerHeader}" delims=":" var="item" varStatus="status">
        <c:choose>
          <c:when test="${status.first}">
            <c:set var="explanatory" value="${item}" />
          </c:when>
          <c:otherwise>
            <c:set var="it" value="${item}" />
          </c:otherwise>
        </c:choose>
      </c:forTokens>

      <c:choose>
        <c:when test="${not empty it}">
          <div id="assetBrowserHeader"><c:out value="${explanatory}"/>: <span id="assetBrowserHeaderItem"><c:out value="${it}"/></span></div>
        </c:when>
        <c:otherwise>
          <div id="assetBrowserHeader"><c:out value="${explanatory}"/></div>
        </c:otherwise>
      </c:choose>
    </c:if>
    <%-- end Show header --%>

    <dspel:droplet name="Switch">
      <dspel:param bean="RepositoryAssetFormHandler.formError" name="value"/>
      <dspel:oparam name="true">
        <ul>
        <dspel:droplet name="ErrorMessageForEach">
          <dspel:param bean="RepositoryAssetFormHandler.formExceptions" name="exceptions"/>
          <dspel:oparam name="output">
            <li><div class="error"><dspel:valueof param="message"/></div>
          </dspel:oparam>
        </dspel:droplet>
        </ul>
      </dspel:oparam>
    </dspel:droplet>

    <div id="includedContainer">
      <c:choose>
        <c:when test="${ apView eq AP_ITEM_TYPE_SELECT }">
          <dspel:include page="${ AP_ITEM_TYPE_SELECT_PAGE }"/>
        </c:when>
        <c:when test="${ apView eq AP_CONTAINER }">
          <dspel:include page="${ AP_CONTAINER_PAGE }"/>
        </c:when>
        <c:otherwise>
          <dspel:include page="${ AP_CONTAINER_PAGE }"/>
        </c:otherwise>
      </c:choose>
    </div>

    <div id="createForms" style="display: none;">
      <%@ include file="createAsset.jsp" %>
    </div>

  </body>

</c:catch>
</html>

<%
  Throwable tt = (Throwable) pageContext.getAttribute("ex");
  if ( tt != null ) {
    System.out.println("Caught exception in assetPicker.jsp:");
    tt.printStackTrace();
  }
%>

</dspel:page>

<!-- End assetPicker.jsp -->
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/assetPicker/assetPicker.jsp#2 $$Change: 651448 $--%>
