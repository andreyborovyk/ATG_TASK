<%--
  Read-only view for PMDL offers
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/commerce/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <b><fmt:message key="promotionEditor.applyHeading"/></b><br/>

  <%-- Render the offer portion of the property value as an expression --%>
  <dspel:getvalueof var="expressionModel" bean="/atg/commerce/web/promotion/PromotionOfferExpressionModel"/>
  <web-ui:renderExpression var="expression" expression="${expressionModel.rootExpression}"/>
  <c:out value="${expression}"/>
  
</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/Versioned/src/web-apps/DCS-UI-Versioned/assetEditor/property/promotion/offerViewer.jsp#2 $$Change: 651448 $--%>
