<%--
  Tab for browsing general info about targeter assets.

  @param  view          A request scoped, MappedView item for this view
  @param  formHandler   The form handler

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/web/personalization/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Display selected form handler and Nucleus properties --%>
  <%-- NB: We need to figure out how to use view mapping here, too! --%>
  <c:set var="tabName" value="targeterGeneral"/>
  <table class="formTable">

    <c:set var="propertyName" value="nucleusComponentName"/>
    <tr>
      <td class="formLabel">
        <fmt:message key="${tabName}.${propertyName}"/>:
      </td>
      <td>
        <dspel:valueof bean="${requestScope.formHandlerPath}.${propertyName}"/>
      </td>
    </tr>

    <c:set var="propertyName" value="$description"/>
    <tr>
      <td class="formLabel">
        <fmt:message key="${tabName}.${propertyName}"/>:
      </td>
      <td>
        <dspel:valueof bean="${requestScope.formHandlerPath}.properties.${propertyName}"/>
      </td>
    </tr>

    <c:set var="propertyName" value="repository"/>
    <tr>
      <td class="formLabel">
        <fmt:message key="${tabName}.${propertyName}"/>:
      </td>
      <td>
        <dspel:valueof bean="${requestScope.formHandlerPath}.targetedRepository"/>
      </td>
    </tr>

    <c:set var="propertyName" value="repositoryViewName"/>
    <tr>
      <td class="formLabel">
        <fmt:message key="${tabName}.${propertyName}"/>:
      </td>
      <td>
        <dspel:valueof bean="${requestScope.formHandlerPath}.targetedRepositoryView" />
      </td>
    </tr>

    <c:set var="propertyName" value="profileRepositoryViewName"/>
    <tr>
      <td class="formLabel">
        <fmt:message key="${tabName}.${propertyName}"/>:
      </td>
      <td>
        <dspel:valueof bean="${requestScope.formHandlerPath}.targetedProfileRepositoryView" />
      </td>
    </tr>

  </table>

</dspel:page>
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/tab/targeting/browseTargeterGeneral.jsp#2 $$Change: 651448 $--%>
