<%--
  Viewer for array/list and set assets in diff/merge mode
  @param  mpv  A request scoped, MappedPropertyView item for this view

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"    uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <%-- Get the collection type for this property --%>
  <c:set var="collectionType" value="${propertyView.propertyDescriptor.typeName}"/>
  <c:set var="isSet"  value="${collectionType == 'set'}"/>
  <c:set var="isList"  value="${collectionType == 'list'}"/>
  <c:set var="isArray" value="${not isSet and not isList }"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Display each value in the list using the appropriate component viewer --%>
  <dspel:getvalueof var="values" bean="${propertyView.formHandlerProperty}"/>

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

  <c:if test="${isSet or isList}">
    <web-ui:collectionPropertySize var="numValues" collection="${values}"/>
  </c:if>
  <c:if test="${isArray}">
    <web-ui:collectionPropertySize var="numValues" array="${values}"/>
  </c:if>

  <c:set var="listIsTruncated" value="false"/>
  <c:if test="${numValues > maxNumDisplayedValues}">
    <c:set var="numValues" value="${maxNumDisplayedValues}"/>
    <c:set var="listIsTruncated" value="true"/>
  </c:if>

  <c:if test="${numValues > 0}">
    <c:forEach var="value" items="${values}" begin="0" end="${numValues-1}" varStatus="valuesLoop">
      <c:set scope="request" var="componentValue" value="${value}"/>
      <c:if test="${isList or isArray}">
        <c:out value="${valuesLoop.index + 1}"/>.&nbsp;
      </c:if>
      <dspel:include otherContext="${propertyView.componentApplication}" page="${propertyView.componentUri}"/>
      <br/>
    </c:forEach>
  </c:if>

  <c:if test="${listIsTruncated == 'true'}">
    <span class="diffWarning">
      <fmt:message key="listViewer.listIsTruncated">
        <fmt:param value="${maxNumDisplayedValues}"/>
      </fmt:message>
    </span>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/diff/listViewer.jsp#2 $$Change: 651448 $--%>
