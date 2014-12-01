<%--
  Fragment that displays a row of tabs.

  The tabs are generated using a given list of tab IDs.  The display name
  for each tab is obtained from the tab configuration.
  Each tab is a hyperlink to the same given URL, but it also assigns the
  associated tab ID as the value of a given request parameter.

  @param  editable           Whether or not to render the tabs as readonly or readwrite
  @param  tabIds             The list of tab IDs, in display order.
  @param  resourcePath       Path of resource bundle for generating tab strings.
  @param  selectedTab        The name of the selected tab.  If null, then the
                             first tab is selected.
  @param  destinationPage    Target page for all of the tab buttons.
  @param  tabParameter       The name of a request parameter to be passed to the
                             destination page.  The value of this parameter will
                             be the name of the tab selected by the user.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/tabBanner.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"   uri="http://www.atg.com/taglibs/pws"                   %>

<dspel:page>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramDestinationPage"   param="destinationPage"/>
  <dspel:getvalueof var="paramSelectedTab"       param="selectedTab"/>
  <dspel:getvalueof var="paramTabIds"            param="tabIds"/>
  <dspel:getvalueof var="paramTabParameter"      param="tabParameter"/>
  <dspel:getvalueof var="paramJavascriptWrapper" param="javascriptWrapper"/>
  <dspel:getvalueof var="paramEditable"          param="editable"/>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

   <script type="text/javascript" charset="utf-8">

     dojo.require("dijit._Widget");
     dojo.require("dijit._Templated");
     dojo.require("dojo.parser");	// scan page for widgets and instantiate them

     // Register the Embedded Assistance widget namespace
     dojo.registerModulePath("atg.widget", "/WebUI/dijit");
     //dojo.require("atg.widget.progress.clockwise");
     </script>
    <script type="text/javascript" charset="utf-8" src="/WebUI/dijit/progress/clockwise.js"></script>
    <link rel="stylesheet" href="/WebUI/dijit/progress/templates/clock.css" type="text/css" media="screen" title="no title" charset="utf-8">
  <ul>

    <%-- Loop through each of the given tab IDs --%>
    <c:forEach var="tabId" items="${paramTabIds}">
      <c:set var="tabBannerConfig" value="${requestScope.managerConfig.tabs[tabId]}"/>

      <%-- Ensure that the selectedTab is specified --%>
      <c:if test="${empty paramSelectedTab}">
        <c:set var="paramSelectedTab" value="${tabId}"/>
      </c:if>

      <%-- Get the tab label string --%>
      <fmt:setBundle basename="${tabBannerConfig.resourceBundle}"/>
      <fmt:message var="tabLabel" key="${tabBannerConfig.displayNameResource}"/>
  
      <%-- Derive the destination URL for the tab --%>
      <c:url var="tabHref" value="${paramDestinationPage}">
        <c:param name="${paramTabParameter}" value="${tabId}"/>
      </c:url>

      <%-- If the tab should call javascript, call the method --%>
      <c:if test="${not empty paramJavascriptWrapper}">
        <c:set var="tabHref"
          value="javascript:${paramJavascriptWrapper}('${tabHref}')"/>
      </c:if>

      <%-- if we aren't editable, then don't show the multiedit tab --%>
      <c:if test="${tabBannerConfig.displayNameResource ne 'assetManager.tab.multiEdit' or paramEditable}">

        <c:choose>

          <%-- Add a selected tab --%>
          <c:when test="${paramSelectedTab eq tabId}">
            <c:set var="currentClass" value="current"/>
          </c:when>
          <%-- Add an unselected tab --%>
          <c:otherwise>
            <c:set var="currentClass" value=""/>
          </c:otherwise>

        </c:choose>


        <li class="<c:out value='${currentClass}'/>" >
        <a href="<c:out value='${tabHref}'/>">
            <c:out value="${tabLabel}"/>
         <c:if test="${tabBannerConfig.displayNameResource == 'assetManager.tab.multiEdit'}">
           <dspel:getvalueof var="isFinishedWaiting" bean="/atg/web/assetmanager/multiedit/MultiEditSessionInfo.multiEditRunnable.multiEditFinished"/>
           <dspel:getvalueof var="percentFinished" bean="/atg/web/assetmanager/multiedit/MultiEditSessionInfo.multiEditRunnable.multiEditPercentFinished"/>
          <c:set var="multiEditCount" value="${config.sessionInfo.multiEditSessionInfo.totalAssetCountEstimate}"/>
          <fmt:message var="countLabel" key="assetManager.tab.count">
           <fmt:param value="${multiEditCount}"/>
          </fmt:message> 

          <c:choose>
            <c:when test="${isFinishedWaiting == 'false'}">
              <c:set var="countstyle" value="none"/>
              <c:set var="clockstyle" value=""/>
            </c:when>
            <c:otherwise>
              <c:set var="countstyle" value=""/>
              <c:set var="clockstyle" value="none"/>
              <c:set var="percentFinished" value="0"/>
            </c:otherwise>
          </c:choose>
           <span id="multiEditWaitClock" style="display:<c:out value='${clockstyle}'/>" >
              <span id="progress2" topicDriven="true" forwardOnly="true" dojoType="atg.widget.progressClock" percentProgress="<c:out value='${percentFinished}'/>" ></span>
           </span>
           <span id="multiEditWaitCount" style="display:<c:out value='${countstyle}'/>" >
              <c:out value="${countLabel}"/>
           </span>
         </c:if>
        </a>
       </li>

      </c:if> <%-- not multiedit or editable --%>
    </c:forEach>
  </ul>



<script type="text/javascript">
      
<c:if test="${paramEditable}">

  // hide/show the tab clock
  function updateMultiEditTabClock(pValue) {
    if (pValue == '100') {
      updateMultiEditTabCount("<c:out value='${multiEditCount}'/>");
      return;
    }

    var countLabelSpan = document.getElementById("multiEditWaitCount");
    var clockLabelSpan = document.getElementById("multiEditWaitClock");

    if (countLabelSpan) countLabelSpan.style.display = "none";
    if (clockLabelSpan) clockLabelSpan.style.display = "";

  }


  // hide/show and update the tab count
  function updateMultiEditTabCount(pValue) {
    var commaNumber = addCommas(pValue);
    var countLabelSpan = document.getElementById("multiEditWaitCount");
    var clockLabelSpan = document.getElementById("multiEditWaitClock");

    if (clockLabelSpan) clockLabelSpan.style.display = "none";

    if (countLabelSpan) {
      countLabelSpan.style.display = "";
      countLabelSpan.innerHTML = "(" + commaNumber + ")";
    }
  }


</c:if>
</script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/tabBanner.jsp#2 $$Change: 651448 $--%>
