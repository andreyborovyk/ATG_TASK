<%--
  Property editor for editing expression-based segment rules.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/views/property/segmentRules/editRules.jsp#2 $ $Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<!-- Begin /AssetUI/views/property/segmentRules/editRules.jsp -->
<dspel:page>

  <fmt:setBundle basename="atg.web.targeting.TargetingResources"/>

  <table border="0" cellpadding="0" cellspacing="3" width="100%">
    <c:choose>

      <c:when test="${formHandler.editable}">
          <tr>
            <c:set var="pvClassAttr" value="class=\"formLabel\""/>
            <c:if test="${mpv.propertyDescriptor.required}">
              <c:set var="pvClassAttr" value="class=\"formLabel formLabelRequired\""/>
            </c:if>

            <c:set var="pvTitle" value="${mpv.propertyDescriptor.displayName}"/>
            <c:if test="${mpv.attributes.title != null}">
              <c:set var="pvTitle" value="${mpv.attributes.title}"/>
            </c:if>

            <td <c:out value="${pvClassAttr}" escapeXml="false"/> >
              <c:out value="<label for=\"${mpv.propertyDescriptor.name}\">" escapeXml="false"/><c:out value="${pvTitle}" escapeXml="false"/>:</label>
            </td>

            <td colspan="2" width="auto">
              <dspel:include page="segmentExpressionPanel.jsp"/>
            </td>
          </tr>
      </c:when>

      <c:otherwise>
        <%-- For non-editable segments, just display an anglicized rule string. --%>
        <tr>
          <c:set var="pvClassAttr" value="class=\"formLabel\""/>
          <c:if test="${mpv.propertyDescriptor.required}">
            <c:set var="pvClassAttr" value="class=\"formLabel formLabelRequired\""/>
          </c:if>

          <c:set var="pvTitle" value="${mpv.propertyDescriptor.displayName}"/>
          <c:if test="${mpv.attributes.title != null}">
            <c:set var="pvTitle" value="${mpv.attributes.title}"/>
          </c:if>

          <td <c:out value="${pvClassAttr}" escapeXml="false"/> >
            <c:out value="<label for=\"${mpv.propertyDescriptor.name}\">" escapeXml="false"/><c:out value="${pvTitle}" escapeXml="false"/>:</label>
          </td>

          <td>
            <table border="0" cellpadding="3" cellspacing="0">
              <tr>
                <td class="leftAlign">
                  <span class="tableInfo">
                    <c:out value="${formHandler.rulestring}"/>
                  </span>
                </td>
              </tr>
            </table>
          </td>
        <tr>
      </c:otherwise>

    </c:choose>

    <%-- Segment Counts. --%>
    <c:catch var="countException">
      <c:set var="count" value="${formHandler.groupCount}"/>
    </c:catch>
    <c:if test="${empty countException}">
      <tr>
        <td class="formLabel">
          <label for="groupCount"><fmt:message key="segmentCountLabel"/></label>
        </td>
        <td class="formText formPadding"><c:out value="${count}"/>&nbsp;
          <input type="button" value='<fmt:message key="segmentCountUpdateButtonText"/>'
            class="standardButton"
            onmousedown='javascript:eljGetBodies(
                function() {
                  saveChanges( { actionType: "setView" } );
                });'/>
        </td>
      </tr>
    </c:if>
  </table>

  <br />

</dspel:page>
<!-- End /AssetUI/views/property/segmentRules/editRules.jsp -->
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/views/property/segmentRules/editRules.jsp#2 $$Change: 651448 $--%>
