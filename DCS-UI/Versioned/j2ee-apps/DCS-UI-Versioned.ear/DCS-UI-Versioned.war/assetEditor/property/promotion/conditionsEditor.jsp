<%--
  Edit view for PMDL conditions
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id: //product/DCS-UI/version/10.0.3/Versioned/src/web-apps/DCS-UI-Versioned/assetEditor/property/promotion/conditionsEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt" %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/commerce/web/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  &nbsp;&nbsp;
  <c:choose>
    <c:when test="${requestScope.formHandler.itemDescriptorName eq requestScope.formHandler.closenessQualifierItemType}">
      <b><fmt:message key="promotionEditor.closenessQualifierConditionsHeading"/></b>
    </c:when>
    <c:otherwise>
      <b><fmt:message key="promotionEditor.conditionsHeading"/></b>
    </c:otherwise>
  </c:choose>

  <dspel:include page="expressionEditor.jsp">
    <dspel:param name="model" value="/atg/commerce/web/promotion/PromotionConditionsExpressionModel"/>
  </dspel:include>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/Versioned/src/web-apps/DCS-UI-Versioned/assetEditor/property/promotion/conditionsEditor.jsp#2 $$Change: 651448 $--%>
