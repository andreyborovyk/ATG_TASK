<%--

  Default editor for availableRankingProperty item.

  The following request-scoped variables are expected to be set:

    @param  view          A request scoped, MappedItem (itemMapping) item for this view
    @param  imap          A request scoped, MappedItemView (itemViewMapping)
    @param  formHandler   The form handler

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/availableRankingPropertyEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/search/web/assetmanager/Configuration"/>

  <dspel:importbean var="configInfo" bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <fmt:setBundle var="bundle" basename="${config.resourceBundle}"/>

  <%-- Get the form handler from the requestScope --%>
  <c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>
  <c:set var="formHandler"     value="${requestScope.formHandler}"/>
  <dspel:getvalueof bean="${formHandlerPath}.repositoryItem.transient" var="transient" />

  <c:choose>
    <c:when test="${view.itemMapping.mode == 'AssetManager.view'}">
      <c:set var="disabled" value="disabled"/>
    </c:when>
    <c:otherwise>
      <c:set var="disabled" value=""/>
    </c:otherwise>
  </c:choose>

  <script type="text/javascript">
    registerOnSubmit(function() {

      var propType = document.getElementById("propertyType").value;

      if( (propType == 'date') || (propType == 'float') || (propType == 'integer') ) {
        document.getElementById("typeDsp").value = "range";
        return;
      }

      document.getElementById("typeDsp").value = "element";
    })
  </script>

  <br />
    
  <table class="formTable">
    <tr>
      <td colspan="2">
        <c:choose>
          <c:when test="${transient}">
            <fmt:message bundle="${bundle}" key="helpText.availableRankingProperty" />
          </c:when>
          <c:otherwise>
            <fmt:message bundle="${bundle}" key="helpText.existingAvailableRankingProperty" />
          </c:otherwise>
        </c:choose>
      </td>
    </tr>

    <dspel:include otherContext="AssetUI-Search" page="/assetEditor/property/indexedPropertySelector.jsp">
      <dspel:param name="formNameProperty" value="propertyName"/>
      <dspel:param name="formTypeProperty" value="dataType"/>
      <dspel:param name="resourceBundle"   value="atg.search.web.assetmanager.WebAppResources"/>
      <dspel:param name="nameResourceKey"  value="availableRankingPropertyEditor.name"/>
      <dspel:param name="typeResourceKey"  value="availableRankingPropertyEditor.dataType"/>
      <%-- don't allow editing propertyName on existing assets --%> 
      <dspel:param name="readonlyPropName"  value="${not transient}"/>
    </dspel:include>

    <tr style="display:none">

      <dspel:input type="hidden"
                       id="typeDsp" 
                       bean="${formHandlerPath}.value.valueType"/>

      <td class="formLabel">
        <fmt:message bundle="${bundle}" key="availableRankingPropertyEditor.valueType" />
      </td>

      <dspel:getvalueof bean="${formHandlerPath}.value.valueType" var="propVal" />
      <dspel:getvalueof bean="${formHandlerPath}.value.dataType" var="propType" />

      <c:set var="disableValueType" value="${disabled}" scope="request"/>

      <td>
        <c:choose>
          <%-- Don't allow changing valueType for existing assets.  Only for transient assets that 
               are numeric or date --%>
          <c:when test="${not transient}">
            <dspel:getvalueof bean="${formHandlerPath}.value.valueType" var="valueType" />
            <c:choose>
              <c:when test="${valueType eq 'range'}">
                <fmt:message key="availableRankingPropertyEditor.allowRanges" bundle="${bundle}"/>
              </c:when>
              <c:otherwise>
                <fmt:message key="availableRankingPropertyEditor.noRanges" bundle="${bundle}"/>
              </c:otherwise>
            </c:choose> 
          </c:when>
          <c:when test="${(propType ne 'integer') and (propType ne 'float') and (propType ne 'date')}">
            <c:set var="disableValueType" value="true" scope="request"/>

            <dspel:setvalue bean="${formHandlerPath}.value.valueType" value="element"/>

            <dspel:select id="valueType" disabled="${disableValueType}"
                          bean="${formHandlerPath}.value.valueType"
                          onchange="markAssetModified()">
              <dspel:option value="element">
                <fmt:message key="availableRankingPropertyEditor.noRanges" bundle="${bundle}"/>
              </dspel:option>
            </dspel:select>
          </c:when>
          <c:otherwise>
            <dspel:select id="valueType"
                          bean="${formHandlerPath}.value.valueType"
                          onchange="markAssetModified()">
              <dspel:option value="element" selected="false">
                <fmt:message key="availableRankingPropertyEditor.noRanges" bundle="${bundle}"/>
              </dspel:option>

              <dspel:option value="range" selected="true">
                <fmt:message key="availableRankingPropertyEditor.allowRanges" bundle="${bundle}"/>
              </dspel:option>

            </dspel:select>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>

  <!-- Set application name from session info -->
  <dspel:setvalue bean="${formHandlerPath}.value.application" value="${configInfo.sessionInfo.currentApplicationName}"/>
  
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/availableRankingPropertyEditor.jsp#2 $$Change: 651448 $--%>
