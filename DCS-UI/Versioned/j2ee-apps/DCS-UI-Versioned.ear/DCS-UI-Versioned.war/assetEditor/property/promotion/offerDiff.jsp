<%--
  Diff view for PMDL offers
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <%-- Render the conditions and offer portions of the property value as expressions --%>
  <c:set var="item" value="${propertyView.view.itemMapping.item}"/>
  <dspel:getvalueof var="formHandler" bean="${propertyView.view.itemMapping.formHandler.path}"/>
  Conditions: <c:out value="${formHandler.conditionsText[item]}"/><br/>
  Offer: <c:out value="${formHandler.offerText[item]}"/>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/Versioned/src/web-apps/DCS-UI-Versioned/assetEditor/property/promotion/offerDiff.jsp#2 $$Change: 651448 $--%>
