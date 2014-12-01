<%--
  General info tab for targeter assets.

  @param  view          A request scoped, MappedView item for this view
  @param  formHandler   The form handler

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <dspel:importbean var="assetManagerConfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="config" bean="/atg/web/personalization/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <c:set var="sessionInfo" value="${assetManagerConfig.sessionInfo}"/>

  <%-- Find out which repository we are targeting. If none specified, use the default
       for this task. --%>
  <c:set var="propertyName" value="profileRepository"/>
  <dspel:getvalueof var="currentProfileRepository"
                    bean="${requestScope.formHandlerPath}.properties.${propertyName}" />
  <c:if test="${empty currentProfileRepository}">
    <c:set var="profileRepository"
           value="${sessionInfo.taskConfiguration.editorConfiguration.configuration.profileRepositoryName}"/>
    <dspel:input type="hidden" id="propertyValue_${propertyName}"
                 bean="${requestScope.formHandlerPath}.properties.${propertyName}"
                 value="${profileRepository}"/>
  </c:if>

  <%-- Display selected form handler and Nucleus properties --%>
  <%-- NB: We need to figure out how to use view mapping here, too! --%>

  <%-- amitj: Include the Content Editor to show Content Source and Content Type as drop down lists --%>
  <dspel:include page="/assetEditor/tab/targeting/componentProperties.jsp">
    <dspel:param name="tabName" value="targeterGeneral"/>
    <dspel:param name="profileRepository" value="${profileRepository}"/>
  </dspel:include>

</dspel:page>
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/tab/targeting/targeterGeneral.jsp#2 $$Change: 651448 $--%>
