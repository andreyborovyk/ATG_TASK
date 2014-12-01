<%--
  sort property editor

  The following request-scoped variables are expected to be set:
  
  @param  propertyView A MappedPropertyView item for this view
  @param  view         A MappedItemView item for this view
  @param  formHandler  The form handler for the form that displays this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>
  <dspel:importbean var="config"
                    bean="/atg/commerce/search/web/Configuration"/>
  <fmt:setBundle var="bundle" basename="${config.resourceBundle}"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="view" value="${requestScope.view}"/>

  <%-- Get an ID for the property's input element --%>  
  <c:set var="inputId" value="propertyValue_${propertyView.uniqueId}"/>
  <c:if test="${not empty requestScope.uniqueAssetID}">
    <c:set var="inputId" value="propertyValue_${requestScope.uniqueAssetID}${propertyView.uniqueId}"/>  
  </c:if>

  <dspel:getvalueof var="currentValue" bean="${propertyView.formHandlerProperty}"/>

  <%-- determine which mode we're in --%>
  <c:choose>
    <c:when test="${view.itemMapping.mode == 'AssetManager.edit'}">
      <c:set var="disabled" value="false"/>
    </c:when>
    <c:otherwise>
      <c:set var="disabled" value="true"/>
    </c:otherwise>
  </c:choose>

  <ul class="formVerticalList selectionBullets">
    <%-- Iterate over each of the options in the enumeration descriptor --%>
    <c:forEach items="${propertyView.propertyDescriptor.enumeratedValues}" var="enumValue" varStatus="enumStatus">

      <%-- Check if this item should be the currently selected item --%>
      <c:choose>
        <c:when test="${propertyView.propertyDescriptor.useCodeForValue}">
          <dspel:droplet name="/atg/dynamo/droplet/Switch">
            <dspel:param bean="${propertyView.formHandlerProperty}" name="value"/>
            <dspel:oparam name="${propertyView.propertyDescriptor.enumeratedCodes[enumStatus.index]}">
              <li><dspel:input type="radio" value="${enumValue}" iclass="radioBullet" bean="${propertyView.formHandlerProperty}"
                           id="propertyValue_${propertyView.uniqueId}_${enumValue}"
                           onchange="markAssetModified()"
                           onpropertychange="formFieldModified()"
                           disabled="${disabled}" checked="true" />
            </dspel:oparam>
            <dspel:oparam name="default">
              <li><dspel:input type="radio" value="${enumValue}" iclass="radioBullet" bean="${propertyView.formHandlerProperty}"
                           id="propertyValue_${propertyView.uniqueId}_${enumValue}"
                           onchange="markAssetModified()"
                           onpropertychange="formFieldModified()"
                           disabled="${disabled}"/>
            </dspel:oparam>
          </dspel:droplet>
        </c:when>
        <c:otherwise>
          <dspel:droplet name="/atg/dynamo/droplet/Switch">
            <dspel:param bean="${propertyView.formHandlerProperty}" name="value"/>
            <dspel:oparam name="${enumValue}">
              <li><dspel:input type="radio" value="${enumValue}" iclass="radioBullet" bean="${propertyView.formHandlerProperty}"
                           id="propertyValue_${propertyView.uniqueId}_${enumValue}"
                           onchange="markAssetModified()"
                           onpropertychange="formFieldModified()"
                           disabled="${disabled}" checked="true" />
            </dspel:oparam>
            <dspel:oparam name="default">
              <li><dspel:input type="radio" value="${enumValue}" iclass="radioBullet"
                           id="propertyValue_${propertyView.uniqueId}_${enumValue}"
                           onchange="markAssetModified()"
                           onpropertychange="formFieldModified()"
                           disabled="${disabled}" bean="${propertyView.formHandlerProperty}" />
            </dspel:oparam>
          </dspel:droplet>
        </c:otherwise>
      </c:choose>

      <c:if test="${debug}">
        <c:out value="${enumValue}" escapeXml="false"/>
      </c:if>
      <dspel:importbean bean="/atg/dynamo/droplet/Switch"/>
      <dspel:droplet name="Switch">
        <dspel:param name="value" value="${enumValue}" />
        <dspel:oparam name="count">
          <fmt:message key="valueCount" bundle="${bundle}"/></li>
        </dspel:oparam>
        <dspel:oparam name="order">
          <fmt:message key="specifiedOrder" bundle="${bundle}"/>
          <%-- Only render if this is a numeric facet --%>
          <dspel:droplet name="Switch">
            <dspel:param name="value" bean="${formHandlerPath}.value.propertyType"/>
            <dspel:oparam name="float">
              <br><fmt:message key="specifiedOrderNote" bundle="${bundle}"/>
            </dspel:oparam>
            <dspel:oparam name="integer">
              <br><fmt:message key="specifiedOrderNote" bundle="${bundle}"/>            
            </dspel:oparam>
          </dspel:droplet>
          </li>
        </dspel:oparam>
        <dspel:oparam name="default">
          <fmt:message key="numericAlphanumeric" bundle="${bundle}"/></li>
        </dspel:oparam>
      </dspel:droplet>
    </c:forEach>
  </ul>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/property/facetValueSortingEdit.jsp#2 $$Change: 651448 $--%>
