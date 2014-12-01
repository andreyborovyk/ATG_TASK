<%--
  Wraps the property editor page
  @param  view          A request scoped, MappedView item for this view
  @param  formHandler   The form handler
  @param  property      The property
  
  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/propertyContainer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

<c:set var="debug" value="false"/>

  <dspel:getvalueof var="propertyName" param="property"/>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:importbean var="searchConfig"
                    bean="/atg/commerce/search/web/Configuration"/>
  <fmt:setBundle var="searchBundle" basename="${searchConfig.resourceBundle}"/>

  <c:set var="formHandler"  value="${requestScope.formHandler}"/>

      <%-- include the correct property editor for rendering  --%>
      <c:set var="propView" value="${view.propertyMappings[propertyName]}"/>

      <%-- Set a request variable that is unique across all property views that are
           currently being displayed in the asset editor.  This can be used to
           formulate IDs that must be unique. --%>
      <c:set scope="request" var="atgPropertyViewId"
             value="${requestScope.uniqueAssetID}${propView.uniqueId}"/>

      <%-- set the formHandlerProperty string.  In multi edit mode, this overrides
         the formhandler set in the viewmapping  --%>
      <c:set var="formHandlerPath"  value="${requestScope.formHandlerPath}"/>
      <c:set var="propViewFormHandlerPath" value="${propView.formHandlerPath}"/>
      <c:set target="${propView}" property="formHandlerPath" value="${formHandlerPath}"/>

      <c:if test="${ !empty propView.uri}">
        <tr>

          <%-- Render the property name if the view specifies that we should --%>
          <c:choose>
            <c:when test="${empty propView.attributes.fullWidth or showApplyToAll}">
              <td class="formLabel">

                <%-- In edit mode, display an indicator for errors and for
                     required properties --%>
                <c:if test="${requestScope.editMode}">
                  <c:choose>
                    <c:when test="${not empty formHandler.propertyExceptions[propView.propertyName]}">
                      <span class="error" title="<c:out value='${formHandler.propertyExceptions[propView.propertyName].message}'/>">
                        !
                      </span>
                    </c:when>
                    <c:when test="${propView.propertyDescriptor.required}">
                      <span class="required" title="<fmt:message key='assetEditor.required.title'/>">
                        *
                      </span>
                    </c:when>
                  </c:choose>
                </c:if>
    
                <%-- Display the property name --%>
                <c:out value="${propView.propertyDescriptor.displayName}"/>:

              </td>
              <td>
            </c:when>
            <c:otherwise>
              <td colspan="2">
            </c:otherwise>
          </c:choose>

          <c:set value="${propView}" var="mpv" scope="request"/>
          <c:set var="showPropertyEditor" value="display:inline" scope="request"/>

          <c:if test="${not empty formHandler.propertyExceptions[propView.propertyName]}">
            <%-- NB: Need a CSS class --%>
            <span style="color: #ff0000">
              <c:out value="${formHandler.propertyExceptions[propView.propertyName].message}"/>
            </span>
            <br/>
          </c:if>

          <div id="propEditorDiv_<c:out value='${propView.propertyName}'/>" style="<c:out value='${showPropertyEditor}'/>">
            <c:if test="${!empty propView.attributes.informationText}">
              <fmt:message var="informationText"
                  bundle="${searchBundle}"
                  key="${propView.attributes.informationText}"/>
            </c:if>
            <c:out value="<!-- Begin JSP for ${propView.propertyDescriptor.name}, context root ${propView.contextRoot}, URI ${propView.uri} -->" escapeXml="false"/>
            <dspel:include otherContext="${propView.contextRoot}" page="${propView.uri}">
              <c:if test="${not empty informationText}">
                <dspel:param name="informationText" value="${informationText}"/>
              </c:if>
            </dspel:include>
            <c:out value="<!-- End jsp for  ${propView.propertyDescriptor.name} URI ${propView.uri} -->" escapeXml="false"/>
          </div>

        </td></tr>
      </c:if>

      <c:set target="${propView}" property="formHandlerPath" value="${propViewFormHandlerPath}"/>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/propertyContainer.jsp#2 $$Change: 651448 $--%>
