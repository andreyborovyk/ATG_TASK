<%--
  Editor for the rows in an escalationQueues table.

  The following request-scoped variables are expected to be set:

  @param  mpv  A MappedPropertyView item for this view
  @author sdrye
  @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/logicalOrganizations/escalationQueueComponentEditor.jsp#2 $$Change: 651448 $
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
  <dspel:getvalueof var="paramValueId"      param="valueId"/>
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
  <c:set var="id"                  value="${requestScope.atgPropertyViewId}"/>
  <c:set var="linkIdPrefix"        value="${id}_link_"/>
  <c:set var="linkId"              value="${linkIdPrefix}${paramIndex}"/>
  <c:set var="radioId"             value="${id}_defaultQueueRadio_"/>
  <c:set var="defaultQueueId"      value="${id}_defaultQueue_"/>
  <c:set var="onPickFunc"          value="${id}_onPick"/>
  <c:set var="onSelectDefaultFunc" value="${id}_onSelectDefault"/>

  <c:choose>
    <c:when test="${paramInitialize}">

      <%-- Indicate to the parent editor, via a request variable, the name of the
           function to be called when the "Add New" button is clicked --%>
      <c:set scope="request" var="addNewFunc" value="${paramInfoObj}.createAsset"/>

      <%-- Get the default escalation queue ID for the containing ticket queue,
           so that we can determine whether to select the associated radio button --%>
      <dspel:getvalueof scope="request"
                        var="defaultEscalationQueueId"
                        bean="${requestScope.formHandlerPath}.value.defaultEscalationQueue.repositoryId"/>

      <%-- Create a hidden DSP input connected to the default escalation queue ID
           for the containing ticket queue.  This will be programmatically updated
           when the user selects one of the associated radio buttons. --%>
      <dspel:input type="hidden"
                   id="${defaultQueueId}"
                   converter="nullable"
                   bean="${requestScope.formHandlerPath}.value.defaultEscalationQueue.repositoryId"/>
      
      <script type="text/javascript">
        <c:out escapeXml="false" value="//<![CDATA["/>

        // Called when the user clicks a radio button to set the parent queue's
        // default escalation queue
        //
        // @param  selectedId  The ID of the queue
        //
        function <c:out value="${onSelectDefaultFunc}"/>(selectedId) {
          var input = document.getElementById("<c:out value='${defaultQueueId}'/>");
          input.value = selectedId;
          markAssetModified();
        }

        // Indicate that the collection editor should use the asset picker, and
        // specify a selection callback.
        <c:out value="${paramInfoObj}"/>.useAssetPicker = true;
        <c:out value="${paramInfoObj}"/>.onPick = <c:out value='${onPickFunc}'/>;

        // Called when the user makes a selection from the asset picker.
        //
        // @param  pSelected  An object containing info about the selected asset
        // @param  pIndex     The index of the collection element to which the
        //                    asset picker applies
        //
        function <c:out value="${onPickFunc}"/>(pSelected, pIndex) {

          // Change the display string and destination of the drill-down link
          // field to reflect the selected asset.
          var link = document.getElementById("<c:out value='${linkIdPrefix}'/>" + pIndex);
          if (link) {
            link.innerHTML = pSelected.displayName;
            link.href = "javascript:<c:out value='${paramInfoObj}'/>.drillDown('" + pSelected.uri + "')";
          }
        }

        <c:out escapeXml="false" value="//]]>"/>
      </script>
    </c:when>

    <c:when test="${paramRenderHeader}">
      <thead>
        <tr class="header">
          <th class="formValueCell">
            <fmt:message bundle="${personalizationBundle}" key="ticketQueue.ticketQueueHeader"/>
          </th>
          <th class="formValueCell">
            <fmt:message bundle="${personalizationBundle}" key="ticketQueue.organizationHeader"/>
          </th>
          <th class="formValueCell">
            <fmt:message bundle="${personalizationBundle}" key="ticketQueue.defaultHeader"/>
          </th>
          <th class="formValueCell">
            &nbsp;
          </th>
        </tr>
      </thead>
    </c:when>

    <c:otherwise>

      <%-- Get the current property value, unless we are rendering a template --%>
      <c:if test="${not paramTemplate}">
        <dspel:getvalueof var="assetId" bean="${propertyView.formHandlerProperty}.repositoryIds[${paramIndex}]"/>
      </c:if>

      <td class="formValueCell">

        <%-- Store the property value's repositoryId in a hidden input whose value
             will be manipulated by CollectionEditor functions --%>
        <input type="hidden" id="<c:out value='${paramValueId}'/>"
               class="formTextField" value=""/>

        <%-- Display the asset name, either as a label or a link --%>
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
        <c:choose>
          <c:when test="${not empty assetId}">
            <dspel:valueof bean="${mpv.formHandlerProperty}[${paramIndex}].physicalOrganizationName"/>
          </c:when>
          <c:otherwise>
            &nbsp;
          </c:otherwise>
        </c:choose>
      </td>
      <td class="formValueCell">
        <c:choose>
          <c:when test="${not empty assetId}">
            <c:if test="${assetId == requestScope.defaultEscalationQueueId}">
              <input type="radio" name="<c:out value='${radioId}'/>" id="<c:out value='${radioId}'/>" value="<c:out value='${assetId}'/>" checked="checked"/>
            </c:if>
            <c:if test="${assetId != requestScope.defaultEscalationQueueId}">
              <input type="radio" name="<c:out value='${radioId}'/>" id="<c:out value='${radioId}'/>" value="<c:out value='${assetId}'/>" onclick="<c:out value='${onSelectDefaultFunc}'/>('<c:out value="${assetId}"/>');"/>
            </c:if>
          </c:when>
          <c:otherwise>
            &nbsp;
          </c:otherwise>
        </c:choose>
      </td>
    </c:otherwise>
  </c:choose>

</dspel:page>
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/logicalOrganizations/escalationQueueComponentEditor.jsp#2 $$Change: 651448 $--%>
