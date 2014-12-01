<%--
  Viewer for the suggested merge of 2 conflicting list or array properties.

  @param  mpv  A request scoped, MappedPropertyView item for this view

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"    uri="http://www.atg.com/taglibs/web-ui"            %>

<dspel:page>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramFormHandler" param="formHandler"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <fmt:message key="commonDiff.suggestion.addedIndicator" var="addedIndicator"/>
  <fmt:message key="commonDiff.suggestion.reorderedIndicator" var="reorderedIndicator"/>

  <c:set var="propertyName" value="${propertyView.propertyName}"/>
  <c:set var="isList" value="${propertyView.propertyDescriptor.typeName == 'list'}"/>

  <%-- Use a configurable setting for the maximum number of entries to display.
       This attribute could be set on the property view itself or its parent
       view. --%>
  <c:set var="maxNumDisplayedValues" value="2300"/>
  <c:if test="${not empty propertyView.view.attributes.maxNumDisplayedValues}">
    <c:set var="maxNumDisplayedValues" value="${propertyView.view.attributes.maxNumDisplayedValues}"/>
  </c:if>
  <c:if test="${not empty propertyView.attributes.maxNumDisplayedValues}">
    <c:set var="maxNumDisplayedValues" value="${propertyView.attributes.maxNumDisplayedValues}"/>
  </c:if>

  <%-- Display the suggestion broken out to reordered, added, unchanged, and
       removed items.  --%>
  <c:set var="reorderedItems"
         value="${paramFormHandler.reorderedItemsInSuggestion[propertyName]}"/>
  <c:set var="addedItems"
         value="${paramFormHandler.addedItemsInSuggestion[propertyName]}"/>
  <c:set var="unchangedItems"
         value="${paramFormHandler.unchangedItemsInSuggestion[propertyName]}"/>
  <c:set var="removedItems"
         value="${paramFormHandler.removedItemsInSuggestion[propertyName]}"/>

  <c:choose>
    <c:when test="${isList}">
      <web-ui:collectionPropertySize var="numReordered" collection="${reorderedItems}"/>
      <web-ui:collectionPropertySize var="numAdded" collection="${addedItems}"/>
      <web-ui:collectionPropertySize var="numUnchanged" collection="${unchangedItems}"/>
      <web-ui:collectionPropertySize var="numRemoved" collection="${removedItems}"/>
    </c:when>
    <c:otherwise>
      <web-ui:collectionPropertySize var="numReordered" array="${reorderedItems}"/>
      <web-ui:collectionPropertySize var="numAdded" array="${addedItems}"/>
      <web-ui:collectionPropertySize var="numUnchanged" array="${unchangedItems}"/>
      <web-ui:collectionPropertySize var="numRemoved" array="${removedItems}"/>
    </c:otherwise>
  </c:choose>


  <%-- Display reordered items up to the max number allowed.  --%>
  <c:set var="displayIndexOffset" value="1"/>
  <c:choose>
    <c:when test="${numReordered > maxNumDisplayedValues}">
      <c:set var="lastIndex" value="${maxNumDisplayedValues - 1}"/>
      <c:set var="numDisplayedValues" value="${maxNumDisplayedValues}"/>
    </c:when>
    <c:otherwise>
      <c:set var="lastIndex" value="${numReordered - 1}"/>
      <c:set var="numDisplayedValues" value="${numReordered}"/>
    </c:otherwise>
  </c:choose>
  <c:if test="${lastIndex >= 0}">
    <c:forEach var="reorderedItem" items="${reorderedItems}" begin="0" end="${lastIndex}" varStatus="reorderLoop">
      <c:set scope="request" var="componentValue" value="${reorderedItem}"/>
      <c:out value="${reorderedIndicator}"/>
      <c:out value="${displayIndexOffset + reorderLoop.index}"/>.&nbsp;
      <dspel:include otherContext="${propertyView.componentApplication}" page="${propertyView.componentUri}"/>
      <br/>
    </c:forEach>
    <c:set var="displayIndexOffset" value="${displayIndexOffset + lastIndex + 1}"/>
  </c:if>

  <%-- Display added items up to the max number allowed.  --%>
  <c:choose>
    <c:when test="${numDisplayedValues eq maxNumDisplayedValues}">
      <c:set var="lastIndex" value="-1"/>
    </c:when>
    <c:when test="${(numAdded + numDisplayedValues) > maxNumDisplayedValues}">
      <c:set var="lastIndex" value="${(numAdded - (numAdded + numDisplayedValues - maxNumDisplayedValues)) - 1}"/>
      <c:set var="numDisplayedValues" value="${maxNumDisplayedValues}"/>
    </c:when>
    <c:otherwise>
      <c:set var="lastIndex" value="${numAdded - 1}"/>
      <c:set var="numDisplayedValues" value="${numAdded + numDisplayedValues}"/>
    </c:otherwise>
  </c:choose>
  <c:if test="${lastIndex >= 0}">
    <c:forEach var="addedItem" items="${addedItems}" begin="0" end="${lastIndex}" varStatus="addedLoop">
      <c:set scope="request" var="componentValue" value="${addedItem}"/>
      <c:out value="${addedIndicator}"/>
      <c:out value="${displayIndexOffset + addedLoop.index}"/>.&nbsp;
      <dspel:include otherContext="${propertyView.componentApplication}" page="${propertyView.componentUri}"/>
      <br/>
    </c:forEach>
    <c:set var="displayIndexOffset" value="${displayIndexOffset + lastIndex + 1}"/>
  </c:if>

  <%-- Display unchanged items up to the max number allowed.  --%>
  <c:choose>
    <c:when test="${numDisplayedValues eq maxNumDisplayedValues}">
      <c:set var="lastIndex" value="-1"/>
    </c:when>
    <c:when test="${(numUnchanged + numDisplayedValues) > maxNumDisplayedValues}">
      <c:set var="lastIndex" value="${(numUnchanged - (numUnchanged + numDisplayedValues - maxNumDisplayedValues)) - 1}"/>
      <c:set var="numDisplayedValues" value="${maxNumDisplayedValues}"/>
    </c:when>
    <c:otherwise>
      <c:set var="lastIndex" value="${numUnchanged - 1}"/>
      <c:set var="numDisplayedValues" value="${numUnchanged + numDisplayedValues}"/>
    </c:otherwise>
  </c:choose>
  <c:if test="${lastIndex >= 0}">
    <c:forEach var="unchangedItem" items="${unchangedItems}" begin="0" end="${lastIndex}" varStatus="unchangedLoop">
      <c:set scope="request" var="componentValue" value="${unchangedItem}"/>
      <c:out value="${displayIndexOffset + unchangedLoop.index}"/>.&nbsp;
      <dspel:include otherContext="${propertyView.componentApplication}" page="${propertyView.componentUri}"/>
      <br/>
    </c:forEach>
    <c:set var="displayIndexOffset" value="${displayIndexOffset + lastIndex + 1}"/>
  </c:if>

  <%-- Display removed items up to the max number allowed.  --%>
  <c:choose>
    <c:when test="${numDisplayedValues eq maxNumDisplayedValues}">
      <c:set var="lastIndex" value="-1"/>
    </c:when>
    <c:when test="${(numRemoved + numDisplayedValues) > maxNumDisplayedValues}">
      <c:set var="lastIndex" value="${(numRemoved - (numRemoved + numDisplayedValues - maxNumDisplayedValues)) - 1}"/>
      <c:set var="numDisplayedValues" value="${maxNumDisplayedValues}"/>
    </c:when>
    <c:otherwise>
      <c:set var="lastIndex" value="${numRemoved - 1}"/>
      <c:set var="numDisplayedValues" value="${numRemoved + numDisplayedValues}"/>
    </c:otherwise>
  </c:choose>
  <c:if test="${lastIndex >= 0}">
    <c:forEach var="removedItem" items="${removedItems}" begin="0" end="${lastIndex}">
      <c:set scope="request" var="componentValue" value="${removedItem}"/>
      <del>
        <dspel:include otherContext="${propertyView.componentApplication}" page="${propertyView.componentUri}"/>
      </del>
      <br/>
    </c:forEach>
  </c:if>

  <c:if test="${(numReordered + numAdded + numUnchanged + numRemoved) > maxNumDisplayedValues}">
    <span class="diffWarning">
      <fmt:message key="listViewer.listIsTruncated">
        <fmt:param value="${maxNumDisplayedValues}"/>
      </fmt:message>
    </span>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/diff/suggestedListViewer.jsp#2 $$Change: 651448 $--%>

