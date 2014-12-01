<%--
  Page fragment displaying an image in an additional column inside an
  asset editor table.

  The following request-scoped variables are expected to be set:

  @param  numRowsToSpan   The number of rows in the table.
  @param  mpv             A MappedPropertyView displaying the first property inside the table.

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="pNumRowsToSpan" param="numRowsToSpan"/>

  <%-- Get the associated image. --%>
  <c:set var="formHandlerPath" value="${requestScope.mpv.formHandlerPath}"/>
  <c:set var="valueDictName" value="${requestScope.mpv.view.itemMapping.valueDictionaryName}"/>
  <c:set var="imagePropertyName" value="${requestScope.atgItemViewMapping.attributes.additionalColumnImagePropertyName}"/>
  <dspel:getvalueof var="url" bean="${formHandlerPath}.${valueDictName}.${imagePropertyName}.url"/>
  <c:set var="imageWidth" value="${requestScope.atgItemViewMapping.attributes.additionalColumnImageWidth}"/>
  <c:set var="imageHeight" value="${requestScope.atgItemViewMapping.attributes.additionalColumnImageHeight}"/>

  <c:if test="${!empty url}">
    <td rowspan="<c:out value='${pNumRowsToSpan}'/>" class="assetImage">
      <c:choose>
        <c:when test="${not empty imageWidth && not empty imageHeight}">
          <img src="<c:out value='${url}'/>"
               width="<c:out value='${imageWidth}'/>"
               height="<c:out value='${imageHeight}'/>"/>
        </c:when>
        <c:otherwise>
          <img src="<c:out value='${url}'/>"/>
        </c:otherwise>
      </c:choose>
    </td>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/image/imageDisplayColumn.jsp#2 $$Change: 651448 $--%>
