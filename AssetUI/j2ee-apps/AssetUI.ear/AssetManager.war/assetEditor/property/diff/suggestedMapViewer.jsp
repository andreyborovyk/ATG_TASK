<%--
  Viewer for the suggested merge of 2 conflicting map properties.

  @param  mpv  A request scoped, MappedPropertyView item for this view

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramFormHandler" param="formHandler"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <fmt:message key="commonDiff.suggestion.changedIndicator" var="changedIndicator"/>
  <fmt:message key="commonDiff.suggestion.addedIndicator" var="addedIndicator"/>
  <c:set var="propertyName" value="${propertyView.propertyName}"/>

  <%-- Display the suggestion broken out to changed, added, unchanged, and removed.  --%>
  <c:set var="changedEntries" value="${paramFormHandler.changedItemsInSuggestion[propertyName]}"/>
  <c:set var="addedEntries" value="${paramFormHandler.addedItemsInSuggestion[propertyName]}"/>
  <c:set var="unchangedEntries" value="${paramFormHandler.unchangedItemsInSuggestion[propertyName]}"/>
  <c:set var="removedEntries" value="${paramFormHandler.removedItemsInSuggestion[propertyName]}"/>
  <web-ui:collectionPropertySize var="numChanged" map="${changedEntries}"/>
  <web-ui:collectionPropertySize var="numAdded" map="${addedEntries}"/>
  <web-ui:collectionPropertySize var="numUnchanged" map="${unchangedEntries}"/>
  <web-ui:collectionPropertySize var="numRemoved" map="${removedEntries}"/>

  <%-- Display changed entries.  --%>
  <c:forEach var="changedEntry" items="${changedEntries}">
    <c:out value="${changedIndicator}"/>
    <c:out value="${changedEntry.key}"/>:
    <c:set scope="request" var="componentValue" value="${changedEntry.value}"/>
    <dspel:include otherContext="${propertyView.componentApplication}" page="${propertyView.componentUri}"/>
    <br/>
  </c:forEach>

  <%-- Display added entries.  --%>
  <c:forEach var="addedEntry" items="${addedEntries}">
    <c:out value="${addedIndicator}"/>
    <c:out value="${addedEntry.key}"/>:
    <c:set scope="request" var="componentValue" value="${addedEntry.value}"/>
    <dspel:include otherContext="${propertyView.componentApplication}" page="${propertyView.componentUri}"/>
    <br/>
  </c:forEach>

  <%-- Display unchanged entries.  --%>
  <c:forEach var="unchangedEntry" items="${unchangedEntries}">
    <c:out value="${unchangedEntry.key}"/>:
    <c:set scope="request" var="componentValue" value="${unchangedEntry.value}"/>
    <dspel:include otherContext="${propertyView.componentApplication}" page="${propertyView.componentUri}"/>
    <br/>
  </c:forEach>

  <%-- Display removed entries.  --%>
  <c:forEach var="removedEntry" items="${removedEntries}">
    <del>
      <c:out value="${removedEntry.key}"/>:
      <c:set scope="request" var="componentValue" value="${removedEntry.value}"/>
      <dspel:include otherContext="${propertyView.componentApplication}" page="${propertyView.componentUri}"/>
    </del>
    <br/>
  </c:forEach>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/diff/suggestedMapViewer.jsp#2 $$Change: 651448 $--%>
