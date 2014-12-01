<%--
  General info tab for segment assets.

  @param  view          A request scoped, MappedView item for this view
  @param  formHandler   The form handler

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"           %>

<dspel:page>

  <dspel:importbean var="assetManagerConfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="config" bean="/atg/web/personalization/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <c:set var="sessionInfo" value="${assetManagerConfig.sessionInfo}"/>

  <c:set var="assetMgrResources" value="${assetManagerConfig.resourceBundle}"/>
  <fmt:bundle basename="${assetMgrResources}">
    <fmt:message var="requiredTitle" key='assetEditor.required.title'/>
    <fmt:message var="requiredMarker" key="assetEditor.requiredMarker"/>
  </fmt:bundle>

  <%-- Find out which repository we are targeting. If none specified, use the default
       for this task. --%>
  <c:set var="propertyName" value="repository"/>
  <dspel:getvalueof var="currentRepository"
                    bean="${requestScope.formHandlerPath}.properties.${propertyName}" />
  <c:if test="${empty currentRepository}">
    <c:set var="repository"
           value="${sessionInfo.taskConfiguration.editorConfiguration.configuration.profileRepositoryName}"/>
    <dspel:input type="hidden" id="propertyValue_${propertyName}"
                 bean="${requestScope.formHandlerPath}.properties.${propertyName}"
                 value="${repository}"/>
  </c:if>

  <fieldset>
  <c:if test="${not empty view.attributes.resourceBundle and not empty view.displayName}">
    <legend>
      <span>
       <fmt:setBundle var="resBundle" basename="${view.attributes.resourceBundle}"/>
       <fmt:message key="${view.displayName}" bundle="${resBundle}"/>
      </span>
    </legend>
  </c:if>


  <%-- Display selected form handler and Nucleus properties --%>
  <%-- NB: We need to figure out how to use view mapping here, too! --%>
  <c:set var="tabName" value="segmentGeneral"/>
  <table class="formTable">

    <c:set var="propertyName" value="nucleusComponentName"/>
    <tr>
      <td class="formLabel">
        <span class="required" title="${requiredTitle}">
          <c:out value="${requiredMarker}"/>
        </span>
        <fmt:message key="${tabName}.${propertyName}"/>:
      </td>
      <td>
        <dspel:input type="text" id="propertyValue_${propertyName}"
                     iclass="formTextField" size="50" maxlength="254"
                     oninput="formFieldModified()"
                     onpropertychange="formFieldModified()"
                     bean="${requestScope.formHandlerPath}.${propertyName}"/>
      </td>
    </tr>

    <c:set var="propertyName" value="$description"/>
    <tr>
      <td class="formLabel">
        <fmt:message key="${tabName}.${propertyName}"/>:
      </td>
      <td>
        <dspel:input type="text" id="propertyValue_${propertyName}"
                     iclass="formTextField" size="50" maxlength="254"
                     oninput="formFieldModified()"
                     onpropertychange="formFieldModified()"
                     bean="${requestScope.formHandlerPath}.properties.${propertyName}"/>
      </td>
    </tr>

    <%-- Handle setting the profile type --%>
    <c:set var="propertyName" value="repositoryViewName"/>
    <dspel:getvalueof var="currentValue"
                      bean="${requestScope.formHandlerPath}.properties.${propertyName}" />
    <tr>
      <td class="formLabel">
        <fmt:message key="${tabName}.${propertyName}"/>:
      </td>
      <td>
        <c:choose>
          <c:when test="${currentValue eq null}">
            <asset-ui:getProfileTypes var="profileTypesList" repositoryPath="${repository}"/>

            <dspel:select id="propertyValue_${propertyName}"
                          onchange="markAssetModified()"
                          bean="${requestScope.formHandlerPath}.properties.${propertyName}">

              <c:forEach var="profileType" items="${profileTypesList}">
                <dspel:option value="${profileType.type}">
                  <c:out value="${profileType.displayName}" escapeXml="false"/>
                </dspel:option>
              </c:forEach>
            </dspel:select>
          </c:when>

          <c:otherwise>
            <dspel:valueof bean="${requestScope.formHandlerPath}.targetedRepositoryView" />
          </c:otherwise>
        </c:choose>
      </td>
    </tr>

  </table>


  </fieldset>


</dspel:page>
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/tab/targeting/segmentGeneral.jsp#2 $$Change: 651448 $--%>
