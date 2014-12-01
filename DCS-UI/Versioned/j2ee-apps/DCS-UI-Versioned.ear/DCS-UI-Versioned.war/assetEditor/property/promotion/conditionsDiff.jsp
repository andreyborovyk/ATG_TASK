<%--
  Diff view for PMDL conditions
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <%-- Render the conditions portion of the property value as an expression --%>
  <c:set var="item" value="${propertyView.view.itemMapping.item}"/>
  <dspel:getvalueof var="formHandler" bean="${propertyView.view.itemMapping.formHandler.path}"/>
  <c:out value="${formHandler.conditionsText[item]}"/>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/Versioned/src/web-apps/DCS-UI-Versioned/assetEditor/property/promotion/conditionsDiff.jsp#2 $$Change: 651448 $--%>
