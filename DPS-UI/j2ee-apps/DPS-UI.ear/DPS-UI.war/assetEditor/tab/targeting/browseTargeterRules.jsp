<%--
  Tab for browsing targeter rules.

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

  &nbsp;&nbsp;
  <c:set var="containerId" value="targeterExpreditorContainer"/>

  <div id="<c:out value='${containerId}'/>" class="targeterExpreditorContainer">
    <dspel:include otherContext="${assetManagerConfig.webuiRoot}" page="/expreditor/targeting/browseTargeterPanel.jsp">
      <dspel:param name="model" value="${requestScope.formHandler.expressionService.absoluteName}"/>
      <dspel:param name="container" value="${containerId}"/>
    </dspel:include>
  </div>

</dspel:page>
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/tab/targeting/browseTargeterRules.jsp#2 $$Change: 651448 $--%>
