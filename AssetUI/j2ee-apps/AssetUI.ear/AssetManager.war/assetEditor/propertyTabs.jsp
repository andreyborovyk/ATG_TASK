<%--
  The property tabs.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/propertyTabs.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <c:set var="sessionInfoPath" value="/atg/web/assetmanager/SessionInfo"/>
  <dspel:importbean var="sessionInfo" bean="${sessionInfoPath}"/>

  <%-- unpack the param(s) --%>
  <dspel:getvalueof var="resetViewNumber" param="resetViewNumber"/>
  <dspel:getvalueof var="imap" param="imap"/>
  <dspel:getvalueof var="overrideViewNumber" param="overrideViewNumber"/>


  <%-- Get the assetEditor for the current left tab --%>
  <c:set var="currentView" value="${sessionInfo.assetEditorViewID}"/>
  <c:if test="${not empty currentView}">
    <c:set var="assetEditorPath" value="${sessionInfo.assetEditors[currentView]}"/>
    <dspel:importbean var="assetEditor" bean="${assetEditorPath}"/>
  </c:if>


  <c:set var="debug" value="false"/>

  <script type="text/javascript">
    function submitViewChange(viewNumber) {
      if (assetEditorLoaded) {

        var form = getForm();
        form.elements["currentViewInput"].value = viewNumber;

        var button = document.getElementById("tabChangeButton");
        if (button) {
          parent.messages.hide();
          fireOnSubmit();
          button.click();
        }
        else {
          submitForm();
        }
      }
    }
  </script>

  <%-- 
    Set 'currentViewNumber' (current Property Tab index)
 
    The current tab for each item descriptor is saved in the sessionInfo 
   --%>

  <c:choose>

    <c:when test="${not empty resetViewNumber}">
      <c:set var="currentViewNumber" value="0"/> 
    </c:when>

    <c:when test="${not empty overrideViewNumber}">
      <c:set var="currentViewNumber" value="${overrideViewNumber}"/> 
    </c:when>

    <c:when test="${not empty sessionInfo.propertyTabs[imap.itemName]}">
      <c:set var="currentViewNumber" value="${sessionInfo.propertyTabs[imap.itemName]}"/>
    </c:when>

    <c:otherwise>
      <c:set var="currentViewNumber" value="0"/> 
    </c:otherwise>
            
  </c:choose>

  <%-- Update the session's view number --%>
  <dspel:input id="currentViewInput" type="hidden" value="${currentViewNumber}" 
                bean="${sessionInfoPath}.propertyTabs.${imap.itemName}"/>
  <c:set target="${sessionInfo.propertyTabs}" property="${imap.itemName}" value="${currentViewNumber}"/>

  <c:if test="${debug}">
              [editAsset] Current View: <c:out value="${currentViewNumber}"/>
  </c:if>
     
  <%-- Update the asset context's view number  --%>
  <c:set target="${assetEditor.assetContext}" property="currentViewNumber" value="${currentViewNumber}"/>

  <%-- 
         Render Property Tabs
   --%>     
  <dspel:test var="tabsTestResult" value="${imap.viewMappings}"/>
  <c:set var="numTabs" value="${tabsTestResult.size}"/>

  <c:if test="${numTabs > 1}">
  <!-- Sub Nav : Includes right pane sub-navigation tabs -->
    <ul>
      <c:forEach items="${imap.viewMappings}" var="aView" varStatus="vstat">

        <c:set var="bundleName" value="${aView.attributes.resourceBundle}"/>
        <c:choose>
          <c:when test="${not empty bundleName}">
            <fmt:setBundle var="resBundle" basename="${bundleName}"/>
            <fmt:message var="tabDisplayName" key="${aView.displayName}" bundle="${resBundle}"/>
          </c:when>
          <c:otherwise>
            <c:set var="tabDisplayName" value="${aView.displayName}"/>
          </c:otherwise>
        </c:choose>

         <c:if test="${vstat.first}">
          <c:set var="view" value="${aView}" scope="request"/>
        </c:if>
                 
        <c:choose>
          <c:when test="${ vstat.index == currentViewNumber }">
            <c:set var="view" value="${aView}" scope="request"/>
            <li class="current" onclick='submitViewChange("<c:out value="${vstat.index}"/>");'><a href='#'>
              <c:out value="${tabDisplayName}"/>
            </a></li>
          </c:when>

          <c:otherwise>
            <li onclick='submitViewChange("<c:out value="${vstat.index}"/>");'><a href='#'>
              <c:out value="${tabDisplayName}"/>
            </a></li>
           </c:otherwise>
                    
         </c:choose>
                  
       </c:forEach>
                
     </ul>

  </c:if>

  <c:if test="${numTabs == 1}">
    <c:set var="view" value="${imap.viewMappings[0]}" scope="request"/>
  </c:if>


</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/propertyTabs.jsp#2 $$Change: 651448 $--%>
