<%--
  Page fragment that displays an expression editor for a section of a PMDL rule
  @param   model  An ExpressionModel component containing the expression to be edited
  
  @version $Id: //product/DCS-UI/version/10.0.3/Versioned/src/web-apps/DCS-UI-Versioned/assetEditor/property/promotion/expressionEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/commerce/web/Configuration"/>

  <%-- Unpack all DSP parameters --%>
  <dspel:getvalueof var="paramModel" param="model"/>

  <c:set var="containerId" value="expreditorContainer"/>

  <div class="expreditorContainer">
    <div id="<c:out value='${containerId}'/>" class="expreditorPanel">
      <dspel:include otherContext="${config.webuiRoot}" page="/expreditor/inlineExpressionPanel.jsp">
        <dspel:param name="model" value="${paramModel}"/>
        <dspel:param name="container" value="${containerId}"/>
        <dspel:param name="callback" value="markAssetModified"/>
      </dspel:include>
    </div>
  </div>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/Versioned/src/web-apps/DCS-UI-Versioned/assetEditor/property/promotion/expressionEditor.jsp#2 $$Change: 651448 $--%>
