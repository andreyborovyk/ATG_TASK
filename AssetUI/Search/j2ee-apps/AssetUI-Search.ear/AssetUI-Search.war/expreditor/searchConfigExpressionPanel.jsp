<%--
  Page fragment that displays a table of expression editors.

  @param  model       An ExpressionModel component containing the expression to
                      be edited.
  @param  container   The ID of the page element that contains this fragment.
                      This allows the element to be reloaded when the user
                      interacts with the controls in this editor.
  @param  mode        Showing mode 

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/expreditor/searchConfigExpressionPanel.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt" %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:getvalueof var="paramModelPath" param="model"/>
  <dspel:getvalueof var="paramContainer" param="container"/>
  <dspel:getvalueof var="paramMode" param="mode"/>

  <c:set scope="request" var="searchConfigExpressionPanelContextRoot" value="${config.contextRoot}"/>
  <c:set scope="request" var="searchConfigExpressionPanelUrl" value="/expreditor/searchConfigExpressionPanel.jsp"/>

  <dspel:importbean var="model" bean="${paramModelPath}"/>

  <c:if test="${paramMode == 'main' && requestScope.view.itemMapping.mode == 'AssetManager.edit'}">
    <script type="text/javascript">
      function addNewRule() {
        var addRule = document.getElementById("addNewRule");
        addRule.click();
      }

      //----------------------------------------------------------------------------//
      /**
       * Handle a key press event in a row-positioning field.
       * 
       * @param pEvent  The event (if W3C DOM is supported; use window.event otherwise)
       */
      function searchConfig_onPositionFieldKeyPress(pEvent) {

        // Determine which key was pressed.
        if (pEvent == null)
          pEvent = event;
        var charCode = pEvent.charCode;
        if (charCode == null || charCode == 0)
          charCode = pEvent.keyCode;
        if (charCode == null)
          charCode = pEvent.which;

        // Don't allow keys that represent text other than digits.
        if (!pEvent.ctrlKey && !pEvent.altKey && charCode > 31 && (charCode < 48 || charCode > 57)) {
          return false;
        }

        // If the Enter key was pressed, call the onchange function.
        // Always return false, because the Enter key submits the form on Firefox.
        if (charCode == 13 || charCode == 3) {
          return false;
        }

        markAssetModified();
        return true;
      }

      //----------------------------------------------------------------------------//
      /**
       * Handle a change event in a row-positioning field.
       */
      function searchConfig_onPositionFieldChange(pEvent) {

        // Find the event target.
        if (pEvent == null)
          pEvent = event;
        var target = null;
        if (pEvent.srcElement)
          target = pEvent.srcElement;
        else if (pEvent.target)
          target = pEvent.target;

        // Display an error dialog and reject the input if it is not valid.
        var value = parseInt(target.value);
        if (isNaN(value)) {
          alert("Invalid location value: " + target.value);
          target.value = target.defaultValue;
          return false;
        }

        // If the value is OK, save it.
        markAssetModified();
        return true;
      }
    </script>

    <dspel:input id="currExpressionService" type="hidden" bean="${requestScope.formHandlerPath}.currExpressionService" value="${paramModelPath}" />
    <dspel:input id="currIndex" type="hidden" bean="${requestScope.formHandlerPath}.currIndex" />

    <div class="atg_smerch_configControl">
      <fmt:message key="searchConfigHeaderRender.addNewRule" var="addNewRuleText"/>

      <dspel:input id="addNewRule" type="submit" bean="${requestScope.formHandlerPath}.addNewRule" value="${addNewRuleText}" style="display: none" onclick="markAssetModified()" />

      <a href="javascript:addNewRule()"
         class="buttonSmall"
         title="<fmt:message key='searchConfigHeaderRender.addNewRule'/>">
        <span><fmt:message key="searchConfigHeaderRender.addNewRule"/></span>
      </a>

      <dspel:input id="removeRule" type="submit" bean="${requestScope.formHandlerPath}.removeRule" value="" style="display: none" onclick="markAssetModified()" />
    </div>
  </c:if>
    
  <c:if test="${!empty model.ruleList && !(model.ruleList[0].blank && requestScope.view.itemMapping.mode != 'AssetManager.edit')}">
  
    <dspel:include page="searchConfigHeaderRender.jsp">
      <dspel:param name="model" value="${paramModelPath}"/>
      <dspel:param name="container" value="${paramContainer}"/>
      <dspel:param name="mode" value="${paramMode}"/>
    </dspel:include>          

    <c:forEach var="rule" items="${model.ruleList}" varStatus="loop">
      <c:if test="${!rule.blank || requestScope.view.itemMapping.mode == 'AssetManager.edit'}">
        <dspel:include page="searchConfigRuleRender.jsp">
          <dspel:param name="model" value="${paramModelPath}"/>
          <dspel:param name="searchRule" value="${rule}"/>
          <dspel:param name="container" value="${paramContainer}"/>
          <dspel:param name="mode" value="${paramMode}"/>
          <dspel:param name="index" value="${loop.index}"/>
        </dspel:include>          
      </c:if>
    </c:forEach>

    <dspel:include page="searchConfigFooterRender.jsp">
      <dspel:param name="model" value="${paramModelPath}"/>
      <dspel:param name="container" value="${paramContainer}"/>
      <dspel:param name="mode" value="${paramMode}"/>
    </dspel:include>          

  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/expreditor/searchConfigExpressionPanel.jsp#2 $$Change: 651448 $--%>
