<%--
  Read-only view for PMDL conditions
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id: //product/DCS-UI/version/10.0.3/Versioned/src/web-apps/DCS-UI-Versioned/assetEditor/property/promotion/conditionsViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
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

  <c:choose>
    <c:when test="${requestScope.formHandler.itemDescriptorName eq requestScope.formHandler.closenessQualifierItemType}">
      <b><fmt:message key="promotionEditor.closenessQualifierConditionsHeading"/></b>
    </c:when>
    <c:otherwise>
      <b><fmt:message key="promotionEditor.conditionsHeading"/></b>
    </c:otherwise>
  </c:choose>
  <br/>

  <%-- Render the conditions portion of the property value as an expression --%>
  <dspel:getvalueof var="expressionModel" bean="/atg/commerce/web/promotion/PromotionConditionsExpressionModel"/>
  <web-ui:renderExpression var="expression" expression="${expressionModel.rootExpression}"/>
  <c:out value="${expression}"/>
  
</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/Versioned/src/web-apps/DCS-UI-Versioned/assetEditor/property/promotion/conditionsViewer.jsp#2 $$Change: 651448 $--%>
