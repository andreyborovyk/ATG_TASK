<%--
  Default asset diff view.
  All parameters are request scoped:
  @param  view1               A MappedView item for this view
  @param  view2               A MappedView item for this view
  @param  item1               A repository item corresponding to view1
  @param  item2               A repository item corresponding to view2
  @param  conflictMode        A boolean indicating whether we are in conflict mode
  @param  conflictFormHandlerPath A form handler that handles selection of properties for merge
  @param  item1Title          Title for the first item
  @param  item2Title          Title for the second item

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

<fmt:setBundle var="propertiesBundle" basename="atg.epub.PublishingRepositoryResources"/>

<!-- Begin content. -->

<%-- Table comparing 2 versions of an item in multiple columns. --%>
<div id="nonTableContent" style="padding-top: 0px;">
  <table border="0" cellpadding="5" cellspacing="0">

    <%-- Setup 4 columns in conflictMode for the extra radio buttons. --%>
    <c:choose>
      <c:when test="${! empty conflictMode}" >
        <tr>
          <td>&nbsp;</td>
          <td>&nbsp;</td>
          <td rowspan="8" width="20" class="diffLine">&nbsp;</td>
          <td>&nbsp;</td>
        </tr>
      </c:when>
      <c:otherwise>
        <tr>
          <td>&nbsp;</td>
          <td width="20">&nbsp;</td>
          <td>&nbsp;</td>
        </tr>
      </c:otherwise>
    </c:choose>

    <tr>
      <%-- Column sep --%>
      <c:if test="${! empty conflictMode}" >
        <td></td>
      </c:if>
      <%-- Version 1 Header --%>
      <td class="verticalAligned diffDate"><h5><c:out value="${item1Title}"/></h5></td>
      <%-- Column sep --%>
      <c:choose>
        <c:when test="${! empty conflictMode}" >
          <td></td>
        </c:when>
        <c:otherwise>
          <td>&nbsp;</td>
        </c:otherwise>
      </c:choose>
      <%-- Version 2 Header --%>
      <td class="verticalAligned diffDate"><h5><c:out value="${item2Title}"/></h5></td>
    </tr>

    <tr>
      <%-- Column sep --%>
      <c:if test="${! empty conflictMode}" >
        <td></td>
      </c:if>
      <!-- Version 1: author and date info  -->
      <fmt:message key="author" var="itemAuthorText" bundle="${propertiesBundle}"/>
	  <fmt:message key="mergeErrorMessage" var="mergeProblem" bundle="${propertiesBundle}"/>
      <td><p>
        <jsp:useBean id="date1" class="java.util.Date"/>
		<!-- 	Changes to display the edit time of the refrenced asset	  -->
		<c:if test="${item1.workspace.checkInTime == 0 && item1.predecessorVersionItem != null}">
			<c:set target="${date1}" property="time" value="${item1.predecessorVersionItem.workspace.checkInTime}"/>
			<fmt:formatDate value="${date1}" type="BOTH" dateStyle="MEDIUM" timeStyle="SHORT"/><br />
	        <c:out value="${itemAuthorText}"/>:  <c:out value="${item1.predecessorVersionItem.workspace.user}"/>
		</c:if>
		<c:if test="${item1.workspace.checkInTime != 0}">
			<c:set target="${date1}" property="time" value="${item1.workspace.checkInTime}"/>
			<fmt:formatDate value="${date1}" type="BOTH" dateStyle="MEDIUM" timeStyle="SHORT"/><br />
	        <c:out value="${itemAuthorText}"/>:  <c:out value="${item1.workspace.user}"/>
		</c:if>
		<c:if test="${item1.workspace.checkInTime == 0 && item1.predecessorVersionItem == null}">
		  <c:out value="${mergeProblem}"/>
		 </c:if>
      </p></td>
      <%-- Column sep --%>
      <c:choose>
        <c:when test="${! empty conflictMode}" >
          <td></td>
        </c:when>
        <c:otherwise>
          <td>&nbsp;</td>
        </c:otherwise>
      </c:choose>
      <!-- Version 2: author and date info  -->
      <td><p>
        <jsp:useBean id="date2" class="java.util.Date"/>
        <c:set target="${date2}" property="time" value="${item2.workspace.checkInTime}"/>
        <fmt:formatDate value="${date2}" type="BOTH" dateStyle="MEDIUM" timeStyle="SHORT"/><br />
        <c:out value="${itemAuthorText}"/>:  <c:out value="${item2.workspace.user}"/>
      </p></td>
    </tr>

    <c:forEach items="${view1.propertyMappings}" var="mpv1">
      <%-- Dotted line sep between diff properties.
      <c:if test="${empty conflictMode}" >
        <tr><td colspan="5" class="sep">&nbsp;</td></tr>
      </c:if>
      --%>

      <tr>
        <c:if test="${mpv1.value.mapped}">
          <c:set value="${mpv1.value}" var="mpv" scope="request"/>
          <c:if test="${ !empty mpv.uri}">

            <%-- Radio 1 for conflict mode. --%>
            <c:set value="${mpv.propertyName}" var="propertyName"/>
            <c:if test="${!empty conflictMode}">
              <td class="verticalAligned">
                <dspel:input bean="${conflictFormHandlerPath}.selectedVersionsForMerge.${propertyName}"
                  type="radio" value="1" iclass="radio"/>
              </td>
            </c:if>

            <%-- Version 1 property mapping. --%>
            <td class="verticalAligned">
              <dspel:include otherContext="${mpv.contextRoot}" page="${mpv.uri}"/>
            </td>
          </c:if>

          <%-- Radio 2 for conflict mode, or column sep. --%>
          <c:choose>
            <c:when test="${! empty conflictMode}">
              <td class="verticalAligned">
                <dspel:input bean="${conflictFormHandlerPath}.selectedVersionsForMerge.${propertyName}"
                  type="radio" value="2" iclass="radio"/>
              </td>
            </c:when>
            <c:otherwise>
              <td>&nbsp;</td>
            </c:otherwise>
          </c:choose>

          <%-- Version 2 property mapping (for the same property). --%>
          <c:set value="${view2.propertyMappings[propertyName]}" var="mpv" scope="request"/>
          <c:if test="${ !empty mpv.uri}">
            <td class="verticalAligned">
              <dspel:include otherContext="${mpv.contextRoot}" page="${mpv.uri}"/>
            </td>
          </c:if>
        </c:if>
      </tr>
    </c:forEach>

  </table>
</div>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/item/gsa/diff.jsp#2 $$Change: 651448 $--%>
