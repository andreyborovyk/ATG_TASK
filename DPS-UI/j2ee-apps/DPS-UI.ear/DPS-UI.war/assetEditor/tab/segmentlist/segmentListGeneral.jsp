<%--
  General info tab for segment list assets.

  The following request-scoped variables are expected to be set:

  @param  view          A request scoped, MappedView item for this view
  @param  formHandler   The form handler

  @version $Id //product/DPS-UI/main/src/web-apps/DPS-UI/assetEditor/tab/segmentlist/segmentListGeneral.jsp $$Change $
  @updated $DateTime $$Author amitj $
  --%>

<%@ taglib prefix="c"            uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"        uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"          uri="http://java.sun.com/jstl/fmt"                     %>

<!-- Begin DPS-UI's /assetEditor/tab/segmentlist/segmentListGeneral.jsp -->

<dspel:page>

<fmt:setBundle basename="${view.attributes.resourceBundle}"/>

<dspel:getvalueof var="imap" param="imap"/>

<%-- get the formHandler from the requestScope --%>
<c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>
<c:set var="formHandler"     value="${requestScope.formHandler}"/>

<fieldset>
  <legend><span><fmt:message key="${view.displayName}"/></span></legend>
  <table class="formTable">
    <tr>
      <c:set var="propertyName" value="displayName"/>
      <td class="formLabel">
        <fmt:message key="segmentList.${propertyName}"/>:
      </td>
      <td>
        <dspel:valueof bean="${formHandlerPath}.value.${propertyName}"/>
      </td>
    </tr>
    <tr>
      <c:set var="propertyName" value="description"/>
      <td class="formLabel">
        <fmt:message key="segmentList.${propertyName}"/>:
      </td>
      <td>
        <dspel:valueof bean="${formHandlerPath}.value.${propertyName}"/>
      </td>
    </tr>
  </table>
</fieldset>

<c:set var="propertyName" value="groups" />
<c:set var="propView" value="${view.propertyMappings[propertyName]}"/>

<fieldset>
  <legend><span><fmt:message key="segmentListGroups.displayName"/></span></legend>
  <table class="formTable">
    <c:if test="${!empty propView.uri}">
      <tr>
        <td class="formLabel">
          <%-- In edit mode, display an indicator for errors --%>
          <c:if test="${requestScope.editMode}">
            <c:if test="${not empty formHandler.propertyExceptions[propView.propertyName]}">
              <span class="error" title="<c:out value='${formHandler.propertyExceptions[propView.propertyName].message}'/>">
                !
              </span>
            </c:if>
          </c:if>
          <%-- Display the property name --%>
          <fmt:message key="segmentList.${propertyName}.displayName"/>:
        </td>
        <td>
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
            <c:out value="<!-- Begin JSP for ${propView.propertyDescriptor.name}, context root ${propView.contextRoot}, URI ${propView.uri} -->" escapeXml="false" />
            <dspel:include otherContext="${propView.contextRoot}" page="${propView.uri}"/>
            <c:out value="<!-- End jsp for ${propView.propertyDescriptor.name} URI ${propView.uri} -->" escapeXml="false"/>
          </div>
        </td>
      </tr>
    </c:if>
  </table>
</fieldset>

</dspel:page>

<!-- End DPS-UI's /assetEditor/tab/segmentlist/segmentListGeneral.jsp -->
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/tab/segmentlist/segmentListGeneral.jsp#2 $$Change: 651448 $--%>
