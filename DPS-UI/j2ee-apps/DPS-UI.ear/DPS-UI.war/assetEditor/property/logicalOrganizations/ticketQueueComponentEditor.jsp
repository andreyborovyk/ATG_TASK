<%--
  Editor for the rows in a ticketQueues table.

  The following request-scoped variables are expected to be set:

  @param  mpv  A MappedPropertyView item for this view
  @author sdrye
  @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/logicalOrganizations/ticketQueueComponentEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramInitialize"   param="initialize"/>
  <dspel:getvalueof var="paramRenderHeader" param="renderHeader"/>
  <dspel:getvalueof var="paramTemplate"     param="template"/>
  <dspel:getvalueof var="paramIndex"        param="index"/>
  <dspel:getvalueof var="paramInfoObj"      param="infoObj"/>
  <dspel:getvalueof var="paramDisplayName"  param="displayName"/>
  <dspel:getvalueof var="paramAssetURI"     param="assetURI"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <dspel:importbean var="config" bean="/atg/web/personalization/Configuration"/>
  <fmt:setBundle var="personalizationBundle" basename="${config.resourceBundle}"/>
  <dspel:importbean var="assetManagerConfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${assetManagerConfig.resourceBundle}"/>

  <%-- Derive IDs for some form elements and data objects --%>
  <c:set var="id"     value="${requestScope.atgPropertyViewId}"/>
  <c:set var="linkId" value="${id}_link_${paramIndex}"/>

  <c:choose>
    <c:when test="${paramInitialize}">

      <%-- Indicate to the parent editor, via a request variable, the name of the
           function to be called when the "Add New" button is clicked --%>
      <c:set scope="request" var="addNewFunc" value="${paramInfoObj}.createAsset"/>
    </c:when>

    <c:when test="${paramRenderHeader}">
      <thead>
        <tr class="header">
          <th class="formValueCell">
            <fmt:message bundle="${personalizationBundle}" key="ticketing.ticketQueuesHeader"/>
          </th>
          <th class="formValueCell">
            <fmt:message bundle="${personalizationBundle}" key="ticketing.defaultEscalationQueuesHeader"/>
          </th>
          <th class="formValueCell">
            <fmt:message bundle="${personalizationBundle}" key="ticketing.targetSlaHeader"/>
          </th>
          <th class="formValueCell">
            <fmt:message bundle="${personalizationBundle}" key="ticketing.activeHeader"/>
          </th>
          <th class="formValueCell">
            &nbsp;
          </th>
        </tr>
      </thead>
    </c:when>

    <c:when test="${paramTemplate}">
      <%-- Since we don't support the "Add Existing" button, we don't need to
           render a template --%>
    </c:when>

    <c:otherwise>

      <%-- Display the asset name, either as a label or a link --%>
      <td class="formValueCell">
        <c:choose>
          <c:when test="${requestScope.atgIsMultiEditView or propertyView.attributes.prohibitDrillDown}">
            <span id="<c:out value='${linkId}'/>">
              <c:out value="${paramDisplayName}"/>
            </span>
          </c:when>
          <c:otherwise>
            <a id="<c:out value='${linkId}'/>"
               href="javascript:<c:out value='${paramInfoObj}.drillDown("${paramAssetURI}")'/>">
              <c:out value="${paramDisplayName}"/>
            </a>
          </c:otherwise>
        </c:choose>
      </td>
      <td class="formValueCell">
        <dspel:valueof bean="${mpv.formHandlerProperty}[${paramIndex}].defaultEscalationQueue.name"/>
      </td>
      <td class="formValueCell">
        <dspel:valueof bean="${mpv.formHandlerProperty}[${paramIndex}].slaMinutes"/>
      </td>
      <td class="formValueCell">
        <dspel:getvalueof var="item" bean="${mpv.formHandlerProperty}[${paramIndex}].acceptingTickets"/>
        <c:if test="${item == true || item == 'true'}">
          <fmt:message key="yes"/>
        </c:if>
        <c:if test="${item == false || item == 'false'}">
          <fmt:message key="no"/>
        </c:if>
      </td>      
    </c:otherwise>
  </c:choose>

</dspel:page>
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/logicalOrganizations/ticketQueueComponentEditor.jsp#2 $$Change: 651448 $--%>
