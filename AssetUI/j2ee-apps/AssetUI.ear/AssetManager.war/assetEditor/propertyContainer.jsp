<%--
  Default page fragment for rendering a property editor.  This takes care of
  rendering the surrounding labels and controls.

  @param  prop  A TabPropertyFilter.PropertyDescriptorWrapper object for the
                property to be rendered

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/propertyContainer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="pProp" param="prop"/>
  <dspel:getvalueof var="pColumnFragment" param="columnFragment"/>
  <dspel:getvalueof var="pColumnFragmentContext" param="columnFragmentContext"/>
  <dspel:getvalueof var="pNumRowsToSpan" param="numRowsToSpan"/>

  <%@ include file="/multiEdit/multiEditConstants.jspf" %>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set var="multiEditMode" value="${requestScope.multiEditMode}"/>
  <c:set var="multiEditOperation" value="${requestScope.multiEditOperation}"/>
  <c:set var="formHandler" value="${requestScope.formHandler}"/>
  <c:set var="propertyDescriptor" value="${pProp.propertyDescriptor}"/>
  <c:set var="propertyName" value="${propertyDescriptor.name}"/>

  <%-- Get a MappedPropertyView object for the property.  This provides access to
       info that is stored in the associated propertyViewMapping and propertyView
       items. --%>
  <c:set var="propView"
         value="${requestScope.atgItemViewMapping.propertyMappings[propertyName]}"/>

  <%-- Special case: If the user does not have write access to this property
       because of security settings, the property view will not reflect this
       fact.  So we have to replace the view with the read-only version. --%>
  <c:if test="${pProp.readOnlySecured}">
    <c:set var="propView" value="${propView.readOnlyPropertyMapping}"/>
  </c:if>

  <%-- Set a request variable that is unique across all property views that are
       currently being displayed in the asset editor.  This can be used to
       formulate IDs that must be unique. --%>
  <c:set scope="request" var="atgPropertyViewId"
         value="${requestScope.uniqueAssetID}${propView.uniqueId}"/>

  <%-- Derive IDs for page elements --%>
  <c:set var="propEditorDivId"     value="propEditorDiv_${requestScope.atgPropertyViewId}"/>
  <c:set var="inheritedValueDivId" value="inheritedValueDiv_${requestScope.atgPropertyViewId}"/>

  <tr>
    <c:choose>
      <c:when test="${empty propView}">

        <%-- Display an error message if there is no view mapping for the property --%>
        <td colspan="2">
          <fmt:message key="assetEditor.propertyNotFound">
            <fmt:param value="${propertyName}"/>
          </fmt:message>
        </td>
      </c:when>
      <c:otherwise>

        <%-- Store the propertyView in a request-scoped variable, for access by
             any of the included property editor page fragments.  The name "mpv"
             is used for historical reasons. --%>
        <c:set scope="request" var="mpv" value="${propView}"/>

        <%-- Also store the wrapped property descriptor --%>
        <c:set scope="request" var="atgPropertyDescriptorWrapper" value="${pProp}"/>

        <%-- Determine if this property is writable --%>
        <c:set var="writable"
               value="${requestScope.atgIsAssetEditable and
                        propertyDescriptor.writable and
                        not pProp.readOnlySecured and
                        propView.mode ne 'AssetManager.view'}"/>

        <%-- The apply-to-all icon should be displayed for a writable property
             if the asset editor panel is in apply-to-all mode. --%>
        <c:set var="applyToAllMode"
               value="${multiEditMode eq MODE_MULTI and
                        multiEditOperation eq OPERATION_APPLY_TO_ALL}"/>
        <c:set var="showApplyToAll"
               value="${writable and applyToAllMode}"/>

        <%-- The derived property icon should be displayed for a writable
             derived property *unless* the asset editor panel is in
             apply-to-all mode.  The display of the icon can also be
             suppressed by specifying an "ignoreDerivation" attribute on the
             property view. --%>
        <c:set var="showDerivedPropMenu"
               value="${writable and
                        propView.derived and
                        not showApplyToAll and
                        not propView.attributes.ignoreDerivation}"/>

        <%-- See if there was an exception for this property --%>
        <c:set var="propertyException" value="${formHandler.propertyExceptions[propertyName]}"/>

        <%-- In multi-edit mode, we want to ensure that the property editor uses
             the correct bean path when it references the formHandlerProperty of
             the property view.  To protect against cases in which the formHandler
             of the multi-edit itemMapping does not match that of the single-edit
             itemMapping, we explicitly override the formHandlerPath that is
             specified in the property view so that it uses the formHandlerPath from
             the requestScope (which is always obtained from the single-edit
             itemMapping).  We probably should at least log a console warning in
             this case, since this condition is really caused by a programming error.
             It should be noted that when we explicitly set the formHandlerPath,
             the MappedPropertyView object no longer inherits that property value
             from its parent. --%>
        <c:if test="${requestScope.atgIsMultiEditView}">
          <c:set var="origPropViewFormHandlerPath" value="${propView.formHandlerPath}"/>
          <c:set target="${propView}" property="formHandlerPath" value="${requestScope.formHandlerPath}"/>
        </c:if>

        <%-- Typically, the property view should include a table cell that
             displays the property name.  This can be overridden by specifying
             a property view attribute named "fullWidth", which causes the
             property editor to occupy the entire table row.  Also, we are
             implementing a temporary fix for PR 124239, in which we always
             display the property name cell if the apply-to-all panel is being
             displayed. --%>
        <c:choose>
          <c:when test="${empty propView.attributes.fullWidth or applyToAllMode}">
            <td class="formLabel">

              <%-- In edit mode, display an indicator for errors and for
                   required properties --%>
              <c:if test="${requestScope.atgIsAssetEditable}">
                <c:choose>
                  <c:when test="${not empty propertyException}">
                    <span class="error" title="<c:out value='${propertyException.message}'/>">
                      <fmt:message key="assetEditor.errorMarker"/>
                    </span>
                  </c:when>
                  <c:when test="${propertyDescriptor.required}">
                    <span class="required" title="<fmt:message key='assetEditor.required.title'/>">
                      <fmt:message key="assetEditor.requiredMarker"/>
                    </span>
                  </c:when>
                </c:choose>
              </c:if>

              <%-- Display the property name --%>
              <c:out value="${pProp.displayName}"/>:

            </td>
            <td>
          </c:when>
          <c:otherwise>

            <%-- Allow a full-width property editor to include a cell divider.
                 For example, this would be necessary if the property editor
                 wants to display a string that resembles a property name.  In
                 this case, it would render the name string followed by a cell
                 divider, followed by the editing controls. --%>
            <c:choose>
              <c:when test="${not empty propView.attributes.includeCellDivider}">
                <td>
              </c:when>
              <c:otherwise>
                <td colspan="2">
              </c:otherwise>
            </c:choose>
          </c:otherwise>
        </c:choose>

        <%-- Set the request variable that will control the visiblity of the
             property editor.  The initial visibility is true, but it might be
             changed to false by applyToAllMenu.jsp, which we use to render to
             apply-to-all icon if necessary.  --%>
        <c:set scope="request" var="atgPropertyEditorVisibilityStyle" value="display:inline"/>
        <c:if test="${showApplyToAll}">
          <dspel:include page="/multiEdit/applyToAllMenu.jsp">
            <dspel:param name="propEditorDivId" value="${propEditorDivId}"/>
          </dspel:include>
        </c:if>

        <%-- Render the derived-property icon if necessary --%>
        <c:if test="${showDerivedPropMenu}">

          <%-- Determine if the property value is overridden.  Unfortunately,
               we can't use ${propView.overridden}, because it assumes that
               there is an item associated with the view.  So we figure it out in the
               wrapped property descriptor. --%>
          <c:set var="overridden" value="${pProp.overridden}"/>

          <dspel:include page="derivedPropertyMenu.jsp">
            <dspel:param name="overridden" value="${overridden}"/>
            <dspel:param name="propEditorDivId" value="${propEditorDivId}"/>
            <dspel:param name="inheritedValueDivId" value="${inheritedValueDivId}"/>
          </dspel:include>
        </c:if>

        <%-- Display the error message for this property, if there is one --%>
        <c:if test="${not empty propertyException}">
          <span class="propertyError">
            <c:out value="${propertyException.message}"/>
          </span>
          <br/>
        </c:if>

        <c:if test="${showDerivedPropMenu}">

          <%-- For a derived property, we have 2 property editors: one editable
               one for the override property and a second disabled one for the
               inherited value.  The choice of inherit vs. override will hide
               and display these as appropriate. --%>
          <c:choose>
            <c:when test="${overridden}">
              <c:set var="inheritedPropVisibilityStyle" value="display:none"/>
            </c:when>
            <c:otherwise>
              <c:set var="inheritedPropVisibilityStyle" value="display:inline"/>
              <c:set scope="request" var="atgPropertyEditorVisibilityStyle" value="display:none"/>
            </c:otherwise>
          </c:choose>

          <%-- The following div is shown if the "inherit" option is selected from
               the derived property menu. --%>
          <div id="<c:out value='${inheritedValueDivId}'/>"
               style="<c:out value='${inheritedPropVisibilityStyle}'/>">
            <span class="disabled">
              <c:choose>
                <c:when test="${overridden}">

                  <%-- We can't actually display the inherited value if the value is
                       currently overridden, because the property viewers display the
                       current value of the property. If the user selects "inherit"
                       from the menu, this message will be displayed instead.  See PR 130780. --%>
                  <fmt:message key="assetEditor.derivedProperty.useInheritedValue"/>

                </c:when>
                <c:otherwise>

                  <%-- Render the derived value in the read-only page fragment for
                       this property.  Note that we have to temporarily replace the
                       requestScope.mpv value with the read-only property view. --%>
                  <c:set var="readOnlyPropView" value="${propView.readOnlyPropertyMapping}"/>
                  <c:set var="mpvSave" value="${requestScope.mpv}"/>
                  <c:set scope="request" var="mpv" value="${readOnlyPropView}"/>

                  <%-- We also have to override the formHandlerPath of the read-only
                       multi-edit property view! (see above) :-/ --%>
                  <c:if test="${requestScope.atgIsMultiEditView}">
                    <c:set var="origReadOnlyPropViewFormHandlerPath" value="${readOnlyPropView.formHandlerPath}"/>
                    <c:set target="${readOnlyPropView}" property="formHandlerPath" value="${requestScope.formHandlerPath}"/>
                  </c:if>

                  <c:out value="<!-- Begin JSP for inherited property ${propertyName}: contextRoot=${readOnlyPropView.contextRoot} URI=${readOnlyPropView.uri} -->"
                         escapeXml="false"/>

                  <dspel:include otherContext="${readOnlyPropView.contextRoot}"
                                 page="${readOnlyPropView.uri}"/>

                  <c:out value="<!-- End JSP for inherited property ${propertyName} -->"
                         escapeXml="false"/>

                  <%-- Restore the original property view value --%>
                  <c:set scope="request" var="mpv" value="${mpvSave}"/>

                  <%-- Restore the original property view form handler path if we modified it above --%>
                  <c:if test="${requestScope.atgIsMultiEditView}">
                    <c:set target="${readOnlyPropView}" property="formHandlerPath" value="${origReadOnlyPropViewFormHandlerPath}"/>
                  </c:if>

                </c:otherwise>
              </c:choose>
            </span>
          </div>

        </c:if>

        <%-- Finally, render the property editor page fragment inside a div.
             The div may be initially invisible if the property is derived or
             if we are inside the apply-to-all panel. --%>

        <div id="<c:out value='${propEditorDivId}'/>"
             style="<c:out value='${requestScope.atgPropertyEditorVisibilityStyle}'/>">

          <c:out value="<!-- Begin JSP for property ${propertyName}: contextRoot=${propView.contextRoot} URI=${propView.uri} -->"
                 escapeXml="false"/>

          <dspel:include otherContext="${propView.contextRoot}" page="${propView.uri}"/>

          <c:out value="<!-- End JSP for property ${propertyName} -->"
                 escapeXml="false"/>

        </div>
        </td>

        <%-- Include an additional column fragment if specified. --%>
        <c:if test="${not empty pColumnFragment}">
          <dspel:include otherContext="${pColumnFragmentContext}" page="${pColumnFragment}">
            <dspel:param name="numRowsToSpan" value="${pNumRowsToSpan}"/>
          </dspel:include>
        </c:if>

        <%-- Restore the original property view form handler path if we modified it above --%>
        <c:if test="${requestScope.atgIsMultiEditView}">
          <c:set target="${propView}" property="formHandlerPath" value="${origPropViewFormHandlerPath}"/>
        </c:if>

      </c:otherwise>
    </c:choose>
  </tr>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/propertyContainer.jsp#2 $$Change: 651448 $--%>
